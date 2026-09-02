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
        level: String
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
        
        return """
You are a creative Japanese story writer and language teacher.
Create an engaging, coherent Japanese reading story in the genre of "$genre" for a "$level" Japanese learner.

Target Vocabulary to naturally weave into the story:
$vocabList

Requirements:
1. Write a natural, compelling story in Japanese (150-300 words).
2. Highlight and embed as many of the provided target vocabulary words as possible naturally.
3. Provide the full natural English translation.
4. Create 3 to 5 multiple-choice reading comprehension questions testing understanding of the story and the target vocabulary words. Each question must have 4 options and the 0-indexed correct option index.
5. Return ONLY valid JSON with this exact schema (no markdown, no other text):
{
  "title": "Story Title in Japanese with English subtitle",
  "storyJapanese": "Full Japanese text with natural punctuation.",
  "storyEnglish": "Full English translation of the story.",
  "targetWords": [
    {
      "kanji": "Target Kanji/Word",
      "reading": "Hiragana reading",
      "meaning": "English meaning"
    }
  ],
  "questions": [
    {
      "id": 1,
      "questionText": "Question testing comprehension or vocabulary context",
      "options": ["Option A", "Option B", "Option C", "Option D"],
      "correctOptionIndex": 0,
      "explanation": "Why this answer is correct in context of the story"
    }
  ]
}
""".trimIndent()
    }
}
