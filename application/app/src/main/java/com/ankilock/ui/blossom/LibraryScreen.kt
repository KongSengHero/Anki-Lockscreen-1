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
import androidx.compose.foundation.layout.padding 
import androidx.compose.foundation.layout.size 
import androidx.compose.foundation.layout.width 
import androidx.compose.foundation.lazy.LazyColumn 
import androidx.compose.foundation.lazy.items 
import androidx.compose.foundation.shape.CircleShape 
import androidx.compose.foundation.shape.RoundedCornerShape 
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.filled.Add 
import androidx.compose.material.icons.filled.Bookmark 
import androidx.compose.material.icons.filled.BookmarkBorder 
import androidx.compose.material.icons.filled.Close 
import androidx.compose.material.icons.filled.Delete 
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
import androidx.compose.ui.text.font.FontWeight 
import androidx.compose.ui.text.style.TextOverflow 
import androidx.compose.ui.unit.dp 
import androidx.compose.ui.unit.sp 
import androidx.compose.ui.window.Dialog 
import androidx.core.content.FileProvider 
import com.ankilock.anki.AnkiDroidHelper 
import com.ankilock.anki.AnkiPackageExporter 
import com.ankilock.data.BookmarkManager 
import com.ankilock.data.BookmarkedWord 
import com.ankilock.data.PreferencesManager 
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

    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) } 
    DisposableEffect(context) { 
        val tts = TextToSpeech(context) { status -> 
            if (status == TextToSpeech.SUCCESS) { 
                ttsInstance?.language = Locale.JAPANESE 
            } 
        } 
        ttsInstance = tts 
        onDispose { 
            tts.stop() 
            tts.shutdown() 
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

    Box( 
        modifier = Modifier 
            .fillMaxSize() 
            .padding(padding) 
    ) { 
        Column( 
            modifier = Modifier.fillMaxSize() 
        ) { 
            LibraryHeader( 
                wordCount = words.size, 
                onAddWord = { showAddWordDialog = true }, 
                onOpenSettings = { showDeckSettingsDialog = true } 
            ) 

            Spacer(modifier = Modifier.height(12.dp)) 

            LibrarySearchBar( 
                query = searchQuery, 
                onQueryChange = { searchQuery = it } 
            ) 

            Spacer(modifier = Modifier.height(12.dp)) 

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
                        bottom = 120.dp 
                    ), 
                    verticalArrangement = Arrangement.spacedBy(10.dp) 
                ) { 
                    items( 
                        items = filteredWords, 
                        key = { it.id } 
                    ) { word -> 
                        LibraryWordCard( 
                            word = word, 
                            onPlayAudio = { 
                                val toSpeak = word.reading.ifBlank { word.kanji } 
                                ttsInstance?.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, word.id) 
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
            wordCount = words.size, 
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
            onSyncToAnkiDroid = { 
                if (words.isEmpty()) { 
                    Toast.makeText(context, "No bookmarked words to sync", Toast.LENGTH_SHORT).show() 
                    return@LibraryBottomActionBar 
                } 
                if (!ankiHelper.hasApiPermission()) { 
                    Toast.makeText(context, "AnkiDroid permission required. Check Cards tab settings.", Toast.LENGTH_LONG).show() 
                    return@LibraryBottomActionBar 
                } 
                scope.launch { 
                    val count = withContext(Dispatchers.IO) { 
                        ankiHelper.addNotesToDeck(deckName, words.toList()) 
                    } 
                    if (count > 0) { 
                        Toast.makeText(context, "Successfully synced $count cards to AnkiDroid!", Toast.LENGTH_SHORT).show() 
                    } else { 
                        Toast.makeText(context, "Sync completed or cards already present", Toast.LENGTH_SHORT).show() 
                    } 
                } 
            }, 
            modifier = Modifier.align(Alignment.BottomCenter) 
        ) 
    } 

    if (showDeckSettingsDialog) { 
        DeckSettingsDialog( 
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
        AddOrEditWordDialog( 
            initialWord = null, 
            onDismiss = { showAddWordDialog = false }, 
            onSave = { newWord -> 
                BookmarkManager.addWord(newWord) 
                showAddWordDialog = false 
            } 
        ) 
    } 

    wordToEdit?.let { editTarget -> 
        AddOrEditWordDialog( 
            initialWord = editTarget, 
            onDismiss = { wordToEdit = null }, 
            onSave = { updatedWord -> 
                BookmarkManager.updateWord(updatedWord) 
                wordToEdit = null 
            } 
        ) 
    } 

    wordToDelete?.let { deleteTarget -> 
        AlertDialog( 
            onDismissRequest = { wordToDelete = null }, 
            title = { 
                Text( 
                    text = "Remove Bookmark", 
                    color = BlossomColors.TextPrimary, 
                    fontWeight = FontWeight.Bold 
                ) 
            }, 
            text = { 
                Text( 
                    text = "Remove '${deleteTarget.kanji}' from your Anki library?", 
                    color = BlossomColors.TextSecondary 
                ) 
            }, 
            confirmButton = { 
                Button( 
                    onClick = { 
                        BookmarkManager.removeWord(deleteTarget.kanji) 
                        wordToDelete = null 
                    }, 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = BlossomColors.CoralRed 
                    ) 
                ) { 
                    Text("Remove", color = Color.White) 
                } 
            }, 
            dismissButton = { 
                TextButton(onClick = { wordToDelete = null }) { 
                    Text("Cancel", color = BlossomColors.TextSecondary) 
                } 
            }, 
            containerColor = BlossomColors.SurfaceElevated, 
            shape = RoundedCornerShape(16.dp) 
        ) 
    } 
} 

@Composable 
private fun LibraryHeader( 
    wordCount: Int, 
    onAddWord: () -> Unit, 
    onOpenSettings: () -> Unit 
) { 
    Row( 
        modifier = Modifier 
            .fillMaxWidth() 
            .padding(horizontal = 16.dp, vertical = 6.dp), 
        horizontalArrangement = Arrangement.SpaceBetween, 
        verticalAlignment = Alignment.CenterVertically 
    ) { 
        Column { 
            Row(verticalAlignment = Alignment.CenterVertically) { 
                Text( 
                    text = "Blossom Library", 
                    fontSize = 22.sp, 
                    fontFamily = BlossomNunito, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = BlossomColors.TextPrimary 
                ) 
                Spacer(modifier = Modifier.width(8.dp)) 
                Surface( 
                    shape = RoundedCornerShape(10.dp), 
                    color = BlossomColors.WarmAmberContainer.copy(alpha = 0.45f) 
                ) { 
                    Text( 
                        text = "Kaishi 1.5k", 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.WarmAmber, 
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                    ) 
                } 
            } 
            Text( 
                text = "$wordCount words mined • Ready for Anki", 
                fontSize = 12.sp, 
                fontFamily = BlossomNunito, 
                fontWeight = FontWeight.Medium, 
                color = BlossomColors.TextSecondary 
            ) 
        } 

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { 
            IconButton( 
                onClick = onAddWord, 
                modifier = Modifier 
                    .size(38.dp) 
                    .clip(CircleShape) 
                    .background(BlossomColors.WarmAmber) 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Add, 
                    contentDescription = "Add Word", 
                    tint = Color.Black, 
                    modifier = Modifier.size(20.dp) 
                ) 
            } 

            IconButton( 
                onClick = onOpenSettings, 
                modifier = Modifier 
                    .size(38.dp) 
                    .clip(CircleShape) 
                    .background(BlossomColors.SurfaceElevated) 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Settings, 
                    contentDescription = "Deck Settings", 
                    tint = BlossomColors.TextPrimary, 
                    modifier = Modifier.size(18.dp) 
                ) 
            } 
        } 
    } 
} 

@Composable 
private fun LibrarySearchBar( 
    query: String, 
    onQueryChange: (String) -> Unit 
) { 
    OutlinedTextField( 
        value = query, 
        onValueChange = onQueryChange, 
        placeholder = { 
            Text( 
                text = "Search by kanji, reading, or meaning...", 
                fontSize = 13.sp, 
                color = BlossomColors.TextMuted 
            ) 
        }, 
        leadingIcon = { 
            Icon( 
                imageVector = Icons.Default.Search, 
                contentDescription = null, 
                tint = BlossomColors.WarmAmber, 
                modifier = Modifier.size(18.dp) 
            ) 
        }, 
        trailingIcon = { 
            if (query.isNotEmpty()) { 
                IconButton(onClick = { onQueryChange("") }) { 
                    Icon( 
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Clear", 
                        tint = BlossomColors.TextSecondary, 
                        modifier = Modifier.size(16.dp) 
                    ) 
                } 
            } 
        }, 
        singleLine = true, 
        shape = RoundedCornerShape(14.dp), 
        colors = OutlinedTextFieldDefaults.colors( 
            focusedBorderColor = BlossomColors.WarmAmber, 
            unfocusedBorderColor = BlossomColors.CardBorderSubtle, 
            focusedContainerColor = BlossomColors.SurfaceElevated.copy(alpha = 0.5f), 
            unfocusedContainerColor = BlossomColors.SurfaceElevated.copy(alpha = 0.35f), 
            focusedTextColor = BlossomColors.TextPrimary, 
            unfocusedTextColor = BlossomColors.TextPrimary 
        ), 
        modifier = Modifier 
            .fillMaxWidth() 
            .padding(horizontal = 16.dp) 
            .height(50.dp) 
    ) 
} 

@Composable 
private fun LibraryWordCard( 
    word: BookmarkedWord, 
    onPlayAudio: () -> Unit, 
    onEdit: () -> Unit, 
    onDelete: () -> Unit, 
    onJishoLookup: () -> Unit 
) { 
    Card( 
        shape = RoundedCornerShape(16.dp), 
        colors = CardDefaults.cardColors( 
            containerColor = BlossomColors.SurfaceElevated 
        ), 
        modifier = Modifier 
            .fillMaxWidth() 
            .border( 
                width = 1.dp, 
                color = BlossomColors.CardBorderSubtle, 
                shape = RoundedCornerShape(16.dp) 
            ) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(14.dp) 
        ) { 
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.weight(1f) 
                ) { 
                    Text( 
                        text = word.kanji, 
                        fontSize = 20.sp, 
                        fontFamily = BlossomNunito, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.TextPrimary 
                    ) 

                    if (word.reading.isNotBlank() && word.reading != word.kanji) { 
                        Spacer(modifier = Modifier.width(8.dp)) 
                        Surface( 
                            shape = RoundedCornerShape(8.dp), 
                            color = BlossomColors.WarmAmberContainer.copy(alpha = 0.35f) 
                        ) { 
                            Text( 
                                text = word.reading, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = BlossomColors.WarmAmber, 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                            ) 
                        } 
                    } 
                } 

                Row(verticalAlignment = Alignment.CenterVertically) { 
                    IconButton( 
                        onClick = onPlayAudio, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.VolumeUp, 
                            contentDescription = "Pronounce", 
                            tint = BlossomColors.WarmAmber, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 

                    IconButton( 
                        onClick = onJishoLookup, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Search, 
                            contentDescription = "Lookup Jisho", 
                            tint = BlossomColors.MatchaSage, 
                            modifier = Modifier.size(16.dp) 
                        ) 
                    } 

                    IconButton( 
                        onClick = onEdit, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Edit, 
                            contentDescription = "Edit", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(16.dp) 
                        ) 
                    } 

                    IconButton( 
                        onClick = onDelete, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Delete, 
                            contentDescription = "Delete", 
                            tint = BlossomColors.CoralRed.copy(alpha = 0.8f), 
                            modifier = Modifier.size(16.dp) 
                        ) 
                    } 
                } 
            } 

            if (word.meaning.isNotBlank()) { 
                Spacer(modifier = Modifier.height(4.dp)) 
                Text( 
                    text = word.meaning, 
                    fontSize = 14.sp, 
                    fontFamily = BlossomNunito, 
                    fontWeight = FontWeight.Medium, 
                    color = BlossomColors.SkyCyan, 
                    maxLines = 2, 
                    overflow = TextOverflow.Ellipsis 
                ) 
            } 

            if (word.sentence.isNotBlank()) { 
                Spacer(modifier = Modifier.height(8.dp)) 
                Surface( 
                    shape = RoundedCornerShape(10.dp), 
                    color = BlossomColors.BackgroundDeep.copy(alpha = 0.6f), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Column( 
                        modifier = Modifier.padding(10.dp) 
                    ) { 
                        Text( 
                            text = word.sentence.replace("<b>", "").replace("</b>", ""), 
                            fontSize = 13.sp, 
                            color = BlossomColors.TextPrimary, 
                            lineHeight = 18.sp 
                        ) 
                        if (word.sentenceMeaning.isNotBlank()) { 
                            Spacer(modifier = Modifier.height(3.dp)) 
                            Text( 
                                text = word.sentenceMeaning, 
                                fontSize = 11.sp, 
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
            .padding(32.dp), 
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
                    tint = Color.Black, 
                    modifier = Modifier.size(16.dp) 
                ) 
                Spacer(modifier = Modifier.width(6.dp)) 
                Text("Add First Word", color = Color.Black, fontWeight = FontWeight.Bold) 
            } 
        } 
    } 
} 

@Composable 
private fun LibraryBottomActionBar( 
    wordCount: Int, 
    isExporting: Boolean, 
    onBuildApkg: () -> Unit, 
    onSyncToAnkiDroid: () -> Unit, 
    modifier: Modifier = Modifier 
) { 
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .background( 
                Brush.verticalGradient( 
                    colors = listOf( 
                        Color.Transparent, 
                        BlossomColors.BackgroundDeep.copy(alpha = 0.80f), 
                        BlossomColors.BackgroundDeep.copy(alpha = 0.98f) 
                    ) 
                ) 
            ) 
            .padding(horizontal = 16.dp, vertical = 12.dp) 
    ) { 
        Row( 
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.spacedBy(10.dp) 
        ) { 
            Button( 
                onClick = onBuildApkg, 
                enabled = !isExporting, 
                shape = RoundedCornerShape(14.dp), 
                colors = ButtonDefaults.buttonColors( 
                    containerColor = BlossomColors.WarmAmber, 
                    disabledContainerColor = BlossomColors.WarmAmber.copy(alpha = 0.4f) 
                ), 
                modifier = Modifier 
                    .weight(1f) 
                    .height(48.dp) 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Bookmark, 
                    contentDescription = null, 
                    tint = Color.Black, 
                    modifier = Modifier.size(18.dp) 
                ) 
                Spacer(modifier = Modifier.width(6.dp)) 
                Text( 
                    text = if (isExporting) "Building..." else "BUILD .APKG", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Black 
                ) 
            } 

            Button( 
                onClick = onSyncToAnkiDroid, 
                shape = RoundedCornerShape(14.dp), 
                colors = ButtonDefaults.buttonColors( 
                    containerColor = BlossomColors.SurfaceElevated 
                ), 
                modifier = Modifier 
                    .weight(1f) 
                    .height(48.dp) 
                    .border(1.dp, BlossomColors.CardBorderSubtle, RoundedCornerShape(14.dp)) 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Sync, 
                    contentDescription = null, 
                    tint = BlossomColors.MatchaSage, 
                    modifier = Modifier.size(18.dp) 
                ) 
                Spacer(modifier = Modifier.width(6.dp)) 
                Text( 
                    text = "SYNC ANKIDROID", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = BlossomColors.TextPrimary 
                ) 
            } 
        } 
    } 
} 

@Composable 
private fun DeckSettingsDialog( 
    initialDeckName: String, 
    initialTag: String, 
    onDismiss: () -> Unit, 
    onSave: (String, String) -> Unit 
) { 
    var deckName by remember { mutableStateOf(initialDeckName) } 
    var tag by remember { mutableStateOf(initialTag) } 

    AlertDialog( 
        onDismissRequest = onDismiss, 
        title = { 
            Text( 
                text = "Deck Configuration", 
                color = BlossomColors.TextPrimary, 
                fontWeight = FontWeight.Bold 
            ) 
        }, 
        text = { 
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { 
                OutlinedTextField( 
                    value = deckName, 
                    onValueChange = { deckName = it }, 
                    label = { Text("Anki Deck Name") }, 
                    placeholder = { Text("e.g. Blossom::Vocabulary") }, 
                    singleLine = true, 
                    shape = RoundedCornerShape(12.dp), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedBorderColor = BlossomColors.WarmAmber, 
                        unfocusedBorderColor = BlossomColors.CardBorderSubtle 
                    ), 
                    modifier = Modifier.fillMaxWidth() 
                ) 

                OutlinedTextField( 
                    value = tag, 
                    onValueChange = { tag = it }, 
                    label = { Text("Card Tag") }, 
                    placeholder = { Text("e.g. Blossom") }, 
                    singleLine = true, 
                    shape = RoundedCornerShape(12.dp), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedBorderColor = BlossomColors.WarmAmber, 
                        unfocusedBorderColor = BlossomColors.CardBorderSubtle 
                    ), 
                    modifier = Modifier.fillMaxWidth() 
                ) 
            } 
        }, 
        confirmButton = { 
            Button( 
                onClick = { onSave(deckName.ifBlank { "Blossom::Vocabulary" }, tag.ifBlank { "Blossom" }) }, 
                colors = ButtonDefaults.buttonColors(containerColor = BlossomColors.WarmAmber) 
            ) { 
                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold) 
            } 
        }, 
        dismissButton = { 
            TextButton(onClick = onDismiss) { 
                Text("Cancel", color = BlossomColors.TextSecondary) 
            } 
        }, 
        containerColor = BlossomColors.SurfaceElevated, 
        shape = RoundedCornerShape(16.dp) 
    ) 
} 

@Composable 
private fun AddOrEditWordDialog( 
    initialWord: BookmarkedWord?, 
    onDismiss: () -> Unit, 
    onSave: (BookmarkedWord) -> Unit 
) { 
    var kanji by remember { mutableStateOf(initialWord?.kanji ?: "") } 
    var reading by remember { mutableStateOf(initialWord?.reading ?: "") } 
    var meaning by remember { mutableStateOf(initialWord?.meaning ?: "") } 
    var sentence by remember { mutableStateOf(initialWord?.sentence ?: "") } 
    var sentenceMeaning by remember { mutableStateOf(initialWord?.sentenceMeaning ?: "") } 

    Dialog(onDismissRequest = onDismiss) { 
        Surface( 
            shape = RoundedCornerShape(20.dp), 
            color = BlossomColors.SurfaceElevated, 
            modifier = Modifier.fillMaxWidth() 
        ) { 
            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(20.dp), 
                verticalArrangement = Arrangement.spacedBy(10.dp) 
            ) { 
                Text( 
                    text = if (initialWord == null) "Add Word to Library" else "Edit Library Word", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = BlossomColors.TextPrimary 
                ) 

                OutlinedTextField( 
                    value = kanji, 
                    onValueChange = { kanji = it }, 
                    label = { Text("Word / Kanji") }, 
                    singleLine = true, 
                    shape = RoundedCornerShape(10.dp), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedBorderColor = BlossomColors.WarmAmber, 
                        unfocusedBorderColor = BlossomColors.CardBorderSubtle 
                    ), 
                    modifier = Modifier.fillMaxWidth() 
                ) 

                OutlinedTextField( 
                    value = reading, 
                    onValueChange = { reading = it }, 
                    label = { Text("Reading (Kana)") }, 
                    singleLine = true, 
                    shape = RoundedCornerShape(10.dp), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedBorderColor = BlossomColors.WarmAmber, 
                        unfocusedBorderColor = BlossomColors.CardBorderSubtle 
                    ), 
                    modifier = Modifier.fillMaxWidth() 
                ) 

                OutlinedTextField( 
                    value = meaning, 
                    onValueChange = { meaning = it }, 
                    label = { Text("English Meaning") }, 
                    singleLine = true, 
                    shape = RoundedCornerShape(10.dp), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedBorderColor = BlossomColors.WarmAmber, 
                        unfocusedBorderColor = BlossomColors.CardBorderSubtle 
                    ), 
                    modifier = Modifier.fillMaxWidth() 
                ) 

                OutlinedTextField( 
                    value = sentence, 
                    onValueChange = { sentence = it }, 
                    label = { Text("Example Sentence (Japanese)") }, 
                    shape = RoundedCornerShape(10.dp), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedBorderColor = BlossomColors.WarmAmber, 
                        unfocusedBorderColor = BlossomColors.CardBorderSubtle 
                    ), 
                    modifier = Modifier.fillMaxWidth() 
                ) 

                OutlinedTextField( 
                    value = sentenceMeaning, 
                    onValueChange = { sentenceMeaning = it }, 
                    label = { Text("Sentence Translation") }, 
                    shape = RoundedCornerShape(10.dp), 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedBorderColor = BlossomColors.WarmAmber, 
                        unfocusedBorderColor = BlossomColors.CardBorderSubtle 
                    ), 
                    modifier = Modifier.fillMaxWidth() 
                ) 

                Spacer(modifier = Modifier.height(6.dp)) 

                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.End, 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    TextButton(onClick = onDismiss) { 
                        Text("Cancel", color = BlossomColors.TextSecondary) 
                    } 
                    Spacer(modifier = Modifier.width(8.dp)) 
                    Button( 
                        onClick = { 
                            if (kanji.isNotBlank()) { 
                                val word = initialWord?.copy( 
                                    kanji = kanji.trim(), 
                                    reading = reading.trim(), 
                                    meaning = meaning.trim(), 
                                    sentence = sentence.trim(), 
                                    sentenceMeaning = sentenceMeaning.trim() 
                                ) ?: BookmarkedWord( 
                                    kanji = kanji.trim(), 
                                    reading = reading.trim(), 
                                    meaning = meaning.trim(), 
                                    sentence = sentence.trim(), 
                                    sentenceMeaning = sentenceMeaning.trim() 
                                ) 
                                onSave(word) 
                            } 
                        }, 
                        enabled = kanji.isNotBlank(), 
                        colors = ButtonDefaults.buttonColors( 
                            containerColor = BlossomColors.WarmAmber, 
                            disabledContainerColor = BlossomColors.WarmAmber.copy(alpha = 0.4f) 
                        ), 
                        shape = RoundedCornerShape(12.dp) 
                    ) { 
                        Text("Save", color = Color.Black, fontWeight = FontWeight.Bold) 
                    } 
                } 
            } 
        } 
    } 
} 
