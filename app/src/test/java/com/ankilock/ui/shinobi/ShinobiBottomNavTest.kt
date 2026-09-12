package com.ankilock.ui.shinobi

import org.junit.Assert.assertEquals
import org.junit.Test

class ShinobiBottomNavTest {
    @Test
    fun testShinobiTabsCount() {
        assertEquals(4, ShinobiTab.values().size)
        assertEquals(ShinobiTab.STUDY, ShinobiTab.values()[0])
        assertEquals(ShinobiTab.STORIES, ShinobiTab.values()[1])
        assertEquals(ShinobiTab.VOCAB, ShinobiTab.values()[2])
        assertEquals(ShinobiTab.DOJO, ShinobiTab.values()[3])
    }
}
