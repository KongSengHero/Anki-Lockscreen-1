package com.ankilock.ui.blossom.story
    
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.ForgedStory
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StoryQuestion
import com.ankilock.data.StoryWordItem
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomNunito
import com.ankilock.ui.blossom.BlossomShapes 
import com.ankilock.ui.blossom.BlossomSoundEffects 
import com.ankilock.ui.blossom.BlossomStoryTokenView 
import com.ankilock.ui.blossom.BlossomTactileButton
import com.ankilock.util.AudioPlayerHelper
    
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BlossomExercisesScreen( 
    story: ForgedStory, 
    onCompleteQuiz: (correctCount: Int, totalCount: Int) -> Unit, 
    onExitQuiz: () -> Unit, 
    audioPlayer: AudioPlayerHelper? = null, 
    modifier: Modifier = Modifier 
) { 
    val context = LocalContext.current 
    val prefs = remember { PreferencesManager(context) } 
    val questions = story.questions 
    if (questions.isEmpty()) { 
        onCompleteQuiz(0, 0) 
        return 
    } 
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) } 
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) } 
    var isSubmitted by remember { mutableStateOf(false) } 
    var correctAnswersCount by remember { mutableIntStateOf(0) } 
    var wrongAnswersCount by remember { mutableIntStateOf(0) } 
    var showSettingsSheet by remember { mutableStateOf(false) } 
    var showPronunciation by remember { mutableStateOf(prefs.storyShowPronunciation) } 
    var pronunciationType by remember { mutableStateOf(prefs.storyPronunciationType) } 
    var enlargeTextFont by remember { mutableStateOf(prefs.storyEnlargeFont) } 
    var showImages by remember { mutableStateOf(prefs.storyShowImages) } 
    var highlightWordOnAudio by remember { mutableStateOf(prefs.storyHighlightAudio) } 
    
    var submittedIsCorrect by remember { mutableStateOf(false) } 
    var submittedQuestion by remember { mutableStateOf<StoryQuestion?>(null) } 
    var submittedSelectedWord by remember { mutableStateOf("") } 
    
    val totalCount = questions.size 
    val currentQuestion = questions.getOrElse(currentQuestionIndex) { questions[0] } 
    
    val animatedProgress by animateFloatAsState( 
        targetValue = (currentQuestionIndex + 1f) / totalCount.toFloat(), 
        label = "quizProgressBar" 
    ) 
    
    BackHandler { 
        onExitQuiz() 
    } 
    
    Box( 
        modifier = modifier 
            .fillMaxSize() 
            .background(Color(0xFF0C0A09)) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .statusBarsPadding() 
                .navigationBarsPadding() 
                .padding(horizontal = 16.dp, vertical = 10.dp) 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(10.dp) 
            ) { 
                Story3DButton( 
                    onClick = onExitQuiz, 
                    width = 44.dp, 
                    height = 44.dp, 
                    faceColor = Color(0xFF27272A), 
                    lipColor = Color(0xFF3F3F46), 
                    borderColor = Color(0xFF52525B), 
                    borderWidth = 2.dp, 
                    lipHeight = 4.5.dp, 
                    cornerRadius = 14.dp 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Exit Quiz", 
                        tint = Color(0xFFE4E4E7), 
                        modifier = Modifier.size(20.dp) 
                    ) 
                } 
                
                val barShape = RoundedCornerShape(10.dp) 
                val clampedProgress = animatedProgress.coerceIn(0.04f, 1f) 
                
                Box( 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(34.dp) 
                        .clip(barShape) 
                        .background(Color(0xFF27272A)), 
                    contentAlignment = Alignment.CenterStart 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth(clampedProgress) 
                            .fillMaxHeight() 
                            .clip(barShape) 
                            .background(Color(0xFF15803D)) 
                            .padding(bottom = 2.5.dp) 
                            .clip(RoundedCornerShape(8.dp)) 
                            .background(Color(0xFF22C55E)) 
                    ) 
                    
                    Text( 
                        text = "${currentQuestionIndex + 1} / $totalCount", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Black, 
                        fontFamily = BlossomNunito, 
                        color = Color.White, 
                        style = androidx.compose.ui.text.TextStyle( 
                            shadow = androidx.compose.ui.graphics.Shadow( 
                                color = Color.Black.copy(alpha = 0.55f), 
                                blurRadius = 4f 
                            ) 
                        ), 
                        modifier = Modifier.align(Alignment.Center) 
                    ) 
                } 
                
                Box( 
                    modifier = Modifier 
                        .clip(BlossomShapes.Pill) 
                        .background(Color(0xFF0F381E)) 
                        .border(1.dp, Color(0xFF16A34A), BlossomShapes.Pill) 
                        .padding(horizontal = 10.dp, vertical = 6.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(4.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Check, 
                            contentDescription = null, 
                            tint = Color(0xFF22C55E), 
                            modifier = Modifier.size(15.dp) 
                        ) 
                        Text( 
                            text = "$correctAnswersCount", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Black, 
                            fontFamily = BlossomNunito, 
                            color = Color(0xFF22C55E) 
                        ) 
                    } 
                } 
                
                Box( 
                    modifier = Modifier 
                        .clip(BlossomShapes.Pill) 
                        .background(Color(0xFF381414)) 
                        .border(1.dp, Color(0xFFDC2626), BlossomShapes.Pill) 
                        .padding(horizontal = 10.dp, vertical = 6.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(4.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Close, 
                            contentDescription = null, 
                            tint = Color(0xFFEF4444), 
                            modifier = Modifier.size(15.dp) 
                        ) 
                        Text( 
                            text = "$wrongAnswersCount", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Black, 
                            fontFamily = BlossomNunito, 
                            color = Color(0xFFEF4444) 
                        ) 
                    } 
                } 
                
                Story3DButton( 
                    onClick = { showSettingsSheet = true }, 
                    width = 44.dp, 
                    height = 44.dp, 
                    faceColor = Color(0xFF27272A), 
                    lipColor = Color(0xFF18181B), 
                    borderColor = Color(0xFF3F3F46), 
                    borderWidth = 1.5.dp, 
                    lipHeight = 4.dp, 
                    cornerRadius = 14.dp 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Settings, 
                        contentDescription = "Settings", 
                        tint = Color(0xFFE4E4E7), 
                        modifier = Modifier.size(20.dp) 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(26.dp)) 
            
            val questionRaw = currentQuestion.questionFurigana.ifBlank { currentQuestion.questionText } 
            val questionTokens = remember(questionRaw, story.targetWords) { 
                StoryTokenizer.tokenizeToStoryTokens(questionRaw, story.targetWords) 
            } 
            
            FlowRow( 
                horizontalArrangement = Arrangement.spacedBy(2.dp), 
                verticalArrangement = Arrangement.spacedBy(4.dp), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                questionTokens.forEach { token -> 
                    BlossomStoryTokenView( 
                        token = token, 
                        showPronunciation = showPronunciation, 
                        pronunciationType = pronunciationType, 
                        enlargeTextFont = enlargeTextFont, 
                        onClick = { 
                            audioPlayer?.playSentenceText(token.surface) 
                        } 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.weight(1f)) 
            
            val isKanjiWords = currentQuestion.options.all { 
                it.length <= 8 && !it.contains("。") && !it.contains("？") 
            } 
            
            if (isKanjiWords) { 
                val optionChunks = currentQuestion.options.chunked(2) 
                Column( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalArrangement = Arrangement.spacedBy(12.dp) 
                ) { 
                    optionChunks.forEachIndexed { rowIndex, rowOpts -> 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.spacedBy(12.dp) 
                        ) { 
                            rowOpts.forEachIndexed { colIndex, optText -> 
                                val optIndex = rowIndex * 2 + colIndex 
                                val isSelected = selectedOptionIndex == optIndex 
                                val optFuri = currentQuestion.optionsFurigana.getOrNull(optIndex) ?: "" 
                                val rawOpt = optFuri.ifBlank { optText } 
                                val optTokens = remember(rawOpt, story.targetWords) { 
                                    StoryTokenizer.tokenizeToStoryTokens(rawOpt, story.targetWords) 
                                } 
                                val baseBtnHeight = if (enlargeTextFont) 84.dp else 72.dp 
                                
                                Box(modifier = Modifier.weight(1f)) { 
                                    Story3DButton( 
                                        onClick = { 
                                            if (!isSubmitted) { 
                                                selectedOptionIndex = optIndex 
                                                audioPlayer?.playSentenceText(optText) 
                                            } 
                                        }, 
                                        modifier = Modifier.fillMaxWidth(), 
                                        height = baseBtnHeight, 
                                        faceColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF27272A), 
                                        lipColor = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF3F3F46), 
                                        borderColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFF52525B), 
                                        borderWidth = 2.dp, 
                                        lipHeight = 4.5.dp, 
                                        cornerRadius = 14.dp 
                                    ) { 
                                        Row( 
                                            horizontalArrangement = Arrangement.Center, 
                                            verticalAlignment = Alignment.CenterVertically, 
                                            modifier = Modifier.padding(horizontal = 4.dp) 
                                        ) { 
                                            optTokens.forEach { token -> 
                                                BlossomStoryTokenView( 
                                                    token = token, 
                                                    showPronunciation = showPronunciation, 
                                                    pronunciationType = pronunciationType, 
                                                    enlargeTextFont = enlargeTextFont, 
                                                    onClick = { 
                                                        if (!isSubmitted) { 
                                                            selectedOptionIndex = optIndex 
                                                            audioPlayer?.playSentenceText(optText) 
                                                        } 
                                                    } 
                                                ) 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                            if (rowOpts.size == 1) { 
                                Spacer(modifier = Modifier.weight(1f)) 
                            } 
                        } 
                    } 
                } 
            } else { 
                Column( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    currentQuestion.options.forEachIndexed { optIndex, optText -> 
                        val isSelected = selectedOptionIndex == optIndex 
                        val optFuri = currentQuestion.optionsFurigana.getOrNull(optIndex) ?: "" 
                        val rawOpt = optFuri.ifBlank { optText } 
                        val optTokens = remember(rawOpt, story.targetWords) { 
                            StoryTokenizer.tokenizeToStoryTokens(rawOpt, story.targetWords) 
                        } 
                        val baseHeight = if (enlargeTextFont) 68.dp else 56.dp 
                        Story3DButton( 
                            onClick = { 
                                if (!isSubmitted) { 
                                    selectedOptionIndex = optIndex 
                                    audioPlayer?.playSentenceText(optText) 
                                } 
                            }, 
                            modifier = Modifier.fillMaxWidth(), 
                            height = baseHeight, 
                            faceColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF27272A), 
                            lipColor = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF3F3F46), 
                            borderColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFF52525B), 
                            borderWidth = 2.dp, 
                            lipHeight = 4.5.dp, 
                            cornerRadius = 14.dp 
                        ) { 
                            FlowRow( 
                                horizontalArrangement = Arrangement.spacedBy(2.dp), 
                                verticalArrangement = Arrangement.spacedBy(2.dp), 
                                modifier = Modifier.padding(horizontal = 12.dp) 
                            ) { 
                                optTokens.forEach { token -> 
                                    BlossomStoryTokenView( 
                                        token = token, 
                                        showPronunciation = showPronunciation, 
                                        pronunciationType = pronunciationType, 
                                        enlargeTextFont = enlargeTextFont, 
                                        onClick = { 
                                            if (!isSubmitted) { 
                                                selectedOptionIndex = optIndex 
                                                audioPlayer?.playSentenceText(optText) 
                                            } 
                                        } 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(18.dp)) 
            
            BlossomTactileButton( 
                onClick = { 
                    if (selectedOptionIndex != null && !isSubmitted) { 
                        val selectedIdx = selectedOptionIndex!! 
                        val correct = selectedIdx == currentQuestion.correctOptionIndex 
                        submittedIsCorrect = correct 
                        submittedQuestion = currentQuestion 
                        submittedSelectedWord = currentQuestion.options.getOrElse(selectedIdx) { "" } 
                        if (correct) { 
                            correctAnswersCount++ 
                            BlossomSoundEffects.playSuccess() 
                        } else { 
                            wrongAnswersCount++ 
                            BlossomSoundEffects.playError() 
                        } 
                        isSubmitted = true 
                    } 
                }, 
                modifier = Modifier.fillMaxWidth(), 
                faceColor = if (selectedOptionIndex != null) Color(0xFF1D4ED8) else Color(0xFF1E293B), 
                lipColor = if (selectedOptionIndex != null) Color(0xFF1E40AF) else Color(0xFF0F172A), 
                enabled = selectedOptionIndex != null, 
                buttonHeight = 52.dp 
            ) { 
                Text( 
                    text = "CONTINUE", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Black, 
                    fontFamily = BlossomNunito, 
                    color = if (selectedOptionIndex != null) Color.White else Color(0xFF64748B) 
                ) 
            } 
        } 
        
        AnimatedVisibility( 
            visible = isSubmitted, 
            enter = slideInVertically(initialOffsetY = { it }), 
            exit = slideOutVertically(targetOffsetY = { it }), 
            modifier = Modifier.align(Alignment.BottomCenter) 
        ) { 
            val isSuccess = submittedIsCorrect 
            val currentQ = submittedQuestion ?: currentQuestion 
            val targetWordText = if (isSuccess) { 
                submittedSelectedWord 
            } else { 
                currentQ.options.getOrElse(currentQ.correctOptionIndex) { "" } 
            } 
            val parsedResult = parseFuriganaAndWord(targetWordText, story) 
            val meanings = extractMeanings(targetWordText, currentQ, story) 
            
            val sheetBg = if (isSuccess) Color(0xFF14532D) else Color(0xFF7F1D1D) 
            val boxBg = if (isSuccess) Color(0xFF166534) else Color(0xFF991B1B) 
            val actionTextColor = if (isSuccess) Color(0xFF15803D) else Color(0xFF991B1B) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) 
                    .background(sheetBg) 
                    .padding(horizontal = 20.dp, vertical = 20.dp) 
                    .navigationBarsPadding() 
            ) { 
                Column( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalAlignment = Alignment.CenterHorizontally 
                ) { 
                    Text( 
                        text = if (isSuccess) "Excellent!" else "One more try!", 
                        fontSize = 26.sp, 
                        fontWeight = FontWeight.Black, 
                        fontFamily = BlossomNunito, 
                        color = Color.White, 
                        textAlign = TextAlign.Center 
                    ) 
                    
                    Spacer(modifier = Modifier.height(4.dp)) 
                    
                    Text( 
                        text = if (isSuccess) "Your answer:" else "The correct answer is:", 
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.Medium, 
                        fontFamily = BlossomNunito, 
                        color = Color.White.copy(alpha = 0.85f), 
                        textAlign = TextAlign.Center 
                    ) 
                    
                    Spacer(modifier = Modifier.height(16.dp)) 
                    
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .clip(RoundedCornerShape(16.dp)) 
                            .background(boxBg) 
                            .padding(vertical = 14.dp, horizontal = 16.dp), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                            if (parsedResult.first.isNotBlank()) { 
                                Text( 
                                    text = parsedResult.first, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Medium, 
                                    fontFamily = BlossomNunito, 
                                    color = Color.White.copy(alpha = 0.85f) 
                                ) 
                            } 
                            Text( 
                                text = parsedResult.second, 
                                fontSize = 24.sp, 
                                fontWeight = FontWeight.Black, 
                                fontFamily = BlossomNunito, 
                                color = Color.White 
                            ) 
                        } 
                    } 
                    
                    Spacer(modifier = Modifier.height(12.dp)) 
                    
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .clip(RoundedCornerShape(16.dp)) 
                            .background(boxBg) 
                            .padding(horizontal = 16.dp, vertical = 14.dp) 
                    ) { 
                        Column( 
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalAlignment = Alignment.CenterHorizontally 
                        ) { 
                            Box( 
                                modifier = Modifier 
                                    .clip(BlossomShapes.Pill) 
                                    .background(Color.White.copy(alpha = 0.2f)) 
                                    .padding(horizontal = 12.dp, vertical = 4.dp) 
                            ) { 
                                Text( 
                                    text = if (meanings.size > 1) "MEANINGS" else "MEANING", 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    fontFamily = BlossomNunito, 
                                    color = Color.White 
                                ) 
                            } 
                            
                            Spacer(modifier = Modifier.height(10.dp)) 
                            
                            Column( 
                                modifier = Modifier.fillMaxWidth(), 
                                verticalArrangement = Arrangement.spacedBy(6.dp) 
                            ) { 
                                meanings.forEachIndexed { idx, meaning -> 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.spacedBy(8.dp) 
                                    ) { 
                                        Box( 
                                            modifier = Modifier 
                                                .size(20.dp) 
                                                .clip(CircleShape) 
                                                .background(Color.White.copy(alpha = 0.25f)), 
                                            contentAlignment = Alignment.Center 
                                        ) { 
                                            Text( 
                                                text = "${idx + 1}", 
                                                fontSize = 11.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                fontFamily = BlossomNunito, 
                                                color = Color.White 
                                            ) 
                                        } 
                                        Text( 
                                            text = meaning, 
                                            fontSize = 15.sp, 
                                            fontFamily = BlossomNunito, 
                                            color = Color.White 
                                        ) 
                                    } 
                                } 
                            } 
                        } 
                    } 
                    
                    Spacer(modifier = Modifier.height(18.dp)) 
                    
                    BlossomTactileButton( 
                        onClick = { 
                            isSubmitted = false 
                            if (currentQuestionIndex < totalCount - 1) { 
                                currentQuestionIndex++ 
                                selectedOptionIndex = null 
                            } else { 
                                onCompleteQuiz(correctAnswersCount, totalCount) 
                            } 
                        }, 
                        modifier = Modifier.fillMaxWidth(), 
                        faceColor = Color.White, 
                        lipColor = Color(0xFFE4E4E7), 
                        buttonHeight = 52.dp 
                    ) { 
                        Text( 
                            text = "CONTINUE", 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Black, 
                            fontFamily = BlossomNunito, 
                            color = actionTextColor 
                        ) 
                    } 
                } 
            } 
        } 
        
        if (showSettingsSheet) { 
            BlossomStorySettingsSheet( 
                showPronunciation = showPronunciation, 
                onToggleShowPronunciation = { 
                    showPronunciation = it 
                    prefs.storyShowPronunciation = it 
                }, 
                pronunciationType = pronunciationType, 
                onChangePronunciationType = { 
                    pronunciationType = it 
                    prefs.storyPronunciationType = it 
                }, 
                enlargeTextFont = enlargeTextFont, 
                onToggleEnlargeTextFont = { 
                    enlargeTextFont = it 
                    prefs.storyEnlargeFont = it 
                }, 
                showImages = showImages, 
                onToggleShowImages = { 
                    showImages = it 
                    prefs.storyShowImages = it 
                }, 
                highlightWordOnAudio = highlightWordOnAudio, 
                onToggleHighlightWordOnAudio = { 
                    highlightWordOnAudio = it 
                    prefs.storyHighlightAudio = it 
                }, 
                onDismiss = { showSettingsSheet = false } 
            ) 
        } 
    } 
} 
    
private fun parseFuriganaAndWord(text: String, story: ForgedStory): Pair<String, String> { 
    val matched = story.targetWords.firstOrNull { it.kanji == text || it.surface == text } 
    if (matched != null && matched.reading.isNotBlank()) { 
        val raw = matched.reading 
        if (raw.contains("[") && raw.contains("]")) { 
            val segs = StoryTokenizer.parseBracketSegments(raw) 
            val furi = segs.mapNotNull { it.ruby }.joinToString("") 
            val base = segs.map { it.text }.joinToString("") 
            return Pair(furi, base) 
        } 
        return Pair(matched.reading, matched.kanji.ifBlank { text }) 
    } 
    val mapped = StoryTokenizer.commonFuriganaMap[text] 
    if (mapped != null) { 
        val segs = StoryTokenizer.parseBracketSegments(mapped) 
        val furi = segs.mapNotNull { it.ruby }.joinToString("") 
        val base = segs.map { it.text }.joinToString("") 
        return Pair(furi, base) 
    } 
    if (text.contains("[") && text.contains("]")) { 
        val segs = StoryTokenizer.parseBracketSegments(text) 
        val furi = segs.mapNotNull { it.ruby }.joinToString("") 
        val base = segs.map { it.text }.joinToString("") 
        return Pair(furi, base) 
    } 
    return Pair("", text) 
} 
    
private fun extractMeanings(text: String, question: StoryQuestion, story: ForgedStory): List<String> { 
    val matched = story.targetWords.firstOrNull { it.kanji == text || it.surface == text } 
    if (matched != null && matched.meaning.isNotBlank()) { 
        return listOf(matched.meaning) 
    } 
    if (question.explanation.isNotBlank()) { 
        return question.explanation.split("\n").map { it.trim() }.filter { it.isNotBlank() } 
    } 
    return listOf(text) 
} 
