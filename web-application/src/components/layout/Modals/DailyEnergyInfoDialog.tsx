import React from 'react' 
import { Zap } from 'lucide-react' 

interface DailyEnergyInfoDialogProps {
  storyEnergy: number 
  onDismiss: () => void 
} 

export const DailyEnergyInfoDialog: React.FC<DailyEnergyInfoDialogProps> = ({
  storyEnergy, 
  onDismiss 
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-sm rounded-[20px] bg-[#1E222B] border border-[#2C3240] p-[22px] shadow-2xl flex flex-col gap-3.5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <Zap className="w-6 h-6 text-[#9678B6] fill-[#9678B6]" /> 
            <h2 className="text-lg font-bold text-[#E2E8F0]">
              Daily Story Energy
            </h2> 
          </div> 

          <div className="px-2 py-1 rounded-[8px] bg-[#2E1E3B] border border-[#9678B6]/50 text-[#9678B6] text-xs font-bold">
            {storyEnergy} / 7 Available
          </div> 
        </div> 

        <p className="text-[13px] leading-5 text-[#94A3B8]">
          You have 7 daily story energy points. Each new AI story you generate consumes 1 energy point.
          <br /><br />
          Energy automatically replenishes back to 7 every day at midnight, giving you fresh story reading daily!
        </p> 

        <button
          onClick={onDismiss}
          className="w-full py-2.5 rounded-[12px] bg-[#9678B6] hover:bg-[#9678B6]/90 text-white font-bold text-sm active:scale-95 transition-all shadow"
        >
          Got It
        </button> 
      </div> 
    </div> 
  ) 
} 
