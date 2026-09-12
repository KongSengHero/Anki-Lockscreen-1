package com.ankilock.ui.reading 

import android.content.Context 
import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.background 
import androidx.compose.foundation.clickable 
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
import androidx.compose.material.icons.filled.GraphicEq 
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
import androidx.compose.runtime.remember 
import androidx.compose.runtime.rememberCoroutineScope 
import androidx.compose.runtime.setValue 
import androidx.compose.ui.Alignment 
import androidx.compose.ui.Modifier 
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.platform.LocalContext 
import androidx.compose.ui.platform.LocalSoftwareKeyboardController 
import androidx.compose.ui.text.font.FontWeight 
import androidx.compose.ui.text.input.ImeAction 
import androidx.compose.ui.text.style.TextOverflow 
import androidx.compose.ui.unit.dp 
import androidx.compose.ui.unit.sp 
import com.ankilock.data.FishAudioVoiceOption 
import com.ankilock.data.PreferencesManager 
import com.ankilock.reading.FishAudioService 
import com.ankilock.ui.blossom.BlossomColors 
import kotlinx.coroutines.launch 

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
    val audioService = remember { FishAudioService(context) } 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    
    val presets = PreferencesManager.PRESET_FISH_AUDIO_VOICES 
    val cleanCurrentId = PreferencesManager.extractVoiceId(currentVoiceId) 
    
    var searchQuery by remember { mutableStateOf("") } 
    var isSearching by remember { mutableStateOf(false) } 
    var searchResults by remember { mutableStateOf<List<FishAudioVoiceOption>>(emptyList()) } 
    var searchError by remember { mutableStateOf<String?>(null) } 
    var hasSearched by remember { mutableStateOf(false) } 
    
    var customInput by remember { mutableStateOf("") } 
    var showCustomSection by remember { mutableStateOf(false) } 
    
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
    
    fun performSearch() { 
        val q = searchQuery.trim() 
        if (q.isBlank()) return 
        if (apiKey.isBlank()) { 
            searchError = "Please enter Fish Audio API key to search public voices" 
            return 
        } 
        keyboardController?.hide() 
        isSearching = true 
        searchError = null 
        hasSearched = true 
        
        coroutineScope.launch { 
            val res = audioService.searchVoices(apiKey = apiKey, query = q) 
            isSearching = false 
            res.onSuccess { list -> 
                searchResults = list 
                if (list.isEmpty()) { 
                    searchError = "No public voices found matching \"$q\"" 
                } 
            }.onFailure { err -> 
                searchError = err.message ?: "Search request failed" 
            } 
        } 
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
                
                if (hasSearched) { 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        Text( 
                            text = "Search Results (${searchResults.size})", 
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
                    
                    searchResults.forEach { voice -> 
                        val isSelected = cleanCurrentId.equals(voice.id, ignoreCase = true) 
                        VoiceOptionCard( 
                            voice = voice, 
                            isSelected = isSelected, 
                            isGenerating = (isGeneratingPreview && previewVoiceId == voice.id), 
                            isPlaying = (isPlayingAudio && previewVoiceId == voice.id), 
                            onSelect = { handleSelect(voice.id, voice.name) }, 
                            onPreview = { handlePreview(voice.id) } 
                        ) 
                    } 
                    
                    Spacer(modifier = Modifier.height(6.dp)) 
                } 
                
                Text( 
                    text = "Recommended Japanese Presets", 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = BlossomColors.TextPrimary 
                ) 
                
                presets.forEach { preset -> 
                    val isSelected = cleanCurrentId.equals(preset.id, ignoreCase = true) 
                    VoiceOptionCard( 
                        voice = preset, 
                        isSelected = isSelected, 
                        isGenerating = (isGeneratingPreview && previewVoiceId == preset.id), 
                        isPlaying = (isPlayingAudio && previewVoiceId == preset.id), 
                        onSelect = { handleSelect(preset.id, preset.name) }, 
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
private fun VoiceOptionCard( 
    voice: FishAudioVoiceOption, 
    isSelected: Boolean, 
    isGenerating: Boolean, 
    isPlaying: Boolean, 
    onSelect: () -> Unit, 
    onPreview: () -> Unit 
) { 
    Surface( 
        onClick = onSelect, 
        shape = RoundedCornerShape(14.dp), 
        color = if (isSelected) BlossomColors.SakuraRoseContainer.copy(alpha = 0.5f) else BlossomColors.SurfaceElevated, 
        border = BorderStroke( 
            1.dp, 
            if (isSelected) BlossomColors.SakuraRose.copy(alpha = 0.55f) else BlossomColors.CardBorderSubtle 
        ), 
        modifier = Modifier.fillMaxWidth() 
    ) { 
        Row( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 14.dp, vertical = 12.dp), 
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.SpaceBetween 
        ) { 
            Column(modifier = Modifier.weight(1f)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Text( 
                        text = voice.name, 
                        fontSize = 14.5.sp, 
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold, 
                        color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextPrimary, 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis 
                    ) 
                    if (voice.tag.isNotBlank()) { 
                        Spacer(modifier = Modifier.width(6.dp)) 
                        Surface( 
                            shape = RoundedCornerShape(6.dp), 
                            color = if (isSelected) BlossomColors.SakuraRose.copy(alpha = 0.20f) else BlossomColors.SurfaceCard2 
                        ) { 
                            Text( 
                                text = voice.tag, 
                                fontSize = 10.5.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.TextSecondary, 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                            ) 
                        } 
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
                } else if (voice.author.isNotBlank()) { 
                    Spacer(modifier = Modifier.height(3.dp)) 
                    Text( 
                        text = "by ${voice.author}", 
                        fontSize = 11.5.sp, 
                        color = BlossomColors.TextMuted, 
                        maxLines = 1 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.width(10.dp)) 
            
            Row(verticalAlignment = Alignment.CenterVertically) { 
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
                    Spacer(modifier = Modifier.width(10.dp)) 
                    Icon( 
                        Icons.Filled.Check, 
                        contentDescription = "Selected", 
                        tint = BlossomColors.SakuraRose, 
                        modifier = Modifier.size(20.dp) 
                    ) 
                } 
            } 
        } 
    } 
} 
