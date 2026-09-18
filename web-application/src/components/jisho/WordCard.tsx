import React, { useState } from 'react' 
import { Volume2, Copy, ChevronDown, ChevronUp, Check, BookmarkPlus } from 'lucide-react' 
import type { JishoWord } from '../../types/jisho' 
import { db } from '../../services/db' 
import { BlossomTactileButton } from '../blossom/BlossomTactileButton' 

interface WordCardProps {
  word: JishoWord 
  onAddedToDeck?: () => void 
} 

export const WordCard: React.FC<WordCardProps> = ({ word, onAddedToDeck }) => {
  const [isExpanded, setIsExpanded] = useState(false) 
  const [copied, setCopied] = useState(false) 
  const [added, setAdded] = useState(false) 

  const primaryJp = word.japanese?.[0] || { word: word.slug, reading: word.slug } 
  const displayKanji = primaryJp.word || word.slug 
  const displayReading = primaryJp.reading || '' 
  const wkTags = word.tags?.filter(t => t.toLowerCase().startsWith('wanikani')) || [] 

  const speak = () => {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(displayKanji) 
      utterance.lang = 'ja-JP' 
      utterance.rate = 0.9 
      window.speechSynthesis.speak(utterance) 
    } 
  } 

  const copyWord = () => {
    const text = displayReading && displayReading !== displayKanji 
      ? `${displayKanji} [${displayReading}]` 
      : displayKanji 
    navigator.clipboard.writeText(text) 
    setCopied(true) 
    setTimeout(() => setCopied(false), 2000) 
  } 

  const handleAddToAnki = async () => {
    const decks = await db.decks.toArray() 
    if (decks.length === 0) return 
    const targetDeck = decks[0] 

    const primaryDef = word.senses?.[0]?.english_definitions?.join(', ') || 'No definition' 

    await db.cards.add({
      deckId: targetDeck.id, 
      kanji: displayKanji, 
      kana: displayReading || displayKanji, 
      english: primaryDef, 
      sentenceJp: `${displayKanji}の意味を覚えています。`, 
      sentenceRubyJp: `${displayKanji}の意味を覚えています。`, 
      sentenceEn: `Remembering the meaning of ${displayKanji}.`, 
      cardType: 0, 
      interval: 0, 
      easeFactor: 2500, 
      repetitions: 0, 
      dueDate: Date.now() 
    }) 

    await db.decks.update(targetDeck.id, {
      cardCount: targetDeck.cardCount + 1, 
      dueCount: targetDeck.dueCount + 1 
    }) 

    setAdded(true) 
    if (onAddedToDeck) onAddedToDeck() 
    setTimeout(() => setAdded(false), 2500) 
  } 

  return (
    <div
      className="bg-[#252A35] border border-[#2C3240] rounded-[18px] p-4 transition-all"
      style={{
        boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.08), 0 4px 20px rgba(0, 0, 0, 0.35)' 
      }}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          {displayReading && displayReading !== displayKanji && (
            <div className="text-[15px] font-semibold text-[#5FA77C] mb-0.5">
              {displayReading} 
            </div> 
          )}

          <div className="text-2xl font-bold text-[#E8EAF0] tracking-tight">
            {displayKanji} 
          </div> 
        </div> 

        <div className="flex items-center gap-1">
          <button
            onClick={speak}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] hover:bg-white/5 active:scale-95 transition-all"
            title="Pronounce"
          >
            <Volume2 className="w-[18px] h-[18px]" /> 
          </button> 

          <button
            onClick={copyWord}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] hover:bg-white/5 active:scale-95 transition-all"
            title="Copy"
          >
            {copied ? (
              <Check className="w-[17px] h-[17px] text-[#5FA77C]" /> 
            ) : (
              <Copy className="w-[17px] h-[17px]" /> 
            )}
          </button> 

          <button
            onClick={() => setIsExpanded(prev => !prev)}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-[#5FA77C] hover:bg-white/5 active:scale-95 transition-all"
            title={isExpanded ? 'Collapse' : 'Expand'}
          >
            {isExpanded ? (
              <ChevronUp className="w-5 h-5" /> 
            ) : (
              <ChevronDown className="w-5 h-5" /> 
            )}
          </button> 
        </div> 
      </div> 

      <div className="mt-2 flex flex-wrap gap-1.5 items-center">
        {word.is_common && (
          <span className="px-2 py-0.5 rounded-[6px] text-[10px] font-bold bg-[#1B3024] text-[#5FA77C] border border-[#5FA77C]/35">
            COMMON
          </span> 
        )}
        {word.jlpt?.map((lvl, lIdx) => (
          <span
            key={lIdx}
            className="px-2 py-0.5 rounded-[6px] text-[10px] font-bold bg-[#162B35] text-[#22D3EE] border border-[#22D3EE]/35 uppercase"
          >
            {lvl.replace('jlpt-', '')} 
          </span> 
        ))}
        {wkTags.slice(0, 1).map((wk, wIdx) => (
          <span
            key={wIdx}
            className="px-2 py-0.5 rounded-[6px] text-[10px] font-bold bg-[#2A1B20] text-[#E87A90] border border-[#E87A90]/35 uppercase"
          >
            {wk.replace(/wanikani/i, 'WK ')} 
          </span> 
        ))}
      </div> 

      <div className="mt-3 pt-3 border-t border-[#2C3240] space-y-1.5">
        {(isExpanded ? word.senses : word.senses?.slice(0, 2))?.map((sense, sIdx) => (
          <div key={sIdx} className="text-[13px] leading-relaxed text-[#E8EAF0]">
            <span className="text-[#6E7482] mr-2 font-mono text-[11px]">
              {sIdx + 1}.
            </span> 
            <span>
              {sense.english_definitions?.join('; ')} 
            </span> 
            {sense.parts_of_speech && sense.parts_of_speech.length > 0 && (
              <span className="text-[11px] text-[#9AA1AD] ml-2 italic">
                ({sense.parts_of_speech.join(', ')})
              </span> 
            )}
          </div> 
        ))}
      </div> 

      <div className="mt-3.5 pt-3 border-t border-[#2C3240]/60 flex justify-end">
        <BlossomTactileButton
          onClick={handleAddToAnki}
          variant={added ? 'success' : 'danger'}
          lipHeight={2}
          className="text-xs"
        >
          {added ? (
            <span className="flex items-center gap-1.5">
              <Check className="w-3.5 h-3.5" /> 
              Saved to Deck
            </span> 
          ) : (
            <span className="flex items-center gap-1.5">
              <BookmarkPlus className="w-3.5 h-3.5" /> 
              Add to Anki
            </span> 
          )}
        </BlossomTactileButton> 
      </div> 
    </div> 
  ) 
} 
