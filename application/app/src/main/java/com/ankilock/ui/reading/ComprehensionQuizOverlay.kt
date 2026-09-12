package com.ankilock.ui.reading

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.navigationBarsPadding 
import androidx.compose.foundation.layout.statusBarsPadding 
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.automirrored.filled.ArrowBack 
import androidx.compose.material.icons.automirrored.filled.ArrowForward 
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ankilock.data.StoryQuizQuestion
import com.ankilock.ui.blossom.BlossomColors

@Composable
fun ComprehensionQuizOverlay( 
    questions: List<StoryQuizQuestion>, 
    onDismiss: () -> Unit 
) { 
    if (questions.isEmpty()) return 
    
    var currentIndex by remember { mutableIntStateOf(0) } 
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } 
    var isQuizCompleted by remember { mutableStateOf(false) } 
    
    Dialog( 
        onDismissRequest = onDismiss, 
        properties = DialogProperties( 
            usePlatformDefaultWidth = false, 
            decorFitsSystemWindows = false 
        ) 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxSize() 
                .background(BlossomColors.BackgroundDeep) 
        ) { 
            Column( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .statusBarsPadding() 
                    .navigationBarsPadding() 
                    .padding(horizontal = 20.dp, vertical = 16.dp) 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    IconButton( 
                        onClick = onDismiss, 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(CircleShape) 
                            .background(BlossomColors.SurfaceElevated) 
                    ) { 
                        Icon( 
                            Icons.Filled.Close, 
                            contentDescription = "Close Quiz", 
                            tint = BlossomColors.TextPrimary, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                    } 
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                        Text( 
                            text = "Comprehension Quiz", 
                            fontSize = 17.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        if (!isQuizCompleted) { 
                            Text( 
                                text = "Question ${currentIndex + 1} of ${questions.size}", 
                                fontSize = 12.sp, 
                                color = BlossomColors.TextSecondary 
                            ) 
                        } 
                    } 
                    
                    IconButton( 
                        onClick = { 
                            userAnswers.clear() 
                            currentIndex = 0 
                            isQuizCompleted = false 
                        }, 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(CircleShape) 
                            .background(BlossomColors.SurfaceElevated) 
                    ) { 
                        Icon( 
                            Icons.Filled.Refresh, 
                            contentDescription = "Restart", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                } 
                
                Spacer(modifier = Modifier.height(16.dp)) 
                
                val progress = if (isQuizCompleted) 1f else (currentIndex.toFloat() / questions.size) 
                LinearProgressIndicator( 
                    progress = { progress }, 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(6.dp) 
                        .clip(RoundedCornerShape(3.dp)), 
                    color = BlossomColors.SakuraRose, 
                    trackColor = BlossomColors.SurfaceCard2 
                ) 
                
                Spacer(modifier = Modifier.height(20.dp)) 
                
                AnimatedContent( 
                    targetState = isQuizCompleted, 
                    transitionSpec = { 
                        fadeIn(tween(200)) togetherWith fadeOut(tween(150)) 
                    }, 
                    label = "QuizStepTransition" 
                ) { completed -> 
                    if (completed) { 
                        val correctCount = questions.count { userAnswers[it.id] == it.correctOptionIndex } 
                        val isPerfect = correctCount == questions.size 
                        
                        Column( 
                            modifier = Modifier 
                                .fillMaxSize() 
                                .verticalScroll(rememberScrollState()), 
                            horizontalAlignment = Alignment.CenterHorizontally, 
                            verticalArrangement = Arrangement.spacedBy(16.dp) 
                        ) { 
                            Spacer(modifier = Modifier.height(24.dp)) 
                            
                            Surface( 
                                shape = CircleShape, 
                                color = if (isPerfect) BlossomColors.BlossomGreenSurface else BlossomColors.SakuraRoseContainer, 
                                border = BorderStroke( 
                                    2.dp, 
                                    if (isPerfect) BlossomColors.BlossomGreen else BlossomColors.SakuraRose 
                                ), 
                                modifier = Modifier.size(90.dp) 
                            ) { 
                                Box(contentAlignment = Alignment.Center) { 
                                    Icon( 
                                        imageVector = Icons.Filled.EmojiEvents, 
                                        contentDescription = null, 
                                        tint = if (isPerfect) BlossomColors.BlossomGreen else BlossomColors.SakuraRose, 
                                        modifier = Modifier.size(46.dp) 
                                    ) 
                                } 
                            } 
                            
                            Text( 
                                text = if (isPerfect) "素晴らしい！ Perfect Score!" else "Quiz Complete!", 
                                fontSize = 22.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = BlossomColors.TextPrimary 
                            ) 
                            
                            Text( 
                                text = "You got $correctCount out of ${questions.size} correct", 
                                fontSize = 15.sp, 
                                color = BlossomColors.TextSecondary 
                            ) 
                            
                            Spacer(modifier = Modifier.height(8.dp)) 
                            
                            Column( 
                                modifier = Modifier.fillMaxWidth(), 
                                verticalArrangement = Arrangement.spacedBy(10.dp) 
                            ) { 
                                questions.forEachIndexed { idx, q -> 
                                    val isCorrect = userAnswers[q.id] == q.correctOptionIndex 
                                    Surface( 
                                        shape = RoundedCornerShape(12.dp), 
                                        color = BlossomColors.SurfaceElevated, 
                                        border = BorderStroke( 
                                            1.dp, 
                                            if (isCorrect) BlossomColors.BlossomGreen.copy(alpha = 0.5f) 
                                            else BlossomColors.BlossomRed.copy(alpha = 0.5f) 
                                        ), 
                                        modifier = Modifier.fillMaxWidth() 
                                    ) { 
                                        Row( 
                                            modifier = Modifier.padding(14.dp), 
                                            verticalAlignment = Alignment.CenterVertically 
                                        ) { 
                                            Icon( 
                                                imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close, 
                                                contentDescription = null, 
                                                tint = if (isCorrect) BlossomColors.BlossomGreen else BlossomColors.BlossomRed, 
                                                modifier = Modifier.size(20.dp) 
                                            ) 
                                            Spacer(modifier = Modifier.width(12.dp)) 
                                            Column(modifier = Modifier.weight(1f)) { 
                                                Text( 
                                                    text = "${idx + 1}. ${q.questionText}", 
                                                    fontSize = 13.sp, 
                                                    fontWeight = FontWeight.SemiBold, 
                                                    color = BlossomColors.TextPrimary 
                                                ) 
                                                val userOptIdx = userAnswers[q.id] 
                                                val correctOptText = q.options.getOrNull(q.correctOptionIndex) ?: "" 
                                                if (!isCorrect && userOptIdx != null) { 
                                                    val chosenOptText = q.options.getOrNull(userOptIdx) ?: "" 
                                                    Spacer(modifier = Modifier.height(4.dp)) 
                                                    Text( 
                                                        text = "Your answer: $chosenOptText", 
                                                        fontSize = 12.sp, 
                                                        color = BlossomColors.BlossomRed 
                                                    ) 
                                                    Text( 
                                                        text = "Correct: $correctOptText", 
                                                        fontSize = 12.sp, 
                                                        color = BlossomColors.BlossomGreen, 
                                                        fontWeight = FontWeight.Medium 
                                                    ) 
                                                } 
                                                if (q.explanation.isNotBlank()) { 
                                                    Spacer(modifier = Modifier.height(4.dp)) 
                                                    Text( 
                                                        text = q.explanation, 
                                                        fontSize = 11.sp, 
                                                        color = BlossomColors.TextSecondary 
                                                    ) 
                                                } 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                            
                            Spacer(modifier = Modifier.height(16.dp)) 
                            
                            Row( 
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.spacedBy(12.dp) 
                            ) { 
                                Box( 
                                    modifier = Modifier 
                                        .weight(1f) 
                                        .height(52.dp) 
                                        .clip(RoundedCornerShape(20.dp)) 
                                        .background(BlossomColors.SurfaceElevated) 
                                        .border(BorderStroke(1.dp, BlossomColors.CardBorder), shape = RoundedCornerShape(20.dp)) 
                                        .clickable { 
                                            userAnswers.clear() 
                                            currentIndex = 0 
                                            isQuizCompleted = false 
                                        }, 
                                    contentAlignment = Alignment.Center 
                                ) { 
                                    Text( 
                                        text = "Retake", 
                                        color = BlossomColors.TextPrimary, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 15.sp 
                                    ) 
                                } 
                                
                                Box( 
                                    modifier = Modifier 
                                        .weight(1f) 
                                        .height(52.dp) 
                                        .clip(RoundedCornerShape(20.dp)) 
                                        .background(BlossomColors.SakuraRose.copy(alpha = 0.85f)) 
                                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)), shape = RoundedCornerShape(20.dp)) 
                                        .clickable { onDismiss() }, 
                                    contentAlignment = Alignment.Center 
                                ) { 
                                    Text( 
                                        text = "Done", 
                                        color = BlossomColors.BlossomWhite, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 15.sp 
                                    ) 
                                } 
                            } 
                        } 
                    } else { 
                        val currentQuestion = questions.getOrNull(currentIndex) ?: return@AnimatedContent 
                        val selectedOpt = userAnswers[currentQuestion.id] 
                        
                        Column( 
                            modifier = Modifier 
                                .fillMaxSize() 
                                .verticalScroll(rememberScrollState()), 
                            verticalArrangement = Arrangement.SpaceBetween 
                        ) { 
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { 
                                Surface( 
                                    shape = RoundedCornerShape(16.dp), 
                                    color = BlossomColors.SurfaceElevated, 
                                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                                    modifier = Modifier.fillMaxWidth() 
                                ) { 
                                    Text( 
                                        text = currentQuestion.questionText, 
                                        fontSize = 17.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = BlossomColors.TextPrimary, 
                                        lineHeight = 24.sp, 
                                        modifier = Modifier.padding(20.dp) 
                                    ) 
                                } 
                                
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { 
                                    val optionLabels = listOf("A", "B", "C", "D") 
                                    currentQuestion.options.forEachIndexed { optIdx, optText -> 
                                        val isSelected = selectedOpt == optIdx 
                                        
                                        val bgColor = if (isSelected) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceCard1 
                                        val borderColor = if (isSelected) BlossomColors.SakuraRose else BlossomColors.CardBorderSubtle 
                                        val textColor = if (isSelected) BlossomColors.TextPrimary else BlossomColors.TextSecondary 
                                        
                                        Surface( 
                                            onClick = { 
                                                userAnswers[currentQuestion.id] = optIdx 
                                            }, 
                                            shape = RoundedCornerShape(16.dp), 
                                            color = bgColor, 
                                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor), 
                                            modifier = Modifier.fillMaxWidth() 
                                        ) { 
                                            Row( 
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), 
                                                verticalAlignment = Alignment.CenterVertically 
                                            ) { 
                                                Surface( 
                                                    shape = CircleShape, 
                                                    color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.SurfaceCard2, 
                                                    modifier = Modifier.size(32.dp) 
                                                ) { 
                                                    Box(contentAlignment = Alignment.Center) { 
                                                        Text( 
                                                            text = optionLabels.getOrElse(optIdx) { "${optIdx + 1}" }, 
                                                            fontSize = 13.sp, 
                                                            fontWeight = FontWeight.Bold, 
                                                            color = if (isSelected) BlossomColors.BlossomWhite else BlossomColors.TextSecondary 
                                                        ) 
                                                    } 
                                                } 
                                                Spacer(modifier = Modifier.width(12.dp)) 
                                                Text( 
                                                    text = optText, 
                                                    fontSize = 15.sp, 
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium, 
                                                    color = textColor, 
                                                    modifier = Modifier.weight(1f) 
                                                ) 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                            
                            Column { 
                                Spacer(modifier = Modifier.height(20.dp)) 
                                val isFinalQuestion = currentIndex == questions.size - 1 
                                val hasAnsweredCurrent = userAnswers[currentQuestion.id] != null 
                                val nextButtonBackground = if (hasAnsweredCurrent) { 
                                    BlossomColors.SakuraRose.copy(alpha = 0.85f) 
                                } else { 
                                    BlossomColors.SurfaceElevated 
                                } 
                                val nextButtonBorder = if (hasAnsweredCurrent) { 
                                    BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)) 
                                } else { 
                                    BorderStroke(1.dp, BlossomColors.CardBorder) 
                                } 
                                val nextTextColor = if (hasAnsweredCurrent) { 
                                    BlossomColors.BlossomWhite 
                                } else { 
                                    BlossomColors.TextSecondary 
                                } 
                                
                                Row( 
                                    modifier = Modifier.fillMaxWidth(), 
                                    horizontalArrangement = Arrangement.spacedBy(10.dp) 
                                ) { 
                                    if (currentIndex > 0) { 
                                        Box( 
                                            modifier = Modifier 
                                                .weight(1f) 
                                                .height(52.dp) 
                                                .clip(RoundedCornerShape(20.dp)) 
                                                .background(BlossomColors.SurfaceElevated) 
                                                .border( 
                                                    BorderStroke(1.dp, BlossomColors.CardBorder), 
                                                    shape = RoundedCornerShape(20.dp) 
                                                ) 
                                                .clickable { 
                                                    currentIndex-- 
                                                }, 
                                            contentAlignment = Alignment.Center 
                                        ) { 
                                            Row( 
                                                verticalAlignment = Alignment.CenterVertically, 
                                                horizontalArrangement = Arrangement.Center 
                                            ) { 
                                                Icon( 
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                                    contentDescription = null, 
                                                    tint = BlossomColors.TextSecondary, 
                                                    modifier = Modifier.size(18.dp) 
                                                ) 
                                                Spacer(modifier = Modifier.width(6.dp)) 
                                                Text( 
                                                    text = "Previous", 
                                                    fontSize = 15.sp, 
                                                    fontWeight = FontWeight.SemiBold, 
                                                    color = BlossomColors.TextSecondary 
                                                ) 
                                            } 
                                        } 
                                    } 
                                    
                                    Box( 
                                        modifier = Modifier 
                                            .weight(if (currentIndex > 0) 1.5f else 1f) 
                                            .height(52.dp) 
                                            .clip(RoundedCornerShape(20.dp)) 
                                            .background(nextButtonBackground) 
                                            .border(nextButtonBorder, shape = RoundedCornerShape(20.dp)) 
                                            .clickable(enabled = hasAnsweredCurrent) { 
                                                if (!isFinalQuestion) { 
                                                    currentIndex++ 
                                                } else { 
                                                    isQuizCompleted = true 
                                                } 
                                            }, 
                                        contentAlignment = Alignment.Center 
                                    ) { 
                                        Row( 
                                            verticalAlignment = Alignment.CenterVertically, 
                                            horizontalArrangement = Arrangement.Center 
                                        ) { 
                                            Text( 
                                                text = if (isFinalQuestion) "Submit Quiz" else "Next Question", 
                                                fontSize = 15.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                color = nextTextColor 
                                            ) 
                                            Spacer(modifier = Modifier.width(8.dp)) 
                                            Icon( 
                                                imageVector = if (isFinalQuestion) Icons.Filled.Check else Icons.AutoMirrored.Filled.ArrowForward, 
                                                contentDescription = null, 
                                                tint = nextTextColor, 
                                                modifier = Modifier.size(18.dp) 
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
