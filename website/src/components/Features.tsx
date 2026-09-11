import { 
  Smartphone, 
  BookOpen, 
  Search, 
  Layers, 
  ShieldCheck, 
  Volume2, 
  Languages 
} from 'lucide-react' 

interface FeatureItem {
  icon: typeof Smartphone 
  title: string 
  description: string 
  badge: string 
  color: string 
} 

const FEATURES: FeatureItem[] = [
  {
    icon: Smartphone, 
    title: 'Lockscreen Media Player Widget', 
    description: 'Bypasses the barrier of opening apps. Cards appear disguised as a native media player session right on your Android lock screen with quick-answer buttons.', 
    badge: 'Core Engine', 
    color: 'from-cyan-500 to-blue-600', 
  }, 
  {
    icon: BookOpen, 
    title: 'Single-Page AI Immersion Stories', 
    description: 'Read gripping Japanese tales in a clean, unified scrollable page. Tap any word for Furigana rubies, listen to paragraph audio, and reveal sentence translations.', 
    badge: 'Redesigned Reader', 
    color: 'from-indigo-500 to-purple-600', 
  }, 
  {
    icon: Search, 
    title: 'Offline Jisho & Kanji Breakdown', 
    description: 'Look up unknown vocabulary, stroke order, radicals, and JLPT levels instantly without leaving your study flow or needing an internet connection.', 
    badge: '100% Offline', 
    color: 'from-emerald-500 to-teal-600', 
  }, 
  {
    icon: Layers, 
    title: 'Multi-Deck Live Carousel', 
    description: 'Seamlessly switch between vocabulary, kanji, and sentence mining decks. Independent reveal states ensure you never accidentally expose your answers.', 
    badge: 'Seamless Workflow', 
    color: 'from-amber-500 to-orange-600', 
  }, 
  {
    icon: Volume2, 
    title: 'Natural Japanese TTS Audio', 
    description: 'Listen to native Japanese speech synthesis for every card and story paragraph. Train your ears alongside your reading comprehension.', 
    badge: 'Audio Narration', 
    color: 'from-rose-500 to-pink-600', 
  }, 
  {
    icon: ShieldCheck, 
    title: 'Zero Tracking & Local AnkiDroid Sync', 
    description: 'Blossom talks directly to the official AnkiDroid content provider on your device. Your data, scheduling algorithm, and statistics remain completely yours.', 
    badge: 'Privacy First', 
    color: 'from-cyan-400 to-emerald-500', 
  }, 
] 
 
export const Features = () => {
  return (
    <section id="features" className="relative py-20 px-4 sm:px-6 lg:px-8 border-t border-white/[0.06]"> 
      <div className="mx-auto max-w-7xl"> 
        <div className="text-center max-w-3xl mx-auto mb-16"> 
          <div className="inline-flex items-center gap-2 rounded-full border border-indigo-500/20 bg-indigo-500/10 px-3.5 py-1 text-xs font-semibold text-indigo-400 mb-4"> 
            <Languages className="h-3.5 w-3.5" /> 
            <span>Built For Dedicated Learners</span> 
          </div> 
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-white"> 
            Engineered to eliminate Japanese study friction 
          </h2> 
          <p className="mt-4 text-base text-slate-400"> 
            Traditional flashcard apps require conscious willpower to launch. Blossom embeds SRS micro-reviews into your existing habits. 
          </p> 
        </div> 

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"> 
          {FEATURES.map((item, idx) => {
            const Icon = item.icon 
            return (
              <div 
                key={idx} 
                className="group relative rounded-2xl border border-white/[0.08] bg-white/[0.02] p-7 backdrop-blur-xl transition-all hover:bg-white/[0.04] hover:border-white/20 hover:-translate-y-1" 
              > 
                <div className="flex items-center justify-between mb-5"> 
                  <div className={`flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-tr ${item.color} shadow-lg shadow-black/40`}> 
                    <Icon className="h-6 w-6 text-white" /> 
                  </div> 
                  <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider px-2.5 py-1 rounded-md bg-white/[0.05]"> 
                    {item.badge} 
                  </span> 
                </div> 

                <h3 className="text-lg font-bold text-white group-hover:text-cyan-300 transition-colors"> 
                  {item.title} 
                </h3> 
                <p className="mt-2.5 text-sm text-slate-400 leading-relaxed"> 
                  {item.description} 
                </p> 
              </div> 
            ) 
          })} 
        </div> 
      </div> 
    </section> 
  ) 
} 
