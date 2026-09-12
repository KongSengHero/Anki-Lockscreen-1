package com.ankilock.ui.components
    
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ankilock.ai.AiServiceHelper
import com.ankilock.data.PreferencesManager
import kotlinx.coroutines.launch
    
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiKeyConfigDialog( 
    prefs: PreferencesManager, 
    onDismiss: () -> Unit, 
    onSaved: () -> Unit
) { 
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var apiKeyText by remember { mutableStateOf(prefs.aiApiKey) }
    var selectedProvider by remember { mutableStateOf(prefs.aiProvider) }
    var selectedModel by remember { mutableStateOf(prefs.aiModel) }
    var isProviderDropdownExpanded by remember { mutableStateOf(false) }
    var isModelDropdownExpanded by remember { mutableStateOf(false) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var isTestSuccess by remember { mutableStateOf(false) }
    
    val providers = listOf( 
        "gemini" to "Google Gemini (Free Tier)", 
        "openai" to "OpenAI (GPT-4o mini)", 
        "groq" to "Groq (Llama 3.1)"
    )
    
    val geminiModels = listOf( 
        "auto" to "Auto Detect (Recommended)", 
        "gemini-3.7-flash" to "Gemini 3.7 Flash", 
        "gemini-3.7-pro" to "Gemini 3.7 Pro", 
        "gemini-3.5-flash" to "Gemini 3.5 Flash", 
        "gemini-3.0-flash" to "Gemini 3.0 Flash", 
        "gemini-2.5-flash" to "Gemini 2.5 Flash", 
        "gemini-2.0-flash" to "Gemini 2.0 Flash", 
        "gemini-1.5-flash" to "Gemini 1.5 Flash"
    )
    
    Dialog(onDismissRequest = onDismiss) { 
        Surface( 
            shape = RoundedCornerShape(24.dp), 
            color = Color(0xFF0F172A), 
            border = BorderStroke(1.dp, Color(0xFF334155)), 
            shadowElevation = 16.dp, 
            modifier = Modifier.fillMaxWidth()
        ) { 
            Column( 
                modifier = Modifier 
                    .padding(22.dp) 
                    .fillMaxWidth()
            ) { 
                Row( 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) { 
                    Surface( 
                        shape = RoundedCornerShape(12.dp), 
                        color = Color(0xFF38BDF8).copy(alpha = 0.15f), 
                        modifier = Modifier.size(38.dp)
                    ) { 
                        Icon( 
                            Icons.Default.Key, 
                            contentDescription = null, 
                            tint = Color(0xFF38BDF8), 
                            modifier = Modifier 
                                .padding(8.dp) 
                                .size(22.dp)
                        )
                    }
                    Column { 
                        Text( 
                            "AI Configuration", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White
                        )
                        Text( 
                            "Powers Listening evaluation & Forge Story", 
                            fontSize = 12.sp, 
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text( 
                    "AI Model Provider", 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = Color(0xFFE2E8F0)
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
                            focusedContainerColor = Color(0xFF1E293B), 
                            unfocusedContainerColor = Color(0xFF1E293B), 
                            focusedTextColor = Color.White, 
                            unfocusedTextColor = Color.White, 
                            focusedBorderColor = Color(0xFF38BDF8), 
                            unfocusedBorderColor = Color(0xFF475569)
                        ), 
                        shape = RoundedCornerShape(12.dp), 
                        modifier = Modifier 
                            .menuAnchor() 
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu( 
                        expanded = isProviderDropdownExpanded, 
                        onDismissRequest = { isProviderDropdownExpanded = false }, 
                        modifier = Modifier.background(Color(0xFF1E293B))
                    ) { 
                        providers.forEach { (key, label) -> 
                            DropdownMenuItem( 
                                text = { Text(label, color = Color.White, fontSize = 14.sp) }, 
                                onClick = { 
                                    selectedProvider = key
                                    isProviderDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                
                if (selectedProvider == "gemini") { 
                    Spacer(modifier = Modifier.height(12.dp))
                    Text( 
                        "Model Selection", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    ExposedDropdownMenuBox( 
                        expanded = isModelDropdownExpanded, 
                        onExpandedChange = { isModelDropdownExpanded = it }
                    ) { 
                        OutlinedTextField( 
                            value = geminiModels.find { it.first == selectedModel }?.second ?: selectedModel, 
                            onValueChange = { selectedModel = it }, 
                            readOnly = false, 
                            trailingIcon = { 
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelDropdownExpanded)
                            }, 
                            colors = OutlinedTextFieldDefaults.colors( 
                                focusedContainerColor = Color(0xFF1E293B), 
                                unfocusedContainerColor = Color(0xFF1E293B), 
                                focusedTextColor = Color.White, 
                                unfocusedTextColor = Color.White, 
                                focusedBorderColor = Color(0xFF38BDF8), 
                                unfocusedBorderColor = Color(0xFF475569)
                            ), 
                            shape = RoundedCornerShape(12.dp), 
                            modifier = Modifier 
                                .menuAnchor() 
                                .fillMaxWidth()
                        )
                        
                        ExposedDropdownMenu( 
                            expanded = isModelDropdownExpanded, 
                            onDismissRequest = { isModelDropdownExpanded = false }, 
                            modifier = Modifier.background(Color(0xFF1E293B))
                        ) { 
                            geminiModels.forEach { (modelKey, modelLabel) -> 
                                DropdownMenuItem( 
                                    text = { Text(modelLabel, color = Color.White, fontSize = 14.sp) }, 
                                    onClick = { 
                                        selectedModel = modelKey
                                        isModelDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Text( 
                    "API Key", 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                OutlinedTextField( 
                    value = apiKeyText, 
                    onValueChange = { apiKeyText = it }, 
                    placeholder = { Text("Paste your API key here", color = Color(0xFF64748B), fontSize = 13.sp) }, 
                    visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(), 
                    trailingIcon = { 
                        IconButton(onClick = { isKeyVisible = !isKeyVisible }) { 
                            Icon( 
                                if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, 
                                contentDescription = "Toggle Visibility", 
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }, 
                    colors = OutlinedTextFieldDefaults.colors( 
                        focusedContainerColor = Color(0xFF1E293B), 
                        unfocusedContainerColor = Color(0xFF1E293B), 
                        focusedTextColor = Color.White, 
                        unfocusedTextColor = Color.White, 
                        focusedBorderColor = Color(0xFF38BDF8), 
                        unfocusedBorderColor = Color(0xFF475569)
                    ), 
                    shape = RoundedCornerShape(12.dp), 
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (selectedProvider == "gemini") { 
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface( 
                        onClick = { 
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                            context.startActivity(intent)
                        }, 
                        shape = RoundedCornerShape(8.dp), 
                        color = Color(0xFF38BDF8).copy(alpha = 0.10f), 
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f)), 
                        modifier = Modifier.fillMaxWidth()
                    ) { 
                        Row( 
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) { 
                            Text( 
                                "Get Free Gemini Key (aistudio.google.com)", 
                                fontSize = 11.sp, 
                                color = Color(0xFF38BDF8), 
                                fontWeight = FontWeight.Medium, 
                                modifier = Modifier.weight(1f)
                            )
                            Icon( 
                                Icons.AutoMirrored.Filled.OpenInNew, 
                                contentDescription = null, 
                                tint = Color(0xFF38BDF8), 
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
                
                if (testResult != null) { 
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface( 
                        shape = RoundedCornerShape(8.dp), 
                        color = if (isTestSuccess) Color(0xFF065F46).copy(alpha = 0.4f) else Color(0xFF7F1D1D).copy(alpha = 0.4f), 
                        border = BorderStroke(1.dp, if (isTestSuccess) Color(0xFF10B981) else Color(0xFFEF4444)), 
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
                                tint = if (isTestSuccess) Color(0xFF10B981) else Color(0xFFEF4444), 
                                modifier = Modifier.size(16.dp)
                            )
                            Text( 
                                testResult ?: "", 
                                fontSize = 12.sp, 
                                color = if (isTestSuccess) Color(0xFFA7F3D0) else Color(0xFFFECACA)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(18.dp))
                
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) { 
                    OutlinedButton( 
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
                        shape = RoundedCornerShape(12.dp), 
                        border = BorderStroke(1.dp, Color(0xFF475569)), 
                        modifier = Modifier.weight(1f)
                    ) { 
                        if (isTesting) { 
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF38BDF8), strokeWidth = 2.dp)
                        } else { 
                            Text("Test Key", color = Color(0xFFE2E8F0), fontSize = 13.sp)
                        }
                    }
                    
                    Button( 
                        onClick = { 
                            prefs.aiApiKey = apiKeyText.trim()
                            prefs.aiProvider = selectedProvider
                            prefs.aiModel = selectedModel
                            onSaved()
                        }, 
                        shape = RoundedCornerShape(12.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)), 
                        modifier = Modifier.weight(1f)
                    ) { 
                        Text("Save Key", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
