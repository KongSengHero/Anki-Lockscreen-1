package com.ankilock.ui.blossom
    
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
    
enum class TactileButtonVariant { 
    PRIMARY, 
    SUCCESS, 
    OUTLINE, 
    DANGER, 
    PREMIUM 
} 
    
@Composable
fun BlossomTactileButton( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    faceColor: Color = BlossomColors.BlossomBlue, 
    lipColor: Color = BlossomColors.BlossomBlueLip, 
    contentColor: Color = Color.White, 
    enabled: Boolean = true, 
    shape: RoundedCornerShape = BlossomShapes.SquircleMedium, 
    buttonHeight: Dp = 50.dp, 
    lipHeight: Dp = 3.dp, 
    content: @Composable RowScope.() -> Unit 
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
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 60), 
        label = "buttonBottomLip" 
    ) 
    val topPush by animateDpAsState( 
        targetValue = if (isEffectivelyPressed) lipHeight else 0.dp, 
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 60), 
        label = "buttonTopPush" 
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
    
    Box( 
        modifier = modifier 
            .padding(top = topPush) 
            .clip(shape) 
            .background(if (enabled) lipColor else BlossomColors.Stone700) 
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
                        delay(70L) 
                        isClickAnimating = false 
                        delay(30L) 
                        hasVibratedForCurrentTouch = false 
                        onClick() 
                    } 
                } 
            ) 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(bottom = currentBottomLip) 
                .clip(shape) 
                .background(if (enabled) faceColor else BlossomColors.Stone800) 
                .border( 
                    width = 2.dp, 
                    color = if (enabled) lipColor else BlossomColors.Stone700, 
                    shape = shape 
                ) 
                .height(buttonHeight) 
                .padding(horizontal = 16.dp), 
            contentAlignment = Alignment.Center 
        ) { 
            CompositionLocalProvider(LocalContentColor provides if (enabled) contentColor else BlossomColors.TextMuted) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    content = content 
                ) 
            } 
        } 
    } 
} 
    
@Composable
fun BlossomTextButton( 
    text: String, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    variant: TactileButtonVariant = TactileButtonVariant.PRIMARY, 
    faceColor: Color? = null, 
    lipColor: Color? = null, 
    contentColor: Color? = null, 
    enabled: Boolean = true 
) { 
    val defaultColors = when (variant) { 
        TactileButtonVariant.PRIMARY -> Triple(BlossomColors.BlossomBlue, BlossomColors.BlossomBlueLip, Color.White) 
        TactileButtonVariant.SUCCESS -> Triple(BlossomColors.BlossomGreen, BlossomColors.BlossomGreenLip, Color.White) 
        TactileButtonVariant.OUTLINE -> Triple(BlossomColors.Stone800, BlossomColors.Stone600, Color.White) 
        TactileButtonVariant.DANGER -> Triple(BlossomColors.BlossomRed, BlossomColors.BlossomRedLip, Color.White) 
        TactileButtonVariant.PREMIUM -> Triple(BlossomColors.BlossomAmber, BlossomColors.BlossomAmberLip, Color(0xFF1E1D20)) 
    } 
    val finalFace = faceColor ?: defaultColors.first 
    val finalLip = lipColor ?: defaultColors.second 
    val finalContentColor = contentColor ?: defaultColors.third 
    
    BlossomTactileButton( 
        onClick = onClick, 
        modifier = modifier, 
        faceColor = finalFace, 
        lipColor = finalLip, 
        contentColor = finalContentColor, 
        enabled = enabled 
    ) { 
        Text( 
            text = text, 
            color = finalContentColor, 
            fontSize = 16.sp, 
            fontWeight = FontWeight.Bold, 
            fontFamily = BlossomNunito, 
            letterSpacing = 0.5.sp 
        ) 
    } 
} 
