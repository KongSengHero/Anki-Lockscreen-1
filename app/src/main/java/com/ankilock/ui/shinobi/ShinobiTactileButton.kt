package com.ankilock.ui.shinobi
    
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
    
enum class TactileButtonVariant { 
    PRIMARY, 
    SUCCESS, 
    OUTLINE, 
    DANGER, 
    PREMIUM 
} 
    
@Composable
fun ShinobiTactileButton( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    faceColor: Color = ShinobiColors.ShinobiBlue, 
    lipColor: Color = ShinobiColors.ShinobiBlueLip, 
    contentColor: Color = Color.White, 
    enabled: Boolean = true, 
    shape: RoundedCornerShape = ShinobiShapes.SquircleMedium, 
    buttonHeight: Dp = 50.dp, 
    lipHeight: Dp = 3.dp, 
    content: @Composable RowScope.() -> Unit 
) { 
    val interactionSource = remember { MutableInteractionSource() } 
    val isPressed by interactionSource.collectIsPressedAsState() 
    val view = LocalView.current 
    
    val currentBottomLip by animateDpAsState( 
        targetValue = if (isPressed && enabled) 0.dp else lipHeight, 
        label = "buttonBottomLip" 
    ) 
    val topPush by animateDpAsState( 
        targetValue = if (isPressed && enabled) lipHeight else 0.dp, 
        label = "buttonTopPush" 
    ) 
    
    LaunchedEffect(isPressed) { 
        if (isPressed && enabled) { 
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
        } 
    } 
    
    Box( 
        modifier = modifier 
            .padding(top = topPush) 
            .clip(shape) 
            .background(if (enabled) lipColor else ShinobiColors.Stone700) 
            .clickable( 
                interactionSource = interactionSource, 
                indication = null, 
                enabled = enabled, 
                onClick = onClick 
            ) 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(bottom = currentBottomLip) 
                .clip(shape) 
                .background(if (enabled) faceColor else ShinobiColors.Stone800) 
                .border( 
                    width = 2.dp, 
                    color = if (enabled) lipColor else ShinobiColors.Stone700, 
                    shape = shape 
                ) 
                .height(buttonHeight) 
                .padding(horizontal = 16.dp), 
            contentAlignment = Alignment.Center 
        ) { 
            CompositionLocalProvider(LocalContentColor provides if (enabled) contentColor else ShinobiColors.TextMuted) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    content = content 
                ) 
            } 
        } 
    } 
} 
    
@Composable
fun ShinobiTextButton( 
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
        TactileButtonVariant.PRIMARY -> Triple(ShinobiColors.ShinobiBlue, ShinobiColors.ShinobiBlueLip, Color.White) 
        TactileButtonVariant.SUCCESS -> Triple(ShinobiColors.ShinobiGreen, ShinobiColors.ShinobiGreenLip, Color.White) 
        TactileButtonVariant.OUTLINE -> Triple(ShinobiColors.Stone800, ShinobiColors.Stone600, Color.White) 
        TactileButtonVariant.DANGER -> Triple(ShinobiColors.ShinobiRed, ShinobiColors.ShinobiRedLip, Color.White) 
        TactileButtonVariant.PREMIUM -> Triple(ShinobiColors.ShinobiAmber, ShinobiColors.ShinobiAmberLip, Color(0xFF1E1D20)) 
    } 
    val finalFace = faceColor ?: defaultColors.first 
    val finalLip = lipColor ?: defaultColors.second 
    val finalContentColor = contentColor ?: defaultColors.third 
    
    ShinobiTactileButton( 
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
            fontFamily = ShinobiNunito, 
            letterSpacing = 0.5.sp 
        ) 
    } 
} 
