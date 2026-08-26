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
    
    val isSnoozed: Boolean
        get() = System.currentTimeMillis() < snoozeUntil
    
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
    }
}
