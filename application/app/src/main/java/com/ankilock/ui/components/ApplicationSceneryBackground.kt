package com.ankilock.ui.components 

import android.graphics.BitmapFactory 
import androidx.compose.foundation.Image 
import androidx.compose.foundation.background 
import androidx.compose.foundation.layout.Box 
import androidx.compose.foundation.layout.fillMaxSize 
import androidx.compose.foundation.layout.fillMaxWidth 
import androidx.compose.foundation.layout.height 
import androidx.compose.runtime.Composable 
import androidx.compose.runtime.remember 
import androidx.compose.ui.Modifier 
import androidx.compose.ui.graphics.Brush 
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.graphics.asImageBitmap 
import androidx.compose.ui.graphics.graphicsLayer 
import androidx.compose.ui.layout.ContentScale 
import androidx.compose.ui.platform.LocalContext 
import androidx.compose.ui.unit.dp 
import com.ankilock.R 
import com.ankilock.ui.blossom.BlossomColors 
import com.ankilock.util.ImageBlurUtil 

@Composable
fun ApplicationSceneryBackground( 
    appBackgroundType: String, 
    customImageUri: String? = null, 
    blurRadius: Int = 20, 
    dimOpacity: Float = 0.10f, 
    artworkOpacity: Float = 0.5f, 
    modifier: Modifier = Modifier 
) { 
    val context = LocalContext.current 
    val bgBitmap = remember(appBackgroundType, customImageUri) { 
        when (appBackgroundType) { 
            "blossom", "anki_lock", "reading" -> { 
                try { 
                    BitmapFactory.decodeResource(context.resources, R.drawable.anki_lock) 
                } catch (e: Exception) { 
                    null 
                } 
            } 
            "dark_blur", "sunset" -> { 
                ImageBlurUtil.createPresetBackground("dark_blur", 720, 1280) 
            } 
            "custom" -> { 
                if (!customImageUri.isNullOrBlank()) { 
                    try { 
                        val uri = android.net.Uri.parse(customImageUri) 
                        context.contentResolver.openInputStream(uri)?.use { stream -> 
                            BitmapFactory.decodeStream(stream) 
                        } 
                    } catch (e: Exception) { 
                        null 
                    } 
                } else null 
            } 
            else -> null 
        } 
    } 

    val processedBitmap = remember(bgBitmap, blurRadius) { 
        if (bgBitmap != null && blurRadius > 0) { 
            ImageBlurUtil.fastBlur(bgBitmap, 0.25f, blurRadius.coerceIn(1, 60)) 
        } else { 
            bgBitmap 
        } 
    } 

    Box( 
        modifier = modifier 
            .fillMaxSize() 
            .background(BlossomColors.BackgroundDeep) 
    ) { 
        if (processedBitmap != null && appBackgroundType != "transparent" && appBackgroundType != "none") { 
            Box( 
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(520.dp) 
            ) { 
                Image( 
                    bitmap = processedBitmap.asImageBitmap(), 
                    contentDescription = null, 
                    contentScale = ContentScale.Crop, 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .graphicsLayer { alpha = artworkOpacity.coerceIn(0.05f, 1f) } 
                ) 
                if (dimOpacity > 0f) { 
                    Box( 
                        modifier = Modifier 
                            .fillMaxSize() 
                            .background(Color.Black.copy(alpha = dimOpacity.coerceIn(0f, 0.95f))) 
                    ) 
                } 
                Box( 
                    modifier = Modifier 
                        .fillMaxSize() 
                        .background( 
                            Brush.verticalGradient( 
                                colorStops = arrayOf( 
                                    0.0f to Color(0xCC000000), 
                                    0.18f to Color(0x66000000), 
                                    0.32f to Color.Transparent, 
                                    0.55f to BlossomColors.BackgroundDeep.copy(alpha = 0.40f), 
                                    0.80f to BlossomColors.BackgroundDeep.copy(alpha = 0.85f), 
                                    1.0f to BlossomColors.BackgroundDeep 
                                ) 
                            ) 
                        ) 
                ) 
            } 
        } 
    } 
} 
