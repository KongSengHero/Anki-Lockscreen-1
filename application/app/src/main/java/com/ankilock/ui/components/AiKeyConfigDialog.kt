package com.ankilock.ui.components
    
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import android.widget.Toast 
import androidx.compose.material.icons.filled.Close 
import androidx.compose.material.icons.filled.ContentCopy 
import androidx.compose.ui.platform.LocalClipboardManager 
import androidx.compose.ui.text.AnnotatedString 
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.ai.AiServiceHelper
import com.ankilock.data.PreferencesManager
import com.ankilock.ui.blossom.BlossomColors
import com.ankilock.ui.blossom.BlossomShapes
import com.ankilock.ui.reading.FishAudioDialog 
import com.ankilock.ui.reading.FishAudioVoiceDialog 
import com.ankilock.ui.reading.GeminiModelDialog 
import kotlinx.coroutines.launch
    
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiKeyConfigDialog( 
    prefs: PreferencesManager, 
    onDismiss: () -> Unit, 
    onSaved: () -> Unit
) { 
    val context = LocalContext.current 
    val clipboardManager = LocalClipboardManager.current 
    val scope = rememberCoroutineScope() 
    
    val providers = listOf( 
        "gemini" to "Google Gemini (Free Tier)", 
        "openai" to "OpenAI (GPT-4o mini)", 
        "groq" to "Groq (Llama 3.1)" 
    ) 
    
    var apiKeyText by remember { mutableStateOf(prefs.aiApiKey) } 
    var selectedProvider by remember { mutableStateOf(prefs.aiProvider) } 
    var selectedModel by remember { mutableStateOf(prefs.aiModel) } 
    var isProviderDropdownExpanded by remember { mutableStateOf(false) } 
    var isKeyVisible by remember { mutableStateOf(false) } 
    var wallhavenKeyText by remember { mutableStateOf(prefs.wallhavenApiKey) } 
    var isWallhavenKeyVisible by remember { mutableStateOf(false) } 
    var isTesting by remember { mutableStateOf(false) } 
    var testResult by remember { mutableStateOf<String?>(null) } 
    var isTestSuccess by remember { mutableStateOf(false) } 
    
    var showModelDialog by remember { mutableStateOf(false) } 
    var showVoiceDialog by remember { mutableStateOf(false) } 
    var fishAudioApiKey by remember { mutableStateOf(prefs.fishAudioApiKey ?: "") } 
    var isFishAudioKeyVisible by remember { mutableStateOf(false) } 
    var fishAudioVoiceId by remember { mutableStateOf(prefs.fishAudioVoiceId) } 
    var fishAudioVoiceName by remember { mutableStateOf(prefs.fishAudioVoiceName ?: "") } 
    
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
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = BlossomColors.BackgroundDeep, 
        windowInsets = WindowInsets(0) 
    ) { 
        Column( 
            modifier = Modifier 
                .fillMaxWidth() 
                .fillMaxHeight(0.88f) 
                .nestedScroll(noBounceNestedScroll) 
                .verticalScroll(rememberScrollState()) 
                .padding(horizontal = 22.dp) 
                .navigationBarsPadding() 
                .padding(bottom = 16.dp) 
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
                        color = BlossomColors.SakuraRose.copy(alpha = 0.15f), 
                        modifier = Modifier.size(38.dp) 
                    ) { 
                        Icon( 
                            Icons.Default.Key, 
                            contentDescription = null, 
                            tint = BlossomColors.SakuraRose, 
                            modifier = Modifier 
                                .padding(8.dp) 
                                .size(22.dp) 
                        ) 
                    } 
                    Column { 
                        Text( 
                            "API Configuration", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        Text( 
                            "Powers Listening evaluation & Stories", 
                            fontSize = 12.sp, 
                            color = BlossomColors.TextSecondary 
                        ) 
                    } 
                } 
                IconButton( 
                    onClick = onDismiss, 
                    modifier = Modifier.size(36.dp) 
                ) { 
                    Icon( 
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Close", 
                        tint = BlossomColors.TextSecondary 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
            
            Text( 
                "AI Model Provider", 
                fontSize = 13.sp, 
                fontWeight = FontWeight.SemiBold, 
                color = BlossomColors.TextPrimary 
            ) 
            Spacer(modifier = Modifier.height(6.dp)) 
            
            ExposedDropdownMenuBox( 
                expanded = isProviderDropdownExpanded, 
                onExpandedChange = { isProviderDropdownExpanded = it } 
            ) { 
                OutlinedTextField( 
                    value = providers.find { it.first == selectedProvider }?.second ?: selectedProvider, 
                    onValueChange = {}, 
                    readOnly = true, 
                    trailingIcon = { 
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProviderDropdownExpanded) 
                    }, 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedContainerColor = BlossomColors.SurfaceElevated, 
                        unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                        focusedTextColor = BlossomColors.TextPrimary, 
                        unfocusedTextColor = BlossomColors.TextPrimary, 
                        focusedBorderColor = BlossomColors.SakuraRose, 
                        unfocusedBorderColor = BlossomColors.CardBorder 
                    ), 
                    shape = RoundedCornerShape(12.dp), 
                    modifier = Modifier 
                        .menuAnchor() 
                        .fillMaxWidth() 
                ) 
                
                ExposedDropdownMenu( 
                    expanded = isProviderDropdownExpanded, 
                    onDismissRequest = { isProviderDropdownExpanded = false }, 
                    modifier = Modifier.background(BlossomColors.SurfaceElevated) 
                ) { 
                    providers.forEach { (key, label) -> 
                        DropdownMenuItem( 
                            text = { Text(label, color = BlossomColors.TextPrimary, fontSize = 14.sp) }, 
                            onClick = { 
                                selectedProvider = key 
                                isProviderDropdownExpanded = false 
                            } 
                        ) 
                    } 
                } 
            } 
            
            if (selectedProvider == "gemini") { 
                Spacer(modifier = Modifier.height(14.dp)) 
                Text( 
                    "Gemini Model", 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = BlossomColors.TextPrimary 
                ) 
                Spacer(modifier = Modifier.height(6.dp)) 
                
                Surface( 
                    onClick = { showModelDialog = true }, 
                    shape = RoundedCornerShape(12.dp), 
                    color = BlossomColors.SurfaceElevated, 
                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Row( 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .padding(horizontal = 14.dp, vertical = 12.dp), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.SpaceBetween 
                    ) { 
                        Row( 
                            verticalAlignment = Alignment.CenterVertically, 
                            modifier = Modifier.weight(1f, fill = false) 
                        ) { 
                            Icon( 
                                Icons.Filled.AutoAwesome, 
                                contentDescription = null, 
                                tint = BlossomColors.SakuraRose, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                            Spacer(modifier = Modifier.width(10.dp)) 
                            Text( 
                                text = "Model: ${PreferencesManager.getModelDisplayName(selectedModel)}", 
                                fontSize = 13.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = BlossomColors.TextPrimary, 
                                maxLines = 1, 
                                overflow = TextOverflow.Ellipsis 
                            ) 
                        } 
                        Spacer(modifier = Modifier.width(6.dp)) 
                        Row(verticalAlignment = Alignment.CenterVertically) { 
                            Text( 
                                text = "Switch", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = BlossomColors.TextSecondary, 
                                maxLines = 1, 
                                softWrap = false 
                            ) 
                            Icon( 
                                Icons.Filled.ArrowDropDown, 
                                contentDescription = null, 
                                tint = BlossomColors.TextSecondary, 
                                modifier = Modifier.size(18.dp) 
                            ) 
                        } 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            Text( 
                "Voice Model (Fish Audio)", 
                fontSize = 13.sp, 
                fontWeight = FontWeight.SemiBold, 
                color = BlossomColors.TextPrimary 
            ) 
            Spacer(modifier = Modifier.height(6.dp)) 
            
            Surface( 
                onClick = { showVoiceDialog = true }, 
                shape = RoundedCornerShape(12.dp), 
                color = BlossomColors.SurfaceElevated, 
                border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                Row( 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(horizontal = 14.dp, vertical = 12.dp), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    Row( 
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.weight(1f, fill = false) 
                    ) { 
                        Icon( 
                            Icons.Filled.GraphicEq, 
                            contentDescription = null, 
                            tint = BlossomColors.SakuraRose, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                        Spacer(modifier = Modifier.width(10.dp)) 
                        Text( 
                            text = "Voice: ${if (fishAudioVoiceName.isNotBlank()) fishAudioVoiceName else PreferencesManager.getPresetVoiceDisplayName(fishAudioVoiceId)}", 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = BlossomColors.TextPrimary, 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis 
                        ) 
                    } 
                    Spacer(modifier = Modifier.width(6.dp)) 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        Text( 
                            text = "Switch", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = BlossomColors.TextSecondary, 
                            maxLines = 1, 
                            softWrap = false 
                        ) 
                        Icon( 
                            Icons.Filled.ArrowDropDown, 
                            contentDescription = null, 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                } 
            } 
            
            Text( 
                "Gemini API Key", 
                fontSize = 13.sp, 
                fontWeight = FontWeight.SemiBold, 
                color = BlossomColors.TextPrimary 
            ) 
            Spacer(modifier = Modifier.height(6.dp)) 
            
            OutlinedTextField( 
                value = apiKeyText, 
                onValueChange = { apiKeyText = it }, 
                placeholder = { Text("Paste your API key here", color = BlossomColors.TextMuted, fontSize = 13.sp) }, 
                singleLine = true, 
                visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(), 
                trailingIcon = { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        if (apiKeyText.isNotBlank()) { 
                            IconButton( 
                                onClick = { 
                                    clipboardManager.setText(AnnotatedString(apiKeyText)) 
                                    Toast.makeText(context, "API Key copied", Toast.LENGTH_SHORT).show() 
                                } 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.ContentCopy, 
                                    contentDescription = "Copy API Key", 
                                    tint = BlossomColors.TextSecondary, 
                                    modifier = Modifier.size(18.dp) 
                                ) 
                            } 
                        } 
                        IconButton(onClick = { isKeyVisible = !isKeyVisible }) { 
                            Icon( 
                                if (isKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, 
                                contentDescription = "Toggle Visibility", 
                                tint = BlossomColors.TextSecondary 
                            ) 
                        } 
                    } 
                }, 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedBorderColor = BlossomColors.SakuraRose, 
                    unfocusedBorderColor = BlossomColors.CardBorder 
                ), 
                shape = RoundedCornerShape(12.dp), 
                modifier = Modifier.fillMaxWidth() 
            ) 
            
            if (selectedProvider == "gemini") { 
                Surface( 
                    shape = RoundedCornerShape(8.dp), 
                    color = BlossomColors.SurfaceElevated, 
                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(top = 6.dp) 
                        .clickable { 
                            try { 
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey")) 
                                context.startActivity(intent) 
                            } catch (_: Exception) { 
                            } 
                        } 
                ) { 
                    Row( 
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), 
                        verticalAlignment = Alignment.CenterVertically 
                    ) { 
                        Text( 
                            "Get Gemini Key (aistudio.google.com)", 
                            fontSize = 11.sp, 
                            color = BlossomColors.SakuraRose, 
                            fontWeight = FontWeight.Medium, 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis, 
                            softWrap = false, 
                            modifier = Modifier.weight(1f) 
                        ) 
                        Icon( 
                            Icons.AutoMirrored.Filled.OpenInNew, 
                            contentDescription = null, 
                            tint = BlossomColors.SakuraRose, 
                            modifier = Modifier.size(13.dp) 
                        ) 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            
            Text( 
                "Wallhaven API Key (Optional Artwork Key)", 
                fontSize = 13.sp, 
                fontWeight = FontWeight.SemiBold, 
                color = BlossomColors.TextPrimary 
            ) 
            Spacer(modifier = Modifier.height(6.dp)) 
            
            OutlinedTextField( 
                value = wallhavenKeyText, 
                onValueChange = { wallhavenKeyText = it }, 
                placeholder = { Text("Optional - works free without key", color = BlossomColors.TextMuted, fontSize = 13.sp) }, 
                singleLine = true, 
                visualTransformation = if (isWallhavenKeyVisible) VisualTransformation.None else PasswordVisualTransformation(), 
                trailingIcon = { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        if (wallhavenKeyText.isNotBlank()) { 
                            IconButton( 
                                onClick = { 
                                    clipboardManager.setText(AnnotatedString(wallhavenKeyText)) 
                                    Toast.makeText(context, "API Key copied", Toast.LENGTH_SHORT).show() 
                                } 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.ContentCopy, 
                                    contentDescription = "Copy API Key", 
                                    tint = BlossomColors.TextSecondary, 
                                    modifier = Modifier.size(18.dp) 
                                ) 
                            } 
                        } 
                        IconButton(onClick = { isWallhavenKeyVisible = !isWallhavenKeyVisible }) { 
                            Icon( 
                                if (isWallhavenKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, 
                                contentDescription = null, 
                                tint = BlossomColors.TextSecondary 
                            ) 
                        } 
                    } 
                }, 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedBorderColor = BlossomColors.SakuraRose, 
                    unfocusedBorderColor = BlossomColors.CardBorder 
                ), 
                shape = RoundedCornerShape(12.dp), 
                modifier = Modifier.fillMaxWidth() 
            ) 
            
            Surface( 
                shape = RoundedCornerShape(8.dp), 
                color = BlossomColors.SurfaceElevated, 
                border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(top = 6.dp) 
                    .clickable { 
                        try { 
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wallhaven.cc/settings/account")) 
                            context.startActivity(intent) 
                        } catch (_: Exception) { 
                        } 
                    } 
            ) { 
                Row( 
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    Text( 
                        "Get Wallhaven Key (wallhaven.cc/settings/account)", 
                        fontSize = 11.sp, 
                        color = BlossomColors.SakuraRose, 
                        fontWeight = FontWeight.Medium, 
                        modifier = Modifier.weight(1f) 
                    ) 
                    Icon( 
                        Icons.AutoMirrored.Filled.OpenInNew, 
                        contentDescription = null, 
                        tint = BlossomColors.SakuraRose, 
                        modifier = Modifier.size(13.dp) 
                    ) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(14.dp)) 
            
            Text( 
                "Fish Audio API Key (Optional Narration Key)", 
                fontSize = 13.sp, 
                fontWeight = FontWeight.SemiBold, 
                color = BlossomColors.TextPrimary 
            ) 
            Spacer(modifier = Modifier.height(6.dp)) 
            
            OutlinedTextField( 
                value = fishAudioApiKey, 
                onValueChange = { fishAudioApiKey = it }, 
                placeholder = { Text("Optional - add key for Fish Audio TTS", color = BlossomColors.TextMuted, fontSize = 13.sp) }, 
                singleLine = true, 
                visualTransformation = if (isFishAudioKeyVisible) VisualTransformation.None else PasswordVisualTransformation(), 
                trailingIcon = { 
                    Row(verticalAlignment = Alignment.CenterVertically) { 
                        if (fishAudioApiKey.isNotBlank()) { 
                            IconButton( 
                                onClick = { 
                                    clipboardManager.setText(AnnotatedString(fishAudioApiKey)) 
                                    Toast.makeText(context, "API Key copied", Toast.LENGTH_SHORT).show() 
                                } 
                            ) { 
                                Icon( 
                                    imageVector = Icons.Default.ContentCopy, 
                                    contentDescription = "Copy API Key", 
                                    tint = BlossomColors.TextSecondary, 
                                    modifier = Modifier.size(18.dp) 
                                ) 
                            } 
                        } 
                        IconButton(onClick = { isFishAudioKeyVisible = !isFishAudioKeyVisible }) { 
                            Icon( 
                                if (isFishAudioKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, 
                                contentDescription = null, 
                                tint = BlossomColors.TextSecondary 
                            ) 
                        } 
                    } 
                }, 
                colors = OutlinedTextFieldDefaults.colors( 
                    focusedContainerColor = BlossomColors.SurfaceElevated, 
                    unfocusedContainerColor = BlossomColors.SurfaceElevated, 
                    focusedTextColor = BlossomColors.TextPrimary, 
                    unfocusedTextColor = BlossomColors.TextPrimary, 
                    focusedBorderColor = BlossomColors.SakuraRose, 
                    unfocusedBorderColor = BlossomColors.CardBorder 
                ), 
                shape = RoundedCornerShape(12.dp), 
                modifier = Modifier.fillMaxWidth() 
            ) 
            
            Surface( 
                shape = RoundedCornerShape(8.dp), 
                color = BlossomColors.SurfaceElevated, 
                border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .padding(top = 6.dp) 
                    .clickable { 
                        try { 
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://fish.audio")) 
                            context.startActivity(intent) 
                        } catch (_: Exception) { 
                        } 
                    } 
            ) { 
                Row( 
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), 
                    verticalAlignment = Alignment.CenterVertically 
                ) { 
                    Text( 
                        "Get Fish Audio Key (fish.audio)", 
                        fontSize = 11.sp, 
                        color = BlossomColors.SakuraRose, 
                        fontWeight = FontWeight.Medium, 
                        modifier = Modifier.weight(1f) 
                    ) 
                    Icon( 
                        Icons.AutoMirrored.Filled.OpenInNew, 
                        contentDescription = null, 
                        tint = BlossomColors.SakuraRose, 
                        modifier = Modifier.size(13.dp) 
                    ) 
                } 
            } 
            
            if (testResult != null) { 
                Spacer(modifier = Modifier.height(10.dp)) 
                Surface( 
                    shape = RoundedCornerShape(8.dp), 
                    color = if (isTestSuccess) BlossomColors.MatchaSageContainer else BlossomColors.SakuraRoseContainer, 
                    border = BorderStroke(1.dp, if (isTestSuccess) BlossomColors.MatchaSage else BlossomColors.BlossomRed), 
                    modifier = Modifier.fillMaxWidth() 
                ) { 
                    Row( 
                        modifier = Modifier.padding(8.dp), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(6.dp) 
                    ) { 
                        Icon( 
                            if (isTestSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline, 
                            contentDescription = null, 
                            tint = if (isTestSuccess) BlossomColors.MatchaSage else BlossomColors.BlossomRed, 
                            modifier = Modifier.size(16.dp) 
                        ) 
                        Text( 
                            testResult ?: "", 
                            fontSize = 12.sp, 
                            color = if (isTestSuccess) BlossomColors.MatchaSage else BlossomColors.BlossomRed 
                        ) 
                    } 
                } 
            } 
            
            Spacer(modifier = Modifier.height(18.dp)) 
            
            Row( 
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(8.dp) 
            ) { 
                Squircle3DButton( 
                    onClick = { 
                        if (apiKeyText.isNotBlank()) { 
                            isTesting = true 
                            testResult = null 
                            scope.launch { 
                                val res = AiServiceHelper.testConnection( 
                                    apiKey = apiKeyText, 
                                    provider = selectedProvider, 
                                    model = selectedModel 
                                ) 
                                isTesting = false 
                                if (res.isSuccess) { 
                                    isTestSuccess = true 
                                    testResult = res.getOrNull() ?: "Connection Successful!" 
                                } else { 
                                    isTestSuccess = false 
                                    testResult = res.exceptionOrNull()?.message ?: "Failed to connect" 
                                } 
                            } 
                        } 
                    }, 
                    enabled = apiKeyText.isNotBlank() && !isTesting, 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(46.dp), 
                    containerColor = BlossomColors.SurfaceElevated, 
                    bevelColor = BlossomColors.CardBorder, 
                    shape = BlossomShapes.SquircleMedium, 
                    depth = 3.dp 
                ) { 
                    if (isTesting) { 
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BlossomColors.SakuraRose, strokeWidth = 2.dp) 
                    } else { 
                        Text("Test Key", color = BlossomColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) 
                    } 
                } 
                
                Squircle3DButton( 
                    onClick = { 
                        prefs.aiApiKey = apiKeyText.trim() 
                        prefs.aiProvider = selectedProvider 
                        prefs.aiModel = selectedModel 
                        prefs.wallhavenApiKey = wallhavenKeyText.trim() 
                        prefs.fishAudioApiKey = fishAudioApiKey.trim() 
                        prefs.fishAudioVoiceId = fishAudioVoiceId.trim() 
                        if (fishAudioVoiceName.isNotBlank()) { 
                            prefs.fishAudioVoiceName = fishAudioVoiceName.trim() 
                        } 
                        onSaved() 
                    }, 
                    modifier = Modifier 
                        .weight(1f) 
                        .height(46.dp), 
                    containerColor = BlossomColors.SakuraRose, 
                    bevelColor = BlossomColors.SakuraRoseLip, 
                    shape = BlossomShapes.SquircleMedium, 
                    depth = 3.dp 
                ) { 
                    Text("Save Key", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) 
                } 
            } 
            
            Spacer(modifier = Modifier.height(16.dp)) 
        } 
    } 
    
    if (showModelDialog) { 
        GeminiModelDialog( 
            currentModel = selectedModel, 
            onSave = { model -> 
                selectedModel = model 
                prefs.aiModel = model 
                showModelDialog = false 
            }, 
            onDismiss = { showModelDialog = false } 
        ) 
    } 
    
    if (showVoiceDialog) { 
        FishAudioVoiceDialog( 
            currentVoiceId = fishAudioVoiceId, 
            currentVoiceName = fishAudioVoiceName, 
            apiKey = fishAudioApiKey, 
            onSelectVoice = { newId, newName -> 
                fishAudioVoiceId = newId 
                fishAudioVoiceName = newName 
                prefs.fishAudioVoiceId = newId 
                prefs.fishAudioVoiceName = newName 
                showVoiceDialog = false 
            }, 
            onDismiss = { showVoiceDialog = false } 
        ) 
    } 
} 
