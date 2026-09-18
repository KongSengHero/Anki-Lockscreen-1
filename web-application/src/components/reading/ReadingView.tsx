import React, { useState, useEffect, useRef, useMemo } from 'react' 
import {
  History, 
  Volume2, 
  Square, 
  Languages, 
  Sparkles, 
  Copy, 
  ChevronDown, 
  ExternalLink, 
  Lock, 
  GraduationCap, 
  Check, 
  Image as ImageIcon, 
  Loader2, 
  AlertCircle 
} from 'lucide-react' 
import type { GeneratedStory, StoryToken } from '../../types/story' 
import type { UserPreferences } from '../../types/preferences' 
import { db, getStories, saveStory } from '../../services/db' 
import { generateJapaneseStory } from '../../services/geminiService' 
import { AudioNarrationService, extractSentences } from '../../services/audioService' 
import { Squircle3DButton } from '../blossom/Squircle3DButton' 
import { RotatingStatusText } from './RotatingStatusText' 
import { RubyText } from './RubyText' 
import { QuizOverlay } from './QuizOverlay' 
import { FloatingNarration } from './FloatingNarration' 
import { FloatingActions } from './FloatingActions' 
import { StoryConfigBottomSheet } from './StoryConfigBottomSheet' 
import { StoryHistorySheet } from './StoryHistorySheet' 
import { TranslationBottomSheet } from './TranslationBottomSheet' 
import { WordDetailBottomSheet } from './WordDetailBottomSheet' 

interface ReadingViewProps {
  preferences: UserPreferences 
  onSearchJisho: (query: string) => void 
  onUpdateStreak: () => void 
  onOpenAiConfig: () => void 
} 

const SPEED_OPTIONS = [0.75, 1.0, 1.25, 1.5] 

const ROTATING_PHRASES = [
  'Weaving the plot...', 
  'Polishing details...', 
  'Adding some flair...', 
  'Fine-tuning emotions...', 
  'Almost there...' 
] 

export const ReadingView: React.FC<ReadingViewProps> = ({
  preferences, 
  onSearchJisho, 
  onUpdateStreak, 
  onOpenAiConfig 
}) => {
  const [stories, setStories] = useState<GeneratedStory[]>([]) 
  const [currentStoryIndex, setCurrentStoryIndex] = useState(0) 
  const [selectedJlpt, setSelectedJlpt] = useState('N4') 
  const [selectedLength, setSelectedLength] = useState('Medium') 
  const [selectedTheme, setSelectedTheme] = useState('Random Theme') 
  const [selectedTopic, setSelectedTopic] = useState('') 
  const [connectingWordsCount, setConnectingWordsCount] = useState(5) 
  const [isGenerating, setIsGenerating] = useState(false) 
  const [generationError, setGenerationError] = useState<string | null>(null) 

  const [showFurigana, setShowFurigana] = useState(preferences.showFurigana ?? true) 
  const [highlightVocab, setHighlightVocab] = useState(true) 

  const [showConfigSheet, setShowConfigSheet] = useState(false) 
  const [showHistorySheet, setShowHistorySheet] = useState(false) 
  const [showTranslationSheet, setShowTranslationSheet] = useState(false) 
  const [selectedToken, setSelectedToken] = useState<StoryToken | null>(null) 
  const [isQuizOpen, setIsQuizOpen] = useState(false) 

  const [isPlaying, setIsPlaying] = useState(false) 
  const [isAudioPaused, setIsAudioPaused] = useState(false) 
  const [currentSentenceIdx, setCurrentSentenceIdx] = useState(-1) 
  const [audioSpeed, setAudioSpeed] = useState(preferences.audioSpeed || 1.0) 

  const [scrolledPastHeader, setScrolledPastHeader] = useState(false) 
  const storyActionsRef = useRef<HTMLDivElement>(null) 

  const narrationRef = useRef<AudioNarrationService | null>(null) 

  const loadStories = async () => {
    const list = await getStories() 
    setStories(list) 
  } 

  useEffect(() => {
    loadStories() 
  }, []) 

  const currentStory: GeneratedStory | undefined = stories[currentStoryIndex] 

  const sentences = useMemo(() => {
    if (!currentStory) return [] 
    return extractSentences(currentStory.content) 
  }, [currentStory]) 

  useEffect(() => {
    if (!currentStory) return 

    narrationRef.current = new AudioNarrationService(
      currentStory.content, 
      preferences.fishAudioApiKey, 
      preferences.fishAudioVoiceId, 
      audioSpeed, 
      (index) => setCurrentSentenceIdx(index), 
      (playing) => {
        setIsPlaying(playing) 
        if (!playing) {
          setIsAudioPaused(true) 
        } else {
          setIsAudioPaused(false) 
        } 
      } 
    ) 

    return () => {
      if (narrationRef.current) {
        narrationRef.current.stop() 
      } 
    } 
  }, [currentStory, preferences.fishAudioApiKey, preferences.fishAudioVoiceId, audioSpeed]) 

  useEffect(() => {
    const handleScroll = () => {
      if (!storyActionsRef.current) {
        setScrolledPastHeader(false) 
        return 
      } 
      const rect = storyActionsRef.current.getBoundingClientRect() 
      setScrolledPastHeader(rect.bottom <= 80) 
    } 

    window.addEventListener('scroll', handleScroll, { passive: true }) 
    return () => window.removeEventListener('scroll', handleScroll) 
  }, [currentStory]) 

  const handlePlayPause = () => {
    if (!narrationRef.current) return 
    if (isPlaying) {
      narrationRef.current.pause() 
      setIsPlaying(false) 
      setIsAudioPaused(true) 
    } else {
      narrationRef.current.play() 
      setIsPlaying(true) 
      setIsAudioPaused(false) 
    } 
  } 

  const handleStopNarration = () => {
    if (narrationRef.current) {
      narrationRef.current.stop() 
    } 
    setIsPlaying(false) 
    setIsAudioPaused(false) 
    setCurrentSentenceIdx(-1) 
  } 

  const handleSkipNext = () => {
    if (narrationRef.current) {
      narrationRef.current.skipNext() 
    } 
  } 

  const handleSkipPrev = () => {
    if (narrationRef.current) {
      narrationRef.current.skipPrevious() 
    } 
  } 

  const handleSpeedCycle = () => {
    const currentIdx = SPEED_OPTIONS.indexOf(audioSpeed) 
    const nextIdx = (currentIdx + 1) % SPEED_OPTIONS.length 
    const newSpeed = SPEED_OPTIONS[nextIdx] 
    setAudioSpeed(newSpeed) 
    if (narrationRef.current) {
      narrationRef.current.setSpeed(newSpeed) 
    } 
  } 

  const handleGenerateStory = async () => {
    if (!preferences.geminiApiKey) {
      onOpenAiConfig() 
      return 
    } 

    setIsGenerating(true) 
    setGenerationError(null) 

    try {
      const dueCards = await db.cards.limit(connectingWordsCount > 0 ? connectingWordsCount : 5).toArray() 
      const targetVocab = dueCards.map(c => c.kanji) 

      const newStory = await generateJapaneseStory(
        preferences.geminiApiKey, 
        selectedJlpt, 
        targetVocab, 
        preferences.geminiModel, 
        selectedTheme === 'Random Theme' ? undefined : selectedTheme, 
        selectedTopic || undefined, 
        selectedLength, 
        3 
      ) 

      await saveStory(newStory) 
      await loadStories() 
      setCurrentStoryIndex(0) 
      onUpdateStreak() 
    } catch (err: any) {
      setGenerationError(err?.message || 'Failed to generate story') 
    } finally {
      setIsGenerating(false) 
    } 
  } 

  const copyStory = () => {
    if (!currentStory) return 
    navigator.clipboard.writeText(`${currentStory.title}\n\n${currentStory.content}`) 
  } 

  const handleQuizSubmitted = async (score: number, isPassed: boolean) => {
    if (!currentStory) return 
    await db.stories.update(currentStory.id, {
      quizScore: score, 
      isPassed: currentStory.isPassed || isPassed 
    }) 
    await loadStories() 
  } 

  const phrases = [`Crafting ${selectedJlpt} story...`, ...ROTATING_PHRASES] 

  const isLockedWithoutKey = !preferences.geminiApiKey 
  const buttonBg = isLockedWithoutKey 
    ? '#252A35' 
    : isGenerating 
    ? 'rgba(232, 122, 144, 0.40)' 
    : '#E87A90' 
  const buttonBevel = isLockedWithoutKey 
    ? '#2C3240' 
    : isGenerating 
    ? 'rgba(168, 71, 91, 0.40)' 
    : '#A8475B' 

  return (
    <div className="w-full max-w-md mx-auto px-4 pt-16 pb-32 flex flex-col gap-4">
      <div className="flex flex-col gap-3">
        <div className="rounded-[20px] bg-[#1E222B] border border-[#2C3240] p-4 flex flex-col gap-2.5 shadow-md">
          <div className="flex items-center justify-between">
            <span className="text-[15px] font-bold text-[#E8EAF0]">
              Story Immersion
            </span> 
            <button
              onClick={() => setShowHistorySheet(true)}
              className={`w-9 h-9 rounded-lg flex items-center justify-center hover:bg-white/5 active:scale-95 transition-all ${
                stories.length > 0 ? 'text-[#E87A90]' : 'text-[#9AA1AD]' 
              }`}
              title="Stories History"
            >
              <History className="w-5 h-5" /> 
            </button> 
          </div> 

          <div
            onClick={() => setShowConfigSheet(true)}
            className="rounded-[14px] bg-[#252A35] border border-[#2C3240] px-3.5 py-2.5 flex items-center justify-between cursor-pointer hover:border-[#E87A90]/40 transition-all shadow-sm"
          >
            <div className="flex flex-col gap-1 flex-1">
              <div className="flex items-center gap-1.5 text-xs">
                <span className="px-1.5 py-0.5 rounded-[6px] bg-[#2A1B20] text-[#E87A90] border border-[#E87A90]/50 font-bold text-[11px]">
                  {selectedJlpt} 
                </span> 
                <span className="text-[#9AA1AD]">•</span> 
                <span className="font-medium text-[#E8EAF0]">{selectedLength}</span> 
                <span className="text-[#9AA1AD]">•</span> 
                <span className="font-medium text-[#E8EAF0] truncate max-w-[120px]">
                  {selectedTheme} 
                </span> 
              </div> 
              <span className="text-[11px] text-[#9AA1AD]">
                {connectingWordsCount === 0 
                  ? '0 connecting words (Free story)' 
                  : connectingWordsCount === -1 
                  ? 'ALL connecting cards' 
                  : `${connectingWordsCount} connecting cards`} 
              </span> 
            </div> 

            <div className="flex items-center text-[#E87A90] text-xs font-medium pl-2">
              <span>Tune</span> 
              <ChevronDown className="w-4 h-4 ml-0.5" /> 
            </div> 
          </div> 
        </div> 

        {!preferences.geminiApiKey && (
          <div className="rounded-[20px] bg-[#252A35] border border-[#9678B6]/35 p-[18px] flex flex-col gap-2.5 shadow-lg">
            <div className="flex items-center gap-2 text-[#9678B6]">
              <Sparkles className="w-4 h-4" /> 
              <span className="text-xs font-bold tracking-wider uppercase">
                GEMINI API KEY REQUIRED
              </span> 
            </div> 
            <p className="text-[13px] text-[#9AA1AD] leading-relaxed">
              To generate personalized Japanese reading stories based on your Anki flashcards, connect your free Google Gemini API key.
            </p> 
            <div className="flex items-center gap-2.5 pt-1">
              <button
                onClick={onOpenAiConfig}
                className="flex-1 py-2 rounded-[12px] bg-[#E87A90] text-white font-semibold text-xs active:scale-95 transition-all shadow"
              >
                Enter API Key
              </button> 
              <a
                href="https://aistudio.google.com/app/apikey"
                target="_blank"
                rel="noreferrer"
                className="flex-1 py-2 rounded-[12px] border border-[#2C3240] text-[#E8EAF0] font-semibold text-xs flex items-center justify-center gap-1.5 active:scale-95 transition-all"
              >
                <ExternalLink className="w-3.5 h-3.5 text-[#9AA1AD]" /> 
                Get Free Key
              </a> 
            </div> 
          </div> 
        )}

        {generationError && (
          <div className="rounded-[16px] bg-[#3B1E22] border border-[#E87A90]/40 p-3.5 flex items-center gap-2.5 text-xs text-[#E87A90] shadow-sm">
            <AlertCircle className="w-4 h-4 shrink-0" /> 
            <span>{generationError}</span> 
          </div> 
        )}

        <Squircle3DButton
          onClick={handleGenerateStory}
          disabled={isGenerating}
          containerColor={buttonBg}
          bevelColor={buttonBevel}
          hasSweepingShine={!isLockedWithoutKey && !isGenerating}
          height={50}
          shapeRadius={16}
          depth={3}
          className="w-full"
        >
          {isGenerating ? (
            <div className="flex items-center gap-2 text-white">
              <Loader2 className="w-4 h-4 animate-spin" /> 
              <RotatingStatusText phrases={phrases} isGenerating={isGenerating} /> 
            </div> 
          ) : isLockedWithoutKey ? (
            <div className="flex items-center gap-2 text-[#E87A90]">
              <Lock className="w-4 h-4" /> 
              <span>Set up API Key to Generate</span> 
            </div> 
          ) : (
            <div className="flex items-center gap-2 text-white">
              <Sparkles className="w-4 h-4" /> 
              <span>
                {currentStory 
                  ? `Generate Another Story (7/7)` 
                  : `Generate ${selectedJlpt} Story (7/7)`} 
              </span> 
            </div> 
          )}
        </Squircle3DButton> 
      </div> 

      {!currentStory && !isGenerating && (
        <div className="rounded-[26px] bg-[#1E222B] border border-[#2C3240] py-12 px-6 flex flex-col items-center justify-center text-center gap-3 shadow-md">
          <GraduationCap className="w-12 h-12 text-[#E87A90]" /> 
          <div>
            <h3 className="text-sm font-semibold text-[#9AA1AD]">
              No story generated yet
            </h3> 
            <p className="text-xs text-[#6E7482] mt-0.5">
              Pick your JLPT level and tap Generate Story above
            </p> 
          </div> 
        </div> 
      )}

      {currentStory && (
        <div className="rounded-[26px] bg-[#1E222B] border border-[#2C3240] p-[22px] flex flex-col gap-4 shadow-xl transition-all">
          <div ref={storyActionsRef} className="flex items-center justify-between">
            <button
              onClick={() => setShowConfigSheet(true)}
              className="px-2.5 py-1 rounded-[8px] bg-[#252A35] border border-[#2C3240] text-xs font-bold text-[#E87A90] active:scale-95 transition-all"
            >
              JLPT {currentStory.jlptLevel} 
            </button> 

            <div className="flex items-center gap-1 text-[#9AA1AD]">
              <button
                onClick={(isPlaying || isAudioPaused) ? handleStopNarration : handlePlayPause}
                className="w-8 h-8 rounded-lg flex items-center justify-center hover:text-[#E8EAF0] hover:bg-white/5 active:scale-95 transition-all"
                title={(isPlaying || isAudioPaused) ? 'Stop Narration' : 'Narrate Story'}
              >
                {(isPlaying || isAudioPaused) ? (
                  <Square className="w-4 h-4 text-[#E87A90] fill-current" /> 
                ) : (
                  <Volume2 className="w-4 h-4" /> 
                )}
              </button> 

              <button
                onClick={() => setShowTranslationSheet(true)}
                className="w-8 h-8 rounded-lg flex items-center justify-center hover:text-[#E8EAF0] hover:bg-white/5 active:scale-95 transition-all"
                title="Translate Story"
              >
                <Languages className="w-4 h-4" /> 
              </button> 

              <button
                onClick={() => setShowFurigana(!showFurigana)}
                className={`w-8 h-8 rounded-lg flex items-center justify-center font-bold text-base active:scale-95 transition-colors select-none ${
                  showFurigana ? 'text-[#E87A90]' : 'text-[#9AA1AD] hover:text-[#E8EAF0]' 
                }`}
                title="Toggle Furigana"
              >
                ふ
              </button> 

              <button
                onClick={() => setHighlightVocab(!highlightVocab)}
                className={`w-8 h-8 rounded-lg flex items-center justify-center active:scale-95 transition-colors ${
                  highlightVocab ? 'text-[#E87A90]' : 'text-[#9AA1AD] hover:text-[#E8EAF0]' 
                }`}
                title="Toggle Vocabulary Highlight"
              >
                <Sparkles className="w-4 h-4" /> 
              </button> 

              <button
                onClick={copyStory}
                className="w-8 h-8 rounded-lg flex items-center justify-center hover:text-[#E8EAF0] hover:bg-white/5 active:scale-95 transition-all"
                title="Copy Story"
              >
                <Copy className="w-4 h-4" /> 
              </button> 

              <button
                onClick={() => setShowConfigSheet(true)}
                className="w-8 h-8 rounded-lg flex items-center justify-center hover:text-[#E8EAF0] hover:bg-white/5 active:scale-95 transition-all"
                title="Theme Settings"
              >
                <ImageIcon className="w-4 h-4" /> 
              </button> 
            </div> 
          </div> 

          {(isPlaying || isAudioPaused) && (
            <div className="rounded-[14px] bg-[#252A35] border border-[#E87A90]/35 p-3 flex items-center justify-between animate-fadeIn shadow-sm">
              <div className="flex items-center gap-2.5">
                <div className="w-7 h-7 rounded-full bg-[#2A1B20] flex items-center justify-center text-[#E87A90]">
                  <Volume2 className="w-3.5 h-3.5" /> 
                </div> 
                <div className="flex flex-col">
                  <span className="text-[11.5px] font-bold text-[#E8EAF0]">
                    {isAudioPaused ? 'Narration Paused' : 'Playing Narration'} 
                  </span> 
                  <span className="text-[10.5px] text-[#9AA1AD]">
                    {currentSentenceIdx < 0 
                      ? 'Title' 
                      : `Sentence ${currentSentenceIdx + 1} / ${sentences.length}`} 
                  </span> 
                </div> 
              </div> 

              <div className="flex items-center gap-1.5">
                <button
                  onClick={handleSpeedCycle}
                  className="px-2 py-1 rounded-[8px] bg-[#2A1B20]/60 text-[#E87A90] font-bold text-[11px] active:scale-90 transition-all"
                >
                  {audioSpeed}x
                </button> 

                <button
                  onClick={handleSkipPrev}
                  disabled={currentSentenceIdx <= 0}
                  className="w-7 h-7 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] disabled:opacity-30 active:scale-90 transition-all"
                >
                  <ChevronDown className="w-4 h-4 rotate-90" /> 
                </button> 

                <button
                  onClick={handlePlayPause}
                  className="w-8 h-8 rounded-full bg-[#E87A90] text-white flex items-center justify-center active:scale-90 transition-all shadow"
                >
                  {isPlaying ? (
                    <Square className="w-3.5 h-3.5 fill-current" /> 
                  ) : (
                    <Volume2 className="w-3.5 h-3.5" /> 
                  )}
                </button> 

                <button
                  onClick={handleSkipNext}
                  disabled={currentSentenceIdx >= sentences.length - 1}
                  className="w-7 h-7 rounded-lg flex items-center justify-center text-[#9AA1AD] hover:text-[#E8EAF0] disabled:opacity-30 active:scale-90 transition-all"
                >
                  <ChevronDown className="w-4 h-4 -rotate-90" /> 
                </button> 
              </div> 
            </div> 
          )}

          <div
            className={`py-1.5 px-2 rounded-[8px] transition-colors ${
              (isPlaying || isAudioPaused) && currentSentenceIdx === -1 
                ? 'bg-[#E87A90]/20' 
                : '' 
            }`}
          >
            <h1 className="text-[22px] font-bold text-[#E8EAF0] leading-[30px] tracking-wide">
              {currentStory.title} 
            </h1> 
          </div> 

          <div className="flex flex-col gap-3.5 pt-1">
            {sentences.map((sent, sIdx) => {
              const isSentenceActive = (isPlaying || isAudioPaused) && currentSentenceIdx === sIdx 
              return (
                <div key={sIdx} className="transition-all">
                  <RubyText
                    text={sent}
                    showFurigana={showFurigana}
                    isSentenceActive={isSentenceActive}
                    targetWords={highlightVocab ? currentStory.targetWords : []}
                    targetWordsData={currentStory.targetWordsData || []}
                    onWordClick={(token) => setSelectedToken(token)}
                  /> 
                </div> 
              ) 
            })}
          </div> 

          {currentStory.targetWords && currentStory.targetWords.length > 0 && (
            <div className="rounded-[14px] bg-[#252A35] border border-[#2C3240] p-3.5 mt-2 flex flex-col gap-2.5">
              <div className="flex items-center gap-2">
                <GraduationCap className="w-4 h-4 text-[#E87A90]" /> 
                <span className="text-[11px] font-bold tracking-wider uppercase text-[#9AA1AD]">
                  TARGET WORDS FROM YOUR CARDS ({currentStory.targetWords.length})
                </span> 
              </div> 

              <div className="flex flex-wrap gap-1.5">
                {currentStory.targetWords.map((word, wIdx) => {
                  const isPresent = currentStory.content.includes(word) 
                  return (
                    <div
                      key={wIdx}
                      onClick={() => {
                        setSelectedToken({
                          surface: word, 
                          isKanji: true, 
                          meaning: '' 
                        }) 
                      }}
                      className={`px-2.5 py-1 rounded-[8px] text-xs font-semibold flex items-center gap-1.5 cursor-pointer border active:scale-95 transition-all ${
                        isPresent 
                          ? 'bg-[#2A1B20] text-[#E87A90] border-[#E87A90]/50' 
                          : 'bg-[#1E222B] text-[#9AA1AD] border-[#2C3240]' 
                      }`}
                    >
                      <span>{word}</span> 
                      {isPresent && (
                        <Check className="w-3 h-3 text-[#5FA77C]" /> 
                      )}
                    </div> 
                  ) 
                })}
              </div> 
            </div> 
          )}

          {currentStory.questions && currentStory.questions.length > 0 && (
            <div className="pt-2">
              <Squircle3DButton
                onClick={() => setIsQuizOpen(true)}
                containerColor="#E87A90"
                bevelColor="#A8475B"
                height={50}
                depth={3}
                shapeRadius={16}
                className="w-full"
              >
                <div className="flex items-center gap-2 text-white font-semibold">
                  <Sparkles className="w-4 h-4" /> 
                  <span>Take Test ({currentStory.questions.length})</span> 
                </div> 
              </Squircle3DButton> 
            </div> 
          )}
        </div> 
      )}

      {currentStory && (
        <>
          <FloatingNarration
            visible={scrolledPastHeader && (isPlaying || isAudioPaused)}
            isPlaying={isPlaying}
            isPaused={isAudioPaused}
            currentSentenceIndex={currentSentenceIdx >= 0 ? currentSentenceIdx : 0}
            totalSentences={sentences.length}
            currentSpeed={audioSpeed}
            onPlayPause={handlePlayPause}
            onSkipPrevious={handleSkipPrev}
            onSkipNext={handleSkipNext}
            onSpeedCycle={handleSpeedCycle}
          /> 

          <FloatingActions
            visible={scrolledPastHeader}
            hasWallpaper={Boolean(currentStory.coverUrl)}
            showFurigana={showFurigana}
            highlightVocab={highlightVocab}
            isPlayingAudio={isPlaying}
            isAudioPaused={isAudioPaused}
            onWallpaperClick={() => setShowConfigSheet(true)}
            onCopyStory={copyStory}
            onToggleHighlightVocab={() => setHighlightVocab(!highlightVocab)}
            onToggleFurigana={() => setShowFurigana(!showFurigana)}
            onOpenTranslation={() => setShowTranslationSheet(true)}
            onToggleAudio={handlePlayPause}
          /> 
        </> 
      )}

      {showConfigSheet && (
        <StoryConfigBottomSheet
          selectedJlpt={selectedJlpt}
          onSelectJlpt={setSelectedJlpt}
          selectedLength={selectedLength}
          onSelectLength={setSelectedLength}
          selectedTheme={selectedTheme}
          onSelectTheme={setSelectedTheme}
          selectedTopic={selectedTopic}
          onSelectTopic={setSelectedTopic}
          connectingWordsCount={connectingWordsCount}
          onSelectConnectingWordsCount={setConnectingWordsCount}
          onDismiss={() => setShowConfigSheet(false)}
        /> 
      )}

      {showHistorySheet && (
        <StoryHistorySheet
          stories={stories}
          onSelectStory={(index) => setCurrentStoryIndex(index)}
          onStoriesChanged={loadStories}
          onDismiss={() => setShowHistorySheet(false)}
        /> 
      )}

      {showTranslationSheet && currentStory && (
        <TranslationBottomSheet
          japaneseText={currentStory.content}
          englishText={currentStory.translation}
          onDismiss={() => setShowTranslationSheet(false)}
          onNavigateToJisho={onSearchJisho}
        /> 
      )}

      {selectedToken && (
        <WordDetailBottomSheet
          token={selectedToken}
          exampleSentenceJp={currentStory?.content}
          exampleSentenceEn={currentStory?.translation}
          onSearchJisho={onSearchJisho}
          onDismiss={() => setSelectedToken(null)}
        /> 
      )}

      {isQuizOpen && currentStory && (
        <QuizOverlay
          questions={currentStory.questions}
          onClose={() => setIsQuizOpen(false)}
          onQuizSubmitted={handleQuizSubmitted}
          onNavigateToJisho={onSearchJisho}
        /> 
      )}
    </div> 
  ) 
} 
