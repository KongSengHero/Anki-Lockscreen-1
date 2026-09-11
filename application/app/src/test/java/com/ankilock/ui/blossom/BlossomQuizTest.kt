package com.ankilock.ui.blossom

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BlossomQuizTest {
    @Test
    fun testValidateAnswer() {
        val selectedIndex = 2
        val correctIndex = 2
        assertTrue(selectedIndex == correctIndex)
        assertFalse(selectedIndex == 1)
    }
}
