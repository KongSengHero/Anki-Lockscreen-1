import React, { useState } from 'react' 
import { Flame, Check, ChevronLeft, ChevronRight } from 'lucide-react' 

interface StreakCalendarDialogProps {
  streakCount: number 
  isStreakActive?: boolean 
  activeDates?: Set<string> 
  onDismiss: () => void 
} 

export const StreakCalendarDialog: React.FC<StreakCalendarDialogProps> = ({
  streakCount, 
  isStreakActive = false, 
  activeDates = new Set(), 
  onDismiss 
}) => {
  const [monthOffset, setMonthOffset] = useState(0) 

  const dateObj = new Date() 
  dateObj.setMonth(dateObj.getMonth() + monthOffset) 
  const year = dateObj.getFullYear() 
  const month = dateObj.getMonth() 

  const monthTitle = dateObj.toLocaleDateString('en-US', {
    month: 'long', 
    year: 'numeric' 
  }) 

  const now = new Date() 
  const todayStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}` 

  const firstDayOfWeek = (new Date(year, month, 1).getDay() + 6) % 7 
  const daysInMonth = new Date(year, month + 1, 0).getDate() 
  const dayNames = ['M', 'T', 'W', 'T', 'F', 'S', 'S'] 

  const totalCells = Math.ceil((firstDayOfWeek + daysInMonth) / 7) * 7 

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-sm rounded-[20px] bg-[#1E222B] border border-[#2C3240] p-5 shadow-2xl flex flex-col gap-3.5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <Flame className="w-6 h-6 text-[#FF5722] fill-[#FF5722]" /> 
            <h2 className="text-lg font-bold text-[#E2E8F0]">
              Daily Streak
            </h2> 
          </div> 

          <div className="px-2 py-1 rounded-[8px] bg-[#381E17] border border-[#FF5722]/50 text-[#FF5722] text-xs font-bold">
            {streakCount} Days
          </div> 
        </div> 

        <div className="flex items-center px-3 py-2.5 rounded-[12px] bg-[#252A35] border border-[#20242E]">
          {isStreakActive ? (
            <Check className="w-4 h-4 text-[#5FA77C] shrink-0" /> 
          ) : (
            <Flame className="w-4 h-4 text-[#FF5722] shrink-0" /> 
          )}
          <p className="ml-2 text-xs text-[#94A3B8]">
            {isStreakActive 
              ? 'Streak active today! Awesome work.' 
              : 'Pass a story quiz or review 10 cards to keep your streak!'}
          </p> 
        </div> 

        <div className="flex items-center justify-between">
          <button
            onClick={() => setMonthOffset(prev => prev - 1)}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-[#94A3B8] hover:text-[#E2E8F0] hover:bg-white/5 active:scale-95"
          >
            <ChevronLeft className="w-[18px] h-[18px]" /> 
          </button> 

          <span className="text-sm font-bold text-[#E2E8F0]">
            {monthTitle} 
          </span> 

          <button
            onClick={() => monthOffset < 0 && setMonthOffset(prev => prev + 1)}
            disabled={monthOffset >= 0}
            className={`w-8 h-8 rounded-lg flex items-center justify-center ${
              monthOffset < 0 
                ? 'text-[#94A3B8] hover:text-[#E2E8F0] hover:bg-white/5 active:scale-95' 
                : 'text-[#64748B]/30' 
            }`}
          >
            <ChevronRight className="w-[18px] h-[18px]" /> 
          </button> 
        </div> 

        <div className="grid grid-cols-7 gap-1 text-center">
          {dayNames.map((d, i) => (
            <span key={i} className="text-[11px] font-semibold text-[#64748B]">
              {d} 
            </span> 
          ))}
        </div> 

        <div className="grid grid-cols-7 gap-1">
          {Array.from({ length: totalCells }).map((_, idx) => {
            const dayNum = idx - firstDayOfWeek + 1 
            if (dayNum < 1 || dayNum > daysInMonth) {
              return <div key={idx} className="h-[34px]" /> 
            } 

            const dateKey = `${year}-${String(month + 1).padStart(2, '0')}-${String(dayNum).padStart(2, '0')}` 
            const isCompleted = activeDates.has(dateKey) 
            const isToday = dateKey === todayStr 

            return (
              <div
                key={idx}
                className={`h-[34px] rounded-[8px] flex items-center justify-center text-xs ${
                  isCompleted 
                    ? 'bg-[#FF5722] text-white font-bold' 
                    : isToday 
                    ? 'bg-[#FF5722]/15 text-[#FF5722] font-bold border border-[#FF5722]/60' 
                    : 'text-[#E2E8F0]' 
                }`}
              >
                {dayNum} 
              </div> 
            ) 
          })}
        </div> 

        <button
          onClick={onDismiss}
          className="w-full py-2.5 rounded-[12px] bg-[#FF5722] hover:bg-[#FF5722]/90 text-white font-bold text-sm active:scale-95 transition-all shadow"
        >
          Close
        </button> 
      </div> 
    </div> 
  ) 
} 
