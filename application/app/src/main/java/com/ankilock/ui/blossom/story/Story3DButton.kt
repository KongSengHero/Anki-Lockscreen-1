package com.ankilock.ui.blossom.story
    
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
    
@Composable
fun Story3DButton( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    width: Dp = 48.dp, 
    height: Dp = 48.dp, 
    faceColor: Color, 
    lipColor: Color, 
    borderColor: Color, 
    borderWidth: Dp = 2.dp, 
    lipHeight: Dp = 5.dp, 
    cornerRadius: Dp = 15.dp, 
    enabled: Boolean = true, 
    content: @Composable BoxScope.() -> Unit 
) { 
    val interactionSource = remember { MutableInteractionSource() } 
    val isPressed by interactionSource.collectIsPressedAsState() 
    val view = LocalView.current 
    val coroutineScope = rememberCoroutineScope() 
    var isClickAnimating by remember { mutableStateOf(false) } 
    var hasVibrated by remember { mutableStateOf(false) } 
    
    val isEffectivelyPressed = (isPressed || isClickAnimating) && enabled 
    
    val currentBottomLip by animateDpAsState( 
        targetValue = if (isEffectivelyPressed) 0.dp else lipHeight, 
        animationSpec = tween(durationMillis = 60), 
        label = "story3DBottomLip" 
    ) 
    val topPush by animateDpAsState( 
        targetValue = if (isEffectivelyPressed) lipHeight else 0.dp, 
        animationSpec = tween(durationMillis = 60), 
        label = "story3DTopPush" 
    ) 
    
    LaunchedEffect(isPressed) { 
        if (isPressed && enabled) { 
            if (!hasVibrated) { 
                hasVibrated = true 
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
            } 
        } else if (!isPressed) { 
            delay(80L) 
            hasVibrated = false 
        } 
    } 
    
    val shape = RoundedCornerShape(cornerRadius) 
    
    Box( 
        modifier = modifier 
            .size(width = width, height = height) 
            .padding(top = topPush) 
            .clip(shape) 
            .background(if (enabled) lipColor else lipColor.copy(alpha = 0.5f)) 
            .clickable( 
                interactionSource = interactionSource, 
                indication = null, 
                enabled = enabled, 
                onClick = { 
                    if (enabled && !hasVibrated) { 
                        hasVibrated = true 
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
                    } 
                    coroutineScope.launch { 
                        isClickAnimating = true 
                        delay(65L) 
                        isClickAnimating = false 
                        delay(25L) 
                        hasVibrated = false 
                        onClick() 
                    } 
                } 
            ) 
    ) { 
        val actualBorderColor = if (borderColor.red >= faceColor.red && borderColor.green >= faceColor.green && borderColor.blue >= faceColor.blue) { 
            faceColor.copy( 
                red = (faceColor.red * 0.72f).coerceIn(0f, 1f), 
                green = (faceColor.green * 0.72f).coerceIn(0f, 1f), 
                blue = (faceColor.blue * 0.72f).coerceIn(0f, 1f), 
                alpha = 1f 
            ) 
        } else { 
            borderColor 
        } 
        
        Box( 
            modifier = Modifier 
                .fillMaxSize() 
                .padding(bottom = currentBottomLip) 
                .clip(shape) 
                .background(if (enabled) faceColor else faceColor.copy(alpha = 0.6f)) 
                .border( 
                    width = borderWidth, 
                    color = if (enabled) actualBorderColor else actualBorderColor.copy(alpha = 0.5f), 
                    shape = shape 
                ), 
            contentAlignment = Alignment.Center, 
            content = content 
        ) 
    } 
}
