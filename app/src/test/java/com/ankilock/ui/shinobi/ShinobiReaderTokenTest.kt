package com.ankilock.ui.shinobi

import org.junit.Assert.assertEquals
import org.junit.Test

class ShinobiReaderTokenTest {
    @Test
    fun testUnderlineColorAlternation() {
        assertEquals(ShinobiColors.UnderlineOrange, getUnderlineColor(0))
        assertEquals(ShinobiColors.UnderlineBlue, getUnderlineColor(1))
        assertEquals(ShinobiColors.UnderlineOrange, getUnderlineColor(2))
        assertEquals(ShinobiColors.UnderlineBlue, getUnderlineColor(3))
    }
}
