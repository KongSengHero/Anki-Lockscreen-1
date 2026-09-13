package com.ankilock.translation

import android.content.Context
import com.ankilock.anki.JapaneseFieldParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody 
import okhttp3.OkHttpClient 
import okhttp3.Request 
import org.json.JSONArray 
import java.io.IOException 
import java.util.concurrent.TimeUnit 

data class TranslationResult( 
    val sourceText: String, 
    val translatedText: String, 
    val romaji: String 
) 

class TranslatorService( 
    private val context: Context? = null, 
    private val client: OkHttpClient = OkHttpClient.Builder() 
        .connectTimeout(12, TimeUnit.SECONDS) 
        .readTimeout(15, TimeUnit.SECONDS) 
        .build() 
) { 
    private val cacheManager = context?.let { TranslationCacheManager(it) } 

    suspend fun translate(text: String): Result<TranslationResult> = withContext(Dispatchers.IO) { 
        val trimmed = text.trim() 
        if (trimmed.isBlank()) { 
            return@withContext Result.success(TranslationResult("", "", "")) 
        } 

        val cached = cacheManager?.get(trimmed) 
        if (cached != null && cached.translatedText.isNotBlank()) { 
            return@withContext Result.success(cached) 
        } 

        try { 
            val formBody = FormBody.Builder() 
                .add("client", "gtx") 
                .add("sl", "ja") 
                .add("tl", "en") 
                .add("dt", "t") 
                .add("dt", "rm") 
                .add("q", trimmed) 
                .build() 

            val request = Request.Builder() 
                .url("https://translate.googleapis.com/translate_a/single") 
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36") 
                .post(formBody) 
                .build() 

            val response = client.newCall(request).execute() 
            if (!response.isSuccessful) { 
                if (cached != null && cached.translatedText.isNotBlank()) { 
                    return@withContext Result.success(cached) 
                } 
                return@withContext Result.failure( 
                    IOException("Translation request failed: HTTP ${response.code}") 
                ) 
            } 

            val body = response.body?.string() 
                ?: return@withContext Result.failure(IOException("Empty translation response")) 

            val rootArray = JSONArray(body) 
            val outerArray = rootArray.optJSONArray(0) 

            val translationSb = StringBuilder() 
            var extractedRomaji: String? = null 

            if (outerArray != null) { 
                for (i in 0 until outerArray.length()) { 
                    val piece = outerArray.optJSONArray(i) ?: continue 

                    val translatedPiece = piece.optString(0, "") 
                    if (translatedPiece.isNotBlank() && translatedPiece != "null") { 
                        translationSb.append(translatedPiece) 
                    } 

                    for (k in listOf(3, 2)) { 
                        val candidate = piece.optString(k, "") 
                        if (candidate.isNotBlank() && candidate != "null" && isLatinString(candidate)) { 
                            extractedRomaji = candidate 
                        } 
                    } 
                } 
            } 

            val finalTranslation = translationSb.toString().trim() 
            if (finalTranslation.isBlank()) { 
                return@withContext Result.failure(IOException("No translation returned")) 
            } 

            val finalRomaji = extractedRomaji?.trim()?.ifBlank { null } 
                ?: JapaneseFieldParser.kanaToRomaji(trimmed).ifBlank { "" } 

            val res = TranslationResult( 
                sourceText = trimmed, 
                translatedText = finalTranslation, 
                romaji = finalRomaji 
            ) 
            cacheManager?.put(res) 
            Result.success(res) 
        } catch (e: Exception) { 
            if (cached != null && cached.translatedText.isNotBlank()) { 
                return@withContext Result.success(cached) 
            } 
            Result.failure(e) 
        } 
    } 

    private fun isLatinString(str: String): Boolean { 
        return str.any { it in 'a'..'z' || it in 'A'..'Z' } 
    } 
} 
