package com.ankilock.ui.blossom
    
import androidx.compose.animation.animateContentSize 
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding 
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome 
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape 
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale 
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ui.components.squircleLiquidGlass
    
enum class BlossomTab( 
    val title: String, 
    val icon: ImageVector 
) { 
    CARDS("Cards", Icons.Default.Style), 
    STORIES("Stories", Icons.AutoMirrored.Filled.MenuBook), 
    JISHO("Jisho", Icons.Default.Search); 
    
    val activeColor: Color 
        get() = when (this) { 
            CARDS -> BlossomColors.SlateBlue 
            STORIES -> BlossomColors.MatchaSage 
            JISHO -> BlossomColors.WarmOchre 
        } 
        
    val containerColor: Color 
        get() = when (this) { 
            CARDS -> BlossomColors.SlateBlueContainer 
            STORIES -> BlossomColors.MatchaSageContainer 
            JISHO -> BlossomColors.WarmOchreContainer 
        } 
    
    companion object { 
        val STUDY get() = CARDS 
        val VOCAB get() = JISHO 
    } 
} 
    
@Composable
fun BlossomBottomNav( 
    selectedTab: BlossomTab, 
    onTabSelected: (BlossomTab) -> Unit, 
    modifier: Modifier = Modifier, 
    badgeCounts: Map<BlossomTab, Int> = emptyMap() 
) { 
    val context = LocalContext.current 
    val view = LocalView.current 
    val tabs = BlossomTab.values() 
    val isLight = BlossomColors.currentTheme == AppTheme.LIGHT 
    val navBg = if (isLight) Color(0xFFFFFFFF).copy(alpha = 0.98f) else BlossomColors.SurfaceOverlay.copy(alpha = 0.95f) 
    val navElevation = if (isLight) 8.dp else 12.dp 
    val navSpecular = if (isLight) 0.15f else 0.35f 
    val navBorder = if (isLight) 0.12f else 0.35f 
    
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .navigationBarsPadding() 
            .padding(horizontal = 20.dp, vertical = 10.dp), 
        contentAlignment = Alignment.Center 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .height(58.dp) 
                .squircleLiquidGlass( 
                    shape = RoundedCornerShape(16.dp), 
                    cornerRadius = 16.dp, 
                    tintColor = if (isLight) Color.White else Color.Transparent, 
                    darkBaseAlpha = 0f, 
                    backgroundColor = navBg, 
                    borderAlpha = navBorder, 
                    specularAlpha = navSpecular, 
                    shadowElevation = navElevation 
                ) 
        ) { 
            BoxWithConstraints( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .padding(4.dp) 
            ) { 
                val tabWidth = maxWidth / tabs.size 
                val currentTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0) 
                val indicatorOffset by animateDpAsState( 
                    targetValue = tabWidth * currentTabIndex, 
                    animationSpec = spring( 
                        dampingRatio = Spring.DampingRatioMediumBouncy, 
                        stiffness = Spring.StiffnessMediumLow 
                    ), 
                    label = "BubblyNavIndicatorOffset" 
                ) 

                Box( 
                    modifier = Modifier 
                        .offset(x = indicatorOffset) 
                        .width(tabWidth) 
                        .fillMaxHeight() 
                        .squircleLiquidGlass( 
                            shape = RoundedCornerShape(12.dp), 
                            cornerRadius = 12.dp, 
                            tintColor = selectedTab.activeColor.copy(alpha = if (isLight) 0.12f else 0.25f), 
                            darkBaseAlpha = 0f, 
                            backgroundColor = selectedTab.containerColor.copy(alpha = if (isLight) 0.98f else 0.92f), 
                            borderAlpha = if (isLight) 0.15f else 0.35f, 
                            specularAlpha = if (isLight) 0.20f else 0.55f, 
                            shadowElevation = if (isLight) 2.dp else 4.dp 
                        ) 
                ) 

                Row(modifier = Modifier.fillMaxSize()) { 
                    tabs.forEach { tab -> 
                        val isSelected = selectedTab == tab 
                        val interactionSource = remember { MutableInteractionSource() } 
                        val isPressed by interactionSource.collectIsPressedAsState() 

                        val scale by animateFloatAsState( 
                            targetValue = if (isPressed) 0.88f else if (isSelected) 1.03f else 1.0f, 
                            animationSpec = spring( 
                                dampingRatio = Spring.DampingRatioMediumBouncy, 
                                stiffness = Spring.StiffnessMedium 
                            ), 
                            label = "BubblyTabScale" 
                        ) 
                        val badgeCount = badgeCounts[tab] ?: 0 

                        Box( 
                            modifier = Modifier 
                                .weight(1f) 
                                .fillMaxHeight() 
                                .clip(RoundedCornerShape(12.dp)) 
                                .clickable( 
                                    interactionSource = interactionSource, 
                                    indication = null 
                                ) { 
                                    BlossomHaptics.click(context, view) 
                                    onTabSelected(tab) 
                                } 
                                .scale(scale), 
                            contentAlignment = Alignment.Center 
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.Center 
                            ) { 
                                Box { 
                                    Icon( 
                                        imageVector = tab.icon, 
                                        contentDescription = tab.title, 
                                        tint = if (isSelected) tab.activeColor else BlossomColors.TextSecondary, 
                                        modifier = Modifier.size(19.dp) 
                                    ) 
                                    if (badgeCount > 0 && !isSelected) { 
                                        Box( 
                                            modifier = Modifier 
                                                .align(Alignment.TopEnd) 
                                                .offset(x = 6.dp, y = (-4).dp) 
                                                .size(14.dp) 
                                                .clip(CircleShape) 
                                                .background(BlossomColors.MutedRose), 
                                            contentAlignment = Alignment.Center 
                                        ) { 
                                            Text( 
                                                text = if (badgeCount > 9) "9+" else badgeCount.toString(), 
                                                color = Color.White, 
                                                fontSize = 8.5.sp, 
                                                fontWeight = FontWeight.Bold 
                                            ) 
                                        } 
                                    } 
                                } 
                                if (isSelected) { 
                                    Spacer(modifier = Modifier.width(5.dp)) 
                                    Text( 
                                        text = tab.title, 
                                        fontSize = 12.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = tab.activeColor, 
                                        maxLines = 1, 
                                        softWrap = false 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
        } 
    }  
} 
