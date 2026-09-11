package com.ankilock.receiver
    
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ankilock.data.PreferencesManager
import com.ankilock.service.AnkiNotificationService
    
class BootReceiver : BroadcastReceiver() { 
    
    override fun onReceive(context: Context, intent: Intent) { 
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) { 
            val prefs = PreferencesManager(context)
            if (prefs.isServiceEnabled) { 
                AnkiNotificationService.start(context)
            }
        }
    }
}
