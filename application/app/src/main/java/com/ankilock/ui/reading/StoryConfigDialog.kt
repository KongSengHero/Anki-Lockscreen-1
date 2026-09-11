package com.ankilock.ui.reading
    
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StoryThemes
import com.ankilock.ui.blossom.BlossomColors
    
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryConfigDialog( 
    prefs: PreferencesManager, 
    onDismiss: () -> Unit, 
    onSaved: () -> Unit 
) { 
    val jlptLevels = listOf( 
        "N5" to "Beginner", 
        "N4" to "Elementary", 
        "N3" to "Intermediate", 
        "N2" to "Pre-Advanced", 
        "N1" to "Advanced" 
    ) 
    
    val storyLengths = listOf( 
        "Short" to "~150w", 
        "Medium" to "~300w", 
        "Long" to "~500w" 
    ) 
    
    val connectingWordsOptions = listOf(0, 3, 4, 5) 
    val questionsCountOptions = listOf(3, 4, 5) 
    
    var selectedLevel by remember { 
        mutableStateOf(if (prefs.readingJlptLevel.isNotBlank()) prefs.readingJlptLevel else "N5") 
    } 
    var selectedLength by remember { 
        mutableStateOf(if (prefs.storyLength.isNotBlank()) prefs.storyLength else "Medium") 
    } 
    var selectedWordsCount by remember { 
        mutableIntStateOf(prefs.storyConnectingWordsCount) 
    } 
    var selectedQuestionsCount by remember { 
        mutableIntStateOf(prefs.storyQuestionsCount) 
    } 
    var selectedTheme by remember { 
        mutableStateOf( 
            if (prefs.isCustomThemeModeActive && !prefs.customStoryTheme.isNullOrBlank()) { 
                prefs.customStoryTheme!! 
            } else { 
                "Random" 
            } 
        ) 
    } 
    
    Dialog(onDismissRequest = onDismiss) { 
        Card( 
            shape = RoundedCornerShape(24.dp), 
            colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(vertical = 12.dp) 
        ) { 
            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(20.dp) 
                    .verticalScroll(rememberScrollState()), 
                verticalArrangement = Arrangement.spacedBy(16.dp) 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        Surface( 
                            shape = CircleShape, 
                            color = BlossomColors.SakuraRoseContainer, 
                            modifier = Modifier.size(36.dp) 
                        ) { 
                            Box(contentAlignment = Alignment.Center) { 
                                Icon( 
                                    Icons.Filled.Tune, 
                                    contentDescription = null, 
                                    tint = BlossomColors.SakuraRose, 
                                    modifier = Modifier.size(18.dp) 
                                ) 
                            } 
                        } 
                        Spacer(modifier = Modifier.width(10.dp)) 
                        Column { 
                            Text( 
                                text = "Story Configuration", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp, 
                                color = BlossomColors.TextPrimary 
                            ) 
                            Text( 
                                text = "Length, theme, level & connecting words", 
                                fontSize = 11.sp, 
                                color = BlossomColors.TextSecondary 
                            ) 
                        } 
                    } 
                    
                    IconButton( 
                        onClick = onDismiss, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        Icon( 
                            Icons.Filled.Close, 
                            contentDescription = "Close", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                } 
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                    Text( 
                        text = "JLPT Difficulty Level", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(6.dp) 
                    ) { 
                        jlptLevels.forEach { (level, subtitle) -> 
                            val isSelected = selectedLevel == level 
                            Surface( 
                                onClick = { selectedLevel = level }, 
                                shape = RoundedCornerShape(10.dp), 
                                color = if (isSelected) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceElevated, 
                                border = BorderStroke( 
                                    1.dp, 
                                    if (isSelected) BlossomColors.SakuraRose else BlossomColors.CardBorder 
                                ), 
                                modifier = Modifier.weight(1f) 
                            ) { 
                                Column( 
                                    modifier = Modifier.padding(vertical = 8.dp), 
                                    horizontalAlignment = Alignment.CenterHorizontally 
                                ) { 
                                    Text( 
                                        text = level, 
                                        fontSize = 13.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextPrimary 
                                    ) 
                                    Text( 
                                        text = subtitle, 
                                        fontSize = 9.sp, 
                                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextMuted, 
                                        maxLines = 1 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                    Text( 
                        text = "Story Length", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp) 
                    ) { 
                        storyLengths.forEach { (length, desc) -> 
                            val isSelected = selectedLength.equals(length, ignoreCase = true) 
                            Surface( 
                                onClick = { selectedLength = length }, 
                                shape = RoundedCornerShape(10.dp), 
                                color = if (isSelected) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceElevated, 
                                border = BorderStroke( 
                                    1.dp, 
                                    if (isSelected) BlossomColors.SakuraRose else BlossomColors.CardBorder 
                                ), 
                                modifier = Modifier.weight(1f) 
                            ) { 
                                Column( 
                                    modifier = Modifier.padding(vertical = 8.dp), 
                                    horizontalAlignment = Alignment.CenterHorizontally 
                                ) { 
                                    Text( 
                                        text = length, 
                                        fontSize = 13.sp, 
                                        fontWeight = FontWeight.SemiBold, 
                                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextPrimary 
                                    ) 
                                    Text( 
                                        text = desc, 
                                        fontSize = 10.sp, 
                                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextMuted 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                    Text( 
                        text = "Connecting Studied Words (0 / 3 / 4 / 5)", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp) 
                    ) { 
                        connectingWordsOptions.forEach { count -> 
                            val isSelected = selectedWordsCount == count 
                            Surface( 
                                onClick = { selectedWordsCount = count }, 
                                shape = RoundedCornerShape(10.dp), 
                                color = if (isSelected) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceElevated, 
                                border = BorderStroke( 
                                    1.dp, 
                                    if (isSelected) BlossomColors.SakuraRose else BlossomColors.CardBorder 
                                ), 
                                modifier = Modifier.weight(1f) 
                            ) { 
                                Column( 
                                    modifier = Modifier.padding(vertical = 8.dp), 
                                    horizontalAlignment = Alignment.CenterHorizontally 
                                ) { 
                                    Text( 
                                        text = "$count", 
                                        fontSize = 15.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextPrimary 
                                    ) 
                                    Text( 
                                        text = if (count == 0) "Free" else "Words", 
                                        fontSize = 10.sp, 
                                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextMuted 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                    
                    Surface( 
                        shape = RoundedCornerShape(10.dp), 
                        color = BlossomColors.SurfaceElevated, 
                        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        Text( 
                            text = if (selectedWordsCount == 0) { 
                                "✨ 0 Words: Gemini creates a free, creative story without Anki card vocabulary constraints." 
                            } else { 
                                "🎯 $selectedWordsCount Words: Gemini weaves $selectedWordsCount target words from your Anki deck into the story narrative." 
                            }, 
                            fontSize = 11.sp, 
                            color = BlossomColors.TextSecondary, 
                            lineHeight = 16.sp, 
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp) 
                        ) 
                    } 
                } 
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                    Text( 
                        text = "Quiz Questions", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp) 
                    ) { 
                        questionsCountOptions.forEach { qCount -> 
                            val isSelected = selectedQuestionsCount == qCount 
                            Surface( 
                                onClick = { selectedQuestionsCount = qCount }, 
                                shape = RoundedCornerShape(10.dp), 
                                color = if (isSelected) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceElevated, 
                                border = BorderStroke( 
                                    1.dp, 
                                    if (isSelected) BlossomColors.SakuraRose else BlossomColors.CardBorder 
                                ), 
                                modifier = Modifier.weight(1f) 
                            ) { 
                                Column( 
                                    modifier = Modifier.padding(vertical = 8.dp), 
                                    horizontalAlignment = Alignment.CenterHorizontally 
                                ) { 
                                    Text( 
                                        text = "$qCount", 
                                        fontSize = 15.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextPrimary 
                                    ) 
                                    Text( 
                                        text = "Questions", 
                                        fontSize = 10.sp, 
                                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextMuted 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                    Text( 
                        text = "Story Theme", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    FlowRow( 
                        horizontalArrangement = Arrangement.spacedBy(6.dp), 
                        verticalArrangement = Arrangement.spacedBy(6.dp), 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        val isRandomSelected = selectedTheme == "Random" 
                        Surface( 
                            onClick = { selectedTheme = "Random" }, 
                            shape = RoundedCornerShape(8.dp), 
                            color = if (isRandomSelected) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceElevated, 
                            border = BorderStroke( 
                                1.dp, 
                                if (isRandomSelected) BlossomColors.SakuraRose else BlossomColors.CardBorder 
                            ) 
                        ) { 
                            Text( 
                                text = "🎲 Random / Any", 
                                fontSize = 11.sp, 
                                fontWeight = if (isRandomSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isRandomSelected) BlossomColors.SakuraRose else BlossomColors.TextSecondary, 
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp) 
                            ) 
                        } 
                        
                        StoryThemes.ALL_THEMES.forEach { themeKey -> 
                            val isSelected = selectedTheme == themeKey 
                            val formattedName = StoryThemes.formatThemeName(themeKey) 
                            Surface( 
                                onClick = { selectedTheme = themeKey }, 
                                shape = RoundedCornerShape(8.dp), 
                                color = if (isSelected) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceElevated, 
                                border = BorderStroke( 
                                    1.dp, 
                                    if (isSelected) BlossomColors.SakuraRose else BlossomColors.CardBorder 
                                ) 
                            ) { 
                                Text( 
                                    text = formattedName, 
                                    fontSize = 11.sp, 
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                    color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextSecondary, 
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp) 
                                ) 
                            } 
                        } 
                    } 
                } 
                
                Spacer(modifier = Modifier.height(4.dp)) 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    OutlinedButton( 
                        onClick = onDismiss, 
                        shape = RoundedCornerShape(12.dp), 
                        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                        modifier = Modifier.weight(1f) 
                    ) { 
                        Text( 
                            text = "Cancel", 
                            color = BlossomColors.TextSecondary, 
                            fontSize = 13.sp 
                        ) 
                    } 
                    
                    Button( 
                        onClick = { 
                            prefs.readingJlptLevel = selectedLevel 
                            prefs.storyLength = selectedLength 
                            prefs.storyConnectingWordsCount = selectedWordsCount 
                            prefs.storyQuestionsCount = selectedQuestionsCount 
                            if (selectedTheme == "Random") { 
                                prefs.isCustomThemeModeActive = false 
                                prefs.customStoryTheme = null 
                            } else { 
                                prefs.isCustomThemeModeActive = true 
                                prefs.customStoryTheme = selectedTheme 
                            } 
                            onSaved() 
                            onDismiss() 
                        }, 
                        shape = RoundedCornerShape(12.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = BlossomColors.SakuraRose), 
                        modifier = Modifier.weight(1f) 
                    ) { 
                        Text( 
                            text = "Save Settings", 
                            color = Color.White, 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.SemiBold 
                        ) 
                    } 
                } 
            } 
        } 
    } 
} 
