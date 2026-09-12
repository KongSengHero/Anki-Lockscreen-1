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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.ExperimentalFoundationApi 
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
import androidx.compose.foundation.layout.WindowInsets 
import androidx.compose.foundation.layout.asPaddingValues 
import androidx.compose.foundation.layout.statusBars 
import androidx.compose.foundation.layout.statusBarsPadding 
import androidx.compose.foundation.layout.width 
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.animateContentSize 
import com.ankilock.ui.components.ApplicationSceneryBackground 
import com.ankilock.util.ImageBlurUtil
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ChevronRight 
import androidx.compose.material.icons.filled.Palette 
import androidx.compose.material.icons.filled.Opacity 
import androidx.compose.material.icons.filled.Refresh 
import androidx.compose.material.icons.filled.Style 
import androidx.compose.material.icons.filled.Visibility 
import androidx.compose.material.icons.filled.Headphones 
import androidx.compose.material.icons.filled.Lock 
import androidx.compose.material.icons.filled.SmartToy 
import androidx.compose.material3.ModalBottomSheet 
import androidx.compose.material3.rememberModalBottomSheetState 
import androidx.compose.ui.text.style.TextOverflow 
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.ui.cards.DeckCarouselCard
import com.ankilock.ui.components.Squircle3DButton
import com.ankilock.ui.shinobi.ShinobiBottomNav
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiShapes
import com.ankilock.ui.shinobi.ShinobiTab
import com.ankilock.ui.shinobi.VocabScreen
import com.ankilock.ui.shinobi.DojoScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.ankilock.data.CardInfo
import com.ankilock.data.CardSessionManager
import com.ankilock.data.DeckInfo
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StorySessionManager
import com.ankilock.service.AnkiNotificationService
import com.ankilock.ui.components.AiKeyConfigDialog
import com.ankilock.ui.study.ListeningDictationScreen
import com.ankilock.ui.study.ForgeStoryScreen
import com.ankilock.ui.theme.AnkiLockTheme
import com.ankilock.util.AudioPlayerHelper
import com.ankilock.util.AudioTrackPlaying
import com.ankilock.util.MediaArtworkGenerator
import com.ankilock.ui.components.BlossomLoadingScreen 
import com.ankilock.ui.components.GlobalSeekerContainer 
import com.ankilock.ui.components.GlobalSeekerRow 
import com.ankilock.ui.components.ThemeSwitcher 
import com.ankilock.ui.shinobi.AppTheme 
import androidx.compose.ui.graphics.SolidColor 
import com.ankilock.widget.AnkiAppWidgetProvider 
import com.ankilock.worker.DueCountWorker 
import kotlin.math.roundToInt
    
class MainActivity : ComponentActivity() { 
    
    private lateinit var ankiHelper: AnkiDroidHelper
    private lateinit var prefs: PreferencesManager
    private lateinit var audioPlayer: AudioPlayerHelper
    
    private var isAnkiInstalledState by mutableStateOf(false)
    private var hasPermissionState by mutableStateOf(false)
    private var decksState by mutableStateOf<List<DeckInfo>>(emptyList())
    private var previewCardState by mutableStateOf<CardInfo?>(null)
    private var backgroundTypeState by mutableStateOf("transparent") 
    private var appBackgroundTypeState by mutableStateOf("reading") 
    private var appCustomImageUriState by mutableStateOf<String?>(null) 
    private var appSavedImageUrisState by mutableStateOf<Set<String>>(emptySet()) 
    private var customImageUriState by mutableStateOf<String?>(null)
    private var savedImageUrisState by mutableStateOf<Set<String>>(emptySet())
    private var blurRadiusState by mutableFloatStateOf(20f) 
    private var dimOpacityState by mutableFloatStateOf(0.10f) 
    private var artworkOpacityState by mutableFloatStateOf(0.5f) 
    private var appBlurRadiusState by mutableFloatStateOf(20f) 
    private var appDimOpacityState by mutableFloatStateOf(0.10f) 
    private var appArtworkOpacityState by mutableFloatStateOf(0.5f) 
    private var appThemeState by mutableStateOf("dim")
    
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
    
    private val appImagePickerLauncher = registerForActivityResult( 
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
            prefs.addSavedAppImageUri(uriStr) 
            prefs.appCustomImageUri = uriStr 
            prefs.appBackgroundType = "custom" 
            
            appSavedImageUrisState = prefs.appSavedImageUris 
            appCustomImageUriState = uriStr 
            appBackgroundTypeState = "custom" 
        } 
    } 
    
    override fun onCreate(savedInstanceState: Bundle?) { 
        enableEdgeToEdge( 
            navigationBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT) 
        ) 
        super.onCreate(savedInstanceState) 
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { 
            window.isNavigationBarContrastEnforced = false 
        } 
        window.navigationBarColor = android.graphics.Color.TRANSPARENT 
        
        ankiHelper = AnkiDroidHelper(this) 
        prefs = PreferencesManager(this) 
        audioPlayer = AudioPlayerHelper(this) 
        
        backgroundTypeState = prefs.backgroundType 
        appBackgroundTypeState = prefs.appBackgroundType 
        appCustomImageUriState = prefs.appCustomImageUri 
        appSavedImageUrisState = prefs.appSavedImageUris 
        customImageUriState = prefs.customImageUri 
        savedImageUrisState = prefs.savedImageUris 
        blurRadiusState = prefs.blurRadius.toFloat() 
        dimOpacityState = prefs.dimOpacity 
        artworkOpacityState = prefs.artworkOpacity 
        appBlurRadiusState = prefs.appBlurRadius.toFloat() 
        appDimOpacityState = prefs.appDimOpacity 
        appArtworkOpacityState = prefs.appArtworkOpacity 
        appThemeState = prefs.appTheme 
        ShinobiColors.applyTheme(AppTheme.fromId(appThemeState)) 
        
        requestInitialPermissions() 
        
        setContent { 
            AnkiLockTheme { 
                var showLoadingOverlay by remember { mutableStateOf(true) } 
                var isAppLoading by remember { mutableStateOf(true) } 
                LaunchedEffect(Unit) { 
                    kotlinx.coroutines.delay(3400) 
                    isAppLoading = false 
                } 
                Box(modifier = Modifier.fillMaxSize()) { 
                    MainContainer() 
                    if (showLoadingOverlay) { 
                        BlossomLoadingScreen( 
                            isLoading = isAppLoading, 
                            onFinished = { showLoadingOverlay = false } 
                        ) 
                    } 
                } 
            } 
        } 
    }
    
    override fun onDestroy() { 
        super.onDestroy()
        if (::audioPlayer.isInitialized) { 
            audioPlayer.release()
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
            previewCardState = CardSessionManager.getOrFetchCard(this, forceRefresh = true)
        }
        prefs.evaluateDailyStreak()
        backgroundTypeState = prefs.backgroundType 
        appBackgroundTypeState = prefs.appBackgroundType 
        appCustomImageUriState = prefs.appCustomImageUri 
        appSavedImageUrisState = prefs.appSavedImageUris 
        customImageUriState = prefs.customImageUri
        savedImageUrisState = prefs.savedImageUris
        blurRadiusState = prefs.blurRadius.toFloat()
        dimOpacityState = prefs.dimOpacity
        artworkOpacityState = prefs.artworkOpacity
        appBlurRadiusState = prefs.appBlurRadius.toFloat() 
        appDimOpacityState = prefs.appDimOpacity 
        appArtworkOpacityState = prefs.appArtworkOpacity
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
    
    @Composable
    fun MinimalTopBar( 
        streakCount: Int, 
        isStreakActive: Boolean, 
        completedStoriesCount: Int, 
        dueCardsCount: Int, 
        isRefreshing: Boolean, 
        onRefresh: () -> Unit, 
        modifier: Modifier = Modifier 
    ) { 
        Box( 
            modifier = modifier 
                .fillMaxWidth() 
                .background( 
                    Brush.verticalGradient( 
                        colors = listOf( 
                            ShinobiColors.BackgroundDeep.copy(alpha = 0.95f), 
                            ShinobiColors.BackgroundDeep.copy(alpha = 0.85f), 
                            ShinobiColors.BackgroundDeep.copy(alpha = 0.45f), 
                            Color.Transparent 
                        ) 
                    ) 
                ) 
                .statusBarsPadding() 
        ) { 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 18.dp, vertical = 10.dp), 
                verticalAlignment = Alignment.CenterVertically 
            ) { 
                Text( 
                    text = "ブロッサム", 
                    fontSize = 19.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = ShinobiColors.TextPrimary 
                ) 
                
                Spacer(modifier = Modifier.weight(1f)) 
                
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    Surface( 
                        shape = ShinobiShapes.SquircleSmall, 
                        color = ShinobiColors.SurfaceCard1, 
                        border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
                    ) { 
                        Row( 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                            verticalAlignment = Alignment.CenterVertically 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.LocalFireDepartment, 
                                contentDescription = "Streak", 
                                tint = if (isStreakActive) ShinobiColors.WarmOchre else ShinobiColors.TextMuted, 
                                modifier = Modifier.size(14.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(4.dp)) 
                            Text( 
                                text = "$streakCount", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = ShinobiColors.TextPrimary 
                            ) 
                        } 
                    } 
                    
                    Surface( 
                        shape = ShinobiShapes.SquircleSmall, 
                        color = ShinobiColors.SurfaceCard1, 
                        border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
                    ) { 
                        Row( 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                            verticalAlignment = Alignment.CenterVertically 
                        ) { 
                            Icon( 
                                imageVector = Icons.AutoMirrored.Filled.MenuBook, 
                                contentDescription = "Stories Completed", 
                                tint = ShinobiColors.MatchaSage, 
                                modifier = Modifier.size(13.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(4.dp)) 
                            Text( 
                                text = "$completedStoriesCount", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = ShinobiColors.TextPrimary 
                            ) 
                        } 
                    } 
                    
                    Surface( 
                        shape = ShinobiShapes.SquircleSmall, 
                        color = ShinobiColors.SurfaceCard1, 
                        border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
                    ) { 
                        Row( 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                            verticalAlignment = Alignment.CenterVertically 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Bolt, 
                                contentDescription = "Cards Due", 
                                tint = ShinobiColors.SlateBlue, 
                                modifier = Modifier.size(14.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(4.dp)) 
                            Text( 
                                text = "$dueCardsCount", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = ShinobiColors.TextPrimary 
                            ) 
                        } 
                    } 
                    
                    IconButton( 
                        onClick = onRefresh, 
                        modifier = Modifier.size(32.dp) 
                    ) { 
                        if (isRefreshing) { 
                            androidx.compose.material3.CircularProgressIndicator( 
                                modifier = Modifier.size(16.dp), 
                                color = ShinobiColors.SlateBlue, 
                                strokeWidth = 2.dp 
                            ) 
                        } else { 
                            Icon( 
                                imageVector = Icons.Default.Refresh, 
                                contentDescription = "Refresh", 
                                tint = ShinobiColors.TextSecondary, 
                                modifier = Modifier.size(17.dp) 
                            ) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class) 
    @Composable
    fun MainContainer() { 
        val tabs = remember { ShinobiTab.values() } 
        var selectedShinobiTab by remember { mutableStateOf(ShinobiTab.CARDS) } 
        val pagerState = rememberPagerState(initialPage = selectedShinobiTab.ordinal) { tabs.size } 
        var showAiConfigDialog by remember { mutableStateOf(false) } 
        var isRefreshing by remember { mutableStateOf(false) } 
        var stats by remember { mutableStateOf(CardSessionManager.currentStats) } 
        var streakCount by remember { mutableIntStateOf(prefs.dailyStreakCount) } 
        var isStreakActive by remember { mutableStateOf(prefs.isStreakCompletedToday) } 
        var completedStoriesCount by remember { mutableIntStateOf(prefs.completedStoryIds.size) } 
        val coroutineScope = androidx.compose.runtime.rememberCoroutineScope() 
        
        LaunchedEffect(pagerState.currentPage) { 
            audioPlayer.stop() 
            selectedShinobiTab = tabs[pagerState.currentPage] 
        } 
        
        val isStoryReaderActive = ((selectedShinobiTab == ShinobiTab.STORIES || selectedShinobiTab == ShinobiTab.FORGED) && StorySessionManager.currentStory != null) 
        val isStoryDetailsActive = ((selectedShinobiTab == ShinobiTab.STORIES || selectedShinobiTab == ShinobiTab.FORGED) && StorySessionManager.selectedStoryForDetails != null) 
        
        DisposableEffect(Unit) { 
            val listener = { 
                stats = CardSessionManager.currentStats 
                streakCount = prefs.dailyStreakCount 
                isStreakActive = prefs.isStreakCompletedToday 
                completedStoriesCount = prefs.completedStoryIds.size 
            } 
            CardSessionManager.addListener(listener) 
            onDispose { 
                CardSessionManager.removeListener(listener) 
            } 
        } 
        
        Box( 
            modifier = Modifier 
                .fillMaxSize() 
                .background(ShinobiColors.BackgroundDeep) 
        ) { 
            ApplicationSceneryBackground( 
                appBackgroundType = appBackgroundTypeState, 
                customImageUri = appCustomImageUriState, 
                blurRadius = appBlurRadiusState.toInt(), 
                dimOpacity = appDimOpacityState, 
                artworkOpacity = appArtworkOpacityState 
            ) 
            
            Box(modifier = Modifier.fillMaxSize()) { 
                HorizontalPager( 
                    state = pagerState, 
                    beyondBoundsPageCount = 1, 
                    flingBehavior = PagerDefaults.flingBehavior( 
                        state = pagerState, 
                        snapAnimationSpec = spring( 
                            dampingRatio = Spring.DampingRatioNoBouncy, 
                            stiffness = Spring.StiffnessMediumLow 
                        ) 
                    ), 
                    modifier = Modifier.fillMaxSize(), 
                    userScrollEnabled = !isStoryReaderActive 
                ) { page -> 
                    when (tabs[page]) { 
                        ShinobiTab.CARDS -> ModernSettingsScreen( 
                            padding = PaddingValues(0.dp), 
                            onOpenAiConfig = { showAiConfigDialog = true } 
                        ) 
                        ShinobiTab.STORIES -> ForgeStoryScreen( 
                            ankiHelper = ankiHelper, 
                            prefs = prefs, 
                            audioPlayer = audioPlayer, 
                            onOpenAiConfig = { showAiConfigDialog = true }, 
                            onNavigateToForged = { 
                                selectedShinobiTab = ShinobiTab.FORGED 
                                coroutineScope.launch { 
                                    pagerState.animateScrollToPage(ShinobiTab.FORGED.ordinal) 
                                } 
                            } 
                        ) 
                        ShinobiTab.FORGED -> com.ankilock.ui.study.ForgedStoriesScreen( 
                            prefs = prefs, 
                            audioPlayer = audioPlayer, 
                            onOpenForgeStudio = { 
                                selectedShinobiTab = ShinobiTab.STORIES 
                                coroutineScope.launch { 
                                    pagerState.animateScrollToPage(ShinobiTab.STORIES.ordinal) 
                                } 
                            }, 
                            onOpenStory = { story -> 
                                StorySessionManager.currentStory = story 
                                StorySessionManager.userAnswers.clear() 
                                selectedShinobiTab = ShinobiTab.STORIES 
                                coroutineScope.launch { 
                                    pagerState.scrollToPage(ShinobiTab.STORIES.ordinal) 
                                } 
                            } 
                        ) 
                        ShinobiTab.JISHO -> com.ankilock.ui.jisho.JishoScreen( 
                            padding = PaddingValues( 
                                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 58.dp, 
                                bottom = 96.dp 
                            ), 
                            prefs = prefs 
                        ) 
                        ShinobiTab.DOJO -> DojoScreen( 
                            onOpenSettings = { showAiConfigDialog = true }, 
                            modifier = Modifier 
                                .fillMaxSize() 
                                .padding( 
                                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 58.dp, 
                                    bottom = 96.dp 
                                ) 
                        ) 
                    } 
                } 
                
                if (!isStoryReaderActive && !isStoryDetailsActive) { 
                    MinimalTopBar( 
                        streakCount = streakCount, 
                        isStreakActive = isStreakActive, 
                        completedStoriesCount = completedStoriesCount, 
                        dueCardsCount = if (prefs.selectedDeckIds.isEmpty()) 0 else if (stats.first + stats.second + stats.third > 0) (stats.first + stats.second + stats.third) else decksState.filter { it.id.toString() in prefs.selectedDeckIds }.sumOf { it.totalDue }, 
                        isRefreshing = isRefreshing, 
                        onRefresh = { 
                            coroutineScope.launch { 
                                isRefreshing = true 
                                refreshData() 
                                CardSessionManager.refresh(this@MainActivity) 
                                stats = CardSessionManager.currentStats 
                                streakCount = prefs.dailyStreakCount 
                                isStreakActive = prefs.isStreakCompletedToday 
                                completedStoriesCount = prefs.completedStoryIds.size 
                                isRefreshing = false 
                            } 
                        }, 
                        modifier = Modifier.align(Alignment.TopCenter) 
                    ) 
                } 
                
                if (showAiConfigDialog) { 
                    AiKeyConfigDialog( 
                        prefs = prefs, 
                        onDismiss = { showAiConfigDialog = false }, 
                        onSaved = { showAiConfigDialog = false } 
                    ) 
                } 
                
                if (!isStoryReaderActive) { 
                    Box( 
                        modifier = Modifier 
                            .align(Alignment.BottomCenter) 
                            .fillMaxWidth() 
                            .background( 
                                Brush.verticalGradient( 
                                    colors = listOf( 
                                        Color.Transparent, 
                                        ShinobiColors.BackgroundDeep.copy(alpha = 0.50f), 
                                        ShinobiColors.BackgroundDeep.copy(alpha = 0.88f), 
                                        ShinobiColors.BackgroundDeep.copy(alpha = 0.98f) 
                                    ) 
                                ) 
                            ) 
                    ) { 
                        ShinobiBottomNav( 
                            selectedTab = selectedShinobiTab, 
                            onTabSelected = { tab -> 
                                audioPlayer.stop() 
                                selectedShinobiTab = tab 
                                coroutineScope.launch { 
                                    pagerState.animateScrollToPage(tab.ordinal) 
                                } 
                            } 
                        ) 
                    } 
                } 
            } 
        } 
    } 
    
    @OptIn(ExperimentalMaterial3Api::class) 
    @Composable
    fun ModernSettingsScreen(padding: PaddingValues, onOpenAiConfig: () -> Unit) { 
        var isEnabled by remember { mutableStateOf(prefs.isServiceEnabled) } 
        var isMusicPlayerStyle by remember { mutableStateOf(prefs.isMusicPlayerStyle) } 
        var classicRevealedAction by remember { mutableStateOf(prefs.classicRevealedAction) } 
        var isAutoPlayAudio by remember { mutableStateOf(prefs.isAutoPlayAudio) } 
        val selectedDeckIds = remember { mutableStateListOf<String>() } 
        var updateInterval by remember { mutableIntStateOf(prefs.updateIntervalMinutes) } 
        var snoozeDuration by remember { mutableIntStateOf(prefs.snoozeDurationMinutes) } 
        var showBackgroundsSheet by remember { mutableStateOf(false) } 
        var showDecksSheet by remember { mutableStateOf(false) } 
        var showStyleSheet by remember { mutableStateOf(false) } 
        
        var activeCard by remember { mutableStateOf(CardSessionManager.currentCard) } 
        var isRevealed by remember { mutableStateOf(CardSessionManager.isRevealed) } 
        var stats by remember { mutableStateOf(CardSessionManager.currentStats) } 
        val deckCardsCache = remember { mutableStateMapOf<Long, CardInfo?>() } 
        val deckStatsCache = remember { mutableStateMapOf<Long, Triple<Int, Int, Int>>() } 
        val deckCardQueues = remember { mutableStateMapOf<Long, MutableList<CardInfo>>() } 
        val coroutineScope = androidx.compose.runtime.rememberCoroutineScope() 
        
        DisposableEffect(Unit) { 
            val listener = { 
                activeCard = CardSessionManager.currentCard 
                isRevealed = CardSessionManager.isRevealed 
                stats = CardSessionManager.currentStats 
            } 
            CardSessionManager.addListener(listener) 
            onDispose { 
                CardSessionManager.removeListener(listener) 
            } 
        } 
        
        LaunchedEffect(Unit) { 
            withContext(Dispatchers.IO) { 
                CardSessionManager.getOrFetchCard(this@MainActivity) 
            } 
        } 
        
        remember { 
            selectedDeckIds.clear() 
            selectedDeckIds.addAll(prefs.selectedDeckIds) 
            true 
        } 
        
        val selectedDecksList = remember(decksState, selectedDeckIds.toList()) { 
            if (selectedDeckIds.isEmpty()) { 
                emptyList() 
            } else { 
                decksState.filter { it.id.toString() in selectedDeckIds } 
            } 
        } 
        
        LaunchedEffect(decksState, selectedDeckIds.toList()) { 
            withContext(Dispatchers.IO) { 
                val decksToLoad = decksState.filter { it.id.toString() in selectedDeckIds } 
                for (deck in decksToLoad) { 
                    if (!deckCardsCache.containsKey(deck.id)) { 
                        val batch = ankiHelper.getDueCardsForDeck(deck.id, limit = 5, deckName = deck.name) 
                        val firstCard = batch.firstOrNull() 
                        val remaining = if (batch.size > 1) batch.drop(1).toMutableList() else mutableListOf() 
                        withContext(Dispatchers.Main) { 
                            deckCardsCache[deck.id] = firstCard 
                            deckCardQueues[deck.id] = remaining 
                        } 
                    } 
                    if (!deckStatsCache.containsKey(deck.id)) { 
                        val deckStats = ankiHelper.getDeckStatsForDeck(deck.name) 
                        withContext(Dispatchers.Main) { 
                            deckStatsCache[deck.id] = deckStats 
                        } 
                    } 
                } 
            } 
        } 
        
        val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .verticalScroll(rememberScrollState()) 
                .padding(horizontal = 16.dp), 
            verticalArrangement = Arrangement.spacedBy(16.dp) 
        ) { 
            Spacer(modifier = Modifier.height(statusBarTop + 58.dp)) 
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
            
            DeckCarouselCard( 
                decks = selectedDecksList, 
                activeCard = activeCard, 
                stats = stats, 
                isRevealed = isRevealed, 
                deckCardsCache = deckCardsCache, 
                deckStatsCache = deckStatsCache, 
                backgroundType = backgroundTypeState, 
                blurRadius = blurRadiusState.toInt(), 
                dimOpacity = dimOpacityState, 
                artworkOpacity = artworkOpacityState, 
                customImageUri = customImageUriState, 
                isAutoPlay = isAutoPlayAudio, 
                onToggleAutoPlay = { autoPlay -> 
                    isAutoPlayAudio = autoPlay 
                    prefs.isAutoPlayAudio = autoPlay 
                }, 
                isPlayingWord = (audioPlayer.currentPlayingTrack == AudioTrackPlaying.WORD), 
                isPlayingSentence = (audioPlayer.currentPlayingTrack == AudioTrackPlaying.SENTENCE), 
                onToggleReveal = { card -> 
                    val willReveal = !isRevealed 
                    isRevealed = willReveal 
                    CardSessionManager.toggleReveal(this@MainActivity) 
                    if (willReveal && isAutoPlayAudio) { 
                        (card ?: activeCard)?.let { audioPlayer.playSequence(it) } 
                    } 
                }, 
                onRefresh = { 
                    audioPlayer.stop() 
                    isRevealed = false 
                    CardSessionManager.refresh(this@MainActivity) 
                    coroutineScope.launch(Dispatchers.IO) { 
                        val decksToLoad = decksState.filter { it.id.toString() in selectedDeckIds } 
                        for (deck in decksToLoad) { 
                            val batch = ankiHelper.getDueCardsForDeck(deck.id, limit = 5, deckName = deck.name) 
                            val firstCard = batch.firstOrNull() 
                            val remaining = if (batch.size > 1) batch.drop(1).toMutableList() else mutableListOf() 
                            val deckStats = ankiHelper.getDeckStatsForDeck(deck.name) 
                            withContext(Dispatchers.Main) { 
                                deckCardsCache[deck.id] = firstCard 
                                deckCardQueues[deck.id] = remaining 
                                deckStatsCache[deck.id] = deckStats 
                            } 
                        } 
                    } 
                }, 
                onAgain = { deck, card -> 
                    audioPlayer.stop() 
                    isRevealed = false 
                    val queue = deckCardQueues[deck.id] 
                    val nextCard = if (queue != null && queue.isNotEmpty()) queue.removeAt(0) else null 
                    deckCardsCache[deck.id] = nextCard 
                    val currentStats = deckStatsCache[deck.id] ?: Triple(deck.newCount, deck.learnCount, deck.reviewCount) 
                    val optNew = if (currentStats.first > 0) currentStats.first - 1 else 0 
                    val optLearn = currentStats.second + 1 
                    val optReview = currentStats.third 
                    deckStatsCache[deck.id] = Triple(optNew, optLearn, optReview) 
                    coroutineScope.launch(Dispatchers.IO) { 
                        ankiHelper.answerCard(card.noteId, card.cardOrd, 1, 5000L, deck.id) 
                        val remainingCount = deckCardQueues[deck.id]?.size ?: 0 
                        if (remainingCount < 3) { 
                            val freshBatch = ankiHelper.getDueCardsForDeck(deck.id, limit = 5, excludeNoteId = nextCard?.noteId, deckName = deck.name) 
                            withContext(Dispatchers.Main) { 
                                val currentQ = deckCardQueues.getOrPut(deck.id) { mutableListOf() } 
                                for (freshCard in freshBatch) { 
                                    if (freshCard.noteId != nextCard?.noteId && currentQ.none { it.noteId == freshCard.noteId }) { 
                                        currentQ.add(freshCard) 
                                    } 
                                } 
                                if (deckCardsCache[deck.id] == null && currentQ.isNotEmpty()) { 
                                    deckCardsCache[deck.id] = currentQ.removeAt(0) 
                                } 
                            } 
                        } 
                        val freshStats = ankiHelper.getDeckStatsForDeck(deck.name) 
                        withContext(Dispatchers.Main) { 
                            deckStatsCache[deck.id] = freshStats 
                        } 
                        if (selectedDecksList.firstOrNull()?.id == deck.id) { 
                            CardSessionManager.refresh(this@MainActivity) 
                        } 
                    } 
                }, 
                onGood = { deck, card -> 
                    audioPlayer.stop() 
                    isRevealed = false 
                    val queue = deckCardQueues[deck.id] 
                    val nextCard = if (queue != null && queue.isNotEmpty()) queue.removeAt(0) else null 
                    deckCardsCache[deck.id] = nextCard 
                    val currentStats = deckStatsCache[deck.id] ?: Triple(deck.newCount, deck.learnCount, deck.reviewCount) 
                    val optNew = if (currentStats.first > 0) currentStats.first - 1 else 0 
                    val optLearn = if (currentStats.second > 0) currentStats.second - 1 else 0 
                    val optReview = if (currentStats.first == 0 && currentStats.second == 0 && currentStats.third > 0) currentStats.third - 1 else currentStats.third 
                    deckStatsCache[deck.id] = Triple(optNew, optLearn, optReview) 
                    coroutineScope.launch(Dispatchers.IO) { 
                        ankiHelper.answerCard(card.noteId, card.cardOrd, 3, 5000L, deck.id) 
                        val remainingCount = deckCardQueues[deck.id]?.size ?: 0 
                        if (remainingCount < 3) { 
                            val freshBatch = ankiHelper.getDueCardsForDeck(deck.id, limit = 5, excludeNoteId = nextCard?.noteId, deckName = deck.name) 
                            withContext(Dispatchers.Main) { 
                                val currentQ = deckCardQueues.getOrPut(deck.id) { mutableListOf() } 
                                for (freshCard in freshBatch) { 
                                    if (freshCard.noteId != nextCard?.noteId && currentQ.none { it.noteId == freshCard.noteId }) { 
                                        currentQ.add(freshCard) 
                                    } 
                                } 
                                if (deckCardsCache[deck.id] == null && currentQ.isNotEmpty()) { 
                                    deckCardsCache[deck.id] = currentQ.removeAt(0) 
                                } 
                            } 
                        } 
                        val freshStats = ankiHelper.getDeckStatsForDeck(deck.name) 
                        withContext(Dispatchers.Main) { 
                            deckStatsCache[deck.id] = freshStats 
                        } 
                        if (selectedDecksList.firstOrNull()?.id == deck.id) { 
                            CardSessionManager.refresh(this@MainActivity) 
                        } 
                    } 
                }, 
                onOpenAnki = { 
                    val launchIntent = ankiHelper.getAnkiLaunchIntent() 
                    startActivity(launchIntent) 
                }, 
                onPlayWord = { card -> 
                    audioPlayer.playWord(card) 
                }, 
                onPlaySentence = { card -> 
                    audioPlayer.playSentence(card) 
                }, 
                onDeckChanged = { deckId -> 
                    val targetDeck = decksState.find { it.id == deckId } 
                    coroutineScope.launch(Dispatchers.IO) { 
                        if (!deckCardsCache.containsKey(deckId)) { 
                            val batch = ankiHelper.getDueCardsForDeck(deckId, limit = 5, deckName = targetDeck?.name) 
                            val firstCard = batch.firstOrNull() 
                            val remaining = if (batch.size > 1) batch.drop(1).toMutableList() else mutableListOf() 
                            withContext(Dispatchers.Main) { 
                                deckCardsCache[deckId] = firstCard 
                                deckCardQueues[deckId] = remaining 
                            } 
                        } 
                        if (targetDeck != null && !deckStatsCache.containsKey(deckId)) { 
                            val deckStats = ankiHelper.getDeckStatsForDeck(targetDeck.name) 
                            withContext(Dispatchers.Main) { 
                                deckStatsCache[deckId] = deckStats 
                            } 
                        } 
                    } 
                } 
            ) 
            
            Row( 
                horizontalArrangement = Arrangement.spacedBy(12.dp), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                QuickAccessHubTile( 
                    icon = Icons.Filled.Palette, 
                    title = "Backgrounds", 
                    subtitle = "Card & App Themes", 
                    accentColor = ShinobiColors.SlateBlue, 
                    containerColor = ShinobiColors.SlateBlueContainer, 
                    onClick = { showBackgroundsSheet = true }, 
                    modifier = Modifier.weight(1f) 
                ) 
                QuickAccessHubTile( 
                    icon = Icons.Filled.Style, 
                    title = "Decks & Sync", 
                    subtitle = if (selectedDeckIds.isEmpty()) "0 Selected" else "${selectedDeckIds.size} Selected", 
                    accentColor = ShinobiColors.WarmOchre, 
                    containerColor = ShinobiColors.WarmOchreContainer, 
                    onClick = { showDecksSheet = true }, 
                    modifier = Modifier.weight(1f) 
                ) 
            } 
            
            Row( 
                horizontalArrangement = Arrangement.spacedBy(12.dp), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                QuickAccessHubTile( 
                    icon = Icons.Filled.Layers, 
                    title = "Lockscreen Style", 
                    subtitle = if (isMusicPlayerStyle) "Music Player" else "Classic Card", 
                    accentColor = ShinobiColors.WisteriaViolet, 
                    containerColor = ShinobiColors.WisteriaVioletContainer, 
                    onClick = { showStyleSheet = true }, 
                    modifier = Modifier.weight(1f) 
                ) 
                QuickAccessHubTile( 
                    icon = Icons.Filled.SmartToy, 
                    title = "AI Story Config", 
                    subtitle = "API Keys & Models", 
                    accentColor = ShinobiColors.MatchaSage, 
                    containerColor = ShinobiColors.MatchaSageContainer, 
                    onClick = onOpenAiConfig, 
                    modifier = Modifier.weight(1f) 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(108.dp)) 
        } 
        
        if (showBackgroundsSheet) { 
            ModalBottomSheet( 
                onDismissRequest = { showBackgroundsSheet = false }, 
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), 
                containerColor = ShinobiColors.BackgroundDeep 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(horizontal = 20.dp) 
                        .padding(bottom = 36.dp) 
                        .verticalScroll(rememberScrollState()), 
                    verticalArrangement = Arrangement.spacedBy(16.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        Text( 
                            "Backgrounds Studio", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = ShinobiColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.weight(1f)) 
                        IconButton(onClick = { showBackgroundsSheet = false }) { 
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = ShinobiColors.TextSecondary) 
                        } 
                    } 
                    
                    ThemeSwitcher( 
                        selectedTheme = AppTheme.fromId(appThemeState), 
                        onThemeSelected = { newTheme -> 
                            appThemeState = newTheme.id 
                            prefs.appTheme = newTheme.id 
                            ShinobiColors.applyTheme(newTheme) 
                        } 
                    ) 
                    
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
                            AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                        }, 
                        onSelectSavedUri = { uriStr -> 
                            customImageUriState = uriStr 
                            prefs.customImageUri = uriStr 
                            backgroundTypeState = "custom" 
                            prefs.backgroundType = "custom" 
                            if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                            AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                        }, 
                        onRemoveSavedUri = { uriStr -> 
                            prefs.removeSavedImageUri(uriStr) 
                            savedImageUrisState = prefs.savedImageUris 
                            customImageUriState = prefs.customImageUri 
                            backgroundTypeState = prefs.backgroundType 
                            if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                            AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                        }, 
                        onPickNewImage = { 
                            imagePickerLauncher.launch("image/*") 
                        }, 
                        onBlurChange = { newRadius -> 
                            blurRadiusState = newRadius 
                        }, 
                        onBlurCommit = { 
                            prefs.blurRadius = blurRadiusState.toInt() 
                            if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                            AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                        }, 
                        onOpacityChange = { newOpacity -> 
                            dimOpacityState = newOpacity 
                        }, 
                        onOpacityCommit = { 
                            prefs.dimOpacity = dimOpacityState 
                            if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                            AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                        }, 
                        onArtworkOpacityChange = { newArtOpacity -> 
                            artworkOpacityState = newArtOpacity 
                        }, 
                        onArtworkOpacityCommit = { 
                            prefs.artworkOpacity = artworkOpacityState 
                            if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                            AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                        } 
                    ) 
                    
                    ModernAppBackgroundCard( 
                        currentType = appBackgroundTypeState, 
                        blurRadius = appBlurRadiusState, 
                        dimOpacity = appDimOpacityState, 
                        artworkOpacity = appArtworkOpacityState, 
                        savedUris = appSavedImageUrisState, 
                        currentUri = appCustomImageUriState, 
                        onSelectType = { type -> 
                            appBackgroundTypeState = type 
                            prefs.appBackgroundType = type 
                        }, 
                        onSelectSavedUri = { uriStr -> 
                            appCustomImageUriState = uriStr 
                            prefs.appCustomImageUri = uriStr 
                            appBackgroundTypeState = "custom" 
                            prefs.appBackgroundType = "custom" 
                        }, 
                        onRemoveSavedUri = { uriStr -> 
                            prefs.removeSavedAppImageUri(uriStr) 
                            appSavedImageUrisState = prefs.appSavedImageUris 
                            appCustomImageUriState = prefs.appCustomImageUri 
                            appBackgroundTypeState = prefs.appBackgroundType 
                        }, 
                        onPickNewImage = { 
                            appImagePickerLauncher.launch("image/*") 
                        }, 
                        onBlurChange = { newRadius -> 
                            appBlurRadiusState = newRadius 
                        }, 
                        onBlurCommit = { 
                            prefs.appBlurRadius = appBlurRadiusState.toInt() 
                        }, 
                        onOpacityChange = { newOpacity -> 
                            appDimOpacityState = newOpacity 
                        }, 
                        onOpacityCommit = { 
                            prefs.appDimOpacity = appDimOpacityState 
                        }, 
                        onArtworkOpacityChange = { newArtOpacity -> 
                            appArtworkOpacityState = newArtOpacity 
                        }, 
                        onArtworkOpacityCommit = { 
                            prefs.appArtworkOpacity = appArtworkOpacityState 
                        } 
                    ) 
                } 
            } 
        } 
        
        if (showDecksSheet) { 
            ModalBottomSheet( 
                onDismissRequest = { showDecksSheet = false }, 
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), 
                containerColor = ShinobiColors.BackgroundDeep 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(horizontal = 20.dp) 
                        .padding(bottom = 36.dp) 
                        .verticalScroll(rememberScrollState()), 
                    verticalArrangement = Arrangement.spacedBy(16.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        Text( 
                            "Decks & Sync", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = ShinobiColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.weight(1f)) 
                        IconButton(onClick = { showDecksSheet = false }) { 
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = ShinobiColors.TextSecondary) 
                        } 
                    } 
                    
                    if (decksState.isNotEmpty()) { 
                        ModernDeckSelectorCard(decksState, selectedDeckIds) { deckId, checked -> 
                            if (checked) 
                                selectedDeckIds.add(deckId) 
                            else 
                                selectedDeckIds.remove(deckId) 
                            prefs.selectedDeckIds = selectedDeckIds.toSet() 
                            CardSessionManager.refresh(this@MainActivity) 
                        } 
                    } 
                    
                    ModernIntervalCard( 
                        updateInterval, 
                        snoozeDuration, 
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
                } 
            } 
        } 
        
        if (showStyleSheet) { 
            ModalBottomSheet( 
                onDismissRequest = { showStyleSheet = false }, 
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), 
                containerColor = ShinobiColors.BackgroundDeep 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(horizontal = 20.dp) 
                        .padding(bottom = 36.dp) 
                        .verticalScroll(rememberScrollState()), 
                    verticalArrangement = Arrangement.spacedBy(16.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        Text( 
                            "Lockscreen Style", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = ShinobiColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.weight(1f)) 
                        IconButton(onClick = { showStyleSheet = false }) { 
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = ShinobiColors.TextSecondary) 
                        } 
                    } 
                    
                    ModernStyleCard(isMusicPlayerStyle) { isMusic -> 
                        isMusicPlayerStyle = isMusic 
                        prefs.isMusicPlayerStyle = isMusic 
                        if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                    } 
                    
                    if (!isMusicPlayerStyle) { 
                        ModernClassicActionCard(classicRevealedAction) { action -> 
                            classicRevealedAction = action 
                            prefs.classicRevealedAction = action 
                            if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    @Composable
    fun ModernHeroCard( 
        isEnabled: Boolean, 
        onToggle: (Boolean) -> Unit 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .clip(ShinobiShapes.SquircleLarge) 
                .background( 
                    Brush.linearGradient( 
                        colors = if (isEnabled) listOf( 
                            ShinobiColors.SurfaceCard2, 
                            ShinobiColors.SlateBlue.copy(alpha = 0.15f), 
                            ShinobiColors.SurfaceCard1 
                        ) else listOf( 
                            ShinobiColors.SurfaceCard1, 
                            ShinobiColors.SurfaceCard2 
                        ) 
                    ) 
                ) 
                .border( 
                    width = 1.2.dp, 
                    brush = if (isEnabled) { 
                        Brush.linearGradient( 
                            0.0f to ShinobiColors.SlateBlue.copy(alpha = 0.85f), 
                            0.25f to ShinobiColors.SlateBlue.copy(alpha = 0.20f), 
                            0.5f to Color.Transparent, 
                            0.75f to ShinobiColors.SlateBlue.copy(alpha = 0.20f), 
                            1.0f to ShinobiColors.SlateBlue.copy(alpha = 0.85f) 
                        ) 
                    } else { 
                        SolidColor(ShinobiColors.CardBorderSubtle) 
                    }, 
                    shape = ShinobiShapes.SquircleLarge 
                ) 
                .padding(horizontal = 20.dp, vertical = 16.dp) 
        ) { 
            Row( 
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                Box( 
                    modifier = Modifier 
                        .size(44.dp) 
                        .clip(ShinobiShapes.SquircleMedium) 
                        .background( 
                            if (isEnabled) ShinobiColors.SlateBlueContainer 
                            else ShinobiColors.SurfaceCard3 
                        ), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Icon( 
                        imageVector = if (isEnabled) Icons.Default.Bolt else Icons.Default.Lock, 
                        contentDescription = null, 
                        tint = if (isEnabled) ShinobiColors.SlateBlue else ShinobiColors.TextMuted, 
                        modifier = Modifier.size(22.dp) 
                    ) 
                } 
                Spacer(modifier = Modifier.width(14.dp)) 
                Column(modifier = Modifier.weight(1f)) { 
                    Text( 
                        if (isEnabled) "Lockscreen Active" else "Lockscreen Inactive", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp, 
                        color = ShinobiColors.TextPrimary 
                    ) 
                    Spacer(modifier = Modifier.height(2.dp)) 
                    Text( 
                        if (isEnabled) "Review cards on your lockscreen" 
                        else "Toggle switch to start practicing", 
                        fontSize = 12.sp, 
                        color = ShinobiColors.TextSecondary 
                    ) 
                } 
                Switch( 
                    checked = isEnabled, 
                    onCheckedChange = onToggle, 
                    colors = SwitchDefaults.colors( 
                        checkedTrackColor = ShinobiColors.SlateBlue, 
                        checkedThumbColor = Color.White, 
                        uncheckedTrackColor = ShinobiColors.SurfaceCard3, 
                        uncheckedThumbColor = ShinobiColors.TextMuted 
                    ) 
                ) 
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
            shape = ShinobiShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = ShinobiColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(ShinobiShapes.SquircleMedium) 
                            .background(ShinobiColors.WisteriaVioletContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.Layers, 
                            contentDescription = null, 
                            tint = ShinobiColors.WisteriaViolet, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Text( 
                        "Lockscreen Layout", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp, 
                        color = ShinobiColors.TextPrimary 
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
                            ), 
                            colors = SegmentedButtonDefaults.colors( 
                                activeContainerColor = ShinobiColors.SlateBlue, 
                                activeContentColor = Color.White, 
                                inactiveContainerColor = ShinobiColors.SurfaceCard2, 
                                inactiveContentColor = ShinobiColors.TextSecondary 
                            ) 
                        ) { 
                            Text(labels[index], fontSize = 12.sp, fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    @OptIn(ExperimentalMaterial3Api::class) 
    @Composable
    fun ModernClassicActionCard(currentAction: String, onSelect: (String) -> Unit) { 
        val options = listOf("suspend", "open_anki", "undo", "open_app") 
        val labels = listOf("Suspend", "Open Anki", "Undo", "Open App") 
        val selectedIndex = options.indexOf(currentAction).coerceAtLeast(0) 
        
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = ShinobiShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = ShinobiColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(ShinobiShapes.SquircleMedium) 
                            .background(ShinobiColors.WarmOchreContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.AutoAwesome, 
                            contentDescription = null, 
                            tint = ShinobiColors.WarmOchre, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Text( 
                        "Classic Action (Revealed)", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp, 
                        color = ShinobiColors.TextPrimary 
                    ) 
                } 
                Spacer(modifier = Modifier.height(14.dp)) 
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                    options.forEachIndexed { index, action -> 
                        SegmentedButton( 
                            selected = index == selectedIndex, 
                            onClick = { onSelect(action) }, 
                            shape = SegmentedButtonDefaults.itemShape( 
                                index = index, 
                                count = options.size 
                            ), 
                            colors = SegmentedButtonDefaults.colors( 
                                activeContainerColor = ShinobiColors.WarmOchre, 
                                activeContentColor = Color.White, 
                                inactiveContainerColor = ShinobiColors.SurfaceCard2, 
                                inactiveContentColor = ShinobiColors.TextSecondary 
                            ) 
                        ) { 
                            Text(labels[index], fontSize = 12.sp, fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    @OptIn(ExperimentalMaterial3Api::class) 
    @Composable
    fun ModernAppBackgroundCard( 
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
        onBlurCommit: () -> Unit, 
        onOpacityChange: (Float) -> Unit, 
        onOpacityCommit: () -> Unit, 
        onArtworkOpacityChange: (Float) -> Unit, 
        onArtworkOpacityCommit: () -> Unit 
    ) { 
        val context = LocalContext.current 
        val options = listOf("blossom", "transparent", "dark_blur", "custom") 
        val labels = listOf("Blossom", "Glass", "Dark Blur", "Custom") 
        val normalizedType = when (currentType) { 
            "anki_lock", "reading" -> "blossom" 
            "sunset" -> "blossom" 
            "none" -> "transparent" 
            else -> currentType 
        } 
        val selectedIndex = options.indexOf(normalizedType).let { if (it >= 0) it else 0 } 
        
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = ShinobiShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = ShinobiColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(ShinobiShapes.SquircleMedium) 
                            .background(ShinobiColors.WisteriaVioletContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.AutoAwesome, 
                            contentDescription = null, 
                            tint = ShinobiColors.WisteriaViolet, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Column { 
                        Text( 
                            "Application Background", 
                            fontWeight = FontWeight.SemiBold, 
                            fontSize = 15.sp, 
                            color = ShinobiColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            "Top backdrop across Cards, Stories & Dojo", 
                            fontSize = 12.sp, 
                            color = ShinobiColors.TextSecondary 
                        ) 
                    } 
                } 
                Spacer(modifier = Modifier.height(14.dp)) 
                LazyRow( 
                    horizontalArrangement = Arrangement.spacedBy(8.dp), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    items(options.indices.toList()) { index -> 
                        val type = options[index] 
                        val isSelected = (index == selectedIndex) 
                        Surface( 
                            onClick = { onSelectType(type) }, 
                            shape = ShinobiShapes.SquircleSmall, 
                            color = if (isSelected) ShinobiColors.SlateBlue else ShinobiColors.SurfaceCard2, 
                            border = BorderStroke( 
                                1.dp, 
                                if (isSelected) ShinobiColors.SlateBlue 
                                else ShinobiColors.CardBorderSubtle 
                            ) 
                        ) { 
                            Text( 
                                text = labels[index], 
                                fontSize = 12.sp, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isSelected) Color.White else ShinobiColors.TextSecondary, 
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp) 
                            ) 
                        } 
                    } 
                } 
                
                if (normalizedType != "transparent") { 
                    Spacer(modifier = Modifier.height(14.dp)) 
                    GlobalSeekerContainer { 
                        GlobalSeekerRow( 
                            icon = Icons.Filled.BlurOn, 
                            label = "Blur Radius", 
                            valueDisplay = "${blurRadius.toInt()}px", 
                            value = blurRadius, 
                            valueRange = 0f..60f, 
                            onValueChange = { newRadius -> 
                                onBlurChange(((newRadius / 5f).roundToInt() * 5f).coerceIn(0f, 60f)) 
                            }, 
                            onValueChangeFinished = onBlurCommit, 
                            accentColor = ShinobiColors.WisteriaViolet 
                        ) 
                        GlobalSeekerRow( 
                            icon = Icons.Filled.Opacity, 
                            label = "Dark Dimming Tint", 
                            valueDisplay = "${(dimOpacity * 100).toInt()}%", 
                            value = dimOpacity, 
                            valueRange = 0.0f..0.9f, 
                            onValueChange = { newOpacity -> 
                                onOpacityChange(((newOpacity * 20f).roundToInt() / 20f).coerceIn(0f, 0.9f)) 
                            }, 
                            onValueChangeFinished = onOpacityCommit, 
                            accentColor = ShinobiColors.WisteriaViolet 
                        ) 
                        GlobalSeekerRow( 
                            icon = Icons.Filled.AutoAwesome, 
                            label = "Artwork Opacity", 
                            valueDisplay = "${(artworkOpacity * 100).toInt()}%", 
                            value = artworkOpacity, 
                            valueRange = 0.1f..1.0f, 
                            onValueChange = { newArtOpacity -> 
                                onArtworkOpacityChange(((newArtOpacity * 20f).roundToInt() / 20f).coerceIn(0.1f, 1.0f)) 
                            }, 
                            onValueChangeFinished = onArtworkOpacityCommit, 
                            accentColor = ShinobiColors.WisteriaViolet 
                        ) 
                    } 
                } 
                
                if (normalizedType == "custom") { 
                    Spacer(modifier = Modifier.height(14.dp)) 
                    if (savedUris.isNotEmpty()) { 
                        Text( 
                            "Saved App Backdrops (${savedUris.size})", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = ShinobiColors.TextSecondary 
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
                                        .clip(ShinobiShapes.SquircleMedium) 
                                        .border( 
                                            2.dp, 
                                            if (isSelected) ShinobiColors.SlateBlue 
                                             else Color.Transparent, 
                                            ShinobiShapes.SquircleMedium 
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
                                            .size(20.dp) 
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
                                                .size(20.dp) 
                                                .clip(CircleShape) 
                                                .background(ShinobiColors.SlateBlue), 
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
                    Squircle3DButton( 
                        onClick = onPickNewImage, 
                        containerColor = ShinobiColors.WisteriaViolet, 
                        bevelColor = ShinobiColors.WisteriaVioletLip, 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center, 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .padding(vertical = 4.dp) 
                        ) { 
                            Icon( 
                                Icons.Filled.AddPhotoAlternate, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = "Add App Background from Phone", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 13.sp 
                            ) 
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
        onBlurCommit: () -> Unit, 
        onOpacityChange: (Float) -> Unit, 
        onOpacityCommit: () -> Unit, 
        onArtworkOpacityChange: (Float) -> Unit, 
        onArtworkOpacityCommit: () -> Unit 
    ) { 
        val context = LocalContext.current 
        val options = listOf("blossom", "transparent", "dark_blur", "custom") 
        val labels = listOf("Blossom", "Glass", "Dark Blur", "Custom") 
        val normalizedType = when (currentType) { 
            "anki_lock", "reading" -> "blossom" 
            "sunset" -> "blossom" 
            "none" -> "transparent" 
            else -> currentType 
        } 
        val selectedIndex = options.indexOf(normalizedType).let { if (it >= 0) it else 0 } 
        
        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = ShinobiShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = ShinobiColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(ShinobiShapes.SquircleMedium) 
                            .background(ShinobiColors.SlateBlueContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.Image, 
                            contentDescription = null, 
                            tint = ShinobiColors.SlateBlue, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Text( 
                        "Card Background", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp, 
                        color = ShinobiColors.TextPrimary 
                    ) 
                } 
                Spacer(modifier = Modifier.height(14.dp)) 
                LazyRow( 
                    horizontalArrangement = Arrangement.spacedBy(8.dp), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    items(options.indices.toList()) { index -> 
                        val type = options[index] 
                        val isSelected = (index == selectedIndex) 
                        Surface( 
                            onClick = { onSelectType(type) }, 
                            shape = ShinobiShapes.SquircleSmall, 
                            color = if (isSelected) ShinobiColors.SlateBlue else ShinobiColors.SurfaceCard2, 
                            border = BorderStroke( 
                                1.dp, 
                                if (isSelected) ShinobiColors.SlateBlue 
                                else ShinobiColors.CardBorderSubtle 
                            ) 
                        ) { 
                            Text( 
                                text = labels[index], 
                                fontSize = 12.sp, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isSelected) Color.White else ShinobiColors.TextSecondary, 
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp) 
                            ) 
                        } 
                    } 
                } 
                
                if (normalizedType != "transparent") { 
                    Spacer(modifier = Modifier.height(14.dp)) 
                    GlobalSeekerContainer { 
                        GlobalSeekerRow( 
                            icon = Icons.Filled.BlurOn, 
                            label = "Blur Radius", 
                            valueDisplay = "${blurRadius.toInt()}px", 
                            value = blurRadius, 
                            valueRange = 5f..60f, 
                            onValueChange = { newRadius -> 
                                onBlurChange(((newRadius / 5f).roundToInt() * 5f).coerceIn(5f, 60f)) 
                            }, 
                            onValueChangeFinished = onBlurCommit, 
                            accentColor = ShinobiColors.SlateBlue 
                        ) 
                        GlobalSeekerRow( 
                            icon = Icons.Filled.Opacity, 
                            label = "Dark Dimming Tint", 
                            valueDisplay = "${(dimOpacity * 100).toInt()}%", 
                            value = dimOpacity, 
                            valueRange = 0.0f..0.9f, 
                            onValueChange = { newOpacity -> 
                                onOpacityChange(((newOpacity * 20f).roundToInt() / 20f).coerceIn(0f, 0.9f)) 
                            }, 
                            onValueChangeFinished = onOpacityCommit, 
                            accentColor = ShinobiColors.SlateBlue 
                        ) 
                        GlobalSeekerRow( 
                            icon = Icons.Filled.AutoAwesome, 
                            label = "Artwork Opacity", 
                            valueDisplay = "${(artworkOpacity * 100).toInt()}%", 
                            value = artworkOpacity, 
                            valueRange = 0.1f..1.0f, 
                            onValueChange = { newArtOpacity -> 
                                onArtworkOpacityChange(((newArtOpacity * 20f).roundToInt() / 20f).coerceIn(0.1f, 1.0f)) 
                            }, 
                            onValueChangeFinished = onArtworkOpacityCommit, 
                            accentColor = ShinobiColors.SlateBlue 
                        ) 
                    } 
                } 
                
                if (normalizedType == "custom") { 
                    Spacer(modifier = Modifier.height(14.dp)) 
                    if (savedUris.isNotEmpty()) { 
                        Text( 
                            "Saved Wallpapers (${savedUris.size})", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = ShinobiColors.TextSecondary 
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
                                        .clip(ShinobiShapes.SquircleMedium) 
                                        .border( 
                                            2.dp, 
                                            if (isSelected) ShinobiColors.SlateBlue 
                                            else Color.Transparent, 
                                            ShinobiShapes.SquircleMedium 
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
                                            .size(20.dp) 
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
                                                .size(20.dp) 
                                                .clip(CircleShape) 
                                                .background(ShinobiColors.SlateBlue), 
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
                    Squircle3DButton( 
                        onClick = onPickNewImage, 
                        containerColor = ShinobiColors.SlateBlue, 
                        bevelColor = ShinobiColors.SlateBlueLip, 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.Center, 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .padding(vertical = 4.dp) 
                        ) { 
                            Icon( 
                                Icons.Filled.AddPhotoAlternate, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = "Add Picture from Phone", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 13.sp 
                            ) 
                        } 
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
        val groupedDecks = remember(decks) { 
            decks.groupBy { deck -> 
                deck.name.substringBefore("::") 
            } 
        } 
        val expandedGroups = remember { mutableStateMapOf<String, Boolean>() } 

        Card( 
            modifier = Modifier.fillMaxWidth(), 
            shape = ShinobiShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = ShinobiColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(ShinobiShapes.SquircleMedium) 
                            .background(ShinobiColors.SlateBlueContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.Style, 
                            contentDescription = null, 
                            tint = ShinobiColors.SlateBlue, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Text( 
                        "Anki Decks", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp, 
                        color = ShinobiColors.TextPrimary 
                    ) 
                } 
                Spacer(modifier = Modifier.height(12.dp)) 
                groupedDecks.forEach { (rootName, groupDecks) -> 
                    val hasSubdecks = groupDecks.size > 1 || groupDecks.any { it.name.contains("::") } 
                    if (!hasSubdecks) { 
                        val deck = groupDecks.first() 
                        val deckIdStr = deck.id.toString() 
                        val isSelected = deckIdStr in selectedIds 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .clip(ShinobiShapes.SquircleSmall) 
                                .clickable { onDeckToggle(deckIdStr, !isSelected) } 
                                .padding(vertical = 6.dp, horizontal = 4.dp) 
                        ) { 
                            Checkbox( 
                                checked = isSelected, 
                                onCheckedChange = { onDeckToggle(deckIdStr, it) }, 
                                colors = CheckboxDefaults.colors( 
                                    checkedColor = ShinobiColors.SlateBlue, 
                                    uncheckedColor = ShinobiColors.TextMuted 
                                ) 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = deck.name, 
                                style = MaterialTheme.typography.bodyMedium, 
                                fontWeight = FontWeight.Medium, 
                                color = ShinobiColors.TextPrimary, 
                                modifier = Modifier.weight(1f) 
                            ) 
                            DueCountInline(deck.newCount, deck.learnCount, deck.reviewCount) 
                        } 
                    } else { 
                        val isExpanded = expandedGroups[rootName] ?: false 
                        val allChecked = groupDecks.all { it.id.toString() in selectedIds } 
                        val groupNew = groupDecks.sumOf { it.newCount } 
                        val groupLearn = groupDecks.sumOf { it.learnCount } 
                        val groupReview = groupDecks.sumOf { it.reviewCount } 

                        Column( 
                            modifier = Modifier 
                                .fillMaxWidth() 
                                .animateContentSize() 
                        ) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                modifier = Modifier 
                                    .fillMaxWidth() 
                                    .clip(ShinobiShapes.SquircleSmall) 
                                    .clickable { expandedGroups[rootName] = !isExpanded } 
                                    .padding(vertical = 6.dp, horizontal = 4.dp) 
                            ) { 
                                Icon( 
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, 
                                    contentDescription = if (isExpanded) "Collapse" else "Expand", 
                                    tint = ShinobiColors.TextSecondary, 
                                    modifier = Modifier.size(20.dp) 
                                ) 
                                Spacer(modifier = Modifier.width(4.dp)) 
                                Checkbox( 
                                    checked = allChecked, 
                                    onCheckedChange = { check -> 
                                        groupDecks.forEach { d -> 
                                            onDeckToggle(d.id.toString(), check) 
                                        } 
                                    }, 
                                    colors = CheckboxDefaults.colors( 
                                        checkedColor = ShinobiColors.SlateBlue, 
                                        uncheckedColor = ShinobiColors.TextMuted 
                                    ) 
                                ) 
                                Spacer(modifier = Modifier.width(6.dp)) 
                                Text( 
                                    text = rootName, 
                                    style = MaterialTheme.typography.bodyMedium, 
                                    fontWeight = FontWeight.SemiBold, 
                                    color = ShinobiColors.TextPrimary, 
                                    modifier = Modifier.weight(1f) 
                                ) 
                                DueCountInline(groupNew, groupLearn, groupReview) 
                            } 

                            if (isExpanded) { 
                                groupDecks.forEach { subdeck -> 
                                    val subdeckIdStr = subdeck.id.toString() 
                                    val isSubdeckSelected = subdeckIdStr in selectedIds 
                                    val displayName = if (subdeck.name == rootName) { 
                                        "Root / Main" 
                                    } else { 
                                        subdeck.name.removePrefix("$rootName::").replace("::", " / ") 
                                    } 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        modifier = Modifier 
                                            .fillMaxWidth() 
                                            .padding(start = 28.dp) 
                                            .clip(ShinobiShapes.SquircleSmall) 
                                            .clickable { onDeckToggle(subdeckIdStr, !isSubdeckSelected) } 
                                            .padding(vertical = 4.dp, horizontal = 4.dp) 
                                    ) { 
                                        Checkbox( 
                                            checked = isSubdeckSelected, 
                                            onCheckedChange = { onDeckToggle(subdeckIdStr, it) }, 
                                            colors = CheckboxDefaults.colors( 
                                                checkedColor = ShinobiColors.SlateBlue, 
                                                uncheckedColor = ShinobiColors.TextMuted 
                                            ) 
                                        ) 
                                        Spacer(modifier = Modifier.width(6.dp)) 
                                        Text( 
                                            text = displayName, 
                                            style = MaterialTheme.typography.bodySmall, 
                                            fontWeight = FontWeight.Normal, 
                                            color = ShinobiColors.TextSecondary, 
                                            modifier = Modifier.weight(1f) 
                                        ) 
                                        DueCountInline(subdeck.newCount, subdeck.learnCount, subdeck.reviewCount) 
                                    } 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
        } 
    } 

    @Composable
    fun DueCountInline(newCount: Int, learnCount: Int, reviewCount: Int) { 
        Row(verticalAlignment = Alignment.CenterVertically) { 
            Text( 
                text = "$newCount", 
                color = Color(0xFF8AB4F8), 
                fontSize = 11.5.sp, 
                fontWeight = FontWeight.Bold 
            ) 
            Text( 
                text = " · ", 
                color = ShinobiColors.TextMuted, 
                fontSize = 11.5.sp 
            ) 
            Text( 
                text = "$learnCount", 
                color = Color(0xFFF28B82), 
                fontSize = 11.5.sp, 
                fontWeight = FontWeight.Bold 
            ) 
            Text( 
                text = " · ", 
                color = ShinobiColors.TextMuted, 
                fontSize = 11.5.sp 
            ) 
            Text( 
                text = "$reviewCount", 
                color = Color(0xFF81C995), 
                fontSize = 11.5.sp, 
                fontWeight = FontWeight.Bold 
            ) 
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
            shape = ShinobiShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = ShinobiColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Text( 
                    "Frequency & Snooze", 
                    fontWeight = FontWeight.SemiBold, 
                    fontSize = 15.sp, 
                    color = ShinobiColors.TextPrimary 
                ) 
                Spacer(modifier = Modifier.height(12.dp)) 
                Text( 
                    "Background Sync Interval", 
                    fontSize = 12.sp, 
                    color = ShinobiColors.TextSecondary 
                ) 
                Spacer(modifier = Modifier.height(6.dp)) 
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                    options.forEachIndexed { index, min -> 
                        SegmentedButton( 
                            selected = index == updateIdx, 
                            onClick = { onUpdateSelect(min) }, 
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size), 
                            colors = SegmentedButtonDefaults.colors( 
                                activeContainerColor = ShinobiColors.SlateBlue, 
                                activeContentColor = Color.White, 
                                inactiveContainerColor = ShinobiColors.SurfaceCard2, 
                                inactiveContentColor = ShinobiColors.TextSecondary 
                            ) 
                        ) { 
                            Text(labels[index], fontSize = 12.sp, fontWeight = if (index == updateIdx) FontWeight.Bold else FontWeight.Normal) 
                        } 
                    } 
                } 
                Spacer(modifier = Modifier.height(14.dp)) 
                Text( 
                    "Snooze Duration", 
                    fontSize = 12.sp, 
                    color = ShinobiColors.TextSecondary 
                ) 
                Spacer(modifier = Modifier.height(6.dp)) 
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { 
                    options.forEachIndexed { index, min -> 
                        SegmentedButton( 
                            selected = index == snoozeIdx, 
                            onClick = { onSnoozeSelect(min) }, 
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size), 
                            colors = SegmentedButtonDefaults.colors( 
                                activeContainerColor = ShinobiColors.SlateBlue, 
                                activeContentColor = Color.White, 
                                inactiveContainerColor = ShinobiColors.SurfaceCard2, 
                                inactiveContentColor = ShinobiColors.TextSecondary 
                            ) 
                        ) { 
                            Text(labels[index], fontSize = 12.sp, fontWeight = if (index == snoozeIdx) FontWeight.Bold else FontWeight.Normal) 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    @Composable 
    fun CompactSliderRow( 
        icon: ImageVector, 
        label: String, 
        valueDisplay: String, 
        value: Float, 
        valueRange: ClosedFloatingPointRange<Float>, 
        steps: Int = 0, 
        onValueChange: (Float) -> Unit, 
        onValueChangeFinished: (() -> Unit)? = null 
    ) { 
        Column(modifier = Modifier.fillMaxWidth()) { 
            Row( 
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                Icon( 
                    icon, 
                    contentDescription = null, 
                    tint = ShinobiColors.SakuraRose, 
                    modifier = Modifier.size(15.dp) 
                ) 
                Spacer(modifier = Modifier.width(6.dp)) 
                Text( 
                    label, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Medium, 
                    color = ShinobiColors.TextSecondary 
                ) 
                Spacer(modifier = Modifier.weight(1f)) 
                Surface( 
                    shape = RoundedCornerShape(6.dp), 
                    color = ShinobiColors.SakuraRoseContainer 
                ) { 
                    Text( 
                        valueDisplay, 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = ShinobiColors.SakuraRose, 
                        maxLines = 1, 
                        softWrap = false, 
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) 
                    ) 
                } 
            } 
            Slider( 
                value = value, 
                onValueChange = onValueChange, 
                onValueChangeFinished = onValueChangeFinished, 
                valueRange = valueRange, 
                steps = steps, 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(28.dp), 
                colors = SliderDefaults.colors( 
                    thumbColor = ShinobiColors.SakuraRose, 
                    activeTrackColor = ShinobiColors.SakuraRose, 
                    inactiveTrackColor = ShinobiColors.CardBorder 
                ) 
            ) 
        } 
    } 
    
    @Composable
    fun QuickAccessHubTile( 
        icon: ImageVector, 
        title: String, 
        subtitle: String, 
        accentColor: Color, 
        containerColor: Color, 
        onClick: () -> Unit, 
        modifier: Modifier = Modifier 
    ) { 
        Surface( 
            onClick = onClick, 
            shape = ShinobiShapes.SquircleLarge, 
            color = ShinobiColors.SurfaceCard1, 
            border = BorderStroke(1.dp, ShinobiColors.CardBorderSubtle), 
            modifier = modifier 
        ) { 
            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(14.dp) 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .size(36.dp) 
                            .clip(ShinobiShapes.SquircleSmall) 
                            .background(containerColor), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            imageVector = icon, 
                            contentDescription = null, 
                            tint = accentColor, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.weight(1f)) 
                    Icon( 
                        imageVector = Icons.Default.ChevronRight, 
                        contentDescription = null, 
                        tint = ShinobiColors.TextMuted, 
                        modifier = Modifier.size(16.dp) 
                    ) 
                } 
                Spacer(modifier = Modifier.height(10.dp)) 
                Text( 
                    text = title, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = ShinobiColors.TextPrimary, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis 
                ) 
                Spacer(modifier = Modifier.height(2.dp)) 
                Text( 
                    text = subtitle, 
                    fontSize = 11.sp, 
                    color = ShinobiColors.TextSecondary, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis 
                ) 
            } 
        } 
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


