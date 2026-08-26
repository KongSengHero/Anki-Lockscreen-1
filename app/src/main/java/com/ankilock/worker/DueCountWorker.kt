package com.ankilock.worker
    
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ankilock.data.PreferencesManager
import com.ankilock.service.AnkiNotificationService
import java.util.concurrent.TimeUnit
    
class DueCountWorker( 
    context: Context, 
    params: WorkerParameters
) : CoroutineWorker(context, params) { 
    
    override suspend fun doWork(): Result { 
        val prefs = PreferencesManager(applicationContext)
        
        if (!prefs.isServiceEnabled) return Result.success()
        if (prefs.isSnoozed) return Result.success()
        
        AnkiNotificationService.update(applicationContext)
        return Result.success()
    }
    
    companion object { 
        private const val WORK_NAME = "ankilock_due_count_refresh"
        
        fun schedule(context: Context, intervalMinutes: Long = 30) { 
            val request = PeriodicWorkRequestBuilder<DueCountWorker>( 
                intervalMinutes, 
                TimeUnit.MINUTES
            ).build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork( 
                WORK_NAME, 
                ExistingPeriodicWorkPolicy.UPDATE, 
                request
            )
        }
        
        fun cancel(context: Context) { 
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
