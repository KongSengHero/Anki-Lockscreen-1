package com.ankilock.receiver
    
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.PreferencesManager
import com.ankilock.service.AnkiNotificationService
    
class NotificationActionReceiver : BroadcastReceiver() { 
    
    override fun onReceive(context: Context, intent: Intent) { 
        when (intent.action) { 
            ACTION_SNOOZE -> { 
                val prefs = PreferencesManager(context)
                val durationMs = prefs.snoozeDurationMinutes * 60 * 1000L
                prefs.snoozeUntil = System.currentTimeMillis() + durationMs
                AnkiNotificationService.update(context)
            }
            ACTION_OPEN_ANKI -> { 
                val helper = AnkiDroidHelper(context)
                helper.openAnkiDroidReviewer()
            }
        }
    }
    
    companion object { 
        const val ACTION_SNOOZE = "com.ankilock.ACTION_SNOOZE"
        const val ACTION_OPEN_ANKI = "com.ankilock.ACTION_OPEN_ANKI"
    }
}
