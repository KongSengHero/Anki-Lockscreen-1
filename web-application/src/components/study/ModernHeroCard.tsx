import React from 'react' 
import { Zap, Lock } from 'lucide-react' 

interface ModernHeroCardProps {
  isEnabled: boolean 
  onToggle: (enabled: boolean) => void 
} 

export const ModernHeroCard: React.FC<ModernHeroCardProps> = ({
  isEnabled, 
  onToggle 
}) => {
  return (
    <div
      className={`w-full rounded-[24px] p-5 transition-all duration-300 border ${
        isEnabled 
          ? 'bg-gradient-to-r from-[#161920] via-[#7C8CF8]/15 to-[#1E222B] border-[#7C8CF8]/60 shadow-lg shadow-[#7C8CF8]/10' 
          : 'bg-gradient-to-r from-[#1E222B] to-[#161920] border-[#20242E]' 
      }`}
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3.5">
          <div
            className={`w-11 h-11 rounded-[14px] flex items-center justify-center transition-colors ${
              isEnabled ? 'bg-[#1B1F38] text-[#7C8CF8]' : 'bg-[#252A35] text-[#64748B]' 
            }`}
          >
            {isEnabled ? (
              <Zap className="w-[22px] h-[22px] fill-current" /> 
            ) : (
              <Lock className="w-[22px] h-[22px]" /> 
            )}
          </div> 

          <div>
            <h3 className="text-base font-bold text-[#E2E8F0]">
              {isEnabled ? 'Lockscreen Active' : 'Lockscreen Inactive'} 
            </h3> 
            <p className="text-xs text-[#94A3B8]">
              {isEnabled 
                ? 'Review cards on your lockscreen' 
                : 'Toggle switch to start practicing'} 
            </p> 
          </div> 
        </div> 

        <label className="relative inline-flex items-center cursor-pointer">
          <input
            type="checkbox"
            checked={isEnabled}
            onChange={(e) => onToggle(e.target.checked)}
            className="sr-only peer"
          /> 
          <div className="w-12 h-7 bg-[#252A35] peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[3px] after:left-[4px] after:bg-white after:rounded-full after:h-[22px] after:w-[22px] after:transition-all peer-checked:bg-[#7C8CF8]" /> 
        </label> 
      </div> 
    </div> 
  ) 
} 
