package com.ankilock.widget
    
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.View
import android.widget.RemoteViews
import com.ankilock.MainActivity
import com.ankilock.R
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.PreferencesManager
import com.ankilock.service.AnkiNotificationService
    
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
                isRevealed = !isRevealed
                updateAllWidgets(context)
            }
            ACTION_WIDGET_AGAIN -> { 
                handleGrade(context, 1)
            }
            ACTION_WIDGET_GOOD -> { 
                handleGrade(context, 3)
            }
            ACTION_WIDGET_REFRESH -> { 
                currentCard = null
                isRevealed = false
                updateAllWidgets(context)
            }
        }
    }
    
    private fun handleGrade(context: Context, ease: Int) { 
        val ankiHelper = AnkiDroidHelper(context)
        val card = currentCard ?: ankiHelper.getNextDueCard()
        if (card != null) { 
            Thread { 
                ankiHelper.answerCard(card.noteId, card.cardOrd, ease, 5000L)
                currentCard = ankiHelper.getNextDueCard(excludeNoteId = card.noteId)
                isRevealed = false
                updateAllWidgets(context)
                if (PreferencesManager(context).isServiceEnabled) { 
                    AnkiNotificationService.update(context)
                }
            }.start()
        } else { 
            currentCard = null
            isRevealed = false
            updateAllWidgets(context)
        }
    }
    
    companion object { 
        const val ACTION_WIDGET_REVEAL = "com.ankilock.widget.ACTION_REVEAL"
        const val ACTION_WIDGET_AGAIN = "com.ankilock.widget.ACTION_AGAIN"
        const val ACTION_WIDGET_GOOD = "com.ankilock.widget.ACTION_GOOD"
        const val ACTION_WIDGET_REFRESH = "com.ankilock.widget.ACTION_REFRESH"
        
        private var currentCard: CardInfo? = null
        private var isRevealed: Boolean = false
        
        fun syncCard(card: CardInfo?, revealed: Boolean) { 
            currentCard = card
            isRevealed = revealed
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
            val prefs = PreferencesManager(context)
            val selectedDecks = prefs.getSelectedDeckIdsAsLongs()
            val deckId = if (selectedDecks.size == 1) selectedDecks.first() else null
            
            if (currentCard == null) { 
                currentCard = ankiHelper.getNextDueCard(deckId)
            }
            
            val card = currentCard
            val stats = ankiHelper.getSelectedDeckStats()
            val rv = RemoteViews(context.packageName, R.layout.widget_card)
            
            val deckName = card?.deckName?.ifEmpty { "Kaishi 1.5k" } ?: "All Decks"
            val newC = stats.first
            val learnC = stats.second
            val revC = stats.third
            val statsHtml = "<font color='#8AB4F8'>$newC</font> · <font color='#F28B82'>$learnC</font> · <font color='#81C995'>$revC</font>"
            
            rv.setTextViewText(R.id.tv_widget_deck, deckName)
            rv.setTextViewText(R.id.tv_widget_stats, Html.fromHtml(statsHtml, Html.FROM_HTML_MODE_COMPACT))
            
            val ankiIntent = context.packageManager.getLaunchIntentForPackage("com.ichi2.anki")?.apply { 
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            } ?: Intent(Intent.ACTION_MAIN).apply { 
                setPackage("com.ichi2.anki")
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            }
            val ankiPending = PendingIntent.getActivity( 
                context, 
                304, 
                ankiIntent, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            rv.setOnClickPendingIntent(R.id.btn_widget_open, ankiPending)
            
            if (card == null) { 
                rv.setTextViewText(R.id.tv_widget_kanji, "お疲れ様でした！")
                rv.setViewVisibility(R.id.tv_widget_furigana, View.GONE)
                rv.setTextViewText(R.id.tv_widget_meaning, "All reviews complete for today! 🎉")
                rv.setViewVisibility(R.id.tv_widget_meaning, View.VISIBLE)
                rv.setTextViewText(R.id.tv_widget_sentence, "また明日頑張りましょう！")
                rv.setViewVisibility(R.id.tv_widget_sentence, View.VISIBLE)
                rv.setTextViewText(R.id.tv_widget_sentence_meaning, "Come back tomorrow for your next cards.")
                rv.setViewVisibility(R.id.tv_widget_sentence_meaning, View.VISIBLE)
                
                rv.setViewVisibility(R.id.btn_widget_again, View.GONE)
                rv.setViewVisibility(R.id.btn_widget_reveal, View.GONE)
                rv.setViewVisibility(R.id.btn_widget_good, View.GONE)
                rv.setViewVisibility(R.id.btn_widget_open, View.VISIBLE)
                rv.setTextViewText(R.id.btn_widget_open, "Open AnkiDroid")
                
                rv.setOnClickPendingIntent(R.id.widget_card_content, ankiPending)
                appWidgetManager.updateAppWidget(widgetId, rv)
                return
            }
            
            rv.setViewVisibility(R.id.btn_widget_again, View.VISIBLE)
            rv.setViewVisibility(R.id.btn_widget_reveal, View.VISIBLE)
            rv.setViewVisibility(R.id.btn_widget_good, View.VISIBLE)
            rv.setTextViewText(R.id.btn_widget_open, "Anki")
            
            val kanjiText = card.kanji.ifEmpty { card.question }
            val kanjiFurigana = card.kanjiFurigana
            val kanjiMeaning = card.kanjiMeaning.ifEmpty { card.answer }
            val cleanMeaning = kanjiMeaning.replace(Regex("<[^>]*>"), "").trim()
            val rawSentence = card.sentence.replace(Regex("<[^>]*>"), "").trim()
            val sentenceMeaning = card.sentenceMeaning
            val cleanSentenceMeaning = sentenceMeaning.replace(Regex("<[^>]*>"), "").trim()
            
            rv.setTextViewText(R.id.tv_widget_kanji, kanjiText)
            
            if (isRevealed) { 
                if (kanjiFurigana.isNotBlank()) { 
                    rv.setTextViewText(R.id.tv_widget_furigana, kanjiFurigana)
                    rv.setViewVisibility(R.id.tv_widget_furigana, View.VISIBLE)
                } else { 
                    rv.setViewVisibility(R.id.tv_widget_furigana, View.GONE)
                }
                
                if (cleanMeaning.isNotBlank()) { 
                    rv.setTextViewText(R.id.tv_widget_meaning, cleanMeaning)
                    rv.setViewVisibility(R.id.tv_widget_meaning, View.VISIBLE)
                } else { 
                    rv.setViewVisibility(R.id.tv_widget_meaning, View.GONE)
                }
                
                if (cleanSentenceMeaning.isNotBlank()) { 
                    rv.setTextViewText(R.id.tv_widget_sentence_meaning, cleanSentenceMeaning)
                    rv.setViewVisibility(R.id.tv_widget_sentence_meaning, View.VISIBLE)
                } else { 
                    rv.setViewVisibility(R.id.tv_widget_sentence_meaning, View.GONE)
                }
                
                rv.setTextViewText(R.id.btn_widget_reveal, "Hide")
            } else { 
                rv.setViewVisibility(R.id.tv_widget_furigana, View.GONE)
                rv.setViewVisibility(R.id.tv_widget_meaning, View.GONE)
                rv.setViewVisibility(R.id.tv_widget_sentence_meaning, View.GONE)
                rv.setTextViewText(R.id.btn_widget_reveal, "Reveal")
            }
            
            if (rawSentence.isNotBlank()) { 
                rv.setTextViewText(R.id.tv_widget_sentence, rawSentence)
                rv.setViewVisibility(R.id.tv_widget_sentence, View.VISIBLE)
            } else { 
                rv.setViewVisibility(R.id.tv_widget_sentence, View.GONE)
            }
            
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
            rv.setOnClickPendingIntent(R.id.widget_card_content, revealPending)
            
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
            
            appWidgetManager.updateAppWidget(widgetId, rv)
        }
    }
}
