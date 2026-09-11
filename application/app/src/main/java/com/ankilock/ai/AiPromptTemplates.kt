package com.ankilock.ai
    
import com.ankilock.data.CardInfo
import org.json.JSONArray
import org.json.JSONObject
    
object AiPromptTemplates { 
    
    fun buildListeningEvaluationPrompt( 
        card: CardInfo, 
        userWordTranslation: String, 
        userSentenceTranslation: String
    ): String { 
        val targetWord = card.kanji.ifBlank { card.question }
        val targetWordMeaning = card.kanjiMeaning.ifBlank { card.answer }
        val targetSentence = card.sentence.ifBlank { card.sentenceFurigana }
        val targetSentenceMeaning = card.sentenceMeaning
        
        return """
You are a precise Japanese language evaluator for flashcard learning.
Evaluate the user's English translations from a listening test.

Target Japanese Word: $targetWord
Official Word Meaning: $targetWordMeaning
User's Word Translation: $userWordTranslation

Target Japanese Sentence: $targetSentence
Official Sentence Meaning: $targetSentenceMeaning
User's Sentence Translation: $userSentenceTranslation

Instructions:
1. isWordCorrect: true if user's word translation conveys the accurate meaning (accept close synonyms and appropriate context).
2. isSentenceCorrect: true if user's sentence translation accurately captures the core meaning and tense of the sentence.
3. isOverallPass: true if both are substantially correct (or word is correct and sentence is at least 70% accurate).
4. feedback: 1-2 friendly, precise sentences explaining what was good and any nuance or vocabulary missed.
5. Return ONLY valid JSON with this exact schema (no markdown fences, no extra text):
{
  "isWordCorrect": boolean,
  "isSentenceCorrect": boolean,
  "isOverallPass": boolean,
  "feedback": "string",
  "correctWordMeaning": "$targetWordMeaning",
  "correctSentenceMeaning": "$targetSentenceMeaning"
}
""".trimIndent()
    }
    
    fun buildStoryForgePrompt( 
        cards: List<CardInfo>, 
        genre: String, 
        level: String, 
        length: String = "Medium" 
    ): String { 
        val vocabList = JSONArray() 
        for (c in cards) { 
            val word = c.kanji.ifBlank { c.question } 
            val reading = c.kanjiFurigana.ifBlank { word } 
            val meaning = c.kanjiMeaning.ifBlank { c.answer } 
            if (word.isNotBlank()) { 
                vocabList.put(JSONObject().apply { 
                    put("word", word) 
                    put("reading", reading) 
                    put("meaning", meaning) 
                }) 
            } 
        } 
        val targetPages = getTargetPageCount(level, length) 
        val targetQuestions = getTargetQuestionCount(length) 
        val scalingRules = getLevelScalingInstructions(level, length) 
        
        val vocabSection = if (vocabList.length() > 0) { 
            "Mandatory Target Vocabulary:\n$vocabList\n\n1. You MUST incorporate EVERY SINGLE ONE of the provided target words naturally into the story." 
        } else { 
            "Target Vocabulary:\nFree creative mode (no required anchor cards). Naturally weave 4-6 level-appropriate ($level) Japanese vocabulary words into the $genre storyline.\n\n1. Freely craft an engaging story matching the $genre theme." 
        } 
        
        return """
You are an expert Japanese storyteller and language teacher in the style of Blossom: Read & Learn Japanese. 
Create an engaging, coherent Japanese reading story in the genre of "$genre" for a "$level" Japanese learner. 

Target Level Scaling Rules: 
$scalingRules 

$vocabSection 

Story Construction Rules: 
2. The title MUST be strictly formatted as: "English Title - 日本語タイトル" (Example: "Mari Does Groceries - マリの買い物"). 
3. Character Names: NEVER use the name 'Sora', and do NOT use cliché overused names like Ken, Hana, Taro, Takeshi, Sakura, or Yuki. Invent fresh, diverse Japanese character names fitting the "$genre" theme (e.g. Haruto, Daiki, Takumi, Kouta, Kazuki, Ren, Souta, Hinata, Kazuha, Nanami, Riko, Akari, Mio, Yotsuba, Koharu, Aoi, Kaito, Yuna, Shoma, Mei). Rotate different names across stories. 
4. Visual Anchor: Establish a cohesive visual anchor in "visualAnchor". It must explicitly define the main character's gender (boy, girl, young man, young woman), hair style, hair color, eye color, signature outfit, and art style ("Kyoto Animation 2D anime aesthetic, vibrant cel shading, clean crisp line art, cinematic lighting"). Every page illustration must feature this character. 
5. Split the story into exactly $targetPages page chunks in the "sentences" array. Each element in sentences corresponds to one page. 
   - In "japanese": Provide clean standard Japanese text without ruby brackets or inline annotations (e.g. "友達と公園で会いました。"). This is used for TTS audio. 
   - In "furigana": Provide the exact same sentence with ruby brackets for EVERY kanji or kanji compound without exception (e.g. "友[とも]達[だち]と公[こう]園[えん]で会[あ]いました。"). Every single kanji in the sentence MUST have [reading] brackets. 
6. In each sentence, include an "imagePrompt" field describing the specific scene action, emotion, and environment (e.g. "An anime boy standing in front of a neon-lit ramen stall at dusk, holding a hot bowl with chopsticks, smiling happily, soft bokeh lights in background"). 
7. In "targetWords", provide "kanji" (the word as written in the story), "reading" (strictly pure hiragana reading only, e.g. "ともだち"), and "meaning" (concise English meaning). Ensure verb readings use accurate kun'yomi stem readings (e.g. 会いました is read あいました, NOT かいました). Include all key verbs, nouns, and adjectives from the story in targetWords. 
8. Create exactly $targetQuestions multiple-choice reading comprehension questions in Japanese testing story understanding and vocabulary. Each question must include: 
   - "questionText": standard Japanese question text without ruby brackets (e.g. "主人公はどこで友達と会いましたか？"). 
   - "questionFurigana": the exact same question with ruby brackets for EVERY kanji or kanji compound without exception (e.g. "主[しゅ]人[じん]公[こう]はどこで友[とも]達[だち]と会[あ]いましたか？"). 
   - "options": exactly 4 short answer options in standard Japanese without ruby brackets (e.g. ["公園", "学校", "駅", "図書館"]). 
   - "optionsFurigana": array of the same 4 options with ruby brackets for EVERY kanji without exception (e.g. ["公[こう]園[えん]", "学[がっ]校[こう]", "駅[えき]", "図[と]書[しょ]館[かん]"]). 
   - "correctOptionIndex": 0-indexed integer (0, 1, 2, or 3). 
   - "explanation": concise English explanation. 
9. In "artTags", provide 4 to 8 popular anime art tags suitable for searching Wallhaven anime wallpapers matching the story theme, character, and setting (e.g. ["anime boy", "schoolboy", "school uniform", "classroom", "sunset"]). 
10. Return ONLY valid JSON with this exact schema (no markdown, no code fencing, no extra text): 
{ 
  "title": "English Title - 日本語タイトル", 
  "visualAnchor": "Character: Boy (neat dark brown hair, hazel eyes, school blazer). Art style: Kyoto Animation 2D anime aesthetic, crisp anime line art, vibrant cel shading, cinematic atmospheric lighting, masterpiece.", 
  "artTags": ["anime boy", "schoolboy", "school uniform", "classroom", "sunset"], 
  "storyJapanese": "Full coherent story text in Japanese", 
  "storyEnglish": "Full coherent story text in English", 
  "sentences": [ 
    { 
      "id": 1, 
      "japanese": "友達と公園で会いました。", 
      "furigana": "友[とも]達[だち]と公[こう]園[えん]で会[あ]いました。", 
      "english": "Page 1 English translation.", 
      "image": "1.png", 
      "imagePrompt": "An anime boy walking through a park under a blue sky, smiling happily with hands in pockets.", 
      "targetWords": ["友達", "公園", "会いました"] 
    } 
  ], 
  "targetWords": [ 
    { 
      "kanji": "友達", 
      "reading": "ともだち", 
      "meaning": "friend", 
      "pos": "Noun" 
    }, 
    { 
      "kanji": "公園", 
      "reading": "こうえん", 
      "meaning": "park", 
      "pos": "Noun" 
    }, 
    { 
      "kanji": "会いました", 
      "reading": "あいました", 
      "meaning": "met", 
      "pos": "Verb" 
    } 
  ], 
  "questions": [ 
    { 
      "id": 1, 
      "questionText": "主人公はどこで友達と会いましたか？", 
      "questionFurigana": "主[しゅ]人[じん]公[こう]はどこで友[とも]達[だち]と会[あ]いましたか？", 
      "options": ["公園", "学校", "駅", "図書館"], 
      "optionsFurigana": ["公[こう]園[えん]", "学[がっ]校[こう]", "駅[えき]", "図[と]書[しょ]館[かん]"], 
      "correctOptionIndex": 0, 
      "explanation": "公園で友達と会いました。" 
    } 
  ] 
}
""".trimIndent() 
    } 
} 

fun getTargetPageCount(level: String, length: String = "Medium"): Int { 
    return when (length.lowercase()) { 
        "short" -> 5 
        "long" -> 9 
        else -> 6 
    } 
} 

fun getTargetQuestionCount(length: String = "Medium"): Int { 
    return when (length.lowercase()) { 
        "short" -> 5 
        "long" -> 9 
        else -> 6 
    } 
} 

fun getLevelScalingInstructions(level: String, length: String = "Medium"): String { 
    val clean = level.uppercase() 
    val pageCount = getTargetPageCount(level, length) 
    return when { 
        clean.startsWith("N5") -> "Length: exactly $pageCount pages. Each page contains 1-2 short sentences. Use simple S-V-O, basic particles, and accessible beginner vocabulary." 
        clean.startsWith("N4") -> "Length: exactly $pageCount pages. Each page contains 2-3 sentences. Use compound sentences, conditional forms, and potential verbs." 
        clean.startsWith("N3") -> "Length: exactly $pageCount pages. Each page contains 2-4 sentences. Use relative clauses, passive/causative, and conversational patterns." 
        clean.startsWith("N2") -> "Length: exactly $pageCount pages. Each page contains 3-5 sentences. Use authentic natural Japanese, complex conjunctions, and workplace expressions." 
        clean.startsWith("N1") -> "Length: exactly $pageCount pages. Each page contains 3-5 sentences. Use sophisticated literary depth, abstract concepts, and four-character idioms." 
        else -> "Length: exactly $pageCount pages. Each page contains 2-3 natural sentences." 
    } 
} 
