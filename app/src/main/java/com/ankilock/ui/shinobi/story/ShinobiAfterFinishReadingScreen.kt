package com.ankilock.ui.shinobi.story
    
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiNunito
import com.ankilock.ui.shinobi.ShinobiShapes
import com.ankilock.data.ForgedStory
import com.ankilock.ui.shinobi.ShinobiTactileButton
    
@Composable
fun ShinobiAfterFinishReadingScreen( 
    story: ForgedStory? = null, 
    onTakeQuiz: () -> Unit, 
    onBackToStory: () -> Unit, 
    modifier: Modifier = Modifier, 
    estimatedMinutes: Int = 4 
) { 
    Box( 
        modifier = modifier 
            .fillMaxSize() 
            .background(Color(0xFF0C0A09)) 
            .statusBarsPadding() 
            .navigationBarsPadding() 
            .padding(horizontal = 24.dp, vertical = 20.dp) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .align(Alignment.Center), 
            horizontalAlignment = Alignment.CenterHorizontally 
        ) { 
            Text( 
                text = "It's time to prove your skills!", 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Black, 
                fontFamily = ShinobiNunito, 
                color = Color.White, 
                textAlign = TextAlign.Center, 
                lineHeight = 36.sp 
            ) 
            
            Spacer(modifier = Modifier.height(16.dp)) 
            
            Text( 
                text = "Take a quick quiz to validate your understanding of the story and unlock the next one.", 
                fontSize = 17.sp, 
                fontWeight = FontWeight.Medium, 
                fontFamily = ShinobiNunito, 
                color = Color(0xFFD4D4D8), 
                textAlign = TextAlign.Center, 
                lineHeight = 25.sp 
            ) 
            
            Spacer(modifier = Modifier.height(28.dp)) 
            
            Row( 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(8.dp) 
            ) { 
                Text( 
                    text = "Time to complete:", 
                    fontSize = 17.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = ShinobiNunito, 
                    color = Color.White, 
                    textDecoration = TextDecoration.Underline 
                ) 
                
                Box( 
                    modifier = Modifier 
                        .clip(ShinobiShapes.Pill) 
                        .background(Color(0xFF14532D)) 
                        .padding(horizontal = 12.dp, vertical = 5.dp) 
                ) { 
                    Text( 
                        text = "~ $estimatedMinutes mins", 
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontFamily = ShinobiNunito, 
                        color = Color(0xFF86EFAC) 
                    ) 
                } 
            } 
        } 
        
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .align(Alignment.BottomCenter), 
            verticalArrangement = Arrangement.spacedBy(14.dp) 
        ) { 
            ShinobiTactileButton( 
                onClick = onTakeQuiz, 
                modifier = Modifier.fillMaxWidth(), 
                faceColor = ShinobiColors.ElectricBlue, 
                lipColor = ShinobiColors.ElectricBlueLip, 
                buttonHeight = 56.dp 
            ) { 
                Text( 
                    text = "TAKE THE QUIZ", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Black, 
                    fontFamily = ShinobiNunito, 
                    color = Color.White 
                ) 
            } 
            
            ShinobiTactileButton( 
                onClick = onBackToStory, 
                modifier = Modifier.fillMaxWidth(), 
                faceColor = Color(0xFF27272A), 
                lipColor = Color(0xFF18181B), 
                buttonHeight = 56.dp 
            ) { 
                Text( 
                    text = "BACK TO STORY", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Black, 
                    fontFamily = ShinobiNunito, 
                    color = Color(0xFFD4D4D8) 
                ) 
            } 
        } 
    } 
} 
