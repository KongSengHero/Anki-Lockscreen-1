import JSZip from 'jszip' 
import initSqlJs from 'sql.js' 
import type { AnkiCard, AnkiDeck } from '../types/anki' 
import { db } from './db' 

function cleanHtml(html: string): string {
  if (!html) return '' 
  const doc = new DOMParser().parseFromString(html, 'text/html') 
  return (doc.body.textContent || '').trim() 
} 

function cleanCloze(text: string): string {
  if (!text) return '' 
  return text.replace(/\{\{c\d+::([^:}]+)(?:::([^}]+))?\}\}/g, '$1') 
} 

export async function parseAnkiDeck(file: File): Promise<{ deckName: string; cardCount: number; deckId: number }> {
  const zip = new JSZip() 
  const unzipped = await zip.loadAsync(file) 
  
  let ankiDbFile = unzipped.file('collection.anki21') || unzipped.file('collection.anki2') 
  if (!ankiDbFile) {
    const files = Object.keys(unzipped.files) 
    const fallback = files.find(f => f.endsWith('.anki2') || f.endsWith('.anki21')) 
    if (fallback) {
      ankiDbFile = unzipped.file(fallback) 
    } 
  } 

  if (!ankiDbFile) {
    throw new Error('Valid Anki database not found in archive') 
  } 

  const dbBytes = await ankiDbFile.async('uint8array') 
  
  const SQL = await initSqlJs({
    locateFile: () => '/sql-wasm.wasm' 
  }) 

  const sqliteDb = new SQL.Database(dbBytes) 

  let deckName = file.name.replace(/\.(apkg|colpkg)$/i, '') 
  try {
    const colRes = sqliteDb.exec('SELECT decks FROM col LIMIT 1') 
    if (colRes.length > 0 && colRes[0].values.length > 0) {
      const decksJson = colRes[0].values[0][0] as string 
      const parsedDecks = JSON.parse(decksJson) 
      const deckKeys = Object.keys(parsedDecks) 
      const validKey = deckKeys.find(k => k !== '1' && parsedDecks[k]?.name) 
      if (validKey) {
        deckName = parsedDecks[validKey].name 
      } 
    } 
  } catch {
  } 

  const notesQuery = `
    SELECT c.id, c.did, n.flds, c.ivl, c.factor, c.reps, c.due 
    FROM cards c 
    JOIN notes n ON c.nid = n.id
  ` 

  const queryRes = sqliteDb.exec(notesQuery) 
  if (!queryRes.length || !queryRes[0].values.length) {
    throw new Error('No flashcards found in the selected deck') 
  } 

  const existingDeck = await db.decks.where('name').equals(deckName).first() 
  let createdDeckId: number 
  if (existingDeck && existingDeck.id) {
    createdDeckId = existingDeck.id 
  } else {
    const newDeck: Omit<AnkiDeck, 'id'> = {
      name: deckName, 
      cardCount: queryRes[0].values.length, 
      dueCount: queryRes[0].values.length, 
      createdAt: Date.now() 
    } 
    createdDeckId = (await db.decks.add(newDeck as AnkiDeck)) as number 
  } 

  const now = Date.now() 
  const cardsToInsert: Omit<AnkiCard, 'id'>[] = [] 

  for (const row of queryRes[0].values) {
    const rawFlds = String(row[2] || '') 
    const fieldParts = rawFlds.split('\x1f').map(p => cleanCloze(cleanHtml(p))) 

    const kanji = fieldParts[0] || '単語' 
    const kana = fieldParts[1] || kanji 
    const english = fieldParts[2] || fieldParts[1] || 'No definition' 
    const sentenceJp = fieldParts[3] || `${kanji}を勉強しています。` 
    const sentenceEn = fieldParts[4] || `Studying ${kanji}.` 
    const interval = Number(row[3]) || 0 
    const easeFactor = Number(row[4]) || 2500 
    const reps = Number(row[5]) || 0 
    const dueRaw = Number(row[6]) || 0 
    const dueDate = dueRaw > 1000000000000 ? dueRaw : now + interval * 86400000 

    cardsToInsert.push({
      deckId: createdDeckId, 
      kanji, 
      kana, 
      english, 
      sentenceJp, 
      sentenceRubyJp: sentenceJp, 
      sentenceEn, 
      cardType: 0, 
      interval, 
      easeFactor, 
      repetitions: reps, 
      dueDate 
    }) 
  } 

  await db.cards.bulkAdd(cardsToInsert as AnkiCard[]) 
  
  const totalInDeck = await db.cards.where('deckId').equals(createdDeckId).count() 
  const dueInDeck = await db.cards
    .where('deckId')
    .equals(createdDeckId)
    .and(c => c.dueDate <= now)
    .count() 

  await db.decks.update(createdDeckId, {
    cardCount: totalInDeck, 
    dueCount: dueInDeck 
  }) 

  sqliteDb.close() 

  return {
    deckName, 
    cardCount: cardsToInsert.length, 
    deckId: createdDeckId 
  } 
} 

export const parseAnkiApkg = parseAnkiDeck 
