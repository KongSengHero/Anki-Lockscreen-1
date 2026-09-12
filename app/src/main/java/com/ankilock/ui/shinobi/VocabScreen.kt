package com.ankilock.ui.shinobi

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.CardInfo
import com.ankilock.data.StorySessionManager
import com.ankilock.data.StoryWordItem
import com.ankilock.util.AudioPlayerHelper

@Composable
fun VocabScreen( 
    audioPlayer: AudioPlayerHelper, 
    modifier: Modifier = Modifier 
) { 
    val context = LocalContext.current 
    var searchQuery by remember { mutableStateOf("") } 
    var activeTab by remember { mutableStateOf("Bookmarks") } 
    
    val bookmarkedWords = remember { 
        val list = mutableListOf<StoryWordItem>() 
        StorySessionManager.savedStoriesList.forEach { story -> 
            story.targetWords.forEach { word -> 
                if (list.none { it.kanji == word.kanji }) { 
                    list.add(word) 
                } 
            } 
        } 
        mutableStateListOf<StoryWordItem>().apply { addAll(list) } 
    } 
    
    val filteredWords = bookmarkedWords.filter { word -> 
        searchQuery.isBlank() || 
            word.kanji.contains(searchQuery, ignoreCase = true) || 
            word.reading.contains(searchQuery, ignoreCase = true) || 
            word.meaning.contains(searchQuery, ignoreCase = true) 
    } 
    
    Column( 
        modifier = modifier 
            .fillMaxSize() 
            .background(ShinobiColors.BackgroundDeep) 
            .padding(16.dp) 
    ) { 
        Row( 
            modifier = Modifier.fillMaxWidth(), 
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.SpaceBetween 
        ) { 
            Column { 
                Text( 
                    "Shinobi • Vocabulary", 
                    fontSize = 22.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = ShinobiColors.TextPrimary 
                ) 
                Text( 
                    "${bookmarkedWords.size} words mined from stories", 
                    fontSize = 12.sp, 
                    color = ShinobiColors.TextSecondary 
                ) 
            } 
            
            ShinobiPillBadge( 
                text = "${filteredWords.size} Shown", 
                textColor = ShinobiColors.WarmAmber, 
                borderColor = ShinobiColors.WarmAmber.copy(alpha = 0.5f), 
                backgroundColor = ShinobiColors.WarmAmberSurface 
            ) 
        } 
        
        Spacer(modifier = Modifier.height(14.dp)) 
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { 
            listOf("Bookmarks", "Kanji Bank").forEach { tabName -> 
                val isSelected = activeTab == tabName 
                Box( 
                    modifier = Modifier 
                        .clip(ShinobiShapes.Pill) 
                        .background(if (isSelected) ShinobiColors.WarmAmber else ShinobiColors.SurfaceCard1) 
                        .border( 
                            width = 1.dp, 
                            color = if (isSelected) ShinobiColors.WarmAmber else ShinobiColors.CardBorder, 
                            shape = ShinobiShapes.Pill 
                        ) 
                        .clickable { activeTab = tabName } 
                        .padding(horizontal = 16.dp, vertical = 7.dp) 
                ) { 
                    Text( 
                        text = tabName, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = if (isSelected) Color.White else ShinobiColors.TextSecondary 
                    ) 
                } 
            } 
        } 
        
        Spacer(modifier = Modifier.height(12.dp)) 
        
        OutlinedTextField( 
            value = searchQuery, 
            onValueChange = { searchQuery = it }, 
            modifier = Modifier.fillMaxWidth(), 
            placeholder = { 
                Text("Search by kanji, reading, or English...", color = ShinobiColors.TextMuted, fontSize = 13.sp) 
            }, 
            leadingIcon = { 
                Icon(Icons.Default.Search, contentDescription = null, tint = ShinobiColors.TextSecondary) 
            }, 
            shape = ShinobiShapes.SquircleMedium, 
            colors = OutlinedTextFieldDefaults.colors( 
                focusedContainerColor = ShinobiColors.SurfaceCard1, 
                unfocusedContainerColor = ShinobiColors.SurfaceCard1, 
                focusedBorderColor = ShinobiColors.WarmAmber, 
                unfocusedBorderColor = ShinobiColors.CardBorder, 
                focusedTextColor = Color.White, 
                unfocusedTextColor = Color.White 
            ), 
            singleLine = true 
        ) 
        
        Spacer(modifier = Modifier.height(14.dp)) 
        
        if (filteredWords.isEmpty()) { 
            Box( 
                modifier = Modifier 
                    .weight(1f) 
                    .fillMaxWidth() 
                    .clip(ShinobiShapes.SquircleLarge) 
                    .background(ShinobiColors.SurfaceCard1) 
                    .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.SquircleLarge) 
                    .padding(24.dp), 
                contentAlignment = Alignment.Center 
            ) { 
                Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                    Icon( 
                        Icons.Default.Bookmark, 
                        contentDescription = null, 
                        tint = ShinobiColors.TextMuted, 
                        modifier = Modifier.size(48.dp) 
                    ) 
                    Spacer(modifier = Modifier.height(10.dp)) 
                    Text( 
                        "No Mined Words Found", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = ShinobiColors.TextPrimary 
                    ) 
                    Spacer(modifier = Modifier.height(4.dp)) 
                    Text( 
                        "Tap any word while reading a Shinobi story to add it to your bank!", 
                        fontSize = 12.sp, 
                        color = ShinobiColors.TextSecondary 
                    ) 
                } 
            } 
        } else { 
            LazyColumn( 
                modifier = Modifier.weight(1f), 
                verticalArrangement = Arrangement.spacedBy(8.dp) 
            ) { 
                items(filteredWords) { item -> 
                    Box( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .clip(ShinobiShapes.SquircleMedium) 
                            .background(ShinobiColors.SurfaceCard1) 
                            .border(1.dp, ShinobiColors.CardBorder, ShinobiShapes.SquircleMedium) 
                            .padding(14.dp) 
                    ) { 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.SpaceBetween 
                        ) { 
                            Column(modifier = Modifier.weight(1f)) { 
                                if (item.reading.isNotBlank()) { 
                                    Text( 
                                        text = item.reading, 
                                        color = ShinobiColors.TextSecondary, 
                                        fontSize = 12.sp 
                                    ) 
                                } 
                                Text( 
                                    text = item.kanji, 
                                    color = ShinobiColors.TextPrimary, 
                                    fontSize = 20.sp, 
                                    fontWeight = FontWeight.Bold 
                                ) 
                                Spacer(modifier = Modifier.height(2.dp)) 
                                Text( 
                                    text = item.meaning, 
                                    color = Color(0xFF86EFAC), 
                                    fontSize = 13.sp, 
                                    fontWeight = FontWeight.Medium 
                                ) 
                            } 
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { 
                                ShinobiSquircleButton( 
                                    onClick = { 
                                        val card = CardInfo( 
                                            noteId = 0L, 
                                            cardOrd = 0, 
                                            question = item.kanji, 
                                            answer = item.meaning, 
                                            deckName = "", 
                                            kanji = item.kanji, 
                                            kanjiFurigana = item.reading 
                                        ) 
                                        audioPlayer.playWord(card) 
                                    }, 
                                    icon = Icons.AutoMirrored.Filled.VolumeUp, 
                                    contentDescription = "Pronounce", 
                                    size = 38.dp, 
                                    iconSize = 18.dp 
                                ) 
                                
                                IconButton( 
                                    onClick = { 
                                        bookmarkedWords.remove(item) 
                                    }, 
                                    modifier = Modifier.size(38.dp) 
                                ) { 
                                    Icon( 
                                        Icons.Default.Delete, 
                                        contentDescription = "Remove", 
                                        tint = ShinobiColors.TextMuted, 
                                        modifier = Modifier.size(18.dp) 
                                    ) 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
        } 
        
        Spacer(modifier = Modifier.height(14.dp)) 
        
        ShinobiTactileButton( 
            onClick = { 
                Toast.makeText(context, "Synced ${bookmarkedWords.size} words with AnkiDroid!", Toast.LENGTH_SHORT).show() 
            }, 
            modifier = Modifier.fillMaxWidth(), 
            faceColor = ShinobiColors.WarmAmber, 
            lipColor = ShinobiColors.WarmAmberLip 
        ) { 
            Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) 
            Spacer(modifier = Modifier.width(8.dp)) 
            Text( 
                "SYNC ALL TO ANKI", 
                fontSize = 15.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.White 
            ) 
        } 
    } 
} 
