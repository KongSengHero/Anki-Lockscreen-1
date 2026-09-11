package com.ankilock.ui.blossom
    
import org.junit.Assert.assertEquals
import org.junit.Test
    
class BlossomReaderTest { 
    @Test
    fun testUnderlineColorAlternation() { 
        val color0 = getUnderlineColor(0) 
        val color1 = getUnderlineColor(1) 
        val color2 = getUnderlineColor(2) 
        assertEquals(BlossomColors.UnderlineOrange, color0) 
        assertEquals(BlossomColors.UnderlineBlue, color1) 
        assertEquals(BlossomColors.UnderlineOrange, color2) 
    } 
}
