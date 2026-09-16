package com.ankilock.ui.reading

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.ankilock.data.StoryWordItem
import com.ankilock.ui.blossom.BlossomStoryTokenView
import com.ankilock.ui.blossom.BlossomWordBottomSheet
import com.ankilock.ui.blossom.story.StoryTokenizer
import com.ankilock.ui.study.WallhavenImagePickerSheet
import com.ankilock.util.ImageBlurUtil
import com.ankilock.util.JapaneseTtsHelper
import androidx.compose.ui.geometry.Offset 
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection 
import androidx.compose.ui.input.nestedscroll.NestedScrollSource 
import androidx.compose.ui.input.nestedscroll.nestedScroll 
import androidx.compose.ui.unit.Velocity 
import java.io.File
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.anki.ReadingVocabularyExtractor
import com.ankilock.data.AnkiVocabularyItem
import com.ankilock.data.GeneratedStory
import com.ankilock.data.PreferencesManager
import com.ankilock.data.ReadingHistoryManager
import com.ankilock.data.ReadingVocabularySummary
import com.ankilock.reading.FishAudioService
import com.ankilock.reading.GeminiStoryService
import com.ankilock.data.StoryThemes 
import com.ankilock.ui.blossom.AppTheme 
import com.ankilock.ui.blossom.BlossomColors 
import com.ankilock.ui.blossom.BlossomShapes 
import com.ankilock.ui.components.Squircle3DButton 
import com.ankilock.ui.components.Squircle3DCard 
import com.ankilock.ui.components.squircleLiquidGlass 
import com.ankilock.ui.blossom.story.StoryToken 
import kotlinx.coroutines.delay 
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import java.text.SimpleDateFormat 
import java.util.Date 
import java.util.Locale 

private data class TokenizedSentence( 
    val globalIndex: Int, 
    val plainText: String, 
    val tokens: List<StoryToken> 
) 

private data class TokenizedParagraph( 
    val sentences: List<TokenizedSentence> 
) 

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) 
@Composable 
fun ReadingScreen( 
    padding: PaddingValues, 
    prefs: PreferencesManager, 
    hasAnkiPermission: Boolean, 
    openHistoryTrigger: Int = 0, 
    onHistoryTriggerConsumed: () -> Unit = {}, 
    onNavigateToJisho: (String) -> Unit = {}, 
    onTopBarStatsChanged: () -> Unit = {} 
) { 
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val historyManager = remember { ReadingHistoryManager(context) } 
    val storyService = remember { GeminiStoryService() } 
    val vocabExtractor = remember { ReadingVocabularyExtractor(context) } 
    val audioService = remember { FishAudioService(context) } 
    val ttsHelper = remember { JapaneseTtsHelper(context) } 
    
    var apiKey by remember { mutableStateOf(prefs.geminiApiKey ?: "") } 
    var selectedJlpt by remember { mutableStateOf(prefs.readingJlptLevel) } 
    var selectedModel by remember { mutableStateOf(prefs.geminiModel) } 
    var selectedLength by remember { mutableStateOf(prefs.storyLength) } 
    var connectingWordsCount by remember { mutableIntStateOf(prefs.storyConnectingWordsCount) } 
    var showApiKeyDialog by remember { mutableStateOf(false) } 
    var showStoryConfigDialog by remember { mutableStateOf(false) } 
    
    var showFishAudioDialog by remember { mutableStateOf(false) } 
    var fishAudioApiKey by remember { mutableStateOf(prefs.fishAudioApiKey ?: "") } 
    var fishAudioVoiceId by remember { mutableStateOf(prefs.fishAudioVoiceId) } 
    var fishAudioVoiceName by remember { mutableStateOf(prefs.fishAudioVoiceName) } 
    var fishAudioModel by remember { mutableStateOf(prefs.fishAudioModel) } 
    var isNarrating by remember { mutableStateOf(false) } 
    var isAudioPaused by remember { mutableStateOf(false) } 
    var isSynthesizingAudio by remember { mutableStateOf(false) } 
    var currentlyPlayingStoryId by remember { mutableStateOf<String?>(null) } 
    var activeSentenceIndex by remember { mutableIntStateOf(-1) } 
    var showQuizOverlay by remember { mutableStateOf(false) } 
    var showFurigana by remember { mutableStateOf(prefs.showFuriganaInReader) } 
    var highlightWords by remember { mutableStateOf(prefs.highlightVocabularyWords) } 
    
    DisposableEffect(Unit) { 
        onDispose { 
            audioService.stopAudio() 
            ttsHelper.stop() 
            ttsHelper.shutdown() 
            activeSentenceIndex = -1 
        } 
    } 
    
    var vocabSummary by remember { mutableStateOf<ReadingVocabularySummary?>(null) } 
    var isLoadingVocab by remember { mutableStateOf(false) } 
    
    var currentStory by remember { mutableStateOf<GeneratedStory?>(null) } 
    var isGeneratingStory by remember { mutableStateOf(false) } 
    var generationError by remember { mutableStateOf<String?>(null) } 
    var showInternetConsentDialog by remember { mutableStateOf(false) } 
    
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } 
    var quizCurrentIndex by remember { mutableIntStateOf(0) } 
    var isQuizCompleted by remember { mutableStateOf(false) } 
    
    LaunchedEffect(currentStory?.id) { 
        userAnswers.clear() 
        quizCurrentIndex = 0 
        isQuizCompleted = false 
        showQuizOverlay = false 
        activeSentenceIndex = -1 
        isAudioPaused = false 
        if (currentlyPlayingStoryId != null && currentlyPlayingStoryId != currentStory?.id) { 
            audioService.stopAudio() 
            ttsHelper.stop() 
            isNarrating = false 
            isAudioPaused = false 
            currentlyPlayingStoryId = null 
        } 
        currentStory?.id?.let { id -> 
            prefs.lastReadStoryId = id 
        } 
    } 
    
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    var showHistorySheet by remember { mutableStateOf(false) } 
    var savedStories by remember { mutableStateOf(historyManager.getStories()) } 
    var showClearAllConfirmation by remember { mutableStateOf(false) } 
    var storyToDelete by remember { mutableStateOf<GeneratedStory?>(null) } 
    
    val historyNoBounceNestedScroll = remember { 
        object : NestedScrollConnection { 
            override fun onPostScroll( 
                consumed: Offset, 
                available: Offset, 
                source: NestedScrollSource 
            ): Offset { 
                return if (available.y < 0f) Offset(0f, available.y) else Offset.Zero 
            } 
            override suspend fun onPostFling( 
                consumed: Velocity, 
                available: Velocity 
            ): Velocity { 
                return Velocity(0f, available.y) 
            } 
        } 
    } 
    
    LaunchedEffect(showHistorySheet) { 
        if (showHistorySheet) { 
            savedStories = historyManager.getStories() 
        } 
    } 
    
    val wordDetailSheetState = rememberModalBottomSheetState() 
    var selectedWordDetail by remember { mutableStateOf<AnkiVocabularyItem?>(null) } 
    
    var showTranslationSheet by remember { mutableStateOf(false) } 
    var translateTargetText by remember { mutableStateOf("") } 
    
    var showWallhavenPicker by remember { mutableStateOf(false) } 
    var wallpaperUpdateTrigger by remember { mutableIntStateOf(0) } 
    var quickJishoWord by remember { mutableStateOf<String?>(null) } 
    
    val currentCoverFile = remember(currentStory?.id, wallpaperUpdateTrigger) { 
        currentStory?.id?.let { id -> 
            val f = File(context.filesDir, "stories/images/$id/cover.png") 
            if (f.exists()) f else null 
        } 
    } 
    val blurRadius = prefs.appBlurRadius 
    var coverBitmap by remember { mutableStateOf<ImageBitmap?>(null) } 
    LaunchedEffect(currentCoverFile, blurRadius) { 
        val file = currentCoverFile 
        if (file == null || !file.exists()) { 
            coverBitmap = null 
        } else { 
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { 
                try { 
                    val raw = BitmapFactory.decodeFile(file.absolutePath) 
                    if (raw != null) { 
                        val blurred = if (blurRadius > 0) { 
                            ImageBlurUtil.fastBlur(raw, 0.25f, blurRadius.coerceIn(1, 60)) 
                        } else raw 
                        coverBitmap = blurred.asImageBitmap() 
                    } else { 
                        coverBitmap = null 
                    } 
                } catch (_: Exception) { 
                    coverBitmap = null 
                } 
            } 
        } 
    } 
    
    LaunchedEffect(openHistoryTrigger) { 
        if (openHistoryTrigger > 0) { 
            savedStories = historyManager.getStories() 
            showHistorySheet = true 
            onHistoryTriggerConsumed() 
        } 
    } 
    
    LaunchedEffect(Unit) { 
        val past = historyManager.getStories() 
        savedStories = past 
        if (past.isNotEmpty() && currentStory == null) { 
            val lastId = prefs.lastReadStoryId 
            val found = if (lastId != null) past.find { it.id == lastId } else null 
            currentStory = found ?: past.first() 
        } 
    } 
    
    fun loadVocabulary() { 
        if (!hasAnkiPermission) return 
        coroutineScope.launch { 
            isLoadingVocab = true 
            val deckIds = prefs.getSelectedDeckIdsAsLongs() 
            vocabSummary = vocabExtractor.extractVocabulary(deckIds) 
            isLoadingVocab = false 
        } 
    } 
    
    LaunchedEffect(hasAnkiPermission, prefs.selectedDeckIds) { 
        loadVocabulary() 
    } 
    
    var isCustomThemeModeActive by remember { mutableStateOf(prefs.isCustomThemeModeActive) } 
    var customStoryTheme by remember { mutableStateOf(prefs.customStoryTheme) } 
    var customStoryTopic by remember { mutableStateOf(prefs.customStoryTopic) } 
    
    fun executeGeneration() { 
        coroutineScope.launch { 
            if (prefs.storyDailyEnergyRemaining <= 0) { 
                generationError = "Daily story energy depleted (0/7). Resets tomorrow!" 
                return@launch 
            } 
            isGeneratingStory = true 
            generationError = null 
            
            val (targetTheme, targetTopic) = if (isCustomThemeModeActive && !customStoryTheme.isNullOrBlank()) { 
                val chosenTheme = customStoryTheme!! 
                val chosenTopic = customStoryTopic ?: run { 
                    val topics = StoryThemes.CATEGORIES[chosenTheme] ?: emptyList() 
                    val eligible = topics.filter { it !in prefs.disabledStoryTopics }.ifEmpty { topics } 
                    if (eligible.isNotEmpty()) eligible.random() else "A memorable event" 
                } 
                Pair(chosenTheme, chosenTopic) 
            } else { 
                StoryThemes.getRandomThemeAndTopic( 
                    disabledThemes = prefs.disabledStoryThemes, 
                    disabledTopics = prefs.disabledStoryTopics 
                ) 
            } 
            
            val count = prefs.storyConnectingWordsCount 
            val allWords = vocabSummary?.words ?: emptyList() 
            val filter = prefs.storyVocabularyFilter 
            val eligibleVocab = if (filter == "all" || filter.isBlank()) { 
                allWords 
            } else { 
                allWords.filter { it.state.equals(filter, ignoreCase = true) } 
            } 
            val words = when { 
                count == -1 && eligibleVocab.isNotEmpty() -> eligibleVocab.shuffled() 
                count > 0 && eligibleVocab.isNotEmpty() -> eligibleVocab.shuffled().take(count) 
                else -> emptyList() 
            } 
            val result = storyService.generateStory( 
                apiKey = apiKey, 
                jlptLevel = selectedJlpt, 
                vocabularyList = words, 
                preferredModel = selectedModel, 
                theme = targetTheme, 
                topic = targetTopic, 
                storyLength = prefs.storyLength, 
                questionsCount = prefs.storyQuestionsCount 
            ) 
            result.onSuccess { story -> 
                val enrichedStory = if (story.targetWordsData.isEmpty() && words.isNotEmpty()) { 
                    story.copy( 
                        targetWordsData = words.map { 
                            StoryWordItem( 
                                kanji = it.kanji.ifBlank { it.reading }, 
                                reading = it.reading, 
                                meaning = it.meaning 
                            ) 
                        } 
                    ) 
                } else { 
                    story 
                } 
                currentStory = enrichedStory 
                historyManager.saveStory(enrichedStory) 
                savedStories = historyManager.getStories() 
                prefs.consumeDailyStoryEnergy() 
                onTopBarStatsChanged() 
            }.onFailure { err -> 
                generationError = err.message ?: "Failed to generate story" 
            } 
            isGeneratingStory = false 
        } 
    } 
    
    val story = currentStory 
    val isDark = BlossomColors.currentTheme != AppTheme.LIGHT 
    val storySentences = remember(story?.content) { 
        story?.content?.split(Regex("(?<=[。！？\n])"))?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList() 
    } 

    fun getSentencePositionMs(targetIndex: Int, durMs: Int): Int { 
        val s = story ?: return 0 
        if (durMs <= 0 || storySentences.isEmpty()) return 0 
        val titleLen = s.title.length + 1 
        val sentenceLens = storySentences.map { it.length } 
        val totalChars = titleLen + sentenceLens.sum() 
        if (totalChars <= 0) return 0 
        val targetChars = if (targetIndex < 0) { 
            0 
        } else { 
            titleLen + sentenceLens.take(targetIndex).sum() 
        } 
        val frac = (targetChars.toFloat() / totalChars.toFloat()).coerceIn(0f, 1f) 
        return (frac * durMs).toInt() 
    } 

    fun startNarration(fromSentenceIdx: Int = -1) { 
        val s = story ?: return 
        audioService.stopAudio() 
        ttsHelper.stop() 
        isNarrating = false 
        isAudioPaused = false 
        currentlyPlayingStoryId = s.id 
        activeSentenceIndex = fromSentenceIdx 

        val currentApiKey = prefs.fishAudioApiKey ?: fishAudioApiKey 
        val currentVoiceId = prefs.fishAudioVoiceId 
        val currentModel = prefs.fishAudioModel 
        if (currentApiKey.isNotBlank()) { 
            isSynthesizingAudio = true 
            coroutineScope.launch { 
                val narrationText = "${s.title}。\n\n${s.content}" 
                val result = audioService.synthesizeStoryAudio( 
                    apiKey = currentApiKey, 
                    voiceId = currentVoiceId, 
                    model = currentModel, 
                    storyId = s.id, 
                    text = narrationText 
                ) 
                isSynthesizingAudio = false 
                result.onSuccess { audioFile -> 
                    isNarrating = true 
                    isAudioPaused = false 
                    currentlyPlayingStoryId = s.id 
                    audioService.playAudio( 
                        file = audioFile, 
                        onPlaybackStateChanged = { playing -> 
                            if (!isAudioPaused) { 
                                isNarrating = playing 
                            } 
                            if (playing) { 
                                currentlyPlayingStoryId = s.id 
                            } else if (currentlyPlayingStoryId == s.id && !isAudioPaused) { 
                                currentlyPlayingStoryId = null 
                                activeSentenceIndex = -1 
                            } 
                        }, 
                        onCompletion = { 
                            isNarrating = false 
                            isAudioPaused = false 
                            if (currentlyPlayingStoryId == s.id) { 
                                currentlyPlayingStoryId = null 
                                activeSentenceIndex = -1 
                            } 
                        } 
                    ) 
                    if (fromSentenceIdx >= 0) { 
                        delay(200) 
                        val dur = audioService.getDurationMs() 
                        val targetMs = getSentencePositionMs(fromSentenceIdx, dur) 
                        audioService.seekTo(targetMs) 
                        activeSentenceIndex = fromSentenceIdx 
                    } 
                }.onFailure { error -> 
                    isNarrating = false 
                    isAudioPaused = false 
                    currentlyPlayingStoryId = null 
                    activeSentenceIndex = -1 
                    Toast.makeText(context, "Narration error: ${error.message ?: "Failed to generate audio"}", Toast.LENGTH_LONG).show() 
                } 
            } 
        } else { 
            val narrationText = if (fromSentenceIdx < 0) { 
                "${s.title}。\n\n${s.content}" 
            } else { 
                storySentences.drop(fromSentenceIdx).joinToString("") 
            } 
            isNarrating = true 
            isAudioPaused = false 
            currentlyPlayingStoryId = s.id 
            ttsHelper.speak( 
                text = narrationText, 
                onRangeStart = { start, _ -> 
                    if (fromSentenceIdx < 0) { 
                        val titleLen = s.title.length + 1 
                        if (start < titleLen) { 
                            activeSentenceIndex = -1 
                        } else { 
                            var cum = titleLen 
                            var found = 0 
                            for (i in storySentences.indices) { 
                                cum += storySentences[i].length 
                                if (start <= cum) { 
                                    found = i 
                                    break 
                                } 
                                found = i 
                            } 
                            activeSentenceIndex = found 
                        } 
                    } else { 
                        val subSentences = storySentences.drop(fromSentenceIdx) 
                        var cum = 0 
                        var found = fromSentenceIdx 
                        for (i in subSentences.indices) { 
                            cum += subSentences[i].length 
                            if (start <= cum) { 
                                found = fromSentenceIdx + i 
                                break 
                            } 
                            found = fromSentenceIdx + i 
                        } 
                        activeSentenceIndex = found 
                    } 
                }, 
                onStart = { 
                    isNarrating = true 
                    isAudioPaused = false 
                    currentlyPlayingStoryId = s.id 
                }, 
                onDone = { 
                    isNarrating = false 
                    isAudioPaused = false 
                    activeSentenceIndex = -1 
                    if (currentlyPlayingStoryId == s.id) { 
                        currentlyPlayingStoryId = null 
                    } 
                }, 
                onError = { 
                    isNarrating = false 
                    isAudioPaused = false 
                    activeSentenceIndex = -1 
                    if (currentlyPlayingStoryId == s.id) { 
                        currentlyPlayingStoryId = null 
                    } 
                } 
            ) 
        } 
    } 

    fun pauseNarration() { 
        if (audioService.getDurationMs() > 0) { 
            audioService.pauseAudio() 
        } else { 
            ttsHelper.stop() 
        } 
        isAudioPaused = true 
        isNarrating = false 
    } 

    fun resumeNarration() { 
        if (audioService.getDurationMs() > 0) { 
            audioService.resumeAudio() 
            isAudioPaused = false 
            isNarrating = true 
        } else { 
            val startIdx = activeSentenceIndex.coerceAtLeast(0) 
            startNarration(startIdx) 
        } 
    } 

    fun stopNarration() { 
        audioService.stopAudio() 
        ttsHelper.stop() 
        isNarrating = false 
        isAudioPaused = false 
        isSynthesizingAudio = false 
        currentlyPlayingStoryId = null 
        activeSentenceIndex = -1 
    } 

    fun seekToSentence(targetIdx: Int) { 
        if (story == null || storySentences.isEmpty()) return 
        val clamped = targetIdx.coerceIn(0, storySentences.size - 1) 
        activeSentenceIndex = clamped 
        if (audioService.getDurationMs() > 0) { 
            val dur = audioService.getDurationMs() 
            val targetMs = getSentencePositionMs(clamped, dur) 
            audioService.seekTo(targetMs) 
            if (isAudioPaused) { 
                audioService.resumeAudio() 
                isAudioPaused = false 
                isNarrating = true 
            } 
        } else { 
            ttsHelper.stop() 
            startNarration(clamped) 
        } 
    } 

    fun skipToNextSentence() { 
        if (storySentences.isEmpty()) return 
        val nextIdx = (activeSentenceIndex + 1).coerceAtMost(storySentences.size - 1) 
        seekToSentence(nextIdx) 
    } 

    fun rewindToPreviousSentence() { 
        if (storySentences.isEmpty()) return 
        val prevIdx = (activeSentenceIndex - 1).coerceAtLeast(0) 
        seekToSentence(prevIdx) 
    } 

    Box(modifier = Modifier.fillMaxSize()) { 
        val currentBitmap = coverBitmap 
        if (currentBitmap != null) { 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(520.dp) 
                    .background(BlossomColors.BackgroundDeep) 
            ) { 
                Image( 
                    bitmap = currentBitmap, 
                    contentDescription = null, 
                    contentScale = ContentScale.Crop, 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .graphicsLayer { alpha = prefs.appArtworkOpacity.coerceIn(0.05f, 1f) } 
                ) 
                if (prefs.appDimOpacity > 0f) { 
                    Box( 
                        modifier = Modifier 
                            .fillMaxSize() 
                            .background(Color.Black.copy(alpha = prefs.appDimOpacity.coerceIn(0f, 0.95f))) 
                    ) 
                } 
                Box( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .background( 
                            Brush.verticalGradient( 
                                colorStops = arrayOf( 
                                    0.0f to Color(0xCC000000), 
                                    0.18f to Color(0x66000000), 
                                    0.32f to Color.Transparent, 
                                    0.55f to BlossomColors.BackgroundDeep.copy(alpha = 0.40f), 
                                    0.80f to BlossomColors.BackgroundDeep.copy(alpha = 0.85f), 
                                    1.0f to BlossomColors.BackgroundDeep 
                                ) 
                            ) 
                        ) 
                ) 
            } 
        } 

        CustomSelectionContainer( 
            onTranslate = { selectedText -> 
                translateTargetText = selectedText 
                showTranslationSheet = true 
            }, 
            onJisho = { selectedWord -> 
                quickJishoWord = selectedWord 
            } 
        ) { 
            SelectionContainer { 
                Column( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .verticalScroll(rememberScrollState()) 
                        .padding(horizontal = 16.dp), 
                    verticalArrangement = Arrangement.spacedBy(16.dp) 
                ) { 
                    Spacer(modifier = Modifier.height(padding.calculateTopPadding())) 
                    DisableSelection { 
                        Box( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .squircleLiquidGlass( 
                    shape = BlossomShapes.SquircleLarge, 
                    cornerRadius = 20.dp, 
                    backgroundColor = if (coverBitmap != null) { 
                        if (isDark) Color(0xFF141721).copy(alpha = 0.65f) 
                        else Color.White.copy(alpha = 0.72f) 
                    } else BlossomColors.SurfaceCard1, 
                    specularAlpha = if (coverBitmap != null) 0.50f else 0.20f, 
                    borderAlpha = if (coverBitmap != null) 0.45f else 0.25f, 
                    shadowElevation = 0.dp, 
                    hasTopGloss = coverBitmap != null 
                ) 
        ) { 
            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp, vertical = 14.dp), 
                verticalArrangement = Arrangement.spacedBy(10.dp) 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Text( 
                        text = "Story Immersion", 
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    
                    IconButton( 
                        onClick = { 
                            savedStories = historyManager.getStories() 
                            showHistorySheet = true 
                        }, 
                        modifier = Modifier.size(36.dp) 
                    ) { 
                        Icon( 
                            Icons.Filled.History, 
                            contentDescription = "Stories History", 
                            tint = if (savedStories.isNotEmpty()) BlossomColors.SakuraRose else BlossomColors.TextSecondary, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                    } 
                } 
                
                val currentThemeName = if (isCustomThemeModeActive && !customStoryTheme.isNullOrBlank()) { 
                    StoryThemes.formatThemeName(customStoryTheme!!) 
                } else { 
                    "Random Theme" 
                } 
                val wordsDesc = when (connectingWordsCount) { 
                    0 -> "0 connecting words (Free story)" 
                    -1 -> "ALL connecting cards" 
                    else -> "$connectingWordsCount connecting cards" 
                } 
                
                Surface( 
                    onClick = { showStoryConfigDialog = true }, 
                    shape = RoundedCornerShape(14.dp), 
                    color = BlossomColors.SurfaceElevated, 
                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Row( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(horizontal = 14.dp, vertical = 11.dp), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        Column(modifier = Modifier.weight(1f)) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(6.dp) 
                            ) { 
                                Surface( 
                                    shape = RoundedCornerShape(6.dp), 
                                    color = BlossomColors.SakuraRoseContainer, 
                                    border = BorderStroke(1.dp, BlossomColors.SakuraRose.copy(alpha = 0.5f)) 
                                ) { 
                                    Text( 
                                        text = selectedJlpt, 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = BlossomColors.SakuraRose, 
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                                    ) 
                                } 
                                Text( 
                                    text = "•", 
                                    fontSize = 12.sp, 
                                    color = BlossomColors.TextSecondary 
                                ) 
                                Text( 
                                    text = selectedLength, 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Medium, 
                                    color = BlossomColors.TextPrimary 
                                ) 
                                Text( 
                                    text = "•", 
                                    fontSize = 12.sp, 
                                    color = BlossomColors.TextSecondary 
                                ) 
                                Text( 
                                    text = currentThemeName, 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Medium, 
                                    color = BlossomColors.TextPrimary, 
                                    maxLines = 1, 
                                    overflow = TextOverflow.Ellipsis 
                                ) 
                            } 
                            Spacer(modifier = Modifier.height(4.dp)) 
                            Text( 
                                text = wordsDesc, 
                                fontSize = 11.sp, 
                                color = BlossomColors.TextSecondary 
                            ) 
                        } 
                        
                        Row(verticalAlignment = Alignment.CenterVertically) { 
                            Text( 
                                text = "Tune", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = BlossomColors.SakuraRose 
                            ) 
                            Icon( 
                                Icons.Filled.ArrowDropDown, 
                                contentDescription = null, 
                                tint = BlossomColors.SakuraRose, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                        } 
                    } 
                } 
            } 
        } 

        if (apiKey.isBlank()) { 
            Card( 
                shape = RoundedCornerShape(20.dp), 
                colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceElevated), 
                border = BorderStroke(1.dp, BlossomColors.WisteriaViolet.copy(alpha = 0.35f)) 
            ) { 
                Column(modifier = Modifier.padding(18.dp)) { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = BlossomColors.WisteriaViolet) 
                        Spacer(modifier = Modifier.width(10.dp)) 
                        Text( 
                            text = "GEMINI API KEY REQUIRED", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.WisteriaViolet, 
                            letterSpacing = 1.sp 
                        ) 
                    } 
                    Spacer(modifier = Modifier.height(8.dp)) 
                    Text( 
                        text = "To generate personalized Japanese reading stories based on your Anki flashcards, connect your free Google Gemini API key.", 
                        fontSize = 13.sp, 
                        color = BlossomColors.TextSecondary, 
                        lineHeight = 18.sp 
                    ) 
                    Spacer(modifier = Modifier.height(14.dp)) 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        Button( 
                            onClick = { showApiKeyDialog = true }, 
                            colors = ButtonDefaults.buttonColors( 
                                containerColor = BlossomColors.SakuraRose, 
                                contentColor = BlossomColors.BlossomWhite 
                            ), 
                            shape = RoundedCornerShape(12.dp), 
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp), 
                            modifier = Modifier.weight(1f) 
                        ) { 
                            Text("Enter API Key", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, maxLines = 1, softWrap = false) 
                        } 
                        OutlinedButton( 
                            onClick = { 
                                val url = "https://aistudio.google.com/app/apikey" 
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)) 
                                context.startActivity(intent) 
                            }, 
                            shape = RoundedCornerShape(12.dp), 
                            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp), 
                            modifier = Modifier.weight(1f) 
                        ) { 
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp), tint = BlossomColors.TextPrimary) 
                            Spacer(modifier = Modifier.width(6.dp)) 
                            Text("Get Free Key", color = BlossomColors.TextPrimary, fontSize = 12.5.sp, maxLines = 1, softWrap = false) 
                        } 
                    } 
                } 
            } 
        } 
    } 

        if (story != null) { 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .squircleLiquidGlass( 
                        shape = RoundedCornerShape(26.dp), 
                        cornerRadius = 26.dp, 
                        backgroundColor = if (coverBitmap != null) { 
                            if (isDark) Color(0xFF141721).copy(alpha = 0.70f) 
                            else Color.White.copy(alpha = 0.78f) 
                        } else BlossomColors.SurfaceCard1, 
                        specularAlpha = if (coverBitmap != null) 0.50f else 0.20f, 
                        borderAlpha = if (coverBitmap != null) 0.45f else 0.25f, 
                        shadowElevation = 0.dp, 
                        hasTopGloss = coverBitmap != null 
                    ) 
            ) { 
                Column(modifier = Modifier.padding(22.dp)) { 
                    DisableSelection { 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.SpaceBetween 
                        ) { 
                            Surface( 
                                shape = RoundedCornerShape(8.dp), 
                                color = BlossomColors.SurfaceElevated, 
                                border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                                modifier = Modifier.clickable { showStoryConfigDialog = true } 
                            ) { 
                                Text( 
                                    text = "JLPT ${story.jlptLevel}", 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = BlossomColors.SakuraRose, 
                                    maxLines = 1, 
                                    softWrap = false, 
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp) 
                                ) 
                            } 

                            Row(verticalAlignment = Alignment.CenterVertically) { 
                                val isCurrentStoryPlaying = isNarrating && currentlyPlayingStoryId == story.id 
                                val isCurrentStoryPaused = isAudioPaused && currentlyPlayingStoryId == story.id 
                                val isCurrentStorySynthesizing = isSynthesizingAudio && currentlyPlayingStoryId == story.id 

                                IconButton( 
                                    onClick = { 
                                        if (isCurrentStoryPlaying || isCurrentStoryPaused || isCurrentStorySynthesizing) { 
                                            stopNarration() 
                                        } else { 
                                            startNarration() 
                                        } 
                                    } 
                                ) { 
                                    if (isCurrentStorySynthesizing) { 
                                        CircularProgressIndicator( 
                                            modifier = Modifier.size(18.dp), 
                                            strokeWidth = 2.dp, 
                                            color = BlossomColors.SakuraRose 
                                        ) 
                                    } else if (isCurrentStoryPlaying || isCurrentStoryPaused) { 
                                        Icon( 
                                            Icons.Filled.Stop, 
                                            contentDescription = "Stop Narration", 
                                            tint = BlossomColors.BlossomRed, 
                                            modifier = Modifier.size(20.dp) 
                                        ) 
                                    } else { 
                                        Icon( 
                                            Icons.AutoMirrored.Filled.VolumeUp, 
                                            contentDescription = "Narrate Story", 
                                            tint = BlossomColors.TextSecondary, 
                                            modifier = Modifier.size(20.dp) 
                                        ) 
                                    } 
                                } 

                                IconButton( 
                                    onClick = { 
                                        translateTargetText = story.content 
                                        showTranslationSheet = true 
                                    } 
                                ) { 
                                    Icon( 
                                        Icons.Filled.Translate, 
                                        contentDescription = "Translate Story", 
                                        tint = BlossomColors.TextSecondary, 
                                        modifier = Modifier.size(20.dp) 
                                    ) 
                                } 

                                IconButton( 
                                    onClick = { 
                                        showFurigana = !showFurigana 
                                        prefs.showFuriganaInReader = showFurigana 
                                    } 
                                ) { 
                                    Text( 
                                        text = "ふ", 
                                        fontSize = 17.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = if (showFurigana) BlossomColors.SakuraRose else BlossomColors.TextSecondary 
                                    ) 
                                } 

                                IconButton( 
                                    onClick = { 
                                        highlightWords = !highlightWords 
                                        prefs.highlightVocabularyWords = highlightWords 
                                    } 
                                ) { 
                                    Icon( 
                                        Icons.Filled.AutoAwesome, 
                                        contentDescription = "Toggle Vocabulary Highlight", 
                                        tint = if (highlightWords) BlossomColors.SakuraRose else BlossomColors.TextSecondary, 
                                        modifier = Modifier.size(20.dp) 
                                    ) 
                                } 

                                IconButton( 
                                    onClick = { 
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager 
                                        val clip = ClipData.newPlainText("Japanese Story", "${story.title}\n\n${story.content}") 
                                        clipboard.setPrimaryClip(clip) 
                                        Toast.makeText(context, "Story copied to clipboard!", Toast.LENGTH_SHORT).show() 
                                    } 
                                ) { 
                                    Icon( 
                                        Icons.Filled.ContentCopy, 
                                        contentDescription = "Copy Story", 
                                        tint = BlossomColors.TextSecondary, 
                                        modifier = Modifier.size(20.dp) 
                                    ) 
                                } 

                                IconButton( 
                                    onClick = { showWallhavenPicker = true } 
                                ) { 
                                    Icon( 
                                        Icons.Filled.Wallpaper, 
                                        contentDescription = "Change Wallpaper", 
                                        tint = if (coverBitmap != null) BlossomColors.SakuraRose else BlossomColors.TextSecondary, 
                                        modifier = Modifier.size(20.dp) 
                                    ) 
                                } 
                            } 
                        } 
                    } 

                    DisableSelection { 
                        AnimatedVisibility( 
                            visible = (isNarrating || isAudioPaused || isSynthesizingAudio) && currentlyPlayingStoryId == story.id, 
                            enter = fadeIn() + expandVertically(), 
                            exit = fadeOut() + shrinkVertically() 
                        ) { 
                            Surface( 
                                shape = RoundedCornerShape(14.dp), 
                                color = BlossomColors.SurfaceElevated, 
                                border = BorderStroke(1.dp, BlossomColors.SakuraRose.copy(alpha = 0.35f)), 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .padding(top = 10.dp) 
                            ) { 
                                Row( 
                                    modifier = Modifier 
                                        .fillMaxWidth() 
                                        .padding(horizontal = 12.dp, vertical = 8.dp), 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.SpaceBetween 
                                ) { 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.spacedBy(8.dp), 
                                        modifier = Modifier.weight(1f, fill = false) 
                                    ) { 
                                        Surface( 
                                            shape = CircleShape, 
                                            color = BlossomColors.SakuraRoseContainer, 
                                            modifier = Modifier.size(28.dp) 
                                        ) { 
                                            Box(contentAlignment = Alignment.Center) { 
                                                if (isSynthesizingAudio) { 
                                                    CircularProgressIndicator( 
                                                        modifier = Modifier.size(14.dp), 
                                                        strokeWidth = 2.dp, 
                                                        color = BlossomColors.SakuraRose 
                                                    ) 
                                                } else { 
                                                    Icon( 
                                                        Icons.Filled.GraphicEq, 
                                                        contentDescription = null, 
                                                        tint = BlossomColors.SakuraRose, 
                                                        modifier = Modifier.size(14.dp) 
                                                    ) 
                                                } 
                                            } 
                                        } 
                                        
                                        Column { 
                                            Text( 
                                                text = if (isSynthesizingAudio) "Generating audio..." else if (isAudioPaused) "Narration Paused" else "Playing Narration", 
                                                fontSize = 11.5.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                color = BlossomColors.TextPrimary, 
                                                maxLines = 1, 
                                                softWrap = false 
                                            ) 
                                            val sentenceStatus = if (activeSentenceIndex < 0) "Title" else "Sentence ${activeSentenceIndex + 1} / ${storySentences.size}" 
                                            Text( 
                                                text = sentenceStatus, 
                                                fontSize = 10.5.sp, 
                                                color = BlossomColors.TextSecondary, 
                                                maxLines = 1, 
                                                softWrap = false 
                                            ) 
                                        } 
                                    } 
                                    
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.spacedBy(2.dp) 
                                    ) { 
                                        IconButton( 
                                            onClick = { rewindToPreviousSentence() }, 
                                            enabled = !isSynthesizingAudio && activeSentenceIndex > 0, 
                                            modifier = Modifier.size(30.dp) 
                                        ) { 
                                            Icon( 
                                                Icons.Filled.SkipPrevious, 
                                                contentDescription = "Previous sentence", 
                                                tint = if (!isSynthesizingAudio && activeSentenceIndex > 0) BlossomColors.TextPrimary else BlossomColors.TextMuted, 
                                                modifier = Modifier.size(19.dp) 
                                            ) 
                                        } 
                                        
                                        IconButton( 
                                            onClick = { 
                                                if (isNarrating) { 
                                                    pauseNarration() 
                                                } else if (isAudioPaused) { 
                                                    resumeNarration() 
                                                } 
                                            }, 
                                            enabled = !isSynthesizingAudio, 
                                            modifier = Modifier.size(32.dp) 
                                        ) { 
                                            Surface( 
                                                shape = CircleShape, 
                                                color = BlossomColors.SakuraRose, 
                                                modifier = Modifier.fillMaxSize() 
                                            ) { 
                                                Box(contentAlignment = Alignment.Center) { 
                                                    Icon( 
                                                        imageVector = if (isNarrating) Icons.Filled.Pause else Icons.Filled.PlayArrow, 
                                                        contentDescription = if (isNarrating) "Pause" else "Play", 
                                                        tint = BlossomColors.BlossomWhite, 
                                                        modifier = Modifier.size(18.dp) 
                                                    ) 
                                                } 
                                            } 
                                        } 
                                        
                                        IconButton( 
                                            onClick = { skipToNextSentence() }, 
                                            enabled = !isSynthesizingAudio && activeSentenceIndex < storySentences.size - 1, 
                                            modifier = Modifier.size(30.dp) 
                                        ) { 
                                            Icon( 
                                                Icons.Filled.SkipNext, 
                                                contentDescription = "Next sentence", 
                                                tint = if (!isSynthesizingAudio && activeSentenceIndex < storySentences.size - 1) BlossomColors.TextPrimary else BlossomColors.TextMuted, 
                                                modifier = Modifier.size(19.dp) 
                                            ) 
                                        } 
                                        
                                        IconButton( 
                                            onClick = { stopNarration() }, 
                                            modifier = Modifier.size(30.dp) 
                                        ) { 
                                            Icon( 
                                                Icons.Filled.Stop, 
                                                contentDescription = "Stop narration", 
                                                tint = BlossomColors.BlossomRed, 
                                                modifier = Modifier.size(19.dp) 
                                            ) 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 

                        val targetWordItems = remember(story.targetWords, story.targetWordsData, vocabSummary?.words) { 
                            if (story.targetWordsData.isNotEmpty()) { 
                                story.targetWordsData 
                            } else { 
                                story.targetWords.map { tw -> 
                                    val v = vocabSummary?.words?.find { 
                                        it.displayWord.equals(tw, ignoreCase = true) || 
                                        it.kanji.equals(tw, ignoreCase = true) || 
                                        it.reading.equals(tw, ignoreCase = true) 
                                    } 
                                    StoryWordItem( 
                                        kanji = v?.kanji?.ifBlank { tw } ?: tw, 
                                        reading = v?.reading ?: "", 
                                        meaning = v?.meaning ?: "" 
                                    ) 
                                } 
                            } 
                        } 

                        val precomputedParagraphTokens = remember(story.id, story.content, story.furiganaContent, targetWordItems) { 
                            val rawContentParas = story.content.split(Regex("\n+")).map { it.trim() }.filter { it.isNotBlank() } 
                            val furiganaSentences = (story.furiganaContent ?: story.content).split(Regex("(?<=[。！？\n])")).map { it.trim() }.filter { it.isNotBlank() } 
                            val hasMatchingFurigana = furiganaSentences.size == storySentences.size 

                            var globalIdx = 0 
                            rawContentParas.map { paraText -> 
                                val sList = mutableListOf<TokenizedSentence>() 
                                var searchPos = 0 
                                while (globalIdx < storySentences.size) { 
                                    val sText = storySentences[globalIdx] 
                                    val idx = paraText.indexOf(sText, searchPos) 
                                    if (idx != -1) { 
                                        val fText = if (hasMatchingFurigana) furiganaSentences[globalIdx] else sText 
                                        val rawTokens = StoryTokenizer.tokenizeToStoryTokens(fText, targetWordItems) 
                                        val resolvedTokens = rawTokens.map { token -> 
                                            val isTargetMatch = token.isTarget || 
                                                story.targetWords.any { tw -> 
                                                    token.surface.isNotBlank() && ( 
                                                        tw.equals(token.surface, ignoreCase = true) || 
                                                        (tw.length >= 2 && token.surface.contains(tw)) || 
                                                        (token.surface.length >= 2 && tw.contains(token.surface)) 
                                                    ) 
                                                } || 
                                                story.targetWordsData.any { td -> 
                                                    token.surface.isNotBlank() && td.kanji.equals(token.surface, ignoreCase = true) 
                                                } 
                                            if (token.isTarget != isTargetMatch) token.copy(isTarget = isTargetMatch) else token 
                                        } 
                                        sList.add( 
                                            TokenizedSentence( 
                                                globalIndex = globalIdx, 
                                                plainText = sText, 
                                                tokens = resolvedTokens 
                                            ) 
                                        ) 
                                        searchPos = idx + sText.length 
                                        globalIdx++ 
                                    } else { 
                                        break 
                                    } 
                                } 
                                TokenizedParagraph(sentences = sList) 
                            } 
                        } 

                        LaunchedEffect(isNarrating, isAudioPaused, currentlyPlayingStoryId, story.id) { 
                            if ((isNarrating || isAudioPaused) && currentlyPlayingStoryId == story.id) { 
                                val titleLen = story.title.length + 1 
                                val sentenceLens = storySentences.map { it.length } 
                                val totalChars = titleLen + sentenceLens.sum() 
                                while ((isNarrating || isAudioPaused) && currentlyPlayingStoryId == story.id) { 
                                    if (isNarrating && !isAudioPaused) { 
                                        val dur = audioService.getDurationMs() 
                                        val pos = audioService.getCurrentPositionMs() 
                                        if (dur > 0 && pos >= 0) { 
                                            val frac = (pos.toFloat() / dur.toFloat()).coerceIn(0f, 1f) 
                                            val targetChar = (frac * totalChars).toInt() 
                                            if (targetChar < titleLen) { 
                                                activeSentenceIndex = -1 
                                            } else { 
                                                var cum = titleLen 
                                                var found = 0 
                                                for (i in sentenceLens.indices) { 
                                                    cum += sentenceLens[i] 
                                                    if (targetChar <= cum) { 
                                                        found = i 
                                                        break 
                                                    } 
                                                    found = i 
                                                } 
                                                activeSentenceIndex = found 
                                            } 
                                        } 
                                    } 
                                    delay(60) 
                                } 
                            } else if (!isAudioPaused) { 
                                activeSentenceIndex = -1 
                            } 
                        } 

                        Spacer(modifier = Modifier.height(16.dp)) 

                        val isTitleActive = (isNarrating || isAudioPaused) && currentlyPlayingStoryId == story.id && activeSentenceIndex == -1 
                        Text( 
                                    text = story.title, 
                                    fontSize = 22.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = BlossomColors.TextPrimary, 
                                    lineHeight = 30.sp, 
                                    modifier = Modifier 
                                        .fillMaxWidth() 
                                        .clip(RoundedCornerShape(8.dp)) 
                                        .background(if (isTitleActive) BlossomColors.SakuraRose.copy(alpha = 0.20f) else Color.Transparent) 
                                        .padding(horizontal = 6.dp, vertical = 4.dp) 
                                ) 
                                
                                Spacer(modifier = Modifier.height(16.dp)) 

                                val targetVocabWords = remember(story.targetWords, story.targetWordsData, vocabSummary?.words, highlightWords) { 
                                    if (!highlightWords || (story.targetWords.isEmpty() && story.targetWordsData.isEmpty())) { 
                                        emptyList() 
                                    } else if (story.targetWordsData.isNotEmpty()) { 
                                        story.targetWordsData.map { 
                                            AnkiVocabularyItem( 
                                                kanji = it.kanji, 
                                                reading = it.reading, 
                                                meaning = it.meaning 
                                            ) 
                                        } 
                                    } else { 
                                        val found = vocabSummary?.words?.filter { wordItem -> 
                                            story.targetWords.any { tw -> 
                                                wordItem.displayWord.equals(tw, ignoreCase = true) || 
                                                wordItem.kanji.equals(tw, ignoreCase = true) || 
                                                wordItem.reading.equals(tw, ignoreCase = true) 
                                            } 
                                        } ?: emptyList() 
                                        if (found.isNotEmpty()) found 
                                        else story.targetWords.map { AnkiVocabularyItem(kanji = it) } 
                                    } 
                                } 

                                if (!showFurigana) { 
                                    val isSelectionActive = LocalSelectionActive.current 
                                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) { 
                                        precomputedParagraphTokens.forEach { para -> 
                                            val paraText = remember(para) { para.sentences.joinToString("") { it.plainText } } 
                                            val activeSent = if ((isNarrating || isAudioPaused) && currentlyPlayingStoryId == story.id) { 
                                                para.sentences.find { it.globalIndex == activeSentenceIndex } 
                                            } else null 
                                            val activeRange = remember(activeSent, paraText) { 
                                                activeSent?.let { sent -> 
                                                    val start = paraText.indexOf(sent.plainText) 
                                                    if (start != -1) Pair(start, start + sent.plainText.length) else null 
                                                } 
                                            } 
                                            val annotatedParagraph = remember(paraText, targetVocabWords, activeRange, BlossomColors.currentTheme) { 
                                                buildHighlightedStoryText( 
                                                    content = paraText, 
                                                    vocabWords = targetVocabWords, 
                                                    activeSentenceRange = activeRange 
                                                ) 
                                            } 
                                            var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) } 
                                            Text( 
                                                text = annotatedParagraph, 
                                                fontSize = 17.sp, 
                                                color = BlossomColors.TextPrimary, 
                                                lineHeight = 28.sp, 
                                                letterSpacing = 0.5.sp, 
                                                onTextLayout = { textLayoutResult = it }, 
                                                modifier = Modifier.pointerInput(targetVocabWords, isSelectionActive) { 
                                                    awaitEachGesture { 
                                                        val down = awaitFirstDown(requireUnconsumed = false) 
                                                        val up = waitForUpOrCancellation() 
                                                        if (up != null && !up.isConsumed) { 
                                                            val diff = up.position - down.position 
                                                            val isTap = (diff.x * diff.x + diff.y * diff.y) < (viewConfiguration.touchSlop * viewConfiguration.touchSlop) 
                                                            if (isTap) { 
                                                                if (!isSelectionActive) { 
                                                                    textLayoutResult?.let { layout -> 
                                                                        val position = layout.getOffsetForPosition(up.position) 
                                                                        val wordAnnotation = annotatedParagraph.getStringAnnotations(tag = "WORD", start = position, end = position).firstOrNull() 
                                                                        if (wordAnnotation != null) { 
                                                                            val word = targetVocabWords.find { it.displayWord == wordAnnotation.item } 
                                                                            if (word != null) { 
                                                                                up.consume() 
                                                                                selectedWordDetail = word 
                                                                            } 
                                                                        } 
                                                                    } 
                                                                } 
                                                            } 
                                                        } 
                                                    } 
                                                } 
                                            ) 
                                        } 
                                    } 
                                } else { 
                                    DisableSelection { 
                                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) { 
                                        precomputedParagraphTokens.forEach { para -> 
                                            FlowRow( 
                                                modifier = Modifier.fillMaxWidth(), 
                                                horizontalArrangement = Arrangement.Start, 
                                                verticalArrangement = Arrangement.Center 
                                            ) { 
                                                para.sentences.forEach { sItem -> 
                                                    val isSentenceActive = (isNarrating || isAudioPaused) && currentlyPlayingStoryId == story.id && activeSentenceIndex == sItem.globalIndex 
                                                    sItem.tokens.forEach { token -> 
                                                        val isTarget = highlightWords && token.isTarget 
                                                        BlossomStoryTokenView( 
                                                            token = token, 
                                                            showPronunciation = true, 
                                                            pronunciationType = "japanese", 
                                                            isAudioHighlighted = isSentenceActive, 
                                                            isSelected = isTarget, 
                                                            onClick = { 
                                                                if (token.surface.isNotBlank()) { 
                                                                    val item = story.targetWordsData.find { 
                                                                        it.kanji == token.surface || it.surface == token.surface || 
                                                                        (it.kanji.any { c -> StoryTokenizer.isKanji(c) } && 
                                                                            it.kanji.filter { c -> StoryTokenizer.isKanji(c) } == token.surface.filter { c -> StoryTokenizer.isKanji(c) }) 
                                                                    }?.let { 
                                                                        AnkiVocabularyItem(kanji = it.kanji, reading = it.reading, meaning = it.meaning) 
                                                                    } ?: vocabSummary?.words?.find { it.displayWord == token.surface || it.kanji == token.surface } 
                                                                      ?: AnkiVocabularyItem( 
                                                                          kanji = token.surface, 
                                                                          reading = token.segments.joinToString("") { it.ruby ?: it.text }, 
                                                                          meaning = token.meaning 
                                                                      ) 
                                                                    selectedWordDetail = item 
                                                                } 
                                                            } 
                                                        ) 
                                                    } 
                                                } 
                                            } 
                                        } 
                                    } 
                                } 
                                } 
                                
                                DisableSelection { 
                                    if (story.targetWords.isNotEmpty()) { 
                                    Spacer(modifier = Modifier.height(20.dp)) 
                                    Surface( 
                                        shape = RoundedCornerShape(14.dp), 
                                        color = BlossomColors.SurfaceElevated, 
                                        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                                        modifier = Modifier.fillMaxWidth() 
                                    ) { 
                                                Column(modifier = Modifier.padding(14.dp)) { 
                                                    Row(verticalAlignment = Alignment.CenterVertically) { 
                                                        Icon( 
                                                            Icons.Filled.School, 
                                                            contentDescription = null, 
                                                            tint = BlossomColors.SakuraRose, 
                                                            modifier = Modifier.size(16.dp) 
                                                        ) 
                                                        Spacer(modifier = Modifier.width(8.dp)) 
                                                        Text( 
                                                            text = "TARGET WORDS FROM YOUR CARDS (${story.targetWords.size})", 
                                                            fontSize = 11.sp, 
                                                            fontWeight = FontWeight.Bold, 
                                                            color = BlossomColors.TextSecondary, 
                                                            letterSpacing = 0.5.sp 
                                                        ) 
                                                    } 
                                                    Spacer(modifier = Modifier.height(10.dp)) 
                                                    FlowRow( 
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp), 
                                                        verticalArrangement = Arrangement.spacedBy(6.dp), 
                                                        modifier = Modifier.fillMaxWidth() 
                                                    ) { 
                                                        story.targetWords.forEach { word -> 
                                                            val isPresent = story.content.contains(word) 
                                                            val matchedItem = story.targetWordsData.find { it.kanji == word || it.surface == word }?.let { 
                                                                AnkiVocabularyItem(kanji = it.kanji, reading = it.reading, meaning = it.meaning) 
                                                            } ?: vocabSummary?.words?.find { it.displayWord == word || it.kanji == word } 
                                                            Surface( 
                                                                shape = RoundedCornerShape(8.dp), 
                                                                color = if (isPresent) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceCard1, 
                                                                border = BorderStroke( 
                                                                    1.dp, 
                                                                    if (isPresent) BlossomColors.SakuraRose.copy(alpha = 0.5f) else BlossomColors.CardBorderSubtle 
                                                                ), 
                                                                onClick = { 
                                                                    matchedItem?.let { selectedWordDetail = it } 
                                                                } 
                                                            ) { 
                                                                Row( 
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                                                                    verticalAlignment = Alignment.CenterVertically 
                                                                ) { 
                                                                    Text( 
                                                                        text = word, 
                                                                        fontSize = 12.sp, 
                                                                        fontWeight = if (isPresent) FontWeight.Bold else FontWeight.Normal, 
                                                                        color = if (isPresent) BlossomColors.SakuraRose else BlossomColors.TextSecondary, 
                                                                        maxLines = 1, 
                                                                        softWrap = false 
                                                                    ) 
                                                                    if (isPresent) { 
                                                                        Spacer(modifier = Modifier.width(4.dp)) 
                                                                        Icon( 
                                                                            Icons.Filled.Check, 
                                                                            contentDescription = "Used in story", 
                                                                            tint = BlossomColors.BlossomGreen, 
                                                                            modifier = Modifier.size(12.dp) 
                                                                        ) 
                                                                    } 
                                                                } 
                                                            } 
                                                        } 
                                                    } 
                                                } 
                                            } 
                                        } 
                                        
                                        if (story.questions.isNotEmpty()) { 
                                            Spacer(modifier = Modifier.height(20.dp)) 
                                            Squircle3DButton( 
                                                onClick = { showQuizOverlay = true }, 
                                                modifier = Modifier 
                                                    .fillMaxWidth() 
                                                    .height(50.dp), 
                                                containerColor = BlossomColors.SakuraRose, 
                                                bevelColor = BlossomColors.SakuraRoseLip, 
                                                shape = BlossomShapes.SquircleMedium, 
                                                depth = 3.dp 
                                            ) { 
                                                Row( 
                                                    verticalAlignment = Alignment.CenterVertically, 
                                                    horizontalArrangement = Arrangement.Center 
                                                ) { 
                                                    Icon( 
                                                        Icons.Filled.AutoAwesome, 
                                                        contentDescription = null, 
                                                        tint = Color.White, 
                                                        modifier = Modifier.size(18.dp) 
                                                    ) 
                                                    Spacer(modifier = Modifier.width(8.dp)) 
                                                    Text( 
                                                        text = "Take Test (${story.questions.size})", 
                                                        fontSize = 15.sp, 
                                                        fontWeight = FontWeight.SemiBold, 
                                                        color = Color.White 
                                                    ) 
                                                } 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                        } 
        
        if (generationError != null) { 
            Card( 
                shape = RoundedCornerShape(16.dp), 
                colors = CardDefaults.cardColors(containerColor = BlossomColors.SakuraRoseContainer), 
                border = BorderStroke(1.dp, BlossomColors.BlossomRed.copy(alpha = 0.4f)) 
            ) { 
                Row( 
                    modifier = Modifier.padding(14.dp), 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = BlossomColors.BlossomRed) 
                    Spacer(modifier = Modifier.width(10.dp)) 
                    Text( 
                        text = generationError ?: "Error generating story", 
                        fontSize = 13.sp, 
                        color = BlossomColors.BlossomRed 
                    ) 
                } 
            } 
        } 
        
        val isLockedWithoutKey = apiKey.isBlank() 
        val buttonBackground = when { 
            isLockedWithoutKey -> BlossomColors.SurfaceElevated 
            isGeneratingStory -> BlossomColors.SakuraRose.copy(alpha = 0.40f) 
            else -> BlossomColors.SakuraRose 
        } 
        val buttonBevel = when { 
            isLockedWithoutKey -> BlossomColors.CardBorder 
            isGeneratingStory -> BlossomColors.SakuraRoseLip.copy(alpha = 0.40f) 
            else -> BlossomColors.SakuraRoseLip 
        } 
        
        Squircle3DButton( 
            onClick = { 
                if (apiKey.isBlank()) { 
                    showApiKeyDialog = true 
                } else if (!prefs.hasAcceptedInternetDisclosure) { 
                    showInternetConsentDialog = true 
                } else if (prefs.storyDailyEnergyRemaining <= 0) { 
                    generationError = "Daily story energy depleted (0/7). Resets tomorrow!" 
                } else { 
                    executeGeneration() 
                } 
            }, 
            enabled = !isGeneratingStory, 
            modifier = Modifier 
                .fillMaxWidth() 
                .height(50.dp), 
            containerColor = buttonBackground, 
            bevelColor = buttonBevel, 
            shape = BlossomShapes.SquircleMedium, 
            depth = 3.dp, 
            hasSweepingShine = !isLockedWithoutKey && !isGeneratingStory 
        ) { 
            AnimatedContent( 
                targetState = when { 
                    isGeneratingStory -> 0 
                    isLockedWithoutKey -> 1 
                    else -> 2 
                }, 
                transitionSpec = { 
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(150)) 
                }, 
                label = "StoryButtonStateTransition" 
            ) { state -> 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.Center, 
                    modifier = Modifier.padding(horizontal = 16.dp) 
                ) { 
                    when (state) { 
                        0 -> { 
                            CircularProgressIndicator( 
                                modifier = Modifier.size(18.dp), 
                                strokeWidth = 2.dp, 
                                color = BlossomColors.BlossomWhite 
                            ) 
                            Spacer(modifier = Modifier.width(10.dp)) 
                            RotatingStatusText( 
                                phrases = listOf( 
                                    "Crafting $selectedJlpt story...", 
                                    "Weaving the plot...", 
                                    "Polishing details...", 
                                    "Adding some flair...", 
                                    "Fine-tuning emotions...", 
                                    "Almost there..." 
                                ), 
                                isGenerating = isGeneratingStory, 
                                color = BlossomColors.BlossomWhite 
                            ) 
                        } 
                        1 -> { 
                            Icon( 
                                Icons.Filled.Lock, 
                                contentDescription = "API Key Required", 
                                modifier = Modifier.size(18.dp), 
                                tint = BlossomColors.BlossomRed 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = "Set up API Key to Generate", 
                                fontWeight = FontWeight.SemiBold, 
                                fontSize = 15.sp, 
                                color = BlossomColors.BlossomRed, 
                                maxLines = 1, 
                                softWrap = false 
                            ) 
                        } 
                        else -> { 
                            Icon( 
                                Icons.Filled.AutoAwesome, 
                                contentDescription = null, 
                                modifier = Modifier.size(18.dp), 
                                tint = BlossomColors.BlossomWhite 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = if (currentStory == null) "Generate $selectedJlpt Story (${prefs.storyDailyEnergyRemaining}/7)" else "Generate Another Story (${prefs.storyDailyEnergyRemaining}/7)", 
                                fontWeight = FontWeight.SemiBold, 
                                fontSize = 15.sp, 
                                color = BlossomColors.BlossomWhite, 
                                maxLines = 1, 
                                softWrap = false 
                            ) 
                        } 
                    } 
                } 
            } 
        } 

        if (story == null && !isGeneratingStory) { 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(vertical = 32.dp), 
                contentAlignment = Alignment.Center 
            ) { 
                Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                    Icon( 
                        Icons.Filled.School, 
                        contentDescription = null, 
                        tint = BlossomColors.SakuraRose, 
                        modifier = Modifier.size(48.dp) 
                    ) 
                    Spacer(modifier = Modifier.height(12.dp)) 
                    Text( 
                        text = "No story generated yet", 
                        color = BlossomColors.TextSecondary, 
                        fontSize = 14.sp 
                    ) 
                    Text( 
                        text = "Pick your JLPT level and tap Generate Story above", 
                        color = BlossomColors.TextMuted, 
                        fontSize = 12.sp 
                    ) 
                } 
            } 
        } 

        Spacer(modifier = Modifier.height(padding.calculateBottomPadding() + 32.dp)) 
                } 
            } 
        } 
    } 
    
    if (showHistorySheet) { 
        ModalBottomSheet( 
            onDismissRequest = { showHistorySheet = false }, 
            sheetState = historySheetState, 
            containerColor = BlossomColors.SurfaceCard1, 
            contentColor = BlossomColors.TextPrimary 
        ) { 
            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .fillMaxHeight(0.88f) 
                    .padding(horizontal = 20.dp, vertical = 12.dp) 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Column { 
                        Text( 
                            text = "Stories History (${savedStories.size})", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        val totalStorageBytes = remember(savedStories) { historyManager.getTotalStorageBytes() } 
                        Text( 
                            text = "Storage: ${historyManager.formatStorageSize(totalStorageBytes)}", 
                            fontSize = 12.sp, 
                            color = BlossomColors.TextSecondary 
                        ) 
                    } 
                    if (savedStories.isNotEmpty()) { 
                        TextButton( 
                            onClick = { 
                                showClearAllConfirmation = true 
                            } 
                        ) { 
                            Text("Clear All", color = BlossomColors.BlossomRed, fontSize = 13.sp, maxLines = 1, softWrap = false) 
                        } 
                    } 
                } 
                
                Spacer(modifier = Modifier.height(12.dp)) 
                
                if (savedStories.isEmpty()) { 
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(vertical = 40.dp), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Text("No saved stories yet", color = BlossomColors.TextMuted, fontSize = 14.sp) 
                    } 
                } else { 
                    LazyColumn( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .weight(1f) 
                            .nestedScroll(historyNoBounceNestedScroll), 
                        verticalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        items(savedStories, key = { it.id }) { item -> 
                            val itemCoverFile = remember(item.id, wallpaperUpdateTrigger) { 
                                val f = File(context.filesDir, "stories/images/${item.id}/cover.png") 
                                if (f.exists()) f else null 
                            } 
                            val itemBitmap = remember(itemCoverFile) { 
                                itemCoverFile?.let { f -> 
                                    try { 
                                        BitmapFactory.decodeFile(f.absolutePath)?.asImageBitmap() 
                                    } catch (e: Exception) { 
                                        null 
                                    } 
                                } 
                            } 

                            val itemStorage = remember(item.id) { historyManager.getStoryStorageBytes(item) } 
                            val itemStorageFormatted = remember(itemStorage) { historyManager.formatStorageSize(itemStorage) } 

                            Squircle3DCard( 
                                onClick = { 
                                    currentStory = item 
                                    coroutineScope.launch { 
                                        historySheetState.hide() 
                                        showHistorySheet = false 
                                    } 
                                }, 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .height(135.dp), 
                                shape = RoundedCornerShape(16.dp), 
                                containerColor = BlossomColors.SurfaceElevated, 
                                borderBrush = androidx.compose.ui.graphics.SolidColor( 
                                    if (item.isPassed) BlossomColors.BlossomGreen else BlossomColors.CardBorder 
                                ), 
                                depth = 3.dp 
                            ) { 
                                Box(modifier = Modifier.fillMaxSize()) { 
                                    if (itemBitmap != null) { 
                                        Image( 
                                            bitmap = itemBitmap, 
                                            contentDescription = null, 
                                            contentScale = ContentScale.Crop, 
                                            modifier = Modifier.fillMaxSize() 
                                        ) 
                                        Box( 
                                            modifier = Modifier 
                                                .fillMaxSize() 
                                                .background( 
                                                    Brush.verticalGradient( 
                                                        colors = listOf( 
                                                            Color.Black.copy(alpha = 0.50f), 
                                                            Color.Black.copy(alpha = 0.85f) 
                                                        ) 
                                                    ) 
                                                ) 
                                        ) 
                                    } 

                                    Row( 
                                        modifier = Modifier 
                                            .fillMaxSize() 
                                            .padding(14.dp), 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.SpaceBetween 
                                    ) { 
                                        Column( 
                                            modifier = Modifier 
                                                .weight(1f) 
                                                .fillMaxHeight(), 
                                            verticalArrangement = Arrangement.SpaceBetween 
                                        ) { 
                                            Row(verticalAlignment = Alignment.CenterVertically) { 
                                                Surface( 
                                                    shape = RoundedCornerShape(6.dp), 
                                                    color = BlossomColors.SakuraRoseContainer 
                                                ) { 
                                                    Text( 
                                                        text = item.jlptLevel, 
                                                        fontSize = 10.sp, 
                                                        fontWeight = FontWeight.Bold, 
                                                        color = BlossomColors.SakuraRose, 
                                                        maxLines = 1, 
                                                        softWrap = false, 
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                                                    ) 
                                                } 
                                                if (item.isPassed) { 
                                                    Spacer(modifier = Modifier.width(6.dp)) 
                                                    Surface( 
                                                        shape = RoundedCornerShape(6.dp), 
                                                        color = BlossomColors.BlossomGreenSurface, 
                                                        border = BorderStroke(1.dp, BlossomColors.BlossomGreen.copy(alpha = 0.5f)) 
                                                    ) { 
                                                        Text( 
                                                            text = if (item.quizScore != null) "Passed (${item.quizScore}%)" else "Passed", 
                                                            fontSize = 10.sp, 
                                                            fontWeight = FontWeight.Bold, 
                                                            color = BlossomColors.BlossomGreen, 
                                                            maxLines = 1, 
                                                            softWrap = false, 
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                                                        ) 
                                                    } 
                                                } 
                                                if (!item.theme.isNullOrBlank()) { 
                                                    Spacer(modifier = Modifier.width(6.dp)) 
                                                    val themeBadge = StoryThemes.getThemeBadgeColors(item.theme) 
                                                    Surface( 
                                                        shape = RoundedCornerShape(6.dp), 
                                                        color = themeBadge.backgroundColor, 
                                                        border = BorderStroke(1.dp, themeBadge.borderColor) 
                                                    ) { 
                                                        Text( 
                                                            text = StoryThemes.formatThemeName(item.theme), 
                                                            fontSize = 10.sp, 
                                                            fontWeight = FontWeight.Bold, 
                                                            color = themeBadge.contentColor, 
                                                            maxLines = 1, 
                                                            softWrap = false, 
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                                                        ) 
                                                    } 
                                                } 
                                                Spacer(modifier = Modifier.width(8.dp)) 
                                                val dateFormatted = remember(item.createdAt) { 
                                                    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(item.createdAt)) 
                                                } 
                                                Text( 
                                                    text = "$dateFormatted • $itemStorageFormatted", 
                                                    fontSize = 11.sp, 
                                                    color = if (itemBitmap != null) Color.White.copy(alpha = 0.75f) else BlossomColors.TextSecondary, 
                                                    maxLines = 1, 
                                                    softWrap = false 
                                                ) 
                                            } 
                                            Text( 
                                                text = item.title, 
                                                fontWeight = FontWeight.SemiBold, 
                                                fontSize = 15.sp, 
                                                color = if (itemBitmap != null) Color.White else BlossomColors.TextPrimary, 
                                                maxLines = 2, 
                                                overflow = TextOverflow.Ellipsis 
                                            ) 
                                        } 
                                        Spacer(modifier = Modifier.width(8.dp)) 
                                        Row(verticalAlignment = Alignment.CenterVertically) { 
                                            IconButton( 
                                                onClick = { 
                                                    historyManager.togglePin(item.id) 
                                                    savedStories = historyManager.getStories() 
                                                } 
                                            ) { 
                                                Icon( 
                                                    if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, 
                                                    contentDescription = if (item.isPinned) "Unpin Story" else "Pin Story", 
                                                    tint = if (item.isPinned) BlossomColors.SakuraRose else (if (itemBitmap != null) Color.White.copy(alpha = 0.75f) else BlossomColors.TextSecondary) 
                                                ) 
                                            } 
                                            IconButton( 
                                                onClick = { 
                                                    storyToDelete = item 
                                                } 
                                            ) { 
                                                Icon( 
                                                    Icons.Filled.DeleteOutline, 
                                                    contentDescription = "Delete Story", 
                                                    tint = BlossomColors.BlossomRed 
                                                ) 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    if (showClearAllConfirmation) { 
        androidx.compose.ui.window.Dialog(onDismissRequest = { showClearAllConfirmation = false }) { 
            Card( 
                shape = RoundedCornerShape(20.dp), 
                colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
                border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                Column( 
                    modifier = Modifier.padding(22.dp), 
                    verticalArrangement = Arrangement.spacedBy(14.dp) 
                ) { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        Icon( 
                            Icons.Filled.DeleteOutline, 
                            contentDescription = null, 
                            tint = BlossomColors.BlossomRed, 
                            modifier = Modifier.size(24.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(10.dp)) 
                        Text( 
                            text = "Clear Stories History?", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 18.sp, 
                            color = BlossomColors.TextPrimary 
                        ) 
                    } 
                    
                    Text( 
                        text = "Are you sure you want to clear all stories history? This will permanently delete all saved stories.", 
                        fontSize = 13.sp, 
                        color = BlossomColors.TextSecondary, 
                        lineHeight = 20.sp 
                    ) 
                    
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        OutlinedButton( 
                            onClick = { showClearAllConfirmation = false }, 
                            shape = RoundedCornerShape(12.dp), 
                            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                            modifier = Modifier.weight(1f), 
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp) 
                        ) { 
                            Text("Cancel", color = BlossomColors.TextSecondary, maxLines = 1, softWrap = false) 
                        } 
                        
                        Button( 
                            onClick = { 
                                historyManager.clearAll() 
                                savedStories = emptyList() 
                                showClearAllConfirmation = false 
                            }, 
                            shape = RoundedCornerShape(12.dp), 
                            colors = ButtonDefaults.buttonColors( 
                                containerColor = BlossomColors.BlossomRed, 
                                contentColor = BlossomColors.BlossomWhite 
                            ), 
                            modifier = Modifier.weight(1f), 
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp) 
                        ) { 
                            Text("Clear All", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    if (storyToDelete != null) { 
        val targetStory = storyToDelete!! 
        androidx.compose.ui.window.Dialog(onDismissRequest = { storyToDelete = null }) { 
            Card( 
                shape = RoundedCornerShape(20.dp), 
                colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
                border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                Column( 
                    modifier = Modifier.padding(22.dp), 
                    verticalArrangement = Arrangement.spacedBy(14.dp) 
                ) { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        Icon( 
                            Icons.Filled.DeleteOutline, 
                            contentDescription = null, 
                            tint = BlossomColors.BlossomRed, 
                            modifier = Modifier.size(24.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(10.dp)) 
                        Text( 
                            text = "Delete Story?", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 18.sp, 
                            color = BlossomColors.TextPrimary 
                        ) 
                    } 
                    
                    Text( 
                        text = "Are you sure you want to delete \"${targetStory.title}\"? This action cannot be undone.", 
                        fontSize = 13.sp, 
                        color = BlossomColors.TextSecondary, 
                        lineHeight = 20.sp 
                    ) 
                    
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        OutlinedButton( 
                            onClick = { storyToDelete = null }, 
                            shape = RoundedCornerShape(12.dp), 
                            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                            modifier = Modifier.weight(1f), 
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp) 
                        ) { 
                            Text("Cancel", color = BlossomColors.TextSecondary, maxLines = 1, softWrap = false) 
                        } 
                        
                        Button( 
                            onClick = { 
                                historyManager.deleteStory(targetStory.id) 
                                savedStories = historyManager.getStories() 
                                if (currentStory?.id == targetStory.id) { 
                                    currentStory = savedStories.firstOrNull() 
                                } 
                                storyToDelete = null 
                            }, 
                            shape = RoundedCornerShape(12.dp), 
                            colors = ButtonDefaults.buttonColors( 
                                containerColor = BlossomColors.BlossomRed, 
                                contentColor = BlossomColors.BlossomWhite 
                            ), 
                            modifier = Modifier.weight(1f), 
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp) 
                        ) { 
                            Text("Delete", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    if (selectedWordDetail != null) { 
        WordDetailBottomSheet( 
            item = selectedWordDetail!!, 
            sheetState = wordDetailSheetState, 
            onDismiss = { 
                coroutineScope.launch { 
                    wordDetailSheetState.hide()
                    selectedWordDetail = null 
                } 
            }, 
            onNavigateToJisho = onNavigateToJisho 
        )
    } 
    
    
    if (showTranslationSheet && translateTargetText.isNotBlank()) { 
        val matchingFurigana = remember(translateTargetText, currentStory?.furiganaContent) { 
            val fContent = currentStory?.furiganaContent 
            if (!fContent.isNullOrBlank()) { 
                StoryTokenizer.findMatchingFuriganaSlice(translateTargetText, fContent) 
            } else { 
                null 
            } 
        } 
        TranslationBottomSheet( 
            sourceText = translateTargetText, 
            furiganaSource = matchingFurigana, 
            targetWords = currentStory?.targetWordsData ?: emptyList(), 
            onDismiss = { 
                showTranslationSheet = false 
                translateTargetText = "" 
            }, 
            onNavigateToJisho = onNavigateToJisho 
        ) 
    } 
    
    
    if (showApiKeyDialog) { 
        ApiKeySetupDialog( 
            currentKey = apiKey, 
            onSave = { newKey ->
                apiKey = newKey 
                prefs.geminiApiKey = newKey 
                showApiKeyDialog = false 
                Toast.makeText(context, "Gemini API key saved!", Toast.LENGTH_SHORT).show()
            }, 
            onDismiss = { showApiKeyDialog = false } 
        )
    } 
    
    if (showFishAudioDialog) { 
        FishAudioDialog( 
            currentApiKey = fishAudioApiKey, 
            currentVoiceId = fishAudioVoiceId, 
            currentVoiceName = fishAudioVoiceName, 
            currentModel = fishAudioModel, 
            onSave = { newKey, newVoiceId, newVoiceName, newModel ->
                fishAudioApiKey = newKey 
                prefs.fishAudioApiKey = newKey 
                fishAudioVoiceId = newVoiceId 
                prefs.fishAudioVoiceId = newVoiceId 
                fishAudioVoiceName = newVoiceName 
                prefs.fishAudioVoiceName = newVoiceName 
                fishAudioModel = newModel 
                prefs.fishAudioModel = newModel 
                showFishAudioDialog = false 
                Toast.makeText(context, "Fish Audio configuration saved!", Toast.LENGTH_SHORT).show()
            }, 
            onDismiss = { showFishAudioDialog = false } 
        )
    } 
    
    if (showInternetConsentDialog) { 
        InternetAccessDisclosureDialog( 
            onConfirm = { 
                prefs.hasAcceptedInternetDisclosure = true 
                showInternetConsentDialog = false 
                executeGeneration() 
            }, 
            onDismiss = { showInternetConsentDialog = false } 
        ) 
    } 
    
    if (showStoryConfigDialog) { 
        StoryConfigBottomSheet( 
            prefs = prefs, 
            onDismiss = { showStoryConfigDialog = false }, 
            onSaved = { 
                selectedJlpt = prefs.readingJlptLevel 
                selectedLength = prefs.storyLength 
                connectingWordsCount = prefs.storyConnectingWordsCount 
                customStoryTheme = prefs.customStoryTheme 
                customStoryTopic = prefs.customStoryTopic 
                isCustomThemeModeActive = prefs.isCustomThemeModeActive 
                showStoryConfigDialog = false 
            } 
        ) 
    } 
    
    if (showQuizOverlay && currentStory != null) { 
        ComprehensionQuizOverlay( 
            questions = currentStory!!.questions, 
            userAnswers = userAnswers, 
            currentIndex = quizCurrentIndex, 
            onCurrentIndexChange = { quizCurrentIndex = it }, 
            isQuizCompleted = isQuizCompleted, 
            onQuizCompletedChange = { isQuizCompleted = it }, 
            onNavigateToJisho = { word -> 
                showQuizOverlay = false 
                onNavigateToJisho(word) 
            }, 
            onDismiss = { showQuizOverlay = false }, 
            onQuizSubmitted = { score, isPassed -> 
                val story = currentStory 
                if (story != null) { 
                    historyManager.recordQuizResult(story.id, score, isPassed) 
                    if (isPassed) { 
                        prefs.markStoryPassed(story.id) 
                    } 
                    currentStory = story.copy(quizScore = score, isPassed = story.isPassed || isPassed) 
                    savedStories = historyManager.getStories() 
                    onTopBarStatsChanged() 
                } 
            } 
        ) 
    } 
    
    if (showWallhavenPicker && currentStory != null) { 
        val curStory = currentStory!! 
        WallhavenImagePickerSheet( 
            storyId = curStory.id, 
            initialTitle = curStory.title, 
            initialGenre = curStory.theme ?: "Anime", 
            onDismiss = { showWallhavenPicker = false }, 
            onImageSelected = { 
                wallpaperUpdateTrigger++ 
                showWallhavenPicker = false 
            } 
        ) 
    } 
    
    if (quickJishoWord != null) { 
        val wordText = quickJishoWord!! 
        val existingItem = currentStory?.targetWordsData?.find { it.kanji == wordText || it.surface == wordText } 
        val itemToDisplay = existingItem ?: StoryWordItem( 
            kanji = wordText, 
            reading = "", 
            meaning = "" 
        ) 
        BlossomWordBottomSheet( 
            wordItem = itemToDisplay, 
            isBookmarked = false, 
            showFurigana = showFurigana, 
            onToggleFurigana = { 
                showFurigana = !showFurigana 
                prefs.showFuriganaInReader = showFurigana 
            }, 
            onToggleBookmark = {}, 
            onPlayAudio = { 
                quickJishoWord?.let { ttsHelper.speak(it) } 
            }, 
            onDismiss = { quickJishoWord = null } 
        ) 
    } 
} 

private fun buildHighlightedStoryText( 
    content: String, 
    vocabWords: List<AnkiVocabularyItem>, 
    activeSentenceRange: Pair<Int, Int>? = null 
): AnnotatedString { 
    if (content.isEmpty()) { 
        return AnnotatedString(content) 
    } 
    
    if (vocabWords.isEmpty()) { 
        return buildAnnotatedString { 
            append(content) 
            if (activeSentenceRange != null) { 
                addStyle( 
                    style = SpanStyle( 
                        background = BlossomColors.SakuraRose.copy(alpha = 0.28f) 
                    ), 
                    start = activeSentenceRange.first.coerceIn(0, content.length), 
                    end = activeSentenceRange.second.coerceIn(0, content.length) 
                ) 
            } 
        } 
    } 
    
    val validWords = vocabWords 
        .filter { it.displayWord.isNotBlank() && (it.displayWord.length >= 2 || it.kanji.isNotBlank()) } 
        .distinctBy { it.displayWord } 
        .sortedByDescending { it.displayWord.length } 
        
    data class RangeMatch(val start: Int, val end: Int, val item: AnkiVocabularyItem) 
    val matches = mutableListOf<RangeMatch>() 
    val occupied = BooleanArray(content.length) 
    
    for (item in validWords) { 
        val word = item.displayWord 
        var searchFrom = 0 
        while (searchFrom < content.length) { 
            val idx = content.indexOf(word, searchFrom) 
            if (idx == -1) break 
            val endIdx = idx + word.length 
            var isFree = true 
            for (i in idx until endIdx) { 
                if (occupied[i]) { 
                    isFree = false 
                    break 
                } 
            } 
            if (isFree) { 
                for (i in idx until endIdx) { 
                    occupied[i] = true 
                } 
                matches.add(RangeMatch(idx, endIdx, item)) 
            } 
            searchFrom = idx + 1 
        } 
    } 
    
    matches.sortBy { it.start } 
    
    return buildAnnotatedString { 
        append(content) 
        if (activeSentenceRange != null) { 
            addStyle( 
                style = SpanStyle( 
                    background = BlossomColors.SakuraRose.copy(alpha = 0.28f) 
                ), 
                start = activeSentenceRange.first.coerceIn(0, content.length), 
                end = activeSentenceRange.second.coerceIn(0, content.length) 
            ) 
        } 
        for (match in matches) { 
            addStyle( 
                style = SpanStyle( 
                    background = BlossomColors.SakuraRoseContainer, 
                    color = BlossomColors.SakuraRose, 
                    fontWeight = FontWeight.Bold 
                ), 
                start = match.start, 
                end = match.end 
            ) 
            addStringAnnotation( 
                tag = "WORD", 
                annotation = match.item.displayWord, 
                start = match.start, 
                end = match.end 
            ) 
        } 
    } 
} 

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordDetailBottomSheet( 
    item: AnkiVocabularyItem, 
    sheetState: androidx.compose.material3.SheetState, 
    onDismiss: () -> Unit, 
    onNavigateToJisho: (String) -> Unit = {} 
) { 
    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = BlossomColors.SurfaceCard1, 
        contentColor = BlossomColors.TextPrimary 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp)
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Surface( 
                    shape = RoundedCornerShape(8.dp), 
                    color = if (item.isSuspended) BlossomColors.BlossomAmberSurface else BlossomColors.SakuraRoseContainer, 
                    border = BorderStroke( 
                        1.dp, 
                        if (item.isSuspended) BlossomColors.BlossomAmber.copy(alpha = 0.5f)
                        else BlossomColors.SakuraRose.copy(alpha = 0.5f)
                    )
                ) { 
                    Text( 
                        text = if (item.isSuspended) "Suspended Card" else "Studied Card", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = if (item.isSuspended) BlossomColors.BlossomAmber else BlossomColors.SakuraRose, 
                        maxLines = 1, 
                        softWrap = false, 
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                } 
                
                IconButton(onClick = onDismiss) { 
                    Icon( 
                        Icons.Filled.Close, 
                        contentDescription = "Close", 
                        tint = BlossomColors.TextSecondary 
                    )
                } 
            } 
            
            Spacer(modifier = Modifier.height(16.dp))
            
            
            Text( 
                text = item.displayWord, 
                fontSize = 32.sp, 
                fontWeight = FontWeight.Bold, 
                color = BlossomColors.TextPrimary 
            )
            
            
            if (item.reading.isNotBlank() && item.reading != item.kanji) { 
                Spacer(modifier = Modifier.height(4.dp))
                Text( 
                    text = item.reading, 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = BlossomColors.SakuraRose 
                )
            } 
            
            Spacer(modifier = Modifier.height(16.dp))
            
            
            if (item.meaning.isNotBlank()) { 
                Surface( 
                    shape = RoundedCornerShape(14.dp), 
                    color = BlossomColors.SurfaceElevated, 
                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                    modifier = Modifier.fillMaxWidth()
                ) { 
                    Column(modifier = Modifier.padding(16.dp)) { 
                        Text( 
                            text = "ENGLISH MEANING", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextMuted, 
                            letterSpacing = 0.8.sp 
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text( 
                            text = item.meaning, 
                            fontSize = 15.sp, 
                            color = BlossomColors.TextPrimary, 
                            lineHeight = 22.sp 
                        )
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Squircle3DButton( 
                onClick = { 
                    onDismiss() 
                    onNavigateToJisho(item.displayWord) 
                }, 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(48.dp), 
                containerColor = BlossomColors.SakuraRose, 
                bevelColor = BlossomColors.SakuraRoseLip, 
                contentColor = Color.White, 
                shape = BlossomShapes.SquircleMedium, 
                depth = 3.dp 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.Center 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Search, 
                        contentDescription = null, 
                        tint = Color.White, 
                        modifier = Modifier.size(18.dp) 
                    ) 
                    Spacer(modifier = Modifier.width(8.dp)) 
                    Text( 
                        text = "Look up in Jisho", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = Color.White 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(28.dp))
        } 
    } 
} 

@Composable
fun ApiKeySetupDialog( 
    currentKey: String, 
    onSave: (String) -> Unit, 
    onDismiss: () -> Unit 
) { 
    var keyInput by remember { mutableStateOf(currentKey) } 
    val context = LocalContext.current
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) { 
        Card( 
            shape = RoundedCornerShape(20.dp), 
            colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
            modifier = Modifier.fillMaxWidth()
        ) { 
            Column( 
                modifier = Modifier.padding(22.dp), 
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Icon(Icons.Filled.Key, contentDescription = null, tint = BlossomColors.SakuraRose)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text( 
                        text = "Gemini API Key", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp, 
                        color = BlossomColors.TextPrimary 
                    )
                } 
                
                Text( 
                    text = "Your key is stored strictly on your device. It is used solely to generate reading immersion stories.", 
                    fontSize = 13.sp, 
                    color = BlossomColors.TextSecondary, 
                    lineHeight = 18.sp 
                )
                
                OutlinedTextField( 
                    value = keyInput, 
                    onValueChange = { keyInput = it }, 
                    placeholder = { Text("AIzaSy...", color = BlossomColors.TextMuted) }, 
                    singleLine = true, 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedTextColor = BlossomColors.TextPrimary, 
                        unfocusedTextColor = BlossomColors.TextPrimary, 
                        focusedBorderColor = BlossomColors.SakuraRose, 
                        unfocusedBorderColor = BlossomColors.CardBorder, 
                        focusedContainerColor = BlossomColors.SurfaceElevated, 
                        unfocusedContainerColor = BlossomColors.SurfaceElevated 
                    ), 
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    TextButton( 
                        onClick = { 
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                            context.startActivity(intent)
                        }, 
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) { 
                        Row(verticalAlignment = Alignment.CenterVertically) { 
                            Text("Get Free Key", fontSize = 12.sp, color = BlossomColors.SakuraRose, maxLines = 1, softWrap = false)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = BlossomColors.SakuraRose, modifier = Modifier.size(14.dp))
                        } 
                    } 
                    
                    if (keyInput.isNotBlank()) { 
                        TextButton( 
                            onClick = { keyInput = "" }, 
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) { 
                            Text("Clear", fontSize = 12.sp, color = BlossomColors.BlossomRed, maxLines = 1, softWrap = false)
                        } 
                    } 
                } 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) { 
                    OutlinedButton( 
                        onClick = onDismiss, 
                        shape = RoundedCornerShape(12.dp), 
                        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                        modifier = Modifier.weight(1f), 
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) { 
                        Text("Cancel", color = BlossomColors.TextSecondary, maxLines = 1, softWrap = false)
                    } 
                    
                    Button( 
                        onClick = { onSave(keyInput.trim()) }, 
                        shape = RoundedCornerShape(12.dp), 
                        colors = ButtonDefaults.buttonColors( 
                            containerColor = BlossomColors.SakuraRose, 
                            contentColor = BlossomColors.BlossomWhite 
                        ), 
                        modifier = Modifier.weight(1f), 
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) { 
                        Text("Save", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    } 
                } 
            } 
        } 
    } 
} 

@Composable
fun InternetAccessDisclosureDialog( 
    onConfirm: () -> Unit, 
    onDismiss: () -> Unit 
) { 
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) { 
        Card( 
            shape = RoundedCornerShape(20.dp), 
            colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
            modifier = Modifier.fillMaxWidth()
        ) { 
            Column( 
                modifier = Modifier.padding(22.dp), 
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Icon( 
                        Icons.Filled.Public, 
                        contentDescription = null, 
                        tint = BlossomColors.SakuraRose, 
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text( 
                        text = "Enable Internet for AI?", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp, 
                        color = BlossomColors.TextPrimary 
                    )
                } 
                
                Text( 
                    text = "Blossom is 100% offline for flashcards and widgets.\n\nAI Reading connects directly to Google's Gemini API with your private API key to compose custom Japanese stories.\n\nOnly vocabulary from your cards is sent for prompt generation. No personal data, passwords, or tracking telemetry are ever sent.", 
                    fontSize = 13.sp, 
                    color = BlossomColors.TextSecondary, 
                    lineHeight = 20.sp 
                )
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) { 
                    OutlinedButton( 
                        onClick = onDismiss, 
                        shape = RoundedCornerShape(12.dp), 
                        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                        modifier = Modifier.weight(1f), 
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) { 
                        Text("Cancel", color = BlossomColors.TextSecondary, maxLines = 1, softWrap = false)
                    } 
                    
                    Button( 
                        onClick = onConfirm, 
                        shape = RoundedCornerShape(12.dp), 
                        colors = ButtonDefaults.buttonColors( 
                            containerColor = BlossomColors.SakuraRose, 
                            contentColor = BlossomColors.BlossomWhite 
                        ), 
                        modifier = Modifier.weight(1f), 
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                    ) { 
                        Text("Allow & Continue", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    } 
                } 
            } 
        } 
    } 
} 

private fun getJlptLabel(level: String): String { 
    return when (level) { 
        "N5" -> "(Beginner)" 
        "N4" -> "(Upper Beginner)" 
        "N3" -> "(Intermediate)" 
        "N2" -> "(Pre-Advanced)" 
        "N1" -> "(Advanced)" 
        else -> "" 
    } 
} 
