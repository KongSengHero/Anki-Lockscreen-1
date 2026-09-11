package com.ankilock.ui.blossom
    
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
    
object BlossomHaptics { 
    fun click(context: Context, view: View? = null) { 
        try { 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { 
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager 
                val vibrator = vm?.defaultVibrator 
                if (vibrator != null && vibrator.hasVibrator()) { 
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)) 
                } 
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { 
                @Suppress("DEPRECATION") 
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator 
                if (vibrator != null && vibrator.hasVibrator()) { 
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)) 
                } 
            } else { 
                @Suppress("DEPRECATION") 
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator 
                if (vibrator != null && vibrator.hasVibrator()) { 
                    vibrator.vibrate(20L) 
                } 
            } 
        } catch (_: Exception) { 
        } 
        try { 
            view?.performHapticFeedback( 
                HapticFeedbackConstants.KEYBOARD_TAP, 
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING or HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING 
            ) 
        } catch (_: Exception) { 
        } 
    } 
} 
