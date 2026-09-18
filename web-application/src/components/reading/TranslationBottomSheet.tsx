import React, { useState } from 'react' 
import { X, Copy, Check, Volume2, Search, Languages } from 'lucide-react' 
import { Squircle3DButton } from '../blossom/Squircle3DButton' 

interface TranslationBottomSheetProps {
  japaneseText: string 
  englishText: string 
  onDismiss: () => void 
  onNavigateToJisho?: (word: string) => void 
} 

export const TranslationBottomSheet: React.FC<TranslationBottomSheetProps> = ({
  japaneseText, 
  englishText, 
  onDismiss, 
  onNavigateToJisho 
}) => {
  const [copiedJp, setCopiedJp] = useState(false) 
  const [copiedEn, setCopiedEn] = useState(false) 

  const handleCopyJp = () => {
    navigator.clipboard.writeText(japaneseText) 
    setCopiedJp(true) 
    setTimeout(() => setCopiedJp(false), 2000) 
  } 

  const handleCopyEn = () => {
    navigator.clipboard.writeText(englishText) 
    setCopiedEn(true) 
    setTimeout(() => setCopiedEn(false), 2000) 
  } 

  const handlePlayAudio = () => {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel() 
      const utterance = new SpeechSynthesisUtterance(japaneseText) 
      utterance.lang = 'ja-JP' 
      utterance.rate = 0.95 
      window.speechSynthesis.speak(utterance) 
    } 
  } 

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-md animate-fadeIn">
      <div className="w-full max-w-md max-h-[85vh] overflow-y-auto rounded-t-[26px] sm:rounded-[26px] bg-[#1E222B] border border-[#2C3240] p-6 shadow-2xl flex flex-col gap-4">
        <div className="flex items-center justify-between pb-2 border-b border-[#20242E]">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-[10px] bg-[#22D3EE]/15 flex items-center justify-center text-[#22D3EE]">
              <Languages className="w-5 h-5" /> 
            </div> 
            <h2 className="text-lg font-bold text-[#E8EAF0]">
              Translation
            </h2> 
          </div> 

          <button
            onClick={onDismiss}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-95"
          >
            <X className="w-5 h-5" /> 
          </button> 
        </div> 

        <div className="rounded-[16px] bg-[#252A35] border border-[#2C3240] p-4 flex flex-col gap-2">
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-bold uppercase tracking-wider text-[#9AA1AD]">
              JAPANESE
            </span> 
            <div className="flex items-center gap-1">
              <button
                onClick={handlePlayAudio}
                className="w-7 h-7 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-90"
                title="Speak Japanese"
              >
                <Volume2 className="w-4 h-4" /> 
              </button> 
              <button
                onClick={handleCopyJp}
                className="w-7 h-7 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-90"
                title="Copy Japanese"
              >
                {copiedJp ? <Check className="w-3.5 h-3.5 text-[#5FA77C]" /> : <Copy className="w-3.5 h-3.5" />} 
              </button> 
            </div> 
          </div> 
          <p className="text-[16px] text-[#E8EAF0] leading-relaxed font-medium">
            {japaneseText} 
          </p> 
        </div> 

        <div className="rounded-[16px] bg-[#252A35] border border-[#2C3240] p-4 flex flex-col gap-2">
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-bold uppercase tracking-wider text-[#9AA1AD]">
              ENGLISH MEANING
            </span> 
            <button
              onClick={handleCopyEn}
              className="w-7 h-7 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-90"
              title="Copy English"
            >
              {copiedEn ? <Check className="w-3.5 h-3.5 text-[#5FA77C]" /> : <Copy className="w-3.5 h-3.5" />} 
            </button> 
          </div> 
          <p className="text-[15px] text-[#CBD5E1] leading-relaxed">
            {englishText} 
          </p> 
        </div> 

        {onNavigateToJisho && (
          <Squircle3DButton
            onClick={() => {
              onDismiss() 
              onNavigateToJisho(japaneseText) 
            }}
            containerColor="#252A35"
            bevelColor="#2C3240"
            contentColor="#E8EAF0"
            height={46}
            shapeRadius={12}
            className="w-full"
          >
            <Search className="w-4 h-4 mr-2 text-[#22D3EE]" /> 
            Look up in Jisho
          </Squircle3DButton> 
        )}
      </div> 
    </div> 
  ) 
} 
