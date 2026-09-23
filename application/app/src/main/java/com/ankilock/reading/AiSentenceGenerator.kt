package com.ankilock.reading

import kotlinx.coroutines.Dispatchers 
import kotlinx.coroutines.withContext 
import okhttp3.MediaType.Companion.toMediaType 
import okhttp3.OkHttpClient 
import okhttp3.Request 
import okhttp3.RequestBody.Companion.toRequestBody 
import org.json.JSONArray 
import org.json.JSONObject 
import java.io.IOException 
import java.util.concurrent.TimeUnit 

data class AiSentenceResult( 
    val wordFurigana: String, 
    val sentence: String, 
    val sentenceFurigana: String, 
    val sentenceMeaning: String 
) 

object AiSentenceGenerator { 

    private val client = OkHttpClient.Builder() 
        .connectTimeout(15, TimeUnit.SECONDS) 
        .readTimeout(25, TimeUnit.SECONDS) 
        .writeTimeout(15, TimeUnit.SECONDS) 
        .build() 

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType() 

    suspend fun generateSentence( 
        apiKey: String, 
        model: String = "gemini-2.5-flash", 
        word: String, 
        reading: String = "", 
        meaning: String = "", 
        tag: String = "Kaishi 1.5k" 
    ): Result<AiSentenceResult> = withContext(Dispatchers.IO) { 
        val cleanWord = word.trim() 
        if (cleanWord.isBlank()) { 
            return@withContext Result.failure(IllegalArgumentException("Word cannot be blank")) 
        } 
        if (apiKey.isBlank()) { 
            return@withContext Result.failure(IllegalArgumentException("Gemini API key is required")) 
        } 

        val cleanModel = (if (model.isNotBlank()) model else "gemini-2.5-flash").removePrefix("models/") 
        val cleanTag = if (tag.isNotBlank()) tag else "Kaishi 1.5k" 
        val prompt = """For the following Japanese vocabulary word, generate a natural Japanese example sentence relevant to $cleanTag context, along with Anki Kaishi furigana notation and natural English translation.
Word: "$cleanWord"
Reading: "${reading.trim()}"
Meaning: "${meaning.trim()}"

Return ONLY a JSON object with this exact structure:
{
  "wordFurigana": "Word with every single kanji annotated individually with Anki furigana brackets like 自[じ] 治[ち] 体[たい]",
  "sentence": "Japanese sentence with target word surrounded by <b></b> (NO furigana brackets here, clean kanji/kana only)",
  "sentenceFurigana": "Japanese sentence with every individual kanji annotated individually with Anki furigana brackets and spaces like 粗[そ] 大[だい]ゴミ and target word bolded like <b> 自[じ] 治[ち] 体[たい]</b> (CRITICAL: Always enclose the target word and its furigana inside <b>...</b>)",
  "sentenceMeaning": "Accurate natural English translation of the sentence"
}""" 

        val requestJson = JSONObject().apply { 
            val contentsArr = JSONArray().apply { 
                val contentObj = JSONObject().apply { 
                    val partsArr = JSONArray().apply { 
                        put(JSONObject().apply { put("text", prompt) }) 
                    } 
                    put("parts", partsArr) 
                } 
                put(contentObj) 
            } 
            put("contents", contentsArr) 

            val generationConfig = JSONObject().apply { 
                put("temperature", 0.2) 
                put("responseMimeType", "application/json") 
            } 
            put("generationConfig", generationConfig) 
        } 

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$cleanModel:generateContent?key=$apiKey" 
        val requestBody = requestJson.toString().toRequestBody(jsonMediaType) 
        val request = Request.Builder() 
            .url(url) 
            .post(requestBody) 
            .build() 

        try { 
            val response = client.newCall(request).execute() 
            val responseBody = response.body?.string() ?: "" 
            if (!response.isSuccessful) { 
                return@withContext Result.failure(IOException("Gemini API error (${response.code}): $responseBody")) 
            } 

            val root = JSONObject(responseBody) 
            val candidates = root.optJSONArray("candidates") 
            val firstCandidate = candidates?.optJSONObject(0) 
            val parts = firstCandidate?.optJSONObject("content")?.optJSONArray("parts") 
            val text = parts?.optJSONObject(0)?.optString("text", "") ?: "{}" 
            val cleanJson = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim() 
            val resObj = JSONObject(cleanJson) 

            var sent = resObj.optString("sentence", "") 
            if (sent.contains("[")) { 
                sent = sent.replace(Regex("<b>(.*?)</b>")) { m -> 
                    "<b>" + m.groupValues[1].replace(Regex("\\[[^\\]]*\\]"), "") + "</b>" 
                } 
                sent = sent.replace(Regex("\\[[^\\]]*\\]"), "") 
            } 
            if (cleanWord.isNotBlank() && sent.isNotBlank() && !sent.contains("<b>")) { 
                val escaped = Regex.escape(cleanWord) 
                sent = sent.replace(Regex("($escaped)"), "<b>$1</b>") 
            } 

            var sentFuri = resObj.optString("sentenceFurigana", "") 
            if (sentFuri.isBlank()) sentFuri = sent 
            val wordFuri = resObj.optString("wordFurigana", "") 
            val sentMeaning = resObj.optString("sentenceMeaning", "").ifBlank { 
                resObj.optString("sentenceEnglish", "") 
            } 

            Result.success( 
                AiSentenceResult( 
                    wordFurigana = wordFuri, 
                    sentence = sent, 
                    sentenceFurigana = sentFuri, 
                    sentenceMeaning = sentMeaning 
                ) 
            ) 
        } catch (e: Exception) { 
            Result.failure(e) 
        } 
    } 
} 
