package com.ankilock.util
    
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
    
enum class AudioTrackPlaying { 
    NONE, 
    WORD, 
    SENTENCE
}
    
class AudioPlayerHelper(private val context: Context) { 
    
    private val ankiHelper = AnkiDroidHelper(context)
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val ttsCallbacks = ConcurrentHashMap<String, () -> Unit>()
    private val mainHandler = Handler(Looper.getMainLooper())
    
    var currentPlayingTrack by mutableStateOf(AudioTrackPlaying.NONE)
        private set
    
    init { 
        initTts()
    }
    
    private fun initTts() { 
        try { 
            tts = TextToSpeech(context.applicationContext) { status -> 
                if (status == TextToSpeech.SUCCESS) { 
                    val langResult = tts?.setLanguage(Locale.JAPANESE)
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) { 
                        tts?.setLanguage(Locale.JAPAN)
                    }
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() { 
                        override fun onStart(utteranceId: String?) { 
                        }
                        
                        override fun onDone(utteranceId: String?) { 
                            if (utteranceId != null) { 
                                val callback = ttsCallbacks.remove(utteranceId)
                                callback?.invoke()
                            }
                        }
                        
                        override fun onError(utteranceId: String?, errorCode: Int) { 
                            if (utteranceId != null) { 
                                val callback = ttsCallbacks.remove(utteranceId)
                                callback?.invoke()
                            }
                        }
                        
                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) { 
                            if (utteranceId != null) { 
                                val callback = ttsCallbacks.remove(utteranceId)
                                callback?.invoke()
                            }
                        }
                    })
                    isTtsReady = true
                }
            }
        } catch (e: Exception) { 
        }
    }
    
    fun playWord(card: CardInfo?, onComplete: (() -> Unit)? = null) { 
        if (card == null) { 
            onComplete?.invoke()
            return
        }
        val audioName = card.wordAudio
        val fallbackText = card.kanji.ifBlank { card.kanjiFurigana.ifBlank { card.question } }
        if (audioName.isBlank() && fallbackText.isBlank()) { 
            onComplete?.invoke()
            return
        }
        playAudio(audioName, fallbackText, AudioTrackPlaying.WORD, onComplete)
    }
    
    fun playSentence(card: CardInfo?, onComplete: (() -> Unit)? = null) { 
        if (card == null) { 
            onComplete?.invoke()
            return
        }
        val audioName = card.sentenceAudio
        val fallbackText = card.sentence.ifBlank { card.sentenceFurigana }
        if (audioName.isBlank() && fallbackText.isBlank()) { 
            onComplete?.invoke()
            return
        }
        playAudio(audioName, fallbackText, AudioTrackPlaying.SENTENCE, onComplete)
    }
    
    fun playSequence(card: CardInfo?, onComplete: (() -> Unit)? = null) { 
        if (card == null) { 
            onComplete?.invoke()
            return
        }
        val hasWord = card.wordAudio.isNotBlank() || card.kanji.isNotBlank() || card.question.isNotBlank()
        val hasSentence = card.sentenceAudio.isNotBlank() || card.sentence.isNotBlank() || card.sentenceFurigana.isNotBlank()
        
        if (hasWord) { 
            playWord(card) { 
                if (hasSentence) { 
                    mainHandler.postDelayed({ 
                        playSentence(card, onComplete)
                    }, 300L)
                } else { 
                    onComplete?.invoke()
                }
            }
        } else if (hasSentence) { 
            playSentence(card, onComplete)
        } else { 
            onComplete?.invoke()
        }
    }
    
    fun stop() { 
        try { 
            mediaPlayer?.apply { 
                if (isPlaying) { 
                    stop()
                }
                reset()
            }
        } catch (e: Exception) { 
        }
        try { 
            tts?.stop()
            ttsCallbacks.clear()
        } catch (e: Exception) { 
        }
        currentPlayingTrack = AudioTrackPlaying.NONE
    }
    
    fun release() { 
        stop()
        try { 
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) { 
        }
        try { 
            tts?.shutdown()
            tts = null
            isTtsReady = false
        } catch (e: Exception) { 
        }
    }
    
    private fun cleanJapaneseTextForTts(text: String): String { 
        var result = text
        result = result.replace(Regex("<ruby>([^<]+)<rt>[^<]+</rt></ruby>"), "$1")
        result = result.replace(Regex("([^\\[\\s]+)\\[([^\\]]+)\\]"), "$1")
        result = result.replace(Regex("\\[sound:[^\\]]+\\]", RegexOption.IGNORE_CASE), "")
        result = result.replace(Regex("<style[\\s\\S]*?</style>", RegexOption.IGNORE_CASE), "")
        result = result.replace(Regex("<script[\\s\\S]*?</script>", RegexOption.IGNORE_CASE), "")
        result = result.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), " ")
        result = result.replace(Regex("<[^>]*>"), "")
        result = result.replace("&nbsp;", " ")
        result = result.replace("&amp;", "&")
        result = result.replace("&lt;", "<")
        result = result.replace("&gt;", ">")
        result = result.replace("&#39;", "'")
        result = result.replace("&quot;", "\"")
        return result.trim()
    }
    
    private fun speakTts( 
        text: String, 
        trackType: AudioTrackPlaying, 
        onComplete: (() -> Unit)?
    ) { 
        val cleaned = cleanJapaneseTextForTts(text)
        if (cleaned.isBlank()) { 
            currentPlayingTrack = AudioTrackPlaying.NONE
            onComplete?.invoke()
            return
        }
        
        val engine = tts
        if (engine == null || !isTtsReady) { 
            currentPlayingTrack = AudioTrackPlaying.NONE
            onComplete?.invoke()
            return
        }
        
        currentPlayingTrack = trackType
        val utteranceId = "ankilock_${System.currentTimeMillis()}_${trackType.name}"
        ttsCallbacks[utteranceId] = { 
            mainHandler.post { 
                if (currentPlayingTrack == trackType) { 
                    currentPlayingTrack = AudioTrackPlaying.NONE
                }
                onComplete?.invoke()
            }
        }
        
        val params = Bundle().apply { 
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        val result = engine.speak(cleaned, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        if (result != TextToSpeech.SUCCESS) { 
            ttsCallbacks.remove(utteranceId)
            currentPlayingTrack = AudioTrackPlaying.NONE
            onComplete?.invoke()
        }
    }
    
    private fun playAudio( 
        audioName: String, 
        fallbackText: String, 
        trackType: AudioTrackPlaying, 
        onComplete: (() -> Unit)?
    ) { 
        stop()
        
        Thread { 
            val file = if (audioName.isNotBlank()) ankiHelper.getAudioFile(audioName) else null
            if (file != null && file.exists() && file.canRead()) { 
                mainHandler.post { 
                    try { 
                        val player = (mediaPlayer ?: MediaPlayer().also { mediaPlayer = it }).apply { 
                            reset()
                            setAudioAttributes( 
                                AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                            )
                            setDataSource(file.absolutePath)
                            prepare()
                            setOnCompletionListener { 
                                currentPlayingTrack = AudioTrackPlaying.NONE
                                onComplete?.invoke()
                            }
                            setOnErrorListener { _, _, _ -> 
                                currentPlayingTrack = AudioTrackPlaying.NONE
                                if (fallbackText.isNotBlank()) { 
                                    speakTts(fallbackText, trackType, onComplete)
                                } else { 
                                    onComplete?.invoke()
                                }
                                true
                            }
                        }
                        currentPlayingTrack = trackType
                        player.start()
                    } catch (e: Exception) { 
                        if (fallbackText.isNotBlank()) { 
                            speakTts(fallbackText, trackType, onComplete)
                        } else { 
                            currentPlayingTrack = AudioTrackPlaying.NONE
                            onComplete?.invoke()
                        }
                    }
                }
            } else if (fallbackText.isNotBlank()) { 
                mainHandler.post { 
                    speakTts(fallbackText, trackType, onComplete)
                }
            } else { 
                mainHandler.post { 
                    currentPlayingTrack = AudioTrackPlaying.NONE
                    onComplete?.invoke()
                }
            }
        }.start()
    }
}
