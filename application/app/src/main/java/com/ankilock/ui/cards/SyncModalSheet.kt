package com.ankilock.ui.cards
 
import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.layout.Arrangement 
import androidx.compose.foundation.layout.Box 
import androidx.compose.foundation.layout.Column 
import androidx.compose.foundation.layout.Row 
import androidx.compose.foundation.layout.Spacer 
import androidx.compose.foundation.layout.WindowInsets 
import androidx.compose.foundation.layout.fillMaxWidth 
import androidx.compose.foundation.layout.height 
import androidx.compose.foundation.layout.navigationBarsPadding 
import androidx.compose.foundation.layout.padding 
import androidx.compose.foundation.layout.size 
import androidx.compose.foundation.layout.width 
import androidx.compose.foundation.layout.widthIn 
import androidx.compose.foundation.rememberScrollState 
import androidx.compose.foundation.verticalScroll 
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.filled.CheckCircle 
import androidx.compose.material.icons.filled.Close 
import androidx.compose.material.icons.filled.CloudDownload 
import androidx.compose.material.icons.filled.CloudUpload 
import androidx.compose.material.icons.filled.Sync 
import androidx.compose.material3.CircularProgressIndicator 
import androidx.compose.material3.ExperimentalMaterial3Api 
import androidx.compose.material3.Icon 
import androidx.compose.material3.IconButton 
import androidx.compose.material3.ModalBottomSheet 
import androidx.compose.material3.Surface 
import androidx.compose.material3.Text 
import androidx.compose.material3.rememberModalBottomSheetState 
import androidx.compose.runtime.Composable 
import androidx.compose.runtime.remember 
import androidx.compose.ui.Alignment 
import androidx.compose.ui.Modifier 
import androidx.compose.ui.geometry.Offset 
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection 
import androidx.compose.ui.input.nestedscroll.NestedScrollSource 
import androidx.compose.ui.input.nestedscroll.nestedScroll 
import androidx.compose.ui.text.font.FontWeight 
import androidx.compose.ui.text.style.TextOverflow 
import androidx.compose.ui.unit.Velocity 
import androidx.compose.ui.unit.dp 
import androidx.compose.ui.unit.sp 
import com.ankilock.ui.blossom.BlossomColors 
import com.ankilock.ui.blossom.BlossomShapes 
import com.ankilock.ui.components.Squircle3DButton 
 
@OptIn(ExperimentalMaterial3Api::class) 
@Composable 
fun SyncModalSheet( 
    pendingReviewsCount: Int, 
    isSyncing: Boolean, 
    onDismissRequest: () -> Unit, 
    onPushSync: () -> Unit, 
    onPullSync: () -> Unit, 
    deckName: String = "", 
    isOpen: Boolean = true 
) { 
    if (!isOpen) return 
     
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
     
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
     
    ModalBottomSheet( 
        onDismissRequest = onDismissRequest, 
        sheetState = sheetState, 
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
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.weight(1f, fill = false) 
                ) { 
                    Icon( 
                        imageVector = Icons.Filled.Sync, 
                        contentDescription = null, 
                        tint = BlossomColors.SakuraRose, 
                        modifier = Modifier.size(22.dp) 
                    ) 
                    Spacer(modifier = Modifier.width(8.dp)) 
                    Text( 
                        text = "Deck Synchronization", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = BlossomColors.TextPrimary, 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis 
                    ) 
                } 
                 
                Spacer(modifier = Modifier.width(8.dp)) 
                 
                if (deckName.isNotBlank()) { 
                    Surface( 
                        shape = BlossomShapes.SquircleSmall, 
                        color = BlossomColors.WisteriaVioletContainer, 
                        border = BorderStroke(1.dp, BlossomColors.WisteriaViolet.copy(alpha = 0.5f)) 
                    ) { 
                        Text( 
                            text = deckName, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.WisteriaViolet, 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis, 
                            modifier = Modifier 
                                .widthIn(max = 120.dp) 
                                .padding(horizontal = 8.dp, vertical = 4.dp) 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(8.dp)) 
                } 
                 
                IconButton(onClick = onDismissRequest) { 
                    Icon( 
                        imageVector = Icons.Filled.Close, 
                        contentDescription = "Close", 
                        tint = BlossomColors.TextSecondary 
                    ) 
                } 
            } 
             
            Surface( 
                shape = BlossomShapes.SquircleMedium, 
                color = if (pendingReviewsCount > 0) BlossomColors.SakuraRoseContainer else BlossomColors.MatchaSageContainer, 
                border = BorderStroke( 
                    1.dp, 
                    if (pendingReviewsCount > 0) BlossomColors.SakuraRose.copy(alpha = 0.4f) else BlossomColors.MatchaSage.copy(alpha = 0.4f) 
                ), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                Row( 
                    modifier = Modifier.padding(14.dp), 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    Icon( 
                        imageVector = if (pendingReviewsCount > 0) Icons.Default.CloudUpload else Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = if (pendingReviewsCount > 0) BlossomColors.SakuraRose else BlossomColors.MatchaSage, 
                        modifier = Modifier.size(24.dp) 
                    ) 
                    Spacer(modifier = Modifier.width(12.dp)) 
                    Column(modifier = Modifier.weight(1f)) { 
                        Text( 
                            text = if (pendingReviewsCount > 0) "$pendingReviewsCount Local Review(s) Pending" else "All Reviews Synchronized", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        Spacer(modifier = Modifier.height(2.dp)) 
                        Text( 
                            text = if (pendingReviewsCount > 0) { 
                                "Reviews are held in local session for instant response and lossless undo. Push to write them to AnkiDroid." 
                            } else { 
                                "Your local review queue is in sync with AnkiDroid." 
                            }, 
                            fontSize = 12.sp, 
                            color = BlossomColors.TextSecondary, 
                            lineHeight = 16.sp 
                        ) 
                    } 
                } 
            } 
             
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { 
                Squircle3DButton( 
                    onClick = onPushSync, 
                    enabled = !isSyncing, 
                    containerColor = BlossomColors.SakuraRose, 
                    bevelColor = BlossomColors.SakuraRoseLip, 
                    contentColor = Color.White, 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(52.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.Center, 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(horizontal = 12.dp) 
                    ) { 
                        if (isSyncing) { 
                            CircularProgressIndicator( 
                                modifier = Modifier.size(18.dp), 
                                strokeWidth = 2.dp, 
                                color = Color.White 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = "Pushing Reviews...", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 14.sp, 
                                color = Color.White 
                            ) 
                        } else { 
                            Icon( 
                                imageVector = Icons.Default.CloudUpload, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(20.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(8.dp)) 
                            Text( 
                                text = if (pendingReviewsCount > 0) "Push Sync ($pendingReviewsCount Reviews)" else "Push Sync (0 Reviews Pending)", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 14.sp, 
                                color = Color.White 
                            ) 
                        } 
                    } 
                } 
                 
                Squircle3DButton( 
                    onClick = onPullSync, 
                    enabled = !isSyncing, 
                    containerColor = BlossomColors.SkyCyan, 
                    bevelColor = BlossomColors.SkyCyanLip, 
                    contentColor = Color.White, 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(52.dp) 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.Center, 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(horizontal = 12.dp) 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.CloudDownload, 
                            contentDescription = null, 
                            tint = Color.White, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(8.dp)) 
                        Text( 
                            text = if (pendingReviewsCount > 0) "Pull Sync (Discard Local Progress)" else "Pull Sync (Refresh from AnkiDroid)", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 14.sp, 
                            color = Color.White 
                        ) 
                    } 
                } 
            } 
        } 
    } 
} 
