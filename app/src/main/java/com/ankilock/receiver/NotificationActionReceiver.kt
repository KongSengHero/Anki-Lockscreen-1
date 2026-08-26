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
            ACTION_REVEAL -> { 
                AnkiNotificationService.revealCard(context)
            }
            ACTION_GRADE_AGAIN -> { 
                AnkiNotificationService.gradeCurrentCard(context, 1)
            }
            ACTION_GRADE_GOOD -> { 
                AnkiNotificationService.gradeCurrentCard(context, 3)
            }
            ACTION_SNOOZE -> { 
                val prefs = PreferencesManager(context)
                val durationMs = prefs.snoozeDurationMinutes * 60 * 1000L
                prefs.snoozeUntil = System.currentTimeMillis() + durationMs
                AnkiNotificationService.update(context)
            }
            ACTION_UNSNOOZE -> { 
                val prefs = PreferencesManager(context)
                prefs.snoozeUntil = 0L
                AnkiNotificationService.update(context)
            }
            ACTION_DISMISSED -> { 
                AnkiNotificationService.update(context)
            }
            ACTION_OPEN_ANKI -> { 
                val ankiIntent = context.packageManager.getLaunchIntentForPackage("com.ichi2.anki")?.apply { 
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                } ?: Intent(Intent.ACTION_MAIN).apply { 
                    setPackage("com.ichi2.anki")
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                }
                try { 
                    context.startActivity(ankiIntent)
                } catch (e: Exception) { 
                    val helper = AnkiDroidHelper(context)
                    helper.openAnkiDroidReviewer()
                }
            }
        }
    }
    
    companion object { 
        const val ACTION_REVEAL = "com.ankilock.ACTION_REVEAL"
        const val ACTION_GRADE_AGAIN = "com.ankilock.ACTION_GRADE_AGAIN"
        const val ACTION_GRADE_GOOD = "com.ankilock.ACTION_GRADE_GOOD"
        const val ACTION_SNOOZE = "com.ankilock.ACTION_SNOOZE"
        const val ACTION_UNSNOOZE = "com.ankilock.ACTION_UNSNOOZE"
        const val ACTION_DISMISSED = "com.ankilock.ACTION_DISMISSED"
        const val ACTION_OPEN_ANKI = "com.ankilock.ACTION_OPEN_ANKI"
    }
}
