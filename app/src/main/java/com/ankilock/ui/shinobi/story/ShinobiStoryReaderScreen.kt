package com.ankilock.ui.shinobi.story
    
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.ForgedStory
import com.ankilock.data.StorySentenceItem
import com.ankilock.data.StorySessionManager
import com.ankilock.data.StoryWordItem
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiNunito
import com.ankilock.ui.shinobi.ShinobiReaderTokenView
import com.ankilock.ui.shinobi.ShinobiShapes
import com.ankilock.ui.shinobi.ShinobiSquircleButton
import com.ankilock.ui.shinobi.ShinobiTextButton
import com.ankilock.ui.shinobi.ShinobiWordBottomSheet
import com.ankilock.ui.shinobi.StoryArtworkThumbnail
import com.ankilock.ui.shinobi.TactileButtonVariant
import com.ankilock.ui.study.tokenizeStorySentence
import com.ankilock.util.AudioPlayerHelper
    
@OptIn(ExperimentalLayoutApi::class) 
@Composable
fun ShinobiStoryReaderScreen( 
    story: ForgedStory, 
    currentPage: Int = 0, 
    onPageChange: (Int) -> Unit = {}, 
    onFinishStory: () -> Unit, 
    onBack: () -> Unit, 
    audioPlayer: AudioPlayerHelper, 
    modifier: Modifier = Modifier 
) { 
    val context = LocalContext.current 
    val scrollState = rememberScrollState() 
    var showFurigana by remember { mutableStateOf(true) } 
    var isSlowSpeed by remember { mutableStateOf(false) } 
    var showTranslation by remember { mutableStateOf(false) } 
    var selectedWordForDetail by remember { mutableStateOf<StoryWordItem?>(null) } 
    
    val sentences = story.sentences.ifEmpty { 
        listOf(StorySentenceItem(id = 1, japanese = story.storyJapanese, english = story.storyEnglish)) 
    } 
    val totalPages = sentences.size 
    val activeSentence = sentences.getOrElse(currentPage.coerceIn(0, totalPages - 1)) { sentences[0] } 
    val isCurrentSaved = StorySessionManager.isCurrentStorySaved() 
    val isPlayingThis = audioPlayer.currentPlayingSentenceIndex == currentPage 
    
    Box( 
        modifier = modifier 
            .fillMaxSize() 
            .background(ShinobiColors.Stone950) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .verticalScroll(scrollState) 
                .padding(horizontal = 16.dp, vertical = 10.dp) 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                ShinobiSquircleButton( 
                    onClick = { 
                        audioPlayer.stop() 
                        onBack() 
                    }, 
                    icon = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back" 
                ) 
                
                Box( 
                    modifier = Modifier 
                        .width(160.dp) 
                        .height(28.dp) 
                        .clip(ShinobiShapes.Pill) 
                        .background(ShinobiColors.Stone900) 
                        .border(1.dp, ShinobiColors.Stone700, ShinobiShapes.Pill), 
                    contentAlignment = Alignment.CenterStart 
                ) { 
                    val progressFraction = (currentPage + 1f) / totalPages.coerceAtLeast(1) 
                    Box( 
                        modifier = Modifier 
                            .fillMaxHeight() 
                            .fillMaxWidth(progressFraction) 
                            .background(ShinobiColors.ShinobiGreen) 
                    ) 
                    Text( 
                        text = "${currentPage + 1} / $totalPages", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = ShinobiNunito, 
                        color = Color.White, 
                        modifier = Modifier.align(Alignment.Center) 
                    ) 
                } 
                
                ShinobiSquircleButton( 
                    onClick = { 
                        if (isCurrentSaved) { 
                            StorySessionManager.deleteSavedStory(context, story.id) 
                        } else { 
                            StorySessionManager.saveCurrentStory(context) 
                        } 
                    }, 
                    icon = if (isCurrentSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, 
                    contentDescription = "Save Story", 
                    iconColor = if (isCurrentSaved) ShinobiColors.ShinobiAmber else ShinobiColors.TextSecondary, 
                    borderColor = if (isCurrentSaved) ShinobiColors.ShinobiAmber else ShinobiColors.Stone700 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(RoundedCornerShape(16.dp)) 
                    .background(ShinobiColors.Stone800) 
                    .border( 
                        width = 2.dp, 
                        color = ShinobiColors.Stone600, 
                        shape = RoundedCornerShape(16.dp) 
                    ) 
            ) { 
                Column(modifier = Modifier.fillMaxWidth()) { 
                    StoryArtworkThumbnail( 
                        artworkType = story.genre, 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .aspectRatio(16f / 10f) 
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)) 
                    ) 
                    
                    Column(modifier = Modifier.padding(18.dp)) { 
                        val tokens = tokenizeStorySentence(activeSentence.japanese, story.targetWords) 
                        
                        FlowRow( 
                            horizontalArrangement = Arrangement.spacedBy(4.dp), 
                            verticalArrangement = Arrangement.spacedBy(6.dp) 
                        ) { 
                            tokens.forEachIndexed { tokenIdx, token -> 
                                ShinobiReaderTokenView( 
                                    token = token, 
                                    index = tokenIdx, 
                                    showFurigana = showFurigana, 
                                    isSelected = (selectedWordForDetail?.kanji == token.kanji && token.kanji.isNotBlank()), 
                                    onTokenClick = { clickedToken -> 
                                        selectedWordForDetail = clickedToken 
                                    } 
                                ) 
                            } 
                        } 
                        
                        if (showTranslation && activeSentence.english.isNotBlank()) { 
                            Spacer(modifier = Modifier.height(14.dp)) 
                            Box( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .clip(ShinobiShapes.SquircleSmall) 
                                    .background(ShinobiColors.Stone900) 
                                    .border(1.dp, ShinobiColors.Stone700, ShinobiShapes.SquircleSmall) 
                                    .padding(12.dp) 
                            ) { 
                                Text( 
                                    text = activeSentence.english, 
                                    color = ShinobiColors.TextSecondary, 
                                    fontSize = 14.sp, 
                                    fontFamily = ShinobiNunito 
                                ) 
                            } 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(18.dp)) 
            
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(ShinobiShapes.SquircleMedium) 
                    .background(ShinobiColors.Stone900) 
                    .border(1.5.dp, ShinobiColors.Stone700, ShinobiShapes.SquircleMedium) 
                    .padding(8.dp), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                ShinobiSquircleButton( 
                    onClick = { showFurigana = !showFurigana }, 
                    text = "あ", 
                    contentDescription = "Furigana Toggle", 
                    active = showFurigana, 
                    activeBorderColor = ShinobiColors.ShinobiBlue 
                ) 
                
                ShinobiSquircleButton( 
                    onClick = { isSlowSpeed = !isSlowSpeed }, 
                    icon = Icons.Default.HourglassTop, 
                    contentDescription = "Speed Snail", 
                    active = isSlowSpeed, 
                    activeBorderColor = ShinobiColors.ShinobiAmber 
                ) 
                
                ShinobiSquircleButton( 
                    onClick = { 
                        if (isPlayingThis) { 
                            audioPlayer.stop() 
                        } else { 
                            audioPlayer.playSentenceText(activeSentence.japanese) 
                        } 
                    }, 
                    icon = if (isPlayingThis) Icons.Default.Stop else Icons.Default.PlayArrow, 
                    contentDescription = "Audio Play", 
                    iconColor = ShinobiColors.ShinobiRed, 
                    borderColor = ShinobiColors.ShinobiRed.copy(alpha = 0.5f), 
                    active = isPlayingThis, 
                    activeBorderColor = ShinobiColors.ShinobiRed 
                ) 
                
                ShinobiSquircleButton( 
                    onClick = { showTranslation = !showTranslation }, 
                    icon = Icons.Default.Translate, 
                    contentDescription = "Translation", 
                    active = showTranslation, 
                    activeBorderColor = ShinobiColors.ShinobiBlue 
                ) 
                
                ShinobiSquircleButton( 
                    onClick = { 
                        if (currentPage > 0) onPageChange(currentPage - 1) 
                    }, 
                    icon = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Prev Page", 
                    enabled = currentPage > 0 
                ) 
                
                ShinobiSquircleButton( 
                    onClick = { 
                        if (currentPage < totalPages - 1) { 
                            onPageChange(currentPage + 1) 
                        } else { 
                            audioPlayer.stop() 
                            onFinishStory() 
                        } 
                    }, 
                    icon = Icons.AutoMirrored.Filled.ArrowForward, 
                    contentDescription = "Next Page" 
                ) 
            } 
            
            if (currentPage == totalPages - 1) { 
                Spacer(modifier = Modifier.height(18.dp)) 
                ShinobiTextButton( 
                    text = "TAKE THE QUIZ →", 
                    onClick = { 
                        audioPlayer.stop() 
                        onFinishStory() 
                    }, 
                    modifier = Modifier.fillMaxWidth(), 
                    variant = TactileButtonVariant.PRIMARY 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(24.dp)) 
        } 
        
        if (selectedWordForDetail != null) { 
            val wordItem = selectedWordForDetail!! 
            ShinobiWordBottomSheet( 
                wordItem = wordItem, 
                isBookmarked = false, 
                showFurigana = showFurigana, 
                onToggleFurigana = { showFurigana = !showFurigana }, 
                onToggleBookmark = { }, 
                onPlayAudio = { audioPlayer.playSentenceText(wordItem.surface) }, 
                onDismiss = { selectedWordForDetail = null } 
            ) 
        } 
    } 
} 
