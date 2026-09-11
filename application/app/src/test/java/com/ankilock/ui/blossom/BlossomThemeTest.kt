package com.ankilock.ui.blossom
    
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
    
class BlossomThemeTest { 
    @Test
    fun testColorTokensMatchSpec() { 
        assertEquals(Color(0xFF121418), BlossomColors.Stone950) 
        assertEquals(Color(0xFF1E222B), BlossomColors.Stone800) 
        assertEquals(Color(0xFF2C3240), BlossomColors.Stone600) 
        assertEquals(Color(0xFFE87A90), BlossomColors.SakuraRose) 
        assertEquals(Color(0xFFA8475B), BlossomColors.SakuraRoseLip) 
        assertEquals(Color(0xFF5FA77C), BlossomColors.BlossomGreen) 
        assertEquals(Color(0xFF3E7755), BlossomColors.BlossomGreenLip) 
        assertEquals(Color(0xFFE87A90), BlossomColors.BlossomRed) 
        assertEquals(Color(0xFFCFA055), BlossomColors.BlossomAmber) 
        assertEquals(Color(0xFFE8EAF0), BlossomColors.BlossomWhite) 
    } 
}
