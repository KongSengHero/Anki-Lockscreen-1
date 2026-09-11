package com.ankilock.ui.blossom.story
    
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ui.blossom.BlossomNunito
import com.ankilock.ui.blossom.BlossomShapes
import com.ankilock.ui.blossom.BlossomSquircleSwitch
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
    
@OptIn(ExperimentalMaterial3Api::class) 
@Composable
fun BlossomStorySettingsSheet( 
    showPronunciation: Boolean, 
    onToggleShowPronunciation: (Boolean) -> Unit, 
    pronunciationType: String, 
    onChangePronunciationType: (String) -> Unit, 
    enlargeTextFont: Boolean, 
    onToggleEnlargeTextFont: (Boolean) -> Unit, 
    showImages: Boolean, 
    onToggleShowImages: (Boolean) -> Unit, 
    highlightWordOnAudio: Boolean, 
    onToggleHighlightWordOnAudio: (Boolean) -> Unit, 
    onDismiss: () -> Unit 
) { 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    
    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = Color(0xFF18181B), 
        windowInsets = WindowInsets(0), 
        dragHandle = { 
            Box( 
                modifier = Modifier 
                    .padding(top = 12.dp, bottom = 8.dp) 
                    .size(width = 44.dp, height = 4.dp) 
                    .clip(BlossomShapes.Pill) 
                    .background(Color(0xFF52525B)) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 20.dp, vertical = 6.dp) 
                .navigationBarsPadding() 
                .padding(bottom = 16.dp) 
        ) { 
            Text( 
                text = "Story Settings", 
                fontSize = 20.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = BlossomNunito, 
                color = Color.White, 
                textAlign = TextAlign.Center, 
                modifier = Modifier.fillMaxWidth() 
            ) 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(1.dp) 
                    .background(Color(0xFF27272A)) 
            ) 
            
            Spacer(modifier = Modifier.height(18.dp)) 
            
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Text( 
                    text = "Show pronunciation", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = BlossomNunito, 
                    color = Color.White 
                ) 
                
                BlossomSquircleSwitch( 
                    checked = showPronunciation, 
                    onCheckedChange = onToggleShowPronunciation 
                ) 
            } 
            
            if (showPronunciation) { 
                Spacer(modifier = Modifier.height(14.dp)) 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(14.dp) 
                ) { 
                    val isRomanized = pronunciationType == "romanized" 
                    val isJapanese = pronunciationType == "japanese" 
                    
                    Pronunciation3DOptionButton( 
                        modifier = Modifier.weight(1f), 
                        isSelected = isRomanized, 
                        topFurigana = "ni   hon   go", 
                        centerKanji = "日本語", 
                        bottomLabel = "Romanized", 
                        onClick = { onChangePronunciationType("romanized") } 
                    ) 
                    
                    Pronunciation3DOptionButton( 
                        modifier = Modifier.weight(1f), 
                        isSelected = isJapanese, 
                        topFurigana = "に   ほん   ご", 
                        centerKanji = "日本語", 
                        bottomLabel = "Japanese", 
                        onClick = { onChangePronunciationType("japanese") } 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(20.dp)) 
            
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Text( 
                    text = "Enlarge Text Font", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = BlossomNunito, 
                    color = Color.White 
                ) 
                
                BlossomSquircleSwitch( 
                    checked = enlargeTextFont, 
                    onCheckedChange = onToggleEnlargeTextFont 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(20.dp)) 
            
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Text( 
                    text = "Show Images", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = BlossomNunito, 
                    color = Color.White 
                ) 
                
                BlossomSquircleSwitch( 
                    checked = showImages, 
                    onCheckedChange = onToggleShowImages 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(20.dp)) 
            
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Text( 
                    text = "Highlight word while audio plays", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = BlossomNunito, 
                    color = Color.White 
                ) 
                
                BlossomSquircleSwitch( 
                    checked = highlightWordOnAudio, 
                    onCheckedChange = onToggleHighlightWordOnAudio 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
        } 
    } 
} 
    
@Composable
private fun Pronunciation3DOptionButton( 
    isSelected: Boolean, 
    topFurigana: String, 
    centerKanji: String, 
    bottomLabel: String, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val interactionSource = remember { MutableInteractionSource() } 
    val isPressed by interactionSource.collectIsPressedAsState() 
    val view = LocalView.current 
    val coroutineScope = rememberCoroutineScope() 
    var isClickAnimating by remember { mutableStateOf(false) } 
    var hasVibrated by remember { mutableStateOf(false) } 
    
    val isEffectivelyPressed = isPressed || isClickAnimating 
    val lipHeight = 4.dp 
    
    val currentBottomLip by animateDpAsState( 
        targetValue = if (isEffectivelyPressed) 0.dp else lipHeight, 
        animationSpec = tween(durationMillis = 60), 
        label = "pronunciationBottomLip" 
    ) 
    val topPush by animateDpAsState( 
        targetValue = if (isEffectivelyPressed) lipHeight else 0.dp, 
        animationSpec = tween(durationMillis = 60), 
        label = "pronunciationTopPush" 
    ) 
    
    LaunchedEffect(isPressed) { 
        if (isPressed) { 
            if (!hasVibrated) { 
                hasVibrated = true 
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
            } 
        } else { 
            delay(80L) 
            hasVibrated = false 
        } 
    } 
    
    val shape = RoundedCornerShape(16.dp) 
    val faceColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF201F21) 
    val lipColor = if (isSelected) Color(0xFF0F172A) else Color(0xFF141416) 
    val borderColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFF333338) 
    val borderWidth = if (isSelected) 2.dp else 1.dp 
    
    Box( 
        modifier = modifier 
            .height(120.dp) 
            .padding(top = topPush) 
            .clip(shape) 
            .background(lipColor) 
            .clickable( 
                interactionSource = interactionSource, 
                indication = null, 
                onClick = { 
                    if (!hasVibrated) { 
                        hasVibrated = true 
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) 
                    } 
                    coroutineScope.launch { 
                        isClickAnimating = true 
                        delay(65L) 
                        isClickAnimating = false 
                        delay(25L) 
                        hasVibrated = false 
                        onClick() 
                    } 
                } 
            ) 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxSize() 
                .padding(bottom = currentBottomLip) 
                .clip(shape) 
                .background(faceColor) 
                .border(borderWidth, borderColor, shape) 
                .padding(vertical = 12.dp, horizontal = 8.dp), 
            contentAlignment = Alignment.Center 
        ) { 
            Column( 
                horizontalAlignment = Alignment.CenterHorizontally, 
                verticalArrangement = Arrangement.Center 
            ) { 
                Text( 
                    text = topFurigana, 
                    fontSize = 11.sp, 
                    color = if (isSelected) Color(0xFF93C5FD) else Color(0xFF9CA3AF), 
                    fontWeight = FontWeight.Normal 
                ) 
                Spacer(modifier = Modifier.height(4.dp)) 
                Text( 
                    text = centerKanji, 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Normal, 
                    color = if (isSelected) Color(0xFF60A5FA) else Color(0xFFE4E4E7) 
                ) 
                Spacer(modifier = Modifier.height(6.dp)) 
                Text( 
                    text = bottomLabel, 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.Normal, 
                    fontFamily = BlossomNunito, 
                    color = if (isSelected) Color(0xFF93C5FD) else Color(0xFF9CA3AF) 
                ) 
            } 
        } 
    } 
}
