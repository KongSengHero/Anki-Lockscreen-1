package com.ankilock.data
    
import androidx.compose.ui.graphics.Color
import com.ankilock.ui.blossom.AppTheme 
import com.ankilock.ui.blossom.BlossomColors 

data class ThemeBadgeStyle( 
    val backgroundColor: Color, 
    val contentColor: Color, 
    val borderColor: Color 
) 

object StoryThemes { 
    
    val CATEGORIES: Map<String, List<String>> = linkedMapOf( 
        "Happy" to listOf( 
            "Love story - first love", 
            "Love story - reunited after years apart", 
            "Unexpected romance", 
            "Passing a dream university", 
            "Passing a big exam after struggling", 
            "Reconciling with an estranged family member", 
            "Achieving a long-held dream (music, art, sports)", 
            "Landing a dream career", 
            "Adopting a pet", 
            "Childhood friends reuniting", 
            "A small act of kindness from a stranger", 
            "Starting a new job that turns out great", 
            "Surprise birthday celebration", 
            "Surprise promotion at work", 
            "A proposal", 
            "Finding a lost item after a long search", 
            "Finding a lost person after a long search", 
            "Overcoming a fear of heights", 
            "Overcoming a fear of public speaking", 
            "Overcoming a fear of swimming", 
            "A community coming together to help someone", 
            "Winning a competition after many failed attempts", 
            "A long-distance friendship finally meeting in person" 
        ), 
        "Sad" to listOf( 
            "Broken love story - breakup", 
            "Unrequited love", 
            "A loved one passing away", 
            "Trying your best but failing anyway", 
            "Losing touch with an old friend", 
            "A pet passing away", 
            "Moving away from a beloved hometown", 
            "Betrayal by someone trusted", 
            "Regret over a missed opportunity", 
            "Watching someone you love struggle and being unable to help", 
            "Growing apart from family", 
            "A promise that couldn't be kept", 
            "Saying goodbye to a childhood home", 
            "Realizing a dream is no longer achievable", 
            "A friendship ending after a big fight", 
            "Missing someone who moved far away", 
            "Letting go of something you've held onto for years" 
        ), 
        "SliceOfLife" to listOf( 
            "A day at school", 
            "First day at a new school", 
            "Office life", 
            "First day at a new job", 
            "Coworker dynamics", 
            "A trip to somewhere cool - a big city", 
            "A trip to somewhere cool - nature/mountains", 
            "A trip abroad", 
            "A rainy day at home", 
            "Grocery shopping", 
            "Cooking a meal for someone", 
            "Commuting and people-watching", 
            "A weekend with no plans", 
            "Family dinner conversations", 
            "Late-night conversation with a friend", 
            "The first snow of winter", 
            "The start of summer break", 
            "An autumn walk", 
            "A quiet hobby session (painting, gardening, gaming)", 
            "Running errands and small daily encounters", 
            "A lazy Sunday morning", 
            "Studying together with friends" 
        ), 
        "Mystery" to listOf( 
            "A strange letter with no sender", 
            "A neighbor who suddenly disappeared", 
            "An unsolved local legend", 
            "A locked room with no explanation", 
            "Following a trail of clues left behind by someone", 
            "A hidden message found in an old book", 
            "Someone who seems to know too much about you" 
        ), 
        "Horror" to listOf( 
            "A haunted house on the edge of town", 
            "An urban legend that turns out to be real", 
            "A ghost story passed down in a family", 
            "Something strange happening at midnight", 
            "A doll or object that shouldn't move on its own", 
            "A road that isn't supposed to exist" 
        ), 
        "Adventure" to listOf( 
            "A treasure hunt with an old map", 
            "Survival in the wild after getting lost", 
            "A road trip that goes completely sideways", 
            "Exploring an abandoned place", 
            "A journey to find something rare", 
            "Racing against time to reach a destination" 
        ), 
        "Inspirational" to listOf( 
            "An underdog story", 
            "A comeback after public failure", 
            "A mentor who changes someone's life", 
            "Someone overcoming a disability to achieve a goal", 
            "Starting over from nothing", 
            "A community rebuilding after a disaster" 
        ), 
        "Fantasy" to listOf( 
            "A hidden magic school", 
            "Discovering a hidden world within our own", 
            "An encounter with a mythical creature", 
            "A cursed object that grants wishes with a price", 
            "A prophecy that a young person must fulfill", 
            "Two rival kingdoms and an unlikely alliance" 
        ), 
        "Comedy" to listOf( 
            "Pretending to understand Japanese slang in front of cool teens", 
            "A disastrous first attempt at making sushi at home", 
            "Accidentally joining a competitive dodgeball club", 
            "Mistaking a costume festival for a normal day", 
            "A runaway robot vacuum that escapes into the neighborhood", 
            "Two shy people both trying desperately to pay the bill", 
            "Practicing a confident speech only to say the completely wrong word", 
            "A cat that behaves like a corporate CEO", 
            "Getting locked inside a shopping mall overnight", 
            "An epic battle against a stubborn mosquito before sleep" 
        ), 
        "Nostalgic" to listOf( 
            "Summer festival (Matsuri) memories from ten years ago", 
            "The taste of childhood shaved ice (Kakigori) on a hot day", 
            "Hearing the cicadas on the last day of summer vacation", 
            "An old cassette tape found in an attic", 
            "Walking past one's elementary school at dusk", 
            "The sound of the evening chime (Gojinohanashi) echoing through town", 
            "Finding an old handwritten letter in a textbook", 
            "Drinking ramune by the riverbank in high school uniform", 
            "The scent of tatami mats in a grandparents' summer house", 
            "A faded polaroid photograph of old friends who lost touch" 
        ) 
    ) 
    
    val ALL_THEMES: List<String> = CATEGORIES.keys.toList() 
    
    fun formatThemeName(themeKey: String): String { 
        return when (themeKey) { 
            "SliceOfLife" -> "Slice of Life" 
            else -> themeKey 
        } 
    } 
    
    fun getRandomThemeAndTopic( 
        disabledThemes: Set<String> = emptySet(), 
        disabledTopics: Set<String> = emptySet() 
    ): Pair<String, String> { 
        val availableThemes = ALL_THEMES.filter { it !in disabledThemes }.ifEmpty { ALL_THEMES } 
        val theme = availableThemes.random() 
        
        val allTopicsForTheme = CATEGORIES[theme] ?: emptyList() 
        val availableTopics = allTopicsForTheme.filter { it !in disabledTopics }.ifEmpty { allTopicsForTheme } 
        val topic = if (availableTopics.isNotEmpty()) availableTopics.random() else "A memorable event" 
        
        return Pair(theme, topic) 
    } 
    
    fun getThemeBadgeColors(themeKey: String, isDark: Boolean = BlossomColors.currentTheme != AppTheme.LIGHT): ThemeBadgeStyle { 
        if (!isDark) { 
            return when (themeKey) { 
                "Happy" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFFFF3E0), 
                    contentColor = Color(0xFFE65100), 
                    borderColor = Color(0xFFFFCC80) 
                ) 
                "Sad" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFE3F2FD), 
                    contentColor = Color(0xFF1565C0), 
                    borderColor = Color(0xFF90CAF9) 
                ) 
                "SliceOfLife" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFE8F5E9), 
                    contentColor = Color(0xFF2E7D32), 
                    borderColor = Color(0xFFA5D6A7) 
                ) 
                "Mystery" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFF3E5F5), 
                    contentColor = Color(0xFF7B1FA2), 
                    borderColor = Color(0xFFCE93D8) 
                ) 
                "Horror" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFFFEBEE), 
                    contentColor = Color(0xFFC62828), 
                    borderColor = Color(0xFFEF9A9A) 
                ) 
                "Adventure" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFFFF8E1), 
                    contentColor = Color(0xFFEF6C00), 
                    borderColor = Color(0xFFFFE082) 
                ) 
                "Inspirational" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFE0F7FA), 
                    contentColor = Color(0xFF00838F), 
                    borderColor = Color(0xFF80DEEA) 
                ) 
                "Fantasy" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFEDE7F6), 
                    contentColor = Color(0xFF512DA8), 
                    borderColor = Color(0xFFB39DDB) 
                ) 
                "Comedy" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFFFFDE7), 
                    contentColor = Color(0xFFF57F17), 
                    borderColor = Color(0xFFFFF59D) 
                ) 
                "Nostalgic" -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFEFEBE9), 
                    contentColor = Color(0xFF4E342E), 
                    borderColor = Color(0xFFD7CCC8) 
                ) 
                else -> ThemeBadgeStyle( 
                    backgroundColor = Color(0xFFECEFF1), 
                    contentColor = Color(0xFF37474F), 
                    borderColor = Color(0xFFCFD8DC) 
                ) 
            } 
        } 
        return when (themeKey) { 
            "Happy" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF33261D), 
                contentColor = Color(0xFFFFB74D), 
                borderColor = Color(0x66FFB74D) 
            ) 
            "Sad" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF1E2836), 
                contentColor = Color(0xFF90CAF9), 
                borderColor = Color(0x6690CAF9) 
            ) 
            "SliceOfLife" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF1E2F26), 
                contentColor = Color(0xFFA5D6A7), 
                borderColor = Color(0x66A5D6A7) 
            ) 
            "Mystery" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF28203E), 
                contentColor = Color(0xFFCE93D8), 
                borderColor = Color(0x66CE93D8) 
            ) 
            "Horror" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF381C20), 
                contentColor = Color(0xFFEF9A9A), 
                borderColor = Color(0x66EF9A9A) 
            ) 
            "Adventure" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF38291A), 
                contentColor = Color(0xFFFFCC80), 
                borderColor = Color(0x66FFCC80) 
            ) 
            "Inspirational" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF1B2F33), 
                contentColor = Color(0xFF80DEEA), 
                borderColor = Color(0x6680DEEA) 
            ) 
            "Fantasy" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF2B213A), 
                contentColor = Color(0xFFB39DDB), 
                borderColor = Color(0x66B39DDB) 
            ) 
            "Comedy" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF33301B), 
                contentColor = Color(0xFFFFF59D), 
                borderColor = Color(0x66FFF59D) 
            ) 
            "Nostalgic" -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF332724), 
                contentColor = Color(0xFFD7CCC8), 
                borderColor = Color(0x66D7CCC8) 
            ) 
            else -> ThemeBadgeStyle( 
                backgroundColor = Color(0xFF232730), 
                contentColor = Color(0xFFB0BEC5), 
                borderColor = Color(0x66B0BEC5) 
            ) 
        } 
    } 
} 
