import { useState, useEffect } from 'react' 
import type { UserPreferences } from '../types/preferences' 

const STORAGE_KEY = 'blossom_user_preferences' 

const DEFAULT_PREFERENCES: UserPreferences = {
  geminiApiKey: '', 
  geminiModel: 'gemini-2.5-flash', 
  fishAudioApiKey: '', 
  fishAudioVoiceId: '5f9b45763b0143828c460b54019bf44a', 
  showFurigana: true, 
  audioSpeed: 1.0, 
  artworkDimming: 45, 
  streakCount: 7, 
  lastStreakDate: new Date().toISOString().split('T')[0], 
  storiesReadCount: 12, 
  energyCount: 85 
} 

export function usePreferences() {
  const [preferences, setPreferences] = useState<UserPreferences>(() => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY) 
      if (stored) {
        return { ...DEFAULT_PREFERENCES, ...JSON.parse(stored) } 
      } 
    } catch {
    } 
    return DEFAULT_PREFERENCES 
  }) 

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(preferences)) 
    } catch {
    } 
  }, [preferences]) 

  const updatePreference = <K extends keyof UserPreferences>(
    key: K, 
    value: UserPreferences[K] 
  ) => {
    setPreferences(prev => ({
      ...prev, 
      [key]: value 
    })) 
  } 

  return {
    preferences, 
    updatePreference 
  } 
} 
