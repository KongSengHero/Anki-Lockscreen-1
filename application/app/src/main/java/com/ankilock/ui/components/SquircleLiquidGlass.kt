package com.ankilock.ui.components
    
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants 
import androidx.compose.foundation.layout.defaultMinSize 
import androidx.compose.foundation.layout.fillMaxWidth 
import androidx.compose.material3.Icon 
import androidx.compose.runtime.mutableStateOf 
import androidx.compose.runtime.rememberCoroutineScope 
import androidx.compose.runtime.setValue 
import androidx.compose.ui.platform.LocalContext 
import kotlinx.coroutines.delay 
import kotlinx.coroutines.launch 
import androidx.compose.animation.core.LinearEasing 
import androidx.compose.animation.core.RepeatMode 
import androidx.compose.animation.core.Spring 
import androidx.compose.animation.core.animateDpAsState 
import androidx.compose.animation.core.animateFloat 
import androidx.compose.animation.core.animateFloatAsState 
import androidx.compose.animation.core.infiniteRepeatable 
import androidx.compose.animation.core.rememberInfiniteTransition 
import androidx.compose.animation.core.spring 
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomShapes
    
fun Modifier.squircleLiquidGlass( 
    shape: Shape = BlossomShapes.SquircleLarge, 
    cornerRadius: Dp = 18.dp, 
    tintColor: Color = Color.White.copy(alpha = 0.08f), 
    darkBaseAlpha: Float = 0.90f, 
    specularAlpha: Float = 0.40f, 
    borderAlpha: Float = 0.35f, 
    shadowElevation: Dp = 8.dp, 
    backgroundColor: Color? = null, 
    hasTopGloss: Boolean = false 
): Modifier { 
    val finalBg = backgroundColor ?: if (darkBaseAlpha <= 0f) { 
        tintColor 
    } else { 
        Color(0xFF161922).copy(alpha = darkBaseAlpha) 
    } 
    
    val shadowModifier = if (shadowElevation > 0.dp) { 
        Modifier.shadow( 
            elevation = shadowElevation, 
            shape = shape, 
            ambientColor = Color.Black.copy(alpha = 0.35f), 
            spotColor = Color.Black.copy(alpha = 0.50f) 
        ) 
    } else Modifier 
    
    val baseModifier = this 
        .then(shadowModifier) 
        .clip(shape) 
        .background(finalBg) 
        .border( 
            width = 1.dp, 
            brush = Brush.linearGradient( 
                colors = listOf( 
                    Color.White.copy(alpha = specularAlpha.coerceAtMost(borderAlpha)), 
                    Color.White.copy(alpha = (specularAlpha * 0.25f).coerceAtMost(borderAlpha)), 
                    Color.White.copy(alpha = 0.04f), 
                    Color.White.copy(alpha = (specularAlpha * 0.45f).coerceAtMost(borderAlpha)) 
                ), 
                start = Offset.Zero, 
                end = Offset.Infinite 
            ), 
            shape = shape 
        ) 
        
    return if (specularAlpha > 0f || hasTopGloss) { 
        baseModifier.drawWithContent { 
            drawContent() 
            val cr = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()) 
            if (specularAlpha > 0f) { 
                drawRoundRect( 
                    brush = Brush.linearGradient( 
                        0.0f to Color.White.copy(alpha = specularAlpha * 0.35f), 
                        0.40f to Color.Transparent, 
                        1.0f to Color.White.copy(alpha = specularAlpha * 0.15f), 
                        start = Offset(2f, 2f), 
                        end = Offset(size.width - 2f, size.height - 2f) 
                    ), 
                    topLeft = Offset(1f, 1f), 
                    size = Size(size.width - 2f, size.height - 2f), 
                    cornerRadius = cr, 
                    style = Stroke(width = 1f) 
                ) 
            } 
            if (hasTopGloss) { 
                drawRoundRect( 
                    brush = Brush.verticalGradient( 
                        colors = listOf( 
                            Color.White.copy(alpha = 0.10f), 
                            Color.White.copy(alpha = 0.02f), 
                            Color.Transparent 
                        ), 
                        startY = 0f, 
                        endY = size.height * 0.35f 
                    ), 
                    topLeft = Offset.Zero, 
                    size = Size(size.width, size.height * 0.35f), 
                    cornerRadius = cr 
                ) 
            } 
        } 
    } else { 
        baseModifier 
    } 
} 
    
@Composable
fun SquircleLiquidGlassBox( 
    modifier: Modifier = Modifier, 
    shape: Shape = BlossomShapes.SquircleLarge, 
    cornerRadius: Dp = 18.dp, 
    tintColor: Color = Color.White.copy(alpha = 0.08f), 
    darkBaseAlpha: Float = 0.90f, 
    specularAlpha: Float = 0.40f, 
    borderAlpha: Float = 0.35f, 
    shadowElevation: Dp = 8.dp, 
    backgroundColor: Color? = null, 
    hasTopGloss: Boolean = false, 
    contentAlignment: Alignment = Alignment.TopStart, 
    content: @Composable BoxScope.() -> Unit 
) { 
    Box( 
        modifier = modifier.squircleLiquidGlass( 
            shape = shape, 
            cornerRadius = cornerRadius, 
            tintColor = tintColor, 
            darkBaseAlpha = darkBaseAlpha, 
            specularAlpha = specularAlpha, 
            borderAlpha = borderAlpha, 
            shadowElevation = shadowElevation, 
            backgroundColor = backgroundColor, 
            hasTopGloss = hasTopGloss 
        ), 
        contentAlignment = contentAlignment, 
        content = content 
    ) 
} 
    
@Composable
fun Squircle3DButton( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    enabled: Boolean = true, 
    containerColor: Color = BlossomColors.SlateBlue, 
    bevelColor: Color? = null, 
    contentColor: Color = Color.White, 
    containerBrush: Brush? = null, 
    bevelBrush: Brush? = null, 
    borderBrush: Brush? = null, 
    progressFraction: Float? = null, 
    progressColor: Color? = null, 
    hasSweepingShine: Boolean = false, 
    shape: Shape = BlossomShapes.SquircleMedium, 
    depth: Dp = 3.dp, 
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp), 
    overlayContent: (@Composable BoxScope.() -> Unit)? = null, 
    content: @Composable BoxScope.() -> Unit 
) { 
    val view = LocalView.current 
    val coroutineScope = rememberCoroutineScope() 
    val interactionSource = remember { MutableInteractionSource() } 
    val isPressed by interactionSource.collectIsPressedAsState() 
    var isClickAnimating by remember { mutableStateOf(false) } 
    val isVisualPressed = (isPressed || isClickAnimating) && enabled 
    
    val baseBevel = bevelColor ?: containerColor.copy( 
        red = (containerColor.red * 0.65f).coerceIn(0f, 1f), 
        green = (containerColor.green * 0.65f).coerceIn(0f, 1f), 
        blue = (containerColor.blue * 0.65f).coerceIn(0f, 1f), 
        alpha = 1f 
    ) 
    val actualBevelColor = if (enabled) { 
        baseBevel 
    } else { 
        baseBevel.copy( 
            red = (baseBevel.red * 0.50f).coerceIn(0f, 1f), 
            green = (baseBevel.green * 0.50f).coerceIn(0f, 1f), 
            blue = (baseBevel.blue * 0.50f).coerceIn(0f, 1f), 
            alpha = 1f 
        ) 
    } 
    val effectiveBorderColor = if (enabled) { 
        Color.White.copy(alpha = 0.22f) 
    } else { 
        Color.Transparent 
    } 
    val faceColor = if (enabled) { 
        containerColor 
    } else { 
        containerColor.copy( 
            red = (containerColor.red * 0.50f).coerceIn(0f, 1f), 
            green = (containerColor.green * 0.50f).coerceIn(0f, 1f), 
            blue = (containerColor.blue * 0.50f).coerceIn(0f, 1f), 
            alpha = 1f 
        ) 
    } 
    
    val currentOffset by animateDpAsState( 
        targetValue = if (!enabled || isVisualPressed) depth else 0.dp, 
        animationSpec = spring( 
            dampingRatio = Spring.DampingRatioMediumBouncy, 
            stiffness = Spring.StiffnessMedium 
        ), 
        label = "buttonPressOffset" 
    ) 
    
    Box( 
        modifier = modifier 
            .clickable( 
                interactionSource = interactionSource, 
                indication = null, 
                enabled = enabled && !isClickAnimating 
            ) { 
                coroutineScope.launch { 
                    isClickAnimating = true 
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
                    delay(120) 
                    isClickAnimating = false 
                    delay(70) 
                    onClick() 
                } 
            } 
    ) { 
        Box( 
            modifier = Modifier 
                .matchParentSize() 
                .padding(top = depth) 
                .clip(shape) 
                .then( 
                    if (bevelBrush != null) { 
                        Modifier.background(bevelBrush) 
                    } else { 
                        Modifier.background(if (enabled) actualBevelColor else actualBevelColor.copy(alpha = 0.35f)) 
                    } 
                ) 
        ) 
        
        Box( 
            modifier = Modifier 
                .matchParentSize() 
                .padding(bottom = depth) 
                .offset(y = currentOffset) 
                .clip(shape) 
                .then( 
                    if (containerBrush != null) { 
                        Modifier.background(containerBrush) 
                    } else { 
                        Modifier.background(faceColor) 
                    } 
                ) 
                .border( 
                    width = 1.dp, 
                    color = effectiveBorderColor, 
                    shape = shape 
                ) 
        ) { 
            if (progressFraction != null && progressFraction > 0f) { 
                Box( 
                    modifier = Modifier 
                        .matchParentSize() 
                        .fillMaxWidth(progressFraction.coerceIn(0f, 1f)) 
                        .clip(shape) 
                        .background(progressColor ?: BlossomColors.SakuraRose.copy(alpha = 0.30f)) 
                ) 
            } 
            
            if (hasSweepingShine && enabled) { 
                val infiniteTransition = rememberInfiniteTransition(label = "shineTransition") 
                val shineOffset by infiniteTransition.animateFloat( 
                    initialValue = -1f, 
                    targetValue = 2f, 
                    animationSpec = infiniteRepeatable( 
                        animation = tween(3500, easing = LinearEasing), 
                        repeatMode = RepeatMode.Restart 
                    ), 
                    label = "shineOffset" 
                ) 
                Box( 
                    modifier = Modifier 
                        .matchParentSize() 
                        .clip(shape) 
                        .drawWithContent { 
                            drawContent() 
                            val width = size.width 
                            val startX = shineOffset * width 
                            drawRect( 
                                brush = Brush.horizontalGradient( 
                                    colors = listOf( 
                                        Color.Transparent, 
                                        Color.White.copy(alpha = 0.28f), 
                                        Color.Transparent 
                                    ), 
                                    startX = startX, 
                                    endX = startX + width * 0.65f 
                                ) 
                            ) 
                        } 
                ) 
            } 
            
            overlayContent?.invoke(this) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .padding(contentPadding), 
                contentAlignment = Alignment.Center 
            ) { 
                androidx.compose.runtime.CompositionLocalProvider( 
                    androidx.compose.material3.LocalContentColor provides contentColor 
                ) { 
                    content() 
                } 
            } 
        } 
    } 
} 
    
@Composable 
fun Squircle3DButton( 
    text: String, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null, 
    enabled: Boolean = true, 
    containerColor: Color = BlossomColors.SlateBlue, 
    bevelColor: Color? = null, 
    contentColor: Color = Color.White, 
    containerBrush: Brush? = null, 
    bevelBrush: Brush? = null, 
    borderBrush: Brush? = null, 
    progressFraction: Float? = null, 
    progressColor: Color? = null, 
    hasSweepingShine: Boolean = false, 
    shape: Shape = BlossomShapes.SquircleMedium, 
    depth: Dp = 3.dp, 
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp), 
    overlayContent: (@Composable BoxScope.() -> Unit)? = null 
) { 
    Squircle3DButton( 
        onClick = onClick, 
        modifier = modifier, 
        enabled = enabled, 
        containerColor = containerColor, 
        bevelColor = bevelColor, 
        contentColor = contentColor, 
        containerBrush = containerBrush, 
        bevelBrush = bevelBrush, 
        borderBrush = borderBrush, 
        progressFraction = progressFraction, 
        progressColor = progressColor, 
        hasSweepingShine = hasSweepingShine, 
        shape = shape, 
        depth = depth, 
        contentPadding = contentPadding, 
        overlayContent = overlayContent 
    ) { 
        androidx.compose.foundation.layout.Row( 
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center 
        ) { 
            if (icon != null) { 
                Icon( 
                    imageVector = icon, 
                    contentDescription = null, 
                    modifier = Modifier.padding(end = 8.dp) 
                ) 
            } 
            Text( 
                text = text, 
                fontSize = 15.sp, 
                fontWeight = FontWeight.SemiBold, 
                color = contentColor 
            ) 
        } 
    } 
} 
    
@Composable 
fun Squircle3DCard( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    enabled: Boolean = true, 
    containerColor: Color = BlossomColors.SurfaceElevated, 
    bevelColor: Color? = null, 
    borderBrush: Brush? = null, 
    shape: Shape = RoundedCornerShape(18.dp), 
    depth: Dp = 3.dp, 
    contentPadding: PaddingValues = PaddingValues(0.dp), 
    content: @Composable BoxScope.() -> Unit 
) { 
    Squircle3DButton( 
        onClick = onClick, 
        modifier = modifier, 
        enabled = enabled, 
        containerColor = containerColor, 
        bevelColor = bevelColor, 
        borderBrush = borderBrush, 
        shape = shape, 
        depth = depth, 
        contentPadding = contentPadding, 
        content = content 
    ) 
} 
