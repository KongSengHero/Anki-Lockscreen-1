package com.ankilock.util
    
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
    
class JapaneseTtsHelper(context: Context) { 
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private val startCallbacks = ConcurrentHashMap<String, () -> Unit>()
    private val doneCallbacks = ConcurrentHashMap<String, () -> Unit>()
    private val errorCallbacks = ConcurrentHashMap<String, () -> Unit>()
    private var pendingSpeakText: String? = null
    private var pendingOnStart: (() -> Unit)? = null
    private var pendingOnDone: (() -> Unit)? = null
    private var pendingOnError: (() -> Unit)? = null
    
    init { 
        try { 
            tts = TextToSpeech(context.applicationContext) { status -> 
                if (status == TextToSpeech.SUCCESS) { 
                    val result = tts?.setLanguage(Locale.JAPANESE)
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) { 
                        isInitialized = true
                        setupListener()
                        val pending = pendingSpeakText
                        if (pending != null) { 
                            val pStart = pendingOnStart ?: {}
                            val pDone = pendingOnDone ?: {}
                            val pError = pendingOnError ?: {}
                            pendingSpeakText = null
                            pendingOnStart = null
                            pendingOnDone = null
                            pendingOnError = null
                            speak(pending, pStart, pDone, pError)
                        } 
                    } 
                } 
            } 
        } catch (ignored: Exception) { 
            isInitialized = false
        } 
    } 
    
    private fun setupListener() { 
        try { 
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() { 
                override fun onStart(utteranceId: String?) { 
                    if (utteranceId != null) { 
                        val cb = startCallbacks.remove(utteranceId)
                        if (cb != null) { 
                            mainHandler.post { cb.invoke() }
                        } 
                    } 
                } 
    
                override fun onDone(utteranceId: String?) { 
                    if (utteranceId != null) { 
                        val cb = doneCallbacks.remove(utteranceId)
                        errorCallbacks.remove(utteranceId)
                        if (cb != null) { 
                            mainHandler.post { cb.invoke() }
                        } 
                    } 
                } 
    
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) { 
                    if (utteranceId != null) { 
                        val cb = errorCallbacks.remove(utteranceId)
                        doneCallbacks.remove(utteranceId)
                        if (cb != null) { 
                            mainHandler.post { cb.invoke() }
                        } 
                    } 
                } 
            }) 
        } catch (ignored: Exception) { 
        } 
    } 
    
    fun speak( 
        text: String, 
        onStart: () -> Unit = {}, 
        onDone: () -> Unit = {}, 
        onError: () -> Unit = {} 
    ) { 
        if (text.isBlank()) return
        if (!isInitialized) { 
            pendingSpeakText = text
            pendingOnStart = onStart
            pendingOnDone = onDone
            pendingOnError = onError
            return
        } 
        try { 
            val utteranceId = "BlossomTTS_${System.currentTimeMillis()}"
            startCallbacks[utteranceId] = onStart
            doneCallbacks[utteranceId] = onDone
            errorCallbacks[utteranceId] = onError
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) { 
            onError.invoke()
        } 
    } 
    
    fun stop() { 
        try { 
            startCallbacks.clear()
            doneCallbacks.clear()
            errorCallbacks.clear()
            pendingSpeakText = null
            tts?.stop() 
        } catch (ignored: Exception) { 
        } 
    } 
    
    fun isSpeaking(): Boolean { 
        return try { 
            tts?.isSpeaking == true 
        } catch (e: Exception) { 
            false 
        } 
    } 
    
    fun shutdown() { 
        try { 
            startCallbacks.clear()
            doneCallbacks.clear()
            errorCallbacks.clear()
            pendingSpeakText = null
            tts?.stop() 
            tts?.shutdown() 
        } catch (ignored: Exception) { 
        } 
    } 
} 
