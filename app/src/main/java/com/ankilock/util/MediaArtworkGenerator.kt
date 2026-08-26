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
        
        var curY = 125f
        
        if (isRevealed && kanjiFurigana.isNotBlank()) { 
            val furiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#7EB6FF")
                textSize = 30f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText( 
                kanjiFurigana, 
                ARTWORK_SIZE / 2f, 
                curY, 
                furiPaint
            )
            curY += 46f
        } else { 
            curY += 36f
        }
        
        val kanjiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.WHITE
            textSize = if (kanjiText.length > 5) 56f else 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText( 
            kanjiText, 
            ARTWORK_SIZE / 2f, 
            curY + 12f, 
            kanjiPaint
        )
        curY += 54f
        
        if (isRevealed && cleanMeaning.isNotBlank()) { 
            val meaningPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#F1F5F9")
                textSize = if (cleanMeaning.length > 30) 28f else 32f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val meaningLayout = StaticLayout.Builder.obtain( 
                cleanMeaning, 
                0, 
                cleanMeaning.length, 
                meaningPaint, 
                (ARTWORK_SIZE * 0.84f).toInt()
            ) 
            .setAlignment(Layout.Alignment.ALIGN_CENTER) 
            .setMaxLines(2) 
            .build()
            
            canvas.save()
            canvas.translate( 
                ARTWORK_SIZE * 0.08f, 
                curY
            )
            meaningLayout.draw(canvas)
            canvas.restore()
            curY += meaningLayout.height + 14f
        } else { 
            curY += 16f
        }
        
        val dividerY = max(curY, 290f)
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#38FFFFFF")
            strokeWidth = 2f
        }
        canvas.drawLine( 
            ARTWORK_SIZE * 0.15f, 
            dividerY, 
            ARTWORK_SIZE * 0.85f, 
            dividerY, 
            dividerPaint
        )
        
        var sentenceSectionY = dividerY + 20f
        
        val rubyBitmap = if (isRevealed && sentenceFurigana.isNotBlank()) { 
            RubyTextRenderer.renderRubyBitmap( 
                context = context, 
                rawText = sentenceFurigana, 
                highlightWord = kanjiText, 
                baseTextSizeSp = 15f, 
                rubyTextSizeSp = 8.5f, 
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
            sentenceSectionY += rubyBitmap.height + 12f
        } else if (rawSentence.isNotBlank()) { 
            val sentencePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.WHITE
                textSize = 26f
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
            sentenceSectionY += sentenceLayout.height + 12f
        }
        
        if (isRevealed && cleanSentenceMeaning.isNotBlank()) { 
            val sentMeaningPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { 
                color = Color.parseColor("#CBD5E1")
                textSize = 23f
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
            sentenceSectionY += sentMeaningLayout.height + 12f
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
            val imgTop = min(sentenceSectionY + 8f, ARTWORK_SIZE - drawHeight - 50f)
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
        
        when (bgType) { 
            "anki_lock", "default" -> { 
                try { 
                    val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.anki_lock)
                    if (originalBitmap != null) { 
                        val blurred = if (radius > 0) ImageBlurUtil.fastBlur(originalBitmap, 0.25f, radius) else originalBitmap
                        val bmpW = blurred.width
                        val bmpH = blurred.height
                        val cropSize = min(bmpW, bmpH)
                        val cropX = (bmpW - cropSize) / 2
                        val cropY = (bmpH - cropSize) / 2
                        val srcRect = Rect(cropX, cropY, cropX + cropSize, cropY + cropSize)
                        val bmpPaint = Paint(Paint.FILTER_BITMAP_FLAG).apply { 
                            alpha = artworkAlpha
                        }
                        canvas.drawBitmap(blurred, srcRect, dstRect, bmpPaint)
                        
                        if (dimAlpha > 0) { 
                            val dimPaint = Paint().apply { 
                                color = Color.argb(dimAlpha, 0, 0, 0)
                            }
                            canvas.drawRect(dstRect, dimPaint)
                        }
                    }
                } catch (e: Exception) { 
                    e.printStackTrace()
                }
            }
            "custom" -> { 
                val uriStr = prefs.customImageUri
                if (!uriStr.isNullOrBlank()) { 
                    try { 
                        val uri = Uri.parse(uriStr)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val originalBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                        if (originalBitmap != null) { 
                            val blurred = ImageBlurUtil.fastBlur(originalBitmap, 0.25f, radius)
                            val bmpW = blurred.width
                            val bmpH = blurred.height
                            val cropSize = min(bmpW, bmpH)
                            val cropX = (bmpW - cropSize) / 2
                            val cropY = (bmpH - cropSize) / 2
                            val srcRect = Rect(cropX, cropY, cropX + cropSize, cropY + cropSize)
                            val bmpPaint = Paint(Paint.FILTER_BITMAP_FLAG).apply { 
                                alpha = artworkAlpha
                            }
                            canvas.drawBitmap(blurred, srcRect, dstRect, bmpPaint)
                            
                            if (dimAlpha > 0) { 
                                val dimPaint = Paint().apply { 
                                    color = Color.argb(dimAlpha, 0, 0, 0)
                                }
                                canvas.drawRect(dstRect, dimPaint)
                            }
                        }
                    } catch (e: Exception) { 
                        e.printStackTrace()
                    }
                }
            }
            "dark_blur", "sunset" -> { 
                val preset = ImageBlurUtil.createPresetBackground(bgType, ARTWORK_SIZE, ARTWORK_SIZE)
                val blurred = ImageBlurUtil.fastBlur(preset, 0.5f, radius)
                val srcRect = Rect(0, 0, blurred.width, blurred.height)
                val bmpPaint = Paint(Paint.FILTER_BITMAP_FLAG).apply { 
                    alpha = artworkAlpha
                }
                canvas.drawBitmap(blurred, srcRect, dstRect, bmpPaint)
                
                if (dimAlpha > 0) { 
                    val dimPaint = Paint().apply { 
                        color = Color.argb(dimAlpha, 0, 0, 0)
                    }
                    canvas.drawRect(dstRect, dimPaint)
                }
            }
            else -> { 
            }
        }
    }
}
