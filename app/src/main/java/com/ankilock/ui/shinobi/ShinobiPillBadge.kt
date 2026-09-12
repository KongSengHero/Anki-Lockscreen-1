package com.ankilock.ui.shinobi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShinobiPillBadge( 
    text: String, 
    modifier: Modifier = Modifier, 
    icon: ImageVector? = null, 
    textColor: Color = ShinobiColors.TextPrimary, 
    borderColor: Color = Color.Transparent, 
    backgroundColor: Color = ShinobiColors.SurfaceCard2 
) { 
    Row( 
        modifier = modifier 
            .clip(ShinobiShapes.Pill) 
            .background(backgroundColor) 
            .border(1.dp, borderColor, ShinobiShapes.Pill) 
            .padding(horizontal = 10.dp, vertical = 4.dp), 
        verticalAlignment = Alignment.CenterVertically 
    ) { 
        if (icon != null) { 
            Icon( 
                imageVector = icon, 
                contentDescription = null, 
                tint = textColor, 
                modifier = Modifier.size(14.dp) 
            ) 
            Spacer(modifier = Modifier.width(4.dp)) 
        } 
        Text( 
            text = text, 
            color = textColor, 
            fontSize = 12.sp, 
            fontWeight = FontWeight.SemiBold 
        ) 
    } 
} 
