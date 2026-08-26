package com.ankilock.util
    
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.ankilock.R
import com.ankilock.data.CardInfo
import com.ankilock.data.PreferencesManager
import kotlin.math.max
import kotlin.math.min
    
object MediaArtworkGenerator { 
    
    private const val ARTWORK_SIZE = 800
    
    fun generateArtwork( 
        context: Context, 
        card: CardInfo?, 
        stats: Triple<Int, Int, Int>, 
        isRevealed: Boolean, 
        imageBitmap: Bitmap?
    ): Bitmap { 
        val prefs = PreferencesManager(context)
        val bitmap = Bitmap.createBitmap(ARTWORK_SIZE, ARTWORK_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        drawBackground(context, prefs, canvas)
        
        val deckName = card?.deckName?.ifEmpty { "Kaishi 1.5k" } ?: "All Caught Up"
        val deckPillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#38FFFFFF")
            style = Paint.Style.FILL
        }
        val deckTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#E2E8F0")
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        
        val deckWidth = deckTextPaint.measureText(deckName) + 36f
        val deckHeight = 44f
        val deckPillLeft = ARTWORK_SIZE - 36f - deckWidth
        val deckRect = RectF( 
            deckPillLeft, 
            36f, 
            deckPillLeft + deckWidth, 
            36f + deckHeight
        )
        canvas.drawRoundRect(deckRect, 22f, 22f, deckPillPaint)
        canvas.drawText( 
            deckName, 
            deckPillLeft + deckWidth / 2f, 
            36f + 30f, 
            deckTextPaint
        )
        
        val cardType = card?.cardType ?: 0
        val newC = stats.first
        val learnC = stats.second
        val revC = stats.third
        
        val newText = "$newC"
        val learnText = "$learnC"
        val revText = "$revC"
        val dotText = " · "
        
        val newPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#8AB4F8")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val dotPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#94A3B8")
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val learnPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#F28B82")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val revPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#81C995")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        val wNew = newPaint.measureText(newText)
        val wDot1 = dotPaint.measureText(dotText)
        val wLearn = learnPaint.measureText(learnText)
        val wDot2 = dotPaint.measureText(dotText)
        val wRev = revPaint.measureText(revText)
        
        val totalStatsWidth = wNew + wDot1 + wLearn + wDot2 + wRev
        val statsPillWidth = totalStatsWidth + 36f
        val statsPillLeft = 36f
        val statsRect = RectF( 
            statsPillLeft, 
            36f, 
            statsPillLeft + statsPillWidth, 
            36f + deckHeight
        )
        val statsPillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#38FFFFFF")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(statsRect, 22f, 22f, statsPillPaint)
        
        var curStatsX = statsPillLeft + 18f
        val statsY = 36f + 30f
        
        canvas.drawText(newText, curStatsX, statsY, newPaint)
        if (cardType == 0 && card != null) { 
            val underlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#8AB4F8")
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect( 
                RectF(curStatsX, statsY + 5f, curStatsX + wNew, statsY + 9f), 
                2f, 
                2f, 
                underlinePaint
            )
        }
        curStatsX += wNew
        
        canvas.drawText(dotText, curStatsX, statsY, dotPaint)
        curStatsX += wDot1
        
        canvas.drawText(learnText, curStatsX, statsY, learnPaint)
        if (cardType == 1 && card != null) { 
            val underlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#F28B82")
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect( 
                RectF(curStatsX, statsY + 5f, curStatsX + wLearn, statsY + 9f), 
                2f, 
                2f, 
                underlinePaint
            )
        }
        curStatsX += wLearn
        
        canvas.drawText(dotText, curStatsX, statsY, dotPaint)
        curStatsX += wDot2
        
        canvas.drawText(revText, curStatsX, statsY, revPaint)
        if (cardType == 2 && card != null) { 
            val underlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#81C995")
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect( 
                RectF(curStatsX, statsY + 5f, curStatsX + wRev, statsY + 9f), 
                2f, 
                2f, 
                underlinePaint
            )
        }
        
        if (card == null) { 
            drawCongratulations(canvas)
            return bitmap
        }
        
        val kanjiText = card.kanji.ifEmpty { card.question }
        val kanjiFurigana = card.kanjiFurigana
        val kanjiMeaning = card.kanjiMeaning.ifEmpty { card.answer }
        val cleanMeaning = kanjiMeaning.replace(Regex("<[^>]*>"), "").trim()
        val rawSentence = card.sentence.replace(Regex("<[^>]*>"), "").trim()
        val sentenceFurigana = card.sentenceFurigana
        val sentenceMeaning = card.sentenceMeaning
        val cleanSentenceMeaning = sentenceMeaning.replace(Regex("<[^>]*>"), "").trim()
        
        val vocabRubyText = if (isRevealed && kanjiFurigana.isNotBlank()) { 
            RubyTextRenderer.buildRubyVocab(kanjiText, kanjiFurigana)
        } else { 
            kanjiText
        }
        
        val vocabRubyBmp = RubyTextRenderer.renderRubyBitmapPx( 
            context = context, 
            rawText = vocabRubyText, 
            baseTextSizePx = if (kanjiText.length > 5) 64f else 78f, 
            rubyTextSizePx = if (isRevealed) 30f else 0f, 
            baseTextColor = Color.WHITE, 
            rubyTextColor = Color.parseColor("#7EB6FF"), 
            highlightColor = Color.WHITE, 
            maxWidthPx = (ARTWORK_SIZE * 0.88f).toInt(), 
            isCentered = true, 
            isBold = true
        )
        val vocabH = vocabRubyBmp?.height?.toFloat() ?: 80f
        
        val meaningLayout = if (isRevealed && cleanMeaning.isNotBlank()) { 
            val meaningPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#F1F5F9")
                textSize = if (cleanMeaning.length > 30) 32f else 36f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            StaticLayout.Builder.obtain( 
                cleanMeaning, 
                0, 
                cleanMeaning.length, 
                meaningPaint, 
                (ARTWORK_SIZE * 0.84f).toInt()
            ) 
            .setAlignment(Layout.Alignment.ALIGN_CENTER) 
            .setMaxLines(2) 
            .build()
        } else { 
            null
        }
        val meaningH = meaningLayout?.height?.toFloat() ?: 0f
        
        val dividerGapTop = 16f
        val dividerGapBottom = 18f
        val dividerH = dividerGapTop + 2f + dividerGapBottom
        
        val sentenceRubyBmp = if (isRevealed && sentenceFurigana.isNotBlank()) { 
            RubyTextRenderer.renderRubyBitmapPx( 
                context = context, 
                rawText = sentenceFurigana, 
                highlightWord = kanjiText, 
                baseTextSizePx = 42f, 
                rubyTextSizePx = 20f, 
                baseTextColor = Color.WHITE, 
                rubyTextColor = Color.parseColor("#90CAF9"), 
                highlightColor = Color.parseColor("#8AB4F8"), 
                maxWidthPx = (ARTWORK_SIZE * 0.86f).toInt(), 
                isCentered = true, 
                isBold = false
            )
        } else { 
            null
        }
        
        val sentenceLayout = if (sentenceRubyBmp == null && rawSentence.isNotBlank()) { 
            val sentencePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.WHITE
                textSize = 42f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            StaticLayout.Builder.obtain( 
                rawSentence, 
                0, 
                rawSentence.length, 
                sentencePaint, 
                (ARTWORK_SIZE * 0.84f).toInt()
            ) 
            .setAlignment(Layout.Alignment.ALIGN_CENTER) 
            .setMaxLines(3) 
            .build()
        } else { 
            null
        }
        val sentenceH = sentenceRubyBmp?.height?.toFloat() 
            ?: sentenceLayout?.height?.toFloat() 
            ?: 0f
        
        val sentMeaningLayout = if (isRevealed && cleanSentenceMeaning.isNotBlank()) { 
            val sentMeaningPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#CBD5E1")
                textSize = 30f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            StaticLayout.Builder.obtain( 
                cleanSentenceMeaning, 
                0, 
                cleanSentenceMeaning.length, 
                sentMeaningPaint, 
                (ARTWORK_SIZE * 0.84f).toInt()
            ) 
            .setAlignment(Layout.Alignment.ALIGN_CENTER) 
            .setMaxLines(3) 
            .build()
        } else { 
            null
        }
        val sentMeaningH = sentMeaningLayout?.height?.toFloat() ?: 0f
        
        val maxImgWidth = (ARTWORK_SIZE * 0.38f).toInt()
        val maxImgHeight = 100f
        val imgDrawWidth: Float
        val imgDrawHeight: Float
        if (imageBitmap != null) { 
            val imgAspect = imageBitmap.width.toFloat() / max(1, imageBitmap.height).toFloat()
            if (imgAspect > 1f) { 
                imgDrawWidth = min(maxImgWidth.toFloat(), imageBitmap.width.toFloat())
                imgDrawHeight = imgDrawWidth / imgAspect
            } else { 
                imgDrawHeight = min(maxImgHeight, imageBitmap.height.toFloat())
                imgDrawWidth = imgDrawHeight * imgAspect
            }
        } else { 
            imgDrawWidth = 0f
            imgDrawHeight = 0f
        }
        
        val contentTop = 92f
        val contentBottom = 736f
        val availableH = contentBottom - contentTop
        
        var totalContentH = vocabH
        if (meaningH > 0f) totalContentH += 12f + meaningH
        totalContentH += dividerH
        totalContentH += sentenceH
        if (sentMeaningH > 0f) totalContentH += 14f + sentMeaningH
        if (imgDrawHeight > 0f) totalContentH += 16f + imgDrawHeight
        
        val startY = contentTop + max(0f, (availableH - totalContentH) / 2f)
        var curY = startY
        
        if (vocabRubyBmp != null) { 
            val vocabX = (ARTWORK_SIZE - vocabRubyBmp.width) / 2f
            canvas.drawBitmap(vocabRubyBmp, vocabX, curY, null)
            curY += vocabH
        }
        
        if (meaningLayout != null) { 
            curY += 12f
            canvas.save()
            canvas.translate(ARTWORK_SIZE * 0.08f, curY)
            meaningLayout.draw(canvas)
            canvas.restore()
            curY += meaningH
        }
        
        curY += dividerGapTop
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#38FFFFFF")
            strokeWidth = 2f
        }
        canvas.drawLine( 
            ARTWORK_SIZE * 0.15f, 
            curY, 
            ARTWORK_SIZE * 0.85f, 
            curY, 
            dividerPaint
        )
        curY += 2f + dividerGapBottom
        
        if (sentenceRubyBmp != null) { 
            val sentenceX = (ARTWORK_SIZE - sentenceRubyBmp.width) / 2f
            canvas.drawBitmap(sentenceRubyBmp, sentenceX, curY, null)
            curY += sentenceH
        } else if (sentenceLayout != null) { 
            canvas.save()
            canvas.translate(ARTWORK_SIZE * 0.08f, curY)
            sentenceLayout.draw(canvas)
            canvas.restore()
            curY += sentenceH
        }
        
        if (sentMeaningLayout != null) { 
            curY += 14f
            canvas.save()
            canvas.translate(ARTWORK_SIZE * 0.08f, curY)
            sentMeaningLayout.draw(canvas)
            canvas.restore()
            curY += sentMeaningH
        }
        
        if (imageBitmap != null && imgDrawHeight > 0f) { 
            curY += 16f
            val imgLeft = (ARTWORK_SIZE - imgDrawWidth) / 2f
            val dstRect = RectF(imgLeft, curY, imgLeft + imgDrawWidth, curY + imgDrawHeight)
            val imgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.save()
            val clipPath = android.graphics.Path().apply { 
                addRoundRect(dstRect, 14f, 14f, android.graphics.Path.Direction.CW)
            }
            canvas.clipPath(clipPath)
            canvas.drawBitmap(imageBitmap, null, dstRect, imgPaint)
            canvas.restore()
        }
        
        val hintText = if (!isRevealed) "|◀ Again   •   ▶ Reveal   •   ▶| Good" else "|◀ Again   •   ❚❚ Hide   •   ▶| Good"
        val hintPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#94A3B8")
            textSize = 21f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText( 
            hintText, 
            ARTWORK_SIZE / 2f, 
            ARTWORK_SIZE - 28f, 
            hintPaint
        )
        
        return bitmap
    }
    
    private fun drawCongratulations(canvas: Canvas) { 
        val congratsPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.WHITE
            textSize = 62f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText( 
            "お疲れ様でした！", 
            ARTWORK_SIZE / 2f, 
            ARTWORK_SIZE * 0.35f, 
            congratsPaint
        )
        
        val subPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#F1F5F9")
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText( 
            "All reviews complete for today! 🎉", 
            ARTWORK_SIZE / 2f, 
            ARTWORK_SIZE * 0.44f, 
            subPaint
        )
        
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#38FFFFFF")
            strokeWidth = 2f
        }
        canvas.drawLine( 
            ARTWORK_SIZE * 0.15f, 
            ARTWORK_SIZE * 0.50f, 
            ARTWORK_SIZE * 0.85f, 
            ARTWORK_SIZE * 0.50f, 
            dividerPaint
        )
        
        val cheerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#CBD5E1")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText( 
            "また明日頑張りましょう！", 
            ARTWORK_SIZE / 2f, 
            ARTWORK_SIZE * 0.58f, 
            cheerPaint
        )
        
        val tomorrowPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#94A3B8")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText( 
            "Come back tomorrow for your next cards.", 
            ARTWORK_SIZE / 2f, 
            ARTWORK_SIZE * 0.65f, 
            tomorrowPaint
        )
        
        val hintPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#8AB4F8")
            textSize = 21f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText( 
            "• Tap Open Anki to Study Ahead •", 
            ARTWORK_SIZE / 2f, 
            ARTWORK_SIZE - 28f, 
            hintPaint
        )
    }
    
    private fun drawBackground(context: Context, prefs: PreferencesManager, canvas: Canvas) { 
        val bgType = prefs.backgroundType
        val radius = prefs.blurRadius
        val dimAlpha = (prefs.dimOpacity * 255).toInt().coerceIn(0, 255)
        val artworkAlpha = (prefs.artworkOpacity * 255).toInt().coerceIn(0, 255)
        val dstRect = RectF(0f, 0f, ARTWORK_SIZE.toFloat(), ARTWORK_SIZE.toFloat())
        
        val bitmapToDraw: Bitmap? = when (bgType) { 
            "anki_lock", "default" -> { 
                try { 
                    val original = BitmapFactory.decodeResource(context.resources, R.drawable.anki_lock)
                    if (original != null && radius > 0) ImageBlurUtil.fastBlur(original, 0.25f, radius) else original
                } catch (e: Exception) { 
                    null
                }
            }
            "custom" -> { 
                val uriStr = prefs.customImageUri
                if (!uriStr.isNullOrBlank()) { 
                    try { 
                        val uri = Uri.parse(uriStr)
                        context.contentResolver.openInputStream(uri)?.use { stream -> 
                            val original = BitmapFactory.decodeStream(stream)
                            if (original != null) ImageBlurUtil.fastBlur(original, 0.25f, radius) else null
                        }
                    } catch (e: Exception) { 
                        null
                    }
                } else null
            }
            "dark_blur", "sunset" -> { 
                val preset = ImageBlurUtil.createPresetBackground(bgType, ARTWORK_SIZE, ARTWORK_SIZE)
                ImageBlurUtil.fastBlur(preset, 0.5f, radius)
            }
            else -> null
        }
        
        if (bitmapToDraw != null) { 
            drawCroppedBitmapWithOverlay(canvas, bitmapToDraw, artworkAlpha, dimAlpha, dstRect)
        }
    }
    
    private fun drawCroppedBitmapWithOverlay( 
        canvas: Canvas, 
        bitmap: Bitmap, 
        artworkAlpha: Int, 
        dimAlpha: Int, 
        dstRect: RectF
    ) { 
        val bmpW = bitmap.width
        val bmpH = bitmap.height
        val cropSize = min(bmpW, bmpH)
        val cropX = (bmpW - cropSize) / 2
        val cropY = (bmpH - cropSize) / 2
        val srcRect = Rect(cropX, cropY, cropX + cropSize, cropY + cropSize)
        val bmpPaint = Paint(Paint.FILTER_BITMAP_FLAG).apply { 
            alpha = artworkAlpha
        }
        canvas.drawBitmap(bitmap, srcRect, dstRect, bmpPaint)
        
        if (dimAlpha > 0) { 
            val dimPaint = Paint().apply { 
                color = Color.argb(dimAlpha, 0, 0, 0)
            }
            canvas.drawRect(dstRect, dimPaint)
        }
    }
}
