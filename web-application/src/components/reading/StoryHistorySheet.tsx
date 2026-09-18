import React, { useState } from 'react' 
import { Pin, Trash2, X, Check, BookOpen, AlertTriangle } from 'lucide-react' 
import type { GeneratedStory } from '../../types/story' 
import { db } from '../../services/db' 
import { getThemeBadgeColors, formatThemeName } from '../../data/storyThemes' 

interface StoryHistorySheetProps {
  stories: GeneratedStory[] 
  onSelectStory: (index: number) => void 
  onStoriesChanged: () => void 
  onDismiss: () => void 
} 

export const StoryHistorySheet: React.FC<StoryHistorySheetProps> = ({
  stories, 
  onSelectStory, 
  onStoriesChanged, 
  onDismiss 
}) => {
  const [showClearConfirm, setShowClearConfirm] = useState(false) 
  const [storyToDelete, setStoryToDelete] = useState<GeneratedStory | null>(null) 

  const handleTogglePin = async (e: React.MouseEvent, story: GeneratedStory) => {
    e.stopPropagation() 
    await db.stories.update(story.id, { isPinned: !story.isPinned }) 
    onStoriesChanged() 
  } 

  const handleDelete = async (e: React.MouseEvent, story: GeneratedStory) => {
    e.stopPropagation() 
    setStoryToDelete(story) 
  } 

  const confirmDeleteStory = async () => {
    if (storyToDelete) {
      await db.stories.delete(storyToDelete.id) 
      setStoryToDelete(null) 
      onStoriesChanged() 
    } 
  } 

  const handleClearAll = async () => {
    await db.stories.clear() 
    setShowClearConfirm(false) 
    onStoriesChanged() 
  } 

  const totalBytes = stories.reduce((acc, s) => acc + (s.title.length + s.content.length) * 2, 0) 
  const formatBytes = (bytes: number) => {
    if (bytes < 1024) return `${bytes} B` 
    return `${(bytes / 1024).toFixed(1)} KB` 
  } 

  const sortedStories = [...stories].sort((a, b) => {
    if (a.isPinned && !b.isPinned) return -1 
    if (!a.isPinned && b.isPinned) return 1 
    return b.createdAt - a.createdAt 
  }) 

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-md animate-fadeIn">
      <div className="w-full max-w-md max-h-[88vh] h-[88vh] overflow-hidden rounded-t-[26px] sm:rounded-[26px] bg-[#1E222B] border border-[#2C3240] shadow-2xl flex flex-col">
        <div className="px-5 py-3.5 border-b border-[#20242E] flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold text-[#E8EAF0]">
              Stories History ({stories.length})
            </h2> 
            <p className="text-xs text-[#9AA1AD]">
              Storage: {formatBytes(totalBytes)} 
            </p> 
          </div> 

          <div className="flex items-center gap-2">
            {stories.length > 0 && (
              <button
                onClick={() => setShowClearConfirm(true)}
                className="text-xs font-semibold text-[#E87A90] hover:text-[#E87A90]/80 px-2 py-1 transition-colors"
              >
                Clear All
              </button> 
            )}
            <button
              onClick={onDismiss}
              className="w-8 h-8 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] active:scale-95"
            >
              <X className="w-5 h-5" /> 
            </button> 
          </div> 
        </div> 

        <div className="flex-1 overflow-y-auto px-5 py-4 flex flex-col gap-3">
          {sortedStories.length === 0 ? (
            <div className="flex-1 flex flex-col items-center justify-center text-center py-16 gap-3">
              <BookOpen className="w-12 h-12 text-[#E87A90]/50" /> 
              <p className="text-sm text-[#6E7482]">
                No saved stories yet.
              </p> 
            </div> 
          ) : (
            sortedStories.map((item) => {
              const themeStyle = getThemeBadgeColors(item.theme || 'Daily Life') 
              const originalIndex = stories.findIndex(s => s.id === item.id) 

              return (
                <div
                  key={item.id}
                  onClick={() => {
                    onSelectStory(originalIndex >= 0 ? originalIndex : 0) 
                    onDismiss() 
                  }}
                  className="relative rounded-[16px] h-[135px] p-4 flex flex-col justify-between cursor-pointer border transition-all active:scale-[0.99] overflow-hidden group shadow-md"
                  style={{
                    backgroundColor: '#252A35', 
                    borderColor: item.isPassed ? '#5FA77C' : '#2C3240' 
                  }}
                >
                  <div
                    className="absolute inset-0 pointer-events-none opacity-40 group-hover:opacity-60 transition-opacity"
                    style={{
                      backgroundImage: `url(${item.coverUrl || 'https://images.unsplash.com/photo-1528164344705-475426879c0d?w=800&auto=format&fit=crop&q=80'})`, 
                      backgroundSize: 'cover', 
                      backgroundPosition: 'center' 
                    }}
                  /> 
                  <div
                    className="absolute inset-0 pointer-events-none"
                    style={{
                      background: 'linear-gradient(to bottom, rgba(18, 20, 24, 0.65) 0%, rgba(18, 20, 24, 0.92) 100%)' 
                    }}
                  /> 

                  <div className="relative z-10 flex items-center justify-between">
                    <div className="flex items-center gap-1.5 flex-wrap">
                      <span className="px-2 py-0.5 rounded-[6px] bg-[#2A1B20] text-[#E87A90] border border-[#E87A90]/50 text-[10px] font-bold">
                        JLPT {item.jlptLevel} 
                      </span> 

                      {item.isPassed && (
                        <span className="px-2 py-0.5 rounded-[6px] bg-[#1B3024] text-[#5FA77C] border border-[#5FA77C]/50 text-[10px] font-bold flex items-center gap-1">
                          <Check className="w-3 h-3" /> 
                          Passed {item.quizScore ? `(${item.quizScore}%)` : ''} 
                        </span> 
                      )}

                      {item.theme && (
                        <span
                          className="px-2 py-0.5 rounded-[6px] text-[10px] font-bold border"
                          style={{
                            backgroundColor: themeStyle.backgroundColor, 
                            color: themeStyle.contentColor, 
                            borderColor: themeStyle.borderColor 
                          }}
                        >
                          {formatThemeName(item.theme)} 
                        </span> 
                      )}
                    </div> 

                    <div className="flex items-center gap-1">
                      <button
                        onClick={(e) => handleTogglePin(e, item)}
                        className="w-8 h-8 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-white active:scale-90 transition-all"
                        title={item.isPinned ? 'Unpin story' : 'Pin story'}
                      >
                        <Pin
                          className={`w-4 h-4 ${
                            item.isPinned ? 'text-[#E87A90] fill-[#E87A90]' : '' 
                          }`}
                        /> 
                      </button> 

                      <button
                        onClick={(e) => handleDelete(e, item)}
                        className="w-8 h-8 rounded-lg flex items-center justify-center text-[#E87A90] hover:text-[#E87A90]/80 active:scale-90 transition-all"
                        title="Delete story"
                      >
                        <Trash2 className="w-4 h-4" /> 
                      </button> 
                    </div> 
                  </div> 

                  <div className="relative z-10 flex flex-col gap-1">
                    <h3 className="text-[15px] font-semibold text-[#E8EAF0] line-clamp-2 leading-snug">
                      {item.title} 
                    </h3> 
                    <span className="text-[11px] text-[#9AA1AD]">
                      {new Date(item.createdAt).toLocaleDateString()} • {formatBytes((item.title.length + item.content.length) * 2)} 
                    </span> 
                  </div> 
                </div> 
              ) 
            }) 
          )}
        </div> 
      </div> 

      {storyToDelete && (
        <div className="fixed inset-0 z-60 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fadeIn">
          <div className="w-full max-w-sm rounded-[20px] bg-[#1E222B] border border-[#2C3240] p-6 shadow-2xl flex flex-col gap-4">
            <div className="flex items-center gap-3 text-[#E87A90]">
              <Trash2 className="w-6 h-6" /> 
              <h3 className="text-lg font-bold text-[#E8EAF0]">
                Delete Story?
              </h3> 
            </div> 
            <p className="text-sm text-[#9AA1AD] leading-relaxed">
              Are you sure you want to delete <span className="text-white font-semibold">"{storyToDelete.title}"</span>? This cannot be undone.
            </p> 
            <div className="flex items-center gap-3 pt-2">
              <button
                onClick={() => setStoryToDelete(null)}
                className="flex-1 py-2.5 rounded-[12px] border border-[#2C3240] text-[#9AA1AD] font-semibold text-xs active:scale-95"
              >
                Cancel
              </button> 
              <button
                onClick={confirmDeleteStory}
                className="flex-1 py-2.5 rounded-[12px] bg-[#E87A90] text-white font-bold text-xs active:scale-95 shadow"
              >
                Delete
              </button> 
            </div> 
          </div> 
        </div> 
      )}

      {showClearConfirm && (
        <div className="fixed inset-0 z-60 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fadeIn">
          <div className="w-full max-w-sm rounded-[20px] bg-[#1E222B] border border-[#2C3240] p-6 shadow-2xl flex flex-col gap-4">
            <div className="flex items-center gap-3 text-[#E87A90]">
              <AlertTriangle className="w-6 h-6" /> 
              <h3 className="text-lg font-bold text-[#E8EAF0]">
                Clear Stories History?
              </h3> 
            </div> 
            <p className="text-sm text-[#9AA1AD] leading-relaxed">
              Are you sure you want to clear all stories history? This will permanently delete all saved stories.
            </p> 
            <div className="flex items-center gap-3 pt-2">
              <button
                onClick={() => setShowClearConfirm(false)}
                className="flex-1 py-2.5 rounded-[12px] border border-[#2C3240] text-[#9AA1AD] font-semibold text-xs active:scale-95"
              >
                Cancel
              </button> 
              <button
                onClick={handleClearAll}
                className="flex-1 py-2.5 rounded-[12px] bg-[#E87A90] text-white font-bold text-xs active:scale-95 shadow"
              >
                Clear All
              </button> 
            </div> 
          </div> 
        </div> 
      )}
    </div> 
  ) 
} 
