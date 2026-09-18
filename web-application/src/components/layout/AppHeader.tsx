import React from 'react' 
import { Flame, Zap, BookOpen, Settings } from 'lucide-react' 

interface AppHeaderProps {
  streakCount: number 
  storiesReadCount: number 
  energyCount: number 
  onOpenStreak: () => void 
  onOpenEnergy: () => void 
  onOpenSettings: () => void 
} 

export const AppHeader: React.FC<AppHeaderProps> = ({
  streakCount, 
  storiesReadCount, 
  energyCount, 
  onOpenStreak, 
  onOpenEnergy, 
  onOpenSettings 
}) => {
  return (
    <header className="sticky top-0 z-30 w-full bg-[#161922]/95 backdrop-blur-xl border-b border-[#2C3240] safe-top px-4 py-2.5">
      <div className="max-w-4xl mx-auto flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-xl">🌸</span> 
          <span className="font-extrabold text-lg text-[#E8EAF0] tracking-tight">
            Blossom
          </span> 
        </div> 

        <div className="flex items-center gap-2 sm:gap-3">
          <button
            onClick={onOpenStreak}
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#332717] hover:bg-[#332717]/80 border border-[#CFA055]/40 text-[#CFA055] text-xs font-bold transition-all active:scale-95 shadow-sm"
            title="Daily Streak"
          >
            <Flame className="w-3.5 h-3.5 fill-current text-[#CFA055]" /> 
            <span>{streakCount}</span> 
          </button> 

          <div
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#162B35] border border-[#22D3EE]/40 text-[#22D3EE] text-xs font-bold shadow-sm"
            title="Stories Read"
          >
            <BookOpen className="w-3.5 h-3.5 text-[#22D3EE]" /> 
            <span>{storiesReadCount}</span> 
          </div> 

          <button
            onClick={onOpenEnergy}
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#291F35] hover:bg-[#291F35]/80 border border-[#9678B6]/40 text-[#9678B6] text-xs font-bold transition-all active:scale-95 shadow-sm"
            title="Study Energy"
          >
            <Zap className="w-3.5 h-3.5 fill-current text-[#9678B6]" /> 
            <span>{energyCount}</span> 
          </button> 

          <button
            onClick={onOpenSettings}
            className="p-1.5 rounded-full bg-[#1E222B] hover:bg-[#252A35] border border-[#2C3240] text-[#9AA1AD] hover:text-[#E8EAF0] transition-colors active:scale-95"
            title="Settings"
          >
            <Settings className="w-4 h-4" /> 
          </button> 
        </div> 
      </div> 
    </header> 
  ) 
} 
