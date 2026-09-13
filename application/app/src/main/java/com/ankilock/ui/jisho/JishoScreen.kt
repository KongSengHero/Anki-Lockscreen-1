package com.ankilock.ui.jisho
    
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.JishoServiceHelper
import com.ankilock.data.JishoWord
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StorySessionManager
import com.ankilock.data.StoryWordItem
import com.ankilock.ui.components.Squircle3DButton
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomShapes
import com.ankilock.util.JapaneseTtsHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
    
@OptIn(ExperimentalLayoutApi::class) 
@Composable
fun JishoScreen( 
    padding: PaddingValues, 
    prefs: PreferencesManager, 
    initialQuery: String = "", 
    onInitialQueryConsumed: () -> Unit = {} 
) { 
    val context = LocalContext.current 
    val clipboardManager = LocalClipboardManager.current 
    val keyboardController = LocalSoftwareKeyboardController.current 
    val focusManager = LocalFocusManager.current 
    val scope = rememberCoroutineScope() 
    
    val ttsHelper = remember { JapaneseTtsHelper(context) } 
    
    DisposableEffect(Unit) { 
        onDispose { 
            ttsHelper.shutdown() 
        } 
    } 
    
    var query by remember { mutableStateOf("") } 
    var lastExecutedQuery by remember { mutableStateOf("") } 
    var results by remember { mutableStateOf<List<JishoWord>>(emptyList()) } 
    var isLoading by remember { mutableStateOf(false) } 
    var errorMessage by remember { mutableStateOf<String?>(null) } 
    var recentSearches by remember { mutableStateOf(prefs.recentJishoSearches) } 
    val expandedSlugs = remember { mutableStateListOf<String>() } 
    
    
    fun executeSearch(searchTerm: String) { 
        val trimmed = searchTerm.trim() 
        if (trimmed.isBlank()) { 
            results = emptyList() 
            isLoading = false 
            errorMessage = null 
            lastExecutedQuery = "" 
            return 
        } 
        if (trimmed == lastExecutedQuery && results.isNotEmpty()) { 
            return 
        } 
        
        lastExecutedQuery = trimmed 
        isLoading = true 
        errorMessage = null 
        
        scope.launch { 
            val res = JishoServiceHelper.searchWords(trimmed) 
            res.onSuccess { list -> 
                results = list 
                isLoading = false 
                if (list.isNotEmpty()) { 
                    prefs.addRecentJishoSearch(trimmed) 
                    recentSearches = prefs.recentJishoSearches 
                } 
            }.onFailure { err -> 
                errorMessage = err.localizedMessage ?: "Failed to connect to Jisho" 
                isLoading = false 
            } 
        } 
    } 
    
    LaunchedEffect(query) { 
        if (query.isBlank()) { 
            results = emptyList() 
            isLoading = false 
            errorMessage = null 
            return@LaunchedEffect 
        } 
        if (query.trim() == lastExecutedQuery) { 
            return@LaunchedEffect 
        } 
        delay(500) 
        executeSearch(query) 
    } 
    
    LaunchedEffect(initialQuery) { 
        if (initialQuery.isNotBlank()) { 
            query = initialQuery 
            executeSearch(initialQuery) 
            onInitialQueryConsumed() 
        } 
    } 
    
    Column( 
        modifier = Modifier 
            .fillMaxSize() 
            .padding(padding) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 16.dp, vertical = 12.dp) 
        ) { 
            OutlinedTextField( 
                value = query, 
                onValueChange = { query = it }, 
                modifier = Modifier.fillMaxWidth(), 
                placeholder = { 
                    Text( 
                        text = "Search Romaji, English, Kanji, Kana...", 
                        fontSize = 13.5.sp, 
                        color = BlossomColors.TextMuted 
                    ) 
                }, 
                leadingIcon = { 
                    Icon( 
                        imageVector = Icons.Default.Search, 
                        contentDescription = "Search", 
                        tint = BlossomColors.WarmOchre, 
                        modifier = Modifier.size(20.dp) 
                    ) 
                }, 
                trailingIcon = { 
                    if (query.isNotEmpty()) { 
                        IconButton(onClick = { 
                            query = "" 
                            results = emptyList() 
                            errorMessage = null 
                        }) { 
                            Icon( 
                                imageVector = Icons.Default.Close, 
                                contentDescription = "Clear", 
                                tint = BlossomColors.TextSecondary, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                        } 
                    } 
                }, 
                singleLine = true, 
                shape = BlossomShapes.SquircleMedium, 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceCard1, 
                    focusedBorderColor = BlossomColors.WarmOchre, 
                    unfocusedBorderColor = BlossomColors.CardBorder, 
                    cursorColor = BlossomColors.WarmOchre, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary 
                ), 
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), 
                keyboardActions = KeyboardActions( 
                    onSearch = { 
                        keyboardController?.hide() 
                        focusManager.clearFocus() 
                        executeSearch(query) 
                    } 
                ) 
            ) 
        } 
        
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .weight(1f) 
        ) { 
            if (query.isBlank()) { 
                LazyColumn( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .padding(horizontal = 16.dp), 
                    contentPadding = PaddingValues(bottom = 100.dp), 
                    verticalArrangement = Arrangement.spacedBy(16.dp) 
                ) { 
                    if (recentSearches.isNotEmpty()) { 
                        item { 
                            Row( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .padding(top = 4.dp, bottom = 4.dp), 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.SpaceBetween 
                            ) { 
                                Surface( 
                                    shape = BlossomShapes.Pill, 
                                    color = BlossomColors.SurfaceElevated, 
                                    border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
                                ) { 
                                    Text( 
                                        text = "RECENT SEARCHES", 
                                        fontSize = 10.5.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = BlossomColors.TextSecondary, 
                                        letterSpacing = 0.8.sp, 
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp) 
                                    ) 
                                } 
                                TextButton( 
                                    onClick = { 
                                        prefs.clearRecentJishoSearches() 
                                        recentSearches = emptyList() 
                                    }, 
                                    contentPadding = PaddingValues(0.dp) 
                                ) { 
                                    Surface( 
                                        shape = BlossomShapes.Pill, 
                                        color = BlossomColors.SurfaceElevated, 
                                        border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
                                    ) { 
                                        Text( 
                                            text = "Clear", 
                                            fontSize = 11.5.sp, 
                                            fontWeight = FontWeight.Medium, 
                                            color = BlossomColors.WarmOchre, 
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp) 
                                        ) 
                                    } 
                                } 
                            } 
                            
                            FlowRow( 
                                horizontalArrangement = Arrangement.spacedBy(8.dp), 
                                verticalArrangement = Arrangement.spacedBy(8.dp), 
                                modifier = Modifier.fillMaxWidth() 
                            ) { 
                                recentSearches.forEach { term -> 
                                    Surface( 
                                        shape = BlossomShapes.SquircleSmall, 
                                        color = BlossomColors.SurfaceCard1, 
                                        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                                        modifier = Modifier.clickable { 
                                            query = term 
                                            keyboardController?.hide() 
                                            executeSearch(term) 
                                        } 
                                    ) { 
                                        Row( 
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), 
                                            verticalAlignment = Alignment.CenterVertically 
                                        ) { 
                                            Icon( 
                                                imageVector = Icons.Default.History, 
                                                contentDescription = null, 
                                                tint = BlossomColors.TextSecondary, 
                                                modifier = Modifier.size(13.dp) 
                                            ) 
                                            Spacer(modifier = Modifier.width(6.dp)) 
                                            Text( 
                                                text = term, 
                                                fontSize = 12.5.sp, 
                                                color = BlossomColors.TextPrimary 
                                            ) 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 
                    
                    
                    item { 
                        Surface( 
                            shape = BlossomShapes.Pill, 
                            color = BlossomColors.SurfaceElevated, 
                            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle), 
                            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp) 
                        ) { 
                            Text( 
                                text = "SUGGESTED EXPLORATIONS", 
                                fontSize = 10.5.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = BlossomColors.TextSecondary, 
                                letterSpacing = 0.8.sp, 
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp) 
                            ) 
                        } 
                        
                        val suggested = listOf( 
                            "JLPT N5", "JLPT N4", "食べる", "猫", "ありがとう", 
                            "桜", "美しい", "勉強", "友だち", "taberu", "house" 
                        ) 
                        
                        FlowRow( 
                            horizontalArrangement = Arrangement.spacedBy(8.dp), 
                            verticalArrangement = Arrangement.spacedBy(8.dp), 
                            modifier = Modifier.fillMaxWidth() 
                        ) { 
                            suggested.forEach { term -> 
                                Surface( 
                                    shape = BlossomShapes.SquircleSmall, 
                                    color = BlossomColors.SurfaceCard1, 
                                    border = BorderStroke(1.dp, BlossomColors.MatchaSage.copy(alpha = 0.45f)), 
                                    modifier = Modifier.clickable { 
                                        query = term 
                                        keyboardController?.hide() 
                                        executeSearch(term) 
                                    } 
                                ) { 
                                    Text( 
                                        text = term, 
                                        fontSize = 12.5.sp, 
                                        fontWeight = FontWeight.SemiBold, 
                                        color = BlossomColors.MatchaSage, 
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp) 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                    
                    item { 
                        Surface( 
                            shape = BlossomShapes.SquircleMedium, 
                            color = BlossomColors.SurfaceCard1, 
                            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                            modifier = Modifier.fillMaxWidth() 
                        ) { 
                            Column(modifier = Modifier.padding(16.dp)) { 
                                Row(verticalAlignment = Alignment.CenterVertically) { 
                                    Icon( 
                                        imageVector = Icons.Default.Search, 
                                        contentDescription = null, 
                                        tint = BlossomColors.WarmOchre, 
                                        modifier = Modifier.size(16.dp) 
                                    ) 
                                    Spacer(modifier = Modifier.width(8.dp)) 
                                    Text( 
                                        text = "Universal Search", 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 14.sp, 
                                        color = BlossomColors.TextPrimary 
                                    ) 
                                } 
                                Spacer(modifier = Modifier.height(6.dp)) 
                                Text( 
                                    text = "Type English words ('eat'), Romaji ('taberu'), Kanji ('食べる'), or Kana ('たべる'). Search JLPT levels with '#jlpt-n5'.", 
                                    fontSize = 12.5.sp, 
                                    color = BlossomColors.TextSecondary, 
                                    lineHeight = 18.sp 
                                ) 
                            } 
                        } 
                    } 
                } 
            } else if (isLoading) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .padding(bottom = 60.dp), 
                    verticalArrangement = Arrangement.Center, 
                    horizontalAlignment = Alignment.CenterHorizontally 
                ) { 
                    CircularProgressIndicator( 
                        color = BlossomColors.WarmOchre, 
                        strokeWidth = 2.5.dp, 
                        modifier = Modifier.size(32.dp) 
                    ) 
                    Spacer(modifier = Modifier.height(12.dp)) 
                    Text( 
                        text = "Searching Jisho...", 
                        fontSize = 13.5.sp, 
                        color = BlossomColors.TextSecondary 
                    ) 
                } 
            } else if (errorMessage != null) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .padding(horizontal = 24.dp), 
                    verticalArrangement = Arrangement.Center, 
                    horizontalAlignment = Alignment.CenterHorizontally 
                ) { 
                    Text( 
                        text = errorMessage ?: "Failed to connect", 
                        fontSize = 13.5.sp, 
                        color = BlossomColors.MutedRose, 
                        modifier = Modifier.padding(bottom = 12.dp) 
                    ) 
                    Squircle3DButton( 
                        onClick = { executeSearch(query) }, 
                        containerColor = BlossomColors.WarmOchre, 
                        bevelColor = BlossomColors.WarmOchreLip 
                    ) { 
                        Row(verticalAlignment = Alignment.CenterVertically) { 
                            Icon( 
                                imageVector = Icons.Default.Refresh, 
                                contentDescription = null, 
                                modifier = Modifier.size(15.dp), 
                                tint = Color.White 
                            ) 
                            Spacer(modifier = Modifier.width(6.dp)) 
                            Text("Retry", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White) 
                        } 
                    } 
                } 
            } else if (results.isEmpty()) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .padding(horizontal = 24.dp, vertical = 40.dp), 
                    horizontalAlignment = Alignment.CenterHorizontally 
                ) { 
                    Text( 
                        text = "No results found for \"$query\"", 
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    Spacer(modifier = Modifier.height(6.dp)) 
                    Text( 
                        text = "Try checking the spelling or searching using Romaji or plain English.", 
                        fontSize = 12.5.sp, 
                        color = BlossomColors.TextSecondary, 
                        modifier = Modifier.padding(horizontal = 20.dp) 
                    ) 
                } 
            } else { 
                LazyColumn( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .padding(horizontal = 16.dp), 
                    contentPadding = PaddingValues(bottom = 100.dp), 
                    verticalArrangement = Arrangement.spacedBy(12.dp) 
                ) { 
                    item { 
                        Text( 
                            text = "${results.size} RESULTS FOR \"$query\"", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextMuted, 
                            letterSpacing = 0.8.sp, 
                            modifier = Modifier.padding(vertical = 2.dp) 
                        ) 
                    } 
                    
                    items(results, key = { it.slug + it.primaryReading }) { word -> 
                        val isExpanded = expandedSlugs.contains(word.slug) 
                        JishoWordCard( 
                            word = word, 
                            isExpanded = isExpanded, 
                            onToggleExpand = { 
                                if (isExpanded) { 
                                    expandedSlugs.remove(word.slug) 
                                } else { 
                                    expandedSlugs.add(word.slug) 
                                } 
                            }, 
                            onPronounce = { 
                                ttsHelper.speak(word.primaryWord.ifBlank { word.primaryReading }) 
                            }, 
                            onCopy = { 
                                val copyContent = if (word.primaryReading.isNotBlank() && word.primaryReading != word.primaryWord) { 
                                    "${word.primaryWord} [${word.primaryReading}]" 
                                } else { 
                                    word.primaryWord 
                                } 
                                clipboardManager.setText(AnnotatedString(copyContent)) 
                                Toast.makeText(context, "Copied: $copyContent", Toast.LENGTH_SHORT).show() 
                            } 
                        ) 
                    } 
                } 
            } 
        } 
    } 
} 
    
@OptIn(ExperimentalLayoutApi::class) 
@Composable
private fun JishoWordCard( 
    word: JishoWord, 
    isExpanded: Boolean, 
    onToggleExpand: () -> Unit, 
    onPronounce: () -> Unit, 
    onCopy: () -> Unit 
) { 
    Surface( 
        shape = BlossomShapes.SquircleLarge, 
        color = BlossomColors.SurfaceCard1, 
        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
        modifier = Modifier 
            .fillMaxWidth() 
            .animateContentSize() 
    ) { 
        Column(modifier = Modifier.padding(16.dp)) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.Top, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Column(modifier = Modifier.weight(1f)) { 
                    if (word.primaryReading.isNotBlank() && word.primaryReading != word.primaryWord) { 
                        Text( 
                            text = word.primaryReading, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = BlossomColors.MatchaSage 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                    } 
                    
                    Text( 
                        text = word.primaryWord, 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    
                    if (word.romaji.isNotBlank()) { 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            text = word.romaji, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = BlossomColors.TextMuted 
                        ) 
                    } 
                } 
                
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    IconButton( 
                        onClick = onPronounce, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp, 
                            contentDescription = "Speak", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(17.dp) 
                        ) 
                    } 
                    
                    IconButton( 
                        onClick = onCopy, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.ContentCopy, 
                            contentDescription = "Copy", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(16.dp) 
                        ) 
                    } 
                    
                    IconButton( 
                        onClick = onToggleExpand, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        Icon( 
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, 
                            contentDescription = if (isExpanded) "Collapse" else "Expand", 
                            tint = BlossomColors.WarmOchre, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                    } 
                } 
            } 
            
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(top = 8.dp, bottom = 8.dp), 
                horizontalArrangement = Arrangement.spacedBy(6.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                if (word.isCommon) { 
                    Surface( 
                        shape = RoundedCornerShape(6.dp), 
                        color = BlossomColors.MatchaSageContainer, 
                        border = BorderStroke(1.dp, BlossomColors.MatchaSage.copy(alpha = 0.4f)) 
                    ) { 
                        Text( 
                            text = "common", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.MatchaSage, 
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp) 
                        ) 
                    } 
                } 
                
                word.jlptBadge?.let { badge -> 
                    Surface( 
                        shape = RoundedCornerShape(6.dp), 
                        color = BlossomColors.WarmOchreContainer, 
                        border = BorderStroke(1.dp, BlossomColors.WarmOchre.copy(alpha = 0.4f)) 
                    ) { 
                        Text( 
                            text = badge, 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.WarmOchre, 
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp) 
                        ) 
                    } 
                } 
            } 
            
            val sensesToShow = if (isExpanded) word.senses else word.senses.take(2) 
            sensesToShow.forEachIndexed { index, sense -> 
                if (index > 0) { 
                    Spacer(modifier = Modifier.height(6.dp)) 
                } 
                
                Column { 
                    if (sense.partsOfSpeech.isNotEmpty()) { 
                        Text( 
                            text = sense.partsOfSpeech.joinToString(", "), 
                            fontSize = 10.5.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = BlossomColors.TextMuted 
                        ) 
                    } 
                    
                    Row(verticalAlignment = Alignment.Top) { 
                        Text( 
                            text = "${index + 1}. ", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.WarmOchre 
                        ) 
                        Text( 
                            text = sense.englishDefinitions.joinToString("; "), 
                            fontSize = 13.sp, 
                            color = BlossomColors.TextPrimary, 
                            lineHeight = 18.sp 
                        ) 
                    } 
                } 
            } 
            
            if (!isExpanded && word.senses.size > 2) { 
                Spacer(modifier = Modifier.height(4.dp)) 
                Text( 
                    text = "+ ${word.senses.size - 2} more definitions...", 
                    fontSize = 11.5.sp, 
                    fontWeight = FontWeight.Medium, 
                    color = BlossomColors.TextMuted, 
                    modifier = Modifier.clickable { onToggleExpand() } 
                ) 
            } 
        } 
    } 
} 
