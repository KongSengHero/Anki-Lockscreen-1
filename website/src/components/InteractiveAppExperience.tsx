import { useState } from 'react' 
import { 
  Sparkles, 
  Smartphone, 
  Layers, 
  ChevronLeft, 
  Volume2, 
  Check, 
  ArrowRight, 
  Clock, 
  Wifi, 
  Battery, 
  Download, 
  Zap, 
  BookOpen, 
  Trophy, 
  Bookmark, 
  User, 
  GraduationCap 
} from 'lucide-react' 

interface AnkiCard {
  id: number 
  kanji: string 
  kana: string 
  english: string 
  sentenceJp: string 
  sentenceRubyJp: string 
  sentenceEn: string 
  cardType: number 
} 

const DEMO_ANKI_CARDS: AnkiCard[] = [
  {
    id: 1, 
    kanji: '記憶', 
    kana: 'きおく', 
    english: 'Memory, recollection, remembrance', 
    sentenceJp: '彼女の記憶力は驚くほど鋭い。', 
    sentenceRubyJp: '彼女の記憶力は驚くほど鋭い。', 
    sentenceEn: 'Her memory is surprisingly sharp.', 
    cardType: 0, 
  }, 
  {
    id: 2, 
    kanji: '約束', 
    kana: 'やくそく', 
    english: 'Promise, agreement, arrangement', 
    sentenceJp: 'どんなことがあっても約束を守る。', 
    sentenceRubyJp: 'どんなことがあっても約束を守る。', 
    sentenceEn: 'I keep my promises no matter what.', 
    cardType: 1, 
  }, 
  {
    id: 3, 
    kanji: '挑戦', 
    kana: 'ちょうせん', 
    english: 'Challenge, defiance, dare', 
    sentenceJp: '未知の世界へ挑戦し続ける。', 
    sentenceRubyJp: '未知の世界へ挑戦し続ける。', 
    sentenceEn: 'Continuously challenging the unknown world.', 
    cardType: 2, 
  }, 
] 

export const InteractiveAppExperience = () => {
  const [activeTab, setActiveTab] = useState<'app' | 'lockscreen'>('app') 
  const [appScreen, setAppScreen] = useState<'details' | 'story' | 'quiz' | 'celebrate'>('details') 
  const [selectedWord, setSelectedWord] = useState<{ word: string; kana: string; meaning: string } | null>(null) 
  const [selectedQuizOption, setSelectedQuizOption] = useState<number | null>(null) 

  const [cardIndex, setCardIndex] = useState(0) 
  const [isRevealed, setIsRevealed] = useState(false) 
  const [stats, setStats] = useState({ newCount: 3, learnCount: 1, reviewCount: 14 }) 

  const currentCard = DEMO_ANKI_CARDS[cardIndex] 

  const handleNextCard = () => {
    setIsRevealed(false) 
    setCardIndex((prev) => (prev + 1) % DEMO_ANKI_CARDS.length) 
  } 

  const handlePrevCard = () => {
    setIsRevealed(false) 
    setCardIndex((prev) => (prev - 1 + DEMO_ANKI_CARDS.length) % DEMO_ANKI_CARDS.length) 
  } 

  const handleRate = (type: 'again' | 'hard' | 'good' | 'easy') => {
    if (type === 'again') {
      setStats((s) => ({ ...s, learnCount: s.learnCount + 1 })) 
    } else {
      setStats((s) => ({ 
        ...s, 
        reviewCount: s.reviewCount + 1, 
        newCount: Math.max(0, s.newCount - 1) 
      })) 
    } 
    handleNextCard() 
  } 

  const handleQuizAnswer = (optionIdx: number) => {
    setSelectedQuizOption(optionIdx) 
  } 

  return (
    <section id="interactive" className="relative py-24 px-4 sm:px-6 lg:px-8 bg-[#070A10]"> 
      <div className="mx-auto max-w-7xl"> 
        <div className="text-center mb-12"> 
          <div className="inline-flex items-center gap-2 rounded-full border border-pink-500/30 bg-pink-500/10 px-4 py-1.5 text-xs font-semibold text-pink-300 mb-4"> 
            <Sparkles className="h-3.5 w-3.5" /> 
            <span>Pixel-Faithful Interactive Demo</span> 
          </div> 
          <h2 className="text-3xl sm:text-5xl font-extrabold tracking-tight text-white"> 
            Experience Blossom Directly in Browser 
          </h2> 
          <p className="mt-4 text-base sm:text-lg text-slate-400 max-w-2xl mx-auto"> 
            Identical in every detail. Test Blossom's lockscreen media notification generator or walk through the illustrated story reader and tactile quiz flow. 
          </p> 

          <div className="mt-8 inline-flex p-1.5 rounded-2xl bg-white/[0.04] border border-white/10"> 
            <button 
              onClick={() => setActiveTab('app')} 
              className={`flex items-center gap-2 px-6 py-2.5 rounded-xl text-sm font-bold transition-all ${
                activeTab === 'app' 
                  ? 'bg-gradient-to-r from-pink-500 to-indigo-600 text-white shadow-lg shadow-pink-500/25' 
                  : 'text-slate-400 hover:text-white' 
              }`} 
            > 
              <Smartphone className="h-4 w-4" /> 
              <span>Story & Quiz Reader (App)</span> 
            </button> 
            <button 
              onClick={() => setActiveTab('lockscreen')} 
              className={`flex items-center gap-2 px-6 py-2.5 rounded-xl text-sm font-bold transition-all ${
                activeTab === 'lockscreen' 
                  ? 'bg-gradient-to-r from-pink-500 to-indigo-600 text-white shadow-lg shadow-pink-500/25' 
                  : 'text-slate-400 hover:text-white' 
              }`} 
            > 
              <Layers className="h-4 w-4" /> 
              <span>Lockscreen Media Session</span> 
            </button> 
          </div> 
        </div> 

        <div className="flex justify-center"> 
          {activeTab === 'app' ? (
            <div className="relative w-full max-w-[375px] rounded-[48px] border-[8px] border-[#181D26] bg-[#0E1015] shadow-2xl shadow-indigo-950/40 ring-1 ring-white/10 overflow-hidden flex flex-col min-h-[720px]"> 
              <div className="pt-3 pb-2 px-6 flex justify-between items-center text-[12px] font-semibold text-slate-300 z-30 bg-[#0E1015]"> 
                <span className="font-bold tracking-tight">8:25</span> 
                <div className="flex items-center gap-2 text-slate-400"> 
                  <Wifi className="h-3 w-3" /> 
                  <Battery className="h-3.5 w-3.5 text-emerald-400" /> 
                  <span className="text-[11px] font-bold text-slate-300">98%</span> 
                </div> 
              </div> 

              {appScreen === 'details' && (
                <div className="flex-1 flex flex-col justify-between p-4 bg-[#0E1015] animate-fadeIn"> 
                  <div> 
                    <div className="flex items-center justify-between mb-4"> 
                      <button className="h-9 w-9 rounded-2xl bg-white/[0.06] border border-white/10 flex items-center justify-center text-slate-300"> 
                        <ChevronLeft className="h-5 w-5" /> 
                      </button> 
                      <h3 className="text-base font-bold text-white">Story Details</h3> 
                      <div className="flex items-center gap-1 rounded-full bg-amber-500/20 border border-amber-500/30 px-2.5 py-0.5 text-amber-300 text-xs font-bold"> 
                        <Zap className="h-3 w-3 fill-amber-300" /> 
                        <span>∞</span> 
                      </div> 
                    </div> 

                    <div className="relative rounded-3xl overflow-hidden aspect-[4/3] border border-white/10 shadow-lg"> 
                      <img 
                        src="/assets/designs/StoryDetails_StartReading.jpg" 
                        alt="Makoto Goes to School" 
                        className="h-full w-full object-cover" 
                      /> 
                      <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/20 to-transparent"></div> 
                      <div className="absolute bottom-3.5 left-4 right-4"> 
                        <h4 className="text-lg font-bold text-white leading-snug"> 
                          Makoto Goes to School - マコトは学校へ行く 
                        </h4> 
                      </div> 
                    </div> 

                    <div className="mt-4 flex items-center justify-between"> 
                      <span className="rounded-full bg-sky-400/20 border border-sky-400/30 text-sky-300 px-3.5 py-1 text-xs font-bold"> 
                        School 
                      </span> 
                      <button className="h-8 w-8 rounded-full border border-emerald-500/40 bg-emerald-500/10 flex items-center justify-center text-emerald-400"> 
                        <Download className="h-4 w-4" /> 
                      </button> 
                    </div> 

                    <p className="mt-3 text-xs text-slate-300 leading-relaxed"> 
                      Makoto's morning routine leads him to school and his waiting classroom. 
                    </p> 
                    <p className="mt-1 text-[11px] text-slate-400"> 
                      Author: Kenji 
                    </p> 

                    <div className="mt-4 flex gap-2"> 
                      <div className="flex-1 p-2 rounded-xl bg-white/[0.03] border border-white/[0.06] text-center text-xs text-slate-300 flex items-center justify-center gap-1"> 
                        <Clock className="h-3.5 w-3.5 text-sky-400" /> 
                        <span>~5 mins</span> 
                      </div> 
                      <div className="flex-1 p-2 rounded-xl bg-white/[0.03] border border-white/[0.06] text-center text-xs text-amber-300 flex items-center justify-center gap-1 font-semibold"> 
                        <span>★ 100 XP</span> 
                      </div> 
                      <div className="flex-1 p-2 rounded-xl bg-white/[0.03] border border-white/[0.06] text-center text-xs text-purple-300 flex items-center justify-center gap-1"> 
                        <BookOpen className="h-3.5 w-3.5 text-purple-400" /> 
                        <span>0</span> 
                      </div> 
                    </div> 
                  </div> 

                  <div className="pt-4"> 
                    <button 
                      onClick={() => setAppScreen('story')} 
                      className="w-full relative py-3.5 rounded-2xl bg-blue-600 border-b-4 border-blue-800 text-white font-bold text-sm tracking-wide shadow-lg shadow-blue-600/30 active:translate-y-1 active:border-b-0 transition-all uppercase" 
                    > 
                      Start Reading 
                    </button> 
                  </div> 
                </div> 
              )} 

              {appScreen === 'story' && (
                <div className="flex-1 flex flex-col justify-between p-4 bg-[#0E1015] animate-fadeIn"> 
                  <div> 
                    <div className="flex items-center justify-between mb-3"> 
                      <button 
                        onClick={() => setAppScreen('details')} 
                        className="h-8 w-8 rounded-xl bg-white/[0.06] border border-white/10 flex items-center justify-center text-slate-300" 
                      > 
                        <ChevronLeft className="h-4 w-4" /> 
                      </button> 
                      <span className="text-xs font-bold text-slate-300">Makoto Goes to School</span> 
                      <button className="h-8 w-8 rounded-xl bg-white/[0.06] border border-white/10 flex items-center justify-center text-slate-300"> 
                        <Volume2 className="h-4 w-4 text-sky-400" /> 
                      </button> 
                    </div> 

                    <div className="rounded-2xl overflow-hidden aspect-[16/10] border border-white/10 mb-4"> 
                      <img 
                        src="/assets/designs/MainStory.jpg" 
                        alt="Story Scene" 
                        className="h-full w-full object-cover" 
                      /> 
                    </div> 

                    <div className="p-4 rounded-2xl bg-[#161920] border border-white/[0.08]"> 
                      <div className="text-xl sm:text-2xl font-medium text-white leading-loose tracking-wide font-['Noto_Serif_JP']"> 
                        <span 
                          onClick={() => setSelectedWord({ word: 'マコト', kana: 'まこと', meaning: 'Makoto (Character name)' })} 
                          className="text-purple-400 cursor-pointer hover:underline" 
                        > 
                          マコト 
                        </span> 
                        {' '} 
                        <span 
                          onClick={() => setSelectedWord({ word: 'バス', kana: 'ばす', meaning: 'Bus' })} 
                          className="border-b-2 border-sky-400 cursor-pointer hover:text-sky-300 pb-0.5" 
                        > 
                          はバス 
                        </span> 
                        {' '} 
                        <span 
                          onClick={() => setSelectedWord({ word: '乗って', kana: 'のって', meaning: 'To ride / get on (te-form)' })} 
                          className="border-b-2 border-sky-400 cursor-pointer hover:text-sky-300 pb-0.5" 
                        > 
                          に<ruby>乗<rt className="text-xs text-sky-300 font-bold">の</rt></ruby>って 
                        </span> 
                        {' '} 
                        <span 
                          onClick={() => setSelectedWord({ word: '学校', kana: 'がっこう', meaning: 'School' })} 
                          className="border-b-2 border-sky-400 cursor-pointer hover:text-sky-300 pb-0.5" 
                        > 
                          <ruby>学校<rt className="text-xs text-sky-300 font-bold">がっこう</rt></ruby> 
                        </span> 
                        {' '} 
                        <span 
                          onClick={() => setSelectedWord({ word: '行きます', kana: 'いきます', meaning: 'To go (polite present)' })} 
                          className="border-b-2 border-sky-400 cursor-pointer hover:text-sky-300 pb-0.5" 
                        > 
                          に<ruby>行<rt className="text-xs text-sky-300 font-bold">い</rt></ruby>きます 
                        </span> 
                      </div> 
                    </div> 

                    {selectedWord && (
                      <div className="mt-3 p-3.5 rounded-xl bg-sky-950/40 border border-sky-500/30 text-xs animate-fadeIn"> 
                        <div className="flex items-center justify-between text-sky-300 font-bold"> 
                          <span>{selectedWord.word} ({selectedWord.kana})</span> 
                          <button onClick={() => setSelectedWord(null)} className="text-slate-400 hover:text-white">✕</button> 
                        </div> 
                        <div className="text-slate-300 mt-1">{selectedWord.meaning}</div> 
                      </div> 
                    )} 
                  </div> 

                  <div className="pt-4"> 
                    <button 
                      onClick={() => setAppScreen('quiz')} 
                      className="w-full relative py-3.5 rounded-2xl bg-blue-600 border-b-4 border-blue-800 text-white font-bold text-sm tracking-wide shadow-lg shadow-blue-600/30 active:translate-y-1 active:border-b-0 transition-all uppercase flex items-center justify-center gap-2" 
                    > 
                      <span>Continue to Quiz</span> 
                      <ArrowRight className="h-4 w-4" /> 
                    </button> 
                  </div> 
                </div> 
              )} 

              {appScreen === 'quiz' && (
                <div className="flex-1 flex flex-col justify-between p-4 bg-[#0E1015] animate-fadeIn"> 
                  <div> 
                    <div className="flex items-center justify-between mb-3"> 
                      <button 
                        onClick={() => setAppScreen('story')} 
                        className="h-8 w-8 rounded-xl bg-white/[0.06] border border-white/10 flex items-center justify-center text-slate-300" 
                      > 
                        <ChevronLeft className="h-4 w-4" /> 
                      </button> 
                      <span className="text-xs font-bold text-slate-300">Comprehension Drill</span> 
                      <span className="text-xs font-bold text-amber-400">+100 XP</span> 
                    </div> 

                    <div className="rounded-2xl overflow-hidden aspect-[16/10] border border-white/10 mb-3"> 
                      <img 
                        src="/assets/designs/QuizesMain.jpg" 
                        alt="Quiz Illustration" 
                        className="h-full w-full object-cover" 
                      /> 
                    </div> 

                    <div className="p-3.5 rounded-2xl bg-[#161920] border border-white/[0.08] text-center mb-3"> 
                      <div className="text-xl font-bold text-white font-['Noto_Serif_JP']"> 
                        <ruby>私<rt className="text-xs text-sky-300 font-bold">わたし</rt></ruby> は <span className="text-sky-400 border-b-2 border-sky-400 px-3 inline-block">______</span> です。 
                      </div> 
                      <div className="text-xs text-slate-400 mt-1"> 
                        I am 25 years old. 
                      </div> 
                    </div> 

                    <div className="grid grid-cols-2 gap-2.5"> 
                      {[
                        { id: 0, text: '雨', furigana: 'あめ' }, 
                        { id: 1, text: '学校', furigana: 'がっこう' }, 
                        { id: 2, text: '木', furigana: 'き' }, 
                        { id: 3, text: '25 歳', furigana: 'にじゅうごさい' }, 
                      ].map((opt) => {
                        const isSelected = selectedQuizOption === opt.id 
                        const isCorrect = opt.id === 3 
                        return (
                          <button 
                            key={opt.id} 
                            onClick={() => handleQuizAnswer(opt.id)} 
                            className={`p-3 rounded-2xl border text-center transition-all ${
                              isSelected 
                                ? isCorrect 
                                  ? 'bg-emerald-500/20 border-emerald-500 text-emerald-300 shadow-md shadow-emerald-500/20' 
                                  : 'bg-rose-500/20 border-rose-500 text-rose-300' 
                                : 'bg-white/[0.03] border-white/[0.08] text-slate-200 hover:bg-white/[0.06]' 
                            }`} 
                          > 
                            <div className="text-[11px] text-slate-400">{opt.furigana}</div> 
                            <div className="text-base font-bold">{opt.text}</div> 
                          </button> 
                        ) 
                      })} 
                    </div> 
                  </div> 

                  <div className="pt-4"> 
                    <button 
                      onClick={() => setAppScreen('celebrate')} 
                      disabled={selectedQuizOption !== 3} 
                      className={`w-full relative py-3.5 rounded-2xl font-bold text-sm tracking-wide transition-all uppercase ${
                        selectedQuizOption === 3 
                          ? 'bg-blue-600 border-b-4 border-blue-800 text-white shadow-lg shadow-blue-600/30 active:translate-y-1 active:border-b-0' 
                          : 'bg-slate-800 text-slate-500 cursor-not-allowed' 
                      }`} 
                    > 
                      Continue 
                    </button> 
                  </div> 
                </div> 
              )} 

              {appScreen === 'celebrate' && (
                <div className="flex-1 flex flex-col justify-between p-6 bg-[#041F12] animate-fadeIn text-center"> 
                  <div className="pt-6"> 
                    <div className="mx-auto h-20 w-20 rounded-full bg-emerald-500 flex items-center justify-center text-slate-950 shadow-xl shadow-emerald-500/40 mb-6 animate-bounce"> 
                      <Check className="h-10 w-10 stroke-[3]" /> 
                    </div> 

                    <h3 className="text-2xl font-extrabold text-white"> 
                      おめでとう! 
                    </h3> 
                    <h4 className="text-xl font-bold text-emerald-300 mt-1 italic"> 
                      Congratulations! 
                    </h4> 
                    <p className="mt-3 text-xs text-slate-300 max-w-xs mx-auto leading-relaxed"> 
                      You successfully completed the story and unlocked a new one! 
                    </p> 

                    <div className="mt-8 grid grid-cols-2 gap-3"> 
                      <div className="p-4 rounded-2xl bg-emerald-600 text-white font-bold text-center"> 
                        <div className="text-xs text-emerald-100 flex justify-end">+1</div> 
                        <div className="text-2xl flex items-center justify-center gap-1 mt-1"> 
                          <span>3</span> 
                          <BookOpen className="h-5 w-5" /> 
                        </div> 
                      </div> 
                      <div className="p-4 rounded-2xl bg-blue-600 text-white font-bold text-center"> 
                        <div className="text-xs text-blue-100 flex justify-end">+100</div> 
                        <div className="text-2xl flex items-center justify-center gap-1 mt-1"> 
                          <span>550</span> 
                          <span className="text-xs font-normal">XP</span> 
                        </div> 
                      </div> 
                    </div> 
                  </div> 

                  <div className="pt-6"> 
                    <button 
                      onClick={() => {
                        setAppScreen('details') 
                        setSelectedQuizOption(null) 
                      }} 
                      className="w-full py-3.5 rounded-2xl bg-white text-slate-950 font-extrabold text-sm tracking-wide shadow-lg shadow-white/10 active:scale-98 transition-all uppercase" 
                    > 
                      Continue 
                    </button> 
                  </div> 
                </div> 
              )} 

              <div className="py-2.5 px-6 border-t border-white/[0.06] bg-[#0A0D14] flex justify-between items-center text-slate-400"> 
                <div className="flex flex-col items-center gap-0.5 text-sky-400"> 
                  <div className="h-7 w-7 rounded-full bg-sky-500/20 flex items-center justify-center"> 
                    <GraduationCap className="h-4 w-4 text-sky-400" /> 
                  </div> 
                </div> 
                <div className="flex flex-col items-center gap-0.5"> 
                  <div className="h-7 w-7 rounded-full bg-emerald-500/20 flex items-center justify-center"> 
                    <BookOpen className="h-4 w-4 text-emerald-400" /> 
                  </div> 
                </div> 
                <div className="relative flex flex-col items-center gap-0.5"> 
                  <div className="h-7 w-7 rounded-full bg-orange-500/20 flex items-center justify-center"> 
                    <Trophy className="h-4 w-4 text-orange-400" /> 
                  </div> 
                  <span className="absolute -top-1 -right-1.5 h-4 w-4 rounded-full bg-rose-500 text-white text-[9px] font-bold flex items-center justify-center"> 
                    19 
                  </span> 
                </div> 
                <div className="flex flex-col items-center gap-0.5"> 
                  <div className="h-7 w-7 rounded-full bg-amber-500/20 flex items-center justify-center"> 
                    <Bookmark className="h-4 w-4 text-amber-400" /> 
                  </div> 
                </div> 
                <div className="flex flex-col items-center gap-0.5"> 
                  <div className="h-7 w-7 rounded-full bg-purple-500/20 flex items-center justify-center"> 
                    <User className="h-4 w-4 text-purple-400" /> 
                  </div> 
                </div> 
              </div> 
            </div> 
          ) : (
            <div className="relative w-full max-w-[380px] rounded-[48px] border-[8px] border-[#181D26] bg-[#06080E] p-4 shadow-2xl shadow-cyan-950/40 ring-1 ring-white/10 flex flex-col min-h-[660px] justify-between"> 
              <div> 
                <div className="pt-2 pb-2 px-3 flex justify-between items-center text-[11px] font-semibold text-slate-400"> 
                  <span>09:42</span> 
                  <div className="flex items-center gap-2"> 
                    <Wifi className="h-3 w-3" /> 
                    <Battery className="h-3.5 w-3.5" /> 
                  </div> 
                </div> 

                <div className="text-center my-4"> 
                  <div className="text-5xl font-light tracking-tight text-white font-sans">09:42</div> 
                  <div className="text-xs text-slate-400 mt-1">Friday, September 11</div> 
                </div> 

                <div className="rounded-3xl border border-white/10 bg-[#0E1524]/95 p-4 backdrop-blur-2xl shadow-xl"> 
                  <div className="flex items-center justify-between border-b border-white/[0.08] pb-2.5 mb-3 text-xs"> 
                    <div className="flex items-center gap-1.5 font-bold"> 
                      <span className="text-[#8AB4F8]">{stats.newCount}</span> 
                      <span className="text-slate-500">·</span> 
                      <span className="text-[#F28B82]">{stats.learnCount}</span> 
                      <span className="text-slate-500">·</span> 
                      <span className="text-[#81C995]">{stats.reviewCount}</span> 
                    </div> 
                    <span className="text-xs font-semibold text-slate-300"> 
                      Core 2.3k Vocabulary 
                    </span> 
                  </div> 

                  <div className="text-center py-6 min-h-[160px] flex flex-col items-center justify-center"> 
                    <div className="text-4xl font-extrabold text-white tracking-wide font-['Noto_Serif_JP']"> 
                      {isRevealed ? (
                        <ruby> 
                          {currentCard.kanji} 
                          <rt className="text-sm font-semibold text-[#7EB6FF] block mb-1"> 
                            {currentCard.kana} 
                          </rt> 
                        </ruby> 
                      ) : (
                        currentCard.kanji 
                      )} 
                    </div> 

                    {isRevealed && (
                      <div className="mt-3 animate-fadeIn space-y-1"> 
                        <div className="text-sm font-bold text-[#F1F5F9]"> 
                          {currentCard.english} 
                        </div> 
                        <div className="text-xs text-slate-300 font-['Noto_Serif_JP'] pt-2"> 
                          {currentCard.sentenceJp} 
                        </div> 
                        <div className="text-[11px] text-slate-400 italic"> 
                          {currentCard.sentenceEn} 
                        </div> 
                      </div> 
                    )} 
                  </div> 

                  <div className="flex items-center justify-center gap-6 pt-3 border-t border-white/[0.06]"> 
                    <button 
                      onClick={handlePrevCard} 
                      className="text-slate-400 hover:text-white transition-colors" 
                      title="Previous Card" 
                    > 
                      ⏮ 
                    </button> 
                    <button 
                      onClick={() => setIsRevealed(!isRevealed)} 
                      className="px-4 py-1.5 rounded-xl bg-cyan-500/20 border border-cyan-500/40 text-cyan-300 text-xs font-bold hover:bg-cyan-500/30 transition-all" 
                    > 
                      {isRevealed ? 'Hide' : 'Reveal'} 
                    </button> 
                    <button 
                      onClick={handleNextCard} 
                      className="text-slate-400 hover:text-white transition-colors" 
                      title="Next Card" 
                    > 
                      ⏭ 
                    </button> 
                  </div> 
                </div> 
              </div> 

              <div className="p-3 bg-white/[0.02] border border-white/[0.06] rounded-2xl"> 
                <div className="text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-2 text-center"> 
                  SRS Rating (Android Notification Actions) 
                </div> 
                <div className="grid grid-cols-4 gap-1.5 text-xs font-bold"> 
                  <button 
                    onClick={() => handleRate('again')} 
                    className="p-2 rounded-xl bg-rose-500/20 text-rose-300 border border-rose-500/30 hover:bg-rose-500/30" 
                  > 
                    Again 
                  </button> 
                  <button 
                    onClick={() => handleRate('hard')} 
                    className="p-2 rounded-xl bg-amber-500/20 text-amber-300 border border-amber-500/30 hover:bg-amber-500/30" 
                  > 
                    Hard 
                  </button> 
                  <button 
                    onClick={() => handleRate('good')} 
                    className="p-2 rounded-xl bg-blue-500/20 text-blue-300 border border-blue-500/30 hover:bg-blue-500/30" 
                  > 
                    Good 
                  </button> 
                  <button 
                    onClick={() => handleRate('easy')} 
                    className="p-2 rounded-xl bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 hover:bg-emerald-500/30" 
                  > 
                    Easy 
                  </button> 
                </div> 
              </div> 
            </div> 
          )} 
        </div> 
      </div> 
    </section> 
  ) 
} 
