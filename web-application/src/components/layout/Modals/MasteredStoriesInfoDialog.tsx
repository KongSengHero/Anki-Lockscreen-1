import React from 'react' 
import { BookOpen } from 'lucide-react' 

interface MasteredStoriesInfoDialogProps {
  completedStoriesCount: number 
  onDismiss: () => void 
} 

export const MasteredStoriesInfoDialog: React.FC<MasteredStoriesInfoDialogProps> = ({
  completedStoriesCount, 
  onDismiss 
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-sm rounded-[20px] bg-[#1E222B] border border-[#2C3240] p-[22px] shadow-2xl flex flex-col gap-3.5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <BookOpen className="w-6 h-6 text-[#22D3EE]" /> 
            <h2 className="text-lg font-bold text-[#E2E8F0]">
              Mastered Stories
            </h2> 
          </div> 

          <div className="px-2 py-1 rounded-[8px] bg-[#162B35] border border-[#22D3EE]/50 text-[#22D3EE] text-xs font-bold">
            {completedStoriesCount} Passed
          </div> 
        </div> 

        <p className="text-[13px] leading-5 text-[#94A3B8]">
          This counter tracks the total number of stories whose comprehension quiz you have passed with a score of 80% or higher.
          <br /><br />
          Completing tests reinforces your vocabulary and reading mastery in real Japanese context. Keep reading and taking tests to expand your collection!
        </p> 

        <button
          onClick={onDismiss}
          className="w-full py-2.5 rounded-[12px] bg-[#22D3EE] hover:bg-[#22D3EE]/90 text-black font-bold text-sm active:scale-95 transition-all shadow"
        >
          Got It
        </button> 
      </div> 
    </div> 
  ) 
} 
