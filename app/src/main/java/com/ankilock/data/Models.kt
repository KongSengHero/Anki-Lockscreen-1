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
    val sentenceAudio: String = "" 
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
    val meaning: String
)
    
data class StoryQuestion( 
    val id: Int, 
    val questionText: String, 
    val options: List<String>, 
    val correctOptionIndex: Int, 
    val explanation: String
)
    
data class ForgedStory( 
    val title: String, 
    val storyJapanese: String, 
    val storyEnglish: String, 
    val targetWords: List<StoryWordItem>, 
    val questions: List<StoryQuestion>
)
