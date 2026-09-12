package com.ankilock.data
    
import android.content.Context
import android.content.SharedPreferences
    
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
    val tag: String = "" 
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
    
    var dailyStreakCount: Int 
        get() { 
            checkAndResetDailyProgress() 
            return prefs.getInt(KEY_DAILY_STREAK_COUNT, 0) 
        } 
        set(value) = prefs.edit().putInt(KEY_DAILY_STREAK_COUNT, value).apply() 
    
    var lastCompletedStreakDate: String 
        get() = prefs.getString(KEY_LAST_COMPLETED_STREAK_DATE, "") ?: "" 
        set(value) = prefs.edit().putString(KEY_LAST_COMPLETED_STREAK_DATE, value).apply() 
    
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
    
    fun checkAndResetDailyProgress() { 
        val today = getTodayDateString() 
        val recordedDate = prefs.getString(KEY_TODAY_PROGRESS_DATE, "") ?: "" 
        if (recordedDate != today) { 
            val yesterday = getYesterdayDateString() 
            val lastStreakDate = prefs.getString(KEY_LAST_COMPLETED_STREAK_DATE, "") ?: "" 
            if (lastStreakDate.isNotEmpty() && lastStreakDate != yesterday && lastStreakDate != today) { 
                prefs.edit().putInt(KEY_DAILY_STREAK_COUNT, 0).apply() 
            } 
            prefs.edit() 
                .putString(KEY_TODAY_PROGRESS_DATE, today) 
                .putInt(KEY_TODAY_LEARNED_CARDS_COUNT, 0) 
                .putBoolean(KEY_TODAY_STORY_COMPLETED, false) 
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
    
    fun evaluateDailyStreak() { 
        val today = getTodayDateString() 
        val lastStreakDate = prefs.getString(KEY_LAST_COMPLETED_STREAK_DATE, "") ?: "" 
        val learnedCards = prefs.getInt(KEY_TODAY_LEARNED_CARDS_COUNT, 0) 
        val storyDone = prefs.getBoolean(KEY_TODAY_STORY_COMPLETED, false) 
        val qualifies = learnedCards >= 10 || storyDone 
        if (qualifies && lastStreakDate != today) { 
            val yesterday = getYesterdayDateString() 
            val currentStreak = prefs.getInt(KEY_DAILY_STREAK_COUNT, 0) 
            val newStreak = if (lastStreakDate == yesterday) currentStreak + 1 else 1 
            prefs.edit() 
                .putInt(KEY_DAILY_STREAK_COUNT, newStreak) 
                .putString(KEY_LAST_COMPLETED_STREAK_DATE, today) 
                .apply() 
        } 
    } 
    
    val isStreakCompletedToday: Boolean 
        get() { 
            checkAndResetDailyProgress() 
            return lastCompletedStreakDate == getTodayDateString() 
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
                tag = "Default • Recommended" 
            ), 
            FishAudioVoiceOption( 
                id = "1952e5ebf8c24f6cb65f12e84d43ae68", 
                name = "Sakura", 
                author = "Community", 
                description = "Soft, expressive anime-style voice great for emotive dialogues.", 
                tag = "Anime Style" 
            ), 
            FishAudioVoiceOption( 
                id = "8f8605eb803848b6b1580228de684674", 
                name = "Kenji", 
                author = "Community", 
                description = "Calm, steady and deep male narrator voice for classic stories.", 
                tag = "Male Narrator" 
            ), 
            FishAudioVoiceOption( 
                id = "800a744bbec9449884607ff9ec988ec7", 
                name = "Aoi", 
                author = "Community", 
                description = "Natural conversation pacing and clear modern phrasing.", 
                tag = "Conversational" 
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
        private const val KEY_AI_API_KEY = "ai_api_key"
        private const val KEY_AI_PROVIDER = "ai_provider"
        private const val KEY_AI_MODEL = "ai_model"
        private const val KEY_WALLHAVEN_API_KEY = "wallhaven_api_key" 
        private const val KEY_APP_THEME = "app_theme" 
        private const val KEY_STORY_GENRE = "story_genre" 
        private const val KEY_STORY_LEVEL = "story_level" 
        private const val KEY_STORY_LENGTH = "story_length" 
        private const val KEY_COMPLETED_STORIES = "completed_stories" 
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

