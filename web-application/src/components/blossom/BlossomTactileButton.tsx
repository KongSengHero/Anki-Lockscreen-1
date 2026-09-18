import React, { useState } from 'react' 

export interface BlossomTactileButtonProps {
  onClick?: () => void 
  disabled?: boolean 
  variant?: 'primary' | 'success' | 'danger' | 'secondary' | 'cyan' | 'matcha' 
  faceColor?: string 
  lipColor?: string 
  textColor?: string 
  lipHeight?: number 
  className?: string 
  children: React.ReactNode 
} 

const VARIANT_MAP = {
  primary: {
    face: '#7C8CF8', 
    lip: '#4E5BA6', 
    text: '#FFFFFF' 
  }, 
  success: {
    face: '#5FA77C', 
    lip: '#3E7755', 
    text: '#FFFFFF' 
  }, 
  danger: {
    face: '#E87A90', 
    lip: '#A8475B', 
    text: '#FFFFFF' 
  }, 
  secondary: {
    face: '#252A35', 
    lip: '#181C24', 
    text: '#E8EAF0' 
  }, 
  cyan: {
    face: '#22D3EE', 
    lip: '#0891B2', 
    text: '#0C1B23' 
  }, 
  matcha: {
    face: '#5FA77C', 
    lip: '#3E7755', 
    text: '#FFFFFF' 
  } 
} 

export const BlossomTactileButton: React.FC<BlossomTactileButtonProps> = ({
  onClick, 
  disabled = false, 
  variant = 'primary', 
  faceColor, 
  lipColor, 
  textColor, 
  lipHeight = 3, 
  className = '', 
  children 
}) => {
  const [isPressed, setIsPressed] = useState(false) 
  const palette = VARIANT_MAP[variant] || VARIANT_MAP.primary 
  const activeFace = faceColor || palette.face 
  const activeLip = lipColor || palette.lip 
  const activeText = textColor || palette.text 

  const handlePointerDown = () => {
    if (!disabled) {
      setIsPressed(true) 
    } 
  } 

  const handlePointerUp = () => {
    setIsPressed(false) 
  } 

  return (
    <div
      className={`relative select-none inline-flex cursor-pointer transition-opacity ${
        disabled ? 'opacity-45 pointer-events-none' : '' 
      } ${className}`}
      style={{
        paddingBottom: `${lipHeight}px` 
      }}
      onPointerDown={handlePointerDown}
      onPointerUp={handlePointerUp}
      onPointerLeave={handlePointerUp}
      onClick={disabled ? undefined : onClick}
    >
      <div
        className="absolute inset-x-0 bottom-0 top-[2px] rounded-[16px] pointer-events-none transition-all"
        style={{
          backgroundColor: activeLip 
        }}
      /> 
      <div
        className="relative z-10 w-full rounded-[16px] px-4 py-2.5 flex items-center justify-center font-bold text-sm tracking-tight transition-transform duration-75 active:scale-[0.99]"
        style={{
          backgroundColor: activeFace, 
          color: activeText, 
          transform: isPressed ? `translateY(${lipHeight}px)` : 'translateY(0px)', 
          boxShadow: isPressed 
            ? 'none' 
            : 'inset 0 1px 1px rgba(255, 255, 255, 0.28)' 
        }}
      >
        {children} 
      </div> 
    </div> 
  ) 
} 
