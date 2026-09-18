export interface JishoJapanese {
  word?: string 
  reading?: string 
} 

export interface JishoSense {
  english_definitions: string[] 
  parts_of_speech: string[] 
  tags?: string[] 
  see_also?: string[] 
} 

export interface JishoWord {
  slug: string 
  is_common?: boolean 
  jlpt?: string[] 
  tags?: string[] 
  japanese: JishoJapanese[] 
  senses: JishoSense[] 
} 

export interface SearchHistoryItem {
  id?: number 
  query: string 
  timestamp: number 
} 
