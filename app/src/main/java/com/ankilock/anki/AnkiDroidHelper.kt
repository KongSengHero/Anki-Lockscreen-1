package com.ankilock.anki
    
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContextCompat
import com.ankilock.data.CardInfo
import com.ankilock.data.DeckInfo
import java.io.File
    
data class CardParsedContent( 
    val question: String = "", 
    val answer: String = "", 
    val kanji: String = "", 
    val kanjiFurigana: String = "", 
    val kanjiMeaning: String = "", 
    val sentence: String = "", 
    val sentenceFurigana: String = "", 
    val sentenceMeaning: String = "", 
    val imageFileName: String = ""
)
    
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
    
    fun getSelectedDeckStats(): Triple<Int, Int, Int> { 
        val uris = listOf( 
            Uri.parse("content://$AUTHORITY/selected_deck"), 
            Uri.parse("content://$AUTHORITY/selected_deck/"), 
            Uri.parse("content://$AUTHORITY/decks/"), 
            Uri.parse("content://$AUTHORITY/decks")
        )
        
        for (uri in uris) { 
            try { 
                resolver.query(uri, null, null, null, null)?.use { cur -> 
                    val countsIdx = cur.getColumnIndex(COL_DECK_COUNTS)
                    val nameIdx = cur.getColumnIndex(COL_DECK_NAME)
                    
                    while (cur.moveToNext()) { 
                        val name = if (nameIdx >= 0) cur.getString(nameIdx) ?: "" else ""
                        if (countsIdx >= 0) { 
                            val countsStr = cur.getString(countsIdx) ?: ""
                            val nums = Regex("\\d+").findAll(countsStr).map { it.value.toInt() }.toList()
                            if (nums.size >= 3) { 
                                val learnC = nums[0]
                                val revC = nums[1]
                                var newC = nums[2]
                                if (newC > 100) { 
                                    val dueNew = getNotesDueCount("deck:\"$name\" is:new is:due")
                                    if (dueNew > 0) newC = dueNew
                                }
                                if (learnC > 0 || revC > 0 || newC > 0) { 
                                    return Triple(newC, learnC, revC)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) { 
            }
        }
        
        val newNotes = getNotesDueCount("deck:\"Kaishi 1.5k\" is:new is:due").takeIf { it > 0 } 
            ?: getNotesDueCount("deck:\"Kaishi 1.5k\" is:new")
        val learnNotes = getNotesDueCount("deck:\"Kaishi 1.5k\" is:learn")
        val dueNotes = getNotesDueCount("deck:\"Kaishi 1.5k\" is:due -is:learn -is:new")
        if (newNotes > 0 || learnNotes > 0 || dueNotes > 0) { 
            return Triple(newNotes, learnNotes, dueNotes)
        }
        
        return Triple(0, 0, 0)
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
    
    fun getDueCount(deckIds: Set<Long>? = null): Int { 
        val decks = getDeckList()
        val filtered = if (deckIds == null || deckIds.isEmpty()) { 
            decks
        } else { 
            decks.filter { it.id in deckIds }
        }
        val sum = filtered.sumOf { it.totalDue }
        if (sum > 0) return sum
        val stats = getSelectedDeckStats()
        return stats.first + stats.second + stats.third
    }
    
    fun getDueDecksBreakdown(deckIds: Set<Long>? = null): List<DeckInfo> { 
        val decks = getDeckList()
        val filtered = if (deckIds == null || deckIds.isEmpty()) { 
            decks
        } else { 
            decks.filter { it.id in deckIds }
        }
        return filtered.filter { it.totalDue > 0 }
    }
    
    fun getNextDueCard(deckId: Long? = null, excludeNoteId: Long? = null): CardInfo? { 
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
                        getDeckList().find { it.id == deckId }?.name ?: getDeckNameForNote(noteId)
                    } else { 
                        getDeckNameForNote(noteId)
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
                    
                    return CardInfo( 
                        noteId = noteId, 
                        cardOrd = cardOrd, 
                        question = parsed.question, 
                        answer = parsed.answer, 
                        deckName = deckName.ifEmpty { "Kaishi 1.5k" }, 
                        buttonCount = buttonCount, 
                        nextReviewTimes = nextTimes, 
                        kanji = parsed.kanji, 
                        kanjiFurigana = parsed.kanjiFurigana, 
                        kanjiMeaning = parsed.kanjiMeaning, 
                        sentence = parsed.sentence, 
                        sentenceFurigana = parsed.sentenceFurigana, 
                        sentenceMeaning = parsed.sentenceMeaning, 
                        imageFileName = parsed.imageFileName, 
                        cardType = cardType
                    ) 
                } 
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        
        return null
    }
    
    fun getCardContent(noteId: Long): CardParsedContent { 
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
        return CardParsedContent()
    }
    
    fun parseCardContent(fields: String): CardParsedContent { 
        val rawParts = fields.split("\u001f")
        if (rawParts.isEmpty()) { 
            return CardParsedContent()
        }
        
        var detectedKanji = ""
        var detectedKanjiFurigana = ""
        var detectedKanjiMeaning = ""
        var detectedSentence = ""
        var detectedSentenceFurigana = ""
        var detectedSentenceMeaning = ""
        var detectedImage = ""
        
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
            
            detectedKanji = cleanVocab
            if (vocabWithFurigana.contains("[") && vocabWithFurigana.contains("]")) { 
                val rubyMatches = Regex("([^\\[\\s]+)\\[([^\\]]+)\\]").findAll(vocabWithFurigana)
                val combined = rubyMatches.joinToString("") { it.groupValues[2] }
                detectedKanjiFurigana = combined.ifEmpty { cleanHtml(vocabWithFurigana) }
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
                val rubyMatches = Regex("([^\\[\\s]+)\\[([^\\]]+)\\]").findAll(rawVocab)
                detectedKanjiFurigana = rubyMatches.joinToString("") { it.groupValues[2] }
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
        
        return CardParsedContent( 
            question = detectedKanji, 
            answer = detectedKanjiMeaning, 
            kanji = detectedKanji, 
            kanjiFurigana = detectedKanjiFurigana, 
            kanjiMeaning = detectedKanjiMeaning, 
            sentence = detectedSentence, 
            sentenceFurigana = detectedSentenceFurigana, 
            sentenceMeaning = detectedSentenceMeaning, 
            imageFileName = detectedImage
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
    
    private fun getDeckNameForNote(noteId: Long): String { 
        try { 
            val decks = getDeckList()
            if (decks.isNotEmpty()) return decks.first().name
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
    
    fun openAnkiDroidReviewer(): Boolean { 
        return try { 
            val intent = context.packageManager.getLaunchIntentForPackage(ANKI_PACKAGE)
            if (intent != null) { 
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else { 
                false
            }
        } catch (e: Exception) { 
            false
        }
    }
    
    companion object { 
        const val ANKI_PACKAGE = "com.ichi2.anki"
        const val PERMISSION_READ_WRITE_DATABASE = "com.ichi2.anki.permission.READ_WRITE_DATABASE"
        private const val AUTHORITY = "com.ichi2.anki.flashcards"
        
        val DECKS_URI: Uri = Uri.parse("content://$AUTHORITY/decks/")
        val SELECTED_DECK_URI: Uri = Uri.parse("content://$AUTHORITY/selected_deck/")
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
    }
}
