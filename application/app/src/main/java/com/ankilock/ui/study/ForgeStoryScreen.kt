package com.ankilock.ui.study
    
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ai.AiServiceHelper
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.ForgedStory
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StorySentenceItem
import com.ankilock.data.StorySessionManager
import com.ankilock.data.StoryWordItem
import com.ankilock.data.calculatedEstimatedMinutes
import com.ankilock.ui.components.squircleLiquidGlass
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomShapes
import com.ankilock.ui.blossom.BlossomStoryTokenView
import com.ankilock.ui.blossom.BlossomWordBottomSheet
import com.ankilock.ui.blossom.story.StoryTokenizer
import com.ankilock.util.AudioPlayerHelper
import com.ankilock.util.JapaneseTtsHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
    
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ForgeStoryScreen( 
    ankiHelper: AnkiDroidHelper, 
    prefs: PreferencesManager, 
    audioPlayer: AudioPlayerHelper, 
    onOpenAiConfig: () -> Unit, 
    onNavigateToForged: () -> Unit = {} 
) { 
    val context = LocalContext.current 
    val coroutineScope = rememberCoroutineScope() 
    val ttsHelper = remember { JapaneseTtsHelper(context) } 
    
    DisposableEffect(Unit) { 
        StorySessionManager.loadSavedStories(context) 
        onDispose { 
            ttsHelper.stop() 
            ttsHelper.shutdown() 
        } 
    } 
    
    val jlptLevels = listOf("N5", "N4", "N3", "N2", "N1") 
    var selectedLevel by remember { 
        mutableStateOf(if (prefs.readingJlptLevel.isNotBlank()) prefs.readingJlptLevel else "N5") 
    } 
    var useStudiedWords by remember { mutableStateOf(prefs.connectStudiedWords) } 
    var showFurigana by remember { mutableStateOf(true) } 
    var showLevelMenu by remember { mutableStateOf(false) } 
    var showHistorySheet by remember { mutableStateOf(false) } 
    
    var isGenerating by remember { mutableStateOf(false) } 
    var errorMessage by remember { mutableStateOf<String?>(null) } 
    var currentlyPlayingSentenceId by remember { mutableStateOf<Int?>(null) } 
    var selectedSentenceForTranslation by remember { mutableStateOf<StorySentenceItem?>(null) } 
    var selectedWordForDetail by remember { mutableStateOf<StoryWordItem?>(null) } 
    
    val currentStory = StorySessionManager.currentStory 
    
    LaunchedEffect(currentlyPlayingSentenceId) { 
        if (currentlyPlayingSentenceId != null) { 
            while (ttsHelper.isSpeaking()) { 
                delay(200) 
            } 
            currentlyPlayingSentenceId = null 
        } 
    } 
    
    BackHandler(enabled = currentStory != null) { 
        ttsHelper.stop() 
        currentlyPlayingSentenceId = null 
        StorySessionManager.currentStory = null 
    } 
    
    fun triggerGeneration() { 
        if (prefs.aiApiKey.isBlank()) { 
            onOpenAiConfig() 
            return 
        } 
        coroutineScope.launch { 
            isGenerating = true 
            errorMessage = null 
            ttsHelper.stop() 
            currentlyPlayingSentenceId = null 
            try { 
                val targetCards = if (useStudiedWords) { 
                    val selectedDecks = prefs.getSelectedDeckIdsAsLongs() 
                    val due = ankiHelper.getDistinctDueCards(selectedDecks, 6) 
                    if (due.isEmpty()) ankiHelper.getDistinctDueCards(limit = 6) else due 
                } else { 
                    emptyList() 
                } 
                val result = AiServiceHelper.forgeStory( 
                    cards = targetCards, 
                    genre = "Daily Life", 
                    level = selectedLevel, 
                    length = "Medium", 
                    apiKey = prefs.aiApiKey, 
                    provider = prefs.aiProvider, 
                    model = prefs.aiModel 
                ) 
                if (result.isSuccess) { 
                    val story = result.getOrNull() 
                    if (story != null) { 
                        StorySessionManager.currentStory = story 
                        StorySessionManager.saveStory(context, story) 
                    } 
                } else { 
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to generate story" 
                } 
            } catch (e: Exception) { 
                errorMessage = e.message ?: "Unexpected error" 
            } finally { 
                isGenerating = false 
            } 
        } 
    } 
    
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() 
    
    Box( 
        modifier = Modifier 
            .fillMaxSize() 
            .background(Color.Transparent) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .verticalScroll(rememberScrollState()) 
                .padding(horizontal = 16.dp), 
            verticalArrangement = Arrangement.spacedBy(16.dp) 
        ) { 
            Spacer(modifier = Modifier.height(statusBarTop + 58.dp)) 
            
            Card( 
                modifier = Modifier.fillMaxWidth(), 
                shape = BlossomShapes.SquircleLarge, 
                colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
                border = BorderStroke(1.dp, BlossomColors.CardBorder) 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(16.dp), 
                    verticalArrangement = Arrangement.spacedBy(14.dp) 
                ) { 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        Box { 
                            Surface( 
                                onClick = { showLevelMenu = true }, 
                                shape = RoundedCornerShape(10.dp), 
                                color = BlossomColors.SurfaceCard2, 
                                border = BorderStroke(1.dp, BlossomColors.CardBorder) 
                            ) { 
                                Row( 
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.spacedBy(6.dp) 
                                ) { 
                                    Text( 
                                        text = "Level", 
                                        fontSize = 12.sp, 
                                        color = BlossomColors.TextMuted 
                                    ) 
                                    Text( 
                                        text = selectedLevel, 
                                        fontSize = 13.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = BlossomColors.MatchaSage 
                                    ) 
                                } 
                            } 
                            DropdownMenu( 
                                expanded = showLevelMenu, 
                                onDismissRequest = { showLevelMenu = false }, 
                                modifier = Modifier.background(BlossomColors.SurfaceCard2) 
                            ) { 
                                jlptLevels.forEach { lvl -> 
                                    DropdownMenuItem( 
                                        text = { 
                                            Text( 
                                                text = lvl, 
                                                fontWeight = if (lvl == selectedLevel) FontWeight.Bold else FontWeight.Normal, 
                                                color = if (lvl == selectedLevel) BlossomColors.MatchaSage else Color.White 
                                            ) 
                                        }, 
                                        onClick = { 
                                            selectedLevel = lvl 
                                            prefs.readingJlptLevel = lvl 
                                            showLevelMenu = false 
                                        } 
                                    ) 
                                } 
                            } 
                        } 
                        
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(8.dp) 
                        ) { 
                            Surface( 
                                onClick = { showFurigana = !showFurigana }, 
                                shape = RoundedCornerShape(10.dp), 
                                color = if (showFurigana) BlossomColors.SlateBlue.copy(alpha = 0.2f) else BlossomColors.SurfaceCard2, 
                                border = BorderStroke(1.dp, if (showFurigana) BlossomColors.SlateBlue else BlossomColors.CardBorder) 
                            ) { 
                                Text( 
                                    text = if (showFurigana) "Furigana ON" else "Furigana OFF", 
                                    fontSize = 11.5.sp, 
                                    fontWeight = FontWeight.SemiBold, 
                                    color = if (showFurigana) BlossomColors.SlateBlue else BlossomColors.TextSecondary, 
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp) 
                                ) 
                            } 
                            
                            IconButton( 
                                onClick = { showHistorySheet = true }, 
                                modifier = Modifier 
                                    .size(36.dp) 
                                    .clip(RoundedCornerShape(10.dp)) 
                                    .background(BlossomColors.SurfaceCard2) 
                                    .border(1.dp, BlossomColors.CardBorder, RoundedCornerShape(10.dp)) 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.History, 
                                    contentDescription = "Story History", 
                                    tint = BlossomColors.TextSecondary, 
                                    modifier = Modifier.size(18.dp) 
                                ) 
                            } 
                            
                            IconButton( 
                                onClick = onOpenAiConfig, 
                                modifier = Modifier 
                                    .size(36.dp) 
                                    .clip(RoundedCornerShape(10.dp)) 
                                    .background(BlossomColors.SurfaceCard2) 
                                    .border(1.dp, BlossomColors.CardBorder, RoundedCornerShape(10.dp)) 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.Key, 
                                    contentDescription = "AI Settings", 
                                    tint = BlossomColors.TextSecondary, 
                                    modifier = Modifier.size(18.dp) 
                                ) 
                            } 
                        } 
                    } 
                    
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        Column { 
                            Text( 
                                text = "Use Studied Anki Words", 
                                fontSize = 13.5.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = Color.White 
                            ) 
                            Text( 
                                text = if (useStudiedWords) "Incorporate your due cards" else "Free creative level immersion", 
                                fontSize = 11.5.sp, 
                                color = BlossomColors.TextMuted 
                            ) 
                        } 
                        Switch( 
                            checked = useStudiedWords, 
                            onCheckedChange = { checked -> 
                                useStudiedWords = checked 
                                prefs.connectStudiedWords = checked 
                            }, 
                            colors = SwitchDefaults.colors( 
                                checkedThumbColor = Color.White, 
                                checkedTrackColor = BlossomColors.MatchaSage, 
                                uncheckedThumbColor = BlossomColors.TextMuted, 
                                uncheckedTrackColor = BlossomColors.SurfaceCard2 
                            ) 
                        ) 
                    } 
                    
                    Button( 
                        onClick = { triggerGeneration() }, 
                        enabled = !isGenerating, 
                        shape = RoundedCornerShape(12.dp), 
                        colors = ButtonDefaults.buttonColors( 
                            containerColor = BlossomColors.MatchaSage, 
                            disabledContainerColor = BlossomColors.MatchaSage.copy(alpha = 0.5f) 
                        ), 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .height(46.dp) 
                    ) { 
                        if (isGenerating) { 
                            CircularProgressIndicator( 
                                modifier = Modifier.size(18.dp), 
                                strokeWidth = 2.dp, 
                                color = Color.Black 
                            ) 
                            Spacer(modifier = Modifier.width(10.dp)) 
                            Text( 
                                text = "Crafting $selectedLevel Story...", 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.Black 
                            ) 
                        } else { 
                            Icon( 
                                imageVector = Icons.Default.AutoAwesome, 
                                contentDescription = null, 
                                tint = Color.Black, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = if (currentStory == null) "Generate Graded Story" else "Forge New Story", 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.Black 
                            ) 
                        } 
                    } 
                } 
            } 
            
            errorMessage?.let { err -> 
                Card( 
                    modifier = Modifier.fillMaxWidth(), 
                    shape = RoundedCornerShape(12.dp), 
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF451A1A)), 
                    border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)) 
                ) { 
                    Row( 
                        modifier = Modifier.padding(14.dp), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        Text( 
                            text = err, 
                            color = Color(0xFFFCA5A5), 
                            fontSize = 12.5.sp, 
                            modifier = Modifier.weight(1f) 
                        ) 
                    } 
                } 
            } 
            
            if (currentStory == null && !isGenerating) { 
                Card( 
                    modifier = Modifier.fillMaxWidth(), 
                    shape = BlossomShapes.SquircleLarge, 
                    colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
                    border = BorderStroke(1.dp, BlossomColors.CardBorder) 
                ) { 
                    Column( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(28.dp), 
                        horizontalAlignment = Alignment.CenterHorizontally, 
                        verticalArrangement = Arrangement.spacedBy(12.dp) 
                    ) { 
                        Box( 
                            modifier = Modifier 
                                .size(56.dp) 
                                .clip(CircleShape) 
                                .background(BlossomColors.MatchaSage.copy(alpha = 0.15f)), 
                            contentAlignment = Alignment.Center 
                        ) { 
                            Icon( 
                                imageVector = Icons.AutoMirrored.Filled.MenuBook, 
                                contentDescription = null, 
                                tint = BlossomColors.MatchaSage, 
                                modifier = Modifier.size(28.dp) 
                            ) 
                        } 
                        Text( 
                            text = "Graded Japanese Immersion", 
                            fontSize = 17.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White 
                        ) 
                        Text( 
                            text = "Read single-page stories generated from your Anki vocabulary. Tap words for instant definitions, toggle Furigana, or listen to voice narration.", 
                            fontSize = 13.sp, 
                            color = BlossomColors.TextSecondary, 
                            textAlign = TextAlign.Center, 
                            lineHeight = 19.sp 
                        ) 
                        if (StorySessionManager.savedStoriesList.isNotEmpty()) { 
                            Spacer(modifier = Modifier.height(4.dp)) 
                            Surface( 
                                onClick = { showHistorySheet = true }, 
                                shape = RoundedCornerShape(10.dp), 
                                color = BlossomColors.SurfaceCard2, 
                                border = BorderStroke(1.dp, BlossomColors.CardBorder) 
                            ) { 
                                Row( 
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.spacedBy(6.dp) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.Default.History, 
                                        contentDescription = null, 
                                        tint = BlossomColors.MatchaSage, 
                                        modifier = Modifier.size(16.dp) 
                                    ) 
                                    Text( 
                                        text = "Open Saved Library (${StorySessionManager.savedStoriesList.size})", 
                                        fontSize = 12.5.sp, 
                                        fontWeight = FontWeight.SemiBold, 
                                        color = Color.White 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
            
            currentStory?.let { story -> 
                Card( 
                    modifier = Modifier.fillMaxWidth(), 
                    shape = BlossomShapes.SquircleLarge, 
                    colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
                    border = BorderStroke(1.dp, BlossomColors.CardBorder) 
                ) { 
                    Column( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(18.dp), 
                        verticalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.SpaceBetween 
                        ) { 
                            Surface( 
                                shape = RoundedCornerShape(6.dp), 
                                color = BlossomColors.MatchaSage.copy(alpha = 0.15f), 
                                border = BorderStroke(1.dp, BlossomColors.MatchaSage.copy(alpha = 0.4f)) 
                            ) { 
                                Text( 
                                    text = story.level.ifBlank { selectedLevel }, 
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = BlossomColors.MatchaSage, 
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp) 
                                ) 
                            } 
                            Text( 
                                text = "${story.calculatedEstimatedMinutes} min read", 
                                fontSize = 11.5.sp, 
                                color = BlossomColors.TextMuted 
                            ) 
                        } 
                        Text( 
                            text = story.title, 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color.White, 
                            lineHeight = 26.sp 
                        ) 
                        if (story.titleEnglish.isNotBlank() && story.titleEnglish != story.title) { 
                            Text( 
                                text = story.titleEnglish, 
                                fontSize = 13.sp, 
                                color = BlossomColors.TextSecondary 
                            ) 
                        } 
                    } 
                } 
                
                val sentencesToDisplay = if (story.sentences.isNotEmpty()) { 
                    story.sentences 
                } else { 
                    val chunks = story.storyJapanese.split(Regex("(?<=[。！？\n])")).filter { it.isNotBlank() } 
                    chunks.mapIndexed { idx, chk -> 
                        StorySentenceItem(id = idx + 1, japanese = chk.trim(), english = "") 
                    } 
                } 
                
                sentencesToDisplay.forEachIndexed { index, sentence -> 
                    val isSentencePlaying = (currentlyPlayingSentenceId == sentence.id) 
                    Card( 
                        modifier = Modifier.fillMaxWidth(), 
                        shape = RoundedCornerShape(16.dp), 
                        colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
                        border = BorderStroke(1.dp, BlossomColors.CardBorder) 
                    ) { 
                        Column( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .padding(16.dp) 
                        ) { 
                            val tokens = remember(sentence.japanese, sentence.furigana, story.targetWords) { 
                                val sourceText = if (sentence.furigana.isNotBlank()) sentence.furigana else sentence.japanese 
                                StoryTokenizer.tokenizeToStoryTokens(sourceText, story.targetWords) 
                            } 
                            
                            FlowRow( 
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.Start, 
                                verticalArrangement = Arrangement.Center 
                            ) { 
                                tokens.forEach { token -> 
                                    BlossomStoryTokenView( 
                                        token = token, 
                                        showPronunciation = showFurigana, 
                                        pronunciationType = "japanese", 
                                        onClick = { 
                                            if (token.surface.isNotBlank()) { 
                                                selectedWordForDetail = StoryWordItem( 
                                                    kanji = token.surface, 
                                                    reading = token.segments.joinToString("") { it.ruby ?: it.text }, 
                                                    meaning = token.meaning 
                                                ) 
                                            } 
                                        } 
                                    ) 
                                } 
                            } 
                            
                            Spacer(modifier = Modifier.height(14.dp)) 
                            
                            Row( 
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.End, 
                                verticalAlignment = Alignment.CenterVertically 
                            ) { 
                                IconButton( 
                                    onClick = { 
                                        if (isSentencePlaying) { 
                                            ttsHelper.stop() 
                                            currentlyPlayingSentenceId = null 
                                        } else { 
                                            currentlyPlayingSentenceId = sentence.id 
                                            ttsHelper.speak(sentence.japanese) 
                                        } 
                                    }, 
                                    modifier = Modifier 
                                        .size(34.dp) 
                                        .clip(RoundedCornerShape(8.dp)) 
                                        .background(if (isSentencePlaying) BlossomColors.MatchaSage.copy(alpha = 0.25f) else BlossomColors.SurfaceCard2) 
                                        .border(1.dp, if (isSentencePlaying) BlossomColors.MatchaSage else BlossomColors.CardBorder, RoundedCornerShape(8.dp)) 
                                ) { 
                                    Icon( 
                                        imageVector = if (isSentencePlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp, 
                                        contentDescription = "Play Audio", 
                                        tint = if (isSentencePlaying) BlossomColors.MatchaSage else BlossomColors.TextSecondary, 
                                        modifier = Modifier.size(17.dp) 
                                    ) 
                                } 
                                
                                Spacer(modifier = Modifier.width(8.dp)) 
                                
                                IconButton( 
                                    onClick = { selectedSentenceForTranslation = sentence }, 
                                    modifier = Modifier 
                                        .size(34.dp) 
                                        .clip(RoundedCornerShape(8.dp)) 
                                        .background(BlossomColors.SurfaceCard2) 
                                        .border(1.dp, BlossomColors.CardBorder, RoundedCornerShape(8.dp)) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.Default.Translate, 
                                        contentDescription = "Translate Paragraph", 
                                        tint = BlossomColors.WarmOchre, 
                                        modifier = Modifier.size(17.dp) 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
                
                if (story.targetWords.isNotEmpty()) { 
                    Card( 
                        modifier = Modifier.fillMaxWidth(), 
                        shape = BlossomShapes.SquircleLarge, 
                        colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
                        border = BorderStroke(1.dp, BlossomColors.CardBorder) 
                    ) { 
                        Column( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .padding(16.dp), 
                            verticalArrangement = Arrangement.spacedBy(12.dp) 
                        ) { 
                            Text( 
                                text = "Key Vocabulary", 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White 
                            ) 
                            FlowRow( 
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.spacedBy(8.dp), 
                                verticalArrangement = Arrangement.spacedBy(8.dp) 
                            ) { 
                                story.targetWords.forEach { word -> 
                                    Surface( 
                                        onClick = { selectedWordForDetail = word }, 
                                        shape = RoundedCornerShape(8.dp), 
                                        color = BlossomColors.SurfaceCard2, 
                                        border = BorderStroke(1.dp, BlossomColors.CardBorder) 
                                    ) { 
                                        Row( 
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), 
                                            verticalAlignment = Alignment.CenterVertically, 
                                            horizontalArrangement = Arrangement.spacedBy(6.dp) 
                                        ) { 
                                            Text( 
                                                text = word.kanji, 
                                                fontSize = 13.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                color = BlossomColors.MatchaSage 
                                            ) 
                                            if (word.reading.isNotBlank()) { 
                                                Text( 
                                                    text = word.reading, 
                                                    fontSize = 11.5.sp, 
                                                    color = BlossomColors.TextMuted 
                                                ) 
                                            } 
                                            if (word.meaning.isNotBlank()) { 
                                                Text( 
                                                    text = "• ${word.meaning}", 
                                                    fontSize = 11.5.sp, 
                                                    color = BlossomColors.TextSecondary, 
                                                    maxLines = 1, 
                                                    overflow = TextOverflow.Ellipsis 
                                                ) 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 
                } 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    val isSaved = StorySessionManager.isCurrentStorySaved() 
                    Surface( 
                        onClick = { 
                            if (isSaved) { 
                                StorySessionManager.deleteSavedStory(context, story.id) 
                            } else { 
                                StorySessionManager.saveCurrentStory(context) 
                            } 
                        }, 
                        shape = RoundedCornerShape(12.dp), 
                        color = BlossomColors.SurfaceCard1, 
                        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                        modifier = Modifier.weight(1f) 
                    ) { 
                        Row( 
                            modifier = Modifier.padding(vertical = 12.dp), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center 
                        ) { 
                            Icon( 
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, 
                                contentDescription = null, 
                                tint = if (isSaved) BlossomColors.MatchaSage else BlossomColors.TextSecondary, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = if (isSaved) "Saved to Library" else "Save Story", 
                                fontSize = 13.5.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = if (isSaved) BlossomColors.MatchaSage else Color.White 
                            ) 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(110.dp)) 
        } 
        
        selectedWordForDetail?.let { word -> 
            val isBookmarked = StorySessionManager.savedStoriesList.any { s -> 
                s.targetWords.any { it.kanji == word.kanji } 
            } 
            BlossomWordBottomSheet( 
                wordItem = word, 
                isBookmarked = isBookmarked, 
                showFurigana = showFurigana, 
                onToggleFurigana = { showFurigana = !showFurigana }, 
                onToggleBookmark = {}, 
                onPlayAudio = { 
                    val card = CardInfo( 
                        noteId = 0L, 
                        cardOrd = 0, 
                        question = word.kanji, 
                        answer = word.meaning, 
                        deckName = "", 
                        kanji = word.kanji, 
                        kanjiFurigana = word.reading 
                    ) 
                    audioPlayer.playWord(card) 
                }, 
                onDismiss = { selectedWordForDetail = null } 
            ) 
        } 
        
        selectedSentenceForTranslation?.let { sentence -> 
            ParagraphTranslationSheet( 
                sentence = sentence, 
                onDismiss = { selectedSentenceForTranslation = null } 
            ) 
        } 
        
        if (showHistorySheet) { 
            SavedStoriesLibrarySheet( 
                onSelectStory = { story -> 
                    StorySessionManager.currentStory = story 
                    showHistorySheet = false 
                }, 
                onDismiss = { showHistorySheet = false } 
            ) 
        } 
    } 
} 
    
@OptIn(ExperimentalMaterial3Api::class) 
@Composable 
fun ParagraphTranslationSheet( 
    sentence: StorySentenceItem, 
    onDismiss: () -> Unit 
) { 
    val context = LocalContext.current 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    
    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = Color(0xFF18181B), 
        windowInsets = WindowInsets(0), 
        dragHandle = { 
            Box( 
                modifier = Modifier 
                    .padding(top = 12.dp, bottom = 8.dp) 
                    .size(width = 44.dp, height = 4.dp) 
                    .clip(BlossomShapes.Pill) 
                    .background(Color(0xFF52525B)) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .navigationBarsPadding() 
                .padding(horizontal = 20.dp, vertical = 8.dp), 
            verticalArrangement = Arrangement.spacedBy(14.dp) 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Translate, 
                        contentDescription = null, 
                        tint = BlossomColors.WarmOchre, 
                        modifier = Modifier.size(18.dp) 
                    ) 
                    Text( 
                        text = "Paragraph Translation", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White 
                    ) 
                } 
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { 
                    IconButton( 
                        onClick = { 
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager 
                            cm.setPrimaryClip(ClipData.newPlainText("Translation", sentence.english)) 
                            Toast.makeText(context, "Translation copied", Toast.LENGTH_SHORT).show() 
                        }, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.ContentCopy, 
                            contentDescription = "Copy Translation", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                    IconButton( 
                        onClick = onDismiss, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Close, 
                            contentDescription = "Close", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                } 
            } 
            
            Surface( 
                shape = RoundedCornerShape(12.dp), 
                color = Color(0xFF27272A), 
                border = BorderStroke(1.dp, BlossomColors.CardBorder) 
            ) { 
                Text( 
                    text = sentence.japanese, 
                    fontSize = 15.sp, 
                    lineHeight = 22.sp, 
                    color = BlossomColors.TextSecondary, 
                    modifier = Modifier.padding(14.dp) 
                ) 
            } 
            
            Surface( 
                shape = RoundedCornerShape(12.dp), 
                color = BlossomColors.WarmOchre.copy(alpha = 0.08f), 
                border = BorderStroke(1.dp, BlossomColors.WarmOchre.copy(alpha = 0.3f)) 
            ) { 
                Text( 
                    text = sentence.english.ifBlank { "No translation available for this paragraph." }, 
                    fontSize = 15.sp, 
                    lineHeight = 22.sp, 
                    fontWeight = FontWeight.Medium, 
                    color = Color.White, 
                    modifier = Modifier.padding(14.dp) 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
        } 
    } 
} 
    
@OptIn(ExperimentalMaterial3Api::class) 
@Composable 
fun SavedStoriesLibrarySheet( 
    onSelectStory: (ForgedStory) -> Unit, 
    onDismiss: () -> Unit 
) { 
    val context = LocalContext.current 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    val savedList = StorySessionManager.savedStoriesList 
    
    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = Color(0xFF18181B), 
        windowInsets = WindowInsets(0), 
        dragHandle = { 
            Box( 
                modifier = Modifier 
                    .padding(top = 12.dp, bottom = 8.dp) 
                    .size(width = 44.dp, height = 4.dp) 
                    .clip(BlossomShapes.Pill) 
                    .background(Color(0xFF52525B)) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .navigationBarsPadding() 
                .padding(horizontal = 20.dp, vertical = 6.dp) 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Column { 
                    Text( 
                        text = "Saved Stories History", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White 
                    ) 
                    Text( 
                        text = "${savedList.size} stories saved on device", 
                        fontSize = 12.sp, 
                        color = BlossomColors.TextSecondary 
                    ) 
                } 
                IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) { 
                    Icon( 
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Close", 
                        tint = BlossomColors.TextSecondary, 
                        modifier = Modifier.size(18.dp) 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            
            if (savedList.isEmpty()) { 
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(vertical = 40.dp), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Text( 
                        text = "No saved stories yet.\nGenerate a story to read anytime.", 
                        fontSize = 13.sp, 
                        color = BlossomColors.TextMuted, 
                        textAlign = TextAlign.Center, 
                        lineHeight = 18.sp 
                    ) 
                } 
            } else { 
                LazyColumn( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(380.dp), 
                    verticalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    items(savedList, key = { it.id }) { story -> 
                        val formattedDate = remember(story.createdAt) { 
                            val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) 
                            sdf.format(Date(story.createdAt)) 
                        } 
                        Card( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .clickable { onSelectStory(story) }, 
                            shape = RoundedCornerShape(12.dp), 
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF27272A)), 
                            border = BorderStroke(1.dp, BlossomColors.CardBorder) 
                        ) { 
                            Row( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .padding(14.dp), 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.SpaceBetween 
                            ) { 
                                Column(modifier = Modifier.weight(1f)) { 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.spacedBy(8.dp) 
                                    ) { 
                                        Surface( 
                                            shape = RoundedCornerShape(4.dp), 
                                            color = BlossomColors.MatchaSage.copy(alpha = 0.15f) 
                                        ) { 
                                            Text( 
                                                text = story.level, 
                                                fontSize = 10.5.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                color = BlossomColors.MatchaSage, 
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                                            ) 
                                        } 
                                        Text( 
                                            text = formattedDate, 
                                            fontSize = 11.sp, 
                                            color = BlossomColors.TextMuted 
                                        ) 
                                    } 
                                    Spacer(modifier = Modifier.height(4.dp)) 
                                    Text( 
                                        text = story.title, 
                                        fontSize = 14.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = Color.White, 
                                        maxLines = 1, 
                                        overflow = TextOverflow.Ellipsis 
                                    ) 
                                    if (story.titleEnglish.isNotBlank() && story.titleEnglish != story.title) { 
                                        Text( 
                                            text = story.titleEnglish, 
                                            fontSize = 11.5.sp, 
                                            color = BlossomColors.TextSecondary, 
                                            maxLines = 1, 
                                            overflow = TextOverflow.Ellipsis 
                                        ) 
                                    } 
                                } 
                                IconButton( 
                                    onClick = { StorySessionManager.deleteSavedStory(context, story.id) }, 
                                    modifier = Modifier.size(32.dp) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.Default.DeleteOutline, 
                                        contentDescription = "Delete Story", 
                                        tint = Color(0xFFEF4444).copy(alpha = 0.8f), 
                                        modifier = Modifier.size(17.dp) 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
        } 
    } 
} 
