package com.ankilock.ui.jisho
    
import android.widget.Toast
import androidx.compose.animation.animateContentSize 
import androidx.compose.foundation.background 
import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.horizontalScroll 
import androidx.compose.foundation.rememberScrollState 
import androidx.compose.foundation.gestures.detectTapGestures 
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ankilock.data.JishoServiceHelper
import com.ankilock.data.JishoWord
import com.ankilock.data.PreferencesManager
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomShapes
import com.ankilock.ui.components.Squircle3DButton
import com.ankilock.util.JapaneseTtsHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
    
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class) 
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
    var searchToDelete by remember { mutableStateOf<String?>(null) } 
    val expandedSlugs = remember { mutableStateListOf<String>() } 
    var playingSlug by remember { mutableStateOf<String?>(null) } 
    val recentListState = rememberLazyListState() 
    val resultsListState = rememberLazyListState() 
    
    LaunchedEffect(recentListState.isScrollInProgress, resultsListState.isScrollInProgress) { 
        if (recentListState.isScrollInProgress || resultsListState.isScrollInProgress) { 
            focusManager.clearFocus() 
            keyboardController?.hide() 
        } 
    } 
    
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
            .pointerInput(Unit) { 
                detectTapGestures(onTap = { 
                    focusManager.clearFocus() 
                    keyboardController?.hide() 
                }) 
            } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 20.dp, vertical = 12.dp) 
        ) { 
            OutlinedTextField( 
                value = query, 
                onValueChange = { query = it }, 
                modifier = Modifier.fillMaxWidth(), 
                placeholder = { 
                    Text( 
                        text = "Search Romaji, English, Kanji, Kana...", 
                        fontSize = 14.sp, 
                        color = BlossomColors.TextMuted 
                    ) 
                }, 
                leadingIcon = { 
                    Icon( 
                        imageVector = Icons.Default.Search, 
                        contentDescription = "Search", 
                        tint = BlossomColors.MatchaSage 
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
                                tint = BlossomColors.TextSecondary 
                            ) 
                        } 
                    } 
                }, 
                singleLine = true, 
                shape = RoundedCornerShape(24.dp), 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedBorderColor = BlossomColors.MatchaSage, 
                    unfocusedBorderColor = BlossomColors.CardBorder, 
                    cursorColor = BlossomColors.MatchaSage, 
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
                .pointerInput(Unit) { 
                    detectTapGestures(onTap = { 
                        focusManager.clearFocus() 
                        keyboardController?.hide() 
                    }) 
                } 
        ) { 
            if (query.isBlank()) { 
                LazyColumn( 
                    state = recentListState, 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .padding(horizontal = 20.dp), 
                    contentPadding = PaddingValues(bottom = 120.dp) 
                ) { 
                    if (recentSearches.isNotEmpty()) { 
                        item { 
                            Row( 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .padding(top = 8.dp, bottom = 12.dp), 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.SpaceBetween 
                            ) { 
                                Surface( 
                                    shape = RoundedCornerShape(8.dp), 
                                    color = BlossomColors.SurfaceElevated.copy(alpha = 0.5f), 
                                    border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
                                ) { 
                                    Text( 
                                        text = "RECENT SEARCHES", 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = BlossomColors.TextSecondary, 
                                        letterSpacing = 0.8.sp, 
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp) 
                                    ) 
                                } 
                                TextButton( 
                                    onClick = { 
                                        prefs.clearRecentJishoSearches() 
                                        recentSearches = emptyList() 
                                    }, 
                                    contentPadding = PaddingValues(0.dp) 
                                ) { 
                                    Text( 
                                        text = "Clear", 
                                        fontSize = 12.sp, 
                                        color = BlossomColors.MatchaSage 
                                    ) 
                                } 
                            } 
                            
                            FlowRow( 
                                horizontalArrangement = Arrangement.spacedBy(8.dp), 
                                verticalArrangement = Arrangement.spacedBy(8.dp), 
                                modifier = Modifier.fillMaxWidth() 
                            ) { 
                                recentSearches.forEach { term -> 
                                    Surface( 
                                        shape = RoundedCornerShape(16.dp), 
                                        color = BlossomColors.SurfaceElevated.copy(alpha = 0.5f), 
                                        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                                        modifier = Modifier 
                                            .clip(RoundedCornerShape(16.dp)) 
                                            .combinedClickable( 
                                                onClick = { 
                                                    query = term 
                                                    keyboardController?.hide() 
                                                    executeSearch(term) 
                                                }, 
                                                onLongClick = { 
                                                    searchToDelete = term 
                                                } 
                                            ) 
                                    ) { 
                                        Row( 
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), 
                                            verticalAlignment = Alignment.CenterVertically 
                                        ) { 
                                            Icon( 
                                                imageVector = Icons.Default.History, 
                                                contentDescription = null, 
                                                tint = BlossomColors.TextMuted, 
                                                modifier = Modifier.size(13.dp) 
                                            ) 
                                            Spacer(modifier = Modifier.width(6.dp)) 
                                            Text( 
                                                text = term, 
                                                fontSize = 13.sp, 
                                                color = BlossomColors.TextPrimary 
                                            ) 
                                        } 
                                    } 
                                } 
                            } 
                            
                            Spacer(modifier = Modifier.height(24.dp)) 
                        } 
                    } 
                    
                    item { 
                        Surface( 
                            shape = RoundedCornerShape(8.dp), 
                            color = BlossomColors.SurfaceElevated.copy(alpha = 0.5f), 
                            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle), 
                            modifier = Modifier.padding(bottom = 12.dp) 
                        ) { 
                            Text( 
                                text = "SUGGESTED EXPLORATIONS", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = BlossomColors.TextSecondary, 
                                letterSpacing = 0.8.sp, 
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp) 
                            ) 
                        } 
                        
                        val suggested = listOf( 
                            "JLPT N5", "JLPT N4", "JLPT N3", "JLPT N2", "JLPT N1", 
                            "食べる", "飲む", "見る", "聞く", "話す", 
                            "猫", "犬", "桜", "ありがとう", "美しい", 
                            "勉強", "友だち", "旅行", "家族", "時間", 
                            "仕事", "日常", "音楽", "夢", "幸せ", 
                            "自然", "心", "空", "雨", "海" 
                        ) 
                        val half = (suggested.size + 1) / 2 
                        val row1 = suggested.take(half) 
                        val row2 = suggested.drop(half) 
                        
                        Column( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .horizontalScroll(rememberScrollState()), 
                            verticalArrangement = Arrangement.spacedBy(8.dp) 
                        ) { 
                            Row( 
                                horizontalArrangement = Arrangement.spacedBy(8.dp) 
                            ) { 
                                row1.forEach { term -> 
                                    Surface( 
                                        shape = RoundedCornerShape(16.dp), 
                                        color = BlossomColors.MatchaSageContainer.copy(alpha = 0.5f), 
                                        border = BorderStroke(1.dp, BlossomColors.MatchaSage.copy(alpha = 0.35f)), 
                                        modifier = Modifier.clickable { 
                                            query = term 
                                            keyboardController?.hide() 
                                            executeSearch(term) 
                                        } 
                                    ) { 
                                        Text( 
                                            text = term, 
                                            fontSize = 13.sp, 
                                            fontWeight = FontWeight.Medium, 
                                            color = BlossomColors.MatchaSage, 
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp) 
                                        ) 
                                    } 
                                } 
                            } 
                            Row( 
                                horizontalArrangement = Arrangement.spacedBy(8.dp) 
                            ) { 
                                row2.forEach { term -> 
                                    Surface( 
                                        shape = RoundedCornerShape(16.dp), 
                                        color = BlossomColors.MatchaSageContainer.copy(alpha = 0.5f), 
                                        border = BorderStroke(1.dp, BlossomColors.MatchaSage.copy(alpha = 0.35f)), 
                                        modifier = Modifier.clickable { 
                                            query = term 
                                            keyboardController?.hide() 
                                            executeSearch(term) 
                                        } 
                                    ) { 
                                        Text( 
                                            text = term, 
                                            fontSize = 13.sp, 
                                            fontWeight = FontWeight.Medium, 
                                            color = BlossomColors.MatchaSage, 
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp) 
                                        ) 
                                    } 
                                } 
                            } 
                        } 
                        
                        Spacer(modifier = Modifier.height(28.dp)) 
                        
                        Surface( 
                            shape = RoundedCornerShape(16.dp), 
                            color = BlossomColors.SurfaceElevated, 
                            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                            modifier = Modifier.fillMaxWidth() 
                        ) { 
                            Column(modifier = Modifier.padding(18.dp)) { 
                                Row(verticalAlignment = Alignment.CenterVertically) { 
                                    Icon( 
                                        imageVector = Icons.Default.Search, 
                                        contentDescription = null, 
                                        tint = BlossomColors.MatchaSage, 
                                        modifier = Modifier.size(18.dp) 
                                    ) 
                                    Spacer(modifier = Modifier.width(8.dp)) 
                                    Text( 
                                        text = "Universal Search", 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 15.sp, 
                                        color = BlossomColors.TextPrimary 
                                    ) 
                                } 
                                Spacer(modifier = Modifier.height(6.dp)) 
                                Text( 
                                    text = "Type English words ('eat'), Romaji ('taberu'), Kanji ('食べる'), or Kana ('たべる'). You can also search by level, e.g. '#jlpt-n5'.", 
                                    fontSize = 13.sp, 
                                    color = BlossomColors.TextSecondary, 
                                    lineHeight = 20.sp 
                                ) 
                            } 
                        } 
                    } 
                } 
            } else if (isLoading) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .padding(bottom = 80.dp), 
                    verticalArrangement = Arrangement.Center, 
                    horizontalAlignment = Alignment.CenterHorizontally 
                ) { 
                    CircularProgressIndicator( 
                        color = BlossomColors.MatchaSage, 
                        strokeWidth = 3.dp, 
                        modifier = Modifier.size(36.dp) 
                    ) 
                    Spacer(modifier = Modifier.height(14.dp)) 
                    Text( 
                        text = "Searching Jisho...", 
                        fontSize = 14.sp, 
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
                        fontSize = 14.sp, 
                        color = BlossomColors.BlossomRed, 
                        modifier = Modifier.padding(bottom = 12.dp) 
                    ) 
                    OutlinedButton( 
                        onClick = { executeSearch(query) }, 
                        colors = ButtonDefaults.outlinedButtonColors( 
                            contentColor = BlossomColors.MatchaSage 
                        ) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Refresh, 
                            contentDescription = null, 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(6.dp)) 
                        Text("Retry", fontWeight = FontWeight.Bold) 
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
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    Spacer(modifier = Modifier.height(8.dp)) 
                    Text( 
                        text = "Try checking the spelling or searching using Romaji or plain English.", 
                        fontSize = 13.sp, 
                        color = BlossomColors.TextSecondary, 
                        modifier = Modifier.padding(horizontal = 20.dp) 
                    ) 
                } 
            } else { 
                LazyColumn( 
                    state = resultsListState, 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .padding(horizontal = 20.dp), 
                    contentPadding = PaddingValues(bottom = 120.dp), 
                    verticalArrangement = Arrangement.spacedBy(14.dp) 
                ) { 
                    item { 
                        Text( 
                            text = "${results.size} RESULTS FOR \"$query\"", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextMuted, 
                            letterSpacing = 0.8.sp, 
                            modifier = Modifier 
                                .background( 
                                    color = BlossomColors.SurfaceElevated.copy(alpha = 0.5f), 
                                    shape = RoundedCornerShape(8.dp) 
                                ) 
                                .padding(horizontal = 10.dp, vertical = 4.dp) 
                        ) 
                    } 
                    
                    items(results, key = { it.slug + it.primaryReading }) { word -> 
                        val itemKey = word.slug + word.primaryReading 
                        val isExpanded = expandedSlugs.contains(word.slug) 
                        val isPlaying = playingSlug == itemKey 
                        JishoWordCard( 
                            word = word, 
                            isExpanded = isExpanded, 
                            isPlaying = isPlaying, 
                            onToggleExpand = { 
                                if (isExpanded) { 
                                    expandedSlugs.remove(word.slug) 
                                } else { 
                                    expandedSlugs.add(word.slug) 
                                } 
                            }, 
                            onPronounce = { 
                                playingSlug = itemKey 
                                ttsHelper.speak( 
                                    text = word.primaryWord.ifBlank { word.primaryReading }, 
                                    onStart = { playingSlug = itemKey }, 
                                    onDone = { if (playingSlug == itemKey) playingSlug = null }, 
                                    onError = { if (playingSlug == itemKey) playingSlug = null } 
                                ) 
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
    
    if (searchToDelete != null) { 
        val targetTerm = searchToDelete ?: "" 
        Dialog(onDismissRequest = { searchToDelete = null }) { 
            Card( 
                shape = RoundedCornerShape(20.dp), 
                colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
                border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                Column( 
                    modifier = Modifier.padding(22.dp), 
                    verticalArrangement = Arrangement.spacedBy(14.dp) 
                ) { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        Icon( 
                            Icons.Filled.DeleteOutline, 
                            contentDescription = null, 
                            tint = BlossomColors.BlossomRed, 
                            modifier = Modifier.size(24.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(10.dp)) 
                        Text( 
                            text = "Delete Search History?", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 18.sp, 
                            color = BlossomColors.TextPrimary 
                        ) 
                    } 
                    
                    Text( 
                        text = "Are you sure you want to delete \"$targetTerm\" from your recent searches? This action cannot be undone.", 
                        fontSize = 13.sp, 
                        color = BlossomColors.TextSecondary, 
                        lineHeight = 20.sp 
                    ) 
                    
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        OutlinedButton( 
                            onClick = { searchToDelete = null }, 
                            shape = RoundedCornerShape(12.dp), 
                            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                            modifier = Modifier.weight(1f), 
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp) 
                        ) { 
                            Text("Cancel", color = BlossomColors.TextSecondary, maxLines = 1, softWrap = false) 
                        } 
                        
                        Button( 
                            onClick = { 
                                prefs.removeRecentJishoSearch(targetTerm) 
                                recentSearches = prefs.recentJishoSearches 
                                searchToDelete = null 
                            }, 
                            shape = RoundedCornerShape(12.dp), 
                            colors = ButtonDefaults.buttonColors( 
                                containerColor = BlossomColors.BlossomRed, 
                                contentColor = BlossomColors.BlossomWhite 
                            ), 
                            modifier = Modifier.weight(1f), 
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp) 
                        ) { 
                            Text("Delete", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false) 
                        } 
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
    isPlaying: Boolean = false, 
    onToggleExpand: () -> Unit, 
    onPronounce: () -> Unit, 
    onCopy: () -> Unit 
) { 
    Surface( 
        shape = RoundedCornerShape(18.dp), 
        color = BlossomColors.SurfaceElevated, 
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
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = BlossomColors.MatchaSage 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                    } 
                    
                    Text( 
                        text = word.primaryWord, 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    
                    if (word.romaji.isNotBlank()) { 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            text = word.romaji, 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = BlossomColors.TextMuted 
                        ) 
                    } 
                } 
                
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    IconButton( 
                        onClick = onPronounce, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp, 
                            contentDescription = "Speak", 
                            tint = if (isPlaying) BlossomColors.MatchaSage else BlossomColors.TextSecondary.copy(alpha = 0.45f), 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                    
                    IconButton( 
                        onClick = onCopy, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.ContentCopy, 
                            contentDescription = "Copy", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(17.dp) 
                        ) 
                    } 
                    
                    IconButton( 
                        onClick = onToggleExpand, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, 
                            contentDescription = if (isExpanded) "Collapse" else "Expand", 
                            tint = BlossomColors.MatchaSage, 
                            modifier = Modifier.size(22.dp) 
                        ) 
                    } 
                } 
            } 
            
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(top = 8.dp, bottom = 10.dp), 
                horizontalArrangement = Arrangement.spacedBy(6.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                if (word.isCommon) { 
                    Surface( 
                        shape = RoundedCornerShape(6.dp), 
                        color = Color(0xFF43A047).copy(alpha = 0.15f), 
                        border = BorderStroke(1.dp, Color(0xFF43A047).copy(alpha = 0.4f)) 
                    ) { 
                        Text( 
                            text = "common", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF43A047), 
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp) 
                        ) 
                    } 
                } 
                
                word.jlptBadge?.let { badge -> 
                    Surface( 
                        shape = RoundedCornerShape(6.dp), 
                        color = BlossomColors.MatchaSageContainer, 
                        border = BorderStroke(1.dp, BlossomColors.MatchaSage.copy(alpha = 0.4f)) 
                    ) { 
                        Text( 
                            text = badge, 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.MatchaSage, 
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp) 
                        ) 
                    } 
                } 
                
                word.tags.filter { it.startsWith("wanikani", ignoreCase = true) }.take(1).forEach { wkTag -> 
                    Surface( 
                        shape = RoundedCornerShape(6.dp), 
                        color = Color(0xFFF24E1E).copy(alpha = 0.12f), 
                        border = BorderStroke(1.dp, Color(0xFFF24E1E).copy(alpha = 0.35f)) 
                    ) { 
                        Text( 
                            text = wkTag.replace("wanikani", "WK ", ignoreCase = true), 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFFF24E1E), 
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp) 
                        ) 
                    } 
                } 
            } 
            
            HorizontalDivider(color = BlossomColors.CardBorderSubtle, thickness = 0.8.dp) 
            
            Spacer(modifier = Modifier.height(10.dp)) 
            
            val sensesToShow = if (isExpanded) word.senses else word.senses.take(1) 
            
            sensesToShow.forEachIndexed { sIdx, sense -> 
                if (sIdx > 0) { 
                    Spacer(modifier = Modifier.height(10.dp)) 
                } 
                
                Column { 
                    if (sense.partsOfSpeech.isNotEmpty()) { 
                        Text( 
                            text = sense.partsOfSpeech.joinToString(", "), 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = BlossomColors.MatchaSage, 
                            modifier = Modifier.padding(bottom = 3.dp) 
                        ) 
                    } 
                    
                    val defsText = sense.englishDefinitions.mapIndexed { dIdx, def -> 
                        if (sense.englishDefinitions.size > 1) "${dIdx + 1}. $def" else def 
                    }.joinToString("; ") 
                    
                    Text( 
                        text = defsText, 
                        fontSize = 14.sp, 
                        color = BlossomColors.TextPrimary, 
                        lineHeight = 20.sp 
                    ) 
                    
                    if (isExpanded) { 
                        val tagsAndInfo = (sense.tags + sense.info).distinct() 
                        if (tagsAndInfo.isNotEmpty()) { 
                            Spacer(modifier = Modifier.height(4.dp)) 
                            Text( 
                                text = tagsAndInfo.joinToString(" • "), 
                                fontSize = 11.sp, 
                                color = BlossomColors.TextMuted, 
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic 
                            ) 
                        } 
                        
                        if (sense.seeAlso.isNotEmpty()) { 
                            Spacer(modifier = Modifier.height(2.dp)) 
                            Text( 
                                text = "See also: " + sense.seeAlso.joinToString(", "), 
                                fontSize = 11.sp, 
                                color = BlossomColors.TextMuted 
                            ) 
                        } 
                    } 
                } 
            } 
            
            if (isExpanded && word.otherForms.isNotEmpty()) { 
                Spacer(modifier = Modifier.height(12.dp)) 
                HorizontalDivider(color = BlossomColors.CardBorderSubtle, thickness = 0.8.dp) 
                Spacer(modifier = Modifier.height(8.dp)) 
                Text( 
                    text = "OTHER WRITINGS: " + word.otherForms.joinToString(", "), 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Medium, 
                    color = BlossomColors.TextMuted 
                ) 
            } 
        } 
    } 
} 
