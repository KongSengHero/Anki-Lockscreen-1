package com.ankilock.ui.blossom
    
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.StoryWordItem
import com.ankilock.ui.blossom.story.RubySegment
import com.ankilock.ui.blossom.story.StoryToken
import com.ankilock.util.RomajiHelper
    
@Composable
fun BlossomStoryTokenView( 
    token: StoryToken, 
    showPronunciation: Boolean, 
    pronunciationType: String = "japanese", 
    enlargeTextFont: Boolean = false, 
    isAudioHighlighted: Boolean = false, 
    isSelected: Boolean = false, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val baseFontSize = if (enlargeTextFont) 25.sp else 21.sp 
    val rubyFontSize = if (enlargeTextFont) 12.sp else 11.sp 
    val rubySlotHeight = if (enlargeTextFont) 16.dp else 14.dp 
    val underlineHeight = if (enlargeTextFont) 3.dp else 2.5.dp 
    val underlineColor = Color(0xFF3B82F6) 
    
    val hasKanji = token.surface.any { it in '\u4E00'..'\u9FAF' || it in '\u3400'..'\u4DBF' } 
    val shouldUnderline = hasKanji && !token.isPunctuation && !token.isName 
    
    val bgModifier = when { 
        isSelected -> Modifier.background(Color(0xFF2563EB).copy(alpha = 0.35f)) 
        isAudioHighlighted -> Modifier.background(Color(0xFF1E3A8A).copy(alpha = 0.25f)) 
        else -> Modifier 
    } 
    
    val tokenPaddingBottom = underlineHeight + 4.dp 
    
    Box( 
        modifier = modifier 
            .clip(RoundedCornerShape(4.dp)) 
            .then(bgModifier) 
            .clickable(enabled = !token.isPunctuation) { onClick() } 
            .drawBehind { 
                if (shouldUnderline) { 
                    val strokeH = underlineHeight.toPx() 
                    drawRect( 
                        color = underlineColor, 
                        topLeft = Offset(0f, size.height - strokeH), 
                        size = Size(size.width, strokeH) 
                    ) 
                } 
            } 
            .padding( 
                start = if (token.isPunctuation) 0.dp else 1.5.dp, 
                end = if (token.isPunctuation) 0.dp else 1.5.dp, 
                top = 1.dp, 
                bottom = tokenPaddingBottom 
            ) 
    ) { 
        Column( 
            horizontalAlignment = Alignment.CenterHorizontally, 
            verticalArrangement = Arrangement.Bottom 
        ) { 
            Row( 
                verticalAlignment = Alignment.Bottom 
            ) { 
                token.segments.forEach { segment -> 
                    val fallbackRuby = if (segment.text.any { com.ankilock.ui.blossom.story.StoryTokenizer.isKanji(it) }) { 
                        com.ankilock.ui.blossom.story.StoryTokenizer.getKanjiReading(segment.text) 
                    } else null 
                    val rawRuby = segment.ruby?.ifBlank { null } ?: fallbackRuby 
                    val displayRuby = when { 
                        rawRuby.isNullOrBlank() -> null 
                        pronunciationType == "romanized" -> RomajiHelper.kanaToRomaji(rawRuby) 
                        else -> rawRuby 
                    } 
                    
                    Column( 
                        horizontalAlignment = Alignment.CenterHorizontally, 
                        verticalArrangement = Arrangement.Bottom 
                    ) { 
                        if (showPronunciation) { 
                            if (!displayRuby.isNullOrBlank()) { 
                                Text( 
                                    text = displayRuby, 
                                    fontSize = rubyFontSize, 
                                    color = Color(0xFFCBD5E1), 
                                    fontWeight = FontWeight.Normal, 
                                    lineHeight = rubyFontSize, 
                                    maxLines = 1 
                                ) 
                            } else { 
                                Spacer(modifier = Modifier.height(rubySlotHeight)) 
                            } 
                        } 
                        
                        Text( 
                            text = segment.text, 
                            fontSize = baseFontSize, 
                            fontWeight = if (token.isName) FontWeight.Bold else FontWeight.Medium, 
                            color = when { 
                                token.isName -> Color(0xFFA855F7) 
                                isAudioHighlighted -> Color(0xFF60A5FA) 
                                else -> Color.White 
                            }, 
                            lineHeight = baseFontSize 
                        ) 
                    } 
                } 
            } 
        } 
    } 
} 
    
@Composable
fun BlossomReaderTokenView( 
    token: StoryWordItem, 
    index: Int, 
    showFurigana: Boolean, 
    pronunciationType: String = "japanese", 
    enlargeTextFont: Boolean = false, 
    isAudioHighlighted: Boolean = false, 
    isSelected: Boolean, 
    onTokenClick: (StoryWordItem) -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val isPunctuation = token.pos == "punct" || 
        token.surface.all { it == '、' || it == '。' || it == ' ' || it == '！' || it == '？' || it == '」' || it == '「' } 
    val isName = token.pos == "name" || token.surface in setOf("マコト", "マリ", "タロウ", "ユキ", "マット") 
    
    val storyToken = StoryToken( 
        surface = token.surface, 
        segments = listOf( 
            RubySegment( 
                text = token.surface, 
                ruby = token.furigana.ifBlank { null } 
            ) 
        ), 
        isName = isName, 
        isPunctuation = isPunctuation, 
        isTarget = token.pos == "target", 
        meaning = token.meaning 
    ) 
    
    BlossomStoryTokenView( 
        token = storyToken, 
        showPronunciation = showFurigana, 
        pronunciationType = pronunciationType, 
        enlargeTextFont = enlargeTextFont, 
        isAudioHighlighted = isAudioHighlighted, 
        isSelected = isSelected, 
        onClick = { onTokenClick(token) }, 
        modifier = modifier 
    ) 
}
