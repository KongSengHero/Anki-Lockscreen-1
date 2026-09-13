package com.ankilock.ui.reading 

import android.graphics.BitmapFactory 
import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.Image 
import androidx.compose.foundation.clickable 
import androidx.compose.foundation.horizontalScroll 
import androidx.compose.foundation.layout.Arrangement 
import androidx.compose.foundation.layout.Box 
import androidx.compose.foundation.layout.Column 
import androidx.compose.foundation.layout.PaddingValues 
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
import androidx.compose.foundation.text.KeyboardActions 
import androidx.compose.foundation.text.KeyboardOptions 
import androidx.compose.foundation.verticalScroll 
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.automirrored.filled.VolumeUp 
import androidx.compose.material.icons.filled.Check 
import androidx.compose.material.icons.filled.Close 
import androidx.compose.material.icons.filled.Favorite 
import androidx.compose.material.icons.filled.FavoriteBorder 
import androidx.compose.material.icons.filled.GraphicEq 
import androidx.compose.material.icons.filled.Headphones 
import androidx.compose.material.icons.filled.Search 
import androidx.compose.material.icons.filled.Stop 
import androidx.compose.material3.BottomSheetDefaults 
import androidx.compose.material3.Button 
import androidx.compose.material3.ButtonDefaults 
import androidx.compose.material3.CircularProgressIndicator 
import androidx.compose.material3.ExperimentalMaterial3Api 
import androidx.compose.material3.Icon 
import androidx.compose.material3.IconButton 
import androidx.compose.material3.ModalBottomSheet 
import androidx.compose.material3.OutlinedTextField 
import androidx.compose.material3.OutlinedTextFieldDefaults 
import androidx.compose.material3.Surface 
import androidx.compose.material3.Text 
import androidx.compose.material3.rememberModalBottomSheetState 
import androidx.compose.runtime.Composable 
import androidx.compose.runtime.DisposableEffect 
import androidx.compose.runtime.getValue 
import androidx.compose.runtime.mutableStateOf 
import androidx.compose.runtime.produceState 
import androidx.compose.runtime.remember 
import androidx.compose.runtime.rememberCoroutineScope 
import androidx.compose.runtime.setValue 
import androidx.compose.ui.Alignment 
import androidx.compose.ui.Modifier 
import androidx.compose.ui.draw.clip 
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.geometry.Offset 
import androidx.compose.ui.graphics.ImageBitmap 
import androidx.compose.ui.graphics.asImageBitmap 
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection 
import androidx.compose.ui.input.nestedscroll.NestedScrollSource 
import androidx.compose.ui.input.nestedscroll.nestedScroll 
import androidx.compose.ui.layout.ContentScale 
import androidx.compose.ui.platform.LocalContext 
import androidx.compose.ui.platform.LocalSoftwareKeyboardController 
import androidx.compose.ui.text.font.FontWeight 
import androidx.compose.ui.text.input.ImeAction 
import androidx.compose.ui.text.style.TextOverflow 
import androidx.compose.ui.unit.Velocity 
import androidx.compose.ui.unit.dp 
import androidx.compose.ui.unit.sp 
import com.ankilock.data.FishAudioVoiceOption 
import com.ankilock.data.PreferencesManager 
import com.ankilock.reading.FishAudioService 
import com.ankilock.ui.blossom.BlossomColors 
import kotlinx.coroutines.Dispatchers 
import kotlinx.coroutines.launch 
import kotlinx.coroutines.withContext 
import java.net.HttpURLConnection 
import java.net.URL 

@OptIn(ExperimentalMaterial3Api::class) 
@Composable 
fun FishAudioVoiceDialog( 
    currentVoiceId: String, 
    currentVoiceName: String, 
    apiKey: String, 
    onSelectVoice: (id: String, name: String) -> Unit, 
    onDismiss: () -> Unit 
) { 
    val context = LocalContext.current 
    val coroutineScope = rememberCoroutineScope() 
    val keyboardController = LocalSoftwareKeyboardController.current 
    val prefs = remember { PreferencesManager(context) } 
    val audioService = remember { FishAudioService(context) } 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    
    val presets = PreferencesManager.PRESET_FISH_AUDIO_VOICES 
    val cleanCurrentId = PreferencesManager.extractVoiceId(currentVoiceId) 
    
    var favoriteIds by remember { mutableStateOf(prefs.favoriteFishAudioVoiceIds) } 
    var customVoices by remember { mutableStateOf(prefs.getSavedCustomVoices()) } 
    val allVoices = remember(customVoices, favoriteIds) { 
        val cleanCustom = customVoices.map { it.copy(id = PreferencesManager.extractVoiceId(it.id).lowercase().trim()) } 
        val cleanPresets = presets.map { it.copy(id = PreferencesManager.extractVoiceId(it.id).lowercase().trim()) } 
        (cleanPresets + cleanCustom).distinctBy { it.id } 
    } 
    val filterChips = listOf("All", "Favorites", "Female", "Male", "Neutral", "Young", "Middle-aged", "Anime", "Narrator") 
    var selectedFilter by remember { mutableStateOf("All") } 
    
    val searchCache = remember { mutableMapOf<String, List<FishAudioVoiceOption>>() } 
    var searchQuery by remember { mutableStateOf("") } 
    var isSearching by remember { mutableStateOf(false) } 
    var searchResults by remember { mutableStateOf<List<FishAudioVoiceOption>>(emptyList()) } 
    var searchError by remember { mutableStateOf<String?>(null) } 
    var hasSearched by remember { mutableStateOf(false) } 
    
    var customInput by remember { mutableStateOf("") } 
    var showCustomSection by remember { mutableStateOf(false) } 
    
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
    
    var previewVoiceId by remember { mutableStateOf<String?>(null) } 
    var isGeneratingPreview by remember { mutableStateOf(false) } 
    var isPlayingAudio by remember { mutableStateOf(false) } 
    var previewError by remember { mutableStateOf<String?>(null) } 
    
    DisposableEffect(Unit) { 
        onDispose { 
            audioService.stopAudio() 
        } 
    } 
    
    fun handleSelect(id: String, name: String) { 
        audioService.stopAudio() 
        coroutineScope.launch { 
            sheetState.hide() 
            onSelectVoice(id, name) 
            onDismiss() 
        } 
    } 
    
    fun handleToggleFavorite(voice: FishAudioVoiceOption) { 
        prefs.toggleFavoriteVoice(voice) 
        favoriteIds = prefs.favoriteFishAudioVoiceIds 
        customVoices = prefs.getSavedCustomVoices() 
    } 
    
    fun handlePreview(voiceId: String) { 
        if (apiKey.isBlank()) { 
            previewError = "Enter Fish Audio key in AI settings to test audio" 
            return 
        } 
        if (previewVoiceId == voiceId && isPlayingAudio) { 
            audioService.stopAudio() 
            isPlayingAudio = false 
            previewVoiceId = null 
            return 
        } 
        
        previewVoiceId = voiceId 
        isGeneratingPreview = true 
        previewError = null 
        
        coroutineScope.launch { 
            val synthResult = audioService.synthesizePreviewSample(apiKey = apiKey, voiceId = voiceId) 
            isGeneratingPreview = false 
            synthResult.onSuccess { audioFile -> 
                isPlayingAudio = true 
                audioService.playAudio( 
                    file = audioFile, 
                    onPlaybackStateChanged = { playing -> 
                        isPlayingAudio = playing 
                    }, 
                    onCompletion = { 
                        isPlayingAudio = false 
                        previewVoiceId = null 
                    } 
                ) 
            }.onFailure { err -> 
                previewError = err.message ?: "Failed to generate preview sample" 
                previewVoiceId = null 
                isPlayingAudio = false 
            } 
        } 
    } 
    
    fun performSearch(tagFilter: String? = if (selectedFilter in listOf("All", "Favorites")) null else selectedFilter) { 
        val q = searchQuery.trim() 
        keyboardController?.hide() 
        isSearching = true 
        searchError = null 
        hasSearched = true 
        
        val cacheKey = "$q|${tagFilter ?: ""}" 
        val cached = searchCache[cacheKey] 
        if (cached != null) { 
            searchResults = cached 
            isSearching = false 
            if (cached.isEmpty()) { 
                searchError = if (q.isNotBlank()) "No public voices found matching \"$q\"" else "No public voices found" 
            } 
            return 
        } 
        
        coroutineScope.launch { 
            val res = audioService.searchVoices( 
                apiKey = apiKey, 
                query = q, 
                tag = tagFilter, 
                sortBy = "score", 
                pageSize = 20 
            ) 
            isSearching = false 
            res.onSuccess { list -> 
                searchCache[cacheKey] = list 
                searchResults = list 
                if (list.isEmpty()) { 
                    searchError = if (q.isNotBlank()) "No public voices found matching \"$q\"" else "No public voices found" 
                } 
            }.onFailure { err -> 
                searchError = err.message ?: "Search request failed" 
            } 
        } 
    } 
    
    val displayedPresets = allVoices.filter { voice -> 
        val cleanId = PreferencesManager.extractVoiceId(voice.id).lowercase().trim() 
        when (selectedFilter) { 
            "All" -> true 
            "Favorites" -> favoriteIds.contains(cleanId) 
            else -> voice.tags.any { it.contains(selectedFilter, ignoreCase = true) } || voice.tag.contains(selectedFilter, ignoreCase = true) 
        } 
    } 
    
    val displayedSearchResults = if (selectedFilter == "Favorites") { 
        searchResults.filter { 
            val cleanId = PreferencesManager.extractVoiceId(it.id).lowercase().trim() 
            favoriteIds.contains(cleanId) 
        } 
    } else { 
        searchResults 
    } 
    
    ModalBottomSheet( 
        onDismissRequest = { 
            audioService.stopAudio() 
            onDismiss() 
        }, 
        sheetState = sheetState, 
        containerColor = BlossomColors.SurfaceCard1, 
        contentColor = BlossomColors.TextPrimary, 
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), 
        dragHandle = { BottomSheetDefaults.DragHandle(color = BlossomColors.CardBorder) } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 20.dp) 
        ) { 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(bottom = 12.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.weight(1f) 
                ) { 
                    Surface( 
                        shape = RoundedCornerShape(10.dp), 
                        color = BlossomColors.SakuraRose.copy(alpha = 0.15f), 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            Icons.Filled.GraphicEq, 
                            contentDescription = null, 
                            tint = BlossomColors.SakuraRose, 
                            modifier = Modifier 
                                .padding(7.dp) 
                                .size(20.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(10.dp)) 
                    Column { 
                        Text( 
                            text = "Voice Model", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        Text( 
                            text = if (currentVoiceName.isNotBlank()) "Current: $currentVoiceName" else "Choose a voice for Japanese story narration", 
                            fontSize = 12.sp, 
                            color = BlossomColors.TextSecondary, 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis 
                        ) 
                    } 
                } 
                IconButton( 
                    onClick = { 
                        audioService.stopAudio() 
                        coroutineScope.launch { 
                            sheetState.hide() 
                            onDismiss() 
                        } 
                    }, 
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
            
            if (previewError != null) { 
                Surface( 
                    shape = RoundedCornerShape(10.dp), 
                    color = BlossomColors.BtnAgainBg, 
                    border = BorderStroke(1.dp, BlossomColors.BtnAgainBorder), 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(bottom = 10.dp) 
                ) { 
                    Text( 
                        text = previewError!!, 
                        fontSize = 12.sp, 
                        color = BlossomColors.BtnAgainText, 
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp) 
                    ) 
                } 
            } 
            
            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .nestedScroll(noBounceNestedScroll) 
                    .verticalScroll(rememberScrollState()), 
                verticalArrangement = Arrangement.spacedBy(14.dp) 
            ) { 
                OutlinedTextField( 
                    value = searchQuery, 
                    onValueChange = { searchQuery = it }, 
                    placeholder = { Text("Search public voices (e.g. Japanese, anime, narrator)...", fontSize = 13.sp, color = BlossomColors.TextMuted) }, 
                    singleLine = true, 
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), 
                    keyboardActions = KeyboardActions(onSearch = { performSearch() }), 
                    trailingIcon = { 
                        if (isSearching) { 
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BlossomColors.SakuraRose, strokeWidth = 2.dp) 
                        } else if (searchQuery.isNotBlank()) { 
                            IconButton(onClick = { performSearch() }) { 
                                Icon(Icons.Filled.Search, contentDescription = "Search", tint = BlossomColors.SakuraRose, modifier = Modifier.size(20.dp)) 
                            } 
                        } 
                    }, 
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
                
                Row( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .horizontalScroll(rememberScrollState()), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    filterChips.forEach { chip -> 
                        val isChipSelected = selectedFilter == chip 
                        Surface( 
                            onClick = { 
                                selectedFilter = chip 
                                if (hasSearched || searchQuery.isNotBlank()) { 
                                    performSearch(tagFilter = if (chip in listOf("All", "Favorites")) null else chip) 
                                } 
                            }, 
                            shape = RoundedCornerShape(20.dp), 
                            color = if (isChipSelected) BlossomColors.SakuraRose else BlossomColors.SurfaceElevated, 
                            border = BorderStroke( 
                                1.dp, 
                                if (isChipSelected) BlossomColors.SakuraRose else BlossomColors.CardBorderSubtle 
                            ) 
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp) 
                            ) { 
                                if (chip == "Favorites") { 
                                    Icon( 
                                        Icons.Filled.Favorite, 
                                        contentDescription = null, 
                                        tint = if (isChipSelected) Color.White else BlossomColors.SakuraRose, 
                                        modifier = Modifier.size(12.dp) 
                                    ) 
                                    Spacer(modifier = Modifier.width(4.dp)) 
                                } 
                                Text( 
                                    text = chip, 
                                    fontSize = 12.sp, 
                                    fontWeight = if (isChipSelected) FontWeight.Bold else FontWeight.Medium, 
                                    color = if (isChipSelected) Color.White else BlossomColors.TextSecondary, 
                                    maxLines = 1, 
                                    softWrap = false 
                                ) 
                            } 
                        } 
                    } 
                } 
                
                if (hasSearched) { 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        Text( 
                            text = "Search Results (${displayedSearchResults.size})", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = BlossomColors.SakuraRose 
                        ) 
                        Text( 
                            text = "Clear", 
                            fontSize = 12.sp, 
                            color = BlossomColors.TextSecondary, 
                            modifier = Modifier.clickable { 
                                searchResults = emptyList() 
                                hasSearched = false 
                                searchQuery = "" 
                                searchError = null 
                                customVoices = prefs.getSavedCustomVoices() 
                                favoriteIds = prefs.favoriteFishAudioVoiceIds 
                            } 
                        ) 
                    } 
                    
                    if (searchError != null) { 
                        Text( 
                            text = searchError!!, 
                            fontSize = 12.5.sp, 
                            color = BlossomColors.TextSecondary, 
                            modifier = Modifier.padding(vertical = 4.dp) 
                        ) 
                    } 
                    
                    displayedSearchResults.forEach { voice -> 
                        val cleanVoiceId = PreferencesManager.extractVoiceId(voice.id).lowercase().trim() 
                        val isSelected = cleanCurrentId.lowercase().trim() == cleanVoiceId 
                        val isFav = favoriteIds.contains(cleanVoiceId) 
                        VoiceOptionCard( 
                            voice = voice, 
                            isSelected = isSelected, 
                            isFavorite = isFav, 
                            isGenerating = (isGeneratingPreview && previewVoiceId == voice.id), 
                            isPlaying = (isPlayingAudio && previewVoiceId == voice.id), 
                            onSelect = { handleSelect(voice.id, voice.name) }, 
                            onToggleFavorite = { handleToggleFavorite(voice) }, 
                            onPreview = { handlePreview(voice.id) } 
                        ) 
                    } 
                    
                    Spacer(modifier = Modifier.height(6.dp)) 
                } 
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Text( 
                        text = if (selectedFilter == "Favorites") "Favorite Voices (${displayedPresets.size})" else "Recommended Japanese Presets (${displayedPresets.size})", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                } 
                
                if (displayedPresets.isEmpty() && selectedFilter == "Favorites" && !hasSearched) { 
                    Text( 
                        text = "No favorite voices saved yet. Tap the heart icon on any voice to save it here!", 
                        fontSize = 12.sp, 
                        color = BlossomColors.TextMuted, 
                        modifier = Modifier.padding(vertical = 8.dp) 
                    ) 
                } 
                
                displayedPresets.forEach { preset -> 
                    val cleanPresetId = PreferencesManager.extractVoiceId(preset.id).lowercase().trim() 
                    val isSelected = cleanCurrentId.lowercase().trim() == cleanPresetId 
                    val isFav = favoriteIds.contains(cleanPresetId) 
                    VoiceOptionCard( 
                        voice = preset, 
                        isSelected = isSelected, 
                        isFavorite = isFav, 
                        isGenerating = (isGeneratingPreview && previewVoiceId == preset.id), 
                        isPlaying = (isPlayingAudio && previewVoiceId == preset.id), 
                        onSelect = { handleSelect(preset.id, preset.name) }, 
                        onToggleFavorite = { handleToggleFavorite(preset) }, 
                        onPreview = { handlePreview(preset.id) } 
                    ) 
                } 
                
                Surface( 
                    onClick = { showCustomSection = !showCustomSection }, 
                    shape = RoundedCornerShape(12.dp), 
                    color = BlossomColors.SurfaceElevated, 
                    border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Column(modifier = Modifier.padding(14.dp)) { 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.SpaceBetween 
                        ) { 
                            Text( 
                                text = "Custom Voice ID / Link", 
                                fontSize = 13.5.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = BlossomColors.TextPrimary 
                            ) 
                            Text( 
                                text = if (showCustomSection) "Hide" else "Expand", 
                                fontSize = 12.sp, 
                                color = BlossomColors.SakuraRose 
                            ) 
                        } 
                        
                        if (showCustomSection) { 
                            Spacer(modifier = Modifier.height(10.dp)) 
                            OutlinedTextField( 
                                value = customInput, 
                                onValueChange = { customInput = it }, 
                                placeholder = { Text("Paste Fish Audio model ID or URL", fontSize = 12.sp, color = BlossomColors.TextMuted) }, 
                                singleLine = true, 
                                colors = OutlinedTextFieldDefaults.colors( 
                                    focusedContainerColor = BlossomColors.SurfaceCard1, 
                                    unfocusedContainerColor = BlossomColors.SurfaceCard1, 
                                    focusedTextColor = BlossomColors.TextPrimary, 
                                    unfocusedTextColor = BlossomColors.TextPrimary, 
                                    focusedBorderColor = BlossomColors.SakuraRose, 
                                    unfocusedBorderColor = BlossomColors.CardBorder 
                                ), 
                                shape = RoundedCornerShape(10.dp), 
                                modifier = Modifier.fillMaxWidth() 
                            ) 
                            Spacer(modifier = Modifier.height(8.dp)) 
                            Row( 
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.End 
                            ) { 
                                Button( 
                                    onClick = { 
                                        val clean = PreferencesManager.extractVoiceId(customInput) 
                                        if (clean.isNotBlank()) { 
                                            handleSelect(clean, "Custom Voice") 
                                        } 
                                    }, 
                                    enabled = customInput.isNotBlank(), 
                                    shape = RoundedCornerShape(10.dp), 
                                    colors = ButtonDefaults.buttonColors(containerColor = BlossomColors.SakuraRose), 
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp) 
                                ) { 
                                    Text("Apply Voice", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White) 
                                } 
                            } 
                        } 
                    } 
                } 
                
                Spacer(modifier = Modifier.height(24.dp)) 
            } 
        } 
    } 
} 

@Composable 
private fun AsyncVoiceAvatar( 
    avatarUrl: String, 
    name: String, 
    modifier: Modifier = Modifier 
) { 
    val imageState = produceState<ImageBitmap?>(initialValue = null, key1 = avatarUrl) { 
        if (avatarUrl.isBlank()) { 
            value = null 
            return@produceState 
        } 
        value = withContext(Dispatchers.IO) { 
            try { 
                val conn = URL(avatarUrl).openConnection() as HttpURLConnection 
                conn.setRequestProperty("User-Agent", "AnkiLock-Blossom/1.0") 
                conn.connectTimeout = 6000 
                conn.readTimeout = 10000 
                conn.instanceFollowRedirects = true 
                conn.inputStream.use { stream -> 
                    BitmapFactory.decodeStream(stream)?.asImageBitmap() 
                } 
            } catch (_: Exception) { 
                null 
            } 
        } 
    } 
    
    val bmp = imageState.value 
    if (bmp != null) { 
        Image( 
            bitmap = bmp, 
            contentDescription = name, 
            contentScale = ContentScale.Crop, 
            modifier = modifier.clip(RoundedCornerShape(12.dp)) 
        ) 
    } else { 
        Surface( 
            shape = RoundedCornerShape(12.dp), 
            color = BlossomColors.SakuraRose.copy(alpha = 0.15f), 
            border = BorderStroke(1.dp, BlossomColors.SakuraRose.copy(alpha = 0.30f)), 
            modifier = modifier 
        ) { 
            Box(contentAlignment = Alignment.Center) { 
                Text( 
                    text = name.take(1).uppercase(), 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = BlossomColors.SakuraRose 
                ) 
            } 
        } 
    } 
} 

private fun formatMetricCount(count: Int): String { 
    if (count <= 0) return "0" 
    if (count < 1000) return count.toString() 
    if (count < 1000000) { 
        val k = count / 1000.0 
        return String.format(java.util.Locale.US, "%.1fk", k) 
    } 
    val m = count / 1000000.0 
    return String.format(java.util.Locale.US, "%.1fM", m) 
} 

@Composable 
private fun VoiceOptionCard( 
    voice: FishAudioVoiceOption, 
    isSelected: Boolean, 
    isFavorite: Boolean, 
    isGenerating: Boolean, 
    isPlaying: Boolean, 
    onSelect: () -> Unit, 
    onToggleFavorite: () -> Unit, 
    onPreview: () -> Unit 
) { 
    Surface( 
        onClick = onSelect, 
        shape = RoundedCornerShape(16.dp), 
        color = if (isSelected) BlossomColors.SakuraRoseContainer.copy(alpha = 0.45f) else BlossomColors.SurfaceElevated, 
        border = BorderStroke( 
            1.dp, 
            if (isSelected) BlossomColors.SakuraRose.copy(alpha = 0.60f) else BlossomColors.CardBorderSubtle 
        ), 
        modifier = Modifier.fillMaxWidth() 
    ) { 
        Row( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(12.dp), 
            verticalAlignment = Alignment.CenterVertically 
        ) { 
            AsyncVoiceAvatar( 
                avatarUrl = voice.avatarUrl, 
                name = voice.name, 
                modifier = Modifier.size(54.dp) 
            ) 
            
            Spacer(modifier = Modifier.width(12.dp)) 
            
            Column(modifier = Modifier.weight(1f)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Text( 
                        text = voice.name, 
                        fontSize = 14.5.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextPrimary, 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis, 
                        modifier = Modifier.weight(1f, fill = false) 
                    ) 
                    if (voice.author.isNotBlank()) { 
                        Spacer(modifier = Modifier.width(6.dp)) 
                        Text( 
                            text = "by ${voice.author}", 
                            fontSize = 11.sp, 
                            color = BlossomColors.TextMuted, 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis 
                        ) 
                    } 
                } 
                
                if (voice.description.isNotBlank()) { 
                    Spacer(modifier = Modifier.height(3.dp)) 
                    Text( 
                        text = voice.description, 
                        fontSize = 11.5.sp, 
                        color = BlossomColors.TextSecondary, 
                        maxLines = 2, 
                        overflow = TextOverflow.Ellipsis 
                    ) 
                } 
                
                Spacer(modifier = Modifier.height(6.dp)) 
                
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    val displayTags = voice.tags.filter { it.isNotBlank() }.take(2) 
                    if (displayTags.isNotEmpty()) { 
                        for (t in displayTags) { 
                            Surface( 
                                shape = RoundedCornerShape(6.dp), 
                                color = BlossomColors.SurfaceCard2 
                            ) { 
                                Text( 
                                    text = t, 
                                    fontSize = 9.5.sp, 
                                    color = BlossomColors.TextSecondary, 
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                                ) 
                            } 
                        } 
                    } else if (voice.tag.isNotBlank()) { 
                        Surface( 
                            shape = RoundedCornerShape(6.dp), 
                            color = BlossomColors.SurfaceCard2 
                        ) { 
                            Text( 
                                text = voice.tag.substringBefore(" •"), 
                                fontSize = 9.5.sp, 
                                color = BlossomColors.TextSecondary, 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                            ) 
                        } 
                    } 
                    
                    if (voice.taskCount > 0) { 
                        Row(verticalAlignment = Alignment.CenterVertically) { 
                            Icon( 
                                Icons.Filled.Headphones, 
                                contentDescription = null, 
                                tint = BlossomColors.TextMuted, 
                                modifier = Modifier.size(11.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(3.dp)) 
                            Text( 
                                text = formatMetricCount(voice.taskCount), 
                                fontSize = 10.sp, 
                                color = BlossomColors.TextMuted 
                            ) 
                        } 
                    } 
                    
                    if (voice.likeCount > 0) { 
                        Row(verticalAlignment = Alignment.CenterVertically) { 
                            Icon( 
                                Icons.Filled.Favorite, 
                                contentDescription = null, 
                                tint = BlossomColors.SakuraRose.copy(alpha = 0.8f), 
                                modifier = Modifier.size(11.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(3.dp)) 
                            Text( 
                                text = formatMetricCount(voice.likeCount), 
                                fontSize = 10.sp, 
                                color = BlossomColors.TextMuted 
                            ) 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.width(8.dp)) 
            
            Row(verticalAlignment = Alignment.CenterVertically) { 
                IconButton( 
                    onClick = onToggleFavorite, 
                    modifier = Modifier.size(30.dp) 
                ) { 
                    Icon( 
                        if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, 
                        contentDescription = "Favorite", 
                        tint = if (isFavorite) BlossomColors.SakuraRose else BlossomColors.TextMuted, 
                        modifier = Modifier.size(17.dp) 
                    ) 
                } 
                
                Spacer(modifier = Modifier.width(4.dp)) 
                
                Surface( 
                    shape = CircleShape, 
                    color = if (isPlaying) BlossomColors.SakuraRose else BlossomColors.SurfaceCard1, 
                    border = BorderStroke(1.dp, if (isPlaying) BlossomColors.SakuraRose else BlossomColors.CardBorder), 
                    modifier = Modifier 
                        .size(32.dp) 
                        .clickable { onPreview() } 
                ) { 
                    Box(contentAlignment = Alignment.Center) { 
                        if (isGenerating) { 
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = BlossomColors.SakuraRose, strokeWidth = 1.8.dp) 
                        } else if (isPlaying) { 
                            Icon(Icons.Filled.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(16.dp)) 
                        } else { 
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play preview", tint = BlossomColors.SakuraRose, modifier = Modifier.size(16.dp)) 
                        } 
                    } 
                } 
                
                if (isSelected) { 
                    Spacer(modifier = Modifier.width(6.dp)) 
                    Icon( 
                        Icons.Filled.Check, 
                        contentDescription = "Selected", 
                        tint = BlossomColors.SakuraRose, 
                        modifier = Modifier.size(18.dp) 
                    ) 
                } 
            } 
        } 
    } 
} 
