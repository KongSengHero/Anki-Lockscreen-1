package com.ankilock.ui.reading
    
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.PreferencesManager 
import com.ankilock.data.StoryThemes 
import com.ankilock.ui.blossom.BlossomColors 
import com.ankilock.ui.blossom.BlossomShapes 
import com.ankilock.ui.components.GlobalSeekerContainer 
import com.ankilock.ui.components.GlobalSeekerRow 
import com.ankilock.ui.components.SlidingPillSwitcher 
import com.ankilock.ui.components.Squircle3DButton 
import androidx.compose.runtime.mutableFloatStateOf 
import kotlin.math.roundToInt 
    
@OptIn(ExperimentalMaterial3Api::class)  
@Composable
fun StoryConfigBottomSheet( 
    prefs: PreferencesManager, 
    onDismiss: () -> Unit, 
    onSaved: () -> Unit 
) { 
    val context = LocalContext.current 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    
    val jlptLevels = listOf( 
        "N5" to "Beginner", 
        "N4" to "Elementary", 
        "N3" to "Intermediate", 
        "N2" to "Pre-Adv", 
        "N1" to "Advanced" 
    ) 
    
    val storyLengths = listOf( 
        "Short" to "~150w", 
        "Medium" to "~300w", 
        "Long" to "~500w" 
    ) 
    
    val connectingWordsSteps = listOf(0, 5, 7, 9, 11, 22, -1) 
    val vocabFilterOptions = listOf("all", "learn", "review", "learned") 
    val vocabFilterLabels = mapOf("all" to "ALL", "learn" to "Learn", "review" to "Review", "learned" to "Learned") 
    
    var selectedLevel by remember { 
        mutableStateOf(if (prefs.readingJlptLevel.isNotBlank()) prefs.readingJlptLevel else "N5") 
    } 
    var selectedLength by remember { 
        mutableStateOf(if (prefs.storyLength.isNotBlank()) prefs.storyLength else "Medium") 
    } 
    var selectedWordsCount by remember { 
        val count = prefs.storyConnectingWordsCount 
        mutableIntStateOf(if (count in connectingWordsSteps) count else 5) 
    } 
    var sliderIndex by remember { 
        val idx = connectingWordsSteps.indexOf(selectedWordsCount).let { if (it >= 0) it.toFloat() else 1f } 
        mutableFloatStateOf(idx) 
    } 
    var selectedVocabFilter by remember { 
        mutableStateOf(prefs.storyVocabularyFilter) 
    } 
    
    var selectedTab by remember { mutableIntStateOf(if (prefs.isCustomThemeModeActive) 1 else 0) } 
    var isCustomActive by remember { mutableStateOf(prefs.isCustomThemeModeActive) } 
    var disabledThemes by remember { mutableStateOf(prefs.disabledStoryThemes.toMutableSet()) } 
    var disabledTopics by remember { mutableStateOf(prefs.disabledStoryTopics.toMutableSet()) } 
    val expandedThemes = remember { mutableStateOf(mutableSetOf<String>()) } 
    var themeSearchQuery by remember { mutableStateOf("") } 
    
    var selectedCustomTheme by remember { 
        mutableStateOf(prefs.customStoryTheme ?: StoryThemes.ALL_THEMES.first()) 
    } 
    var selectedCustomTopic by remember { 
        mutableStateOf(prefs.customStoryTopic) 
    } 
    
    val filteredThemes = remember(themeSearchQuery) { 
        if (themeSearchQuery.isBlank()) { 
            StoryThemes.ALL_THEMES 
        } else { 
            val q = themeSearchQuery.trim().lowercase() 
            StoryThemes.ALL_THEMES.filter { key -> 
                key.lowercase().contains(q) || 
                StoryThemes.formatThemeName(key).lowercase().contains(q) || 
                (StoryThemes.CATEGORIES[key]?.any { it.lowercase().contains(q) } == true) 
            } 
        } 
    } 
    
    val noBounceNestedScroll = remember { 
        object : NestedScrollConnection { 
            override fun onPostScroll( 
                consumed: Offset, 
                available: Offset, 
                source: NestedScrollSource 
            ): Offset { 
                return if (available.y < 0f) Offset(0f, available.y) else Offset.Zero 
            } 
            override suspend fun onPostFling( 
                consumed: Velocity, 
                available: Velocity 
            ): Velocity { 
                return Velocity(0f, available.y) 
            } 
        } 
    } 
    
    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = BlossomColors.SurfaceCard1, 
        contentColor = BlossomColors.TextPrimary, 
        windowInsets = WindowInsets(0) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .fillMaxHeight(0.90f) 
                .padding(horizontal = 20.dp) 
                .navigationBarsPadding() 
        ) { 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(vertical = 12.dp), 
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
                                modifier = Modifier.size(19.dp) 
                            ) 
                        } 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Column { 
                        Text( 
                            text = "Story Configuration", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 17.sp, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        Text( 
                            text = if (selectedTab == 1) "Custom Mode Active" else "Randomizer Active", 
                            fontSize = 12.sp, 
                            color = if (selectedTab == 1) BlossomColors.WisteriaViolet else BlossomColors.SakuraRose, 
                            fontWeight = FontWeight.Medium 
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
            
            Column( 
                modifier = Modifier 
                    .weight(1f) 
                    .nestedScroll(noBounceNestedScroll) 
                    .verticalScroll(rememberScrollState()), 
                verticalArrangement = Arrangement.spacedBy(16.dp) 
            ) { 
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                    Text( 
                        text = "JLPT Difficulty Level", 
                        fontSize = 12.5.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextSecondary 
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
                        fontSize = 12.5.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextSecondary 
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
                
                GlobalSeekerContainer { 
                    GlobalSeekerRow( 
                        icon = Icons.Filled.Tune, 
                        label = "Connecting Studied Words", 
                        valueDisplay = when (selectedWordsCount) { 
                            0 -> "Free (0 words)" 
                            -1 -> "ALL words" 
                            else -> "$selectedWordsCount words" 
                        }, 
                        value = sliderIndex, 
                        valueRange = 0f..connectingWordsSteps.lastIndex.toFloat(), 
                        onValueChange = { newIdx -> 
                            sliderIndex = newIdx 
                            val nearest = newIdx.roundToInt().coerceIn(0, connectingWordsSteps.lastIndex) 
                            selectedWordsCount = connectingWordsSteps[nearest] 
                        }, 
                        onValueChangeFinished = { 
                            sliderIndex = connectingWordsSteps.indexOf(selectedWordsCount).toFloat() 
                        }, 
                        accentColor = BlossomColors.SakuraRose, 
                        snapValues = connectingWordsSteps.indices.map { it.toFloat() } 
                    ) 
                } 
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { 
                    Text( 
                        text = "Vocabulary Filter", 
                        fontSize = 12.5.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextSecondary 
                    ) 
                    SlidingPillSwitcher( 
                        options = vocabFilterOptions, 
                        selectedOption = selectedVocabFilter, 
                        onOptionSelected = { selectedVocabFilter = it }, 
                        labelProvider = { vocabFilterLabels[it] ?: it.uppercase() }, 
                        activeColor = BlossomColors.SakuraRose 
                    ) 
                } 
                
                TabRow( 
                    selectedTabIndex = selectedTab, 
                    containerColor = BlossomColors.SurfaceElevated, 
                    contentColor = BlossomColors.TextPrimary, 
                    indicator = { tabPositions -> 
                        TabRowDefaults.SecondaryIndicator( 
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]), 
                            color = BlossomColors.SakuraRose, 
                            height = 3.dp 
                        ) 
                    }, 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Tab( 
                        selected = selectedTab == 0, 
                        onClick = { selectedTab = 0 }, 
                        text = { 
                            Text( 
                                text = "Randomizer Pool", 
                                fontSize = 13.sp, 
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, 
                                color = if (selectedTab == 0) BlossomColors.SakuraRose else BlossomColors.TextSecondary 
                            ) 
                        } 
                    ) 
                    Tab( 
                        selected = selectedTab == 1, 
                        onClick = { selectedTab = 1 }, 
                        text = { 
                            Text( 
                                text = "Custom Story", 
                                fontSize = 13.sp, 
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, 
                                color = if (selectedTab == 1) BlossomColors.SakuraRose else BlossomColors.TextSecondary 
                            ) 
                        } 
                    ) 
                } 
                
                if (selectedTab == 0) { 
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.SpaceBetween 
                        ) { 
                            Text( 
                                text = "Themes & Topics Pool", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = BlossomColors.TextSecondary 
                            ) 
                            
                            Surface( 
                                shape = RoundedCornerShape(8.dp), 
                                color = BlossomColors.SurfaceElevated, 
                                border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle), 
                                modifier = Modifier.clickable { 
                                    disabledThemes = mutableSetOf() 
                                    disabledTopics = mutableSetOf() 
                                    Toast.makeText(context, "All themes and topics enabled", Toast.LENGTH_SHORT).show() 
                                } 
                            ) { 
                                Row( 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp) 
                                ) { 
                                    Icon( 
                                        Icons.Filled.Refresh, 
                                        contentDescription = null, 
                                        tint = BlossomColors.SakuraRose, 
                                        modifier = Modifier.size(13.dp) 
                                    ) 
                                    Spacer(modifier = Modifier.width(4.dp)) 
                                    Text( 
                                        text = "Enable All", 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Medium, 
                                        color = BlossomColors.SakuraRose 
                                    ) 
                                } 
                            } 
                        } 
                        
                        Text( 
                            text = "Checked themes will be randomly picked during story generation. Expand any theme to toggle specific topics.", 
                            fontSize = 12.sp, 
                            color = BlossomColors.TextMuted, 
                            lineHeight = 16.sp 
                        ) 
                        
                        OutlinedTextField( 
                            value = themeSearchQuery, 
                            onValueChange = { themeSearchQuery = it }, 
                            placeholder = { Text("Search themes & topics...", color = BlossomColors.TextMuted, fontSize = 13.sp) }, 
                            leadingIcon = { 
                                Icon(Icons.Default.Search, contentDescription = null, tint = BlossomColors.TextSecondary, modifier = Modifier.size(16.dp)) 
                            }, 
                            trailingIcon = { 
                                if (themeSearchQuery.isNotEmpty()) { 
                                    IconButton(onClick = { themeSearchQuery = "" }) { 
                                        Icon(Icons.Filled.Close, contentDescription = "Clear", tint = BlossomColors.TextSecondary, modifier = Modifier.size(16.dp)) 
                                    } 
                                } 
                            }, 
                            singleLine = true, 
                            colors = OutlinedTextFieldDefaults.colors( 
                                focusedContainerColor = BlossomColors.SurfaceElevated, 
                                unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                                focusedTextColor = BlossomColors.TextPrimary, 
                                unfocusedTextColor = BlossomColors.TextPrimary, 
                                focusedBorderColor = BlossomColors.SakuraRose, 
                                unfocusedBorderColor = BlossomColors.CardBorder 
                            ), 
                            shape = RoundedCornerShape(12.dp), 
                            modifier = Modifier.fillMaxWidth() 
                        ) 
                        
                        if (filteredThemes.isEmpty()) { 
                            Box( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .padding(vertical = 16.dp), 
                                contentAlignment = Alignment.Center 
                            ) { 
                                Text("No themes match your search", color = BlossomColors.TextMuted, fontSize = 13.sp) 
                            } 
                        } 
                        
                        filteredThemes.forEach { themeKey -> 
                            val isThemeEnabled = themeKey !in disabledThemes 
                            val isExpanded = themeKey in expandedThemes.value 
                            val topics = StoryThemes.CATEGORIES[themeKey] ?: emptyList() 
                            val badgeColors = StoryThemes.getThemeBadgeColors(themeKey) 
                            
                            Card( 
                                shape = RoundedCornerShape(14.dp), 
                                colors = CardDefaults.cardColors( 
                                    containerColor = if (isThemeEnabled) BlossomColors.SurfaceElevated else BlossomColors.SurfaceCard3.copy(alpha = 0.5f) 
                                ), 
                                border = BorderStroke( 
                                    1.dp, 
                                    if (isThemeEnabled) badgeColors.borderColor.copy(alpha = 0.35f) else BlossomColors.CardBorderSubtle 
                                ), 
                                modifier = Modifier.fillMaxWidth() 
                            ) { 
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) { 
                                    Row( 
                                        modifier = Modifier.fillMaxWidth(), 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.SpaceBetween 
                                    ) { 
                                        Row( 
                                            verticalAlignment = Alignment.CenterVertically, 
                                            modifier = Modifier.weight(1f) 
                                        ) { 
                                            Checkbox( 
                                                checked = isThemeEnabled, 
                                                onCheckedChange = { 
                                                    val updated = disabledThemes.toMutableSet() 
                                                    if (themeKey in updated) { 
                                                        updated.remove(themeKey) 
                                                    } else { 
                                                        val enabledCount = StoryThemes.ALL_THEMES.count { it !in updated } 
                                                        if (enabledCount <= 1) { 
                                                            Toast.makeText(context, "At least one theme must remain enabled", Toast.LENGTH_SHORT).show() 
                                                            return@Checkbox 
                                                        } 
                                                        updated.add(themeKey) 
                                                    } 
                                                    disabledThemes = updated 
                                                }, 
                                                colors = CheckboxDefaults.colors( 
                                                    checkedColor = badgeColors.contentColor, 
                                                    checkmarkColor = Color.Black, 
                                                    uncheckedColor = BlossomColors.TextMuted 
                                                ), 
                                                modifier = Modifier.size(32.dp) 
                                            ) 
                                            
                                            Spacer(modifier = Modifier.width(6.dp)) 
                                            
                                            Surface( 
                                                shape = RoundedCornerShape(6.dp), 
                                                color = badgeColors.backgroundColor, 
                                                border = BorderStroke(1.dp, badgeColors.borderColor) 
                                            ) { 
                                                Text( 
                                                    text = StoryThemes.formatThemeName(themeKey), 
                                                    fontSize = 12.sp, 
                                                    fontWeight = FontWeight.Bold, 
                                                    color = badgeColors.contentColor, 
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp) 
                                                ) 
                                            } 
                                            
                                            Spacer(modifier = Modifier.width(8.dp)) 
                                            
                                            Text( 
                                                text = "(${topics.size} topics)", 
                                                fontSize = 11.sp, 
                                                color = BlossomColors.TextMuted 
                                            ) 
                                        } 
                                        
                                        IconButton( 
                                            onClick = { 
                                                val updated = expandedThemes.value.toMutableSet() 
                                                if (themeKey in updated) updated.remove(themeKey) else updated.add(themeKey) 
                                                expandedThemes.value = updated 
                                            }, 
                                            modifier = Modifier.size(32.dp) 
                                        ) { 
                                            Icon( 
                                                if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, 
                                                contentDescription = "Expand topics", 
                                                tint = BlossomColors.TextSecondary, 
                                                modifier = Modifier.size(20.dp) 
                                            ) 
                                        } 
                                    } 
                                    
                                    if (isExpanded) { 
                                        Spacer(modifier = Modifier.height(4.dp)) 
                                        Column( 
                                            modifier = Modifier 
                                                .fillMaxWidth() 
                                                .padding(start = 38.dp, end = 8.dp, bottom = 6.dp), 
                                            verticalArrangement = Arrangement.spacedBy(4.dp) 
                                        ) { 
                                            topics.forEach { topic -> 
                                                val isTopicEnabled = topic !in disabledTopics 
                                                Row( 
                                                    modifier = Modifier 
                                                        .fillMaxWidth() 
                                                        .clickable { 
                                                            val updated = disabledTopics.toMutableSet() 
                                                            if (topic in updated) updated.remove(topic) else updated.add(topic) 
                                                            disabledTopics = updated 
                                                        } 
                                                        .padding(vertical = 4.dp), 
                                                    verticalAlignment = Alignment.CenterVertically 
                                                ) { 
                                                    Checkbox( 
                                                        checked = isTopicEnabled, 
                                                        onCheckedChange = { 
                                                            val updated = disabledTopics.toMutableSet() 
                                                            if (topic in updated) updated.remove(topic) else updated.add(topic) 
                                                            disabledTopics = updated 
                                                        }, 
                                                        colors = CheckboxDefaults.colors( 
                                                            checkedColor = badgeColors.contentColor, 
                                                            checkmarkColor = Color.Black, 
                                                            uncheckedColor = BlossomColors.TextMuted 
                                                        ), 
                                                        modifier = Modifier.size(24.dp) 
                                                    ) 
                                                    Spacer(modifier = Modifier.width(8.dp)) 
                                                    Text( 
                                                        text = topic, 
                                                        fontSize = 12.sp, 
                                                        color = if (isTopicEnabled) BlossomColors.TextPrimary else BlossomColors.TextMuted 
                                                    ) 
                                                } 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 
                } else { 
                    val topics = StoryThemes.CATEGORIES[selectedCustomTheme] ?: emptyList() 
                    val badgeColors = StoryThemes.getThemeBadgeColors(selectedCustomTheme) 
                    
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) { 
                        if (isCustomActive) { 
                            Surface( 
                                shape = RoundedCornerShape(12.dp), 
                                color = BlossomColors.WisteriaVioletContainer.copy(alpha = 0.5f), 
                                border = BorderStroke(1.dp, BlossomColors.WisteriaViolet.copy(alpha = 0.4f)), 
                                modifier = Modifier.fillMaxWidth() 
                            ) { 
                                Row( 
                                    modifier = Modifier 
                                        .fillMaxWidth() 
                                        .padding(12.dp), 
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.SpaceBetween 
                                ) { 
                                    Column(modifier = Modifier.weight(1f)) { 
                                        Text( 
                                            text = "LOCKED CUSTOM STORY", 
                                            fontSize = 10.sp, 
                                            fontWeight = FontWeight.Bold, 
                                            color = BlossomColors.WisteriaViolet, 
                                            letterSpacing = 1.sp 
                                        ) 
                                        Spacer(modifier = Modifier.height(2.dp)) 
                                        Text( 
                                            text = "${StoryThemes.formatThemeName(selectedCustomTheme)}: ${selectedCustomTopic ?: "Any Topic"}", 
                                            fontSize = 13.sp, 
                                            fontWeight = FontWeight.Medium, 
                                            color = BlossomColors.TextPrimary, 
                                            maxLines = 1, 
                                            overflow = TextOverflow.Ellipsis 
                                        ) 
                                    } 
                                    
                                    OutlinedButton( 
                                        onClick = { 
                                            isCustomActive = false 
                                            prefs.isCustomThemeModeActive = false 
                                            selectedTab = 0 
                                            selectedCustomTopic = null 
                                            Toast.makeText(context, "Switched back to Randomizer", Toast.LENGTH_SHORT).show() 
                                        }, 
                                        shape = RoundedCornerShape(8.dp), 
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BlossomColors.BlossomRed), 
                                        border = BorderStroke(1.dp, BlossomColors.BlossomRed.copy(alpha = 0.5f)), 
                                        modifier = Modifier.height(32.dp) 
                                    ) { 
                                        Text(text = "Clear", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) 
                                    } 
                                } 
                            } 
                        } 
                        
                        OutlinedTextField( 
                            value = themeSearchQuery, 
                            onValueChange = { themeSearchQuery = it }, 
                            placeholder = { Text("Search themes & topics...", color = BlossomColors.TextMuted, fontSize = 13.sp) }, 
                            leadingIcon = { 
                                Icon(Icons.Default.Search, contentDescription = null, tint = BlossomColors.TextSecondary, modifier = Modifier.size(16.dp)) 
                            }, 
                            trailingIcon = { 
                                if (themeSearchQuery.isNotEmpty()) { 
                                    IconButton(onClick = { themeSearchQuery = "" }) { 
                                        Icon(Icons.Filled.Close, contentDescription = "Clear", tint = BlossomColors.TextSecondary, modifier = Modifier.size(16.dp)) 
                                    } 
                                } 
                            }, 
                            singleLine = true, 
                            colors = OutlinedTextFieldDefaults.colors( 
                                focusedContainerColor = BlossomColors.SurfaceElevated, 
                                unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                                focusedTextColor = BlossomColors.TextPrimary, 
                                unfocusedTextColor = BlossomColors.TextPrimary, 
                                focusedBorderColor = BlossomColors.SakuraRose, 
                                unfocusedBorderColor = BlossomColors.CardBorder 
                            ), 
                            shape = RoundedCornerShape(12.dp), 
                            modifier = Modifier.fillMaxWidth() 
                        ) 
                        
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { 
                            Text( 
                                text = "1. Select Theme", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = BlossomColors.TextSecondary 
                            ) 
                            
                            Row( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .horizontalScroll(rememberScrollState()), 
                                horizontalArrangement = Arrangement.spacedBy(8.dp) 
                            ) { 
                                filteredThemes.forEach { themeKey -> 
                                    val isSelected = themeKey == selectedCustomTheme 
                                    val style = StoryThemes.getThemeBadgeColors(themeKey) 
                                    
                                    Surface( 
                                        shape = RoundedCornerShape(10.dp), 
                                        color = if (isSelected) style.backgroundColor else BlossomColors.SurfaceElevated, 
                                        border = BorderStroke( 
                                            if (isSelected) 2.dp else 1.dp, 
                                            if (isSelected) style.contentColor else BlossomColors.CardBorderSubtle 
                                        ), 
                                        modifier = Modifier.clickable { 
                                            selectedCustomTheme = themeKey 
                                            selectedCustomTopic = null 
                                        } 
                                    ) { 
                                        Row( 
                                            verticalAlignment = Alignment.CenterVertically, 
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp) 
                                        ) { 
                                            if (isSelected) { 
                                                Icon( 
                                                    Icons.Filled.Check, 
                                                    contentDescription = null, 
                                                    tint = style.contentColor, 
                                                    modifier = Modifier.size(14.dp) 
                                                ) 
                                                Spacer(modifier = Modifier.width(6.dp)) 
                                            } 
                                            Text( 
                                                text = StoryThemes.formatThemeName(themeKey), 
                                                fontSize = 13.sp, 
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                                color = if (isSelected) style.contentColor else BlossomColors.TextPrimary 
                                            ) 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                        
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { 
                            Text( 
                                text = "2. Select Topic for ${StoryThemes.formatThemeName(selectedCustomTheme)}", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = BlossomColors.TextSecondary 
                            ) 
                            
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { 
                                Surface( 
                                    shape = RoundedCornerShape(10.dp), 
                                    color = if (selectedCustomTopic == null) badgeColors.backgroundColor.copy(alpha = 0.6f) else BlossomColors.SurfaceElevated, 
                                    border = BorderStroke( 
                                        1.dp, 
                                        if (selectedCustomTopic == null) badgeColors.borderColor else BlossomColors.CardBorderSubtle 
                                    ), 
                                    modifier = Modifier 
                                        .fillMaxWidth() 
                                        .clickable { selectedCustomTopic = null } 
                                ) { 
                                    Row( 
                                        modifier = Modifier 
                                            .fillMaxWidth() 
                                            .padding(horizontal = 12.dp, vertical = 10.dp), 
                                        verticalAlignment = Alignment.CenterVertically 
                                    ) { 
                                        RadioButton( 
                                            selected = selectedCustomTopic == null, 
                                            onClick = { selectedCustomTopic = null }, 
                                            colors = RadioButtonDefaults.colors( 
                                                selectedColor = badgeColors.contentColor, 
                                                unselectedColor = BlossomColors.TextMuted 
                                            ), 
                                            modifier = Modifier.size(20.dp) 
                                        ) 
                                        Spacer(modifier = Modifier.width(10.dp)) 
                                        Text( 
                                            text = "Any topic in ${StoryThemes.formatThemeName(selectedCustomTheme)} (Random)", 
                                            fontSize = 13.sp, 
                                            fontWeight = if (selectedCustomTopic == null) FontWeight.Bold else FontWeight.Medium, 
                                            color = if (selectedCustomTopic == null) badgeColors.contentColor else BlossomColors.TextPrimary 
                                        ) 
                                    } 
                                } 
                                
                                topics.forEach { topic -> 
                                    val isTopicSelected = selectedCustomTopic == topic 
                                    Surface( 
                                        shape = RoundedCornerShape(10.dp), 
                                        color = if (isTopicSelected) badgeColors.backgroundColor.copy(alpha = 0.6f) else BlossomColors.SurfaceElevated, 
                                        border = BorderStroke( 
                                            1.dp, 
                                            if (isTopicSelected) badgeColors.borderColor else BlossomColors.CardBorderSubtle 
                                        ), 
                                        modifier = Modifier 
                                            .fillMaxWidth() 
                                            .clickable { selectedCustomTopic = topic } 
                                    ) { 
                                        Row( 
                                            modifier = Modifier 
                                                .fillMaxWidth() 
                                                .padding(horizontal = 12.dp, vertical = 10.dp), 
                                            verticalAlignment = Alignment.CenterVertically 
                                        ) { 
                                            RadioButton( 
                                                selected = isTopicSelected, 
                                                onClick = { selectedCustomTopic = topic }, 
                                                colors = RadioButtonDefaults.colors( 
                                                    selectedColor = badgeColors.contentColor, 
                                                    unselectedColor = BlossomColors.TextMuted 
                                                ), 
                                                modifier = Modifier.size(20.dp) 
                                            ) 
                                            Spacer(modifier = Modifier.width(10.dp)) 
                                            Text( 
                                                text = topic, 
                                                fontSize = 13.sp, 
                                                fontWeight = if (isTopicSelected) FontWeight.Bold else FontWeight.Normal, 
                                                color = if (isTopicSelected) badgeColors.contentColor else BlossomColors.TextPrimary 
                                            ) 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(12.dp)) 
            
            Squircle3DButton( 
                onClick = { 
                    prefs.readingJlptLevel = selectedLevel 
                    prefs.storyLength = selectedLength 
                    prefs.storyConnectingWordsCount = selectedWordsCount 
                    prefs.storyVocabularyFilter = selectedVocabFilter 
                    prefs.disabledStoryThemes = disabledThemes 
                    prefs.disabledStoryTopics = disabledTopics 
                    if (selectedTab == 1) { 
                        prefs.isCustomThemeModeActive = true 
                        prefs.customStoryTheme = selectedCustomTheme 
                        prefs.customStoryTopic = selectedCustomTopic 
                    } else { 
                        prefs.isCustomThemeModeActive = false 
                        prefs.customStoryTheme = null 
                        prefs.customStoryTopic = null 
                    } 
                    onSaved() 
                    onDismiss() 
                }, 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(50.dp), 
                shape = BlossomShapes.SquircleMedium, 
                containerColor = BlossomColors.SakuraRose, 
                bevelColor = BlossomColors.SakuraRoseLip, 
                depth = 3.dp 
            ) { 
                Text( 
                    text = "Apply & Save Settings", 
                    fontWeight = FontWeight.SemiBold, 
                    fontSize = 14.5.sp, 
                    color = Color.White 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(8.dp)) 
        } 
    } 
} 
