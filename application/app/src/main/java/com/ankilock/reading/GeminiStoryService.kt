package com.ankilock.reading
    
import com.ankilock.data.AnkiVocabularyItem
import com.ankilock.data.GeneratedStory
import com.ankilock.data.PreferencesManager
import com.ankilock.data.StoryQuizQuestion
import com.ankilock.data.StoryThemes
import com.ankilock.data.StoryWordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiStoryService { 
    
    private val client = OkHttpClient.Builder() 
        .connectTimeout(20, TimeUnit.SECONDS) 
        .readTimeout(40, TimeUnit.SECONDS) 
        .writeTimeout(20, TimeUnit.SECONDS) 
        .build() 
    
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType() 
    
    suspend fun generateStory( 
        apiKey: String, 
        jlptLevel: String, 
        vocabularyList: List<AnkiVocabularyItem> = emptyList(), 
        preferredModel: String = "gemini-2.5-flash", 
        theme: String? = null, 
        topic: String? = null, 
        storyLength: String = "Medium (300 words)", 
        questionsCount: Int = 3 
    ): Result<GeneratedStory> = withContext(Dispatchers.IO) { 
        if (apiKey.isBlank()) { 
            return@withContext Result.failure(IllegalArgumentException("Gemini API key is required")) 
        } 
        
        val selectedWords = if (vocabularyList.isNotEmpty()) { 
            vocabularyList.shuffled().take(12) 
        } else { 
            emptyList() 
        } 
        
        val wordPromptList = if (selectedWords.isNotEmpty()) { 
            selectedWords.joinToString(", ") { item -> 
                val word = item.displayWord 
                val reading = if (item.reading.isNotBlank() && item.reading != word) " (読み: ${item.reading})" else "" 
                val contextMeaning = if (item.meaning.isNotBlank()) " {reference: ${item.meaning.take(35)}}" else "" 
                "$word$reading$contextMeaning" 
            } 
        } else { 
            "" 
        } 
        
        val prompt = buildJlptStoryPrompt(jlptLevel, wordPromptList, theme, topic, storyLength, questionsCount) 
        
        val targetWordItems = selectedWords.map { 
            StoryWordItem( 
                kanji = it.kanji.ifBlank { it.reading }, 
                reading = it.reading, 
                meaning = it.meaning 
            ) 
        } 
        
        try { 
            val story = callGeminiApi( 
                apiKey = apiKey, 
                model = preferredModel, 
                prompt = prompt, 
                jlptLevel = jlptLevel, 
                targetWords = selectedWords.map { it.displayWord }, 
                targetWordsData = targetWordItems, 
                theme = theme, 
                topic = topic 
            ) 
            Result.success(story) 
        } catch (e: Exception) { 
            val errorDetails = e.message ?: "Unknown error" 
            val userMsg = "Model '$preferredModel' failed: $errorDetails\nPlease tap the model selector above to switch to another model (e.g. Gemini 3.5 Flash-Lite or Gemini 3.8 Flash)." 
            Result.failure(IOException(userMsg, e)) 
        } 
    } 
    
    private fun callGeminiApi( 
        apiKey: String, 
        model: String, 
        prompt: String, 
        jlptLevel: String, 
        targetWords: List<String>, 
        targetWordsData: List<StoryWordItem> = emptyList(), 
        theme: String? = null, 
        topic: String? = null 
    ): GeneratedStory { 
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey" 
        
        val requestJson = JSONObject().apply { 
            val contentsArr = JSONArray().apply { 
                val contentObj = JSONObject().apply { 
                    val partsArr = JSONArray().apply { 
                        val partObj = JSONObject().apply { 
                            put("text", prompt) 
                        } 
                        put(partObj) 
                    } 
                    put("parts", partsArr) 
                } 
                put(contentObj) 
            } 
            put("contents", contentsArr) 
            
            val generationConfig = JSONObject().apply { 
                put("temperature", 0.7) 
                put("maxOutputTokens", 4096) 
                put("responseMimeType", "application/json") 
            } 
            put("generationConfig", generationConfig) 
        } 
        
        val requestBody = requestJson.toString().toRequestBody(jsonMediaType) 
        val request = Request.Builder() 
            .url(endpoint) 
            .post(requestBody) 
            .build() 
        
        val response = client.newCall(request).execute() 
        val responseBody = response.body?.string() ?: "" 
        
        if (!response.isSuccessful) { 
            val errorMsg = try { 
                val errorJson = JSONObject(responseBody) 
                errorJson.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}: ${response.message}" 
            } catch (e: Exception) { 
                "HTTP ${response.code}: ${response.message}" 
            } 
            throw IOException(errorMsg) 
        } 
        
        val responseJson = JSONObject(responseBody) 
        val candidates = responseJson.optJSONArray("candidates") 
        if (candidates == null || candidates.length() == 0) { 
            throw IOException("No response generated from Gemini") 
        } 
        
        val firstCandidate = candidates.getJSONObject(0) 
        val contentObj = firstCandidate.optJSONObject("content") 
        val partsArr = contentObj?.optJSONArray("parts") 
        if (partsArr == null || partsArr.length() == 0) { 
            throw IOException("Empty response parts from Gemini") 
        } 
        
        val rawText = partsArr.getJSONObject(0).optString("text", "").trim() 
        if (rawText.isBlank()) { 
            throw IOException("Story text was empty") 
        } 
        
        return parseStoryResponse(rawText, jlptLevel, targetWords, targetWordsData, theme, topic) 
    } 
    
    internal fun parseStoryResponse( 
        rawText: String, 
        jlptLevel: String, 
        targetWords: List<String>, 
        targetWordsData: List<StoryWordItem> = emptyList(), 
        theme: String? = null, 
        topic: String? = null 
    ): GeneratedStory { 
        val clean = cleanJsonString(rawText) 
        return try { 
            val json = JSONObject(clean) 
            val title = json.optString("title", "日本語の物語 ($jlptLevel)").ifBlank { "日本語の物語 ($jlptLevel)" } 
            val storyContent = json.optString("storyJapanese", "").ifBlank { json.optString("content", rawText) } 
            val furiganaContent = json.optString("storyFurigana", "").ifBlank { null } 
            
            val storyWordsArray = json.optJSONArray("storyWords") 
            val storyWordsList = mutableListOf<StoryWordItem>() 
            if (storyWordsArray != null) { 
                for (i in 0 until storyWordsArray.length()) { 
                    val swObj = storyWordsArray.optJSONObject(i) ?: continue 
                    val k = swObj.optString("kanji", "").ifBlank { swObj.optString("surface", "") } 
                    if (k.isNotBlank()) { 
                        storyWordsList.add( 
                            StoryWordItem( 
                                kanji = k, 
                                reading = swObj.optString("reading", "").ifBlank { swObj.optString("furigana", "") }, 
                                meaning = swObj.optString("meaning", "").ifBlank { swObj.optString("english", "") }, 
                                pos = swObj.optString("pos", "") 
                            ) 
                        ) 
                    } 
                } 
            } 
            
            val qArray = json.optJSONArray("questions") 
            val questions = mutableListOf<StoryQuizQuestion>() 
            if (qArray != null) { 
                for (i in 0 until qArray.length()) { 
                    val qObj = qArray.optJSONObject(i) ?: continue 
                    val optsArray = qObj.optJSONArray("options") 
                    val opts = mutableListOf<String>() 
                    if (optsArray != null) { 
                        for (j in 0 until optsArray.length()) { 
                            opts.add(optsArray.getString(j)) 
                        } 
                    } 
                    if (opts.isNotEmpty()) { 
                        questions.add( 
                            StoryQuizQuestion( 
                                id = qObj.optInt("id", i + 1), 
                                questionText = qObj.optString("questionText", ""), 
                                options = opts, 
                                correctOptionIndex = qObj.optInt("correctOptionIndex", 0), 
                                explanation = qObj.optString("explanation", "") 
                            ) 
                        ) 
                    } 
                } 
            } 
            
            GeneratedStory( 
                id = UUID.randomUUID().toString(), 
                title = title, 
                content = storyContent, 
                furiganaContent = furiganaContent, 
                jlptLevel = jlptLevel, 
                createdAt = System.currentTimeMillis(), 
                targetWords = targetWords, 
                targetWordsData = targetWordsData, 
                storyWords = storyWordsList, 
                questions = questions, 
                theme = theme, 
                topic = topic 
            ) 
        } catch (e: Exception) { 
            var title = "日本語の物語 ($jlptLevel)" 
            var body = rawText 
            val lines = rawText.lines() 
            if (lines.isNotEmpty()) { 
                val firstLine = lines.first().trim() 
                if (firstLine.startsWith("タイトル:") || firstLine.startsWith("タイトル：") || firstLine.startsWith("#")) { 
                    title = firstLine.removePrefix("タイトル:").removePrefix("タイトル：").removePrefix("#").trim() 
                    body = lines.drop(1).joinToString("\n").trim() 
                } 
            } 
            GeneratedStory( 
                id = UUID.randomUUID().toString(), 
                title = title, 
                content = body, 
                jlptLevel = jlptLevel, 
                createdAt = System.currentTimeMillis(), 
                targetWords = targetWords, 
                targetWordsData = targetWordsData, 
                storyWords = emptyList(), 
                questions = emptyList(), 
                theme = theme, 
                topic = topic 
            ) 
        } 
    } 
    
    private fun cleanJsonString(raw: String): String { 
        var clean = raw.trim() 
        if (clean.startsWith("```json", ignoreCase = true)) { 
            clean = clean.substring(7) 
        } else if (clean.startsWith("```")) { 
            clean = clean.substring(3) 
        } 
        if (clean.endsWith("```")) { 
            clean = clean.substring(0, clean.length - 3) 
        } 
        return clean.trim() 
    } 
    
    internal fun buildJlptStoryPrompt( 
        jlptLevel: String, 
        wordPromptList: String, 
        theme: String? = null, 
        topic: String? = null, 
        storyLength: String = "Medium", 
        questionsCount: Int = 3 
    ): String { 
        val targetQuestions = questionsCount.coerceIn(3, 5) 
        val levelRules = when (jlptLevel) { 
            "N5" -> "Strictly JLPT N5: Use elementary ~です/~ます forms, simple sentence conjunctions (そして, でも, だから), and basic particles (は, が, を, に, で, へ, と, も). Keep sentences short, relatable, and clear." 
            "N4" -> "Strictly JLPT N4: Use compound sentences, ~て-forms, conditions (~たら, ~なら), comparisons, and basic potential or volitional forms." 
            "N3" -> "Strictly JLPT N3: Use natural everyday storytelling with transitional grammar (~ように, ~わけ, ~について, ~に対して) and intermediate sentence flow." 
            "N2" -> "Strictly JLPT N2: Use nuanced narrative prose, varied sentence structures, idiomatic expressions, and pre-advanced Japanese vocabulary." 
            "N1" -> "Strictly JLPT N1: Use literary, sophisticated Japanese expression, subtle nuances, and rich descriptive vocabulary." 
            else -> "Use natural Japanese suitable for JLPT $jlptLevel." 
        } 
        
        val themeSection = if (!theme.isNullOrBlank() && !topic.isNullOrBlank()) { 
            val formattedTheme = StoryThemes.formatThemeName(theme) 
            """
            [Story Theme & Narrative Premise]
            Theme: $formattedTheme
            Topic / Premise: $topic
            Actively center the story's narrative, characters, emotional tone, and premise around this specific theme and topic.
            """.trimIndent() 
        } else if (!theme.isNullOrBlank()) { 
            val formattedTheme = StoryThemes.formatThemeName(theme) 
            """
            [Story Theme & Narrative Premise]
            Theme: $formattedTheme
            Actively center the story's narrative, characters, and emotional tone around this specific theme.
            """.trimIndent() 
        } else { 
            "" 
        } 
        
        val vocabSection = if (wordPromptList.isNotBlank()) { 
            """
            [Target Vocabulary from Learner's Flashcards to Naturally Integrate]
            The learner has studied the following target words from their Anki deck. Actively prioritize weaving these specific words into the story so the reader encounters their learned vocabulary in real context:
            $wordPromptList

            CRITICAL VOCABULARY USAGE RULES:
            - Incorporate these words strictly as natural Japanese text in the story.
            - NEVER insert English translations, English brackets, or English words next to or instead of the target Japanese word (e.g. NEVER write "公園 [park]" or "友達 (friend)" or English words). The story must be 100% Japanese.
            """.trimIndent() 
        } else { 
            """
            [Target Vocabulary]
            No specific flashcard vocabulary constraints. Freely choose natural and creative Japanese vocabulary strictly appropriate for JLPT $jlptLevel learners. You have complete creative freedom. The story must be 100% Japanese.
            """.trimIndent() 
        } 
        
        val quizSection = """
            [JLPT Reading Comprehension (読解) Quiz Guidelines]
            Design exactly $targetQuestions authentic JLPT-style multiple-choice reading comprehension questions calibrated strictly to JLPT $jlptLevel.
            The questions must be intellectually engaging and test true comprehension—NEVER make basic questions that can be answered at a single glance.

            1. JLPT Question Types & Traps:
               - Overlooked / Forgotten Early Details: Craft questions about crucial premises, reasons, settings, or character intentions established in the first or second paragraph. Readers frequently get absorbed in later plot developments and forget or overlook these earlier details.
               - True Motivation vs. False Clues (なぜ/どうして): Ask why a character acted, felt, or chose something. The genuine reason was established earlier, while subsequent events or superficial excuses act as tempting distractor traps.
               - Timeline & Sequence (前後の関係): Ask what occurred before/after or what triggered a change in events, testing whether the reader maintained clear chronological awareness.
               - Content Verification (本文の内容と合っているもの): Craft questions asking which statement accurately reflects the passage.

            2. Distractor (Trap Choice) Engineering (MANDATORY):
               - ZERO Obvious / Out-of-Context Choices: Every single wrong option MUST reference real characters, events, locations, or dialogue that ACTUALLY appeared in the story. NEVER include absurd, silly, or obviously fake options.
               - Recency Bias / Timeline Trap: An incorrect option accurately describes something that happened, but at the wrong point in the timeline (e.g., in the ending rather than the initial cause).
               - Subject / Object / Role Reversal: An option uses true actions from the text, but attributes them to the wrong character or inverts who did what to whom.
               - Partial Truth / Plausible Misunderstanding: An option sounds very plausible and contains real phrases from the story, but twists a crucial condition, relationship, or conclusion.
               - Uniform Length & Structure: All 4 options must be written in natural Japanese at the JLPT $jlptLevel level, with similar sentence length and grammar style so the correct answer cannot be guessed by appearance.

            3. Language & Formatting:
               - "questionText" and all 4 "options" must be in natural Japanese calibrated to JLPT $jlptLevel.
               - "explanation": Provide a concise explanation in English (or clear Japanese) explaining why the correct choice is right (referencing where in the story the answer is found) and pointing out why the misleading options are traps.
        """.trimIndent() 
        
        val lengthRange = when (storyLength.lowercase()) { 
            "short" -> "around 100-150 words" 
            "long" -> "around 400-500 words" 
            else -> "around 200-300 words" 
        } 
        
        val req1 = if (!theme.isNullOrBlank() && !topic.isNullOrBlank()) { 
            "1. Write a natural, compelling story in Japanese ($lengthRange) centered on the given theme and topic premise with logical cause-and-effect and believable character actions." 
        } else { 
            "1. Write a natural, compelling story in Japanese ($lengthRange) with logical cause-and-effect and believable character actions." 
        } 
        
        val req2 = if (wordPromptList.isNotBlank()) { 
            "2. Actively prioritize and weave target vocabulary words from the learner's list above into the story wherever natural and appropriate." 
        } else { 
            "2. Use natural, creative Japanese vocabulary appropriate for JLPT $jlptLevel learners without restriction." 
        } 
        
        val req4 = if (wordPromptList.isNotBlank()) { 
            "4. Create exactly $targetQuestions multiple-choice reading comprehension questions testing understanding of the story and key vocabulary from the target list in context, strictly adhering to the [JLPT Reading Comprehension (読解) Quiz Guidelines] above.\n   Each question must have exactly 4 options in Japanese and the 0-indexed correctOptionIndex." 
        } else { 
            "4. Create exactly $targetQuestions multiple-choice reading comprehension questions testing story comprehension and vocabulary in context, strictly adhering to the [JLPT Reading Comprehension (読解) Quiz Guidelines] above.\n   Each question must have exactly 4 options in Japanese and the 0-indexed correctOptionIndex." 
        } 
        
        val middleSections = listOfNotNull( 
            "[JLPT Level Calibration]\n$levelRules", 
            themeSection.takeIf { it.isNotBlank() }, 
            vocabSection, 
            quizSection 
        ).joinToString("\n\n") 
        
        return """
            You are a master Japanese teacher and graded-reader author specializing in immersive language learning.
            Write an engaging, coherent Japanese reading passage calibrated strictly to JLPT $jlptLevel.

            $middleSections

            [Requirements]
            $req1
            $req2
            3. Standard Japanese Characters & Furigana:
               - "storyJapanese": Clean standard Japanese text with natural paragraph breaks, WITHOUT romaji and WITHOUT ruby brackets. This is used for audio narration.
               - "storyFurigana": The exact same story text with ruby reading brackets for EVERY kanji or kanji compound without exception (e.g. "友[とも]達[だち]と公[こう]園[えん]で会[あ]いました。").
                 * Full Kanji Stem Readings: The ruby bracket must contain the COMPLETE reading of the kanji before okurigana. NEVER truncate stem readings into single syllables:
                   Correct: 疲[つか]れる / 疲[つか]れ (NEVER write 疲[つ]れ), 忙[いそが]しい (NEVER 忙[い]しい), 楽[たの]しい (NEVER 楽[た]しい), 暖[あたた]かい (NEVER 暖[あ]かい), 驚[おどろ]く (NEVER 驚[お]く), 届[とど]く (NEVER 届[と]く), 静[しず]か (NEVER 静[し]か), 幸[しあわ]せ (NEVER 幸[し]せ).
               - ZERO English in story text: The story in both "storyJapanese" and "storyFurigana" must be 100% Japanese. Never insert English definitions, English annotations, or English brackets into the story.
            $req4
            5. "storyWords": Extract 6 to 10 notable, authentic Japanese vocabulary words that actively appear in the story text (excluding basic grammatical particles and common pronouns). Return them with their kanji/kana, hiragana reading, and English meaning.
            6. Return ONLY valid JSON with this exact schema (no markdown formatting, no code blocks):
            {
              "title": "Story Title in Japanese",
              "storyJapanese": "Full Japanese story text with natural paragraph breaks without ruby brackets.",
              "storyFurigana": "Exact same story with ruby brackets for every kanji, e.g. 友[とも]達[だち]と公[こう]園[えん]で会[あ]いました。",
              "storyWords": [
                {
                  "kanji": "Word in kanji/kana as written in story",
                  "reading": "Hiragana reading",
                  "meaning": "English meaning"
                }
              ],
              "questions": [
                {
                  "id": 1,
                  "questionText": "Question in Japanese (e.g., 本文の内容と合っているものはどれか)",
                  "options": ["選択肢1", "選択肢2", "選択肢3", "選択肢4"],
                  "correctOptionIndex": 0,
                  "explanation": "Explanation citing story context and clarifying why distractors are traps"
                }
              ]
            }
        """.trimIndent() 
    } 
} 
