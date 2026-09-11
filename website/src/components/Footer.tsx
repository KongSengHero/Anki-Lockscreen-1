import { Smartphone, Shield, GitBranch } from 'lucide-react' 

export const Footer = () => {
  return (
    <footer className="w-full border-t border-white/[0.06] bg-[#05080E] py-12 px-4 sm:px-6 lg:px-8"> 
      <div className="mx-auto max-w-7xl flex flex-col md:flex-row items-center justify-between gap-6"> 
        <div className="flex items-center gap-3"> 
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-cyan-500/10 border border-cyan-500/20 text-cyan-400"> 
            <Smartphone className="h-5 w-5" /> 
          </div> 
          <div className="flex flex-col"> 
            <span className="text-sm font-bold text-white">Blossom</span> 
            <span className="text-xs text-slate-500"> 
              Open-source lockscreen immersion tool for Android 
            </span> 
          </div> 
        </div> 

        <div className="flex items-center gap-6 text-xs text-slate-400"> 
          <span className="flex items-center gap-1.5"> 
            <Shield className="h-4 w-4 text-emerald-400" /> 
            GPL v3.0 / Open Source 
          </span> 
          <a 
            href="https://github.com" 
            target="_blank" 
            rel="noreferrer" 
            className="flex items-center gap-1.5 hover:text-cyan-400 transition-colors" 
          > 
            <GitBranch className="h-4 w-4" /> 
            Repository 
          </a> 
        </div> 

        <div className="text-xs text-slate-500 flex items-center gap-1"> 
          <span>Crafted for effortless Japanese acquisition</span> 
        </div> 
      </div> 
    </footer> 
  ) 
} 
