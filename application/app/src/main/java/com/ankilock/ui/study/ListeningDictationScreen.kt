package com.ankilock.ui.study
    
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ai.AiServiceHelper
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.CardSessionManager
import com.ankilock.data.ListeningEvaluationResult
import com.ankilock.data.PreferencesManager
import com.ankilock.util.AudioPlayerHelper
import com.ankilock.util.AudioTrackPlaying
import kotlinx.coroutines.launch
    
@Composable
fun ListeningDictationScreen( 
    ankiHelper: AnkiDroidHelper, 
    prefs: PreferencesManager, 
    audioPlayer: AudioPlayerHelper, 
    onOpenAiConfig: () -> Unit
) { 
    val scope = rememberCoroutineScope()
    
    var activeCard by remember { mutableStateOf<CardInfo?>(null) }
    var wordTranslationInput by remember { mutableStateOf("") }
    var sentenceTranslationInput by remember { mutableStateOf("") }
    var isEvaluating by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<ListeningEvaluationResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var streakCount by remember { mutableIntStateOf(0) }
    var reviewedCount by remember { mutableIntStateOf(0) }
    
    fun loadNextCard() { 
        audioPlayer.stop()
        val next = ankiHelper.getNextDueCard(prefs.getSelectedDeckIdsAsLongs(), excludeNoteId = activeCard?.noteId) 
            ?: ankiHelper.getNextDueCard(prefs.getSelectedDeckIdsAsLongs())
        activeCard = next
        wordTranslationInput = ""
        sentenceTranslationInput = ""
        evaluationResult = null
        errorMessage = null
        if (next != null && prefs.isAutoPlayAudio) { 
            audioPlayer.playSequence(next)
        }
    }
    
    LaunchedEffect(Unit) { 
        loadNextCard()
    }
    
    val scrollState = rememberScrollState()
    
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
                    color = Color(0xFF38BDF8).copy(alpha = 0.15f), 
                    modifier = Modifier.size(40.dp)
                ) { 
                    Icon( 
                        Icons.Default.Headphones, 
                        contentDescription = null, 
                        tint = Color(0xFF38BDF8), 
                        modifier = Modifier 
                            .padding(9.dp) 
                            .size(22.dp)
                    )
                }
                Column { 
                    Text( 
                        "Listening Quiz", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White
                    )
                    Text( 
                        "Active Audio Recall", 
                        fontSize = 12.sp, 
                        color = Color(0xFF94A3B8)
                    )
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { 
                Surface( 
                    shape = RoundedCornerShape(20.dp), 
                    color = Color(0xFF1E293B), 
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) { 
                    Text( 
                        "🔥 $streakCount Streak", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFFF59E0B), 
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
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
                            "AI Key Required for Evaluation", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFFFFEDD5)
                        )
                        Text( 
                            "Tap to enter free Gemini or OpenAI key", 
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
        
        if (activeCard == null) { 
            Card( 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(20.dp), 
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), 
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(32.dp), 
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { 
                    Text( 
                        "🎉 No Cards Due!", 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text( 
                        "You've completed all listening cards for now.", 
                        fontSize = 13.sp, 
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button( 
                        onClick = { loadNextCard() }, 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) { 
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Refresh")
                    }
                }
            }
        } else { 
            val card = activeCard!!
            val isPlayingWord = audioPlayer.currentPlayingTrack == AudioTrackPlaying.WORD
            val isPlayingSentence = audioPlayer.currentPlayingTrack == AudioTrackPlaying.SENTENCE
            
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
                        Surface( 
                            shape = RoundedCornerShape(8.dp), 
                            color = Color(0xFF334155).copy(alpha = 0.6f)
                        ) { 
                            Text( 
                                card.deckName.ifBlank { "Anki Deck" }, 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = Color(0xFFCBD5E1), 
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        
                        IconButton( 
                            onClick = { audioPlayer.playSequence(card) }, 
                            modifier = Modifier.size(32.dp)
                        ) { 
                            Icon( 
                                Icons.Default.PlayArrow, 
                                contentDescription = "Play Both", 
                                tint = Color(0xFF38BDF8)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row( 
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) { 
                        Button( 
                            onClick = { audioPlayer.playWord(card) }, 
                            shape = RoundedCornerShape(14.dp), 
                            colors = ButtonDefaults.buttonColors( 
                                containerColor = if (isPlayingWord) Color(0xFF0284C7) else Color(0xFF1E293B)
                            ), 
                            border = BorderStroke( 
                                1.dp, 
                                if (isPlayingWord) Color(0xFFBAE6FD) else Color(0xFF475569)
                            ), 
                            modifier = Modifier 
                                .weight(1f) 
                                .height(52.dp)
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) { 
                                Icon( 
                                    Icons.AutoMirrored.Filled.VolumeUp, 
                                    contentDescription = null, 
                                    tint = if (isPlayingWord) Color.White else Color(0xFF38BDF8), 
                                    modifier = Modifier.size(18.dp)
                                )
                                Text( 
                                    if (isPlayingWord) "Playing..." else "Play Word", 
                                    fontSize = 13.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = if (isPlayingWord) Color.White else Color(0xFFE2E8F0)
                                )
                            }
                        }
                        
                        Button( 
                            onClick = { audioPlayer.playSentence(card) }, 
                            shape = RoundedCornerShape(14.dp), 
                            colors = ButtonDefaults.buttonColors( 
                                containerColor = if (isPlayingSentence) Color(0xFF0284C7) else Color(0xFF1E293B)
                            ), 
                            border = BorderStroke( 
                                1.dp, 
                                if (isPlayingSentence) Color(0xFFBAE6FD) else Color(0xFF475569)
                            ), 
                            modifier = Modifier 
                                .weight(1f) 
                                .height(52.dp)
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) { 
                                Icon( 
                                    Icons.AutoMirrored.Filled.VolumeUp, 
                                    contentDescription = null, 
                                    tint = if (isPlayingSentence) Color.White else Color(0xFF38BDF8), 
                                    modifier = Modifier.size(18.dp)
                                )
                                Text( 
                                    if (isPlayingSentence) "Playing..." else "Play Sentence", 
                                    fontSize = 13.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = if (isPlayingSentence) Color.White else Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card( 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(20.dp), 
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), 
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) { 
                Column(modifier = Modifier.padding(18.dp)) { 
                    Text( 
                        "1. Word Translation", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField( 
                        value = wordTranslationInput, 
                        onValueChange = { wordTranslationInput = it }, 
                        placeholder = { Text("Enter English meaning of the word", color = Color(0xFF64748B), fontSize = 13.sp) }, 
                        colors = OutlinedTextFieldDefaults.colors( 
                            focusedContainerColor = Color(0xFF1E293B), 
                            unfocusedContainerColor = Color(0xFF1E293B), 
                            focusedTextColor = Color.White, 
                            unfocusedTextColor = Color.White, 
                            focusedBorderColor = Color(0xFF38BDF8), 
                            unfocusedBorderColor = Color(0xFF475569)
                        ), 
                        shape = RoundedCornerShape(12.dp), 
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Text( 
                        "2. Sentence Translation", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField( 
                        value = sentenceTranslationInput, 
                        onValueChange = { sentenceTranslationInput = it }, 
                        placeholder = { Text("Enter English translation of the sentence", color = Color(0xFF64748B), fontSize = 13.sp) }, 
                        colors = OutlinedTextFieldDefaults.colors( 
                            focusedContainerColor = Color(0xFF1E293B), 
                            unfocusedContainerColor = Color(0xFF1E293B), 
                            focusedTextColor = Color.White, 
                            unfocusedTextColor = Color.White, 
                            focusedBorderColor = Color(0xFF38BDF8), 
                            unfocusedBorderColor = Color(0xFF475569)
                        ), 
                        shape = RoundedCornerShape(12.dp), 
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (errorMessage != null) { 
                        Spacer(modifier = Modifier.height(10.dp))
                        Text( 
                            errorMessage ?: "", 
                            fontSize = 12.sp, 
                            color = Color(0xFFEF4444)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    if (evaluationResult == null) { 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) { 
                            OutlinedButton( 
                                onClick = { loadNextCard() }, 
                                shape = RoundedCornerShape(12.dp), 
                                border = BorderStroke(1.dp, Color(0xFF475569)), 
                                modifier = Modifier.weight(1f)
                            ) { 
                                Text("Skip", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            }
                            
                            Button( 
                                onClick = { 
                                    if (prefs.aiApiKey.isBlank()) { 
                                        onOpenAiConfig()
                                        return@Button
                                    }
                                    if (wordTranslationInput.isBlank() && sentenceTranslationInput.isBlank()) { 
                                        errorMessage = "Please enter at least one translation to check."
                                        return@Button
                                    }
                                    isEvaluating = true
                                    errorMessage = null
                                    scope.launch { 
                                        val res = AiServiceHelper.evaluateListening( 
                                            card = card, 
                                            userWordTranslation = wordTranslationInput, 
                                            userSentenceTranslation = sentenceTranslationInput, 
                                            apiKey = prefs.aiApiKey, 
                                            provider = prefs.aiProvider, 
                                            model = prefs.aiModel
                                        )
                                        isEvaluating = false
                                        if (res.isSuccess) { 
                                            val eval = res.getOrThrow()
                                            evaluationResult = eval
                                            if (eval.isOverallPass) { 
                                                streakCount++
                                                reviewedCount++
                                                ankiHelper.answerCard(card.noteId, card.cardOrd, 3, 5000L)
                                            } else { 
                                                streakCount = 0
                                                reviewedCount++
                                                ankiHelper.answerCard(card.noteId, card.cardOrd, 1, 5000L)
                                            }
                                        } else { 
                                            errorMessage = res.exceptionOrNull()?.message ?: "AI Evaluation failed"
                                        }
                                    }
                                }, 
                                enabled = !isEvaluating, 
                                shape = RoundedCornerShape(12.dp), 
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)), 
                                modifier = Modifier.weight(2f)
                            ) { 
                                if (isEvaluating) { 
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("AI Checking...", fontSize = 13.sp)
                                } else { 
                                    Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Check Translations", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            
            AnimatedVisibility( 
                visible = evaluationResult != null, 
                enter = fadeIn() + slideInVertically()
            ) { 
                if (evaluationResult != null) { 
                    val eval = evaluationResult!!
                    val isPass = eval.isOverallPass
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card( 
                        modifier = Modifier.fillMaxWidth(), 
                        shape = RoundedCornerShape(20.dp), 
                        colors = CardDefaults.cardColors( 
                            containerColor = if (isPass) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFF7F1D1D).copy(alpha = 0.5f)
                        ), 
                        border = BorderStroke(1.dp, if (isPass) Color(0xFF10B981) else Color(0xFFEF4444))
                    ) { 
                        Column(modifier = Modifier.padding(18.dp)) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) { 
                                Surface( 
                                    shape = CircleShape, 
                                    color = if (isPass) Color(0xFF10B981) else Color(0xFFEF4444), 
                                    modifier = Modifier.size(28.dp)
                                ) { 
                                    Icon( 
                                        if (isPass) Icons.Default.Check else Icons.Default.Close, 
                                        contentDescription = null, 
                                        tint = Color.White, 
                                        modifier = Modifier.padding(5.dp)
                                    )
                                }
                                Column { 
                                    Text( 
                                        if (isPass) "Passed! (Marked Good)" else "Needs Practice (Marked Again)", 
                                        fontSize = 16.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = if (isPass) Color(0xFFA7F3D0) else Color(0xFFFECACA)
                                    )
                                    Text( 
                                        "Automatically synced to Anki", 
                                        fontSize = 11.sp, 
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                            
                            if (eval.feedback.isNotBlank()) { 
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface( 
                                    shape = RoundedCornerShape(10.dp), 
                                    color = Color(0xFF0F172A).copy(alpha = 0.7f), 
                                    modifier = Modifier.fillMaxWidth()
                                ) { 
                                    Text( 
                                        eval.feedback, 
                                        fontSize = 13.sp, 
                                        color = Color(0xFFE2E8F0), 
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            Text( 
                                "Revealed Japanese:", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text( 
                                card.kanji.ifBlank { card.question }, 
                                fontSize = 20.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFF38BDF8)
                            )
                            if (card.kanjiFurigana.isNotBlank() && card.kanjiFurigana != card.kanji) { 
                                Text( 
                                    card.kanjiFurigana, 
                                    fontSize = 13.sp, 
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text( 
                                "Meaning: " + eval.correctWordMeaning, 
                                fontSize = 13.sp, 
                                color = Color(0xFFCBD5E1)
                            )
                            
                            if (card.sentence.isNotBlank() || card.sentenceFurigana.isNotBlank()) { 
                                Spacer(modifier = Modifier.height(10.dp))
                                Text( 
                                    card.sentence.ifBlank { card.sentenceFurigana }, 
                                    fontSize = 15.sp, 
                                    fontWeight = FontWeight.Medium, 
                                    color = Color.White
                                )
                                Text( 
                                    "Meaning: " + eval.correctSentenceMeaning, 
                                    fontSize = 13.sp, 
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button( 
                                onClick = { loadNextCard() }, 
                                shape = RoundedCornerShape(12.dp), 
                                colors = ButtonDefaults.buttonColors( 
                                    containerColor = if (isPass) Color(0xFF059669) else Color(0xFFDC2626)
                                ), 
                                modifier = Modifier.fillMaxWidth()
                            ) { 
                                Text("Next Card", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
