package com.ankilock.ui.cards
    
import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.ExperimentalFoundationApi 
import androidx.compose.foundation.Image 
import androidx.compose.foundation.background 
import androidx.compose.foundation.border 
import androidx.compose.foundation.clickable 
import androidx.compose.foundation.interaction.MutableInteractionSource 
import androidx.compose.foundation.layout.Arrangement 
import androidx.compose.foundation.layout.Box 
import androidx.compose.foundation.layout.Column 
import androidx.compose.foundation.layout.PaddingValues 
import androidx.compose.foundation.layout.Row 
import androidx.compose.foundation.layout.Spacer 
import androidx.compose.foundation.layout.aspectRatio 
import androidx.compose.foundation.layout.fillMaxSize 
import androidx.compose.foundation.layout.fillMaxWidth 
import androidx.compose.foundation.layout.height 
import androidx.compose.foundation.layout.padding 
import androidx.compose.foundation.layout.size 
import androidx.compose.foundation.layout.width 
import androidx.compose.foundation.pager.HorizontalPager 
import androidx.compose.foundation.pager.rememberPagerState 
import androidx.compose.foundation.shape.RoundedCornerShape 
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.automirrored.filled.VolumeUp 
import androidx.compose.material.icons.filled.AutoAwesome 
import androidx.compose.material.icons.filled.CheckCircle 
import androidx.compose.material.icons.filled.Refresh 
import androidx.compose.material.icons.filled.Visibility 
import androidx.compose.material3.Button 
import androidx.compose.material3.ButtonDefaults 
import androidx.compose.material3.Card 
import androidx.compose.material3.CardDefaults 
import androidx.compose.material3.Icon 
import androidx.compose.material3.IconButton 
import androidx.compose.material3.Text 
import androidx.compose.ui.text.style.TextAlign 
import com.ankilock.ui.components.squircleLiquidGlass 
import androidx.compose.runtime.mutableStateMapOf 
import androidx.compose.runtime.mutableStateOf 
import androidx.compose.runtime.getValue 
import androidx.compose.runtime.setValue 
import androidx.compose.runtime.Composable 
import androidx.compose.runtime.LaunchedEffect 
import androidx.compose.runtime.remember 
import androidx.compose.runtime.snapshotFlow 
import androidx.compose.runtime.snapshots.SnapshotStateMap 
import androidx.compose.ui.Alignment 
import androidx.compose.ui.Modifier 
import androidx.compose.ui.draw.clip 
import androidx.compose.ui.draw.clipToBounds 
import androidx.compose.ui.graphics.Brush 
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.graphics.asImageBitmap 
import androidx.compose.ui.graphics.graphicsLayer 
import androidx.compose.ui.layout.ContentScale 
import androidx.compose.ui.platform.LocalContext 
import androidx.compose.ui.text.font.FontWeight 
import androidx.compose.ui.unit.dp 
import androidx.compose.ui.unit.sp 
import androidx.compose.ui.util.lerp 
import com.ankilock.anki.AnkiDroidHelper 
import com.ankilock.data.CardInfo 
import com.ankilock.data.DeckInfo 
import com.ankilock.data.PreferencesManager 
import com.ankilock.ui.blossom.BlossomColors 
import com.ankilock.ui.blossom.BlossomShapes 
import com.ankilock.util.MediaArtworkGenerator 
import kotlin.math.absoluteValue 
    
@OptIn(ExperimentalFoundationApi::class) 
@Composable
fun DeckCarouselCard( 
    decks: List<DeckInfo>, 
    activeCard: CardInfo?, 
    stats: Triple<Int, Int, Int>, 
    isRevealed: Boolean, 
    deckCardsCache: SnapshotStateMap<Long, CardInfo?>, 
    deckStatsCache: SnapshotStateMap<Long, Triple<Int, Int, Int>> = remember { mutableStateMapOf() }, 
    onToggleReveal: (CardInfo?, Boolean) -> Unit = { _, _ -> }, 
    onRefresh: () -> Unit, 
    onAgain: (DeckInfo, CardInfo) -> Unit, 
    onGood: (DeckInfo, CardInfo) -> Unit, 
    onOpenAnki: () -> Unit, 
    onDeckChanged: (Long) -> Unit, 
    onPlayWord: (CardInfo) -> Unit = {}, 
    onPlaySentence: (CardInfo) -> Unit = {}, 
    isAutoPlay: Boolean = false, 
    onToggleAutoPlay: (Boolean) -> Unit = {}, 
    isPlayingWord: Boolean = false, 
    isPlayingSentence: Boolean = false, 
    backgroundType: String? = null, 
    blurRadius: Int? = null, 
    dimOpacity: Float? = null, 
    artworkOpacity: Float? = null, 
    customImageUri: String? = null, 
    modifier: Modifier = Modifier 
) { 
    var isProcessing by remember { mutableStateOf(false) } 
    var revealedNoteId by remember { mutableStateOf<Long?>(null) } 
    val pageCount = if (decks.isEmpty()) 1 else decks.size 
    val pagerState = rememberPagerState( 
        initialPage = 0, 
        pageCount = { pageCount } 
    ) 
    
    LaunchedEffect(pageCount) { 
        if (pagerState.currentPage >= pageCount) { 
            pagerState.scrollToPage((pageCount - 1).coerceAtLeast(0)) 
        } 
    } 
    
    LaunchedEffect(pagerState) { 
        snapshotFlow { pagerState.currentPage } 
            .collect { page -> 
                revealedNoteId = null 
                val deck = decks.getOrNull(page) 
                if (deck != null && deck.id != -1L) { 
                    onDeckChanged(deck.id) 
                } 
            } 
    } 
    
    val currentCenterIndex = pagerState.currentPage.coerceIn(0, (pageCount - 1).coerceAtLeast(0)) 
    val currentCenterDeck = if (decks.isNotEmpty()) decks.getOrNull(currentCenterIndex) else null 
    val centerCard = if (decks.isNotEmpty() && currentCenterDeck != null) { 
        deckCardsCache[currentCenterDeck.id] 
    } else { 
        null 
    } 
    val activeStats = currentCenterDeck?.let { deckStatsCache[it.id] ?: Triple(it.newCount, it.learnCount, it.reviewCount) } 
        ?: (if (decks.isEmpty()) Triple(0, 0, 0) else stats) 
    
    LaunchedEffect(isRevealed) { 
        if (!isRevealed) { 
            revealedNoteId = null 
        } 
    } 
    
    LaunchedEffect(centerCard?.noteId) { 
        isProcessing = false 
        revealedNoteId = null 
    } 
    
    Card( 
        modifier = modifier.fillMaxWidth(), 
        shape = BlossomShapes.SquircleLarge, 
        colors = CardDefaults.cardColors(containerColor = BlossomColors.SurfaceCard1), 
        border = BorderStroke(1.dp, BlossomColors.CardBorder) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(vertical = 16.dp) 
        ) { 
            Row( 
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp) 
            ) { 
                Icon( 
                    imageVector = Icons.Filled.Visibility, 
                    contentDescription = null, 
                    tint = BlossomColors.SlateBlue, 
                    modifier = Modifier.size(18.dp) 
                ) 
                Spacer(modifier = Modifier.width(8.dp)) 
                Text( 
                    text = currentCenterDeck?.name ?: "Live Card Preview", 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = BlossomColors.TextPrimary, 
                    maxLines = 1 
                ) 
                Spacer(modifier = Modifier.width(10.dp)) 
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Text( 
                        text = "${activeStats.first}", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF8AB4F8) 
                    ) 
                    Text( 
                        text = " · ", 
                        fontSize = 12.sp, 
                        color = BlossomColors.TextMuted 
                    ) 
                    Text( 
                        text = "${activeStats.second}", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFFF28B82) 
                    ) 
                    Text( 
                        text = " · ", 
                        fontSize = 12.sp, 
                        color = BlossomColors.TextMuted 
                    ) 
                    Text( 
                        text = "${activeStats.third}", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF81C995) 
                    ) 
                } 
                Spacer(modifier = Modifier.weight(1f)) 
                IconButton( 
                    onClick = onRefresh, 
                    modifier = Modifier.size(28.dp) 
                ) { 
                    Icon( 
                        imageVector = Icons.Filled.Refresh, 
                        contentDescription = "Refresh", 
                        tint = BlossomColors.TextSecondary, 
                        modifier = Modifier.size(16.dp) 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clipToBounds() 
            ) { 
                Box( 
                    modifier = Modifier 
                        .align(Alignment.Center) 
                        .fillMaxWidth(0.88f) 
                        .height(280.dp) 
                        .clip(BlossomShapes.SquircleLarge) 
                        .background( 
                            Brush.radialGradient( 
                                colors = listOf( 
                                    BlossomColors.SlateBlue.copy(alpha = 0.20f), 
                                    BlossomColors.MatchaSage.copy(alpha = 0.10f), 
                                    Color.Transparent 
                                ) 
                            ) 
                        ) 
                ) 
                
                HorizontalPager( 
                    state = pagerState, 
                    contentPadding = PaddingValues(horizontal = 44.dp), 
                    pageSpacing = 12.dp, 
                    modifier = Modifier.fillMaxWidth() 
                ) { page -> 
                    val isCenterPage = pagerState.currentPage == page 
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue 
                    val scale = lerp(0.86f, 1f, 1f - pageOffset.coerceIn(0f, 1f)) 
                    val alpha = lerp(0.45f, 1f, 1f - pageOffset.coerceIn(0f, 1f)) 
                    
                    if (decks.isEmpty()) { 
                        EmptyPreviewFlashcard( 
                            modifier = Modifier 
                                .graphicsLayer { 
                                    scaleX = scale 
                                    scaleY = scale 
                                    this.alpha = alpha 
                                } 
                        ) 
                    } else { 
                        val deck = decks[page] 
                        val cardForDeck = deckCardsCache[deck.id] 
                        val deckStats = deckStatsCache[deck.id] ?: Triple(deck.newCount, deck.learnCount, deck.reviewCount) 
                        
                        ArtworkFlashcard( 
                            deckName = deck.name, 
                            deckId = deck.id, 
                            card = cardForDeck, 
                            stats = deckStats, 
                            isRevealed = isCenterPage && (revealedNoteId != null && revealedNoteId == cardForDeck?.noteId), 
                            backgroundType = backgroundType, 
                            blurRadius = blurRadius, 
                            dimOpacity = dimOpacity, 
                            artworkOpacity = artworkOpacity, 
                            customImageUri = customImageUri, 
                            modifier = Modifier 
                                .graphicsLayer { 
                                    scaleX = scale 
                                    scaleY = scale 
                                    this.alpha = alpha 
                                } 
                                .clickable( 
                                    enabled = isCenterPage && cardForDeck != null, 
                                    interactionSource = remember { MutableInteractionSource() }, 
                                    indication = null 
                                ) { 
                                    if (cardForDeck != null) { 
                                        val willReveal = (revealedNoteId != cardForDeck.noteId) 
                                        revealedNoteId = if (willReveal) cardForDeck.noteId else null 
                                        onToggleReveal(cardForDeck, willReveal) 
                                    } 
                                } 
                        ) 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp), 
                horizontalArrangement = Arrangement.spacedBy(8.dp) 
            ) { 
                Button( 
                    onClick = { centerCard?.let { onPlayWord(it) } }, 
                    enabled = centerCard != null, 
                    shape = RoundedCornerShape(10.dp), 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = if (isPlayingWord) BlossomColors.SlateBlue.copy(alpha = 0.40f) else BlossomColors.BtnWordBg, 
                        disabledContainerColor = BlossomColors.BtnWordBg.copy(alpha = 0.5f) 
                    ), 
                    border = BorderStroke( 
                        1.dp, 
                        if (isPlayingWord) BlossomColors.SlateBlue else BlossomColors.BtnWordBorder 
                    ), 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(38.dp), 
                    contentPadding = PaddingValues(horizontal = 4.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(5.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp, 
                            contentDescription = null, 
                            tint = if (isPlayingWord) BlossomColors.SlateBlue else Color(0xFF38BDF8), 
                            modifier = Modifier.size(15.dp) 
                        ) 
                        Text( 
                            text = "Word", 
                            fontSize = 11.5.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = if (isPlayingWord) Color.White else BlossomColors.BtnWordText 
                        ) 
                    } 
                } 
                
                Button( 
                    onClick = { onToggleAutoPlay(!isAutoPlay) }, 
                    shape = RoundedCornerShape(10.dp), 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = if (isAutoPlay) BlossomColors.MatchaSage.copy(alpha = 0.35f) else BlossomColors.BtnWordBg 
                    ), 
                    border = BorderStroke( 
                        1.dp, 
                        if (isAutoPlay) BlossomColors.MatchaSage else BlossomColors.BtnWordBorder 
                    ), 
                    modifier = Modifier 
                        .weight(1.1f) 
                        .height(38.dp), 
                    contentPadding = PaddingValues(horizontal = 4.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(5.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Filled.AutoAwesome, 
                            contentDescription = null, 
                            tint = if (isAutoPlay) BlossomColors.MatchaSage else BlossomColors.TextMuted, 
                            modifier = Modifier.size(15.dp) 
                        ) 
                        Text( 
                            text = if (isAutoPlay) "Auto On" else "Auto Play", 
                            fontSize = 11.5.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = if (isAutoPlay) BlossomColors.MatchaSage else BlossomColors.BtnWordText 
                        ) 
                    } 
                } 
                
                Button( 
                    onClick = { centerCard?.let { onPlaySentence(it) } }, 
                    enabled = centerCard != null && centerCard.sentence.isNotBlank(), 
                    shape = RoundedCornerShape(10.dp), 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = if (isPlayingSentence) BlossomColors.SlateBlue.copy(alpha = 0.40f) else BlossomColors.BtnWordBg, 
                        disabledContainerColor = BlossomColors.BtnWordBg.copy(alpha = 0.5f) 
                    ), 
                    border = BorderStroke( 
                        1.dp, 
                        if (isPlayingSentence) BlossomColors.SlateBlue else BlossomColors.BtnWordBorder 
                    ), 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(38.dp), 
                    contentPadding = PaddingValues(horizontal = 4.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(5.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp, 
                            contentDescription = null, 
                            tint = if (isPlayingSentence) BlossomColors.SlateBlue else Color(0xFF38BDF8), 
                            modifier = Modifier.size(15.dp) 
                        ) 
                        Text( 
                            text = "Sentence", 
                            fontSize = 11.5.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = if (isPlayingSentence) Color.White else BlossomColors.BtnWordText 
                        ) 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(10.dp)) 
            
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(horizontal = 16.dp), 
                horizontalArrangement = Arrangement.spacedBy(8.dp) 
            ) { 
                Button( 
                    onClick = { 
                        if (!isProcessing && currentCenterDeck != null && centerCard != null) { 
                            isProcessing = true 
                            revealedNoteId = null 
                            onAgain(currentCenterDeck, centerCard) 
                        } 
                    }, 
                    enabled = centerCard != null && !isProcessing, 
                    shape = RoundedCornerShape(8.dp), 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = BlossomColors.BtnAgainBg, 
                        disabledContainerColor = BlossomColors.BtnAgainBg.copy(alpha = 0.5f) 
                    ), 
                    border = BorderStroke(1.dp, BlossomColors.BtnAgainBorder), 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(44.dp), 
                    contentPadding = PaddingValues(horizontal = 2.dp) 
                ) { 
                    Text( 
                        text = "Again", 
                        fontSize = 12.5.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.BtnAgainText, 
                        maxLines = 1, 
                        softWrap = false 
                    ) 
                } 
                
                Button( 
                    onClick = { 
                        if (centerCard != null) { 
                            val willReveal = (revealedNoteId != centerCard.noteId) 
                            revealedNoteId = if (willReveal) centerCard.noteId else null 
                            onToggleReveal(centerCard, willReveal) 
                        } 
                    }, 
                    enabled = centerCard != null, 
                    shape = RoundedCornerShape(8.dp), 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = BlossomColors.BtnRevealBg, 
                        disabledContainerColor = BlossomColors.BtnRevealBg.copy(alpha = 0.5f) 
                    ), 
                    border = BorderStroke(1.dp, BlossomColors.BtnRevealBorder), 
                    modifier = Modifier 
                        .weight(1.2f) 
                        .height(44.dp), 
                    contentPadding = PaddingValues(horizontal = 2.dp) 
                ) { 
                    Text( 
                        text = if (revealedNoteId != null && revealedNoteId == centerCard?.noteId) "Hide" else "Reveal", 
                        fontSize = 12.5.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.BtnRevealText, 
                        maxLines = 1, 
                        softWrap = false 
                    ) 
                } 
                
                Button( 
                    onClick = { 
                        if (!isProcessing && currentCenterDeck != null && centerCard != null) { 
                            isProcessing = true 
                            revealedNoteId = null 
                            onGood(currentCenterDeck, centerCard) 
                        } 
                    }, 
                    enabled = centerCard != null && !isProcessing, 
                    shape = RoundedCornerShape(8.dp), 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = BlossomColors.BtnGoodBg, 
                        disabledContainerColor = BlossomColors.BtnGoodBg.copy(alpha = 0.5f) 
                    ), 
                    border = BorderStroke(1.dp, BlossomColors.BtnGoodBorder), 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(44.dp), 
                    contentPadding = PaddingValues(horizontal = 2.dp) 
                ) { 
                    Text( 
                        text = "Good", 
                        fontSize = 12.5.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.BtnGoodText, 
                        maxLines = 1, 
                        softWrap = false 
                    ) 
                } 
                
                Button( 
                    onClick = onOpenAnki, 
                    enabled = true, 
                    shape = RoundedCornerShape(8.dp), 
                    colors = ButtonDefaults.buttonColors( 
                        containerColor = BlossomColors.BtnAnkiBg, 
                        disabledContainerColor = BlossomColors.BtnAnkiBg.copy(alpha = 0.5f) 
                    ), 
                    border = BorderStroke(1.dp, BlossomColors.BtnAnkiBorder), 
                    modifier = Modifier 
                        .weight(0.9f) 
                        .height(44.dp), 
                    contentPadding = PaddingValues(horizontal = 2.dp) 
                ) { 
                    Text( 
                        text = "Anki", 
                        fontSize = 12.5.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.BtnAnkiText, 
                        maxLines = 1, 
                        softWrap = false 
                    ) 
                } 
            } 
        } 
    } 
} 
    
@Composable
fun ArtworkFlashcard( 
    deckName: String, 
    deckId: Long, 
    card: CardInfo?, 
    stats: Triple<Int, Int, Int>, 
    isRevealed: Boolean, 
    backgroundType: String? = null, 
    blurRadius: Int? = null, 
    dimOpacity: Float? = null, 
    artworkOpacity: Float? = null, 
    customImageUri: String? = null, 
    modifier: Modifier = Modifier 
) { 
    val context = LocalContext.current 
    val prefs = remember { PreferencesManager(context) } 
    val ankiHelper = remember { AnkiDroidHelper(context) } 
    
    val cardImage = remember(card?.imageFileName) { 
        val fileName = card?.imageFileName 
        if (!fileName.isNullOrBlank()) { 
            ankiHelper.getCardImageBitmap(fileName) 
        } else null 
    } 
    
    val effectiveBgType = backgroundType ?: prefs.backgroundType 
    val effectiveBlurRadius = blurRadius ?: prefs.blurRadius 
    val effectiveDimOpacity = dimOpacity ?: prefs.dimOpacity 
    val effectiveArtworkOpacity = artworkOpacity ?: prefs.artworkOpacity 
    val effectiveCustomUri = customImageUri ?: prefs.customImageUri 
    
    val artworkBitmap = remember( 
        card, 
        isRevealed, 
        stats, 
        deckName, 
        effectiveBgType, 
        effectiveBlurRadius, 
        effectiveDimOpacity, 
        effectiveArtworkOpacity, 
        effectiveCustomUri 
    ) { 
        MediaArtworkGenerator.generateArtwork( 
            context = context, 
            card = card, 
            stats = stats, 
            isRevealed = isRevealed, 
            imageBitmap = cardImage, 
            showBottomControls = false, 
            targetWidth = 656, 
            targetHeight = 800, 
            deckNameOverride = deckName, 
            bgTypeOverride = effectiveBgType, 
            blurRadiusOverride = effectiveBlurRadius, 
            dimOpacityOverride = effectiveDimOpacity, 
            artworkOpacityOverride = effectiveArtworkOpacity, 
            customImageUriOverride = effectiveCustomUri 
        ) 
    } 
    
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .aspectRatio(0.82f) 
            .squircleLiquidGlass( 
                shape = RoundedCornerShape(20.dp), 
                cornerRadius = 20.dp, 
                tintColor = Color.White.copy(alpha = 0.20f), 
                darkBaseAlpha = 0.50f, 
                specularAlpha = 0.65f, 
                borderAlpha = 0.55f, 
                shadowElevation = 10.dp 
            ) 
            .border( 
                width = 1.2.dp, 
                brush = Brush.linearGradient( 
                    0.0f to BlossomColors.SlateBlue.copy(alpha = 0.75f), 
                    0.25f to BlossomColors.SlateBlue.copy(alpha = 0.20f), 
                    0.5f to Color.Transparent, 
                    0.75f to BlossomColors.SlateBlue.copy(alpha = 0.20f), 
                    1.0f to BlossomColors.SlateBlue.copy(alpha = 0.75f) 
                ), 
                shape = RoundedCornerShape(20.dp) 
            ) 
    ) { 
        Image( 
            bitmap = artworkBitmap.asImageBitmap(), 
            contentDescription = "Card Artwork", 
            contentScale = ContentScale.Crop, 
            modifier = Modifier.fillMaxSize() 
        ) 
    } 
} 

@Composable 
fun EmptyPreviewFlashcard( 
    modifier: Modifier = Modifier 
) { 
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .aspectRatio(0.82f) 
            .squircleLiquidGlass( 
                shape = RoundedCornerShape(20.dp), 
                cornerRadius = 20.dp, 
                tintColor = Color.White.copy(alpha = 0.20f), 
                darkBaseAlpha = 0.50f, 
                specularAlpha = 0.65f, 
                borderAlpha = 0.55f, 
                shadowElevation = 10.dp 
            ) 
            .border( 
                width = 1.2.dp, 
                brush = Brush.linearGradient( 
                    0.0f to BlossomColors.SlateBlue.copy(alpha = 0.75f), 
                    0.25f to BlossomColors.SlateBlue.copy(alpha = 0.20f), 
                    0.5f to Color.Transparent, 
                    0.75f to BlossomColors.SlateBlue.copy(alpha = 0.20f), 
                    1.0f to BlossomColors.SlateBlue.copy(alpha = 0.75f) 
                ), 
                shape = RoundedCornerShape(20.dp) 
            ), 
        contentAlignment = Alignment.Center 
    ) { 
        Column( 
            horizontalAlignment = Alignment.CenterHorizontally, 
            verticalArrangement = Arrangement.Center, 
            modifier = Modifier.padding(24.dp) 
        ) { 
            Icon( 
                imageVector = Icons.Filled.CheckCircle, 
                contentDescription = null, 
                tint = BlossomColors.SlateBlue, 
                modifier = Modifier.size(42.dp) 
            ) 
            Spacer(modifier = Modifier.height(14.dp)) 
            Text( 
                text = "Nothing to Review", 
                fontSize = 17.sp, 
                fontWeight = FontWeight.Bold, 
                color = BlossomColors.TextPrimary 
            ) 
            Spacer(modifier = Modifier.height(8.dp)) 
            Text( 
                text = "No Decks Selected\nSelect an Anki deck below to start reviewing", 
                fontSize = 13.sp, 
                color = BlossomColors.TextSecondary, 
                textAlign = TextAlign.Center 
            ) 
        } 
    } 
} 
