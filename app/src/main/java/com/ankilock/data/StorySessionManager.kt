package com.ankilock.data
    
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ankilock.ai.AiServiceHelper
import com.ankilock.ai.WallhavenServiceHelper
import com.ankilock.anki.AnkiDroidHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import com.ankilock.ui.shinobi.ShinobiStoryModel
    
enum class ForgeSlotState { 
    EMPTY, 
    FORGING, 
    QUEUED, 
    COMPLETED 
} 

data class ForgeSlotData( 
    val slotId: Int, 
    val state: ForgeSlotState = ForgeSlotState.EMPTY, 
    val genre: String = "Daily Life", 
    val level: String = "N5", 
    val length: String = "Medium", 
    val cardCount: Int = 3, 
    val generateImage: Boolean = true, 
    val stage: String = "", 
    val progressFraction: Float = 0f, 
    val story: ForgedStory? = null, 
    val error: String? = null 
) 

object StorySessionManager { 
    
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) 
    private const val SAVED_STORIES_FILE = "saved_stories.json" 
    
    var currentStory by mutableStateOf<ForgedStory?>(null) 
    var selectedStoryForDetails by mutableStateOf<ShinobiStoryModel?>(null) 
    var isGenerating by mutableStateOf(false) 
    var errorMessage by mutableStateOf<String?>(null) 
    var activeSentenceIndex by mutableIntStateOf(-1) 
    var isFullStoryPlaying by mutableStateOf(false) 
    var completedStoryIds by mutableStateOf<Set<String>>(emptySet()) 
    val userAnswers = mutableStateMapOf<Int, Int>() 
    val savedStoriesList = mutableStateListOf<ForgedStory>() 
    val storyProgressMap = mutableStateMapOf<String, Float>() 
    val storyPageMap = mutableStateMapOf<String, Int>() 
    var progressVersion by mutableIntStateOf(0) 
    var generationStage by mutableStateOf("") 
    var lastForgedCards by mutableStateOf<List<CardInfo>>(emptyList()) 
    
    var slot1 by mutableStateOf(ForgeSlotData(slotId = 1)) 
    var slot2 by mutableStateOf(ForgeSlotData(slotId = 2)) 
    private var slot1Job: kotlinx.coroutines.Job? = null 
    private var slot2Job: kotlinx.coroutines.Job? = null 
    var newlyCompletedStory by mutableStateOf<ForgedStory?>(null) 
    var showCompletionNotification by mutableStateOf(false) 
    var showWallhavenPickerForStoryId by mutableStateOf<String?>(null) 
    var activeMiniGameDifficulty by mutableStateOf("Advance") 
    
    fun selectAnchorCardsForStory(ankiHelper: AnkiDroidHelper, selectedDeckIds: Set<Long>, count: Int = 8): List<CardInfo> { 
        val dueCards = ankiHelper.getDistinctDueCards(selectedDeckIds, count) 
        if (dueCards.size >= count) { 
            return dueCards.take(count) 
        } 
        return dueCards 
    } 
    
    fun clearSlot(slotId: Int) { 
        if (slotId == 1) { 
            slot1Job?.cancel() 
            slot1Job = null 
            slot1 = ForgeSlotData(slotId = 1) 
        } else { 
            slot2Job?.cancel() 
            slot2Job = null 
            slot2 = ForgeSlotData(slotId = 2) 
        } 
        if (slot1.state != ForgeSlotState.FORGING && slot2.state != ForgeSlotState.FORGING) { 
            isGenerating = false 
            generationStage = "" 
        } 
    } 
    
    fun cancelQueue(slotId: Int) { 
        clearSlot(slotId) 
    } 
    
    fun clearSlotError(slotId: Int) { 
        updateSlot(slotId) { it.copy(error = null) } 
    } 
    
    fun cancelSlotForging(slotId: Int, ankiHelper: AnkiDroidHelper? = null, prefs: PreferencesManager? = null, context: Context? = null) { 
        clearSlot(slotId) 
        if (ankiHelper != null && prefs != null && context != null) { 
            checkAndTriggerQueuedSlot(slotId, ankiHelper, prefs, context) 
        } 
    } 
    
    fun cancelForging() { 
        slot1Job?.cancel() 
        slot1Job = null 
        slot2Job?.cancel() 
        slot2Job = null 
        if (slot1.state == ForgeSlotState.FORGING) slot1 = ForgeSlotData(slotId = 1) 
        if (slot2.state == ForgeSlotState.FORGING) slot2 = ForgeSlotData(slotId = 2) 
        isGenerating = false 
        generationStage = "" 
    } 
    
    fun startOrQueueSlot( 
        slotId: Int, 
        cardCount: Int, 
        genre: String, 
        level: String, 
        length: String = "Medium", 
        generateImage: Boolean = true, 
        ankiHelper: AnkiDroidHelper, 
        prefs: PreferencesManager, 
        context: Context 
    ) { 
        val otherSlot = if (slotId == 1) slot2 else slot1 
        if (otherSlot.state == ForgeSlotState.FORGING) { 
            val queued = ForgeSlotData( 
                slotId = slotId, 
                state = ForgeSlotState.QUEUED, 
                genre = genre, 
                level = level, 
                length = length, 
                cardCount = cardCount, 
                generateImage = generateImage, 
                stage = "Queued..." 
            ) 
            if (slotId == 1) slot1 = queued else slot2 = queued 
            return 
        } 
        
        executeSlotForge( 
            slotId = slotId, 
            cardCount = cardCount, 
            genre = genre, 
            level = level, 
            length = length, 
            generateImage = generateImage, 
            ankiHelper = ankiHelper, 
            prefs = prefs, 
            context = context 
        ) 
    } 
    
    private fun executeSlotForge( 
        slotId: Int, 
        cardCount: Int, 
        genre: String, 
        level: String, 
        length: String, 
        generateImage: Boolean, 
        ankiHelper: AnkiDroidHelper, 
        prefs: PreferencesManager, 
        context: Context 
    ) { 
        val initialData = ForgeSlotData( 
            slotId = slotId, 
            state = ForgeSlotState.FORGING, 
            genre = genre, 
            level = level, 
            length = length, 
            cardCount = cardCount, 
            generateImage = generateImage, 
            stage = "Selecting due Anki cards...", 
            progressFraction = 0.05f 
        ) 
        if (slotId == 1) slot1 = initialData else slot2 = initialData 
        isGenerating = true 
        generationStage = "Selecting due Anki cards..." 
        errorMessage = null 
        
        val job = appScope.launch { 
            try { 
                val selectedDeckIds = prefs.getSelectedDeckIdsAsLongs() 
                val cards = if (cardCount > 0) selectAnchorCardsForStory(ankiHelper, selectedDeckIds, cardCount) else emptyList() 
                lastForgedCards = cards 
                
                if (cardCount > 0 && cards.isEmpty()) { 
                    val err = "No due flashcards found in AnkiDroid." 
                    val errSlot = initialData.copy(state = ForgeSlotState.EMPTY, error = err) 
                    if (slotId == 1) slot1 = errSlot else slot2 = errSlot 
                    isGenerating = false 
                    errorMessage = err 
                    checkAndTriggerQueuedSlot(slotId, ankiHelper, prefs, context) 
                    return@launch 
                } 
                
                updateSlot(slotId) { it.copy(stage = "Weaving Japanese story & dialogue...", progressFraction = 0.15f) } 
                generationStage = "Weaving Japanese story & dialogue..." 
                
                val res = AiServiceHelper.forgeStory( 
                    cards = cards, 
                    genre = genre, 
                    level = level, 
                    length = length, 
                    apiKey = prefs.aiApiKey, 
                    provider = prefs.aiProvider, 
                    model = prefs.aiModel 
                ) 
                
                if (res.isSuccess) { 
                    val story = res.getOrThrow() 
                    userAnswers.clear() 
                    activeSentenceIndex = -1 
                    isFullStoryPlaying = false 
                    
                    saveStory(context, story) 
                    val completedSlot = ForgeSlotData( 
                        slotId = slotId, 
                        state = ForgeSlotState.COMPLETED, 
                        genre = genre, 
                        level = level, 
                        length = length, 
                        stage = "Story Complete!", 
                        progressFraction = 1f, 
                        story = story 
                    ) 
                    if (slotId == 1) slot1 = completedSlot else slot2 = completedSlot 
                    newlyCompletedStory = story 
                    showCompletionNotification = true 
                    generationStage = "Complete!" 
                } else { 
                    val err = res.exceptionOrNull()?.message ?: "Failed to forge story." 
                    val errSlot = ForgeSlotData(slotId = slotId, state = ForgeSlotState.EMPTY, error = err) 
                    if (slotId == 1) slot1 = errSlot else slot2 = errSlot 
                    errorMessage = err 
                } 
                isGenerating = false 
            } catch (e: Exception) { 
                isGenerating = false 
                val err = e.message ?: "Unexpected error forging story." 
                val errSlot = ForgeSlotData(slotId = slotId, state = ForgeSlotState.EMPTY, error = err) 
                if (slotId == 1) slot1 = errSlot else slot2 = errSlot 
                errorMessage = err 
            } finally { 
                if (slotId == 1) slot1Job = null 
                if (slotId == 2) slot2Job = null 
            } 
            checkAndTriggerQueuedSlot(slotId, ankiHelper, prefs, context) 
        } 
        if (slotId == 1) slot1Job = job else slot2Job = job 
    } 
    
    private fun updateSlot(slotId: Int, update: (ForgeSlotData) -> ForgeSlotData) { 
        if (slotId == 1) { 
            slot1 = update(slot1) 
        } else { 
            slot2 = update(slot2) 
        } 
    } 
    
    private fun checkAndTriggerQueuedSlot( 
        currentFinishedSlotId: Int, 
        ankiHelper: AnkiDroidHelper, 
        prefs: PreferencesManager, 
        context: Context 
    ) { 
        val otherSlot = if (currentFinishedSlotId == 1) slot2 else slot1 
        if (otherSlot.state == ForgeSlotState.QUEUED) { 
            executeSlotForge( 
                slotId = otherSlot.slotId, 
                cardCount = otherSlot.cardCount, 
                genre = otherSlot.genre, 
                level = otherSlot.level, 
                length = otherSlot.length, 
                generateImage = otherSlot.generateImage, 
                ankiHelper = ankiHelper, 
                prefs = prefs, 
                context = context 
            ) 
        } 
    } 
    
    fun generateStory( 
        ankiHelper: AnkiDroidHelper, 
        prefs: PreferencesManager, 
        cardCount: Int, 
        genre: String, 
        level: String, 
        length: String = "Medium", 
        context: Context? = null, 
        generateImage: Boolean = true, 
        onFinished: (() -> Unit)? = null 
    ) { 
        if (prefs.aiApiKey.isBlank()) { 
            errorMessage = "API key is required to forge stories." 
            return 
        } 
        if (context != null) { 
            val targetSlot = if (slot1.state == ForgeSlotState.EMPTY || slot1.state == ForgeSlotState.COMPLETED) 1 else 2 
            startOrQueueSlot(targetSlot, cardCount, genre, level, length, generateImage, ankiHelper, prefs, context) 
        } 
    } 
    
    fun loadSavedStories(context: Context) { 
        val list = readSavedStoriesFromDisk(context) 
        try { 
            val mainHandler = android.os.Handler(android.os.Looper.getMainLooper()) 
            mainHandler.post { 
                savedStoriesList.clear() 
                savedStoriesList.addAll(list) 
            } 
        } catch (_: Exception) { 
            savedStoriesList.clear() 
            savedStoriesList.addAll(list) 
        } 
    } 
    
    fun saveCurrentStory(context: Context): Boolean { 
        val story = currentStory ?: return false 
        val list = readSavedStoriesFromDisk(context).toMutableList() 
        val existingIdx = list.indexOfFirst { it.id == story.id } 
        if (existingIdx >= 0) { 
            list[existingIdx] = story 
        } else { 
            list.add(0, story) 
        } 
        writeSavedStoriesToDisk(context, list) 
        try { 
            val mainHandler = android.os.Handler(android.os.Looper.getMainLooper()) 
            mainHandler.post { 
                savedStoriesList.clear() 
                savedStoriesList.addAll(list) 
            } 
        } catch (_: Exception) { 
            savedStoriesList.clear() 
            savedStoriesList.addAll(list) 
        } 
        return true 
    } 
    
    fun saveStory(context: Context, story: ForgedStory): Boolean { 
        val list = readSavedStoriesFromDisk(context).toMutableList() 
        val existingIdx = list.indexOfFirst { it.id == story.id } 
        if (existingIdx >= 0) { 
            list[existingIdx] = story 
        } else { 
            list.add(0, story) 
        } 
        writeSavedStoriesToDisk(context, list) 
        try { 
            val mainHandler = android.os.Handler(android.os.Looper.getMainLooper()) 
            mainHandler.post { 
                savedStoriesList.clear() 
                savedStoriesList.addAll(list) 
            } 
        } catch (_: Exception) { 
            savedStoriesList.clear() 
            savedStoriesList.addAll(list) 
        } 
        return true 
    } 
    
    fun deleteSavedStory(context: Context, storyId: String) { 
        val list = readSavedStoriesFromDisk(context).toMutableList() 
        list.removeAll { it.id == storyId } 
        writeSavedStoriesToDisk(context, list) 
        try { 
            val mainHandler = android.os.Handler(android.os.Looper.getMainLooper()) 
            mainHandler.post { 
                savedStoriesList.clear() 
                savedStoriesList.addAll(list) 
            } 
        } catch (_: Exception) { 
            savedStoriesList.clear() 
            savedStoriesList.addAll(list) 
        } 
    } 
    
    fun isCurrentStorySaved(): Boolean { 
        val story = currentStory ?: return false
        return savedStoriesList.any { it.id == story.id }
    }
    
    private fun readSavedStoriesFromDisk(context: Context): List<ForgedStory> { 
        return try { 
            val file = File(context.filesDir, SAVED_STORIES_FILE)
            if (!file.exists()) return emptyList()
            val content = file.readText()
            if (content.isBlank()) return emptyList()
            val array = JSONArray(content)
            val result = mutableListOf<ForgedStory>()
            for (i in 0 until array.length()) { 
                val obj = array.getJSONObject(i)
                result.add(parseStoryFromJson(obj))
            }
            result
        } catch (e: Exception) { 
            emptyList()
        }
    }
    
    private fun writeSavedStoriesToDisk(context: Context, list: List<ForgedStory>) { 
        try { 
            val file = File(context.filesDir, SAVED_STORIES_FILE)
            val array = JSONArray()
            for (story in list) { 
                array.put(serializeStoryToJson(story))
            }
            file.writeText(array.toString(2))
        } catch (e: Exception) { 
        }
    }
    
    private fun parseStoryFromJson(obj: JSONObject): ForgedStory { 
        val sentencesArray = obj.optJSONArray("sentences") ?: JSONArray()
        val sentences = mutableListOf<StorySentenceItem>()
        for (i in 0 until sentencesArray.length()) { 
            val sObj = sentencesArray.getJSONObject(i)
            val tWordsArray = sObj.optJSONArray("targetWords") ?: JSONArray()
            val tWords = mutableListOf<String>()
            for (k in 0 until tWordsArray.length()) { 
                tWords.add(tWordsArray.getString(k))
            }
            sentences.add( 
                StorySentenceItem( 
                    id = sObj.optInt("id", i + 1), 
                    japanese = sObj.optString("japanese", ""), 
                    english = sObj.optString("english", ""), 
                    targetWords = tWords, 
                    image = sObj.optString("image", "${i + 1}.png"), 
                    imagePrompt = sObj.optString("imagePrompt", ""), 
                    furigana = sObj.optString("furigana", "") 
                ) 
            ) 
        } 
        
        val wordsArray = obj.optJSONArray("targetWords") ?: JSONArray()
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
        
        val qArray = obj.optJSONArray("questions") ?: JSONArray()
        val questions = mutableListOf<StoryQuestion>()
        for (i in 0 until qArray.length()) { 
            val qObj = qArray.getJSONObject(i) 
            val optsArray = qObj.optJSONArray("options") ?: JSONArray() 
            val opts = mutableListOf<String>() 
            for (j in 0 until optsArray.length()) { 
                opts.add(optsArray.getString(j)) 
            } 
            val optsFuriArray = qObj.optJSONArray("optionsFurigana") ?: JSONArray() 
            val optsFuri = mutableListOf<String>() 
            for (j in 0 until optsFuriArray.length()) { 
                optsFuri.add(optsFuriArray.getString(j)) 
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
        
        val artTagsArray = obj.optJSONArray("artTags") ?: JSONArray() 
        val artTags = mutableListOf<String>() 
        for (t in 0 until artTagsArray.length()) { 
            val tagStr = artTagsArray.optString(t, "").trim() 
            if (tagStr.isNotBlank()) { 
                artTags.add(tagStr) 
            } 
        } 
        
        return ForgedStory( 
            id = obj.optString("id", java.util.UUID.randomUUID().toString()), 
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()), 
            title = obj.optString("title", "Japanese Story"), 
            genre = obj.optString("genre", "General"), 
            level = obj.optString("level", "Intermediate"), 
            storyJapanese = obj.optString("storyJapanese", ""), 
            storyEnglish = obj.optString("storyEnglish", ""), 
            sentences = sentences, 
            targetWords = targetWords, 
            questions = questions, 
            visualAnchor = obj.optString("visualAnchor", ""), 
            artTags = artTags 
        ) 
    } 
    
    private fun serializeStoryToJson(story: ForgedStory): JSONObject { 
        return JSONObject().apply { 
            put("id", story.id) 
            put("createdAt", story.createdAt) 
            put("title", story.title) 
            put("genre", story.genre) 
            put("level", story.level) 
            put("storyJapanese", story.storyJapanese) 
            put("storyEnglish", story.storyEnglish) 
            put("visualAnchor", story.visualAnchor) 
            val atArray = JSONArray() 
            for (tag in story.artTags) { 
                atArray.put(tag) 
            } 
            put("artTags", atArray) 
            
            val sArray = JSONArray()
            for (s in story.sentences) { 
                sArray.put(JSONObject().apply { 
                    put("id", s.id) 
                    put("japanese", s.japanese) 
                    put("furigana", s.furigana) 
                    put("english", s.english) 
                    put("image", s.image) 
                    put("imagePrompt", s.imagePrompt) 
                    val twArray = JSONArray() 
                    for (tw in s.targetWords) { 
                        twArray.put(tw) 
                    } 
                    put("targetWords", twArray) 
                }) 
            }
            put("sentences", sArray)
            
            val wArray = JSONArray()
            for (w in story.targetWords) { 
                wArray.put(JSONObject().apply { 
                    put("kanji", w.kanji)
                    put("reading", w.reading)
                    put("meaning", w.meaning)
                })
            }
            put("targetWords", wArray)
            
            val qArray = JSONArray()
            for (q in story.questions) { 
                qArray.put(JSONObject().apply { 
                    put("id", q.id)
                    put("questionText", q.questionText)
                    val optArr = JSONArray()
                    for (opt in q.options) { 
                        optArr.put(opt)
                    }
                    put("options", optArr)
                    put("correctOptionIndex", q.correctOptionIndex)
                    put("explanation", q.explanation)
                })
            }
            put("questions", qArray)
        }
    }
    
    fun initCompletedStories(prefs: PreferencesManager, context: Context? = null) { 
        completedStoryIds = prefs.completedStoryIds 
        val raw = getRawCuratedStories() 
        for (item in raw) { 
            val page = prefs.getStoryProgressPage(item.id) 
            var fraction = if (completedStoryIds.contains(item.id)) 1f else prefs.getStoryProgressFraction(item.id) 
            if (fraction == 0f && page > 0) { 
                fraction = ((page + 1).toFloat() / 5f).coerceIn(0f, 1f) 
            } 
            storyPageMap[item.id] = page 
            storyProgressMap[item.id] = fraction 
        } 
        for (saved in savedStoriesList) { 
            val page = prefs.getStoryProgressPage(saved.id) 
            var fraction = if (completedStoryIds.contains(saved.id)) 1f else prefs.getStoryProgressFraction(saved.id) 
            val total = saved.sentences.size.coerceAtLeast(1) 
            if (fraction == 0f && page > 0) { 
                fraction = ((page + 1).toFloat() / total.toFloat()).coerceIn(0f, 1f) 
            } 
            storyPageMap[saved.id] = page 
            storyProgressMap[saved.id] = fraction 
        } 
        if (context != null) { 
            val assetStories = StoryAssetLoader.loadAllStories(context) 
            for (item in assetStories) { 
                val page = prefs.getStoryProgressPage(item.id) 
                var fraction = if (completedStoryIds.contains(item.id)) 1f else prefs.getStoryProgressFraction(item.id) 
                if (fraction == 0f && page > 0) { 
                    fraction = ((page + 1).toFloat() / 5f).coerceIn(0f, 1f) 
                } 
                storyPageMap[item.id] = page 
                storyProgressMap[item.id] = fraction 
            } 
        } 
        progressVersion++ 
    } 
    
    fun getCategoryStories(context: Context, category: String): List<ShinobiStoryModel> { 
        val list = StoryAssetLoader.loadStoriesForCategory(context, category) 
        return list.map { story -> 
            val isDone = completedStoryIds.contains(story.id) 
            story.copy( 
                isCompleted = isDone, 
                readingProgress = if (isDone) 1f else (storyProgressMap[story.id] ?: 0f) 
            ) 
        } 
    } 
    
    fun setStoryProgress(prefs: PreferencesManager, storyId: String, page: Int, totalPages: Int) { 
        val fraction = if (totalPages > 0) ((page + 1).toFloat() / totalPages.toFloat()).coerceIn(0f, 1f) else 0f 
        prefs.setStoryProgressPage(storyId, page) 
        prefs.setStoryProgressFraction(storyId, fraction) 
        storyPageMap[storyId] = page 
        storyProgressMap[storyId] = fraction 
        progressVersion++ 
    } 
    
    fun getSavedStoryPage(storyId: String): Int { 
        return storyPageMap[storyId] ?: 0 
    } 
    
    fun getSavedStoryProgress(storyId: String): Float { 
        return storyProgressMap[storyId] ?: 0f 
    } 
    
    fun markStoryCompleted(prefs: PreferencesManager, storyId: String) { 
        prefs.markStoryCompleted(storyId) 
        completedStoryIds = prefs.completedStoryIds 
        storyProgressMap[storyId] = 1f 
        progressVersion++ 
    } 
    
    fun isLevelUnlocked(subLevel: Int, completedIds: Set<String> = completedStoryIds): Boolean { 
        if (subLevel <= 1) return true 
        val raw = getRawCuratedStories() 
        for (lvl in 1 until subLevel) { 
            val lvlStories = raw.filter { it.subLevel == lvl } 
            val allDone = lvlStories.all { completedIds.contains(it.id) } 
            if (!allDone) return false 
        } 
        return true 
    } 
    
    fun getLevelCompletedCount(subLevel: Int, completedIds: Set<String> = completedStoryIds): Int { 
        val raw = getRawCuratedStories().filter { it.subLevel == subLevel } 
        return raw.count { completedIds.contains(it.id) } 
    } 
    
    fun getRawCuratedStories(): List<ShinobiStoryModel> { 
        val list = mutableListOf<ShinobiStoryModel>() 
        
        list.add( 
            ShinobiStoryModel( 
                id = "curated_groceries", 
                levelId = "starter", 
                subLevel = 1, 
                storyIndex = 1, 
                titleJapanese = "マリの買い物", 
                titleEnglish = "Mari Does Groceries", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "Mari visits the local supermarket to pick up fresh ingredients for dinner, learning vocabulary for everyday food items and shopping.", 
                author = "Mari" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_taro", 
                levelId = "starter", 
                subLevel = 1, 
                storyIndex = 2, 
                titleJapanese = "友達と遊ぶタロウ", 
                titleEnglish = "Taro Plays with Friends", 
                category = "Social", 
                estimatedMinutes = 5, 
                artworkType = "social", 
                summary = "Taro and his neighborhood friends spend a sunny afternoon playing hide and seek in the local park, learning about teamwork.", 
                author = "Kenji" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_house", 
                levelId = "starter", 
                subLevel = 1, 
                storyIndex = 3, 
                titleJapanese = "私の家で", 
                titleEnglish = "In My House", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "A warm look inside a traditional Japanese home, exploring family rooms, furniture, and daily household objects.", 
                author = "Logan" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_makoto", 
                levelId = "starter", 
                subLevel = 1, 
                storyIndex = 4, 
                titleJapanese = "マコトは学校へ行く", 
                titleEnglish = "Makoto Goes to School", 
                category = "School", 
                estimatedMinutes = 5, 
                artworkType = "school", 
                summary = "Follow Makoto on his morning commute to high school, encountering friends, trains, and morning greetings along the way.", 
                author = "Kenji" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_snail", 
                levelId = "starter", 
                subLevel = 1, 
                storyIndex = 5, 
                titleJapanese = "カタツムリの日", 
                titleEnglish = "The Snail Day", 
                category = "Nature", 
                estimatedMinutes = 5, 
                isEndingCard = true, 
                artworkType = "nature", 
                summary = "A quiet day in a rainy garden where a little snail finds an unexpected adventure.", 
                author = "Yuki" 
            ) 
        ) 
        
        list.add( 
            ShinobiStoryModel( 
                id = "curated_shrine", 
                levelId = "starter", 
                subLevel = 2, 
                storyIndex = 1, 
                titleJapanese = "神社へ", 
                titleEnglish = "To the Shrine", 
                category = "Traditional", 
                estimatedMinutes = 5, 
                artworkType = "traditional", 
                summary = "Walking the quiet stone path leading to the red torii gate of the local shrine.", 
                author = "Takeshi" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_yuki", 
                levelId = "starter", 
                subLevel = 2, 
                storyIndex = 2, 
                titleJapanese = "ユキが朝食を作る", 
                titleEnglish = "Yuki Makes Breakfast", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "Steaming rice, miso soup, and grilled salmon start a peaceful Sunday.", 
                author = "Yuki" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_bath", 
                levelId = "starter", 
                subLevel = 2, 
                storyIndex = 3, 
                titleJapanese = "ケンはお風呂に入る", 
                titleEnglish = "Ken Takes a Bath", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "Winding down after school with a soothing warm bath.", 
                author = "Ken" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_conbini", 
                levelId = "starter", 
                subLevel = 2, 
                storyIndex = 4, 
                titleJapanese = "コンビニで買い物", 
                titleEnglish = "Shopping at Conbini", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "Picking up onigiri and warm tea on a cool autumn evening.", 
                author = "Aoi" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_cafe", 
                levelId = "starter", 
                subLevel = 2, 
                storyIndex = 5, 
                titleJapanese = "雨の日のカフェ", 
                titleEnglish = "Rainy Day Cafe", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                isEndingCard = true, 
                artworkType = "daily", 
                summary = "Reading books while raindrops tap against the cafe window.", 
                author = "Rin" 
            ) 
        ) 
        
        list.add( 
            ShinobiStoryModel( 
                id = "curated_taku", 
                levelId = "starter", 
                subLevel = 3, 
                storyIndex = 1, 
                titleJapanese = "タクと雪だるま", 
                titleEnglish = "Taku and Snowman", 
                category = "Fantasy", 
                estimatedMinutes = 5, 
                artworkType = "fantasy", 
                summary = "A magical snowman comes to life for a playful winter adventure.", 
                author = "Hiroshi" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_body", 
                levelId = "starter", 
                subLevel = 3, 
                storyIndex = 2, 
                titleJapanese = "ボディパーツ", 
                titleEnglish = "Body Parts", 
                category = "Body", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "Explore common Japanese expressions and vocabulary for faces and body parts.", 
                author = "Kenji" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_daily", 
                levelId = "starter", 
                subLevel = 3, 
                storyIndex = 3, 
                titleJapanese = "毎日の日課", 
                titleEnglish = "Daily Routine", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "A cozy look into preparing a traditional Japanese morning breakfast.", 
                author = "Aoi" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_school", 
                levelId = "starter", 
                subLevel = 3, 
                storyIndex = 4, 
                titleJapanese = "文化祭", 
                titleEnglish = "School Festival", 
                category = "School", 
                estimatedMinutes = 5, 
                artworkType = "school", 
                summary = "Excitement builds as high school students prepare food stalls and festival banners.", 
                author = "Daiki" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_ending_winter", 
                levelId = "starter", 
                subLevel = 3, 
                storyIndex = 5, 
                titleJapanese = "冬のミステリー", 
                titleEnglish = "Winter Mystery", 
                category = "Mystery", 
                estimatedMinutes = 5, 
                isEndingCard = true, 
                artworkType = "fantasy", 
                summary = "The final challenge of Level 3. Discover what happens when the winter festival lights turn off.", 
                author = "Shin" 
            ) 
        ) 
        
        list.add( 
            ShinobiStoryModel( 
                id = "curated_station", 
                levelId = "starter", 
                subLevel = 4, 
                storyIndex = 1, 
                titleJapanese = "駅で", 
                titleEnglish = "At the Train Station", 
                category = "Travel", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "Boarding the train toward Tokyo Station during morning rush hour.", 
                author = "Sora" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_trip", 
                levelId = "starter", 
                subLevel = 4, 
                storyIndex = 2, 
                titleJapanese = "週末の旅行", 
                titleEnglish = "Weekend Trip", 
                category = "Travel", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "A weekend getaway to hot springs surrounded by mountains.", 
                author = "Rin" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_lost", 
                levelId = "starter", 
                subLevel = 4, 
                storyIndex = 3, 
                titleJapanese = "落とし物", 
                titleEnglish = "Lost and Found", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "Searching for a missing umbrella at the station office.", 
                author = "Kenji" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_pet", 
                levelId = "starter", 
                subLevel = 4, 
                storyIndex = 4, 
                titleJapanese = "ペットショップ", 
                titleEnglish = "The Pet Shop", 
                category = "Daily Life", 
                estimatedMinutes = 5, 
                artworkType = "daily", 
                summary = "Puppies and kittens welcome visitors on a sunny afternoon.", 
                author = "Aoi" 
            ) 
        ) 
        list.add( 
            ShinobiStoryModel( 
                id = "curated_mountain", 
                levelId = "starter", 
                subLevel = 4, 
                storyIndex = 5, 
                titleJapanese = "山登り", 
                titleEnglish = "Mountain Hike", 
                category = "Nature", 
                estimatedMinutes = 5, 
                isEndingCard = true, 
                artworkType = "nature", 
                summary = "Reaching the mountain peak just as the sunrise paints the sky.", 
                author = "Shin" 
            ) 
        ) 
        
        return list 
    } 
    
    fun getCuratedStories(levelId: String = "starter", completedIds: Set<String> = completedStoryIds): List<ShinobiStoryModel> { 
        val rawStories = getRawCuratedStories() 
        val result = mutableListOf<ShinobiStoryModel>() 
        
        var previousLevelCompleted = true 
        var hasFoundGlobalCurrent = false 
        
        for (lvl in 1..4) { 
            val levelStories = rawStories.filter { it.subLevel == lvl } 
            val isLevelUnlocked = (lvl == 1) || previousLevelCompleted 
            
            var allInLevelCompleted = true 
            var previousStoryInLevelCompleted = true 
            
            for (story in levelStories) { 
                val isCompleted = completedIds.contains(story.id) 
                if (!isCompleted) { 
                    allInLevelCompleted = false 
                } 
                
                val isLocked: Boolean 
                val isCurrent: Boolean 
                
                if (!isLevelUnlocked) { 
                    isLocked = true 
                    isCurrent = false 
                } else { 
                    val unlockedInLevel = (story.storyIndex == 1) || previousStoryInLevelCompleted 
                    isLocked = !unlockedInLevel 
                    if (!isLocked && !isCompleted && !hasFoundGlobalCurrent) { 
                        isCurrent = true 
                        hasFoundGlobalCurrent = true 
                    } else { 
                        isCurrent = false 
                    } 
                } 
                
                if (!isCompleted) { 
                    previousStoryInLevelCompleted = false 
                } 
                
                val curatedForged = buildCuratedForgedStory(story.id) 
                val computedEstMins = curatedForged?.calculatedEstimatedMinutes ?: story.estimatedMinutes 
                
                result.add( 
                    story.copy( 
                        isCompleted = isCompleted, 
                        isLocked = isLocked, 
                        isCurrent = isCurrent, 
                        isEndingCard = story.storyIndex == 5, 
                        xp = if (story.storyIndex == 5) 200 else 100, 
                        readingProgress = if (isCompleted) 1f else (storyProgressMap[story.id] ?: 0f), 
                        estimatedMinutes = computedEstMins 
                    ) 
                ) 
            } 
            previousLevelCompleted = allInLevelCompleted 
        } 
        return result.filter { it.levelId.equals(levelId, ignoreCase = true) } 
    } 
    
    fun buildCuratedForgedStory(id: String, context: Context? = null): ForgedStory? { 
        val builtIn = when (id) { 
            "curated_groceries" -> ForgedStory( 
                id = "curated_groceries", 
                title = "Mari Does Groceries - マリの買い物", 
                genre = "Daily Life", 
                level = "Starter", 
                storyJapanese = "マリは夕方に近くのスーパーへ行きました。今晩は美味しいカレーを作ります。野菜売り場で新鮮な玉ねぎと人参とじゃがいもを選びました。お肉の売り場で牛肉を買いました。レジでお金を払って、袋に詰めました。早く家に帰って料理を始めるのが楽しみです。", 
                storyEnglish = "Mari went to a nearby supermarket in the evening. Tonight she will make delicious curry. In the vegetable section, she chose fresh onions, carrots, and potatoes. She bought beef in the meat section. She paid at the register and packed her bag. She looks forward to going home quickly and starting to cook.", 
                sentences = listOf( 
                    StorySentenceItem(1, "マリは夕方に近くのスーパーへ行きました。", "Mari went to a nearby supermarket in the evening.", listOf("夕方", "スーパー")), 
                    StorySentenceItem(2, "今晩は美味しいカレーを作ります。", "Tonight she will make delicious curry.", listOf("今晩", "美味しい", "作ります")), 
                    StorySentenceItem(3, "野菜売り場で新鮮な玉ねぎと人参とじゃがいもを選びました。", "In the vegetable section, she chose fresh onions, carrots, and potatoes.", listOf("野菜", "新鮮", "選びました")), 
                    StorySentenceItem(4, "お肉の売り場で牛肉を買いました。", "She bought beef in the meat section.", listOf("肉", "牛肉", "買いました")), 
                    StorySentenceItem(5, "レジでお金を払って、袋に詰めました。", "She paid at the register and packed her bag.", listOf("お金", "払って", "袋")) 
                ), 
                targetWords = listOf( 
                    StoryWordItem("スーパー", "すーぱー", "supermarket"), 
                    StoryWordItem("野菜", "やさい", "vegetables"), 
                    StoryWordItem("新鮮", "しんせん", "fresh"), 
                    StoryWordItem("買う", "かう", "to buy"), 
                    StoryWordItem("お金", "おかね", "money"), 
                    StoryWordItem("料理", "りょうり", "cooking") 
                ), 
                questions = listOf( 
                    StoryQuestion(1, "マリは今晩何を作りますか？", listOf("寿司", "カレー", "ラーメン", "天ぷら"), 1, "「今晩は美味しいカレーを作ります」とあります。"), 
                    StoryQuestion(2, "マリはどこに行きましたか？", listOf("スーパー", "本屋", "学校", "病院"), 0, "近くのスーパーへ行きました。"), 
                    StoryQuestion(3, "野菜売り場で何を選びましたか？", listOf("りんごとみかん", "玉ねぎと人参とじゃがいも", "トマトとレタス", "いちご"), 1, "新鮮な玉ねぎと人参とじゃがいもを選びました。") 
                ) 
            ) 
            "curated_taro" -> ForgedStory( 
                id = "curated_taro", 
                title = "Taro Plays with Friends - 友達と遊ぶタロウ", 
                genre = "Social", 
                level = "Starter", 
                storyJapanese = "晴れた土曜日の午後、タロウは公園で友達と会いました。「みんなでかくれんぼをしよう！」とタロウが提案しました。みんな元気に走って隠れました。タロウは大きな桜の木の陰に静かに隠れました。鬼の友達が笑顔でタロウを見つけました。「見つけた！」と叫んで、二人で大笑いしました。日が暮れるまでチームワークを学びながら仲良く遊びました。", 
                storyEnglish = "On a sunny Saturday afternoon, Taro met his friends in the park. \"Let's all play hide and seek!\" Taro suggested. Everyone energetically ran and hid. Taro quietly hid behind a large cherry blossom tree. His friend who was \"it\" found Taro with a smile. \"Found you!\" he shouted, and both laughed out loud. They played happily together while learning teamwork until the sun set.", 
                sentences = listOf( 
                    StorySentenceItem(1, "晴れた土曜日の午後、タロウは公園で友達と会いました。", "On a sunny Saturday afternoon, Taro met his friends in the park.", listOf("晴れた", "午後", "公園", "友達", "会いました")), 
                    StorySentenceItem(2, "「みんなでかくれんぼをしよう！」とタロウが提案しました。", "\"Let's all play hide and seek!\" Taro suggested.", listOf("かくれんぼ", "提案")), 
                    StorySentenceItem(3, "タロウは大きな桜の木の陰に静かに隠れました。", "Taro quietly hid behind a large cherry blossom tree.", listOf("桜", "木", "隠れました")), 
                    StorySentenceItem(4, "鬼の友達が笑顔でタロウを見つけました。", "His friend who was it found Taro with a smile.", listOf("笑顔", "見つけました")), 
                    StorySentenceItem(5, "日が暮れるまでチームワークを学びながら仲良く遊びました。", "They played happily together while learning teamwork until sunset.", listOf("チームワーク", "学びながら", "仲良く")) 
                ), 
                targetWords = listOf( 
                    StoryWordItem("友達", "ともだち", "friend"), 
                    StoryWordItem("会いました", "あいました", "met / saw (a friend)"), 
                    StoryWordItem("公園", "こうえん", "park"), 
                    StoryWordItem("かくれんぼ", "かくれんぼ", "hide and seek"), 
                    StoryWordItem("木", "き", "tree"), 
                    StoryWordItem("笑顔", "えがお", "smile"), 
                    StoryWordItem("遊ぶ", "あそぶ", "to play") 
                ), 
                questions = listOf( 
                    StoryQuestion(1, "タロウは何の遊びを提案しましたか？", listOf("サッカー", "かくれんぼ", "鬼ごっこ", "野球"), 1, "「みんなでかくれんぼをしよう！」と提案しました。"), 
                    StoryQuestion(2, "タロウはどこに隠れましたか？", listOf("ベンチの下", "滑り台の裏", "大きな桜の木の陰", "トイレの後ろ"), 2, "大きな桜の木の陰に静かに隠れました。"), 
                    StoryQuestion(3, "友達はタロウをどうやって見つけましたか？", listOf("怒って", "泣きながら", "笑顔で", "走って"), 2, "笑顔でタロウを見つけました。") 
                ) 
            ) 
            "curated_house" -> ForgedStory( 
                id = "curated_house", 
                title = "In My House - 私の家で", 
                genre = "Daily Life", 
                level = "Starter", 
                storyJapanese = "ここは私の家です。日本の家では、まず玄関で靴を脱ぎます。リビングには家族が集まる温かいこたつがあります。隣の和室には畳の良い香りが広がっています。障子を開けると、緑の庭が見えます。台所からはお母さんが作るお味噌汁のいい匂いがします。家族と過ごすこの家が大好きです。", 
                storyEnglish = "This is my house. In a Japanese house, first we take off our shoes at the entrance. In the living room, there is a warm kotatsu where the family gathers. In the adjoining Japanese room, the pleasant scent of tatami spreads. When you open the sliding paper screen, you can see the green garden. From the kitchen comes the nice aroma of miso soup mother is making. I love this house spent with family.", 
                sentences = listOf( 
                    StorySentenceItem(1, "ここは私の家です。まず玄関で靴を脱ぎます。", "This is my house. First we take off shoes at the entrance.", listOf("家", "玄関", "靴")), 
                    StorySentenceItem(2, "リビングには家族が集まる温かいこたつがあります。", "In the living room, there is a warm kotatsu where family gathers.", listOf("家族", "温かい", "こたつ")), 
                    StorySentenceItem(3, "隣の和室には畳の良い香りが広がっています。", "In the adjoining Japanese room, the pleasant aroma of tatami spreads.", listOf("和室", "畳", "香り")), 
                    StorySentenceItem(4, "障子を開けると、緑の庭が見えます。", "When you open the screen, you see the green garden.", listOf("障子", "庭", "見えます")), 
                    StorySentenceItem(5, "台所からはお味噌汁のいい匂いがします。", "From the kitchen comes the nice aroma of miso soup.", listOf("台所", "お味噌汁", "匂い")) 
                ), 
                targetWords = listOf( 
                    StoryWordItem("家", "いえ", "house, home"), 
                    StoryWordItem("玄関", "げんかん", "entrance hall"), 
                    StoryWordItem("靴", "くつ", "shoes"), 
                    StoryWordItem("家族", "かぞく", "family"), 
                    StoryWordItem("畳", "たたみ", "tatami mat"), 
                    StoryWordItem("庭", "にわ", "garden") 
                ), 
                questions = listOf( 
                    StoryQuestion(1, "日本の家に入るとき、どこで靴を脱ぎますか？", listOf("リビング", "庭", "玄関", "台所"), 2, "まず玄関で靴を脱ぎます。"), 
                    StoryQuestion(2, "和室には何の良い香りが広がっていますか？", listOf("花", "畳", "お茶", "香水"), 1, "和室には畳の良い香りが広がっています。"), 
                    StoryQuestion(3, "台所から何の匂いがしてきましたか？", listOf("カレー", "お味噌汁", "パン", "焼き魚"), 1, "お母さんが作るお味噌汁のいい匂いがします。") 
                ) 
            ) 
            "curated_makoto" -> ForgedStory( 
                id = "curated_makoto", 
                title = "Makoto Goes to School - マコトは学校へ行く", 
                genre = "School", 
                level = "Starter", 
                storyJapanese = "マコトは朝七時に目覚まし時計で起きました。制服を着て、朝ごはんを食べました。「行ってきます！」と元気な声で言って玄関を出ました。爽やかな風を感じながら、駅まで十分歩きました。満員電車に揺られて高校に着きました。校門の前で、友達と先生に「おはようございます！」と元気に挨拶しました。新しい一日の始まりです。", 
                storyEnglish = "Makoto woke up at 7 a.m. to the alarm clock. He put on his uniform and ate breakfast. \"I'm heading out!\" he said in a lively voice and walked out the door. Feeling the refreshing breeze, he walked ten minutes to the station. Riding the crowded train, he arrived at high school. In front of the school gate, he cheerfully greeted his friends and teacher with \"Good morning!\" It is the beginning of a new day.", 
                sentences = listOf( 
                    StorySentenceItem(1, "マコトは朝七時に目覚まし時計で起きました。", "Makoto woke up at 7 a.m. to his alarm clock.", listOf("朝", "七時", "起きました")), 
                    StorySentenceItem(2, "制服を着て、朝ごはんを食べました。", "He put on his uniform and ate breakfast.", listOf("制服", "朝ごはん", "食べました")), 
                    StorySentenceItem(3, "「行ってきます！」と元気な声で言って玄関を出ました。", "\"I'm leaving!\" he said in a lively voice and left.", listOf("元気", "声", "出ました")), 
                    StorySentenceItem(4, "駅まで十分歩いて、電車に乗りました。", "He walked ten minutes to the station and boarded the train.", listOf("駅", "歩いて", "電車")), 
                    StorySentenceItem(5, "校門の前で、友達と先生に「おはようございます！」と挨拶しました。", "In front of the gate, he greeted friends and teacher.", listOf("校門", "先生", "挨拶")) 
                ), 
                targetWords = listOf( 
                    StoryWordItem("制服", "せいふく", "school uniform"), 
                    StoryWordItem("朝ごはん", "あさごはん", "breakfast"), 
                    StoryWordItem("駅", "えき", "train station"), 
                    StoryWordItem("電車", "でんしゃ", "train"), 
                    StoryWordItem("学校", "がっこう", "school"), 
                    StoryWordItem("挨拶", "あいさつ", "greeting") 
                ), 
                questions = listOf( 
                    StoryQuestion(1, "マコトは何時に起きましたか？", listOf("六時", "七時", "八時", "九時"), 1, "「朝七時に目覚まし時計で起きました」とあります。"), 
                    StoryQuestion(2, "マコトはどうやって学校へ行きましたか？", listOf("バス", "自転車", "電車", "車"), 2, "駅まで歩いて電車に乗りました。"), 
                    StoryQuestion(3, "校門の前で何と言って挨拶しましたか？", listOf("こんにちは", "こんばんは", "おはようございます", "さようなら"), 2, "「おはようございます！」と元気に挨拶しました。") 
                ) 
            ) 
            "curated_matt" -> ForgedStory( 
                id = "curated_matt", 
                title = "Matt Introduces Himself - マットの自己紹介", 
                genre = "Daily Life", 
                level = "Starter", 
                storyJapanese = "私 はアメリカ人 です。日本語 を勉強 しています。東京 の大学 に通っています。毎朝 、七時 に起きます。どうぞ よろしく お願いします。", 
                storyEnglish = "I am an American. I am studying Japanese. I attend university in Tokyo. Every morning, I wake up at 7 o'clock. Nice to meet you.", 
                sentences = listOf( 
                    StorySentenceItem(1, "私 はアメリカ人 です。", "I am an American.", listOf("私", "アメリカ人")), 
                    StorySentenceItem(2, "日本語 を勉強 しています。", "I am studying Japanese.", listOf("日本語", "勉強")), 
                    StorySentenceItem(3, "東京 の大学 に通っています。", "I attend university in Tokyo.", listOf("大学", "通っています")), 
                    StorySentenceItem(4, "毎朝 、七時 に起きます。", "Every morning, I wake up at 7 o'clock.", listOf("毎朝", "起きます")), 
                    StorySentenceItem(5, "どうぞ よろしく お願いします。", "Nice to meet you.", listOf("よろしくお願いします")) 
                ), 
                targetWords = listOf( 
                    StoryWordItem("私", "わたし", "I, me"), 
                    StoryWordItem("アメリカ人", "あめりかじん", "American person"), 
                    StoryWordItem("日本語", "にほんご", "Japanese language"), 
                    StoryWordItem("勉強", "べんきょう", "study"), 
                    StoryWordItem("大学", "だいがく", "university"), 
                    StoryWordItem("毎朝", "まいあさ", "every morning"), 
                    StoryWordItem("起きます", "おきます", "to wake up") 
                ), 
                questions = listOf( 
                    StoryQuestion(1, "マットはどこの国の人ですか？", listOf("イギリス人", "アメリカ人", "カナダ人", "オーストラリア人"), 1, "「私はアメリカ人です」と自己紹介しています。"), 
                    StoryQuestion(2, "マットは何を勉強していますか？", listOf("日本語", "フランス語", "中国語", "料理"), 0, "日本語を勉強しています。"), 
                    StoryQuestion(3, "マットは毎朝何時に起きますか？", listOf("六時", "七時", "八時", "九時"), 1, "「毎朝、七時に起きます」と言っています。") 
                ) 
            ) 
            "curated_kana" -> ForgedStory( 
                id = "curated_kana", 
                title = "Kana Draws a Boyfriend - 彼氏を描くカナ", 
                genre = "Romance", 
                level = "Starter", 
                storyJapanese = "カナはさみしい女の子です。「ボーイフレンドがほしいな」と、いつも思っています。ある日、カナは白いノートに男の子の絵を描きました。「かっこいいな」とカナは笑いました。その夜、絵から光が出ました。ノートの中の男の子が動き始めました！「こんにちは、カナ」と男の子は優しく言いました。カナはびっくりしました。二人は夜遅くまで楽しくおしゃべりをしました。カナはもうさみしくありませんでした。", 
                storyEnglish = "Kana is a lonely girl. \"I want a boyfriend,\" she always thinks. One day, Kana drew a picture of a boy in a white notebook. \"He looks cool,\" Kana laughed. That night, light came from the picture. The boy in the notebook started to move! \"Hello, Kana,\" the boy said gently. Kana was surprised. The two talked happily until late at night. Kana was no longer lonely.", 
                sentences = listOf( 
                    StorySentenceItem(1, "カナはさみしい女の子です。「ボーイフレンドがほしいな」と、いつも思っています。", "Kana is a lonely girl. \"I want a boyfriend,\" she always thinks.", listOf("女の子", "思っています")), 
                    StorySentenceItem(2, "ある日、カナは白いノートに男の子の絵を描きました。「かっこいいな」とカナは笑いました。", "One day, Kana drew a boy in a white notebook. \"He looks cool,\" Kana laughed.", listOf("男の子", "描きました")), 
                    StorySentenceItem(3, "その夜、絵から光が出ました。ノートの中の男の子が動き始めました！", "That night, light came from the drawing. The boy in the notebook started to move!", listOf("光", "動き始めました")), 
                    StorySentenceItem(4, "「こんにちは、カナ」と男の子は優しく言いました。カナはびっくりしました。", "\"Hello, Kana,\" the boy said gently. Kana was surprised.", listOf("優しく", "言いました")), 
                    StorySentenceItem(5, "二人は夜遅くまで楽しくおしゃべりをしました。カナはもうさみしくありませんでした。", "The two chatted happily until late at night. Kana was not lonely anymore.", listOf("夜遅く", "さみしくありませんでした")) 
                ), 
                targetWords = listOf( 
                    StoryWordItem("女の子", "おんなのこ", "girl"), 
                    StoryWordItem("描く", "えがく", "to draw"), 
                    StoryWordItem("光", "ひかり", "light"), 
                    StoryWordItem("動く", "うごく", "to move"), 
                    StoryWordItem("優しい", "やさしい", "gentle, kind") 
                ), 
                questions = listOf( 
                    StoryQuestion(1, "カナは何を描きましたか？", listOf("犬", "男の子", "花", "車"), 1, "カナはノートに男の子の絵を描きました。"), 
                    StoryQuestion(2, "夜にノートから何が出ましたか？", listOf("光", "水", "煙", "音"), 0, "その夜、絵から光が出ました。"), 
                    StoryQuestion(3, "カナは最後にどう感じましたか？", listOf("怒った", "もうさみしくない", "悲しい", "眠い"), 1, "カナはもうさみしくありませんでした。") 
                ) 
            ) 
            "curated_snail" -> ForgedStory( 
                id = "curated_snail", 
                title = "The Snail Day - カタツムリの日", 
                genre = "Nature", 
                level = "Starter", 
                storyJapanese = "今日は雨の日です。静かな庭の葉っぱの上に、小さなカタツムリがいました。カタツムリはゆっくりと進みます。雨粒がキラキラと光っています。大きな水たまりに着きました。「渡れるかな」とカタツムリは考えました。落ち葉が水に浮かんできました。カタツムリは船のように乗りました。向こう岸に着いて、カタツムリは嬉しそうに角を伸ばしました。", 
                storyEnglish = "Today is a rainy day. On a leaf in the quiet garden, there was a small snail. The snail moves slowly. Raindrops are sparkling. He arrived at a big puddle. \"Can I cross?\" thought the snail. A fallen leaf came floating on the water. The snail rode it like a boat. Reaching the opposite bank, the snail happily stretched out its horns.", 
                sentences = listOf( 
                    StorySentenceItem(1, "今日は雨の日です。静かな庭の葉っぱの上に、小さなカタツムリがいました。", "Today is a rainy day. On a leaf in the quiet garden, there was a small snail.", listOf("雨", "庭", "葉っぱ")), 
                    StorySentenceItem(2, "カタツムリはゆっくりと進みます。雨粒がキラキラと光っています。", "The snail moves slowly. Raindrops are sparkling.", listOf("ゆっくり", "進みます")), 
                    StorySentenceItem(3, "大きな水たまりに着きました。「渡れるかな」とカタツムリは考えました。", "He arrived at a big puddle. \"Can I cross?\" thought the snail.", listOf("水たまり", "考えました")), 
                    StorySentenceItem(4, "落ち葉が水に浮かんできました。カタツムリは船のように乗りました。", "A fallen leaf came floating on the water. The snail rode it like a boat.", listOf("落ち葉", "船")), 
                    StorySentenceItem(5, "向こう岸に着いて、カタツムリは嬉しそうに角を伸ばしました。", "Reaching the opposite bank, the snail happily stretched out its horns.", listOf("向こう岸", "角")) 
                ), 
                targetWords = listOf( 
                    StoryWordItem("雨", "あめ", "rain"), 
                    StoryWordItem("庭", "にわ", "garden"), 
                    StoryWordItem("葉っぱ", "はっぱ", "leaf"), 
                    StoryWordItem("ゆっくり", "ゆっくり", "slowly"), 
                    StoryWordItem("船", "ふね", "boat, ship") 
                ), 
                questions = listOf( 
                    StoryQuestion(1, "カタツムリはどこにいましたか？", listOf("部屋の中", "庭の葉っぱの上", "木の上", "机の上"), 1, "庭の葉っぱの上にいました。"), 
                    StoryQuestion(2, "カタツムリは何に乗って水を渡りましたか？", listOf("小石", "落ち葉", "木の枝", "花びら"), 1, "落ち葉を船のように使いました。") 
                ) 
            ) 
            "curated_taku" -> ForgedStory( 
                id = "curated_taku", 
                title = "Taku and Snowman - タクと雪だるま", 
                genre = "Fantasy", 
                level = "Starter", 
                storyJapanese = "朝起きると、庭が一面真っ白でした。雪がたくさん降っていました。タクは外に出て、大きな雪だるまを作りました。赤いマフラーを巻いてあげました。すると、雪だるまがパチパチと目を瞬かせました。「ありがとう、タク！」タクと雪だるまは、庭で一緒に雪合戦を始めました。とても楽しかったです。「また明日も遊ぼうね」とタクは約束しました。", 
                storyEnglish = "When he woke up in the morning, the garden was completely white. A lot of snow had fallen. Taku went outside and made a big snowman. He wrapped a red scarf around it. Then, the snowman blinked its eyes. \"Thank you, Taku!\" Taku and the snowman started a snowball fight in the garden together. It was so much fun. \"Let's play again tomorrow,\" Taku promised.", 
                sentences = listOf( 
                    StorySentenceItem(1, "朝起きると、庭が一面真っ白でした。雪がたくさん降っていました。", "When he woke up in the morning, the garden was completely white. A lot of snow had fallen.", listOf("朝", "庭", "雪")), 
                    StorySentenceItem(2, "タクは外に出て、大きな雪だるまを作りました。赤いマフラーを巻いてあげました。", "Taku went outside and made a big snowman. He wrapped a red scarf around it.", listOf("雪だるま", "赤い")), 
                    StorySentenceItem(3, "すると、雪だるまがパチパチと目を瞬かせました。「ありがとう、タク！」", "Then, the snowman blinked its eyes. \"Thank you, Taku!\"", listOf("目", "ありがとう")), 
                    StorySentenceItem(4, "タクと雪だるまは、庭で一緒に雪合戦を始めました。とても楽しかったです。", "Taku and the snowman started a snowball fight in the garden together. It was so much fun.", listOf("雪合戦", "楽しい")), 
                    StorySentenceItem(5, "「また明日も遊ぼうね」とタクは約束しました。", "\"Let's play again tomorrow,\" Taku promised.", listOf("明日", "約束")) 
                ), 
                targetWords = listOf( 
                    StoryWordItem("朝", "あさ", "morning"), 
                    StoryWordItem("雪だるま", "ゆきだるま", "snowman"), 
                    StoryWordItem("赤い", "あかい", "red"), 
                    StoryWordItem("雪合戦", "ゆきがっせん", "snowball fight"), 
                    StoryWordItem("約束", "やくそく", "promise") 
                ), 
                questions = listOf( 
                    StoryQuestion(1, "タクは何を作りましたか？", listOf("雪だるま", "雪の家", "氷の城", "滑り台"), 0, "タクは大きな雪だるまを作りました。"), 
                    StoryQuestion(2, "雪だるまに何を巻いてあげましたか？", listOf("青い帽子", "赤いマフラー", "黄色い手袋", "黒いベルト"), 1, "赤いマフラーを巻いてあげました。"), 
                    StoryQuestion(3, "二人は庭で何をしましたか？", listOf("かくれんぼ", "雪合戦", "スケート", "サッカー"), 1, "一緒に雪合戦を始めました。") 
                ) 
            ) 
            else -> null 
        } 
        if (builtIn != null) return builtIn 
        if (context != null) { 
            return StoryAssetLoader.buildForgedStoryFromAsset(context, id) 
        } 
        return null 
    } 

}
