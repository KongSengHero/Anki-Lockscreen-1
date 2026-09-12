package com.ankilock.ui.shinobi
    
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
    
class ShinobiThemeTest { 
    @Test
    fun testColorTokensMatchSpec() { 
        assertEquals(Color(0xFF0C0A09), ShinobiColors.Stone950) 
        assertEquals(Color(0xFF292524), ShinobiColors.Stone800) 
        assertEquals(Color(0xFF57534E), ShinobiColors.Stone600) 
        assertEquals(Color(0xFF2563EB), ShinobiColors.ShinobiBlue) 
        assertEquals(Color(0xFF1E40AF), ShinobiColors.ShinobiBlueLip) 
        assertEquals(Color(0xFF16A34A), ShinobiColors.ShinobiGreen) 
        assertEquals(Color(0xFF0F7535), ShinobiColors.ShinobiGreenLip) 
        assertEquals(Color(0xFFCE0026), ShinobiColors.ShinobiRed) 
        assertEquals(Color(0xFFF59E0B), ShinobiColors.ShinobiAmber) 
        assertEquals(Color(0xFFF6F6F5), ShinobiColors.ShinobiWhite) 
    } 
} 
