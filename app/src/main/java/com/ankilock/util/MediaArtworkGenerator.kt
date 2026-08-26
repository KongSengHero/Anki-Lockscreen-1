package com.ankilock.util
    
import android.content.Context
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
        context: Context, 
        card: CardInfo?, 
        isRevealed: Boolean, 
        imageBitmap: Bitmap?
    ): Bitmap { 
        return createCardCanvasArtwork(context, card, isRevealed, imageBitmap)
    }
    
    private fun createCardCanvasArtwork( 
        context: Context, 
        card: CardInfo?, 
        isRevealed: Boolean, 
        imageBitmap: Bitmap?
    ): Bitmap { 
        val bitmap = Bitmap.createBitmap(ARTWORK_SIZE, ARTWORK_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val gradient = LinearGradient( 
            0f, 0f, ARTWORK_SIZE.toFloat(), ARTWORK_SIZE.toFloat(), 
            intArrayOf(Color.parseColor("#141322"), Color.parseColor("#221D38"), Color.parseColor("#10111A")), 
            floatArrayOf(0f, 0.5f, 1f), 
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply { 
            shader = gradient
        }
        canvas.drawRect(0f, 0f, ARTWORK_SIZE.toFloat(), ARTWORK_SIZE.toFloat(), bgPaint)
        
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#20FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawCircle(ARTWORK_SIZE * 0.85f, ARTWORK_SIZE * 0.12f, 160f, circlePaint)
        canvas.drawCircle(ARTWORK_SIZE * 0.15f, ARTWORK_SIZE * 0.88f, 130f, circlePaint)
        
        val cardBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#15FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val borderRect = RectF(16f, 16f, ARTWORK_SIZE - 16f, ARTWORK_SIZE - 16f)
        canvas.drawRoundRect(borderRect, 28f, 28f, cardBorderPaint)
        
        val deckName = card?.deckName?.ifEmpty { "Kaishi 1.5k" } ?: "Kaishi 1.5k"
        val deckPillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#24FFFFFF")
            style = Paint.Style.FILL
        }
        val deckTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#C8D6E5")
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        
        val deckWidth = deckTextPaint.measureText(deckName) + 40f
        val deckHeight = 44f
        val deckRect = RectF( 
            40f, 
            40f, 
            40f + deckWidth, 
            40f + deckHeight
        )
        canvas.drawRoundRect(deckRect, 22f, 22f, deckPillPaint)
        canvas.drawText( 
            deckName, 
            40f + deckWidth / 2f, 
            40f + 31f, 
            deckTextPaint
        )
        
        val cardType = card?.cardType ?: 0
        val (badgeLabel, badgeTextColor, badgeBgColor, badgeBorderColor) = when (cardType) { 
            1 -> Quad("LEARN", Color.parseColor("#EF5350"), Color.parseColor("#3B181E"), Color.parseColor("#EF5350"))
            2 -> Quad("REVIEW", Color.parseColor("#66BB6A"), Color.parseColor("#163320"), Color.parseColor("#66BB6A"))
            else -> Quad("NEW", Color.parseColor("#42A5F5"), Color.parseColor("#162842"), Color.parseColor("#42A5F5"))
        }
        
        val badgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = badgeBgColor
            style = Paint.Style.FILL
        }
        val badgeStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = badgeBorderColor
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val badgeTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = badgeTextColor
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        
        val badgeWidth = badgeTextPaint.measureText(badgeLabel) + 36f
        val badgeHeight = 44f
        val badgeLeft = ARTWORK_SIZE - 40f - badgeWidth
        val badgeRect = RectF( 
            badgeLeft, 
            40f, 
            badgeLeft + badgeWidth, 
            40f + badgeHeight
        )
        canvas.drawRoundRect(badgeRect, 22f, 22f, badgeBgPaint)
        canvas.drawRoundRect(badgeRect, 22f, 22f, badgeStrokePaint)
        canvas.drawText( 
            badgeLabel, 
            badgeLeft + badgeWidth / 2f, 
            40f + 31f, 
            badgeTextPaint
        )
        
        val kanjiText = card?.kanji?.ifEmpty { card.question } ?: "Review Deck"
        val kanjiFurigana = card?.kanjiFurigana ?: ""
        val kanjiMeaning = card?.kanjiMeaning?.ifEmpty { card.answer } ?: ""
        val cleanMeaning = kanjiMeaning.replace(Regex("<[^>]*>"), "").trim()
        val rawSentence = card?.sentence?.replace(Regex("<[^>]*>"), "")?.trim() ?: ""
        val sentenceFurigana = card?.sentenceFurigana ?: ""
        val sentenceMeaning = card?.sentenceMeaning ?: ""
        val cleanSentenceMeaning = sentenceMeaning.replace(Regex("<[^>]*>"), "").trim()
        
        if (!isRevealed) { 
            val kanjiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.WHITE
                textSize = if (kanjiText.length > 5) 66f else 86f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText( 
                kanjiText, 
                ARTWORK_SIZE / 2f, 
                ARTWORK_SIZE * 0.38f, 
                kanjiPaint
            )
            
            if (rawSentence.isNotBlank()) { 
                val sentencePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                    color = Color.parseColor("#E2E8F0")
                    textSize = 30f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }
                val sentenceLayout = StaticLayout.Builder.obtain( 
                    rawSentence, 
                    0, 
                    rawSentence.length, 
                    sentencePaint, 
                    (ARTWORK_SIZE * 0.84f).toInt()
                ) 
                .setAlignment(Layout.Alignment.ALIGN_CENTER) 
                .setMaxLines(3) 
                .build()
                
                canvas.save()
                canvas.translate( 
                    ARTWORK_SIZE * 0.08f, 
                    ARTWORK_SIZE * 0.52f
                )
                sentenceLayout.draw(canvas)
                canvas.restore()
            }
            
            val hintPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#94A3B8")
                textSize = 26f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText( 
                "Tap Play to Reveal Answer", 
                ARTWORK_SIZE / 2f, 
                ARTWORK_SIZE * 0.82f, 
                hintPaint
            )
        } else { 
            var topY = 145f
            
            if (kanjiFurigana.isNotBlank()) { 
                val furiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                    color = Color.parseColor("#7EB6FF")
                    textSize = 30f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText( 
                    kanjiFurigana, 
                    ARTWORK_SIZE / 2f, 
                    topY, 
                    furiPaint
                )
                topY += 45f
            } else { 
                topY += 25f
            }
            
            val kanjiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.WHITE
                textSize = if (kanjiText.length > 5) 54f else 68f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText( 
                kanjiText, 
                ARTWORK_SIZE / 2f, 
                topY + 15f, 
                kanjiPaint
            )
            topY += 55f
            
            if (cleanMeaning.isNotBlank()) { 
                val meaningPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                    color = Color.parseColor("#CBD5E1")
                    textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }
                val meaningLayout = StaticLayout.Builder.obtain( 
                    cleanMeaning, 
                    0, 
                    cleanMeaning.length, 
                    meaningPaint, 
                    (ARTWORK_SIZE * 0.82f).toInt()
                ) 
                .setAlignment(Layout.Alignment.ALIGN_CENTER) 
                .setMaxLines(2) 
                .build()
                
                canvas.save()
                canvas.translate( 
                    ARTWORK_SIZE * 0.09f, 
                    topY
                )
                meaningLayout.draw(canvas)
                canvas.restore()
                topY += meaningLayout.height + 15f
            } else { 
                topY += 15f
            }
            
            val dividerY = max(topY, 305f)
            val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#33FFFFFF")
                strokeWidth = 2f
            }
            canvas.drawLine( 
                ARTWORK_SIZE * 0.15f, 
                dividerY, 
                ARTWORK_SIZE * 0.85f, 
                dividerY, 
                dividerPaint
            )
            
            var sentenceSectionY = dividerY + 25f
            
            val rubyBitmap = if (sentenceFurigana.isNotBlank()) { 
                RubyTextRenderer.renderRubyBitmap( 
                    context = context, 
                    rawText = sentenceFurigana, 
                    highlightWord = kanjiText, 
                    baseTextSizeSp = 16f, 
                    rubyTextSizeSp = 9f, 
                    baseTextColor = Color.WHITE, 
                    rubyTextColor = Color.parseColor("#90CAF9"), 
                    highlightColor = Color.parseColor("#8AB4F8"), 
                    maxWidthPx = (ARTWORK_SIZE * 0.86f).toInt(), 
                    isCentered = true
                )
            } else { 
                null
            }
            
            if (rubyBitmap != null) { 
                val rubyX = (ARTWORK_SIZE - rubyBitmap.width) / 2f
                canvas.drawBitmap(rubyBitmap, rubyX, sentenceSectionY, Paint(Paint.ANTI_ALIAS_FLAG))
                sentenceSectionY += rubyBitmap.height + 18f
            } else if (rawSentence.isNotBlank()) { 
                val sentencePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                    color = Color.WHITE
                    textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }
                val sentenceLayout = StaticLayout.Builder.obtain( 
                    rawSentence, 
                    0, 
                    rawSentence.length, 
                    sentencePaint, 
                    (ARTWORK_SIZE * 0.84f).toInt()
                ) 
                .setAlignment(Layout.Alignment.ALIGN_CENTER) 
                .setMaxLines(3) 
                .build()
                
                canvas.save()
                canvas.translate( 
                    ARTWORK_SIZE * 0.08f, 
                    sentenceSectionY
                )
                sentenceLayout.draw(canvas)
                canvas.restore()
                sentenceSectionY += sentenceLayout.height + 18f
            }
            
            if (cleanSentenceMeaning.isNotBlank()) { 
                val sentMeaningPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                    color = Color.parseColor("#94A3B8")
                    textSize = 24f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }
                val sentMeaningLayout = StaticLayout.Builder.obtain( 
                    cleanSentenceMeaning, 
                    0, 
                    cleanSentenceMeaning.length, 
                    sentMeaningPaint, 
                    (ARTWORK_SIZE * 0.84f).toInt()
                ) 
                .setAlignment(Layout.Alignment.ALIGN_CENTER) 
                .setMaxLines(3) 
                .build()
                
                canvas.save()
                canvas.translate( 
                    ARTWORK_SIZE * 0.08f, 
                    sentenceSectionY
                )
                sentMeaningLayout.draw(canvas)
                canvas.restore()
                sentenceSectionY += sentMeaningLayout.height + 15f
            }
            
            if (imageBitmap != null) { 
                val maxImgWidth = (ARTWORK_SIZE * 0.40f).toInt()
                val maxImgHeight = 110
                val imgAspect = imageBitmap.width.toFloat() / max(1, imageBitmap.height).toFloat()
                val drawWidth: Float
                val drawHeight: Float
                if (imgAspect > 1f) { 
                    drawWidth = min(maxImgWidth.toFloat(), imageBitmap.width.toFloat())
                    drawHeight = drawWidth / imgAspect
                } else { 
                    drawHeight = min(maxImgHeight.toFloat(), imageBitmap.height.toFloat())
                    drawWidth = drawHeight * imgAspect
                }
                val imgLeft = (ARTWORK_SIZE - drawWidth) / 2f
                val imgTop = min(sentenceSectionY + 10f, ARTWORK_SIZE - drawHeight - 25f)
                val dstRect = RectF(imgLeft, imgTop, imgLeft + drawWidth, imgTop + drawHeight)
                val imgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                canvas.save()
                val clipPath = android.graphics.Path().apply { 
                    addRoundRect(dstRect, 14f, 14f, android.graphics.Path.Direction.CW)
                }
                canvas.clipPath(clipPath)
                canvas.drawBitmap(imageBitmap, null, dstRect, imgPaint)
                canvas.restore()
            }
        }
        
        return bitmap
    }
    
    private data class Quad<A, B, C, D>( 
        val first: A, 
        val second: B, 
        val third: C, 
        val fourth: D
    )
}
