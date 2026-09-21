import React, { useState } from 'react' 
import { Image as ImageIcon, Copy, Check, Sparkles, Languages, Volume2, Square, Loader2 } from 'lucide-react' 

interface FloatingActionsProps {
  visible: boolean 
  hasWallpaper: boolean 
  showFurigana: boolean 
  highlightVocab: boolean 
  isPlayingAudio: boolean 
  isAudioPaused: boolean 
  isSynthesizingAudio?: boolean 
  onWallpaperClick: () => void 
  onCopyStory: () => void 
  onToggleHighlightVocab: () => void 
  onToggleFurigana: () => void 
  onOpenTranslation: () => void 
  onToggleAudio: () => void 
} 

export const FloatingActions: React.FC<FloatingActionsProps> = ({
  visible, 
  hasWallpaper, 
  showFurigana, 
  highlightVocab, 
  isPlayingAudio, 
  isAudioPaused, 
  isSynthesizingAudio = false, 
  onWallpaperClick, 
  onCopyStory, 
  onToggleHighlightVocab, 
  onToggleFurigana, 
  onOpenTranslation, 
  onToggleAudio 
}) => {
  const [copied, setCopied] = useState(false) 

  const handleCopy = () => {
    onCopyStory() 
    setCopied(true) 
    setTimeout(() => setCopied(false), 2000) 
  } 

  return (
    <div
      className={`fixed top-16 right-3 z-40 transition-all duration-200 ${
        visible 
          ? 'translate-y-0 opacity-100 pointer-events-auto' 
          : '-translate-y-4 opacity-0 pointer-events-none' 
      }`}
    >
      <div
        className="flex flex-row items-center gap-1 px-2.5 py-1.5 rounded-[12px] shadow-2xl backdrop-blur-md"
        style={{
          backgroundColor: 'rgba(30, 34, 43, 0.92)', 
          border: '1px solid #2C3240', 
          boxShadow: '0 8px 24px rgba(0, 0, 0, 0.45)' 
        }}
      >
        <button
          onClick={onToggleAudio}
          className="w-9 h-9 flex items-center justify-center active:scale-90 transition-all"
          title="Toggle Narration"
        >
          {isSynthesizingAudio ? (
            <Loader2 className="w-[18px] h-[18px] animate-spin text-[#E87A90]" /> 
          ) : (isPlayingAudio || isAudioPaused) ? (
            <Square className="w-[18px] h-[18px] text-[#E87A90] fill-current" /> 
          ) : (
            <Volume2 className="w-[19px] h-[19px] text-[#9AA1AD] hover:text-[#E8EAF0]" /> 
          )}
        </button> 

        <button
          onClick={onOpenTranslation}
          className="w-9 h-9 flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-90 transition-all"
          title="Translate Story"
        >
          <Languages className="w-[19px] h-[19px]" /> 
        </button> 

        <button
          onClick={onToggleFurigana}
          className={`w-9 h-9 flex items-center justify-center font-bold text-base active:scale-90 transition-all select-none ${
            showFurigana ? 'text-[#E87A90]' : 'text-[#9AA1AD] hover:text-[#E8EAF0]' 
          }`}
          title="Toggle Furigana"
        >
          ふ
        </button> 

        <button
          onClick={onToggleHighlightVocab}
          className={`w-9 h-9 flex items-center justify-center active:scale-90 transition-all ${
            highlightVocab ? 'text-[#E87A90]' : 'text-[#9AA1AD] hover:text-[#E8EAF0]' 
          }`}
          title="Toggle Vocabulary Highlight"
        >
          <Sparkles className="w-[19px] h-[19px]" /> 
        </button> 

        <button
          onClick={handleCopy}
          className="w-9 h-9 flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-90 transition-all"
          title="Copy Story"
        >
          {copied ? (
            <Check className="w-[19px] h-[19px] text-[#5FA77C]" /> 
          ) : (
            <Copy className="w-[19px] h-[19px]" /> 
          )}
        </button> 

        <button
          onClick={onWallpaperClick}
          className={`w-9 h-9 flex items-center justify-center active:scale-90 transition-all ${
            hasWallpaper ? 'text-[#E87A90]' : 'text-[#9AA1AD] hover:text-[#E8EAF0]' 
          }`}
          title="Change Wallpaper"
        >
          <ImageIcon className="w-[19px] h-[19px]" /> 
        </button> 
      </div> 
    </div> 
  ) 
} 
