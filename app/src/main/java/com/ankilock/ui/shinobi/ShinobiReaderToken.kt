package com.ankilock.ui.shinobi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.StoryWordItem


@Composable
fun ShinobiReaderTokenView( 
    token: StoryWordItem, 
    index: Int, 
    showFurigana: Boolean, 
    isSelected: Boolean, 
    onTokenClick: (StoryWordItem) -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val underlineColor = getUnderlineColor(index) 
    
    Box( 
        modifier = modifier 
            .clip(RoundedCornerShape(6.dp)) 
            .background(if (isSelected) ShinobiColors.ElectricBlue.copy(alpha = 0.25f) else Color.Transparent) 
            .clickable { onTokenClick(token) } 
            .padding(horizontal = 2.dp, vertical = 2.dp) 
    ) { 
        Column(horizontalAlignment = Alignment.CenterHorizontally) { 
            if (showFurigana && token.furigana.isNotEmpty()) { 
                Text( 
                    text = token.furigana, 
                    color = ShinobiColors.TextSecondary, 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Normal 
                ) 
            } 
            Text( 
                text = token.surface, 
                color = ShinobiColors.TextPrimary, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Medium 
            ) 
            val isPunctuation = token.surface.all { it == '、' || it == '。' || it == ' ' || it == '！' || it == '？' || it == '」' || it == '「' } 
            if (!isPunctuation) { 
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(2.dp) 
                        .background(underlineColor) 
                ) 
            } else { 
                Spacer(modifier = Modifier.height(2.dp)) 
            } 
        } 
    } 
} 
