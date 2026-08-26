package com.ankilock.service
    
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.text.Html
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ankilock.MainActivity
import com.ankilock.R
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.PreferencesManager
import com.ankilock.receiver.NotificationActionReceiver
import com.ankilock.util.RubyTextRenderer
    
class AnkiNotificationService : Service() { 
    
    private lateinit var ankiHelper: AnkiDroidHelper
    private lateinit var prefs: PreferencesManager
    private lateinit var notificationManager: NotificationManager
    
    private var currentCard: CardInfo? = null
    private var isRevealed: Boolean = false
    private var cardStartTime: Long = 0L
    
    override fun onCreate() { 
        super.onCreate()
        ankiHelper = AnkiDroidHelper(this)
        prefs = PreferencesManager(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { 
        when (intent?.action) { 
            ACTION_REVEAL_INTERNAL -> { 
                isRevealed = true
                updateNotification()
            }
            ACTION_GRADE_INTERNAL -> { 
                val ease = intent.getIntExtra(EXTRA_EASE, 3)
                handleGrade(ease)
            }
            ACTION_UPDATE -> { 
                isRevealed = false
                currentCard = null
                updateNotification()
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { 
            val channel = NotificationChannel( 
                CHANNEL_ID, 
                getString(R.string.notification_channel_name), 
                NotificationManager.IMPORTANCE_HIGH
            ).apply { 
                description = getString(R.string.notification_channel_description)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(true)
                enableVibration(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun handleGrade(ease: Int) { 
        val card = currentCard
        val timeTaken = if (cardStartTime > 0L) { 
            SystemClock.elapsedRealtime() - cardStartTime
        } else { 
            5000L
        }
        
        if (card != null) { 
            Thread { 
                ankiHelper.answerCard(card.noteId, card.cardOrd, ease, timeTaken)
                isRevealed = false
                currentCard = null
                updateNotification()
            }.start()
        } else { 
            isRevealed = false
            currentCard = null
            updateNotification()
        }
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
        val deckId = if (selectedDecks.size == 1) selectedDecks.first() else null
        
        if (currentCard == null) { 
            currentCard = ankiHelper.getNextDueCard(deckId)
            cardStartTime = SystemClock.elapsedRealtime()
        }
        
        val card = currentCard
        val stats = ankiHelper.getSelectedDeckStats()
        val totalDue = stats.first + stats.second + stats.third
        
        if (card == null && totalDue == 0) { 
            return buildAllCaughtUpNotification()
        }
        
        return buildFlashcardNotification(card, stats)
    }
    
    private fun buildFlashcardNotification( 
        card: CardInfo?, 
        stats: Triple<Int, Int, Int>
    ): Notification { 
        val collapsedViews = RemoteViews(packageName, R.layout.notification_card_collapsed)
        val expandedViews = RemoteViews(packageName, R.layout.notification_card_expanded)
        
        val revealIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_REVEAL
        }
        val revealPending = PendingIntent.getBroadcast( 
            this, 
            101, 
            revealIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val againIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_GRADE_AGAIN
        }
        val againPending = PendingIntent.getBroadcast( 
            this, 
            102, 
            againIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val goodIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_GRADE_GOOD
        }
        val goodPending = PendingIntent.getBroadcast( 
            this, 
            103, 
            goodIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val snoozeIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_SNOOZE
        }
        val snoozePending = PendingIntent.getBroadcast( 
            this, 
            104, 
            snoozeIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val openAnkiIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_OPEN_ANKI
        }
        val openAnkiPending = PendingIntent.getBroadcast( 
            this, 
            105, 
            openAnkiIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val deckName = card?.deckName?.ifEmpty { "Kaishi 1.5k" } ?: "Kaishi 1.5k"
        val newC = stats.first
        val learnC = stats.second
        val revC = stats.third
        val statsHtml = "<font color='#8AB4F8'>$newC</font> · <font color='#F28B82'>$learnC</font> · <font color='#81C995'>$revC</font>"
        val statsSpanned = fromHtmlCompat(statsHtml)
        
        val kanjiText = card?.kanji?.ifEmpty { card.question } ?: "Review Deck"
        val kanjiFurigana = card?.kanjiFurigana ?: ""
        val kanjiMeaning = card?.kanjiMeaning?.ifEmpty { card.answer } ?: ""
        
        val rawSentence = card?.sentence ?: ""
        val highlightedSentence = highlightWordInSentence(rawSentence, kanjiText)
        val sentenceSpanned = fromHtmlCompat(highlightedSentence)
        val sentenceFurigana = card?.sentenceFurigana ?: ""
        val sentenceMeaning = card?.sentenceMeaning ?: ""
        
        val imageBitmap = if (!card?.imageFileName.isNullOrBlank()) { 
            ankiHelper.getCardImageBitmap(card!!.imageFileName)
        } else { 
            null
        }
        
        val rubyBitmapExpanded = if (isRevealed && sentenceFurigana.isNotBlank()) { 
            RubyTextRenderer.renderRubyBitmap( 
                context = this, 
                rawText = sentenceFurigana, 
                highlightWord = kanjiText, 
                baseTextSizeSp = 15f, 
                rubyTextSizeSp = 8.5f, 
                isCentered = true
            )
        } else { 
            null
        }
        
        val rubyBitmapCollapsed = if (isRevealed && sentenceFurigana.isNotBlank()) { 
            RubyTextRenderer.renderRubyBitmap( 
                context = this, 
                rawText = sentenceFurigana, 
                highlightWord = kanjiText, 
                baseTextSizeSp = 13f, 
                rubyTextSizeSp = 7.5f, 
                isCentered = false
            )
        } else { 
            null
        }
        
        val viewsAndRuby = listOf( 
            Pair(collapsedViews, rubyBitmapCollapsed), 
            Pair(expandedViews, rubyBitmapExpanded)
        )
        
        viewsAndRuby.forEach { (rv, rubyBitmap) -> 
            rv.setTextViewText(R.id.tv_deck_name, deckName)
            rv.setTextViewText(R.id.tv_deck_stats, statsSpanned)
            rv.setTextViewText(R.id.tv_kanji, kanjiText)
            
            if (imageBitmap != null) { 
                rv.setImageViewBitmap(R.id.iv_card_image, imageBitmap)
                rv.setViewVisibility(R.id.iv_card_image, View.VISIBLE)
            } else { 
                rv.setViewVisibility(R.id.iv_card_image, View.GONE)
            }
            
            rv.setOnClickPendingIntent(R.id.notification_root, openAnkiPending)
            rv.setOnClickPendingIntent(R.id.btn_open_anki, openAnkiPending)
            rv.setOnClickPendingIntent(R.id.btn_reveal, revealPending)
            rv.setOnClickPendingIntent(R.id.btn_again, againPending)
            rv.setOnClickPendingIntent(R.id.btn_good, goodPending)
            rv.setOnClickPendingIntent(R.id.btn_snooze, snoozePending)
            
            if (!isRevealed) { 
                rv.setViewVisibility(R.id.btn_reveal, View.VISIBLE)
                rv.setViewVisibility(R.id.btn_snooze, View.VISIBLE)
                rv.setViewVisibility(R.id.btn_open_anki, View.VISIBLE)
                rv.setViewVisibility(R.id.btn_again, View.GONE)
                rv.setViewVisibility(R.id.btn_good, View.GONE)
                
                rv.setViewVisibility(R.id.tv_kanji_furigana, View.GONE)
                rv.setViewVisibility(R.id.tv_kanji_meaning, View.GONE)
                rv.setViewVisibility(R.id.tv_sentence_furigana, View.GONE)
                rv.setViewVisibility(R.id.iv_sentence_ruby, View.GONE)
                rv.setViewVisibility(R.id.tv_sentence_meaning, View.GONE)
                
                if (rawSentence.isNotBlank()) { 
                    rv.setTextViewText(R.id.tv_sentence, sentenceSpanned)
                    rv.setViewVisibility(R.id.tv_sentence, View.VISIBLE)
                } else { 
                    rv.setViewVisibility(R.id.tv_sentence, View.GONE)
                }
            } else { 
                rv.setViewVisibility(R.id.btn_reveal, View.GONE)
                rv.setViewVisibility(R.id.btn_snooze, View.GONE)
                rv.setViewVisibility(R.id.btn_open_anki, View.VISIBLE)
                rv.setViewVisibility(R.id.btn_again, View.VISIBLE)
                rv.setViewVisibility(R.id.btn_good, View.VISIBLE)
                
                if (kanjiFurigana.isNotBlank()) { 
                    rv.setTextViewText(R.id.tv_kanji_furigana, kanjiFurigana)
                    rv.setViewVisibility(R.id.tv_kanji_furigana, View.VISIBLE)
                } else { 
                    rv.setViewVisibility(R.id.tv_kanji_furigana, View.GONE)
                }
                
                if (kanjiMeaning.isNotBlank()) { 
                    rv.setTextViewText(R.id.tv_kanji_meaning, kanjiMeaning)
                    rv.setViewVisibility(R.id.tv_kanji_meaning, View.VISIBLE)
                } else { 
                    rv.setViewVisibility(R.id.tv_kanji_meaning, View.GONE)
                }
                
                rv.setViewVisibility(R.id.tv_sentence_furigana, View.GONE)
                
                if (rubyBitmap != null) { 
                    rv.setImageViewBitmap(R.id.iv_sentence_ruby, rubyBitmap)
                    rv.setViewVisibility(R.id.iv_sentence_ruby, View.VISIBLE)
                    rv.setViewVisibility(R.id.tv_sentence, View.GONE)
                } else if (rawSentence.isNotBlank()) { 
                    rv.setViewVisibility(R.id.iv_sentence_ruby, View.GONE)
                    rv.setTextViewText(R.id.tv_sentence, sentenceSpanned)
                    rv.setViewVisibility(R.id.tv_sentence, View.VISIBLE)
                } else { 
                    rv.setViewVisibility(R.id.iv_sentence_ruby, View.GONE)
                    rv.setViewVisibility(R.id.tv_sentence, View.GONE)
                }
                
                if (sentenceMeaning.isNotBlank()) { 
                    rv.setTextViewText(R.id.tv_sentence_meaning, sentenceMeaning)
                    rv.setViewVisibility(R.id.tv_sentence_meaning, View.VISIBLE)
                } else { 
                    rv.setViewVisibility(R.id.tv_sentence_meaning, View.GONE)
                }
            }
        }
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()) 
            .setCustomContentView(collapsedViews) 
            .setCustomBigContentView(expandedViews) 
            .setOngoing(true) 
            .setAutoCancel(false) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setPriority(NotificationCompat.PRIORITY_MAX) 
            .setCategory(NotificationCompat.CATEGORY_STATUS) 
            .setContentIntent(openAnkiPending) 
            .setSilent(true) 
            .build()
    }
    
    private fun highlightWordInSentence(sentence: String, kanji: String): String { 
        if (sentence.isBlank()) return ""
        if (sentence.contains("<b>") || sentence.contains("<strong>")) { 
            return sentence
                .replace("<b>", "<font color='#8AB4F8'><b>")
                .replace("</b>", "</b></font>")
                .replace("<strong>", "<font color='#8AB4F8'><strong>")
                .replace("</strong>", "</strong></font>")
        }
        val cleanKanji = kanji.trim()
        if (cleanKanji.isNotEmpty() && sentence.contains(cleanKanji)) { 
            return sentence.replace(cleanKanji, "<font color='#8AB4F8'>$cleanKanji</font>")
        }
        val rootKanji = cleanKanji.filter { it in '\u4E00'..'\u9FAF' }
        if (rootKanji.isNotEmpty() && sentence.contains(rootKanji)) { 
            val regex = Regex("(${Regex.escape(rootKanji)}[\u3040-\u309F]*)")
            return regex.replace(sentence, "<font color='#8AB4F8'>$1</font>")
        }
        return sentence
    }
    
    private fun fromHtmlCompat(html: String): CharSequence { 
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { 
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else { 
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }
    }
    
    private fun buildAllCaughtUpNotification(): Notification { 
        val openAnkiIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_OPEN_ANKI
        }
        val openAnkiPending = PendingIntent.getBroadcast( 
            this, 
            201, 
            openAnkiIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setContentTitle(getString(R.string.all_caught_up)) 
            .setContentText("Tap to open AnkiDroid") 
            .setOngoing(true) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setContentIntent(openAnkiPending) 
            .setPriority(NotificationCompat.PRIORITY_HIGH) 
            .setSilent(true) 
            .build()
    }
    
    private fun buildSnoozedNotification(): Notification { 
        val remainingMs = prefs.snoozeUntil - System.currentTimeMillis()
        val remainingMin = (remainingMs / 60000).coerceAtLeast(1)
        
        val openAnkiIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_OPEN_ANKI
        }
        val openAnkiPending = PendingIntent.getBroadcast( 
            this, 
            202, 
            openAnkiIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setContentTitle("AnkiLock Snoozed") 
            .setContentText("Snoozed for ${remainingMin}m · Tap to open AnkiDroid") 
            .setOngoing(true) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setContentIntent(openAnkiPending) 
            .setPriority(NotificationCompat.PRIORITY_LOW) 
            .setSilent(true) 
            .build()
    }
    
    private fun buildErrorNotification(message: String): Notification { 
        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPending = PendingIntent.getActivity( 
            this, 
            203, 
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
            .setPriority(NotificationCompat.PRIORITY_HIGH) 
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
        const val ACTION_REVEAL_INTERNAL = "com.ankilock.ACTION_REVEAL_INTERNAL"
        const val ACTION_GRADE_INTERNAL = "com.ankilock.ACTION_GRADE_INTERNAL"
        const val EXTRA_EASE = "extra_ease"
        
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
        
        fun revealCard(context: Context) { 
            val intent = Intent(context, AnkiNotificationService::class.java).apply { 
                action = ACTION_REVEAL_INTERNAL
            }
            context.startService(intent)
        }
        
        fun gradeCurrentCard(context: Context, ease: Int) { 
            val intent = Intent(context, AnkiNotificationService::class.java).apply { 
                action = ACTION_GRADE_INTERNAL
                putExtra(EXTRA_EASE, ease)
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
