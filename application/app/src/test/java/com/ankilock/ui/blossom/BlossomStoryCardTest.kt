package com.ankilock.ui.blossom

import com.ankilock.data.StorySessionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BlossomStoryCardTest { 
    @Test
    fun testCuratedStoriesContainThreeCardTypes() { 
        val stories = StorySessionManager.getCuratedStories("starter", emptySet()) 
        assertTrue(stories.size >= 5) 
        
        val firstCard = stories.find { it.id == "curated_groceries" } 
        assertNotNull(firstCard) 
        assertTrue(firstCard!!.isCurrent) 
        assertFalse(firstCard.isLocked) 
        assertFalse(firstCard.isEndingCard) 
        assertEquals(100, firstCard.xp) 
        
        val lockedCard = stories.find { it.id == "curated_taro" } 
        assertNotNull(lockedCard) 
        assertTrue(lockedCard!!.isLocked) 
        assertFalse(lockedCard.isCurrent) 
        assertEquals(100, lockedCard.xp) 
        
        val endingCard = stories.find { it.id == "curated_snail" } 
        assertNotNull(endingCard) 
        assertTrue(endingCard!!.isEndingCard) 
        assertEquals(200, endingCard.xp) 
        
        val progressedStories = StorySessionManager.getCuratedStories("starter", setOf("curated_groceries")) 
        val completedCard = progressedStories.find { it.id == "curated_groceries" } 
        assertNotNull(completedCard) 
        assertTrue(completedCard!!.isCompleted) 
        
        val nextCurrent = progressedStories.find { it.id == "curated_taro" } 
        assertNotNull(nextCurrent) 
        assertTrue(nextCurrent!!.isCurrent) 
        assertFalse(nextCurrent.isLocked) 
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
