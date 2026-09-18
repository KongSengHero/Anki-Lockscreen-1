import React, { useState, useEffect, useCallback } from 'react' 
import { Volume2, RotateCw, CheckCircle, ArrowLeft, Eye } from 'lucide-react' 
import type { AnkiCard, SrsGrade } from '../../types/anki' 
import { getDueCards, updateCardReview } from '../../services/db' 
import { calculateSrsReview } from '../../services/srsScheduler' 
import { RubyText } from '../reading/RubyText' 

interface StudyViewProps {
  deckId?: number 
  onBackToDecks?: () => void 
  onReviewCompleted?: () => void 
} 

export const StudyView: React.FC<StudyViewProps> = ({
  deckId, 
  onBackToDecks, 
  onReviewCompleted 
}) => {
  const [dueCards, setDueCards] = useState<AnkiCard[]>([]) 
  const [currentIndex, setCurrentIndex] = useState(0) 
  const [isFlipped, setIsFlipped] = useState(false) 
  const [reviewedCount, setReviewedCount] = useState(0) 
  const [isLoading, setIsLoading] = useState(true) 

  const loadCards = useCallback(async () => {
    setIsLoading(true) 
    const cards = await getDueCards(deckId) 
    setDueCards(cards) 
    setCurrentIndex(0) 
    setIsFlipped(false) 
    setIsLoading(false) 
  }, [deckId]) 

  useEffect(() => {
    loadCards() 
  }, [loadCards]) 

  const currentCard: AnkiCard | undefined = dueCards[currentIndex] 

  const handleGrade = async (grade: SrsGrade) => {
    if (!currentCard) return 

    const reviewResult = calculateSrsReview(currentCard, grade) 
    await updateCardReview(
      currentCard.id, 
      reviewResult.interval, 
      reviewResult.easeFactor, 
      reviewResult.repetitions, 
      reviewResult.dueDate 
    ) 

    setReviewedCount(prev => prev + 1) 
    setIsFlipped(false) 

    if (currentIndex < dueCards.length - 1) {
      setCurrentIndex(prev => prev + 1) 
    } else {
      setDueCards([]) 
      if (onReviewCompleted) {
        onReviewCompleted() 
      } 
    } 
  } 

  const speak = (text: string) => {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(text) 
      utterance.lang = 'ja-JP' 
      utterance.rate = 0.9 
      window.speechSynthesis.speak(utterance) 
    } 
  } 

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="w-9 h-9 rounded-full border-[3px] border-[#E87A90] border-t-transparent animate-spin" /> 
      </div> 
    ) 
  } 

  if (!currentCard || dueCards.length === 0) {
    return (
      <div className="max-w-md mx-auto px-5 py-16 text-center">
        <div className="w-20 h-20 rounded-[24px] bg-[#1A3324] text-[#81C995] border border-[#81C995]/40 flex items-center justify-center mx-auto mb-6 shadow-xl">
          <CheckCircle className="w-10 h-10" /> 
        </div> 
        <h2 className="text-2xl font-bold text-[#E8EAF0] mb-2">
          All Caught Up!
        </h2> 
        <p className="text-[#9AA1AD] text-sm mb-6 leading-relaxed">
          You've completed all due flashcard reviews for now.
        </p> 
        {reviewedCount > 0 && (
          <div className="p-4 rounded-[18px] bg-[#252A35] border border-[#2C3240] mb-6 text-sm text-[#E8EAF0]">
            Reviewed <span className="text-[#E87A90] font-bold">{reviewedCount}</span> cards in this session!
          </div> 
        )}
        {onBackToDecks && (
          <button
            onClick={onBackToDecks}
            className="inline-flex items-center gap-2 px-6 py-3 rounded-[16px] bg-[#1E222B] hover:bg-[#252A35] border border-[#2C3240] text-[#E8EAF0] font-medium text-sm transition-all active:scale-95"
          >
            <ArrowLeft className="w-4 h-4" /> 
            Back to Decks
          </button> 
        )}
      </div> 
    ) 
  } 

  return (
    <div className="max-w-md mx-auto px-5 pt-4 pb-28">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          {onBackToDecks && (
            <button
              onClick={onBackToDecks}
              className="p-1.5 rounded-xl bg-[#1E222B] border border-[#2C3240] text-[#9AA1AD] hover:text-[#E8EAF0] transition-colors"
            >
              <ArrowLeft className="w-4 h-4" /> 
            </button> 
          )}
          <span className="text-xs font-bold uppercase tracking-wider text-[#E87A90]">
            Spaced Repetition
          </span> 
        </div> 

        <div className="text-xs font-bold px-3 py-1 rounded-full bg-[#2A1B20] text-[#E87A90] border border-[#E87A90]/40">
          {currentIndex + 1} / {dueCards.length} due
        </div> 
      </div> 

      <div 
        className="w-full h-[410px] perspective-1000 cursor-pointer"
        onClick={() => setIsFlipped(prev => !prev)}
      >
        <div 
          className={`relative w-full h-full preserve-3d transition-transform duration-500 rounded-[24px] ${
            isFlipped ? 'rotate-y-180' : '' 
          }`}
        >
          <div className="absolute inset-0 backface-hidden bg-[#1E222B] border border-[#2C3240] rounded-[24px] p-7 flex flex-col justify-between shadow-2xl">
            <div className="flex justify-between items-start">
              <span className="text-[11px] font-bold uppercase tracking-widest text-[#6E7482]">
                Front
              </span> 
              <button
                onClick={(e) => {
                  e.stopPropagation() 
                  speak(currentCard.kanji) 
                }}
                className="w-8 h-8 rounded-lg bg-[#252A35] flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] transition-colors"
              >
                <Volume2 className="w-4 h-4" /> 
              </button> 
            </div> 

            <div className="text-center my-auto">
              <div className="text-5xl sm:text-6xl font-extrabold text-[#E8EAF0] tracking-tight mb-4">
                {currentCard.kanji} 
              </div> 
              {currentCard.sentenceJp && (
                <div className="mt-4 text-base text-[#9AA1AD] max-w-xs mx-auto leading-relaxed">
                  <RubyText
                    text={currentCard.sentenceJp}
                    showFurigana={true}
                  /> 
                </div> 
              )}
            </div> 

            <div className="text-center text-xs text-[#6E7482] flex items-center justify-center gap-1.5">
              <RotateCw className="w-3.5 h-3.5 animate-pulse" /> 
              Tap card or Reveal to flip
            </div> 
          </div> 

          <div className="absolute inset-0 backface-hidden rotate-y-180 bg-[#161920] border border-[#E87A90]/40 rounded-[24px] p-7 flex flex-col justify-between shadow-2xl">
            <div className="flex justify-between items-start">
              <span className="text-[11px] font-bold uppercase tracking-widest text-[#E87A90]">
                Answer
              </span> 
              <button
                onClick={(e) => {
                  e.stopPropagation() 
                  speak(`${currentCard.kana}. ${currentCard.sentenceJp || ''}`) 
                }}
                className="w-8 h-8 rounded-lg bg-[#252A35] flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] transition-colors"
              >
                <Volume2 className="w-4 h-4" /> 
              </button> 
            </div> 

            <div className="text-center my-auto">
              <div className="text-3xl font-bold text-[#E8EAF0] mb-1">
                {currentCard.kanji} 
              </div> 
              <div className="text-lg text-[#E87A90] font-semibold mb-4">
                {currentCard.kana} 
              </div> 
              <div className="text-base text-[#E8EAF0] font-medium leading-snug px-2 mb-3">
                {currentCard.english} 
              </div> 
              {currentCard.sentenceEn && (
                <div className="text-xs text-[#9AA1AD] italic max-w-xs mx-auto">
                  "{currentCard.sentenceEn}"
                </div> 
              )}
            </div> 

            <div className="text-center text-xs text-[#6E7482]">
              Select rating below to schedule
            </div> 
          </div> 
        </div> 
      </div> 

      <div className="mt-5">
        {isFlipped ? (
          <div className="grid grid-cols-4 gap-2 animate-in slide-in-from-bottom-2 duration-150">
            <button
              onClick={() => handleGrade('again')}
              className="py-3 px-1 rounded-[16px] bg-[#3B1E22] border border-[#EF5350]/60 text-[#EF5350] flex flex-col items-center justify-center active:scale-95 transition-all shadow-md"
            >
              <span className="text-xs font-bold">Again</span> 
              <span className="text-[10px] opacity-80 mt-0.5">&lt; 10m</span> 
            </button> 

            <button
              onClick={() => handleGrade('hard')}
              className="py-3 px-1 rounded-[16px] bg-[#332717] border border-[#CFA055]/60 text-[#CFA055] flex flex-col items-center justify-center active:scale-95 transition-all shadow-md"
            >
              <span className="text-xs font-bold">Hard</span> 
              <span className="text-[10px] opacity-80 mt-0.5">1d</span> 
            </button> 

            <button
              onClick={() => handleGrade('good')}
              className="py-3 px-1 rounded-[16px] bg-[#1A3324] border border-[#81C995]/60 text-[#81C995] flex flex-col items-center justify-center active:scale-95 transition-all shadow-md"
            >
              <span className="text-xs font-bold">Good</span> 
              <span className="text-[10px] opacity-80 mt-0.5">
                {Math.max(1, currentCard.interval || 1)}d
              </span> 
            </button> 

            <button
              onClick={() => handleGrade('easy')}
              className="py-3 px-1 rounded-[16px] bg-[#162B35] border border-[#22D3EE]/60 text-[#22D3EE] flex flex-col items-center justify-center active:scale-95 transition-all shadow-md"
            >
              <span className="text-xs font-bold">Easy</span> 
              <span className="text-[10px] opacity-80 mt-0.5">
                {Math.max(4, Math.round((currentCard.interval || 1) * 2.5))}d
              </span> 
            </button> 
          </div> 
        ) : (
          <button
            onClick={() => setIsFlipped(true)}
            className="w-full py-3.5 rounded-[18px] bg-[#1F2A44] border border-[#5C6BC0]/60 text-[#7EB6FF] font-bold text-sm flex items-center justify-center gap-2 active:scale-[0.98] transition-all shadow-lg"
          >
            <Eye className="w-4 h-4" /> 
            Reveal
          </button> 
        )}
      </div> 
    </div> 
  ) 
} 
