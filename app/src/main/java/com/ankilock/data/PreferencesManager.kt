package com.ankilock.data
    
import android.content.Context
import android.content.SharedPreferences
    
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
        get() = prefs.getBoolean(KEY_MUSIC_PLAYER_STYLE, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_PLAYER_STYLE, value).apply()
    
    var backgroundType: String
        get() = prefs.getString(KEY_BACKGROUND_TYPE, "anki_lock") ?: "anki_lock"
        set(value) = prefs.edit().putString(KEY_BACKGROUND_TYPE, value).apply()
    
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
        get() = prefs.getString(KEY_CLASSIC_REVEALED_ACTION, "suspend") ?: "suspend"
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
    
    var storyGenre: String
        get() = prefs.getString(KEY_STORY_GENRE, "IT & Workplace") ?: "IT & Workplace"
        set(value) = prefs.edit().putString(KEY_STORY_GENRE, value).apply()
    
    var storyLevel: String
        get() = prefs.getString(KEY_STORY_LEVEL, "Intermediate") ?: "Intermediate"
        set(value) = prefs.edit().putString(KEY_STORY_LEVEL, value).apply()
    
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
    
    fun getSelectedDeckIdsAsLongs(): Set<Long> { 
        return selectedDeckIds.mapNotNull { it.toLongOrNull() }.toSet()
    }
    
    companion object { 
        private const val KEY_SERVICE_ENABLED = "service_enabled"
        private const val KEY_MUSIC_PLAYER_STYLE = "music_player_style"
        private const val KEY_SELECTED_DECKS = "selected_decks"
        private const val KEY_UPDATE_INTERVAL = "update_interval"
        private const val KEY_SNOOZE_DURATION = "snooze_duration"
        private const val KEY_SNOOZE_UNTIL = "snooze_until"
        private const val KEY_BACKGROUND_TYPE = "background_type"
        private const val KEY_CUSTOM_IMAGE_URI = "custom_image_uri"
        private const val KEY_SAVED_IMAGE_URIS = "saved_image_uris"
        private const val KEY_BLUR_RADIUS = "blur_radius"
        private const val KEY_DIM_OPACITY = "dim_opacity"
        private const val KEY_ARTWORK_OPACITY = "artwork_opacity"
        private const val KEY_CLASSIC_REVEALED_ACTION = "classic_revealed_action"
        private const val KEY_AUTOPLAY_AUDIO = "autoplay_audio"
        private const val KEY_AI_API_KEY = "ai_api_key"
        private const val KEY_AI_PROVIDER = "ai_provider"
        private const val KEY_AI_MODEL = "ai_model"
        private const val KEY_STORY_GENRE = "story_genre"
        private const val KEY_STORY_LEVEL = "story_level"
    }
}
