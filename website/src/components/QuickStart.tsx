import React from 'react' 
import { Download, Sliders, CheckCircle2, ShieldCheck, ExternalLink } from 'lucide-react' 

interface StepItem {
  number: string 
  title: string 
  description: string 
  icon: React.ElementType 
  detail: string 
} 

const STEPS: StepItem[] = [
  {
    number: '01', 
    title: 'Install AnkiDroid & Blossom', 
    description: 'Ensure AnkiDroid is installed on your Android device and has at least one deck with due cards.', 
    icon: Download, 
    detail: 'Blossom works alongside your existing collection without duplicating cards.', 
  }, 
  {
    number: '02', 
    title: 'Enable AnkiDroid API Permission', 
    description: 'Open AnkiDroid > Settings > Advanced, then toggle "Enable AnkiDroid API" to allow secure local queries.', 
    icon: Sliders, 
    detail: 'This grants read and answer write access strictly to your local database.', 
  }, 
  {
    number: '03', 
    title: 'Select Active Decks & Lock Phone', 
    description: 'Launch Blossom, pick the decks you wish to review, turn on the lockscreen service, and tap your power button.', 
    icon: CheckCircle2, 
    detail: 'Your next due card will be waiting face-down on your media player notification.', 
  }, 
] 

export const QuickStart: React.FC = () => {
  return (
    <section id="guide" className="relative py-20 px-4 sm:px-6 lg:px-8 border-t border-white/[0.06]"> 
      <div className="mx-auto max-w-6xl"> 
        <div className="text-center max-w-2xl mx-auto mb-16"> 
          <div className="inline-flex items-center gap-2 rounded-full border border-amber-500/20 bg-amber-500/10 px-3.5 py-1 text-xs font-semibold text-amber-400 mb-4"> 
            <ShieldCheck className="h-3.5 w-3.5" /> 
            <span>3-Minute Setup</span> 
          </div> 
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-white"> 
            Start reviewing without changing your routine 
          </h2> 
          <p className="mt-3 text-base text-slate-400"> 
            No complex configuration. Fully compatible with AnkiDroid 2.16+ and Android 10 through Android 15. 
          </p> 
        </div> 

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8"> 
          {STEPS.map((step, idx) => {
            const Icon = step.icon 
            return (
              <div 
                key={idx} 
                className="relative rounded-2xl border border-white/[0.08] bg-white/[0.02] p-7 backdrop-blur-xl flex flex-col justify-between" 
              > 
                <div> 
                  <div className="flex items-center justify-between mb-6"> 
                    <span className="text-3xl font-black text-cyan-500/40 font-mono"> 
                      {step.number} 
                    </span> 
                    <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-cyan-500/10 text-cyan-400 border border-cyan-500/20"> 
                      <Icon className="h-5 w-5" /> 
                    </div> 
                  </div> 

                  <h3 className="text-lg font-bold text-white mb-2"> 
                    {step.title} 
                  </h3> 
                  <p className="text-sm text-slate-400 leading-relaxed mb-4"> 
                    {step.description} 
                  </p> 
                </div> 

                <div className="pt-4 border-t border-white/[0.06] text-xs text-slate-500"> 
                  {step.detail} 
                </div> 
              </div> 
            ) 
          })} 
        </div> 

        <div id="download" className="mt-16 rounded-3xl border border-cyan-500/20 bg-gradient-to-r from-cyan-950/40 via-indigo-950/40 to-slate-900/60 p-8 sm:p-12 text-center backdrop-blur-2xl"> 
          <h3 className="text-2xl sm:text-3xl font-bold text-white"> 
            Ready to upgrade your Japanese study habit? 
          </h3> 
          <p className="mt-3 text-sm sm:text-base text-slate-300 max-w-xl mx-auto"> 
            Download the latest release APK or build directly from source using the self-contained Android project. 
          </p> 

          <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4"> 
            <a 
              href="https://github.com" 
              target="_blank" 
              rel="noreferrer" 
              className="w-full sm:w-auto flex items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 px-7 py-3.5 text-sm font-bold text-white shadow-xl shadow-cyan-500/25 transition-all hover:brightness-110 active:scale-95" 
            > 
              <Download className="h-4 w-4" /> 
              <span>Download Blossom v2.4 APK</span> 
            </a> 

            <a 
              href="https://github.com" 
              target="_blank" 
              rel="noreferrer" 
              className="w-full sm:w-auto flex items-center justify-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-7 py-3.5 text-sm font-semibold text-slate-200 transition-all hover:bg-white/[0.08]" 
            > 
              <span>View on GitHub</span> 
              <ExternalLink className="h-4 w-4 text-slate-400" /> 
            </a> 
          </div> 
        </div> 
      </div> 
    </section> 
  ) 
} 
