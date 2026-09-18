import React from 'react' 
import { X, Flame, Award } from 'lucide-react' 

interface StreakModalProps {
  streakCount: number 
  onClose: () => void 
} 

export const StreakModal: React.FC<StreakModalProps> = ({ streakCount, onClose }) => {
  const days = ['M', 'T', 'W', 'T', 'F', 'S', 'S'] 

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div 
        className="fixed inset-0 bg-black/70 backdrop-blur-sm" 
        onClick={onClose} 
      /> 

      <div className="relative w-full max-w-sm bg-[#0D111A] border border-white/15 rounded-3xl p-6 shadow-2xl z-10 text-center animate-in zoom-in-95 duration-150">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-1.5 rounded-xl bg-white/5 text-slate-400 hover:text-white"
        >
          <X className="w-4 h-4" /> 
        </button> 

        <div className="w-16 h-16 rounded-3xl bg-orange-500/20 border border-orange-500/30 text-orange-400 flex items-center justify-center mx-auto mb-4">
          <Flame className="w-9 h-9 fill-current" /> 
        </div> 

        <h3 className="text-3xl font-extrabold text-white mb-1">
          {streakCount} Day Streak
        </h3> 
        <p className="text-xs text-slate-400 mb-6">
          You've practiced Japanese consistently every day!
        </p> 

        <div className="flex justify-between items-center p-3 rounded-2xl bg-white/[0.03] border border-white/10 mb-6">
          {days.map((day, idx) => (
            <div key={idx} className="flex flex-col items-center gap-1.5">
              <span className="text-[10px] text-slate-400 font-semibold">{day}</span> 
              <div 
                className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold ${
                  idx < 5 
                    ? 'bg-orange-500 text-white shadow-sm shadow-orange-500/40' 
                    : 'bg-white/5 text-slate-500' 
                }`}
              >
                {idx < 5 ? '✓' : ''} 
              </div> 
            </div> 
          ))}
        </div> 

        <div className="p-3.5 rounded-2xl bg-orange-500/10 border border-orange-500/20 text-xs text-orange-300 flex items-center gap-2 text-left mb-5">
          <Award className="w-5 h-5 flex-shrink-0 text-orange-400" /> 
          <span>Complete 1 story or review 5 cards every day before midnight to keep your streak alive.</span> 
        </div> 

        <button
          onClick={onClose}
          className="w-full py-3 rounded-xl bg-gradient-to-r from-orange-500 to-amber-500 text-white font-semibold text-xs shadow-md transition-all active:scale-95"
        >
          Got it!
        </button> 
      </div> 
    </div> 
  ) 
} 
