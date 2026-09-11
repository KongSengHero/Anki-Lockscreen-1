package com.ankilock.ui.blossom
    
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
    
@Composable
fun BlossomTactileIconButton( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    icon: ImageVector? = null, 
    text: String? = null, 
    contentDescription: String? = null, 
    size: Dp = 48.dp, 
    iconSize: Dp = 22.dp, 
    faceColor: Color = Color(0xFF1E293B), 
    lipColor: Color = Color(0xFF0F172A), 
    activeFaceColor: Color = Color(0xFF2563EB), 
    activeLipColor: Color = Color(0xFF1D4ED8), 
    activeBorderColor: Color = Color(0xFF60A5FA), 
    iconColor: Color = Color.White, 
    active: Boolean = false, 
    enabled: Boolean = true, 
    lipHeight: Dp = 3.5.dp 
) { 
    val interactionSource = remember { MutableInteractionSource() } 
    val isPressed by interactionSource.collectIsPressedAsState() 
    val view = LocalView.current 
    val coroutineScope = rememberCoroutineScope() 
    var isClickAnimating by remember { mutableStateOf(false) } 
    var hasVibratedForCurrentTouch by remember { mutableStateOf(false) } 
    
    val isEffectivelyPressed = (isPressed || isClickAnimating) && enabled 
    
    val currentBottomLip by animateDpAsState( 
        targetValue = if (isEffectivelyPressed) 0.dp else lipHeight, 
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 75), 
        label = "iconBottomLip" 
    ) 
    val topPush by animateDpAsState( 
        targetValue = if (isEffectivelyPressed) lipHeight else 0.dp, 
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 75), 
        label = "iconTopPush" 
    ) 
    
    LaunchedEffect(isPressed) { 
        if (isPressed && enabled) { 
            if (!hasVibratedForCurrentTouch) { 
                hasVibratedForCurrentTouch = true 
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
            } 
        } else if (!isPressed) { 
            delay(100L) 
            hasVibratedForCurrentTouch = false 
        } 
    } 
    
    val currentFace = when { 
        !enabled -> Color(0xFF1E293B).copy(alpha = 0.5f) 
        active -> activeFaceColor 
        else -> faceColor 
    } 
    val currentLip = when { 
        !enabled -> Color(0xFF0F172A).copy(alpha = 0.5f) 
        active -> activeLipColor 
        else -> lipColor 
    } 
    
    val currentBorder = if (active) activeBorderColor else Color(0xFF3F3F46) 
    val currentBorderWidth = if (active) 1.5.dp else 1.dp 
    
    Box( 
        modifier = modifier 
            .size(size) 
            .padding(top = topPush) 
            .clip(BlossomShapes.SquircleMedium) 
            .background(currentLip) 
            .clickable( 
                interactionSource = interactionSource, 
                indication = null, 
                enabled = enabled, 
                onClick = { 
                    if (enabled && !hasVibratedForCurrentTouch) { 
                        hasVibratedForCurrentTouch = true 
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
                    } 
                    coroutineScope.launch { 
                        isClickAnimating = true 
                        delay(80L) 
                        isClickAnimating = false 
                        delay(40L) 
                        hasVibratedForCurrentTouch = false 
                        onClick() 
                    } 
                } 
            ) 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxSize() 
                .padding(bottom = currentBottomLip) 
                .clip(BlossomShapes.SquircleMedium) 
                .background(currentFace) 
                .border( 
                    width = 2.dp, 
                    color = currentLip, 
                    shape = BlossomShapes.SquircleMedium 
                ), 
            contentAlignment = Alignment.Center 
        ) { 
            if (icon != null) { 
                Icon( 
                    imageVector = icon, 
                    contentDescription = contentDescription, 
                    tint = if (enabled) (if (active) Color.White else iconColor) else Color(0xFF64748B), 
                    modifier = Modifier.size(iconSize) 
                ) 
            } else if (text != null) { 
                Text( 
                    text = text, 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = if (enabled) (if (active) Color.White else iconColor) else Color(0xFF64748B) 
                ) 
            } 
        } 
    } 
} 
