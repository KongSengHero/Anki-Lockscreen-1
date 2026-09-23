package com.ankilock.reading
    
import com.ankilock.data.StoryWordItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
    
class GeminiStoryWordsTest { 
    @Test
    fun testParseStoryWordsFromGeminiResponse() { 
        val json = """
            {
              "title": "図書館での出会い",
              "storyJapanese": "佐藤さんは静かな図書館で新しい本を読みました。",
              "storyFurigana": "佐[さ]藤[とう]さんは静[しず]かな図[と]書[しょ]館[かん]で新[あたら]しい本[ほん]を読[よ]みました。",
              "storyWords": [
                {
                  "kanji": "図書館",
                  "reading": "としょかん",
                  "meaning": "library"
                },
                {
                  "kanji": "静か",
                  "reading": "しずか",
                  "meaning": "quiet"
                }
              ],
              "questions": []
            }
        """.trimIndent() 
        
        val service = GeminiStoryService() 
        val story = service.parseStoryResponse( 
            rawText = json, 
            jlptLevel = "N4", 
            targetWords = emptyList(), 
            targetWordsData = emptyList() 
        ) 
        
        assertNotNull(story.storyWords) 
        assertEquals(2, story.storyWords.size) 
        assertEquals("図書館", story.storyWords[0].kanji) 
        assertEquals("としょかん", story.storyWords[0].reading) 
        assertEquals("library", story.storyWords[0].meaning) 
        assertEquals("静か", story.storyWords[1].kanji) 
    } 
} 
