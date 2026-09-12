package com.ankilock.ui.shinobi.story
    
import com.ankilock.util.AudioPlayerHelper
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.ForgedStory
import com.ankilock.data.StoryQuestion
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiNunito
import com.ankilock.ui.shinobi.ShinobiShapes
import com.ankilock.ui.shinobi.ShinobiSoundEffects
import com.ankilock.ui.shinobi.ShinobiSquircleButton
import com.ankilock.ui.shinobi.ShinobiTactileButton
import com.ankilock.ui.shinobi.ShinobiTextButton
import com.ankilock.ui.shinobi.TactileButtonVariant
    
@Composable
fun ShinobiExercisesScreen( 
    story: ForgedStory, 
    onCompleteQuiz: (correctCount: Int, totalCount: Int) -> Unit, 
    onExitQuiz: () -> Unit, 
    audioPlayer: AudioPlayerHelper? = null, 
    modifier: Modifier = Modifier 
) { 
    val questions = story.questions 
    if (questions.isEmpty()) { 
        onCompleteQuiz(0, 0) 
        return 
    } 
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) } 
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) } 
    var isSubmitted by remember { mutableStateOf(false) } 
    var correctAnswersCount by remember { mutableIntStateOf(0) } 
    
    val totalCount = questions.size 
    val currentQuestion = questions.getOrElse(currentQuestionIndex) { questions[0] } 
    val isCorrect = selectedOptionIndex == currentQuestion.correctOptionIndex 
    
    val animatedProgress by animateFloatAsState( 
        targetValue = (currentQuestionIndex.toFloat()) / totalCount.toFloat(), 
        label = "quizProgressBar" 
    ) 
    
    val scrollState = rememberScrollState() 
    
    Box( 
        modifier = modifier 
            .fillMaxSize() 
            .background(ShinobiColors.Stone950) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .padding(bottom = if (isSubmitted) 160.dp else 90.dp) 
                .verticalScroll(scrollState) 
                .padding(horizontal = 20.dp, vertical = 14.dp) 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(16.dp) 
            ) { 
                ShinobiSquircleButton( 
                    onClick = onExitQuiz, 
                    icon = Icons.Default.Close, 
                    contentDescription = "Exit Quiz", 
                    size = 40.dp 
                ) 
                
                Box( 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(10.dp) 
                        .clip(ShinobiShapes.Pill) 
                        .background(ShinobiColors.Stone900) 
                        .border(1.dp, ShinobiColors.Stone700, ShinobiShapes.Pill) 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .fillMaxHeight() 
                            .fillMaxWidth(animatedProgress) 
                            .background(ShinobiColors.ShinobiGreen) 
                    ) 
                } 
                
                Text( 
                    text = "${currentQuestionIndex + 1}/$totalCount", 
                    color = ShinobiColors.TextSecondary, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = ShinobiNunito 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(28.dp)) 
            
            Text( 
                text = currentQuestion.questionText, 
                color = Color.White, 
                fontSize = 20.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = ShinobiNunito, 
                lineHeight = 28.sp 
            ) 
            
            Spacer(modifier = Modifier.height(24.dp)) 
            
            val isTrueFalse = currentQuestion.options.size == 2 && 
                (currentQuestion.options.any { it.equals("true", ignoreCase = true) || it == "正しい" || it == "○" }) 
            
            if (isTrueFalse) { 
                Row( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(top = 20.dp), 
                    horizontalArrangement = Arrangement.SpaceEvenly 
                ) { 
                    currentQuestion.options.forEachIndexed { optIndex, optText -> 
                        val isSelected = selectedOptionIndex == optIndex 
                        val isTrueOpt = optText.equals("true", ignoreCase = true) || optText == "正しい" || optText == "○" 
                        val optColor = if (isTrueOpt) ShinobiColors.ShinobiGreen else ShinobiColors.ShinobiRed 
                        val optLip = if (isTrueOpt) ShinobiColors.ShinobiGreenLip else ShinobiColors.ShinobiRedLip 
                        
                        Box( 
                            modifier = Modifier 
                                .size(96.dp) 
                                .clip(CircleShape) 
                                .background(optLip) 
                                .clickable(enabled = !isSubmitted) { 
                                    selectedOptionIndex = optIndex 
                                }, 
                            contentAlignment = Alignment.TopCenter 
                        ) { 
                            Box( 
                                modifier = Modifier 
                                    .size(96.dp) 
                                    .padding(bottom = if (isSelected) 0.dp else 5.dp) 
                                    .clip(CircleShape) 
                                    .background(if (isSelected) optColor else ShinobiColors.Stone800) 
                                    .border( 
                                        width = if (isSelected) 3.dp else 2.dp, 
                                        color = if (isSelected) Color.White else ShinobiColors.Stone600, 
                                        shape = CircleShape 
                                    ), 
                                contentAlignment = Alignment.Center 
                            ) { 
                                Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                                    Icon( 
                                        imageVector = if (isTrueOpt) Icons.Default.Check else Icons.Default.Close, 
                                        contentDescription = optText, 
                                        tint = if (isSelected) Color.White else optColor, 
                                        modifier = Modifier.size(32.dp) 
                                    ) 
                                    Text( 
                                        text = optText, 
                                        color = if (isSelected) Color.White else ShinobiColors.TextSecondary, 
                                        fontSize = 13.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        fontFamily = ShinobiNunito 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
            } else { 
                Column( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalArrangement = Arrangement.spacedBy(12.dp) 
                ) { 
                    currentQuestion.options.forEachIndexed { optIndex, optText -> 
                        val isSelected = selectedOptionIndex == optIndex 
                        
                        Box( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .clip(ShinobiShapes.SquircleMedium) 
                                .background(if (isSelected) ShinobiColors.ShinobiBlueLip else ShinobiColors.Stone700) 
                                .clickable(enabled = !isSubmitted) { 
                                    selectedOptionIndex = optIndex 
                                } 
                        ) { 
                            Box( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .padding(bottom = if (isSelected) 0.dp else 3.dp) 
                                    .clip(ShinobiShapes.SquircleMedium) 
                                    .background(if (isSelected) ShinobiColors.ShinobiBlue else ShinobiColors.Stone800) 
                                    .border( 
                                        width = 2.dp, 
                                        color = if (isSelected) ShinobiColors.ShinobiBlueLip else ShinobiColors.Stone600, 
                                        shape = ShinobiShapes.SquircleMedium 
                                    ) 
                                    .padding(horizontal = 18.dp, vertical = 16.dp) 
                            ) { 
                                Row( 
                                    modifier = Modifier.fillMaxWidth(), 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.spacedBy(14.dp) 
                                ) { 
                                    Box( 
                                        modifier = Modifier 
                                            .size(28.dp) 
                                            .clip(CircleShape) 
                                            .background(if (isSelected) Color.White.copy(alpha = 0.2f) else ShinobiColors.Stone900) 
                                            .border(1.5.dp, if (isSelected) Color.White else ShinobiColors.Stone600, CircleShape), 
                                        contentAlignment = Alignment.Center 
                                    ) { 
                                        Text( 
                                            text = "${('A'.code + optIndex).toChar()}", 
                                            color = if (isSelected) Color.White else ShinobiColors.TextSecondary, 
                                            fontSize = 13.sp, 
                                            fontWeight = FontWeight.Bold, 
                                            fontFamily = ShinobiNunito 
                                        ) 
                                    } 
                                    Text( 
                                        text = optText, 
                                        color = if (isSelected) Color.White else ShinobiColors.TextPrimary, 
                                        fontSize = 16.sp, 
                                        fontWeight = FontWeight.SemiBold, 
                                        fontFamily = ShinobiNunito, 
                                        lineHeight = 22.sp 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
        } 
        
        if (!isSubmitted) { 
            Box( 
                modifier = Modifier 
                    .align(Alignment.BottomCenter) 
                    .fillMaxWidth() 
                    .background(ShinobiColors.Stone950) 
                    .border( 
                        width = 1.dp, 
                        color = ShinobiColors.Stone800, 
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) 
                    ) 
                    .padding(horizontal = 20.dp, vertical = 16.dp) 
            ) { 
                ShinobiTextButton( 
                    text = "CHECK", 
                    onClick = { 
                        if (selectedOptionIndex != null) { 
                            isSubmitted = true 
                            if (isCorrect) { 
                                correctAnswersCount++ 
                                ShinobiSoundEffects.playSuccess() 
                            } else { 
                                ShinobiSoundEffects.playError() 
                            } 
                        } 
                    }, 
                    modifier = Modifier.fillMaxWidth(), 
                    variant = if (selectedOptionIndex != null) TactileButtonVariant.SUCCESS else TactileButtonVariant.OUTLINE, 
                    enabled = selectedOptionIndex != null 
                ) 
            } 
        } 
        
        AnimatedVisibility( 
            visible = isSubmitted, 
            enter = slideInVertically(initialOffsetY = { it }), 
            exit = slideOutVertically(targetOffsetY = { it }), 
            modifier = Modifier.align(Alignment.BottomCenter) 
        ) { 
            val sheetBg = if (isCorrect) ShinobiColors.ShinobiGreenSurface else Color(0xFF2B1416) 
            val accentColor = if (isCorrect) ShinobiColors.ShinobiGreen else ShinobiColors.ShinobiRed 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) 
                    .background(sheetBg) 
                    .border( 
                        width = 2.dp, 
                        color = accentColor.copy(alpha = 0.5f), 
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp) 
                    ) 
                    .padding(horizontal = 20.dp, vertical = 16.dp) 
            ) { 
                Column(modifier = Modifier.fillMaxWidth()) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        Box( 
                            modifier = Modifier 
                                .size(32.dp) 
                                .clip(CircleShape) 
                                .background(accentColor), 
                            contentAlignment = Alignment.Center 
                        ) { 
                            Icon( 
                                imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                        } 
                        Text( 
                            text = if (isCorrect) "Great job!" else "Incorrect", 
                            color = accentColor, 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito 
                        ) 
                    } 
                    
                    if (!isCorrect) { 
                        Spacer(modifier = Modifier.height(8.dp)) 
                        val correctText = currentQuestion.options.getOrNull(currentQuestion.correctOptionIndex) ?: "" 
                        Text( 
                            text = "Correct answer: $correctText", 
                            color = Color.White, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = ShinobiNunito 
                        ) 
                    } 
                    
                    if (currentQuestion.explanation.isNotBlank()) { 
                        Spacer(modifier = Modifier.height(6.dp)) 
                        Text( 
                            text = currentQuestion.explanation, 
                            color = ShinobiColors.TextSecondary, 
                            fontSize = 13.sp, 
                            fontFamily = ShinobiNunito, 
                            lineHeight = 18.sp 
                        ) 
                    } 
                    
                    Spacer(modifier = Modifier.height(16.dp)) 
                    
                    ShinobiTextButton( 
                        text = "CONTINUE", 
                        onClick = { 
                            if (currentQuestionIndex < totalCount - 1) { 
                                currentQuestionIndex++ 
                                selectedOptionIndex = null 
                                isSubmitted = false 
                            } else { 
                                onCompleteQuiz(correctAnswersCount, totalCount) 
                            } 
                        }, 
                        modifier = Modifier.fillMaxWidth(), 
                        variant = if (isCorrect) TactileButtonVariant.SUCCESS else TactileButtonVariant.DANGER 
                    ) 
                } 
            } 
        } 
    } 
} 
