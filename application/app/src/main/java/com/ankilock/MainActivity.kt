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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.ankilock.ui.components.SlidingPillSwitcher 
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
import androidx.compose.ui.unit.Velocity
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.ui.cards.DeckCarouselCard
import com.ankilock.ui.components.Squircle3DButton
import com.ankilock.ui.blossom.BlossomBottomNav
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomShapes
import com.ankilock.ui.blossom.BlossomTab
import com.ankilock.ui.blossom.VocabScreen 
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
import com.ankilock.ui.blossom.AppTheme 
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
        BlossomColors.applyTheme(AppTheme.fromId(appThemeState)) 
        
        requestInitialPermissions() 
        
        setContent { 
            AnkiLockTheme { 
                var showLoadingOverlay by remember { mutableStateOf(true) } 
                var isAppLoading by remember { mutableStateOf(true) } 
                LaunchedEffect(Unit) { 
                    kotlinx.coroutines.delay(2400) 
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
                            BlossomColors.BackgroundDeep.copy(alpha = 0.95f), 
                            BlossomColors.BackgroundDeep.copy(alpha = 0.85f), 
                            BlossomColors.BackgroundDeep.copy(alpha = 0.45f), 
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
                    color = BlossomColors.TextPrimary 
                ) 
                
                Spacer(modifier = Modifier.weight(1f)) 
                
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(8.dp) 
                ) { 
                    Surface( 
                        shape = BlossomShapes.SquircleSmall, 
                        color = BlossomColors.SurfaceCard1, 
                        border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
                    ) { 
                        Row( 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                            verticalAlignment = Alignment.CenterVertically 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.LocalFireDepartment, 
                                contentDescription = "Streak", 
                                tint = if (isStreakActive) BlossomColors.WarmOchre else BlossomColors.TextMuted, 
                                modifier = Modifier.size(14.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(4.dp)) 
                            Text( 
                                text = "$streakCount", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = BlossomColors.TextPrimary 
                            ) 
                        } 
                    } 
                    
                    Surface( 
                        shape = BlossomShapes.SquircleSmall, 
                        color = BlossomColors.SurfaceCard1, 
                        border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
                    ) { 
                        Row( 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                            verticalAlignment = Alignment.CenterVertically 
                        ) { 
                            Icon( 
                                imageVector = Icons.AutoMirrored.Filled.MenuBook, 
                                contentDescription = "Stories Completed", 
                                tint = BlossomColors.MatchaSage, 
                                modifier = Modifier.size(13.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(4.dp)) 
                            Text( 
                                text = "$completedStoriesCount", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = BlossomColors.TextPrimary 
                            ) 
                        } 
                    } 
                    
                    Surface( 
                        shape = BlossomShapes.SquircleSmall, 
                        color = BlossomColors.SurfaceCard1, 
                        border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
                    ) { 
                        Row( 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                            verticalAlignment = Alignment.CenterVertically 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.Bolt, 
                                contentDescription = "Cards Due", 
                                tint = BlossomColors.SlateBlue, 
                                modifier = Modifier.size(14.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(4.dp)) 
                            Text( 
                                text = "$dueCardsCount", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = BlossomColors.TextPrimary 
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
                                color = BlossomColors.SlateBlue, 
                                strokeWidth = 2.dp 
                            ) 
                        } else { 
                            Icon( 
                                imageVector = Icons.Default.Refresh, 
                                contentDescription = "Refresh", 
                                tint = BlossomColors.TextSecondary, 
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
        val tabs = remember { BlossomTab.values() } 
        var selectedBlossomTab by remember { mutableStateOf(BlossomTab.CARDS) } 
        val pagerState = rememberPagerState(initialPage = selectedBlossomTab.ordinal) { tabs.size } 
        var showAiConfigDialog by remember { mutableStateOf(false) } 
        var isRefreshing by remember { mutableStateOf(false) } 
        var stats by remember { mutableStateOf(CardSessionManager.currentStats) } 
        var streakCount by remember { mutableIntStateOf(prefs.dailyStreakCount) } 
        var isStreakActive by remember { mutableStateOf(prefs.isStreakCompletedToday) } 
        var completedStoriesCount by remember { mutableIntStateOf(prefs.completedStoryIds.size) } 
        var jishoTargetQuery by remember { mutableStateOf("") } 
        val coroutineScope = androidx.compose.runtime.rememberCoroutineScope() 
        
        LaunchedEffect(pagerState.currentPage) { 
            audioPlayer.stop() 
            selectedBlossomTab = tabs[pagerState.currentPage] 
        } 
        
        val isStoryReaderActive = (selectedBlossomTab == BlossomTab.STORIES && StorySessionManager.currentStory != null) 
        val isStoryDetailsActive = (selectedBlossomTab == BlossomTab.STORIES && StorySessionManager.selectedStoryForDetails != null) 
        
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
                .background(BlossomColors.BackgroundDeep) 
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
                        BlossomTab.CARDS -> ModernSettingsScreen( 
                            padding = PaddingValues(0.dp), 
                            onOpenAiConfig = { showAiConfigDialog = true } 
                        ) 
                        BlossomTab.STORIES -> com.ankilock.ui.reading.ReadingScreen( 
                            padding = PaddingValues( 
                                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 58.dp, 
                                bottom = 96.dp 
                            ), 
                            prefs = prefs, 
                            hasAnkiPermission = hasPermissionState, 
                            onNavigateToJisho = { word -> 
                                jishoTargetQuery = word 
                                coroutineScope.launch { 
                                    pagerState.animateScrollToPage(BlossomTab.JISHO.ordinal) 
                                } 
                            } 
                        ) 
                        BlossomTab.JISHO -> com.ankilock.ui.jisho.JishoScreen( 
                            padding = PaddingValues( 
                                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 58.dp, 
                                bottom = 96.dp 
                            ), 
                            prefs = prefs, 
                            initialQuery = jishoTargetQuery, 
                            onInitialQueryConsumed = { jishoTargetQuery = "" } 
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
                                        BlossomColors.BackgroundDeep.copy(alpha = 0.50f), 
                                        BlossomColors.BackgroundDeep.copy(alpha = 0.88f), 
                                        BlossomColors.BackgroundDeep.copy(alpha = 0.98f) 
                                    ) 
                                ) 
                            ) 
                    ) { 
                        BlossomBottomNav( 
                            selectedTab = selectedBlossomTab, 
                            onTabSelected = { tab -> 
                                audioPlayer.stop() 
                                selectedBlossomTab = tab 
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
        var autoPlayMode by remember { mutableIntStateOf(prefs.autoPlayAudioMode) } 
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
        val lastAnsweredCardByDeck = remember { mutableStateMapOf<Long, CardInfo>() } 
        val lastAnsweredStatsByDeck = remember { mutableStateMapOf<Long, Triple<Int, Int, Int>>() } 
        val coroutineScope = androidx.compose.runtime.rememberCoroutineScope() 
        
        DisposableEffect(Unit) { 
            val listener = { 
                activeCard = CardSessionManager.currentCard 
                isRevealed = CardSessionManager.isRevealed 
                stats = CardSessionManager.currentStats 
                val updatedDecks = ankiHelper.getDeckList() 
                decksState = updatedDecks 
                for (d in updatedDecks) { 
                    deckStatsCache[d.id] = Triple(d.newCount, d.learnCount, d.reviewCount) 
                } 
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
                        withContext(Dispatchers.Main) { 
                            deckStatsCache[deck.id] = Triple(deck.newCount, deck.learnCount, deck.reviewCount) 
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
                autoPlayMode = autoPlayMode, 
                onToggleAutoPlay = { mode -> 
                    autoPlayMode = mode 
                    prefs.autoPlayAudioMode = mode 
                }, 
                isPlayingWord = (audioPlayer.currentPlayingTrack == AudioTrackPlaying.WORD), 
                isPlayingSentence = (audioPlayer.currentPlayingTrack == AudioTrackPlaying.SENTENCE), 
                onToggleReveal = { card, willReveal -> 
                    isRevealed = willReveal 
                    if (willReveal) { 
                        CardSessionManager.reveal(this@MainActivity) 
                        val targetCard = card ?: activeCard 
                        if (targetCard != null) { 
                            when (autoPlayMode) { 
                                1 -> audioPlayer.playWord(targetCard) 
                                2 -> audioPlayer.playSentence(targetCard) 
                                3 -> audioPlayer.playSequence(targetCard) 
                            } 
                        } 
                    } else { 
                        CardSessionManager.hide(this@MainActivity) 
                    } 
                }, 
                onRefresh = { 
                    audioPlayer.stop() 
                    isRevealed = false 
                    CardSessionManager.refresh(this@MainActivity) 
                    coroutineScope.launch(Dispatchers.IO) { 
                        val freshDecks = ankiHelper.getDeckList() 
                        val decksToLoad = freshDecks.filter { it.id.toString() in selectedDeckIds } 
                        for (deck in decksToLoad) { 
                            val batch = ankiHelper.getDueCardsForDeck(deck.id, limit = 5, deckName = deck.name) 
                            val firstCard = batch.firstOrNull() 
                            val remaining = if (batch.size > 1) batch.drop(1).toMutableList() else mutableListOf() 
                            val deckStats = Triple(deck.newCount, deck.learnCount, deck.reviewCount) 
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
                    val currentStats = deckStatsCache[deck.id] ?: Triple(deck.newCount, deck.learnCount, deck.reviewCount) 
                    lastAnsweredCardByDeck[deck.id] = card 
                    lastAnsweredStatsByDeck[deck.id] = currentStats 
                    CardSessionManager.recordAnswered(card, currentStats) 
                    val queue = deckCardQueues[deck.id] 
                    val nextCard = if (queue != null && queue.isNotEmpty()) queue.removeAt(0) else null 
                    deckCardsCache[deck.id] = nextCard 
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
                        val freshDecks = ankiHelper.getDeckList() 
                        val freshStats = freshDecks.find { it.id == deck.id }?.let { Triple(it.newCount, it.learnCount, it.reviewCount) } 
                            ?: ankiHelper.getDeckStatsForDeck(deck.name) 
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
                    val currentStats = deckStatsCache[deck.id] ?: Triple(deck.newCount, deck.learnCount, deck.reviewCount) 
                    lastAnsweredCardByDeck[deck.id] = card 
                    lastAnsweredStatsByDeck[deck.id] = currentStats 
                    CardSessionManager.recordAnswered(card, currentStats) 
                    val queue = deckCardQueues[deck.id] 
                    val nextCard = if (queue != null && queue.isNotEmpty()) queue.removeAt(0) else null 
                    deckCardsCache[deck.id] = nextCard 
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
                        val freshDecks = ankiHelper.getDeckList() 
                        val freshStats = freshDecks.find { it.id == deck.id }?.let { Triple(it.newCount, it.learnCount, it.reviewCount) } 
                            ?: ankiHelper.getDeckStatsForDeck(deck.name) 
                        withContext(Dispatchers.Main) { 
                            deckStatsCache[deck.id] = freshStats 
                        } 
                        if (selectedDecksList.firstOrNull()?.id == deck.id) { 
                            CardSessionManager.refresh(this@MainActivity) 
                        } 
                    } 
                }, 
                classicAction = classicRevealedAction, 
                onClassicAction = { deck, card -> 
                    when (classicRevealedAction) { 
                        "suspend" -> { 
                            audioPlayer.stop() 
                            isRevealed = false 
                            val queue = deckCardQueues[deck.id] 
                            val nextCard = if (queue != null && queue.isNotEmpty()) queue.removeAt(0) else null 
                            deckCardsCache[deck.id] = nextCard 
                            coroutineScope.launch(Dispatchers.IO) { 
                                ankiHelper.suspendCard(card.noteId, card.cardOrd) 
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
                                val freshDecks = ankiHelper.getDeckList() 
                                val freshStats = freshDecks.find { it.id == deck.id }?.let { Triple(it.newCount, it.learnCount, it.reviewCount) } 
                                    ?: ankiHelper.getDeckStatsForDeck(deck.name) 
                                withContext(Dispatchers.Main) { 
                                    deckStatsCache[deck.id] = freshStats 
                                } 
                                if (selectedDecksList.firstOrNull()?.id == deck.id) { 
                                    CardSessionManager.refresh(this@MainActivity) 
                                } 
                            } 
                        } 
                        "undo" -> { 
                            val restoredCard = lastAnsweredCardByDeck.remove(deck.id) 
                            val restoredStats = lastAnsweredStatsByDeck.remove(deck.id) 
                            if (restoredCard != null) { 
                                val currentVisibleCard = deckCardsCache[deck.id] 
                                if (currentVisibleCard != null) { 
                                    val q = deckCardQueues.getOrPut(deck.id) { mutableListOf() } 
                                    q.add(0, currentVisibleCard) 
                                } 
                                deckCardsCache[deck.id] = restoredCard 
                                if (restoredStats != null) { 
                                    deckStatsCache[deck.id] = restoredStats 
                                } 
                                isRevealed = false 
                                CardSessionManager.undoLastReview(this@MainActivity) 
                            } else { 
                                CardSessionManager.undoLastReview(this@MainActivity) 
                                coroutineScope.launch(Dispatchers.IO) { 
                                    val freshDecks = ankiHelper.getDeckList() 
                                    val freshStats = freshDecks.find { it.id == deck.id }?.let { Triple(it.newCount, it.learnCount, it.reviewCount) } 
                                        ?: ankiHelper.getDeckStatsForDeck(deck.name) 
                                    val batch = ankiHelper.getDueCardsForDeck(deck.id, limit = 5, deckName = deck.name) 
                                    val firstCard = batch.firstOrNull() 
                                    val remaining = if (batch.size > 1) batch.drop(1).toMutableList() else mutableListOf() 
                                    withContext(Dispatchers.Main) { 
                                        deckCardsCache[deck.id] = firstCard 
                                        deckCardQueues[deck.id] = remaining 
                                        deckStatsCache[deck.id] = freshStats 
                                    } 
                                } 
                            } 
                        } 
                        "open_app" -> { 
                            val intent = Intent(this@MainActivity, MainActivity::class.java).apply { 
                                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT 
                            } 
                            startActivity(intent) 
                        } 
                        else -> { 
                            val launchIntent = ankiHelper.getAnkiLaunchIntent() 
                            startActivity(launchIntent) 
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
                    isRevealed = false 
                    CardSessionManager.hide(this@MainActivity) 
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
                            withContext(Dispatchers.Main) { 
                                deckStatsCache[deckId] = Triple(targetDeck.newCount, targetDeck.learnCount, targetDeck.reviewCount) 
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
                    accentColor = BlossomColors.SlateBlue, 
                    containerColor = BlossomColors.SlateBlueContainer, 
                    onClick = { showBackgroundsSheet = true }, 
                    modifier = Modifier.weight(1f) 
                ) 
                QuickAccessHubTile( 
                    icon = Icons.Filled.Style, 
                    title = "Decks & Sync", 
                    subtitle = if (selectedDeckIds.isEmpty()) "0 Selected" else "${selectedDeckIds.size} Selected", 
                    accentColor = BlossomColors.WarmOchre, 
                    containerColor = BlossomColors.WarmOchreContainer, 
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
                    title = "Lockscreen Config", 
                    subtitle = if (isMusicPlayerStyle) "Music Player" else "Classic Card", 
                    accentColor = BlossomColors.WisteriaViolet, 
                    containerColor = BlossomColors.WisteriaVioletContainer, 
                    onClick = { showStyleSheet = true }, 
                    modifier = Modifier.weight(1f) 
                ) 
                QuickAccessHubTile( 
                    icon = Icons.Filled.SmartToy, 
                    title = "API Config", 
                    subtitle = "API Keys & Models", 
                    accentColor = BlossomColors.MatchaSage, 
                    containerColor = BlossomColors.MatchaSageContainer, 
                    onClick = onOpenAiConfig, 
                    modifier = Modifier.weight(1f) 
                ) 
            } 
            
            Spacer(modifier = Modifier.height(108.dp)) 
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
                override suspend fun onPostFling( 
                    consumed: Velocity, 
                    available: Velocity 
                ): Velocity { 
                    return Velocity(0f, available.y) 
                } 
            } 
        } 
        
        if (showBackgroundsSheet) { 
            ModalBottomSheet( 
                onDismissRequest = { showBackgroundsSheet = false }, 
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), 
                containerColor = BlossomColors.BackgroundDeep, 
                windowInsets = WindowInsets(0) 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .fillMaxHeight(0.88f) 
                        .nestedScroll(noBounceNestedScroll) 
                        .verticalScroll(rememberScrollState()) 
                        .padding(horizontal = 20.dp) 
                        .navigationBarsPadding() 
                        .padding(bottom = 16.dp), 
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
                            color = BlossomColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.weight(1f)) 
                        IconButton(onClick = { showBackgroundsSheet = false }) { 
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = BlossomColors.TextSecondary) 
                        } 
                    } 
                    
                    ThemeSwitcher( 
                        selectedTheme = AppTheme.fromId(appThemeState), 
                        onThemeSelected = { newTheme -> 
                            appThemeState = newTheme.id 
                            prefs.appTheme = newTheme.id 
                            BlossomColors.applyTheme(newTheme) 
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
                            coroutineScope.launch(Dispatchers.IO) { 
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                                AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                            } 
                        }, 
                        onSelectSavedUri = { uriStr -> 
                            customImageUriState = uriStr 
                            prefs.customImageUri = uriStr 
                            backgroundTypeState = "custom" 
                            prefs.backgroundType = "custom" 
                            coroutineScope.launch(Dispatchers.IO) { 
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                                AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                            } 
                        }, 
                        onRemoveSavedUri = { uriStr -> 
                            prefs.removeSavedImageUri(uriStr) 
                            savedImageUrisState = prefs.savedImageUris 
                            customImageUriState = prefs.customImageUri 
                            backgroundTypeState = prefs.backgroundType 
                            coroutineScope.launch(Dispatchers.IO) { 
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                                AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                            } 
                        }, 
                        onPickNewImage = { 
                            imagePickerLauncher.launch("image/*") 
                        }, 
                        onBlurChange = { newRadius -> 
                            blurRadiusState = newRadius 
                        }, 
                        onBlurCommit = { 
                            prefs.blurRadius = blurRadiusState.toInt() 
                            coroutineScope.launch(Dispatchers.IO) { 
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                                AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                            } 
                        }, 
                        onOpacityChange = { newOpacity -> 
                            dimOpacityState = newOpacity 
                        }, 
                        onOpacityCommit = { 
                            prefs.dimOpacity = dimOpacityState 
                            coroutineScope.launch(Dispatchers.IO) { 
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                                AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                            } 
                        }, 
                        onArtworkOpacityChange = { newArtOpacity -> 
                            artworkOpacityState = newArtOpacity 
                        }, 
                        onArtworkOpacityCommit = { 
                            prefs.artworkOpacity = artworkOpacityState 
                            coroutineScope.launch(Dispatchers.IO) { 
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                                AnkiAppWidgetProvider.updateAllWidgets(this@MainActivity) 
                            } 
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
                containerColor = BlossomColors.BackgroundDeep, 
                windowInsets = WindowInsets(0) 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .nestedScroll(noBounceNestedScroll) 
                        .verticalScroll(rememberScrollState()) 
                        .padding(horizontal = 20.dp) 
                        .navigationBarsPadding() 
                        .padding(bottom = 16.dp), 
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
                            color = BlossomColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.weight(1f)) 
                        IconButton(onClick = { showDecksSheet = false }) { 
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = BlossomColors.TextSecondary) 
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
                } 
            } 
        } 
        
        if (showStyleSheet) { 
            ModalBottomSheet( 
                onDismissRequest = { showStyleSheet = false }, 
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), 
                containerColor = BlossomColors.BackgroundDeep, 
                windowInsets = WindowInsets(0) 
            ) { 
                Column( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .nestedScroll(noBounceNestedScroll) 
                        .verticalScroll(rememberScrollState()) 
                        .padding(horizontal = 20.dp) 
                        .navigationBarsPadding() 
                        .padding(bottom = 16.dp), 
                    verticalArrangement = Arrangement.spacedBy(16.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.fillMaxWidth() 
                    ) { 
                        Text( 
                            "Lockscreen Configuration", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.weight(1f)) 
                        IconButton(onClick = { showStyleSheet = false }) { 
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = BlossomColors.TextSecondary) 
                        } 
                    } 
                    
                    ModernStyleCard(isMusicPlayerStyle) { isMusic -> 
                        isMusicPlayerStyle = isMusic 
                        prefs.isMusicPlayerStyle = isMusic 
                        coroutineScope.launch(Dispatchers.IO) { 
                            if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                        } 
                    } 
                    
                    if (!isMusicPlayerStyle) { 
                        ModernClassicActionCard(classicRevealedAction) { action -> 
                            classicRevealedAction = action 
                            prefs.classicRevealedAction = action 
                            coroutineScope.launch(Dispatchers.IO) { 
                                if (isEnabled) AnkiNotificationService.update(this@MainActivity) 
                            } 
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
    } 
    
    @Composable
    fun ModernHeroCard( 
        isEnabled: Boolean, 
        onToggle: (Boolean) -> Unit 
    ) { 
        Box( 
            modifier = Modifier 
                .fillMaxWidth() 
                .clip(BlossomShapes.SquircleLarge) 
                .background( 
                    Brush.linearGradient( 
                        colors = if (isEnabled) listOf( 
                            BlossomColors.SurfaceCard2, 
                            BlossomColors.SlateBlue.copy(alpha = 0.15f), 
                            BlossomColors.SurfaceCard1 
                        ) else listOf( 
                            BlossomColors.SurfaceCard1, 
                            BlossomColors.SurfaceCard2 
                        ) 
                    ) 
                ) 
                .border( 
                    width = 1.2.dp, 
                    brush = if (isEnabled) { 
                        Brush.linearGradient( 
                            0.0f to BlossomColors.SlateBlue.copy(alpha = 0.85f), 
                            0.25f to BlossomColors.SlateBlue.copy(alpha = 0.20f), 
                            0.5f to Color.Transparent, 
                            0.75f to BlossomColors.SlateBlue.copy(alpha = 0.20f), 
                            1.0f to BlossomColors.SlateBlue.copy(alpha = 0.85f) 
                        ) 
                    } else { 
                        SolidColor(BlossomColors.CardBorderSubtle) 
                    }, 
                    shape = BlossomShapes.SquircleLarge 
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
                        .clip(BlossomShapes.SquircleMedium) 
                        .background( 
                            if (isEnabled) BlossomColors.SlateBlueContainer 
                            else BlossomColors.SurfaceCard3 
                        ), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Icon( 
                        imageVector = if (isEnabled) Icons.Default.Bolt else Icons.Default.Lock, 
                        contentDescription = null, 
                        tint = if (isEnabled) BlossomColors.SlateBlue else BlossomColors.TextMuted, 
                        modifier = Modifier.size(22.dp) 
                    ) 
                } 
                Spacer(modifier = Modifier.width(14.dp)) 
                Column(modifier = Modifier.weight(1f)) { 
                    Text( 
                        if (isEnabled) "Lockscreen Active" else "Lockscreen Inactive", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp, 
                        color = BlossomColors.TextPrimary 
                    ) 
                    Spacer(modifier = Modifier.height(2.dp)) 
                    Text( 
                        if (isEnabled) "Review cards on your lockscreen" 
                        else "Toggle switch to start practicing", 
                        fontSize = 12.sp, 
                        color = BlossomColors.TextSecondary 
                    ) 
                } 
                Switch( 
                    checked = isEnabled, 
                    onCheckedChange = onToggle, 
                    colors = SwitchDefaults.colors( 
                        checkedTrackColor = BlossomColors.SlateBlue, 
                        checkedThumbColor = Color.White, 
                        uncheckedTrackColor = BlossomColors.SurfaceCard3, 
                        uncheckedThumbColor = BlossomColors.TextMuted 
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
            shape = BlossomShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = BlossomColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(BlossomShapes.SquircleMedium) 
                            .background(BlossomColors.WisteriaVioletContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.Layers, 
                            contentDescription = null, 
                            tint = BlossomColors.WisteriaViolet, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Text( 
                        "Lockscreen Layout", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp, 
                        color = BlossomColors.TextPrimary 
                    ) 
                } 
                Spacer(modifier = Modifier.height(14.dp)) 
                SlidingPillSwitcher( 
                    options = options, 
                    selectedOption = isMusicStyle, 
                    onOptionSelected = onSelect, 
                    labelProvider = { if (it) "Music Layout" else "Classic Layout" }, 
                    activeColor = BlossomColors.SlateBlue 
                ) 
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
            shape = BlossomShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = BlossomColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(BlossomShapes.SquircleMedium) 
                            .background(BlossomColors.WarmOchreContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.AutoAwesome, 
                            contentDescription = null, 
                            tint = BlossomColors.WarmOchre, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Text( 
                        "Classic Action (Revealed)", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp, 
                        color = BlossomColors.TextPrimary 
                    ) 
                } 
                Spacer(modifier = Modifier.height(14.dp)) 
                SlidingPillSwitcher( 
                    options = options, 
                    selectedOption = currentAction, 
                    onOptionSelected = onSelect, 
                    labelProvider = { labels.getOrNull(options.indexOf(it)) ?: it }, 
                    activeColor = BlossomColors.WarmOchre 
                ) 
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
            shape = BlossomShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = BlossomColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(BlossomShapes.SquircleMedium) 
                            .background(BlossomColors.WisteriaVioletContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.AutoAwesome, 
                            contentDescription = null, 
                            tint = BlossomColors.WisteriaViolet, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Column { 
                        Text( 
                            "Application Background", 
                            fontWeight = FontWeight.SemiBold, 
                            fontSize = 15.sp, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            "Top backdrop across Cards, Stories & Dojo", 
                            fontSize = 12.sp, 
                            color = BlossomColors.TextSecondary 
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
                            shape = BlossomShapes.SquircleSmall, 
                            color = if (isSelected) BlossomColors.SlateBlue else BlossomColors.SurfaceCard2, 
                            border = BorderStroke( 
                                1.dp, 
                                if (isSelected) BlossomColors.SlateBlue 
                                else BlossomColors.CardBorderSubtle 
                            ) 
                        ) { 
                            Text( 
                                text = labels[index], 
                                fontSize = 12.sp, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isSelected) Color.White else BlossomColors.TextSecondary, 
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
                            accentColor = BlossomColors.WisteriaViolet, 
                            snapValues = listOf(0f, 5f, 10f, 15f, 20f, 25f, 30f, 35f, 40f, 45f, 50f, 55f, 60f) 
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
                            accentColor = BlossomColors.WisteriaViolet, 
                            snapValues = listOf(0f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.45f, 0.50f, 0.55f, 0.60f, 0.65f, 0.70f, 0.75f, 0.80f, 0.85f, 0.90f) 
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
                            accentColor = BlossomColors.WisteriaViolet, 
                            snapValues = listOf(0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.45f, 0.50f, 0.55f, 0.60f, 0.65f, 0.70f, 0.75f, 0.80f, 0.85f, 0.90f, 0.95f, 1.0f) 
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
                            color = BlossomColors.TextSecondary 
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
                                        .clip(BlossomShapes.SquircleMedium) 
                                        .border( 
                                            2.dp, 
                                            if (isSelected) BlossomColors.SlateBlue 
                                             else Color.Transparent, 
                                            BlossomShapes.SquircleMedium 
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
                                                .background(BlossomColors.SlateBlue), 
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
                        containerColor = BlossomColors.WisteriaViolet, 
                        bevelColor = BlossomColors.WisteriaVioletLip, 
                        modifier = Modifier.fillMaxWidth().height(48.dp) 
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
                                fontWeight = FontWeight.SemiBold, 
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
            shape = BlossomShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = BlossomColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(BlossomShapes.SquircleMedium) 
                            .background(BlossomColors.SlateBlueContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.Image, 
                            contentDescription = null, 
                            tint = BlossomColors.SlateBlue, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Text( 
                        "Card Background", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp, 
                        color = BlossomColors.TextPrimary 
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
                            shape = BlossomShapes.SquircleSmall, 
                            color = if (isSelected) BlossomColors.SlateBlue else BlossomColors.SurfaceCard2, 
                            border = BorderStroke( 
                                1.dp, 
                                if (isSelected) BlossomColors.SlateBlue 
                                else BlossomColors.CardBorderSubtle 
                            ) 
                        ) { 
                            Text( 
                                text = labels[index], 
                                fontSize = 12.sp, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                                color = if (isSelected) Color.White else BlossomColors.TextSecondary, 
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
                            accentColor = BlossomColors.SlateBlue, 
                            snapValues = listOf(5f, 10f, 15f, 20f, 25f, 30f, 35f, 40f, 45f, 50f, 55f, 60f) 
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
                            accentColor = BlossomColors.SlateBlue, 
                            snapValues = listOf(0f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.45f, 0.50f, 0.55f, 0.60f, 0.65f, 0.70f, 0.75f, 0.80f, 0.85f, 0.90f) 
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
                            accentColor = BlossomColors.SlateBlue, 
                            snapValues = listOf(0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.45f, 0.50f, 0.55f, 0.60f, 0.65f, 0.70f, 0.75f, 0.80f, 0.85f, 0.90f, 0.95f, 1.0f) 
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
                            color = BlossomColors.TextSecondary 
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
                                        .clip(BlossomShapes.SquircleMedium) 
                                        .border( 
                                            2.dp, 
                                            if (isSelected) BlossomColors.SlateBlue 
                                            else Color.Transparent, 
                                            BlossomShapes.SquircleMedium 
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
                                                .background(BlossomColors.SlateBlue), 
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
                        containerColor = BlossomColors.SlateBlue, 
                        bevelColor = BlossomColors.SlateBlueLip, 
                        modifier = Modifier.fillMaxWidth().height(48.dp) 
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
                                fontWeight = FontWeight.SemiBold, 
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
            shape = BlossomShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = BlossomColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box( 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(BlossomShapes.SquircleMedium) 
                            .background(BlossomColors.SlateBlueContainer), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            Icons.Filled.Style, 
                            contentDescription = null, 
                            tint = BlossomColors.SlateBlue, 
                            modifier = Modifier.size(19.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Text( 
                        "Anki Decks", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 15.sp, 
                        color = BlossomColors.TextPrimary 
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
                                .clip(BlossomShapes.SquircleSmall) 
                                .clickable { onDeckToggle(deckIdStr, !isSelected) } 
                                .padding(vertical = 6.dp, horizontal = 4.dp) 
                        ) { 
                            Checkbox( 
                                checked = isSelected, 
                                onCheckedChange = { onDeckToggle(deckIdStr, it) }, 
                                colors = CheckboxDefaults.colors( 
                                    checkedColor = BlossomColors.SlateBlue, 
                                    uncheckedColor = BlossomColors.TextMuted 
                                ) 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = deck.name, 
                                style = MaterialTheme.typography.bodyMedium, 
                                fontWeight = FontWeight.Medium, 
                                color = BlossomColors.TextPrimary, 
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
                                    .clip(BlossomShapes.SquircleSmall) 
                                    .clickable { expandedGroups[rootName] = !isExpanded } 
                                    .padding(vertical = 6.dp, horizontal = 4.dp) 
                            ) { 
                                Icon( 
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, 
                                    contentDescription = if (isExpanded) "Collapse" else "Expand", 
                                    tint = BlossomColors.TextSecondary, 
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
                                        checkedColor = BlossomColors.SlateBlue, 
                                        uncheckedColor = BlossomColors.TextMuted 
                                    ) 
                                ) 
                                Spacer(modifier = Modifier.width(6.dp)) 
                                Text( 
                                    text = rootName, 
                                    style = MaterialTheme.typography.bodyMedium, 
                                    fontWeight = FontWeight.SemiBold, 
                                    color = BlossomColors.TextPrimary, 
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
                                            .clip(BlossomShapes.SquircleSmall) 
                                            .clickable { onDeckToggle(subdeckIdStr, !isSubdeckSelected) } 
                                            .padding(vertical = 4.dp, horizontal = 4.dp) 
                                    ) { 
                                        Checkbox( 
                                            checked = isSubdeckSelected, 
                                            onCheckedChange = { onDeckToggle(subdeckIdStr, it) }, 
                                            colors = CheckboxDefaults.colors( 
                                                checkedColor = BlossomColors.SlateBlue, 
                                                uncheckedColor = BlossomColors.TextMuted 
                                            ) 
                                        ) 
                                        Spacer(modifier = Modifier.width(6.dp)) 
                                        Text( 
                                            text = displayName, 
                                            style = MaterialTheme.typography.bodySmall, 
                                            fontWeight = FontWeight.Normal, 
                                            color = BlossomColors.TextSecondary, 
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
                color = BlossomColors.TextMuted, 
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
                color = BlossomColors.TextMuted, 
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
            shape = BlossomShapes.SquircleLarge, 
            colors = CardDefaults.cardColors( 
                containerColor = BlossomColors.SurfaceCard1 
            ), 
            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle) 
        ) { 
            Column(modifier = Modifier.padding(18.dp)) { 
                Text( 
                    "Frequency & Snooze", 
                    fontWeight = FontWeight.SemiBold, 
                    fontSize = 15.sp, 
                    color = BlossomColors.TextPrimary 
                ) 
                Spacer(modifier = Modifier.height(12.dp)) 
                Text( 
                    "Background Sync Interval", 
                    fontSize = 12.sp, 
                    color = BlossomColors.TextSecondary 
                ) 
                Spacer(modifier = Modifier.height(6.dp)) 
                SlidingPillSwitcher( 
                    options = options, 
                    selectedOption = options.getOrElse(updateIdx) { options[0] }, 
                    onOptionSelected = onUpdateSelect, 
                    labelProvider = { min -> labels.getOrElse(options.indexOf(min)) { "${min}m" } }, 
                    activeColor = BlossomColors.SlateBlue 
                ) 
                Spacer(modifier = Modifier.height(14.dp)) 
                Text( 
                    "Snooze Duration", 
                    fontSize = 12.sp, 
                    color = BlossomColors.TextSecondary 
                ) 
                Spacer(modifier = Modifier.height(6.dp)) 
                SlidingPillSwitcher( 
                    options = options, 
                    selectedOption = options.getOrElse(snoozeIdx) { options[0] }, 
                    onOptionSelected = onSnoozeSelect, 
                    labelProvider = { min -> labels.getOrElse(options.indexOf(min)) { "${min}m" } }, 
                    activeColor = BlossomColors.SlateBlue 
                ) 
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
                    tint = BlossomColors.SakuraRose, 
                    modifier = Modifier.size(15.dp) 
                ) 
                Spacer(modifier = Modifier.width(6.dp)) 
                Text( 
                    label, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Medium, 
                    color = BlossomColors.TextSecondary 
                ) 
                Spacer(modifier = Modifier.weight(1f)) 
                Surface( 
                    shape = RoundedCornerShape(6.dp), 
                    color = BlossomColors.SakuraRoseContainer 
                ) { 
                    Text( 
                        valueDisplay, 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.SakuraRose, 
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
                    thumbColor = BlossomColors.SakuraRose, 
                    activeTrackColor = BlossomColors.SakuraRose, 
                    inactiveTrackColor = BlossomColors.CardBorder 
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
            shape = BlossomShapes.SquircleLarge, 
            color = BlossomColors.SurfaceCard1, 
            border = BorderStroke(1.dp, BlossomColors.CardBorderSubtle), 
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
                            .clip(BlossomShapes.SquircleSmall) 
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
                        tint = BlossomColors.TextMuted, 
                        modifier = Modifier.size(16.dp) 
                    ) 
                } 
                Spacer(modifier = Modifier.height(10.dp)) 
                Text( 
                    text = title, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = BlossomColors.TextPrimary, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis 
                ) 
                Spacer(modifier = Modifier.height(2.dp)) 
                Text( 
                    text = subtitle, 
                    fontSize = 11.sp, 
                    color = BlossomColors.TextSecondary, 
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


