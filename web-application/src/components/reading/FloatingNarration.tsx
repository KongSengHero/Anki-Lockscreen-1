import React from 'react' 
import { Play, Pause, ChevronLeft, ChevronRight, Activity, Loader2 } from 'lucide-react' 

interface FloatingNarrationProps {
  visible: boolean 
  isPlaying: boolean 
  isPaused: boolean 
  isSynthesizing?: boolean 
  currentSentenceIndex: number 
  totalSentences: number 
  currentSpeed: number 
  onPlayPause: () => void 
  onSkipPrevious: () => void 
  onSkipNext: () => void 
  onSpeedCycle: () => void 
} 

export const FloatingNarration: React.FC<FloatingNarrationProps> = ({
  visible, 
  isPlaying, 
  isPaused, 
  isSynthesizing = false, 
  currentSentenceIndex, 
  totalSentences, 
  currentSpeed, 
  onPlayPause, 
  onSkipPrevious, 
  onSkipNext, 
  onSpeedCycle 
}) => {
  return (
    <div
      className={`fixed bottom-28 left-1/2 -translate-x-1/2 z-40 transition-all duration-200 ${
        visible 
          ? 'translate-y-0 opacity-100 pointer-events-auto' 
          : 'translate-y-4 opacity-0 pointer-events-none' 
      }`}
    >
      <div
        className="flex flex-row items-center gap-1.5 px-2.5 py-1.5 rounded-[12px] shadow-2xl backdrop-blur-md"
        style={{
          backgroundColor: 'rgba(30, 34, 43, 0.92)', 
          border: '1px solid rgba(232, 122, 144, 0.35)', 
          boxShadow: '0 8px 24px rgba(0, 0, 0, 0.45)' 
        }}
      >
        <button
          onClick={onSkipPrevious}
          disabled={isSynthesizing || currentSentenceIndex <= 0}
          className="w-9 h-9 flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-90 disabled:opacity-30 disabled:pointer-events-none transition-all"
          title="Previous sentence"
        >
          <ChevronLeft className="w-[19px] h-[19px]" /> 
        </button> 

        <button
          onClick={onPlayPause}
          disabled={isSynthesizing}
          className="w-9 h-9 flex items-center justify-center active:scale-90 transition-all"
          title={isPlaying ? 'Pause' : 'Play'}
        >
          <div className="w-[30px] h-[30px] rounded-full bg-[#E87A90] flex items-center justify-center shadow-sm">
            {isPlaying ? (
              <Pause className="w-4 h-4 fill-white text-white" /> 
            ) : (
              <Play className="w-4 h-4 fill-white text-white translate-x-0.5" /> 
            )}
          </div> 
        </button> 

        <button
          onClick={onSkipNext}
          disabled={isSynthesizing || currentSentenceIndex >= totalSentences - 1}
          className="w-9 h-9 flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-90 disabled:opacity-30 disabled:pointer-events-none transition-all"
          title="Next sentence"
        >
          <ChevronRight className="w-[19px] h-[19px]" /> 
        </button> 

        <button
          onClick={onSpeedCycle}
          className="px-2 py-1 rounded-[8px] bg-[#2A1B20]/65 text-[#E87A90] font-bold text-[11px] leading-none active:scale-90 transition-all"
          title="Playback speed"
        >
          {currentSpeed}x
        </button> 

        <div className="w-8 h-8 flex items-center justify-center text-[#E87A90]">
          {isSynthesizing ? (
            <Loader2 className="w-4 h-4 animate-spin text-[#E87A90]" /> 
          ) : (
            <Activity className={`w-4 h-4 ${(isPlaying && !isPaused) ? 'animate-pulse' : 'opacity-80'}`} /> 
          )}
        </div> 
      </div> 
    </div> 
  ) 
} 
