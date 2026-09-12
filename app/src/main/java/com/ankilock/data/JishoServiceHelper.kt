package com.ankilock.data
    
import com.ankilock.util.RomajiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
    
data class JishoWordResult( 
    val keyword: String, 
    val surface: String, 
    val reading: String, 
    val isCommon: Boolean, 
    val jlpt: String?, 
    val partsOfSpeech: List<String>, 
    val definitions: List<String> 
) 
    
object JishoServiceHelper { 
    
    private val legacyCache = ConcurrentHashMap<String, JishoWordResult>() 
    private val wordsCache = ConcurrentHashMap<String, List<JishoWord>>() 
    
    suspend fun searchWords(keyword: String): Result<List<JishoWord>> { 
        val trimmed = keyword.trim() 
        if (trimmed.isBlank()) return Result.success(emptyList()) 
        
        wordsCache[trimmed.lowercase()]?.let { return Result.success(it) } 
        
        return withContext(Dispatchers.IO) { 
            var connection: HttpURLConnection? = null 
            try { 
                val encoded = URLEncoder.encode(trimmed, "UTF-8") 
                val url = URL("https://jisho.org/api/v1/search/words?keyword=$encoded") 
                connection = url.openConnection() as HttpURLConnection 
                connection.requestMethod = "GET" 
                connection.connectTimeout = 12000 
                connection.readTimeout = 12000 
                connection.setRequestProperty("User-Agent", "AnkiLockscreen/1.0") 
                
                val responseCode = connection.responseCode 
                if (responseCode == HttpURLConnection.HTTP_OK) { 
                    val reader = BufferedReader(InputStreamReader(connection.inputStream)) 
                    val response = reader.readText() 
                    reader.close() 
                    
                    val list = parseJishoWords(response) 
                    wordsCache[trimmed.lowercase()] = list 
                    Result.success(list) 
                } else { 
                    Result.failure(Exception("Jisho API HTTP error: $responseCode")) 
                } 
            } catch (e: Exception) { 
                Result.failure(e) 
            } finally { 
                connection?.disconnect() 
            } 
        } 
    } 
    
    suspend fun searchWord(keyword: String): JishoWordResult? { 
        val trimmed = keyword.trim() 
        if (trimmed.isBlank()) return null 
        
        legacyCache[trimmed]?.let { return it } 
        
        return withContext(Dispatchers.IO) { 
            var connection: HttpURLConnection? = null 
            try { 
                val encoded = URLEncoder.encode(trimmed, "UTF-8") 
                val url = URL("https://jisho.org/api/v1/search/words?keyword=$encoded") 
                connection = url.openConnection() as HttpURLConnection 
                connection.requestMethod = "GET" 
                connection.connectTimeout = 8000 
                connection.readTimeout = 8000 
                connection.setRequestProperty("User-Agent", "AnkiLockscreen/1.0") 
                
                val responseCode = connection.responseCode 
                if (responseCode == HttpURLConnection.HTTP_OK) { 
                    val reader = BufferedReader(InputStreamReader(connection.inputStream)) 
                    val response = reader.readText() 
                    reader.close() 
                    
                    val parsed = parseLegacyJson(trimmed, response) 
                    if (parsed != null) { 
                        legacyCache[trimmed] = parsed 
                    } 
                    parsed 
                } else { 
                    null 
                } 
            } catch (e: Exception) { 
                null 
            } finally { 
                connection?.disconnect() 
            } 
        } 
    } 
    
    private fun parseJishoWords(jsonStr: String): List<JishoWord> { 
        return try { 
            val root = JSONObject(jsonStr) 
            val dataArray = root.optJSONArray("data") ?: return emptyList() 
            val list = mutableListOf<JishoWord>() 
            
            for (i in 0 until dataArray.length()) { 
                val item = dataArray.optJSONObject(i) ?: continue 
                val slug = item.optString("slug", "") 
                val isCommon = item.optBoolean("is_common", false) 
                val tags = parseJsonStringList(item.optJSONArray("tags")) 
                val jlpt = parseJsonStringList(item.optJSONArray("jlpt")) 
                
                val jArray = item.optJSONArray("japanese") ?: JSONArray() 
                val japaneseList = mutableListOf<JishoJapanese>() 
                for (j in 0 until jArray.length()) { 
                    val jObj = jArray.optJSONObject(j) ?: continue 
                    val word = if (jObj.has("word") && !jObj.isNull("word")) jObj.optString("word") else null 
                    val reading = if (jObj.has("reading") && !jObj.isNull("reading")) jObj.optString("reading") else null 
                    japaneseList.add(JishoJapanese(word = word, reading = reading)) 
                } 
                
                val sensesArray = item.optJSONArray("senses") ?: JSONArray() 
                val sensesList = mutableListOf<JishoSense>() 
                for (s in 0 until sensesArray.length()) { 
                    val sObj = sensesArray.optJSONObject(s) ?: continue 
                    val defs = parseJsonStringList(sObj.optJSONArray("english_definitions")) 
                    val pos = parseJsonStringList(sObj.optJSONArray("parts_of_speech")) 
                    val senseTags = parseJsonStringList(sObj.optJSONArray("tags")) 
                    sensesList.add( 
                        JishoSense( 
                            englishDefinitions = defs, 
                            partsOfSpeech = pos, 
                            tags = senseTags 
                        ) 
                    ) 
                } 
                
                list.add( 
                    JishoWord( 
                        slug = slug, 
                        isCommon = isCommon, 
                        tags = tags, 
                        jlpt = jlpt, 
                        japanese = japaneseList, 
                        senses = sensesList 
                    ) 
                ) 
            } 
            list 
        } catch (e: Exception) { 
            emptyList() 
        } 
    } 
    
    private fun parseLegacyJson(keyword: String, jsonStr: String): JishoWordResult? { 
        return try { 
            val root = JSONObject(jsonStr) 
            val dataArray = root.optJSONArray("data") ?: return null 
            if (dataArray.length() == 0) return null 
            
            val first = dataArray.getJSONObject(0) 
            val isCommon = first.optBoolean("is_common", false) 
            val jlptArray = first.optJSONArray("jlpt") 
            val jlpt = if (jlptArray != null && jlptArray.length() > 0) { 
                jlptArray.getString(0).replace("jlpt-", "JLPT ").uppercase() 
            } else { 
                null 
            } 
            
            val jArray = first.optJSONArray("japanese") 
            var surface = keyword 
            var reading = "" 
            if (jArray != null && jArray.length() > 0) { 
                val jObj = jArray.getJSONObject(0) 
                surface = jObj.optString("word", keyword).ifBlank { keyword } 
                reading = jObj.optString("reading", "") 
            } 
            
            val sensesArray = first.optJSONArray("senses") 
            val definitions = mutableListOf<String>() 
            val partsOfSpeech = mutableListOf<String>() 
            
            if (sensesArray != null) { 
                for (i in 0 until sensesArray.length()) { 
                    val sObj = sensesArray.getJSONObject(i) 
                    val defArray = sObj.optJSONArray("english_definitions") 
                    if (defArray != null) { 
                        val defs = mutableListOf<String>() 
                        for (j in 0 until defArray.length()) { 
                            defs.add(defArray.getString(j)) 
                        } 
                        if (defs.isNotEmpty()) { 
                            definitions.add(defs.joinToString(", ")) 
                        } 
                    } 
                    val posArray = sObj.optJSONArray("parts_of_speech") 
                    if (posArray != null) { 
                        for (k in 0 until posArray.length()) { 
                            val pos = posArray.getString(k) 
                            if (!partsOfSpeech.contains(pos)) { 
                                partsOfSpeech.add(pos) 
                            } 
                        } 
                    } 
                } 
            } 
            
            JishoWordResult( 
                keyword = keyword, 
                surface = surface, 
                reading = reading, 
                isCommon = isCommon, 
                jlpt = jlpt, 
                partsOfSpeech = partsOfSpeech, 
                definitions = definitions 
            ) 
        } catch (e: Exception) { 
            null 
        } 
    } 
    
    private fun parseJsonStringList(array: JSONArray?): List<String> { 
        if (array == null) return emptyList() 
        val list = mutableListOf<String>() 
        for (i in 0 until array.length()) { 
            val str = array.optString(i, "") 
            if (str.isNotBlank()) list.add(str) 
        } 
        return list 
    } 
} 
