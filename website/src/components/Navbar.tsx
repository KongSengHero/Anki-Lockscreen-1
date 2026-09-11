import { ShieldCheck, Download, Smartphone, Sparkles, BookOpen, Layers } from 'lucide-react' 

export const Navbar = () => {
  return (
    <header className="sticky top-0 z-50 w-full border-b border-white/[0.06] bg-[#080C14]/80 backdrop-blur-xl"> 
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8"> 
        <a href="#" className="flex items-center gap-3 transition-opacity hover:opacity-90"> 
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-cyan-500 to-indigo-600 shadow-lg shadow-cyan-500/20"> 
            <Smartphone className="h-5 w-5 text-white" /> 
          </div> 
          <div className="flex flex-col"> 
            <span className="text-base font-bold tracking-tight text-white"> 
              Blossom 
            </span> 
            <span className="text-[10px] font-medium uppercase tracking-widest text-cyan-400"> 
              Lockscreen Immersion 
            </span> 
          </div> 
        </a> 
 
        <nav className="hidden md:flex items-center gap-8 text-sm font-medium text-slate-300"> 
          <a href="#demo-video" className="transition-colors hover:text-cyan-400 flex items-center gap-1.5"> 
            <Sparkles className="h-4 w-4 text-cyan-400" /> 
            App Showcase 
          </a> 
          <a href="#interactive" className="transition-colors hover:text-cyan-400 flex items-center gap-1.5"> 
            <Layers className="h-4 w-4 text-indigo-400" /> 
            Interactive Demo 
          </a> 
          <a href="#features" className="transition-colors hover:text-cyan-400 flex items-center gap-1.5"> 
            <BookOpen className="h-4 w-4 text-emerald-400" /> 
            Features 
          </a> 
          <a href="#guide" className="transition-colors hover:text-cyan-400 flex items-center gap-1.5"> 
            <ShieldCheck className="h-4 w-4 text-amber-400" /> 
            Setup Guide 
          </a> 
        </nav> 

        <div className="flex items-center gap-3"> 
          <a 
            href="#download" 
            className="flex items-center gap-2 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 px-4 py-2 text-xs font-semibold text-white shadow-lg shadow-cyan-500/25 transition-all hover:brightness-110 active:scale-95" 
          > 
            <Download className="h-4 w-4" /> 
            <span>Get APK</span> 
          </a> 
        </div> 
      </div> 
    </header> 
  ) 
} 
