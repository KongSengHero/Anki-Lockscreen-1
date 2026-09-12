package com.ankilock.ui.study
    
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.ForgedStory
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StorySessionManager
import com.ankilock.data.calculatedEstimatedMinutes
import com.ankilock.ui.shinobi.ShinobiColors
import com.ankilock.ui.shinobi.ShinobiNunito
import com.ankilock.ui.shinobi.ShinobiPillBadge
import com.ankilock.ui.shinobi.ShinobiShapes
import com.ankilock.ui.shinobi.ShinobiSquircleButton
import com.ankilock.ui.shinobi.ShinobiTextButton
import com.ankilock.ui.shinobi.StoryArtworkThumbnail
import com.ankilock.ui.shinobi.TactileButtonVariant
import com.ankilock.util.AudioPlayerHelper
    
enum class ForgedLibraryTab { STARTED, READ, FORGED } 
    
@Composable
fun ForgedStoriesScreen( 
    prefs: PreferencesManager, 
    audioPlayer: AudioPlayerHelper, 
    onOpenForgeStudio: () -> Unit, 
    onOpenStory: (ForgedStory) -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val context = LocalContext.current 
    var selectedTab by remember { mutableStateOf(ForgedLibraryTab.STARTED) } 
    val savedStories = StorySessionManager.savedStoriesList 
    val completedIds = prefs.completedStoryIds 
    
    val startedStories = remember(savedStories, completedIds) { 
        savedStories.filter { !completedIds.contains(it.id) } 
    } 
    val readStories = remember(savedStories, completedIds) { 
        savedStories.filter { completedIds.contains(it.id) } 
    } 
    val forgedStories = remember(savedStories) { 
        savedStories 
    } 
    
    val activeList = when (selectedTab) { 
        ForgedLibraryTab.STARTED -> startedStories 
        ForgedLibraryTab.READ -> readStories 
        ForgedLibraryTab.FORGED -> forgedStories 
    } 
    
    Column( 
        modifier = modifier 
            .fillMaxSize() 
            .background(ShinobiColors.BackgroundDeep) 
            .padding(horizontal = 16.dp) 
    ) { 
        Spacer(modifier = Modifier.height(16.dp)) 
        
        Row( 
            modifier = Modifier.fillMaxWidth(), 
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.SpaceBetween 
        ) { 
            Column { 
                Text( 
                    text = "Forged Library", 
                    fontSize = 22.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = ShinobiColors.TextPrimary 
                ) 
                Text( 
                    text = "${savedStories.size} stories in collection", 
                    fontSize = 12.sp, 
                    color = ShinobiColors.TextSecondary 
                ) 
            } 
            
            ShinobiSquircleButton( 
                onClick = onOpenForgeStudio, 
                icon = Icons.Default.AutoAwesome, 
                contentDescription = "Forge Story", 
                borderColor = ShinobiColors.WisteriaViolet.copy(alpha = 0.5f), 
                iconColor = ShinobiColors.WisteriaViolet 
            ) 
        } 
        
        Spacer(modifier = Modifier.height(16.dp)) 
        
        Row( 
            modifier = Modifier 
                .fillMaxWidth() 
                .clip(ShinobiShapes.SquircleMedium) 
                .background(ShinobiColors.SurfaceCard1) 
                .border(1.dp, ShinobiColors.CardBorderSubtle, ShinobiShapes.SquircleMedium) 
                .padding(4.dp), 
            horizontalArrangement = Arrangement.spacedBy(4.dp) 
        ) { 
            ForgedLibraryTab.values().forEach { tab -> 
                val isSelected = selectedTab == tab 
                val tabTitle = when (tab) { 
                    ForgedLibraryTab.STARTED -> "Started (${startedStories.size})" 
                    ForgedLibraryTab.READ -> "Read (${readStories.size})" 
                    ForgedLibraryTab.FORGED -> "All (${forgedStories.size})" 
                } 
                
                Box( 
                    modifier = Modifier 
                        .weight(1f) 
                        .clip(ShinobiShapes.SquircleSmall) 
                        .background(if (isSelected) ShinobiColors.WisteriaViolet else Color.Transparent) 
                        .clickable { selectedTab = tab } 
                        .padding(vertical = 8.dp), 
                    contentAlignment = Alignment.Center 
                ) { 
                    Text( 
                        text = tabTitle, 
                        fontSize = 12.sp, 
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                        color = if (isSelected) Color.White else ShinobiColors.TextSecondary 
                    ) 
                } 
            } 
        } 
        
        Spacer(modifier = Modifier.height(16.dp)) 
        
        if (activeList.isEmpty()) { 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .weight(1f), 
                contentAlignment = Alignment.Center 
            ) { 
                Column( 
                    horizontalAlignment = Alignment.CenterHorizontally, 
                    modifier = Modifier.padding(32.dp) 
                ) { 
                    Box( 
                        modifier = Modifier 
                            .size(64.dp) 
                            .clip(ShinobiShapes.SquircleLarge) 
                            .background(ShinobiColors.SurfaceCard1) 
                            .border(1.dp, ShinobiColors.CardBorderSubtle, ShinobiShapes.SquircleLarge), 
                        contentAlignment = Alignment.Center 
                    ) { 
                        Icon( 
                            imageVector = when (selectedTab) { 
                                ForgedLibraryTab.STARTED -> Icons.Default.MenuBook 
                                ForgedLibraryTab.READ -> Icons.Default.Bookmark 
                                ForgedLibraryTab.FORGED -> Icons.Default.AutoAwesome 
                            }, 
                            contentDescription = null, 
                            tint = ShinobiColors.TextMuted, 
                            modifier = Modifier.size(32.dp) 
                        ) 
                    } 
                    
                    Spacer(modifier = Modifier.height(16.dp)) 
                    
                    val emptyTitle = when (selectedTab) { 
                        ForgedLibraryTab.STARTED -> "No stories in progress" 
                        ForgedLibraryTab.READ -> "No completed stories yet" 
                        ForgedLibraryTab.FORGED -> "No forged stories yet" 
                    } 
                    val emptySubtitle = when (selectedTab) { 
                        ForgedLibraryTab.STARTED -> "Stories you start reading will show up here." 
                        ForgedLibraryTab.READ -> "Finish stories to mark them as read." 
                        ForgedLibraryTab.FORGED -> "Forge custom stories tailored to your Anki vocabulary." 
                    } 
                    
                    Text( 
                        text = emptyTitle, 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = ShinobiColors.TextPrimary 
                    ) 
                    Spacer(modifier = Modifier.height(4.dp)) 
                    Text( 
                        text = emptySubtitle, 
                        fontSize = 12.sp, 
                        color = ShinobiColors.TextSecondary 
                    ) 
                    
                    Spacer(modifier = Modifier.height(20.dp)) 
                    
                    ShinobiTextButton( 
                        text = "FORGE STORY", 
                        onClick = onOpenForgeStudio, 
                        modifier = Modifier.width(180.dp), 
                        variant = TactileButtonVariant.PRIMARY, 
                        faceColor = ShinobiColors.WisteriaViolet, 
                        lipColor = ShinobiColors.WisteriaVioletLip 
                    ) 
                } 
            } 
        } else { 
            LazyColumn( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .weight(1f), 
                verticalArrangement = Arrangement.spacedBy(12.dp), 
                contentPadding = PaddingValues(bottom = 96.dp) 
            ) { 
                items(activeList, key = { it.id }) { story -> 
                    ForgedStoryCardItem( 
                        story = story, 
                        isCompleted = completedIds.contains(story.id), 
                        onClick = { onOpenStory(story) }, 
                        onDelete = { StorySessionManager.deleteSavedStory(context, story.id) } 
                    ) 
                } 
            } 
        } 
    } 
} 
    
@Composable
fun ForgedStoryCardItem( 
    story: ForgedStory, 
    isCompleted: Boolean, 
    onClick: () -> Unit, 
    onDelete: () -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val minutes = story.calculatedEstimatedMinutes 
    
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .clip(ShinobiShapes.SquircleLarge) 
            .background(ShinobiColors.SurfaceCard1) 
            .border(1.dp, ShinobiColors.CardBorderSubtle, ShinobiShapes.SquircleLarge) 
            .clickable { onClick() } 
            .padding(14.dp) 
    ) { 
        Row( 
            modifier = Modifier.fillMaxWidth(), 
            verticalAlignment = Alignment.CenterVertically 
        ) { 
            StoryArtworkThumbnail( 
                artworkType = story.genre, 
                modifier = Modifier 
                    .size(60.dp) 
                    .clip(ShinobiShapes.SquircleSmall) 
            ) 
            
            Spacer(modifier = Modifier.width(14.dp)) 
            
            Column(modifier = Modifier.weight(1f)) { 
                Text( 
                    text = story.title, 
                    fontSize = 15.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = ShinobiColors.TextPrimary, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis 
                ) 
                
                if (story.genre.isNotBlank()) { 
                    Text( 
                        text = story.genre, 
                        fontSize = 12.sp, 
                        color = ShinobiColors.TextSecondary, 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis 
                    ) 
                } 
                
                Spacer(modifier = Modifier.height(6.dp)) 
                
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(6.dp) 
                ) { 
                    ShinobiPillBadge( 
                        text = story.level, 
                        textColor = ShinobiColors.SlateBlue, 
                        backgroundColor = ShinobiColors.SlateBlueContainer 
                    ) 
                    
                    ShinobiPillBadge( 
                        text = "$minutes min", 
                        textColor = ShinobiColors.WarmOchre, 
                        backgroundColor = ShinobiColors.WarmOchreContainer 
                    ) 
                    
                    if (isCompleted) { 
                        ShinobiPillBadge( 
                            text = "Done", 
                            textColor = ShinobiColors.MatchaSage, 
                            backgroundColor = ShinobiColors.MatchaSageContainer 
                        ) 
                    } 
                } 
            } 
            
            IconButton( 
                onClick = onDelete, 
                modifier = Modifier.size(32.dp) 
            ) { 
                Icon( 
                    imageVector = Icons.Default.DeleteOutline, 
                    contentDescription = "Delete", 
                    tint = ShinobiColors.TextMuted, 
                    modifier = Modifier.size(18.dp) 
                ) 
            } 
        } 
    } 
}
