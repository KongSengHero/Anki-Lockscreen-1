package com.ankilock.ui.blossom
    
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
fun BlossomSquircleButton( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    icon: ImageVector? = null, 
    text: String? = null, 
    contentDescription: String? = null, 
    borderColor: Color = BlossomColors.CardBorder, 
    activeBorderColor: Color = BlossomColors.BlossomBlue, 
    iconColor: Color = BlossomColors.TextPrimary, 
    backgroundColor: Color = BlossomColors.Stone800, 
    activeBackgroundColor: Color = BlossomColors.Stone900, 
    size: Dp = 44.dp, 
    iconSize: Dp = 22.dp, 
    active: Boolean = false, 
    enabled: Boolean = true 
) { 
    val interactionSource = remember { MutableInteractionSource() } 
    val isPressed by interactionSource.collectIsPressedAsState() 
    val context = androidx.compose.ui.platform.LocalContext.current 
    val view = LocalView.current 
    
    val topPush by animateDpAsState( 
        targetValue = if (isPressed && enabled) 2.dp else 0.dp, 
        label = "squircleTopPush" 
    ) 
    
    LaunchedEffect(isPressed) { 
        if (isPressed && enabled) { 
            BlossomHaptics.click(context, view) 
        } 
    } 
    
    val effectiveBorderColor = when { 
        !enabled -> BlossomColors.Stone700 
        active -> activeBorderColor 
        else -> borderColor 
    } 
    val effectiveBg = when { 
        !enabled -> BlossomColors.Stone800.copy(alpha = 0.5f) 
        active -> activeBackgroundColor 
        else -> backgroundColor 
    } 
    
    Box( 
        modifier = modifier 
            .padding(top = topPush) 
            .size(size) 
            .clip(BlossomShapes.SquircleSmall) 
            .background(effectiveBg) 
            .border( 
                width = if (active) 2.dp else 1.5.dp, 
                color = effectiveBorderColor, 
                shape = BlossomShapes.SquircleSmall 
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
                tint = if (enabled) (if (active) activeBorderColor else iconColor) else BlossomColors.TextMuted, 
                modifier = Modifier.size(iconSize) 
            ) 
        } else if (text != null) { 
            Text( 
                text = text, 
                color = if (enabled) (if (active) activeBorderColor else iconColor) else BlossomColors.TextMuted, 
                fontSize = 17.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = BlossomNunito 
            ) 
        } 
    } 
} 
