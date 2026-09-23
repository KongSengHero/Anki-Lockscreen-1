package com.ankilock.ui.blossom

import org.junit.Assert.assertEquals
import org.junit.Test

class BlossomBottomNavTest { 
    @Test
    fun testBlossomTabsCount() { 
        assertEquals(4, BlossomTab.values().size) 
        assertEquals(BlossomTab.CARDS, BlossomTab.values()[0]) 
        assertEquals(BlossomTab.STORIES, BlossomTab.values()[1]) 
        assertEquals(BlossomTab.LIBRARY, BlossomTab.values()[2]) 
        assertEquals(BlossomTab.JISHO, BlossomTab.values()[3]) 
    } 
} 
