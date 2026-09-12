package com.ankilock.ui.shinobi

import android.graphics.Matrix
import android.graphics.SweepGradient
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.drawscope.rotate
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent 
import androidx.compose.ui.draw.shadow 
import androidx.compose.ui.geometry.CornerRadius 
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush 
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.graphics.Paint 
import androidx.compose.ui.graphics.drawscope.Stroke 
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas 
import androidx.compose.ui.graphics.nativeCanvas 
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.ForgedStory
import com.ankilock.data.StoryQuestion
import com.ankilock.data.StorySentenceItem

data class ShinobiStoryModel( 
    val id: String, 
    val levelId: String = "starter", 
    val subLevel: Int = 1, 
    val storyIndex: Int = 1, 
    val titleJapanese: String, 
    val titleEnglish: String, 
    val category: String, 
    val estimatedMinutes: Int = 5, 
    val xp: Int = 100, 
    val isEndingCard: Boolean = false, 
    val isCompleted: Boolean = false, 
    val isLocked: Boolean = false, 
    val isCurrent: Boolean = false, 
    val artworkType: String = "romance", 
    val summary: String = "", 
    val author: String = "", 
    val sentences: List<StorySentenceItem> = emptyList(), 
    val questions: List<StoryQuestion> = emptyList(), 
    val readingProgress: Float = 0f, 
    val coverImage: String = "", 
    val section: String = "", 
    val story: ForgedStory? = null 
) 

@Composable
fun StoryArtworkThumbnail( 
    artworkType: String, 
    modifier: Modifier = Modifier 
) { 
    val (gradient, icon) = when (artworkType.lowercase()) { 
        "romance" -> Pair( 
            Brush.verticalGradient(listOf(Color(0xFF831843), Color(0xFF4C0519), Color(0xFF1F030B))), 
            Icons.Default.Favorite 
        ) 
        "nature", "traditional" -> Pair( 
            Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF022C22), Color(0xFF061A14))), 
            Icons.Default.Spa 
        ) 
        "fantasy" -> Pair( 
            Brush.verticalGradient(listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF14072B))), 
            Icons.Default.AutoAwesome 
        ) 
        "school" -> Pair( 
            Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF172554), Color(0xFF0B132B))), 
            Icons.Default.School 
        ) 
        "social" -> Pair( 
            Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF15803D), Color(0xFF064E3B))), 
            Icons.Default.School 
        ) 
        "daily", "dailylife", "daily life" -> Pair( 
            Brush.verticalGradient(listOf(Color(0xFF4338CA), Color(0xFF1E1B4B), Color(0xFF0F172A))), 
            Icons.AutoMirrored.Filled.MenuBook 
        ) 
        else -> Pair( 
            Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))), 
            Icons.AutoMirrored.Filled.MenuBook 
        ) 
    } 
    
    Box( 
        modifier = modifier 
            .background(gradient), 
        contentAlignment = Alignment.Center 
    ) { 
        Box( 
            modifier = Modifier 
                .size(44.dp) 
                .clip(CircleShape) 
                .background(Color.Black.copy(alpha = 0.25f)), 
            contentAlignment = Alignment.Center 
        ) { 
            Icon( 
                imageVector = icon, 
                contentDescription = null, 
                tint = Color.White.copy(alpha = 0.65f), 
                modifier = Modifier.size(24.dp) 
            ) 
        } 
    } 
} 

@Composable
fun Blossom3DProgressBar( 
    progress: Float, 
    modifier: Modifier = Modifier, 
    width: androidx.compose.ui.unit.Dp = 72.dp, 
    height: androidx.compose.ui.unit.Dp = 10.dp 
) { 
    val clampedProgress = progress.coerceIn(0.05f, 1f) 
    Box( 
        modifier = modifier 
            .width(width) 
            .height(height) 
            .clip(RoundedCornerShape(5.dp)) 
            .background(Color(0xFF27272A)), 
        contentAlignment = Alignment.CenterStart 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxHeight() 
                .fillMaxWidth(clampedProgress) 
                .clip(RoundedCornerShape(5.dp)) 
                .background(Color(0xFF15803D)) 
                .padding(bottom = 2.dp) 
                .clip(RoundedCornerShape(4.dp)) 
                .background(Color(0xFF22C55E)) 
        ) 
    } 
} 

@Composable
fun ShinobiStoryCard( 
    story: ShinobiStoryModel, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    titleLanguage: String = "ja" 
) { 
    val isCurrent = story.isCurrent 
    val isCompleted = story.isCompleted 
    val isEnding = story.isEndingCard 
    val hasBorderBeam = isCurrent 
    
    val angle = if (hasBorderBeam) { 
        val infiniteTransition = rememberInfiniteTransition(label = "borderSpin") 
        val animAngle by infiniteTransition.animateFloat( 
            initialValue = 0f, 
            targetValue = 360f, 
            animationSpec = infiniteRepeatable( 
                animation = tween(durationMillis = 16000, easing = LinearEasing), 
                repeatMode = RepeatMode.Restart 
            ), 
            label = "borderAngle" 
        ) 
        animAngle 
    } else { 
        0f 
    } 
    
    val endingShineOffset = if (isEnding) { 
        val shineTransition = rememberInfiniteTransition(label = "endingShine") 
        val offset by shineTransition.animateFloat( 
            initialValue = -1.5f, 
            targetValue = 2.5f, 
            animationSpec = infiniteRepeatable( 
                animation = keyframes { 
                    durationMillis = 6500 
                    -1.5f at 0 with FastOutSlowInEasing 
                    2.5f at 3800 
                    2.5f at 6500 
                }, 
                repeatMode = RepeatMode.Restart 
            ), 
            label = "shineOffset" 
        ) 
        offset 
    } else { 
        0f 
    } 
    
    val lipColor = when { 
        isCurrent -> ShinobiColors.CardBorder 
        isCompleted -> ShinobiColors.BlossomGreen 
        isEnding -> ShinobiColors.WisteriaViolet 
        else -> ShinobiColors.CardBorder 
    } 
    val cardBackground = when { 
        isEnding -> Color(0xFF261225) 
        isCurrent -> ShinobiColors.SurfaceCard2 
        isCompleted -> ShinobiColors.SurfaceCard1 
        else -> ShinobiColors.SurfaceCard1 
    } 
    
    val shape = RoundedCornerShape(16.dp) 
    
    Box( 
        modifier = modifier 
            .width(165.dp) 
            .height(215.dp) 
            .shadow( 
                elevation = if (hasBorderBeam || isCompleted) 8.dp else 4.dp, 
                shape = shape, 
                spotColor = if (hasBorderBeam) Color(0xFFFB7185).copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.3f), 
                ambientColor = if (hasBorderBeam) Color(0xFFFB7185).copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f) 
            ) 
            .clip(shape) 
            .background(lipColor) 
            .clickable(onClick = onClick) 
            .drawWithContent { 
                drawContent() 
                val strokePx = 2.dp.toPx() 
                val halfStroke = strokePx / 2f 
                val cornerRadiusPx = 16.dp.toPx() 
                
                if (hasBorderBeam) { 
                    drawIntoCanvas { canvas -> 
                        val nativePaint = Paint().asFrameworkPaint().apply { 
                            isAntiAlias = true 
                            style = android.graphics.Paint.Style.STROKE 
                            strokeWidth = strokePx 
                            val matrix = Matrix().apply { 
                                postRotate(angle, size.width / 2f, size.height / 2f) 
                            } 
                            val colors = intArrayOf( 
                                0xFFFFFFFF.toInt(), 
                                0xFFFFF1F2.toInt(), 
                                0xFFFB7185.toInt(), 
                                0x00FB7185, 
                                0x00FB7185, 
                                0xFFFB7185.toInt(), 
                                0xFFFFF1F2.toInt(), 
                                0xFFFFFFFF.toInt(), 
                                0xFFFFF1F2.toInt(), 
                                0xFFFB7185.toInt(), 
                                0x00FB7185, 
                                0x00FB7185, 
                                0xFFFB7185.toInt(), 
                                0xFFFFF1F2.toInt(), 
                                0xFFFFFFFF.toInt() 
                            ) 
                            val positions = floatArrayOf( 
                                0.00f, 0.04f, 0.08f, 0.14f, 
                                0.36f, 0.42f, 0.46f, 0.50f, 0.54f, 0.58f, 0.64f, 
                                0.86f, 0.92f, 0.96f, 1.00f 
                            ) 
                            val sweep = SweepGradient(size.width / 2f, size.height / 2f, colors, positions) 
                            sweep.setLocalMatrix(matrix) 
                            shader = sweep 
                        } 
                        canvas.nativeCanvas.drawRoundRect( 
                            halfStroke, 
                            halfStroke, 
                            size.width - halfStroke, 
                            size.height - halfStroke, 
                            cornerRadiusPx, 
                            cornerRadiusPx, 
                            nativePaint 
                        ) 
                    } 
                } 
                
                if (isEnding) { 
                    val shineWidth = size.width * 1.6f 
                    val currentX = size.width * endingShineOffset 
                    val shineBrush = Brush.horizontalGradient( 
                        colors = listOf( 
                            Color.White.copy(alpha = 0f), 
                            Color.White.copy(alpha = 0.05f), 
                            Color.White.copy(alpha = 0.35f), 
                            Color.White.copy(alpha = 0.05f), 
                            Color.White.copy(alpha = 0f) 
                        ), 
                        startX = currentX, 
                        endX = currentX + shineWidth 
                    ) 
                    rotate( 
                        degrees = 12f, 
                        pivot = Offset(currentX + shineWidth / 2f, size.height / 2f) 
                    ) { 
                        drawRect( 
                            brush = shineBrush, 
                            topLeft = Offset(currentX - 50f, -size.height * 0.6f), 
                            size = Size(shineWidth + 100f, size.height * 2.2f) 
                        ) 
                    } 
                } 
            } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .padding(bottom = 3.dp) 
                .clip(shape) 
                .background(cardBackground) 
                .border( 
                    width = 1.dp, 
                    color = lipColor, 
                    shape = shape 
                ) 
        ) { 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(115.dp) 
            ) { 
                val context = androidx.compose.ui.platform.LocalContext.current 
                val coverBitmap = androidx.compose.runtime.remember(story.id, story.coverImage) { 
                    com.ankilock.data.StoryAssetLoader.loadCoverImage(context, story) 
                } 
                if (coverBitmap != null) { 
                    androidx.compose.foundation.Image( 
                        bitmap = coverBitmap, 
                        contentDescription = null, 
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop, 
                        modifier = Modifier 
                            .fillMaxSize() 
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) 
                    ) 
                } else { 
                    StoryArtworkThumbnail( 
                        artworkType = story.artworkType, 
                        modifier = Modifier 
                            .fillMaxSize() 
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) 
                    ) 
                } 
            } 
            
            Column( 
                modifier = Modifier 
                    .weight(1f) 
                    .fillMaxWidth() 
                    .padding(horizontal = 10.dp, vertical = 6.dp), 
                verticalArrangement = Arrangement.SpaceBetween 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Text( 
                        text = story.category, 
                        fontSize = 11.5.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = ShinobiNunito, 
                        color = ShinobiColors.TextSecondary 
                    ) 
                    
                    if (story.isCompleted) { 
                        Icon( 
                            Icons.Default.Check, 
                            contentDescription = "Completed", 
                            tint = ShinobiColors.BlossomGreen, 
                            modifier = Modifier.size(15.dp) 
                        ) 
                    } else if (story.isCurrent || story.readingProgress > 0f) { 
                        Blossom3DProgressBar( 
                            progress = story.readingProgress, 
                            width = 56.dp, 
                            height = 8.dp 
                        ) 
                    } else if (story.isLocked) { 
                        Icon( 
                            Icons.Default.Lock, 
                            contentDescription = null, 
                            tint = ShinobiColors.TextMuted, 
                            modifier = Modifier.size(14.dp) 
                        ) 
                    } 
                } 
                
                val displayTitle = when (titleLanguage.lowercase()) { 
                    "en" -> { 
                        if (story.titleJapanese.isNotBlank() && story.titleEnglish.isNotBlank() && story.titleJapanese != story.titleEnglish) { 
                            "${story.titleJapanese} - ${story.titleEnglish}" 
                        } else { 
                            story.titleEnglish.ifBlank { story.titleJapanese } 
                        } 
                    } 
                    "ja" -> story.titleJapanese.ifBlank { story.titleEnglish } 
                    else -> { 
                        if (story.titleJapanese.isNotBlank() && story.titleEnglish.isNotBlank() && story.titleJapanese != story.titleEnglish) { 
                            "${story.titleJapanese} - ${story.titleEnglish}" 
                        } else { 
                            story.titleEnglish.ifBlank { story.titleJapanese } 
                        } 
                    } 
                } 
                
                Text( 
                    text = displayTitle, 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = ShinobiNunito, 
                    color = ShinobiColors.TextPrimary, 
                    maxLines = 2, 
                    overflow = TextOverflow.Ellipsis, 
                    lineHeight = 16.sp 
                ) 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    if (story.xp >= 200) { 
                        Text( 
                            text = "XP x2", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = ShinobiNunito, 
                            color = ShinobiColors.WisteriaViolet 
                        ) 
                    } else { 
                        Spacer(modifier = Modifier.width(1.dp)) 
                    } 
                    
                    Text( 
                        text = "~${story.estimatedMinutes} mins", 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = ShinobiNunito, 
                        color = ShinobiColors.TextSecondary 
                    ) 
                } 
            } 
        } 
    } 
} 

@Composable
fun ShinobiNormalStoryCard( 
    story: ShinobiStoryModel, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    titleLanguage: String = "ja" 
) { 
    ShinobiStoryCard(story = story, onClick = onClick, modifier = modifier, titleLanguage = titleLanguage) 
} 

@Composable
fun ShinobiEndingStoryCard( 
    story: ShinobiStoryModel, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    titleLanguage: String = "ja" 
) { 
    ShinobiStoryCard(story = story, onClick = onClick, modifier = modifier, titleLanguage = titleLanguage) 
} 

@Composable
fun ShinobiCurrentStoryCard( 
    story: ShinobiStoryModel, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    titleLanguage: String = "ja" 
) { 
    ShinobiStoryCard(story = story, onClick = onClick, modifier = modifier, titleLanguage = titleLanguage) 
} 
