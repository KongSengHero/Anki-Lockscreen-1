import React from 'react' 
import { Layers, Check, X } from 'lucide-react' 

interface LockscreenConfigSheetProps {
  isMusicStyle: boolean 
  onToggleStyle: (isMusic: boolean) => void 
  onDismiss: () => void 
} 

export const LockscreenConfigSheet: React.FC<LockscreenConfigSheetProps> = ({
  isMusicStyle, 
  onToggleStyle, 
  onDismiss 
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/75 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-md max-h-[85vh] overflow-y-auto rounded-t-[24px] sm:rounded-[24px] bg-[#121418] border border-[#2C3240] p-6 shadow-2xl flex flex-col gap-4">
        <div className="flex items-center justify-between pb-2 border-b border-[#20242E]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-[12px] bg-[#9678B6]/15 flex items-center justify-center text-[#9678B6]">
              <Layers className="w-5 h-5" /> 
            </div> 
            <div>
              <h2 className="text-base font-bold text-[#E2E8F0]">
                Lockscreen Config
              </h2> 
              <p className="text-xs text-[#94A3B8]">
                Display Layout & Card Presentation
              </p> 
            </div> 
          </div> 

          <button
            onClick={onDismiss}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-[#94A3B8] hover:text-[#E2E8F0] hover:bg-white/5 active:scale-95"
          >
            <X className="w-5 h-5" /> 
          </button> 
        </div> 

        <div className="flex flex-col gap-2.5">
          <h3 className="text-xs font-bold uppercase text-[#94A3B8] tracking-wider">
            Layout Style
          </h3> 

          <div
            onClick={() => onToggleStyle(true)}
            className={`flex items-center justify-between p-3.5 rounded-[14px] cursor-pointer transition-all border ${
              isMusicStyle 
                ? 'bg-[#2E1E3B] border-[#9678B6] shadow' 
                : 'bg-[#1E222B] border-[#2C3240] hover:border-[#9678B6]/40' 
            }`}
          >
            <div>
              <h4 className="text-sm font-bold text-[#E2E8F0]">
                Music Player Style
              </h4> 
              <p className="text-[11px] text-[#94A3B8]">
                Compact media artwork card with lockscreen audio controls
              </p> 
            </div> 
            <div className={`w-5 h-5 rounded-full flex items-center justify-center border ${
              isMusicStyle 
                ? 'bg-[#9678B6] border-[#9678B6] text-white' 
                : 'border-[#64748B]' 
            }`}>
              {isMusicStyle && <Check className="w-3 h-3 stroke-[3]" />}
            </div> 
          </div> 

          <div
            onClick={() => onToggleStyle(false)}
            className={`flex items-center justify-between p-3.5 rounded-[14px] cursor-pointer transition-all border ${
              !isMusicStyle 
                ? 'bg-[#2E1E3B] border-[#9678B6] shadow' 
                : 'bg-[#1E222B] border-[#2C3240] hover:border-[#9678B6]/40' 
            }`}
          >
            <div>
              <h4 className="text-sm font-bold text-[#E2E8F0]">
                Classic Card Style
              </h4> 
              <p className="text-[11px] text-[#94A3B8]">
                Full flashcard front and back view with SRS grading buttons
              </p> 
            </div> 
            <div className={`w-5 h-5 rounded-full flex items-center justify-center border ${
              !isMusicStyle 
                ? 'bg-[#9678B6] border-[#9678B6] text-white' 
                : 'border-[#64748B]' 
            }`}>
              {!isMusicStyle && <Check className="w-3 h-3 stroke-[3]" />}
            </div> 
          </div> 
        </div> 

        <button
          onClick={onDismiss}
          className="w-full py-2.5 rounded-[12px] bg-[#252A35] hover:bg-[#252A35]/80 text-[#E2E8F0] font-bold text-sm active:scale-95 transition-all mt-2"
        >
          Done
        </button> 
      </div> 
    </div> 
  ) 
} 
