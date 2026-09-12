package com.ankilock.ai
    
import com.ankilock.data.CardInfo
import com.ankilock.data.ForgedStory
import com.ankilock.data.ListeningEvaluationResult
import com.ankilock.data.StoryQuestion
import com.ankilock.data.StorySentenceItem
import com.ankilock.data.StoryWordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
    
object AiServiceHelper { 
    
    @Volatile
    private var lastUsedApiKey: String? = null
    
    @Volatile
    private var workingGeminiModel: String? = null
    
    @Volatile
    private var cachedDiscoveredModels: List<String>? = null
    
    suspend fun testConnection(apiKey: String, provider: String, model: String = "auto"): Result<String> { 
        return withContext(Dispatchers.IO) { 
            try { 
                val prompt = "Respond only with: {\"status\":\"ok\"}"
                val responseJson = executeAiRequest(prompt, apiKey, provider, model)
                val clean = cleanJsonString(responseJson)
                val obj = JSONObject(clean)
                if (obj.optString("status") == "ok" || obj.length() > 0) { 
                    val activeModel = if (provider.lowercase() == "gemini") { 
                        workingGeminiModel ?: model
                    } else { 
                        model
                    }
                    Result.success("Connected ($activeModel)!")
                } else { 
                    Result.failure(Exception("Unexpected response from $provider"))
                }
            } catch (e: Exception) { 
                Result.failure(e)
            }
        }
    }
    
    suspend fun evaluateListening( 
        card: CardInfo, 
        userWordTranslation: String, 
        userSentenceTranslation: String, 
        apiKey: String, 
        provider: String, 
        model: String = "auto"
    ): Result<ListeningEvaluationResult> { 
        return withContext(Dispatchers.IO) { 
            try { 
                val prompt = AiPromptTemplates.buildListeningEvaluationPrompt( 
                    card, 
                    userWordTranslation, 
                    userSentenceTranslation
                )
                val rawResponse = executeAiRequest(prompt, apiKey, provider, model)
                val clean = cleanJsonString(rawResponse)
                val json = JSONObject(clean)
                
                val isWord = json.optBoolean("isWordCorrect", false)
                val isSent = json.optBoolean("isSentenceCorrect", false)
                val isPass = json.optBoolean("isOverallPass", isWord && isSent)
                val feedback = json.optString("feedback", "")
                val wordMeaning = json.optString("correctWordMeaning", card.kanjiMeaning.ifBlank { card.answer })
                val sentMeaning = json.optString("correctSentenceMeaning", card.sentenceMeaning)
                
                Result.success( 
                    ListeningEvaluationResult( 
                        isWordCorrect = isWord, 
                        isSentenceCorrect = isSent, 
                        isOverallPass = isPass, 
                        feedback = feedback, 
                        correctWordMeaning = wordMeaning, 
                        correctSentenceMeaning = sentMeaning
                    )
                )
            } catch (e: Exception) { 
                Result.failure(e)
            }
        }
    }
    
    suspend fun forgeStory( 
        cards: List<CardInfo>, 
        genre: String, 
        level: String, 
        length: String = "Medium", 
        apiKey: String, 
        provider: String, 
        model: String = "auto" 
    ): Result<ForgedStory> { 
        return withContext(Dispatchers.IO) { 
            try { 
                val prompt = AiPromptTemplates.buildStoryForgePrompt(cards, genre, level, length) 
                val rawResponse = executeAiRequest(prompt, apiKey, provider, model) 
                val clean = cleanJsonString(rawResponse) 
                val json = JSONObject(clean)
                
                val title = json.optString("title", "Japanese Story")
                val visualAnchor = json.optString("visualAnchor", "") 
                val storyJp = json.optString("storyJapanese", "")
                val storyEn = json.optString("storyEnglish", "")
                
                val sentencesArray = json.optJSONArray("sentences") ?: JSONArray()
                val sentences = mutableListOf<StorySentenceItem>()
                for (i in 0 until sentencesArray.length()) { 
                    val sObj = sentencesArray.getJSONObject(i)
                    val tWordsArray = sObj.optJSONArray("targetWords") ?: JSONArray()
                    val tWords = mutableListOf<String>()
                    for (k in 0 until tWordsArray.length()) { 
                        tWords.add(tWordsArray.getString(k))
                    }
                    val imgName = sObj.optString("image", "${i + 1}.png").ifBlank { "${i + 1}.png" } 
                    val imgPrompt = sObj.optString("imagePrompt", "") 
                    val furi = sObj.optString("furigana", "") 
                    sentences.add( 
                        StorySentenceItem( 
                            id = sObj.optInt("id", i + 1), 
                            japanese = sObj.optString("japanese", ""), 
                            english = sObj.optString("english", ""), 
                            targetWords = tWords, 
                            image = imgName, 
                            imagePrompt = imgPrompt, 
                            furigana = furi 
                        ) 
                    ) 
                } 
                
                if (sentences.isEmpty() && storyJp.isNotBlank()) { 
                    val chunks = storyJp.split(Regex("(?<=[。！？\n])")).filter { it.isNotBlank() } 
                    chunks.forEachIndexed { idx, chk -> 
                        sentences.add( 
                            StorySentenceItem( 
                                id = idx + 1, 
                                japanese = chk.trim(), 
                                english = "", 
                                image = "${idx + 1}.png" 
                            ) 
                        ) 
                    } 
                }
                
                val wordsArray = json.optJSONArray("targetWords") ?: JSONArray()
                val targetWords = mutableListOf<StoryWordItem>()
                for (i in 0 until wordsArray.length()) { 
                    val wObj = wordsArray.getJSONObject(i)
                    targetWords.add( 
                        StoryWordItem( 
                            kanji = wObj.optString("kanji", ""), 
                            reading = wObj.optString("reading", ""), 
                            meaning = wObj.optString("meaning", "")
                        )
                    )
                }
                
                val qArray = json.optJSONArray("questions") ?: JSONArray()
                val questions = mutableListOf<StoryQuestion>()
                for (i in 0 until qArray.length()) { 
                    val qObj = qArray.getJSONObject(i) 
                    val optsArray = qObj.optJSONArray("options") ?: JSONArray() 
                    val opts = mutableListOf<String>() 
                    for (j in 0 until optsArray.length()) { 
                        opts.add(optsArray.getString(j)) 
                    } 
                    val optsFuriArray = qObj.optJSONArray("optionsFurigana") ?: JSONArray() 
                    val optsFuri = mutableListOf<String>() 
                    for (j in 0 until optsFuriArray.length()) { 
                        optsFuri.add(optsFuriArray.getString(j)) 
                    } 
                    questions.add( 
                        StoryQuestion( 
                            id = qObj.optInt("id", i + 1), 
                            questionText = qObj.optString("questionText", ""), 
                            options = opts, 
                            correctOptionIndex = qObj.optInt("correctOptionIndex", 0), 
                            explanation = qObj.optString("explanation", ""), 
                            questionFurigana = qObj.optString("questionFurigana", ""), 
                            optionsFurigana = optsFuri 
                        ) 
                    ) 
                } 
                
                val artTagsArray = json.optJSONArray("artTags") ?: JSONArray() 
                val artTags = mutableListOf<String>() 
                for (t in 0 until artTagsArray.length()) { 
                    val tagStr = artTagsArray.optString(t, "").trim() 
                    if (tagStr.isNotBlank()) { 
                        artTags.add(tagStr) 
                    } 
                } 
                
                Result.success( 
                    ForgedStory( 
                        title = title, 
                        genre = genre, 
                        level = level, 
                        storyJapanese = storyJp, 
                        storyEnglish = storyEn, 
                        sentences = sentences, 
                        targetWords = targetWords, 
                        questions = questions, 
                        visualAnchor = visualAnchor, 
                        artTags = artTags 
                    ) 
                ) 
            } catch (e: Exception) { 
                Result.failure(e)
            }
        }
    }
    
    private fun executeAiRequest( 
        prompt: String, 
        apiKey: String, 
        provider: String, 
        model: String = "auto"
    ): String { 
        val cleanKey = apiKey.trim()
        if (cleanKey.isBlank()) { 
            throw IllegalArgumentException("API Key is missing. Please enter your API key in Settings.")
        }
        
        if (lastUsedApiKey != cleanKey) { 
            lastUsedApiKey = cleanKey
            workingGeminiModel = null
            cachedDiscoveredModels = null
        }
        
        return when (provider.lowercase()) { 
            "openai" -> { 
                val targetModel = if (model != "auto" && model.isNotBlank()) model else "gpt-4o-mini"
                callOpenAiCompatible( 
                    endpoint = "https://api.openai.com/v1/chat/completions", 
                    model = targetModel, 
                    apiKey = cleanKey, 
                    prompt = prompt
                )
            }
            "groq" -> { 
                val targetModel = if (model != "auto" && model.isNotBlank()) model else "llama-3.1-8b-instant"
                callOpenAiCompatible( 
                    endpoint = "https://api.groq.com/openai/v1/chat/completions", 
                    model = targetModel, 
                    apiKey = cleanKey, 
                    prompt = prompt
                )
            }
            else -> callGemini(cleanKey, prompt, model)
        }
    }
    
    private fun fetchAllAvailableGeminiModels(apiKey: String): List<String> { 
        val cached = cachedDiscoveredModels
        if (cached != null && cached.isNotEmpty()) { 
            return cached
        }
        
        val discovered = mutableListOf<String>()
        try { 
            val listUrl = URL("https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey")
            val conn = listUrl.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            if (conn.responseCode in 200..299) { 
                val text = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val root = JSONObject(text)
                val models = root.optJSONArray("models")
                if (models != null) { 
                    for (i in 0 until models.length()) { 
                        val m = models.getJSONObject(i)
                        val name = m.optString("name", "")
                        val methods = m.optJSONArray("supportedGenerationMethods")
                        var canGenerate = false
                        if (methods != null) { 
                            for (j in 0 until methods.length()) { 
                                if (methods.optString(j) == "generateContent") { 
                                    canGenerate = true
                                    break
                                }
                            }
                        }
                        if (canGenerate && name.isNotBlank()) { 
                            val cleanName = name.removePrefix("models/") 
                            val isBelowThree = cleanName.startsWith("gemini-1.") || cleanName.startsWith("gemini-2.") || cleanName.startsWith("gemini-0.") 
                            if (!isBelowThree && !discovered.contains(cleanName)) { 
                                discovered.add(cleanName) 
                            } 
                        }
                    }
                }
            }
        } catch (e: Exception) { 
        }
        
        val sorted = discovered.sortedWith( 
            compareBy<String> { 
                when { 
                    it.contains("flash", ignoreCase = true) -> 0
                    it.contains("pro", ignoreCase = true) -> 1
                    else -> 2
                }
            }.thenByDescending { it }
        )
        if (sorted.isNotEmpty()) { 
            cachedDiscoveredModels = sorted
        }
        return sorted
    }
    
    private fun callGemini(apiKey: String, prompt: String, requestedModel: String): String { 
        val candidateModels = mutableListOf<String>()
        
        val isBelowThree = requestedModel.startsWith("gemini-1.") || requestedModel.startsWith("gemini-2.") || requestedModel.startsWith("gemini-0.") 
        if (requestedModel != "auto" && requestedModel.isNotBlank() && !isBelowThree) { 
            candidateModels.add(requestedModel.removePrefix("models/")) 
        } else { 
            val cachedWorking = workingGeminiModel 
            if (!cachedWorking.isNullOrBlank() && !cachedWorking.startsWith("gemini-1.") && !cachedWorking.startsWith("gemini-2.")) { 
                candidateModels.add(cachedWorking) 
            } else { 
                candidateModels.add("gemini-3.5-flash") 
                candidateModels.add("gemini-3.5-flash-lite") 
                candidateModels.add("gemini-3.0-flash") 
                val discovered = fetchAllAvailableGeminiModels(apiKey) 
                if (discovered.isNotEmpty()) { 
                    candidateModels.addAll(discovered.take(2)) 
                } 
            } 
        }
        
        var lastException: Exception? = null
        for (m in candidateModels.distinct()) { 
            try { 
                val result = executeGeminiWithModel(m, "v1beta", apiKey, prompt)
                workingGeminiModel = m
                return result
            } catch (e: Exception) { 
                lastException = e
                val msg = e.message.orEmpty()
                if (!msg.contains("404") && !msg.contains("NOT_FOUND")) { 
                    throw e
                }
            }
        }
        throw lastException ?: Exception("Selected Gemini model failed.")
    }
    
    private fun executeGeminiWithModel( 
        model: String, 
        apiVersion: String, 
        apiKey: String, 
        prompt: String
    ): String { 
        val cleanModel = model.removePrefix("models/")
        val urlStr = "https://generativelanguage.googleapis.com/$apiVersion/models/$cleanModel:generateContent?key=$apiKey"
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.connectTimeout = 15000
        conn.readTimeout = 30000
        conn.doOutput = true
        
        val body = JSONObject().apply { 
            val contents = JSONArray().apply { 
                put(JSONObject().apply { 
                    val parts = JSONArray().apply { 
                        put(JSONObject().apply { 
                            put("text", prompt)
                        })
                    }
                    put("parts", parts)
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply { 
                put("responseMimeType", "application/json")
            })
        }
        
        OutputStreamWriter(conn.outputStream).use { writer -> 
            writer.write(body.toString())
            writer.flush()
        }
        
        val code = conn.responseCode
        if (code in 200..299) { 
            val responseText = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
            val root = JSONObject(responseText)
            val candidates = root.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) { 
                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) { 
                    return parts.getJSONObject(0).optString("text", "")
                }
            }
            return responseText
        } else { 
            val rawErr = BufferedReader(InputStreamReader(conn.errorStream ?: conn.inputStream)).use { it.readText() }
            var errMsg = rawErr
            try { 
                val errObj = JSONObject(rawErr).optJSONObject("error")
                if (errObj != null) { 
                    val m = errObj.optString("message", "")
                    val status = errObj.optString("status", "")
                    if (m.isNotBlank()) { 
                        errMsg = if (status.isNotBlank()) "$status: $m" else m
                    }
                }
            } catch (ignored: Exception) { }
            throw Exception("Gemini ($code) [$cleanModel]: $errMsg")
        }
    }
    
    private fun callOpenAiCompatible( 
        endpoint: String, 
        model: String, 
        apiKey: String, 
        prompt: String
    ): String { 
        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.connectTimeout = 15000
        conn.readTimeout = 30000
        conn.doOutput = true
        
        val body = JSONObject().apply { 
            put("model", model)
            val messages = JSONArray().apply { 
                put(JSONObject().apply { 
                    put("role", "user")
                    put("content", prompt)
                })
            }
            put("messages", messages)
            put("response_format", JSONObject().apply { 
                put("type", "json_object")
            })
        }
        
        OutputStreamWriter(conn.outputStream).use { writer -> 
            writer.write(body.toString())
            writer.flush()
        }
        
        val code = conn.responseCode
        if (code in 200..299) { 
            val responseText = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
            val root = JSONObject(responseText)
            val choices = root.optJSONArray("choices")
            if (choices != null && choices.length() > 0) { 
                val message = choices.getJSONObject(0).optJSONObject("message")
                return message?.optString("content", "") ?: ""
            }
            return responseText
        } else { 
            val rawErr = BufferedReader(InputStreamReader(conn.errorStream ?: conn.inputStream)).use { it.readText() }
            var errMsg = rawErr
            try { 
                val errObj = JSONObject(rawErr).optJSONObject("error")
                if (errObj != null) { 
                    val m = errObj.optString("message", "")
                    if (m.isNotBlank()) errMsg = m
                }
            } catch (ignored: Exception) { }
            throw Exception("AI API Error ($code): $errMsg")
        }
    }
    
    private fun cleanJsonString(raw: String): String { 
        var text = raw.trim() 
        if (text.startsWith("```json")) { 
            text = text.substring(7) 
        } else if (text.startsWith("```")) { 
            text = text.substring(3) 
        } 
        if (text.endsWith("```")) { 
            text = text.substring(0, text.length - 3) 
        } 
        return text.trim() 
    } 
    
    suspend fun generateStoryImage( 
        prompt: String, 
        apiKey: String, 
        provider: String, 
        seed: Int = 0 
    ): Result<ByteArray> { 
        return withContext(Dispatchers.IO) { 
            try { 
                when (provider.lowercase()) { 
                    "openai" -> { 
                        val cleanKey = apiKey.trim() 
                        if (cleanKey.isNotBlank()) { 
                            try { 
                                val bytes = callOpenAiDallE(cleanKey, prompt) 
                                return@withContext Result.success(bytes) 
                            } catch (_: Exception) { 
                            } 
                        } 
                    } 
                    "gemini" -> { 
                        val cleanKey = apiKey.trim() 
                        if (cleanKey.isNotBlank()) { 
                            try { 
                                val bytes = callGeminiImagen(cleanKey, prompt) 
                                return@withContext Result.success(bytes) 
                            } catch (_: Exception) { 
                            } 
                        } 
                    } 
                } 
                
                val fallbackBytes = fetchPollinationsImage(prompt, seed) 
                Result.success(fallbackBytes) 
            } catch (e: Exception) { 
                Result.failure(e) 
            } 
        } 
    } 
    
    private fun callOpenAiDallE(apiKey: String, prompt: String): ByteArray { 
        val url = URL("https://api.openai.com/v1/images/generations") 
        val conn = url.openConnection() as HttpURLConnection 
        conn.requestMethod = "POST" 
        conn.setRequestProperty("Content-Type", "application/json") 
        conn.setRequestProperty("Authorization", "Bearer $apiKey") 
        conn.connectTimeout = 20000 
        conn.readTimeout = 40000 
        conn.doOutput = true 
        
        val animePrompt = formatAnimePrompt(prompt) 
        val body = JSONObject().apply { 
            put("model", "dall-e-3") 
            put("prompt", animePrompt) 
            put("n", 1) 
            put("size", "1024x1024") 
            put("response_format", "b64_json") 
        } 
        
        OutputStreamWriter(conn.outputStream).use { writer -> 
            writer.write(body.toString()) 
            writer.flush() 
        } 
        
        val code = conn.responseCode 
        if (code in 200..299) { 
            val responseText = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() } 
            val root = JSONObject(responseText) 
            val dataArray = root.optJSONArray("data") 
            if (dataArray != null && dataArray.length() > 0) { 
                val b64 = dataArray.getJSONObject(0).optString("b64_json", "") 
                if (b64.isNotBlank()) { 
                    return android.util.Base64.decode(b64, android.util.Base64.DEFAULT) 
                } 
                val imgUrlStr = dataArray.getJSONObject(0).optString("url", "") 
                if (imgUrlStr.isNotBlank()) { 
                    val imgUrl = URL(imgUrlStr) 
                    return imgUrl.openStream().use { it.readBytes() } 
                } 
            } 
        } 
        throw Exception("OpenAI Image generation failed ($code)") 
    } 
    
    private fun callGeminiImagen(apiKey: String, prompt: String): ByteArray { 
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/imagen-3.0-generate-002:predict?key=$apiKey") 
        val conn = url.openConnection() as HttpURLConnection 
        conn.requestMethod = "POST" 
        conn.setRequestProperty("Content-Type", "application/json") 
        conn.connectTimeout = 20000 
        conn.readTimeout = 40000 
        conn.doOutput = true 
        
        val animePrompt = formatAnimePrompt(prompt) 
        val body = JSONObject().apply { 
            val instances = JSONArray().apply { 
                put(JSONObject().apply { 
                    put("prompt", animePrompt) 
                }) 
            } 
            put("instances", instances) 
            put("parameters", JSONObject().apply { 
                put("sampleCount", 1) 
                put("aspectRatio", "3:4") 
            }) 
        } 
        
        OutputStreamWriter(conn.outputStream).use { writer -> 
            writer.write(body.toString()) 
            writer.flush() 
        } 
        
        val code = conn.responseCode 
        if (code in 200..299) { 
            val responseText = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() } 
            val root = JSONObject(responseText) 
            val predictions = root.optJSONArray("predictions") 
            if (predictions != null && predictions.length() > 0) { 
                val b64 = predictions.getJSONObject(0).optString("bytesBase64Encoded", "") 
                if (b64.isNotBlank()) { 
                    return android.util.Base64.decode(b64, android.util.Base64.DEFAULT) 
                } 
            } 
        } 
        throw Exception("Gemini Imagen failed ($code)") 
    } 
    
    private fun formatAnimePrompt(prompt: String): String { 
        val p = prompt.trim() 
        return if (p.contains("anime", ignoreCase = true) || p.contains("illustration", ignoreCase = true)) { 
            "$p, masterpiece, no text, no words, no watermark" 
        } else { 
            "2D Japanese anime visual novel illustration, Kyoto Animation style, vibrant cel shaded, clean anime line art: $p, masterpiece, no text, no words, no watermark" 
        } 
    } 
    
    private fun fetchPollinationsImage(prompt: String, customSeed: Int = 0): ByteArray { 
        val animePrompt = formatAnimePrompt(prompt) 
        val encodedPrompt = java.net.URLEncoder.encode(animePrompt, "UTF-8") 
        val seed = if (customSeed > 0) customSeed else (System.currentTimeMillis() % 100000).toInt() 
        val urlStr = "https://image.pollinations.ai/prompt/$encodedPrompt?model=flux-anime&width=768&height=960&nologo=true&seed=$seed" 
        val url = URL(urlStr) 
        val conn = url.openConnection() as HttpURLConnection 
        conn.requestMethod = "GET" 
        conn.connectTimeout = 25000 
        conn.readTimeout = 45000 
        conn.instanceFollowRedirects = true 
        
        val code = conn.responseCode 
        if (code in 200..299) { 
            return conn.inputStream.use { it.readBytes() } 
        } 
        throw Exception("Pollinations image generation failed ($code)") 
    } 
} 
