package com.ankilock
    
import android.app.Application
import com.ankilock.data.PreferencesManager
import com.ankilock.service.AnkiNotificationService
import com.ankilock.worker.DueCountWorker
    
class AnkiLockApp : Application() { 
    
    lateinit var prefs: PreferencesManager
        private set
    
    override fun onCreate() { 
        super.onCreate()
        prefs = PreferencesManager(this)
        
        if (prefs.isServiceEnabled) { 
            AnkiNotificationService.start(this)
            DueCountWorker.schedule(this, prefs.updateIntervalMinutes.toLong())
        }
    }
}
