package com.ankilock.data
    
import android.content.Context 
import android.content.SharedPreferences 
import org.json.JSONArray 
import org.json.JSONObject 
    
data class GeminiModelOption( 
    val id: String, 
    val name: String, 
    val description: String 
) 
    
data class FishAudioModelOption( 
    val id: String, 
    val name: String, 
    val description: String 
) 
    
data class FishAudioVoiceOption( 
    val id: String, 
    val name: String, 
    val author: String = "", 
    val description: String = "", 
    val tag: String = "", 
    val avatarUrl: String = "", 
    val likeCount: Int = 0, 
    val taskCount: Int = 0, 
    val tags: List<String> = emptyList(), 
    val sampleAudioUrl: String = "" 
) 
    
class PreferencesManager(context: Context) { 
    
    private val prefs: SharedPreferences = context.getSharedPreferences( 
        "ankilock_prefs", 
        Context.MODE_PRIVATE
    )
    
    var isServiceEnabled: Boolean
        get() = prefs.getBoolean(KEY_SERVICE_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SERVICE_ENABLED, value).apply()
    
    var selectedDeckIds: Set<String>
        get() = prefs.getStringSet(KEY_SELECTED_DECKS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_SELECTED_DECKS, value).apply()
    
    var updateIntervalMinutes: Int
        get() = prefs.getInt(KEY_UPDATE_INTERVAL, 30)
        set(value) = prefs.edit().putInt(KEY_UPDATE_INTERVAL, value).apply()
    
    var snoozeDurationMinutes: Int
        get() = prefs.getInt(KEY_SNOOZE_DURATION, 60)
        set(value) = prefs.edit().putInt(KEY_SNOOZE_DURATION, value).apply()
    
    var snoozeUntil: Long
        get() = prefs.getLong(KEY_SNOOZE_UNTIL, 0L)
        set(value) = prefs.edit().putLong(KEY_SNOOZE_UNTIL, value).apply()
    
    var isMusicPlayerStyle: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_PLAYER_STYLE, false)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_PLAYER_STYLE, value).apply()
    
    var backgroundType: String
        get() = prefs.getString(KEY_BACKGROUND_TYPE, "blossom") ?: "blossom"
        set(value) = prefs.edit().putString(KEY_BACKGROUND_TYPE, value).apply()
    
    var appBackgroundType: String 
        get() = prefs.getString(KEY_APP_BACKGROUND_TYPE, "blossom") ?: "blossom" 
        set(value) = prefs.edit().putString(KEY_APP_BACKGROUND_TYPE, value).apply() 
    
    var appCustomImageUri: String? 
        get() = prefs.getString(KEY_APP_CUSTOM_IMAGE_URI, null) 
        set(value) = prefs.edit().putString(KEY_APP_CUSTOM_IMAGE_URI, value).apply() 
    
    var appSavedImageUris: Set<String> 
        get() = prefs.getStringSet(KEY_APP_SAVED_IMAGE_URIS, emptySet()) ?: emptySet() 
        set(value) = prefs.edit().putStringSet(KEY_APP_SAVED_IMAGE_URIS, value).apply() 
    
    var appBlurRadius: Int 
        get() = prefs.getInt(KEY_APP_BLUR_RADIUS, 20) 
        set(value) = prefs.edit().putInt(KEY_APP_BLUR_RADIUS, value).apply() 
    
    var appDimOpacity: Float 
        get() = prefs.getFloat(KEY_APP_DIM_OPACITY, 0.10f) 
        set(value) = prefs.edit().putFloat(KEY_APP_DIM_OPACITY, value).apply() 
    
    var appArtworkOpacity: Float 
        get() = prefs.getFloat(KEY_APP_ARTWORK_OPACITY, 0.5f) 
        set(value) = prefs.edit().putFloat(KEY_APP_ARTWORK_OPACITY, value).apply() 
    
    var customImageUri: String?
        get() = prefs.getString(KEY_CUSTOM_IMAGE_URI, null)
        set(value) = prefs.edit().putString(KEY_CUSTOM_IMAGE_URI, value).apply()
    
    var savedImageUris: Set<String>
        get() = prefs.getStringSet(KEY_SAVED_IMAGE_URIS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_SAVED_IMAGE_URIS, value).apply()
    
    var blurRadius: Int
        get() = prefs.getInt(KEY_BLUR_RADIUS, 20)
        set(value) = prefs.edit().putInt(KEY_BLUR_RADIUS, value).apply()
    
    var dimOpacity: Float
        get() = prefs.getFloat(KEY_DIM_OPACITY, 0.10f)
        set(value) = prefs.edit().putFloat(KEY_DIM_OPACITY, value).apply()
    
    var artworkOpacity: Float
        get() = prefs.getFloat(KEY_ARTWORK_OPACITY, 0.5f)
        set(value) = prefs.edit().putFloat(KEY_ARTWORK_OPACITY, value).apply()
    
    var classicRevealedAction: String
        get() = prefs.getString(KEY_CLASSIC_REVEALED_ACTION, "undo") ?: "undo"
        set(value) = prefs.edit().putString(KEY_CLASSIC_REVEALED_ACTION, value).apply()
    
    var isAutoPlayAudio: Boolean
        get() = prefs.getBoolean(KEY_AUTOPLAY_AUDIO, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTOPLAY_AUDIO, value).apply()
    
    var autoPlayAudioMode: Int 
        get() = prefs.getInt(KEY_AUTOPLAY_AUDIO_MODE, if (isAutoPlayAudio) 3 else 0) 
        set(value) = prefs.edit().putInt(KEY_AUTOPLAY_AUDIO_MODE, value).putBoolean(KEY_AUTOPLAY_AUDIO, value > 0).apply() 
    
    var aiApiKey: String
        get() = prefs.getString(KEY_AI_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_AI_API_KEY, value).apply()
    
    var aiProvider: String
        get() = prefs.getString(KEY_AI_PROVIDER, "gemini") ?: "gemini"
        set(value) = prefs.edit().putString(KEY_AI_PROVIDER, value).apply()
    
    var aiModel: String
        get() = prefs.getString(KEY_AI_MODEL, "auto") ?: "auto"
        set(value) = prefs.edit().putString(KEY_AI_MODEL, value).apply()
    
    var wallhavenApiKey: String 
        get() = prefs.getString(KEY_WALLHAVEN_API_KEY, "") ?: "" 
        set(value) = prefs.edit().putString(KEY_WALLHAVEN_API_KEY, value).apply() 
    
    var geminiApiKey: String? 
        get() = if (aiApiKey.isNotBlank()) aiApiKey else prefs.getString(KEY_GEMINI_API_KEY, null)?.takeIf { it.isNotBlank() } 
        set(value) { 
            aiApiKey = value ?: "" 
            prefs.edit().putString(KEY_GEMINI_API_KEY, value?.trim()).apply() 
        } 
    
    var geminiModel: String 
        get() { 
            val saved = prefs.getString(KEY_GEMINI_MODEL, null) 
            return if (saved.isNullOrBlank() || saved.startsWith("gemini-1.") || saved.startsWith("gemini-2.")) { 
                DEFAULT_GEMINI_MODEL 
            } else { 
                saved 
            } 
        } 
        set(value) = prefs.edit().putString(KEY_GEMINI_MODEL, value.trim()).apply() 
    
    var hasAcceptedInternetDisclosure: Boolean 
        get() = prefs.getBoolean(KEY_INTERNET_DISCLOSURE, false) 
        set(value) = prefs.edit().putBoolean(KEY_INTERNET_DISCLOSURE, value).apply() 
    
    var highlightVocabularyWords: Boolean 
        get() = prefs.getBoolean(KEY_HIGHLIGHT_VOCABULARY_WORDS, true) 
        set(value) = prefs.edit().putBoolean(KEY_HIGHLIGHT_VOCABULARY_WORDS, value).apply() 
    
    var lastReadStoryId: String? 
        get() = prefs.getString(KEY_LAST_READ_STORY_ID, null) 
        set(value) = prefs.edit().putString(KEY_LAST_READ_STORY_ID, value).apply() 
    
    var readingBackgroundImageUri: String? 
        get() = prefs.getString(KEY_READING_BACKGROUND_IMAGE_URI, null)?.takeIf { it.isNotBlank() } 
        set(value) = prefs.edit().putString(KEY_READING_BACKGROUND_IMAGE_URI, value?.trim()).apply() 
    
    var fishAudioApiKey: String? 
        get() = prefs.getString(KEY_FISH_AUDIO_API_KEY, null)?.takeIf { it.isNotBlank() } 
        set(value) = prefs.edit().putString(KEY_FISH_AUDIO_API_KEY, value?.trim()).apply() 
    
    var fishAudioVoiceId: String 
        get() { 
            val saved = prefs.getString(KEY_FISH_AUDIO_VOICE_ID, null) 
            return if (saved.isNullOrBlank()) DEFAULT_FISH_AUDIO_VOICE_ID else extractVoiceId(saved) 
        } 
        set(value) = prefs.edit().putString(KEY_FISH_AUDIO_VOICE_ID, extractVoiceId(value).trim()).apply() 
    
    var fishAudioVoiceName: String? 
        get() = prefs.getString(KEY_FISH_AUDIO_VOICE_NAME, null)?.takeIf { it.isNotBlank() } 
        set(value) = prefs.edit().putString(KEY_FISH_AUDIO_VOICE_NAME, value?.trim()).apply() 
    
    var fishAudioModel: String 
        get() = prefs.getString(KEY_FISH_AUDIO_MODEL, DEFAULT_FISH_AUDIO_MODEL) ?: DEFAULT_FISH_AUDIO_MODEL 
        set(value) = prefs.edit().putString(KEY_FISH_AUDIO_MODEL, value.trim()).apply() 
    
    var favoriteFishAudioVoiceIds: Set<String> 
        get() { 
            val raw = prefs.getString(KEY_FAVORITE_FISH_AUDIO_VOICES + "_str", null) 
            if (raw != null) { 
                return raw.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }.toSet() 
            } 
            val oldSet = prefs.getStringSet(KEY_FAVORITE_FISH_AUDIO_VOICES, emptySet()) ?: emptySet() 
            return oldSet.map { extractVoiceId(it).lowercase().trim() }.filter { it.isNotBlank() }.toSet() 
        } 
        set(value) { 
            val cleanSet = value.map { extractVoiceId(it).lowercase().trim() }.filter { it.isNotBlank() }.toSet() 
            prefs.edit() 
                .putString(KEY_FAVORITE_FISH_AUDIO_VOICES + "_str", cleanSet.joinToString(",")) 
                .putStringSet(KEY_FAVORITE_FISH_AUDIO_VOICES, HashSet(cleanSet)) 
                .commit() 
        } 
    
    fun toggleFavoriteVoice(voiceId: String): Boolean { 
        val cleanId = extractVoiceId(voiceId).lowercase().trim() 
        val current = favoriteFishAudioVoiceIds.toMutableSet() 
        val isFav = if (current.contains(cleanId)) { 
            current.remove(cleanId) 
            false 
        } else { 
            current.add(cleanId) 
            true 
        } 
        favoriteFishAudioVoiceIds = current 
        return isFav 
    } 
    
    fun toggleFavoriteVoice(voice: FishAudioVoiceOption): Boolean { 
        val cleanId = extractVoiceId(voice.id).lowercase().trim() 
        val current = favoriteFishAudioVoiceIds.toMutableSet() 
        val isFav = if (current.contains(cleanId)) { 
            current.remove(cleanId) 
            removeCustomVoice(cleanId) 
            false 
        } else { 
            current.add(cleanId) 
            saveCustomVoice(voice.copy(id = cleanId)) 
            true 
        } 
        favoriteFishAudioVoiceIds = current 
        return isFav 
    } 
    
    fun getSavedCustomVoices(): List<FishAudioVoiceOption> { 
        val jsonStr = prefs.getString(KEY_SAVED_CUSTOM_VOICES, null) ?: return emptyList() 
        return try { 
            val jsonArray = JSONArray(jsonStr) 
            val list = mutableListOf<FishAudioVoiceOption>() 
            for (i in 0 until jsonArray.length()) { 
                val obj = jsonArray.getJSONObject(i) 
                val tagsArr = obj.optJSONArray("tags") 
                val tagsList = mutableListOf<String>() 
                if (tagsArr != null) { 
                    for (t in 0 until tagsArr.length()) { 
                        tagsList.add(tagsArr.getString(t)) 
                    } 
                } 
                val rawId = obj.optString("id", "") 
                val cleanId = extractVoiceId(rawId).lowercase().trim() 
                list.add( 
                    FishAudioVoiceOption( 
                        id = cleanId, 
                        name = obj.optString("name", ""), 
                        author = obj.optString("author", ""), 
                        description = obj.optString("description", ""), 
                        tag = obj.optString("tag", ""), 
                        avatarUrl = obj.optString("avatarUrl", ""), 
                        likeCount = obj.optInt("likeCount", 0), 
                        taskCount = obj.optInt("taskCount", 0), 
                        tags = tagsList, 
                        sampleAudioUrl = obj.optString("sampleAudioUrl", "") 
                    ) 
                ) 
            } 
            list 
        } catch (_: Exception) { 
            emptyList() 
        } 
    } 

    fun saveCustomVoice(voice: FishAudioVoiceOption) { 
        val cleanId = extractVoiceId(voice.id).lowercase().trim() 
        val normalizedVoice = voice.copy(id = cleanId) 
        val current = getSavedCustomVoices().toMutableList() 
        current.removeAll { extractVoiceId(it.id).equals(cleanId, ignoreCase = true) } 
        current.add(0, normalizedVoice) 
        val jsonArray = JSONArray() 
        for (v in current) { 
            val vCleanId = extractVoiceId(v.id).lowercase().trim() 
            val obj = JSONObject().apply { 
                put("id", vCleanId) 
                put("name", v.name) 
                put("author", v.author) 
                put("description", v.description) 
                put("tag", v.tag) 
                put("avatarUrl", v.avatarUrl) 
                put("likeCount", v.likeCount) 
                put("taskCount", v.taskCount) 
                put("sampleAudioUrl", v.sampleAudioUrl) 
                val tagsArr = JSONArray() 
                v.tags.forEach { tagsArr.put(it) } 
                put("tags", tagsArr) 
            } 
            jsonArray.put(obj) 
        } 
        prefs.edit().putString(KEY_SAVED_CUSTOM_VOICES, jsonArray.toString()).commit() 
    } 

    fun removeCustomVoice(voiceId: String) { 
        val cleanId = extractVoiceId(voiceId).lowercase().trim() 
        val current = getSavedCustomVoices().filterNot { extractVoiceId(it.id).equals(cleanId, ignoreCase = true) } 
        val jsonArray = JSONArray() 
        for (v in current) { 
            val vCleanId = extractVoiceId(v.id).lowercase().trim() 
            val obj = JSONObject().apply { 
                put("id", vCleanId) 
                put("name", v.name) 
                put("author", v.author) 
                put("description", v.description) 
                put("tag", v.tag) 
                put("avatarUrl", v.avatarUrl) 
                put("likeCount", v.likeCount) 
                put("taskCount", v.taskCount) 
                put("sampleAudioUrl", v.sampleAudioUrl) 
                val tagsArr = JSONArray() 
                v.tags.forEach { tagsArr.put(it) } 
                put("tags", tagsArr) 
            } 
            jsonArray.put(obj) 
        } 
        prefs.edit().putString(KEY_SAVED_CUSTOM_VOICES, jsonArray.toString()).commit() 
    } 

    fun isVoiceFavorite(voiceId: String): Boolean { 
        val cleanId = extractVoiceId(voiceId).lowercase().trim() 
        return favoriteFishAudioVoiceIds.contains(cleanId) 
    } 
    
    var disabledStoryThemes: Set<String> 
        get() = prefs.getStringSet(KEY_DISABLED_STORY_THEMES, emptySet()) ?: emptySet() 
        set(value) = prefs.edit().putStringSet(KEY_DISABLED_STORY_THEMES, value).apply() 
    
    var disabledStoryTopics: Set<String> 
        get() = prefs.getStringSet(KEY_DISABLED_STORY_TOPICS, emptySet()) ?: emptySet() 
        set(value) = prefs.edit().putStringSet(KEY_DISABLED_STORY_TOPICS, value).apply() 
    
    var customStoryTheme: String? 
        get() = prefs.getString(KEY_CUSTOM_STORY_THEME, null) 
        set(value) = prefs.edit().putString(KEY_CUSTOM_STORY_THEME, value).apply() 
    
    var customStoryTopic: String? 
        get() = prefs.getString(KEY_CUSTOM_STORY_TOPIC, null) 
        set(value) = prefs.edit().putString(KEY_CUSTOM_STORY_TOPIC, value).apply() 
    
    var isCustomThemeModeActive: Boolean 
        get() = prefs.getBoolean(KEY_CUSTOM_THEME_MODE_ACTIVE, false) 
        set(value) = prefs.edit().putBoolean(KEY_CUSTOM_THEME_MODE_ACTIVE, value).apply() 
    
    var appTheme: String 
        get() = prefs.getString(KEY_APP_THEME, "dim") ?: "dim" 
        set(value) = prefs.edit().putString(KEY_APP_THEME, value).apply() 
    
    var storyGenre: String
        get() = prefs.getString(KEY_STORY_GENRE, "IT & Workplace") ?: "IT & Workplace"
        set(value) = prefs.edit().putString(KEY_STORY_GENRE, value).apply()
    
    var storyLevel: String 
        get() = prefs.getString(KEY_STORY_LEVEL, "Intermediate") ?: "Intermediate" 
        set(value) = prefs.edit().putString(KEY_STORY_LEVEL, value).apply() 
    
    var completedStoryIds: Set<String> 
        get() = prefs.getStringSet(KEY_COMPLETED_STORIES, emptySet()) ?: emptySet() 
        set(value) = prefs.edit().putStringSet(KEY_COMPLETED_STORIES, value).apply() 
    
    var passedStoryIds: Set<String> 
        get() = prefs.getStringSet(KEY_PASSED_STORIES, emptySet()) ?: emptySet() 
        set(value) = prefs.edit().putStringSet(KEY_PASSED_STORIES, value).apply() 
    
    fun markStoryPassed(storyId: String) { 
        val alreadyPassed = passedStoryIds.contains(storyId) 
        val set = passedStoryIds.toMutableSet() 
        set.add(storyId) 
        passedStoryIds = set 
        if (!alreadyPassed) { 
            recordStoryTestPassed() 
        } 
    } 
    
    var storyDailyEnergyRemaining: Int 
        get() { 
            checkAndResetDailyEnergy() 
            return prefs.getInt(KEY_DAILY_STORY_ENERGY_REMAINING, MAX_DAILY_STORY_ENERGY) 
        } 
        set(value) = prefs.edit().putInt(KEY_DAILY_STORY_ENERGY_REMAINING, value).apply() 
    
    fun checkAndResetDailyEnergy() { 
        val today = getTodayDateString() 
        val recordedDate = prefs.getString(KEY_DAILY_STORY_ENERGY_DATE, "") ?: "" 
        if (recordedDate != today) { 
            prefs.edit() 
                .putString(KEY_DAILY_STORY_ENERGY_DATE, today) 
                .putInt(KEY_DAILY_STORY_ENERGY_REMAINING, MAX_DAILY_STORY_ENERGY) 
                .apply() 
        } 
    } 
    
    fun consumeDailyStoryEnergy(): Boolean { 
        checkAndResetDailyEnergy() 
        val current = storyDailyEnergyRemaining 
        if (current <= 0) return false 
        storyDailyEnergyRemaining = current - 1 
        return true 
    } 
    
    var storyLanguage: String 
        get() = prefs.getString(KEY_STORY_LANGUAGE, "ja") ?: "ja" 
        set(value) = prefs.edit().putString(KEY_STORY_LANGUAGE, value).apply() 
    
    fun getStoryProgressPage(storyId: String): Int { 
        return prefs.getInt(KEY_STORY_PAGE_PREFIX + storyId, 0) 
    } 
    
    fun setStoryProgressPage(storyId: String, page: Int) { 
        prefs.edit().putInt(KEY_STORY_PAGE_PREFIX + storyId, page).apply() 
    } 
    
    fun getStoryProgressFraction(storyId: String): Float { 
        return prefs.getFloat(KEY_STORY_FRACTION_PREFIX + storyId, 0f) 
    } 
    
    fun setStoryProgressFraction(storyId: String, fraction: Float) { 
        prefs.edit().putFloat(KEY_STORY_FRACTION_PREFIX + storyId, fraction).apply() 
    } 
    
    fun markStoryCompleted(storyId: String) { 
        val set = completedStoryIds.toMutableSet() 
        set.add(storyId) 
        completedStoryIds = set 
        setStoryProgressFraction(storyId, 1f) 
    } 
    
    val isSnoozed: Boolean 
        get() = System.currentTimeMillis() < snoozeUntil 
    
    fun addSavedImageUri(uriStr: String) { 
        val set = savedImageUris.toMutableSet()
        set.add(uriStr)
        savedImageUris = set
    }
    
    fun removeSavedImageUri(uriStr: String) { 
        val set = savedImageUris.toMutableSet()
        set.remove(uriStr)
        savedImageUris = set
        if (customImageUri == uriStr) { 
            customImageUri = set.firstOrNull()
            if (customImageUri == null) { 
                backgroundType = "transparent"
            }
        }
    }
    
    fun addSavedAppImageUri(uriStr: String) { 
        val set = appSavedImageUris.toMutableSet() 
        set.add(uriStr) 
        appSavedImageUris = set 
    } 
    
    fun removeSavedAppImageUri(uriStr: String) { 
        val set = appSavedImageUris.toMutableSet() 
        set.remove(uriStr) 
        appSavedImageUris = set 
        if (appCustomImageUri == uriStr) { 
            appCustomImageUri = set.firstOrNull() 
            if (appCustomImageUri == null) { 
                appBackgroundType = "none" 
            } 
        } 
    } 
    
    fun getSelectedDeckIdsAsLongs(): Set<Long> { 
        return selectedDeckIds.mapNotNull { it.toLongOrNull() }.toSet()
    }
    
    var readingJlptLevel: String 
        get() = prefs.getString("reading_jlpt_level", storyLevel.ifBlank { "N5" }) ?: "N5" 
        set(value) = prefs.edit().putString("reading_jlpt_level", value).apply() 
    
    var connectStudiedWords: Boolean 
        get() = prefs.getBoolean("connect_studied_words", true) 
        set(value) = prefs.edit().putBoolean("connect_studied_words", value).apply() 
    
    var storyShowPronunciation: Boolean
        get() = prefs.getBoolean(KEY_STORY_SHOW_PRONUNCIATION, true)
        set(value) = prefs.edit().putBoolean(KEY_STORY_SHOW_PRONUNCIATION, value).apply()
    
    var storyPronunciationType: String
        get() = prefs.getString(KEY_STORY_PRONUNCIATION_TYPE, "japanese") ?: "japanese"
        set(value) = prefs.edit().putString(KEY_STORY_PRONUNCIATION_TYPE, value).apply()
    
    var storyEnlargeFont: Boolean
        get() = prefs.getBoolean(KEY_STORY_ENLARGE_FONT, false)
        set(value) = prefs.edit().putBoolean(KEY_STORY_ENLARGE_FONT, value).apply()
    
    var storyShowImages: Boolean
        get() = prefs.getBoolean(KEY_STORY_SHOW_IMAGES, true)
        set(value) = prefs.edit().putBoolean(KEY_STORY_SHOW_IMAGES, value).apply()
    
    var storyHighlightAudio: Boolean
        get() = prefs.getBoolean(KEY_STORY_HIGHLIGHT_AUDIO, true)
        set(value) = prefs.edit().putBoolean(KEY_STORY_HIGHLIGHT_AUDIO, value).apply()
    
    var storyLength: String 
        get() = prefs.getString(KEY_STORY_LENGTH, "Medium") ?: "Medium" 
        set(value) = prefs.edit().putString(KEY_STORY_LENGTH, value).apply() 
    
    var storyConnectingWordsCount: Int 
        get() = prefs.getInt("story_connecting_words_count", 4) 
        set(value) = prefs.edit().putInt("story_connecting_words_count", value).apply() 
    
    var storyQuestionsCount: Int 
        get() = prefs.getInt("story_questions_count", 3).coerceIn(3, 5) 
        set(value) = prefs.edit().putInt("story_questions_count", value.coerceIn(3, 5)).apply() 
    
    var showFuriganaInReader: Boolean 
        get() = prefs.getBoolean("show_furigana_in_reader", true) 
        set(value) = prefs.edit().putBoolean("show_furigana_in_reader", value).apply() 
    
    fun getSpeedStrikeHighScore(tier: String): Int { 
        return prefs.getInt("speed_strike_high_score_${tier.lowercase()}", 0) 
    } 
    
    fun setSpeedStrikeHighScore(tier: String, score: Int) { 
        val current = getSpeedStrikeHighScore(tier) 
        if (score > current) { 
            prefs.edit().putInt("speed_strike_high_score_${tier.lowercase()}", score).apply() 
        } 
    } 
    
    fun getSpeedStrikeBestStreak(tier: String): Int { 
        return prefs.getInt("speed_strike_best_streak_${tier.lowercase()}", 0) 
    } 
    
    fun setSpeedStrikeBestStreak(tier: String, streak: Int) { 
        val current = getSpeedStrikeBestStreak(tier) 
        if (streak > current) { 
            prefs.edit().putInt("speed_strike_best_streak_${tier.lowercase()}", streak).apply() 
        } 
    } 
    
    var recentJishoSearches: List<String> 
        get() { 
            val raw = prefs.getString(KEY_RECENT_JISHO_SEARCHES, "") ?: "" 
            return if (raw.isBlank()) emptyList() else raw.split("|||") 
        } 
        set(value) { 
            prefs.edit().putString(KEY_RECENT_JISHO_SEARCHES, value.joinToString("|||")).apply() 
        } 
    
    fun addRecentJishoSearch(term: String) { 
        val trimmed = term.trim() 
        if (trimmed.isBlank()) return 
        val current = recentJishoSearches.toMutableList() 
        current.removeAll { it.equals(trimmed, ignoreCase = true) } 
        current.add(0, trimmed) 
        recentJishoSearches = current.take(15) 
    } 
    
    fun clearRecentJishoSearches() { 
        recentJishoSearches = emptyList() 
    } 
    
    fun removeRecentJishoSearch(term: String) { 
        val current = recentJishoSearches.toMutableList() 
        current.removeAll { it.equals(term.trim(), ignoreCase = true) } 
        recentJishoSearches = current 
    } 
    
    var storyVocabularyFilter: String 
        get() = prefs.getString(KEY_STORY_VOCABULARY_FILTER, "all") ?: "all" 
        set(value) = prefs.edit().putString(KEY_STORY_VOCABULARY_FILTER, value).apply() 
    
    var dailyStreakCount: Int 
        get() { 
            checkAndResetDailyProgress() 
            val today = getTodayDateString() 
            val yesterday = getYesterdayDateString() 
            val dates = streakCompletedDates.toMutableSet() 
            val storedCount = prefs.getInt(KEY_DAILY_STREAK_COUNT, 0) 
            val lastDate = prefs.getString(KEY_LAST_COMPLETED_STREAK_DATE, "") ?: "" 
            if (storedCount > 0 && lastDate.isNotEmpty()) { 
                try { 
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US) 
                    val cal = java.util.Calendar.getInstance() 
                    val parsed = sdf.parse(lastDate) 
                    if (parsed != null) { 
                        cal.time = parsed 
                        var backfilled = false 
                        for (i in 0 until storedCount) { 
                            val dStr = sdf.format(cal.time) 
                            if (dates.add(dStr)) { 
                                backfilled = true 
                            } 
                            cal.add(java.util.Calendar.DAY_OF_YEAR, -1) 
                        } 
                        if (backfilled) { 
                            streakCompletedDates = dates 
                        } 
                    } 
                } catch (e: Exception) { 
                } 
            } 
            val calculated = calculateStreakFromDates(dates, today, yesterday) 
            if (calculated != storedCount) { 
                prefs.edit().putInt(KEY_DAILY_STREAK_COUNT, calculated).apply() 
            } 
            return calculated 
        } 
        set(value) = prefs.edit().putInt(KEY_DAILY_STREAK_COUNT, value).apply() 
    
    var lastCompletedStreakDate: String 
        get() = prefs.getString(KEY_LAST_COMPLETED_STREAK_DATE, "") ?: "" 
        set(value) = prefs.edit().putString(KEY_LAST_COMPLETED_STREAK_DATE, value).apply() 
    
    var streakViewMode: String 
        get() = prefs.getString(KEY_STREAK_VIEW_MODE, "calendar") ?: "calendar" 
        set(value) = prefs.edit().putString(KEY_STREAK_VIEW_MODE, value).apply() 
    
    var todayLearnedCardsCount: Int 
        get() { 
            checkAndResetDailyProgress() 
            return prefs.getInt(KEY_TODAY_LEARNED_CARDS_COUNT, 0) 
        } 
        set(value) = prefs.edit().putInt(KEY_TODAY_LEARNED_CARDS_COUNT, value).apply() 
    
    var todayStoryCompleted: Boolean 
        get() { 
            checkAndResetDailyProgress() 
            return prefs.getBoolean(KEY_TODAY_STORY_COMPLETED, false) 
        } 
        set(value) = prefs.edit().putBoolean(KEY_TODAY_STORY_COMPLETED, value).apply() 
    
    var todayProgressDate: String 
        get() = prefs.getString(KEY_TODAY_PROGRESS_DATE, "") ?: "" 
        set(value) = prefs.edit().putString(KEY_TODAY_PROGRESS_DATE, value).apply() 
    
    private fun getTodayDateString(): String { 
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()) 
    } 
    
    private fun getYesterdayDateString(): String { 
        val cal = java.util.Calendar.getInstance() 
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1) 
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(cal.time) 
    } 
    
    fun calculateStreakFromDates(dates: Set<String>, today: String, yesterday: String): Int { 
        if (dates.isEmpty()) { 
            return 0 
        } 
        val startCal = java.util.Calendar.getInstance() 
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US) 
        if (dates.contains(today)) { 
            try { 
                val parsed = sdf.parse(today) ?: return 0 
                startCal.time = parsed 
            } catch (e: Exception) { 
                return 0 
            } 
        } else if (dates.contains(yesterday)) { 
            try { 
                val parsed = sdf.parse(yesterday) ?: return 0 
                startCal.time = parsed 
            } catch (e: Exception) { 
                return 0 
            } 
        } else { 
            return 0 
        } 
        
        var count = 0 
        while (true) { 
            val checkDate = sdf.format(startCal.time) 
            if (dates.contains(checkDate)) { 
                count++ 
                startCal.add(java.util.Calendar.DAY_OF_YEAR, -1) 
            } else { 
                break 
            } 
        } 
        return count 
    } 
    
    fun checkAndResetDailyProgress() { 
        val today = getTodayDateString() 
        val recordedDate = prefs.getString(KEY_TODAY_PROGRESS_DATE, "") ?: "" 
        if (recordedDate != today) { 
            val yesterday = getYesterdayDateString() 
            val calculated = calculateStreakFromDates(streakCompletedDates, today, yesterday) 
            prefs.edit() 
                .putString(KEY_TODAY_PROGRESS_DATE, today) 
                .putInt(KEY_TODAY_LEARNED_CARDS_COUNT, 0) 
                .putBoolean(KEY_TODAY_STORY_COMPLETED, false) 
                .putInt(KEY_DAILY_STREAK_COUNT, calculated) 
                .apply() 
        } 
    } 
    
    fun recordNewCardLearned() { 
        checkAndResetDailyProgress() 
        val currentCount = prefs.getInt(KEY_TODAY_LEARNED_CARDS_COUNT, 0) + 1 
        prefs.edit().putInt(KEY_TODAY_LEARNED_CARDS_COUNT, currentCount).apply() 
        evaluateDailyStreak() 
    } 
    
    fun recordStoryCompletedToday() { 
        checkAndResetDailyProgress() 
        prefs.edit().putBoolean(KEY_TODAY_STORY_COMPLETED, true).apply() 
        evaluateDailyStreak() 
    } 
    
    var streakCompletedDates: Set<String> 
        get() = prefs.getStringSet(KEY_STREAK_COMPLETED_DATES, emptySet()) ?: emptySet() 
        set(value) = prefs.edit().putStringSet(KEY_STREAK_COMPLETED_DATES, value).apply() 
    
    fun addStreakCompletedDate(date: String) { 
        val current = streakCompletedDates.toMutableSet() 
        current.add(date) 
        streakCompletedDates = current 
    } 
    
    fun getActiveStreakDates(): Set<String> { 
        val result = streakCompletedDates.toMutableSet() 
        val count = dailyStreakCount 
        val lastDate = lastCompletedStreakDate 
        if (count > 0 && lastDate.isNotEmpty()) { 
            try { 
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US) 
                val cal = java.util.Calendar.getInstance() 
                val date = sdf.parse(lastDate) 
                if (date != null) { 
                    cal.time = date 
                    for (i in 0 until count) { 
                        result.add(sdf.format(cal.time)) 
                        cal.add(java.util.Calendar.DAY_OF_YEAR, -1) 
                    } 
                } 
            } catch (e: Exception) { 
            } 
        } 
        return result 
    } 
    
    fun recordStoryTestPassed() { 
        checkAndResetDailyProgress() 
        val today = getTodayDateString() 
        addStreakCompletedDate(today) 
        prefs.edit() 
            .putString(KEY_LAST_COMPLETED_STREAK_DATE, today) 
            .putBoolean(KEY_TODAY_STORY_COMPLETED, true) 
            .apply() 
        val yesterday = getYesterdayDateString() 
        val calculated = calculateStreakFromDates(streakCompletedDates, today, yesterday) 
        prefs.edit().putInt(KEY_DAILY_STREAK_COUNT, calculated).apply() 
    } 
    
    fun evaluateDailyStreak(ankiDroidHelper: com.ankilock.anki.AnkiDroidHelper? = null) { 
        checkAndResetDailyProgress() 
        val today = getTodayDateString() 
        var learnedCards = prefs.getInt(KEY_TODAY_LEARNED_CARDS_COUNT, 0) 
        if (ankiDroidHelper != null && ankiDroidHelper.hasApiPermission()) { 
            val introduced = ankiDroidHelper.getCardsCount("introduced:1") 
            val rated = ankiDroidHelper.getCardsCount("rated:1") 
            val ankiMax = maxOf(introduced, rated) 
            if (ankiMax > learnedCards) { 
                learnedCards = ankiMax 
                prefs.edit().putInt(KEY_TODAY_LEARNED_CARDS_COUNT, learnedCards).apply() 
            } 
        } 
        val storyDone = prefs.getBoolean(KEY_TODAY_STORY_COMPLETED, false) 
        val qualifies = learnedCards >= 10 || storyDone 
        if (qualifies) { 
            addStreakCompletedDate(today) 
            prefs.edit().putString(KEY_LAST_COMPLETED_STREAK_DATE, today).apply() 
        } 
        val yesterday = getYesterdayDateString() 
        val calculated = calculateStreakFromDates(streakCompletedDates, today, yesterday) 
        prefs.edit().putInt(KEY_DAILY_STREAK_COUNT, calculated).apply() 
    } 
    
    val isStreakCompletedToday: Boolean 
        get() { 
            checkAndResetDailyProgress() 
            return streakCompletedDates.contains(getTodayDateString()) 
        } 
    
    fun incrementThemeUsage(theme: String) { 
        val key = "theme_usage_${theme.lowercase().replace(" ", "_")}" 
        val current = prefs.getInt(key, 0) 
        prefs.edit().putInt(key, current + 1).apply() 
    } 
    
    fun getThemeUsageCount(theme: String): Int { 
        val key = "theme_usage_${theme.lowercase().replace(" ", "_")}" 
        return prefs.getInt(key, 0) 
    } 
    
    fun getTopThemes(allThemes: List<String>, count: Int = 2): List<String> { 
        return allThemes 
            .filter { getThemeUsageCount(it) > 0 } 
            .sortedByDescending { getThemeUsageCount(it) } 
            .take(count) 
    } 
    
    fun incrementSearchCategoryUsage(category: String) { 
        val key = "search_cat_usage_${category.lowercase().replace(" ", "_")}" 
        val current = prefs.getInt(key, 0) 
        prefs.edit().putInt(key, current + 1).apply() 
    } 
    
    fun getSearchCategoryUsageCount(category: String): Int { 
        val key = "search_cat_usage_${category.lowercase().replace(" ", "_")}" 
        return prefs.getInt(key, 0) 
    } 
    
    fun getTopSearchCategories(allCategories: List<String>, count: Int = 2): List<String> { 
        return allCategories 
            .filter { getSearchCategoryUsageCount(it) > 0 } 
            .sortedByDescending { getSearchCategoryUsageCount(it) } 
            .take(count) 
    } 
    
    companion object { 
        const val DEFAULT_GEMINI_MODEL = "gemini-3.5-flash-lite" 
        
        val AVAILABLE_GEMINI_MODELS = listOf( 
            GeminiModelOption("gemini-3.5-flash-lite", "Gemini 3.5 Flash-Lite", "Default • Fastest"), 
            GeminiModelOption("gemini-3.8-flash", "Gemini 3.8 Flash", "Recommended • Most capable"), 
            GeminiModelOption("gemini-3.7-flash", "Gemini 3.7 Flash", "Fast & multimodal reasoning"), 
            GeminiModelOption("gemini-3.6-flash", "Gemini 3.6 Flash", "Stable general performance"), 
            GeminiModelOption("gemini-3.5-flash", "Gemini 3.5 Flash", "Balanced speed & quality") 
        ) 
        
        fun getModelDisplayName(modelId: String): String { 
            return AVAILABLE_GEMINI_MODELS.firstOrNull { it.id.equals(modelId, ignoreCase = true) }?.name ?: modelId 
        } 
        
        fun getShortModelLabel(modelId: String): String { 
            return when (modelId.lowercase()) { 
                "gemini-3.5-flash-lite" -> "3.5 Lite (Fastest)" 
                "gemini-3.8-flash" -> "3.8 Flash" 
                "gemini-3.7-flash" -> "3.7 Flash" 
                "gemini-3.6-flash" -> "3.6 Flash" 
                "gemini-3.5-flash" -> "3.5 Flash" 
                else -> modelId.removePrefix("gemini-") 
            } 
        } 
        
        const val DEFAULT_FISH_AUDIO_VOICE_ID = "5b09815a54a04395bf6ad642d57ce12a" 
        const val DEFAULT_FISH_AUDIO_VOICE_URL = "https://fish.audio/m/5b09815a54a04395bf6ad642d57ce12a" 
        const val DEFAULT_FISH_AUDIO_MODEL = "s2.1-pro-free" 
        
        val AVAILABLE_FISH_AUDIO_MODELS = listOf( 
            FishAudioModelOption("s2.1-pro-free", "s2.1-pro-free", "Free Tier ($0)"), 
            FishAudioModelOption("s2.1-pro", "s2.1-pro", "Paid Tier (Production)") 
        ) 
        
        val PRESET_FISH_AUDIO_VOICES = listOf( 
            FishAudioVoiceOption( 
                id = "5b09815a54a04395bf6ad642d57ce12a", 
                name = "Yuna", 
                author = "Official", 
                description = "Gentle storyteller with balanced, clear Japanese pronunciation.", 
                tag = "Default • Recommended", 
                avatarUrl = "https://public-platform.r2.fish.audio/cdn-cgi/image/width=96,format=webp/coverimage/5b09815a54a04395bf6ad642d57ce12a", 
                likeCount = 1860, 
                taskCount = 451000, 
                tags = listOf("female", "young", "storytelling", "gentle", "Japanese") 
            ), 
            FishAudioVoiceOption( 
                id = "5161d41404314212af1254556477c17d", 
                name = "Sakura", 
                author = "比留間大地", 
                description = "Soft, expressive anime-style voice great for emotive dialogues.", 
                tag = "Anime Style", 
                avatarUrl = "https://public-platform.r2.fish.audio/cdn-cgi/image/width=96,format=webp/coverimage/5161d41404314212af1254556477c17d", 
                likeCount = 1868, 
                taskCount = 451741, 
                tags = listOf("female", "young", "anime", "bright", "Japanese") 
            ), 
            FishAudioVoiceOption( 
                id = "4bc1d3d1fa60415f989b8e0b99f333e1", 
                name = "Kenji", 
                author = "amaillo", 
                description = "Calm, steady and deep male narrator voice for classic stories.", 
                tag = "Male Narrator", 
                avatarUrl = "https://public-platform.r2.fish.audio/cdn-cgi/image/width=96,format=webp/coverimage/4bc1d3d1fa60415f989b8e0b99f333e1", 
                likeCount = 251, 
                taskCount = 31403, 
                tags = listOf("male", "young", "narrator", "calm", "Japanese") 
            ), 
            FishAudioVoiceOption( 
                id = "df5c6c19dca944918dcbd6f1368fd02f", 
                name = "Aoi", 
                author = "Community", 
                description = "Natural conversation pacing and clear modern phrasing.", 
                tag = "Conversational", 
                avatarUrl = "https://public-platform.r2.fish.audio/cdn-cgi/image/width=96,format=webp/coverimage/df5c6c19dca944918dcbd6f1368fd02f", 
                likeCount = 146, 
                taskCount = 24878, 
                tags = listOf("female", "young", "conversational", "soft", "Japanese") 
            ) 
        ) 
        
        fun getPresetVoiceDisplayName(voiceId: String): String { 
            val cleanId = extractVoiceId(voiceId) 
            val preset = PRESET_FISH_AUDIO_VOICES.firstOrNull { it.id.equals(cleanId, ignoreCase = true) } 
            return if (preset != null) { 
                "${preset.name} (${preset.tag.substringBefore(" •")})" 
            } else { 
                if (cleanId.length > 8) "${cleanId.take(8)}..." else cleanId 
            } 
        } 
        
        fun extractVoiceId(input: String): String { 
            val trimmed = input.trim() 
            if (trimmed.isBlank()) return DEFAULT_FISH_AUDIO_VOICE_ID 
            val voicePattern = Regex("""/(?:m|models|voices)/([a-zA-Z0-9_-]+)""") 
            val match = voicePattern.find(trimmed) 
            if (match != null) { 
                return match.groupValues[1] 
            } 
            if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) { 
                val cleanUrl = trimmed.substringBefore('?').substringBefore('#').trimEnd('/') 
                val lastSegment = cleanUrl.substringAfterLast('/') 
                if (lastSegment.isNotBlank()) { 
                    return lastSegment 
                } 
            } 
            return trimmed 
        } 
        
        private const val KEY_GEMINI_API_KEY = "gemini_api_key" 
        private const val KEY_GEMINI_MODEL = "gemini_model" 
        private const val KEY_INTERNET_DISCLOSURE = "internet_disclosure_accepted" 
        private const val KEY_HIGHLIGHT_VOCABULARY_WORDS = "highlight_vocabulary_words" 
        private const val KEY_FISH_AUDIO_API_KEY = "fish_audio_api_key" 
        private const val KEY_FISH_AUDIO_VOICE_ID = "fish_audio_voice_id" 
        private const val KEY_FISH_AUDIO_VOICE_NAME = "fish_audio_voice_name" 
        private const val KEY_FISH_AUDIO_MODEL = "fish_audio_model" 
        private const val KEY_FAVORITE_FISH_AUDIO_VOICES = "favorite_fish_audio_voices" 
        private const val KEY_SAVED_CUSTOM_VOICES = "saved_custom_voices" 
        private const val KEY_LAST_READ_STORY_ID = "last_read_story_id" 
        private const val KEY_READING_BACKGROUND_IMAGE_URI = "reading_background_image_uri" 
        private const val KEY_DISABLED_STORY_THEMES = "disabled_story_themes" 
        private const val KEY_DISABLED_STORY_TOPICS = "disabled_story_topics" 
        private const val KEY_CUSTOM_STORY_THEME = "custom_story_theme" 
        private const val KEY_CUSTOM_STORY_TOPIC = "custom_story_topic" 
        private const val KEY_CUSTOM_THEME_MODE_ACTIVE = "custom_theme_mode_active" 
        private const val KEY_RECENT_JISHO_SEARCHES = "recent_jisho_searches" 
        private const val KEY_DAILY_STREAK_COUNT = "daily_streak_count" 
        private const val KEY_LAST_COMPLETED_STREAK_DATE = "last_completed_streak_date" 
        private const val KEY_STREAK_COMPLETED_DATES = "streak_completed_dates" 
        private const val KEY_STREAK_VIEW_MODE = "streak_view_mode" 
        private const val KEY_TODAY_LEARNED_CARDS_COUNT = "today_learned_cards_count" 
        private const val KEY_TODAY_STORY_COMPLETED = "today_story_completed" 
        private const val KEY_TODAY_PROGRESS_DATE = "today_progress_date" 
        private const val KEY_SERVICE_ENABLED = "service_enabled"
        private const val KEY_MUSIC_PLAYER_STYLE = "music_player_style"
        private const val KEY_SELECTED_DECKS = "selected_decks"
        private const val KEY_UPDATE_INTERVAL = "update_interval"
        private const val KEY_SNOOZE_DURATION = "snooze_duration"
        private const val KEY_SNOOZE_UNTIL = "snooze_until"
        private const val KEY_BACKGROUND_TYPE = "background_type" 
        private const val KEY_APP_BACKGROUND_TYPE = "app_background_type" 
        private const val KEY_APP_CUSTOM_IMAGE_URI = "app_custom_image_uri" 
        private const val KEY_APP_SAVED_IMAGE_URIS = "app_saved_image_uris" 
        private const val KEY_CUSTOM_IMAGE_URI = "custom_image_uri"
        private const val KEY_SAVED_IMAGE_URIS = "saved_image_uris"
        private const val KEY_APP_BLUR_RADIUS = "app_blur_radius" 
        private const val KEY_APP_DIM_OPACITY = "app_dim_opacity" 
        private const val KEY_APP_ARTWORK_OPACITY = "app_artwork_opacity" 
        private const val KEY_BLUR_RADIUS = "blur_radius"
        private const val KEY_DIM_OPACITY = "dim_opacity"
        private const val KEY_ARTWORK_OPACITY = "artwork_opacity"
        private const val KEY_CLASSIC_REVEALED_ACTION = "classic_revealed_action"
        private const val KEY_AUTOPLAY_AUDIO = "autoplay_audio"
        private const val KEY_AUTOPLAY_AUDIO_MODE = "autoplay_audio_mode" 
        private const val KEY_STORY_VOCABULARY_FILTER = "story_vocabulary_filter" 
        private const val KEY_AI_API_KEY = "ai_api_key"
        private const val KEY_AI_PROVIDER = "ai_provider"
        private const val KEY_AI_MODEL = "ai_model"
        private const val KEY_WALLHAVEN_API_KEY = "wallhaven_api_key" 
        private const val KEY_APP_THEME = "app_theme" 
        private const val KEY_STORY_GENRE = "story_genre" 
        private const val KEY_STORY_LEVEL = "story_level" 
        private const val KEY_STORY_LENGTH = "story_length" 
        private const val KEY_COMPLETED_STORIES = "completed_stories" 
        private const val KEY_PASSED_STORIES = "passed_stories" 
        private const val KEY_DAILY_STORY_ENERGY_DATE = "daily_story_energy_date" 
        private const val KEY_DAILY_STORY_ENERGY_REMAINING = "daily_story_energy_remaining" 
        const val MAX_DAILY_STORY_ENERGY = 7 
        private const val KEY_STORY_LANGUAGE = "story_language" 
        private const val KEY_STORY_PAGE_PREFIX = "story_page_" 
        private const val KEY_STORY_FRACTION_PREFIX = "story_fraction_" 
        private const val KEY_STORY_SHOW_PRONUNCIATION = "story_show_pronunciation" 
        private const val KEY_STORY_PRONUNCIATION_TYPE = "story_pronunciation_type" 
        private const val KEY_STORY_ENLARGE_FONT = "story_enlarge_font" 
        private const val KEY_STORY_SHOW_IMAGES = "story_show_images" 
        private const val KEY_STORY_HIGHLIGHT_AUDIO = "story_highlight_audio" 
    } 
} 

