package com.ankilock.ui.shinobi

import com.ankilock.data.StorySessionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShinobiStoryCardTest { 
    @Test
    fun testCuratedStoriesContainThreeCardTypes() { 
        val stories = StorySessionManager.getCuratedStories("starter") 
        assertTrue(stories.size >= 5) 
        
        val normalCard = stories.find { it.id == "curated_kana" } 
        assertNotNull(normalCard) 
        assertTrue(normalCard!!.isCompleted) 
        assertFalse(normalCard.isEndingCard) 
        assertFalse(normalCard.isCurrent) 
        
        val endingCard = stories.find { it.id == "curated_snail" } 
        assertNotNull(endingCard) 
        assertTrue(endingCard!!.isEndingCard) 
        assertEquals(200, endingCard.xp) 
        
        val currentCard = stories.find { it.id == "curated_taku" } 
        assertNotNull(currentCard) 
        assertTrue(currentCard!!.isCurrent) 
        assertFalse(currentCard.isEndingCard) 
        assertFalse(currentCard.isLocked) 
    } 

    @Test
    fun testCuratedStoryForgingBuildsCompleteStory() { 
        val takuStory = StorySessionManager.buildCuratedForgedStory("curated_taku") 
        assertNotNull(takuStory) 
        assertEquals(5, takuStory!!.sentences.size) 
        assertTrue(takuStory.targetWords.isNotEmpty()) 
        assertTrue(takuStory.questions.isNotEmpty()) 
        
        val mattStory = StorySessionManager.buildCuratedForgedStory("curated_matt") 
        assertNotNull(mattStory) 
        assertEquals(5, mattStory!!.sentences.size) 
        assertEquals("Matt Introduces Himself - マットの自己紹介", mattStory.title) 
    } 
} 
