package com.ankilock.ui.reading

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.clickable 
import androidx.compose.foundation.layout.ExperimentalLayoutApi 
import androidx.compose.foundation.layout.FlowRow 
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.StoryWordItem 
import com.ankilock.translation.TranslationResult
import com.ankilock.translation.TranslatorService
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.story.RubySegment 
import com.ankilock.ui.blossom.story.StoryTokenizer 
import com.ankilock.ui.components.Squircle3DButton
import com.ankilock.util.JapaneseTtsHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationBottomSheet( 
    sourceText: String, 
    furiganaSource: String? = null, 
    targetWords: List<StoryWordItem> = emptyList(), 
    onDismiss: () -> Unit, 
    onNavigateToJisho: (String) -> Unit = {} 
) { 
    val context = LocalContext.current 
    val clipboardManager = LocalClipboardManager.current 
    val scope = rememberCoroutineScope() 
    val translatorService = remember(context) { TranslatorService(context) } 
    val ttsHelper = remember { JapaneseTtsHelper(context) } 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 
    
    val rubySegments = remember(sourceText, furiganaSource, targetWords) { 
        val clean = sourceText.trim() 
        if (clean.isBlank()) { 
            emptyList() 
        } else if (!furiganaSource.isNullOrBlank() && furiganaSource.contains("[")) { 
            StoryTokenizer.parseBracketSegments(furiganaSource) 
        } else { 
            val tokens = StoryTokenizer.tokenizeToStoryTokens(clean, targetWords) 
            tokens.flatMap { it.segments } 
        } 
    } 
    
    val resolvedSegments = remember(rubySegments) { 
        rubySegments.flatMap { segment -> 
            if (segment.ruby.isNullOrBlank() && segment.text.any { StoryTokenizer.isKanji(it) }) { 
                val fallback = StoryTokenizer.getKanjiReading(segment.text) 
                if (!fallback.isNullOrBlank()) { 
                    listOf(RubySegment(text = segment.text, ruby = fallback)) 
                } else { 
                    StoryTokenizer.resolveFallbackSegments(segment.text) 
                } 
            } else { 
                listOf(segment) 
            } 
        } 
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

    fun dismissWithAnimation() { 
        scope.launch { 
            try { 
                sheetState.hide() 
            } finally { 
                onDismiss() 
            } 
        } 
    } 

    DisposableEffect(Unit) { 
        onDispose { 
            ttsHelper.shutdown() 
        } 
    } 

    var isLoading by remember { mutableStateOf(true) } 
    var result by remember { mutableStateOf<TranslationResult?>(null) } 
    var errorMsg by remember { mutableStateOf<String?>(null) } 

    fun doTranslate() { 
        if (sourceText.isBlank()) { 
            isLoading = false 
            return 
        } 
        isLoading = true 
        errorMsg = null 
        result = null 
        scope.launch { 
            val res = translatorService.translate(sourceText) 
            res.onSuccess { 
                result = it 
                isLoading = false 
            }.onFailure { err -> 
                errorMsg = err.localizedMessage ?: "Translation failed" 
                isLoading = false 
            } 
        } 
    } 

    LaunchedEffect(sourceText) { 
        doTranslate() 
    } 

    ModalBottomSheet( 
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = BlossomColors.SurfaceCard1, 
        contentColor = BlossomColors.TextPrimary, 
        windowInsets = WindowInsets(0) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .fillMaxHeight(0.85f) 
                .padding(horizontal = 24.dp) 
                .navigationBarsPadding() 
        ) { 
            Row( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(top = 14.dp, bottom = 8.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween 
            ) { 
                Surface( 
                    shape = RoundedCornerShape(8.dp), 
                    color = BlossomColors.SakuraRoseContainer, 
                    border = BorderStroke(1.dp, BlossomColors.SakuraRose.copy(alpha = 0.5f)) 
                ) { 
                    Row( 
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), 
                        verticalAlignment = Alignment.CenterVertically 
                    ) { 
                        Icon( 
                            imageVector = Icons.Default.Translate, 
                            contentDescription = null, 
                            tint = BlossomColors.SakuraRose, 
                            modifier = Modifier.size(14.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(6.dp)) 
                        Text( 
                            text = "TRANSLATE JA → EN", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.SakuraRose, 
                            letterSpacing = 0.5.sp 
                        ) 
                    } 
                } 

                IconButton(onClick = { dismissWithAnimation() }) { 
                    Icon( 
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Close", 
                        tint = BlossomColors.TextSecondary 
                    ) 
                } 
            } 

            Column( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .weight(1f) 
                    .nestedScroll(noBounceNestedScroll) 
                    .verticalScroll(rememberScrollState()) 
                    .padding(vertical = 8.dp) 
            ) { 
                Surface( 
                    shape = RoundedCornerShape(14.dp), 
                    color = BlossomColors.SurfaceElevated, 
                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Column(modifier = Modifier.padding(16.dp)) { 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.SpaceBetween 
                        ) { 
                            Text( 
                                text = "JAPANESE", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = BlossomColors.TextMuted, 
                                letterSpacing = 0.8.sp 
                            ) 

                            Row(verticalAlignment = Alignment.CenterVertically) { 
                                IconButton( 
                                    onClick = { 
                                        ttsHelper.speak(sourceText) 
                                    }, 
                                    modifier = Modifier.size(24.dp) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp, 
                                        contentDescription = "Pronounce Japanese", 
                                        tint = BlossomColors.SakuraRose, 
                                        modifier = Modifier.size(16.dp) 
                                    ) 
                                } 

                                Spacer(modifier = Modifier.width(6.dp)) 

                                IconButton( 
                                    onClick = { 
                                        clipboardManager.setText(AnnotatedString(sourceText)) 
                                        Toast.makeText(context, "Japanese copied", Toast.LENGTH_SHORT).show() 
                                    }, 
                                    modifier = Modifier.size(24.dp) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.Default.ContentCopy, 
                                        contentDescription = "Copy Japanese", 
                                        tint = BlossomColors.TextMuted, 
                                        modifier = Modifier.size(14.dp) 
                                    ) 
                                } 
                            } 
                        } 

                        Spacer(modifier = Modifier.height(10.dp)) 

                        JapaneseFuriganaDisplay( 
                            segments = resolvedSegments, 
                            onSegmentClick = { word -> 
                                ttsHelper.speak(word) 
                            } 
                        ) 

                        if (!isLoading && !result?.romaji.isNullOrBlank()) { 
                            Spacer(modifier = Modifier.height(8.dp)) 
                            Text( 
                                text = result!!.romaji, 
                                fontSize = 13.sp, 
                                color = BlossomColors.TextSecondary, 
                                fontWeight = FontWeight.Normal 
                            ) 
                        } 
                    } 
                } 

                Spacer(modifier = Modifier.height(12.dp)) 

                Surface( 
                    shape = RoundedCornerShape(14.dp), 
                    color = BlossomColors.SurfaceElevated, 
                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Column(modifier = Modifier.padding(16.dp)) { 
                        Row( 
                            modifier = Modifier.fillMaxWidth(), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.SpaceBetween 
                        ) { 
                            Text( 
                                text = "ENGLISH TRANSLATION", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = BlossomColors.TextMuted, 
                                letterSpacing = 0.8.sp 
                            ) 

                            if (!isLoading && result != null && result!!.translatedText.isNotBlank()) { 
                                IconButton( 
                                    onClick = { 
                                        clipboardManager.setText(AnnotatedString(result!!.translatedText)) 
                                        Toast.makeText(context, "Translation copied", Toast.LENGTH_SHORT).show() 
                                    }, 
                                    modifier = Modifier.size(24.dp) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.Default.ContentCopy, 
                                        contentDescription = "Copy Translation", 
                                        tint = BlossomColors.TextMuted, 
                                        modifier = Modifier.size(14.dp) 
                                    ) 
                                } 
                            } 
                        } 

                        Spacer(modifier = Modifier.height(10.dp)) 

                        if (isLoading) { 
                            Row( 
                                verticalAlignment = Alignment.CenterVertically, 
                                modifier = Modifier.padding(vertical = 12.dp) 
                            ) { 
                                CircularProgressIndicator( 
                                    modifier = Modifier.size(18.dp), 
                                    strokeWidth = 2.dp, 
                                    color = BlossomColors.SakuraRose 
                                ) 
                                Spacer(modifier = Modifier.width(12.dp)) 
                                Text( 
                                    text = "Translating...", 
                                    fontSize = 14.sp, 
                                    color = BlossomColors.TextSecondary 
                                ) 
                            } 
                        } else if (errorMsg != null) { 
                            Column { 
                                Text( 
                                    text = errorMsg ?: "Translation failed", 
                                    fontSize = 14.sp, 
                                    color = BlossomColors.BlossomAmber 
                                ) 
                                Spacer(modifier = Modifier.height(8.dp)) 
                                OutlinedButton( 
                                    onClick = { doTranslate() }, 
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BlossomColors.SakuraRose) 
                                ) { 
                                    Icon( 
                                        imageVector = Icons.Default.Refresh, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(16.dp) 
                                    ) 
                                    Spacer(modifier = Modifier.width(6.dp)) 
                                    Text("Retry") 
                                } 
                            } 
                        } else { 
                            Text( 
                                text = result?.translatedText ?: "No translation available", 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Normal, 
                                color = BlossomColors.TextPrimary, 
                                lineHeight = 24.sp 
                            ) 
                        } 
                    } 
                } 
            } 

            Spacer(modifier = Modifier.height(12.dp)) 

            Squircle3DButton( 
                onClick = { 
                    scope.launch { 
                        try { 
                            sheetState.hide() 
                        } finally { 
                            onDismiss() 
                            onNavigateToJisho(sourceText) 
                        } 
                    } 
                }, 
                shape = RoundedCornerShape(12.dp), 
                containerColor = BlossomColors.SakuraRose, 
                depth = 3.dp, 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(48.dp) 
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.Center 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Search, 
                        contentDescription = null, 
                        tint = BlossomColors.BlossomWhite, 
                        modifier = Modifier.size(18.dp) 
                    ) 
                    Spacer(modifier = Modifier.width(8.dp)) 
                    Text( 
                        text = "Look up in Jisho", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = BlossomColors.BlossomWhite 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
        } 
    } 
} 

@OptIn(ExperimentalLayoutApi::class) 
@Composable 
private fun JapaneseFuriganaDisplay( 
    segments: List<RubySegment>, 
    onSegmentClick: ((String) -> Unit)? = null 
) { 
    val hasAnyRuby = segments.any { !it.ruby.isNullOrBlank() } 
    
    FlowRow( 
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.Start, 
        verticalArrangement = Arrangement.spacedBy(4.dp) 
    ) { 
        segments.forEach { seg -> 
            val ruby = seg.ruby?.trim()?.ifBlank { null } 
            val isKanjiText = seg.text.any { StoryTokenizer.isKanji(it) } 
            
            Column( 
                horizontalAlignment = Alignment.CenterHorizontally, 
                verticalArrangement = Arrangement.Bottom, 
                modifier = Modifier 
                    .padding(horizontal = 1.dp) 
                    .clickable(enabled = onSegmentClick != null) { 
                        onSegmentClick?.invoke(seg.text) 
                    } 
            ) { 
                if (hasAnyRuby) { 
                    if (!ruby.isNullOrBlank()) { 
                        Text( 
                            text = ruby, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = BlossomColors.SakuraRose, 
                            maxLines = 1, 
                            lineHeight = 12.sp 
                        ) 
                    } else { 
                        Spacer(modifier = Modifier.height(14.dp)) 
                    } 
                } 
                
                Text( 
                    text = seg.text, 
                    fontSize = 19.sp, 
                    fontWeight = if (isKanjiText) FontWeight.SemiBold else FontWeight.Normal, 
                    color = BlossomColors.TextPrimary, 
                    lineHeight = 26.sp 
                ) 
            } 
        } 
    } 
} 
