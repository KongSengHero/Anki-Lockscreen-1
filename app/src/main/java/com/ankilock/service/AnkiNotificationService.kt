package com.ankilock.service
    
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ankilock.MainActivity
import com.ankilock.R
import com.ankilock.ReviewActivity
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.DeckInfo
import com.ankilock.data.PreferencesManager
import com.ankilock.receiver.NotificationActionReceiver
    
class AnkiNotificationService : Service() { 
    
    private lateinit var ankiHelper: AnkiDroidHelper
    private lateinit var prefs: PreferencesManager
    private lateinit var notificationManager: NotificationManager
    
    override fun onCreate() { 
        super.onCreate()
        ankiHelper = AnkiDroidHelper(this)
        prefs = PreferencesManager(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { 
        when (intent?.action) { 
            ACTION_UPDATE -> updateNotification()
            ACTION_STOP -> { 
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> { 
                startForeground(NOTIFICATION_ID, buildNotification())
            }
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() { 
        val channel = NotificationChannel( 
            CHANNEL_ID, 
            getString(R.string.notification_channel_name), 
            NotificationManager.IMPORTANCE_LOW
        ).apply { 
            description = getString(R.string.notification_channel_description)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun buildNotification(): Notification { 
        if (prefs.isSnoozed) { 
            return buildSnoozedNotification()
        }
        
        if (!ankiHelper.isAnkiDroidInstalled()) { 
            return buildErrorNotification(getString(R.string.ankidroid_not_installed))
        }
        
        if (!ankiHelper.hasApiPermission()) { 
            return buildErrorNotification(getString(R.string.api_permission_needed))
        }
        
        val selectedDecks = prefs.getSelectedDeckIdsAsLongs()
        val dueDecks = ankiHelper.getDueDecksBreakdown( 
            if (selectedDecks.isEmpty()) null else selectedDecks
        )
        val totalDue = dueDecks.sumOf { it.totalDue }
        
        if (totalDue == 0) { 
            return buildAllCaughtUpNotification()
        }
        
        return buildDueNotification(totalDue, dueDecks)
    }
    
    private fun buildDueNotification(totalDue: Int, dueDecks: List<DeckInfo>): Notification { 
        val reviewIntent = Intent(this, ReviewActivity::class.java).apply { 
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val reviewPending = PendingIntent.getActivity( 
            this, 
            0, 
            reviewIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val snoozeIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_SNOOZE
        }
        val snoozePending = PendingIntent.getBroadcast( 
            this, 
            1, 
            snoozeIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val openAnkiIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_OPEN_ANKI
        }
        val openAnkiPending = PendingIntent.getBroadcast( 
            this, 
            2, 
            openAnkiIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val deckBreakdown = dueDecks.take(4).joinToString(" · ") { 
            "${it.name}: ${it.totalDue}"
        }
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setContentTitle(getString(R.string.cards_due, totalDue)) 
            .setContentText(deckBreakdown) 
            .setStyle(NotificationCompat.BigTextStyle().bigText(deckBreakdown)) 
            .setOngoing(true) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setContentIntent(openAnkiPending) 
            .addAction(0, getString(R.string.review_now), reviewPending) 
            .addAction(0, getString(R.string.snooze), snoozePending) 
            .setCategory(NotificationCompat.CATEGORY_REMINDER) 
            .setPriority(NotificationCompat.PRIORITY_LOW) 
            .setSilent(true) 
            .build()
    }
    
    private fun buildAllCaughtUpNotification(): Notification { 
        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPending = PendingIntent.getActivity( 
            this, 
            0, 
            mainIntent, 
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setContentTitle(getString(R.string.all_caught_up)) 
            .setOngoing(true) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setContentIntent(mainPending) 
            .setPriority(NotificationCompat.PRIORITY_LOW) 
            .setSilent(true) 
            .build()
    }
    
    private fun buildSnoozedNotification(): Notification { 
        val remainingMs = prefs.snoozeUntil - System.currentTimeMillis()
        val remainingMin = (remainingMs / 60000).coerceAtLeast(1)
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setContentTitle("Snoozed for ${remainingMin}m") 
            .setOngoing(true) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setPriority(NotificationCompat.PRIORITY_MIN) 
            .setSilent(true) 
            .build()
    }
    
    private fun buildErrorNotification(message: String): Notification { 
        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPending = PendingIntent.getActivity( 
            this, 
            0, 
            mainIntent, 
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setContentTitle("AnkiLock") 
            .setContentText(message) 
            .setOngoing(true) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setContentIntent(mainPending) 
            .setPriority(NotificationCompat.PRIORITY_LOW) 
            .setSilent(true) 
            .build()
    }
    
    fun updateNotification() { 
        val notification = buildNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    companion object { 
        const val CHANNEL_ID = "ankilock_due_cards"
        const val NOTIFICATION_ID = 1001
        const val ACTION_UPDATE = "com.ankilock.ACTION_UPDATE"
        const val ACTION_STOP = "com.ankilock.ACTION_STOP"
        
        fun start(context: Context) { 
            val intent = Intent(context, AnkiNotificationService::class.java)
            context.startForegroundService(intent)
        }
        
        fun update(context: Context) { 
            val intent = Intent(context, AnkiNotificationService::class.java).apply { 
                action = ACTION_UPDATE
            }
            context.startService(intent)
        }
        
        fun stop(context: Context) { 
            val intent = Intent(context, AnkiNotificationService::class.java).apply { 
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
