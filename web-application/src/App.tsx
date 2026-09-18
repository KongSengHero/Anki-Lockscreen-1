import { useState, useEffect } from 'react' 
import { seedInitialData, getDueCards, db } from './services/db' 
import type { AnkiDeck } from './types/anki' 
import { usePreferences } from './hooks/usePreferences' 
import { MinimalTopBar } from './components/layout/MinimalTopBar' 
import { BottomNav, type TabType } from './components/layout/BottomNav' 
import { CardsView } from './components/study/CardsView' 
import { ReadingView } from './components/reading/ReadingView' 
import { JishoView } from './components/jisho/JishoView' 
import { StreakCalendarDialog } from './components/layout/Modals/StreakCalendarDialog' 
import { MasteredStoriesInfoDialog } from './components/layout/Modals/MasteredStoriesInfoDialog' 
import { DailyEnergyInfoDialog } from './components/layout/Modals/DailyEnergyInfoDialog' 
import { AiKeyConfigDialog } from './components/layout/Modals/AiKeyConfigDialog' 
import { DecksSheet } from './components/layout/Modals/DecksSheet' 
import { BackgroundsSheet } from './components/layout/Modals/BackgroundsSheet' 
import { LockscreenConfigSheet } from './components/layout/Modals/LockscreenConfigSheet' 
import { ApplicationSceneryBackground } from './components/blossom/ApplicationSceneryBackground' 

export function App() {
  const { preferences, updatePreference } = usePreferences() 
  const [activeTab, setActiveTab] = useState<TabType>('cards') 
  const [dueCount, setDueCount] = useState(0) 
  const [decks, setDecks] = useState<AnkiDeck[]>([]) 
  const [selectedDeckId, setSelectedDeckId] = useState<number | undefined>(undefined) 
  const [jishoQuery, setJishoQuery] = useState('') 

  const [isStreakOpen, setIsStreakOpen] = useState(false) 
  const [isStoriesInfoOpen, setIsStoriesInfoOpen] = useState(false) 
  const [isEnergyInfoOpen, setIsEnergyInfoOpen] = useState(false) 
  const [isAiConfigOpen, setIsAiConfigOpen] = useState(false) 
  const [isDecksOpen, setIsDecksOpen] = useState(false) 
  const [isBackgroundsOpen, setIsBackgroundsOpen] = useState(false) 
  const [isLockscreenConfigOpen, setIsLockscreenConfigOpen] = useState(false) 

  const [currentTheme, setCurrentTheme] = useState('midnight') 
  const [isMusicStyle, setIsMusicStyle] = useState(false) 
  const [storyEnergy] = useState(7) 

  const refreshDeckData = async () => {
    const allDecks = await db.decks.toArray() 
    setDecks(allDecks) 
    if (!selectedDeckId && allDecks.length > 0) {
      setSelectedDeckId(allDecks[0].id) 
    } 
    const due = await getDueCards() 
    setDueCount(due.length) 
  } 

  const initApp = async () => {
    await seedInitialData() 
    await refreshDeckData() 
  } 

  useEffect(() => {
    initApp() 
  }, []) 

  const handleSearchJisho = (query: string) => {
    setJishoQuery(query) 
    setActiveTab('jisho') 
  } 

  const handleCardReviewed = async () => {
    updatePreference('streakCount', preferences.streakCount + 1) 
    await refreshDeckData() 
  } 

  const activeDates = new Set<string>() 
  const now = new Date() 
  activeDates.add(`${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`) 

  return (
    <div
      className="min-h-screen text-[#E2E8F0] flex flex-col font-sans transition-colors duration-500 relative overflow-x-hidden"
      style={{
        backgroundColor: currentTheme === 'matcha' 
          ? '#101C16' 
          : currentTheme === 'sakura' 
          ? '#1C1217' 
          : currentTheme === 'slate' 
          ? '#141724' 
          : '#121418' 
      }}
    >
      <ApplicationSceneryBackground theme={currentTheme} /> 

      <MinimalTopBar
        streakCount={preferences.streakCount}
        isStreakActive={true}
        completedStoriesCount={preferences.storiesReadCount}
        storyEnergy={storyEnergy}
        onStreakClick={() => setIsStreakOpen(true)}
        onBookClick={() => setIsStoriesInfoOpen(true)}
        onEnergyClick={() => setIsEnergyInfoOpen(true)}
      /> 

      <main className="relative z-10 flex-1 w-full">
        {activeTab === 'cards' && (
          <CardsView
            onOpenBackgrounds={() => setIsBackgroundsOpen(true)}
            onOpenDecks={() => setIsDecksOpen(true)}
            onOpenLockscreenConfig={() => setIsLockscreenConfigOpen(true)}
            onOpenAiConfig={() => setIsAiConfigOpen(true)}
            onCardReviewed={handleCardReviewed}
          /> 
        )}

        {activeTab === 'stories' && (
          <ReadingView
            preferences={preferences}
            onSearchJisho={handleSearchJisho}
            onUpdateStreak={() => {
              updatePreference('storiesReadCount', preferences.storiesReadCount + 1) 
              handleCardReviewed() 
            }}
            onOpenAiConfig={() => setIsAiConfigOpen(true)}
          /> 
        )}

        {activeTab === 'jisho' && (
          <JishoView initialQuery={jishoQuery} /> 
        )}
      </main> 

      <BottomNav
        activeTab={activeTab}
        dueCardsCount={dueCount}
        onSelectTab={(tab) => setActiveTab(tab)}
      /> 

      {isStreakOpen && (
        <StreakCalendarDialog
          streakCount={preferences.streakCount}
          isStreakActive={true}
          activeDates={activeDates}
          onDismiss={() => setIsStreakOpen(false)}
        /> 
      )}

      {isStoriesInfoOpen && (
        <MasteredStoriesInfoDialog
          completedStoriesCount={preferences.storiesReadCount}
          onDismiss={() => setIsStoriesInfoOpen(false)}
        /> 
      )}

      {isEnergyInfoOpen && (
        <DailyEnergyInfoDialog
          storyEnergy={storyEnergy}
          onDismiss={() => setIsEnergyInfoOpen(false)}
        /> 
      )}

      {isAiConfigOpen && (
        <AiKeyConfigDialog
          geminiApiKey={preferences.geminiApiKey}
          fishAudioApiKey={preferences.fishAudioApiKey}
          fishAudioVoiceId={preferences.fishAudioVoiceId}
          onSave={({ geminiApiKey, fishAudioApiKey, fishAudioVoiceId }) => {
            updatePreference('geminiApiKey', geminiApiKey) 
            updatePreference('fishAudioApiKey', fishAudioApiKey) 
            updatePreference('fishAudioVoiceId', fishAudioVoiceId) 
          }}
          onDismiss={() => setIsAiConfigOpen(false)}
        /> 
      )}

      {isDecksOpen && (
        <DecksSheet
          decks={decks}
          selectedDeckId={selectedDeckId}
          onSelectDeck={(id) => setSelectedDeckId(id)}
          onDecksUpdated={refreshDeckData}
          onDismiss={() => setIsDecksOpen(false)}
        /> 
      )}

      {isBackgroundsOpen && (
        <BackgroundsSheet
          currentTheme={currentTheme}
          onSelectTheme={(theme) => setCurrentTheme(theme)}
          onDismiss={() => setIsBackgroundsOpen(false)}
        /> 
      )}

      {isLockscreenConfigOpen && (
        <LockscreenConfigSheet
          isMusicStyle={isMusicStyle}
          onToggleStyle={(music) => setIsMusicStyle(music)}
          onDismiss={() => setIsLockscreenConfigOpen(false)}
        /> 
      )}
    </div> 
  ) 
} 

export default App 
