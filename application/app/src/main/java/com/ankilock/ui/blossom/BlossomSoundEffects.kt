package com.ankilock.ui.blossom
    
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.ankilock.R
    
object BlossomSoundEffects { 
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
        soundPool?.play(soundSuccessId, 0.35f, 0.35f, 1, 0, 1f) 
    } 
    
    fun playError() { 
        soundPool?.play(soundErrorId, 0.35f, 0.35f, 1, 0, 1f) 
    } 
    
    fun playCompleted() { 
        soundPool?.play(soundCompletedId, 0.35f, 0.35f, 1, 0, 1f) 
    } 
} 
