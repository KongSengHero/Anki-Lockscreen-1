import React from 'react' 
import { Layers, BookOpen, Search } from 'lucide-react' 

export type TabType = 'cards' | 'stories' | 'jisho' 

interface BottomNavProps {
  activeTab: TabType 
  onSelectTab: (tab: TabType) => void 
  dueCardsCount?: number 
  badgeCounts?: Partial<Record<TabType, number>> 
} 

const TABS = [
  {
    id: 'cards' as TabType, 
    title: 'Cards', 
    icon: Layers, 
    activeColor: '#7C8CF8', 
    containerBg: 'rgba(27, 31, 56, 0.92)', 
    borderColor: 'rgba(124, 140, 248, 0.45)', 
    glow: '0 0 20px -4px rgba(124, 140, 248, 0.35)' 
  }, 
  {
    id: 'stories' as TabType, 
    title: 'Stories', 
    icon: BookOpen, 
    activeColor: '#22D3EE', 
    containerBg: 'rgba(22, 43, 53, 0.92)', 
    borderColor: 'rgba(34, 211, 238, 0.45)', 
    glow: '0 0 20px -4px rgba(34, 211, 238, 0.35)' 
  }, 
  {
    id: 'jisho' as TabType, 
    title: 'Jisho', 
    icon: Search, 
    activeColor: '#5FA77C', 
    containerBg: 'rgba(27, 48, 36, 0.92)', 
    borderColor: 'rgba(95, 167, 124, 0.45)', 
    glow: '0 0 20px -4px rgba(95, 167, 124, 0.35)' 
  } 
] 

export const BottomNav: React.FC<BottomNavProps> = ({
  activeTab, 
  onSelectTab, 
  dueCardsCount = 0, 
  badgeCounts = {} 
}) => {
  const activeIndex = TABS.findIndex(t => t.id === activeTab) 
  const currentTab = TABS[activeIndex] || TABS[0] 

  return (
    <div className="fixed bottom-0 left-0 right-0 z-40 pointer-events-none pb-safe">
      <div className="w-full bg-gradient-to-t from-[#121418] via-[#121418]/90 to-transparent pt-6 pb-3">
        <div className="max-w-md mx-auto px-5 pointer-events-auto">
          <div
            className="relative w-full h-[60px] rounded-[18px] p-1 shadow-2xl flex items-center transition-all"
            style={{
              backgroundColor: 'rgba(22, 25, 34, 0.88)', 
              backdropFilter: 'blur(24px)', 
              WebkitBackdropFilter: 'blur(24px)', 
              border: '1px solid rgba(255, 255, 255, 0.10)', 
              boxShadow: 'inset 0 1px 1px 0 rgba(255, 255, 255, 0.22), 0 12px 36px 0 rgba(0, 0, 0, 0.5)' 
            }}
          >
            <div
              className="absolute top-1 bottom-1 rounded-[14px] transition-all duration-300 ease-[cubic-bezier(0.34,1.56,0.64,1)]"
              style={{
                width: 'calc((100% - 8px) / 3)', 
                left: `calc(4px + ${activeIndex} * ((100% - 8px) / 3))`, 
                backgroundColor: currentTab.containerBg, 
                border: `1px solid ${currentTab.borderColor}`, 
                boxShadow: `inset 0 1px 1px 0 rgba(255, 255, 255, 0.32), ${currentTab.glow}` 
              }}
            /> 

            <div className="relative z-10 w-full h-full grid grid-cols-3">
              {TABS.map((tab) => {
                const isSelected = activeTab === tab.id 
                const Icon = tab.icon 
                const badge = tab.id === 'cards' 
                  ? dueCardsCount 
                  : (badgeCounts[tab.id] || 0) 

                return (
                  <button
                    key={tab.id}
                    onClick={() => onSelectTab(tab.id)}
                    className="w-full h-full flex items-center justify-center rounded-[14px] transition-transform active:scale-95 select-none"
                  >
                    <div className="relative flex items-center justify-center">
                      <Icon
                        className="w-[20px] h-[20px] transition-colors duration-200"
                        style={{
                          color: isSelected ? tab.activeColor : '#94A3B8' 
                        }}
                      /> 

                      {badge > 0 && !isSelected && (
                        <div className="absolute -top-1.5 -right-2.5 min-w-[15px] h-[15px] px-1 rounded-full bg-[#E87A90] text-white text-[9px] font-extrabold flex items-center justify-center shadow-md">
                          {badge > 9 ? '9+' : badge} 
                        </div> 
                      )}

                      {isSelected && (
                        <span
                          className="ml-2 text-xs font-bold whitespace-nowrap tracking-wide"
                          style={{ color: tab.activeColor }}
                        >
                          {tab.title} 
                        </span> 
                      )}
                    </div> 
                  </button> 
                ) 
              })}
            </div> 
          </div> 
        </div> 
      </div> 
    </div> 
  ) 
} 
