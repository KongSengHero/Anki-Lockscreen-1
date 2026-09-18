import React, { useState } from 'react' 
import { Eye, RefreshCw, Volume2, Sparkles, CheckCircle } from 'lucide-react' 
import type { AnkiCard, AnkiDeck } from '../../types/anki' 
import { BlossomTactileButton } from '../blossom/BlossomTactileButton' 

interface DeckCarouselCardProps {
  deck?: AnkiDeck 
  card?: AnkiCard | null 
  stats: {
    new: number 
    learn: number 
    review: number 
  } 
  isRevealed: boolean 
  onToggleReveal: () => void 
  onRefresh: () => void 
  onAgain: () => void 
  onGood: () => void 
  onOpenDecks: () => void 
  onPlayWord: () => void 
  onPlaySentence: () => void 
} 

export const DeckCarouselCard: React.FC<DeckCarouselCardProps> = ({
  deck, 
  card, 
  stats, 
  isRevealed, 
  onToggleReveal, 
  onRefresh, 
  onAgain, 
  onGood, 
  onOpenDecks, 
  onPlayWord, 
  onPlaySentence 
}) => {
  const [autoPlayMode, setAutoPlayMode] = useState(0) 

  const autoPlayFillFraction = autoPlayMode === 1 
    ? '33%' 
    : autoPlayMode === 2 
    ? '66%' 
    : autoPlayMode === 3 
    ? '100%' 
    : '0%' 

  const autoPlayLabel = autoPlayMode === 1 
    ? 'Word Only' 
    : autoPlayMode === 2 
    ? 'Sentence' 
    : autoPlayMode === 3 
    ? 'Word & Sent' 
    : 'Auto Play' 

  return (
    <div className="w-full rounded-[24px] bg-[#1E222B] border border-[#2C3240] p-4 flex flex-col gap-3.5 shadow-xl">
      <div className="flex items-center justify-between px-1">
        <div className="flex items-center gap-2 overflow-hidden">
          <Eye className="w-[18px] h-[18px] text-[#7C8CF8] shrink-0" /> 
          <span className="text-sm font-semibold text-[#E2E8F0] truncate">
            {deck?.name || 'Live Card Preview'} 
          </span> 
        </div> 

        <div className="flex items-center gap-2 shrink-0">
          <div className="flex items-center text-xs font-bold">
            <div className="flex flex-col items-center">
              <span className="text-[#8AB4F8]">{stats.new}</span> 
              {card && card.cardType === 0 && (
                <div className="w-3.5 h-0.5 rounded-full bg-[#8AB4F8] mt-0.5" /> 
              )}
            </div> 
            <span className="mx-1 text-[#64748B]">·</span> 
            <div className="flex flex-col items-center">
              <span className="text-[#F28B82]">{stats.learn}</span> 
              {card && card.cardType === 1 && (
                <div className="w-3.5 h-0.5 rounded-full bg-[#F28B82] mt-0.5" /> 
              )}
            </div> 
            <span className="mx-1 text-[#64748B]">·</span> 
            <div className="flex flex-col items-center">
              <span className="text-[#81C995]">{stats.review}</span> 
              {card && card.cardType === 2 && (
                <div className="w-3.5 h-0.5 rounded-full bg-[#81C995] mt-0.5" /> 
              )}
            </div> 
          </div> 

          <button
            onClick={onRefresh}
            className="w-7 h-7 rounded-lg flex items-center justify-center text-[#94A3B8] hover:text-[#E2E8F0] hover:bg-white/5 active:scale-95 transition-all ml-1"
            title="Refresh Card"
          >
            <RefreshCw className="w-4 h-4" /> 
          </button> 
        </div> 
      </div> 

      <div className="relative w-full aspect-[0.82] rounded-[20px] overflow-hidden select-none cursor-pointer border border-[#7C8CF8]/40 shadow-inner flex flex-col justify-between p-6 bg-gradient-to-b from-[#1E222B]/90 to-[#121418]/95"
        onClick={onToggleReveal}
      >
        <div className="absolute inset-0 pointer-events-none bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))] from-[#7C8CF8]/15 via-[#5FA77C]/5 to-transparent" /> 

        {!card ? (
          <div className="relative z-10 w-full h-full flex flex-col items-center justify-center text-center p-4">
            <CheckCircle className="w-11 h-11 text-[#7C8CF8] mb-3" /> 
            <h3 className="text-[17px] font-bold text-[#E2E8F0]">
              Nothing to Review
            </h3> 
            <p className="text-xs text-[#94A3B8] mt-2 whitespace-pre-line leading-relaxed">
              No Decks Selected
              Select an Anki deck below to start reviewing
            </p> 
          </div> 
        ) : (
          <div className="relative z-10 w-full h-full flex flex-col justify-between">
            <div className="flex items-center justify-between text-xs font-bold text-[#94A3B8]">
              <div className="flex items-center gap-1.5">
                <span className="text-[#8AB4F8]">{stats.new}</span> 
                <span>·</span> 
                <span className="text-[#F28B82]">{stats.learn}</span> 
                <span>·</span> 
                <span className="text-[#81C995]">{stats.review}</span> 
              </div> 
              <span className="truncate max-w-[150px] text-[#E2E8F0]">
                {deck?.name} 
              </span> 
            </div> 

            <div className="flex flex-col items-center justify-center text-center my-auto py-4">
              {isRevealed && card.kana && card.kana !== card.kanji && (
                <div className="text-[15px] font-semibold text-[#7EB6FF] mb-1 animate-fadeIn">
                  {card.kana} 
                </div> 
              )}

              <div className="text-4xl font-extrabold text-white tracking-wide">
                {card.kanji} 
              </div> 

              {isRevealed && (
                <div className="mt-3 text-[17.5px] font-bold text-[#F1F5F9] max-w-[90%] leading-snug animate-fadeIn">
                  {card.english} 
                </div> 
              )}

              <div className="w-16 h-px bg-white/10 my-4" /> 

              <div className="text-lg font-bold text-white max-w-[95%] leading-relaxed">
                {card.sentenceJp} 
              </div> 

              {isRevealed && (
                <div className="mt-2 text-[14px] text-[#CBD5E1] max-w-[90%] leading-relaxed animate-fadeIn">
                  {card.sentenceEn} 
                </div> 
              )}
            </div> 

            <div className="text-center text-[11px] text-[#64748B]">
              Tap card to {isRevealed ? 'hide answer' : 'reveal answer'} 
            </div> 
          </div> 
        )}
      </div> 

      <div className="grid grid-cols-3 gap-2">
        <button
          onClick={onPlayWord}
          disabled={!card}
          className="h-[38px] rounded-[10px] bg-[#161920] border border-[#2C3240] hover:border-[#38BDF8]/60 disabled:opacity-50 text-[#38BDF8] flex items-center justify-center gap-1.5 transition-all active:scale-95"
        >
          <Volume2 className="w-[15px] h-[15px]" /> 
          <span className="text-[11.5px] font-bold text-[#94A3B8]">Word</span> 
        </button> 

        <div
          onClick={() => setAutoPlayMode(prev => (prev + 1) % 4)}
          className={`relative h-[38px] rounded-[10px] bg-[#161920] border overflow-hidden cursor-pointer flex items-center justify-center transition-all ${
            autoPlayMode > 0 ? 'border-[#5FA77C]' : 'border-[#2C3240]' 
          }`}
        >
          <div
            className="absolute top-0 bottom-0 left-0 bg-[#5FA77C]/25 transition-all duration-200 pointer-events-none"
            style={{ width: autoPlayFillFraction }}
          /> 
          <div className="relative z-10 flex items-center gap-1.5 px-2">
            <Sparkles
              className={`w-[15px] h-[15px] ${
                autoPlayMode > 0 ? 'text-[#5FA77C]' : 'text-[#64748B]' 
              }`}
            /> 
            <span
              className={`text-[11px] font-bold whitespace-nowrap ${
                autoPlayMode > 0 ? 'text-[#5FA77C]' : 'text-[#94A3B8]' 
              }`}
            >
              {autoPlayLabel} 
            </span> 
          </div> 
        </div> 

        <button
          onClick={onPlaySentence}
          disabled={!card}
          className="h-[38px] rounded-[10px] bg-[#161920] border border-[#2C3240] hover:border-[#38BDF8]/60 disabled:opacity-50 text-[#38BDF8] flex items-center justify-center gap-1.5 transition-all active:scale-95"
        >
          <Volume2 className="w-[15px] h-[15px]" /> 
          <span className="text-[11.5px] font-bold text-[#94A3B8]">Sentence</span> 
        </button> 
      </div> 

      <div className="grid grid-cols-4 gap-2">
        <BlossomTactileButton
          onClick={onAgain}
          disabled={!card}
          variant="danger"
          lipHeight={2}
          className="text-xs w-full"
        >
          Again
        </BlossomTactileButton> 

        <BlossomTactileButton
          onClick={onToggleReveal}
          disabled={!card}
          variant="primary"
          lipHeight={2}
          className="text-xs w-full"
        >
          {isRevealed ? 'Hide' : 'Reveal'} 
        </BlossomTactileButton> 

        <BlossomTactileButton
          onClick={onGood}
          disabled={!card}
          variant="success"
          lipHeight={2}
          className="text-xs w-full"
        >
          Good
        </BlossomTactileButton> 

        <BlossomTactileButton
          onClick={onOpenDecks}
          variant="secondary"
          lipHeight={2}
          className="text-xs w-full"
        >
          Decks
        </BlossomTactileButton> 
      </div> 
    </div> 
  ) 
} 
