package com.ankilock.data

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.ankilock.ui.blossom.BlossomStoryModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStreamReader

object StoryAssetLoader { 

    private val imageCache = mutableMapOf<String, ImageBitmap>()
    private var cachedStories: List<BlossomStoryModel>? = null
    private val rawStoriesJsonMap = mutableMapOf<String, JSONObject>()

    fun invalidateStoryCache(storyId: String) { 
        val keysToRemove = imageCache.keys.filter { it.contains(storyId) } 
        keysToRemove.forEach { imageCache.remove(it) } 
    } 

    fun loadAllStories(context: Context): List<BlossomStoryModel> { 
        val cached = cachedStories 
        if (cached != null && cached.isNotEmpty()) { 
            return cached 
        } 

        val result = mutableListOf<BlossomStoryModel>() 
        rawStoriesJsonMap.clear() 

        val sections = listOf( 
            "n5", 
            "n4", 
            "news", 
            "animals", 
            "adventure", 
            "work", 
            "food", 
            "culture", 
            "sports", 
            "romance" 
        ) 

        for (sec in sections) { 
            val dirPath = "stories/$sec" 
            try { 
                val files = context.assets.list(dirPath) ?: emptyArray() 
                for (fileName in files) { 
                    if (fileName.endsWith(".json", ignoreCase = true)) { 
                        val fullPath = "$dirPath/$fileName" 
                        try { 
                            val text = context.assets.open(fullPath).use { 
                                InputStreamReader(it, Charsets.UTF_8).readText() 
                            } 
                            val json = JSONObject(text) 
                            val storyModel = parseStoryModelFromJson(json, sec) 
                            result.add(storyModel) 
                            rawStoriesJsonMap[storyModel.id] = json 
                        } catch (_: Exception) { 
                        } 
                    } 
                } 
            } catch (_: Exception) { 
            } 
        } 

        if (result.isNotEmpty()) { 
            cachedStories = result 
        } 
        return result 
    } 

    fun loadStoriesForCategory(context: Context, category: String): List<BlossomStoryModel> { 
        val all = loadAllStories(context) 
        val catClean = category.trim().lowercase() 
        return all.filter { 
            it.category.trim().lowercase() == catClean || 
            it.section.trim().lowercase() == catClean 
        } 
    } 

    fun buildForgedStoryFromAsset(context: Context, storyId: String): ForgedStory? { 
        if (!rawStoriesJsonMap.containsKey(storyId)) { 
            loadAllStories(context) 
        } 
        val json = rawStoriesJsonMap[storyId] ?: return null 
        return parseForgedStoryFromJson(json) 
    } 

    fun loadCoverImage(context: Context, story: BlossomStoryModel): ImageBitmap? { 
        val customLocal = File(context.filesDir, "stories/images/${story.id}/cover.png") 
        val localFile = if (customLocal.exists()) customLocal else File(context.filesDir, "stories/images/${story.id}/1.png") 
        if (localFile.exists()) { 
            val cacheKey = localFile.absolutePath 
            imageCache[cacheKey]?.let { return it } 
            val bmp = BitmapFactory.decodeFile(localFile.absolutePath) 
            if (bmp != null) { 
                val ib = bmp.asImageBitmap() 
                imageCache[cacheKey] = ib 
                return ib 
            } 
        } 

        val imgName = story.coverImage.ifBlank { "1.png" } 
        val assetPath = "stories/${story.section}/images/${story.id}/$imgName" 
        imageCache[assetPath]?.let { return it } 

        return try { 
            context.assets.open(assetPath).use { 
                val bmp = BitmapFactory.decodeStream(it) 
                if (bmp != null) { 
                    val ib = bmp.asImageBitmap() 
                    imageCache[assetPath] = ib 
                    ib 
                } else null 
            } 
        } catch (_: Exception) { 
            null 
        } 
    } 

    fun loadPageImage( 
        context: Context, 
        section: String, 
        storyId: String, 
        imageName: String 
    ): ImageBitmap? { 
        if (imageName.isBlank()) return null 

        val localFile = File(context.filesDir, "stories/images/$storyId/$imageName") 
        val fileToLoad = when { 
            localFile.exists() -> localFile 
            File(context.filesDir, "stories/images/$storyId/1.png").exists() -> File(context.filesDir, "stories/images/$storyId/1.png") 
            File(context.filesDir, "stories/images/$storyId/cover.png").exists() -> File(context.filesDir, "stories/images/$storyId/cover.png") 
            else -> null 
        } 

        if (fileToLoad != null && fileToLoad.exists()) { 
            val cacheKey = fileToLoad.absolutePath 
            imageCache[cacheKey]?.let { return it } 
            val bmp = BitmapFactory.decodeFile(fileToLoad.absolutePath) 
            if (bmp != null) { 
                val ib = bmp.asImageBitmap() 
                imageCache[cacheKey] = ib 
                return ib 
            } 
        } 

        val assetPath = "stories/$section/images/$storyId/$imageName" 
        imageCache[assetPath]?.let { return it } 

        return try { 
            context.assets.open(assetPath).use { 
                val bmp = BitmapFactory.decodeStream(it) 
                if (bmp != null) { 
                    val ib = bmp.asImageBitmap() 
                    imageCache[assetPath] = ib 
                    ib 
                } else null 
            } 
        } catch (_: Exception) { 
            null 
        } 
    } 

    private fun parseStoryModelFromJson(json: JSONObject, section: String): BlossomStoryModel { 
        val sentencesArray = json.optJSONArray("sentences") ?: JSONArray() 
        val sentences = mutableListOf<StorySentenceItem>() 
        for (i in 0 until sentencesArray.length()) { 
            val sObj = sentencesArray.getJSONObject(i) 
            val twArray = sObj.optJSONArray("targetWords") ?: JSONArray() 
            val twList = mutableListOf<String>() 
            for (k in 0 until twArray.length()) { 
                twList.add(twArray.getString(k)) 
            } 
            sentences.add( 
                StorySentenceItem( 
                    id = sObj.optInt("id", i + 1), 
                    japanese = sObj.optString("japanese", ""), 
                    english = sObj.optString("english", ""), 
                    targetWords = twList, 
                    image = sObj.optString("image", "${i + 1}.png"), 
                    imagePrompt = sObj.optString("imagePrompt", ""), 
                    furigana = sObj.optString("furigana", "") 
                ) 
            ) 
        } 

        val wordsArray = json.optJSONArray("targetWords") ?: JSONArray() 
        val targetWords = mutableListOf<StoryWordItem>() 
        for (i in 0 until wordsArray.length()) { 
            val wObj = wordsArray.getJSONObject(i) 
            targetWords.add( 
                StoryWordItem( 
                    kanji = wObj.optString("kanji", ""), 
                    reading = wObj.optString("reading", ""), 
                    meaning = wObj.optString("meaning", "") 
                ) 
            ) 
        } 

        val qArray = json.optJSONArray("questions") ?: JSONArray() 
        val questions = mutableListOf<StoryQuestion>() 
        for (i in 0 until qArray.length()) { 
            val qObj = qArray.getJSONObject(i) 
            val optArr = qObj.optJSONArray("options") ?: JSONArray() 
            val opts = mutableListOf<String>() 
            for (j in 0 until optArr.length()) { 
                opts.add(optArr.getString(j)) 
            } 
            val optFuriArr = qObj.optJSONArray("optionsFurigana") ?: JSONArray() 
            val optsFuri = mutableListOf<String>() 
            for (j in 0 until optFuriArr.length()) { 
                optsFuri.add(optFuriArr.getString(j)) 
            } 
            questions.add( 
                StoryQuestion( 
                    id = qObj.optInt("id", i + 1), 
                    questionText = qObj.optString("questionText", ""), 
                    options = opts, 
                    correctOptionIndex = qObj.optInt("correctOptionIndex", 0), 
                    explanation = qObj.optString("explanation", ""), 
                    questionFurigana = qObj.optString("questionFurigana", ""), 
                    optionsFurigana = optsFuri 
                ) 
            ) 
        } 

        val category = json.optString("category", section.replaceFirstChar { it.uppercase() }) 
        val id = json.optString("id", "${section}_${System.currentTimeMillis()}") 
        val titleJp = json.optString("titleJapanese", "タイトル") 
        val titleEn = json.optString("titleEnglish", "Title") 

        val forged = ForgedStory( 
            id = id, 
            title = titleJp, 
            genre = category, 
            level = json.optString("levelId", "starter"), 
            storyJapanese = json.optString("storyJapanese", ""), 
            storyEnglish = json.optString("storyEnglish", ""), 
            sentences = sentences, 
            targetWords = targetWords, 
            questions = questions 
        ) 

        val computedEstMinutes = calculateEstimatedReadingMinutes( 
            pageCount = sentences.size, 
            japaneseText = json.optString("storyJapanese", sentences.joinToString(" ") { it.japanese }), 
            englishText = json.optString("storyEnglish", sentences.joinToString(" ") { it.english }), 
            questionCount = questions.size 
        ) 
 
        return BlossomStoryModel( 
            id = id, 
            levelId = json.optString("levelId", "starter"), 
            subLevel = json.optInt("subLevel", 1), 
            storyIndex = json.optInt("storyIndex", 1), 
            titleJapanese = titleJp, 
            titleEnglish = titleEn, 
            category = category, 
            estimatedMinutes = computedEstMinutes, 
            xp = json.optInt("xp", 100), 
            isEndingCard = json.optBoolean("isEndingCard", false), 
            artworkType = json.optString("artworkType", section), 
            summary = json.optString("summary", ""), 
            author = json.optString("author", ""), 
            sentences = sentences, 
            questions = questions, 
            coverImage = json.optString("coverImage", "1.png"), 
            section = section, 
            story = forged 
        ) 
    } 

    private fun parseForgedStoryFromJson(json: JSONObject): ForgedStory { 
        val sentencesArray = json.optJSONArray("sentences") ?: JSONArray() 
        val sentences = mutableListOf<StorySentenceItem>() 
        for (i in 0 until sentencesArray.length()) { 
            val sObj = sentencesArray.getJSONObject(i) 
            val twArray = sObj.optJSONArray("targetWords") ?: JSONArray() 
            val twList = mutableListOf<String>() 
            for (k in 0 until twArray.length()) { 
                twList.add(twArray.getString(k)) 
            } 
            sentences.add( 
                StorySentenceItem( 
                    id = sObj.optInt("id", i + 1), 
                    japanese = sObj.optString("japanese", ""), 
                    english = sObj.optString("english", ""), 
                    targetWords = twList, 
                    image = sObj.optString("image", "${i + 1}.png"), 
                    imagePrompt = sObj.optString("imagePrompt", ""), 
                    furigana = sObj.optString("furigana", "") 
                ) 
            ) 
        } 

        val wordsArray = json.optJSONArray("targetWords") ?: JSONArray() 
        val targetWords = mutableListOf<StoryWordItem>() 
        for (i in 0 until wordsArray.length()) { 
            val wObj = wordsArray.getJSONObject(i) 
            targetWords.add( 
                StoryWordItem( 
                    kanji = wObj.optString("kanji", ""), 
                    reading = wObj.optString("reading", ""), 
                    meaning = wObj.optString("meaning", "") 
                ) 
            ) 
        } 

        val qArray = json.optJSONArray("questions") ?: JSONArray() 
        val questions = mutableListOf<StoryQuestion>() 
        for (i in 0 until qArray.length()) { 
            val qObj = qArray.getJSONObject(i) 
            val optArr = qObj.optJSONArray("options") ?: JSONArray() 
            val opts = mutableListOf<String>() 
            for (j in 0 until optArr.length()) { 
                opts.add(optArr.getString(j)) 
            } 
            val optFuriArr = qObj.optJSONArray("optionsFurigana") ?: JSONArray() 
            val optsFuri = mutableListOf<String>() 
            for (j in 0 until optFuriArr.length()) { 
                optsFuri.add(optFuriArr.getString(j)) 
            } 
            questions.add( 
                StoryQuestion( 
                    id = qObj.optInt("id", i + 1), 
                    questionText = qObj.optString("questionText", ""), 
                    options = opts, 
                    correctOptionIndex = qObj.optInt("correctOptionIndex", 0), 
                    explanation = qObj.optString("explanation", ""), 
                    questionFurigana = qObj.optString("questionFurigana", ""), 
                    optionsFurigana = optsFuri 
                ) 
            ) 
        } 

        return ForgedStory( 
            id = json.optString("id", java.util.UUID.randomUUID().toString()), 
            title = json.optString("titleJapanese", "Japanese Story"), 
            genre = json.optString("category", "General"), 
            level = json.optString("levelId", "Intermediate"), 
            storyJapanese = json.optString("storyJapanese", ""), 
            storyEnglish = json.optString("storyEnglish", ""), 
            sentences = sentences, 
            targetWords = targetWords, 
            questions = questions 
        ) 
    } 
} 
