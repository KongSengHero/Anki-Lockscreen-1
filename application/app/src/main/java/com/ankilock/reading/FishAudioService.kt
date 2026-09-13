package com.ankilock.reading
    
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer 
import android.os.Handler 
import android.os.Looper 
import com.ankilock.data.FishAudioVoiceOption 
import com.ankilock.data.PreferencesManager 
import kotlinx.coroutines.Dispatchers 
import kotlinx.coroutines.withContext 
import okhttp3.MediaType.Companion.toMediaType 
import okhttp3.OkHttpClient 
import okhttp3.Request 
import okhttp3.RequestBody.Companion.toRequestBody 
import org.json.JSONObject 
import java.io.File 
import java.io.FileOutputStream 
import java.io.IOException 
import java.net.URLEncoder 
import java.util.concurrent.TimeUnit 

class FishAudioService(private val context: Context) { 
    
    private val client = OkHttpClient.Builder() 
        .connectTimeout(20, TimeUnit.SECONDS) 
        .readTimeout(60, TimeUnit.SECONDS) 
        .writeTimeout(30, TimeUnit.SECONDS) 
        .build() 
    
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType() 
    
    private var mediaPlayer: MediaPlayer? = null 
    private var onPlaybackStateCallback: ((Boolean) -> Unit)? = null 
    private var onCompletionCallback: (() -> Unit)? = null 
    private val mainHandler = Handler(Looper.getMainLooper()) 
    
    suspend fun fetchVoiceName(apiKey: String, voiceId: String): Result<String> = withContext(Dispatchers.IO) { 
        if (apiKey.isBlank()) { 
            return@withContext Result.failure(IllegalArgumentException("Fish Audio API key is required")) 
        } 
        val cleanVoiceId = PreferencesManager.extractVoiceId(voiceId) 
        if (cleanVoiceId.isBlank()) { 
            return@withContext Result.failure(IllegalArgumentException("Voice ID is required")) 
        } 
        
        val request = Request.Builder() 
            .url("https://api.fish.audio/model/$cleanVoiceId") 
            .header("Authorization", "Bearer ${apiKey.trim()}") 
            .get() 
            .build() 
        
        try { 
            client.newCall(request).execute().use { response -> 
                val bodyStr = response.body?.string() ?: "" 
                if (!response.isSuccessful) { 
                    val errorMsg = parseErrorMessage(bodyStr, response.code) 
                    return@withContext Result.failure(IOException(errorMsg)) 
                } 
                
                val json = JSONObject(bodyStr) 
                val title = json.optString("title", "").ifBlank { 
                    json.optString("name", "Custom Voice") 
                } 
                val author = json.optJSONObject("author")?.optString("nickname", "") ?: "" 
                val displayName = if (author.isNotBlank()) "$title (by $author)" else title 
                Result.success(displayName.ifBlank { "Voice Model ($cleanVoiceId)" }) 
            } 
        } catch (e: Exception) { 
            Result.failure(e) 
        } 
    } 
    
    private val auditionPhrases = listOf( 
        "こんにちは、日本語の練習をしましょう。", 
        "今日はどんな物語を読みますか？楽しみにしています。", 
        "昔々、ある所におじいさんとおばあさんが住んでいました。", 
        "ゆっくり、自分のペースで勉強していきましょうね。", 
        "今日も一日、お疲れ様でした。明日も頑張りましょう！", 
        "風が心地よく吹いて、とても穏やかな午後ですね。", 
        "本を開くと、新しい世界が広がっていきます。" 
    ) 
    
    suspend fun searchVoices( 
        apiKey: String = "", 
        query: String = "", 
        tag: String? = null, 
        sortBy: String = "score", 
        pageSize: Int = 20 
    ): Result<List<FishAudioVoiceOption>> = withContext(Dispatchers.IO) { 
        val params = mutableListOf<String>() 
        if (query.isNotBlank()) { 
            params.add("title=${URLEncoder.encode(query.trim(), "UTF-8")}") 
        } 
        if (!tag.isNullOrBlank() && !tag.equals("all", ignoreCase = true) && !tag.equals("favorites", ignoreCase = true)) { 
            params.add("tag=${URLEncoder.encode(tag.trim().lowercase(), "UTF-8")}") 
        } 
        params.add("sort_by=$sortBy") 
        params.add("page_size=$pageSize") 
        val url = "https://api.fish.audio/model?" + params.joinToString("&") 
        
        val reqBuilder = Request.Builder().url(url).get() 
        if (apiKey.isNotBlank()) { 
            reqBuilder.header("Authorization", "Bearer ${apiKey.trim()}") 
        } 
        val request = reqBuilder.build() 
        
        try { 
            client.newCall(request).execute().use { response -> 
                val bodyStr = response.body?.string() ?: "" 
                if (!response.isSuccessful) { 
                    val errorMsg = parseErrorMessage(bodyStr, response.code) 
                    return@withContext Result.failure(IOException(errorMsg)) 
                } 
                
                val list = mutableListOf<FishAudioVoiceOption>() 
                val itemsArray = if (bodyStr.trim().startsWith("[")) { 
                    org.json.JSONArray(bodyStr) 
                } else { 
                    val json = JSONObject(bodyStr) 
                    json.optJSONArray("items") ?: org.json.JSONArray() 
                } 
                
                for (i in 0 until itemsArray.length()) { 
                    val item = itemsArray.optJSONObject(i) ?: continue 
                    val id = item.optString("_id", "").ifBlank { item.optString("id", "") } 
                    if (id.isBlank()) continue 
                    val title = item.optString("title", "").ifBlank { item.optString("name", "Voice Model") } 
                    val authorObj = item.optJSONObject("author") 
                    val author = authorObj?.optString("nickname", "") ?: "" 
                    val authorAvatar = authorObj?.optString("avatar", "") ?: "" 
                    val desc = item.optString("description", "") 
                    val coverImage = item.optString("cover_image", "") 
                    val rawAvatar = coverImage.ifBlank { authorAvatar } 
                    val avatarUrl = if (rawAvatar.isNotBlank()) { 
                        if (rawAvatar.startsWith("http://") || rawAvatar.startsWith("https://")) { 
                            rawAvatar 
                        } else { 
                            "https://public-platform.r2.fish.audio/cdn-cgi/image/width=96,format=webp/${rawAvatar.removePrefix("/")}" 
                        } 
                    } else "" 
                    val likeCount = item.optInt("like_count", 0) 
                    val taskCount = item.optInt("task_count", 0) 
                    val tagsJson = item.optJSONArray("tags") 
                    val tagsList = mutableListOf<String>() 
                    if (tagsJson != null) { 
                        for (t in 0 until tagsJson.length()) { 
                            val tagStr = tagsJson.optString(t, "") 
                            if (tagStr.isNotBlank()) tagsList.add(tagStr) 
                        } 
                    } 
                    val samplesArray = item.optJSONArray("samples") 
                    val sampleAudioUrl = samplesArray?.optJSONObject(0)?.optString("audio", "") ?: "" 
                    val primaryTag = tagsList.firstOrNull { it.isNotBlank() } ?: "Community" 
                    list.add( 
                        FishAudioVoiceOption( 
                            id = id, 
                            name = title, 
                            author = author, 
                            description = desc, 
                            tag = primaryTag, 
                            avatarUrl = avatarUrl, 
                            likeCount = likeCount, 
                            taskCount = taskCount, 
                            tags = tagsList, 
                            sampleAudioUrl = sampleAudioUrl 
                        ) 
                    ) 
                } 
                Result.success(list) 
            } 
        } catch (e: Exception) { 
            Result.failure(e) 
        } 
    } 
    
    suspend fun synthesizePreviewSample( 
        apiKey: String, 
        voiceId: String, 
        model: String = PreferencesManager.DEFAULT_FISH_AUDIO_MODEL 
    ): Result<File> = withContext(Dispatchers.IO) { 
        val phrase = auditionPhrases.random() 
        val phraseKey = Math.abs(phrase.hashCode() % 1000) 
        synthesizeStoryAudio( 
            apiKey = apiKey, 
            voiceId = voiceId, 
            model = model, 
            storyId = "preview_${PreferencesManager.extractVoiceId(voiceId)}_$phraseKey", 
            text = phrase 
        ) 
    } 
    
    suspend fun synthesizeStoryAudio( 
        apiKey: String, 
        voiceId: String, 
        model: String = PreferencesManager.DEFAULT_FISH_AUDIO_MODEL, 
        storyId: String, 
        text: String 
    ): Result<File> = withContext(Dispatchers.IO) { 
        if (apiKey.isBlank()) { 
            return@withContext Result.failure(IllegalArgumentException("Fish Audio API key is required")) 
        } 
        val cleanVoiceId = PreferencesManager.extractVoiceId(voiceId) 
        if (cleanVoiceId.isBlank()) { 
            return@withContext Result.failure(IllegalArgumentException("Voice ID is required")) 
        } 
        if (text.isBlank()) { 
            return@withContext Result.failure(IllegalArgumentException("Text cannot be empty")) 
        } 
        
        val audioFile = getAudioFile(context, storyId, cleanVoiceId) 
        if (audioFile.exists() && audioFile.length() > 0) { 
            return@withContext Result.success(audioFile) 
        } 
        
        val cleanModel = model.ifBlank { PreferencesManager.DEFAULT_FISH_AUDIO_MODEL } 
        
        val requestJson = JSONObject().apply { 
            put("text", text) 
            put("reference_id", cleanVoiceId) 
            put("format", "mp3") 
        } 
        
        val requestBody = requestJson.toString().toRequestBody(jsonMediaType) 
        val request = Request.Builder() 
            .url("https://api.fish.audio/v1/tts") 
            .header("Authorization", "Bearer ${apiKey.trim()}") 
            .header("model", cleanModel) 
            .header("Accept", "audio/mpeg") 
            .post(requestBody) 
            .build() 
        
        try { 
            client.newCall(request).execute().use { response -> 
                if (!response.isSuccessful) { 
                    val errorBody = response.body?.string() ?: "" 
                    val errorMsg = parseErrorMessage(errorBody, response.code) 
                    return@withContext Result.failure(IOException(errorMsg)) 
                } 
                
                val body = response.body ?: return@withContext Result.failure(IOException("Empty response body from Fish Audio")) 
                val dir = getAudioDir(context) 
                if (!dir.exists()) { 
                    dir.mkdirs() 
                } 
                
                val tempFile = File(dir, "temp_${storyId}_${System.currentTimeMillis()}.mp3") 
                FileOutputStream(tempFile).use { fos -> 
                    body.byteStream().use { input -> 
                        input.copyTo(fos) 
                    } 
                } 
                
                if (tempFile.exists() && tempFile.length() > 0) { 
                    if (audioFile.exists()) { 
                        audioFile.delete() 
                    } 
                    if (tempFile.renameTo(audioFile)) { 
                        Result.success(audioFile) 
                    } else { 
                        tempFile.copyTo(audioFile, overwrite = true) 
                        tempFile.delete() 
                        Result.success(audioFile) 
                    } 
                } else { 
                    tempFile.delete() 
                    Result.failure(IOException("Failed to write audio file")) 
                } 
            } 
        } catch (e: Exception) { 
            Result.failure(e) 
        } 
    } 
    
    fun playAudio( 
        file: File, 
        speed: Float = 1.0f, 
        onPlaybackStateChanged: (isPlaying: Boolean) -> Unit, 
        onCompletion: () -> Unit 
    ) { 
        stopAudio() 
        
        try { 
            onPlaybackStateCallback = onPlaybackStateChanged 
            onCompletionCallback = onCompletion 
            
            val player = MediaPlayer().apply { 
                setAudioAttributes( 
                    AudioAttributes.Builder() 
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH) 
                        .setUsage(AudioAttributes.USAGE_MEDIA) 
                        .build() 
                ) 
                setDataSource(file.absolutePath) 
                setOnPreparedListener { mp -> 
                    try { 
                        if (speed != 1.0f) { 
                            mp.playbackParams = mp.playbackParams.setSpeed(speed) 
                        } 
                    } catch (_: Exception) { 
                    } 
                    mp.start() 
                    mainHandler.post { 
                        onPlaybackStateCallback?.invoke(true) 
                    } 
                } 
                setOnCompletionListener { 
                    val comp = onCompletionCallback 
                    stopAudio() 
                    mainHandler.post { 
                        comp?.invoke() 
                    } 
                } 
                setOnErrorListener { _, _, _ -> 
                    val comp = onCompletionCallback 
                    stopAudio() 
                    mainHandler.post { 
                        comp?.invoke() 
                    } 
                    true 
                } 
                prepareAsync() 
            } 
            mediaPlayer = player 
        } catch (e: Exception) { 
            e.printStackTrace() 
            stopAudio() 
        } 
    } 
    
    fun setPlaybackSpeed(speed: Float) { 
        try { 
            mediaPlayer?.let { mp -> 
                if (mp.isPlaying) { 
                    mp.playbackParams = mp.playbackParams.setSpeed(speed) 
                } 
            } 
        } catch (_: Exception) { 
        } 
    } 
    
    fun stopAudio() { 
        val cb = onPlaybackStateCallback 
        try { 
            mediaPlayer?.let { mp -> 
                if (mp.isPlaying) { 
                    mp.stop() 
                } 
                mp.reset() 
                mp.release() 
            } 
        } catch (ignored: Exception) { 
        } finally { 
            mediaPlayer = null 
            onPlaybackStateCallback = null 
            onCompletionCallback = null 
            mainHandler.post { 
                cb?.invoke(false) 
            } 
        } 
    } 
    
    fun pauseAudio() { 
        try { 
            mediaPlayer?.let { mp -> 
                if (mp.isPlaying) { 
                    mp.pause() 
                    mainHandler.post { 
                        onPlaybackStateCallback?.invoke(false) 
                    } 
                } 
            } 
        } catch (_: Exception) { 
        } 
    } 
    
    fun resumeAudio() { 
        try { 
            mediaPlayer?.let { mp -> 
                if (!mp.isPlaying) { 
                    mp.start() 
                    mainHandler.post { 
                        onPlaybackStateCallback?.invoke(true) 
                    } 
                } 
            } 
        } catch (_: Exception) { 
        } 
    } 
    
    fun seekTo(positionMs: Int) { 
        try { 
            mediaPlayer?.seekTo(positionMs) 
        } catch (_: Exception) { 
        } 
    } 
    
    fun getCurrentPositionMs(): Int { 
        return try { 
            mediaPlayer?.currentPosition ?: 0 
        } catch (_: Exception) { 
            0 
        } 
    } 
    
    fun getDurationMs(): Int { 
        return try { 
            mediaPlayer?.duration ?: 0 
        } catch (_: Exception) { 
            0 
        } 
    } 
    
    fun isPlaying(): Boolean { 
        return try { 
            mediaPlayer?.isPlaying == true 
        } catch (ignored: Exception) { 
            false 
        } 
    } 
    
    private fun parseErrorMessage(body: String, code: Int): String { 
        return try { 
            val json = JSONObject(body) 
            val message = json.optString("message").ifBlank { 
                json.optJSONObject("detail")?.optString("message") ?: json.optString("detail") 
            } 
            if (message.isNotBlank()) { 
                message 
            } else { 
                "Fish Audio API Error (HTTP $code)" 
            } 
        } catch (ignored: Exception) { 
            if (body.isNotBlank()) body.take(150) else "Fish Audio request failed (HTTP $code)" 
        } 
    } 
    
    companion object { 
        fun getAudioDir(context: Context): File { 
            return File(context.cacheDir, "story_audio") 
        } 
        
        fun getAudioFile(context: Context, storyId: String, voiceId: String? = null): File { 
            val safeId = storyId.replace(Regex("[^a-zA-Z0-9_-]"), "_") 
            val cleanVoice = if (!voiceId.isNullOrBlank()) { 
                "_" + PreferencesManager.extractVoiceId(voiceId).take(12) 
            } else "" 
            return File(getAudioDir(context), "story_${safeId}${cleanVoice}.mp3") 
        } 
        
        fun deleteAudioForStory(context: Context, storyId: String): Boolean { 
            val safeId = storyId.replace(Regex("[^a-zA-Z0-9_-]"), "_") 
            val prefix = "story_${safeId}" 
            val dir = getAudioDir(context) 
            var deletedAny = false 
            if (dir.exists() && dir.isDirectory) { 
                dir.listFiles()?.forEach { file -> 
                    if (file.name.startsWith(prefix) && file.name.endsWith(".mp3")) { 
                        if (file.delete()) deletedAny = true 
                    } 
                } 
            } 
            return deletedAny 
        } 
        
        fun clearAllAudio(context: Context): Boolean { 
            val dir = getAudioDir(context) 
            return if (dir.exists() && dir.isDirectory) { 
                dir.listFiles()?.forEach { it.delete() } 
                true 
            } else { 
                false 
            } 
        } 
    } 
} 
