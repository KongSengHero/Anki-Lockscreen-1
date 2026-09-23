package com.ankilock.anki

import android.content.Context 
import android.database.sqlite.SQLiteDatabase 
import com.ankilock.data.BookmarkedWord 
import org.json.JSONArray 
import org.json.JSONObject 
import java.io.File 
import java.io.FileOutputStream 
import java.security.MessageDigest 
import java.security.SecureRandom 
import java.util.zip.ZipEntry 
import java.util.zip.ZipOutputStream 

interface AnkiDbConnection { 
    fun execSQL(sql: String) 
    fun bindAndExecute(sql: String, args: Array<Any?>) 
    fun close() 
} 

class AndroidAnkiDbConnection(private val db: SQLiteDatabase) : AnkiDbConnection { 
    override fun execSQL(sql: String) { 
        db.execSQL(sql) 
    } 

    override fun bindAndExecute(sql: String, args: Array<Any?>) { 
        db.execSQL(sql, args) 
    } 

    override fun close() { 
        db.close() 
    } 
} 

object AnkiPackageExporter { 

    const val GUID_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&()*+,-./:;<=>?@[]^_`{|}~" 
    const val KAISHI_MODEL_ID = 1708628080880L 

    const val KAISHI_CSS = """.card {
 font-family: "ヒラギノ角ゴ Pro W3", "Hiragino Kaku Gothic Pro", "Noto Sans JP", "Noto Sans CJK JP", Osaka, "メイリオ", Meiryo, "ＭＳ Ｐゴシック", "MS PGothic", "MS UI Gothic", sans-serif;
 font-size: 44px;
 text-align: center;
 background-color: #181a20;
 color: #ffffff;
 padding: 36px 16px;
}
 
:not(.nightMode) .card,
.nightMode .card {
 background-color: #181a20;
 color: #ffffff;
}
 
img {
 max-width: 300px;
 max-height: 250px;
}
 
.mobile img {
 max-width: 50vw;
}
 
b {
 color: #5586cd;
 font-weight: bold;
}
 
:not(.nightMode) b,
.nightMode b {
 color: #5586cd;
}
 
ruby {
 display: ruby !important;
}
 
ruby > rt,
rt {
 display: ruby-text !important;
 font-size: 0.5em !important;
 line-height: 1.1;
 text-align: center;
 user-select: none;
 color: #ffffff;
 font-weight: normal;
}""" 

    const val KAISHI_Q_FMT = """<div lang="ja">
{{Word}}
{{#Sentence}}
<div style='font-size: 20px;'>{{Sentence}}</div>
{{/Sentence}}
</div>""" 

    const val KAISHI_A_FMT = """<style>
ruby { display: ruby !important; }
rt { display: ruby-text !important; font-size: 0.5em !important; }
</style>
<div lang="ja">
{{furigana:Word Furigana}}
<div style='font-size: 25px; padding-bottom: 20px;'>{{Word Meaning}}</div>
<div style='font-size: 25px;'>{{furigana:Sentence Furigana}}</div>
<div style='font-size: 25px; padding-bottom: 10px;'>{{Sentence Meaning}}</div>
{{#Word Audio}}{{Word Audio}}{{/Word Audio}}
{{#Sentence Audio}}{{Sentence Audio}}{{/Sentence Audio}}
<br>
{{#Picture}}{{Picture}}{{/Picture}}
{{#Notes}}
<br>
<div style="font-size: 20px; padding-top: 12px;">Note: {{Notes}}</div>
{{/Notes}}
</div>""" 

    fun generateGuid(): String { 
        val random = SecureRandom() 
        val sb = StringBuilder(10) 
        for (i in 0 until 10) { 
            val idx = random.nextInt(GUID_CHARS.length) 
            sb.append(GUID_CHARS[idx]) 
        } 
        return sb.toString() 
    } 

    fun computeChecksum(str: String): Long { 
        val md = MessageDigest.getInstance("SHA-1") 
        val digest = md.digest(str.toByteArray(Charsets.UTF_8)) 
        var hash = 0L 
        for (i in 0 until 4) { 
            hash = (hash shl 8) or (digest[i].toLong() and 0xFF) 
        } 
        return hash 
    } 

    fun exportDeckWithConnection( 
        apkgFile: File, 
        dbFile: File, 
        deckName: String, 
        tag: String, 
        words: List<BookmarkedWord>, 
        db: AnkiDbConnection 
    ): File { 
        val nowMs = System.currentTimeMillis() 
        val nowSec = nowMs / 1000L 
        val deckId = 1000000000L + (SecureRandom().nextDouble() * 1000000000L).toLong() 

        db.execSQL("""
            CREATE TABLE col (
                id integer primary key,
                crt integer not null,
                mod integer not null,
                scm integer not null,
                ver integer not null,
                dty integer not null,
                usn integer not null,
                ls integer not null,
                conf text not null,
                models text not null,
                decks text not null,
                dconf text not null,
                tags text not null
            );
        """.trimIndent()) 

        db.execSQL("""
            CREATE TABLE notes (
                id integer primary key,
                guid text not null,
                mid integer not null,
                mod integer not null,
                usn integer not null,
                tags text not null,
                flds text not null,
                sfld text not null,
                csum integer not null,
                flags integer not null,
                data text not null
            );
        """.trimIndent()) 

        db.execSQL("""
            CREATE TABLE cards (
                id integer primary key,
                nid integer not null,
                did integer not null,
                ord integer not null,
                mod integer not null,
                usn integer not null,
                type integer not null,
                queue integer not null,
                due integer not null,
                ivl integer not null,
                factor integer not null,
                reps integer not null,
                lapses integer not null,
                left integer not null,
                odue integer not null,
                odid integer not null,
                flags integer not null,
                data text not null
            );
        """.trimIndent()) 

        db.execSQL("""
            CREATE TABLE revlog (
                id integer primary key,
                cid integer not null,
                usn integer not null,
                ease integer not null,
                ivl integer not null,
                lastIvl integer not null,
                factor integer not null,
                time integer not null,
                type integer not null
            );
        """.trimIndent()) 

        db.execSQL("""
            CREATE TABLE graves (
                usn integer not null,
                oid integer not null,
                type integer not null
            );
        """.trimIndent()) 

        val indexStatements = listOf( 
            "CREATE INDEX ix_notes_usn on notes (usn);", 
            "CREATE INDEX ix_cards_usn on cards (usn);", 
            "CREATE INDEX ix_revlog_usn on revlog (usn);", 
            "CREATE INDEX ix_cards_nid on cards (nid);", 
            "CREATE INDEX ix_cards_sched on cards (did, queue, due);", 
            "CREATE INDEX ix_revlog_cid on revlog (cid);", 
            "CREATE INDEX ix_notes_csum on notes (csum);" 
        ) 
        indexStatements.forEach { db.execSQL(it) } 

        val confJson = JSONObject().apply { 
            put("activeDecks", JSONArray(listOf(1))) 
            put("curDeck", 1) 
            put("newSpread", 0) 
            put("collapseTime", 1200) 
            put("timeLim", 0) 
            put("estTimes", true) 
            put("dueCounts", true) 
            put("curModel", KAISHI_MODEL_ID.toString()) 
            put("nextPos", 1) 
        } 

        val fieldNames = listOf( 
            "Word", "Word Reading", "Word Meaning", "Word Furigana", 
            "Word Audio", "Sentence", "Sentence Meaning", "Sentence Furigana", 
            "Sentence Audio", "Notes", "Pitch Accent", "Pitch Accent Notes", 
            "Frequency", "Picture" 
        ) 
        val fldsArray = JSONArray() 
        fieldNames.forEachIndexed { idx, name -> 
            fldsArray.put(JSONObject().apply { 
                put("font", "Liberation Sans") 
                put("media", JSONArray()) 
                put("name", name) 
                put("ord", idx) 
                put("rtl", false) 
                put("size", 20) 
                put("sticky", false) 
            }) 
        } 

        val tmplsArray = JSONArray().apply { 
            put(JSONObject().apply { 
                put("afmt", KAISHI_A_FMT) 
                put("bafmt", "") 
                put("bfont", "") 
                put("bqfmt", "") 
                put("bsize", 0) 
                put("did", JSONObject.NULL) 
                put("name", "Card 1") 
                put("ord", 0) 
                put("qfmt", KAISHI_Q_FMT) 
            }) 
        } 

        val modelObj = JSONObject().apply { 
            put("css", KAISHI_CSS) 
            put("did", deckId) 
            put("flds", fldsArray) 
            put("id", KAISHI_MODEL_ID.toString()) 
            put("latexPost", "\\end{document}") 
            put("latexPre", "\\documentclass[12pt]{article}\n\\special{papersize=3in,5in}\n\\usepackage[utf8]{inputenc}\n\\usepackage{amssymb,amsmath}\n\\pagestyle{empty}\n\\setlength{\\parindent}{0in}\n\\begin{document}\n") 
            put("latexsvg", false) 
            put("mod", nowSec) 
            put("name", "Kaishi 1.5k") 
            put("req", JSONArray().apply { 
                put(JSONArray(listOf(0, "all", JSONArray(listOf(0))))) 
            }) 
            put("sortf", 0) 
            put("tags", JSONArray()) 
            put("tmpls", tmplsArray) 
            put("type", 0) 
            put("usn", -1) 
            put("vers", JSONArray()) 
        } 

        val modelsJson = JSONObject().apply { 
            put(KAISHI_MODEL_ID.toString(), modelObj) 
        } 

        val decksJson = JSONObject().apply { 
            put("1", JSONObject().apply { 
                put("collapsed", false) 
                put("conf", 1) 
                put("desc", "") 
                put("dyn", 0) 
                put("extendNew", 10) 
                put("extendRev", 50) 
                put("id", 1) 
                put("lrnToday", JSONArray(listOf(0, 0))) 
                put("mod", nowSec) 
                put("name", "Default") 
                put("newToday", JSONArray(listOf(0, 0))) 
                put("revToday", JSONArray(listOf(0, 0))) 
                put("timeToday", JSONArray(listOf(0, 0))) 
                put("usn", 0) 
            }) 
            put(deckId.toString(), JSONObject().apply { 
                put("collapsed", false) 
                put("conf", 1) 
                put("desc", "Kaishi 1.5k Core Vocabulary & Example Sentences") 
                put("dyn", 0) 
                put("extendNew", 0) 
                put("extendRev", 50) 
                put("id", deckId) 
                put("lrnToday", JSONArray(listOf(0, 0))) 
                put("mod", nowSec) 
                put("name", deckName) 
                put("newToday", JSONArray(listOf(0, 0))) 
                put("revToday", JSONArray(listOf(0, 0))) 
                put("timeToday", JSONArray(listOf(0, 0))) 
                put("usn", -1) 
            }) 
        } 

        val dconfJson = JSONObject().apply { 
            put("1", JSONObject().apply { 
                put("autoplay", true) 
                put("id", 1) 
                put("lapse", JSONObject().apply { 
                    put("delays", JSONArray(listOf(10))) 
                    put("leechAction", 0) 
                    put("leechFails", 8) 
                    put("minInt", 1) 
                    put("mult", 0) 
                }) 
                put("maxTaken", 60) 
                put("mod", 0) 
                put("name", "Default") 
                put("new", JSONObject().apply { 
                    put("bury", true) 
                    put("delays", JSONArray(listOf(1, 10))) 
                    put("initialFactor", 2500) 
                    put("ints", JSONArray(listOf(1, 4, 7))) 
                    put("order", 1) 
                    put("perDay", 20) 
                    put("separate", true) 
                }) 
                put("replayq", true) 
                put("rev", JSONObject().apply { 
                    put("bury", true) 
                    put("ease4", 1.3) 
                    put("fuzz", 0.05) 
                    put("ivlFct", 1) 
                    put("maxIvl", 36500) 
                    put("minSpace", 1) 
                    put("perDay", 100) 
                }) 
                put("timer", 0) 
                put("usn", 0) 
            }) 
        } 

        db.bindAndExecute( 
            "INSERT INTO col VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
            arrayOf( 
                1, 
                nowSec, 
                nowMs, 
                nowMs, 
                11, 
                0, 
                0, 
                0, 
                confJson.toString(), 
                modelsJson.toString(), 
                decksJson.toString(), 
                dconfJson.toString(), 
                "{}" 
            ) 
        ) 

        val formattedTags = if (tag.isNotBlank()) " $tag " else "" 

        words.forEachIndexed { idx, word -> 
            val noteId = nowMs + (idx * 2L) 
            val cardId = nowMs + (idx * 2L) + 1L 
            val guid = generateGuid() 

            val wordText = word.kanji 
            val wordReading = word.reading 
            val wordMeaning = word.meaning 
            val wordFurigana = if (word.furigana.isNotBlank()) { 
                word.furigana 
            } else if (wordReading.isNotBlank() && wordReading != wordText) { 
                "$wordText[$wordReading]" 
            } else { 
                wordText 
            } 
            var sentenceText = word.sentence 
            if (wordText.isNotBlank() && sentenceText.isNotBlank() && !sentenceText.contains("<b>")) { 
                sentenceText = sentenceText.replace(wordText, "<b>$wordText</b>") 
            } 
            val sentenceMeaning = word.sentenceMeaning 
            val sentenceFurigana = if (word.sentenceFurigana.isNotBlank()) word.sentenceFurigana else sentenceText 

            val fldsList = listOf( 
                wordText, 
                wordReading, 
                wordMeaning, 
                wordFurigana, 
                "", 
                sentenceText, 
                sentenceMeaning, 
                sentenceFurigana, 
                "", 
                "", 
                "", 
                "", 
                (idx + 1).toString(), 
                "" 
            ) 
            val flds = fldsList.joinToString("\u001f") 
            val sfld = wordText 
            val csum = computeChecksum(sfld) 

            db.bindAndExecute( 
                "INSERT INTO notes VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
                arrayOf( 
                    noteId, 
                    guid, 
                    KAISHI_MODEL_ID, 
                    nowSec, 
                    -1, 
                    formattedTags, 
                    flds, 
                    sfld, 
                    csum, 
                    0, 
                    "" 
                ) 
            ) 

            db.bindAndExecute( 
                "INSERT INTO cards VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
                arrayOf( 
                    cardId, 
                    noteId, 
                    deckId, 
                    0, 
                    nowSec, 
                    -1, 
                    0, 
                    0, 
                    idx + 1, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    "" 
                ) 
            ) 
        } 

        db.close() 

        ZipOutputStream(FileOutputStream(apkgFile)).use { zip -> 
            zip.putNextEntry(ZipEntry("collection.anki2")) 
            dbFile.inputStream().use { input -> 
                input.copyTo(zip) 
            } 
            zip.closeEntry() 

            zip.putNextEntry(ZipEntry("media")) 
            zip.write("{}".toByteArray(Charsets.UTF_8)) 
            zip.closeEntry() 
        } 

        return apkgFile 
    } 

    fun exportDeck( 
        context: Context, 
        deckName: String, 
        tag: String, 
        words: List<BookmarkedWord> 
    ): File { 
        val cleanDeckName = if (deckName.isBlank()) "Blossom::Vocabulary" else deckName 
        val cleanTag = if (tag.isBlank()) "Blossom" else tag 
        val safeName = cleanDeckName.replace(Regex("[^a-zA-Z0-9_\\u3040-\\u309f\\u30a0-\\u30ff\\u4e00-\\u9faf-]"), "_") 
        val exportDir = File(context.cacheDir, "anki_exports").apply { mkdirs() } 
        val apkgFile = File(exportDir, "$safeName.apkg") 
        val tempDbFile = File(exportDir, "collection_${System.currentTimeMillis()}.anki2") 
        if (tempDbFile.exists()) tempDbFile.delete() 

        val db = SQLiteDatabase.openOrCreateDatabase(tempDbFile, null) 
        val dbConn = AndroidAnkiDbConnection(db) 
        val result = exportDeckWithConnection( 
            apkgFile = apkgFile, 
            dbFile = tempDbFile, 
            deckName = cleanDeckName, 
            tag = cleanTag, 
            words = words, 
            db = dbConn 
        ) 
        tempDbFile.delete() 
        return result 
    } 
} 
