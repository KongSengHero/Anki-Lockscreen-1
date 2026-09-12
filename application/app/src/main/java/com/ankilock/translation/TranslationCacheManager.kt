package com.ankilock.translation
 
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.security.MessageDigest
 
class TranslationCacheManager(context: Context) { 
    private val prefs: SharedPreferences = context.getSharedPreferences("blossom_translation_cache", Context.MODE_PRIVATE) 
    
    private fun hashKey(text: String): String { 
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.trim().toByteArray()) 
        return bytes.joinToString("") { "%02x".format(it) } 
    } 
    
    fun get(text: String): TranslationResult? { 
        val key = hashKey(text) 
        val jsonStr = prefs.getString(key, null) ?: return null 
        return try { 
            val json = JSONObject(jsonStr) 
            TranslationResult( 
                sourceText = json.optString("sourceText", text), 
                translatedText = json.optString("translatedText", ""), 
                romaji = json.optString("romaji", "") 
            ) 
        } catch (_: Exception) { 
            null 
        } 
    } 
    
    fun put(result: TranslationResult) { 
        val key = hashKey(result.sourceText) 
        try { 
            val json = JSONObject().apply { 
                put("sourceText", result.sourceText) 
                put("translatedText", result.translatedText) 
                put("romaji", result.romaji) 
            } 
            prefs.edit().putString(key, json.toString()).apply() 
        } catch (_: Exception) { 
        } 
    } 
}
