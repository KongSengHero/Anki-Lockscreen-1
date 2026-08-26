package com.ankilock.util

object FuriganaGridFormatter { 

    data class AlignedSentence( 
        val furiganaHtml: String, 
        val sentenceHtml: String
    )

    private fun normalizeRubyString(input: String): String { 
        var text = input.replace(Regex("<ruby>([^<]+)<rt>([^<]+)</rt></ruby>"), "$1[$2]")
        text = text.replace("&nbsp;", " ") 
            .replace("&amp;", "&") 
            .replace("&lt;", "<") 
            .replace("&gt;", ">") 
            .replace(Regex("<[^>]*>"), "") 
        return text.trim()
    }

    fun formatAlignedGrid( 
        rawSentenceFurigana: String, 
        highlightKanji: String = "", 
        minSlotWidth: Int = 3
    ): AlignedSentence { 
        val normalized = normalizeRubyString(rawSentenceFurigana)
        if (normalized.isBlank()) return AlignedSentence("", "")

        val topBuilder = StringBuilder()
        val bottomBuilder = StringBuilder()

        val regex = Regex("([^\\[\\s]+)\\[([^\\]]+)\\]")
        var lastIndex = 0

        val cleanHighlight = highlightKanji.trim()
        val matches = regex.findAll(normalized).toList()

        for (match in matches) { 
            val start = match.range.first
            val end = match.range.last + 1

            if (start > lastIndex) { 
                var plain = normalized.substring(lastIndex, start)
                plain = plain.replace(" ", "")
                for (char in plain) { 
                    val charStr = char.toString()
                    val isTarget = cleanHighlight.isNotEmpty() && cleanHighlight.contains(charStr)

                    val topSlot = "\u3000".repeat(minSlotWidth)
                    val bottomSlot = charStr + "\u3000".repeat(minSlotWidth - 1)

                    topBuilder.append(topSlot)
                    if (isTarget) { 
                        bottomBuilder.append("<font color='#8AB4F8'><b>$bottomSlot</b></font>")
                    } else { 
                        bottomBuilder.append(bottomSlot)
                    }
                }
            }

            val base = match.groupValues[1]
            val ruby = match.groupValues[2]

            val isTarget = cleanHighlight.isNotEmpty() && (cleanHighlight.contains(base) || base.contains(cleanHighlight))
            val slotWidth = maxOf(minSlotWidth * base.length, ruby.length)

            val topPadded = ruby + "\u3000".repeat(slotWidth - ruby.length)
            val bottomPadded = base + "\u3000".repeat(slotWidth - base.length)

            if (isTarget) { 
                topBuilder.append("<font color='#8AB4F8'>$topPadded</font>")
                bottomBuilder.append("<font color='#8AB4F8'><b>$bottomPadded</b></font>")
            } else { 
                topBuilder.append(topPadded)
                bottomBuilder.append(bottomPadded)
            }

            lastIndex = end
        }

        if (lastIndex < normalized.length) { 
            var tail = normalized.substring(lastIndex)
            tail = tail.replace(" ", "")
            for (char in tail) { 
                val charStr = char.toString()
                val isTarget = cleanHighlight.isNotEmpty() && cleanHighlight.contains(charStr)

                val topSlot = "\u3000".repeat(minSlotWidth)
                val bottomSlot = charStr + "\u3000".repeat(minSlotWidth - 1)

                topBuilder.append(topSlot)
                if (isTarget) { 
                    bottomBuilder.append("<font color='#8AB4F8'><b>$bottomSlot</b></font>")
                } else { 
                    bottomBuilder.append(bottomSlot)
                }
            }
        }

        return AlignedSentence( 
            furiganaHtml = topBuilder.toString(), 
            sentenceHtml = bottomBuilder.toString()
        )
    }
}
