package com.ankilock.ai

import org.junit.Assert.assertEquals
import org.junit.Test

class BlossomPromptScalingTest { 
    @Test
    fun testLevelPageCounts() { 
        assertEquals(5, getTargetPageCount("N5", "Short")) 
        assertEquals(6, getTargetPageCount("N4", "Medium")) 
        assertEquals(9, getTargetPageCount("N3", "Long")) 
    } 
}
