import { Download, Sparkles, ShieldCheck, Zap, ArrowRight } from 'lucide-react' 

export const Hero = () => {
  return (
    <section className="relative overflow-hidden pt-20 pb-16 lg:pt-28 lg:pb-24"> 
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 h-96 w-96 rounded-full bg-cyan-500/10 blur-[120px] pointer-events-none"></div> 
      <div className="absolute top-1/3 left-1/3 -translate-x-1/2 -translate-y-1/2 h-80 w-80 rounded-full bg-indigo-500/10 blur-[140px] pointer-events-none"></div> 

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 text-center relative z-10"> 
        <div className="inline-flex items-center gap-2 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-4 py-1.5 text-xs font-semibold text-cyan-300 shadow-sm backdrop-blur-md mb-8"> 
          <Sparkles className="h-4 w-4 text-cyan-400" /> 
          <span>Blossom v2.4 · Illustrated Stories & Lockscreen Immersion Engine</span> 
        </div> 

        <h1 className="text-4xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight text-white max-w-4xl mx-auto leading-[1.12]"> 
          Turn Every Phone Unlock Into a{' '} 
          <span className="bg-gradient-to-r from-cyan-400 via-teal-300 to-indigo-400 bg-clip-text text-transparent"> 
            5-Second Japanese Review 
          </span> 
        </h1> 

        <p className="mt-6 text-base sm:text-lg text-slate-300 max-w-2xl mx-auto leading-relaxed"> 
          Blossom transforms Android media notifications into an interactive flashcard session. Answer cards face-down from your lock screen, immerse yourself in single-page illustrated stories, and build lasting fluency without breaking your day. 
        </p> 

        <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4"> 
          <a 
            href="#download" 
            className="w-full sm:w-auto flex items-center justify-center gap-2.5 rounded-2xl bg-gradient-to-r from-cyan-500 to-indigo-600 px-7 py-3.5 text-sm font-bold text-white shadow-xl shadow-cyan-500/25 transition-all hover:brightness-110 active:scale-95" 
          > 
            <Download className="h-5 w-5" /> 
            <span>Download Blossom APK</span> 
          </a> 

          <a 
            href="#interactive" 
            className="w-full sm:w-auto flex items-center justify-center gap-2.5 rounded-2xl border border-white/10 bg-white/[0.04] px-7 py-3.5 text-sm font-semibold text-slate-200 transition-all hover:bg-white/[0.08] hover:text-white" 
          > 
            <span>Try Interactive Demo</span> 
            <ArrowRight className="h-4 w-4 text-cyan-400" /> 
          </a> 
        </div> 

        <div className="mt-12 flex flex-wrap items-center justify-center gap-6 sm:gap-10 text-xs font-semibold text-slate-400"> 
          <div className="flex items-center gap-2"> 
            <ShieldCheck className="h-4 w-4 text-emerald-400" /> 
            <span>100% On-Device & Private</span> 
          </div> 
          <div className="flex items-center gap-2"> 
            <Zap className="h-4 w-4 text-cyan-400" /> 
            <span>Official AnkiDroid API</span> 
          </div> 
          <div className="flex items-center gap-2"> 
            <Sparkles className="h-4 w-4 text-indigo-400" /> 
            <span>Zero Root Required</span> 
          </div> 
        </div> 
      </div> 
    </section> 
  ) 
} 
