package com.ankilock.ui.study
 
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline 
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star 
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ankilock.ai.WallhavenImageResult
import com.ankilock.ai.WallhavenServiceHelper
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StoryAssetLoader
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomNunito
import com.ankilock.ui.blossom.BlossomShapes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
 
data class WallhavenPickerTarget( 
    val storyId: String, 
    val title: String, 
    val genre: String, 
    val visualAnchor: String = "", 
    val artTags: List<String> = emptyList() 
) 
 
@OptIn(ExperimentalMaterial3Api::class) 
@Composable
fun WallhavenImagePickerSheet( 
    storyId: String, 
    initialTitle: String, 
    initialGenre: String, 
    initialVisualAnchor: String = "", 
    initialArtTags: List<String> = emptyList(), 
    onDismiss: () -> Unit, 
    onImageSelected: (File) -> Unit 
) { 
    val context = LocalContext.current 
    val prefs = remember { PreferencesManager(context) } 
    val coroutineScope = rememberCoroutineScope() 
    val keyboardController = LocalSoftwareKeyboardController.current 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    
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
    
    var apiKeyText by remember { mutableStateOf(prefs.wallhavenApiKey) } 
    var isApiKeyEditing by remember { mutableStateOf(false) } 
    val hasExistingCover = remember(storyId) { 
        File(context.filesDir, "stories/images/$storyId/cover.png").exists() 
    } 
    val allSearchCategories = remember { 
        listOf( 
            "Landscape", "School", "Forest", "City", "Sunset", "Cherry Blossom", 
            "Rain", "Fantasy", "Cyberpunk", "Shrine", "Beach", "Night Sky", 
            "Room", "Cafe", "Festival", "Winter" 
        ) 
    } 
    val topCategories = remember { prefs.getTopSearchCategories(allSearchCategories, 2) } 
    val categoryChips = remember(topCategories) { 
        val remaining = (allSearchCategories.filter { it !in topCategories }).shuffled() 
        topCategories + remaining 
    } 
    var searchQuery by remember { mutableStateOf("anime") } 
    var isSearching by remember { mutableStateOf(false) } 
    var isDownloading by remember { mutableStateOf(false) } 
    var errorMessage by remember { mutableStateOf<String?>(null) } 
    var results by remember { mutableStateOf<List<WallhavenImageResult>>(emptyList()) } 
    var previewingItem by remember { mutableStateOf<WallhavenImageResult?>(null) } 
    
    val storyTags = remember(initialTitle, initialGenre, initialVisualAnchor, initialArtTags) { 
        WallhavenServiceHelper.buildSearchTags(initialTitle, initialGenre, initialVisualAnchor, initialArtTags) 
    } 
    
    fun performSearch(query: String) { 
        isSearching = true 
        errorMessage = null 
        keyboardController?.hide() 
        coroutineScope.launch { 
            val res = WallhavenServiceHelper.searchImages( 
                query = query, 
                apiKey = prefs.wallhavenApiKey 
            ) 
            isSearching = false 
            if (res.isSuccess) { 
                results = res.getOrDefault(emptyList()) 
                if (results.isEmpty()) { 
                    errorMessage = "No anime wallpapers found for \"$query\". Try other keywords." 
                } 
            } else { 
                errorMessage = res.exceptionOrNull()?.message ?: "Search failed" 
            } 
        } 
    } 
    
    LaunchedEffect(Unit) { 
        performSearch(searchQuery) 
    } 
    
    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = BlossomColors.SurfaceOverlay, 
        dragHandle = { 
            Box( 
                modifier = Modifier 
                    .padding(vertical = 12.dp) 
                    .size(width = 38.dp, height = 4.dp) 
                    .clip(BlossomShapes.Pill) 
                    .background(BlossomColors.CardBorder) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .fillMaxHeight(0.88f) 
                .padding(horizontal = 18.dp) 
                .navigationBarsPadding() 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .size(36.dp) 
                            .clip(CircleShape) 
                            .background(BlossomColors.MutedRoseContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Image, 
                            contentDescription = null, 
                            tint = BlossomColors.MutedRose, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                    } 
                    Column { 
                        Text( 
                            text = "Story Cover Art", 
                            color = BlossomColors.TextPrimary, 
                            fontSize = 17.sp, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = BlossomNunito 
                        ) 
                        Text( 
                            text = "Powered by Wallhaven", 
                            color = BlossomColors.TextMuted, 
                            fontSize = 11.sp, 
                            fontFamily = BlossomNunito 
                        ) 
                    } 
                } 
                
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(6.dp) 
                ) { 
                    if (hasExistingCover) { 
                        IconButton( 
                            onClick = { 
                                val coverFile = File(context.filesDir, "stories/images/$storyId/cover.png") 
                                if (coverFile.exists()) { 
                                    coverFile.delete() 
                                } 
                                Toast.makeText(context, "Wallpaper removed", Toast.LENGTH_SHORT).show() 
                                onImageSelected(coverFile) 
                                onDismiss() 
                            }, 
                            modifier = Modifier.size(36.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.DeleteOutline, 
                                contentDescription = "Remove Wallpaper", 
                                tint = BlossomColors.BlossomRed, 
                                modifier = Modifier.size(20.dp) 
                            ) 
                        } 
                    } 
                    IconButton( 
                        onClick = { isApiKeyEditing = !isApiKeyEditing }, 
                        modifier = Modifier.size(36.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Key, 
                            contentDescription = "API Key", 
                            tint = if (prefs.wallhavenApiKey.isNotBlank()) BlossomColors.MatchaSage else BlossomColors.TextSecondary, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    IconButton( 
                        onClick = onDismiss, 
                        modifier = Modifier.size(36.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Close, 
                            contentDescription = "Close", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                    } 
                } 
            } 
            
            AnimatedVisibility(visible = isApiKeyEditing) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(vertical = 10.dp) 
                        .clip(BlossomShapes.SquircleMedium) 
                        .background(BlossomColors.Charcoal900) 
                        .border(1.dp, BlossomColors.CardBorder, BlossomShapes.SquircleMedium) 
                        .padding(14.dp) 
                ) { 
                    Text( 
                        text = "Wallhaven API Key (Optional)", 
                        color = BlossomColors.TextPrimary, 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = BlossomNunito 
                    ) 
                    Text( 
                        text = "Free SFW searching works without a key. Add an API key from wallhaven.cc/settings/account for collections or higher limits.", 
                        color = BlossomColors.TextMuted, 
                        fontSize = 11.sp, 
                        fontFamily = BlossomNunito 
                    ) 
                    Spacer(modifier = Modifier.height(8.dp)) 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp), 
                        verticalAlignment = Alignment.CenterVertically 
                    ) { 
                        OutlinedTextField( 
                            value = apiKeyText, 
                            onValueChange = { apiKeyText = it }, 
                            placeholder = { Text("Optional API Key", color = BlossomColors.TextMuted, fontSize = 12.sp) }, 
                            singleLine = true, 
                            colors = OutlinedTextFieldDefaults.colors( 
                                focusedTextColor = BlossomColors.TextPrimary, 
                                unfocusedTextColor = BlossomColors.TextPrimary, 
                                focusedContainerColor = BlossomColors.Charcoal800, 
                                unfocusedContainerColor = BlossomColors.Charcoal800, 
                                focusedBorderColor = BlossomColors.MutedRose, 
                                unfocusedBorderColor = BlossomColors.CardBorder 
                            ), 
                            modifier = Modifier.weight(1f) 
                        ) 
                        Box( 
                            modifier = Modifier 
                                .clip(BlossomShapes.SquircleSmall) 
                                .background(BlossomColors.MutedRose) 
                                .clickable { 
                                    prefs.wallhavenApiKey = apiKeyText.trim() 
                                    isApiKeyEditing = false 
                                    performSearch(searchQuery) 
                                } 
                                .padding(horizontal = 14.dp, vertical = 14.dp) 
                        ) { 
                            Text( 
                                text = "Save", 
                                color = Color.White, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                fontFamily = BlossomNunito 
                            ) 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(10.dp)) 
            
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(8.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                OutlinedTextField( 
                    value = searchQuery, 
                    onValueChange = { searchQuery = it }, 
                    singleLine = true, 
                    placeholder = { Text("Search Wallhaven anime...", color = BlossomColors.TextMuted, fontSize = 13.sp) }, 
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), 
                    keyboardActions = KeyboardActions(onSearch = { performSearch(searchQuery) }), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedTextColor = BlossomColors.TextPrimary, 
                        unfocusedTextColor = BlossomColors.TextPrimary, 
                        focusedContainerColor = BlossomColors.Charcoal900, 
                        unfocusedContainerColor = BlossomColors.Charcoal900, 
                        focusedBorderColor = BlossomColors.MutedRose, 
                        unfocusedBorderColor = BlossomColors.CardBorder 
                    ), 
                    modifier = Modifier.weight(1f) 
                ) 
                
                Box( 
                    modifier = Modifier 
                        .clip(BlossomShapes.SquircleMedium) 
                        .background(BlossomColors.MutedRose) 
                        .clickable { performSearch(searchQuery) } 
                        .padding(14.dp), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Search, 
                        contentDescription = "Search", 
                        tint = Color.White, 
                        modifier = Modifier.size(20.dp) 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(10.dp)) 
            
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .horizontalScroll(rememberScrollState()), 
                horizontalArrangement = Arrangement.spacedBy(6.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                categoryChips.forEach { cat -> 
                    val isTop = cat in topCategories 
                    val isSelected = searchQuery.equals("anime $cat", ignoreCase = true) 
                    Box( 
                        modifier = Modifier 
                            .clip(RoundedCornerShape(10.dp)) 
                            .background( 
                                if (isSelected) BlossomColors.MutedRose 
                                else if (isTop) Color(0xFF2A2038) 
                                else BlossomColors.Charcoal800 
                            ) 
                            .border( 
                                1.dp, 
                                if (isSelected) BlossomColors.MutedRose 
                                else if (isTop) Color(0xFFF59E0B).copy(alpha = 0.6f) 
                                else BlossomColors.CardBorder, 
                                RoundedCornerShape(10.dp) 
                            ) 
                            .clickable { 
                                val newQuery = "anime $cat" 
                                searchQuery = newQuery 
                                prefs.incrementSearchCategoryUsage(cat) 
                                performSearch(newQuery) 
                            } 
                            .padding(horizontal = 10.dp, vertical = 6.dp) 
                    ) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(4.dp) 
                        ) { 
                            if (isTop) { 
                                Icon( 
                                    imageVector = Icons.Default.Star, 
                                    contentDescription = null, 
                                    tint = if (isSelected) Color(0xFFFDE047) else Color(0xFFF59E0B), 
                                    modifier = Modifier.size(11.dp) 
                                ) 
                            } 
                            Text( 
                                text = cat, 
                                fontSize = 11.sp, 
                                fontWeight = if (isTop || isSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isSelected) Color.White else if (isTop) Color(0xFFFEF3C7) else BlossomColors.TextSecondary, 
                                fontFamily = BlossomNunito 
                            ) 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(10.dp)) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .weight(1f) 
            ) { 
                when { 
                    isSearching -> { 
                        Column( 
                            modifier = Modifier.fillMaxSize(), 
                            verticalArrangement = Arrangement.Center, 
                            horizontalAlignment = Alignment.CenterHorizontally 
                        ) { 
                            CircularProgressIndicator( 
                                color = BlossomColors.MutedRose, 
                                strokeWidth = 3.dp, 
                                modifier = Modifier.size(38.dp) 
                            ) 
                            Spacer(modifier = Modifier.height(14.dp)) 
                            Text( 
                                text = "Searching Wallhaven wallpapers...", 
                                color = BlossomColors.TextSecondary, 
                                fontSize = 13.sp, 
                                fontFamily = BlossomNunito 
                            ) 
                        } 
                    } 
                    errorMessage != null -> { 
                        Column( 
                            modifier = Modifier 
                                .fillMaxSize() 
                                .padding(24.dp), 
                            verticalArrangement = Arrangement.Center, 
                            horizontalAlignment = Alignment.CenterHorizontally 
                        ) { 
                            Text( 
                                text = errorMessage ?: "", 
                                color = BlossomColors.MutedRose, 
                                fontSize = 13.sp, 
                                fontFamily = BlossomNunito 
                            ) 
                            Spacer(modifier = Modifier.height(14.dp)) 
                            Box( 
                                modifier = Modifier 
                                    .clip(BlossomShapes.SquircleSmall) 
                                    .background(BlossomColors.Charcoal800) 
                                    .clickable { performSearch(searchQuery) } 
                                    .padding(horizontal = 16.dp, vertical = 8.dp) 
                            ) { 
                                Text( 
                                    text = "Try Again", 
                                    color = BlossomColors.TextPrimary, 
                                    fontSize = 12.sp, 
                                    fontFamily = BlossomNunito 
                                ) 
                            } 
                        } 
                    } 
                    results.isNotEmpty() -> { 
                        LazyVerticalStaggeredGrid( 
                            columns = StaggeredGridCells.Fixed(2), 
                            horizontalArrangement = Arrangement.spacedBy(10.dp), 
                            verticalItemSpacing = 10.dp, 
                            contentPadding = PaddingValues(bottom = 24.dp), 
                            modifier = Modifier 
                                .fillMaxSize() 
                                .nestedScroll(noBounceNestedScroll) 
                        ) { 
                            items(results, key = { it.id }) { item -> 
                                val rawRatio = item.ratio.toFloatOrNull() ?: 1f 
                                val aspect = rawRatio.coerceIn(0.52f, 2.1f) 
                                Box( 
                                    modifier = Modifier 
                                        .fillMaxWidth() 
                                        .aspectRatio(aspect) 
                                        .clip(BlossomShapes.SquircleMedium) 
                                        .background(BlossomColors.Charcoal900) 
                                        .border(1.dp, BlossomColors.CardBorder, BlossomShapes.SquircleMedium) 
                                        .clickable { 
                                            previewingItem = item 
                                        } 
                                ) { 
                                    WallhavenRemoteThumbnail( 
                                        url = item.largeThumb.ifBlank { item.smallThumb }, 
                                        modifier = Modifier.fillMaxSize() 
                                    ) 
                                    
                                    if (item.resolution.isNotBlank()) { 
                                        Box( 
                                            modifier = Modifier 
                                                .align(Alignment.BottomCenter) 
                                                .fillMaxWidth() 
                                                .background(Color(0xBB000000)) 
                                                .padding(horizontal = 8.dp, vertical = 4.dp) 
                                        ) { 
                                            Text( 
                                                text = item.resolution, 
                                                color = BlossomColors.TextSecondary, 
                                                fontSize = 9.sp, 
                                                maxLines = 1, 
                                                fontFamily = BlossomNunito 
                                            ) 
                                        } 
                                    } 
                                } 
                            } 
                        } 
                    } 
                    else -> { 
                        Column( 
                            modifier = Modifier.fillMaxSize(), 
                            verticalArrangement = Arrangement.Center, 
                            horizontalAlignment = Alignment.CenterHorizontally 
                        ) { 
                            Text( 
                                text = "Search for keywords to find anime wallpapers", 
                                color = BlossomColors.TextMuted, 
                                fontSize = 13.sp, 
                                fontFamily = BlossomNunito 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    if (previewingItem != null) { 
        val item = previewingItem!! 
        Dialog( 
            onDismissRequest = { if (!isDownloading) previewingItem = null }, 
            properties = DialogProperties( 
                usePlatformDefaultWidth = false, 
                decorFitsSystemWindows = true 
            ) 
        ) { 
            val configuration = LocalConfiguration.current 
            val density = LocalDensity.current 
            val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() } 
            val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() } 
            
            var scale by remember { mutableFloatStateOf(1f) } 
            var offset by remember { mutableStateOf(Offset.Zero) } 
            var rotationAngle by remember { mutableFloatStateOf(0f) } 
            val transformState = rememberTransformableState { zoomChange, panChange, _ -> 
                val newScale = (scale * zoomChange).coerceIn(1f, 5f) 
                scale = newScale 
                if (newScale <= 1.05f) { 
                    offset = Offset.Zero 
                } else { 
                    val panMultiplier = 1f + (newScale - 1f) * 1.5f 
                    val maxOffsetX = (screenWidthPx * (newScale - 1f) * 0.75f).coerceAtLeast(0f) 
                    val maxOffsetY = (screenHeightPx * (newScale - 1f) * 0.75f).coerceAtLeast(0f) 
                    offset = Offset( 
                        x = (offset.x + panChange.x * panMultiplier).coerceIn(-maxOffsetX, maxOffsetX), 
                        y = (offset.y + panChange.y * panMultiplier).coerceIn(-maxOffsetY, maxOffsetY) 
                    ) 
                } 
            } 
            
            Box( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .background(Color.Black.copy(alpha = 0.96f)) 
            ) { 
                Box( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .pointerInput(Unit) { 
                            detectTapGestures( 
                                onDoubleTap = { 
                                    if (scale > 1.2f) { 
                                        scale = 1f 
                                        offset = Offset.Zero 
                                    } else { 
                                        scale = 2.5f 
                                    } 
                                } 
                            ) 
                        }, 
                    contentAlignment = Alignment.Center 
                ) { 
                    WallhavenRemoteThumbnail( 
                        url = item.path.ifBlank { item.largeThumb }, 
                        contentScale = ContentScale.Fit, 
                        modifier = Modifier 
                            .fillMaxSize() 
                            .graphicsLayer { 
                                scaleX = scale 
                                scaleY = scale 
                                rotationZ = rotationAngle 
                                translationX = offset.x 
                                translationY = offset.y 
                            } 
                            .transformable(state = transformState) 
                    ) 
                } 
                
                Row( 
                    modifier = Modifier 
                        .align(Alignment.TopEnd) 
                        .statusBarsPadding() 
                        .padding(16.dp), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    IconButton( 
                        onClick = { 
                            rotationAngle = (rotationAngle + 90f) % 360f 
                            offset = Offset.Zero 
                        }, 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(CircleShape) 
                            .background(Color.Black.copy(alpha = 0.65f)) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Refresh, 
                            contentDescription = "Rotate", 
                            tint = Color.White, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                    } 
                    
                    Button( 
                        onClick = { 
                            isDownloading = true 
                            coroutineScope.launch { 
                                val dlRes = WallhavenServiceHelper.downloadImageBytes(item.path) 
                                val bytes = dlRes.getOrNull() 
                                if (bytes != null && bytes.isNotEmpty()) { 
                                    val dir = File(context.filesDir, "stories/images/$storyId") 
                                    dir.mkdirs() 
                                    val coverFile = File(dir, "cover.png") 
                                    coverFile.writeBytes(bytes) 
                                    val pageFile = File(dir, "1.png") 
                                    pageFile.writeBytes(bytes) 
                                    StoryAssetLoader.invalidateStoryCache(storyId) 
                                    isDownloading = false 
                                    previewingItem = null 
                                    onImageSelected(coverFile) 
                                    onDismiss() 
                                } else { 
                                    isDownloading = false 
                                    errorMessage = "Failed to download selected wallpaper." 
                                    previewingItem = null 
                                } 
                            } 
                        }, 
                        enabled = !isDownloading, 
                        shape = BlossomShapes.Pill, 
                        colors = ButtonDefaults.buttonColors( 
                            containerColor = BlossomColors.SlateBlue 
                        ), 
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp), 
                        modifier = Modifier.height(38.dp) 
                    ) { 
                        if (isDownloading) { 
                            CircularProgressIndicator( 
                                color = Color.White, 
                                strokeWidth = 2.dp, 
                                modifier = Modifier.size(16.dp) 
                            ) 
                        } else { 
                            Text( 
                                text = "Apply", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 13.sp 
                            ) 
                        } 
                    } 
                    
                    IconButton( 
                        onClick = { if (!isDownloading) previewingItem = null }, 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(CircleShape) 
                            .background(Color.Black.copy(alpha = 0.65f)) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Close, 
                            contentDescription = "Close", 
                            tint = Color.White, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                    } 
                } 
            } 
        } 
    } 
} 
 
@Composable
fun WallhavenRemoteThumbnail( 
    url: String, 
    contentScale: ContentScale = ContentScale.Crop, 
    modifier: Modifier = Modifier 
) { 
    val bitmapState = produceState<ImageBitmap?>(initialValue = null, key1 = url) { 
        value = withContext(Dispatchers.IO) { 
            try { 
                val conn = URL(url).openConnection() as HttpURLConnection 
                conn.setRequestProperty("User-Agent", "AnkiLock-Blossom/1.0") 
                conn.connectTimeout = 8000 
                conn.readTimeout = 12000 
                conn.instanceFollowRedirects = true 
                conn.inputStream.use { stream -> 
                    BitmapFactory.decodeStream(stream)?.asImageBitmap() 
                } 
            } catch (_: Exception) { 
                null 
            } 
        } 
    } 
    
    val bmp = bitmapState.value 
    if (bmp != null) { 
        Image( 
            bitmap = bmp, 
            contentDescription = null, 
            contentScale = contentScale, 
            modifier = modifier 
        ) 
    } else { 
        Box( 
            modifier = modifier.background(BlossomColors.Charcoal800), 
            contentAlignment = Alignment.Center 
        ) { 
            CircularProgressIndicator( 
                color = BlossomColors.MutedRose, 
                strokeWidth = 2.dp, 
                modifier = Modifier.size(20.dp) 
            ) 
        } 
    } 
} 
