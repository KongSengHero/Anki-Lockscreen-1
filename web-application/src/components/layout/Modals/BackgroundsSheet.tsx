import React from 'react' 
import { Palette, Check, X } from 'lucide-react' 

interface BackgroundsSheetProps {
  currentTheme: string 
  onSelectTheme: (themeId: string) => void 
  onDismiss: () => void 
} 

const THEMES = [
  { id: 'midnight', name: 'Midnight Bloom', bg: '#121418', border: '#7C8CF8', desc: 'Deep Obsidian with Slate Blue accents' }, 
  { id: 'matcha', name: 'Matcha Garden', bg: '#101C16', border: '#5FA77C', desc: 'Botanical Emerald & Matcha Sage' }, 
  { id: 'sakura', name: 'Sakura Spring', bg: '#1C1217', border: '#E87A90', desc: 'Cherry Blossom Rose & Deep Mahogany' }, 
  { id: 'slate', name: 'Slate Dusk', bg: '#141724', border: '#22D3EE', desc: 'Cyan Glow with Nightfall Indigo' } 
] 

export const BackgroundsSheet: React.FC<BackgroundsSheetProps> = ({
  currentTheme, 
  onSelectTheme, 
  onDismiss 
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/75 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-md max-h-[85vh] overflow-y-auto rounded-t-[24px] sm:rounded-[24px] bg-[#121418] border border-[#2C3240] p-6 shadow-2xl flex flex-col gap-4">
        <div className="flex items-center justify-between pb-2 border-b border-[#20242E]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-[12px] bg-[#7C8CF8]/15 flex items-center justify-center text-[#7C8CF8]">
              <Palette className="w-5 h-5" /> 
            </div> 
            <div>
              <h2 className="text-base font-bold text-[#E2E8F0]">
                Backgrounds Studio
              </h2> 
              <p className="text-xs text-[#94A3B8]">
                Card & App Themes
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
            Palette Themes
          </h3> 

          {THEMES.map(theme => {
            const isSelected = currentTheme === theme.id 
            return (
              <div
                key={theme.id}
                onClick={() => onSelectTheme(theme.id)}
                className={`flex items-center justify-between p-3.5 rounded-[14px] cursor-pointer transition-all border ${
                  isSelected 
                    ? 'bg-[#1E222B] border-white/60 shadow-lg' 
                    : 'bg-[#1E222B]/60 border-[#2C3240] hover:border-white/20' 
                }`}
              >
                <div className="flex items-center gap-3">
                  <div
                    className="w-8 h-8 rounded-full border-2 flex items-center justify-center shadow"
                    style={{
                      backgroundColor: theme.bg, 
                      borderColor: theme.border 
                    }}
                  >
                    {isSelected && <Check className="w-4 h-4 text-white" />}
                  </div> 
                  <div>
                    <h4 className="text-sm font-bold text-[#E2E8F0]">
                      {theme.name} 
                    </h4> 
                    <p className="text-[11px] text-[#94A3B8]">
                      {theme.desc} 
                    </p> 
                  </div> 
                </div> 
              </div> 
            ) 
          })}
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
