import type { AnkiCard, SrsGrade, SrsReviewResult } from '../types/anki' 

export function calculateSrsReview(card: AnkiCard, grade: SrsGrade): SrsReviewResult {
  let interval = card.interval || 0 
  let easeFactor = card.easeFactor || 2500 
  let repetitions = card.repetitions || 0 

  switch (grade) {
    case 'again':
      repetitions = 0 
      interval = 1 
      easeFactor = Math.max(1300, easeFactor - 200) 
      break 

    case 'hard':
      repetitions += 1 
      interval = Math.max(1, Math.round(Math.max(1, interval) * 1.2)) 
      easeFactor = Math.max(1300, easeFactor - 150) 
      break 

    case 'good':
      repetitions += 1 
      if (repetitions === 1) {
        interval = 1 
      } else if (repetitions === 2) {
        interval = 6 
      } else {
        interval = Math.max(1, Math.round(interval * (easeFactor / 1000))) 
      } 
      break 

    case 'easy':
      repetitions += 1 
      if (repetitions === 1) {
        interval = 4 
      } else {
        interval = Math.max(1, Math.round(interval * (easeFactor / 1000) * 1.3)) 
      } 
      easeFactor += 150 
      break 
  } 

  const dueDate = Date.now() + interval * 86400000 

  return {
    interval, 
    easeFactor, 
    repetitions, 
    dueDate 
  } 
} 

export async function reviewCard(cardId: number, grade: SrsGrade): Promise<void> {
  const { db, updateCardReview } = await import('./db') 
  const card = await db.cards.get(cardId) 
  if (!card) return 
  const res = calculateSrsReview(card, grade) 
  await updateCardReview(cardId, res.interval, res.easeFactor, res.repetitions, res.dueDate) 
} 
