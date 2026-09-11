package com.ankilock.ui.blossom.story
    
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh 
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.ForgedStory
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomNunito
import com.ankilock.ui.blossom.BlossomShapes
import com.ankilock.ui.blossom.BlossomSoundEffects
import com.ankilock.ui.blossom.BlossomTextButton
import com.ankilock.ui.blossom.StoryArtworkThumbnail
import com.ankilock.ui.blossom.TactileButtonVariant
    
@OptIn(ExperimentalLayoutApi::class) 
@Composable
fun BlossomStoryCompletedScreen( 
    story: ForgedStory, 
    correctCount: Int, 
    totalCount: Int, 
    onContinue: () -> Unit, 
    onReadAgain: () -> Unit, 
    onRetryQuiz: () -> Unit = onReadAgain, 
    isPassed: Boolean = true, 
    modifier: Modifier = Modifier 
) { 
    val scrollState = rememberScrollState() 
    
    LaunchedEffect(isPassed) { 
        if (isPassed) { 
            BlossomSoundEffects.playCompleted() 
        } 
    } 
    
    val percentage = if (totalCount > 0) ((correctCount.toFloat() / totalCount) * 100).toInt() else 100 
    
    Box( 
        modifier = modifier 
            .fillMaxSize() 
            .background(BlossomColors.Stone950) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .verticalScroll(scrollState) 
                .padding(horizontal = 20.dp, vertical = 24.dp), 
            horizontalAlignment = Alignment.CenterHorizontally 
        ) { 
            Spacer(modifier = Modifier.height(10.dp)) 
            
            Box( 
                modifier = Modifier 
                    .size(90.dp) 
                    .clip(CircleShape) 
                    .background(if (isPassed) BlossomColors.BlossomAmberSurface else BlossomColors.MutedRose.copy(alpha = 0.15f)) 
                    .border(2.dp, if (isPassed) BlossomColors.BlossomAmber else BlossomColors.MutedRose, CircleShape), 
                contentAlignment = Alignment.Center 
            ) { 
                Icon( 
                    imageVector = if (isPassed) Icons.Default.Star else Icons.Default.Refresh, 
                    contentDescription = null, 
                    tint = if (isPassed) BlossomColors.BlossomAmber else BlossomColors.MutedRose, 
                    modifier = Modifier.size(52.dp) 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(20.dp)) 
            
            Text( 
                text = if (isPassed) "よくできました！" else "もう一息！", 
                color = if (isPassed) BlossomColors.BlossomAmber else BlossomColors.MutedRose, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = BlossomNunito 
            ) 
            
            Spacer(modifier = Modifier.height(6.dp)) 
            
            Text( 
                text = if (isPassed) "Story Completed!" else "Quiz Not Passed", 
                color = Color.White, 
                fontSize = 28.sp, 
                fontWeight = FontWeight.ExtraBold, 
                fontFamily = BlossomNunito, 
                textAlign = TextAlign.Center 
            ) 
            
            Text( 
                text = if (isPassed) story.title else "Score: $percentage% (Requires 70% to pass)", 
                color = BlossomColors.TextSecondary, 
                fontSize = 15.sp, 
                fontFamily = BlossomNunito, 
                textAlign = TextAlign.Center 
            ) 
            
            Spacer(modifier = Modifier.height(26.dp)) 
            
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(12.dp) 
            ) { 
                Box( 
                    modifier = Modifier 
                        .weight(1f) 
                        .clip(BlossomShapes.SquircleMedium) 
                        .background(if (isPassed) BlossomColors.BlossomAmberSurface else BlossomColors.Stone900) 
                        .border(1.5.dp, if (isPassed) BlossomColors.BlossomAmber.copy(alpha = 0.6f) else BlossomColors.Stone700, BlossomShapes.SquircleMedium) 
                        .padding(vertical = 16.dp), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                        Text( 
                            text = if (isPassed) "+50 XP" else "0 XP", 
                            color = if (isPassed) BlossomColors.BlossomAmber else BlossomColors.TextMuted, 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = BlossomNunito 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            text = if (isPassed) "EARNED" else "TRY AGAIN", 
                            color = BlossomColors.TextSecondary, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            letterSpacing = 1.sp, 
                            fontFamily = BlossomNunito 
                        ) 
                    } 
                } 
                
                Box( 
                    modifier = Modifier 
                        .weight(1f) 
                        .clip(BlossomShapes.SquircleMedium) 
                        .background(BlossomColors.BlossomGreenSurface) 
                        .border(1.5.dp, BlossomColors.BlossomGreen.copy(alpha = 0.6f), BlossomShapes.SquircleMedium) 
                        .padding(vertical = 16.dp), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                        Text( 
                            text = "$percentage%", 
                            color = BlossomColors.BlossomGreen, 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = BlossomNunito 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            text = "$correctCount / $totalCount ACCURACY", 
                            color = BlossomColors.TextSecondary, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            letterSpacing = 0.5.sp, 
                            fontFamily = BlossomNunito 
                        ) 
                    } 
                } 
            } 
            
            if (story.targetWords.isNotEmpty()) { 
                Spacer(modifier = Modifier.height(24.dp)) 
                
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(BlossomShapes.SquircleMedium) 
                        .background(BlossomColors.Stone900) 
                        .border(1.5.dp, BlossomColors.Stone700, BlossomShapes.SquircleMedium) 
                        .padding(16.dp) 
                ) { 
                    Column(modifier = Modifier.fillMaxWidth()) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(8.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.CheckCircle, 
                                contentDescription = null, 
                                tint = BlossomColors.BlossomBlue, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                            Text( 
                                text = "VOCABULARY PRACTICED", 
                                color = BlossomColors.TextSecondary, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                letterSpacing = 1.sp, 
                                fontFamily = BlossomNunito 
                            ) 
                        } 
                        
                        Spacer(modifier = Modifier.height(12.dp)) 
                        
                        FlowRow( 
                            horizontalArrangement = Arrangement.spacedBy(8.dp), 
                            verticalArrangement = Arrangement.spacedBy(8.dp) 
                        ) { 
                            story.targetWords.forEach { word -> 
                                Box( 
                                    modifier = Modifier 
                                        .clip(BlossomShapes.SquircleSmall) 
                                        .background(BlossomColors.Stone800) 
                                        .border(1.dp, BlossomColors.Stone600, BlossomShapes.SquircleSmall) 
                                        .padding(horizontal = 10.dp, vertical = 6.dp) 
                                ) { 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.spacedBy(6.dp) 
                                    ) { 
                                        Text( 
                                            text = word.surface, 
                                            color = Color.White, 
                                            fontSize = 14.sp, 
                                            fontWeight = FontWeight.Bold 
                                        ) 
                                        if (word.english.isNotBlank()) { 
                                            Text( 
                                                text = word.english, 
                                                color = BlossomColors.TextSecondary, 
                                                fontSize = 12.sp, 
                                                fontFamily = BlossomNunito 
                                            ) 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(32.dp)) 
            
            Column( 
                modifier = Modifier.fillMaxWidth(), 
                verticalArrangement = Arrangement.spacedBy(12.dp) 
            ) { 
                BlossomTextButton( 
                    text = if (isPassed) "CONTINUE" else "RETRY QUIZ", 
                    onClick = if (isPassed) onContinue else onRetryQuiz, 
                    modifier = Modifier.fillMaxWidth(), 
                    variant = TactileButtonVariant.PRIMARY 
                ) 
                
                BlossomTextButton( 
                    text = "READ AGAIN", 
                    onClick = onReadAgain, 
                    modifier = Modifier.fillMaxWidth(), 
                    variant = TactileButtonVariant.OUTLINE 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(20.dp)) 
        } 
    } 
} 
