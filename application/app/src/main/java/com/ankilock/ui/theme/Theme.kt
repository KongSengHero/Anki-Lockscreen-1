package com.ankilock.ui.theme
    
import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ankilock.ui.blossom.AppTheme
import com.ankilock.ui.blossom.BlossomColors
    
@Composable 
fun AnkiLockTheme( 
    content: @Composable () -> Unit 
) { 
    val isLight = BlossomColors.currentTheme == AppTheme.LIGHT 
    val colorScheme = if (isLight) { 
        lightColorScheme( 
            primary = BlossomColors.SakuraRose, 
            secondary = BlossomColors.MatchaSage, 
            background = BlossomColors.BackgroundDeep, 
            surface = BlossomColors.SurfaceCard1, 
            surfaceVariant = BlossomColors.SurfaceCard2 
        ) 
    } else { 
        darkColorScheme( 
            primary = BlossomColors.SakuraRose, 
            secondary = BlossomColors.MatchaSage, 
            background = BlossomColors.BackgroundDeep, 
            surface = BlossomColors.SurfaceCard1, 
            surfaceVariant = BlossomColors.SurfaceCard2 
        ) 
    } 
    
    val view = LocalView.current 
    if (!view.isInEditMode) { 
        SideEffect { 
            val window = (view.context as Activity).window 
            window.statusBarColor = android.graphics.Color.TRANSPARENT 
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLight 
        } 
    } 
    
    MaterialTheme( 
        colorScheme = colorScheme, 
        typography = Typography, 
        content = content 
    ) 
} 
