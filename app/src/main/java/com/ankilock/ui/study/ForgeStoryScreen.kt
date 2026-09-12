package com.ankilock.ui.study

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing 
import androidx.compose.animation.core.keyframes 
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically 
import androidx.compose.animation.slideOutVertically 
import androidx.compose.animation.togetherWith
import androidx.compose.ui.draw.drawWithContent 
import androidx.compose.ui.geometry.Size 
import androidx.compose.ui.graphics.drawscope.rotate 
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook 
import androidx.compose.material.icons.automirrored.filled.Article 
import androidx.compose.material.icons.filled.Add 
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune 
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator 
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider 
import androidx.compose.material3.SliderDefaults 
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf 
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt 
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.ForgedStory
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StorySentenceItem
import com.ankilock.data.StorySessionManager
import com.ankilock.data.StoryWordItem
import com.ankilock.data.calculatedEstimatedMinutes
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiMultipleChoiceCard
import com.ankilock.ui.shinobi.ShinobiPillBadge
import com.ankilock.ui.shinobi.ShinobiQuizOutcomeSheet
import com.ankilock.ui.shinobi.ShinobiReaderTokenView
import com.ankilock.ui.shinobi.ShinobiShapes
import com.ankilock.ui.shinobi.ShinobiSquircleButton
import com.ankilock.ui.components.GlobalSeekerContainer 
import com.ankilock.ui.shinobi.ShinobiStoryCard
import com.ankilock.ui.shinobi.ShinobiStoryModel
import com.ankilock.ui.shinobi.ShinobiTactileButton
import com.ankilock.ui.shinobi.ShinobiTactileIconButton
import com.ankilock.ui.shinobi.ShinobiTextButton
import com.ankilock.ui.shinobi.ShinobiWordBottomSheet
import com.ankilock.ui.shinobi.StoryArtworkThumbnail
import com.ankilock.ui.shinobi.ShinobiSoundEffects
import com.ankilock.ui.shinobi.story.ShinobiStoryReaderScreen
import com.ankilock.ui.shinobi.story.ShinobiExercisesScreen
import com.ankilock.ui.shinobi.story.ShinobiStoryCompletedScreen
import com.ankilock.ui.shinobi.story.ShinobiAfterFinishReadingScreen
import com.ankilock.ui.shinobi.story.StoryTokenizer 
import com.ankilock.ui.shinobi.ShinobiNunito 
import com.ankilock.util.AudioPlayerHelper 
import android.view.HapticFeedbackConstants 
import androidx.compose.animation.core.animateDpAsState 
import androidx.compose.foundation.interaction.MutableInteractionSource 
import androidx.compose.foundation.interaction.collectIsPressedAsState 
import kotlinx.coroutines.delay 
import kotlinx.coroutines.launch 
import androidx.compose.foundation.lazy.rememberLazyListState 
import androidx.compose.ui.platform.LocalView 
import java.text.SimpleDateFormat 
import java.util.Date 
import java.util.Locale 
import androidx.compose.foundation.Image 
import androidx.compose.material.icons.filled.Explore 
import androidx.compose.material.icons.filled.Favorite 
import androidx.compose.material.icons.filled.FitnessCenter 
import androidx.compose.material.icons.filled.Image 
import androidx.compose.material.icons.filled.Pets 
import androidx.compose.material.icons.filled.Restaurant 
import androidx.compose.material.icons.filled.Work 
import androidx.compose.material3.Switch 
import androidx.compose.material3.SwitchDefaults 
import androidx.compose.ui.layout.ContentScale 
import com.ankilock.data.StoryAssetLoader 
import com.ankilock.ui.shinobi.story.ShinobiSpeedStrikeGame 
import androidx.compose.ui.zIndex 
import androidx.compose.ui.geometry.Offset 
import androidx.compose.ui.graphics.asImageBitmap 
import androidx.compose.ui.graphics.ImageBitmap 
import android.graphics.BitmapFactory 
import java.io.File 
import androidx.compose.animation.core.rememberInfiniteTransition 
import androidx.compose.animation.core.animateFloat 
import androidx.compose.animation.core.infiniteRepeatable 
import androidx.compose.animation.core.RepeatMode 
import androidx.compose.animation.core.LinearEasing 
import com.ankilock.data.ForgeSlotState 
import com.ankilock.R 
import androidx.compose.ui.res.painterResource 
import androidx.compose.ui.graphics.graphicsLayer 
import com.ankilock.data.ForgeSlotData 
    
enum class ForgeScreenState { 
    HOME, 
    LEVEL_JOURNEY, 
    FORGE_STUDIO, 
    FORGE_GAME, 
    DETAILS, 
    READER 
} 

enum class StoryPhase { 
    READER, 
    FINISH_PROMPT, 
    EXERCISES, 
    COMPLETED 
} 

fun tokenizeStorySentence(sentence: String, targetWords: List<StoryWordItem>): List<StoryWordItem> { 
    return StoryTokenizer.tokenize(sentence, targetWords) 
} 

fun storyContainsWord(word: StoryWordItem, stories: List<ForgedStory>): Boolean { 
    return stories.any { s -> s.targetWords.any { it.kanji == word.kanji } } 
} 

@Composable
fun LevelProgressDots(completedCount: Int, total: Int = 5) { 
    Row( 
        horizontalArrangement = Arrangement.spacedBy(8.dp), 
        verticalAlignment = Alignment.CenterVertically 
    ) { 
        repeat(total) { idx -> 
            val isCompleted = idx < completedCount 
            Box( 
                modifier = Modifier 
                    .size(13.dp) 
                    .clip(CircleShape) 
                    .background( 
                        if (isCompleted) Color(0xFF10B981) else Color(0xFF134E39) 
                    ) 
            ) 
        } 
    } 
} 

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ForgeStoryScreen( 
    ankiHelper: AnkiDroidHelper, 
    prefs: PreferencesManager, 
    audioPlayer: AudioPlayerHelper, 
    onOpenAiConfig: () -> Unit, 
    onNavigateToForged: (() -> Unit)? = null 
) { 
    val context = LocalContext.current 
    val view = LocalView.current 
    val coroutineScope = rememberCoroutineScope() 
    
    val currentStory = StorySessionManager.currentStory 
    val selectedStoryForDetails = StorySessionManager.selectedStoryForDetails 
    val isGenerating = StorySessionManager.isGenerating 
    val errorMessage = StorySessionManager.errorMessage 
    var cardCountToFetch by remember { mutableIntStateOf(4) } 
    var selectedGenre by remember { mutableStateOf(prefs.storyGenre) } 
    var selectedLevel by remember { mutableStateOf(prefs.storyLevel) } 
    var selectedLength by remember { mutableStateOf(prefs.storyLength) } 
    var selectedLevelId by remember { mutableStateOf("starter") } 
    var selectedLevelName by remember { mutableStateOf("Starter") } 
    var activeSubScreen by remember { mutableStateOf(ForgeScreenState.HOME) } 
    var generateImageAi by remember { mutableStateOf(true) } 
    
    var showSavedLibrarySheet by remember { mutableStateOf(false) } 
    var showLevelSelectorSheet by remember { mutableStateOf(false) } 
    var showForgeDialog by remember { mutableStateOf(false) } 
    var showCreationSheetForSlot by remember { mutableStateOf<Int?>(null) } 
    var selectedWordForDetail by remember { mutableStateOf<StoryWordItem?>(null) } 
    
    var showFurigana by remember { mutableStateOf(true) } 
    var currentStoryPage by remember { mutableIntStateOf(0) } 
    var storyPhase by remember { mutableStateOf(StoryPhase.READER) } 
    var quizCorrectCount by remember { mutableIntStateOf(0) } 
    var quizTotalCount by remember { mutableIntStateOf(0) } 
    var quizIsPassed by remember { mutableStateOf(false) } 
    var isQuizMode by remember { mutableStateOf(false) } 
    var currentQuizIndex by remember { mutableIntStateOf(0) } 
    var quizOutcomeSubmitted by remember { mutableStateOf(false) } 
    var activeWallhavenTarget by remember { mutableStateOf<WallhavenPickerTarget?>(null) } 
    var coverRefreshKey by remember { mutableIntStateOf(0) } 
    
    val genres = listOf("Daily Life", "Traditional", "Nature", "Romance", "School", "Fantasy", "Mystery") 
    val levels = listOf("N5 Starter", "N4 Beginner", "N3 Intermediate", "N2 Advanced", "N1 Master") 
    
    val listScrollState = rememberScrollState() 
    val detailsScrollState = rememberScrollState() 
    val librarySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    val levelSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    val forgeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    val level1LazyState = rememberLazyListState() 
    val level2LazyState = rememberLazyListState() 
    val level3LazyState = rememberLazyListState() 
    val level4LazyState = rememberLazyListState() 
    val savedLazyState = rememberLazyListState() 
    var hasAutoScrolled by remember { mutableStateOf(false) } 
    
    LaunchedEffect(Unit) { 
        ShinobiSoundEffects.init(context) 
        StorySessionManager.loadSavedStories(context) 
        StorySessionManager.initCompletedStories(prefs) 
    } 
    
    var lastSelectedStoryForDetails by remember { mutableStateOf<ShinobiStoryModel?>(null) } 
    var lastCurrentStory by remember { mutableStateOf<ForgedStory?>(null) } 
    var storyLanguage by remember { mutableStateOf(prefs.storyLanguage) } 
    
    LaunchedEffect(selectedStoryForDetails) { 
        if (selectedStoryForDetails != null) { 
            lastSelectedStoryForDetails = selectedStoryForDetails 
        } 
    } 
    
    LaunchedEffect(currentStory) { 
        if (currentStory != null) { 
            lastCurrentStory = currentStory 
            storyPhase = StoryPhase.READER 
            currentStoryPage = StorySessionManager.getSavedStoryPage(currentStory.id) 
        } 
    } 
    
    LaunchedEffect(StorySessionManager.showWallhavenPickerForStoryId) { 
        val pId = StorySessionManager.showWallhavenPickerForStoryId 
        if (pId != null) { 
            val story = StorySessionManager.newlyCompletedStory ?: StorySessionManager.savedStoriesList.find { it.id == pId } 
            if (story != null) { 
                activeWallhavenTarget = WallhavenPickerTarget( 
                    storyId = story.id, 
                    title = story.title, 
                    genre = story.genre, 
                    visualAnchor = story.visualAnchor, 
                    artTags = story.artTags 
                ) 
            } 
            StorySessionManager.showWallhavenPickerForStoryId = null 
        } 
    } 
    
    val completedStoryIds = StorySessionManager.completedStoryIds 
    val progressVersion = StorySessionManager.progressVersion 
    val allCurated = remember(selectedLevelId, completedStoryIds, progressVersion) { 
        StorySessionManager.getCuratedStories(selectedLevelId, completedStoryIds) 
    } 
    val level1Stories = remember(allCurated) { allCurated.filter { it.subLevel == 1 } } 
    val level2Stories = remember(allCurated) { allCurated.filter { it.subLevel == 2 } } 
    val level3Stories = remember(allCurated) { allCurated.filter { it.subLevel == 3 } } 
    val level4Stories = remember(allCurated) { allCurated.filter { it.subLevel == 4 } } 
    
    LaunchedEffect(allCurated) { 
        if (allCurated.isNotEmpty() && !hasAutoScrolled) { 
            val currentStoryItem = allCurated.firstOrNull { it.isCurrent } 
            if (currentStoryItem != null) { 
                when (currentStoryItem.subLevel) { 
                    1 -> { 
                        val idx = level1Stories.indexOfFirst { it.id == currentStoryItem.id } 
                        if (idx > 0) level1LazyState.scrollToItem(idx) 
                    } 
                    2 -> { 
                        val idx = level2Stories.indexOfFirst { it.id == currentStoryItem.id } 
                        if (idx > 0) level2LazyState.scrollToItem(idx) 
                        listScrollState.animateScrollTo(450) 
                    } 
                    3 -> { 
                        val idx = level3Stories.indexOfFirst { it.id == currentStoryItem.id } 
                        if (idx > 0) level3LazyState.scrollToItem(idx) 
                        listScrollState.animateScrollTo(900) 
                    } 
                    4 -> { 
                        val idx = level4Stories.indexOfFirst { it.id == currentStoryItem.id } 
                        if (idx > 0) level4LazyState.scrollToItem(idx) 
                        listScrollState.animateScrollTo(1400) 
                    } 
                } 
                hasAutoScrolled = true 
            } 
        } 
    } 
    
    val isLevel2Unlocked = remember(completedStoryIds) { StorySessionManager.isLevelUnlocked(2, completedStoryIds) } 
    val isLevel3Unlocked = remember(completedStoryIds) { StorySessionManager.isLevelUnlocked(3, completedStoryIds) } 
    val isLevel4Unlocked = remember(completedStoryIds) { StorySessionManager.isLevelUnlocked(4, completedStoryIds) } 
    
    val level1Count = remember(completedStoryIds) { StorySessionManager.getLevelCompletedCount(1, completedStoryIds) } 
    val level2Count = remember(completedStoryIds) { StorySessionManager.getLevelCompletedCount(2, completedStoryIds) } 
    val level3Count = remember(completedStoryIds) { StorySessionManager.getLevelCompletedCount(3, completedStoryIds) } 
    val level4Count = remember(completedStoryIds) { StorySessionManager.getLevelCompletedCount(4, completedStoryIds) } 
    
    val screenState = when { 
        currentStory != null -> ForgeScreenState.READER 
        selectedStoryForDetails != null -> ForgeScreenState.DETAILS 
        activeSubScreen == ForgeScreenState.FORGE_GAME -> ForgeScreenState.FORGE_GAME 
        else -> activeSubScreen 
    } 
    
    BackHandler(enabled = screenState != ForgeScreenState.HOME) { 
        when (screenState) { 
            ForgeScreenState.READER -> { 
                if (storyPhase == StoryPhase.FINISH_PROMPT) { 
                    storyPhase = StoryPhase.READER 
                } else if (storyPhase == StoryPhase.EXERCISES) { 
                    storyPhase = StoryPhase.FINISH_PROMPT 
                } else { 
                    audioPlayer.stop() 
                    StorySessionManager.currentStory = null 
                    storyPhase = StoryPhase.READER 
                } 
            } 
            ForgeScreenState.DETAILS -> { 
                StorySessionManager.selectedStoryForDetails = null 
            } 
            ForgeScreenState.FORGE_GAME -> { 
                activeSubScreen = ForgeScreenState.FORGE_STUDIO 
            } 
            ForgeScreenState.FORGE_STUDIO -> { 
                activeSubScreen = ForgeScreenState.HOME 
            } 
            ForgeScreenState.LEVEL_JOURNEY -> { 
                activeSubScreen = ForgeScreenState.HOME 
            } 
            ForgeScreenState.HOME -> {} 
        } 
    } 
    
    Box( 
        modifier = Modifier 
            .fillMaxSize() 
            .background(if (screenState == ForgeScreenState.HOME || screenState == ForgeScreenState.FORGE_STUDIO || screenState == ForgeScreenState.LEVEL_JOURNEY) Color.Transparent else ShinobiColors.BackgroundDeep) 
    ) { 
        AnimatedContent( 
            targetState = screenState, 
            modifier = Modifier.fillMaxSize(), 
            transitionSpec = { 
                if (targetState.ordinal > initialState.ordinal) { 
                    (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300))) 
                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width / 3 } + fadeOut(animationSpec = tween(300))) 
                } else { 
                    (slideInHorizontally(animationSpec = tween(300)) { width -> -width / 3 } + fadeIn(animationSpec = tween(300))) 
                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300))) 
                } 
            }, 
            label = "ForgeScreenTransition" 
        ) { state -> 
            when (state) { 
                ForgeScreenState.READER -> { 
                    val story = currentStory ?: lastCurrentStory 
                    if (story != null) { 
            when (storyPhase) { 
                StoryPhase.READER -> { 
                    ShinobiStoryReaderScreen( 
                        story = story, 
                        onFinishStory = { 
                            if (story.questions.isNotEmpty()) { 
                                storyPhase = StoryPhase.FINISH_PROMPT 
                            } else { 
                                quizCorrectCount = 0 
                                quizTotalCount = 0 
                                StorySessionManager.markStoryCompleted(prefs, story.id) 
                                storyPhase = StoryPhase.COMPLETED 
                            } 
                        }, 
                        onBack = { 
                            audioPlayer.stop() 
                            StorySessionManager.currentStory = null 
                            storyPhase = StoryPhase.READER 
                        }, 
                        audioPlayer = audioPlayer 
                    ) 
                } 
                StoryPhase.FINISH_PROMPT -> { 
                    ShinobiAfterFinishReadingScreen( 
                        story = story, 
                        onTakeQuiz = { 
                            storyPhase = StoryPhase.EXERCISES 
                        }, 
                        onBackToStory = { 
                            storyPhase = StoryPhase.READER 
                        } 
                    ) 
                } 
                StoryPhase.EXERCISES -> { 
                    ShinobiExercisesScreen( 
                        story = story, 
                        audioPlayer = audioPlayer, 
                        onCompleteQuiz = { correct, total -> 
                            val isPassed = total > 0 && ((correct.toFloat() / total.toFloat()) >= 0.70f) 
                            quizCorrectCount = correct 
                            quizTotalCount = total 
                            quizIsPassed = isPassed 
                            if (isPassed) { 
                                StorySessionManager.markStoryCompleted(prefs, story.id) 
                                prefs.recordStoryCompletedToday() 
                            } 
                            storyPhase = StoryPhase.COMPLETED 
                        }, 
                        onExitQuiz = { 
                            storyPhase = StoryPhase.READER 
                        } 
                    ) 
                } 
                StoryPhase.COMPLETED -> { 
                    ShinobiStoryCompletedScreen( 
                        story = story, 
                        correctCount = quizCorrectCount, 
                        totalCount = quizTotalCount, 
                        isPassed = quizIsPassed, 
                        onContinue = { 
                            audioPlayer.stop() 
                            StorySessionManager.currentStory = null 
                            storyPhase = StoryPhase.READER 
                        }, 
                        onReadAgain = { 
                            storyPhase = StoryPhase.READER 
                        }, 
                        onRetryQuiz = { 
                            storyPhase = StoryPhase.EXERCISES 
                        } 
                    ) 
                } 
            } 
        } 
    } 
    ForgeScreenState.DETAILS -> { 
        val story = selectedStoryForDetails ?: lastSelectedStoryForDetails 
        if (story != null) { 
            Box(modifier = Modifier.fillMaxSize()) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .statusBarsPadding() 
                        .padding(horizontal = 16.dp, vertical = 10.dp) 
                ) { 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        ShinobiTactileIconButton( 
                            onClick = { StorySessionManager.selectedStoryForDetails = null }, 
                            size = 44.dp, 
                            faceColor = Color(0xFF27272A), 
                            lipColor = Color(0xFF3F3F46), 
                            iconColor = Color(0xFFE4E4E7), 
                            icon = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back" 
                        ) 
                         
                        Text( 
                            text = if (storyLanguage == "ja") "ストーリー詳細" else "Story Details", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = ShinobiNunito, 
                            color = Color.White 
                        ) 
                         
                        val isSaved = remember(story.id, StorySessionManager.savedStoriesList.size) { 
                            StorySessionManager.savedStoriesList.any { it.id == story.id } 
                        } 
                        
                        ShinobiTactileIconButton( 
                            onClick = { 
                                if (isSaved) { 
                                    StorySessionManager.deleteSavedStory(context, story.id) 
                                } else { 
                                    val forged = StorySessionManager.buildCuratedForgedStory(story.id) ?: story.story 
                                    if (forged != null) { 
                                        StorySessionManager.saveStory(context, forged) 
                                    } 
                                } 
                            }, 
                            size = 44.dp, 
                            faceColor = if (isSaved) Color(0xFF451A03) else Color(0xFF27272A), 
                            lipColor = if (isSaved) Color(0xFFF59E0B) else Color(0xFF3F3F46), 
                            iconColor = if (isSaved) Color(0xFFF59E0B) else Color(0xFFE4E4E7), 
                            icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, 
                            contentDescription = if (isSaved) "Remove from Saved" else "Save Story" 
                        ) 
                    } 
                     
                    Spacer(modifier = Modifier.height(16.dp)) 
                     
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .height(270.dp) 
                            .clip(RoundedCornerShape(24.dp)) 
                    ) { 
                        val coverBitmap = remember(story.id, coverRefreshKey) { 
                            StoryAssetLoader.loadCoverImage(context, story) 
                        } 
                        if (coverBitmap != null) { 
                            Image( 
                                bitmap = coverBitmap, 
                                contentDescription = null, 
                                contentScale = ContentScale.Crop, 
                                modifier = Modifier.fillMaxSize() 
                            ) 
                        } else { 
                            StoryArtworkThumbnail( 
                                artworkType = story.artworkType, 
                                modifier = Modifier.fillMaxSize() 
                            ) 
                        } 
                         
                        Box( 
                            modifier = Modifier 
                                .fillMaxSize() 
                                .background( 
                                    Brush.verticalGradient( 
                                        listOf( 
                                            Color.Transparent, 
                                            Color(0x88000000), 
                                            Color(0xEE111214) 
                                        ) 
                                    ) 
                                ) 
                        ) 
                        
                        val isCustomStory = story.story != null || StorySessionManager.savedStoriesList.any { it.id == story.id } 
                        if (isCustomStory) { 
                            val coverFile = File(context.filesDir, "stories/images/${story.id}/cover.png") 
                            Row( 
                                modifier = Modifier 
                                    .align(Alignment.TopEnd) 
                                    .padding(12.dp), 
                                horizontalArrangement = Arrangement.spacedBy(8.dp), 
                                verticalAlignment = Alignment.CenterVertically 
                            ) { 
                                if (coverFile.exists()) { 
                                    Box( 
                                        modifier = Modifier 
                                            .clip(ShinobiShapes.Pill) 
                                            .background(Color(0xCC161922)) 
                                            .border(1.dp, ShinobiColors.MutedRose.copy(alpha = 0.5f), ShinobiShapes.Pill) 
                                            .clickable { 
                                                val dir = File(context.filesDir, "stories/images/${story.id}") 
                                                File(dir, "cover.png").delete() 
                                                File(dir, "1.png").delete() 
                                                StoryAssetLoader.invalidateStoryCache(story.id) 
                                                coverRefreshKey++ 
                                            } 
                                            .padding(horizontal = 10.dp, vertical = 6.dp) 
                                    ) { 
                                        Row( 
                                            verticalAlignment = Alignment.CenterVertically, 
                                            horizontalArrangement = Arrangement.spacedBy(4.dp) 
                                        ) { 
                                            Icon( 
                                                imageVector = Icons.Default.Close, 
                                                contentDescription = "Remove Cover", 
                                                tint = ShinobiColors.MutedRose, 
                                                modifier = Modifier.size(13.dp) 
                                            ) 
                                            Text( 
                                                text = "Remove", 
                                                color = ShinobiColors.MutedRose, 
                                                fontSize = 11.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                fontFamily = ShinobiNunito 
                                            ) 
                                        } 
                                    } 
                                } 
                                
                                Box( 
                                    modifier = Modifier 
                                        .clip(ShinobiShapes.Pill) 
                                        .background(Color(0xCC161922)) 
                                        .border(1.dp, Color(0xFF2C3240), ShinobiShapes.Pill) 
                                        .clickable { 
                                            activeWallhavenTarget = WallhavenPickerTarget( 
                                                storyId = story.id, 
                                                title = story.titleEnglish.ifBlank { story.titleJapanese }, 
                                                genre = story.category.ifBlank { story.artworkType } 
                                            ) 
                                        } 
                                        .padding(horizontal = 10.dp, vertical = 6.dp) 
                                ) { 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.spacedBy(4.dp) 
                                    ) { 
                                        Icon( 
                                            imageVector = Icons.Default.Image, 
                                            contentDescription = "Change Cover", 
                                            tint = ShinobiColors.SlateBlue, 
                                            modifier = Modifier.size(14.dp) 
                                        ) 
                                        Text( 
                                            text = "Artwork", 
                                            color = ShinobiColors.TextPrimary, 
                                            fontSize = 11.sp, 
                                            fontWeight = FontWeight.Bold, 
                                            fontFamily = ShinobiNunito 
                                        ) 
                                    } 
                                } 
                            } 
                        } 
                         
                        if (story.isLocked) { 
                            Box( 
                                modifier = Modifier 
                                    .size(64.dp) 
                                    .clip(CircleShape) 
                                    .background(Color.Black.copy(alpha = 0.5f)) 
                                    .align(Alignment.Center), 
                                contentAlignment = Alignment.Center 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.Lock, 
                                    contentDescription = null, 
                                    tint = Color.White, 
                                    modifier = Modifier.size(28.dp) 
                                ) 
                            } 
                        } 
                         
                        Column( 
                            modifier = Modifier 
                                .align(Alignment.BottomStart) 
                                .padding(20.dp) 
                        ) { 
                            val mainTitle = if (storyLanguage == "en") { 
                                story.titleEnglish.ifBlank { story.titleJapanese } 
                            } else { 
                                story.titleJapanese.ifBlank { story.titleEnglish } 
                            } 
                            val subTitle = if (storyLanguage == "en") { 
                                story.titleJapanese 
                            } else { 
                                story.titleEnglish 
                            } 
                            Text( 
                                text = mainTitle, 
                                fontSize = 22.sp, 
                                fontWeight = FontWeight.ExtraBold, 
                                fontFamily = ShinobiNunito, 
                                color = Color.White 
                            ) 
                            if (subTitle.isNotBlank() && subTitle != mainTitle) { 
                                Text( 
                                    text = subTitle, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    fontFamily = ShinobiNunito, 
                                    color = ShinobiColors.TextSecondary, 
                                    modifier = Modifier.padding(top = 2.dp) 
                                ) 
                            } 
                        } 
                    } 
                     
                    Spacer(modifier = Modifier.height(14.dp)) 
                     
                    Column( 
                        modifier = Modifier 
                            .weight(1f) 
                            .fillMaxWidth() 
                            .verticalScroll(detailsScrollState) 
                            .padding(bottom = 86.dp) 
                    ) { 
                        val (categoryTextColor, categoryBgColor) = when { 
                        story.category.equals("Daily Life", ignoreCase = true) -> Pair(Color(0xFF0F172A), Color(0xFFFACC15)) 
                        story.category.equals("Social", ignoreCase = true) -> Pair(Color(0xFF0F172A), Color(0xFF5CE1E6)) 
                        story.category.equals("School", ignoreCase = true) -> Pair(Color(0xFF0F172A), Color(0xFF70B6F6)) 
                        story.category.equals("Romance", ignoreCase = true) -> Pair(Color(0xFF0F172A), Color(0xFFF472B6)) 
                        story.category.equals("Traditional", ignoreCase = true) -> Pair(Color(0xFF0F172A), Color(0xFFF59E0B)) 
                        story.category.equals("Nature", ignoreCase = true) -> Pair(Color(0xFF0F172A), Color(0xFF10B981)) 
                        story.category.equals("Fantasy", ignoreCase = true) -> Pair(Color.White, Color(0xFFA855F7)) 
                        story.category.equals("Mystery", ignoreCase = true) -> Pair(Color.White, Color(0xFF8B5CF6)) 
                        story.category.equals("Body", ignoreCase = true) -> Pair(Color(0xFF0F172A), Color(0xFF38BDF8)) 
                        else -> Pair(Color(0xFF0F172A), Color(0xFF94A3B8)) 
                    } 
                     
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        Row( 
                            horizontalArrangement = Arrangement.spacedBy(8.dp), 
                            verticalAlignment = Alignment.CenterVertically 
                        ) { 
                            val localizedCategory = if (storyLanguage == "ja") { 
                                when (story.category.lowercase()) { 
                                    "daily life" -> "日常" 
                                    "social" -> "日常・交流" 
                                    "school" -> "学校" 
                                    "romance" -> "恋愛" 
                                    "traditional" -> "伝統" 
                                    "nature" -> "自然" 
                                    "fantasy" -> "ファンタジー" 
                                    "mystery" -> "ミステリー" 
                                    "body" -> "健康・身体" 
                                    else -> story.category 
                                } 
                            } else story.category 
                            
                            Box( 
                                modifier = Modifier 
                                    .clip(ShinobiShapes.Pill) 
                                    .background(categoryBgColor) 
                                    .padding(horizontal = 14.dp, vertical = 3.dp) 
                            ) { 
                                Text( 
                                    text = localizedCategory, 
                                    fontSize = 13.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    fontFamily = ShinobiNunito, 
                                    color = categoryTextColor 
                                ) 
                            } 
                             
                            if (story.isCompleted) { 
                                Box( 
                                    modifier = Modifier 
                                        .clip(ShinobiShapes.Pill) 
                                        .background(Color(0xFF22C55E)) 
                                        .padding(horizontal = 12.dp, vertical = 3.dp) 
                                ) { 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.spacedBy(4.dp) 
                                    ) { 
                                        Icon( 
                                            imageVector = Icons.Default.Check, 
                                            contentDescription = null, 
                                            tint = Color.White, 
                                            modifier = Modifier.size(14.dp) 
                                        ) 
                                        Text( 
                                            text = if (storyLanguage == "ja") "完了" else "Completed", 
                                            fontSize = 13.sp, 
                                            fontWeight = FontWeight.ExtraBold, 
                                            fontFamily = ShinobiNunito, 
                                            color = Color.White 
                                        ) 
                                    } 
                                } 
                            } else if (story.readingProgress > 0f) { 
                                Box( 
                                    modifier = Modifier 
                                        .clip(ShinobiShapes.Pill) 
                                        .background(Color(0xFF064E3B)) 
                                        .border(1.dp, Color(0xFF059669), ShinobiShapes.Pill) 
                                        .padding(horizontal = 10.dp, vertical = 3.dp) 
                                ) { 
                                    Text( 
                                        text = "${(story.readingProgress * 100).toInt()}%", 
                                        fontSize = 13.sp, 
                                        fontWeight = FontWeight.ExtraBold, 
                                        fontFamily = ShinobiNunito, 
                                        color = Color(0xFF6EE7B7) 
                                    ) 
                                } 
                            } else if (story.xp >= 200) { 
                                Box( 
                                    modifier = Modifier 
                                        .clip(ShinobiShapes.Pill) 
                                        .background(Color(0xFF042F2E)) 
                                        .border(1.dp, Color(0xFF0D9488), ShinobiShapes.Pill) 
                                        .padding(horizontal = 12.dp, vertical = 3.dp) 
                                ) { 
                                    Text( 
                                        text = if (storyLanguage == "ja") "XP 2倍" else "XP x2", 
                                        fontSize = 13.sp, 
                                        fontWeight = FontWeight.ExtraBold, 
                                        fontFamily = ShinobiNunito, 
                                        color = Color(0xFF2DD4BF) 
                                    ) 
                                } 
                            } 
                        } 
                         
                        Box( 
                            modifier = Modifier 
                                .size(34.dp) 
                                .clip(CircleShape) 
                                .background(Color(0xFF0A1F13)) 
                                .border(1.dp, Color(0xFF22C55E), CircleShape) 
                                .clickable { }, 
                            contentAlignment = Alignment.Center 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Download, 
                                contentDescription = "Download", 
                                tint = Color(0xFF22C55E), 
                                modifier = Modifier.size(16.dp) 
                            ) 
                        } 
                    } 
                     
                    if (story.isCompleted) { 
                        Spacer(modifier = Modifier.height(16.dp)) 
                        val reviewInteractionSource = remember { MutableInteractionSource() } 
                        val reviewPressed by reviewInteractionSource.collectIsPressedAsState() 
                        var isReviewClickAnimating by remember { mutableStateOf(false) } 
                        val isReviewEffectivelyPressed = reviewPressed || isReviewClickAnimating 
                        val reviewTopPush by animateDpAsState( 
                            targetValue = if (isReviewEffectivelyPressed) 3.5.dp else 0.dp, 
                            animationSpec = tween(60), 
                            label = "reviewPush" 
                        ) 
                        val reviewBottomLip by animateDpAsState( 
                            targetValue = if (isReviewEffectivelyPressed) 0.dp else 3.5.dp, 
                            animationSpec = tween(60), 
                            label = "reviewLip" 
                        ) 
                        Box( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .padding(top = reviewTopPush) 
                                .clip(RoundedCornerShape(18.dp)) 
                                .background(Color(0xFF3F3F46)) 
                                .clickable( 
                                    interactionSource = reviewInteractionSource, 
                                    indication = null 
                                ) { 
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
                                    coroutineScope.launch { 
                                        isReviewClickAnimating = true 
                                        delay(75L) 
                                        isReviewClickAnimating = false 
                                        delay(30L) 
                                    } 
                                } 
                        ) { 
                            Box( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .padding(bottom = reviewBottomLip) 
                                    .clip(RoundedCornerShape(18.dp)) 
                                    .background(Color(0xFF27272A)) 
                                    .border(2.dp, Color(0xFF3F3F46), RoundedCornerShape(18.dp)) 
                                    .padding(14.dp) 
                            ) { 
                                Row( 
                                    modifier = Modifier.fillMaxWidth(), 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.spacedBy(14.dp) 
                                ) { 
                                    Box( 
                                        modifier = Modifier.size(44.dp) 
                                    ) { 
                                        Box( 
                                            modifier = Modifier 
                                                .size(40.dp) 
                                                .align(Alignment.BottomStart) 
                                                .clip(RoundedCornerShape(12.dp)) 
                                                .background( 
                                                    Brush.verticalGradient( 
                                                        listOf(Color(0xFFFB923C), Color(0xFFEA580C)) 
                                                    ) 
                                                ), 
                                            contentAlignment = Alignment.Center 
                                        ) { 
                                            Icon( 
                                                imageVector = Icons.Default.Style, 
                                                contentDescription = null, 
                                                tint = Color.White, 
                                                modifier = Modifier.size(22.dp) 
                                            ) 
                                        } 
                                        Box( 
                                            modifier = Modifier 
                                                .align(Alignment.TopEnd) 
                                                .size(18.dp) 
                                                .clip(CircleShape) 
                                                .background(Color(0xFFEF4444)), 
                                            contentAlignment = Alignment.Center 
                                        ) { 
                                            Text( 
                                                text = "12", 
                                                fontSize = 10.sp, 
                                                fontWeight = FontWeight.ExtraBold, 
                                                fontFamily = ShinobiNunito, 
                                                color = Color.White 
                                            ) 
                                        } 
                                    } 
                                    Column(modifier = Modifier.weight(1f)) { 
                                        Text( 
                                            text = if (storyLanguage == "ja") "復習カード 12枚" else "12 cards to review", 
                                            fontSize = 15.sp, 
                                            fontWeight = FontWeight.ExtraBold, 
                                            fontFamily = ShinobiNunito, 
                                            color = Color.White 
                                        ) 
                                        Row( 
                                            verticalAlignment = Alignment.CenterVertically, 
                                            horizontalArrangement = Arrangement.spacedBy(4.dp), 
                                            modifier = Modifier.padding(top = 2.dp) 
                                        ) { 
                                            Icon( 
                                                imageVector = Icons.Default.Bolt, 
                                                contentDescription = null, 
                                                tint = Color(0xFFFB923C), 
                                                modifier = Modifier.size(13.dp) 
                                            ) 
                                            Text( 
                                                text = if (storyLanguage == "ja") "このストーリーの新しい単語を学ぶ！" else "Learn new words from this story!", 
                                                fontSize = 12.sp, 
                                                fontFamily = ShinobiNunito, 
                                                color = Color(0xFFFB923C) 
                                            ) 
                                        } 
                                    } 
                                    Box( 
                                        modifier = Modifier 
                                            .size(44.dp) 
                                            .clip(CircleShape) 
                                            .background(Color(0xFFEA580C)), 
                                        contentAlignment = Alignment.Center 
                                    ) { 
                                        Icon( 
                                            imageVector = Icons.Default.PlayArrow, 
                                            contentDescription = null, 
                                            tint = Color.White, 
                                            modifier = Modifier.size(24.dp) 
                                        ) 
                                    } 
                                } 
                            } 
                        } 
                    } 
                     
                    Spacer(modifier = Modifier.height(16.dp)) 
                     
                    Text( 
                        text = story.summary.ifBlank { "An engaging story crafted to reinforce Japanese vocabulary in natural context." }, 
                        fontSize = 15.sp, 
                        color = Color(0xFFCBD5E1), 
                        lineHeight = 24.sp 
                    ) 
                     
                    if (story.author.isNotBlank()) { 
                        Spacer(modifier = Modifier.height(8.dp)) 
                        Text( 
                            text = if (storyLanguage == "ja") "作者: ${story.author}" else "Author: ${story.author}", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFF94A3B8) 
                        ) 
                    } 
                     
                    Spacer(modifier = Modifier.height(18.dp)) 
                     
                    Row( 
                        horizontalArrangement = Arrangement.spacedBy(10.dp), 
                        verticalAlignment = Alignment.CenterVertically 
                    ) { 
                        Box( 
                            modifier = Modifier 
                                .clip(RoundedCornerShape(12.dp)) 
                                .background(Color(0xFF0F1E38)) 
                                .border(1.dp, Color(0xFF1E40AF), RoundedCornerShape(12.dp)) 
                                .padding(horizontal = 14.dp, vertical = 8.dp) 
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(6.dp) 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.HourglassTop, 
                                    contentDescription = null, 
                                    tint = Color(0xFF93C5FD), 
                                    modifier = Modifier.size(16.dp) 
                                ) 
                                Text( 
                                    text = if (storyLanguage == "ja") "約${story.estimatedMinutes}分" else "~${story.estimatedMinutes} mins", 
                                    fontSize = 13.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    fontFamily = ShinobiNunito, 
                                    color = Color(0xFF93C5FD) 
                                ) 
                            } 
                        } 
                        Box( 
                            modifier = Modifier 
                                .clip(RoundedCornerShape(12.dp)) 
                                .background(Color(0xFF042F2E)) 
                                .border(1.dp, Color(0xFF0D9488), RoundedCornerShape(12.dp)) 
                                .padding(horizontal = 14.dp, vertical = 8.dp) 
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(6.dp) 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.Star, 
                                    contentDescription = null, 
                                    tint = Color(0xFF2DD4BF), 
                                    modifier = Modifier.size(16.dp) 
                                ) 
                                Text( 
                                    text = "${story.xp} XP", 
                                    fontSize = 13.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    fontFamily = ShinobiNunito, 
                                    color = Color(0xFF2DD4BF) 
                                ) 
                            } 
                        } 
                    } 
                     
                    Spacer(modifier = Modifier.height(140.dp)) 
                    } 
                } 
                 
                Box( 
                    modifier = Modifier 
                        .align(Alignment.BottomCenter) 
                        .fillMaxWidth() 
                        .background(Color(0xFF111214)) 
                        .border(1.dp, Color(0xFF27272A)) 
                        .navigationBarsPadding() 
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp) 
                ) { 
                    val buttonFaceColor = if (story.isLocked) Color(0xFF1E293B) else ShinobiColors.ElectricBlue 
                    val buttonLipColor = if (story.isLocked) Color(0xFF0F172A) else ShinobiColors.ElectricBlueLip 
                    val buttonContentColor = if (story.isLocked) Color(0xFF64748B) else Color.White 
                     
                    ShinobiTactileButton( 
                        onClick = { 
                            val forged = StorySessionManager.buildCuratedForgedStory(story.id) ?: story.story 
                            if (forged != null) { 
                                audioPlayer.stop() 
                                val savedPage = StorySessionManager.getSavedStoryPage(forged.id) 
                                currentStoryPage = savedPage 
                                isQuizMode = false 
                                currentQuizIndex = 0 
                                quizOutcomeSubmitted = false 
                                StorySessionManager.userAnswers.clear() 
                                StorySessionManager.currentStory = forged 
                                StorySessionManager.selectedStoryForDetails = null 
                            } 
                        }, 
                        modifier = Modifier.fillMaxWidth(), 
                        faceColor = buttonFaceColor, 
                        lipColor = buttonLipColor, 
                        contentColor = buttonContentColor, 
                        enabled = !story.isLocked 
                    ) { 
                        val actionButtonText = when { 
                            story.isLocked -> if (storyLanguage == "ja") "ロック中" else "STORY LOCKED" 
                            story.isCompleted -> if (storyLanguage == "ja") "もう一度読む" else "READ AGAIN" 
                            story.readingProgress > 0f -> if (storyLanguage == "ja") "続きを読む (${(story.readingProgress * 5).toInt()}/5)" else "CONTINUE READING (${(story.readingProgress * 5).toInt()}/5)" 
                            else -> if (storyLanguage == "ja") "読み始める" else "START READING" 
                        } 
                        Text( 
                            text = actionButtonText, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = buttonContentColor 
                        ) 
                    } 
                } 
            } 
        } 
    } 
    ForgeScreenState.FORGE_GAME -> { 
        val activeTier = StorySessionManager.activeMiniGameDifficulty 
        val (initialHp, secondsPerQ) = when (activeTier.lowercase()) { 
            "easy" -> 10 to 5f 
            "hardcore" -> 4 to 3f 
            else -> 7 to 4f 
        } 
        val strikeTarget = when (activeTier.lowercase()) { 
            "easy" -> 10 
            "hardcore" -> 20 
            else -> 15 
        } 
        val strikeCards = remember(activeTier) { 
            val selectedDeckIds = prefs.getSelectedDeckIdsAsLongs() 
            val fromDeck = ankiHelper.getDistinctDueCards(selectedDeckIds, strikeTarget * 2) 
            if (fromDeck.isNotEmpty()) fromDeck else StorySessionManager.lastForgedCards 
        } 
        ShinobiSpeedStrikeGame( 
            cards = strikeCards, 
            isGenerating = StorySessionManager.isGenerating, 
            generationStage = StorySessionManager.generationStage, 
            errorMessage = StorySessionManager.errorMessage, 
            forgedStory = StorySessionManager.currentStory, 
            difficultyTier = activeTier, 
            initialHp = initialHp, 
            secondsPerQuestion = secondsPerQ, 
            onReadStory = { story -> 
                StorySessionManager.currentStory = story 
                currentStoryPage = 0 
                storyPhase = StoryPhase.READER 
            }, 
            onExitGame = { 
                activeSubScreen = ForgeScreenState.FORGE_STUDIO 
            } 
        ) 
    } 
    ForgeScreenState.FORGE_STUDIO -> { 
        val forgeStudioScroll = rememberScrollState() 
        val slot1 = StorySessionManager.slot1 
        val slot2 = StorySessionManager.slot2 
        
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .statusBarsPadding() 
                .padding(top = 58.dp) 
        ) { 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp, vertical = 10.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(14.dp) 
                ) { 
                    ShinobiTactileIconButton( 
                        onClick = { activeSubScreen = ForgeScreenState.HOME }, 
                        size = 44.dp, 
                        faceColor = Color(0xFF27272A), 
                        lipColor = Color(0xFF3F3F46), 
                        iconColor = Color(0xFFE4E4E7), 
                        icon = Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Back" 
                    ) 
                    Column { 
                        Text( 
                            text = if (storyLanguage == "ja") "ストーリー鍛造スタジオ" else "Story Forge Studio", 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color.White 
                        ) 
                        Surface( 
                            shape = ShinobiShapes.Pill, 
                            color = Color.Black.copy(alpha = 0.50f), 
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)), 
                            modifier = Modifier.padding(top = 3.dp) 
                        ) { 
                            Text( 
                                text = if (storyLanguage == "ja") "デュアルチェンバー AIストーリー生成" else "Dual-Chamber AI Story Synthesis", 
                                fontSize = 11.5.sp, 
                                fontWeight = FontWeight.Bold, 
                                fontFamily = ShinobiNunito, 
                                color = Color(0xFFE2E8F0), 
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp) 
                            ) 
                        } 
                    } 
                } 
                
                val activeCount = listOf(slot1.state, slot2.state).count { it == ForgeSlotState.FORGING } 
                val queuedCount = listOf(slot1.state, slot2.state).count { it == ForgeSlotState.QUEUED } 
                if (activeCount > 0 || queuedCount > 0) { 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(if (activeCount > 0) Color(0xFF065F46) else Color(0xFF78350F)) 
                            .padding(horizontal = 10.dp, vertical = 4.dp) 
                    ) { 
                        Text( 
                            text = if (activeCount > 0) "1 ACTIVE" else "1 QUEUED", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = if (activeCount > 0) Color(0xFF6EE7B7) else Color(0xFFFDE68A) 
                        ) 
                    } 
                } 
            } 
            
            Column( 
                modifier = Modifier 
                    .weight(1f) 
                    .fillMaxWidth() 
                    .verticalScroll(forgeStudioScroll) 
                    .padding(horizontal = 16.dp, vertical = 8.dp) 
                    .padding(bottom = 112.dp), 
                verticalArrangement = Arrangement.spacedBy(16.dp) 
            ) { 
                if (prefs.aiApiKey.isBlank()) { 
                    Surface( 
                        onClick = onOpenAiConfig, 
                        shape = ShinobiShapes.SquircleMedium, 
                        color = Color(0xFF7C2D12).copy(alpha = 0.3f), 
                        border = BorderStroke(1.dp, Color(0xFFEA580C)), 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        Row( 
                            modifier = Modifier.padding(14.dp), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(10.dp) 
                        ) { 
                            Icon( 
                                Icons.Default.Key, 
                                contentDescription = null, 
                                tint = Color(0xFFFB923C), 
                                modifier = Modifier.size(22.dp) 
                            ) 
                            Column(modifier = Modifier.weight(1f)) { 
                                Text( 
                                    text = if (storyLanguage == "ja") "AI APIキーが必要です" else "AI API Key Required", 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = Color(0xFFFFEDD5) 
                                ) 
                                Text( 
                                    text = if (storyLanguage == "ja") "タップしてAPIキーを設定" else "Tap to configure your API key", 
                                    fontSize = 12.sp, 
                                    color = Color(0xFFFDBA74) 
                                ) 
                            } 
                        } 
                    } 
                } 
                
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(RoundedCornerShape(18.dp)) 
                        .background( 
                            Brush.linearGradient( 
                                listOf(Color(0xFF1E1B4B), Color(0xFF2E1065)) 
                            ) 
                        ) 
                        .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.5f), RoundedCornerShape(18.dp)) 
                        .padding(16.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(12.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.AutoAwesome, 
                            contentDescription = null, 
                            tint = Color(0xFFC4B5FD), 
                            modifier = Modifier.size(26.dp) 
                        ) 
                        Text( 
                            text = if (storyLanguage == "ja") 
                                "スロットごとにストーリーを設定して生成できます。生成中はスピードストライクミニゲームで単語を復習しましょう！" 
                            else 
                                "Configure two chambers to forge stories in parallel or queue. Play Speed Strike mini-game while AI crafts your story!", 
                            fontSize = 13.sp, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFFE9D5FF), 
                            lineHeight = 18.sp 
                        ) 
                    } 
                } 
                
                val onQuickForgeForSlot: (Int) -> Unit = { slotId -> 
                    val quickLevel = when (selectedLevelId.lowercase()) { 
                        "starter" -> "N5" 
                        "learner" -> "N4" 
                        "explorer" -> "N3" 
                        "challenger" -> "N2" 
                        "master" -> "N1" 
                        else -> "N5" 
                    } 
                    val randomTheme = listOf("Daily Life", "Traditional", "Nature", "Romance", "School", "Fantasy", "Mystery").random() 
                    ShinobiSoundEffects.playSuccess() 
                    currentStoryPage = 0 
                    isQuizMode = false 
                    currentQuizIndex = 0 
                    StorySessionManager.startOrQueueSlot( 
                        slotId = slotId, 
                        cardCount = 3, 
                        genre = randomTheme, 
                        level = quickLevel, 
                        length = "Medium", 
                        generateImage = true, 
                        ankiHelper = ankiHelper, 
                        prefs = prefs, 
                        context = context 
                    ) 
                } 
                
                ForgeSlotCard( 
                    slot = slot1, 
                    prefs = prefs, 
                    context = context, 
                    storyLanguage = storyLanguage, 
                    onConfigure = { showCreationSheetForSlot = 1 }, 
                    onQuickForge = { onQuickForgeForSlot(1) }, 
                    onCancelQueue = { StorySessionManager.cancelQueue(1) }, 
                    onClearSlot = { StorySessionManager.clearSlot(1) }, 
                    onCancelForging = { StorySessionManager.cancelSlotForging(1, ankiHelper, prefs, context) }, 
                    onReadStory = { story -> 
                        StorySessionManager.currentStory = story 
                        currentStoryPage = 0 
                        storyPhase = StoryPhase.READER 
                    }, 
                    onLaunchGame = { tier -> 
                        StorySessionManager.activeMiniGameDifficulty = tier 
                        activeSubScreen = ForgeScreenState.FORGE_GAME 
                    } 
                ) 
                
                ForgeSlotCard( 
                    slot = slot2, 
                    prefs = prefs, 
                    context = context, 
                    storyLanguage = storyLanguage, 
                    onConfigure = { showCreationSheetForSlot = 2 }, 
                    onQuickForge = { onQuickForgeForSlot(2) }, 
                    onCancelQueue = { StorySessionManager.cancelQueue(2) }, 
                    onClearSlot = { StorySessionManager.clearSlot(2) }, 
                    onCancelForging = { StorySessionManager.cancelSlotForging(2, ankiHelper, prefs, context) }, 
                    onReadStory = { story -> 
                        StorySessionManager.currentStory = story 
                        currentStoryPage = 0 
                        storyPhase = StoryPhase.READER 
                    }, 
                    onLaunchGame = { tier -> 
                        StorySessionManager.activeMiniGameDifficulty = tier 
                        activeSubScreen = ForgeScreenState.FORGE_GAME 
                    } 
                ) 
            } 
        } 
    } 
    ForgeScreenState.LEVEL_JOURNEY -> { 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .statusBarsPadding() 
                .padding(top = 58.dp) 
        ) { 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp, vertical = 10.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(14.dp) 
                ) { 
                    ShinobiTactileIconButton( 
                        onClick = { activeSubScreen = ForgeScreenState.HOME }, 
                        size = 44.dp, 
                        faceColor = Color(0xFF27272A), 
                        lipColor = Color(0xFF3F3F46), 
                        iconColor = Color(0xFFE4E4E7), 
                        icon = Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Back" 
                    ) 
                    Column { 
                        Text( 
                            text = if (storyLanguage == "ja") "日本語レベル進行" else "Level Journey", 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color.White 
                        ) 
                        Surface( 
                            shape = ShinobiShapes.Pill, 
                            color = Color.Black.copy(alpha = 0.50f), 
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)), 
                            modifier = Modifier.padding(top = 3.dp) 
                        ) { 
                            Text( 
                                text = if (storyLanguage == "ja") "$selectedLevelName コース" else "$selectedLevelName Track", 
                                fontSize = 11.5.sp, 
                                fontWeight = FontWeight.Bold, 
                                fontFamily = ShinobiNunito, 
                                color = Color(0xFFE2E8F0), 
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp) 
                            ) 
                        } 
                    } 
                } 
                
                Box( 
                    modifier = Modifier 
                        .clip(ShinobiShapes.Pill) 
                        .background(ShinobiColors.SurfaceCard2) 
                        .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.Pill) 
                        .clickable { showLevelSelectorSheet = true } 
                        .padding(horizontal = 12.dp, vertical = 6.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(4.dp) 
                    ) { 
                        Text( 
                            text = selectedLevelName, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = ShinobiNunito, 
                            color = Color.White 
                        ) 
                        Icon( 
                            Icons.Default.ChevronRight, 
                            contentDescription = null, 
                            tint = ShinobiColors.TextSecondary, 
                            modifier = Modifier.size(16.dp) 
                        ) 
                    } 
                } 
            } 
            
            Column( 
                modifier = Modifier 
                    .weight(1f) 
                    .fillMaxWidth() 
                    .verticalScroll(listScrollState) 
                    .padding(vertical = 8.dp) 
                    .padding(bottom = 112.dp) 
            ) { 
                Row( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(horizontal = 16.dp), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(12.dp) 
                ) { 
                    Text( 
                        text = if (storyLanguage == "ja") "レベル 1" else "Level 1", 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White 
                    ) 
                    LevelProgressDots(completedCount = level1Count, total = 5) 
                } 
                Spacer(modifier = Modifier.height(12.dp)) 
                LazyRow( 
                    state = level1LazyState, 
                    horizontalArrangement = Arrangement.spacedBy(14.dp), 
                    contentPadding = PaddingValues(horizontal = 16.dp) 
                ) { 
                    items(level1Stories) { story -> 
                        ShinobiStoryCard( 
                            story = story, 
                            onClick = { StorySessionManager.selectedStoryForDetails = story }, 
                            titleLanguage = storyLanguage 
                        ) 
                    } 
                } 
                
                Spacer(modifier = Modifier.height(24.dp)) 
                Row( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(horizontal = 16.dp), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(12.dp) 
                ) { 
                    Text( 
                        text = if (storyLanguage == "ja") "レベル 2" else "Level 2", 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White 
                    ) 
                    if (isLevel2Unlocked) { 
                        LevelProgressDots(completedCount = level2Count, total = 5) 
                    } else { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(6.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Lock, 
                                contentDescription = null, 
                                tint = Color(0xFF94A3B8), 
                                modifier = Modifier.size(16.dp) 
                            ) 
                            Text( 
                                text = if (storyLanguage == "ja") "前のレベルを完了して解放" else "Complete previous level to unlock", 
                                fontSize = 13.sp, 
                                color = Color(0xFF94A3B8) 
                            ) 
                        } 
                    } 
                } 
                Spacer(modifier = Modifier.height(12.dp)) 
                LazyRow( 
                    state = level2LazyState, 
                    horizontalArrangement = Arrangement.spacedBy(14.dp), 
                    contentPadding = PaddingValues(horizontal = 16.dp) 
                ) { 
                    items(level2Stories) { story -> 
                        ShinobiStoryCard( 
                            story = story, 
                            onClick = { StorySessionManager.selectedStoryForDetails = story }, 
                            titleLanguage = storyLanguage 
                        ) 
                    } 
                } 
                
                Spacer(modifier = Modifier.height(24.dp)) 
                Row( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(horizontal = 16.dp), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(12.dp) 
                ) { 
                    Text( 
                        text = if (storyLanguage == "ja") "レベル 3" else "Level 3", 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White 
                    ) 
                    if (isLevel3Unlocked) { 
                        LevelProgressDots(completedCount = level3Count, total = 5) 
                    } else { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(6.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Lock, 
                                contentDescription = null, 
                                tint = Color(0xFF94A3B8), 
                                modifier = Modifier.size(16.dp) 
                            ) 
                            Text( 
                                text = if (storyLanguage == "ja") "前のレベルを完了して解放" else "Complete previous level to unlock", 
                                fontSize = 13.sp, 
                                color = Color(0xFF94A3B8) 
                            ) 
                        } 
                    } 
                } 
                Spacer(modifier = Modifier.height(12.dp)) 
                LazyRow( 
                    state = level3LazyState, 
                    horizontalArrangement = Arrangement.spacedBy(14.dp), 
                    contentPadding = PaddingValues(horizontal = 16.dp) 
                ) { 
                    items(level3Stories) { story -> 
                        ShinobiStoryCard( 
                            story = story, 
                            onClick = { StorySessionManager.selectedStoryForDetails = story }, 
                            titleLanguage = storyLanguage 
                        ) 
                    } 
                } 
                
                Spacer(modifier = Modifier.height(24.dp)) 
                Row( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(horizontal = 16.dp), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(12.dp) 
                ) { 
                    Text( 
                        text = if (storyLanguage == "ja") "レベル 4" else "Level 4", 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White 
                    ) 
                    if (isLevel4Unlocked) { 
                        LevelProgressDots(completedCount = level4Count, total = 5) 
                    } else { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(6.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Lock, 
                                contentDescription = null, 
                                tint = Color(0xFF94A3B8), 
                                modifier = Modifier.size(16.dp) 
                            ) 
                            Text( 
                                text = if (storyLanguage == "ja") "前のレベルを完了して解放" else "Complete previous level to unlock", 
                                fontSize = 13.sp, 
                                color = Color(0xFF94A3B8) 
                            ) 
                        } 
                    } 
                } 
                Spacer(modifier = Modifier.height(12.dp)) 
                LazyRow( 
                    state = level4LazyState, 
                    horizontalArrangement = Arrangement.spacedBy(14.dp), 
                    contentPadding = PaddingValues(horizontal = 16.dp) 
                ) { 
                    items(level4Stories) { story -> 
                        ShinobiStoryCard( 
                            story = story, 
                            onClick = { StorySessionManager.selectedStoryForDetails = story }, 
                            titleLanguage = storyLanguage 
                        ) 
                    } 
                } 
            } 
        } 
    } 
    ForgeScreenState.HOME -> { 
        val homeScrollState = rememberScrollState() 
        
        Box(modifier = Modifier.fillMaxSize()) { 
            Column( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .statusBarsPadding() 
                    .padding(top = 58.dp) 
                    .verticalScroll(homeScrollState) 
                    .padding(vertical = 8.dp) 
                    .padding(bottom = 112.dp) 
            ) { 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp) 
                    .padding(bottom = 14.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Text( 
                    text = if (storyLanguage == "ja") "ストーリー" else "Stories", 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontFamily = ShinobiNunito, 
                    color = Color.White 
                ) 
                
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF18181B)) 
                            .border(1.dp, Color(0xFF38383E), ShinobiShapes.Pill) 
                            .padding(3.dp) 
                    ) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(2.dp) 
                        ) { 
                            Box( 
                                modifier = Modifier 
                                    .clip(ShinobiShapes.Pill) 
                                    .then( 
                                        if (storyLanguage == "ja") { 
                                            Modifier 
                                                .background(Color(0xFF1D4ED8)) 
                                                .padding(bottom = 2.dp) 
                                                .clip(ShinobiShapes.Pill) 
                                                .background(ShinobiColors.ElectricBlue) 
                                        } else { 
                                            Modifier.background(Color.Transparent) 
                                        } 
                                    ) 
                                    .clickable { 
                                        storyLanguage = "ja" 
                                        prefs.storyLanguage = "ja" 
                                    } 
                                    .padding(horizontal = 12.dp, vertical = 4.dp), 
                                contentAlignment = Alignment.Center 
                            ) { 
                                Text( 
                                    text = "JP", 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    fontFamily = ShinobiNunito, 
                                    color = if (storyLanguage == "ja") Color.White else Color(0xFF9CA3AF) 
                                ) 
                            } 
                            Box( 
                                modifier = Modifier 
                                    .clip(ShinobiShapes.Pill) 
                                    .then( 
                                        if (storyLanguage == "en") { 
                                            Modifier 
                                                .background(Color(0xFF1D4ED8)) 
                                                .padding(bottom = 2.dp) 
                                                .clip(ShinobiShapes.Pill) 
                                                .background(ShinobiColors.ElectricBlue) 
                                        } else { 
                                            Modifier.background(Color.Transparent) 
                                        } 
                                    ) 
                                    .clickable { 
                                        storyLanguage = "en" 
                                        prefs.storyLanguage = "en" 
                                    } 
                                    .padding(horizontal = 12.dp, vertical = 4.dp), 
                                contentAlignment = Alignment.Center 
                            ) { 
                                Text( 
                                    text = "EN", 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    fontFamily = ShinobiNunito, 
                                    color = if (storyLanguage == "en") Color.White else Color(0xFF9CA3AF) 
                                ) 
                            } 
                        } 
                    } 
                    
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(CircleShape) 
                            .background(Color(0xFF1E1E24)) 
                            .border(1.dp, Color(0xFF38383E), CircleShape) 
                            .clickable { 
                                if (onNavigateToForged != null) { 
                                    onNavigateToForged() 
                                } else { 
                                    showSavedLibrarySheet = true 
                                } 
                            }, 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Bookmark, 
                            contentDescription = "Saved Library", 
                            tint = if (StorySessionManager.savedStoriesList.isNotEmpty()) Color(0xFFF59E0B) else ShinobiColors.TextSecondary, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                } 
            } 
            
            val forgeBannerTransition = rememberInfiniteTransition(label = "forgeBannerShine") 
            val forgeBannerShineOffset by forgeBannerTransition.animateFloat( 
                initialValue = -1.5f, 
                targetValue = 2.5f, 
                animationSpec = infiniteRepeatable( 
                    animation = keyframes { 
                        durationMillis = 6500 
                        -1.5f at 0 with FastOutSlowInEasing 
                        2.5f at 3800 
                        2.5f at 6500 
                    }, 
                    repeatMode = RepeatMode.Restart 
                ), 
                label = "forgeBannerOffset" 
            ) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp) 
                    .clip(RoundedCornerShape(20.dp)) 
                    .background( 
                        Brush.linearGradient( 
                            listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF1E1B4B)) 
                        ) 
                    ) 
                    .border(1.5.dp, Color(0xFF8B5CF6).copy(alpha = 0.6f), RoundedCornerShape(20.dp)) 
                    .clickable { activeSubScreen = ForgeScreenState.FORGE_STUDIO } 
                    .drawWithContent { 
                        drawContent() 
                        val canvasSize = this.size 
                        val shineWidth = canvasSize.width * 1.6f 
                        val currentX = canvasSize.width * forgeBannerShineOffset 
                        val shineBrush = Brush.horizontalGradient( 
                            colors = listOf( 
                                Color.White.copy(alpha = 0f), 
                                Color.White.copy(alpha = 0.04f), 
                                Color.White.copy(alpha = 0.28f), 
                                Color.White.copy(alpha = 0.04f), 
                                Color.White.copy(alpha = 0f) 
                            ), 
                            startX = currentX, 
                            endX = currentX + shineWidth 
                        ) 
                        rotate( 
                            degrees = 12f, 
                            pivot = Offset(currentX + shineWidth / 2f, canvasSize.height / 2f) 
                        ) { 
                            drawRect( 
                                brush = shineBrush, 
                                topLeft = Offset(currentX - 50f, -canvasSize.height * 0.6f), 
                                size = Size(shineWidth + 100f, canvasSize.height * 2.2f) 
                            ) 
                        } 
                    } 
                    .padding(16.dp) 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(14.dp) 
                    ) { 
                        Box( 
                            modifier = Modifier 
                                .size(46.dp) 
                                .clip(CircleShape) 
                                .background(Color(0xFF4C1D95)) 
                                .border(1.dp, Color(0xFFA78BFA), CircleShape), 
                            contentAlignment = Alignment.Center 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.AutoAwesome, 
                                contentDescription = null, 
                                tint = Color(0xFFDDD6FE), 
                                modifier = Modifier.size(24.dp) 
                            ) 
                        } 
                        Column { 
                            Text( 
                                text = if (storyLanguage == "ja") "Ankiカードからストーリーを鍛造" else "Forge Story from Anki Cards", 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.ExtraBold, 
                                fontFamily = ShinobiNunito, 
                                color = Color.White 
                            ) 
                            Text( 
                                text = if (storyLanguage == "ja") "AI挿絵付き生成 ＆ スピードストライク" else "AI illustrated story & Speed Strike", 
                                fontSize = 12.sp, 
                                fontFamily = ShinobiNunito, 
                                color = Color(0xFFC4B5FD) 
                            ) 
                        } 
                    } 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF8B5CF6)) 
                            .padding(horizontal = 12.dp, vertical = 6.dp) 
                    ) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(4.dp) 
                        ) { 
                            Text( 
                                text = if (storyLanguage == "ja") "鍛造" else "Forge", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.ExtraBold, 
                                fontFamily = ShinobiNunito, 
                                color = Color.White 
                            ) 
                            Icon( 
                                imageVector = Icons.Default.Bolt, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(16.dp) 
                            ) 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(20.dp)) 
            
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp, vertical = 4.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Column { 
                    Text( 
                        text = if (storyLanguage == "ja") "日本語レベル進行" else "Level Journey", 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontFamily = ShinobiNunito, 
                        color = Color.White 
                    ) 
                    Text( 
                        text = if (storyLanguage == "ja") "$selectedLevelName コース • 4つのレベル" else "$selectedLevelName Track • 4 Graded Levels", 
                        fontSize = 12.sp, 
                        fontFamily = ShinobiNunito, 
                        color = Color(0xFF94A3B8) 
                    ) 
                } 
                Box( 
                    modifier = Modifier 
                        .clip(ShinobiShapes.Pill) 
                        .background(ShinobiColors.SurfaceCard2) 
                        .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.Pill) 
                        .clickable { showLevelSelectorSheet = true } 
                        .padding(horizontal = 12.dp, vertical = 6.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(4.dp) 
                    ) { 
                        Text( 
                            text = selectedLevelName, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = ShinobiNunito, 
                            color = Color.White 
                        ) 
                        Icon( 
                            Icons.Default.ChevronRight, 
                            contentDescription = null, 
                            tint = ShinobiColors.TextSecondary, 
                            modifier = Modifier.size(16.dp) 
                        ) 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(12.dp) 
            ) { 
                Text( 
                    text = if (storyLanguage == "ja") "レベル 1" else "Level 1", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White 
                ) 
                LevelProgressDots(completedCount = level1Count, total = 5) 
            } 
            Spacer(modifier = Modifier.height(12.dp)) 
            LazyRow( 
                state = level1LazyState, 
                horizontalArrangement = Arrangement.spacedBy(14.dp), 
                contentPadding = PaddingValues(horizontal = 16.dp) 
            ) { 
                items(level1Stories) { story -> 
                    ShinobiStoryCard( 
                        story = story, 
                        onClick = { StorySessionManager.selectedStoryForDetails = story }, 
                        titleLanguage = storyLanguage 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(24.dp)) 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(12.dp) 
            ) { 
                Text( 
                    text = if (storyLanguage == "ja") "レベル 2" else "Level 2", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White 
                ) 
                if (isLevel2Unlocked) { 
                    LevelProgressDots(completedCount = level2Count, total = 5) 
                } else { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(6.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Lock, 
                            contentDescription = null, 
                            tint = Color(0xFF94A3B8), 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Text( 
                            text = if (storyLanguage == "ja") "前のレベルを完了して解放" else "Complete previous level to unlock", 
                            fontSize = 13.sp, 
                            color = Color(0xFF94A3B8) 
                        ) 
                    } 
                } 
            } 
            Spacer(modifier = Modifier.height(12.dp)) 
            LazyRow( 
                state = level2LazyState, 
                horizontalArrangement = Arrangement.spacedBy(14.dp), 
                contentPadding = PaddingValues(horizontal = 16.dp) 
            ) { 
                items(level2Stories) { story -> 
                    ShinobiStoryCard( 
                        story = story, 
                        onClick = { StorySessionManager.selectedStoryForDetails = story }, 
                        titleLanguage = storyLanguage 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(24.dp)) 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(12.dp) 
            ) { 
                Text( 
                    text = if (storyLanguage == "ja") "レベル 3" else "Level 3", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White 
                ) 
                if (isLevel3Unlocked) { 
                    LevelProgressDots(completedCount = level3Count, total = 5) 
                } else { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(6.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Lock, 
                            contentDescription = null, 
                            tint = Color(0xFF94A3B8), 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Text( 
                            text = if (storyLanguage == "ja") "前のレベルを完了して解放" else "Complete previous level to unlock", 
                            fontSize = 13.sp, 
                            color = Color(0xFF94A3B8) 
                        ) 
                    } 
                } 
            } 
            Spacer(modifier = Modifier.height(12.dp)) 
            LazyRow( 
                state = level3LazyState, 
                horizontalArrangement = Arrangement.spacedBy(14.dp), 
                contentPadding = PaddingValues(horizontal = 16.dp) 
            ) { 
                items(level3Stories) { story -> 
                    ShinobiStoryCard( 
                        story = story, 
                        onClick = { StorySessionManager.selectedStoryForDetails = story }, 
                        titleLanguage = storyLanguage 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(24.dp)) 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(12.dp) 
            ) { 
                Text( 
                    text = if (storyLanguage == "ja") "レベル 4" else "Level 4", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White 
                ) 
                if (isLevel4Unlocked) { 
                    LevelProgressDots(completedCount = level4Count, total = 5) 
                } else { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(6.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Lock, 
                            contentDescription = null, 
                            tint = Color(0xFF94A3B8), 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Text( 
                            text = if (storyLanguage == "ja") "前のレベルを完了して解放" else "Complete previous level to unlock", 
                            fontSize = 13.sp, 
                            color = Color(0xFF94A3B8) 
                        ) 
                    } 
                } 
            } 
            Spacer(modifier = Modifier.height(12.dp)) 
            LazyRow( 
                state = level4LazyState, 
                horizontalArrangement = Arrangement.spacedBy(14.dp), 
                contentPadding = PaddingValues(horizontal = 16.dp) 
            ) { 
                items(level4Stories) { story -> 
                    ShinobiStoryCard( 
                        story = story, 
                        onClick = { StorySessionManager.selectedStoryForDetails = story }, 
                        titleLanguage = storyLanguage 
                    ) 
                } 
            } 
        } 
    } 
} 
            } 
        } 
        
        selectedWordForDetail?.let { word -> 
            val isBookmarked = storyContainsWord(word, StorySessionManager.savedStoriesList) 
            ShinobiWordBottomSheet( 
                wordItem = word, 
                isBookmarked = isBookmarked, 
                showFurigana = showFurigana, 
                onToggleFurigana = { showFurigana = !showFurigana }, 
                onToggleBookmark = { 
                }, 
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
        
        if (showLevelSelectorSheet) { 
            ModalBottomSheet( 
                onDismissRequest = { showLevelSelectorSheet = false }, 
                sheetState = levelSheetState, 
                containerColor = ShinobiColors.SurfaceOverlay, 
                windowInsets = WindowInsets(0) 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .navigationBarsPadding() 
                        .padding(20.dp) 
                ) { 
                    Text( 
                        text = if (storyLanguage == "ja") "日本語レベルを選択" else "Select Japanese Level", 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White 
                    ) 
                    Spacer(modifier = Modifier.height(4.dp)) 
                    Text( 
                        text = if (storyLanguage == "ja") "各レベルには段階的なサブレベルとストーリーが用意されています" else "Each level features graded sub-levels and stories", 
                        fontSize = 12.sp, 
                        color = ShinobiColors.TextSecondary 
                    ) 
                    Spacer(modifier = Modifier.height(16.dp)) 
                    
                    val levelList = if (storyLanguage == "ja") { 
                        listOf( 
                            Triple("starter", "初級 (N5)", "33%"), 
                            Triple("learner", "初中級 (N4)", "0%"), 
                            Triple("explorer", "中級 (N3)", "0%"), 
                            Triple("challenger", "上級 (N2)", "0%"), 
                            Triple("master", "最上級 (N1)", "0%") 
                        ) 
                    } else { 
                        listOf( 
                            Triple("starter", "Starter (N5)", "33%"), 
                            Triple("learner", "Learner (N4)", "0%"), 
                            Triple("explorer", "Explorer (N3)", "0%"), 
                            Triple("challenger", "Challenger (N2)", "0%"), 
                            Triple("master", "Master (N1)", "0%") 
                        ) 
                    } 
                    
                    levelList.forEach { (lid, lname, prog) -> 
                        val isSel = selectedLevelId == lid 
                        Box( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .padding(vertical = 5.dp) 
                                .clip(ShinobiShapes.SquircleMedium) 
                                .background(if (isSel) ShinobiColors.EmeraldGreen.copy(alpha = 0.15f) else ShinobiColors.SurfaceCard1) 
                                .border( 
                                    1.dp, 
                                    if (isSel) ShinobiColors.EmeraldGreen else ShinobiColors.CardBorder, 
                                    ShinobiShapes.SquircleMedium 
                                ) 
                                .clickable { 
                                    selectedLevelId = lid 
                                    selectedLevelName = lname.substringBefore(" ") 
                                    showLevelSelectorSheet = false 
                                } 
                                .padding(16.dp) 
                        ) { 
                            Row( 
                                modifier = Modifier.fillMaxWidth(), 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.SpaceBetween 
                            ) { 
                                Row( 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.spacedBy(12.dp) 
                                ) { 
                                    Icon( 
                                        Icons.Default.Spa, 
                                        contentDescription = null, 
                                        tint = if (isSel) ShinobiColors.EmeraldGreen else ShinobiColors.TextSecondary, 
                                        modifier = Modifier.size(24.dp) 
                                    ) 
                                    Text( 
                                        text = lname, 
                                        fontSize = 16.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = if (isSel) Color.White else ShinobiColors.TextPrimary 
                                    ) 
                                } 
                                
                                Box( 
                                    modifier = Modifier 
                                        .clip(ShinobiShapes.Pill) 
                                        .background(ShinobiColors.SurfaceCard2) 
                                        .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.Pill) 
                                        .padding(horizontal = 10.dp, vertical = 4.dp) 
                                ) { 
                                    Text( 
                                        text = prog, 
                                        fontSize = 12.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = if (isSel) ShinobiColors.EmeraldGreen else ShinobiColors.TextSecondary 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                    Spacer(modifier = Modifier.height(20.dp)) 
                } 
            } 
        } 
        showCreationSheetForSlot?.let { slotId -> 
            val otherSlot = if (slotId == 1) StorySessionManager.slot2 else StorySessionManager.slot1 
            ForgeCreationBottomSheet( 
                slotId = slotId, 
                prefs = prefs, 
                storyLanguage = storyLanguage, 
                isOtherSlotForging = otherSlot.state == ForgeSlotState.FORGING, 
                initialGenre = selectedGenre, 
                initialLevel = selectedLevel, 
                initialLength = selectedLength, 
                initialCardCount = cardCountToFetch, 
                initialGenerateImage = generateImageAi, 
                onOpenAiConfig = onOpenAiConfig, 
                onDismiss = { showCreationSheetForSlot = null }, 
                onStartForge = { cardCount, genre, level, length, generateImage -> 
                    showCreationSheetForSlot = null 
                    currentStoryPage = 0 
                    isQuizMode = false 
                    currentQuizIndex = 0 
                    StorySessionManager.startOrQueueSlot( 
                        slotId = slotId, 
                        cardCount = cardCount, 
                        genre = genre, 
                        level = level, 
                        length = length, 
                        generateImage = generateImage, 
                        ankiHelper = ankiHelper, 
                        prefs = prefs, 
                        context = context 
                    ) 
                } 
            ) 
        } 
        
        if (showSavedLibrarySheet) { 
            ModalBottomSheet( 
                onDismissRequest = { showSavedLibrarySheet = false }, 
                sheetState = librarySheetState, 
                containerColor = ShinobiColors.SurfaceOverlay, 
                windowInsets = WindowInsets(0) 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .navigationBarsPadding() 
                        .padding(20.dp) 
                ) { 
                    Text( 
                        text = if (storyLanguage == "ja") "保存済みストーリー一覧" else "Saved Stories Library", 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White 
                    ) 
                    Spacer(modifier = Modifier.height(4.dp)) 
                    Text( 
                        text = if (storyLanguage == "ja") "${StorySessionManager.savedStoriesList.size}件 保存済み" else "${StorySessionManager.savedStoriesList.size} stories saved on device", 
                        fontSize = 12.sp, 
                        color = ShinobiColors.TextSecondary 
                    ) 
                    Spacer(modifier = Modifier.height(14.dp)) 
                    
                    if (StorySessionManager.savedStoriesList.isEmpty()) { 
                        Box( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .padding(28.dp), 
                            contentAlignment = Alignment.Center 
                        ) { 
                            Text( 
                                text = if (storyLanguage == "ja") "保存されたストーリーはまだありません。ストーリーを鍛造してブックマークしましょう！" else "No saved stories yet. Forge a story and tap the bookmark to save!", 
                                fontSize = 13.sp, 
                                color = ShinobiColors.TextMuted 
                            ) 
                        } 
                    } else { 
                        LazyColumn(modifier = Modifier.height(360.dp)) { 
                            items(StorySessionManager.savedStoriesList) { saved -> 
                                val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(saved.createdAt)) 
                                
                                Box( 
                                    modifier = Modifier 
                                        .fillMaxWidth() 
                                        .padding(vertical = 4.dp) 
                                        .clip(ShinobiShapes.SquircleSmall) 
                                        .background(ShinobiColors.SurfaceCard1) 
                                        .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.SquircleSmall) 
                                        .clickable { 
                                            StorySessionManager.currentStory = saved 
                                            StorySessionManager.userAnswers.clear() 
                                            currentStoryPage = 0 
                                            isQuizMode = false 
                                            showSavedLibrarySheet = false 
                                        } 
                                        .padding(14.dp) 
                                ) { 
                                    Row( 
                                        modifier = Modifier.fillMaxWidth(), 
                                        verticalAlignment = Alignment.CenterVertically 
                                    ) { 
                                        Column(modifier = Modifier.weight(1f)) { 
                                            Text( 
                                                saved.title, 
                                                fontSize = 15.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                color = Color.White 
                                            ) 
                                            Text( 
                                                "${saved.genre} • ${saved.level} • $formattedDate", 
                                                fontSize = 11.sp, 
                                                color = ShinobiColors.TextSecondary 
                                            ) 
                                        } 
                                        IconButton( 
                                            onClick = { 
                                                StorySessionManager.deleteSavedStory(context, saved.id) 
                                            } 
                                        ) { 
                                            Icon( 
                                                Icons.Default.Delete, 
                                                contentDescription = "Delete", 
                                                tint = ShinobiColors.CoralRed, 
                                                modifier = Modifier.size(18.dp) 
                                            ) 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 
                    Spacer(modifier = Modifier.height(20.dp)) 
                } 
            } 
        } 
        val newlyCompleted = StorySessionManager.newlyCompletedStory 
        val showBanner = StorySessionManager.showCompletionNotification 
        
        LaunchedEffect(showBanner) { 
            if (showBanner) { 
                delay(9000L) 
                StorySessionManager.showCompletionNotification = false 
            } 
        } 
        
        AnimatedVisibility( 
            visible = showBanner && newlyCompleted != null, 
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(), 
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(), 
            modifier = Modifier 
                .align(Alignment.TopCenter) 
                .statusBarsPadding() 
                .padding(top = 56.dp, start = 16.dp, end = 16.dp) 
                .zIndex(100f) 
        ) { 
            newlyCompleted?.let { story -> 
                val coverFile = remember(story.id, coverRefreshKey) { 
                    File(context.filesDir, "stories/images/${story.id}/cover.png") 
                } 
                val coverBitmap = remember(coverFile.absolutePath, coverRefreshKey) { 
                    if (coverFile.exists()) { 
                        try { 
                            BitmapFactory.decodeFile(coverFile.absolutePath)?.asImageBitmap() 
                        } catch (_: Exception) { null } 
                    } else null 
                } 
                
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(RoundedCornerShape(20.dp)) 
                        .background(Color(0xFF0F172A)) 
                        .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(20.dp)) 
                        .clickable { 
                            StorySessionManager.showCompletionNotification = false 
                            StorySessionManager.currentStory = story 
                            currentStoryPage = 0 
                            storyPhase = StoryPhase.READER 
                        } 
                        .padding(14.dp) 
                ) { 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(12.dp) 
                    ) { 
                        if (coverBitmap != null) { 
                            Image( 
                                bitmap = coverBitmap, 
                                contentDescription = null, 
                                contentScale = ContentScale.Crop, 
                                modifier = Modifier 
                                    .size(46.dp) 
                                    .clip(RoundedCornerShape(10.dp)) 
                            ) 
                        } else { 
                            Box( 
                                modifier = Modifier 
                                    .size(46.dp) 
                                    .clip(RoundedCornerShape(10.dp)) 
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f)), 
                                contentAlignment = Alignment.Center 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.AutoAwesome, 
                                    contentDescription = null, 
                                    tint = Color(0xFF10B981), 
                                    modifier = Modifier.size(24.dp) 
                                ) 
                            } 
                        } 
                        
                        Column(modifier = Modifier.weight(1f)) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(6.dp) 
                            ) { 
                                Box( 
                                    modifier = Modifier 
                                        .clip(ShinobiShapes.Pill) 
                                        .background(Color(0xFF10B981)) 
                                        .padding(horizontal = 6.dp, vertical = 2.dp) 
                                ) { 
                                    Text( 
                                        text = "READY", 
                                        fontSize = 9.sp, 
                                        fontWeight = FontWeight.ExtraBold, 
                                        color = Color.Black 
                                    ) 
                                } 
                                Text( 
                                    text = "✨ Story Forged!", 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = Color(0xFF34D399) 
                                ) 
                            } 
                            Spacer(modifier = Modifier.height(2.dp)) 
                            Text( 
                                text = story.title, 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White, 
                                maxLines = 1, 
                                overflow = TextOverflow.Ellipsis 
                            ) 
                        } 
                        
                        IconButton( 
                            onClick = { 
                                activeWallhavenTarget = WallhavenPickerTarget( 
                                    storyId = story.id, 
                                    title = story.title, 
                                    genre = story.genre, 
                                    visualAnchor = story.visualAnchor, 
                                    artTags = story.artTags 
                                ) 
                            }, 
                            modifier = Modifier.size(32.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Image, 
                                contentDescription = "Pick Artwork", 
                                tint = Color(0xFF6EE7B7), 
                                modifier = Modifier.size(18.dp) 
                            ) 
                        } 
                        
                        IconButton( 
                            onClick = { StorySessionManager.showCompletionNotification = false }, 
                            modifier = Modifier.size(32.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Close, 
                                contentDescription = "Dismiss", 
                                tint = Color(0xFF94A3B8), 
                                modifier = Modifier.size(18.dp) 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    activeWallhavenTarget?.let { target -> 
        WallhavenImagePickerSheet( 
            storyId = target.storyId, 
            initialTitle = target.title, 
            initialGenre = target.genre, 
            initialVisualAnchor = target.visualAnchor, 
            initialArtTags = target.artTags, 
            onDismiss = { activeWallhavenTarget = null }, 
            onImageSelected = { 
                coverRefreshKey++ 
                activeWallhavenTarget = null 
            } 
        ) 
    } 
} 

@Composable
private fun ForgingCardEffects( 
    modifier: Modifier = Modifier, 
    content: @Composable () -> Unit 
) { 
    val infiniteTransition = rememberInfiniteTransition(label = "forgingCardEffects") 
    val shimmerOffset by infiniteTransition.animateFloat( 
        initialValue = -1.5f, 
        targetValue = 2.5f, 
        animationSpec = infiniteRepeatable( 
            animation = keyframes { 
                durationMillis = 6500 
                -1.5f at 0 with FastOutSlowInEasing 
                2.5f at 3800 
                2.5f at 6500 
            }, 
            repeatMode = RepeatMode.Restart 
        ), 
        label = "shimmerOffset" 
    ) 
    val borderRotation by infiniteTransition.animateFloat( 
        initialValue = 0f, 
        targetValue = 360f, 
        animationSpec = infiniteRepeatable( 
            animation = tween(durationMillis = 4000, easing = LinearEasing), 
            repeatMode = RepeatMode.Restart 
        ), 
        label = "borderRotation" 
    ) 
    
    val rotatingBeamBrush = remember(borderRotation) { 
        Brush.sweepGradient( 
            colors = listOf( 
                ShinobiColors.SakuraRose, 
                ShinobiColors.SakuraRose.copy(alpha = 0.3f), 
                Color(0xFFFFF1F2), 
                ShinobiColors.SakuraRose.copy(alpha = 0.3f), 
                ShinobiColors.SakuraRose 
            ) 
        ) 
    } 
    
    Box( 
        modifier = modifier 
            .clip(RoundedCornerShape(22.dp)) 
            .border(2.dp, rotatingBeamBrush, RoundedCornerShape(22.dp)) 
            .background( 
                Brush.linearGradient( 
                    listOf(Color(0xFF1A1318), Color(0xFF2A1B20), Color(0xFF170F14)) 
                ) 
            ) 
    ) { 
        content() 
        
        Box( 
            modifier = Modifier 
                .matchParentSize() 
                .clip(RoundedCornerShape(22.dp)) 
                .background( 
                    Brush.linearGradient( 
                        colors = listOf( 
                            Color.Transparent, 
                            Color.White.copy(alpha = 0.04f), 
                            Color.White.copy(alpha = 0.28f), 
                            Color.White.copy(alpha = 0.04f), 
                            Color.Transparent 
                        ), 
                        start = Offset(shimmerOffset * 1000f, 0f), 
                        end = Offset((shimmerOffset + 1.2f) * 1000f, 1000f) 
                    ) 
                ) 
        ) 
    } 
} 

@Composable
private fun ForgeSlotCard( 
    slot: ForgeSlotData, 
    prefs: PreferencesManager, 
    context: android.content.Context, 
    storyLanguage: String, 
    onConfigure: () -> Unit, 
    onQuickForge: () -> Unit, 
    onCancelQueue: () -> Unit, 
    onClearSlot: () -> Unit, 
    onReadStory: (ForgedStory) -> Unit, 
    onLaunchGame: (String) -> Unit, 
    onCancelForging: () -> Unit = {} 
) { 
    when (slot.state) { 
        ForgeSlotState.EMPTY -> { 
            val hasError = slot.error != null 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(RoundedCornerShape(22.dp)) 
                    .background(Color(0xFF131316)) 
                    .border( 
                        BorderStroke(1.5.dp, if (hasError) Color(0xFFEF4444).copy(alpha = 0.7f) else Color(0xFF3F3F46)), 
                        RoundedCornerShape(22.dp) 
                    ) 
                    .padding(16.dp) 
            ) { 
                Column( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalArrangement = Arrangement.spacedBy(12.dp) 
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
                            Box( 
                                modifier = Modifier 
                                    .size(8.dp) 
                                    .clip(CircleShape) 
                                    .background(if (hasError) Color(0xFFEF4444) else Color(0xFF71717A)) 
                            ) 
                            Text( 
                                text = if (hasError) "CHAMBER ${slot.slotId} • FAILED" else "CHAMBER ${slot.slotId} • READY", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                fontFamily = ShinobiNunito, 
                                color = if (hasError) Color(0xFFF87171) else Color(0xFFA1A1AA) 
                            ) 
                        } 
                        Box( 
                            modifier = Modifier 
                                .clip(ShinobiShapes.Pill) 
                                .background(if (hasError) Color(0xFF451A1A) else Color(0xFF27272A)) 
                                .padding(horizontal = 8.dp, vertical = 3.dp) 
                        ) { 
                            Text( 
                                text = if (hasError) "ERROR" else "AVAILABLE", 
                                fontSize = 10.sp, 
                                fontWeight = FontWeight.ExtraBold, 
                                color = if (hasError) Color(0xFFFCA5A5) else Color(0xFF71717A) 
                            ) 
                        } 
                    } 
                    
                    if (slot.error != null) { 
                        Surface( 
                            shape = RoundedCornerShape(12.dp), 
                            color = Color(0xFF2D1515), 
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)), 
                            modifier = Modifier.fillMaxWidth() 
                        ) { 
                            Row( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .padding(horizontal = 12.dp, vertical = 10.dp), 
                                verticalAlignment = Alignment.CenterVertically 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.ErrorOutline, 
                                    contentDescription = null, 
                                    tint = Color(0xFFEF4444), 
                                    modifier = Modifier.size(18.dp) 
                                ) 
                                Spacer(modifier = Modifier.width(8.dp)) 
                                Text( 
                                    text = slot.error, 
                                    fontSize = 12.sp, 
                                    fontFamily = ShinobiNunito, 
                                    color = Color(0xFFFCA5A5), 
                                    modifier = Modifier.weight(1f) 
                                ) 
                                Spacer(modifier = Modifier.width(6.dp)) 
                                IconButton( 
                                    onClick = { StorySessionManager.clearSlotError(slot.slotId) }, 
                                    modifier = Modifier.size(24.dp) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.Default.Close, 
                                        contentDescription = "Dismiss", 
                                        tint = Color(0xFFFCA5A5), 
                                        modifier = Modifier.size(16.dp) 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                    
                    Text( 
                        text = if (storyLanguage == "ja") "ストーリーを鍛造する準備ができました" else "Chamber ready to synthesize Japanese story", 
                        fontSize = 13.sp, 
                        fontFamily = ShinobiNunito, 
                        color = Color(0xFFE2E8F0) 
                    ) 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    ShinobiTactileButton( 
                        onClick = onConfigure, 
                        modifier = Modifier.weight(1f), 
                        faceColor = Color(0xFF27272A), 
                        lipColor = Color(0xFF3F3F46), 
                        contentColor = Color.White, 
                        buttonHeight = 44.dp 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Tune, 
                            contentDescription = null, 
                            tint = Color(0xFFC4B5FD), 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(6.dp)) 
                        Text( 
                            text = if (storyLanguage == "ja") "手動鍛造" else "MANUAL FORGE", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = ShinobiNunito 
                        ) 
                    } 
                    
                    ShinobiTactileButton( 
                        onClick = onQuickForge, 
                        modifier = Modifier.weight(1f), 
                        faceColor = Color(0xFF7C3AED), 
                        lipColor = Color(0xFF5B21B6), 
                        contentColor = Color.White, 
                        buttonHeight = 44.dp 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Bolt, 
                            contentDescription = null, 
                            tint = Color(0xFFFDE047), 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(6.dp)) 
                        Text( 
                            text = if (storyLanguage == "ja") "クイック鍛造" else "QUICK FORGE", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = ShinobiNunito 
                        ) 
                    } 
                } 
            } 
        } 
    } 
    ForgeSlotState.FORGING -> { 
        ForgingCardEffects(modifier = Modifier.fillMaxWidth()) { 
            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(18.dp), 
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
                        Box( 
                            modifier = Modifier 
                                .size(10.dp) 
                                .clip(CircleShape) 
                                .background(Color(0xFF10B981)) 
                        ) 
                        Text( 
                            text = "CHAMBER ${slot.slotId} • FORGING", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFFA78BFA) 
                        ) 
                    } 
                    Icon( 
                        imageVector = Icons.Default.AutoAwesome, 
                        contentDescription = null, 
                        tint = Color(0xFFFBBF24), 
                        modifier = Modifier.size(20.dp) 
                    ) 
                } 
                
                Row( 
                    horizontalArrangement = Arrangement.spacedBy(6.dp), 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF065F46)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        Text(slot.level, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7)) 
                    } 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF1E3A8A)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        Text(slot.genre, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF93C5FD)) 
                    } 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF78350F)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        val cardText = if (slot.cardCount == 0) "Free Flow" else "${slot.cardCount} Words" 
                        Text(cardText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE68A)) 
                    } 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF4C1D95)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        Text(slot.length, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDDD6FE)) 
                    } 
                } 
                
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { 
                    val stageText = slot.stage.ifBlank { StorySessionManager.generationStage } 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        Text( 
                            text = stageText, 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color.White 
                        ) 
                        Text( 
                            text = "${(slot.progressFraction * 100).toInt()}%", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF34D399) 
                        ) 
                    } 
                    
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .height(8.dp) 
                            .clip(RoundedCornerShape(4.dp)) 
                            .background(Color(0xFF27272A)) 
                    ) { 
                        Box( 
                            modifier = Modifier 
                                .fillMaxWidth(slot.progressFraction.coerceIn(0.08f, 0.98f)) 
                                .fillMaxHeight() 
                                .clip(RoundedCornerShape(4.dp)) 
                                .background( 
                                    Brush.horizontalGradient( 
                                        listOf(Color(0xFF10B981), Color(0xFF38BDF8)) 
                                    ) 
                                ) 
                        ) 
                    } 
                } 
                
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(1.dp) 
                        .background(Color(0xFF2E2E38)) 
                ) 
                
                Text( 
                    text = if (storyLanguage == "ja") "⚔️ 鍛造中ミニゲーム (難易度選択):" else "⚔️ Play Speed Strike while waiting:", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = ShinobiNunito, 
                    color = Color(0xFFCBD5E1) 
                ) 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    listOf( 
                        Triple("Easy", Color(0xFF065F46), Color(0xFF047857)), 
                        Triple("Advance", Color(0xFF1E40AF), Color(0xFF1D4ED8)), 
                        Triple("Hardcore", Color(0xFF991B1B), Color(0xFFB91C1C)) 
                    ).forEach { (tier, face, lip) -> 
                        val highScore = prefs.getSpeedStrikeHighScore(tier) 
                        val subInfo = when (tier) { 
                            "Easy" -> "10 HP • 5s" 
                            "Hardcore" -> "4 HP • 3s" 
                            else -> "7 HP • 4s" 
                        } 
                        ShinobiTactileButton( 
                            onClick = { onLaunchGame(tier) }, 
                            modifier = Modifier.weight(1f), 
                            faceColor = face, 
                            lipColor = lip, 
                            contentColor = Color.White, 
                            buttonHeight = 74.dp 
                        ) { 
                            Column( 
                                horizontalAlignment = Alignment.CenterHorizontally, 
                                verticalArrangement = Arrangement.spacedBy(2.dp), 
                                modifier = Modifier.padding(vertical = 2.dp) 
                            ) { 
                                Text( 
                                    text = tier.uppercase(), 
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    fontFamily = ShinobiNunito, 
                                    color = Color.White 
                                ) 
                                Text( 
                                    text = subInfo, 
                                    fontSize = 9.sp, 
                                    fontFamily = ShinobiNunito, 
                                    color = Color.White.copy(alpha = 0.85f) 
                                ) 
                                Row( 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.spacedBy(2.dp) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.Default.Star, 
                                        contentDescription = null, 
                                        tint = Color(0xFFFBBF24), 
                                        modifier = Modifier.size(11.dp) 
                                    ) 
                                    Text( 
                                        text = "$highScore", 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.ExtraBold, 
                                        fontFamily = ShinobiNunito, 
                                        color = Color(0xFFFDE047) 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
                
                ShinobiTactileButton( 
                    onClick = onCancelForging, 
                    faceColor = Color(0xFF451A1A), 
                    lipColor = Color(0xFF7F1D1D), 
                    contentColor = Color(0xFFFCA5A5), 
                    buttonHeight = 44.dp, 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Close, 
                        contentDescription = null, 
                        tint = Color(0xFFFCA5A5), 
                        modifier = Modifier.size(16.dp) 
                    ) 
                    Spacer(modifier = Modifier.width(6.dp)) 
                    Text( 
                        text = if (storyLanguage == "ja") "鍛造をキャンセル" else "CANCEL FORGING", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = ShinobiNunito 
                    ) 
                } 
            } 
        } 
    } 
    ForgeSlotState.QUEUED -> { 
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .clip(RoundedCornerShape(22.dp)) 
                .background( 
                    Brush.verticalGradient( 
                        listOf(Color(0xFF1C1917), Color(0xFF272015)) 
                    ) 
                ) 
                .border( 
                    BorderStroke(1.5.dp, Color(0xFFD97706).copy(alpha = 0.7f)), 
                    RoundedCornerShape(22.dp) 
                ) 
                .padding(18.dp) 
        ) { 
            Column( 
                modifier = Modifier.fillMaxWidth(), 
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
                            imageVector = Icons.Default.HourglassTop, 
                            contentDescription = null, 
                            tint = Color(0xFFFBBF24), 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Text( 
                            text = "CHAMBER ${slot.slotId} • IN QUEUE", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFFFBBF24) 
                        ) 
                    } 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF78350F)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        Text( 
                            text = "WAITING", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color(0xFFFDE68A) 
                        ) 
                    } 
                } 
                
                Text( 
                    text = if (storyLanguage == "ja") 
                        "第${if (slot.slotId == 1) 2 else 1}チェンバーの生成完了後に自動的に開始されます。" 
                    else 
                        "Will begin forging automatically once Chamber ${if (slot.slotId == 1) 2 else 1} finishes.", 
                    fontSize = 13.sp, 
                    fontFamily = ShinobiNunito, 
                    color = Color(0xFFE2E8F0) 
                ) 
                
                Row( 
                    horizontalArrangement = Arrangement.spacedBy(6.dp), 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF27272A)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        Text(slot.level, fontSize = 11.sp, color = Color.White) 
                    } 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF27272A)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        Text(slot.genre, fontSize = 11.sp, color = Color.White) 
                    } 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF27272A)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        val cardText = if (slot.cardCount == 0) "Free Flow" else "${slot.cardCount} Words" 
                        Text(cardText, fontSize = 11.sp, color = Color.White) 
                    } 
                } 
                
                ShinobiTactileButton( 
                    onClick = onCancelQueue, 
                    faceColor = Color(0xFF27272A), 
                    lipColor = Color(0xFF3F3F46), 
                    contentColor = Color(0xFFE4E4E7), 
                    buttonHeight = 44.dp, 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Text( 
                        text = if (storyLanguage == "ja") "キューをキャンセル" else "CANCEL QUEUE", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Bold 
                    ) 
                } 
            } 
        } 
    } 
    ForgeSlotState.COMPLETED -> { 
        val story = slot.story 
        val coverFile = remember(story?.id, story?.title) { 
            story?.let { File(context.filesDir, "stories/images/${it.id}/cover.png") } 
        } 
        val coverBitmap = remember(coverFile?.absolutePath, coverFile?.exists(), coverFile?.lastModified()) { 
            if (coverFile?.exists() == true) { 
                try { 
                    BitmapFactory.decodeFile(coverFile.absolutePath)?.asImageBitmap() 
                } catch (_: Exception) { null } 
            } else null 
        } 
        
        val chamberModel = story?.let { s -> 
            ShinobiStoryModel( 
                id = s.id, 
                levelId = slot.level.lowercase(), 
                titleJapanese = s.title.substringAfter("-", s.title).trim(), 
                titleEnglish = s.title.substringBefore("-", s.title).trim(), 
                category = s.genre, 
                estimatedMinutes = s.calculatedEstimatedMinutes, 
                xp = 100, 
                isCompleted = StorySessionManager.completedStoryIds.contains(s.id), 
                readingProgress = StorySessionManager.getSavedStoryProgress(s.id), 
                artworkType = s.genre.lowercase(), 
                summary = s.storyEnglish.take(100) + "...", 
                author = "Anki AI", 
                story = s 
            ) 
        } 
        
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .clip(RoundedCornerShape(22.dp)) 
                .background( 
                    Brush.verticalGradient( 
                        listOf(Color(0xFF0F172A), Color(0xFF141F2E)) 
                    ) 
                ) 
                .border( 
                    BorderStroke(1.5.dp, Color(0xFF10B981).copy(alpha = 0.8f)), 
                    RoundedCornerShape(22.dp) 
                ) 
        ) { 
            if (coverBitmap != null) { 
                Image( 
                    bitmap = coverBitmap, 
                    contentDescription = null, 
                    contentScale = ContentScale.Crop, 
                    modifier = Modifier.matchParentSize() 
                ) 
            } else { 
                Box( 
                    modifier = Modifier 
                        .matchParentSize() 
                        .background(Color(0xFF1E293B)), 
                    contentAlignment = Alignment.Center 
                ) { 
                    StoryArtworkThumbnail( 
                        artworkType = slot.genre.lowercase(), 
                        modifier = Modifier.matchParentSize() 
                    ) 
                } 
            } 
            
            Box( 
                modifier = Modifier 
                    .matchParentSize() 
                    .background( 
                        Brush.verticalGradient( 
                            colors = listOf( 
                                Color.Black.copy(alpha = 0.50f), 
                                Color.Black.copy(alpha = 0.75f), 
                                Color.Black.copy(alpha = 0.94f) 
                            ) 
                        ) 
                    ) 
            ) 
            
            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(18.dp), 
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
                            imageVector = Icons.Default.Check, 
                            contentDescription = null, 
                            tint = Color(0xFF10B981), 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Text( 
                            text = "CHAMBER ${slot.slotId} • READY TO READ! ✨", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFF34D399) 
                        ) 
                    } 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(6.dp) 
                    ) { 
                        Box( 
                            modifier = Modifier 
                                .clip(ShinobiShapes.Pill) 
                                .background(Color(0xFF451A03)) 
                                .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), ShinobiShapes.Pill) 
                                .padding(horizontal = 8.dp, vertical = 3.dp) 
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(3.dp) 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.Bookmark, 
                                    contentDescription = null, 
                                    tint = Color(0xFFF59E0B), 
                                    modifier = Modifier.size(11.dp) 
                                ) 
                                Text( 
                                    text = "SAVED", 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    color = Color(0xFFF59E0B) 
                                ) 
                            } 
                        } 
                        Box( 
                            modifier = Modifier 
                                .clip(ShinobiShapes.Pill) 
                                .background(Color(0xFF065F46)) 
                                .padding(horizontal = 8.dp, vertical = 3.dp) 
                        ) { 
                            Text( 
                                text = "DONE", 
                                fontSize = 10.sp, 
                                fontWeight = FontWeight.ExtraBold, 
                                color = Color(0xFF6EE7B7) 
                            ) 
                        } 
                    } 
                } 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(6.dp) 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color.Black.copy(alpha = 0.65f)) 
                            .border(1.dp, Color.White.copy(alpha = 0.25f), ShinobiShapes.Pill) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        Text( 
                            text = "${slot.level} • ${slot.genre}", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White 
                        ) 
                    } 
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF065F46).copy(alpha = 0.85f)) 
                            .padding(horizontal = 8.dp, vertical = 3.dp) 
                    ) { 
                        Text( 
                            text = "${story?.sentences?.size ?: 0} Pages", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF6EE7B7) 
                        ) 
                    } 
                } 
                
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clickable { 
                            if (chamberModel != null) { 
                                StorySessionManager.selectedStoryForDetails = chamberModel 
                            } 
                        } 
                ) { 
                    val rawTitle: String = story?.title ?: "Forged Story" 
                    val titleMain: String = if (rawTitle.contains("-")) rawTitle.substringBefore("-").trim() else rawTitle 
                    val titleSub: String = if (rawTitle.contains("-")) rawTitle.substringAfter("-").trim() else "" 
                    Text( 
                        text = titleMain, 
                        fontSize = 17.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontFamily = ShinobiNunito, 
                        color = Color.White, 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis 
                    ) 
                    if (titleSub.isNotBlank() && titleSub != titleMain) { 
                        Text( 
                            text = titleSub, 
                            fontSize = 13.sp, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFFCBD5E1), 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis 
                        ) 
                    } 
                } 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    ShinobiTactileButton( 
                        onClick = { story?.let { onReadStory(it) } }, 
                        modifier = Modifier.weight(1.3f), 
                        faceColor = Color(0xFF059669), 
                        lipColor = Color(0xFF047857), 
                        contentColor = Color.White, 
                        buttonHeight = 46.dp 
                    ) { 
                        Icon( 
                            imageVector = Icons.AutoMirrored.Filled.MenuBook, 
                            contentDescription = null, 
                            tint = Color.White, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(6.dp)) 
                        Text( 
                            text = if (storyLanguage == "ja") "読む" else "READ STORY", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Bold 
                        ) 
                    } 
                    
                    ShinobiTactileButton( 
                        onClick = onClearSlot, 
                        modifier = Modifier.weight(0.9f), 
                        faceColor = Color(0xFF27272A), 
                        lipColor = Color(0xFF3F3F46), 
                        contentColor = Color(0xFFE4E4E7), 
                        buttonHeight = 46.dp 
                    ) { 
                        Text( 
                            text = if (storyLanguage == "ja") "クリア" else "CLEAR SLOT", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold 
                        ) 
                    } 
                } 
            } 
        } 
    } 
} 
} 

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) 
@Composable
private fun ForgeCreationBottomSheet( 
    slotId: Int, 
    prefs: PreferencesManager, 
    storyLanguage: String, 
    isOtherSlotForging: Boolean, 
    initialGenre: String, 
    initialLevel: String, 
    initialLength: String, 
    initialCardCount: Int, 
    initialGenerateImage: Boolean, 
    onOpenAiConfig: () -> Unit, 
    onDismiss: () -> Unit, 
    onStartForge: (cardCount: Int, genre: String, level: String, length: String, generateImage: Boolean) -> Unit 
) { 
    var localLevel by remember { mutableStateOf(initialLevel) } 
    var localGenre by remember { mutableStateOf(initialGenre) } 
    var localLength by remember { mutableStateOf(initialLength) } 
    var localCardCount by remember { mutableIntStateOf(initialCardCount) } 
    var localGenerateImage by remember { mutableStateOf(initialGenerateImage) } 
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    val allGenres = listOf("Daily Life", "Traditional", "Nature", "Romance", "School", "Fantasy", "Mystery", "Adventure", "Gourmet", "Sci-Fi", "Samurai", "Supernatural", "Sports") 
    val topThemes = remember { prefs.getTopThemes(allGenres, 2) } 
    val genres = remember(topThemes) { 
        val remaining = allGenres.filter { it !in topThemes } 
        topThemes + remaining 
    } 
    val levels = listOf("N5", "N4", "N3", "N2", "N1") 
    val lengths = listOf("Short", "Medium", "Long") 
    val cardCounts = listOf(0, 3, 4, 5) 
    
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
                    .clip(ShinobiShapes.Pill) 
                    .background(Color(0xFF52525B)) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 20.dp, vertical = 6.dp) 
                .navigationBarsPadding() 
                .verticalScroll(rememberScrollState()) 
                .padding(bottom = 24.dp), 
            verticalArrangement = Arrangement.spacedBy(16.dp) 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Column { 
                    Text( 
                        text = if (storyLanguage == "ja") "ストーリー鍛造 • スロット $slotId" else "Forge Story • Chamber $slotId", 
                        fontSize = 19.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontFamily = ShinobiNunito, 
                        color = Color.White 
                    ) 
                    Text( 
                        text = if (storyLanguage == "ja") "AnkiカードからAI挿絵付きストーリーを作成" else "AI illustrated stories tailored to your level", 
                        fontSize = 12.sp, 
                        fontFamily = ShinobiNunito, 
                        color = ShinobiColors.TextSecondary 
                    ) 
                } 
                
                Box( 
                    modifier = Modifier 
                        .clip(ShinobiShapes.Pill) 
                        .background(if (isOtherSlotForging) Color(0xFF7C3AED) else Color(0xFF059669)) 
                        .padding(horizontal = 10.dp, vertical = 4.dp) 
                ) { 
                    Text( 
                        text = if (isOtherSlotForging) "QUEUES NEXT" else "FORGES DIRECT", 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        color = Color.White 
                    ) 
                } 
            } 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(1.dp) 
                    .background(Color(0xFF27272A)) 
            ) 
            
            if (prefs.aiApiKey.isBlank()) { 
                Surface( 
                    onClick = onOpenAiConfig, 
                    shape = ShinobiShapes.SquircleMedium, 
                    color = Color(0xFF7C2D12).copy(alpha = 0.3f), 
                    border = BorderStroke(1.dp, Color(0xFFEA580C)), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Row( 
                        modifier = Modifier.padding(14.dp), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        Icon( 
                            Icons.Default.Key, 
                            contentDescription = null, 
                            tint = Color(0xFFFB923C), 
                            modifier = Modifier.size(22.dp) 
                        ) 
                        Column(modifier = Modifier.weight(1f)) { 
                            Text( 
                                text = if (storyLanguage == "ja") "AI APIキーが必要です" else "AI API Key Required", 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFFFFEDD5) 
                            ) 
                            Text( 
                                text = if (storyLanguage == "ja") "タップしてAPIキーを設定" else "Tap to configure your API key", 
                                fontSize = 12.sp, 
                                color = Color(0xFFFDBA74) 
                            ) 
                        } 
                    } 
                } 
            } 
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { 
                val currentLevelIndex = remember(localLevel) { 
                    when { 
                        localLevel.startsWith("N5") -> 0 
                        localLevel.startsWith("N4") -> 1 
                        localLevel.startsWith("N3") -> 2 
                        localLevel.startsWith("N2") -> 3 
                        localLevel.startsWith("N1") -> 4 
                        else -> 0 
                    } 
                } 
                var sliderValue by remember(localLevel) { mutableFloatStateOf(currentLevelIndex.toFloat()) } 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Text( 
                        text = if (storyLanguage == "ja") "熟練度レベル (JLPT)" else "Proficiency Level (JLPT)", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = ShinobiNunito, 
                        color = Color.White 
                    ) 
                    
                    Box( 
                        modifier = Modifier 
                            .clip(ShinobiShapes.Pill) 
                            .background(Color(0xFF064E3B)) 
                            .border(1.dp, Color(0xFF10B981), ShinobiShapes.Pill) 
                            .padding(horizontal = 10.dp, vertical = 3.dp) 
                    ) { 
                        Text( 
                            text = levels[sliderValue.roundToInt().coerceIn(0, 4)], 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFF6EE7B7) 
                        ) 
                    } 
                } 
                
                GlobalSeekerContainer( 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Slider( 
                        value = sliderValue, 
                        onValueChange = { newVal -> 
                            sliderValue = newVal 
                            val rounded = newVal.roundToInt().coerceIn(0, 4) 
                            localLevel = levels[rounded] 
                        }, 
                        valueRange = 0f..4f, 
                        steps = 3, 
                        colors = SliderDefaults.colors( 
                            thumbColor = Color(0xFF10B981), 
                            activeTrackColor = Color(0xFF10B981), 
                            inactiveTrackColor = ShinobiColors.CardBorderSubtle, 
                            activeTickColor = Color(0xFF6EE7B7), 
                            inactiveTickColor = ShinobiColors.TextMuted 
                        ), 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .height(28.dp) 
                    ) 
                    
                    Row( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(horizontal = 4.dp), 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        levels.forEachIndexed { index, lvl -> 
                            val isSelected = (sliderValue.roundToInt() == index) 
                            Text( 
                                text = lvl, 
                                fontSize = if (isSelected) 13.sp else 11.sp, 
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold, 
                                fontFamily = ShinobiNunito, 
                                color = if (isSelected) Color(0xFF34D399) else Color(0xFF71717A), 
                                modifier = Modifier.clickable { 
                                    sliderValue = index.toFloat() 
                                    localLevel = lvl 
                                } 
                            ) 
                        } 
                    } 
                } 
                
                val levelDesc = when { 
                    localLevel.startsWith("N5") -> "N5 Starter • Basic vocabulary, daily greetings & simple sentence patterns." 
                    localLevel.startsWith("N4") -> "N4 Beginner • Elementary sentences, daily expressions & routine scenarios." 
                    localLevel.startsWith("N3") -> "N3 Intermediate • Conversational Japanese, short stories & varied situations." 
                    localLevel.startsWith("N2") -> "N2 Advanced • Rich narrative prose, nuanced idioms & descriptive grammar." 
                    else -> "N1 Master • Literary fiction, native subtlety & sophisticated vocabulary." 
                } 
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(RoundedCornerShape(12.dp)) 
                        .background(Color(0xFF222228)) 
                        .border(1.dp, Color(0xFF2E2E36), RoundedCornerShape(12.dp)) 
                        .padding(horizontal = 12.dp, vertical = 8.dp) 
                ) { 
                    Text( 
                        text = levelDesc, 
                        fontSize = 11.sp, 
                        color = Color(0xFF93C5FD), 
                        lineHeight = 16.sp 
                    ) 
                } 
            } 
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                Text( 
                    text = if (storyLanguage == "ja") "ストーリーの長さ" else "Story Length", 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = ShinobiNunito, 
                    color = Color.White 
                ) 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    lengths.forEach { len -> 
                        val isSel = localLength == len 
                        val subText = when (len) { 
                            "Short" -> "4-5 pgs" 
                            "Long" -> "9-10 pgs" 
                            else -> "6-7 pgs" 
                        } 
                        ShinobiTactileButton( 
                            onClick = { localLength = len }, 
                            modifier = Modifier.weight(1f), 
                            faceColor = if (isSel) Color(0xFF1E40AF) else Color(0xFF27272A), 
                            lipColor = if (isSel) Color(0xFF1D4ED8) else Color(0xFF3F3F46), 
                            contentColor = if (isSel) Color.White else Color(0xFFA1A1AA), 
                            buttonHeight = 46.dp 
                        ) { 
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                                Text( 
                                    text = len, 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    fontFamily = ShinobiNunito 
                                ) 
                                Text( 
                                    text = subText, 
                                    fontSize = 9.sp, 
                                    color = if (isSel) Color(0xFFBFDBFE) else Color(0xFF71717A) 
                                ) 
                            } 
                        } 
                    } 
                } 
            } 
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                Text( 
                    text = if (storyLanguage == "ja") "ストーリーのテーマ" else "Story Theme", 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = ShinobiNunito, 
                    color = Color.White 
                ) 
                
                FlowRow( 
                    horizontalArrangement = Arrangement.spacedBy(8.dp), 
                    verticalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    genres.forEach { g -> 
                        val isSel = localGenre == g 
                        val isTopTheme = g in topThemes 
                        val displayG = if (storyLanguage == "ja") { 
                            when (g.lowercase()) { 
                                "daily life" -> "日常" 
                                "traditional" -> "伝統" 
                                "nature" -> "自然" 
                                "romance" -> "恋愛" 
                                "school" -> "学校" 
                                "fantasy" -> "ファンタジー" 
                                "mystery" -> "ミステリー" 
                                "adventure" -> "冒険" 
                                "gourmet" -> "グルメ" 
                                "sci-fi" -> "SF" 
                                "samurai" -> "侍・時代劇" 
                                "supernatural" -> "超自然" 
                                "sports" -> "スポーツ" 
                                else -> g 
                            } 
                        } else g 
                        Box( 
                            modifier = Modifier 
                                .clip(RoundedCornerShape(12.dp)) 
                                .background( 
                                    if (isSel) Color(0xFF7C3AED) 
                                    else if (isTopTheme) Color(0xFF2A2038) 
                                    else Color(0xFF27272A) 
                                ) 
                                .border( 
                                    1.dp, 
                                    if (isSel) Color(0xFFA78BFA) 
                                    else if (isTopTheme) Color(0xFFF59E0B).copy(alpha = 0.6f) 
                                    else Color(0xFF3F3F46), 
                                    RoundedCornerShape(12.dp) 
                                ) 
                                .clickable { localGenre = g } 
                                .padding(horizontal = 14.dp, vertical = 9.dp) 
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(4.dp) 
                            ) { 
                                if (isTopTheme) { 
                                    Icon( 
                                        imageVector = Icons.Default.Star, 
                                        contentDescription = null, 
                                        tint = if (isSel) Color(0xFFFDE047) else Color(0xFFF59E0B), 
                                        modifier = Modifier.size(12.dp) 
                                    ) 
                                } 
                                Text( 
                                    text = displayG, 
                                    fontSize = 12.sp, 
                                    fontWeight = if (isTopTheme || isSel) FontWeight.Bold else FontWeight.SemiBold, 
                                    color = if (isSel) Color.White else if (isTopTheme) Color(0xFFFEF3C7) else Color(0xFFD4D4D8) 
                                ) 
                            } 
                        } 
                    } 
                } 
            } 
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                Text( 
                    text = if (storyLanguage == "ja") "使用するAnki単語数" else "Anchor Due Cards", 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = ShinobiNunito, 
                    color = Color.White 
                ) 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    cardCounts.forEach { count -> 
                        val isSel = localCardCount == count 
                        val countLabel = if (count == 0) "0 (Free)" else "$count Words" 
                        ShinobiTactileButton( 
                            onClick = { localCardCount = count }, 
                            modifier = Modifier.weight(1f), 
                            faceColor = if (isSel) Color(0xFFD97706) else Color(0xFF27272A), 
                            lipColor = if (isSel) Color(0xFFB45309) else Color(0xFF3F3F46), 
                            contentColor = if (isSel) Color.White else Color(0xFFA1A1AA), 
                            buttonHeight = 44.dp 
                        ) { 
                            Text( 
                                text = countLabel, 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                fontFamily = ShinobiNunito 
                            ) 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(4.dp)) 
            
            val actionButtonText = if (isOtherSlotForging) { 
                if (storyLanguage == "ja") "キューに追加 (スロット $slotId)" else "ADD TO QUEUE (CHAMBER $slotId)" 
            } else { 
                if (storyLanguage == "ja") "鍛造開始 (スロット $slotId)" else "START FORGING (CHAMBER $slotId)" 
            } 
            val actionFaceColor = if (isOtherSlotForging) Color(0xFF7C3AED) else ShinobiColors.EmeraldGreen 
            val actionLipColor = if (isOtherSlotForging) Color(0xFF6D28D9) else ShinobiColors.EmeraldGreenLip 
            
            ShinobiTactileButton( 
                onClick = { 
                    if (prefs.aiApiKey.isBlank()) { 
                        onOpenAiConfig() 
                        return@ShinobiTactileButton 
                    } 
                    prefs.storyLevel = localLevel 
                    prefs.storyGenre = localGenre 
                    prefs.storyLength = localLength 
                    prefs.incrementThemeUsage(localGenre) 
                    onStartForge(localCardCount, localGenre, localLevel, localLength, localGenerateImage) 
                }, 
                modifier = Modifier.fillMaxWidth(), 
                faceColor = actionFaceColor, 
                lipColor = actionLipColor, 
                contentColor = Color.White, 
                buttonHeight = 52.dp 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Bolt, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.size(22.dp) 
                ) 
                Spacer(modifier = Modifier.width(8.dp)) 
                Text( 
                    text = actionButtonText, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontFamily = ShinobiNunito, 
                    color = Color.White 
                ) 
            } 
        } 
    } 
} 

