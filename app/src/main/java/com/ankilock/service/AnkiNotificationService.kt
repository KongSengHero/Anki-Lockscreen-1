package com.ankilock.service
    
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.Html
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ankilock.MainActivity
import com.ankilock.R
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.CardSessionManager
import com.ankilock.data.PreferencesManager
import com.ankilock.receiver.NotificationActionReceiver
import com.ankilock.util.MediaArtworkGenerator
import com.ankilock.util.RubyTextRenderer
import com.ankilock.widget.AnkiAppWidgetProvider
import kotlin.math.max
    
class AnkiNotificationService : Service() { 
    
    private lateinit var ankiHelper: AnkiDroidHelper
    private lateinit var prefs: PreferencesManager
    private lateinit var notificationManager: NotificationManager
    private var mediaSession: MediaSessionCompat? = null
    
    private var cardStartTime: Long = 0L
    
    override fun onCreate() { 
        super.onCreate()
        ankiHelper = AnkiDroidHelper(this)
        prefs = PreferencesManager(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        setupMediaSession()
    }
    
    override fun onDestroy() { 
        mediaSession?.isActive = false
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }
    
    private fun setupMediaSession() { 
        mediaSession = MediaSessionCompat(this, "AnkiLockSession").apply { 
            setCallback(object : MediaSessionCompat.Callback() { 
                override fun onPlay() { 
                    CardSessionManager.reveal(this@AnkiNotificationService)
                }
                
                override fun onPause() { 
                    CardSessionManager.toggleReveal(this@AnkiNotificationService)
                }
                
                override fun onSkipToNext() { 
                    val timeTaken = if (cardStartTime > 0L) SystemClock.elapsedRealtime() - cardStartTime else 5000L
                    CardSessionManager.gradeCard(this@AnkiNotificationService, 3, timeTaken)
                }
                
                override fun onSkipToPrevious() { 
                    val timeTaken = if (cardStartTime > 0L) SystemClock.elapsedRealtime() - cardStartTime else 5000L
                    CardSessionManager.gradeCard(this@AnkiNotificationService, 1, timeTaken)
                }
                
                override fun onStop() { 
                    val durationMs = prefs.snoozeDurationMinutes * 60 * 1000L
                    prefs.snoozeUntil = System.currentTimeMillis() + durationMs
                    updateNotification()
                }
            })
            isActive = true
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { 
        when (intent?.action) { 
            ACTION_REVEAL_INTERNAL -> { 
                CardSessionManager.toggleReveal(this)
            }
            ACTION_GRADE_INTERNAL -> { 
                val ease = intent.getIntExtra(EXTRA_EASE, 3)
                val timeTaken = if (cardStartTime > 0L) SystemClock.elapsedRealtime() - cardStartTime else 5000L
                CardSessionManager.gradeCard(this, ease, timeTaken)
            }
            ACTION_UPDATE -> { 
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
        
        val card = CardSessionManager.getOrFetchCard(this)
        cardStartTime = SystemClock.elapsedRealtime()
        val stats = CardSessionManager.currentStats
        
        if (card == null) { 
            return if (prefs.isMusicPlayerStyle) { 
                buildMusicStyleNotification(null, stats)
            } else { 
                buildAllCaughtUpNotification()
            }
        }
        
        return if (prefs.isMusicPlayerStyle) { 
            buildMusicStyleNotification(card, stats)
        } else { 
            buildFlashcardNotification(card, stats)
        }
    }
    
    private fun buildMusicStyleNotification( 
        card: CardInfo?, 
        stats: Triple<Int, Int, Int>
    ): Notification { 
        val isRevealed = CardSessionManager.isRevealed
        val selectedDecks = prefs.getSelectedDeckIdsAsLongs()
        val deckName = when { 
            !card?.deckName.isNullOrBlank() -> card!!.deckName
            selectedDecks.size == 1 -> { 
                val singleId = selectedDecks.first()
                ankiHelper.getDeckList().find { it.id == singleId }?.name ?: "All Caught Up"
            }
            selectedDecks.size > 1 -> "${selectedDecks.size} Decks"
            else -> "All Caught Up"
        }
        val newC = stats.first
        val learnC = stats.second
        val revC = stats.third
        val totalDue = newC + learnC + revC
        
        val cardType = card?.cardType ?: 0
        val cardColor = when (cardType) { 
            1 -> Color.parseColor("#EF5350")
            2 -> Color.parseColor("#66BB6A")
            else -> Color.parseColor("#42A5F5")
        }
        val smallIconRes = when (cardType) { 
            1 -> R.drawable.ic_card_learn
            2 -> R.drawable.ic_card_review
            else -> R.drawable.ic_card_new
        }
        
        val kanjiText = card?.kanji?.ifEmpty { card.question } ?: "Review Deck"
        val kanjiFurigana = card?.kanjiFurigana ?: ""
        val kanjiMeaning = card?.kanjiMeaning?.ifEmpty { card.answer } ?: ""
        val cleanMeaning = kanjiMeaning.replace(Regex("<[^>]*>"), "").trim()
        
        val imageBitmap = if (!card?.imageFileName.isNullOrBlank()) { 
            ankiHelper.getCardImageBitmap(card!!.imageFileName)
        } else { 
            null
        }
        
        val artworkBitmap = MediaArtworkGenerator.generateArtwork( 
            context = this, 
            card = card, 
            stats = stats, 
            isRevealed = isRevealed, 
            imageBitmap = imageBitmap
        )
        
        val title = if (!isRevealed) { 
            kanjiText
        } else { 
            val cleanReading = if (kanjiFurigana.contains("[") && kanjiFurigana.contains("]")) { 
                Regex("\\[([^\\]]+)\\]").findAll(kanjiFurigana).map { it.groupValues[1] }.joinToString("")
            } else { 
                kanjiFurigana.trim()
            }
            if (cleanReading.isNotBlank() && cleanReading != kanjiText) "$kanjiText [$cleanReading]" else kanjiText
        }
        
        val artist = if (!isRevealed) { 
            "Tap ▶ to Reveal Answer"
        } else { 
            if (cleanMeaning.isNotBlank()) cleanMeaning else deckName
        }
        
        val album = "$deckName • Due: $totalDue ($newC · $learnC · $revC)"
        
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, album)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, max(1, totalDue).toLong() * 1000L)
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, artworkBitmap)
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, artworkBitmap)
            .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, artworkBitmap)
            .build()
        
        mediaSession?.setMetadata(metadata)
        
        val playbackState = PlaybackStateCompat.Builder()
            .setActions( 
                PlaybackStateCompat.ACTION_PLAY or 
                PlaybackStateCompat.ACTION_PAUSE or 
                PlaybackStateCompat.ACTION_PLAY_PAUSE or 
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or 
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or 
                PlaybackStateCompat.ACTION_STOP
            )
            .setState( 
                PlaybackStateCompat.STATE_PLAYING, 
                if (isRevealed) 1000L else 0L, 
                1.0f
            )
            .build()
        
        mediaSession?.setPlaybackState(playbackState)
        
        val ankiIntent = ankiHelper.getAnkiLaunchIntent()
        val openAnkiPending = PendingIntent.getActivity( 
            this, 
            105, 
            ankiIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val dismissIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_DISMISSED
        }
        val dismissPending = PendingIntent.getBroadcast( 
            this, 
            999, 
            dismissIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
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
        
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)
        
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(smallIconRes)
            .setColor(cardColor)
            .setStyle(mediaStyle)
            .setContentTitle(if (card == null) "All Reviews Complete! 🎉" else title)
            .setContentText(if (card == null) "お疲れ様でした！また明日頑張りましょう！" else artist)
            .setSubText(deckName)
            .setLargeIcon(artworkBitmap)
            .setContentIntent(revealPending)
            .setDeleteIntent(dismissPending)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setSilent(true)
            
        if (card != null) { 
            builder.addAction(R.drawable.ic_skip_previous, "Again", againPending)
            builder.addAction( 
                if (isRevealed) R.drawable.ic_pause else R.drawable.ic_play, 
                if (isRevealed) "Hide" else "Reveal", 
                revealPending
            )
            builder.addAction(R.drawable.ic_skip_next, "Good", goodPending)
        } else { 
            builder.addAction(R.drawable.ic_notification, "Open Anki", openAnkiPending)
        }
        
        return builder.build()
    }
    
    private fun buildFlashcardNotification( 
        card: CardInfo?, 
        stats: Triple<Int, Int, Int>
    ): Notification { 
        val isRevealed = CardSessionManager.isRevealed
        val cardType = card?.cardType ?: 0
        val cardColor = when (cardType) { 
            1 -> Color.parseColor("#EF5350")
            2 -> Color.parseColor("#66BB6A")
            else -> Color.parseColor("#42A5F5")
        }
        val smallIconRes = when (cardType) { 
            1 -> R.drawable.ic_card_learn
            2 -> R.drawable.ic_card_review
            else -> R.drawable.ic_card_new
        }
        
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
        
        val openAnkiIntent = ankiHelper.getAnkiLaunchIntent()
        val openAnkiPending = PendingIntent.getActivity( 
            this, 
            105, 
            openAnkiIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val suspendIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_SUSPEND
        }
        val suspendPending = PendingIntent.getBroadcast( 
            this, 
            106, 
            suspendIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val undoIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_UNDO
        }
        val undoPending = PendingIntent.getBroadcast( 
            this, 
            107, 
            undoIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val openAppIntent = Intent(this, MainActivity::class.java).apply { 
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPending = PendingIntent.getActivity( 
            this, 
            108, 
            openAppIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val dismissIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_DISMISSED
        }
        val dismissPending = PendingIntent.getBroadcast( 
            this, 
            999, 
            dismissIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val revealedActionPending = when (prefs.classicRevealedAction) { 
            "suspend" -> suspendPending
            "undo" -> undoPending
            "open_app" -> openAppPending
            else -> openAnkiPending
        }
        val revealedActionLabel = when (prefs.classicRevealedAction) { 
            "suspend" -> "Suspend"
            "undo" -> "Undo"
            "open_app" -> "Open App"
            else -> "Open Anki"
        }
        
        val selectedDecks = prefs.getSelectedDeckIdsAsLongs()
        val deckName = when { 
            !card?.deckName.isNullOrBlank() -> card!!.deckName
            selectedDecks.size == 1 -> { 
                val singleId = selectedDecks.first()
                ankiHelper.getDeckList().find { it.id == singleId }?.name ?: "All Caught Up"
            }
            selectedDecks.size > 1 -> "${selectedDecks.size} Decks"
            else -> "All Caught Up"
        }
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
                isCentered = true
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
            
            rv.setOnClickPendingIntent(R.id.notification_root, revealPending)
            rv.setOnClickPendingIntent(R.id.btn_reveal, revealPending)
            rv.setOnClickPendingIntent(R.id.btn_again, againPending)
            rv.setOnClickPendingIntent(R.id.btn_good, goodPending)
            rv.setOnClickPendingIntent(R.id.btn_snooze, snoozePending)
            
            if (!isRevealed) { 
                rv.setViewVisibility(R.id.btn_reveal, View.VISIBLE)
                rv.setViewVisibility(R.id.btn_snooze, View.VISIBLE)
                rv.setViewVisibility(R.id.btn_open_anki, View.VISIBLE)
                rv.setTextViewText(R.id.btn_open_anki, "Anki")
                rv.setOnClickPendingIntent(R.id.btn_open_anki, openAnkiPending)
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
                rv.setTextViewText(R.id.btn_open_anki, revealedActionLabel)
                rv.setOnClickPendingIntent(R.id.btn_open_anki, revealedActionPending)
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
            .setSmallIcon(smallIconRes) 
            .setColor(cardColor) 
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()) 
            .setCustomContentView(collapsedViews) 
            .setCustomBigContentView(expandedViews) 
            .setOngoing(true) 
            .setAutoCancel(false) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setPriority(NotificationCompat.PRIORITY_MAX) 
            .setCategory(NotificationCompat.CATEGORY_STATUS) 
            .setContentIntent(revealPending) 
            .setDeleteIntent(dismissPending) 
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
        return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    }
    
    private fun buildAllCaughtUpNotification(): Notification { 
        val ankiIntent = ankiHelper.getAnkiLaunchIntent()
        val openAnkiPending = PendingIntent.getActivity( 
            this, 
            201, 
            ankiIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setContentTitle("お疲れ様でした！ 🎉") 
            .setContentText("All reviews complete for today! Come back tomorrow.") 
            .setOngoing(true) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setContentIntent(openAnkiPending) 
            .addAction(R.drawable.ic_notification, "Open Anki", openAnkiPending) 
            .setPriority(NotificationCompat.PRIORITY_HIGH) 
            .setSilent(true) 
            .build()
    }
    
    private fun buildSnoozedNotification(): Notification { 
        val remainingMs = prefs.snoozeUntil - System.currentTimeMillis()
        val remainingMin = (remainingMs / 60000).coerceAtLeast(1)
        
        val ankiIntent = ankiHelper.getAnkiLaunchIntent()
        val openAnkiPending = PendingIntent.getActivity( 
            this, 
            202, 
            ankiIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val unsnoozeIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_UNSNOOZE
        }
        val unsnoozePending = PendingIntent.getBroadcast( 
            this, 
            204, 
            unsnoozeIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val dismissIntent = Intent(this, NotificationActionReceiver::class.java).apply { 
            action = NotificationActionReceiver.ACTION_DISMISSED
        }
        val dismissPending = PendingIntent.getBroadcast( 
            this, 
            999, 
            dismissIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID) 
            .setSmallIcon(R.drawable.ic_notification) 
            .setContentTitle("AnkiLock Snoozed") 
            .setContentText("Snoozed for ${remainingMin}m · Tap to resume") 
            .setOngoing(true) 
            .setAutoCancel(false) 
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .setContentIntent(unsnoozePending) 
            .setDeleteIntent(dismissPending) 
            .addAction(R.drawable.ic_play, "Resume Now", unsnoozePending) 
            .addAction(R.drawable.ic_notification, "Open Anki", openAnkiPending) 
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
        AnkiAppWidgetProvider.updateAllWidgets(this)
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
            CardSessionManager.toggleReveal(context)
        }
        
        fun gradeCurrentCard(context: Context, ease: Int) { 
            CardSessionManager.gradeCard(context, ease)
        }
        
        fun stop(context: Context) { 
            val intent = Intent(context, AnkiNotificationService::class.java).apply { 
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
