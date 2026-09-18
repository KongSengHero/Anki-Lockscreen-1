import React from 'react' 
import { X, Search, BookmarkPlus, Sparkles, Volume2 } from 'lucide-react' 
import type { StoryToken } from '../../types/story' 
import { db } from '../../services/db' 

interface WordModalProps {
  token: StoryToken | null 
  onClose: () => void 
  onSearchJisho: (query: string) => void 
  onAddedToAnki?: () => void 
} 

export const WordModal: React.FC<WordModalProps> = ({
  token, 
  onClose, 
  onSearchJisho, 
  onAddedToAnki 
}) => {
  if (!token) return null 

  const speakWord = () => {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(token.surface) 
      utterance.lang = 'ja-JP' 
      utterance.rate = 0.9 
      window.speechSynthesis.speak(utterance) 
    } 
  } 

  const handleAddToAnki = async () => {
    const decks = await db.decks.toArray() 
    if (decks.length === 0) return 
    const targetDeck = decks[0] 

    await db.cards.add({
      deckId: targetDeck.id, 
      kanji: token.surface, 
      kana: token.reading || token.furigana || token.surface, 
      english: token.meaning || 'Saved from Story Reader', 
      sentenceJp: `${token.surface}を練習しています。`, 
      sentenceRubyJp: `${token.surface}を練習しています。`, 
      sentenceEn: `Practicing ${token.surface}.`, 
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

    if (onAddedToAnki) {
      onAddedToAnki() 
    } 
    onClose() 
  } 

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4">
      <div 
        className="fixed inset-0 bg-black/65 backdrop-blur-sm transition-opacity" 
        onClick={onClose} 
      /> 

      <div className="relative w-full max-w-md bg-[#0D111A] border-t sm:border border-white/10 rounded-t-3xl sm:rounded-2xl p-6 shadow-2xl z-10 animate-in slide-in-from-bottom-6 duration-200">
        <div className="w-12 h-1.5 bg-white/20 rounded-full mx-auto mb-4 sm:hidden" /> 

        <div className="flex items-start justify-between">
          <div className="flex items-baseline gap-3">
            <span className="text-3xl font-bold tracking-tight text-white">
              {token.surface}
            </span> 
            {token.reading && (
              <span className="text-lg text-pink-400 font-medium">
                {token.reading}
              </span> 
            )}
          </div> 

          <div className="flex items-center gap-2">
            <button
              onClick={speakWord}
              className="p-2 rounded-xl bg-white/5 hover:bg-white/10 text-slate-300 transition-colors"
              title="Pronounce"
            >
              <Volume2 className="w-5 h-5" /> 
            </button> 
            <button
              onClick={onClose}
              className="p-2 rounded-xl bg-white/5 hover:bg-white/10 text-slate-400 hover:text-white transition-colors"
            >
              <X className="w-5 h-5" /> 
            </button> 
          </div> 
        </div> 

        <div className="mt-4 flex flex-wrap gap-2 items-center">
          {token.jlpt && (
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-cyan-500/20 text-cyan-300 border border-cyan-500/30">
              {token.jlpt}
            </span> 
          )}
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-purple-500/20 text-purple-300 border border-purple-500/30 flex items-center gap-1">
            <Sparkles className="w-3 h-3" /> Vocabulary
          </span> 
        </div> 

        <div className="mt-5 p-4 rounded-xl bg-white/[0.03] border border-white/5">
          <div className="text-xs uppercase tracking-wider text-slate-400 font-semibold mb-1">
            Meaning
          </div> 
          <div className="text-base text-slate-100 font-normal leading-relaxed">
            {token.meaning || 'Tap search below for full dictionary definitions and example sentences.'}
          </div> 
        </div> 

        <div className="mt-6 grid grid-cols-2 gap-3">
          <button
            onClick={() => {
              onSearchJisho(token.surface) 
              onClose() 
            }}
            className="flex items-center justify-center gap-2 px-4 py-3 rounded-xl bg-cyan-500/15 hover:bg-cyan-500/25 text-cyan-300 border border-cyan-500/30 font-medium text-sm transition-all active:scale-[0.98]"
          >
            <Search className="w-4 h-4" /> 
            Jisho Lookup
          </button> 

          <button
            onClick={handleAddToAnki}
            className="flex items-center justify-center gap-2 px-4 py-3 rounded-xl bg-gradient-to-r from-pink-500 to-rose-500 hover:from-pink-400 hover:to-rose-400 text-white font-medium text-sm shadow-lg shadow-pink-500/25 transition-all active:scale-[0.98]"
          >
            <BookmarkPlus className="w-4 h-4" /> 
            Add to Anki
          </button> 
        </div> 
      </div> 
    </div> 
  ) 
} 
