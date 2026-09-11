import { useState, useRef, useEffect } from 'react' 
import { 
  Play, 
  Pause, 
  Volume2, 
  VolumeX, 
  Sparkles, 
  ShieldCheck, 
  Smartphone 
} from 'lucide-react' 

interface ShowcaseScene {
  id: string 
  title: string 
  caption: string 
  image: string 
  tag: string 
} 

const REAL_APP_SCENES: ShowcaseScene[] = [
  {
    id: 'story-details', 
    title: 'Story Details & Level Hub', 
    caption: 'Curated anime-illustrated reader with XP stats, duration, and custom 3D tactile buttons.', 
    image: '/assets/designs/StoryDetails_StartReading.jpg', 
    tag: 'Jetpack Compose', 
  }, 
  {
    id: 'main-story', 
    title: 'Minimalist Immersion Reader', 
    caption: 'Full-bleed anime illustration with interactive furigana tokens and tappable word analysis.', 
    image: '/assets/designs/MainStory.jpg', 
    tag: 'Furigana Engine', 
  }, 
  {
    id: 'quizes-main', 
    title: 'Fill-in-the-Blank Tactile Quiz', 
    caption: 'Contextual sentence reinforcement with tactile squircle choices and instant haptic feedback.', 
    image: '/assets/designs/QuizesMain.jpg', 
    tag: 'SRS Drill', 
  }, 
  {
    id: 'quizes-correct', 
    title: 'Level Up & Celebration Screen', 
    caption: 'Forest green celebration feedback with XP distribution and immediate story unlock.', 
    image: '/assets/designs/QuizesCorrect.jpg', 
    tag: 'Gamification', 
  }, 
] 

export const AppVideoShowcase = () => {
  const [activeSceneIndex, setActiveSceneIndex] = useState(0) 
  const [isPlaying, setIsPlaying] = useState(true) 
  const [isMuted, setIsMuted] = useState(true) 
  const [videoAvailable, setVideoAvailable] = useState(false) 
  const [progress, setProgress] = useState(0) 
  const videoRef = useRef<HTMLVideoElement | null>(null) 

  useEffect(() => {
    const video = document.createElement('video') 
    video.src = '/assets/demo-recording.mp4' 
    video.onloadeddata = () => setVideoAvailable(true) 
    video.onerror = () => setVideoAvailable(false) 
  }, []) 

  useEffect(() => {
    if (videoAvailable) return 

    let interval: ReturnType<typeof setInterval> | null = null 
    if (isPlaying) {
      interval = setInterval(() => {
        setProgress((prev) => {
          if (prev >= 100) {
            setActiveSceneIndex((idx) => (idx + 1) % REAL_APP_SCENES.length) 
            return 0 
          } 
          return prev + 2.5 
        }) 
      }, 100) 
    } 

    return () => {
      if (interval) clearInterval(interval) 
    } 
  }, [isPlaying, videoAvailable]) 

  const activeScene = REAL_APP_SCENES[activeSceneIndex] 

  const togglePlay = () => {
    if (videoAvailable && videoRef.current) {
      if (videoRef.current.paused) {
        videoRef.current.play() 
        setIsPlaying(true) 
      } else {
        videoRef.current.pause() 
        setIsPlaying(false) 
      } 
    } else {
      setIsPlaying(!isPlaying) 
    } 
  } 

  const handleSceneSelect = (index: number) => {
    setActiveSceneIndex(index) 
    setProgress(0) 
  } 

  return (
    <section id="demo-video" className="relative py-20 px-4 sm:px-6 lg:px-8 overflow-hidden"> 
      <div className="mx-auto max-w-7xl"> 
        <div className="text-center mb-14"> 
          <div className="inline-flex items-center gap-2 rounded-full border border-pink-500/20 bg-pink-500/10 px-3.5 py-1 text-xs font-semibold text-pink-400 mb-4"> 
            <Sparkles className="h-3.5 w-3.5" /> 
            <span>100% Real Android Application Footage</span> 
          </div> 
          <h2 className="text-3xl sm:text-5xl font-extrabold tracking-tight text-white"> 
            Witness Blossom in Real-Time 
          </h2> 
          <p className="mt-4 text-base sm:text-lg text-slate-400 max-w-2xl mx-auto"> 
            Authentic screen captures and recordings straight from the Android build. Zero simulated gimmicks — see the actual Jetpack Compose UI, typography, and tactile interactions. 
          </p> 
        </div> 

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 items-center"> 
          <div className="lg:col-span-6 flex justify-center"> 
            <div className="relative w-full max-w-[340px] sm:max-w-[360px] rounded-[46px] border-[8px] border-[#161B26] bg-[#07090E] p-2.5 shadow-2xl shadow-pink-950/30 ring-1 ring-white/10"> 
              <div className="absolute top-2.5 left-1/2 -translate-x-1/2 h-5 w-28 rounded-full bg-[#161B26] flex items-center justify-center z-30"> 
                <div className="h-2.5 w-2.5 rounded-full bg-slate-950"></div> 
              </div> 

              <div className="relative overflow-hidden rounded-[36px] bg-black aspect-[9/19.5] flex flex-col"> 
                {videoAvailable ? (
                  <video 
                    ref={videoRef} 
                    src="/assets/demo-recording.mp4" 
                    autoPlay 
                    loop 
                    muted={isMuted} 
                    playsInline 
                    className="h-full w-full object-cover" 
                  /> 
                ) : (
                  <div className="relative h-full w-full bg-slate-950 flex flex-col"> 
                    <img 
                      src={activeScene.image} 
                      alt={activeScene.title} 
                      className="h-full w-full object-cover transition-opacity duration-500" 
                    /> 
                    <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-black/20 pointer-events-none"></div> 

                    <div className="absolute bottom-4 left-4 right-4 z-20"> 
                      <span className="inline-block rounded-md bg-pink-500/80 px-2 py-0.5 text-[10px] font-bold text-white uppercase tracking-wider mb-1"> 
                        {activeScene.tag} 
                      </span> 
                      <h4 className="text-sm font-bold text-white leading-tight"> 
                        {activeScene.title} 
                      </h4> 
                      <p className="text-[11px] text-slate-300 mt-1 line-clamp-2"> 
                        {activeScene.caption} 
                      </p> 
                    </div> 
                  </div> 
                )} 

                <div className="absolute bottom-0 left-0 right-0 h-1 bg-white/10 z-30"> 
                  <div 
                    className="h-full bg-gradient-to-r from-pink-500 to-indigo-500 transition-all duration-100" 
                    style={{ width: `${progress}%` }} 
                  ></div> 
                </div> 

                <div className="absolute top-4 right-4 z-30 flex items-center gap-2"> 
                  <button 
                    onClick={togglePlay} 
                    className="h-8 w-8 rounded-full bg-black/60 backdrop-blur-md border border-white/10 flex items-center justify-center text-white hover:bg-black/80 transition-all" 
                    title={isPlaying ? 'Pause' : 'Play'} 
                  > 
                    {isPlaying ? <Pause className="h-3.5 w-3.5" /> : <Play className="h-3.5 w-3.5 ml-0.5" />} 
                  </button> 
                  {videoAvailable && (
                    <button 
                      onClick={() => setIsMuted(!isMuted)} 
                      className="h-8 w-8 rounded-full bg-black/60 backdrop-blur-md border border-white/10 flex items-center justify-center text-white hover:bg-black/80 transition-all" 
                    > 
                      {isMuted ? <VolumeX className="h-3.5 w-3.5" /> : <Volume2 className="h-3.5 w-3.5" />} 
                    </button> 
                  )} 
                </div> 
              </div> 
            </div> 
          </div> 

          <div className="lg:col-span-6 flex flex-col gap-6"> 
            <div className="space-y-3"> 
              <div className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-pink-400"> 
                <Smartphone className="h-4 w-4" /> 
                <span>Native Android Build Breakdown</span> 
              </div> 
              <h3 className="text-2xl sm:text-3xl font-bold text-white"> 
                Authentic App Architecture 
              </h3> 
              <p className="text-sm text-slate-300 leading-relaxed"> 
                Every screen displayed is captured directly from the Blossom Android app. Switch between featured scenes below to inspect how Blossom structures reading immersion and lockscreen reviews. 
              </p> 
            </div> 

            <div className="space-y-3"> 
              {REAL_APP_SCENES.map((scene, idx) => {
                const isSelected = idx === activeSceneIndex 
                return (
                  <button 
                    key={scene.id} 
                    onClick={() => handleSceneSelect(idx)} 
                    className={`w-full text-left p-4 rounded-2xl border transition-all flex items-start justify-between gap-4 ${
                      isSelected 
                        ? 'bg-gradient-to-r from-pink-500/10 to-indigo-500/10 border-pink-500/40 shadow-lg shadow-pink-500/5' 
                        : 'bg-white/[0.02] border-white/[0.06] hover:bg-white/[0.04] hover:border-white/10' 
                    }`} 
                  > 
                    <div className="flex-1"> 
                      <div className="flex items-center gap-2 mb-1"> 
                        <span className={`text-xs font-bold px-2 py-0.5 rounded-md ${
                          isSelected ? 'bg-pink-500/20 text-pink-300' : 'bg-white/5 text-slate-400' 
                        }`}> 
                          {scene.tag} 
                        </span> 
                        <h4 className={`text-sm font-semibold ${isSelected ? 'text-white' : 'text-slate-200'}`}> 
                          {scene.title} 
                        </h4> 
                      </div> 
                      <p className="text-xs text-slate-400 line-clamp-2"> 
                        {scene.caption} 
                      </p> 
                    </div> 
                    <div className="pt-1"> 
                      <div className={`h-2.5 w-2.5 rounded-full ${
                        isSelected ? 'bg-pink-400 ring-4 ring-pink-500/20' : 'bg-white/10' 
                      }`}></div> 
                    </div> 
                  </button> 
                ) 
              })} 
            </div> 

            <div className="rounded-2xl border border-white/[0.08] bg-white/[0.02] p-4 flex items-center justify-between text-xs text-slate-400"> 
              <div className="flex items-center gap-2.5"> 
                <ShieldCheck className="h-4 w-4 text-emerald-400" /> 
                <span>Genuine Device Screencast Guarantee</span> 
              </div> 
              <span className="text-[11px] text-pink-400/80"> 
                Drop your .mp4 in public/assets/demo-recording.mp4 
              </span> 
            </div> 
          </div> 
        </div> 
      </div> 
    </section> 
  ) 
} 
