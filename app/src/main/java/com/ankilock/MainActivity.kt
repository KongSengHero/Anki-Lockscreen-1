package com.ankilock
    
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.DeckInfo
import com.ankilock.data.PreferencesManager
import com.ankilock.service.AnkiNotificationService
import com.ankilock.ui.theme.AnkiLockTheme
import com.ankilock.worker.DueCountWorker
    
class MainActivity : ComponentActivity() { 
    
    private lateinit var ankiHelper: AnkiDroidHelper
    private lateinit var prefs: PreferencesManager
    
    private var isAnkiInstalledState by mutableStateOf(false)
    private var hasPermissionState by mutableStateOf(false)
    private var decksState by mutableStateOf<List<DeckInfo>>(emptyList())
    
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
    
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState)
        
        ankiHelper = AnkiDroidHelper(this)
        prefs = PreferencesManager(this)
        
        requestInitialPermissions()
        
        setContent { 
            AnkiLockTheme { 
                SettingsScreen()
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
        }
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
    fun SettingsScreen() { 
        var isEnabled by remember { mutableStateOf(prefs.isServiceEnabled) }
        var isMusicPlayerStyle by remember { mutableStateOf(prefs.isMusicPlayerStyle) }
        val selectedDeckIds = remember { mutableStateListOf<String>() }
        var updateInterval by remember { mutableIntStateOf(prefs.updateIntervalMinutes) }
        var snoozeDuration by remember { mutableIntStateOf(prefs.snoozeDurationMinutes) }
        
        remember { 
            selectedDeckIds.clear()
            selectedDeckIds.addAll(prefs.selectedDeckIds)
            true
        }
        
        Scaffold( 
            topBar = { 
                TopAppBar( 
                    title = { 
                        Text( 
                            "AnkiLock", 
                            fontWeight = FontWeight.Bold
                        )
                    }, 
                    colors = TopAppBarDefaults.topAppBarColors( 
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            Column( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .padding(padding) 
                    .verticalScroll(rememberScrollState()) 
                    .padding(16.dp), 
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) { 
                StatusCard( 
                    isInstalled = isAnkiInstalledState, 
                    hasPermission = hasPermissionState, 
                    onRequestPermission = { 
                        ankiPermissionLauncher.launch( 
                            AnkiDroidHelper.PERMISSION_READ_WRITE_DATABASE
                        )
                    }
                )
                
                ServiceToggleCard(isEnabled) { enabled ->
                    isEnabled = enabled
                    prefs.isServiceEnabled = enabled
                    if (enabled) { 
                        AnkiNotificationService.start(this@MainActivity)
                        DueCountWorker.schedule( 
                            this@MainActivity, 
                            updateInterval.toLong()
                        )
                    } else { 
                        AnkiNotificationService.stop(this@MainActivity)
                        DueCountWorker.cancel(this@MainActivity)
                    }
                }
                
                NotificationStyleCard(isMusicPlayerStyle) { isMusic ->
                    isMusicPlayerStyle = isMusic
                    prefs.isMusicPlayerStyle = isMusic
                    if (isEnabled) { 
                        AnkiNotificationService.update(this@MainActivity)
                    }
                }
                
                if (decksState.isNotEmpty()) { 
                    DeckSelectorCard(decksState, selectedDeckIds) { deckId, checked ->
                        if (checked) { 
                            selectedDeckIds.add(deckId)
                        } else { 
                            selectedDeckIds.remove(deckId)
                        }
                        prefs.selectedDeckIds = selectedDeckIds.toSet()
                        if (isEnabled) AnkiNotificationService.update(this@MainActivity)
                    }
                }
                
                UpdateFrequencyCard(updateInterval) { minutes ->
                    updateInterval = minutes
                    prefs.updateIntervalMinutes = minutes
                    if (isEnabled) { 
                        DueCountWorker.schedule(this@MainActivity, minutes.toLong())
                    }
                }
                
                SnoozeDurationCard(snoozeDuration) { minutes ->
                    snoozeDuration = minutes
                    prefs.snoozeDurationMinutes = minutes
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    @Composable
    fun StatusCard( 
        isInstalled: Boolean, 
        hasPermission: Boolean, 
        onRequestPermission: () -> Unit
    ) { 
        SettingsCard( 
            icon = if (isInstalled && hasPermission) Icons.Filled.CheckCircle 
                   else Icons.Filled.Error, 
            title = "AnkiDroid Status"
        ) { 
            StatusRow("AnkiDroid Installed", isInstalled)
            Spacer(modifier = Modifier.height(8.dp))
            StatusRow("API Permission Granted", hasPermission)
            
            if (isInstalled && !hasPermission) { 
                Spacer(modifier = Modifier.height(12.dp))
                Button( 
                    onClick = onRequestPermission, 
                    modifier = Modifier.fillMaxWidth(), 
                    shape = RoundedCornerShape(10.dp), 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) { 
                    Text("Grant AnkiDroid Permission")
                }
            }
        }
    }
    
    @Composable
    fun StatusRow(label: String, isOk: Boolean) { 
        val color by animateColorAsState( 
            if (isOk) MaterialTheme.colorScheme.primary 
            else MaterialTheme.colorScheme.error, 
            label = "status"
        )
        
        Row( 
            verticalAlignment = Alignment.CenterVertically, 
            modifier = Modifier.fillMaxWidth()
        ) { 
            Box( 
                modifier = Modifier 
                    .size(8.dp) 
                    .clip(CircleShape) 
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text( 
                label, 
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text( 
                if (isOk) "Connected" else "Not Connected", 
                style = MaterialTheme.typography.bodySmall, 
                color = color
            )
        }
    }
    
    @Composable
    fun ServiceToggleCard(isEnabled: Boolean, onToggle: (Boolean) -> Unit) { 
        SettingsCard(icon = Icons.Filled.Notifications, title = "Notification Service") { 
            Row( 
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.fillMaxWidth()
            ) { 
                Text( 
                    if (isEnabled) "Service is running" else "Service is stopped", 
                    style = MaterialTheme.typography.bodyMedium, 
                    modifier = Modifier.weight(1f)
                )
                Switch( 
                    checked = isEnabled, 
                    onCheckedChange = onToggle, 
                    colors = SwitchDefaults.colors( 
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
    
    @Composable
    fun DeckSelectorCard( 
        decks: List<DeckInfo>, 
        selectedIds: List<String>, 
        onDeckToggle: (String, Boolean) -> Unit
    ) { 
        SettingsCard(icon = Icons.Filled.Style, title = "Select Decks") { 
            Text( 
                "Leave all unchecked to show all decks", 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            decks.forEach { deck ->
                val deckIdStr = deck.id.toString()
                val isSelected = deckIdStr in selectedIds
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clickable { onDeckToggle(deckIdStr, !isSelected) } 
                        .padding(vertical = 4.dp)
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
                        modifier = Modifier.weight(1f)
                    )
                    Text( 
                        "${deck.totalDue} due", 
                        style = MaterialTheme.typography.bodySmall, 
                        color = if (deck.totalDue > 0) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NotificationStyleCard(isMusicStyle: Boolean, onSelect: (Boolean) -> Unit) { 
        val options = listOf(true, false)
        val labels = listOf("Music Player", "Classic Card")
        val selectedIndex = if (isMusicStyle) 0 else 1
        
        SettingsCard(icon = Icons.Filled.MusicNote, title = "Display Style") { 
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
                        Text(labels[index], fontSize = 13.sp)
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UpdateFrequencyCard(currentMinutes: Int, onSelect: (Int) -> Unit) { 
        val options = listOf(30, 60, 120)
        val labels = listOf("30 min", "1 hour", "2 hours")
        val selectedIndex = options.indexOf(currentMinutes).coerceAtLeast(0)
        
        SettingsCard(icon = Icons.Filled.Schedule, title = "Update Frequency") { 
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                options.forEachIndexed { index, minutes ->
                    SegmentedButton( 
                        selected = index == selectedIndex, 
                        onClick = { onSelect(minutes) }, 
                        shape = SegmentedButtonDefaults.itemShape( 
                            index = index, 
                            count = options.size
                        )
                    ) { 
                        Text(labels[index], fontSize = 13.sp)
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SnoozeDurationCard(currentMinutes: Int, onSelect: (Int) -> Unit) { 
        val options = listOf(30, 60, 120)
        val labels = listOf("30 min", "1 hour", "2 hours")
        val selectedIndex = options.indexOf(currentMinutes).coerceAtLeast(0)
        
        SettingsCard(icon = Icons.Filled.Snooze, title = "Snooze Duration") { 
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                options.forEachIndexed { index, minutes ->
                    SegmentedButton( 
                        selected = index == selectedIndex, 
                        onClick = { onSelect(minutes) }, 
                        shape = SegmentedButtonDefaults.itemShape( 
                            index = index, 
                            count = options.size
                        )
                    ) { 
                        Text(labels[index], fontSize = 13.sp)
                    }
                }
            }
        }
    }
    
    @Composable
    fun SettingsCard( 
        icon: ImageVector, 
        title: String, 
        content: @Composable () -> Unit
    ) { 
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(16.dp), 
            colors = CardDefaults.cardColors( 
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) { 
            Column(modifier = Modifier.padding(20.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Icon( 
                        icon, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary, 
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text( 
                        title, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}
