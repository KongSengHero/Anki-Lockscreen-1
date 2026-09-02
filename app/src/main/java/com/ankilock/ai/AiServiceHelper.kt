package com.ankilock.ai
    
import com.ankilock.data.CardInfo
import com.ankilock.data.ForgedStory
import com.ankilock.data.ListeningEvaluationResult
import com.ankilock.data.StoryQuestion
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
        apiKey: String, 
        provider: String, 
        model: String = "auto"
    ): Result<ForgedStory> { 
        return withContext(Dispatchers.IO) { 
            try { 
                val prompt = AiPromptTemplates.buildStoryForgePrompt(cards, genre, level)
                val rawResponse = executeAiRequest(prompt, apiKey, provider, model)
                val clean = cleanJsonString(rawResponse)
                val json = JSONObject(clean)
                
                val title = json.optString("title", "Japanese Story")
                val storyJp = json.optString("storyJapanese", "")
                val storyEn = json.optString("storyEnglish", "")
                
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
                    questions.add( 
                        StoryQuestion( 
                            id = qObj.optInt("id", i + 1), 
                            questionText = qObj.optString("questionText", ""), 
                            options = opts, 
                            correctOptionIndex = qObj.optInt("correctOptionIndex", 0), 
                            explanation = qObj.optString("explanation", "")
                        )
                    )
                }
                
                Result.success( 
                    ForgedStory( 
                        title = title, 
                        storyJapanese = storyJp, 
                        storyEnglish = storyEn, 
                        targetWords = targetWords, 
                        questions = questions
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
                            if (!discovered.contains(cleanName)) { 
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
        
        if (requestedModel != "auto" && requestedModel.isNotBlank()) { 
            candidateModels.add(requestedModel.removePrefix("models/"))
        } else { 
            val cachedWorking = workingGeminiModel
            if (!cachedWorking.isNullOrBlank()) { 
                candidateModels.add(cachedWorking)
            } else { 
                val discovered = fetchAllAvailableGeminiModels(apiKey)
                if (discovered.isNotEmpty()) { 
                    candidateModels.addAll(discovered.take(2))
                } else { 
                    candidateModels.add("gemini-2.5-flash")
                    candidateModels.add("gemini-2.0-flash")
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
}
