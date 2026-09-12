package com.ankilock.ui.shinobi
    
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    
@Composable
fun ShinobiSquircleButton( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    icon: ImageVector? = null, 
    text: String? = null, 
    contentDescription: String? = null, 
    borderColor: Color = ShinobiColors.CardBorder, 
    activeBorderColor: Color = ShinobiColors.ShinobiBlue, 
    iconColor: Color = ShinobiColors.TextPrimary, 
    backgroundColor: Color = ShinobiColors.Stone800, 
    activeBackgroundColor: Color = ShinobiColors.Stone900, 
    size: Dp = 44.dp, 
    iconSize: Dp = 22.dp, 
    active: Boolean = false, 
    enabled: Boolean = true 
) { 
    val interactionSource = remember { MutableInteractionSource() } 
    val isPressed by interactionSource.collectIsPressedAsState() 
    val view = LocalView.current 
    
    val topPush by animateDpAsState( 
        targetValue = if (isPressed && enabled) 2.dp else 0.dp, 
        label = "squircleTopPush" 
    ) 
    
    LaunchedEffect(isPressed) { 
        if (isPressed && enabled) { 
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
        } 
    } 
    
    val effectiveBorderColor = when { 
        !enabled -> ShinobiColors.Stone700 
        active -> activeBorderColor 
        else -> borderColor 
    } 
    val effectiveBg = when { 
        !enabled -> ShinobiColors.Stone800.copy(alpha = 0.5f) 
        active -> activeBackgroundColor 
        else -> backgroundColor 
    } 
    
    Box( 
        modifier = modifier 
            .padding(top = topPush) 
            .size(size) 
            .clip(ShinobiShapes.SquircleSmall) 
            .background(effectiveBg) 
            .border( 
                width = if (active) 2.dp else 1.5.dp, 
                color = effectiveBorderColor, 
                shape = ShinobiShapes.SquircleSmall 
            ) 
            .clickable( 
                interactionSource = interactionSource, 
                indication = null, 
                enabled = enabled, 
                onClick = onClick 
            ), 
        contentAlignment = Alignment.Center 
    ) { 
        if (icon != null) { 
            Icon( 
                imageVector = icon, 
                contentDescription = contentDescription, 
                tint = if (enabled) (if (active) activeBorderColor else iconColor) else ShinobiColors.TextMuted, 
                modifier = Modifier.size(iconSize) 
            ) 
        } else if (text != null) { 
            Text( 
                text = text, 
                color = if (enabled) (if (active) activeBorderColor else iconColor) else ShinobiColors.TextMuted, 
                fontSize = 17.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = ShinobiNunito 
            ) 
        } 
    } 
} 
