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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.translation.TranslationResult
import com.ankilock.translation.TranslatorService
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.util.JapaneseTtsHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationBottomSheet( 
    sourceText: String, 
    onDismiss: () -> Unit, 
    onNavigateToJisho: (String) -> Unit = {} 
) { 
    val context = LocalContext.current 
    val clipboardManager = LocalClipboardManager.current 
    val scope = rememberCoroutineScope() 
    val translatorService = remember { TranslatorService() } 
    val ttsHelper = remember { JapaneseTtsHelper(context) } 
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) 

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
        contentColor = BlossomColors.TextPrimary 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .fillMaxHeight(0.85f) 
                .padding(horizontal = 24.dp) 
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
                    .verticalScroll(rememberScrollState()) 
                    .padding(vertical = 8.dp) 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.Top, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Column(modifier = Modifier.weight(1f)) { 
                        Text( 
                            text = sourceText, 
                            fontSize = if (sourceText.length > 20) 22.sp else 28.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextPrimary, 
                            lineHeight = if (sourceText.length > 20) 30.sp else 36.sp 
                        ) 

                        val romaji = result?.romaji ?: "" 
                        if (romaji.isNotBlank()) { 
                            Spacer(modifier = Modifier.height(4.dp)) 
                            Text( 
                                text = romaji, 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = BlossomColors.SakuraRose 
                            ) 
                        } 
                    } 

                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        IconButton( 
                            onClick = { ttsHelper.speak(sourceText) }, 
                            modifier = Modifier.size(36.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp, 
                                contentDescription = "Pronounce", 
                                tint = BlossomColors.TextSecondary, 
                                modifier = Modifier.size(20.dp) 
                            ) 
                        } 
                        IconButton( 
                            onClick = { 
                                clipboardManager.setText(AnnotatedString(sourceText)) 
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show() 
                            }, 
                            modifier = Modifier.size(36.dp) 
                        ) { 
                            Icon( 
                                imageVector = Icons.Default.ContentCopy, 
                                contentDescription = "Copy text", 
                                tint = BlossomColors.TextSecondary, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                        } 
                    } 
                } 

                Spacer(modifier = Modifier.height(16.dp)) 

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

            Button( 
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
                colors = ButtonDefaults.buttonColors( 
                    containerColor = BlossomColors.SakuraRose, 
                    contentColor = Color.White 
                ), 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(48.dp) 
            ) { 
                Icon( 
                    imageVector = Icons.Default.Search, 
                    contentDescription = null, 
                    modifier = Modifier.size(18.dp) 
                ) 
                Spacer(modifier = Modifier.width(8.dp)) 
                Text( 
                    text = "Look up in Jisho", 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.SemiBold 
                ) 
            } 

            Spacer(modifier = Modifier.height(16.dp)) 
        } 
    } 
} 
