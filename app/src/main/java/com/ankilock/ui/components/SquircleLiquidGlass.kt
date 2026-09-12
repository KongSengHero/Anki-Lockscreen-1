package com.ankilock.ui.components
    
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiShapes
    
fun Modifier.squircleLiquidGlass( 
    shape: Shape = ShinobiShapes.SquircleLarge, 
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
    
    return this 
        .shadow( 
            elevation = shadowElevation, 
            shape = shape, 
            ambientColor = Color.Black.copy(alpha = 0.35f), 
            spotColor = Color.Black.copy(alpha = 0.50f) 
        ) 
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
        .drawWithContent { 
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
} 
    
@Composable
fun SquircleLiquidGlassBox( 
    modifier: Modifier = Modifier, 
    shape: Shape = ShinobiShapes.SquircleLarge, 
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
    containerColor: Color = ShinobiColors.SlateBlue, 
    bevelColor: Color? = null, 
    contentColor: Color = Color.White, 
    shape: Shape = ShinobiShapes.SquircleMedium, 
    depth: Dp = 3.dp, 
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp), 
    content: @Composable BoxScope.() -> Unit 
) { 
    val view = LocalView.current 
    val interactionSource = remember { MutableInteractionSource() } 
    val isPressed by interactionSource.collectIsPressedAsState() 
    
    val actualBevelColor = bevelColor ?: containerColor.copy( 
        red = (containerColor.red * 0.65f).coerceIn(0f, 1f), 
        green = (containerColor.green * 0.65f).coerceIn(0f, 1f), 
        blue = (containerColor.blue * 0.65f).coerceIn(0f, 1f) 
    ) 
    
    val currentOffset by animateDpAsState( 
        targetValue = if (isPressed && enabled) depth else 0.dp, 
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
                enabled = enabled 
            ) { 
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
                onClick() 
            } 
    ) { 
        Box( 
            modifier = Modifier 
                .matchParentSize() 
                .offset(y = depth) 
                .clip(shape) 
                .background(if (enabled) actualBevelColor else actualBevelColor.copy(alpha = 0.35f)) 
        ) 
        
        Box( 
            modifier = Modifier 
                .offset(y = currentOffset) 
                .clip(shape) 
                .background( 
                    if (enabled) { 
                        Brush.verticalGradient( 
                            colors = listOf( 
                                containerColor.copy(alpha = 1f), 
                                containerColor.copy( 
                                    red = (containerColor.red * 0.90f).coerceIn(0f, 1f), 
                                    green = (containerColor.green * 0.90f).coerceIn(0f, 1f), 
                                    blue = (containerColor.blue * 0.90f).coerceIn(0f, 1f) 
                                ) 
                            ) 
                        ) 
                    } else { 
                        Brush.verticalGradient( 
                            listOf(containerColor.copy(alpha = 0.35f), containerColor.copy(alpha = 0.35f)) 
                        ) 
                    } 
                ) 
                .border( 
                    width = 1.dp, 
                    color = Color.White.copy(alpha = if (enabled) 0.25f else 0.08f), 
                    shape = shape 
                ) 
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
