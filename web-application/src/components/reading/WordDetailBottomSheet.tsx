import React, { useState, useEffect } from 'react' 
import { Volume2, Bookmark, BookmarkCheck, X, Search } from 'lucide-react' 
import type { StoryToken } from '../../types/story' 
import { searchJisho } from '../../services/jishoService' 
import { db } from '../../services/db' 
import { Squircle3DButton } from '../blossom/Squircle3DButton' 

interface WordDetailBottomSheetProps {
  token: StoryToken 
  exampleSentenceJp?: string 
  exampleSentenceEn?: string 
  onSearchJisho?: (query: string) => void 
  onDismiss: () => void 
} 

export const WordDetailBottomSheet: React.FC<WordDetailBottomSheetProps> = ({
  token, 
  exampleSentenceJp = '', 
  exampleSentenceEn = '', 
  onSearchJisho, 
  onDismiss 
}) => {
  const [definition, setDefinition] = useState(token.meaning || '') 
  const [reading, setReading] = useState(token.reading || '') 
  const [isLoading, setIsLoading] = useState(!token.meaning) 
  const [isAdded, setIsAdded] = useState(false) 

  useEffect(() => {
    if (!token.meaning) {
      setIsLoading(true) 
      searchJisho(token.surface)
        .then(results => {
          if (results.length > 0) {
            const first = results[0] 
            const read = first.japanese?.[0]?.reading || '' 
            const def = first.senses?.[0]?.english_definitions?.join('; ') || '' 
            if (read) setReading(read) 
            if (def) setDefinition(def) 
          } 
        })
        .finally(() => setIsLoading(false)) 
    } 
  }, [token.surface, token.meaning]) 

  const handlePronounce = () => {
    if ('speechSynthesis' in window) {
      const u = new SpeechSynthesisUtterance(token.surface) 
      u.lang = 'ja-JP' 
      u.rate = 0.9 
      window.speechSynthesis.speak(u) 
    } 
  } 

  const handleAddToAnki = async () => {
    const decks = await db.decks.toArray() 
    if (decks.length === 0) return 
    const targetDeck = decks[0] 

    await db.cards.add({
      deckId: targetDeck.id, 
      kanji: token.surface, 
      kana: reading || token.surface, 
      english: definition || 'Saved from reading story', 
      sentenceJp: exampleSentenceJp || `${token.surface}の例文です。`, 
      sentenceRubyJp: exampleSentenceJp || `${token.surface}の例文です。`, 
      sentenceEn: exampleSentenceEn || `Example sentence for ${token.surface}.`, 
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

    setIsAdded(true) 
  } 

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-md animate-fadeIn">
      <div className="w-full max-w-md max-h-[85vh] overflow-y-auto rounded-t-[26px] sm:rounded-[26px] bg-[#161922] border border-[#2C3240] p-6 shadow-2xl flex flex-col gap-4">
        <div className="w-10 h-1 rounded-full bg-[#2C3240] mx-auto -mt-1 mb-1" /> 

        <div className="flex items-center justify-between">
          <div className="px-2.5 py-1 rounded-[8px] bg-[#2A1B20] border border-[#E87A90]/50 text-xs font-bold text-[#E87A90]">
            Studied Card
          </div> 

          <div className="flex items-center gap-1.5">
            <button
              onClick={handlePronounce}
              className="w-8 h-8 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-90"
              title="Speak word"
            >
              <Volume2 className="w-4 h-4" /> 
            </button> 

            <button
              onClick={handleAddToAnki}
              className="w-8 h-8 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E87A90] active:scale-90"
              title="Save to cards"
            >
              {isAdded ? (
                <BookmarkCheck className="w-4 h-4 text-[#5FA77C]" /> 
              ) : (
                <Bookmark className="w-4 h-4" /> 
              )}
            </button> 

            <button
              onClick={onDismiss}
              className="w-8 h-8 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-95"
            >
              <X className="w-5 h-5" /> 
            </button> 
          </div> 
        </div> 

        <div className="flex flex-col gap-1">
          <h2 className="text-3xl font-extrabold text-[#E8EAF0] tracking-tight">
            {token.surface} 
          </h2> 
          {reading && reading !== token.surface && (
            <span className="text-lg font-semibold text-[#E87A90]">
              {reading} 
            </span> 
          )}
        </div> 

        <div className="rounded-[14px] bg-[#212631] border border-[#2C3240] p-4 flex flex-col gap-2">
          <span className="text-[11px] font-bold text-[#6E7482] tracking-wider uppercase">
            ENGLISH MEANING
          </span> 
          {isLoading ? (
            <div className="h-5 w-2/3 bg-[#2C3240] animate-pulse rounded" /> 
          ) : (
            <p className="text-[15px] text-[#E8EAF0] leading-relaxed">
              {definition || 'No definition found.'} 
            </p> 
          )}
        </div> 

        <div className="pt-2">
          <Squircle3DButton
            onClick={() => {
              onDismiss() 
              if (onSearchJisho) onSearchJisho(token.surface) 
            }}
            containerColor="#E87A90"
            bevelColor="#A8475B"
            contentColor="#FFFFFF"
            height={48}
            shapeRadius={14}
            className="w-full"
          >
            <Search className="w-4 h-4 mr-2 text-white" /> 
            Look up in Jisho
          </Squircle3DButton> 
        </div> 
      </div> 
    </div> 
  ) 
} 
