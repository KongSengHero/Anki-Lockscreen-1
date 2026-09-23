package com.ankilock.ui.components
    
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ui.blossom.AppTheme
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomNunito
    
data class BlossomThemeOption( 
    val theme: AppTheme, 
    val label: String, 
    val icon: ImageVector 
) 
    
val blossomThemeOptions = listOf( 
    BlossomThemeOption(AppTheme.LIGHT, "Light", Icons.Filled.LightMode), 
    BlossomThemeOption(AppTheme.DARK, "Dark", Icons.Filled.DarkMode), 
    BlossomThemeOption(AppTheme.DIM, "Dim", Icons.Filled.BrightnessMedium) 
) 
    
@Composable 
fun ThemeSwitcher( 
    selectedTheme: AppTheme, 
    onThemeSelected: (AppTheme) -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val selectedIndex = blossomThemeOptions.indexOfFirst { it.theme == selectedTheme }.coerceAtLeast(0) 
    
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .height(48.dp) 
            .clip(RoundedCornerShape(14.dp)) 
            .background(BlossomColors.SurfaceCard2) 
            .border(1.dp, BlossomColors.CardBorder, RoundedCornerShape(14.dp)) 
            .padding(3.dp) 
    ) { 
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) { 
            val segmentWidth = maxWidth / blossomThemeOptions.size 
            val indicatorOffset by animateDpAsState( 
                targetValue = segmentWidth * selectedIndex, 
                animationSpec = spring( 
                    dampingRatio = Spring.DampingRatioMediumBouncy, 
                    stiffness = Spring.StiffnessMediumLow 
                ), 
                label = "ThemeIndicatorOffset" 
            ) 
    
            Box( 
                modifier = Modifier 
                    .offset(x = indicatorOffset) 
                    .width(segmentWidth) 
                    .fillMaxHeight() 
                    .clip(RoundedCornerShape(11.dp)) 
                    .background( 
                        if (selectedTheme == AppTheme.LIGHT) Color.White 
                        else BlossomColors.SurfaceCard1 
                    ) 
                    .border( 
                        width = 1.dp, 
                        color = BlossomColors.SakuraRose.copy(alpha = 0.6f), 
                        shape = RoundedCornerShape(11.dp) 
                    ) 
            ) 
    
            Row(modifier = Modifier.fillMaxSize()) { 
                blossomThemeOptions.forEachIndexed { index, option -> 
                    val isSelected = selectedIndex == index 
                    val interactionSource = remember { MutableInteractionSource() } 
                    val isPressed by interactionSource.collectIsPressedAsState() 
    
                    val scale by animateFloatAsState( 
                        targetValue = if (isPressed) 0.93f else 1.0f, 
                        animationSpec = spring( 
                            dampingRatio = Spring.DampingRatioMediumBouncy, 
                            stiffness = Spring.StiffnessMedium 
                        ), 
                        label = "ThemeOptionScale" 
                    ) 
    
                    Box( 
                        modifier = Modifier 
                            .weight(1f) 
                            .fillMaxHeight() 
                            .clip(RoundedCornerShape(11.dp)) 
                            .clickable( 
                                interactionSource = interactionSource, 
                                indication = null 
                            ) { 
                                onThemeSelected(option.theme) 
                            } 
                            .scale(scale), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center 
                        ) { 
                            Icon( 
                                imageVector = option.icon, 
                                contentDescription = option.label, 
                                tint = if (isSelected) { 
                                    BlossomColors.SakuraRose 
                                } else { 
                                    BlossomColors.TextMuted 
                                }, 
                                modifier = Modifier.size(15.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(6.dp)) 
                            Text( 
                                text = option.label, 
                                fontSize = 12.sp, 
                                fontFamily = BlossomNunito, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isSelected) { 
                                    if (selectedTheme == AppTheme.LIGHT) Color(0xFF161920) 
                                    else Color.White 
                                } else { 
                                    BlossomColors.TextMuted 
                                } 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
    } 
} 
    
@Composable 
fun <T> SlidingPillSwitcher( 
    options: List<T>, 
    selectedOption: T, 
    onOptionSelected: (T) -> Unit, 
    modifier: Modifier = Modifier, 
    labelProvider: (T) -> String = { it.toString() }, 
    iconProvider: ((T) -> ImageVector?)? = null, 
    activeColor: Color = BlossomColors.SakuraRose, 
    height: androidx.compose.ui.unit.Dp = 44.dp, 
    fontSize: TextUnit = if (options.size >= 5) 11.sp else 12.sp 
) { 
    val selectedIndex = options.indexOf(selectedOption).coerceAtLeast(0) 
    
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .height(height) 
            .clip(RoundedCornerShape(14.dp)) 
            .background(BlossomColors.SurfaceCard2) 
            .border(1.dp, BlossomColors.CardBorder, RoundedCornerShape(14.dp)) 
            .padding(3.dp) 
    ) { 
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) { 
            val segmentWidth = maxWidth / options.size 
            val indicatorOffset by animateDpAsState( 
                targetValue = segmentWidth * selectedIndex, 
                animationSpec = spring( 
                    dampingRatio = Spring.DampingRatioMediumBouncy, 
                    stiffness = Spring.StiffnessMediumLow 
                ), 
                label = "SlidingPillOffset" 
            ) 
    
            Box( 
                modifier = Modifier 
                    .offset(x = indicatorOffset) 
                    .width(segmentWidth) 
                    .fillMaxHeight() 
                    .clip(RoundedCornerShape(11.dp)) 
                    .background(BlossomColors.SurfaceCard1) 
                    .border( 
                        width = 1.dp, 
                        color = activeColor.copy(alpha = 0.6f), 
                        shape = RoundedCornerShape(11.dp) 
                    ) 
            ) 
    
            Row(modifier = Modifier.fillMaxSize()) { 
                options.forEachIndexed { index, option -> 
                    val isSelected = selectedIndex == index 
                    val interactionSource = remember { MutableInteractionSource() } 
                    val isPressed by interactionSource.collectIsPressedAsState() 
    
                    val scale by animateFloatAsState( 
                        targetValue = if (isPressed) 0.93f else 1.0f, 
                        animationSpec = spring( 
                            dampingRatio = Spring.DampingRatioMediumBouncy, 
                            stiffness = Spring.StiffnessMedium 
                        ), 
                        label = "SlidingPillOptionScale" 
                    ) 
    
                    Box( 
                        modifier = Modifier 
                            .weight(1f) 
                            .fillMaxHeight() 
                            .clip(RoundedCornerShape(11.dp)) 
                            .clickable( 
                                interactionSource = interactionSource, 
                                indication = null 
                            ) { 
                                onOptionSelected(option) 
                            } 
                            .scale(scale), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center 
                        ) { 
                            val icon = iconProvider?.invoke(option) 
                            if (icon != null) { 
                                Icon( 
                                    imageVector = icon, 
                                    contentDescription = null, 
                                    tint = if (isSelected) activeColor else BlossomColors.TextMuted, 
                                    modifier = Modifier.size(15.dp) 
                                ) 
                                Spacer(modifier = Modifier.width(6.dp)) 
                            } 
                            Text( 
                                text = labelProvider(option), 
                                fontSize = fontSize, 
                                fontFamily = BlossomNunito, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isSelected) activeColor else BlossomColors.TextSecondary, 
                                maxLines = 1 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
    } 
} 
