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
    val imageFileName: String = ""
)
