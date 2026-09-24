package com.ankilock.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CardSessionManagerSyncTest { 
    
    @Before
    fun setUp() { 
        CardSessionManager.clearPendingReviewsForDeck(100L) 
        CardSessionManager.clearPendingReviewsForDeck(200L) 
    } 
    
    @Test
    fun testPendingReviewTrackingAndDeckFilter() { 
        val rev1 = PendingReview(1L, 0, 3, 5000L, 100L) 
        val rev2 = PendingReview(2L, 0, 1, 5000L, 100L) 
        val rev3 = PendingReview(3L, 0, 3, 5000L, 200L) 
        
        CardSessionManager.addPendingReview(rev1) 
        CardSessionManager.addPendingReview(rev2) 
        CardSessionManager.addPendingReview(rev3) 
        
        assertEquals(2, CardSessionManager.getPendingCountForDeck(100L)) 
        assertEquals(1, CardSessionManager.getPendingCountForDeck(200L)) 
        assertEquals(3, CardSessionManager.pendingReviewsCount) 
        
        val popped = CardSessionManager.popLastPendingReview() 
        assertNotNull(popped) 
        assertEquals(3L, popped?.noteId) 
        assertEquals(2, CardSessionManager.pendingReviewsCount) 
        
        CardSessionManager.clearPendingReviewsForDeck(100L) 
        assertEquals(0, CardSessionManager.getPendingCountForDeck(100L)) 
        assertEquals(0, CardSessionManager.pendingReviewsCount) 
    } 
    
    @Test
    fun testSessionQueueRetentionOnRepeatedAgain() { 
        val card = CardInfo( 
            noteId = 101L, 
            cardOrd = 0, 
            question = "test Q", 
            answer = "test A", 
            deckName = "Default", 
            cardType = 1, 
            deckId = 100L, 
            nextReviewTimes = "[\"1m\", \"10m\", \"1d\", \"4d\"]" 
        ) 
        val queue = mutableListOf<CardInfo>() 
        
        val nextAfterFirstAgain = if (queue.isNotEmpty()) { 
            val next = queue.removeAt(0) 
            queue.add(card) 
            next 
        } else { 
            card 
        } 
        CardSessionManager.addPendingReview( 
            PendingReview(card.noteId, card.cardOrd, 1, 5000L, card.deckId) 
        ) 
        assertNotNull(nextAfterFirstAgain) 
        assertEquals(101L, nextAfterFirstAgain.noteId) 
        assertEquals(1, CardSessionManager.pendingReviewsCount) 
        
        val nextAfterSecondAgain = if (queue.isNotEmpty()) { 
            val next = queue.removeAt(0) 
            queue.add(card) 
            next 
        } else { 
            card 
        } 
        CardSessionManager.addPendingReview( 
            PendingReview(card.noteId, card.cardOrd, 1, 5000L, card.deckId) 
        ) 
        assertNotNull(nextAfterSecondAgain) 
        assertEquals(101L, nextAfterSecondAgain.noteId) 
        assertEquals(2, CardSessionManager.pendingReviewsCount) 
        
        val undo1 = CardSessionManager.popLastPendingReview() 
        assertNotNull(undo1) 
        assertEquals(1, CardSessionManager.pendingReviewsCount) 
        
        val undo2 = CardSessionManager.popLastPendingReview() 
        assertNotNull(undo2) 
        assertEquals(0, CardSessionManager.pendingReviewsCount) 
    } 
    
    @Test
    fun testSessionQueueGraduationLogic() { 
        val learningNonGraduatingCard = CardInfo( 
            noteId = 201L, 
            cardOrd = 0, 
            question = "learn Q", 
            answer = "learn A", 
            deckName = "Default", 
            buttonCount = 4, 
            cardType = 1, 
            deckId = 100L, 
            nextReviewTimes = "[\"1m\", \"10m\", \"15m\", \"1d\"]" 
        ) 
        val queue1 = mutableListOf<CardInfo>() 
        val next1 = if (learningNonGraduatingCard.isGoodGraduating) { 
            if (queue1.isNotEmpty()) queue1.removeAt(0) else null 
        } else { 
            if (queue1.isNotEmpty()) { 
                val next = queue1.removeAt(0) 
                queue1.add(learningNonGraduatingCard) 
                next 
            } else { 
                learningNonGraduatingCard 
            } 
        } 
        assertNotNull(next1) 
        assertEquals(201L, next1?.noteId) 
        
        val graduatingCard = CardInfo( 
            noteId = 202L, 
            cardOrd = 0, 
            question = "grad Q", 
            answer = "grad A", 
            deckName = "Default", 
            buttonCount = 4, 
            cardType = 1, 
            deckId = 100L, 
            nextReviewTimes = "[\"1m\", \"10m\", \"1d\", \"4d\"]" 
        ) 
        val queue2 = mutableListOf<CardInfo>() 
        val next2 = if (graduatingCard.isGoodGraduating) { 
            if (queue2.isNotEmpty()) queue2.removeAt(0) else null 
        } else { 
            if (queue2.isNotEmpty()) { 
                val next = queue2.removeAt(0) 
                queue2.add(graduatingCard) 
                next 
            } else { 
                graduatingCard 
            } 
        } 
        assertNull(next2) 
    } 
} 
