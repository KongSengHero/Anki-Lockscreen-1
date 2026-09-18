import React, { useState } from 'react' 
import { Key, Eye, EyeOff, CheckCircle2, ExternalLink, Sparkles, Volume2, X } from 'lucide-react' 

interface AiKeyConfigDialogProps {
  geminiApiKey: string 
  fishAudioApiKey?: string 
  fishAudioVoiceId?: string 
  onSave: (keys: {
    geminiApiKey: string 
    fishAudioApiKey: string 
    fishAudioVoiceId: string 
  }) => void 
  onDismiss: () => void 
} 

export const AiKeyConfigDialog: React.FC<AiKeyConfigDialogProps> = ({
  geminiApiKey: initialGemini, 
  fishAudioApiKey: initialFishAudio = '', 
  fishAudioVoiceId: initialVoiceId = 'e5835698b6a94f6cbb74e5088210339d', 
  onSave, 
  onDismiss 
}) => {
  const [geminiKey, setGeminiKey] = useState(initialGemini) 
  const [fishKey, setFishKey] = useState(initialFishAudio) 
  const [voiceId, setVoiceId] = useState(initialVoiceId) 
  const [showGemini, setShowGemini] = useState(false) 
  const [showFish, setShowFish] = useState(false) 
  const [savedSuccess, setSavedSuccess] = useState(false) 

  const handleSave = () => {
    onSave({
      geminiApiKey: geminiKey.trim(), 
      fishAudioApiKey: fishKey.trim(), 
      fishAudioVoiceId: voiceId.trim() 
    }) 
    setSavedSuccess(true) 
    setTimeout(() => {
      setSavedSuccess(false) 
      onDismiss() 
    }, 1200) 
  } 

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/75 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-md max-h-[88vh] overflow-y-auto rounded-t-[24px] sm:rounded-[24px] bg-[#121418] border border-[#2C3240] p-6 shadow-2xl flex flex-col gap-4">
        <div className="flex items-center justify-between pb-2 border-b border-[#20242E]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-[12px] bg-[#E87A90]/15 flex items-center justify-center text-[#E87A90]">
              <Key className="w-5 h-5" /> 
            </div> 
            <div>
              <h2 className="text-base font-bold text-[#E2E8F0]">
                AI & Voice Config
              </h2> 
              <p className="text-xs text-[#94A3B8]">
                Gemini AI & Fish Audio Neural TTS
              </p> 
            </div> 
          </div> 

          <button
            onClick={onDismiss}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-[#94A3B8] hover:text-[#E2E8F0] hover:bg-white/5 active:scale-95"
          >
            <X className="w-5 h-5" /> 
          </button> 
        </div> 

        <div className="flex flex-col gap-2">
          <div className="flex items-center justify-between">
            <label className="text-xs font-bold text-[#94A3B8] tracking-wider uppercase flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-[#E87A90]" /> 
              Google Gemini API Key
            </label> 
            <a
              href="https://aistudio.google.com/app/apikey"
              target="_blank"
              rel="noreferrer"
              className="text-[11px] font-semibold text-[#E87A90] hover:underline flex items-center gap-1"
            >
              Get Free Key <ExternalLink className="w-3 h-3" /> 
            </a> 
          </div> 
          <div className="relative">
            <input
              type={showGemini ? 'text' : 'password'}
              value={geminiKey}
              onChange={e => setGeminiKey(e.target.value)}
              placeholder="AIzaSy..."
              className="w-full px-3.5 py-2.5 rounded-[12px] bg-[#1E222B] border border-[#2C3240] focus:border-[#E87A90] text-sm text-[#E2E8F0] placeholder-[#64748B] outline-none transition-all pr-10"
            /> 
            <button
              type="button"
              onClick={() => setShowGemini(!showGemini)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-[#94A3B8] hover:text-[#E2E8F0]"
            >
              {showGemini ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button> 
          </div> 
        </div> 

        <div className="flex flex-col gap-2">
          <div className="flex items-center justify-between">
            <label className="text-xs font-bold text-[#94A3B8] tracking-wider uppercase flex items-center gap-1.5">
              <Volume2 className="w-3.5 h-3.5 text-[#22D3EE]" /> 
              Fish Audio API Key (Optional)
            </label> 
            <a
              href="https://fish.audio/go-api"
              target="_blank"
              rel="noreferrer"
              className="text-[11px] font-semibold text-[#22D3EE] hover:underline flex items-center gap-1"
            >
              Get Key <ExternalLink className="w-3 h-3" /> 
            </a> 
          </div> 
          <div className="relative">
            <input
              type={showFish ? 'text' : 'password'}
              value={fishKey}
              onChange={e => setFishKey(e.target.value)}
              placeholder="fish_audio_..."
              className="w-full px-3.5 py-2.5 rounded-[12px] bg-[#1E222B] border border-[#2C3240] focus:border-[#22D3EE] text-sm text-[#E2E8F0] placeholder-[#64748B] outline-none transition-all pr-10"
            /> 
            <button
              type="button"
              onClick={() => setShowFish(!showFish)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-[#94A3B8] hover:text-[#E2E8F0]"
            >
              {showFish ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button> 
          </div> 
        </div> 

        <div className="flex flex-col gap-2">
          <label className="text-xs font-bold text-[#94A3B8] tracking-wider uppercase">
            Fish Audio Voice ID
          </label> 
          <input
            type="text"
            value={voiceId}
            onChange={e => setVoiceId(e.target.value)}
            placeholder="e5835698b6a94f6cbb74e5088210339d"
            className="w-full px-3.5 py-2.5 rounded-[12px] bg-[#1E222B] border border-[#2C3240] focus:border-[#22D3EE] text-sm text-[#E2E8F0] placeholder-[#64748B] outline-none transition-all"
          /> 
        </div> 

        {savedSuccess && (
          <div className="flex items-center gap-2 p-3 rounded-[12px] bg-[#1A3324] border border-[#14532D] text-[#86EFAC] text-xs font-bold animate-fadeIn">
            <CheckCircle2 className="w-4 h-4 text-[#86EFAC]" /> 
            Keys saved successfully!
          </div> 
        )}

        <div className="flex items-center gap-3 pt-2">
          <button
            onClick={onDismiss}
            className="flex-1 py-2.5 rounded-[12px] border border-[#2C3240] text-[#94A3B8] hover:text-[#E2E8F0] font-semibold text-sm active:scale-95 transition-all"
          >
            Cancel
          </button> 
          <button
            onClick={handleSave}
            className="flex-1 py-2.5 rounded-[12px] bg-[#E87A90] hover:bg-[#E87A90]/90 text-white font-bold text-sm active:scale-95 transition-all shadow"
          >
            Save Keys
          </button> 
        </div> 
      </div> 
    </div> 
  ) 
} 
