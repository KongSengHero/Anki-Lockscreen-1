import React, { useState } from 'react' 
import { X, Cpu, Mic, Eye, EyeOff, Check, Share, PlusSquare, Sliders } from 'lucide-react' 
import type { UserPreferences } from '../../../types/preferences' 

interface SettingsModalProps {
  preferences: UserPreferences 
  onUpdatePreference: <K extends keyof UserPreferences>(key: K, value: UserPreferences[K]) => void 
  onClose: () => void 
} 

export const SettingsModal: React.FC<SettingsModalProps> = ({
  preferences, 
  onUpdatePreference, 
  onClose 
}) => {
  const [showGeminiKey, setShowGeminiKey] = useState(false) 
  const [showFishKey, setShowFishKey] = useState(false) 
  const [savedToast, setSavedToast] = useState(false) 

  const handleSave = () => {
    setSavedToast(true) 
    setTimeout(() => {
      setSavedToast(false) 
      onClose() 
    }, 800) 
  } 

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div 
        className="fixed inset-0 bg-black/75 backdrop-blur-md" 
        onClick={onClose} 
      /> 

      <div className="relative w-full max-w-lg bg-[#0D111A] border border-white/15 rounded-3xl p-6 sm:p-7 shadow-2xl z-10 max-h-[90vh] overflow-y-auto no-scrollbar">
        <div className="flex items-center justify-between pb-4 border-b border-white/10">
          <div>
            <h3 className="text-xl font-bold text-white">
              App Settings
            </h3> 
            <p className="text-xs text-slate-400 mt-0.5">
              API Keys, Models, and Preferences
            </p> 
          </div> 
          <button
            onClick={onClose}
            className="p-2 rounded-xl bg-white/5 hover:bg-white/10 text-slate-400 hover:text-white transition-colors"
          >
            <X className="w-5 h-5" /> 
          </button> 
        </div> 

        <div className="mt-5 space-y-6">
          <div className="p-4 rounded-2xl bg-cyan-500/10 border border-cyan-500/25">
            <div className="flex items-center gap-2 text-cyan-300 font-semibold text-xs mb-1.5">
              <Share className="w-3.5 h-3.5" /> 
              iOS Safari "Add to Home Screen"
            </div> 
            <p className="text-xs text-slate-300 leading-relaxed">
              To install Blossom as a standalone iOS app, tap the Safari <span className="font-semibold text-white">Share button</span> at the bottom of your screen, then choose <span className="font-semibold text-white">"Add to Home Screen"</span> <PlusSquare className="w-3.5 h-3.5 inline ml-0.5 align-text-bottom" />.
            </p> 
          </div> 

          <div className="space-y-3">
            <div className="text-xs font-bold uppercase tracking-wider text-pink-400 flex items-center gap-1.5">
              <Cpu className="w-3.5 h-3.5" /> 
              Google Gemini AI Story Generation
            </div> 

            <div>
              <label className="text-xs text-slate-300 block mb-1.5 font-medium">
                Gemini API Key
              </label> 
              <div className="relative">
                <input
                  type={showGeminiKey ? 'text' : 'password'}
                  value={preferences.geminiApiKey}
                  onChange={(e) => onUpdatePreference('geminiApiKey', e.target.value)}
                  placeholder="AIzaSy..."
                  className="w-full pl-3 pr-10 py-2.5 rounded-xl bg-[#090D15] border border-white/10 focus:border-pink-500/50 text-white text-xs outline-none"
                /> 
                <button
                  type="button"
                  onClick={() => setShowGeminiKey(prev => !prev)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-white"
                >
                  {showGeminiKey ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button> 
              </div> 
              <span className="text-[11px] text-slate-400 mt-1 block">
                Free key from Google AI Studio. Stored strictly in your browser.
              </span> 
            </div> 

            <div>
              <label className="text-xs text-slate-300 block mb-1.5 font-medium">
                Gemini Model
              </label> 
              <select
                value={preferences.geminiModel}
                onChange={(e) => onUpdatePreference('geminiModel', e.target.value)}
                className="w-full px-3 py-2.5 rounded-xl bg-[#090D15] border border-white/10 text-white text-xs outline-none focus:border-pink-500/50"
              >
                <option value="gemini-2.5-flash">Gemini 2.5 Flash (Fastest & recommended)</option> 
                <option value="gemini-1.5-flash">Gemini 1.5 Flash</option> 
                <option value="gemini-2.0-flash">Gemini 2.0 Flash</option> 
              </select> 
            </div> 
          </div> 

          <div className="space-y-3 pt-4 border-t border-white/10">
            <div className="text-xs font-bold uppercase tracking-wider text-purple-400 flex items-center gap-1.5">
              <Mic className="w-3.5 h-3.5" /> 
              Fish Audio TTS (Optional)
            </div> 

            <div>
              <label className="text-xs text-slate-300 block mb-1.5 font-medium">
                Fish Audio API Key
              </label> 
              <div className="relative">
                <input
                  type={showFishKey ? 'text' : 'password'}
                  value={preferences.fishAudioApiKey}
                  onChange={(e) => onUpdatePreference('fishAudioApiKey', e.target.value)}
                  placeholder="Enter Fish Audio key (leave empty for Web Speech)"
                  className="w-full pl-3 pr-10 py-2.5 rounded-xl bg-[#090D15] border border-white/10 focus:border-purple-500/50 text-white text-xs outline-none"
                /> 
                <button
                  type="button"
                  onClick={() => setShowFishKey(prev => !prev)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-white"
                >
                  {showFishKey ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button> 
              </div> 
              <span className="text-[11px] text-slate-400 mt-1 block">
                Leave blank to use free built-in browser speech synthesis.
              </span> 
            </div> 
          </div> 

          <div className="space-y-3 pt-4 border-t border-white/10">
            <div className="text-xs font-bold uppercase tracking-wider text-orange-400 flex items-center gap-1.5">
              <Sliders className="w-3.5 h-3.5" /> 
              Artwork & Reading Theme
            </div> 

            <div>
              <div className="flex justify-between text-xs text-slate-300 mb-1.5">
                <span>Cover Dimming</span> 
                <span>{preferences.artworkDimming}%</span> 
              </div> 
              <input
                type="range"
                min="10"
                max="80"
                value={preferences.artworkDimming}
                onChange={(e) => onUpdatePreference('artworkDimming', Number(e.target.value))}
                className="w-full accent-pink-500"
              /> 
            </div> 
          </div> 

          <div className="pt-4 border-t border-white/10 flex justify-end gap-3">
            <button
              onClick={handleSave}
              className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-pink-500 to-rose-500 hover:from-pink-400 text-white font-medium text-xs shadow-lg shadow-pink-500/25 transition-all active:scale-95"
            >
              {savedToast ? <Check className="w-4 h-4" /> : null}
              {savedToast ? 'Saved!' : 'Done'} 
            </button> 
          </div> 
        </div> 
      </div> 
    </div> 
  ) 
} 
