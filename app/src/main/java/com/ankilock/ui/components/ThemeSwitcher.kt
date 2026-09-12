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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ui.shinobi.AppTheme
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiNunito
    
data class ShinobiThemeOption( 
    val theme: AppTheme, 
    val label: String, 
    val icon: ImageVector 
) 
    
val shinobiThemeOptions = listOf( 
    ShinobiThemeOption(AppTheme.LIGHT, "Light", Icons.Filled.LightMode), 
    ShinobiThemeOption(AppTheme.DARK, "Dark", Icons.Filled.DarkMode), 
    ShinobiThemeOption(AppTheme.DIM, "Dim", Icons.Filled.BrightnessMedium) 
) 
    
typealias BlossomThemeOption = ShinobiThemeOption
val blossomThemeOptions = shinobiThemeOptions
    
@Composable 
fun ThemeSwitcher( 
    selectedTheme: AppTheme, 
    onThemeSelected: (AppTheme) -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val selectedIndex = shinobiThemeOptions.indexOfFirst { it.theme == selectedTheme }.coerceAtLeast(0) 
    
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .height(48.dp) 
            .clip(RoundedCornerShape(14.dp)) 
            .background(ShinobiColors.SurfaceCard2) 
            .border(1.dp, ShinobiColors.CardBorder, RoundedCornerShape(14.dp)) 
            .padding(3.dp) 
    ) { 
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) { 
            val segmentWidth = maxWidth / shinobiThemeOptions.size 
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
                        else ShinobiColors.SurfaceCard1 
                    ) 
                    .border( 
                        width = 1.dp, 
                        color = ShinobiColors.SakuraRose.copy(alpha = 0.6f), 
                        shape = RoundedCornerShape(11.dp) 
                    ) 
            ) 
    
            Row(modifier = Modifier.fillMaxSize()) { 
                shinobiThemeOptions.forEachIndexed { index, option -> 
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
                                    ShinobiColors.SakuraRose 
                                } else { 
                                    ShinobiColors.TextMuted 
                                }, 
                                modifier = Modifier.size(15.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(6.dp)) 
                            Text( 
                                text = option.label, 
                                fontSize = 12.sp, 
                                fontFamily = ShinobiNunito, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isSelected) { 
                                    if (selectedTheme == AppTheme.LIGHT) Color(0xFF161920) 
                                    else Color.White 
                                } else { 
                                    ShinobiColors.TextMuted 
                                } 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
    } 
} 
