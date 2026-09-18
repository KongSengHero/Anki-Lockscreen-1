import React, { useState } from 'react' 

export interface Squircle3DButtonProps {
  onClick?: () => void 
  disabled?: boolean 
  containerColor?: string 
  bevelColor?: string 
  contentColor?: string 
  depth?: number 
  shapeRadius?: number 
  hasSweepingShine?: boolean 
  height?: number 
  className?: string 
  children: React.ReactNode 
} 

export const Squircle3DButton: React.FC<Squircle3DButtonProps> = ({
  onClick, 
  disabled = false, 
  containerColor = '#E87A90', 
  bevelColor = '#A8475B', 
  contentColor = '#FFFFFF', 
  depth = 3, 
  shapeRadius = 16, 
  hasSweepingShine = false, 
  height = 50, 
  className = '', 
  children 
}) => {
  const [isPressed, setIsPressed] = useState(false) 

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
      className={`relative select-none inline-flex cursor-pointer transition-opacity overflow-hidden ${
        disabled ? 'opacity-40 pointer-events-none' : '' 
      } ${className}`}
      style={{
        height: `${height}px`, 
        borderRadius: `${shapeRadius}px` 
      }}
      onPointerDown={handlePointerDown}
      onPointerUp={handlePointerUp}
      onPointerLeave={handlePointerUp}
      onClick={disabled ? undefined : onClick}
    >
      <div
        className="absolute inset-x-0 bottom-0 pointer-events-none"
        style={{
          top: `${depth}px`, 
          backgroundColor: bevelColor, 
          borderRadius: `${shapeRadius}px` 
        }}
      /> 

      <div
        className="relative z-10 w-full h-full flex items-center justify-center font-semibold text-[15px] tracking-tight transition-transform duration-75 overflow-hidden"
        style={{
          backgroundColor: containerColor, 
          color: contentColor, 
          borderRadius: `${shapeRadius}px`, 
          height: `calc(100% - ${depth}px)`, 
          transform: isPressed ? `translateY(${depth}px)` : 'translateY(0px)', 
          border: '1px solid rgba(255, 255, 255, 0.22)', 
          boxShadow: isPressed ? 'none' : 'inset 0 1px 1px rgba(255, 255, 255, 0.28)' 
        }}
      >
        {hasSweepingShine && !disabled && (
          <div
            className="absolute inset-0 pointer-events-none animate-sweeping-shine"
            style={{
              background: 'linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.22) 50%, transparent 100%)', 
              width: '50%' 
            }}
          /> 
        )}

        <div className="relative z-20 flex items-center justify-center w-full px-4">
          {children} 
        </div> 
      </div> 
    </div> 
  ) 
} 
