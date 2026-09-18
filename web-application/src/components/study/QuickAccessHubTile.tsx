import React from 'react' 
import { ChevronRight, type LucideIcon } from 'lucide-react' 

interface QuickAccessHubTileProps {
  icon: LucideIcon 
  title: string 
  subtitle: string 
  accentColor: string 
  containerColor: string 
  onClick: () => void 
} 

export const QuickAccessHubTile: React.FC<QuickAccessHubTileProps> = ({
  icon: Icon, 
  title, 
  subtitle, 
  accentColor, 
  containerColor, 
  onClick 
}) => {
  return (
    <button
      onClick={onClick}
      className="flex-1 text-left p-3.5 rounded-[18px] bg-[#1E222B] border border-[#20242E] hover:border-[#2C3240] active:scale-95 transition-all shadow-md group"
    >
      <div className="flex items-center justify-between mb-2.5">
        <div
          className={`w-9 h-9 rounded-[10px] ${containerColor} flex items-center justify-center transition-transform group-hover:scale-105`}
        >
          <Icon className={`w-[18px] h-[18px] ${accentColor}`} /> 
        </div> 

        <ChevronRight className="w-4 h-4 text-[#64748B] group-hover:text-[#E2E8F0] transition-colors" /> 
      </div> 

      <h4 className="text-sm font-semibold text-[#E2E8F0] tracking-tight">
        {title} 
      </h4> 
      <p className="text-xs text-[#94A3B8] mt-0.5">
        {subtitle} 
      </p> 
    </button> 
  ) 
} 
