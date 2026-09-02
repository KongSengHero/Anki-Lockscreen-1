package com.ankilock.ui.study
    
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ai.AiServiceHelper
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.ForgedStory
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StoryWordItem
import com.ankilock.util.AudioPlayerHelper
import kotlinx.coroutines.launch
    
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ForgeStoryScreen( 
    ankiHelper: AnkiDroidHelper, 
    prefs: PreferencesManager, 
    audioPlayer: AudioPlayerHelper, 
    onOpenAiConfig: () -> Unit
) { 
    val scope = rememberCoroutineScope()
    
    var forgedStory by remember { mutableStateOf<ForgedStory?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var cardCountToFetch by remember { mutableIntStateOf(10) }
    var selectedGenre by remember { mutableStateOf(prefs.storyGenre) }
    var selectedLevel by remember { mutableStateOf(prefs.storyLevel) }
    var showEnglishTranslation by remember { mutableStateOf(false) }
    var selectedWordForDetail by remember { mutableStateOf<StoryWordItem?>(null) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    
    val genres = listOf("IT & Workplace", "Cyberpunk", "Daily Life", "Mystery", "Fantasy", "Business Meeting")
    val levels = listOf("Beginner (N5-N4)", "Intermediate (N3)", "Advanced (N2-N1)")
    
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    
    Column( 
        modifier = Modifier 
            .fillMaxSize() 
            .background(Color(0xFF020617)) 
            .verticalScroll(scrollState) 
            .padding(16.dp)
    ) { 
        Row( 
            modifier = Modifier.fillMaxWidth(), 
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.SpaceBetween
        ) { 
            Row( 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) { 
                Surface( 
                    shape = RoundedCornerShape(12.dp), 
                    color = Color(0xFFA855F7).copy(alpha = 0.15f), 
                    modifier = Modifier.size(40.dp)
                ) { 
                    Icon( 
                        Icons.AutoMirrored.Filled.MenuBook, 
                        contentDescription = null, 
                        tint = Color(0xFFA855F7), 
                        modifier = Modifier 
                            .padding(9.dp) 
                            .size(22.dp)
                    )
                }
                Column { 
                    Text( 
                        "Forge Story", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White
                    )
                    Text( 
                        "Contextual Reading & Quiz", 
                        fontSize = 12.sp, 
                        color = Color(0xFF94A3B8)
                    )
                }
            }
            
            if (forgedStory != null) { 
                OutlinedButton( 
                    onClick = { 
                        forgedStory = null
                        userAnswers.clear()
                    }, 
                    shape = RoundedCornerShape(12.dp), 
                    border = BorderStroke(1.dp, Color(0xFF475569))
                ) { 
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Story", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                }
            }
        }
        
        if (prefs.aiApiKey.isBlank()) { 
            Spacer(modifier = Modifier.height(12.dp))
            Surface( 
                onClick = onOpenAiConfig, 
                shape = RoundedCornerShape(14.dp), 
                color = Color(0xFF7C2D12).copy(alpha = 0.3f), 
                border = BorderStroke(1.dp, Color(0xFFEA580C)), 
                modifier = Modifier.fillMaxWidth()
            ) { 
                Row( 
                    modifier = Modifier.padding(12.dp), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) { 
                    Icon( 
                        Icons.Default.Key, 
                        contentDescription = null, 
                        tint = Color(0xFFFB923C), 
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) { 
                        Text( 
                            "AI Key Required to Forge Stories", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFFFFEDD5)
                        )
                        Text( 
                            "Tap to configure Gemini or OpenAI key", 
                            fontSize = 11.sp, 
                            color = Color(0xFFFDBA74)
                        )
                    }
                    Text( 
                        "Setup", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF38BDF8)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (forgedStory == null) { 
            Card( 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(20.dp), 
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), 
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) { 
                Column(modifier = Modifier.padding(18.dp)) { 
                    Text( 
                        "Select Vocabulary Pool", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) { 
                        listOf(5, 10, 15, 20).forEach { count -> 
                            FilterChip( 
                                selected = cardCountToFetch == count, 
                                onClick = { cardCountToFetch = count }, 
                                label = { Text("$count Cards", fontSize = 12.sp) }, 
                                colors = FilterChipDefaults.filterChipColors( 
                                    selectedContainerColor = Color(0xFF7E22CE), 
                                    selectedLabelColor = Color.White, 
                                    containerColor = Color(0xFF1E293B), 
                                    labelColor = Color(0xFF94A3B8)
                                ), 
                                border = FilterChipDefaults.filterChipBorder( 
                                    enabled = true, 
                                    selected = cardCountToFetch == count, 
                                    borderColor = if (cardCountToFetch == count) Color(0xFFA855F7) else Color(0xFF475569)
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Text( 
                        "Story Genre / Theme", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FlowRow( 
                        horizontalArrangement = Arrangement.spacedBy(6.dp), 
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) { 
                        genres.forEach { g -> 
                            FilterChip( 
                                selected = selectedGenre == g, 
                                onClick = { 
                                    selectedGenre = g
                                    prefs.storyGenre = g
                                }, 
                                label = { Text(g, fontSize = 12.sp) }, 
                                colors = FilterChipDefaults.filterChipColors( 
                                    selectedContainerColor = Color(0xFF0284C7), 
                                    selectedLabelColor = Color.White, 
                                    containerColor = Color(0xFF1E293B), 
                                    labelColor = Color(0xFF94A3B8)
                                ), 
                                border = FilterChipDefaults.filterChipBorder( 
                                    enabled = true, 
                                    selected = selectedGenre == g, 
                                    borderColor = if (selectedGenre == g) Color(0xFF38BDF8) else Color(0xFF475569)
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Text( 
                        "Difficulty Level", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) { 
                        levels.forEach { lvl -> 
                            FilterChip( 
                                selected = selectedLevel == lvl, 
                                onClick = { 
                                    selectedLevel = lvl
                                    prefs.storyLevel = lvl
                                }, 
                                label = { Text(lvl.substringBefore(" ("), fontSize = 12.sp) }, 
                                colors = FilterChipDefaults.filterChipColors( 
                                    selectedContainerColor = Color(0xFF059669), 
                                    selectedLabelColor = Color.White, 
                                    containerColor = Color(0xFF1E293B), 
                                    labelColor = Color(0xFF94A3B8)
                                ), 
                                border = FilterChipDefaults.filterChipBorder( 
                                    enabled = true, 
                                    selected = selectedLevel == lvl, 
                                    borderColor = if (selectedLevel == lvl) Color(0xFF10B981) else Color(0xFF475569)
                                )
                            )
                        }
                    }
                    
                    if (errorMessage != null) { 
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(errorMessage ?: "", fontSize = 12.sp, color = Color(0xFFEF4444))
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button( 
                        onClick = { 
                            if (prefs.aiApiKey.isBlank()) { 
                                onOpenAiConfig()
                                return@Button
                            }
                            isGenerating = true
                            errorMessage = null
                            scope.launch { 
                                val cards = mutableListOf<CardInfo>()
                                val selectedDeckIds = prefs.getSelectedDeckIdsAsLongs()
                                var lastNoteId: Long? = null
                                for (i in 0 until cardCountToFetch) { 
                                    val c = ankiHelper.getNextDueCard(selectedDeckIds, excludeNoteId = lastNoteId)
                                    if (c != null) { 
                                        cards.add(c)
                                        lastNoteId = c.noteId
                                    } else { 
                                        break
                                    }
                                }
                                
                                if (cards.isEmpty()) { 
                                    isGenerating = false
                                    errorMessage = "No due cards found in AnkiDroid to forge a story."
                                    return@launch
                                }
                                
                                val res = AiServiceHelper.forgeStory( 
                                    cards = cards, 
                                    genre = selectedGenre, 
                                    level = selectedLevel, 
                                    apiKey = prefs.aiApiKey, 
                                    provider = prefs.aiProvider, 
                                    model = prefs.aiModel
                                )
                                isGenerating = false
                                if (res.isSuccess) { 
                                    forgedStory = res.getOrThrow()
                                } else { 
                                    errorMessage = res.exceptionOrNull()?.message ?: "Failed to forge story."
                                }
                            }
                        }, 
                        enabled = !isGenerating, 
                        shape = RoundedCornerShape(14.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)), 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .height(52.dp)
                    ) { 
                        if (isGenerating) { 
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Forging Story with AI...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        } else { 
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Forge Story Now", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else { 
            val story = forgedStory!!
            
            Card( 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(20.dp), 
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), 
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) { 
                Column(modifier = Modifier.padding(18.dp)) { 
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) { 
                        Text( 
                            story.title, 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFFA855F7), 
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton( 
                            onClick = { 
                                val card = CardInfo( 
                                    noteId = 0L, 
                                    cardOrd = 0, 
                                    question = "", 
                                    answer = "", 
                                    deckName = "", 
                                    sentence = story.storyJapanese
                                )
                                audioPlayer.playSentence(card)
                            }, 
                            modifier = Modifier.size(36.dp)
                        ) { 
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Read Aloud", tint = Color(0xFF38BDF8))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Surface( 
                        shape = RoundedCornerShape(14.dp), 
                        color = Color(0xFF1E293B).copy(alpha = 0.7f), 
                        border = BorderStroke(1.dp, Color(0xFF334155)), 
                        modifier = Modifier.fillMaxWidth()
                    ) { 
                        Text( 
                            story.storyJapanese, 
                            fontSize = 16.sp, 
                            lineHeight = 28.sp, 
                            fontWeight = FontWeight.Normal, 
                            color = Color(0xFFF1F5F9), 
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    
                    if (story.targetWords.isNotEmpty()) { 
                        Spacer(modifier = Modifier.height(14.dp))
                        Text( 
                            "Embedded Target Words (Tap to inspect):", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow( 
                            horizontalArrangement = Arrangement.spacedBy(6.dp), 
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) { 
                            story.targetWords.forEach { w -> 
                                Surface( 
                                    onClick = { selectedWordForDetail = w }, 
                                    shape = RoundedCornerShape(8.dp), 
                                    color = Color(0xFF7E22CE).copy(alpha = 0.2f), 
                                    border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.5f))
                                ) { 
                                    Text( 
                                        "${w.kanji} (${w.reading})", 
                                        fontSize = 12.sp, 
                                        color = Color(0xFFE9D5FF), 
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    OutlinedButton( 
                        onClick = { showEnglishTranslation = !showEnglishTranslation }, 
                        shape = RoundedCornerShape(10.dp), 
                        border = BorderStroke(1.dp, Color(0xFF475569)), 
                        modifier = Modifier.fillMaxWidth()
                    ) { 
                        Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text( 
                            if (showEnglishTranslation) "Hide English Translation" else "Show English Translation", 
                            color = Color(0xFFE2E8F0), 
                            fontSize = 13.sp
                        )
                    }
                    
                    AnimatedVisibility(visible = showEnglishTranslation) { 
                        Column { 
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface( 
                                shape = RoundedCornerShape(12.dp), 
                                color = Color(0xFF0F172A), 
                                border = BorderStroke(1.dp, Color(0xFF334155)), 
                                modifier = Modifier.fillMaxWidth()
                            ) { 
                                Text( 
                                    story.storyEnglish, 
                                    fontSize = 14.sp, 
                                    lineHeight = 22.sp, 
                                    color = Color(0xFFCBD5E1), 
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            if (story.questions.isNotEmpty()) { 
                Spacer(modifier = Modifier.height(16.dp))
                
                Card( 
                    modifier = Modifier.fillMaxWidth(), 
                    shape = RoundedCornerShape(20.dp), 
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), 
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) { 
                    Column(modifier = Modifier.padding(18.dp)) { 
                        Text( 
                            "Reading Comprehension Quiz", 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text( 
                            "Test your understanding of the story and vocabulary", 
                            fontSize = 12.sp, 
                            color = Color(0xFF94A3B8)
                        )
                        
                        story.questions.forEachIndexed { qIdx, q -> 
                            Spacer(modifier = Modifier.height(16.dp))
                            Text( 
                                "${qIdx + 1}. ${q.questionText}", 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = Color(0xFFF1F5F9)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val selectedOpt = userAnswers[q.id]
                            val isAnswered = selectedOpt != null
                            
                            q.options.forEachIndexed { optIdx, optText -> 
                                val isSelected = selectedOpt == optIdx
                                val isCorrectOption = q.correctOptionIndex == optIdx
                                
                                val bgColor = when { 
                                    !isAnswered -> if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B)
                                    isCorrectOption -> Color(0xFF065F46)
                                    isSelected && !isCorrectOption -> Color(0xFF7F1D1D)
                                    else -> Color(0xFF1E293B).copy(alpha = 0.5f)
                                }
                                
                                val borderColor = when { 
                                    !isAnswered -> if (isSelected) Color(0xFF38BDF8) else Color(0xFF475569)
                                    isCorrectOption -> Color(0xFF10B981)
                                    isSelected && !isCorrectOption -> Color(0xFFEF4444)
                                    else -> Color(0xFF334155)
                                }
                                
                                Surface( 
                                    onClick = { 
                                        if (!isAnswered) { 
                                            userAnswers[q.id] = optIdx
                                        }
                                    }, 
                                    shape = RoundedCornerShape(10.dp), 
                                    color = bgColor, 
                                    border = BorderStroke(1.dp, borderColor), 
                                    modifier = Modifier 
                                        .fillMaxWidth() 
                                        .padding(vertical = 4.dp)
                                ) { 
                                    Row( 
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), 
                                        verticalAlignment = Alignment.CenterVertically
                                    ) { 
                                        Text( 
                                            optText, 
                                            fontSize = 13.sp, 
                                            color = Color.White, 
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isAnswered && isCorrectOption) { 
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                        } else if (isAnswered && isSelected && !isCorrectOption) { 
                                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                            
                            if (isAnswered && q.explanation.isNotBlank()) { 
                                Spacer(modifier = Modifier.height(6.dp))
                                Text( 
                                    q.explanation, 
                                    fontSize = 12.sp, 
                                    color = Color(0xFF94A3B8), 
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (selectedWordForDetail != null) { 
        val w = selectedWordForDetail!!
        ModalBottomSheet( 
            onDismissRequest = { selectedWordForDetail = null }, 
            sheetState = sheetState, 
            containerColor = Color(0xFF0F172A)
        ) { 
            Column( 
                modifier = Modifier 
                    .padding(24.dp) 
                    .fillMaxWidth()
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween
                ) { 
                    Column { 
                        Text( 
                            w.kanji, 
                            fontSize = 28.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White
                        )
                        Text( 
                            w.reading, 
                            fontSize = 16.sp, 
                            color = Color(0xFF38BDF8)
                        )
                    }
                    IconButton( 
                        onClick = { 
                            val card = CardInfo( 
                                noteId = 0L, 
                                cardOrd = 0, 
                                question = w.kanji, 
                                answer = w.meaning, 
                                deckName = "", 
                                kanji = w.kanji, 
                                kanjiFurigana = w.reading
                            )
                            audioPlayer.playWord(card)
                        }, 
                        modifier = Modifier.size(44.dp)
                    ) { 
                        Icon( 
                            Icons.AutoMirrored.Filled.VolumeUp, 
                            contentDescription = "Pronounce", 
                            tint = Color(0xFF38BDF8), 
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                Surface( 
                    shape = RoundedCornerShape(12.dp), 
                    color = Color(0xFF1E293B), 
                    modifier = Modifier.fillMaxWidth()
                ) { 
                    Column(modifier = Modifier.padding(14.dp)) { 
                        Text("English Meaning", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(w.meaning, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
