import React from 'react' 

export interface ApplicationSceneryBackgroundProps {
  theme?: string 
  imageSrc?: string 
  blurRadius?: number 
  dimOpacity?: number 
  artworkOpacity?: number 
} 

const THEME_BG_COLORS: Record<string, string> = {
  midnight: '#121418', 
  matcha: '#101C16', 
  sakura: '#1C1217', 
  slate: '#141724' 
} 

export const ApplicationSceneryBackground: React.FC<ApplicationSceneryBackgroundProps> = ({
  theme = 'midnight', 
  imageSrc = '/images/anki_lock.png', 
  blurRadius = 16, 
  dimOpacity = 0.15, 
  artworkOpacity = 0.65 
}) => {
  const baseBg = THEME_BG_COLORS[theme] || '#121418' 

  return (
    <div
      className="fixed inset-0 pointer-events-none z-0 overflow-hidden select-none transition-colors duration-500"
      style={{
        backgroundColor: baseBg 
      }}
    >
      <div className="absolute top-0 left-0 right-0 h-[520px] overflow-hidden">
        <img
          src={imageSrc}
          alt=""
          className="w-full h-full object-cover object-center transition-all duration-700"
          style={{
            opacity: artworkOpacity, 
            filter: `blur(${blurRadius}px) saturate(1.15)`, 
            transform: 'scale(1.08)' 
          }}
        /> 

        {dimOpacity > 0 && (
          <div
            className="absolute inset-0 transition-opacity duration-500"
            style={{
              backgroundColor: `rgba(0, 0, 0, ${dimOpacity})` 
            }}
          /> 
        )}

        <div
          className="absolute inset-0 transition-all duration-500"
          style={{
            background: `linear-gradient(to bottom, 
              rgba(0, 0, 0, 0.75) 0%, 
              rgba(0, 0, 0, 0.35) 18%, 
              rgba(0, 0, 0, 0.0) 32%, 
              ${baseBg}66 55%, 
              ${baseBg}D9 80%, 
              ${baseBg} 100%)` 
          }}
        /> 
      </div> 
    </div> 
  ) 
} 
