package com.ankilock.data
    
import java.util.UUID
    
data class BookmarkedWord( 
    val id: String = UUID.randomUUID().toString(), 
    val kanji: String, 
    val reading: String = "", 
    val meaning: String = "", 
    val furigana: String = "", 
    val sentence: String = "", 
    val sentenceMeaning: String = "", 
    val sentenceFurigana: String = "", 
    val sourceStoryTitle: String? = null, 
    val tags: List<String> = listOf("Blossom", "MinedVocab"), 
    val createdAt: Long = System.currentTimeMillis() 
) 
