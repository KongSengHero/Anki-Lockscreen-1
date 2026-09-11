package com.ankilock.data
    
import android.content.Context
import android.content.SharedPreferences
import com.ankilock.reading.FishAudioService
import org.json.JSONArray
import org.json.JSONObject

class ReadingHistoryManager(private val context: Context) { 
    
    private val prefs: SharedPreferences = context.getSharedPreferences( 
        PREFS_NAME, 
        Context.MODE_PRIVATE 
    ) 
    
    fun getStories(): List<GeneratedStory> { 
        val jsonStr = prefs.getString(KEY_STORIES, null) ?: return emptyList() 
        val list = mutableListOf<GeneratedStory>() 
        try { 
            val jsonArray = JSONArray(jsonStr) 
            for (i in 0 until jsonArray.length()) { 
                val obj = jsonArray.getJSONObject(i) 
                val targetWordsJson = obj.optJSONArray("targetWords") 
                val words = mutableListOf<String>() 
                if (targetWordsJson != null) { 
                    for (j in 0 until targetWordsJson.length()) { 
                        words.add(targetWordsJson.getString(j)) 
                    } 
                } 
                val questionsJson = obj.optJSONArray("questions") 
                val questions = mutableListOf<StoryQuizQuestion>() 
                if (questionsJson != null) { 
                    for (j in 0 until questionsJson.length()) { 
                        val qObj = questionsJson.getJSONObject(j) 
                        val optsJson = qObj.optJSONArray("options") 
                        val opts = mutableListOf<String>() 
                        if (optsJson != null) { 
                            for (k in 0 until optsJson.length()) { 
                                opts.add(optsJson.getString(k)) 
                            } 
                        } 
                        questions.add( 
                            StoryQuizQuestion( 
                                id = qObj.optInt("id", j + 1), 
                                questionText = qObj.optString("questionText", ""), 
                                options = opts, 
                                correctOptionIndex = qObj.optInt("correctOptionIndex", 0), 
                                explanation = qObj.optString("explanation", "") 
                            ) 
                        ) 
                    } 
                } 
                
                val theme = obj.optString("theme", "").ifBlank { null } 
                val topic = obj.optString("topic", "").ifBlank { null } 
                
                list.add( 
                    GeneratedStory( 
                        id = obj.optString("id", ""), 
                        title = obj.optString("title", ""), 
                        content = obj.optString("content", ""), 
                        jlptLevel = obj.optString("jlptLevel", "N5"), 
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()), 
                        targetWords = words, 
                        questions = questions, 
                        theme = theme, 
                        topic = topic 
                    ) 
                ) 
            } 
        } catch (e: Exception) { 
            e.printStackTrace() 
        } 
        return list.sortedByDescending { it.createdAt } 
    } 
    
    fun saveStory(story: GeneratedStory) { 
        val existing = getStories().filterNot { it.id == story.id }.toMutableList() 
        existing.add(0, story) 
        
        val trimmed = if (existing.size > 50) existing.take(50) else existing 
        saveList(trimmed) 
    } 
    
    fun deleteStory(id: String) { 
        val updated = getStories().filterNot { it.id == id } 
        saveList(updated) 
        FishAudioService.deleteAudioForStory(context, id) 
    } 
    
    fun clearAll() { 
        prefs.edit().remove(KEY_STORIES).apply() 
        FishAudioService.clearAllAudio(context) 
    } 
    
    private fun saveList(list: List<GeneratedStory>) { 
        try { 
            val array = JSONArray() 
            for (story in list) { 
                val obj = JSONObject().apply { 
                    put("id", story.id) 
                    put("title", story.title) 
                    put("content", story.content) 
                    put("jlptLevel", story.jlptLevel) 
                    put("createdAt", story.createdAt) 
                    put("targetWords", JSONArray(story.targetWords)) 
                    
                    val qArray = JSONArray() 
                    for (q in story.questions) { 
                        val qObj = JSONObject().apply { 
                            put("id", q.id) 
                            put("questionText", q.questionText) 
                            put("options", JSONArray(q.options)) 
                            put("correctOptionIndex", q.correctOptionIndex) 
                            put("explanation", q.explanation) 
                        } 
                        qArray.put(qObj) 
                    } 
                    put("questions", qArray) 
                    
                    if (story.theme != null) put("theme", story.theme) 
                    if (story.topic != null) put("topic", story.topic) 
                } 
                array.put(obj) 
            } 
            prefs.edit().putString(KEY_STORIES, array.toString()).apply() 
        } catch (e: Exception) { 
            e.printStackTrace() 
        } 
    } 
    
    companion object { 
        private const val PREFS_NAME = "ankilock_reading_history" 
        private const val KEY_STORIES = "saved_reading_stories" 
    } 
} 
