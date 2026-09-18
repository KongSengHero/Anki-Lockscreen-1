import React from 'react' 
import { Flame, BookOpen, Zap } from 'lucide-react' 

interface MinimalTopBarProps {
  streakCount: number 
  isStreakActive?: boolean 
  completedStoriesCount: number 
  storyEnergy: number 
  onStreakClick: () => void 
  onBookClick: () => void 
  onEnergyClick: () => void 
} 

export const MinimalTopBar: React.FC<MinimalTopBarProps> = ({
  streakCount, 
  isStreakActive = false, 
  completedStoriesCount, 
  storyEnergy, 
  onStreakClick, 
  onBookClick, 
  onEnergyClick 
}) => {
  return (
    <div className="fixed top-0 left-0 right-0 z-30 pointer-events-none">
      <div
        className="w-full pt-safe pb-5 transition-colors duration-500"
        style={{
          background: 'linear-gradient(to bottom, rgba(18, 20, 24, 0.95) 0%, rgba(18, 20, 24, 0.85) 45%, rgba(18, 20, 24, 0.35) 75%, transparent 100%)' 
        }}
      >
        <div className="max-w-md mx-auto px-5 py-2.5 flex items-center justify-between pointer-events-auto">
          <span className="text-[20px] font-extrabold text-[#E8EAF0] tracking-tight select-none">
            ブロッサム
          </span> 

          <div className="flex items-center gap-2">
            <button
              onClick={onStreakClick}
              className="flex items-center px-2.5 py-1 rounded-[10px] bg-[#1E222B] border border-[#2C3240] hover:border-[#FF5722]/50 transition-all active:scale-95 shadow-sm"
              style={{
                boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.12)' 
              }}
            >
              <Flame
                className={`w-3.5 h-3.5 ${
                  isStreakActive ? 'text-[#FF5722] fill-[#FF5722]' : 'text-[#64748B]' 
                }`}
              /> 
              <span className="ml-1.5 text-xs font-bold text-[#E8EAF0]">
                {streakCount} 
              </span> 
            </button> 

            <button
              onClick={onBookClick}
              className="flex items-center px-2.5 py-1 rounded-[10px] bg-[#1E222B] border border-[#2C3240] hover:border-[#22D3EE]/50 transition-all active:scale-95 shadow-sm"
              style={{
                boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.12)' 
              }}
            >
              <BookOpen className="w-[13px] h-[13px] text-[#22D3EE]" /> 
              <span className="ml-1.5 text-xs font-bold text-[#E8EAF0]">
                {completedStoriesCount} 
              </span> 
            </button> 

            <button
              onClick={onEnergyClick}
              className="flex items-center px-2.5 py-1 rounded-[10px] bg-[#1E222B] border border-[#2C3240] hover:border-[#9678B6]/50 transition-all active:scale-95 shadow-sm"
              style={{
                boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.12)' 
              }}
            >
              <Zap className="w-3.5 h-3.5 text-[#9678B6] fill-[#9678B6]" /> 
              <span className="ml-1.5 text-xs font-bold text-[#E8EAF0]">
                {storyEnergy} 
              </span> 
            </button> 
          </div> 
        </div> 
      </div> 
    </div> 
  ) 
} 
