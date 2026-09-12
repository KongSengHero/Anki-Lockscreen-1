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
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiNunito
import com.ankilock.ui.shinobi.ShinobiShapes
import com.ankilock.ui.shinobi.ShinobiSoundEffects
import com.ankilock.ui.shinobi.ShinobiTextButton
import com.ankilock.ui.shinobi.StoryArtworkThumbnail
import com.ankilock.ui.shinobi.TactileButtonVariant
    
@OptIn(ExperimentalLayoutApi::class) 
@Composable
fun ShinobiStoryCompletedScreen( 
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
    
    LaunchedEffect(Unit) { 
        ShinobiSoundEffects.playCompleted() 
    } 
    
    val percentage = if (totalCount > 0) ((correctCount.toFloat() / totalCount) * 100).toInt() else 100 
    
    Box( 
        modifier = modifier 
            .fillMaxSize() 
            .background(ShinobiColors.Stone950) 
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
                    .background(ShinobiColors.ShinobiAmberSurface) 
                    .border(2.dp, ShinobiColors.ShinobiAmber, CircleShape), 
                contentAlignment = Alignment.Center 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Star, 
                    contentDescription = null, 
                    tint = ShinobiColors.ShinobiAmber, 
                    modifier = Modifier.size(52.dp) 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(20.dp)) 
            
            Text( 
                text = "よくできました！", 
                color = ShinobiColors.ShinobiAmber, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = ShinobiNunito 
            ) 
            
            Spacer(modifier = Modifier.height(6.dp)) 
            
            Text( 
                text = "Story Completed!", 
                color = Color.White, 
                fontSize = 28.sp, 
                fontWeight = FontWeight.ExtraBold, 
                fontFamily = ShinobiNunito, 
                textAlign = TextAlign.Center 
            ) 
            
            Text( 
                text = story.title, 
                color = ShinobiColors.TextSecondary, 
                fontSize = 15.sp, 
                fontFamily = ShinobiNunito, 
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
                        .clip(ShinobiShapes.SquircleMedium) 
                        .background(ShinobiColors.ShinobiAmberSurface) 
                        .border(1.5.dp, ShinobiColors.ShinobiAmber.copy(alpha = 0.6f), ShinobiShapes.SquircleMedium) 
                        .padding(vertical = 16.dp), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                        Text( 
                            text = "+50 XP", 
                            color = ShinobiColors.ShinobiAmber, 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            text = "EARNED", 
                            color = ShinobiColors.TextSecondary, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            letterSpacing = 1.sp, 
                            fontFamily = ShinobiNunito 
                        ) 
                    } 
                } 
                
                Box( 
                    modifier = Modifier 
                        .weight(1f) 
                        .clip(ShinobiShapes.SquircleMedium) 
                        .background(ShinobiColors.ShinobiGreenSurface) 
                        .border(1.5.dp, ShinobiColors.ShinobiGreen.copy(alpha = 0.6f), ShinobiShapes.SquircleMedium) 
                        .padding(vertical = 16.dp), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                        Text( 
                            text = "$percentage%", 
                            color = ShinobiColors.ShinobiGreen, 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            text = "$correctCount / $totalCount ACCURACY", 
                            color = ShinobiColors.TextSecondary, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            letterSpacing = 0.5.sp, 
                            fontFamily = ShinobiNunito 
                        ) 
                    } 
                } 
            } 
            
            if (story.targetWords.isNotEmpty()) { 
                Spacer(modifier = Modifier.height(24.dp)) 
                
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(ShinobiShapes.SquircleMedium) 
                        .background(ShinobiColors.Stone900) 
                        .border(1.5.dp, ShinobiColors.Stone700, ShinobiShapes.SquircleMedium) 
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
                                tint = ShinobiColors.ShinobiBlue, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                            Text( 
                                text = "VOCABULARY PRACTICED", 
                                color = ShinobiColors.TextSecondary, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                letterSpacing = 1.sp, 
                                fontFamily = ShinobiNunito 
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
                                        .clip(ShinobiShapes.SquircleSmall) 
                                        .background(ShinobiColors.Stone800) 
                                        .border(1.dp, ShinobiColors.Stone600, ShinobiShapes.SquircleSmall) 
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
                                                color = ShinobiColors.TextSecondary, 
                                                fontSize = 12.sp, 
                                                fontFamily = ShinobiNunito 
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
                ShinobiTextButton( 
                    text = "CONTINUE", 
                    onClick = onContinue, 
                    modifier = Modifier.fillMaxWidth(), 
                    variant = TactileButtonVariant.PRIMARY 
                ) 
                
                ShinobiTextButton( 
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
