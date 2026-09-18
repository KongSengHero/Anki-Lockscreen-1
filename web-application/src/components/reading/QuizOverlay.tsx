import React, { useState } from 'react' 
import { X, RotateCcw, Check, CheckCircle2, XCircle, ArrowRight, ArrowLeft, Trophy, Sparkles } from 'lucide-react' 
import type { QuizQuestion } from '../../types/story' 
import { Squircle3DButton } from '../blossom/Squircle3DButton' 

interface QuizOverlayProps {
  questions: QuizQuestion[] 
  onClose: () => void 
  onQuizSubmitted?: (score: number, isPassed: boolean) => void 
  onNavigateToJisho?: (word: string) => void 
} 

const OPTION_LABELS = ['A', 'B', 'C', 'D'] 

export const QuizOverlay: React.FC<QuizOverlayProps> = ({
  questions, 
  onClose, 
  onQuizSubmitted, 
  onNavigateToJisho 
}) => {
  const [currentIndex, setCurrentIndex] = useState(0) 
  const [userAnswers, setUserAnswers] = useState<Record<number, number>>({}) 
  const [isCompleted, setIsCompleted] = useState(false) 

  if (!questions || questions.length === 0) return null 

  const currentQ = questions[currentIndex] 
  const selectedOption = userAnswers[currentIndex] 
  const hasAnsweredCurrent = selectedOption !== undefined 

  const correctIndex = currentQ.correctOptionIndex !== undefined 
    ? currentQ.correctOptionIndex 
    : currentQ.correctIndex 

  const handleSelect = (optionIdx: number) => {
    if (hasAnsweredCurrent || isCompleted) return 
    setUserAnswers(prev => ({
      ...prev, 
      [currentIndex]: optionIdx 
    })) 
  } 

  const handleNext = () => {
    if (currentIndex < questions.length - 1) {
      setCurrentIndex(prev => prev + 1) 
    } else {
      let correctCount = 0 
      questions.forEach((q, idx) => {
        const correct = q.correctOptionIndex !== undefined ? q.correctOptionIndex : q.correctIndex 
        if (userAnswers[idx] === correct) {
          correctCount++ 
        } 
      }) 
      const percentage = Math.round((correctCount / questions.length) * 100) 
      const isPassed = percentage >= 60 
      setIsCompleted(true) 
      if (onQuizSubmitted) {
        onQuizSubmitted(percentage, isPassed) 
      } 
    } 
  } 

  const handlePrev = () => {
    if (currentIndex > 0) {
      setCurrentIndex(prev => prev - 1) 
    } 
  } 

  const handleRestart = () => {
    setUserAnswers({}) 
    setCurrentIndex(0) 
    setIsCompleted(false) 
  } 

  const correctCount = questions.filter((q, idx) => {
    const correct = q.correctOptionIndex !== undefined ? q.correctOptionIndex : q.correctIndex 
    return userAnswers[idx] === correct 
  }).length 
  const scorePercentage = Math.round((correctCount / questions.length) * 100) 
  const isPassed = scorePercentage >= 60 
  const progressPercent = isCompleted ? 100 : Math.round(((currentIndex + 1) / questions.length) * 100) 

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-md animate-fadeIn">
      <div className="w-full max-w-lg max-h-[92vh] h-[92vh] overflow-hidden rounded-t-[26px] sm:rounded-[26px] bg-[#1E222B] border border-[#2C3240] shadow-2xl flex flex-col">
        <div className="px-5 py-3 border-b border-[#20242E] flex items-center justify-between">
          <Squircle3DButton
            onClick={onClose}
            containerColor="#252A35"
            bevelColor="#2C3240"
            contentColor="#E8EAF0"
            height={44}
            shapeRadius={12}
            className="w-[50px]"
          >
            <X className="w-5 h-5" /> 
          </Squircle3DButton> 

          <div className="flex flex-col items-center">
            <h2 className="text-[17px] font-bold text-[#E8EAF0]">
              Comprehension Quiz
            </h2> 
            {!isCompleted && (
              <span className="text-xs text-[#9AA1AD]">
                Question {currentIndex + 1} of {questions.length} 
              </span> 
            )}
          </div> 

          <Squircle3DButton
            onClick={handleRestart}
            containerColor="#252A35"
            bevelColor="#2C3240"
            contentColor="#E8EAF0"
            height={44}
            shapeRadius={12}
            className="w-[50px]"
          >
            <RotateCcw className="w-4 h-4" /> 
          </Squircle3DButton> 
        </div> 

        <div className="w-full h-1.5 bg-[#E87A90]/20 overflow-hidden">
          <div
            className="h-full bg-[#E87A90] transition-all duration-300"
            style={{ width: `${progressPercent}%` }}
          /> 
        </div> 

        <div className="flex-1 overflow-y-auto px-5 py-4 flex flex-col gap-4">
          {!isCompleted ? (
            <>
              <div className="rounded-[16px] bg-[#252A35] border border-[#2C3240] p-4">
                <span className="text-[11px] font-bold tracking-wider uppercase text-[#E87A90] mb-1.5 block">
                  QUESTION {currentIndex + 1} 
                </span> 
                <p className="text-[17px] font-bold text-[#E8EAF0] leading-relaxed">
                  {currentQ.questionText || currentQ.question} 
                </p> 
              </div> 

              <div className="flex flex-col gap-2.5">
                {currentQ.options.map((opt, optIdx) => {
                  const isThisSelected = selectedOption === optIdx 
                  const isThisCorrect = optIdx === correctIndex 
                  const showResult = hasAnsweredCurrent 

                  let cardBg = '#252A35' 
                  let cardBorder = '#2C3240' 
                  let textColor = '#E8EAF0' 
                  let badgeBg = '#1E222B' 
                  let badgeText = '#9AA1AD' 

                  if (showResult) {
                    if (isThisCorrect) {
                      cardBg = '#1B3024' 
                      cardBorder = '#5FA77C' 
                      textColor = '#5FA77C' 
                      badgeBg = '#5FA77C' 
                      badgeText = '#FFFFFF' 
                    } else if (isThisSelected) {
                      cardBg = '#331C22' 
                      cardBorder = '#E87A90' 
                      textColor = '#E87A90' 
                      badgeBg = '#E87A90' 
                      badgeText = '#FFFFFF' 
                    } else {
                      cardBg = '#1E222B' 
                      cardBorder = '#20242E' 
                      textColor = '#6E7482' 
                    } 
                  } else if (isThisSelected) {
                    cardBg = '#2A1B20' 
                    cardBorder = '#E87A90' 
                    textColor = '#E87A90' 
                    badgeBg = '#E87A90' 
                    badgeText = '#FFFFFF' 
                  } 

                  return (
                    <div
                      key={optIdx}
                      onClick={() => handleSelect(optIdx)}
                      className="rounded-[14px] p-3.5 flex items-center justify-between cursor-pointer transition-all active:scale-[0.99]"
                      style={{
                        backgroundColor: cardBg, 
                        border: `1px solid ${cardBorder}`, 
                        boxShadow: isThisSelected ? '0 4px 14px rgba(0, 0, 0, 0.25)' : 'none' 
                      }}
                    >
                      <div className="flex items-center gap-3 flex-1">
                        <div
                          className="w-7 h-7 rounded-[8px] flex items-center justify-center text-xs font-bold shrink-0 transition-colors"
                          style={{
                            backgroundColor: badgeBg, 
                            color: badgeText 
                          }}
                        >
                          {OPTION_LABELS[optIdx]} 
                        </div> 
                        <span
                          className="text-[15px] font-medium leading-snug"
                          style={{ color: textColor }}
                        >
                          {opt} 
                        </span> 
                      </div> 

                      {showResult && (
                        <div className="ml-2 shrink-0">
                          {isThisCorrect ? (
                            <CheckCircle2 className="w-5 h-5 text-[#5FA77C]" /> 
                          ) : isThisSelected ? (
                            <XCircle className="w-5 h-5 text-[#E87A90]" /> 
                          ) : null} 
                        </div> 
                      )}
                    </div> 
                  ) 
                })}
              </div> 

              {hasAnsweredCurrent && currentQ.explanation && (
                <div className="rounded-[14px] bg-[#252A35] border border-[#2C3240] p-4 flex flex-col gap-1.5 animate-fadeIn">
                  <div className="flex items-center gap-2">
                    <Sparkles className="w-4 h-4 text-[#E87A90]" /> 
                    <span className="text-xs font-bold uppercase tracking-wider text-[#E87A90]">
                      EXPLANATION
                    </span> 
                  </div> 
                  <p className="text-sm text-[#CBD5E1] leading-relaxed">
                    {currentQ.explanation} 
                  </p> 
                  {onNavigateToJisho && (
                    <div className="pt-2">
                      <button
                        onClick={() => onNavigateToJisho(currentQ.questionText || currentQ.question)}
                        className="text-xs font-medium text-[#22D3EE] hover:underline"
                      >
                        Search question words in Jisho →
                      </button> 
                    </div> 
                  )}
                </div> 
              )}
            </> 
          ) : (
            <div className="flex-1 flex flex-col items-center justify-center text-center py-6 gap-4">
              <div
                className="w-20 h-20 rounded-full flex items-center justify-center shadow-2xl"
                style={{
                  backgroundColor: isPassed ? '#1B3024' : '#2A1B20', 
                  border: `2px solid ${isPassed ? '#5FA77C' : '#E87A90'}` 
                }}
              >
                <Trophy
                  className="w-10 h-10"
                  style={{ color: isPassed ? '#5FA77C' : '#E87A90' }}
                /> 
              </div> 

              <div>
                <h3 className="text-2xl font-extrabold text-[#E8EAF0]">
                  {isPassed ? 'Story Mastered!' : 'Keep Practicing!'} 
                </h3> 
                <p className="text-sm text-[#9AA1AD] mt-1">
                  You scored <span className="font-bold text-white text-base">{scorePercentage}%</span> ({correctCount}/{questions.length})
                </p> 
              </div> 

              <div
                className="px-3.5 py-1.5 rounded-full text-xs font-bold tracking-wide uppercase"
                style={{
                  backgroundColor: isPassed ? '#1B3024' : '#332717', 
                  color: isPassed ? '#5FA77C' : '#CFA055', 
                  border: `1px solid ${isPassed ? '#5FA77C' : '#CFA055'}` 
                }}
              >
                {isPassed ? 'PASSED & RECORDED' : 'NEEDS REVIEW'} 
              </div> 
            </div> 
          )}
        </div> 

        <div className="px-5 py-3 border-t border-[#20242E] flex items-center justify-between gap-3 bg-[#1A1D24]">
          {!isCompleted ? (
            <>
              <Squircle3DButton
                onClick={handlePrev}
                disabled={currentIndex === 0}
                containerColor="#252A35"
                bevelColor="#2C3240"
                contentColor="#E8EAF0"
                height={46}
                shapeRadius={12}
                className="flex-1"
              >
                <ArrowLeft className="w-4 h-4 mr-1.5" /> 
                Previous
              </Squircle3DButton> 

              <Squircle3DButton
                onClick={handleNext}
                disabled={!hasAnsweredCurrent}
                containerColor="#E87A90"
                bevelColor="#A8475B"
                contentColor="#FFFFFF"
                height={46}
                shapeRadius={12}
                className="flex-1"
              >
                {currentIndex < questions.length - 1 ? (
                  <>
                    Next
                    <ArrowRight className="w-4 h-4 ml-1.5" /> 
                  </> 
                ) : (
                  'Complete Test' 
                )}
              </Squircle3DButton> 
            </> 
          ) : (
            <>
              <Squircle3DButton
                onClick={handleRestart}
                containerColor="#252A35"
                bevelColor="#2C3240"
                contentColor="#E8EAF0"
                height={46}
                shapeRadius={12}
                className="flex-1"
              >
                <RotateCcw className="w-4 h-4 mr-1.5" /> 
                Try Again
              </Squircle3DButton> 

              <Squircle3DButton
                onClick={onClose}
                containerColor="#E87A90"
                bevelColor="#A8475B"
                contentColor="#FFFFFF"
                height={46}
                shapeRadius={12}
                className="flex-1"
              >
                <Check className="w-4 h-4 mr-1.5" /> 
                Done
              </Squircle3DButton> 
            </> 
          )}
        </div> 
      </div> 
    </div> 
  ) 
} 
