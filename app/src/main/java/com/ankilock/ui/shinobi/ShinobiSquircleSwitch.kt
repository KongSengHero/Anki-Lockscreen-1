package com.ankilock.ui.shinobi
    
import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
    
@Composable
fun ShinobiSquircleSwitch( 
    checked: Boolean, 
    onCheckedChange: (Boolean) -> Unit, 
    modifier: Modifier = Modifier, 
    enabled: Boolean = true 
) { 
    val view = LocalView.current 
    val interactionSource = remember { MutableInteractionSource() } 
    val trackWidth = 48.dp 
    val trackHeight = 28.dp 
    val thumbSize = 22.dp 
    val padding = 3.dp 
    
    val trackColor by animateColorAsState( 
        targetValue = if (checked) Color(0xFF22C55E) else Color(0xFF27272A), 
        animationSpec = tween(durationMillis = 150), 
        label = "switchTrackColor" 
    ) 
    val borderColor by animateColorAsState( 
        targetValue = if (checked) Color(0xFF22C55E) else Color(0xFF3F3F46), 
        animationSpec = tween(durationMillis = 150), 
        label = "switchBorderColor" 
    ) 
    val thumbOffset by animateDpAsState( 
        targetValue = if (checked) (trackWidth - thumbSize - padding) else padding, 
        animationSpec = tween(durationMillis = 150), 
        label = "switchThumbOffset" 
    ) 
    
    val trackShape = RoundedCornerShape(8.dp) 
    val thumbShape = RoundedCornerShape(6.dp) 
    
    Box( 
        modifier = modifier 
            .size(width = trackWidth, height = trackHeight) 
            .clip(trackShape) 
            .background(trackColor) 
            .border(1.dp, borderColor, trackShape) 
            .clickable( 
                interactionSource = interactionSource, 
                indication = null, 
                enabled = enabled, 
                onClick = { 
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
                    onCheckedChange(!checked) 
                } 
            ), 
        contentAlignment = Alignment.CenterStart 
    ) { 
        Box( 
            modifier = Modifier 
                .offset(x = thumbOffset) 
                .size(thumbSize) 
                .clip(thumbShape) 
                .background(Color.White) 
        ) 
    } 
}
