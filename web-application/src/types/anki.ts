export interface AnkiCard {
  id: number 
  deckId: number 
  kanji: string 
  kana: string 
  english: string 
  sentenceJp: string 
  sentenceRubyJp: string 
  sentenceEn: string 
  cardType: number 
  interval: number 
  easeFactor: number 
  repetitions: number 
  dueDate: number 
  lastReviewed?: number 
} 

export interface AnkiDeck {
  id: number 
  name: string 
  cardCount: number 
  dueCount: number 
  description?: string 
  createdAt: number 
} 

export type SrsGrade = 'again' | 'hard' | 'good' | 'easy' 

export interface SrsReviewResult {
  interval: number 
  easeFactor: number 
  repetitions: number 
  dueDate: number 
} 
