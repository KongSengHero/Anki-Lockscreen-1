package com.ankilock.ui.shinobi.story

import androidx.compose.ui.graphics.asImageBitmap
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.CardInfo
import com.ankilock.data.ForgedStory
import com.ankilock.data.StoryAssetLoader
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiNunito
import com.ankilock.ui.shinobi.ShinobiShapes
import com.ankilock.ui.shinobi.ShinobiSoundEffects
import com.ankilock.ui.shinobi.ShinobiTactileButton
import com.ankilock.ui.shinobi.StoryArtworkThumbnail
import kotlinx.coroutines.delay
import java.io.File
import kotlin.random.Random

data class SpeedStrikeWord( 
    val kanji: String, 
    val reading: String, 
    val meaning: String 
) 

private val FallbackWordBank = listOf( 
    SpeedStrikeWord("猫", "ねこ", "Cat"), 
    SpeedStrikeWord("犬", "いぬ", "Dog"), 
    SpeedStrikeWord("約束", "やくそく", "Promise"), 
    SpeedStrikeWord("友達", "ともだち", "Friend"), 
    SpeedStrikeWord("桜", "さくら", "Cherry blossom"), 
    SpeedStrikeWord("雨", "あめ", "Rain"), 
    SpeedStrikeWord("星", "ほし", "Star"), 
    SpeedStrikeWord("夜", "よる", "Night"), 
    SpeedStrikeWord("太陽", "たいよう", "Sun"), 
    SpeedStrikeWord("道", "みち", "Road / Path"), 
    SpeedStrikeWord("時間", "じかん", "Time"), 
    SpeedStrikeWord("心", "こころ", "Heart / Spirit"), 
    SpeedStrikeWord("希望", "きぼう", "Hope"), 
    SpeedStrikeWord("勇気", "ゆうき", "Courage"), 
    SpeedStrikeWord("平和", "へいわ", "Peace"), 
    SpeedStrikeWord("夢", "ゆめ", "Dream"), 
    SpeedStrikeWord("笑顔", "えがお", "Smiling face"), 
    SpeedStrikeWord("手紙", "てがみ", "Letter"), 
    SpeedStrikeWord("旅", "たび", "Journey / Travel"), 
    SpeedStrikeWord("思い出", "おもいで", "Memory") 
) 

@Composable
fun ShinobiSpeedStrikeGame( 
    cards: List<CardInfo>, 
    isGenerating: Boolean, 
    generationStage: String, 
    errorMessage: String?, 
    forgedStory: ForgedStory?, 
    difficultyTier: String = "Medium", 
    initialHp: Int = 3, 
    secondsPerQuestion: Float = 10f, 
    onReadStory: (ForgedStory) -> Unit, 
    onExitGame: () -> Unit 
) { 
    val context = LocalContext.current 
    val view = LocalView.current 
    
    val gameWords = remember(cards) { 
        val list = mutableListOf<SpeedStrikeWord>() 
        for (c in cards) { 
            val k = c.kanji.ifBlank { c.question }.trim() 
            val r = c.kanjiFurigana.ifBlank { c.question }.trim() 
            val m = c.kanjiMeaning.ifBlank { c.answer }.trim() 
            if (k.isNotBlank() && m.isNotBlank()) { 
                list.add(SpeedStrikeWord(k, r, m)) 
            } 
        } 
        if (list.size < 4) { 
            list.addAll(FallbackWordBank) 
        } 
        list.shuffled() 
    } 
    
    var questionIndex by remember { mutableIntStateOf(0) } 
    var score by remember { mutableIntStateOf(0) } 
    var combo by remember { mutableIntStateOf(0) } 
    var maxCombo by remember { mutableIntStateOf(0) } 
    var correctCount by remember { mutableIntStateOf(0) } 
    var totalStrikes by remember { mutableIntStateOf(0) } 
    
    val currentWord = remember(questionIndex, gameWords) { 
        gameWords[questionIndex % gameWords.size] 
    } 
    
    val currentOptions = remember(questionIndex, currentWord) { 
        val distractors = gameWords.filter { it.meaning != currentWord.meaning }.shuffled().take(3).map { it.meaning } 
        val pool = (distractors + currentWord.meaning).shuffled() 
        pool 
    } 
    
    var selectedOption by remember { mutableStateOf<String?>(null) } 
    var isRoundEvaluated by remember { mutableStateOf(false) } 
    var isRoundCorrect by remember { mutableStateOf(false) } 
    var roundTimeLeft by remember { mutableFloatStateOf(1f) } 
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition") 
    val pulseGlow by infiniteTransition.animateFloat( 
        initialValue = 0.5f, 
        targetValue = 1f, 
        animationSpec = infiniteRepeatable( 
            animation = tween(1200, easing = FastOutSlowInEasing), 
            repeatMode = RepeatMode.Reverse 
        ), 
        label = "glow" 
    ) 
    
    LaunchedEffect(isGenerating, forgedStory) { 
        if (!isGenerating && forgedStory != null) { 
            ShinobiSoundEffects.playCompleted() 
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS) 
        } 
    } 
    
    LaunchedEffect(questionIndex, isGenerating, isRoundEvaluated) { 
        if (isGenerating && !isRoundEvaluated) { 
            val totalMs = 3500L 
            val stepMs = 50L 
            var elapsed = 0L 
            while (elapsed < totalMs && !isRoundEvaluated) { 
                delay(stepMs) 
                elapsed += stepMs 
                roundTimeLeft = (1f - (elapsed.toFloat() / totalMs.toFloat())).coerceIn(0f, 1f) 
            } 
            if (!isRoundEvaluated) { 
                isRoundEvaluated = true 
                isRoundCorrect = false 
                combo = 0 
                totalStrikes++ 
                ShinobiSoundEffects.playError() 
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK) 
                delay(400L) 
                selectedOption = null 
                isRoundEvaluated = false 
                roundTimeLeft = 1f 
                questionIndex++ 
            } 
        } 
    } 
    
    BackHandler { 
        onExitGame() 
    } 
    
    Box( 
        modifier = Modifier 
            .fillMaxSize() 
            .background(Color(0xFF0C0A09)) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .statusBarsPadding() 
                .padding(horizontal = 16.dp, vertical = 8.dp) 
        ) { 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(bottom = 12.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Box( 
                    modifier = Modifier 
                        .clip(CircleShape) 
                        .background(Color(0xFF1E1E22)) 
                        .clickable { onExitGame() } 
                        .padding(8.dp) 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Exit", 
                        tint = Color(0xFF94A3B8), 
                        modifier = Modifier.size(20.dp) 
                    ) 
                } 
                
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(8.dp), 
                    modifier = Modifier 
                        .clip(RoundedCornerShape(12.dp)) 
                        .background(Color(0xFF18191D)) 
                        .border(1.dp, Color(0xFF333338), RoundedCornerShape(12.dp)) 
                        .padding(horizontal = 12.dp, vertical = 6.dp) 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.LocalFireDepartment, 
                        contentDescription = null, 
                        tint = if (combo > 2) Color(0xFFF97316) else Color(0xFF64748B), 
                        modifier = Modifier.size(16.dp) 
                    ) 
                    Text( 
                        text = "Combo x$combo", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontFamily = ShinobiNunito, 
                        color = if (combo > 2) Color(0xFFF97316) else Color.White 
                    ) 
                    Spacer(modifier = Modifier.width(4.dp)) 
                    Text( 
                        text = "$score pts", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = ShinobiNunito, 
                        color = Color(0xFF38BDF8) 
                    ) 
                } 
            } 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(RoundedCornerShape(16.dp)) 
                    .background(Color(0xFF141F36)) 
                    .border( 
                        1.dp, 
                        Color(0xFF2563EB).copy(alpha = pulseGlow), 
                        RoundedCornerShape(16.dp) 
                    ) 
                    .padding(horizontal = 14.dp, vertical = 10.dp) 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    if (isGenerating) { 
                        CircularProgressIndicator( 
                            modifier = Modifier.size(18.dp), 
                            color = Color(0xFF60A5FA), 
                            strokeWidth = 2.dp 
                        ) 
                    } else { 
                        Icon( 
                            imageVector = Icons.Default.Check, 
                            contentDescription = null, 
                            tint = Color(0xFF22C55E), 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                    
                    Column(modifier = Modifier.weight(1f)) { 
                        Text( 
                            text = if (isGenerating) "AI FORGE IN PROGRESS" else "STORY COMPLETE", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFF60A5FA) 
                        ) 
                        Text( 
                            text = generationStage.ifBlank { "Weaving Japanese story..." }, 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White, 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis 
                        ) 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            
            LinearProgressIndicator( 
                progress = { roundTimeLeft }, 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(6.dp) 
                    .clip(RoundedCornerShape(3.dp)), 
                color = when { 
                    roundTimeLeft > 0.5f -> Color(0xFF22C55E) 
                    roundTimeLeft > 0.25f -> Color(0xFFF59E0B) 
                    else -> Color(0xFFEF4444) 
                }, 
                trackColor = Color(0xFF27272A) 
            ) 
            
            Spacer(modifier = Modifier.height(18.dp)) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(180.dp) 
                    .clip(RoundedCornerShape(22.dp)) 
                    .background(Color(0xFF1E1E24)) 
                    .border( 
                        2.dp, 
                        when { 
                            isRoundEvaluated && isRoundCorrect -> Color(0xFF22C55E) 
                            isRoundEvaluated && !isRoundCorrect -> Color(0xFFEF4444) 
                            else -> Color(0xFF333338) 
                        }, 
                        RoundedCornerShape(22.dp) 
                    ), 
                contentAlignment = Alignment.Center 
            ) { 
                Column( 
                    horizontalAlignment = Alignment.CenterHorizontally, 
                    verticalArrangement = Arrangement.Center 
                ) { 
                    if (currentWord.reading.isNotBlank() && currentWord.reading != currentWord.kanji) { 
                        Text( 
                            text = currentWord.reading, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color(0xFF94A3B8), 
                            modifier = Modifier.padding(bottom = 4.dp) 
                        ) 
                    } 
                    Text( 
                        text = currentWord.kanji, 
                        fontSize = 38.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        color = Color.White, 
                        textAlign = TextAlign.Center 
                    ) 
                    Text( 
                        text = "SPEED STRIKE", 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontFamily = ShinobiNunito, 
                        color = Color(0xFF38BDF8), 
                        modifier = Modifier.padding(top = 8.dp) 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(20.dp)) 
            
            Column( 
                modifier = Modifier.fillMaxWidth(), 
                verticalArrangement = Arrangement.spacedBy(10.dp) 
            ) { 
                currentOptions.forEachIndexed { optIndex, optionText -> 
                    val isSelected = selectedOption == optionText 
                    val isCorrectTarget = optionText == currentWord.meaning 
                    
                    val faceColor = when { 
                        isRoundEvaluated && isCorrectTarget -> Color(0xFF14532D) 
                        isRoundEvaluated && isSelected && !isRoundCorrect -> Color(0xFF7F1D1D) 
                        else -> Color(0xFF27272A) 
                    } 
                    val lipColor = when { 
                        isRoundEvaluated && isCorrectTarget -> Color(0xFF166534) 
                        isRoundEvaluated && isSelected && !isRoundCorrect -> Color(0xFF991B1B) 
                        else -> Color(0xFF3F3F46) 
                    } 
                    val borderColor = when { 
                        isRoundEvaluated && isCorrectTarget -> Color(0xFF22C55E) 
                        isRoundEvaluated && isSelected && !isRoundCorrect -> Color(0xFFEF4444) 
                        else -> Color(0xFF52525B) 
                    } 
                    
                    ShinobiTactileButton( 
                        onClick = { 
                            if (!isRoundEvaluated) { 
                                selectedOption = optionText 
                                isRoundEvaluated = true 
                                totalStrikes++ 
                                if (isCorrectTarget) { 
                                    isRoundCorrect = true 
                                    correctCount++ 
                                    combo++ 
                                    if (combo > maxCombo) maxCombo = combo 
                                    score += 100 * (1 + (combo / 3)) 
                                    ShinobiSoundEffects.playSuccess() 
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
                                } else { 
                                    isRoundCorrect = false 
                                    combo = 0 
                                    ShinobiSoundEffects.playError() 
                                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK) 
                                } 
                                
                                val handler = android.os.Handler(android.os.Looper.getMainLooper()) 
                                handler.postDelayed({ 
                                    selectedOption = null 
                                    isRoundEvaluated = false 
                                    roundTimeLeft = 1f 
                                    questionIndex++ 
                                }, 350L) 
                            } 
                        }, 
                        modifier = Modifier.fillMaxWidth(), 
                        faceColor = faceColor, 
                        lipColor = lipColor, 
                        contentColor = Color.White 
                    ) { 
                        Text( 
                            text = optionText, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = ShinobiNunito, 
                            textAlign = TextAlign.Center, 
                            modifier = Modifier.fillMaxWidth() 
                        ) 
                    } 
                } 
            } 
        } 
        
        AnimatedVisibility( 
            visible = !isGenerating && forgedStory != null, 
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.9f), 
            exit = fadeOut() 
        ) { 
            Box( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .background(Color.Black.copy(alpha = 0.85f)) 
                    .clickable { } 
                    .padding(20.dp), 
                contentAlignment = Alignment.Center 
            ) { 
                Surface( 
                    shape = RoundedCornerShape(24.dp), 
                    color = Color(0xFF18191E), 
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF38BDF8)), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Column( 
                        modifier = Modifier 
                            .padding(22.dp) 
                            .verticalScroll(rememberScrollState()), 
                        horizontalAlignment = Alignment.CenterHorizontally 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.AutoAwesome, 
                            contentDescription = null, 
                            tint = Color(0xFFFBBF24), 
                            modifier = Modifier.size(44.dp) 
                        ) 
                        
                        Spacer(modifier = Modifier.height(8.dp)) 
                        
                        Text( 
                            text = "STORY FORGED!", 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = Color.White 
                        ) 
                        
                        Text( 
                            text = forgedStory?.title ?: "New Japanese Story", 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF38BDF8), 
                            textAlign = TextAlign.Center, 
                            modifier = Modifier.padding(top = 4.dp) 
                        ) 
                        
                        Spacer(modifier = Modifier.height(16.dp)) 
                        
                        Box( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .height(160.dp) 
                                .clip(RoundedCornerShape(16.dp)) 
                        ) { 
                            val coverFile = remember(forgedStory?.id) { 
                                forgedStory?.let { File(context.filesDir, "stories/images/${it.id}/cover.png") } 
                            } 
                            val bitmap = remember(coverFile) { 
                                if (coverFile != null && coverFile.exists()) { 
                                    android.graphics.BitmapFactory.decodeFile(coverFile.absolutePath)?.asImageBitmap() 
                                } else null 
                            } 
                            
                            if (bitmap != null) { 
                                Image( 
                                    bitmap = bitmap, 
                                    contentDescription = null, 
                                    contentScale = ContentScale.Crop, 
                                    modifier = Modifier.fillMaxSize() 
                                ) 
                            } else { 
                                StoryArtworkThumbnail( 
                                    artworkType = forgedStory?.genre ?: "daily", 
                                    modifier = Modifier.fillMaxSize() 
                                ) 
                            } 
                        } 
                        
                        Spacer(modifier = Modifier.height(16.dp)) 
                        
                        Row( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .clip(RoundedCornerShape(14.dp)) 
                                .background(Color(0xFF27272A)) 
                                .padding(12.dp), 
                            horizontalArrangement = Arrangement.SpaceAround 
                        ) { 
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                                Text("Strikes", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = ShinobiNunito) 
                                Text("$correctCount / $totalStrikes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White) 
                            } 
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                                Text("Max Combo", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = ShinobiNunito) 
                                Text("x$maxCombo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316)) 
                            } 
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                                Text("Bonus XP", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = ShinobiNunito) 
                                val bonusXp = (score / 20).coerceAtLeast(25) 
                                Text("+$bonusXp XP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E)) 
                            } 
                        } 
                        
                        Spacer(modifier = Modifier.height(20.dp)) 
                        
                        ShinobiTactileButton( 
                            onClick = { 
                                forgedStory?.let { onReadStory(it) } 
                            }, 
                            modifier = Modifier.fillMaxWidth(), 
                            faceColor = ShinobiColors.ElectricBlue, 
                            lipColor = ShinobiColors.ElectricBlueLip, 
                            contentColor = Color.White 
                        ) { 
                            Text( 
                                text = "READ STORY NOW", 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.ExtraBold, 
                                fontFamily = ShinobiNunito 
                            ) 
                        } 
                        
                        Spacer(modifier = Modifier.height(8.dp)) 
                        
                        Box( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .clickable { onExitGame() } 
                                .padding(vertical = 8.dp), 
                            contentAlignment = Alignment.Center 
                        ) { 
                            Text( 
                                text = "Back to Stories", 
                                fontSize = 13.sp, 
                                fontWeight = FontWeight.Bold, 
                                fontFamily = ShinobiNunito, 
                                color = Color(0xFF94A3B8) 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
        
        AnimatedVisibility( 
            visible = !isGenerating && errorMessage != null, 
            enter = fadeIn(), 
            exit = fadeOut() 
        ) { 
            Box( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .background(Color.Black.copy(alpha = 0.85f)) 
                    .padding(20.dp), 
                contentAlignment = Alignment.Center 
            ) { 
                Surface( 
                    shape = RoundedCornerShape(20.dp), 
                    color = Color(0xFF1E1414), 
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Column( 
                        modifier = Modifier.padding(20.dp), 
                        horizontalAlignment = Alignment.CenterHorizontally 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.ErrorOutline, 
                            contentDescription = null, 
                            tint = Color(0xFFEF4444), 
                            modifier = Modifier.size(40.dp) 
                        ) 
                        Spacer(modifier = Modifier.height(10.dp)) 
                        Text( 
                            text = "Forge Interrupted", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White 
                        ) 
                        Spacer(modifier = Modifier.height(6.dp)) 
                        Text( 
                            text = errorMessage ?: "Failed to generate story.", 
                            fontSize = 13.sp, 
                            color = Color(0xFFFECACA), 
                            textAlign = TextAlign.Center 
                        ) 
                        Spacer(modifier = Modifier.height(16.dp)) 
                        ShinobiTactileButton( 
                            onClick = onExitGame, 
                            modifier = Modifier.fillMaxWidth(), 
                            faceColor = Color(0xFF27272A), 
                            lipColor = Color(0xFF3F3F46), 
                            contentColor = Color.White 
                        ) { 
                            Text("Return to Studio", fontSize = 14.sp, fontWeight = FontWeight.Bold) 
                        } 
                    } 
                } 
            } 
        } 
    } 
} 
