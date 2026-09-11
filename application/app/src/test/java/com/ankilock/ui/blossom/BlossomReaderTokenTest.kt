package com.ankilock.ui.blossom

import org.junit.Assert.assertEquals
import org.junit.Test

class BlossomReaderTokenTest {
    @Test
    fun testUnderlineColorAlternation() {
        assertEquals(BlossomColors.UnderlineOrange, getUnderlineColor(0))
        assertEquals(BlossomColors.UnderlineBlue, getUnderlineColor(1))
        assertEquals(BlossomColors.UnderlineOrange, getUnderlineColor(2))
        assertEquals(BlossomColors.UnderlineBlue, getUnderlineColor(3))
    }
}
