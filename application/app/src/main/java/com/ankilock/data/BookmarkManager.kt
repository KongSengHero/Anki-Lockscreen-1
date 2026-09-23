package com.ankilock.data
    
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
    
object BookmarkManager { 
    private const val PREFS_NAME = "blossom_bookmarks_prefs" 
    private const val KEY_BOOKMARKS = "saved_bookmarks_json" 
    
    private var prefs: SharedPreferences? = null 
    val bookmarkedWords: SnapshotStateList<BookmarkedWord> = mutableStateListOf() 
    
    fun init(context: Context) { 
        if (prefs == null) { 
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) 
            loadBookmarks() 
        } 
    } 
    
    fun isBookmarked(kanji: String): Boolean { 
        val clean = kanji.trim() 
        if (clean.isBlank()) return false 
        return bookmarkedWords.any { it.kanji.trim() == clean } 
    } 
    
    fun addWord(word: BookmarkedWord) { 
        val cleanKanji = word.kanji.trim() 
        if (cleanKanji.isBlank()) return 
        val existingIndex = bookmarkedWords.indexOfFirst { it.kanji.trim() == cleanKanji } 
        if (existingIndex >= 0) { 
            bookmarkedWords[existingIndex] = word 
        } else { 
            bookmarkedWords.add(0, word) 
        } 
        persist() 
    } 
    
    fun removeWord(kanji: String) { 
        val clean = kanji.trim() 
        val removed = bookmarkedWords.removeAll { it.kanji.trim() == clean } 
        if (removed) { 
            persist() 
        } 
    } 
    
    fun toggleBookmark( 
        kanji: String, 
        reading: String = "", 
        meaning: String = "", 
        sentence: String = "", 
        sourceStoryTitle: String? = null 
    ): Boolean { 
        val clean = kanji.trim() 
        if (clean.isBlank()) return false 
        return if (isBookmarked(clean)) { 
            removeWord(clean) 
            false 
        } else { 
            val word = BookmarkedWord( 
                id = UUID.randomUUID().toString(), 
                kanji = clean, 
                reading = reading, 
                meaning = meaning, 
                furigana = if (reading.isNotBlank() && reading != clean) "$clean[$reading]" else clean, 
                sentence = sentence, 
                sourceStoryTitle = sourceStoryTitle 
            ) 
            addWord(word) 
            true 
        } 
    } 
    
    fun updateWord(word: BookmarkedWord) { 
        val index = bookmarkedWords.indexOfFirst { it.id == word.id || it.kanji.trim() == word.kanji.trim() } 
        if (index >= 0) { 
            bookmarkedWords[index] = word 
            persist() 
        } 
    } 
    
    fun clearAll() { 
        bookmarkedWords.clear() 
        persist() 
    } 
    
    private fun loadBookmarks() { 
        val jsonStr = prefs?.getString(KEY_BOOKMARKS, null) ?: return 
        try { 
            val arr = JSONArray(jsonStr) 
            val list = mutableListOf<BookmarkedWord>() 
            for (i in 0 until arr.length()) { 
                val obj = arr.getJSONObject(i) 
                val tagsArr = obj.optJSONArray("tags") 
                val tagsList = mutableListOf<String>() 
                if (tagsArr != null) { 
                    for (t in 0 until tagsArr.length()) { 
                        tagsList.add(tagsArr.getString(t)) 
                    } 
                } 
                list.add( 
                    BookmarkedWord( 
                        id = obj.optString("id", UUID.randomUUID().toString()), 
                        kanji = obj.optString("kanji", ""), 
                        reading = obj.optString("reading", ""), 
                        meaning = obj.optString("meaning", ""), 
                        furigana = obj.optString("furigana", ""), 
                        sentence = obj.optString("sentence", ""), 
                        sentenceMeaning = obj.optString("sentenceMeaning", ""), 
                        sentenceFurigana = obj.optString("sentenceFurigana", ""), 
                        sourceStoryTitle = obj.optString("sourceStoryTitle", "").takeIf { it.isNotBlank() }, 
                        tags = if (tagsList.isNotEmpty()) tagsList else listOf("Blossom", "MinedVocab"), 
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()) 
                    ) 
                ) 
            } 
            bookmarkedWords.clear() 
            bookmarkedWords.addAll(list) 
        } catch (e: Exception) { 
            e.printStackTrace() 
        } 
    } 
    
    private fun persist() { 
        val p = prefs ?: return 
        val arr = JSONArray() 
        for (w in bookmarkedWords) { 
            val obj = JSONObject() 
            obj.put("id", w.id) 
            obj.put("kanji", w.kanji) 
            obj.put("reading", w.reading) 
            obj.put("meaning", w.meaning) 
            obj.put("furigana", w.furigana) 
            obj.put("sentence", w.sentence) 
            obj.put("sentenceMeaning", w.sentenceMeaning) 
            obj.put("sentenceFurigana", w.sentenceFurigana) 
            if (w.sourceStoryTitle != null) { 
                obj.put("sourceStoryTitle", w.sourceStoryTitle) 
            } 
            val tagsArr = JSONArray() 
            for (tag in w.tags) { 
                tagsArr.put(tag) 
            } 
            obj.put("tags", tagsArr) 
            obj.put("createdAt", w.createdAt) 
            arr.put(obj) 
        } 
        p.edit().putString(KEY_BOOKMARKS, arr.toString()).apply() 
    } 
} 
