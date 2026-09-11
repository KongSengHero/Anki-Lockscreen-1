package com.ankilock.ui.blossom
    
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ankilock.R
    
enum class AppTheme(val id: String, val label: String) { 
    LIGHT("light", "Light"), 
    DARK("dark", "Dark"), 
    DIM("dim", "Dim"); 
    
    companion object { 
        fun fromId(id: String): AppTheme = values().find { it.id.equals(id, ignoreCase = true) } ?: DIM 
    } 
} 
    
object BlossomColors { 
    var currentTheme by mutableStateOf(AppTheme.DIM) 
        private set 
    
    var SakuraRose by mutableStateOf(Color(0xFFE87A90)) 
        private set 
    var SakuraRoseLip by mutableStateOf(Color(0xFFA8475B)) 
        private set 
    var SakuraRoseContainer by mutableStateOf(Color(0xFF2A1B20)) 
        private set 
    
    var SlateBlue by mutableStateOf(Color(0xFFE87A90)) 
        private set 
    var SlateBlueLip by mutableStateOf(Color(0xFFA8475B)) 
        private set 
    var SlateBlueContainer by mutableStateOf(Color(0xFF2A1B20)) 
        private set 
    
    var ElectricBlue by mutableStateOf(Color(0xFFE87A90)) 
        private set 
    var ElectricBlueLip by mutableStateOf(Color(0xFFA8475B)) 
        private set 
    var BlossomBlue by mutableStateOf(Color(0xFFE87A90)) 
        private set 
    var BlossomBlueLip by mutableStateOf(Color(0xFFA8475B)) 
        private set 
    
    var MatchaSage by mutableStateOf(Color(0xFF5FA77C)) 
        private set 
    var MatchaSageLip by mutableStateOf(Color(0xFF3E7755)) 
        private set 
    var MatchaSageContainer by mutableStateOf(Color(0xFF1B3024)) 
        private set 
    
    var WarmOchre by mutableStateOf(Color(0xFFCFA055)) 
        private set 
    var WarmOchreLip by mutableStateOf(Color(0xFF9A7233)) 
        private set 
    var WarmOchreContainer by mutableStateOf(Color(0xFF332717)) 
        private set 
    
    var WisteriaViolet by mutableStateOf(Color(0xFF9678B6)) 
        private set 
    var WisteriaVioletLip by mutableStateOf(Color(0xFF6D528A)) 
        private set 
    var WisteriaVioletContainer by mutableStateOf(Color(0xFF291F35)) 
        private set 
    
    var MutedRose by mutableStateOf(Color(0xFFE87A90)) 
        private set 
    var MutedRoseLip by mutableStateOf(Color(0xFFA8475B)) 
        private set 
    var MutedRoseContainer by mutableStateOf(Color(0xFF2A1B20)) 
        private set 
    
    var MutedMatcha by mutableStateOf(Color(0xFF529B68)) 
        private set 
    var MutedMatchaLip by mutableStateOf(Color(0xFF346644)) 
        private set 
    
    var SoftSage by mutableStateOf(Color(0xFF4A8C5E)) 
        private set 
    var SoftSageLip by mutableStateOf(Color(0xFF2D593A)) 
        private set 
    
    var SlateGlass by mutableStateOf(Color(0xFF2C3240)) 
        private set 
    var SlateGlassLip by mutableStateOf(Color(0xFF1A1E27)) 
        private set 
    
    var Charcoal950 by mutableStateOf(Color(0xFF121418)) 
        private set 
    var Charcoal900 by mutableStateOf(Color(0xFF161920)) 
        private set 
    var Charcoal800 by mutableStateOf(Color(0xFF1E222B)) 
        private set 
    var Charcoal700 by mutableStateOf(Color(0xFF252A35)) 
        private set 
    var Charcoal600 by mutableStateOf(Color(0xFF2C3240)) 
        private set 
    
    var Stone950 by mutableStateOf(Color(0xFF121418)) 
        private set 
    var Stone900 by mutableStateOf(Color(0xFF161920)) 
        private set 
    var Stone800 by mutableStateOf(Color(0xFF1E222B)) 
        private set 
    var Stone700 by mutableStateOf(Color(0xFF252A35)) 
        private set 
    var Stone600 by mutableStateOf(Color(0xFF2C3240)) 
        private set 
    
    var BlossomRed by mutableStateOf(Color(0xFFE87A90)) 
        private set 
    var BlossomRedLip by mutableStateOf(Color(0xFFA8475B)) 
        private set 
    
    var BlossomGreen by mutableStateOf(Color(0xFF5FA77C)) 
        private set 
    var BlossomGreenLip by mutableStateOf(Color(0xFF3E7755)) 
        private set 
    var BlossomGreenSurface by mutableStateOf(Color(0xFF1B3024)) 
        private set 
    
    var BlossomAmber by mutableStateOf(Color(0xFFCFA055)) 
        private set 
    var BlossomAmberLip by mutableStateOf(Color(0xFF9A7233)) 
        private set 
    var BlossomAmberSurface by mutableStateOf(Color(0xFF332717)) 
        private set 
    
    var BlossomWhite by mutableStateOf(Color(0xFFE8EAF0)) 
        private set 
    var BlossomBlack by mutableStateOf(Color(0xFF121418)) 
        private set 
    var BlossomGray by mutableStateOf(Color(0xFF6E7482)) 
        private set 
    
    var BackgroundDeep by mutableStateOf(Color(0xFF121418)) 
        private set 
    var SurfaceCard1 by mutableStateOf(Color(0xFF1E222B)) 
        private set 
    var SurfaceCard2 by mutableStateOf(Color(0xFF161920)) 
        private set 
    var SurfaceCard3 by mutableStateOf(Color(0xFF252A35)) 
        private set 
    var SurfaceElevated by mutableStateOf(Color(0xFF252A35)) 
        private set 
    var CardBorder by mutableStateOf(Color(0xFF2C3240)) 
        private set 
    var CardBorderSubtle by mutableStateOf(Color(0xFF20242E)) 
        private set 
    var SurfaceOverlay by mutableStateOf(Color(0xFF161922)) 
        private set 
    
    var EmeraldGreen by mutableStateOf(Color(0xFF5FA77C)) 
        private set 
    var EmeraldGreenLip by mutableStateOf(Color(0xFF3E7755)) 
        private set 
    var EmeraldGreenSurface by mutableStateOf(Color(0xFF1B3024)) 
        private set 
    
    var CoralRed by mutableStateOf(Color(0xFFE87A90)) 
        private set 
    var CoralRedLip by mutableStateOf(Color(0xFFA8475B)) 
        private set 
    
    var WarmAmber by mutableStateOf(Color(0xFFCFA055)) 
        private set 
    var WarmAmberLip by mutableStateOf(Color(0xFF9A7233)) 
        private set 
    var WarmAmberSurface by mutableStateOf(Color(0xFF332717)) 
        private set 
    
    var VioletPurple by mutableStateOf(Color(0xFF9678B6)) 
        private set 
    var VioletPurpleLip by mutableStateOf(Color(0xFF6D528A)) 
        private set 
    
    var TextPrimary by mutableStateOf(Color(0xFFE8EAF0)) 
        private set 
    var TextSecondary by mutableStateOf(Color(0xFF9AA1AD)) 
        private set 
    var TextMuted by mutableStateOf(Color(0xFF6E7482)) 
        private set 
    
    var UnderlineOrange by mutableStateOf(Color(0xFFCFA055)) 
        private set 
    var UnderlineBlue by mutableStateOf(Color(0xFFE87A90)) 
        private set 
    var KanjiCardBg by mutableStateOf(Color(0xFF2A1C1E)) 
        private set 
    
    var BtnWordBg by mutableStateOf(Color(0xFF1E293B).copy(alpha = 0.40f)) 
        private set 
    var BtnWordText by mutableStateOf(Color(0xFFE2E8F0)) 
        private set 
    var BtnWordBorder by mutableStateOf(Color.White.copy(alpha = 0.10f)) 
        private set 
    var BtnAgainBg by mutableStateOf(Color(0xFF3B1E22)) 
        private set 
    var BtnAgainText by mutableStateOf(Color(0xFFEF5350)) 
        private set 
    var BtnAgainBorder by mutableStateOf(Color(0x80EF5350)) 
        private set 
    var BtnRevealBg by mutableStateOf(Color(0xFF1F2A44)) 
        private set 
    var BtnRevealText by mutableStateOf(Color(0xFF7EB6FF)) 
        private set 
    var BtnRevealBorder by mutableStateOf(Color(0x805C6BC0)) 
        private set 
    var BtnGoodBg by mutableStateOf(Color(0xFF1A3324)) 
        private set 
    var BtnGoodText by mutableStateOf(Color(0xFF81C995)) 
        private set 
    var BtnGoodBorder by mutableStateOf(Color(0x8066BB6A)) 
        private set 
    var BtnAnkiBg by mutableStateOf(Color(0xFF2A2D36)) 
        private set 
    var BtnAnkiText by mutableStateOf(Color(0xFFCBD5E1)) 
        private set 
    var BtnAnkiBorder by mutableStateOf(Color(0x40FFFFFF)) 
        private set 
    
    fun applyTheme(theme: AppTheme) { 
        currentTheme = theme 
        when (theme) { 
            AppTheme.LIGHT -> { 
                SakuraRose = Color(0xFFD84A65) 
                SakuraRoseLip = Color(0xFFB52B44) 
                SakuraRoseContainer = Color(0xFFFFF0F3) 
    
                SlateBlue = Color(0xFFD84A65) 
                SlateBlueLip = Color(0xFFB52B44) 
                SlateBlueContainer = Color(0xFFFFF0F3) 
    
                ElectricBlue = Color(0xFFD84A65) 
                ElectricBlueLip = Color(0xFFB52B44) 
                BlossomBlue = Color(0xFFD84A65) 
                BlossomBlueLip = Color(0xFFB52B44) 
    
                MatchaSage = Color(0xFF2E7D4E) 
                MatchaSageLip = Color(0xFF205C37) 
                MatchaSageContainer = Color(0xFFE8F5EC) 
    
                WarmOchre = Color(0xFFC07A15) 
                WarmOchreLip = Color(0xFF8F580B) 
                WarmOchreContainer = Color(0xFFFEF3E2) 
    
                WisteriaViolet = Color(0xFF7C56A6) 
                WisteriaVioletLip = Color(0xFF5B3C7E) 
                WisteriaVioletContainer = Color(0xFFF3EDFA) 
    
                MutedRose = Color(0xFFD84A65) 
                MutedRoseLip = Color(0xFFB52B44) 
                MutedRoseContainer = Color(0xFFFFF0F3) 
    
                MutedMatcha = Color(0xFF2E7D4E) 
                MutedMatchaLip = Color(0xFF205C37) 
    
                SoftSage = Color(0xFF2E7D4E) 
                SoftSageLip = Color(0xFF205C37) 
    
                SlateGlass = Color(0xFFE8EDF5) 
                SlateGlassLip = Color(0xFFD3DCED) 
    
                Charcoal950 = Color(0xFFDFE2E8) 
                Charcoal900 = Color(0xFFE8EBF0) 
                Charcoal800 = Color(0xFFF3F4F7) 
                Charcoal700 = Color(0xFFDFE2E8) 
                Charcoal600 = Color(0xFFCCD2DC) 
    
                Stone950 = Charcoal950 
                Stone900 = Charcoal900 
                Stone800 = Charcoal800 
                Stone700 = Charcoal700 
                Stone600 = Charcoal600 
    
                BlossomRed = Color(0xFFD84A65) 
                BlossomRedLip = Color(0xFFB52B44) 
    
                BlossomGreen = Color(0xFF2E7D4E) 
                BlossomGreenLip = Color(0xFF205C37) 
                BlossomGreenSurface = Color(0xFFE2EFE5) 
    
                BlossomAmber = Color(0xFFC07A15) 
                BlossomAmberLip = Color(0xFF8F580B) 
                BlossomAmberSurface = Color(0xFFFBF0DF) 
    
                BlossomWhite = Color(0xFF15181E) 
                BlossomBlack = Color(0xFFF3F4F7) 
                BlossomGray = Color(0xFF6B7484) 
    
                BackgroundDeep = Color(0xFFE8EAEF) 
                SurfaceCard1 = Color(0xFFF3F4F7) 
                SurfaceCard2 = Color(0xFFE4E7EE) 
                SurfaceCard3 = Color(0xFFD9DEE8) 
                SurfaceElevated = Color(0xFFECEEF3) 
                CardBorder = Color(0xFFCBD2DF) 
                CardBorderSubtle = Color(0xFFDCE2ED) 
                SurfaceOverlay = Color(0xFFECEEF3) 
    
                EmeraldGreen = Color(0xFF2E7D4E) 
                EmeraldGreenLip = Color(0xFF205C37) 
                EmeraldGreenSurface = Color(0xFFE2EFE5) 
    
                CoralRed = Color(0xFFD84A65) 
                CoralRedLip = Color(0xFFB52B44) 
    
                WarmAmber = Color(0xFFC07A15) 
                WarmAmberLip = Color(0xFF8F580B) 
                WarmAmberSurface = Color(0xFFFBF0DF) 
    
                VioletPurple = Color(0xFF7C56A6) 
                VioletPurpleLip = Color(0xFF5B3C7E) 
    
                TextPrimary = Color(0xFF15181E) 
                TextSecondary = Color(0xFF4B5565) 
                TextMuted = Color(0xFF687487) 
    
                UnderlineOrange = Color(0xFFC07A15) 
                UnderlineBlue = Color(0xFFD84A65) 
                KanjiCardBg = Color(0xFFFDE8EC) 
    
                BtnWordBg = Color(0xFFE2E6EE) 
                BtnWordText = Color(0xFF1E293B) 
                BtnWordBorder = Color(0xFFCBD2DF) 
                BtnAgainBg = Color(0xFFFDE8E8) 
                BtnAgainText = Color(0xFFD32F2F) 
                BtnAgainBorder = Color(0xFFF8B4B4) 
                BtnRevealBg = Color(0xFFEBF5FF) 
                BtnRevealText = Color(0xFF1C64F2) 
                BtnRevealBorder = Color(0xFFA4CAFE) 
                BtnGoodBg = Color(0xFFDEF7EC) 
                BtnGoodText = Color(0xFF0E9F6E) 
                BtnGoodBorder = Color(0xFF84E1BC) 
                BtnAnkiBg = Color(0xFFE5E7EB) 
                BtnAnkiText = Color(0xFF374151) 
                BtnAnkiBorder = Color(0xFFD1D5DB) 
            } 
            AppTheme.DARK -> { 
                SakuraRose = Color(0xFFE87A90) 
                SakuraRoseLip = Color(0xFFB54B62) 
                SakuraRoseContainer = Color(0xFF2C151B) 
    
                SlateBlue = Color(0xFFE87A90) 
                SlateBlueLip = Color(0xFFB54B62) 
                SlateBlueContainer = Color(0xFF2C151B) 
    
                ElectricBlue = Color(0xFFE87A90) 
                ElectricBlueLip = Color(0xFFB54B62) 
                BlossomBlue = Color(0xFFE87A90) 
                BlossomBlueLip = Color(0xFFB54B62) 
    
                MatchaSage = Color(0xFF52C47C) 
                MatchaSageLip = Color(0xFF358C54) 
                MatchaSageContainer = Color(0xFF142B1D) 
    
                WarmOchre = Color(0xFFE5C07B) 
                WarmOchreLip = Color(0xFFA68545) 
                WarmOchreContainer = Color(0xFF2B2213) 
    
                WisteriaViolet = Color(0xFFC678DD) 
                WisteriaVioletLip = Color(0xFF8C4C9E) 
                WisteriaVioletContainer = Color(0xFF281733) 
    
                MutedRose = Color(0xFFE87A90) 
                MutedRoseLip = Color(0xFFB54B62) 
                MutedRoseContainer = Color(0xFF2C151B) 
    
                MutedMatcha = Color(0xFF52C47C) 
                MutedMatchaLip = Color(0xFF358C54) 
    
                SoftSage = Color(0xFF52C47C) 
                SoftSageLip = Color(0xFF358C54) 
    
                SlateGlass = Color(0xFF1E2430) 
                SlateGlassLip = Color(0xFF12161E) 
    
                Charcoal950 = Color(0xFF090B0E) 
                Charcoal900 = Color(0xFF0F1217) 
                Charcoal800 = Color(0xFF13171F) 
                Charcoal700 = Color(0xFF1B202B) 
                Charcoal600 = Color(0xFF252C3D) 
    
                Stone950 = Charcoal950 
                Stone900 = Charcoal900 
                Stone800 = Charcoal800 
                Stone700 = Charcoal700 
                Stone600 = Charcoal600 
    
                BlossomRed = Color(0xFFE87A90) 
                BlossomRedLip = Color(0xFFB54B62) 
    
                BlossomGreen = Color(0xFF52C47C) 
                BlossomGreenLip = Color(0xFF358C54) 
                BlossomGreenSurface = Color(0xFF142B1D) 
    
                BlossomAmber = Color(0xFFE5C07B) 
                BlossomAmberLip = Color(0xFFA68545) 
                BlossomAmberSurface = Color(0xFF2B2213) 
    
                BlossomWhite = Color(0xFFF0F3F8) 
                BlossomBlack = Color(0xFF090B0E) 
                BlossomGray = Color(0xFF656D7E) 
    
                BackgroundDeep = Color(0xFF090B0E) 
                SurfaceCard1 = Color(0xFF13171F) 
                SurfaceCard2 = Color(0xFF0F1217) 
                SurfaceCard3 = Color(0xFF1B202B) 
                SurfaceElevated = Color(0xFF1B202B) 
                CardBorder = Color(0xFF252C3D) 
                CardBorderSubtle = Color(0xFF1A1F2C) 
                SurfaceOverlay = Color(0xFF0F1218) 
    
                EmeraldGreen = Color(0xFF52C47C) 
                EmeraldGreenLip = Color(0xFF358C54) 
                EmeraldGreenSurface = Color(0xFF142B1D) 
    
                CoralRed = Color(0xFFE87A90) 
                CoralRedLip = Color(0xFFB54B62) 
    
                WarmAmber = Color(0xFFE5C07B) 
                WarmAmberLip = Color(0xFFA68545) 
                WarmAmberSurface = Color(0xFF2B2213) 
    
                VioletPurple = Color(0xFFC678DD) 
                VioletPurpleLip = Color(0xFF8C4C9E) 
    
                TextPrimary = Color(0xFFF0F3F8) 
                TextSecondary = Color(0xFFA2AAB8) 
                TextMuted = Color(0xFF656D7E) 
    
                UnderlineOrange = Color(0xFFE5C07B) 
                UnderlineBlue = Color(0xFFE87A90) 
                KanjiCardBg = Color(0xFF231419) 
    
                BtnWordBg = Color(0xFF1E293B).copy(alpha = 0.40f) 
                BtnWordText = Color(0xFFE2E8F0) 
                BtnWordBorder = Color.White.copy(alpha = 0.10f) 
                BtnAgainBg = Color(0xFF3B1E22) 
                BtnAgainText = Color(0xFFEF5350) 
                BtnAgainBorder = Color(0x80EF5350) 
                BtnRevealBg = Color(0xFF1F2A44) 
                BtnRevealText = Color(0xFF7EB6FF) 
                BtnRevealBorder = Color(0x805C6BC0) 
                BtnGoodBg = Color(0xFF1A3324) 
                BtnGoodText = Color(0xFF81C995) 
                BtnGoodBorder = Color(0x8066BB6A) 
                BtnAnkiBg = Color(0xFF2A2D36) 
                BtnAnkiText = Color(0xFFCBD5E1) 
                BtnAnkiBorder = Color(0x40FFFFFF) 
            } 
            AppTheme.DIM -> { 
                SakuraRose = Color(0xFFE87A90) 
                SakuraRoseLip = Color(0xFFA8475B) 
                SakuraRoseContainer = Color(0xFF2A1B20) 
    
                SlateBlue = Color(0xFFE87A90) 
                SlateBlueLip = Color(0xFFA8475B) 
                SlateBlueContainer = Color(0xFF2A1B20) 
    
                ElectricBlue = Color(0xFFE87A90) 
                ElectricBlueLip = Color(0xFFA8475B) 
                BlossomBlue = Color(0xFFE87A90) 
                BlossomBlueLip = Color(0xFFA8475B) 
    
                MatchaSage = Color(0xFF5FA77C) 
                MatchaSageLip = Color(0xFF3E7755) 
                MatchaSageContainer = Color(0xFF1B3024) 
    
                WarmOchre = Color(0xFFCFA055) 
                WarmOchreLip = Color(0xFF9A7233) 
                WarmOchreContainer = Color(0xFF332717) 
    
                WisteriaViolet = Color(0xFF9678B6) 
                WisteriaVioletLip = Color(0xFF6D528A) 
                WisteriaVioletContainer = Color(0xFF291F35) 
    
                MutedRose = Color(0xFFE87A90) 
                MutedRoseLip = Color(0xFFA8475B) 
                MutedRoseContainer = Color(0xFF2A1B20) 
    
                MutedMatcha = Color(0xFF529B68) 
                MutedMatchaLip = Color(0xFF346644) 
    
                SoftSage = Color(0xFF4A8C5E) 
                SoftSageLip = Color(0xFF2D593A) 
    
                SlateGlass = Color(0xFF2C3240) 
                SlateGlassLip = Color(0xFF1A1E27) 
    
                Charcoal950 = Color(0xFF121418) 
                Charcoal900 = Color(0xFF161920) 
                Charcoal800 = Color(0xFF1E222B) 
                Charcoal700 = Color(0xFF252A35) 
                Charcoal600 = Color(0xFF2C3240) 
    
                Stone950 = Charcoal950 
                Stone900 = Charcoal900 
                Stone800 = Charcoal800 
                Stone700 = Charcoal700 
                Stone600 = Charcoal600 
    
                BlossomRed = Color(0xFFE87A90) 
                BlossomRedLip = Color(0xFFA8475B) 
    
                BlossomGreen = Color(0xFF5FA77C) 
                BlossomGreenLip = Color(0xFF3E7755) 
                BlossomGreenSurface = Color(0xFF1B3024) 
    
                BlossomAmber = Color(0xFFCFA055) 
                BlossomAmberLip = Color(0xFF9A7233) 
                BlossomAmberSurface = Color(0xFF332717) 
    
                BlossomWhite = Color(0xFFE8EAF0) 
                BlossomBlack = Color(0xFF121418) 
                BlossomGray = Color(0xFF6E7482) 
    
                BackgroundDeep = Color(0xFF121418) 
                SurfaceCard1 = Color(0xFF1E222B) 
                SurfaceCard2 = Color(0xFF161920) 
                SurfaceCard3 = Color(0xFF252A35) 
                SurfaceElevated = Color(0xFF252A35) 
                CardBorder = Color(0xFF2C3240) 
                CardBorderSubtle = Color(0xFF20242E) 
                SurfaceOverlay = Color(0xFF161922) 
    
                EmeraldGreen = Color(0xFF5FA77C) 
                EmeraldGreenLip = Color(0xFF3E7755) 
                EmeraldGreenSurface = Color(0xFF1B3024) 
    
                CoralRed = Color(0xFFE87A90) 
                CoralRedLip = Color(0xFFA8475B) 
    
                WarmAmber = Color(0xFFCFA055) 
                WarmAmberLip = Color(0xFF9A7233) 
                WarmAmberSurface = Color(0xFF332717) 
    
                VioletPurple = Color(0xFF9678B6) 
                VioletPurpleLip = Color(0xFF6D528A) 
    
                TextPrimary = Color(0xFFE8EAF0) 
                TextSecondary = Color(0xFF9AA1AD) 
                TextMuted = Color(0xFF6E7482) 
    
                UnderlineOrange = Color(0xFFCFA055) 
                UnderlineBlue = Color(0xFFE87A90) 
                KanjiCardBg = Color(0xFF2A1C1E) 
    
                BtnWordBg = Color(0xFF1E293B).copy(alpha = 0.40f) 
                BtnWordText = Color(0xFFE2E8F0) 
                BtnWordBorder = Color.White.copy(alpha = 0.10f) 
                BtnAgainBg = Color(0xFF3B1E22) 
                BtnAgainText = Color(0xFFEF5350) 
                BtnAgainBorder = Color(0x80EF5350) 
                BtnRevealBg = Color(0xFF1F2A44) 
                BtnRevealText = Color(0xFF7EB6FF) 
                BtnRevealBorder = Color(0x805C6BC0) 
                BtnGoodBg = Color(0xFF1A3324) 
                BtnGoodText = Color(0xFF81C995) 
                BtnGoodBorder = Color(0x8066BB6A) 
                BtnAnkiBg = Color(0xFF2A2D36) 
                BtnAnkiText = Color(0xFFCBD5E1) 
                BtnAnkiBorder = Color(0x40FFFFFF) 
            } 
        } 
    } 
} 
    
object BlossomShapes { 
    val SquircleSmall = RoundedCornerShape(10.dp) 
    val SquircleMedium = RoundedCornerShape(16.dp) 
    val SquircleLarge = RoundedCornerShape(18.dp) 
    val SquircleXLarge = RoundedCornerShape(20.dp) 
    val Pill = RoundedCornerShape(50) 
} 
    
val BlossomNunito = FontFamily( 
    Font(R.font.nunito_regular, FontWeight.Normal), 
    Font(R.font.nunito_semibold, FontWeight.SemiBold), 
    Font(R.font.nunito_bold, FontWeight.Bold), 
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold) 
) 
    
fun getUnderlineColor(index: Int): Color = if (index % 2 == 0) BlossomColors.UnderlineOrange else BlossomColors.UnderlineBlue 
