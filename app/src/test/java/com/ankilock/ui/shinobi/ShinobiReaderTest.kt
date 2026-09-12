package com.ankilock.ui.shinobi
    
import com.ankilock.data.StoryWordItem
import com.ankilock.ui.study.StoryPhase
import com.ankilock.ui.study.tokenizeStorySentence
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
    
class ShinobiReaderTest { 
    @Test
    fun testUnderlineColorAlternation() { 
        val color0 = getUnderlineColor(0) 
        val color1 = getUnderlineColor(1) 
        val color2 = getUnderlineColor(2) 
        assertEquals(ShinobiColors.UnderlineOrange, color0) 
        assertEquals(ShinobiColors.UnderlineBlue, color1) 
        assertEquals(ShinobiColors.UnderlineOrange, color2) 
    } 
    
    @Test
    fun testStoryPhaseTransitions() { 
        val phases = StoryPhase.values() 
        assertEquals(3, phases.size) 
        assertEquals(StoryPhase.READER, phases[0]) 
        assertEquals(StoryPhase.EXERCISES, phases[1]) 
        assertEquals(StoryPhase.COMPLETED, phases[2]) 
    } 
    
    @Test
    fun testTokenizeStorySentenceMatchesTargetWords() { 
        val target = listOf( 
            StoryWordItem(kanji = "侍", reading = "さむらい", meaning = "Samurai") 
        ) 
        val tokens = tokenizeStorySentence("侍が歩いている。", target) 
        assertTrue(tokens.any { it.kanji == "侍" }) 
    } 
} 
