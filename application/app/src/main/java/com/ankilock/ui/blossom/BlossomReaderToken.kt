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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
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
    val baseFontSize = if (enlargeTextFont) 22.sp else 17.sp 
    val rubyFontSize = if (enlargeTextFont) 11.sp else 9.5.sp 
    val rubySlotHeight = if (enlargeTextFont) 14.dp else 12.dp 
    
    val bgModifier = when { 
        isSelected -> Modifier.background(BlossomColors.SakuraRoseContainer) 
        isAudioHighlighted -> Modifier.background(BlossomColors.SakuraRose.copy(alpha = 0.25f)) 
        else -> Modifier 
    } 
    
    Box( 
        modifier = modifier 
            .clip(RoundedCornerShape(4.dp)) 
            .then(bgModifier) 
            .clickable(enabled = true) { onClick() } 
            .padding( 
                start = if (token.isPunctuation) 0.dp else 1.dp, 
                end = if (token.isPunctuation) 0.dp else 1.dp, 
                top = 0.dp, 
                bottom = 2.dp 
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
                                    color = BlossomColors.TextSecondary, 
                                    fontWeight = FontWeight.Normal, 
                                    lineHeight = rubyFontSize, 
                                    maxLines = 1, 
                                    style = TextStyle( 
                                        platformStyle = PlatformTextStyle( 
                                            includeFontPadding = false 
                                        ) 
                                    ), 
                                    modifier = Modifier.offset(y = 2.dp) 
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
                                isSelected -> BlossomColors.SakuraRose 
                                token.isName -> Color(0xFFA855F7) 
                                isAudioHighlighted -> Color(0xFF60A5FA) 
                                else -> BlossomColors.TextPrimary 
                            }, 
                            lineHeight = baseFontSize, 
                            style = TextStyle( 
                                platformStyle = PlatformTextStyle( 
                                    includeFontPadding = false 
                                ) 
                            ) 
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
