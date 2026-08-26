package com.ankilock.anki
    
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import com.ankilock.data.CardInfo
import com.ankilock.data.DeckInfo
import org.json.JSONArray
    
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
        return try { 
            resolver.query(DECKS_URI, null, null, null, null)?.use { 
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
        try { 
            resolver.query( 
                DECKS_URI, 
                null, 
                null, 
                null, 
                null
            )?.use { cursor ->
                val nameIdx = cursor.getColumnIndex(COL_DECK_NAME)
                val idIdx = cursor.getColumnIndex(COL_DECK_ID)
                val countsIdx = cursor.getColumnIndex(COL_DECK_COUNTS)
                
                while (cursor.moveToNext()) { 
                    val name = cursor.getString(nameIdx) ?: continue
                    val id = cursor.getLong(idIdx)
                    val countsJson = cursor.getString(countsIdx) ?: "[0,0,0]"
                    val counts = JSONArray(countsJson)
                    
                    decks.add( 
                        DeckInfo( 
                            id = id, 
                            name = name, 
                            newCount = counts.optInt(0, 0), 
                            learnCount = counts.optInt(1, 0), 
                            reviewCount = counts.optInt(2, 0)
                        )
                    )
                }
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        return decks
    }
    
    fun getDueCount(deckIds: Set<Long>? = null): Int { 
        val decks = getDeckList()
        return if (deckIds == null || deckIds.isEmpty()) { 
            decks.sumOf { it.totalDue }
        } else { 
            decks.filter { it.id in deckIds }.sumOf { it.totalDue }
        }
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
    
    fun getNextDueCard(deckId: Long? = null): CardInfo? { 
        try { 
            val selectionArgs = if (deckId != null) { 
                arrayOf(deckId.toString())
            } else { 
                null
            }
            val selection = if (deckId != null) "deckID=?" else null
            
            resolver.query( 
                SCHEDULE_URI, 
                null, 
                selection, 
                selectionArgs, 
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) { 
                    val noteId = cursor.getLong( 
                        cursor.getColumnIndexOrThrow(COL_NOTE_ID)
                    )
                    val cardOrd = cursor.getInt( 
                        cursor.getColumnIndexOrThrow(COL_CARD_ORD)
                    )
                    val buttonCount = cursor.getInt( 
                        cursor.getColumnIndexOrThrow(COL_BUTTON_COUNT)
                    )
                    val nextTimes = cursor.getString( 
                        cursor.getColumnIndexOrThrow(COL_NEXT_REVIEW_TIMES)
                    ) ?: ""
                    
                    val cardContent = getCardContent(noteId)
                    
                    return CardInfo( 
                        noteId = noteId, 
                        cardOrd = cardOrd, 
                        question = cardContent.first, 
                        answer = cardContent.second, 
                        deckName = getDeckNameForNote(noteId), 
                        buttonCount = buttonCount, 
                        nextReviewTimes = nextTimes
                    )
                }
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        return null
    }
    
    private fun getCardContent(noteId: Long): Pair<String, String> { 
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
                        val parts = fields.split("\u001f")
                        val question = if (parts.isNotEmpty()) parts[0] else ""
                        val answer = if (parts.size > 1) parts[1] else ""
                        return Pair(question, answer)
                    }
                }
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        return Pair("", "")
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
        private const val AUTHORITY = "com.ichi2.anki.flashcards"
        
        val DECKS_URI: Uri = Uri.parse("content://$AUTHORITY/decks")
        val SCHEDULE_URI: Uri = Uri.parse("content://$AUTHORITY/schedule")
        val NOTES_URI: Uri = Uri.parse("content://$AUTHORITY/notes")
        
        private const val COL_DECK_NAME = "deck_name"
        private const val COL_DECK_ID = "deck_id"
        private const val COL_DECK_COUNTS = "deck_counts"
        private const val COL_NOTE_ID = "note_id"
        private const val COL_CARD_ORD = "card_ord"
        private const val COL_BUTTON_COUNT = "button_count"
        private const val COL_NEXT_REVIEW_TIMES = "next_review_times"
        private const val COL_FLDS = "flds"
        private const val COL_EASE = "ease"
        private const val COL_TIME_TAKEN = "time_taken"
    }
}
