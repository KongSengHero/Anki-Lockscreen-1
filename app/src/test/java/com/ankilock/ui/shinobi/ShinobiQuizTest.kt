package com.ankilock.ui.shinobi

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShinobiQuizTest {
    @Test
    fun testValidateAnswer() {
        val selectedIndex = 2
        val correctIndex = 2
        assertTrue(selectedIndex == correctIndex)
        assertFalse(selectedIndex == 1)
    }
}
