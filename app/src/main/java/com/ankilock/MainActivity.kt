package com.ankilock
    
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.DeckInfo
import com.ankilock.data.PreferencesManager
import com.ankilock.service.AnkiNotificationService
import com.ankilock.ui.theme.AnkiLockTheme
import com.ankilock.widget.AnkiAppWidgetProvider
import com.ankilock.worker.DueCountWorker
    
class MainActivity : ComponentActivity() { 
    
    private lateinit var ankiHelper: AnkiDroidHelper
    private lateinit var prefs: PreferencesManager
    
    private var isAnkiInstalledState by mutableStateOf(false)
    private var hasPermissionState by mutableStateOf(false)
    private var decksState by mutableStateOf<List<DeckInfo>>(emptyList())
    private var previewCardState by mutableStateOf<CardInfo?>(null)
    private var backgroundTypeState by mutableStateOf("transparent")
    private var customImageUriState by mutableStateOf<String?>(null)
    private var savedImageUrisState by mutableStateOf<Set<String>>(emptySet())
    private var blurRadiusState by mutableFloatStateOf(25f)
    private var dimOpacityState by mutableFloatStateOf(0.45f)
    private var artworkOpacityState by mutableFloatStateOf(1.0f)
    
    private val ankiPermissionLauncher = registerForActivityResult( 
        ActivityResultContracts.RequestPermission()
    ) { granted -> 
        hasPermissionState = granted
        if (granted) { 
            refreshData()
            if (prefs.isServiceEnabled) { 
                AnkiNotificationService.update(this)
            }
        }
    }
    
    private val notificationPermissionLauncher = registerForActivityResult( 
        ActivityResultContracts.RequestPermission()
    ) { granted -> 
        if (granted && prefs.isServiceEnabled) { 
            AnkiNotificationService.start(this)
        }
        checkAndRequestAnkiPermission()
    }
    
    private val imagePickerLauncher = registerForActivityResult( 
        ActivityResultContracts.GetContent()
    ) { uri -> 
        if (uri != null) { 
            try { 
                contentResolver.takePersistableUriPermission( 
                    uri, 
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) { 
            }
            val uriStr = uri.toString()
            prefs.addSavedImageUri(uriStr)
            prefs.customImageUri = uriStr
            prefs.backgroundType = "custom"
            
            savedImageUrisState = prefs.savedImageUris
            customImageUriState = uriStr
            backgroundTypeState = "custom"
            
            if (prefs.isServiceEnabled) { 
                AnkiNotificationService.update(this)
            }
            AnkiAppWidgetProvider.updateAllWidgets(this)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState)
        
        ankiHelper = AnkiDroidHelper(this)
        prefs = PreferencesManager(this)
        
        backgroundTypeState = prefs.backgroundType
        customImageUriState = prefs.customImageUri
        savedImageUrisState = prefs.savedImageUris
        blurRadiusState = prefs.blurRadius.toFloat()
        dimOpacityState = prefs.dimOpacity
        artworkOpacityState = prefs.artworkOpacity
        
        requestInitialPermissions()
        
        setContent { 
            AnkiLockTheme { 
                MainContainer()
            }
        }
    }
    
    override fun onResume() { 
        super.onResume()
        refreshData()
    }
    
    private fun refreshData() { 
        isAnkiInstalledState = ankiHelper.isAnkiDroidInstalled()
        hasPermissionState = ankiHelper.hasApiPermission()
        if (hasPermissionState) { 
            decksState = ankiHelper.getDeckList()
            previewCardState = ankiHelper.getNextDueCard()
        }
        backgroundTypeState = prefs.backgroundType
        customImageUriState = prefs.customImageUri
        savedImageUrisState = prefs.savedImageUris
        blurRadiusState = prefs.blurRadius.toFloat()
        dimOpacityState = prefs.dimOpacity
        artworkOpacityState = prefs.artworkOpacity
    }
    
    private fun requestInitialPermissions() { 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { 
            if (ContextCompat.checkSelfPermission( 
                    this, 
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) { 
                notificationPermissionLauncher.launch( 
                    Manifest.permission.POST_NOTIFICATIONS
                )
                return
            }
        }
        checkAndRequestAnkiPermission()
    }
    
    private fun checkAndRequestAnkiPermission() { 
        if (!ankiHelper.hasApiPermission()) { 
            ankiPermissionLauncher.launch( 
                AnkiDroidHelper.PERMISSION_READ_WRITE_DATABASE
            )
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainContainer() { 
        Scaffold( 
            topBar = { 
                TopAppBar( 
                    title = { 
                        Row(verticalAlignment = Alignment.CenterVertically) { 
                            Box( 
                                modifier = Modifier 
                                    .size(10.dp) 
                                    .clip(CircleShape) 
                                    .background( 
                                        if (prefs.isServiceEnabled) Color(0xFF10B981) 
                                        else Color(0xFFEF4444)
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text( 
                                "AnkiLock", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 20.sp
                            )
                        }
                    }, 
                    colors = TopAppBarDefaults.topAppBarColors( 
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding -> 
            ModernSettingsScreen(padding)
        }
    }
    
    @Composable
    fun ModernSettingsScreen(padding: PaddingValues) { 
        var isEnabled by remember { mutableStateOf(prefs.isServiceEnabled) }
        var isMusicPlayerStyle by remember { mutableStateOf(prefs.isMusicPlayerStyle) }
        val selectedDeckIds = remember { mutableStateListOf<String>() }
        var updateInterval by remember { mutableIntStateOf(prefs.updateIntervalMinutes) }
        var snoozeDuration by remember { mutableIntStateOf(prefs.snoozeDurationMinutes) }
        var isPreviewRevealed by remember { mutableStateOf(false) }
        
        remember { 
            selectedDeckIds.clear()
            selectedDeckIds.addAll(prefs.selectedDeckIds)
            true
        }
        
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .padding(padding) 
                .verticalScroll(rememberScrollState()) 
                .padding(16.dp), 
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) { 
            ModernHeroCard( 
                isEnabled = isEnabled, 
                onToggle = { enabled -> 
                    isEnabled = enabled
                    prefs.isServiceEnabled = enabled
                    if (enabled) { 
                        AnkiNotificationService.start(this@MainActivity)
                        DueCountWorker.schedule(this@MainActivity, updateInterval.toLong())
                    } else { 
                        AnkiNotificationService.stop(this@MainActivity)
                        DueCountWorker.cancel(this@MainActivity)
                    }
                    AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity)
                }
            )
            
            if (previewCardState != null) { 
                ModernPreviewCard( 
                    card = previewCardState!!, 
                    isRevealed = isPreviewRevealed, 
                    onToggleReveal = { isPreviewRevealed = !isPreviewRevealed }, 
                    onRefresh = { refreshData() }, 
                    onAgain = { 
                        val card = previewCardState
                        if (card != null) { 
                            Thread { 
                                ankiHelper.answerCard(card.noteId, card.cardOrd, 1, 5000L)
                                previewCardState = ankiHelper.getNextDueCard(excludeNoteId = card.noteId)
                                isPreviewRevealed = false
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                                AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity)
                            }.start()
                        }
                    }, 
                    onGood = { 
                        val card = previewCardState
                        if (card != null) { 
                            Thread { 
                                ankiHelper.answerCard(card.noteId, card.cardOrd, 3, 5000L)
                                previewCardState = ankiHelper.getNextDueCard(excludeNoteId = card.noteId)
                                isPreviewRevealed = false
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                                AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity)
                            }.start()
                        }
                    }, 
                    onOpenAnki = { 
                        val launchIntent = packageManager.getLaunchIntentForPackage("com.ichi2.anki")
                        if (launchIntent != null) startActivity(launchIntent)
                    }
                )
            }
            
            ModernStyleCard(isMusicPlayerStyle) { isMusic -> 
                isMusicPlayerStyle = isMusic
                prefs.isMusicPlayerStyle = isMusic
                if (isEnabled) AnkiNotificationService.update(this@MainActivity)
            }
            
            if (isMusicPlayerStyle) { 
                ModernBackgroundStudioCard( 
                    currentType = backgroundTypeState, 
                    blurRadius = blurRadiusState, 
                    dimOpacity = dimOpacityState, 
                    artworkOpacity = artworkOpacityState, 
                    savedUris = savedImageUrisState, 
                    currentUri = customImageUriState, 
                    onSelectType = { type -> 
                        backgroundTypeState = type
                        prefs.backgroundType = type
                        if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                    }, 
                    onSelectSavedUri = { uriStr -> 
                        customImageUriState = uriStr
                        prefs.customImageUri = uriStr
                        backgroundTypeState = "custom"
                        prefs.backgroundType = "custom"
                        if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                    }, 
                    onRemoveSavedUri = { uriStr -> 
                        prefs.removeSavedImageUri(uriStr)
                        savedImageUrisState = prefs.savedImageUris
                        customImageUriState = prefs.customImageUri
                        backgroundTypeState = prefs.backgroundType
                        if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                    }, 
                    onPickNewImage = { 
                        imagePickerLauncher.launch("image/*")
                    }, 
                    onBlurChange = { newRadius -> 
                        blurRadiusState = newRadius
                        prefs.blurRadius = newRadius.toInt()
                        if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                    }, 
                    onOpacityChange = { newOpacity -> 
                        dimOpacityState = newOpacity
                        prefs.dimOpacity = newOpacity
                        if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                    }, 
                    onArtworkOpacityChange = { newArtOpacity -> 
                        artworkOpacityState = newArtOpacity
                        prefs.artworkOpacity = newArtOpacity
                        if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                    }
                )
            }
            
            if (decksState.isNotEmpty()) { 
                ModernDeckSelectorCard(decksState, selectedDeckIds) { deckId, checked -> 
                    if (checked) { 
                        selectedDeckIds.add(deckId)
                    } else { 
                        selectedDeckIds.remove(deckId)
                    }
                    prefs.selectedDeckIds = selectedDeckIds.toSet()
                    if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                    AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity)
                }
            }
            
            ModernIntervalCard(updateInterval, snoozeDuration, 
                onUpdateSelect = { minutes -> 
                    updateInterval = minutes
                    prefs.updateIntervalMinutes = minutes
                    if (isEnabled) DueCountWorker.schedule(this@MainActivity, minutes.toLong())
                }, 
                onSnoozeSelect = { minutes -> 
                    snoozeDuration = minutes
                    prefs.snoozeDurationMinutes = minutes
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    @Composable
    fun ModernHeroCard(isEnabled: Boolean, onToggle: (Boolean) -> Unit) { 
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(24.dp), 
            colors = CardDefaults.cardColors( 
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ), 
            border = BorderStroke( 
                1.dp, 
                if (isEnabled) Color(0xFF38BDF8).copy(alpha = 0.4f) 
                else Color.White.copy(alpha = 0.1f)
            )
        ) { 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .background( 
                        Brush.linearGradient( 
                            colors = if (isEnabled) listOf( 
                                Color(0xFF0F172A), 
                                Color(0xFF1E293B), 
                                Color(0xFF0C4A6E).copy(alpha = 0.4f)
                            ) else listOf( 
                                Color(0xFF1E1E2E), 
                                Color(0xFF181825)
                            )
                        )
                    ) 
                    .padding(20.dp)
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.fillMaxWidth()
                ) { 
                    Column(modifier = Modifier.weight(1f)) { 
                        Text( 
                            if (isEnabled) "Lockscreen Active" else "Lockscreen Inactive", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 18.sp, 
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text( 
                            if (isEnabled) "Review cards on your lockscreen" 
                            else "Toggle switch to start practicing", 
                            fontSize = 12.sp, 
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Switch( 
                        checked = isEnabled, 
                        onCheckedChange = onToggle, 
                        colors = SwitchDefaults.colors( 
                            checkedTrackColor = Color(0xFF38BDF8), 
                            checkedThumbColor = Color.White
                        )
                    )
                }
            }
        }
    }
    
    @Composable
    fun ModernPreviewCard( 
        card: CardInfo, 
        isRevealed: Boolean, 
        onToggleReveal: () -> Unit, 
        onRefresh: () -> Unit, 
        onAgain: () -> Unit, 
        onGood: () -> Unit, 
        onOpenAnki: () -> Unit
    ) { 
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(20.dp), 
            colors = CardDefaults.cardColors( 
                containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)
            ), 
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.fillMaxWidth()
                ) { 
                    Icon( 
                        Icons.Filled.Visibility, 
                        contentDescription = null, 
                        tint = Color(0xFF38BDF8), 
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text( 
                        "Live Card Preview", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton( 
                        onClick = onRefresh, 
                        modifier = Modifier.size(24.dp)
                    ) { 
                        Icon( 
                            Icons.Filled.Refresh, 
                            contentDescription = "Refresh", 
                            tint = Color(0xFF94A3B8), 
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Surface( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(RoundedCornerShape(16.dp)) 
                        .clickable { onToggleReveal() }, 
                    color = Color(0xFF1E293B).copy(alpha = 0.7f), 
                    border = BorderStroke(1.dp, Color(0xFF475569).copy(alpha = 0.5f))
                ) { 
                    Column( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(18.dp), 
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { 
                        val cleanFuri = if (card.kanjiFurigana.contains("[")) { 
                            Regex("\\[([^\\]]+)\\]").findAll(card.kanjiFurigana).map { it.groupValues[1] }.joinToString("")
                        } else { 
                            card.kanjiFurigana
                        }
                        if (isRevealed && cleanFuri.isNotBlank() && cleanFuri != card.kanji) { 
                            Text( 
                                cleanFuri, 
                                color = Color(0xFF7EB6FF), 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        
                        Text( 
                            card.kanji.ifEmpty { card.question }, 
                            color = Color.White, 
                            fontSize = 32.sp, 
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (!isRevealed) { 
                            Text( 
                                "Tap to Reveal Answer", 
                                color = Color(0xFF94A3B8), 
                                fontSize = 12.sp
                            )
                        } else { 
                            val cleanMeaning = card.kanjiMeaning.replace(Regex("<[^>]*>"), "").trim()
                            if (cleanMeaning.isNotBlank()) { 
                                Text( 
                                    cleanMeaning, 
                                    color = Color(0xFFF1F5F9), 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.SemiBold, 
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        if (card.sentence.isNotBlank()) { 
                            Spacer(modifier = Modifier.height(10.dp))
                            val cleanSent = card.sentence.replace(Regex("<[^>]*>"), "").trim()
                            Text( 
                                cleanSent, 
                                color = Color(0xFFCBD5E1), 
                                fontSize = 12.sp, 
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        if (isRevealed && card.sentenceMeaning.isNotBlank()) { 
                            Spacer(modifier = Modifier.height(4.dp))
                            val cleanSentMeaning = card.sentenceMeaning.replace(Regex("<[^>]*>"), "").trim()
                            Text( 
                                cleanSentMeaning, 
                                color = Color(0xFF94A3B8), 
                                fontSize = 11.sp, 
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) { 
                    Button( 
                        onClick = onAgain, 
                        modifier = Modifier.weight(1f).height(38.dp), 
                        shape = RoundedCornerShape(10.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
                    ) { 
                        Text("Again", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Button( 
                        onClick = onToggleReveal, 
                        modifier = Modifier.weight(1.2f).height(38.dp), 
                        shape = RoundedCornerShape(10.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                    ) { 
                        Text(if (isRevealed) "Hide" else "Reveal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                    Button( 
                        onClick = onGood, 
                        modifier = Modifier.weight(1f).height(38.dp), 
                        shape = RoundedCornerShape(10.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) { 
                        Text("Good", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    OutlinedButton( 
                        onClick = onOpenAnki, 
                        modifier = Modifier.weight(0.9f).height(38.dp), 
                        shape = RoundedCornerShape(10.dp), 
                        border = BorderStroke(1.dp, Color(0xFF64748B))
                    ) { 
                        Text("Anki", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ModernStyleCard(isMusicStyle: Boolean, onSelect: (Boolean) -> Unit) { 
        val options = listOf(true, false)
        val labels = listOf("Music Player Style", "Classic Card Style")
        val selectedIndex = if (isMusicStyle) 0 else 1
        
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(20.dp), 
            colors = CardDefaults.cardColors( 
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Icon( 
                        Icons.Filled.Layers, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary, 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text( 
                        "Lockscreen Layout", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                    options.forEachIndexed { index, isMusic -> 
                        SegmentedButton( 
                            selected = index == selectedIndex, 
                            onClick = { onSelect(isMusic) }, 
                            shape = SegmentedButtonDefaults.itemShape( 
                                index = index, 
                                count = options.size
                            )
                        ) { 
                            Text(labels[index], fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ModernBackgroundStudioCard( 
        currentType: String, 
        blurRadius: Float, 
        dimOpacity: Float, 
        artworkOpacity: Float, 
        savedUris: Set<String>, 
        currentUri: String?, 
        onSelectType: (String) -> Unit, 
        onSelectSavedUri: (String) -> Unit, 
        onRemoveSavedUri: (String) -> Unit, 
        onPickNewImage: () -> Unit, 
        onBlurChange: (Float) -> Unit, 
        onOpacityChange: (Float) -> Unit, 
        onArtworkOpacityChange: (Float) -> Unit
    ) { 
        val context = LocalContext.current
        val options = listOf("anki_lock", "dark_blur", "sunset", "custom", "transparent")
        val labels = listOf("AnkiLock", "Dark Blur", "Sunset", "Custom", "Glass")
        val selectedIndex = options.indexOf(currentType).coerceAtLeast(0)
        
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(20.dp), 
            colors = CardDefaults.cardColors( 
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Icon( 
                        Icons.Filled.Image, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary, 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text( 
                        "Background Studio", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                    options.forEachIndexed { index, type -> 
                        SegmentedButton( 
                            selected = index == selectedIndex, 
                            onClick = { onSelectType(type) }, 
                            shape = SegmentedButtonDefaults.itemShape( 
                                index = index, 
                                count = options.size
                            )
                        ) { 
                            Text(labels[index], fontSize = 12.sp)
                        }
                    }
                }
                
                if (currentType != "transparent") { 
                    Spacer(modifier = Modifier.height(6.dp))
                    StudioSliderRow( 
                        icon = Icons.Filled.BlurOn, 
                        label = "Blur Radius: ${blurRadius.toInt()}px", 
                        value = blurRadius, 
                        valueRange = 5f..60f, 
                        onValueChange = onBlurChange
                    )
                    StudioSliderRow( 
                        icon = Icons.Filled.Opacity, 
                        label = "Dark Dimming Tint: ${(dimOpacity * 100).toInt()}%", 
                        value = dimOpacity, 
                        valueRange = 0.0f..0.9f, 
                        onValueChange = onOpacityChange
                    )
                    StudioSliderRow( 
                        icon = Icons.Filled.AutoAwesome, 
                        label = "Artwork Opacity: ${(artworkOpacity * 100).toInt()}%", 
                        value = artworkOpacity, 
                        valueRange = 0.1f..1.0f, 
                        onValueChange = onArtworkOpacityChange
                    )
                }
                
                if (currentType == "custom") { 
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (savedUris.isNotEmpty()) { 
                        Text( 
                            "Saved Wallpapers (${savedUris.size})", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyRow( 
                            horizontalArrangement = Arrangement.spacedBy(10.dp), 
                            modifier = Modifier.fillMaxWidth()
                        ) { 
                            items(savedUris.toList()) { uriStr -> 
                                val isSelected = (uriStr == currentUri)
                                Box( 
                                    modifier = Modifier 
                                        .size(68.dp) 
                                        .clip(RoundedCornerShape(12.dp)) 
                                        .border( 
                                            2.dp, 
                                            if (isSelected) MaterialTheme.colorScheme.primary 
                                            else Color.Transparent, 
                                            RoundedCornerShape(12.dp)
                                        ) 
                                        .clickable { onSelectSavedUri(uriStr) }
                                ) { 
                                    UriThumbnail( 
                                        context = context, 
                                        uriStr = uriStr, 
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    
                                    Box( 
                                        modifier = Modifier 
                                            .align(Alignment.TopEnd) 
                                            .padding(4.dp) 
                                            .size(18.dp) 
                                            .clip(CircleShape) 
                                            .background(Color(0xCC000000)) 
                                            .clickable { onRemoveSavedUri(uriStr) }, 
                                        contentAlignment = Alignment.Center
                                    ) { 
                                        Icon( 
                                            Icons.Filled.Close, 
                                            contentDescription = "Remove", 
                                            tint = Color.White, 
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    
                                    if (isSelected) { 
                                        Box( 
                                            modifier = Modifier 
                                                .align(Alignment.BottomEnd) 
                                                .padding(4.dp) 
                                                .size(18.dp) 
                                                .clip(CircleShape) 
                                                .background(MaterialTheme.colorScheme.primary), 
                                            contentAlignment = Alignment.Center
                                        ) { 
                                            Icon( 
                                                Icons.Filled.Check, 
                                                contentDescription = null, 
                                                tint = Color.White, 
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    Button( 
                        onClick = onPickNewImage, 
                        modifier = Modifier.fillMaxWidth(), 
                        shape = RoundedCornerShape(12.dp), 
                        colors = ButtonDefaults.buttonColors( 
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) { 
                        Icon( 
                            Icons.Filled.AddPhotoAlternate, 
                            contentDescription = null, 
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Picture from Phone")
                    }
                }
            }
        }
    }
    
    @Composable
    fun ModernDeckSelectorCard( 
        decks: List<DeckInfo>, 
        selectedIds: List<String>, 
        onDeckToggle: (String, Boolean) -> Unit
    ) { 
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(20.dp), 
            colors = CardDefaults.cardColors( 
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Icon( 
                        Icons.Filled.Style, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary, 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text( 
                        "Anki Decks", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                decks.forEach { deck -> 
                    val deckIdStr = deck.id.toString()
                    val isSelected = deckIdStr in selectedIds
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .clip(RoundedCornerShape(12.dp)) 
                            .clickable { onDeckToggle(deckIdStr, !isSelected) } 
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                    ) { 
                        Checkbox( 
                            checked = isSelected, 
                            onCheckedChange = { onDeckToggle(deckIdStr, it) }, 
                            colors = CheckboxDefaults.colors( 
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text( 
                            deck.name, 
                            style = MaterialTheme.typography.bodyMedium, 
                            fontWeight = FontWeight.Medium, 
                            modifier = Modifier.weight(1f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) { 
                            Text( 
                                "${deck.newCount}", 
                                style = MaterialTheme.typography.bodySmall, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFF8AB4F8)
                            )
                            Text( 
                                " · ", 
                                style = MaterialTheme.typography.bodySmall, 
                                color = Color(0xFF94A3B8)
                            )
                            Text( 
                                "${deck.learnCount}", 
                                style = MaterialTheme.typography.bodySmall, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFFF28B82)
                            )
                            Text( 
                                " · ", 
                                style = MaterialTheme.typography.bodySmall, 
                                color = Color(0xFF94A3B8)
                            )
                            Text( 
                                "${deck.reviewCount}", 
                                style = MaterialTheme.typography.bodySmall, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFF81C995)
                            )
                        }
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ModernIntervalCard( 
        updateMinutes: Int, 
        snoozeMinutes: Int, 
        onUpdateSelect: (Int) -> Unit, 
        onSnoozeSelect: (Int) -> Unit
    ) { 
        val options = listOf(30, 60, 120)
        val labels = listOf("30m", "1h", "2h")
        val updateIdx = options.indexOf(updateMinutes).coerceAtLeast(0)
        val snoozeIdx = options.indexOf(snoozeMinutes).coerceAtLeast(0)
        
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(20.dp), 
            colors = CardDefaults.cardColors( 
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Text( 
                    "Frequency & Snooze", 
                    fontWeight = FontWeight.SemiBold, 
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text( 
                    "Background Sync Interval", 
                    fontSize = 12.sp, 
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(6.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                    options.forEachIndexed { index, min -> 
                        SegmentedButton( 
                            selected = index == updateIdx, 
                            onClick = { onUpdateSelect(min) }, 
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                        ) { 
                            Text(labels[index], fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text( 
                    "Snooze Duration", 
                    fontSize = 12.sp, 
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(6.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                    options.forEachIndexed { index, min -> 
                        SegmentedButton( 
                            selected = index == snoozeIdx, 
                            onClick = { onSnoozeSelect(min) }, 
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                        ) { 
                            Text(labels[index], fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    fun StudioSliderRow( 
        icon: ImageVector, 
        label: String, 
        value: Float, 
        valueRange: ClosedFloatingPointRange<Float>, 
        onValueChange: (Float) -> Unit
    ) { 
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) { 
            Icon( 
                icon, 
                contentDescription = null, 
                tint = Color(0xFF94A3B8), 
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text( 
                label, 
                fontSize = 12.sp, 
                color = Color(0xFF94A3B8)
            )
        }
        Slider( 
            value = value, 
            onValueChange = onValueChange, 
            valueRange = valueRange, 
            colors = SliderDefaults.colors( 
                thumbColor = MaterialTheme.colorScheme.primary, 
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
    
@Composable
fun UriThumbnail(context: Context, uriStr: String, modifier: Modifier = Modifier) { 
    val bitmap = remember(uriStr) { 
        try { 
            val uri = Uri.parse(uriStr)
            context.contentResolver.openInputStream(uri)?.use { stream -> 
                val options = BitmapFactory.Options().apply { inSampleSize = 4 }
                BitmapFactory.decodeStream(stream, null, options)
            }
        } catch (e: Exception) { 
            null
        }
    }
    if (bitmap != null) { 
        Image( 
            bitmap = bitmap.asImageBitmap(), 
            contentDescription = null, 
            contentScale = ContentScale.Crop, 
            modifier = modifier
        )
    } else { 
        Box( 
            modifier = modifier.background(Color(0xFF334155)), 
            contentAlignment = Alignment.Center
        ) { 
            Icon(Icons.Filled.Image, contentDescription = null, tint = Color(0xFF94A3B8))
        }
    }
}
