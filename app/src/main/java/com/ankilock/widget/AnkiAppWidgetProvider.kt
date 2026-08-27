package com.ankilock.widget
    
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.ankilock.R
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.CardSessionManager
import com.ankilock.util.MediaArtworkGenerator
    
class AnkiAppWidgetProvider : AppWidgetProvider() { 
    
    override fun onUpdate( 
        context: Context, 
        appWidgetManager: AppWidgetManager, 
        appWidgetIds: IntArray
    ) { 
        for (widgetId in appWidgetIds) { 
            updateWidget(context, appWidgetManager, widgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) { 
        super.onReceive(context, intent)
        
        when (intent.action) { 
            ACTION_WIDGET_REVEAL -> { 
                CardSessionManager.toggleReveal(context)
            }
            ACTION_WIDGET_AGAIN -> { 
                CardSessionManager.gradeCard(context, 1)
            }
            ACTION_WIDGET_GOOD -> { 
                CardSessionManager.gradeCard(context, 3)
            }
            ACTION_WIDGET_REFRESH -> { 
                CardSessionManager.refresh(context)
            }
        }
    }
    
    companion object { 
        const val ACTION_WIDGET_REVEAL = "com.ankilock.widget.ACTION_REVEAL"
        const val ACTION_WIDGET_AGAIN = "com.ankilock.widget.ACTION_AGAIN"
        const val ACTION_WIDGET_GOOD = "com.ankilock.widget.ACTION_GOOD"
        const val ACTION_WIDGET_REFRESH = "com.ankilock.widget.ACTION_REFRESH"
        
        fun syncCard(card: CardInfo?, revealed: Boolean) { 
            // Handled via CardSessionManager
        }
        
        fun updateAllWidgets(context: Context) { 
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, AnkiAppWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) { 
                updateWidget(context, appWidgetManager, widgetId)
            }
        }
        
        private fun updateWidget( 
            context: Context, 
            appWidgetManager: AppWidgetManager, 
            widgetId: Int
        ) { 
            val ankiHelper = AnkiDroidHelper(context)
            val card = CardSessionManager.getOrFetchCard(context)
            val isRevealed = CardSessionManager.isRevealed
            val stats = CardSessionManager.currentStats
            
            val imageBitmap = if (!card?.imageFileName.isNullOrBlank()) { 
                ankiHelper.getCardImageBitmap(card!!.imageFileName)
            } else { 
                null
            }
            
            val artwork = MediaArtworkGenerator.generateArtwork( 
                context = context, 
                card = card, 
                stats = stats, 
                isRevealed = isRevealed, 
                imageBitmap = imageBitmap, 
                showBottomControls = false
            )
            
            val rv = RemoteViews(context.packageName, R.layout.widget_card)
            rv.setImageViewBitmap(R.id.iv_widget_artwork, artwork)
            
            rv.setTextViewText( 
                R.id.btn_widget_reveal, 
                if (isRevealed) "Hide" else "Reveal"
            )
            
            val revealIntent = Intent(context, AnkiAppWidgetProvider::class.java).apply { 
                action = ACTION_WIDGET_REVEAL
            }
            val revealPending = PendingIntent.getBroadcast( 
                context, 
                301, 
                revealIntent, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            rv.setOnClickPendingIntent(R.id.btn_widget_reveal, revealPending)
            rv.setOnClickPendingIntent(R.id.iv_widget_artwork, revealPending)
            
            val againIntent = Intent(context, AnkiAppWidgetProvider::class.java).apply { 
                action = ACTION_WIDGET_AGAIN
            }
            val againPending = PendingIntent.getBroadcast( 
                context, 
                302, 
                againIntent, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            rv.setOnClickPendingIntent(R.id.btn_widget_again, againPending)
            
            val goodIntent = Intent(context, AnkiAppWidgetProvider::class.java).apply { 
                action = ACTION_WIDGET_GOOD
            }
            val goodPending = PendingIntent.getBroadcast( 
                context, 
                303, 
                goodIntent, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            rv.setOnClickPendingIntent(R.id.btn_widget_good, goodPending)
            
            val ankiIntent = ankiHelper.getAnkiLaunchIntent()
            val ankiPending = PendingIntent.getActivity( 
                context, 
                304, 
                ankiIntent, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            rv.setOnClickPendingIntent(R.id.btn_widget_open, ankiPending)
            
            appWidgetManager.updateAppWidget(widgetId, rv)
        }
    }
}

