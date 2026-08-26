package com.ankilock.util
    
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.ankilock.data.CardInfo
import kotlin.math.max
import kotlin.math.min
    
object MediaArtworkGenerator { 
    
    private const val ARTWORK_SIZE = 800
    
    fun generateArtwork( 
        card: CardInfo?, 
        isRevealed: Boolean, 
        imageBitmap: Bitmap?
    ): Bitmap { 
        if (imageBitmap != null) { 
            return createScaledSquareBitmap(imageBitmap, ARTWORK_SIZE)
        }
        return createCardCanvasArtwork(card, isRevealed)
    }
    
    private fun createScaledSquareBitmap(source: Bitmap, targetSize: Int): Bitmap { 
        val output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val srcWidth = source.width
        val srcHeight = source.height
        val srcSize = min(srcWidth, srcHeight)
        val srcX = (srcWidth - srcSize) / 2
        val srcY = (srcHeight - srcSize) / 2
        val srcRect = Rect(srcX, srcY, srcX + srcSize, srcY + srcSize)
        val dstRect = Rect(0, 0, targetSize, targetSize)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(source, srcRect, dstRect, paint)
        return output
    }
    
    private fun createCardCanvasArtwork( 
        card: CardInfo?, 
        isRevealed: Boolean
    ): Bitmap { 
        val bitmap = Bitmap.createBitmap(ARTWORK_SIZE, ARTWORK_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val gradient = LinearGradient( 
            0f, 0f, ARTWORK_SIZE.toFloat(), ARTWORK_SIZE.toFloat(), 
            intArrayOf(Color.parseColor("#1C1A2E"), Color.parseColor("#2B203F"), Color.parseColor("#151828")), 
            floatArrayOf(0f, 0.5f, 1f), 
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply { 
            shader = gradient
        }
        canvas.drawRect(0f, 0f, ARTWORK_SIZE.toFloat(), ARTWORK_SIZE.toFloat(), bgPaint)
        
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#33FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawCircle(ARTWORK_SIZE * 0.85f, ARTWORK_SIZE * 0.15f, 180f, circlePaint)
        canvas.drawCircle(ARTWORK_SIZE * 0.15f, ARTWORK_SIZE * 0.85f, 140f, circlePaint)
        
        val deckName = card?.deckName?.ifEmpty { "AnkiLock" } ?: "AnkiLock"
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#2AFFFFFF")
            style = Paint.Style.FILL
        }
        val badgeTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#C8D6E5")
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        
        val badgeWidth = badgeTextPaint.measureText(deckName) + 60f
        val badgeHeight = 52f
        val badgeRect = RectF( 
            (ARTWORK_SIZE - badgeWidth) / 2f, 
            60f, 
            (ARTWORK_SIZE + badgeWidth) / 2f, 
            60f + badgeHeight
        )
        canvas.drawRoundRect(badgeRect, 26f, 26f, badgePaint)
        canvas.drawText( 
            deckName, 
            ARTWORK_SIZE / 2f, 
            60f + 36f, 
            badgeTextPaint
        )
        
        val kanjiText = card?.kanji?.ifEmpty { card.question } ?: "Review Deck"
        val kanjiFurigana = card?.kanjiFurigana ?: ""
        val kanjiMeaning = card?.kanjiMeaning?.ifEmpty { card.answer } ?: ""
        
        if (!isRevealed) { 
            val kanjiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.WHITE
                textSize = if (kanjiText.length > 5) 64f else 88f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText( 
                kanjiText, 
                ARTWORK_SIZE / 2f, 
                ARTWORK_SIZE / 2f + 20f, 
                kanjiPaint
            )
            
            val hintPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#A0ABC0")
                textSize = 32f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText( 
                "Tap Play to Reveal", 
                ARTWORK_SIZE / 2f, 
                ARTWORK_SIZE * 0.78f, 
                hintPaint
            )
        } else { 
            if (kanjiFurigana.isNotBlank()) { 
                val furiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                    color = Color.parseColor("#7EB6FF")
                    textSize = 38f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText( 
                    kanjiFurigana, 
                    ARTWORK_SIZE / 2f, 
                    ARTWORK_SIZE * 0.32f, 
                    furiPaint
                )
            }
            
            val kanjiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.WHITE
                textSize = if (kanjiText.length > 5) 64f else 80f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText( 
                kanjiText, 
                ARTWORK_SIZE / 2f, 
                ARTWORK_SIZE * 0.46f, 
                kanjiPaint
            )
            
            val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#40FFFFFF")
                strokeWidth = 2f
            }
            canvas.drawLine( 
                ARTWORK_SIZE * 0.25f, 
                ARTWORK_SIZE * 0.53f, 
                ARTWORK_SIZE * 0.75f, 
                ARTWORK_SIZE * 0.53f, 
                dividerPaint
            )
            
            if (kanjiMeaning.isNotBlank()) { 
                val meaningPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                    color = Color.parseColor("#E2E8F0")
                    textSize = 36f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }
                val cleanMeaning = kanjiMeaning.replace(Regex("<[^>]*>"), "")
                val textLayout = StaticLayout.Builder.obtain( 
                    cleanMeaning, 
                    0, 
                    cleanMeaning.length, 
                    meaningPaint, 
                    (ARTWORK_SIZE * 0.8f).toInt()
                ) 
                .setAlignment(Layout.Alignment.ALIGN_CENTER) 
                .setMaxLines(3) 
                .build()
                
                canvas.save()
                canvas.translate( 
                    ARTWORK_SIZE * 0.1f, 
                    ARTWORK_SIZE * 0.58f
                )
                textLayout.draw(canvas)
                canvas.restore()
            }
        }
        
        return bitmap
    }
}
