export interface RubySegment {
  text: string 
  ruby?: string 
} 

export interface StoryWordItem {
  kanji: string 
  reading: string 
  meaning: string 
  surface?: string 
  furigana?: string 
  romaji?: string 
  pos?: string 
} 

export interface StoryToken {
  surface: string 
  segments?: RubySegment[] 
  reading?: string 
  furigana?: string 
  isKanji: boolean 
  isName?: boolean 
  isPunctuation?: boolean 
  isTarget?: boolean 
  meaning?: string 
  jlpt?: string 
} 

export interface QuizQuestion {
  id?: number 
  questionText?: string 
  question: string 
  options: string[] 
  correctOptionIndex?: number 
  correctIndex: number 
  explanation: string 
} 

export interface GeneratedStory {
  id: number 
  title: string 
  titleReading?: string 
  content: string 
  furiganaContent?: string 
  translation: string 
  jlptLevel: string 
  targetWords: string[] 
  targetWordsData?: StoryWordItem[] 
  questions: QuizQuestion[] 
  theme?: string 
  topic?: string 
  isPinned?: boolean 
  quizScore?: number 
  isPassed?: boolean 
  coverUrl?: string 
  createdAt: number 
} 
