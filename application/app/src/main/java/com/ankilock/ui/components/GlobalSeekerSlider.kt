package com.ankilock.ui.components
    
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api 
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas 
import androidx.compose.foundation.layout.Box 
import androidx.compose.foundation.layout.fillMaxSize 
import androidx.compose.ui.draw.shadow 
import androidx.compose.ui.geometry.Offset 
import com.ankilock.ui.blossom.BlossomColors 

@Composable
fun GlobalSeekerContainer( 
    modifier: Modifier = Modifier, 
    content: @Composable ColumnScope.() -> Unit 
) { 
    Column( 
        modifier = modifier 
            .fillMaxWidth() 
            .clip(RoundedCornerShape(16.dp)) 
            .background(BlossomColors.SurfaceElevated.copy(alpha = 0.65f)) 
            .border(BorderStroke(1.dp, BlossomColors.CardBorderSubtle), RoundedCornerShape(16.dp)) 
            .padding(horizontal = 14.dp, vertical = 12.dp), 
        verticalArrangement = Arrangement.spacedBy(8.dp), 
        content = content 
    ) 
} 
    
@OptIn(ExperimentalMaterial3Api::class) 
@Composable
fun GlobalSeekerRow( 
    icon: ImageVector, 
    label: String, 
    valueDisplay: String, 
    value: Float, 
    valueRange: ClosedFloatingPointRange<Float>, 
    onValueChange: (Float) -> Unit, 
    onValueChangeFinished: (() -> Unit)? = null, 
    accentColor: Color = BlossomColors.SlateBlue, 
    snapValues: List<Float>? = null, 
    modifier: Modifier = Modifier 
) { 
    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f) 
    val fraction = ((value - valueRange.start) / rangeSpan).coerceIn(0f, 1f) 
    val stepsCount = if (snapValues != null && snapValues.size > 2) snapValues.size - 2 else 0 

    Column(modifier = modifier.fillMaxWidth()) { 
        Row( 
            verticalAlignment = Alignment.CenterVertically, 
            modifier = Modifier.fillMaxWidth() 
        ) { 
            Icon( 
                icon, 
                contentDescription = null, 
                tint = accentColor, 
                modifier = Modifier.size(15.dp) 
            ) 
            Spacer(modifier = Modifier.width(6.dp)) 
            Text( 
                label, 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Medium, 
                color = BlossomColors.TextSecondary 
            ) 
            Spacer(modifier = Modifier.weight(1f)) 
            Surface( 
                shape = RoundedCornerShape(6.dp), 
                color = BlossomColors.SurfaceCard3 
            ) { 
                Text( 
                    valueDisplay, 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = accentColor, 
                    maxLines = 1, 
                    softWrap = false, 
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                ) 
            } 
        } 
        Spacer(modifier = Modifier.height(4.dp)) 
        Slider( 
            value = value, 
            onValueChange = onValueChange, 
            onValueChangeFinished = onValueChangeFinished, 
            valueRange = valueRange, 
            steps = stepsCount, 
            modifier = Modifier 
                .fillMaxWidth() 
                .height(36.dp), 
            thumb = { 
                Box( 
                    modifier = Modifier 
                        .size(width = 16.dp, height = 24.dp) 
                        .shadow(4.dp, RoundedCornerShape(5.dp), spotColor = Color.Black.copy(alpha = 0.5f)) 
                        .background(accentColor, RoundedCornerShape(5.dp)) 
                        .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(5.dp)) 
                ) 
            }, 
            track = { _ -> 
                Box( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(9.dp) 
                        .clip(RoundedCornerShape(4.5.dp)) 
                        .background(BlossomColors.CardBorderSubtle.copy(alpha = 0.85f)) 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth(fraction) 
                            .height(9.dp) 
                            .background(accentColor) 
                    ) 
                    Canvas( 
                        modifier = Modifier 
                            .fillMaxSize() 
                    ) { 
                        val cy = size.height / 2f 
                        if (snapValues != null && snapValues.isNotEmpty()) { 
                            for (sv in snapValues) { 
                                val snapFraction = ((sv - valueRange.start) / rangeSpan).coerceIn(0f, 1f) 
                                val cx = size.width * snapFraction 
                                val isActive = cx <= size.width * fraction + 1.5f 
                                drawCircle( 
                                    color = if (isActive) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.22f), 
                                    radius = 1.3.dp.toPx(), 
                                    center = Offset(cx, cy) 
                                ) 
                            } 
                        } else { 
                            val dotCount = (size.width / 14.dp.toPx()).toInt().coerceIn(8, 32) 
                            val step = size.width / (dotCount + 1) 
                            for (i in 1..dotCount) { 
                                val cx = step * i 
                                val isActive = cx <= size.width * fraction 
                                drawCircle( 
                                    color = if (isActive) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.22f), 
                                    radius = 1.3.dp.toPx(), 
                                    center = Offset(cx, cy) 
                                ) 
                            } 
                        } 
                    } 
                } 
            } 
        ) 
    } 
} 
