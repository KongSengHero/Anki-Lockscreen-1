package com.ankilock.ui.reading

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
                    .padding(horizontal = 20.dp, vertical = 24.dp) 
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
                                Button( 
                                    onClick = { 
                                        userAnswers.clear() 
                                        currentIndex = 0 
                                        isQuizCompleted = false 
                                    }, 
                                    colors = ButtonDefaults.buttonColors( 
                                        containerColor = BlossomColors.SurfaceElevated 
                                    ), 
                                    shape = RoundedCornerShape(12.dp), 
                                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                                    modifier = Modifier.weight(1f) 
                                ) { 
                                    Text( 
                                        text = "Retake", 
                                        color = BlossomColors.TextPrimary, 
                                        fontWeight = FontWeight.SemiBold 
                                    ) 
                                } 
                                
                                Button( 
                                    onClick = onDismiss, 
                                    colors = ButtonDefaults.buttonColors( 
                                        containerColor = BlossomColors.SakuraRose 
                                    ), 
                                    shape = RoundedCornerShape(12.dp), 
                                    modifier = Modifier.weight(1f) 
                                ) { 
                                    Text( 
                                        text = "Done", 
                                        color = BlossomColors.BlossomWhite, 
                                        fontWeight = FontWeight.Bold 
                                    ) 
                                } 
                            } 
                        } 
                    } else { 
                        val currentQuestion = questions.getOrNull(currentIndex) ?: return@AnimatedContent 
                        val selectedOpt = userAnswers[currentQuestion.id] 
                        val isAnswered = selectedOpt != null 
                        
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
                                        val isCorrect = currentQuestion.correctOptionIndex == optIdx 
                                        
                                        val bgColor = when { 
                                            !isAnswered -> if (isSelected) BlossomColors.SurfaceElevated else BlossomColors.SurfaceCard1 
                                            isCorrect -> BlossomColors.BlossomGreenSurface 
                                            isSelected && !isCorrect -> BlossomColors.SakuraRoseContainer 
                                            else -> BlossomColors.SurfaceCard1 
                                        } 
                                        val borderColor = when { 
                                            !isAnswered -> if (isSelected) BlossomColors.SakuraRose else BlossomColors.CardBorder 
                                            isCorrect -> BlossomColors.BlossomGreen 
                                            isSelected && !isCorrect -> BlossomColors.BlossomRed 
                                            else -> BlossomColors.CardBorderSubtle 
                                        } 
                                        val textColor = when { 
                                            !isAnswered -> BlossomColors.TextPrimary 
                                            isCorrect -> BlossomColors.BlossomGreen 
                                            isSelected && !isCorrect -> BlossomColors.BlossomRed 
                                            else -> BlossomColors.TextSecondary 
                                        } 
                                        
                                        Surface( 
                                            onClick = { 
                                                if (!isAnswered) { 
                                                    userAnswers[currentQuestion.id] = optIdx 
                                                } 
                                            }, 
                                            shape = RoundedCornerShape(12.dp), 
                                            color = bgColor, 
                                            border = BorderStroke(1.dp, borderColor), 
                                            modifier = Modifier.fillMaxWidth() 
                                        ) { 
                                            Row( 
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp), 
                                                verticalAlignment = Alignment.CenterVertically 
                                            ) { 
                                                Surface( 
                                                    shape = CircleShape, 
                                                    color = if (isAnswered && isCorrect) BlossomColors.BlossomGreen.copy(alpha = 0.2f) 
                                                    else BlossomColors.SurfaceCard2, 
                                                    modifier = Modifier.size(28.dp) 
                                                ) { 
                                                    Box(contentAlignment = Alignment.Center) { 
                                                        Text( 
                                                            text = optionLabels.getOrElse(optIdx) { "${optIdx + 1}" }, 
                                                            fontSize = 12.sp, 
                                                            fontWeight = FontWeight.Bold, 
                                                            color = textColor 
                                                        ) 
                                                    } 
                                                } 
                                                Spacer(modifier = Modifier.width(12.dp)) 
                                                Text( 
                                                    text = optText, 
                                                    fontSize = 14.sp, 
                                                    color = textColor, 
                                                    modifier = Modifier.weight(1f) 
                                                ) 
                                                if (isAnswered && isCorrect) { 
                                                    Icon( 
                                                        Icons.Filled.Check, 
                                                        contentDescription = null, 
                                                        tint = BlossomColors.BlossomGreen, 
                                                        modifier = Modifier.size(20.dp) 
                                                    ) 
                                                } else if (isAnswered && isSelected && !isCorrect) { 
                                                    Icon( 
                                                        Icons.Filled.Close, 
                                                        contentDescription = null, 
                                                        tint = BlossomColors.BlossomRed, 
                                                        modifier = Modifier.size(20.dp) 
                                                    ) 
                                                } 
                                            } 
                                        } 
                                    } 
                                } 
                                
                                if (isAnswered && currentQuestion.explanation.isNotBlank()) { 
                                    Surface( 
                                        shape = RoundedCornerShape(10.dp), 
                                        color = BlossomColors.SurfaceCard2, 
                                        border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle), 
                                        modifier = Modifier.fillMaxWidth() 
                                    ) { 
                                        Text( 
                                            text = "💡 ${currentQuestion.explanation}", 
                                            fontSize = 12.sp, 
                                            color = BlossomColors.TextSecondary, 
                                            lineHeight = 18.sp, 
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp) 
                                        ) 
                                    } 
                                } 
                            } 
                            
                            Column { 
                                Spacer(modifier = Modifier.height(20.dp)) 
                                Button( 
                                    onClick = { 
                                        if (currentIndex < questions.size - 1) { 
                                            currentIndex++ 
                                        } else { 
                                            isQuizCompleted = true 
                                        } 
                                    }, 
                                    enabled = isAnswered, 
                                    colors = ButtonDefaults.buttonColors( 
                                        containerColor = BlossomColors.SakuraRose, 
                                        disabledContainerColor = BlossomColors.SurfaceElevated 
                                    ), 
                                    shape = RoundedCornerShape(14.dp), 
                                    modifier = Modifier 
                                        .fillMaxWidth() 
                                        .height(50.dp) 
                                ) { 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.Center 
                                    ) { 
                                        Text( 
                                            text = if (currentIndex < questions.size - 1) "Next Question" else "See Results", 
                                            fontSize = 15.sp, 
                                            fontWeight = FontWeight.Bold, 
                                            color = if (isAnswered) BlossomColors.BlossomWhite else BlossomColors.TextSecondary 
                                        ) 
                                        Spacer(modifier = Modifier.width(8.dp)) 
                                        Icon( 
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward, 
                                            contentDescription = null, 
                                            tint = if (isAnswered) BlossomColors.BlossomWhite else BlossomColors.TextSecondary, 
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
