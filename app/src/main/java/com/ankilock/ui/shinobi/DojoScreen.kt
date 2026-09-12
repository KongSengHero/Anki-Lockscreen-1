package com.ankilock.ui.shinobi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.StorySessionManager

@Composable
fun DojoScreen( 
    onOpenSettings: () -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val scrollState = rememberScrollState() 
    val savedCount = StorySessionManager.savedStoriesList.size 
    
    Column( 
        modifier = modifier 
            .fillMaxSize() 
            .background(ShinobiColors.BackgroundDeep) 
            .verticalScroll(scrollState) 
            .padding(16.dp) 
    ) { 
        Row( 
            modifier = Modifier.fillMaxWidth(), 
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.SpaceBetween 
        ) { 
            Column { 
                Text( 
                    "Shinobi • Dojo", 
                    fontSize = 22.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = ShinobiColors.TextPrimary 
                ) 
                Text( 
                    "Level up your Japanese proficiency", 
                    fontSize = 12.sp, 
                    color = ShinobiColors.TextSecondary 
                ) 
            } 
            
            ShinobiSquircleButton( 
                onClick = onOpenSettings, 
                icon = Icons.Default.Settings, 
                contentDescription = "Settings", 
                borderColor = ShinobiColors.CardBorder 
            ) 
        } 
        
        Spacer(modifier = Modifier.height(16.dp)) 
        
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .clip(ShinobiShapes.SquircleLarge) 
                .background( 
                    Brush.verticalGradient( 
                        listOf( 
                            Color(0xFF2E1065), 
                            Color(0xFF1E1B4B) 
                        ) 
                    ) 
                ) 
                .border(1.dp, ShinobiColors.VioletPurple.copy(alpha = 0.5f), ShinobiShapes.SquircleLarge) 
                .padding(20.dp) 
        ) { 
            Column { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        Box( 
                            modifier = Modifier 
                                .size(48.dp) 
                                .clip(CircleShape) 
                                .background(ShinobiColors.VioletPurple), 
                            contentAlignment = Alignment.Center 
                        ) { 
                            Icon( 
                                Icons.Default.AutoAwesome, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(26.dp) 
                            ) 
                        } 
                        Spacer(modifier = Modifier.width(12.dp)) 
                        Column { 
                            Text( 
                                "Rank: Tetsu III (Iron)", 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White 
                            ) 
                            Text( 
                                "Starter Level • 350 / 500 XP", 
                                fontSize = 12.sp, 
                                color = Color(0xFFDDD6FE) 
                            ) 
                        } 
                    } 
                    
                    ShinobiPillBadge( 
                        text = "LVL 3", 
                        textColor = Color.White, 
                        backgroundColor = ShinobiColors.VioletPurpleLip 
                    ) 
                } 
                
                Spacer(modifier = Modifier.height(16.dp)) 
                
                LinearProgressIndicator( 
                    progress = { 0.7f }, 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(8.dp) 
                        .clip(ShinobiShapes.Pill), 
                    color = ShinobiColors.VioletPurple, 
                    trackColor = Color(0xFF4C1D95).copy(alpha = 0.5f) 
                ) 
            } 
        } 
        
        Spacer(modifier = Modifier.height(16.dp)) 
        
        Row( 
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.spacedBy(10.dp) 
        ) { 
            DojoStatCard( 
                title = "Streak", 
                value = "5 Days", 
                icon = Icons.Default.LocalFireDepartment, 
                color = ShinobiColors.CoralRed, 
                modifier = Modifier.weight(1f) 
            ) 
            DojoStatCard( 
                title = "Stories", 
                value = "$savedCount Read", 
                icon = Icons.AutoMirrored.Filled.MenuBook, 
                color = ShinobiColors.EmeraldGreen, 
                modifier = Modifier.weight(1f) 
            ) 
            DojoStatCard( 
                title = "Total XP", 
                value = "1,250", 
                icon = Icons.Default.Star, 
                color = ShinobiColors.WarmAmber, 
                modifier = Modifier.weight(1f) 
            ) 
        } 
        
        Spacer(modifier = Modifier.height(18.dp)) 
        
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .clip(ShinobiShapes.SquircleLarge) 
                .background(ShinobiColors.SurfaceCard1) 
                .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.SquircleLarge) 
                .padding(18.dp) 
        ) { 
            Column { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Text( 
                        "Daily Shinobi Quests", 
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = ShinobiColors.TextPrimary 
                    ) 
                    ShinobiPillBadge( 
                        text = "Resets in 12h", 
                        textColor = ShinobiColors.TextSecondary 
                    ) 
                } 
                
                Spacer(modifier = Modifier.height(14.dp)) 
                
                DailyQuestItem( 
                    title = "Read 1 Shinobi Story", 
                    xp = 50, 
                    isCompleted = savedCount > 0 
                ) 
                Spacer(modifier = Modifier.height(10.dp)) 
                DailyQuestItem( 
                    title = "Score 100% on Comprehension Quiz", 
                    xp = 100, 
                    isCompleted = false 
                ) 
                Spacer(modifier = Modifier.height(10.dp)) 
                DailyQuestItem( 
                    title = "Mine 5 new words to Vocab Bank", 
                    xp = 50, 
                    isCompleted = true 
                ) 
            } 
        } 
        
        Spacer(modifier = Modifier.height(18.dp)) 
        
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .clip(ShinobiShapes.SquircleLarge) 
                .background(ShinobiColors.SurfaceCard1) 
                .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.SquircleLarge) 
                .padding(18.dp) 
        ) { 
            Column { 
                Text( 
                    "Shinobi Rank Progression", 
                    fontSize = 15.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = ShinobiColors.TextPrimary 
                ) 
                Spacer(modifier = Modifier.height(14.dp)) 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    listOf( 
                        "Tetsu" to ShinobiColors.ElectricBlue, 
                        "Do" to ShinobiColors.WarmAmber, 
                        "Gin" to Color(0xFFE2E8F0), 
                        "Kin" to Color(0xFFFBBF24), 
                        "Hisui" to ShinobiColors.EmeraldGreen 
                    ).forEach { (rankName, color) -> 
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                            Box( 
                                modifier = Modifier 
                                    .size(44.dp) 
                                    .clip(ShinobiShapes.SquircleSmall) 
                                    .background(color.copy(alpha = 0.2f)) 
                                    .border(1.dp, color, ShinobiShapes.SquircleSmall), 
                                contentAlignment = Alignment.Center 
                            ) { 
                                Icon( 
                                    Icons.Default.EmojiEvents, 
                                    contentDescription = rankName, 
                                    tint = color, 
                                    modifier = Modifier.size(20.dp) 
                                ) 
                            } 
                            Spacer(modifier = Modifier.height(4.dp)) 
                            Text( 
                                text = rankName, 
                                fontSize = 11.sp, 
                                color = ShinobiColors.TextSecondary, 
                                fontWeight = FontWeight.Medium 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
        
        Spacer(modifier = Modifier.height(28.dp)) 
    } 
} 

@Composable
fun DojoStatCard( 
    title: String, 
    value: String, 
    icon: ImageVector, 
    color: Color, 
    modifier: Modifier = Modifier 
) { 
    Box( 
        modifier = modifier 
            .clip(ShinobiShapes.SquircleMedium) 
            .background(ShinobiColors.SurfaceCard1) 
            .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.SquircleMedium) 
            .padding(12.dp) 
    ) { 
        Column { 
            Icon( 
                imageVector = icon, 
                contentDescription = null, 
                tint = color, 
                modifier = Modifier.size(22.dp) 
            ) 
            Spacer(modifier = Modifier.height(8.dp)) 
            Text( 
                text = value, 
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.White 
            ) 
            Text( 
                text = title, 
                fontSize = 11.sp, 
                color = ShinobiColors.TextSecondary 
            ) 
        } 
    } 
} 

@Composable
fun DailyQuestItem( 
    title: String, 
    xp: Int, 
    isCompleted: Boolean 
) { 
    Row( 
        modifier = Modifier 
            .fillMaxWidth() 
            .clip(ShinobiShapes.SquircleSmall) 
            .background(if (isCompleted) Color(0xFF133820) else ShinobiColors.SurfaceCard2) 
            .border( 
                width = 1.dp, 
                color = if (isCompleted) ShinobiColors.EmeraldGreen.copy(alpha = 0.4f) else ShinobiColors.CardBorder, 
                shape = ShinobiShapes.SquircleSmall 
            ) 
            .padding(horizontal = 12.dp, vertical = 10.dp), 
        verticalAlignment = Alignment.CenterVertically, 
        horizontalArrangement = Arrangement.SpaceBetween 
    ) { 
        Row( 
            verticalAlignment = Alignment.CenterVertically, 
            modifier = Modifier.weight(1f) 
        ) { 
            Icon( 
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, 
                contentDescription = null, 
                tint = if (isCompleted) ShinobiColors.EmeraldGreen else ShinobiColors.TextMuted, 
                modifier = Modifier.size(18.dp) 
            ) 
            Spacer(modifier = Modifier.width(10.dp)) 
            Text( 
                text = title, 
                fontSize = 13.sp, 
                color = if (isCompleted) Color.White else ShinobiColors.TextSecondary, 
                fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal 
            ) 
        } 
        
        ShinobiPillBadge( 
            text = "+$xp XP", 
            textColor = if (isCompleted) Color(0xFF86EFAC) else ShinobiColors.WarmAmber, 
            backgroundColor = if (isCompleted) Color(0xFF0F2916) else ShinobiColors.WarmAmberSurface 
        ) 
    } 
} 
