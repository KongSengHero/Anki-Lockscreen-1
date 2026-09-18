import React, { useState, useEffect } from 'react' 

export interface RotatingStatusTextProps {
  phrases: string[] 
  isGenerating: boolean 
  intervalMs?: number 
  className?: string 
} 

export const RotatingStatusText: React.FC<RotatingStatusTextProps> = ({
  phrases, 
  isGenerating, 
  intervalMs = 1200, 
  className = '' 
}) => {
  const [currentIndex, setCurrentIndex] = useState(0) 
  const [isFading, setIsFading] = useState(false) 

  useEffect(() => {
    if (!isGenerating || phrases.length === 0) {
      setCurrentIndex(0) 
      return 
    } 

    const interval = setInterval(() => {
      setIsFading(true) 
      setTimeout(() => {
        setCurrentIndex(prev => (prev + 1) % phrases.length) 
        setIsFading(false) 
      }, 150) 
    }, intervalMs) 

    return () => clearInterval(interval) 
  }, [isGenerating, phrases, intervalMs]) 

  if (phrases.length === 0) return null 

  return (
    <span
      className={`inline-block transition-all duration-150 transform ${
        isFading ? '-translate-y-1 opacity-0' : 'translate-y-0 opacity-100' 
      } ${className}`}
    >
      {phrases[currentIndex]} 
    </span> 
  ) 
} 
