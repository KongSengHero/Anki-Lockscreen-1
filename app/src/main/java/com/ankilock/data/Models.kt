package com.ankilock.data
    
data class DeckInfo( 
    val id: Long, 
    val name: String, 
    val newCount: Int, 
    val learnCount: Int, 
    val reviewCount: Int
) { 
    val totalDue: Int get() = newCount + learnCount + reviewCount
}
    
data class CardInfo( 
    val noteId: Long, 
    val cardOrd: Int, 
    val question: String, 
    val answer: String, 
    val deckName: String, 
    val buttonCount: Int = 4, 
    val nextReviewTimes: String = "", 
    val kanji: String = "", 
    val kanjiFurigana: String = "", 
    val kanjiMeaning: String = "", 
    val sentence: String = "", 
    val sentenceFurigana: String = "", 
    val sentenceMeaning: String = "", 
    val imageFileName: String = "", 
    val cardType: Int = 0, 
    val wordAudio: String = "", 
    val sentenceAudio: String = "", 
    val deckId: Long = -1L 
)
    
data class ListeningEvaluationResult( 
    val isWordCorrect: Boolean, 
    val isSentenceCorrect: Boolean, 
    val isOverallPass: Boolean, 
    val feedback: String, 
    val correctWordMeaning: String, 
    val correctSentenceMeaning: String
)
    
data class StoryWordItem( 
    val kanji: String, 
    val reading: String, 
    val meaning: String, 
    val pos: String = "", 
    val kanjiBreakdown: String = "" 
) { 
    val surface: String get() = kanji 
    val furigana: String get() = reading 
    val english: String get() = meaning 
}
    
data class StoryQuestion( 
    val id: Int, 
    val questionText: String, 
    val options: List<String>, 
    val correctOptionIndex: Int, 
    val explanation: String, 
    val questionFurigana: String = "", 
    val optionsFurigana: List<String> = emptyList() 
) 
    
data class StorySentenceItem( 
    val id: Int, 
    val japanese: String, 
    val english: String, 
    val targetWords: List<String> = emptyList(), 
    val image: String = "", 
    val imagePrompt: String = "", 
    val furigana: String = "" 
) 
    
data class ForgedStory( 
    val id: String = java.util.UUID.randomUUID().toString(), 
    val createdAt: Long = System.currentTimeMillis(), 
    val title: String, 
    val genre: String = "General", 
    val level: String = "Intermediate", 
    val storyJapanese: String, 
    val storyEnglish: String, 
    val sentences: List<StorySentenceItem> = emptyList(), 
    val targetWords: List<StoryWordItem>, 
    val questions: List<StoryQuestion>, 
    val visualAnchor: String = "", 
    val artTags: List<String> = emptyList(), 
    val titleEnglish: String = if (title.contains(" - ")) title.substringBefore(" - ").trim() else title, 
    val titleJapanese: String = if (title.contains(" - ")) title.substringAfter(" - ").trim() else title 
) 
 
fun calculateEstimatedReadingMinutes( 
    pageCount: Int, 
    japaneseText: String = "", 
    englishText: String = "", 
    questionCount: Int = 0 
): Int { 
    val cleanJp = japaneseText.filterNot { it.isWhitespace() } 
    val charCount = cleanJp.length 
    val wordCount = englishText.split("\\s+".toRegex()).count { it.isNotBlank() } 
    val textReadingMinutes = if (charCount > 0) { 
        charCount / 220f 
    } else { 
        wordCount / 130f 
    } 
    val pageTurnOverhead = pageCount * 0.25f 
    val quizOverhead = questionCount * 0.5f 
    val total = kotlin.math.ceil(textReadingMinutes + pageTurnOverhead + quizOverhead).toInt() 
    return total.coerceAtLeast(1) 
} 
 
fun calculateEstimatedReadingMinutes( 
    sentences: List<StorySentenceItem>, 
    questionCount: Int = 0 
): Int { 
    val jp = sentences.joinToString(" ") { it.japanese } 
    val en = sentences.joinToString(" ") { it.english } 
    return calculateEstimatedReadingMinutes( 
        pageCount = sentences.size, 
        japaneseText = jp, 
        englishText = en, 
        questionCount = questionCount 
    ) 
} 
 
val ForgedStory.calculatedEstimatedMinutes: Int 
    get() = calculateEstimatedReadingMinutes( 
        pageCount = sentences.size, 
        japaneseText = storyJapanese.ifBlank { sentences.joinToString(" ") { it.japanese } }, 
        englishText = storyEnglish.ifBlank { sentences.joinToString(" ") { it.english } }, 
        questionCount = questions.size 
    ) 
