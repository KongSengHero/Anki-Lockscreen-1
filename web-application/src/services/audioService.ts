export interface AudioController {
  play: () => void 
  pause: () => void 
  skipNext: () => void 
  skipPrevious: () => void 
  setSpeed: (speed: number) => void 
  stop: () => void 
} 

export function extractSentences(text: string): string[] {
  const rawSentences = text.split(/([。！？\n]+)/) 
  const result: string[] = [] 
  
  for (let i = 0; i < rawSentences.length; i += 2) {
    const main = rawSentences[i] 
    const punct = rawSentences[i + 1] || '' 
    const full = (main + punct).trim() 
    if (full.length > 0) {
      result.push(full) 
    } 
  } 

  return result.length > 0 ? result : [text] 
} 

export class AudioNarrationService {
  private sentences: string[] = [] 
  private currentIndex: number = 0 
  private isPlaying: boolean = false 
  private speed: number = 1.0 
  private apiKey: string = '' 
  private voiceId: string = '' 
  private audioEl: HTMLAudioElement | null = null 
  private onSentenceChange?: (index: number) => void 
  private onStateChange?: (playing: boolean) => void 

  constructor(
    text: string, 
    apiKey: string = '', 
    voiceId: string = '', 
    speed: number = 1.0, 
    onSentenceChange?: (index: number) => void, 
    onStateChange?: (playing: boolean) => void 
  ) {
    this.sentences = extractSentences(text) 
    this.apiKey = apiKey 
    this.voiceId = voiceId 
    this.speed = speed 
    this.onSentenceChange = onSentenceChange 
    this.onStateChange = onStateChange 
  } 

  public updateText(text: string) {
    this.stop() 
    this.sentences = extractSentences(text) 
    this.currentIndex = 0 
  } 

  public setSpeed(speed: number) {
    this.speed = speed 
    if (this.audioEl) {
      this.audioEl.playbackRate = speed 
    } 
  } 

  public play() {
    if (this.sentences.length === 0) return 
    this.isPlaying = true 
    if (this.onStateChange) this.onStateChange(true) 
    this.playCurrentSentence() 
  } 

  public pause() {
    this.isPlaying = false 
    if (this.onStateChange) this.onStateChange(false) 

    if (this.audioEl) {
      this.audioEl.pause() 
    } 
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel() 
    } 
  } 

  public stop() {
    this.pause() 
    this.currentIndex = 0 
    if (this.onSentenceChange) this.onSentenceChange(0) 
  } 

  public skipNext() {
    if (this.currentIndex < this.sentences.length - 1) {
      this.currentIndex += 1 
      if (this.onSentenceChange) this.onSentenceChange(this.currentIndex) 
      if (this.isPlaying) {
        this.playCurrentSentence() 
      } 
    } 
  } 

  public skipPrevious() {
    if (this.currentIndex > 0) {
      this.currentIndex -= 1 
      if (this.onSentenceChange) this.onSentenceChange(this.currentIndex) 
      if (this.isPlaying) {
        this.playCurrentSentence() 
      } 
    } 
  } 

  private async playCurrentSentence() {
    if (!this.isPlaying) return 
    if (this.currentIndex >= this.sentences.length) {
      this.stop() 
      return 
    } 

    if (this.onSentenceChange) {
      this.onSentenceChange(this.currentIndex) 
    } 

    const currentText = this.sentences[this.currentIndex] 

    if (this.apiKey && this.apiKey.trim() !== '') {
      try {
        await this.playFishAudio(currentText) 
        return 
      } catch {
      } 
    } 

    this.playWebSpeech(currentText) 
  } 

  private playWebSpeech(text: string) {
    if (!('speechSynthesis' in window)) return 

    window.speechSynthesis.cancel() 
    const utterance = new SpeechSynthesisUtterance(text) 
    utterance.lang = 'ja-JP' 
    utterance.rate = this.speed 

    utterance.onend = () => {
      if (this.isPlaying) {
        this.currentIndex += 1 
        this.playCurrentSentence() 
      } 
    } 

    utterance.onerror = () => {
      if (this.isPlaying) {
        this.currentIndex += 1 
        this.playCurrentSentence() 
      } 
    } 

    window.speechSynthesis.speak(utterance) 
  } 

  private async playFishAudio(text: string) {
    const res = await fetch('https://api.fish.audio/v1/tts', {
      method: 'POST', 
      headers: {
        'Authorization': `Bearer ${this.apiKey.trim()}`, 
        'Content-Type': 'application/json' 
      }, 
      body: JSON.stringify({
        text, 
        reference_id: this.voiceId || '5f9b45763b0143828c460b54019bf44a', 
        format: 'mp3', 
        latency: 'normal' 
      }) 
    }) 

    if (!res.ok) {
      throw new Error(`Fish Audio synthesis failed: ${res.status}`) 
    } 

    const blob = await res.blob() 
    const audioUrl = URL.createObjectURL(blob) 

    if (this.audioEl) {
      this.audioEl.pause() 
    } 

    this.audioEl = new Audio(audioUrl) 
    this.audioEl.playbackRate = this.speed 

    this.audioEl.onended = () => {
      URL.revokeObjectURL(audioUrl) 
      if (this.isPlaying) {
        this.currentIndex += 1 
        this.playCurrentSentence() 
      } 
    } 

    this.audioEl.onerror = () => {
      URL.revokeObjectURL(audioUrl) 
      this.playWebSpeech(text) 
    } 

    await this.audioEl.play() 
  } 
} 
