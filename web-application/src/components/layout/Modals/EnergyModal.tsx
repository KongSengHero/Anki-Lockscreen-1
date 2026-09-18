import React from 'react' 
import { X, Zap, BatteryCharging } from 'lucide-react' 

interface EnergyModalProps {
  energyCount: number 
  onClose: () => void 
} 

export const EnergyModal: React.FC<EnergyModalProps> = ({ energyCount, onClose }) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div 
        className="fixed inset-0 bg-black/70 backdrop-blur-sm" 
        onClick={onClose} 
      /> 

      <div className="relative w-full max-w-sm bg-[#0D111A] border border-white/15 rounded-3xl p-6 shadow-2xl z-10 text-center animate-in zoom-in-95 duration-150">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-1.5 rounded-xl bg-white/5 text-slate-400 hover:text-white"
        >
          <X className="w-4 h-4" /> 
        </button> 

        <div className="w-16 h-16 rounded-3xl bg-purple-500/20 border border-purple-500/30 text-purple-400 flex items-center justify-center mx-auto mb-4">
          <Zap className="w-9 h-9 fill-current" /> 
        </div> 

        <h3 className="text-3xl font-extrabold text-white mb-1">
          {energyCount} / 100
        </h3> 
        <p className="text-xs text-slate-400 mb-6">
          Daily Study Energy
        </p> 

        <div className="w-full bg-white/5 h-3 rounded-full overflow-hidden p-0.5 border border-white/10 mb-6">
          <div
            className="h-full bg-gradient-to-r from-purple-500 to-indigo-500 rounded-full transition-all duration-500"
            style={{ width: `${energyCount}%` }}
          /> 
        </div> 

        <div className="p-4 rounded-2xl bg-purple-500/10 border border-purple-500/20 text-xs text-purple-200 text-left space-y-2 mb-5">
          <div className="flex items-center gap-2 font-semibold text-purple-300">
            <BatteryCharging className="w-4 h-4" /> 
            Recharges Daily
          </div> 
          <p className="leading-relaxed">
            Energy is consumed when generating new AI Japanese stories and syncing neural TTS voices. It automatically recharges to 100 every night.
          </p> 
        </div> 

        <button
          onClick={onClose}
          className="w-full py-3 rounded-xl bg-gradient-to-r from-purple-500 to-indigo-500 text-white font-semibold text-xs shadow-md transition-all active:scale-95"
        >
          Understood
        </button> 
      </div> 
    </div> 
  ) 
} 
