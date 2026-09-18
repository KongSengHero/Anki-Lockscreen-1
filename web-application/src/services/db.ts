import Dexie, { type EntityTable } from 'dexie' 
import type { AnkiCard, AnkiDeck } from '../types/anki' 
import type { GeneratedStory } from '../types/story' 
import type { SearchHistoryItem } from '../types/jisho' 

export class BlossomDatabase extends Dexie {
  decks!: EntityTable<AnkiDeck, 'id'> 
  cards!: EntityTable<AnkiCard, 'id'> 
  stories!: EntityTable<GeneratedStory, 'id'> 
  searchHistory!: EntityTable<SearchHistoryItem, 'id'> 

  constructor() {
    super('BlossomDB') 
    this.version(1).stores({
      decks: '++id, name, cardCount, dueCount, createdAt', 
      cards: '++id, deckId, kanji, kana, interval, dueDate, lastReviewed', 
      stories: '++id, title, jlptLevel, createdAt', 
      searchHistory: '++id, query, timestamp' 
    }) 
  } 
} 

export const db = new BlossomDatabase() 

export async function getDecks(): Promise<AnkiDeck[]> {
  return await db.decks.toArray() 
} 

export async function getDeckCards(deckId: number): Promise<AnkiCard[]> {
  return await db.cards.where('deckId').equals(deckId).toArray() 
} 

export async function getDueCards(deckId?: number): Promise<AnkiCard[]> {
  const now = Date.now() 
  if (deckId !== undefined) {
    return await db.cards
      .where('deckId')
      .equals(deckId)
      .and(card => card.dueDate <= now)
      .toArray() 
  } 
  return await db.cards.filter(card => card.dueDate <= now).toArray() 
} 

export async function updateCardReview(
  cardId: number, 
  interval: number, 
  easeFactor: number, 
  repetitions: number, 
  dueDate: number 
): Promise<void> {
  const now = Date.now() 
  await db.cards.update(cardId, {
    interval, 
    easeFactor, 
    repetitions, 
    dueDate, 
    lastReviewed: now 
  }) 
  
  const card = await db.cards.get(cardId) 
  if (card) {
    const dueInDeck = await db.cards
      .where('deckId')
      .equals(card.deckId)
      .and(c => c.dueDate <= now)
      .count() 
    await db.decks.update(card.deckId, {
      dueCount: dueInDeck 
    }) 
  } 
} 

export async function getStories(): Promise<GeneratedStory[]> {
  return await db.stories.reverse().toArray() 
} 

export async function saveStory(story: Omit<GeneratedStory, 'id'>): Promise<number> {
  return (await db.stories.add(story as GeneratedStory)) as number 
} 

export async function deleteStory(id: number): Promise<void> {
  await db.stories.delete(id) 
} 

export async function getSearchHistory(): Promise<SearchHistoryItem[]> {
  return await db.searchHistory.orderBy('timestamp').reverse().limit(20).toArray() 
} 

export async function addSearchHistory(query: string): Promise<void> {
  const trimmed = query.trim() 
  if (!trimmed) return 
  const existing = await db.searchHistory.where('query').equals(trimmed).first() 
  if (existing && existing.id) {
    await db.searchHistory.update(existing.id, { timestamp: Date.now() }) 
  } else {
    await db.searchHistory.add({ query: trimmed, timestamp: Date.now() }) 
  } 
} 

export async function removeSearchHistory(query: string): Promise<void> {
  await db.searchHistory.where('query').equals(query.trim()).delete() 
} 

export async function clearSearchHistory(): Promise<void> {
  await db.searchHistory.clear() 
} 

export async function seedInitialData(): Promise<void> {
  const deckCount = await db.decks.count() 
  if (deckCount > 0) return 

  const deckId = (await db.decks.add({
    name: 'Core N5 & N4 Essentials', 
    cardCount: 6, 
    dueCount: 6, 
    description: 'Essential everyday vocabulary and lockscreen reading cards', 
    createdAt: Date.now() 
  })) as number 

  const now = Date.now() 
  const initialCards: Omit<AnkiCard, 'id'>[] = [
    {
      deckId, 
      kanji: '約束', 
      kana: 'やくそく', 
      english: 'Promise, agreement, arrangement', 
      sentenceJp: 'どんなことがあっても約束を守る。', 
      sentenceRubyJp: '<ruby>約束<rt>やくそく</rt></ruby>を<ruby>守<rt>まも</rt></ruby>る。', 
      sentenceEn: 'I keep my promises no matter what.', 
      cardType: 0, 
      interval: 0, 
      easeFactor: 2500, 
      repetitions: 0, 
      dueDate: now 
    }, 
    {
      deckId, 
      kanji: '記憶', 
      kana: 'きおく', 
      english: 'Memory, recollection, remembrance', 
      sentenceJp: '彼女の記憶力は驚くほど鋭い。', 
      sentenceRubyJp: '<ruby>彼女<rt>かのじょ</rt></ruby>の<ruby>記憶力<rt>きおくりょく</rt></ruby>は<ruby>鋭<rt>するど</rt></ruby>い。', 
      sentenceEn: 'Her memory is surprisingly sharp.', 
      cardType: 0, 
      interval: 0, 
      easeFactor: 2500, 
      repetitions: 0, 
      dueDate: now 
    }, 
    {
      deckId, 
      kanji: '挑戦', 
      kana: 'ちょうせん', 
      english: 'Challenge, defiance, dare', 
      sentenceJp: '未知の世界へ挑戦し続ける。', 
      sentenceRubyJp: '<ruby>未知<rt>みち</rt></ruby>の<ruby>世界<rt>せかい</rt></ruby>へ<ruby>挑戦<rt>ちょうせん</rt></ruby>し<ruby>続<rt>つづ</rt></ruby>ける。', 
      sentenceEn: 'Keep challenging the unknown world.', 
      cardType: 0, 
      interval: 0, 
      easeFactor: 2500, 
      repetitions: 0, 
      dueDate: now 
    }, 
    {
      deckId, 
      kanji: '桜', 
      kana: 'さくら', 
      english: 'Cherry blossom, cherry tree', 
      sentenceJp: '春になると桜の花が満開になる。', 
      sentenceRubyJp: '<ruby>春<rt>はる</rt></ruby>になると<ruby>桜<rt>さくら</rt></ruby>の<ruby>花<rt>はな</rt></ruby>が<ruby>満開<rt>まんかい</rt></ruby>になる。', 
      sentenceEn: 'In spring, the cherry blossoms come into full bloom.', 
      cardType: 0, 
      interval: 0, 
      easeFactor: 2500, 
      repetitions: 0, 
      dueDate: now 
    }, 
    {
      deckId, 
      kanji: '希望', 
      kana: 'きぼう', 
      english: 'Hope, wish, aspiration', 
      sentenceJp: 'どんな暗闇でも希望の光を見失わない。', 
      sentenceRubyJp: 'どんな<ruby>暗闇<rt>くらやみ</rt></ruby>でも<ruby>希望<rt>きぼう</rt></ruby>の<ruby>光<rt>ひかり</rt></ruby>を<ruby>見失<rt>みうしな</rt></ruby>わない。', 
      sentenceEn: 'Never lose sight of the light of hope in any darkness.', 
      cardType: 0, 
      interval: 0, 
      easeFactor: 2500, 
      repetitions: 0, 
      dueDate: now 
    }, 
    {
      deckId, 
      kanji: '未来', 
      kana: 'みらい', 
      english: 'Future (usually distant), future tense', 
      sentenceJp: '明るい未来のために今できる全力を尽くす。', 
      sentenceRubyJp: '<ruby>明<rt>あか</rt></ruby>るい<ruby>未来<rt>みらい</rt></ruby>のために<ruby>今<rt>いま</rt></ruby>できる<ruby>全力<rt>ぜんりょく</rt></ruby>を<ruby>尽<rt>つ</rt></ruby>くす。', 
      sentenceEn: 'Do everything possible now for a bright future.', 
      cardType: 0, 
      interval: 0, 
      easeFactor: 2500, 
      repetitions: 0, 
      dueDate: now 
    } 
  ] 

  for (const card of initialCards) {
    await db.cards.add(card as AnkiCard) 
  } 

  const initialStory: Omit<GeneratedStory, 'id'> = {
    title: '桜の木の下の約束', 
    titleReading: 'さくらのきのしたのやくそく', 
    content: '春の暖かい午後、ハルトとユキは古い桜の木の下で会いました。満開の桜の花びらが静かに風に舞っています。\n\n「ユキ、覚えている？十年前の今日、僕たちはここで小さな約束をしたんだ。」ハルトは笑顔で言いました。\n\nユキは懐かしい記憶を思い出しました。「ええ、もちろん覚えているわ。将来どんなに遠く離れても、この桜の木の下でまた会おうって。」\n\n二人は新しい未来へ向けて、さらなる挑戦を始める勇気を感じていました。', 
    translation: 'On a warm spring afternoon, Haruto and Yuki met under the old cherry blossom tree. The full-bloom cherry blossom petals fluttered quietly in the gentle wind.\n\n"Yuki, do you remember? Ten years ago today, we made a small promise right here," Haruto said with a smile.\n\nYuki recalled the fond memory. "Yes, of course I remember. We said no matter how far apart we were in the future, we would meet again under this cherry blossom tree."\n\nFacing a new future, both felt the courage to embark on even greater challenges.', 
    jlptLevel: 'N4', 
    targetWords: ['約束', '記憶', '挑戦', '桜', '未来'], 
    coverUrl: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=800&auto=format&fit=crop&q=80', 
    createdAt: Date.now(), 
    questions: [
      {
        question: 'ハルトとユキはどこで会いましたか？', 
        options: ['古い桜の木の下', '海辺のカフェ', '駅の改札口', '学校の図書館'], 
        correctIndex: 0, 
        explanation: '物語の冒頭で「古い桜の木の下で会いました」と書かれています。' 
      }, 
      {
        question: '十年前、二人はどんな約束をしましたか？', 
        options: ['一緒に勉強すること', '遠く離れてもこの桜の木の下で再会すること', '手紙を毎週書くこと', '世界一周旅行をすること'], 
        correctIndex: 1, 
        explanation: 'ユキの言葉に「将来どんなに遠く離れても、この桜の木の下でまた会おうって」とあります。' 
      }, 
      {
        question: '二人は何を感じていましたか？', 
        options: ['深い悲しみ', '新しい未来へ挑戦する勇気', '別れの悔しさ', '過去への後悔'], 
        correctIndex: 1, 
        explanation: '「新しい未来へ向けて、さらなる挑戦を始める勇気を感じていました」とあります。' 
      } 
    ] 
  } 

  await db.stories.add(initialStory as GeneratedStory) 
} 
