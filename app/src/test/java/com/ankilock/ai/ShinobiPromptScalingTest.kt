package com.ankilock.ai

import org.junit.Assert.assertEquals
import org.junit.Test

class ShinobiPromptScalingTest {
    @Test
    fun testLevelPageCounts() {
        assertEquals(5, getTargetPageCount("N5"))
        assertEquals(6, getTargetPageCount("N4"))
        assertEquals(7, getTargetPageCount("N3"))
        assertEquals(8, getTargetPageCount("N2"))
        assertEquals(8, getTargetPageCount("N1"))
    }
}
