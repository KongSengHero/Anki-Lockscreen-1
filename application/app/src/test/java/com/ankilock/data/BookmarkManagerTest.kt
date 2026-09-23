package com.ankilock.data
    
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
    
class BookmarkManagerTest { 
    @Before
    fun setUp() { 
        BookmarkManager.clearAll() 
    } 
    
    @Test
    fun testAddAndCheckBookmark() { 
        val word = BookmarkedWord( 
            kanji = "企業概要", 
            reading = "きぎょうがいよう", 
            meaning = "Company Overview" 
        ) 
        BookmarkManager.addWord(word) 
        assertTrue(BookmarkManager.isBookmarked("企業概要")) 
        assertFalse(BookmarkManager.isBookmarked("要件定義")) 
        assertEquals(1, BookmarkManager.bookmarkedWords.size) 
    } 
    
    @Test
    fun testDuplicateWordDeduplication() { 
        val word1 = BookmarkedWord( 
            kanji = "要件定義", 
            reading = "ようけんていぎ", 
            meaning = "Requirements" 
        ) 
        val word2 = BookmarkedWord( 
            kanji = "要件定義", 
            reading = "ようけんていぎ", 
            meaning = "Requirements Definition Updated" 
        ) 
        BookmarkManager.addWord(word1) 
        BookmarkManager.addWord(word2) 
        assertEquals(1, BookmarkManager.bookmarkedWords.size) 
        assertEquals("Requirements Definition Updated", BookmarkManager.bookmarkedWords[0].meaning) 
    } 
    
    @Test
    fun testToggleBookmark() { 
        val added = BookmarkManager.toggleBookmark( 
            kanji = "クラウド", 
            reading = "クラウド", 
            meaning = "Cloud Computing" 
        ) 
        assertTrue(added) 
        assertTrue(BookmarkManager.isBookmarked("クラウド")) 
        
        val removed = BookmarkManager.toggleBookmark("クラウド") 
        assertFalse(removed) 
        assertFalse(BookmarkManager.isBookmarked("クラウド")) 
        assertEquals(0, BookmarkManager.bookmarkedWords.size) 
    } 
    
    @Test
    fun testRemoveWord() { 
        val word = BookmarkedWord(kanji = "暗号化", meaning = "Encryption") 
        BookmarkManager.addWord(word) 
        assertEquals(1, BookmarkManager.bookmarkedWords.size) 
        BookmarkManager.removeWord("暗号化") 
        assertEquals(0, BookmarkManager.bookmarkedWords.size) 
        assertFalse(BookmarkManager.isBookmarked("暗号化")) 
    } 
} 
