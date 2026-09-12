package com.ankilock.ai
 
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
 
data class WallhavenImageResult( 
    val id: String, 
    val url: String, 
    val path: String, 
    val largeThumb: String, 
    val smallThumb: String, 
    val category: String = "anime", 
    val resolution: String = "", 
    val ratio: String = "" 
) 
 
object WallhavenServiceHelper { 
 
    suspend fun searchImages( 
        query: String, 
        apiKey: String = "", 
        categories: String = "010", 
        purity: String = "100", 
        sorting: String = "relevance", 
        page: Int = 1 
    ): Result<List<WallhavenImageResult>> { 
        return withContext(Dispatchers.IO) { 
            val cleanQuery = query.trim() 
            val encodedQuery = URLEncoder.encode(cleanQuery, "UTF-8") 
            val cleanKey = apiKey.trim() 
            
            val keyParam = if (cleanKey.isNotBlank()) "&apikey=$cleanKey" else "" 
            val endpoint = "https://wallhaven.cc/api/v1/search?q=$encodedQuery&categories=$categories&purity=$purity&sorting=$sorting&order=desc&page=$page$keyParam" 
            
            try { 
                val url = URL(endpoint) 
                val conn = url.openConnection() as HttpURLConnection 
                conn.requestMethod = "GET" 
                conn.setRequestProperty("User-Agent", "AnkiLock-Blossom/1.0") 
                conn.connectTimeout = 15000 
                conn.readTimeout = 20000 
                
                val code = conn.responseCode 
                if (code in 200..299) { 
                    val resp = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() } 
                    val root = JSONObject(resp) 
                    val dataArr = root.optJSONArray("data") 
                    val results = mutableListOf<WallhavenImageResult>() 
                    if (dataArr != null) { 
                        for (i in 0 until dataArr.length()) { 
                            val item = dataArr.getJSONObject(i) 
                            val thumbs = item.optJSONObject("thumbs") 
                            val largeThumb = thumbs?.optString("large", "") ?: "" 
                            val smallThumb = thumbs?.optString("small", "") ?: "" 
                            results.add( 
                                WallhavenImageResult( 
                                    id = item.optString("id", ""), 
                                    url = item.optString("url", ""), 
                                    path = item.optString("path", ""), 
                                    largeThumb = largeThumb, 
                                    smallThumb = smallThumb, 
                                    category = item.optString("category", "anime"), 
                                    resolution = item.optString("resolution", ""), 
                                    ratio = item.optString("ratio", "") 
                                ) 
                            ) 
                        } 
                    } 
                    if (results.isEmpty() && categories == "010") { 
                        return@withContext searchImages(query, apiKey, "110", purity, sorting, page) 
                    } 
                    Result.success(results) 
                } else { 
                    val err = conn.errorStream?.use { BufferedReader(InputStreamReader(it)).readText() } ?: "" 
                    Result.failure(Exception("Wallhaven error ($code): $err")) 
                } 
            } catch (e: Exception) { 
                Result.failure(e) 
            } 
        } 
    } 
 
    suspend fun downloadImageBytes(imageUrl: String): Result<ByteArray> { 
        return withContext(Dispatchers.IO) { 
            try { 
                val url = URL(imageUrl) 
                val conn = url.openConnection() as HttpURLConnection 
                conn.setRequestProperty("User-Agent", "AnkiLock-Blossom/1.0") 
                conn.connectTimeout = 15000 
                conn.readTimeout = 30000 
                conn.instanceFollowRedirects = true 
                val bytes = conn.inputStream.use { it.readBytes() } 
                Result.success(bytes) 
            } catch (e: Exception) { 
                Result.failure(e) 
            } 
        } 
    } 
 
    fun buildSearchTags( 
        title: String, 
        genre: String, 
        visualAnchor: String = "", 
        artTags: List<String> = emptyList() 
    ): List<String> { 
        val tags = mutableListOf<String>() 
        val lowerAnchor = visualAnchor.lowercase() 
        val lowerTitle = title.lowercase() 
        val lowerGenre = genre.lowercase() 
        val combinedText = "$lowerAnchor $lowerTitle $lowerGenre" 
        
        val isFemale = Regex("\\b(girl|female|woman|schoolgirl|daughter|sister|heroine|lady)\\b").containsMatchIn(combinedText) 
        val isMale = Regex("\\b(boy|male|man|schoolboy|son|brother|hero|guy)\\b").containsMatchIn(combinedText) 
        val isSchool = Regex("\\b(school|student|classroom|academy|uniform|club)\\b").containsMatchIn(combinedText) 
        
        when { 
            isSchool && isMale -> { 
                tags.add("schoolboy") 
                tags.add("school uniform") 
                tags.add("anime boy") 
                tags.add("classroom") 
            } 
            isSchool && isFemale -> { 
                tags.add("schoolgirl") 
                tags.add("school uniform") 
                tags.add("anime girl") 
                tags.add("classroom") 
            } 
            isSchool -> { 
                tags.add("school uniform") 
                tags.add("classroom") 
                tags.add("school") 
            } 
            isMale && !isFemale -> { 
                tags.add("anime boy") 
            } 
            isFemale && !isMale -> { 
                tags.add("anime girl") 
            } 
            else -> { 
                tags.add("anime scenery") 
            } 
        } 
        
        when { 
            combinedText.contains("fantasy") || combinedText.contains("isekai") || combinedText.contains("magic") -> { 
                tags.add("fantasy") 
                tags.add("magic") 
            } 
            combinedText.contains("romance") || combinedText.contains("slice of life") -> { 
                tags.add("cherry blossoms") 
                tags.add("sunset") 
            } 
            combinedText.contains("sci-fi") || combinedText.contains("cyberpunk") || combinedText.contains("future") -> { 
                tags.add("cyberpunk") 
                tags.add("city lights") 
            } 
            combinedText.contains("mystery") || combinedText.contains("detective") || combinedText.contains("rain") -> { 
                tags.add("rain") 
                tags.add("night") 
            } 
            combinedText.contains("folklore") || combinedText.contains("yokai") || combinedText.contains("shrine") -> { 
                tags.add("shrine") 
                tags.add("kimono") 
            } 
            combinedText.contains("workplace") || combinedText.contains("office") || combinedText.contains("it") -> { 
                tags.add("office") 
                tags.add("city") 
            } 
            combinedText.contains("culinary") || combinedText.contains("food") || combinedText.contains("cafe") -> { 
                tags.add("cafe") 
            } 
            combinedText.contains("action") || combinedText.contains("martial") || combinedText.contains("samurai") -> { 
                tags.add("katana") 
                tags.add("samurai") 
            } 
        } 
        
        if (lowerAnchor.contains("dark hair") || lowerAnchor.contains("black hair")) { 
            tags.add("dark hair") 
        } else if (lowerAnchor.contains("blonde") || lowerAnchor.contains("blond")) { 
            tags.add("blonde") 
        } else if (lowerAnchor.contains("brown hair")) { 
            tags.add("brown hair") 
        } 
        
        for (tag in artTags) { 
            val clean = tag.trim().lowercase() 
            if (clean.isNotBlank() && !tags.any { it.equals(clean, ignoreCase = true) }) { 
                tags.add(0, clean) 
            } 
        } 
        
        return tags.distinct() 
    } 
 
    fun buildSearchQuery( 
        title: String, 
        genre: String, 
        visualAnchor: String = "", 
        artTags: List<String> = emptyList() 
    ): String { 
        val tags = buildSearchTags(title, genre, visualAnchor, artTags) 
        return if (tags.isNotEmpty()) { 
            tags.take(3).joinToString(" ") 
        } else { 
            "anime wallpaper" 
        } 
    } 
} 
