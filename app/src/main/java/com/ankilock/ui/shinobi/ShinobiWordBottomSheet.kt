package com.ankilock.ui.shinobi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.StoryWordItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShinobiWordBottomSheet( 
    wordItem: StoryWordItem, 
    isBookmarked: Boolean, 
    showFurigana: Boolean, 
    onToggleFurigana: () -> Unit, 
    onToggleBookmark: () -> Unit, 
    onPlayAudio: () -> Unit, 
    onDismiss: () -> Unit 
) { 
    val sheetState = rememberModalBottomSheetState() 
    
    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = ShinobiColors.SurfaceOverlay, 
        dragHandle = { 
            Box( 
                modifier = Modifier 
                    .padding(vertical = 12.dp) 
                    .size(width = 38.dp, height = 4.dp) 
                    .clip(ShinobiShapes.Pill) 
                    .background(ShinobiColors.CardBorder) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 20.dp, vertical = 8.dp) 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Column { 
                    if (showFurigana && wordItem.furigana.isNotEmpty()) { 
                        Text( 
                            text = wordItem.furigana, 
                            color = ShinobiColors.TextSecondary, 
                            fontSize = 14.sp 
                        ) 
                    } 
                    Text( 
                        text = wordItem.surface, 
                        color = ShinobiColors.TextPrimary, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Bold 
                    ) 
                } 
                
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { 
                    ShinobiSquircleButton( 
                        onClick = onPlayAudio, 
                        icon = Icons.AutoMirrored.Filled.VolumeUp, 
                        contentDescription = "Pronounce" 
                    ) 
                    ShinobiSquircleButton( 
                        onClick = onToggleFurigana, 
                        icon = Icons.Default.Translate, 
                        contentDescription = "Furigana Toggle", 
                        iconColor = if (showFurigana) ShinobiColors.ElectricBlue else ShinobiColors.TextMuted, 
                        borderColor = if (showFurigana) ShinobiColors.ElectricBlue else ShinobiColors.CardBorder 
                    ) 
                    ShinobiSquircleButton( 
                        onClick = onToggleBookmark, 
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, 
                        contentDescription = "Bookmark", 
                        iconColor = if (isBookmarked) ShinobiColors.WarmAmber else ShinobiColors.TextSecondary, 
                        borderColor = if (isBookmarked) ShinobiColors.WarmAmber else ShinobiColors.CardBorder 
                    ) 
                } 
            } 
            
            if (wordItem.pos.isNotEmpty()) { 
                Spacer(modifier = Modifier.height(12.dp)) 
                ShinobiPillBadge( 
                    text = wordItem.pos, 
                    textColor = ShinobiColors.WarmAmber, 
                    borderColor = ShinobiColors.WarmAmber.copy(alpha = 0.5f), 
                    backgroundColor = ShinobiColors.WarmAmberSurface 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
            Text( 
                text = "Definitions:", 
                color = ShinobiColors.TextPrimary, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Bold 
            ) 
            Spacer(modifier = Modifier.height(8.dp)) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(ShinobiShapes.SquircleSmall) 
                    .background(ShinobiColors.EmeraldGreenSurface) 
                    .border(1.dp, ShinobiColors.EmeraldGreen.copy(alpha = 0.4f), ShinobiShapes.SquircleSmall) 
                    .padding(14.dp) 
            ) { 
                Text( 
                    text = "• ${wordItem.english}", 
                    color = Color(0xFF86EFAC), 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Medium 
                ) 
            } 
            
            if (wordItem.kanjiBreakdown.isNotEmpty()) { 
                Spacer(modifier = Modifier.height(16.dp)) 
                Text( 
                    text = "Kanji:", 
                    color = ShinobiColors.TextPrimary, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold 
                ) 
                Spacer(modifier = Modifier.height(8.dp)) 
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(ShinobiShapes.SquircleSmall) 
                        .background(ShinobiColors.KanjiCardBg) 
                        .border(1.dp, Color(0xFF7F1D1D), ShinobiShapes.SquircleSmall) 
                        .padding(14.dp) 
                ) { 
                    Text( 
                        text = wordItem.kanjiBreakdown, 
                        color = Color(0xFFFECACA), 
                        fontSize = 14.sp 
                    ) 
                } 
            } 
            Spacer(modifier = Modifier.height(24.dp)) 
        } 
    } 
} 
