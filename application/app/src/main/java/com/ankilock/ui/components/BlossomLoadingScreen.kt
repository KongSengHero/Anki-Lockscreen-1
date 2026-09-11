package com.ankilock.ui.components
    
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomNunito
import com.ankilock.ui.blossom.BlossomShapes
import androidx.compose.ui.graphics.StrokeCap 
import androidx.compose.ui.graphics.drawscope.Stroke 
import kotlinx.coroutines.isActive 
import kotlin.math.cos 
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
    
private data class Petal( 
    val initialX: Float, 
    val speedY: Float, 
    val swayAmplitude: Float, 
    val swaySpeed: Float, 
    val swayPhase: Float, 
    val size: Float, 
    val rotationSpeed: Float, 
    val baseRotation: Float, 
    val alpha: Float, 
    val color: Color 
) 
    
private fun generatePetals(count: Int = 42, seed: Long = 108L): List<Petal> { 
    val rng = Random(seed) 
    val colors = listOf( 
        Color(0xFFFFB7C5), 
        Color(0xFFE87A90), 
        Color(0xFFF48FB1), 
        Color(0xFFFFC0CB), 
        Color(0xFFF8A5B8) 
    ) 
    return List(count) { 
        Petal( 
            initialX = rng.nextFloat(), 
            speedY = 0.08f + rng.nextFloat() * 0.12f, 
            swayAmplitude = 0.04f + rng.nextFloat() * 0.07f, 
            swaySpeed = 1.2f + rng.nextFloat() * 2.0f, 
            swayPhase = rng.nextFloat() * (2f * PI.toFloat()), 
            size = 14f + rng.nextFloat() * 18f, 
            rotationSpeed = (rng.nextFloat() - 0.5f) * 80f, 
            baseRotation = rng.nextFloat() * 360f, 
            alpha = 0.40f + rng.nextFloat() * 0.50f, 
            color = colors[rng.nextInt(colors.size)] 
        ) 
    } 
} 
    
@Composable 
fun BlossomLoadingScreen( 
    isLoading: Boolean, 
    onFinished: () -> Unit = {}, 
    modifier: Modifier = Modifier 
) { 
    val progress = remember { Animatable(0f) } 
    var isVisible by remember { mutableStateOf(true) } 
    val petals = remember { generatePetals(38) } 
    var animTime by remember { mutableFloatStateOf(0f) } 
    
    val infiniteTransition = rememberInfiniteTransition(label = "BlossomGlow") 
    val pulseScale by infiniteTransition.animateFloat( 
        initialValue = 0.96f, 
        targetValue = 1.04f, 
        animationSpec = infiniteRepeatable( 
            animation = tween(1400, easing = FastOutSlowInEasing), 
            repeatMode = RepeatMode.Reverse 
        ), 
        label = "EmblemPulse" 
    ) 
    
    LaunchedEffect(Unit) { 
        val startTime = System.nanoTime() 
        while (isActive) { 
            withFrameNanos { now -> 
                animTime = (now - startTime) / 1_000_000_000f 
            } 
        } 
    } 
    
    LaunchedEffect(Unit) { 
        progress.animateTo( 
            targetValue = 1f, 
            animationSpec = tween(durationMillis = 3400, easing = FastOutSlowInEasing) 
        ) 
    } 
    
    LaunchedEffect(isLoading) { 
        if (!isLoading) { 
            isVisible = false 
            kotlinx.coroutines.delay(600) 
            onFinished() 
        } 
    } 
    
    AnimatedVisibility( 
        visible = isVisible, 
        exit = fadeOut(animationSpec = tween(600, easing = LinearEasing)), 
        modifier = modifier 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxSize() 
                .background( 
                    Brush.radialGradient( 
                        colors = listOf( 
                            Color(0xFF28141B), 
                            Color(0xFF14161C), 
                            Color(0xFF0C0E12) 
                        ) 
                    ) 
                ) 
        ) { 
            Canvas(modifier = Modifier.fillMaxSize()) { 
                val canvasWidth = size.width 
                val canvasHeight = size.height 
    
                petals.forEachIndexed { index, petal -> 
                    val normalizedY = ((animTime * petal.speedY) + (index.toFloat() / petals.size)) % 1.2f - 0.1f 
                    val sway = sin(animTime * petal.swaySpeed + petal.swayPhase) * petal.swayAmplitude 
                    val normalizedX = (petal.initialX + sway + 1f) % 1.0f 
    
                    val xPx = normalizedX * canvasWidth 
                    val yPx = normalizedY * canvasHeight 
                    val currentRotation = petal.baseRotation + (animTime * petal.rotationSpeed) 
    
                    val petalPath = Path().apply { 
                        val w = petal.size 
                        val h = petal.size * 1.6f 
                        moveTo(0f, -h / 2f) 
                        cubicTo(w / 2f, -h / 4f, w / 2f, h / 4f, 0f, h / 2f) 
                        cubicTo(-w / 2f, h / 4f, -w / 2f, -h / 4f, 0f, -h / 2f) 
                        close() 
                    } 
    
                    rotate(degrees = currentRotation, pivot = Offset(xPx, yPx)) { 
                        drawPath( 
                            path = petalPath, 
                            color = petal.color.copy(alpha = petal.alpha) 
                        ) 
                    } 
                } 
            } 
    
            Column( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .padding(horizontal = 32.dp), 
                verticalArrangement = Arrangement.Center, 
                horizontalAlignment = Alignment.CenterHorizontally 
            ) { 
                SproutingSakuraTree( 
                    progress = progress.value, 
                    pulseScale = pulseScale, 
                    modifier = Modifier.size(110.dp) 
                ) 
                
                Spacer(modifier = Modifier.height(20.dp)) 
                
                Text( 
                    text = "BLOSSOM", 
                    fontSize = 22.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontFamily = BlossomNunito, 
                    letterSpacing = 4.sp, 
                    color = Color.White 
                ) 
                
                Spacer(modifier = Modifier.height(6.dp)) 
                
                Text( 
                    text = "Learn Create Japanese Stories", 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.Medium, 
                    fontFamily = BlossomNunito, 
                    color = BlossomColors.SakuraRose.copy(alpha = 0.9f) 
                ) 
                
                Spacer(modifier = Modifier.height(36.dp)) 
    
                Box( 
                    modifier = Modifier 
                        .width(200.dp) 
                        .height(6.dp) 
                        .clip(RoundedCornerShape(50)) 
                        .background(Color(0xFF252A35)) 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth(progress.value) 
                            .fillMaxHeight() 
                            .clip(RoundedCornerShape(50)) 
                            .background( 
                                Brush.horizontalGradient( 
                                    colors = listOf( 
                                        Color(0xFFFFA4B6), 
                                        Color(0xFFE87A90), 
                                        Color(0xFFD84A65) 
                                    ) 
                                ) 
                            ) 
                    ) 
                } 
    
                Spacer(modifier = Modifier.height(14.dp)) 
    
                Text( 
                    text = if (progress.value < 0.7f) "Polishing flashcards & artwork..." else "Ready for study", 
                    fontSize = 11.5.sp, 
                    fontWeight = FontWeight.Normal, 
                    fontFamily = BlossomNunito, 
                    color = BlossomColors.TextMuted 
                ) 
            } 
        } 
    } 
} 

@Composable
fun SproutingSakuraTree( 
    progress: Float, 
    pulseScale: Float, 
    modifier: Modifier = Modifier 
) { 
    val sproutProgress = progress.coerceIn(0f, 1f) 
    val trunkColor = Color(0xFF6B4230) 
    val branchColor = Color(0xFF7C4E3A) 
    val flowerColors = listOf( 
        Color(0xFFFFB7C5), 
        Color(0xFFF48FB1), 
        Color(0xFFE87A90), 
        Color(0xFFFFD1DC) 
    ) 
    
    Canvas(modifier = modifier) { 
        val w = size.width 
        val h = size.height 
        val centerX = w / 2f 
        val baseY = h * 0.94f 
        
        drawCircle( 
            color = Color(0xFF422006).copy(alpha = 0.35f), 
            radius = 16.dp.toPx() * sproutProgress.coerceIn(0.2f, 1f), 
            center = Offset(centerX, baseY + 2.dp.toPx()) 
        ) 
        
        val trunkHeight = (h * 0.52f) * sproutProgress.coerceIn(0f, 1f) 
        val trunkTopY = baseY - trunkHeight 
        
        if (sproutProgress > 0.05f) { 
            val trunkPath = Path().apply { 
                moveTo(centerX - 3.5f, baseY) 
                cubicTo( 
                    centerX - 2f, baseY - trunkHeight * 0.4f, 
                    centerX - 4f, baseY - trunkHeight * 0.7f, 
                    centerX - 1f, trunkTopY 
                ) 
            } 
            drawPath( 
                path = trunkPath, 
                color = trunkColor, 
                style = Stroke(width = 7.dp.toPx() * (1f - 0.35f * sproutProgress), cap = StrokeCap.Round) 
            ) 
        } 
        
        if (sproutProgress > 0.30f) { 
            val branch1Progress = ((sproutProgress - 0.30f) / 0.70f).coerceIn(0f, 1f) 
            val b1EndX = centerX - (w * 0.30f * branch1Progress) 
            val b1EndY = (baseY - h * 0.28f) - (h * 0.16f * branch1Progress) 
            val branch1Path = Path().apply { 
                moveTo(centerX - 2f, baseY - h * 0.26f) 
                quadraticBezierTo(centerX - w * 0.14f, baseY - h * 0.32f, b1EndX, b1EndY) 
            } 
            drawPath( 
                path = branch1Path, 
                color = branchColor, 
                style = Stroke(width = 4.dp.toPx() * (1f - 0.3f * branch1Progress), cap = StrokeCap.Round) 
            ) 
        } 
        
        if (sproutProgress > 0.40f) { 
            val branch2Progress = ((sproutProgress - 0.40f) / 0.60f).coerceIn(0f, 1f) 
            val b2EndX = centerX + (w * 0.32f * branch2Progress) 
            val b2EndY = (baseY - h * 0.32f) - (h * 0.18f * branch2Progress) 
            val branch2Path = Path().apply { 
                moveTo(centerX - 1f, baseY - h * 0.30f) 
                quadraticBezierTo(centerX + w * 0.16f, baseY - h * 0.36f, b2EndX, b2EndY) 
            } 
            drawPath( 
                path = branch2Path, 
                color = branchColor, 
                style = Stroke(width = 3.8.dp.toPx() * (1f - 0.3f * branch2Progress), cap = StrokeCap.Round) 
            ) 
        } 
        
        if (sproutProgress > 0.50f) { 
            val branch3Progress = ((sproutProgress - 0.50f) / 0.50f).coerceIn(0f, 1f) 
            val b3EndX = centerX + (w * 0.10f * branch3Progress) 
            val b3EndY = trunkTopY - (h * 0.16f * branch3Progress) 
            val branch3Path = Path().apply { 
                moveTo(centerX - 1f, trunkTopY) 
                quadraticBezierTo(centerX + w * 0.04f, trunkTopY - h * 0.08f, b3EndX, b3EndY) 
            } 
            drawPath( 
                path = branch3Path, 
                color = branchColor, 
                style = Stroke(width = 3.dp.toPx() * (1f - 0.2f * branch3Progress), cap = StrokeCap.Round) 
            ) 
        } 
        
        val flowerNodes = listOf( 
            Triple(centerX - w * 0.30f, baseY - h * 0.44f, 0.45f), 
            Triple(centerX - w * 0.22f, baseY - h * 0.38f, 0.52f), 
            Triple(centerX - w * 0.12f, baseY - h * 0.52f, 0.60f), 
            Triple(centerX - w * 0.02f, baseY - h * 0.66f, 0.55f), 
            Triple(centerX + w * 0.10f, baseY - h * 0.64f, 0.68f), 
            Triple(centerX + w * 0.22f, baseY - h * 0.56f, 0.58f), 
            Triple(centerX + w * 0.32f, baseY - h * 0.50f, 0.62f), 
            Triple(centerX + w * 0.16f, baseY - h * 0.42f, 0.70f), 
            Triple(centerX - w * 0.20f, baseY - h * 0.48f, 0.75f), 
            Triple(centerX + w * 0.26f, baseY - h * 0.40f, 0.80f), 
            Triple(centerX + w * 0.02f, baseY - h * 0.72f, 0.85f) 
        ) 
        
        flowerNodes.forEachIndexed { index, (fx, fy, threshold) -> 
            if (sproutProgress > threshold) { 
                val bloomProgress = ((sproutProgress - threshold) / (1f - threshold)).coerceIn(0f, 1f) 
                val flowerScale = bloomProgress * pulseScale 
                val baseRadius = (5.5f + (index % 3) * 1.5f) * flowerScale 
                val color = flowerColors[index % flowerColors.size] 
                
                for (petalIdx in 0 until 5) { 
                    val angle = (petalIdx * (2 * PI / 5f) + (index * 0.4f)).toFloat() 
                    val px = fx + cos(angle) * (baseRadius * 1.2f) 
                    val py = fy + sin(angle) * (baseRadius * 1.2f) 
                    drawCircle( 
                        color = color.copy(alpha = 0.90f), 
                        radius = baseRadius * 0.85f, 
                        center = Offset(px, py) 
                    ) 
                } 
                drawCircle( 
                    color = Color(0xFFFFF7ED), 
                    radius = baseRadius * 0.45f, 
                    center = Offset(fx, fy) 
                ) 
            } 
        } 
    } 
} 
