package com.ankilock.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextPaint
import kotlin.math.max
import kotlin.math.min

object RubyTextRenderer { 

    data class RubyToken( 
        val base: String, 
        val ruby: String? = null, 
        val isTarget: Boolean = false
    )

    private data class MeasuredToken( 
        val token: RubyToken, 
        val wBase: Float, 
        val wRuby: Float, 
        val totalWidth: Float
    )

    private fun isJapanese(text: String): Boolean { 
        return text.any { char -> 
            (char in '\u3040'..'\u309F') || 
            (char in '\u30A0'..'\u30FF') || 
            (char in '\u4E00'..'\u9FAF')
        }
    }

    private fun normalizeRubyString(input: String): String { 
        var text = input.replace(Regex("<ruby>([^<]+)<rt>([^<]+)</rt></ruby>"), "$1[$2]")
        text = text.replace("&nbsp;", " ") 
            .replace("&amp;", "&") 
            .replace("&lt;", "<") 
            .replace("&gt;", ">") 
            .replace(Regex("<[^>]*>"), "") 
        return text.trim()
    }

    fun parseRubyTokens(rawText: String, highlightWord: String = ""): List<RubyToken> { 
        val normalized = normalizeRubyString(rawText)
        if (normalized.isBlank()) return emptyList()

        val tokens = mutableListOf<RubyToken>()
        val regex = Regex("([^\\[\\s]+)\\[([^\\]]+)\\]")
        var lastIndex = 0

        val matches = regex.findAll(normalized)
        for (match in matches) { 
            val start = match.range.first
            val end = match.range.last + 1

            if (start > lastIndex) { 
                var plain = normalized.substring(lastIndex, start)
                if (plain.endsWith(" ") && plain.trim().isNotEmpty()) { 
                    val trimmed = plain.trimEnd()
                    if (isJapanese(trimmed)) { 
                        plain = trimmed
                    }
                } else if (plain == " ") { 
                    plain = ""
                }
                if (plain.isNotEmpty()) { 
                    tokens.add(RubyToken(base = plain, ruby = null, isTarget = false))
                }
            }

            val base = match.groupValues[1]
            val ruby = match.groupValues[2]
            tokens.add(RubyToken(base = base, ruby = ruby, isTarget = false))

            lastIndex = end
        }

        if (lastIndex < normalized.length) { 
            val tail = normalized.substring(lastIndex)
            if (tail.isNotEmpty()) { 
                tokens.add(RubyToken(base = tail, ruby = null, isTarget = false))
            }
        }

        val cleanHighlight = highlightWord.trim()
        if (cleanHighlight.isNotEmpty()) { 
            var i = 0
            while (i < tokens.size) { 
                var accumulated = ""
                var j = i
                while (j < tokens.size && accumulated.length < cleanHighlight.length) { 
                    accumulated += tokens[j].base
                    if (accumulated == cleanHighlight) { 
                        for (k in i..j) { 
                            tokens[k] = tokens[k].copy(isTarget = true)
                        }
                        break
                    }
                    j++
                }
                i++
            }
        }

        return tokens
    }

    fun renderRubyBitmap( 
        context: Context, 
        rawText: String, 
        highlightWord: String = "", 
        baseTextSizeSp: Float = 14f, 
        rubyTextSizeSp: Float = 9f, 
        baseTextColor: Int = Color.WHITE, 
        rubyTextColor: Int = Color.parseColor("#9AA0A6"), 
        highlightColor: Int = Color.parseColor("#8AB4F8"), 
        maxWidthPx: Int = 0, 
        isCentered: Boolean = true
    ): Bitmap? { 
        val tokens = parseRubyTokens(rawText, highlightWord)
        if (tokens.isEmpty()) return null

        val density = context.resources.displayMetrics.density
        val baseTextSizePx = baseTextSizeSp * density
        val rubyTextSizePx = rubyTextSizeSp * density

        val basePaint = TextPaint().apply { 
            isAntiAlias = true
            textSize = baseTextSizePx
            color = baseTextColor
            typeface = Typeface.DEFAULT
        }

        val rubyPaint = TextPaint().apply { 
            isAntiAlias = true
            textSize = rubyTextSizePx
            color = rubyTextColor
            typeface = Typeface.DEFAULT
        }

        val targetBasePaint = TextPaint(basePaint).apply { 
            color = highlightColor
            isFakeBoldText = true
        }

        val targetRubyPaint = TextPaint(rubyPaint).apply { 
            color = highlightColor
        }

        val baseMetrics = basePaint.fontMetrics
        val baseHeight = baseMetrics.descent - baseMetrics.ascent

        val rubyMetrics = rubyPaint.fontMetrics
        val rubyHeight = rubyMetrics.descent - rubyMetrics.ascent

        val hasAnyRuby = tokens.any { !it.ruby.isNullOrBlank() }
        val effectiveRubyHeight = if (hasAnyRuby) rubyHeight else 0f
        val rubyBaseGap = if (hasAnyRuby) 2f * density else 0f
        val lineSpacing = 4f * density
        val totalLineHeight = effectiveRubyHeight + rubyBaseGap + baseHeight + lineSpacing

        val measuredTokens = tokens.map { token -> 
            val bPaint = if (token.isTarget) targetBasePaint else basePaint
            val rPaint = if (token.isTarget) targetRubyPaint else rubyPaint
            val wBase = bPaint.measureText(token.base)
            val wRuby = if (!token.ruby.isNullOrBlank()) rPaint.measureText(token.ruby) else 0f
            val totalWidth = max(wBase, wRuby)
            MeasuredToken(token, wBase, wRuby, totalWidth)
        }

        val effectiveMaxWidth = if (maxWidthPx > 0) { 
            maxWidthPx
        } else { 
            (context.resources.displayMetrics.widthPixels * 0.82f).toInt()
        }

        val lines = mutableListOf<MutableList<MeasuredToken>>()
        var currentLine = mutableListOf<MeasuredToken>()
        var currentLineWidth = 0f

        for (mToken in measuredTokens) { 
            if (currentLineWidth + mToken.totalWidth > effectiveMaxWidth && currentLine.isNotEmpty()) { 
                lines.add(currentLine)
                currentLine = mutableListOf(mToken)
                currentLineWidth = mToken.totalWidth
            } else { 
                currentLine.add(mToken)
                currentLineWidth += mToken.totalWidth
            }
        }
        if (currentLine.isNotEmpty()) { 
            lines.add(currentLine)
        }

        val maxLineWidth = lines.maxOfOrNull { line -> 
            line.sumOf { it.totalWidth.toDouble() }.toFloat()
        } ?: 0f

        val bitmapWidth = max(1, min(effectiveMaxWidth, maxLineWidth.toInt() + (8f * density).toInt()))
        val bitmapHeight = max(1, (lines.size * totalLineHeight).toInt())

        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        var currentY = 0f
        for (line in lines) { 
            val lineWidth = line.sumOf { it.totalWidth.toDouble() }.toFloat()
            var currentX = if (isCentered) { 
                max(0f, (bitmapWidth - lineWidth) / 2f)
            } else { 
                2f * density
            }

            val rubyBaseline = currentY - rubyMetrics.ascent
            val baseBaseline = currentY + effectiveRubyHeight + rubyBaseGap - baseMetrics.ascent

            for (mToken in line) { 
                val t = mToken.token
                val bPaint = if (t.isTarget) targetBasePaint else basePaint
                val rPaint = if (t.isTarget) targetRubyPaint else rubyPaint

                val baseX = currentX + (mToken.totalWidth - mToken.wBase) / 2f
                canvas.drawText(t.base, baseX, baseBaseline, bPaint)

                if (!t.ruby.isNullOrBlank() && hasAnyRuby) { 
                    val rubyX = currentX + (mToken.totalWidth - mToken.wRuby) / 2f
                    canvas.drawText(t.ruby, rubyX, rubyBaseline, rPaint)
                }

                currentX += mToken.totalWidth
            }
            currentY += totalLineHeight
        }

        return bitmap
    }
}
