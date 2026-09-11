package com.ankilock.ui.blossom
    
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
    
@Composable
fun SnailIcon( 
    modifier: Modifier = Modifier, 
    size: Dp = 22.dp, 
    tint: Color = Color(0xFFFB923C), 
    cutoutColor: Color = Color(0xFF2A1B10) 
) { 
    Canvas(modifier = modifier.size(size)) { 
        val w = this.size.width 
        val h = this.size.height 
        
        val shellCenter = Offset(w * 0.40f, h * 0.50f) 
        val shellRadius = w * 0.28f 
        
        val bodyPath = Path().apply { 
            moveTo(w * 0.12f, h * 0.76f) 
            lineTo(w * 0.78f, h * 0.76f) 
            quadraticBezierTo(w * 0.88f, h * 0.74f, w * 0.85f, h * 0.56f) 
            quadraticBezierTo(w * 0.82f, h * 0.46f, w * 0.73f, h * 0.50f) 
            quadraticBezierTo(w * 0.64f, h * 0.56f, w * 0.60f, h * 0.68f) 
            lineTo(w * 0.12f, h * 0.76f) 
            close() 
        } 
        drawPath( 
            path = bodyPath, 
            color = tint 
        ) 
        
        drawCircle( 
            color = tint, 
            radius = shellRadius, 
            center = shellCenter 
        ) 
        
        val strokeW = w * 0.075f 
        val spiralPath = Path().apply { 
            moveTo(shellCenter.x, shellCenter.y) 
            cubicTo( 
                shellCenter.x + shellRadius * 0.35f, shellCenter.y - shellRadius * 0.15f, 
                shellCenter.x + shellRadius * 0.45f, shellCenter.y + shellRadius * 0.40f, 
                shellCenter.x, shellCenter.y + shellRadius * 0.45f 
            ) 
            cubicTo( 
                shellCenter.x - shellRadius * 0.45f, shellCenter.y + shellRadius * 0.40f, 
                shellCenter.x - shellRadius * 0.45f, shellCenter.y - shellRadius * 0.35f, 
                shellCenter.x, shellCenter.y - shellRadius * 0.45f 
            ) 
            cubicTo( 
                shellCenter.x + shellRadius * 0.55f, shellCenter.y - shellRadius * 0.45f, 
                shellCenter.x + shellRadius * 0.70f, shellCenter.y + shellRadius * 0.20f, 
                shellCenter.x + shellRadius * 0.40f, shellCenter.y + shellRadius * 0.65f 
            ) 
        } 
        drawPath( 
            path = spiralPath, 
            color = cutoutColor, 
            style = Stroke(width = strokeW, cap = StrokeCap.Round) 
        ) 
        
        val ant1Start = Offset(w * 0.78f, h * 0.50f) 
        val ant1End = Offset(w * 0.88f, h * 0.28f) 
        drawLine( 
            color = tint, 
            start = ant1Start, 
            end = ant1End, 
            strokeWidth = strokeW * 0.85f, 
            cap = StrokeCap.Round 
        ) 
        drawCircle( 
            color = tint, 
            radius = w * 0.045f, 
            center = ant1End 
        ) 
        
        val ant2Start = Offset(w * 0.72f, h * 0.48f) 
        val ant2End = Offset(w * 0.75f, h * 0.24f) 
        drawLine( 
            color = tint, 
            start = ant2Start, 
            end = ant2End, 
            strokeWidth = strokeW * 0.85f, 
            cap = StrokeCap.Round 
        ) 
        drawCircle( 
            color = tint, 
            radius = w * 0.045f, 
            center = ant2End 
        ) 
    } 
}
