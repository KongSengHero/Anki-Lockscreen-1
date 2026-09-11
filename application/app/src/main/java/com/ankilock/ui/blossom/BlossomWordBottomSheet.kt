package com.ankilock.ui.blossom

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.LaunchedEffect 
import androidx.compose.runtime.getValue 
import androidx.compose.runtime.mutableStateOf 
import androidx.compose.runtime.remember 
import androidx.compose.runtime.setValue 
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.JishoServiceHelper
import com.ankilock.data.JishoWordResult
import com.ankilock.data.StoryWordItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlossomWordBottomSheet( 
    wordItem: StoryWordItem, 
    isBookmarked: Boolean, 
    showFurigana: Boolean, 
    onToggleFurigana: () -> Unit, 
    onToggleBookmark: () -> Unit, 
    onPlayAudio: () -> Unit, 
    onDismiss: () -> Unit, 
    exampleSentenceJapanese: String = "", 
    exampleSentenceEnglish: String = "" 
) { 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    var jishoResult by remember(wordItem) { mutableStateOf<JishoWordResult?>(null) } 
    var isLoadingJisho by remember(wordItem) { mutableStateOf(true) } 
 
    LaunchedEffect(wordItem) { 
        val rawQuery = wordItem.kanji.ifBlank { wordItem.surface } 
        val query = when { 
            rawQuery == "会" -> "会う" 
            rawQuery.endsWith("いました") -> rawQuery.dropLast(4) + "う" 
            rawQuery.endsWith("きました") -> rawQuery.dropLast(4) + "く" 
            rawQuery.endsWith("ぎました") -> rawQuery.dropLast(4) + "ぐ" 
            rawQuery.endsWith("しました") -> rawQuery.dropLast(4) + "す" 
            rawQuery.endsWith("ちました") -> rawQuery.dropLast(4) + "つ" 
            rawQuery.endsWith("びました") -> rawQuery.dropLast(4) + "ぶ" 
            rawQuery.endsWith("みました") -> rawQuery.dropLast(4) + "む" 
            rawQuery.endsWith("りました") -> rawQuery.dropLast(4) + "る" 
            rawQuery.endsWith("ました") -> rawQuery.dropLast(3) + "る" 
            rawQuery.endsWith("います") -> rawQuery.dropLast(3) + "う" 
            rawQuery.endsWith("きます") -> rawQuery.dropLast(3) + "く" 
            rawQuery.endsWith("ます") -> rawQuery.dropLast(2) + "る" 
            rawQuery.endsWith("って") -> rawQuery.dropLast(2) + "う" 
            rawQuery.endsWith("いて") -> rawQuery.dropLast(2) + "く" 
            rawQuery.endsWith("んで") -> rawQuery.dropLast(2) + "む" 
            else -> rawQuery 
        } 
        isLoadingJisho = true 
        jishoResult = JishoServiceHelper.searchWord(query) ?: JishoServiceHelper.searchWord(rawQuery) 
        isLoadingJisho = false 
    } 
    
    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = BlossomColors.SurfaceOverlay, 
        windowInsets = WindowInsets(0), 
        dragHandle = { 
            Box( 
                modifier = Modifier 
                    .padding(vertical = 12.dp) 
                    .size(width = 38.dp, height = 4.dp) 
                    .clip(BlossomShapes.Pill) 
                    .background(BlossomColors.CardBorder) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 20.dp, vertical = 8.dp) 
                .navigationBarsPadding() 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.Bottom 
            ) { 
                Column(modifier = Modifier.weight(1f, fill = false)) { 
                    val displayFurigana = when { 
                        wordItem.furigana.isNotEmpty() -> wordItem.furigana 
                        wordItem.reading.isNotEmpty() -> wordItem.reading 
                        jishoResult?.reading?.isNotEmpty() == true -> jishoResult?.reading ?: "" 
                        else -> "" 
                    } 
                    if (displayFurigana.isNotEmpty()) { 
                        Text( 
                            text = displayFurigana, 
                            color = BlossomColors.TextSecondary, 
                            fontSize = 14.sp, 
                            fontFamily = BlossomNunito 
                        ) 
                    } else { 
                        Spacer(modifier = Modifier.height(18.dp)) 
                    } 
                    Text( 
                        text = wordItem.surface, 
                        color = BlossomColors.TextPrimary, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = BlossomNunito 
                    ) 
                } 
                
                Row( 
                    horizontalArrangement = Arrangement.spacedBy(10.dp), 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    BlossomSquircleButton( 
                        onClick = onPlayAudio, 
                        icon = Icons.AutoMirrored.Filled.VolumeUp, 
                        contentDescription = "Pronounce" 
                    ) 
                    BlossomSquircleButton( 
                        onClick = onToggleBookmark, 
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, 
                        contentDescription = "Bookmark", 
                        iconColor = if (isBookmarked) BlossomColors.WarmAmber else BlossomColors.TextSecondary, 
                        borderColor = if (isBookmarked) BlossomColors.WarmAmber else BlossomColors.CardBorder 
                    ) 
                } 
            } 
            
            Row( 
                modifier = Modifier.padding(top = 10.dp), 
                horizontalArrangement = Arrangement.spacedBy(8.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                val jlpt = jishoResult?.jlpt 
                if (jlpt != null) { 
                    BlossomPillBadge( 
                        text = jlpt, 
                        textColor = Color(0xFF60A5FA), 
                        borderColor = Color(0xFF3B82F6).copy(alpha = 0.5f), 
                        backgroundColor = Color(0xFF1E3A8A).copy(alpha = 0.4f) 
                    ) 
                } 
                if (jishoResult?.isCommon == true) { 
                    BlossomPillBadge( 
                        text = "Common", 
                        textColor = Color(0xFF86EFAC), 
                        borderColor = Color(0xFF22C55E).copy(alpha = 0.5f), 
                        backgroundColor = Color(0xFF14532D).copy(alpha = 0.4f) 
                    ) 
                } 
                val posList = jishoResult?.partsOfSpeech?.filter { it.isNotBlank() } ?: emptyList() 
                val displayPos = posList.firstOrNull() ?: wordItem.pos 
                if (displayPos.isNotEmpty()) { 
                    BlossomPillBadge( 
                        text = displayPos, 
                        textColor = BlossomColors.WarmAmber, 
                        borderColor = BlossomColors.WarmAmber.copy(alpha = 0.5f), 
                        backgroundColor = BlossomColors.WarmAmberSurface 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
            Text( 
                text = "Definitions:", 
                color = BlossomColors.TextPrimary, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = BlossomNunito 
            ) 
            Spacer(modifier = Modifier.height(8.dp)) 
            
            val primaryMeaning = wordItem.meaning.ifBlank { wordItem.english } 
            val definitions = if (primaryMeaning.isNotBlank()) { 
                val extra = jishoResult?.definitions?.filter { !it.equals(primaryMeaning, ignoreCase = true) }?.take(2) ?: emptyList() 
                listOf(primaryMeaning) + extra 
            } else { 
                jishoResult?.definitions?.takeIf { it.isNotEmpty() } ?: listOf("No definition found") 
            } 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(BlossomShapes.SquircleSmall) 
                    .background(BlossomColors.EmeraldGreenSurface) 
                    .border(1.dp, BlossomColors.EmeraldGreen.copy(alpha = 0.4f), BlossomShapes.SquircleSmall) 
                    .padding(14.dp) 
            ) { 
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { 
                    for (def in definitions.take(3)) { 
                        Text( 
                            text = "• $def", 
                            color = Color(0xFF86EFAC), 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Medium, 
                            fontFamily = BlossomNunito 
                        ) 
                    } 
                } 
            } 
            
            if (exampleSentenceJapanese.isNotBlank()) { 
                Spacer(modifier = Modifier.height(14.dp)) 
                Text( 
                    text = "Story Context:", 
                    color = BlossomColors.TextPrimary, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = BlossomNunito 
                ) 
                Spacer(modifier = Modifier.height(8.dp)) 
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(BlossomShapes.SquircleSmall) 
                        .background(Color(0xFF1E293B)) 
                        .border(1.dp, Color(0xFF334155), BlossomShapes.SquircleSmall) 
                        .padding(14.dp) 
                ) { 
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) { 
                        Text( 
                            text = exampleSentenceJapanese, 
                            color = Color.White, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Medium, 
                            fontFamily = BlossomNunito 
                        ) 
                        if (exampleSentenceEnglish.isNotBlank()) { 
                            Text( 
                                text = exampleSentenceEnglish, 
                                color = Color(0xFF94A3B8), 
                                fontSize = 13.sp, 
                                fontFamily = BlossomNunito 
                            ) 
                        } 
                    } 
                } 
            } 
            
            if (wordItem.kanjiBreakdown.isNotEmpty()) { 
                Spacer(modifier = Modifier.height(14.dp)) 
                Text( 
                    text = "Kanji:", 
                    color = BlossomColors.TextPrimary, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = BlossomNunito 
                ) 
                Spacer(modifier = Modifier.height(8.dp)) 
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(BlossomShapes.SquircleSmall) 
                        .background(BlossomColors.KanjiCardBg) 
                        .border(1.dp, Color(0xFF7F1D1D), BlossomShapes.SquircleSmall) 
                        .padding(14.dp) 
                ) { 
                    Text( 
                        text = wordItem.kanjiBreakdown, 
                        color = Color(0xFFFECACA), 
                        fontSize = 14.sp, 
                        fontFamily = BlossomNunito 
                    ) 
                } 
            } 
            Spacer(modifier = Modifier.height(24.dp)) 
        } 
    } 
} 
