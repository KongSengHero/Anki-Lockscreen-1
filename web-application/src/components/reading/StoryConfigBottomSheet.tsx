import React, { useState } from 'react' 
import { X, Check, Search, Sparkles } from 'lucide-react' 
import { STORY_CATEGORIES, formatThemeName } from '../../data/storyThemes' 
import { Squircle3DButton } from '../blossom/Squircle3DButton' 

interface StoryConfigBottomSheetProps {
  selectedJlpt: string 
  onSelectJlpt: (jlpt: string) => void 
  selectedLength: string 
  onSelectLength: (length: string) => void 
  selectedTheme: string 
  onSelectTheme: (theme: string) => void 
  selectedTopic: string 
  onSelectTopic: (topic: string) => void 
  connectingWordsCount: number 
  onSelectConnectingWordsCount: (count: number) => void 
  onDismiss: () => void 
} 

const JLPT_LEVELS = [
  { level: 'N5', desc: 'Beginner' }, 
  { level: 'N4', desc: 'Elementary' }, 
  { level: 'N3', desc: 'Intermediate' }, 
  { level: 'N2', desc: 'Pre-Adv' }, 
  { level: 'N1', desc: 'Advanced' } 
] 

const STORY_LENGTHS = [
  { length: 'Short', words: '~150w' }, 
  { length: 'Medium', words: '~300w' }, 
  { length: 'Long', words: '~500w' } 
] 

const CONNECTING_STEPS = [0, 5, 7, 9, 11, 22, -1] 

export const StoryConfigBottomSheet: React.FC<StoryConfigBottomSheetProps> = ({
  selectedJlpt, 
  onSelectJlpt, 
  selectedLength, 
  onSelectLength, 
  selectedTheme, 
  onSelectTheme, 
  selectedTopic, 
  onSelectTopic, 
  connectingWordsCount, 
  onSelectConnectingWordsCount, 
  onDismiss 
}) => {
  const [activeTab, setActiveTab] = useState<'general' | 'themes'>('general') 
  const [searchQuery, setSearchQuery] = useState('') 

  const categories = Object.keys(STORY_CATEGORIES) 
  const filteredCategories = categories.filter(cat => {
    if (!searchQuery.trim()) return true 
    const q = searchQuery.toLowerCase() 
    return cat.toLowerCase().includes(q) || 
      formatThemeName(cat).toLowerCase().includes(q) || 
      STORY_CATEGORIES[cat].some(t => t.toLowerCase().includes(q)) 
  }) 

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-md animate-fadeIn">
      <div className="w-full max-w-md max-h-[88vh] h-[88vh] overflow-hidden rounded-t-[26px] sm:rounded-[26px] bg-[#1E222B] border border-[#2C3240] shadow-2xl flex flex-col">
        <div className="px-5 py-3.5 border-b border-[#20242E] flex items-center justify-between">
          <Squircle3DButton
            onClick={onDismiss}
            containerColor="#252A35"
            bevelColor="#2C3240"
            contentColor="#E8EAF0"
            height={42}
            shapeRadius={12}
            className="w-[46px]"
          >
            <X className="w-4 h-4" /> 
          </Squircle3DButton> 

          <h2 className="text-[17px] font-bold text-[#E8EAF0]">
            Story Configuration
          </h2> 

          <button
            onClick={onDismiss}
            className="px-3.5 py-2 rounded-[12px] bg-[#E87A90] text-white font-bold text-xs active:scale-95 transition-all shadow"
          >
            Save
          </button> 
        </div> 

        <div className="px-5 pt-3 pb-2">
          <div className="w-full p-1 rounded-[14px] bg-[#161920] border border-[#20242E] grid grid-cols-2 gap-1">
            <button
              onClick={() => setActiveTab('general')}
              className={`py-2 rounded-[10px] text-xs font-bold transition-all ${
                activeTab === 'general' 
                  ? 'bg-[#252A35] text-[#E8EAF0] shadow-sm border border-[#2C3240]' 
                  : 'text-[#9AA1AD] hover:text-[#E8EAF0]' 
              }`}
            >
              General
            </button> 
            <button
              onClick={() => setActiveTab('themes')}
              className={`py-2 rounded-[10px] text-xs font-bold transition-all ${
                activeTab === 'themes' 
                  ? 'bg-[#252A35] text-[#E8EAF0] shadow-sm border border-[#2C3240]' 
                  : 'text-[#9AA1AD] hover:text-[#E8EAF0]' 
              }`}
            >
              Themes & Topics
            </button> 
          </div> 
        </div> 

        <div className="flex-1 overflow-y-auto px-5 py-3 flex flex-col gap-5">
          {activeTab === 'general' ? (
            <>
              <div className="flex flex-col gap-2">
                <span className="text-[11px] font-bold uppercase tracking-wider text-[#9AA1AD]">
                  JLPT TARGET LEVEL
                </span> 
                <div className="grid grid-cols-5 gap-1.5">
                  {JLPT_LEVELS.map(({ level, desc }) => {
                    const isSelected = selectedJlpt === level 
                    return (
                      <button
                        key={level}
                        onClick={() => onSelectJlpt(level)}
                        className={`py-2.5 px-1 rounded-[12px] flex flex-col items-center justify-center transition-all border ${
                          isSelected 
                            ? 'bg-[#2A1B20] border-[#E87A90] text-[#E87A90] shadow' 
                            : 'bg-[#252A35] border-[#2C3240] text-[#9AA1AD] hover:border-[#E87A90]/40' 
                        }`}
                      >
                        <span className="text-xs font-extrabold">{level}</span> 
                        <span className="text-[9px] font-medium opacity-80">{desc}</span> 
                      </button> 
                    ) 
                  })}
                </div> 
              </div> 

              <div className="flex flex-col gap-2">
                <span className="text-[11px] font-bold uppercase tracking-wider text-[#9AA1AD]">
                  STORY LENGTH
                </span> 
                <div className="grid grid-cols-3 gap-2">
                  {STORY_LENGTHS.map(({ length, words }) => {
                    const isSelected = selectedLength === length 
                    return (
                      <button
                        key={length}
                        onClick={() => onSelectLength(length)}
                        className={`py-2.5 rounded-[12px] flex flex-col items-center justify-center transition-all border ${
                          isSelected 
                            ? 'bg-[#162B35] border-[#22D3EE] text-[#22D3EE] shadow' 
                            : 'bg-[#252A35] border-[#2C3240] text-[#9AA1AD] hover:border-[#22D3EE]/40' 
                        }`}
                      >
                        <span className="text-xs font-bold">{length}</span> 
                        <span className="text-[10px] font-medium opacity-80">{words}</span> 
                      </button> 
                    ) 
                  })}
                </div> 
              </div> 

              <div className="flex flex-col gap-2">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-bold uppercase tracking-wider text-[#9AA1AD]">
                    CONNECTING FLASHCARDS
                  </span> 
                  <span className="text-xs font-bold text-[#E87A90]">
                    {connectingWordsCount === -1 
                      ? 'ALL cards' 
                      : connectingWordsCount === 0 
                      ? 'Free story' 
                      : `${connectingWordsCount} cards`} 
                  </span> 
                </div> 

                <div className="grid grid-cols-7 gap-1">
                  {CONNECTING_STEPS.map((step) => {
                    const isSelected = connectingWordsCount === step 
                    const label = step === -1 ? 'ALL' : `${step}` 
                    return (
                      <button
                        key={step}
                        onClick={() => onSelectConnectingWordsCount(step)}
                        className={`py-2 rounded-[10px] text-xs font-bold transition-all border ${
                          isSelected 
                            ? 'bg-[#2A1B20] border-[#E87A90] text-[#E87A90] shadow' 
                            : 'bg-[#252A35] border-[#2C3240] text-[#9AA1AD] hover:border-[#E87A90]/40' 
                        }`}
                      >
                        {label} 
                      </button> 
                    ) 
                  })}
                </div> 
              </div> 
            </> 
          ) : (
            <>
              <div className="relative">
                <Search className="w-4 h-4 text-[#9AA1AD] absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" /> 
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search story themes or topics..."
                  className="w-full pl-9 pr-3.5 py-2.5 rounded-[12px] bg-[#252A35] border border-[#2C3240] text-sm text-[#E8EAF0] placeholder-[#6E7482] outline-none focus:border-[#E87A90] transition-colors"
                /> 
              </div> 

              <div
                onClick={() => {
                  onSelectTheme('Random Theme') 
                  onSelectTopic('') 
                }}
                className={`p-3.5 rounded-[14px] cursor-pointer border transition-all flex items-center justify-between ${
                  selectedTheme === 'Random Theme' 
                    ? 'bg-[#2A1B20] border-[#E87A90]' 
                    : 'bg-[#252A35] border-[#2C3240] hover:border-[#E87A90]/40' 
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <Sparkles className="w-4 h-4 text-[#E87A90]" /> 
                  <span className="text-sm font-bold text-[#E8EAF0]">
                    Random Theme & Topic
                  </span> 
                </div> 
                {selectedTheme === 'Random Theme' && (
                  <Check className="w-4 h-4 text-[#E87A90]" /> 
                )}
              </div> 

              <div className="flex flex-col gap-3">
                {filteredCategories.map((cat) => {
                  const isCatSelected = selectedTheme === cat 
                  const topics = STORY_CATEGORIES[cat] 

                  return (
                    <div
                      key={cat}
                      className="rounded-[16px] bg-[#252A35] border border-[#2C3240] p-3.5 flex flex-col gap-2.5"
                    >
                      <div
                        onClick={() => {
                          onSelectTheme(cat) 
                          onSelectTopic('') 
                        }}
                        className="flex items-center justify-between cursor-pointer"
                      >
                        <span className={`text-sm font-bold ${isCatSelected ? 'text-[#E87A90]' : 'text-[#E8EAF0]'}`}>
                          {formatThemeName(cat)} 
                        </span> 
                        {isCatSelected && !selectedTopic && (
                          <Check className="w-4 h-4 text-[#E87A90]" /> 
                        )}
                      </div> 

                      <div className="flex flex-wrap gap-1.5 pt-1 border-t border-[#20242E]">
                        {topics.slice(0, 8).map((top) => {
                          const isTopicSelected = selectedTheme === cat && selectedTopic === top 
                          return (
                            <button
                              key={top}
                              onClick={() => {
                                onSelectTheme(cat) 
                                onSelectTopic(top) 
                              }}
                              className={`px-2.5 py-1 rounded-[8px] text-[11px] font-medium border transition-all ${
                                isTopicSelected 
                                  ? 'bg-[#2A1B20] border-[#E87A90] text-[#E87A90]' 
                                  : 'bg-[#1E222B] border-[#20242E] text-[#9AA1AD] hover:text-[#E8EAF0]' 
                              }`}
                            >
                              {top} 
                            </button> 
                          ) 
                        })}
                      </div> 
                    </div> 
                  ) 
                })}
              </div> 
            </> 
          )}
        </div> 
      </div> 
    </div> 
  ) 
} 
