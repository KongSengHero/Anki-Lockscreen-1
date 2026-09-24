package com.ankilock.data
    
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
    
class CardGraduationTest { 
    
    @Test
    fun testLearningCardWithMinuteIntervalDoesNotGraduate() { 
        val card = CardInfo( 
            noteId = 1L, 
            cardOrd = 0, 
            question = "Q", 
            answer = "A", 
            deckName = "Default", 
            buttonCount = 4, 
            nextReviewTimes = "[\"1m\", \"10m\", \"15m\", \"4d\"]", 
            cardType = 1 
        ) 
        assertFalse(card.isGoodGraduating) 
    } 
    
    @Test
    fun testLearningCardWithDayIntervalGraduates() { 
        val card = CardInfo( 
            noteId = 1L, 
            cardOrd = 0, 
            question = "Q", 
            answer = "A", 
            deckName = "Default", 
            buttonCount = 4, 
            nextReviewTimes = "[\"1m\", \"10m\", \"1d\", \"4d\"]", 
            cardType = 1 
        ) 
        assertTrue(card.isGoodGraduating) 
    } 
    
    @Test
    fun testTwoButtonLearningCardWithMinuteInterval() { 
        val card = CardInfo( 
            noteId = 1L, 
            cardOrd = 0, 
            question = "Q", 
            answer = "A", 
            deckName = "Default", 
            buttonCount = 2, 
            nextReviewTimes = "[\"1m\", \"10m\"]", 
            cardType = 1 
        ) 
        assertFalse(card.isGoodGraduating) 
    } 
    
    @Test
    fun testReviewCardDefaultsToGraduating() { 
        val card = CardInfo( 
            noteId = 1L, 
            cardOrd = 0, 
            question = "Q", 
            answer = "A", 
            deckName = "Default", 
            buttonCount = 4, 
            nextReviewTimes = "[\"10m\", \"1d\", \"3d\", \"5d\"]", 
            cardType = 2 
        ) 
        assertTrue(card.isGoodGraduating) 
    } 
    
    @Test
    fun testPendingReviewDataClass() { 
        val pending = PendingReview( 
            noteId = 123L, 
            cardOrd = 0, 
            ease = 3, 
            deckId = 456L 
        ) 
        org.junit.Assert.assertEquals(123L, pending.noteId) 
        org.junit.Assert.assertEquals(3, pending.ease) 
        org.junit.Assert.assertEquals(456L, pending.deckId) 
    } 
} 
