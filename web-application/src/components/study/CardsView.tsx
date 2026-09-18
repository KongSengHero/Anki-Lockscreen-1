import React, { useState, useEffect } from 'react' 
import { Palette, FolderOpen, Layers, Bot } from 'lucide-react' 
import type { AnkiCard, AnkiDeck } from '../../types/anki' 
import { db } from '../../services/db' 
import { reviewCard } from '../../services/srsScheduler' 
import { ModernHeroCard } from './ModernHeroCard' 
import { DeckCarouselCard } from './DeckCarouselCard' 
import { QuickAccessHubTile } from './QuickAccessHubTile' 

interface CardsViewProps {
  onOpenBackgrounds: () => void 
  onOpenDecks: () => void 
  onOpenLockscreenConfig: () => void 
  onOpenAiConfig: () => void 
  onCardReviewed: () => void 
} 

export const CardsView: React.FC<CardsViewProps> = ({
  onOpenBackgrounds, 
  onOpenDecks, 
  onOpenLockscreenConfig, 
  onOpenAiConfig, 
  onCardReviewed 
}) => {
  const [isLockscreenEnabled, setIsLockscreenEnabled] = useState(true) 
  const [decks, setDecks] = useState<AnkiDeck[]>([]) 
  const [selectedDeck, setSelectedDeck] = useState<AnkiDeck | undefined>(undefined) 
  const [currentCard, setCurrentCard] = useState<AnkiCard | null>(null) 
  const [isRevealed, setIsRevealed] = useState(false) 

  const loadData = async () => {
    const allDecks = await db.decks.toArray() 
    setDecks(allDecks) 

    const activeDeck = selectedDeck || allDecks[0] 
    setSelectedDeck(activeDeck) 

    if (activeDeck) {
      const cards = await db.cards
        .where('deckId')
        .equals(activeDeck.id) 
        .toArray() 

      const due = cards.find(c => c.dueDate <= Date.now()) || cards[0] || null 
      setCurrentCard(due) 
    } else {
      setCurrentCard(null) 
    } 
  } 

  useEffect(() => {
    loadData() 
  }, [selectedDeck?.id]) 

  const handleAgain = async () => {
    if (!currentCard) return 
    await reviewCard(currentCard.id, 'again') 
    setIsRevealed(false) 
    await loadData() 
    onCardReviewed() 
  } 

  const handleGood = async () => {
    if (!currentCard) return 
    await reviewCard(currentCard.id, 'good') 
    setIsRevealed(false) 
    await loadData() 
    onCardReviewed() 
  } 

  const handlePlayWord = () => {
    if (!currentCard) return 
    if ('speechSynthesis' in window) {
      const u = new SpeechSynthesisUtterance(currentCard.kanji) 
      u.lang = 'ja-JP' 
      window.speechSynthesis.speak(u) 
    } 
  } 

  const handlePlaySentence = () => {
    if (!currentCard?.sentenceJp) return 
    if ('speechSynthesis' in window) {
      const u = new SpeechSynthesisUtterance(currentCard.sentenceJp) 
      u.lang = 'ja-JP' 
      window.speechSynthesis.speak(u) 
    } 
  } 

  const stats = {
    new: selectedDeck?.dueCount || 0, 
    learn: 0, 
    review: (selectedDeck?.cardCount || 0) - (selectedDeck?.dueCount || 0) 
  } 

  return (
    <div className="w-full max-w-md mx-auto px-4 pt-16 pb-28 flex flex-col gap-4">
      <ModernHeroCard
        isEnabled={isLockscreenEnabled}
        onToggle={setIsLockscreenEnabled}
      /> 

      <DeckCarouselCard
        deck={selectedDeck}
        card={currentCard}
        stats={stats}
        isRevealed={isRevealed}
        onToggleReveal={() => setIsRevealed(!isRevealed)}
        onRefresh={loadData}
        onAgain={handleAgain}
        onGood={handleGood}
        onOpenDecks={onOpenDecks}
        onPlayWord={handlePlayWord}
        onPlaySentence={handlePlaySentence}
      /> 

      <div className="grid grid-cols-2 gap-3">
        <QuickAccessHubTile
          icon={Palette}
          title="Backgrounds"
          subtitle="Card & App Themes"
          accentColor="text-[#7C8CF8]"
          containerColor="bg-[#1B1F38]"
          onClick={onOpenBackgrounds}
        /> 
        <QuickAccessHubTile
          icon={FolderOpen}
          title="Decks & Sync"
          subtitle={decks.length === 1 ? '1 Selected' : `${decks.length} Selected`}
          accentColor="text-[#CFA055]"
          containerColor="bg-[#332717]"
          onClick={onOpenDecks}
        /> 
      </div> 

      <div className="grid grid-cols-2 gap-3">
        <QuickAccessHubTile
          icon={Layers}
          title="Lockscreen Config"
          subtitle="Classic Card"
          accentColor="text-[#9678B6]"
          containerColor="bg-[#2E1E3B]"
          onClick={onOpenLockscreenConfig}
        /> 
        <QuickAccessHubTile
          icon={Bot}
          title="API Config"
          subtitle="API Keys & Models"
          accentColor="text-[#5FA77C]"
          containerColor="bg-[#1B3024]"
          onClick={onOpenAiConfig}
        /> 
      </div> 
    </div> 
  ) 
} 
