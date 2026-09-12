package com.ankilock.ui.shinobi
    
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.ankilock.R
    
object ShinobiSoundEffects { 
    private var soundPool: SoundPool? = null 
    private var soundSuccessId: Int = 0 
    private var soundErrorId: Int = 0 
    private var soundCompletedId: Int = 0 
    
    fun init(context: Context) { 
        if (soundPool != null) return 
        val attributes = AudioAttributes.Builder() 
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION) 
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) 
            .build() 
        soundPool = SoundPool.Builder() 
            .setMaxStreams(4) 
            .setAudioAttributes(attributes) 
            .build() 
        soundSuccessId = soundPool?.load(context.applicationContext, R.raw.sound_success, 1) ?: 0 
        soundErrorId = soundPool?.load(context.applicationContext, R.raw.sound_error, 1) ?: 0 
        soundCompletedId = soundPool?.load(context.applicationContext, R.raw.sound_completed, 1) ?: 0 
    } 
    
    fun playSuccess() { 
        soundPool?.play(soundSuccessId, 1f, 1f, 1, 0, 1f) 
    } 
    
    fun playError() { 
        soundPool?.play(soundErrorId, 1f, 1f, 1, 0, 1f) 
    } 
    
    fun playCompleted() { 
        soundPool?.play(soundCompletedId, 1f, 1f, 1, 0, 1f) 
    } 
} 
