package com.ankilock.anki
    
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContextCompat
import com.ankilock.data.CardInfo
import com.ankilock.data.DeckInfo
import java.io.File
    
class AnkiDroidHelper(private val context: Context) { 
    
    private val resolver: ContentResolver get() = context.contentResolver
    
    fun isAnkiDroidInstalled(): Boolean { 
        return try { 
            context.packageManager.getPackageInfo(ANKI_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) { 
            false
        }
    }
    
    fun hasApiPermission(): Boolean { 
        val granted = ContextCompat.checkSelfPermission( 
            context, 
            PERMISSION_READ_WRITE_DATABASE
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) { 
            return false
        }
        return try { 
            val cursor = resolver.query(DECKS_URI, null, null, null, null)
            cursor?.use { 
                true
            } ?: false
        } catch (e: SecurityException) { 
            false
        } catch (e: Exception) { 
            false
        }
    }
    
    fun getDeckList(): List<DeckInfo> { 
        val decks = mutableListOf<DeckInfo>()
        val uris = listOf(DECKS_URI, Uri.parse("content://$AUTHORITY/decks"))
        
        for (uri in uris) { 
            try { 
                val cursor = resolver.query(uri, null, null, null, null)
                cursor?.use { cur -> 
                    val nameIdx = cur.getColumnIndex(COL_DECK_NAME)
                    val idIdx = cur.getColumnIndex(COL_DECK_ID)
                    val countsIdx = cur.getColumnIndex(COL_DECK_COUNTS)
                    
                    while (cur.moveToNext()) { 
                        val name = cur.getString(nameIdx) ?: continue
                        val id = cur.getLong(idIdx)
                        var newC = 0
                        var learnC = 0
                        var revC = 0
                        
                        if (countsIdx >= 0) { 
                            val countsStr = cur.getString(countsIdx) ?: ""
                            val nums = Regex("\\d+").findAll(countsStr).map { it.value.toInt() }.toList()
                            if (nums.size >= 3) { 
                                learnC = nums[0]
                                revC = nums[1]
                                newC = nums[2]
                            }
                        }
                        
                        if (newC > 100) { 
                            val dueNew = getNotesDueCount("deck:\"$name\" is:new is:due")
                            if (dueNew > 0) newC = dueNew
                        }
                        
                        if (newC == 0 && learnC == 0 && revC == 0) { 
                            val s = getDeckStatsForDeck(name)
                            newC = s.first
                            learnC = s.second
                            revC = s.third
                        }
                        
                        decks.add( 
                            DeckInfo( 
                                id = id, 
                                name = name, 
                                newCount = newC, 
                                learnCount = learnC, 
                                reviewCount = revC
                            )
                        )
                    }
                }
                if (decks.isNotEmpty()) break
            } catch (e: Exception) { 
                e.printStackTrace()
            }
        }
        return decks
    }
    
    fun getAnkiLaunchIntent(): Intent { 
        return context.packageManager.getLaunchIntentForPackage(ANKI_PACKAGE)?.apply { 
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        } ?: Intent(Intent.ACTION_MAIN).apply { 
            setPackage(ANKI_PACKAGE)
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
    }
    
    fun getSelectedDeckStats(selectedDeckIds: Set<Long> = emptySet()): Triple<Int, Int, Int> { 
        val decks = getDeckList()
        if (decks.isEmpty()) return Triple(0, 0, 0)
        
        val targetDecks = if (selectedDeckIds.isNotEmpty()) { 
            decks.filter { it.id in selectedDeckIds }
        } else { 
            decks
        }
        
        var newC = 0
        var learnC = 0
        var revC = 0
        for (deck in targetDecks) { 
            newC += deck.newCount
            learnC += deck.learnCount
            revC += deck.reviewCount
        }
        return Triple(newC, learnC, revC)
    }
    
    fun getDeckStatsForDeck(deckName: String): Triple<Int, Int, Int> { 
        val cleanDeck = deckName.trim()
        val newNotes = getNotesDueCount("deck:\"$cleanDeck\" is:new is:due").takeIf { it > 0 } 
            ?: getNotesDueCount("deck:\"$cleanDeck\" is:new")
        val learnNotes = getNotesDueCount("deck:\"$cleanDeck\" is:learn")
        val dueNotes = getNotesDueCount("deck:\"$cleanDeck\" is:due -is:learn -is:new")
        if (newNotes > 0 || learnNotes > 0 || dueNotes > 0) { 
            return Triple(newNotes, learnNotes, dueNotes)
        }
        return Triple(0, 0, 0)
    }
    
    fun getNotesDueCount(searchQuery: String): Int { 
        return try { 
            val cursor = resolver.query(NOTES_URI, arrayOf("_id"), searchQuery, null, null)
            cursor?.use { it.count } ?: 0
        } catch (e: Exception) { 
            0
        }
    }
    
    fun getNextDueCard(deckIds: Set<Long> = emptySet(), excludeNoteId: Long? = null): CardInfo? { 
        val decks = getDeckList()
        val queryDeckIds = if (deckIds.isNotEmpty()) deckIds.toList() else listOf<Long?>(null)
        
        for (deckId in queryDeckIds) { 
            val card = queryNextDueCardForDeck(deckId, excludeNoteId, decks)
            if (card != null) return card
        }
        return null
    }
    
    fun getNextDueCard(deckId: Long?, excludeNoteId: Long? = null): CardInfo? { 
        return getNextDueCard(if (deckId != null) setOf(deckId) else emptySet(), excludeNoteId)
    }
    
    private fun queryNextDueCardForDeck( 
        deckId: Long?, 
        excludeNoteId: Long?, 
        decks: List<DeckInfo>
    ): CardInfo? { 
        try { 
            val selection = if (deckId != null) "deckID=$deckId, limit=10" else "limit=10"
            
            val cursor = resolver.query( 
                SCHEDULE_URI, 
                null, 
                selection, 
                null, 
                null
            )
            
            cursor?.use { cur -> 
                while (cur.moveToNext()) { 
                    val noteId = cur.getLong( 
                        cur.getColumnIndexOrThrow(COL_NOTE_ID)
                    )
                    if (excludeNoteId != null && noteId == excludeNoteId && cur.count > 1) { 
                        continue
                    }
                    val cardOrd = cur.getInt( 
                        cur.getColumnIndexOrThrow(COL_CARD_ORD)
                    )
                    val buttonCount = cur.getInt( 
                        cur.getColumnIndexOrThrow(COL_BUTTON_COUNT)
                    )
                    val nextTimes = cur.getString( 
                        cur.getColumnIndexOrThrow(COL_NEXT_REVIEW_TIMES)
                    ) ?: ""
                    
                    val deckName = if (deckId != null) { 
                        decks.find { it.id == deckId }?.name ?: getDeckNameForNote(noteId, decks)
                    } else { 
                        getDeckNameForNote(noteId, decks)
                    }
                    
                    val typeCol = cur.getColumnIndex("type").takeIf { it >= 0 } 
                        ?: cur.getColumnIndex("card_type").takeIf { it >= 0 } 
                    val queueCol = cur.getColumnIndex("queue").takeIf { it >= 0 } 
                    
                    val cardType = if (typeCol != null) { 
                        val rawType = cur.getInt(typeCol) 
                        when (rawType) { 
                            0 -> 0 
                            1, 3 -> 1 
                            2 -> 2 
                            else -> 0 
                        } 
                    } else if (queueCol != null) { 
                        val rawQueue = cur.getInt(queueCol) 
                        when (rawQueue) { 
                            0 -> 0 
                            1, 3 -> 1 
                            2 -> 2 
                            else -> 0 
                        } 
                    } else { 
                        when { 
                            getNotesDueCount("nid:$noteId is:learn") > 0 -> 1 
                            getNotesDueCount("nid:$noteId is:new") > 0 -> 0 
                            else -> 2 
                        } 
                    } 
                    
                    val parsed = getCardContent(noteId) 
                    
                    return parsed.copy( 
                        noteId = noteId, 
                        cardOrd = cardOrd, 
                        deckName = deckName.ifEmpty { decks.firstOrNull()?.name ?: "" }, 
                        buttonCount = buttonCount, 
                        nextReviewTimes = nextTimes, 
                        cardType = cardType
                    ) 
                } 
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        return null
    }
    
    fun getCardContent(noteId: Long): CardInfo { 
        try { 
            val noteUri = Uri.withAppendedPath(NOTES_URI, noteId.toString())
            resolver.query( 
                noteUri, 
                null, 
                null, 
                null, 
                null
            )?.use { cursor -> 
                if (cursor.moveToFirst()) { 
                    val fldsIdx = cursor.getColumnIndex(COL_FLDS)
                    if (fldsIdx >= 0) { 
                        val fields = cursor.getString(fldsIdx) ?: ""
                        return parseCardContent(fields)
                    }
                }
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        return CardInfo(noteId = noteId, cardOrd = 0, question = "", answer = "", deckName = "")
    }
    
    fun parseCardContent(fields: String): CardInfo { 
        val rawParts = fields.split("\u001f")
        if (rawParts.isEmpty()) { 
            return CardInfo(noteId = 0L, cardOrd = 0, question = "", answer = "", deckName = "")
        }
        
        var detectedKanji = ""
        var detectedKanjiFurigana = ""
        var detectedKanjiMeaning = ""
        var detectedSentence = ""
        var detectedSentenceFurigana = ""
        var detectedSentenceMeaning = ""
        var detectedImage = ""
        var detectedWordAudio = ""
        var detectedSentenceAudio = ""
        
        val soundRegex = Regex("\\[sound:\\s*([^\\]]+)\\s*\\]", RegexOption.IGNORE_CASE)
        fun extractSound(text: String): String { 
            val m = soundRegex.find(text)
            if (m != null) return cleanHtml(m.groupValues[1]).trim()
            val clean = cleanHtml(text).trim()
            if (clean.endsWith(".mp3", ignoreCase = true) || 
                clean.endsWith(".ogg", ignoreCase = true) || 
                clean.endsWith(".wav", ignoreCase = true) || 
                clean.endsWith(".m4a", ignoreCase = true) || 
                clean.endsWith(".aac", ignoreCase = true) || 
                clean.endsWith(".opus", ignoreCase = true)
            ) { 
                return clean
            }
            return ""
        }
        
        val imgRegex = Regex("<img[^>]+src=[\"']?([^\"'>\\s]+)[\"']?")
        for (part in rawParts) { 
            val match = imgRegex.find(part)
            if (match != null && detectedImage.isEmpty()) { 
                detectedImage = match.groupValues[1]
            }
        }
        
        if (rawParts.size >= 8) { 
            val cleanVocab = cleanHtml(rawParts[0])
            val vocabWithFurigana = rawParts.getOrNull(3) ?: ""
            val rawVocabAudio = rawParts.getOrNull(4) ?: ""
            val rawSentenceAudio = rawParts.getOrNull(8) ?: ""
            
            detectedWordAudio = extractSound(rawVocabAudio)
            detectedSentenceAudio = extractSound(rawSentenceAudio)
            
            detectedKanji = cleanVocab
            if (vocabWithFurigana.contains("[") && vocabWithFurigana.contains("]")) { 
                detectedKanjiFurigana = vocabWithFurigana
            } else if (rawParts.size > 1) { 
                val cleanKana = cleanHtml(rawParts[1])
                if (cleanKana != cleanVocab && isJapanese(cleanKana)) { 
                    detectedKanjiFurigana = cleanKana
                }
            }
            detectedKanjiMeaning = cleanHtml(rawParts[2])
            
            val rawSentenceClean = cleanHtml(rawParts[5])
            val rawSentenceFuri = rawParts[7]
            
            detectedSentence = rawSentenceClean.ifEmpty { cleanFuriganaToKanji(rawSentenceFuri) }
            detectedSentenceFurigana = rawSentenceFuri.ifEmpty { rawSentenceClean }
            
            val rawSentenceEng = cleanHtml(rawParts[6])
            if (isEnglish(rawSentenceEng)) { 
                detectedSentenceMeaning = rawSentenceEng
            } else { 
                for (p in rawParts) { 
                    val clean = cleanHtml(p)
                    if (clean.length > 10 && isEnglish(clean) && !isJapanese(clean) && clean != detectedKanjiMeaning) { 
                        detectedSentenceMeaning = clean
                        break
                    }
                }
            }
        } else { 
            val rawVocab = rawParts[0]
            if (rawVocab.contains("[") && rawVocab.contains("]")) { 
                detectedKanjiFurigana = rawVocab
                detectedKanji = cleanFuriganaToKanji(rawVocab)
            } else { 
                detectedKanji = cleanHtml(rawVocab)
                if (rawParts.size > 1) { 
                    val p1 = cleanHtml(rawParts[1])
                    if (isJapanese(p1) && p1 != detectedKanji) { 
                        detectedKanjiFurigana = p1
                    }
                }
            }
            if (rawParts.size > 2) detectedKanjiMeaning = cleanHtml(rawParts[2])
            
            for (i in 3 until rawParts.size) { 
                val part = rawParts[i]
                if (part.contains("[") && part.contains("]")) { 
                    detectedSentence = cleanFuriganaToKanji(part)
                    detectedSentenceFurigana = part
                } else if (isEnglish(cleanHtml(part)) && cleanHtml(part).length > 10) { 
                    detectedSentenceMeaning = cleanHtml(part)
                }
            }
        }
        
        if (detectedWordAudio.isEmpty() || detectedSentenceAudio.isEmpty()) { 
            val foundSounds = mutableListOf<String>()
            for (part in rawParts) { 
                soundRegex.findAll(part).forEach { match -> 
                    val soundName = match.groupValues[1].trim()
                    if (soundName.isNotEmpty() && !foundSounds.contains(soundName)) { 
                        foundSounds.add(soundName)
                    }
                }
                val rawSound = extractSound(part)
                if (rawSound.isNotEmpty() && !foundSounds.contains(rawSound)) { 
                    foundSounds.add(rawSound)
                }
            }
            if (detectedWordAudio.isEmpty() && foundSounds.isNotEmpty()) { 
                detectedWordAudio = foundSounds[0]
            }
            if (detectedSentenceAudio.isEmpty()) { 
                if (detectedWordAudio.isNotEmpty() && foundSounds.size > 1) { 
                    detectedSentenceAudio = foundSounds.firstOrNull { it != detectedWordAudio } ?: ""
                } else if (detectedWordAudio.isEmpty() && foundSounds.size > 1) { 
                    detectedSentenceAudio = foundSounds[1]
                }
            }
        }
        
        return CardInfo( 
            noteId = 0L, 
            cardOrd = 0, 
            question = detectedKanji, 
            answer = detectedKanjiMeaning, 
            deckName = "", 
            kanji = detectedKanji, 
            kanjiFurigana = detectedKanjiFurigana, 
            kanjiMeaning = detectedKanjiMeaning, 
            sentence = detectedSentence, 
            sentenceFurigana = detectedSentenceFurigana, 
            sentenceMeaning = detectedSentenceMeaning, 
            imageFileName = detectedImage, 
            wordAudio = detectedWordAudio, 
            sentenceAudio = detectedSentenceAudio
        )
    }
    
    fun getCardImageBitmap(imageFileName: String): Bitmap? { 
        if (imageFileName.isBlank()) return null
        val cleanName = imageFileName.trim()
        
        try { 
            val mediaUri = Uri.parse("content://$AUTHORITY/media/" + Uri.encode(cleanName))
            resolver.openInputStream(mediaUri)?.use { stream -> 
                val options = BitmapFactory.Options().apply { 
                    inSampleSize = 2
                }
                return BitmapFactory.decodeStream(stream, null, options)
            }
        } catch (e: Exception) { 
        }
        
        val possibleFolders = listOf( 
            File("/storage/emulated/0/Android/data/com.ichi2.anki/files/AnkiDroid/collection.media"), 
            File("/storage/emulated/0/AnkiDroid/collection.media"), 
            File(context.filesDir.parentFile?.parentFile, "com.ichi2.anki/files/AnkiDroid/collection.media")
        )
        
        for (folder in possibleFolders) { 
            val file = File(folder, cleanName)
            if (file.exists()) { 
                try { 
                    val options = BitmapFactory.Options().apply { 
                        inSampleSize = 2
                    }
                    return BitmapFactory.decodeFile(file.absolutePath, options)
                } catch (e: Exception) { 
                }
            }
        }
        return null
    }
    
    fun getAudioFile(audioFileName: String): File? { 
        if (audioFileName.isBlank()) return null
        val cleanName = audioFileName.trim()
        
        val possibleFolders = listOf( 
            File("/storage/emulated/0/Android/data/com.ichi2.anki/files/AnkiDroid/collection.media"), 
            File("/storage/emulated/0/AnkiDroid/collection.media"), 
            File("/sdcard/AnkiDroid/collection.media"), 
            File("/storage/emulated/0/Download/AnkiDroid/collection.media"), 
            File("/storage/emulated/0/Download/collection.media"), 
            File("/storage/emulated/0/Documents/AnkiDroid/collection.media"), 
            File("/storage/emulated/0/Documents/collection.media"), 
            File(context.filesDir.parentFile?.parentFile, "com.ichi2.anki/files/AnkiDroid/collection.media"), 
            File(context.getExternalFilesDir(null)?.parentFile?.parentFile, "com.ichi2.anki/files/AnkiDroid/collection.media")
        )
        
        for (folder in possibleFolders) { 
            val file = File(folder, cleanName)
            if (file.exists() && file.canRead()) { 
                return file
            }
        }
        
        try { 
            val mediaUri = Uri.parse("content://$AUTHORITY/media/" + Uri.encode(cleanName))
            resolver.openInputStream(mediaUri)?.use { inputStream -> 
                val cacheDir = File(context.cacheDir, "anki_audio")
                if (!cacheDir.exists()) cacheDir.mkdirs()
                val cacheFile = File(cacheDir, cleanName)
                if (!cacheFile.exists() || cacheFile.length() == 0L) { 
                    cacheFile.outputStream().use { outStream -> 
                        inputStream.copyTo(outStream)
                    }
                }
                if (cacheFile.exists() && cacheFile.length() > 0L) { 
                    return cacheFile
                }
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        return null
    }
    
    private fun isJapanese(text: String): Boolean { 
        return text.any { char -> 
            (char in '\u3040'..'\u309F') || 
            (char in '\u30A0'..'\u30FF') || 
            (char in '\u4E00'..'\u9FAF')
        }
    }
    
    private fun isEnglish(text: String): Boolean { 
        return text.any { char -> char in 'a'..'z' || char in 'A'..'Z' }
    }
    
    private fun cleanHtml(html: String): String { 
        return html.replace(Regex("\\[sound:[^\\]]+\\]"), "") 
            .replace(Regex("<style[\\s\\S]*?</style>"), "") 
            .replace(Regex("<script[\\s\\S]*?</script>"), "") 
            .replace(Regex("<br\\s*/?>"), " ") 
            .replace(Regex("<[^>]*>"), "") 
            .replace("&nbsp;", " ") 
            .replace("&amp;", "&") 
            .replace("&lt;", "<") 
            .replace("&gt;", ">") 
            .trim()
    }
    
    private fun cleanFuriganaToKanji(furiganaField: String): String { 
        var text = furiganaField.replace(Regex("([^\\[\\s]+)\\[([^\\]]+)\\]"), "$1")
        text = text.replace(Regex("<ruby>([^<]+)<rt>[^<]+</rt></ruby>"), "$1")
        return cleanHtml(text)
    }
    
    private fun getDeckNameForNote(noteId: Long, decks: List<DeckInfo> = emptyList()): String { 
        try { 
            val targetDecks = if (decks.isNotEmpty()) decks else getDeckList()
            for (deck in targetDecks) { 
                if (getNotesDueCount("nid:$noteId deck:\"${deck.name}\"") > 0) { 
                    return deck.name
                }
            }
            if (targetDecks.isNotEmpty()) return targetDecks.first().name
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        return ""
    }
    
    fun answerCard(noteId: Long, cardOrd: Int, ease: Int, timeTaken: Long): Boolean { 
        return try { 
            val values = ContentValues().apply { 
                put(COL_NOTE_ID, noteId)
                put(COL_CARD_ORD, cardOrd)
                put(COL_EASE, ease)
                put(COL_TIME_TAKEN, timeTaken)
            }
            resolver.update(SCHEDULE_URI, values, null, null) > 0
        } catch (e: Exception) { 
            e.printStackTrace()
            false
        }
    }
    
    fun suspendCard(noteId: Long, cardOrd: Int): Boolean { 
        return try { 
            val values = ContentValues().apply { 
                put(COL_NOTE_ID, noteId)
                put(COL_CARD_ORD, cardOrd)
                put(COL_SUSPEND, 1)
            }
            resolver.update(SCHEDULE_URI, values, null, null) > 0
        } catch (e: Exception) { 
            e.printStackTrace()
            false
        }
    }
    
    companion object { 
        const val ANKI_PACKAGE = "com.ichi2.anki"
        const val PERMISSION_READ_WRITE_DATABASE = "com.ichi2.anki.permission.READ_WRITE_DATABASE"
        private const val AUTHORITY = "com.ichi2.anki.flashcards"
        
        val DECKS_URI: Uri = Uri.parse("content://$AUTHORITY/decks/")
        val SCHEDULE_URI: Uri = Uri.parse("content://$AUTHORITY/schedule/")
        val NOTES_URI: Uri = Uri.parse("content://$AUTHORITY/notes")
        
        private const val COL_DECK_NAME = "deck_name"
        private const val COL_DECK_ID = "deck_id"
        private const val COL_DECK_COUNTS = "deck_counts"
        private const val COL_NOTE_ID = "note_id"
        private const val COL_CARD_ORD = "ord"
        private const val COL_BUTTON_COUNT = "button_count"
        private const val COL_NEXT_REVIEW_TIMES = "next_review_times"
        private const val COL_FLDS = "flds"
        private const val COL_EASE = "answer_ease"
        private const val COL_TIME_TAKEN = "time_taken"
        private const val COL_SUSPEND = "suspended"
    }
}
