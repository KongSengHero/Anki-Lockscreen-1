package com.ankilock.data
    
import android.content.Context
import android.content.SharedPreferences
import com.ankilock.reading.FishAudioService
import org.json.JSONArray
import org.json.JSONObject
import java.io.File 

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
                val targetWordsDataJson = obj.optJSONArray("targetWordsData") 
                val wordsData = mutableListOf<StoryWordItem>() 
                if (targetWordsDataJson != null) { 
                    for (j in 0 until targetWordsDataJson.length()) { 
                        val twObj = targetWordsDataJson.getJSONObject(j) 
                        wordsData.add( 
                            StoryWordItem( 
                                kanji = twObj.optString("kanji", ""), 
                                reading = twObj.optString("reading", ""), 
                                meaning = twObj.optString("meaning", ""), 
                                pos = twObj.optString("pos", ""), 
                                kanjiBreakdown = twObj.optString("kanjiBreakdown", "") 
                            ) 
                        ) 
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
                val isPinned = obj.optBoolean("isPinned", false) 
                
                val furiganaContent = obj.optString("furiganaContent", "").ifBlank { null } 
                
                list.add( 
                    GeneratedStory( 
                        id = obj.optString("id", ""), 
                        title = obj.optString("title", ""), 
                        content = obj.optString("content", ""), 
                        furiganaContent = furiganaContent, 
                        jlptLevel = obj.optString("jlptLevel", "N5"), 
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()), 
                        targetWords = words, 
                        targetWordsData = wordsData, 
                        questions = questions, 
                        theme = theme, 
                        topic = topic, 
                        isPinned = isPinned 
                    ) 
                ) 
            } 
        } catch (e: Exception) { 
            e.printStackTrace() 
        } 
        return list.sortedWith( 
            compareByDescending<GeneratedStory> { it.isPinned } 
                .thenByDescending { it.createdAt } 
        ) 
    } 
    
    fun saveStory(story: GeneratedStory) { 
        val existing = getStories().filterNot { it.id == story.id }.toMutableList() 
        existing.add(0, story) 
        
        val trimmed = if (existing.size > 50) existing.take(50) else existing 
        saveList(trimmed) 
    } 
    
    fun togglePin(id: String): Boolean { 
        val stories = getStories().map { story -> 
            if (story.id == id) { 
                story.copy(isPinned = !story.isPinned) 
            } else { 
                story 
            } 
        } 
        saveList(stories) 
        return stories.find { it.id == id }?.isPinned ?: false 
    } 
    
    fun deleteStory(id: String) { 
        val updated = getStories().filterNot { it.id == id } 
        saveList(updated) 
        FishAudioService.deleteAudioForStory(context, id) 
        try { 
            File(context.filesDir, "stories/images/$id").deleteRecursively() 
        } catch (_: Exception) { 
        } 
    } 
    
    fun clearAll() { 
        prefs.edit().remove(KEY_STORIES).apply() 
        FishAudioService.clearAllAudio(context) 
        try { 
            File(context.filesDir, "stories/images").deleteRecursively() 
        } catch (_: Exception) { 
        } 
    } 
    
    fun getTotalStorageBytes(): Long { 
        var total = 0L 
        try { 
            val jsonSize = prefs.getString(KEY_STORIES, null)?.toByteArray(Charsets.UTF_8)?.size?.toLong() ?: 0L 
            total += jsonSize 
            
            val imagesDir = File(context.filesDir, "stories/images") 
            if (imagesDir.exists()) { 
                imagesDir.walkTopDown().forEach { file -> 
                    if (file.isFile) total += file.length() 
                } 
            } 
            
            val audioDir = FishAudioService.getAudioDir(context) 
            if (audioDir.exists()) { 
                audioDir.walkTopDown().forEach { file -> 
                    if (file.isFile) total += file.length() 
                } 
            } 
        } catch (_: Exception) { 
        } 
        return total 
    } 
    
    fun getStoryStorageBytes(story: GeneratedStory): Long { 
        var total = 0L 
        try { 
            val storyJsonApprox = (story.title.length + story.content.length + (story.theme?.length ?: 0) + 200).toLong() 
            total += storyJsonApprox 
            
            val imageDir = File(context.filesDir, "stories/images/${story.id}") 
            if (imageDir.exists()) { 
                imageDir.walkTopDown().forEach { file -> 
                    if (file.isFile) total += file.length() 
                } 
            } 
            
            val audioDir = FishAudioService.getAudioDir(context) 
            if (audioDir.exists()) { 
                val safeId = story.id.replace(Regex("[^a-zA-Z0-9_-]"), "_") 
                audioDir.walkTopDown().forEach { file -> 
                    if (file.isFile && (file.name.contains(story.id) || file.name.contains(safeId))) { 
                        total += file.length() 
                    } 
                } 
            } 
        } catch (_: Exception) { 
        } 
        return total 
    } 
    
    fun getAudioStorageBytes(): Long { 
        var total = 0L 
        try { 
            val audioDir = FishAudioService.getAudioDir(context) 
            if (audioDir.exists()) { 
                audioDir.walkTopDown().forEach { file -> 
                    if (file.isFile) total += file.length() 
                } 
            } 
        } catch (_: Exception) { 
        } 
        return total 
    } 
    
    fun formatStorageSize(bytes: Long): String { 
        if (bytes <= 0L) return "0 KB" 
        val kb = bytes / 1024.0 
        if (kb < 1024.0) { 
            return String.format(java.util.Locale.US, "%.1f KB", kb) 
        } 
        val mb = kb / 1024.0 
        return String.format(java.util.Locale.US, "%.1f MB", mb) 
    } 
    
    fun clearAudioCache(): Boolean { 
        return FishAudioService.clearAllAudio(context) 
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
                    
                    val twDataArray = JSONArray() 
                    for (tw in story.targetWordsData) { 
                        val twObj = JSONObject().apply { 
                            put("kanji", tw.kanji) 
                            put("reading", tw.reading) 
                            put("meaning", tw.meaning) 
                            put("pos", tw.pos) 
                            put("kanjiBreakdown", tw.kanjiBreakdown) 
                        } 
                        twDataArray.put(twObj) 
                    } 
                    put("targetWordsData", twDataArray) 
                    
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
                    put("isPinned", story.isPinned) 
                    
                    if (story.furiganaContent != null) put("furiganaContent", story.furiganaContent) 
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
