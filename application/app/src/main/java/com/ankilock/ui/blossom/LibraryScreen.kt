package com.ankilock.ui.blossom

import android.content.Intent 
import android.net.Uri 
import android.speech.tts.TextToSpeech 
import android.widget.Toast 
import androidx.compose.animation.AnimatedVisibility 
import androidx.compose.animation.fadeIn 
import androidx.compose.animation.fadeOut 
import androidx.compose.foundation.background 
import androidx.compose.foundation.border 
import androidx.compose.foundation.clickable 
import androidx.compose.foundation.layout.Arrangement 
import androidx.compose.foundation.layout.Box 
import androidx.compose.foundation.layout.Column 
import androidx.compose.foundation.layout.PaddingValues 
import androidx.compose.foundation.layout.Row 
import androidx.compose.foundation.layout.Spacer 
import androidx.compose.foundation.layout.fillMaxSize 
import androidx.compose.foundation.layout.fillMaxWidth 
import androidx.compose.foundation.layout.height 
import androidx.compose.foundation.layout.offset 
import androidx.compose.foundation.layout.padding 
import androidx.compose.foundation.layout.size 
import androidx.compose.foundation.layout.width 
import androidx.compose.animation.animateContentSize 
import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.lazy.LazyColumn 
import androidx.compose.foundation.lazy.items 
import androidx.compose.foundation.shape.CircleShape 
import androidx.compose.foundation.shape.RoundedCornerShape 
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.automirrored.filled.VolumeUp 
import androidx.compose.material.icons.filled.Add 
import androidx.compose.material.icons.filled.AutoAwesome 
import androidx.compose.material.icons.filled.Bookmark 
import androidx.compose.material.icons.filled.BookmarkBorder 
import androidx.compose.material.icons.filled.Close 
import androidx.compose.material.icons.filled.Delete 
import androidx.compose.material.icons.filled.DeleteOutline 
import androidx.compose.material.icons.filled.Edit 
import androidx.compose.material.icons.filled.Search 
import androidx.compose.material.icons.filled.Settings 
import androidx.compose.material.icons.filled.Sync 
import androidx.compose.material.icons.filled.VolumeUp 
import androidx.compose.material3.AlertDialog 
import androidx.compose.material3.Button 
import androidx.compose.material3.ButtonDefaults 
import androidx.compose.material3.Card 
import androidx.compose.material3.CardDefaults 
import androidx.compose.material3.OutlinedButton 
import androidx.compose.material3.Icon 
import androidx.compose.material3.IconButton 
import androidx.compose.material3.OutlinedTextField 
import androidx.compose.material3.OutlinedTextFieldDefaults 
import androidx.compose.material3.Surface 
import androidx.compose.material3.Text 
import androidx.compose.material3.TextButton 
import androidx.compose.runtime.Composable 
import androidx.compose.runtime.DisposableEffect 
import androidx.compose.runtime.getValue 
import androidx.compose.runtime.mutableStateOf 
import androidx.compose.runtime.remember 
import androidx.compose.runtime.rememberCoroutineScope 
import androidx.compose.runtime.setValue 
import androidx.compose.ui.Alignment 
import androidx.compose.ui.Modifier 
import androidx.compose.ui.draw.clip 
import androidx.compose.ui.graphics.Brush 
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.platform.LocalContext 
import androidx.compose.foundation.rememberScrollState 
import androidx.compose.foundation.verticalScroll 
import androidx.compose.foundation.layout.WindowInsets 
import androidx.compose.foundation.layout.asPaddingValues 
import androidx.compose.foundation.layout.imePadding 
import androidx.compose.foundation.layout.navigationBars 
import androidx.compose.foundation.layout.navigationBarsPadding 
import androidx.compose.material.icons.filled.Translate 
import androidx.compose.material3.CircularProgressIndicator 
import androidx.compose.material3.ExperimentalMaterial3Api 
import androidx.compose.material3.ModalBottomSheet 
import androidx.compose.material3.rememberModalBottomSheetState 
import androidx.compose.ui.geometry.Offset 
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection 
import androidx.compose.ui.input.nestedscroll.NestedScrollSource 
import androidx.compose.ui.input.nestedscroll.nestedScroll 
import androidx.compose.ui.unit.Velocity 
import androidx.compose.foundation.text.KeyboardOptions 
import androidx.compose.ui.text.input.ImeAction 
import androidx.compose.ui.text.font.FontWeight 
import androidx.compose.ui.text.style.TextOverflow 
import androidx.compose.ui.unit.dp 
import androidx.compose.ui.unit.sp 
import androidx.core.content.FileProvider 
import com.ankilock.anki.AnkiDroidHelper 
import com.ankilock.anki.AnkiPackageExporter 
import com.ankilock.reading.AiSentenceGenerator 
import com.ankilock.data.BookmarkManager 
import com.ankilock.data.BookmarkedWord 
import com.ankilock.data.JishoServiceHelper 
import com.ankilock.data.PreferencesManager 
import com.ankilock.translation.TranslatorService 
import com.ankilock.ui.blossom.BlossomShapes 
import com.ankilock.ui.components.Squircle3DButton 
import com.ankilock.util.JapaneseTtsHelper 
import kotlinx.coroutines.Dispatchers 
import kotlinx.coroutines.launch 
import kotlinx.coroutines.withContext 
import java.util.Locale 

@Composable 
fun LibraryScreen( 
    padding: PaddingValues, 
    prefs: PreferencesManager, 
    ankiHelper: AnkiDroidHelper, 
    onNavigateToJisho: (String) -> Unit 
) { 
    val context = LocalContext.current 
    val scope = rememberCoroutineScope() 
    val words = BookmarkManager.bookmarkedWords 

    var searchQuery by remember { mutableStateOf("") } 
    var deckName by remember { mutableStateOf("Blossom::Vocabulary") } 
    var cardTag by remember { mutableStateOf("Blossom") } 

    var showDeckSettingsDialog by remember { mutableStateOf(false) } 
    var showAddWordDialog by remember { mutableStateOf(false) } 
    var wordToEdit by remember { mutableStateOf<BookmarkedWord?>(null) } 
    var wordToDelete by remember { mutableStateOf<BookmarkedWord?>(null) } 
    var isExporting by remember { mutableStateOf(false) } 
    var ttsHelper by remember { mutableStateOf<JapaneseTtsHelper?>(null) } 
    var playingWordId by remember { mutableStateOf<String?>(null) } 
    DisposableEffect(Unit) { 
        onDispose { 
            ttsHelper?.shutdown() 
        } 
    } 

    val filteredWords = remember(words, searchQuery) { 
        if (searchQuery.isBlank()) { 
            words 
        } else { 
            val q = searchQuery.trim().lowercase(Locale.ROOT) 
            words.filter { 
                it.kanji.lowercase(Locale.ROOT).contains(q) || 
                it.reading.lowercase(Locale.ROOT).contains(q) || 
                it.meaning.lowercase(Locale.ROOT).contains(q) || 
                it.sentence.lowercase(Locale.ROOT).contains(q) 
            } 
        } 
    } 

    val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() 
    val appBottomNavHeight = 78.dp 
    val bottomNavTotal = navBarsBottom + appBottomNavHeight 
    
    Box( 
        modifier = Modifier 
            .fillMaxSize() 
            .padding(top = padding.calculateTopPadding()) 
    ) { 
        Column( 
            modifier = Modifier.fillMaxSize() 
        ) { 
            LibraryTopSection( 
                query = searchQuery, 
                onQueryChange = { searchQuery = it }, 
                wordCount = words.size, 
                onAddWord = { showAddWordDialog = true }, 
                onOpenSettings = { showDeckSettingsDialog = true } 
            ) 

            Spacer(modifier = Modifier.height(4.dp)) 

            if (filteredWords.isEmpty()) { 
                EmptyLibraryView( 
                    isSearch = searchQuery.isNotBlank(), 
                    onAddWord = { showAddWordDialog = true } 
                ) 
            } else { 
                LazyColumn( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .weight(1f), 
                    contentPadding = PaddingValues( 
                        start = 16.dp, 
                        end = 16.dp, 
                        top = 4.dp, 
                        bottom = bottomNavTotal + 68.dp 
                    ), 
                    verticalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    items( 
                        items = filteredWords, 
                        key = { it.id }, 
                        contentType = { "word_card" } 
                    ) { word -> 
                        val isPlaying = playingWordId == word.id 
                        LibraryWordCard( 
                            word = word, 
                            isPlaying = isPlaying, 
                            onPlayAudio = { 
                                val helper = ttsHelper ?: JapaneseTtsHelper(context).also { ttsHelper = it } 
                                val toSpeak = word.reading.ifBlank { word.kanji } 
                                playingWordId = word.id 
                                helper.speak( 
                                    text = toSpeak, 
                                    onStart = { playingWordId = word.id }, 
                                    onDone = { if (playingWordId == word.id) playingWordId = null }, 
                                    onError = { if (playingWordId == word.id) playingWordId = null } 
                                ) 
                            }, 
                            onEdit = { wordToEdit = word }, 
                            onDelete = { wordToDelete = word }, 
                            onJishoLookup = { onNavigateToJisho(word.kanji) } 
                        ) 
                    } 
                } 
            } 
        } 

        LibraryBottomActionBar( 
            isExporting = isExporting, 
            onBuildApkg = { 
                if (words.isEmpty()) { 
                    Toast.makeText(context, "No bookmarked words to export", Toast.LENGTH_SHORT).show() 
                    return@LibraryBottomActionBar 
                } 
                isExporting = true 
                scope.launch { 
                    try { 
                        val apkgFile = withContext(Dispatchers.IO) { 
                            AnkiPackageExporter.exportDeck( 
                                context = context, 
                                deckName = deckName, 
                                tag = cardTag, 
                                words = words.toList() 
                            ) 
                        } 
                        val uri = FileProvider.getUriForFile( 
                            context, 
                            "${context.packageName}.fileprovider", 
                            apkgFile 
                        ) 
                        val shareIntent = Intent(Intent.ACTION_SEND).apply { 
                            type = "application/apkg" 
                            putExtra(Intent.EXTRA_STREAM, uri) 
                            putExtra(Intent.EXTRA_SUBJECT, "$deckName Anki Deck") 
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) 
                        } 
                        context.startActivity( 
                            Intent.createChooser(shareIntent, "Save or Open $deckName") 
                        ) 
                    } catch (e: Exception) { 
                        Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_LONG).show() 
                    } finally { 
                        isExporting = false 
                    } 
                } 
            }, 
            bottomSpacing = bottomNavTotal, 
            modifier = Modifier.align(Alignment.BottomCenter) 
        ) 
    } 

    if (showDeckSettingsDialog) { 
        DeckSettingsBottomSheet( 
            initialDeckName = deckName, 
            initialTag = cardTag, 
            onDismiss = { showDeckSettingsDialog = false }, 
            onSave = { newDeck, newTag -> 
                deckName = newDeck 
                cardTag = newTag 
                showDeckSettingsDialog = false 
            } 
        ) 
    } 

    if (showAddWordDialog) { 
        AddOrEditWordBottomSheet( 
            initialWord = null, 
            prefs = prefs, 
            cardTag = cardTag, 
            onDismiss = { showAddWordDialog = false }, 
            onSave = { newWord -> 
                BookmarkManager.addWord(newWord) 
                showAddWordDialog = false 
            } 
        ) 
    } 

    wordToEdit?.let { editTarget -> 
        AddOrEditWordBottomSheet( 
            initialWord = editTarget, 
            prefs = prefs, 
            cardTag = cardTag, 
            onDismiss = { wordToEdit = null }, 
            onSave = { updatedWord -> 
                BookmarkManager.updateWord(updatedWord) 
                wordToEdit = null 
            } 
        ) 
    } 

    wordToDelete?.let { deleteTarget -> 
        androidx.compose.ui.window.Dialog(onDismissRequest = { wordToDelete = null }) { 
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
                            imageVector = Icons.Default.DeleteOutline, 
                            contentDescription = null, 
                            tint = BlossomColors.BlossomRed, 
                            modifier = Modifier.size(24.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(10.dp)) 
                        Text( 
                            text = "Remove Bookmark?", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 18.sp, 
                            color = BlossomColors.TextPrimary 
                        ) 
                    } 

                    Text( 
                        text = "Are you sure you want to remove \"${deleteTarget.kanji}\" from your library? This action cannot be undone.", 
                        fontSize = 13.sp, 
                        color = BlossomColors.TextSecondary, 
                        lineHeight = 20.sp 
                    ) 

                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(10.dp) 
                    ) { 
                        OutlinedButton( 
                            onClick = { wordToDelete = null }, 
                            shape = RoundedCornerShape(12.dp), 
                            border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                            modifier = Modifier.weight(1f), 
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp) 
                        ) { 
                            Text("Cancel", color = BlossomColors.TextSecondary, maxLines = 1, softWrap = false) 
                        } 

                        Button( 
                            onClick = { 
                                BookmarkManager.removeWord(deleteTarget.kanji) 
                                wordToDelete = null 
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

@Composable 
private fun LibraryTopSection( 
    query: String, 
    onQueryChange: (String) -> Unit, 
    wordCount: Int, 
    onAddWord: () -> Unit, 
    onOpenSettings: () -> Unit 
) { 
    Column( 
        modifier = Modifier 
            .fillMaxWidth() 
            .padding(horizontal = 16.dp, vertical = 6.dp) 
    ) { 
        Row( 
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.SpaceBetween, 
            verticalAlignment = Alignment.CenterVertically 
        ) { 
            Text( 
                text = "$wordCount words mined • Ready for Anki", 
                fontSize = 12.sp, 
                fontFamily = BlossomNunito, 
                fontWeight = FontWeight.Medium, 
                color = BlossomColors.TextSecondary, 
                modifier = Modifier 
                    .background( 
                        color = BlossomColors.SurfaceElevated.copy(alpha = 0.5f), 
                        shape = RoundedCornerShape(8.dp) 
                    ) 
                    .padding(horizontal = 10.dp, vertical = 4.dp) 
            ) 
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { 
                Squircle3DButton( 
                    onClick = onAddWord, 
                    modifier = Modifier.size(38.dp), 
                    shape = BlossomShapes.SquircleSmall, 
                    containerColor = BlossomColors.WarmAmber, 
                    bevelColor = Color(0xFFC07000), 
                    contentColor = Color.White, 
                    contentPadding = PaddingValues(0.dp), 
                    depth = 3.dp 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Add, 
                        contentDescription = "Add Word", 
                        tint = Color.White, 
                        modifier = Modifier.size(20.dp) 
                    ) 
                } 
                
                Squircle3DButton( 
                    onClick = onOpenSettings, 
                    modifier = Modifier.size(38.dp), 
                    shape = BlossomShapes.SquircleSmall, 
                    containerColor = BlossomColors.SurfaceElevated, 
                    bevelColor = BlossomColors.CardBorder, 
                    contentColor = BlossomColors.TextSecondary, 
                    contentPadding = PaddingValues(0.dp), 
                    depth = 3.dp 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Settings, 
                        contentDescription = "Deck Settings", 
                        tint = BlossomColors.TextSecondary, 
                        modifier = Modifier.size(18.dp) 
                    ) 
                } 
            } 
        } 
        
        Spacer(modifier = Modifier.height(8.dp)) 
        
        OutlinedTextField( 
            value = query, 
            onValueChange = onQueryChange, 
            modifier = Modifier.fillMaxWidth(), 
            placeholder = { 
                Text( 
                    text = "Search by kanji, reading, or meaning...", 
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
                    IconButton(onClick = { onQueryChange("") }) { 
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search) 
        ) 
    } 
} 

@Composable 
private fun LibraryWordCard( 
    word: BookmarkedWord, 
    isPlaying: Boolean, 
    onPlayAudio: () -> Unit, 
    onEdit: () -> Unit, 
    onDelete: () -> Unit, 
    onJishoLookup: () -> Unit 
) { 
    Surface( 
        shape = RoundedCornerShape(18.dp), 
        color = BlossomColors.SurfaceElevated, 
        border = BorderStroke(1.dp, BlossomColors.CardBorder), 
        modifier = Modifier.fillMaxWidth() 
    ) { 
        Column(modifier = Modifier.padding(16.dp)) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                verticalAlignment = Alignment.Top, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Column(modifier = Modifier.weight(1f)) { 
                    if (word.reading.isNotBlank() && word.reading != word.kanji) { 
                        Text( 
                            text = word.reading, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = BlossomColors.WarmAmber 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                    } 

                    Text( 
                        text = word.kanji, 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.TextPrimary 
                    ) 
                } 

                Row(verticalAlignment = Alignment.CenterVertically) { 
                    IconButton( 
                        onClick = onPlayAudio, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp, 
                            contentDescription = "Pronounce", 
                            tint = if (isPlaying) BlossomColors.WarmAmber else BlossomColors.TextSecondary.copy(alpha = 0.45f), 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 

                    IconButton( 
                        onClick = onJishoLookup, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Search, 
                            contentDescription = "Lookup Jisho", 
                            tint = BlossomColors.MatchaSage, 
                            modifier = Modifier.size(17.dp) 
                        ) 
                    } 

                    IconButton( 
                        onClick = onEdit, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Edit, 
                            contentDescription = "Edit", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(17.dp) 
                        ) 
                    } 

                    IconButton( 
                        onClick = onDelete, 
                        modifier = Modifier.size(34.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.DeleteOutline, 
                            contentDescription = "Delete", 
                            tint = BlossomColors.BlossomRed, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                } 
            } 

            if (word.meaning.isNotBlank()) { 
                Spacer(modifier = Modifier.height(6.dp)) 
                Text( 
                    text = word.meaning, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Medium, 
                    color = BlossomColors.SkyCyan, 
                    maxLines = 2, 
                    overflow = TextOverflow.Ellipsis 
                ) 
            } 

            if (word.sentence.isNotBlank()) { 
                Spacer(modifier = Modifier.height(10.dp)) 
                Surface( 
                    shape = RoundedCornerShape(12.dp), 
                    color = BlossomColors.BackgroundDeep.copy(alpha = 0.6f), 
                    border = BorderStroke(0.8.dp, BlossomColors.CardBorderSubtle), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Column( 
                        modifier = Modifier.padding(12.dp) 
                    ) { 
                        Text( 
                            text = word.sentence.replace("<b>", "").replace("</b>", ""), 
                            fontSize = 13.sp, 
                            color = BlossomColors.TextPrimary, 
                            lineHeight = 18.sp 
                        ) 
                        if (word.sentenceMeaning.isNotBlank()) { 
                            Spacer(modifier = Modifier.height(4.dp)) 
                            Text( 
                                text = word.sentenceMeaning, 
                                fontSize = 12.sp, 
                                color = BlossomColors.TextMuted 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
    } 
} 

@Composable 
private fun EmptyLibraryView( 
    isSearch: Boolean, 
    onAddWord: () -> Unit 
) { 
    Column( 
        modifier = Modifier 
            .fillMaxSize() 
            .padding(horizontal = 32.dp) 
            .offset(y = (-55).dp), 
        horizontalAlignment = Alignment.CenterHorizontally, 
        verticalArrangement = Arrangement.Center 
    ) { 
        Box( 
            modifier = Modifier 
                .size(72.dp) 
                .clip(CircleShape) 
                .background(BlossomColors.WarmAmberContainer.copy(alpha = 0.25f)), 
            contentAlignment = Alignment.Center 
        ) { 
            Icon( 
                imageVector = if (isSearch) Icons.Default.Search else Icons.Default.BookmarkBorder, 
                contentDescription = null, 
                tint = BlossomColors.WarmAmber, 
                modifier = Modifier.size(36.dp) 
            ) 
        } 

        Spacer(modifier = Modifier.height(16.dp)) 

        Text( 
            text = if (isSearch) "No matching words found" else "Your Library is empty", 
            fontSize = 18.sp, 
            fontFamily = BlossomNunito, 
            fontWeight = FontWeight.Bold, 
            color = BlossomColors.TextPrimary 
        ) 

        Spacer(modifier = Modifier.height(8.dp)) 

        Text( 
            text = if (isSearch) "Try searching for a different kanji or meaning." else "Tap the bookmark icon on any word in Stories or Jisho to build your personal Anki deck.", 
            fontSize = 13.sp, 
            fontFamily = BlossomNunito, 
            color = BlossomColors.TextSecondary, 
            textAlign = androidx.compose.ui.text.style.TextAlign.Center, 
            lineHeight = 19.sp 
        ) 

        if (!isSearch) { 
            Spacer(modifier = Modifier.height(20.dp)) 
            Button( 
                onClick = onAddWord, 
                colors = ButtonDefaults.buttonColors( 
                    containerColor = BlossomColors.WarmAmber 
                ), 
                shape = RoundedCornerShape(12.dp) 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Add, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.size(16.dp) 
                ) 
                Spacer(modifier = Modifier.width(6.dp)) 
                Text("Add First Word", color = Color.White, fontWeight = FontWeight.Bold) 
            } 
        } 
    } 
} 

@Composable 
private fun LibraryBottomActionBar( 
    isExporting: Boolean, 
    onBuildApkg: () -> Unit, 
    bottomSpacing: androidx.compose.ui.unit.Dp, 
    modifier: Modifier = Modifier 
) { 
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .background( 
                Brush.verticalGradient( 
                    colors = listOf( 
                        Color.Transparent, 
                        BlossomColors.BackgroundDeep.copy(alpha = 0.85f), 
                        BlossomColors.BackgroundDeep.copy(alpha = 0.98f) 
                    ) 
                ) 
            ) 
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomSpacing + 8.dp) 
    ) { 
        Squircle3DButton( 
            onClick = onBuildApkg, 
            enabled = !isExporting, 
            shape = BlossomShapes.SquircleMedium, 
            containerColor = BlossomColors.WarmAmber, 
            bevelColor = Color(0xFFC07000), 
            contentColor = Color.White, 
            contentPadding = PaddingValues(0.dp), 
            modifier = Modifier 
                .fillMaxWidth() 
                .height(48.dp), 
            depth = 3.dp 
        ) { 
            Row( 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.Center, 
                modifier = Modifier.fillMaxSize() 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Bookmark, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.size(18.dp) 
                ) 
                Spacer(modifier = Modifier.width(6.dp)) 
                Text( 
                    text = if (isExporting) "Building..." else "BUILD .APKG", 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White 
                ) 
            } 
        } 
    } 
} 

@OptIn(ExperimentalMaterial3Api::class) 
@Composable 
private fun DeckSettingsBottomSheet( 
    initialDeckName: String, 
    initialTag: String, 
    onDismiss: () -> Unit, 
    onSave: (String, String) -> Unit 
) { 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    var deckName by remember { mutableStateOf(initialDeckName) } 
    var tag by remember { mutableStateOf(initialTag) } 
    val noBounceNestedScroll = remember { 
        object : NestedScrollConnection { 
            override fun onPostScroll( 
                consumed: Offset, 
                available: Offset, 
                source: NestedScrollSource 
            ): Offset { 
                return if (available.y < 0f) Offset(0f, available.y) else Offset.Zero 
            } 

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity { 
                return Velocity(0f, available.y) 
            } 
        } 
    } 

    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = BlossomColors.BackgroundDeep, 
        windowInsets = WindowInsets(0), 
        dragHandle = { 
            Box( 
                modifier = Modifier 
                    .padding(top = 12.dp, bottom = 8.dp) 
                    .size(width = 44.dp, height = 4.dp) 
                    .clip(BlossomShapes.Pill) 
                    .background(BlossomColors.CardBorder) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .nestedScroll(noBounceNestedScroll) 
                .padding(horizontal = 20.dp, vertical = 6.dp) 
                .navigationBarsPadding() 
                .imePadding(), 
            verticalArrangement = Arrangement.spacedBy(16.dp) 
        ) { 
            Text( 
                text = "Deck Configuration", 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = BlossomNunito, 
                color = BlossomColors.TextPrimary 
            ) 

            OutlinedTextField( 
                value = deckName, 
                onValueChange = { deckName = it }, 
                label = { Text("Deck Name") }, 
                singleLine = true, 
                shape = RoundedCornerShape(12.dp), 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedBorderColor = BlossomColors.WarmAmber, 
                    unfocusedBorderColor = BlossomColors.CardBorder, 
                    cursorColor = BlossomColors.WarmAmber, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedLabelColor = BlossomColors.WarmAmber, 
                    unfocusedLabelColor = BlossomColors.TextSecondary 
                ), 
                modifier = Modifier.fillMaxWidth() 
            ) 

            OutlinedTextField( 
                value = tag, 
                onValueChange = { tag = it }, 
                label = { Text("Card Tag (e.g. Kaishi 1.5k)") }, 
                singleLine = true, 
                shape = RoundedCornerShape(12.dp), 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedBorderColor = BlossomColors.WarmAmber, 
                    unfocusedBorderColor = BlossomColors.CardBorder, 
                    cursorColor = BlossomColors.WarmAmber, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedLabelColor = BlossomColors.WarmAmber, 
                    unfocusedLabelColor = BlossomColors.TextSecondary 
                ), 
                modifier = Modifier.fillMaxWidth() 
            ) 

            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(10.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Squircle3DButton( 
                    onClick = onDismiss, 
                    shape = BlossomShapes.SquircleMedium, 
                    containerColor = BlossomColors.SurfaceElevated, 
                    bevelColor = BlossomColors.CardBorder, 
                    contentColor = BlossomColors.TextSecondary, 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(44.dp), 
                    depth = 3.dp, 
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp) 
                ) { 
                    Text( 
                        text = "Cancel", 
                        color = BlossomColors.TextSecondary, 
                        fontWeight = FontWeight.SemiBold, 
                        modifier = Modifier.align(Alignment.Center) 
                    ) 
                } 

                Squircle3DButton( 
                    onClick = { 
                        onSave(deckName.ifBlank { "Blossom::Vocabulary" }.trim(), tag.ifBlank { "Blossom" }.trim()) 
                    }, 
                    shape = BlossomShapes.SquircleMedium, 
                    containerColor = BlossomColors.WarmAmber, 
                    bevelColor = Color(0xFFC07000), 
                    contentColor = Color.White, 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(44.dp), 
                    depth = 3.dp, 
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp) 
                ) { 
                    Text( 
                        text = "Save", 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.align(Alignment.Center) 
                    ) 
                } 
            } 
            Spacer(modifier = Modifier.height(10.dp)) 
        } 
    } 
} 

@OptIn(ExperimentalMaterial3Api::class) 
@Composable 
private fun AddOrEditWordBottomSheet( 
    initialWord: BookmarkedWord?, 
    prefs: PreferencesManager, 
    cardTag: String, 
    onDismiss: () -> Unit, 
    onSave: (BookmarkedWord) -> Unit 
) { 
    val context = LocalContext.current 
    val scope = rememberCoroutineScope() 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 

    var kanji by remember { mutableStateOf(initialWord?.kanji ?: "") } 
    var reading by remember { mutableStateOf(initialWord?.reading ?: "") } 
    var meaning by remember { mutableStateOf(initialWord?.meaning ?: "") } 
    var furigana by remember { mutableStateOf(initialWord?.furigana ?: "") } 
    var sentence by remember { mutableStateOf(initialWord?.sentence ?: "") } 
    var sentenceMeaning by remember { mutableStateOf(initialWord?.sentenceMeaning ?: "") } 
    var sentenceFurigana by remember { mutableStateOf(initialWord?.sentenceFurigana ?: "") } 

    var isTranslatingWord by remember { mutableStateOf(false) } 
    var isFetchingJishoSentence by remember { mutableStateOf(false) } 
    var isGeneratingAiSentence by remember { mutableStateOf(false) } 
    var isTranslatingSentence by remember { mutableStateOf(false) } 

    val onAutoTranslateWord = { 
        val clean = kanji.trim() 
        if (clean.isNotBlank() && !isTranslatingWord) { 
            isTranslatingWord = true 
            scope.launch { 
                try { 
                    var fetchedReading = "" 
                    var fetchedMeaning = "" 
                    val jishoResult = JishoServiceHelper.searchWords(clean) 
                    val jisho = jishoResult.getOrNull()?.firstOrNull() 
                    if (jisho != null) { 
                        if (jisho.primaryReading.isNotBlank()) { 
                            fetchedReading = jisho.primaryReading 
                        } 
                        if (jisho.primaryEnglish.isNotBlank()) { 
                            fetchedMeaning = jisho.primaryEnglish 
                        } 
                    } 
                    if (fetchedMeaning.isBlank()) { 
                        val translator = TranslatorService(context) 
                        val transRes = translator.translate(clean) 
                        transRes.getOrNull()?.let { 
                            if (it.translatedText.isNotBlank()) { 
                                fetchedMeaning = it.translatedText 
                            } 
                        } 
                    } 
                    if (reading.isBlank() && fetchedReading.isNotBlank()) { 
                        reading = fetchedReading 
                    } 
                    if (fetchedMeaning.isNotBlank()) { 
                        meaning = fetchedMeaning 
                    } 
                } catch (e: Exception) { 
                } finally { 
                    isTranslatingWord = false 
                } 
            } 
        } 
    } 

    val onFetchJishoSentence = { 
        val clean = kanji.trim() 
        if (clean.isNotBlank() && !isFetchingJishoSentence) { 
            isFetchingJishoSentence = true 
            scope.launch { 
                try { 
                    val res = JishoServiceHelper.fetchSentence(clean) 
                    if (res != null && res.sentence.isNotBlank()) { 
                        sentence = res.sentence 
                        if (res.meaning.isNotBlank()) { 
                            sentenceMeaning = res.meaning 
                        } else { 
                            val transRes = TranslatorService(context).translate(res.sentence) 
                            transRes.getOrNull()?.let { 
                                if (it.translatedText.isNotBlank()) { 
                                    sentenceMeaning = it.translatedText 
                                } 
                            } 
                        } 
                    } else { 
                        Toast.makeText(context, "No example sentence found in Jisho for $clean", Toast.LENGTH_SHORT).show() 
                    } 
                } catch (e: Exception) { 
                } finally { 
                    isFetchingJishoSentence = false 
                } 
            } 
        } 
    } 

    val onGenerateAiSentence = { 
        val clean = kanji.trim() 
        if (clean.isBlank()) { 
            Toast.makeText(context, "Please enter a word first", Toast.LENGTH_SHORT).show() 
        } else { 
            val apiKey = prefs.geminiApiKey ?: "" 
            if (apiKey.isBlank()) { 
                Toast.makeText(context, "Gemini API key is required. Please set it in Reading tab settings.", Toast.LENGTH_LONG).show() 
            } else if (!isGeneratingAiSentence) { 
                isGeneratingAiSentence = true 
                scope.launch { 
                    try { 
                        val res = AiSentenceGenerator.generateSentence( 
                            apiKey = apiKey, 
                            model = prefs.geminiModel ?: "gemini-2.5-flash", 
                            word = clean, 
                            reading = reading, 
                            meaning = meaning, 
                            tag = cardTag 
                        ) 
                        res.getOrNull()?.let { aiRes -> 
                            sentence = aiRes.sentence 
                            sentenceMeaning = aiRes.sentenceMeaning 
                            sentenceFurigana = aiRes.sentenceFurigana 
                            if (aiRes.wordFurigana.isNotBlank()) { 
                                furigana = aiRes.wordFurigana 
                            } 
                        } ?: run { 
                            val err = res.exceptionOrNull()?.message ?: "Failed to generate AI sentence" 
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show() 
                        } 
                    } catch (e: Exception) { 
                        Toast.makeText(context, "AI error: ${e.message}", Toast.LENGTH_SHORT).show() 
                    } finally { 
                        isGeneratingAiSentence = false 
                    } 
                } 
            } 
        } 
    } 

    val onAutoTranslateSentence = { 
        val clean = sentence.trim() 
        if (clean.isNotBlank() && !isTranslatingSentence) { 
            isTranslatingSentence = true 
            scope.launch { 
                try { 
                    val translator = TranslatorService(context) 
                    val transRes = translator.translate(clean) 
                    transRes.getOrNull()?.let { 
                        if (it.translatedText.isNotBlank()) { 
                            sentenceMeaning = it.translatedText 
                        } 
                    } 
                } catch (e: Exception) { 
                } finally { 
                    isTranslatingSentence = false 
                } 
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

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity { 
                return Velocity(0f, available.y) 
            } 
        } 
    } 

    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = BlossomColors.BackgroundDeep, 
        windowInsets = WindowInsets(0), 
        dragHandle = { 
            Box( 
                modifier = Modifier 
                    .padding(top = 12.dp, bottom = 8.dp) 
                    .size(width = 44.dp, height = 4.dp) 
                    .clip(BlossomShapes.Pill) 
                    .background(BlossomColors.CardBorder) 
            ) 
        } 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .verticalScroll(rememberScrollState()) 
                .nestedScroll(noBounceNestedScroll) 
                .padding(horizontal = 20.dp, vertical = 6.dp) 
                .imePadding() 
                .navigationBarsPadding() 
                .padding(bottom = 16.dp), 
            verticalArrangement = Arrangement.spacedBy(14.dp) 
        ) { 
            Text( 
                text = if (initialWord == null) "Add Word to Library" else "Edit Library Word", 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = BlossomNunito, 
                color = BlossomColors.TextPrimary 
            ) 

            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(8.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                OutlinedTextField( 
                    value = kanji, 
                    onValueChange = { kanji = it }, 
                    label = { Text("Word / Kanji") }, 
                    singleLine = true, 
                    shape = RoundedCornerShape(12.dp), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedContainerColor = BlossomColors.SurfaceElevated, 
                        unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                        focusedBorderColor = BlossomColors.WarmAmber, 
                        unfocusedBorderColor = BlossomColors.CardBorder, 
                        cursorColor = BlossomColors.WarmAmber, 
                        focusedTextColor = BlossomColors.TextPrimary, 
                        unfocusedTextColor = BlossomColors.TextPrimary, 
                        focusedLabelColor = BlossomColors.WarmAmber, 
                        unfocusedLabelColor = BlossomColors.TextSecondary 
                    ), 
                    modifier = Modifier.weight(1f) 
                ) 

                Squircle3DButton( 
                    onClick = onAutoTranslateWord, 
                    enabled = kanji.isNotBlank() && !isTranslatingWord, 
                    shape = BlossomShapes.SquircleSmall, 
                    containerColor = BlossomColors.MatchaSage, 
                    bevelColor = Color(0xFF4A7C59), 
                    contentColor = Color.White, 
                    modifier = Modifier.height(56.dp), 
                    depth = 3.dp, 
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp) 
                ) { 
                    if (isTranslatingWord) { 
                        CircularProgressIndicator( 
                            modifier = Modifier 
                                .size(18.dp) 
                                .align(Alignment.Center), 
                            color = Color.White, 
                            strokeWidth = 2.dp 
                        ) 
                    } else { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center, 
                            modifier = Modifier.align(Alignment.Center) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Translate, 
                                contentDescription = "Auto Translate Word", 
                                tint = Color.White, 
                                modifier = Modifier.size(16.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(4.dp)) 
                            Text( 
                                text = "Auto", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White 
                            ) 
                        } 
                    } 
                } 
            } 

            OutlinedTextField( 
                value = reading, 
                onValueChange = { reading = it }, 
                label = { Text("Reading (Kana)") }, 
                singleLine = true, 
                shape = RoundedCornerShape(12.dp), 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedBorderColor = BlossomColors.WarmAmber, 
                    unfocusedBorderColor = BlossomColors.CardBorder, 
                    cursorColor = BlossomColors.WarmAmber, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedLabelColor = BlossomColors.WarmAmber, 
                    unfocusedLabelColor = BlossomColors.TextSecondary 
                ), 
                modifier = Modifier.fillMaxWidth() 
            ) 

            OutlinedTextField( 
                value = meaning, 
                onValueChange = { meaning = it }, 
                label = { Text("English Meaning") }, 
                singleLine = true, 
                shape = RoundedCornerShape(12.dp), 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedBorderColor = BlossomColors.WarmAmber, 
                    unfocusedBorderColor = BlossomColors.CardBorder, 
                    cursorColor = BlossomColors.WarmAmber, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedLabelColor = BlossomColors.WarmAmber, 
                    unfocusedLabelColor = BlossomColors.TextSecondary 
                ), 
                modifier = Modifier.fillMaxWidth() 
            ) 

            OutlinedTextField( 
                value = sentence, 
                onValueChange = { sentence = it }, 
                label = { Text("Example Sentence (Japanese)") }, 
                minLines = 2, 
                maxLines = 4, 
                shape = RoundedCornerShape(12.dp), 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedBorderColor = BlossomColors.WarmAmber, 
                    unfocusedBorderColor = BlossomColors.CardBorder, 
                    cursorColor = BlossomColors.WarmAmber, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedLabelColor = BlossomColors.WarmAmber, 
                    unfocusedLabelColor = BlossomColors.TextSecondary 
                ), 
                modifier = Modifier.fillMaxWidth() 
            ) 

            OutlinedTextField( 
                value = sentenceMeaning, 
                onValueChange = { sentenceMeaning = it }, 
                label = { Text("Sentence Translation") }, 
                minLines = 2, 
                maxLines = 4, 
                shape = RoundedCornerShape(12.dp), 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedBorderColor = BlossomColors.WarmAmber, 
                    unfocusedBorderColor = BlossomColors.CardBorder, 
                    cursorColor = BlossomColors.WarmAmber, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedLabelColor = BlossomColors.WarmAmber, 
                    unfocusedLabelColor = BlossomColors.TextSecondary 
                ), 
                modifier = Modifier.fillMaxWidth() 
            ) 

            Spacer(modifier = Modifier.height(4.dp)) 

            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(6.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Squircle3DButton( 
                    onClick = onFetchJishoSentence, 
                    enabled = kanji.isNotBlank() && !isFetchingJishoSentence, 
                    shape = BlossomShapes.SquircleSmall, 
                    containerColor = BlossomColors.MatchaSage, 
                    bevelColor = Color(0xFF4A7C59), 
                    contentColor = Color.White, 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(42.dp), 
                    depth = 3.dp, 
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp) 
                ) { 
                    if (isFetchingJishoSentence) { 
                        CircularProgressIndicator( 
                            modifier = Modifier 
                                .size(14.dp) 
                                .align(Alignment.Center), 
                            color = Color.White, 
                            strokeWidth = 2.dp 
                        ) 
                    } else { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center, 
                            modifier = Modifier.align(Alignment.Center) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Search, 
                                contentDescription = "Jisho", 
                                tint = Color.White, 
                                modifier = Modifier.size(13.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(3.dp)) 
                            Text( 
                                text = "Jisho", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White, 
                                maxLines = 1 
                            ) 
                        } 
                    } 
                } 

                Squircle3DButton( 
                    onClick = onGenerateAiSentence, 
                    enabled = kanji.isNotBlank() && !isGeneratingAiSentence, 
                    shape = BlossomShapes.SquircleSmall, 
                    containerColor = BlossomColors.WisteriaViolet, 
                    bevelColor = Color(0xFF6B4A9E), 
                    contentColor = Color.White, 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(42.dp), 
                    depth = 3.dp, 
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp) 
                ) { 
                    if (isGeneratingAiSentence) { 
                        CircularProgressIndicator( 
                            modifier = Modifier 
                                .size(14.dp) 
                                .align(Alignment.Center), 
                            color = Color.White, 
                            strokeWidth = 2.dp 
                        ) 
                    } else { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center, 
                            modifier = Modifier.align(Alignment.Center) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.AutoAwesome, 
                                contentDescription = "AI Gen", 
                                tint = Color.White, 
                                modifier = Modifier.size(13.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(3.dp)) 
                            Text( 
                                text = "AI Gen", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White, 
                                maxLines = 1 
                            ) 
                        } 
                    } 
                } 

                Squircle3DButton( 
                    onClick = onAutoTranslateSentence, 
                    enabled = sentence.isNotBlank() && !isTranslatingSentence, 
                    shape = BlossomShapes.SquircleSmall, 
                    containerColor = BlossomColors.SkyCyan, 
                    bevelColor = Color(0xFF007A99), 
                    contentColor = Color.White, 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(42.dp), 
                    depth = 3.dp, 
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp) 
                ) { 
                    if (isTranslatingSentence) { 
                        CircularProgressIndicator( 
                            modifier = Modifier 
                                .size(14.dp) 
                                .align(Alignment.Center), 
                            color = Color.White, 
                            strokeWidth = 2.dp 
                        ) 
                    } else { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center, 
                            modifier = Modifier.align(Alignment.Center) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Translate, 
                                contentDescription = "Translate", 
                                tint = Color.White, 
                                modifier = Modifier.size(13.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(3.dp)) 
                            Text( 
                                text = "Trans", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White, 
                                maxLines = 1 
                            ) 
                        } 
                    } 
                } 

                Squircle3DButton( 
                    onClick = onDismiss, 
                    shape = BlossomShapes.SquircleSmall, 
                    containerColor = BlossomColors.SurfaceElevated, 
                    bevelColor = BlossomColors.CardBorder, 
                    contentColor = BlossomColors.TextSecondary, 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(42.dp), 
                    depth = 3.dp, 
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp) 
                ) { 
                    Text( 
                        text = "Cancel", 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.TextSecondary, 
                        maxLines = 1, 
                        modifier = Modifier.align(Alignment.Center) 
                    ) 
                } 

                Squircle3DButton( 
                    onClick = { 
                        if (kanji.isNotBlank()) { 
                            val finalFurigana = if (furigana.isNotBlank()) { 
                                furigana 
                            } else if (reading.isNotBlank() && reading != kanji.trim()) { 
                                "${kanji.trim()}[${reading.trim()}]" 
                            } else { 
                                kanji.trim() 
                            } 
                            val word = initialWord?.copy( 
                                kanji = kanji.trim(), 
                                reading = reading.trim(), 
                                meaning = meaning.trim(), 
                                furigana = finalFurigana, 
                                sentence = sentence.trim(), 
                                sentenceMeaning = sentenceMeaning.trim(), 
                                sentenceFurigana = sentenceFurigana.trim() 
                            ) ?: BookmarkedWord( 
                                kanji = kanji.trim(), 
                                reading = reading.trim(), 
                                meaning = meaning.trim(), 
                                furigana = finalFurigana, 
                                sentence = sentence.trim(), 
                                sentenceMeaning = sentenceMeaning.trim(), 
                                sentenceFurigana = sentenceFurigana.trim() 
                            ) 
                            onSave(word) 
                        } 
                    }, 
                    enabled = kanji.isNotBlank(), 
                    shape = BlossomShapes.SquircleSmall, 
                    containerColor = BlossomColors.WarmAmber, 
                    bevelColor = Color(0xFFC07000), 
                    contentColor = Color.White, 
                    modifier = Modifier 
                        .weight(1.1f) 
                        .height(42.dp), 
                    depth = 3.dp, 
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp) 
                ) { 
                    Text( 
                        text = "Save", 
                        fontSize = 12.sp, 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold, 
                        maxLines = 1, 
                        modifier = Modifier.align(Alignment.Center) 
                    ) 
                } 
            } 
        } 
    } 
} 
 

