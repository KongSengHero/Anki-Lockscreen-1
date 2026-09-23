package com.ankilock.anki

import com.ankilock.data.BookmarkedWord 
import org.junit.Assert.assertEquals 
import org.junit.Assert.assertNotNull 
import org.junit.Assert.assertTrue 
import org.junit.Test 
import java.io.File 
import java.nio.file.Files 
import java.sql.DriverManager 
import java.util.zip.ZipFile 

class AnkiPackageExporterTest { 

    @Test 
    fun testGuidGeneration() { 
        val guid = AnkiPackageExporter.generateGuid() 
        assertEquals(10, guid.length) 
        assertTrue(guid.all { AnkiPackageExporter.GUID_CHARS.contains(it) }) 
    } 

    @Test 
    fun testChecksum() { 
        val csum = AnkiPackageExporter.computeChecksum("食べる") 
        assertTrue(csum > 0L) 
    } 

    @Test 
    fun testApkgExportWithSqlite() { 
        val tempDir = Files.createTempDirectory("anki_test").toFile() 
        val dbFile = File(tempDir, "collection.anki2") 
        val apkgFile = File(tempDir, "TestDeck.apkg") 

        val conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}") 
        val dbConn = JdbcAnkiDbConnection(conn) 

        val words = listOf( 
            BookmarkedWord( 
                kanji = "本棚", 
                reading = "ほんだな", 
                meaning = "bookshelf", 
                furigana = "本棚[ほんだな]", 
                sentence = "部屋に新しい本棚を置いた。", 
                sentenceMeaning = "I placed a new bookshelf in the room." 
            ), 
            BookmarkedWord( 
                kanji = "猫", 
                reading = "ねこ", 
                meaning = "cat", 
                furigana = "猫[ねこ]", 
                sentence = "庭に猫がいる。", 
                sentenceMeaning = "There is a cat in the garden." 
            ) 
        ) 

        val exportedFile: File = AnkiPackageExporter.exportDeckWithConnection( 
            apkgFile = apkgFile, 
            dbFile = dbFile, 
            deckName = "Japanese::Test", 
            tag = "TestTag", 
            words = words, 
            db = dbConn 
        ) 

        assertTrue(exportedFile.exists()) 
        assertTrue(exportedFile.length() > 0) 

        val zip = ZipFile(exportedFile) 
        val colEntry = zip.getEntry("collection.anki2") 
        val mediaEntry = zip.getEntry("media") 
        assertNotNull(colEntry) 
        assertNotNull(mediaEntry) 

        val mediaContent = zip.getInputStream(mediaEntry).bufferedReader().readText() 
        assertEquals("{}", mediaContent.trim()) 
        zip.close() 

        val readConn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}") 
        readConn.createStatement().use { stmt -> 
            val rsNotes = stmt.executeQuery("SELECT count(*) FROM notes") 
            assertTrue(rsNotes.next()) 
            assertEquals(2, rsNotes.getInt(1)) 

            val rsCards = stmt.executeQuery("SELECT count(*) FROM cards") 
            assertTrue(rsCards.next()) 
            assertEquals(2, rsCards.getInt(1)) 

            val rsCol = stmt.executeQuery("SELECT models, decks FROM col") 
            assertTrue(rsCol.next()) 
            val modelsJson = rsCol.getString("models") 
            val decksJson = rsCol.getString("decks") 
            assertTrue(modelsJson.contains("Kaishi 1.5k")) 
            assertTrue(decksJson.contains("Japanese::Test")) 
        } 
        readConn.close() 

        tempDir.deleteRecursively() 
    } 
} 
