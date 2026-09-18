import React from 'react' 
import { tokenizeSentence, parseBracketSegments, isKanji } from '../../services/tokenizer' 
import type { StoryToken, StoryWordItem } from '../../types/story' 

interface RubyTextProps {
  text: string 
  furiganaText?: string 
  showFurigana: boolean 
  isSentenceActive?: boolean 
  targetWords?: string[] 
  targetWordsData?: StoryWordItem[] 
  onWordClick?: (token: StoryToken) => void 
  className?: string 
} 

export const RubyText: React.FC<RubyTextProps> = ({
  text, 
  furiganaText, 
  showFurigana, 
  isSentenceActive = false, 
  targetWords = [], 
  targetWordsData = [], 
  onWordClick, 
  className = '' 
}) => {
  if (!showFurigana) {
    const rawTokens = tokenizeSentence(text, targetWords, targetWordsData) 

    return (
      <span
        className={`inline rounded-[6px] px-1 py-0.5 transition-colors ${
          isSentenceActive ? 'bg-[#E87A90]/28' : '' 
        } ${className}`}
        style={{
          fontSize: '17px', 
          lineHeight: '28px', 
          letterSpacing: '0.5px', 
          color: '#E8EAF0' 
        }}
      >
        {rawTokens.map((token, idx) => {
          if (token.isTarget) {
            return (
              <span
                key={idx}
                onClick={() => onWordClick && onWordClick(token)}
                className="inline-block px-1 py-0.5 rounded-[4px] bg-[#2A1B20] text-[#E87A90] font-bold cursor-pointer hover:bg-[#E87A90]/20 active:scale-95 transition-all select-none"
              >
                {token.surface} 
              </span> 
            ) 
          } 

          const hasKanji = isKanji(token.surface) 
          return (
            <span
              key={idx}
              onClick={() => hasKanji && onWordClick && onWordClick(token)}
              className={hasKanji ? 'cursor-pointer hover:text-[#E87A90] transition-colors' : ''}
            >
              {token.surface} 
            </span> 
          ) 
        })}
      </span> 
    ) 
  } 

  const segments = furiganaText && furiganaText.includes('[') 
    ? parseBracketSegments(furiganaText) 
    : tokenizeSentence(text, targetWords, targetWordsData).flatMap(t => t.segments || [{ text: t.surface, ruby: t.furigana }]) 

  return (
    <span
      className={`inline-flex flex-wrap items-baseline gap-x-[1px] gap-y-1 rounded-[6px] px-1 py-0.5 transition-colors ${
        isSentenceActive ? 'bg-[#E87A90]/25' : '' 
      } ${className}`}
    >
      {segments.map((seg, sIdx) => {
        const hasKanji = seg.text.split('').some(c => isKanji(c)) 
        const isTarget = targetWords.some(tw => tw === seg.text || (tw.length >= 2 && seg.text.includes(tw))) 
        const ruby = seg.ruby 

        const tokenObj: StoryToken = {
          surface: seg.text, 
          reading: ruby, 
          furigana: ruby, 
          isKanji: hasKanji, 
          isTarget 
        } 

        return (
          <span
            key={sIdx}
            onClick={() => hasKanji && onWordClick && onWordClick(tokenObj)}
            className={`inline-flex flex-col items-center justify-end rounded-[4px] px-0.5 transition-all select-none ${
              hasKanji ? 'cursor-pointer' : '' 
            } ${
              isTarget ? 'bg-[#2A1B20]' : '' 
            }`}
          >
            {ruby ? (
              <span
                className="text-[9.5px] text-[#9AA1AD] leading-none font-normal tracking-tight h-[12px] flex items-center justify-center pointer-events-none"
              >
                {ruby} 
              </span> 
            ) : (
              <span className="h-[12px] block pointer-events-none" /> 
            )}

            <span
              className={`text-[17px] leading-tight ${
                isTarget 
                  ? 'text-[#E87A90] font-bold' 
                  : 'text-[#E8EAF0] font-medium' 
              } ${
                hasKanji && !isTarget ? 'hover:text-[#E87A90]' : '' 
              }`}
            >
              {seg.text} 
            </span> 
          </span> 
        ) 
      })}
    </span> 
  ) 
} 
