import type { JishoWord } from '../types/jisho' 
import { db } from './db' 

export async function searchJisho(keyword: string): Promise<JishoWord[]> {
  const query = keyword.trim() 
  if (!query) return [] 

  const results: JishoWord[] = [] 

  const matchingCards = await db.cards
    .filter(c => c.kanji.includes(query) || c.kana.includes(query) || c.english.toLowerCase().includes(query.toLowerCase())) 
    .limit(10) 
    .toArray() 

  for (const card of matchingCards) {
    results.push({
      slug: card.kanji, 
      is_common: true, 
      jlpt: ['JLPT N5-N3'], 
      japanese: [
        {
          word: card.kanji, 
          reading: card.kana 
        } 
      ], 
      senses: [
        {
          english_definitions: [card.english], 
          parts_of_speech: ['Expression', 'Noun'] 
        } 
      ] 
    }) 
  } 

  try {
    const encoded = encodeURIComponent(query) 
    const proxyUrl = `https://corsproxy.io/?url=${encodeURIComponent(`https://jisho.org/api/v1/search/words?keyword=${encoded}`)}` 
    
    const controller = new AbortController() 
    const timeout = setTimeout(() => controller.abort(), 4000) 
    const res = await fetch(proxyUrl, { signal: controller.signal }) 
    clearTimeout(timeout) 

    if (res.ok) {
      const data = await res.json() 
      if (data && Array.isArray(data.data)) {
        for (const item of data.data) {
          if (!results.some(r => r.slug === item.slug)) {
            results.push({
              slug: item.slug, 
              is_common: item.is_common, 
              jlpt: item.jlpt, 
              japanese: item.japanese || [], 
              senses: item.senses || [] 
            }) 
          } 
        } 
      } 
    } 
  } catch {
  } 

  return results 
} 
