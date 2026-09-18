import type { StoryToken, RubySegment, StoryWordItem } from '../types/story' 

const COMMON_DICTIONARY: Record<string, { reading: string; meaning: string; jlpt: string }> = {
  '春': { reading: 'はる', meaning: 'Spring season', jlpt: 'N5' }, 
  '暖かい': { reading: 'あたたかい', meaning: 'Warm (weather/temperature)', jlpt: 'N5' }, 
  '午後': { reading: 'ごご', meaning: 'Afternoon, p.m.', jlpt: 'N5' }, 
  '古い': { reading: 'ふるい', meaning: 'Old (not person), aged', jlpt: 'N5' }, 
  '桜': { reading: 'さくら', meaning: 'Cherry blossom, sakura', jlpt: 'N3' }, 
  '木': { reading: 'き', meaning: 'Tree, wood', jlpt: 'N5' }, 
  '下': { reading: 'した', meaning: 'Under, below, beneath', jlpt: 'N5' }, 
  '会いました': { reading: 'あいました', meaning: 'Met (past polite of 会う)', jlpt: 'N5' }, 
  '会う': { reading: 'あう', meaning: 'To meet / see (someone)', jlpt: 'N5' }, 
  '満開': { reading: 'まんかい', meaning: 'Full bloom', jlpt: 'N2' }, 
  '花びら': { reading: 'はなびら', meaning: 'Flower petal', jlpt: 'N2' }, 
  '静か': { reading: 'しずか', meaning: 'Quiet, calm, peaceful', jlpt: 'N5' }, 
  '風': { reading: 'かぜ', meaning: 'Wind, breeze', jlpt: 'N5' }, 
  '舞って': { reading: 'まって', meaning: 'Dancing, fluttering (舞う)', jlpt: 'N2' }, 
  '十年': { reading: 'じゅうねん', meaning: 'Ten years', jlpt: 'N5' }, 
  '今日': { reading: 'きょう', meaning: 'Today', jlpt: 'N5' }, 
  '僕たち': { reading: 'ぼくたち', meaning: 'We (casual masculine)', jlpt: 'N5' }, 
  '約束': { reading: 'やくそく', meaning: 'Promise, arrangement', jlpt: 'N4' }, 
  '笑顔': { reading: 'えがお', meaning: 'Smiling face, smile', jlpt: 'N3' }, 
  '言いました': { reading: 'いいました', meaning: 'Said (past polite of 言う)', jlpt: 'N5' }, 
  '懐かしい': { reading: 'なつかしい', meaning: 'Dear, nostalgic, missed', jlpt: 'N3' }, 
  '記憶': { reading: 'きおく', meaning: 'Memory, recollection', jlpt: 'N3' }, 
  '思い出しました': { reading: 'おもいだしました', meaning: 'Recalled, remembered', jlpt: 'N4' }, 
  '将来': { reading: 'しょうらい', meaning: 'Future (usually near future)', jlpt: 'N4' }, 
  '遠く': { reading: 'とおく', meaning: 'Far away, distant', jlpt: 'N5' }, 
  '離れて': { reading: 'はなれて', meaning: 'Separated, distant (離れる)', jlpt: 'N3' }, 
  '二人': { reading: 'ふたり', meaning: 'Two people, pair', jlpt: 'N5' }, 
  '新しい': { reading: 'あたらしい', meaning: 'New, novel, fresh', jlpt: 'N5' }, 
  '未来': { reading: 'みらい', meaning: 'Future (usually distant future)', jlpt: 'N3' }, 
  '挑戦': { reading: 'ちょうせん', meaning: 'Challenge, defiance', jlpt: 'N3' }, 
  '始める': { reading: 'はじめる', meaning: 'To start, to begin', jlpt: 'N5' }, 
  '勇気': { reading: 'ゆうき', meaning: 'Courage, bravery', jlpt: 'N3' }, 
  '感じて': { reading: 'かんじて', meaning: 'Feeling, sensing (感じる)', jlpt: 'N3' }, 
  '希望': { reading: 'きぼう', meaning: 'Hope, aspiration', jlpt: 'N3' }, 
  '世界': { reading: 'せかい', meaning: 'World, society', jlpt: 'N4' }, 
  '時間': { reading: 'じかん', meaning: 'Time, hours', jlpt: 'N5' }, 
  '言葉': { reading: 'ことば', meaning: 'Word, language', jlpt: 'N5' }, 
  '大切': { reading: 'たいせつ', meaning: 'Important, precious', jlpt: 'N5' }, 
  '友達': { reading: 'ともだち', meaning: 'Friend, companion', jlpt: 'N5' }, 
  '勉強': { reading: 'べんきょう', meaning: 'Study, diligence', jlpt: 'N5' }, 
  '日本': { reading: 'にほん', meaning: 'Japan', jlpt: 'N5' }, 
  '生活': { reading: 'せいかつ', meaning: 'Life, living', jlpt: 'N4' }, 
  '毎日': { reading: 'まいにち', meaning: 'Every day', jlpt: 'N5' }, 
  '学校': { reading: 'がっこう', meaning: 'School', jlpt: 'N5' }, 
  '行く': { reading: 'いく', meaning: 'To go', jlpt: 'N5' }, 
  '行きました': { reading: 'いきました', meaning: 'Went', jlpt: 'N5' }, 
  '食べる': { reading: 'たべる', meaning: 'To eat', jlpt: 'N5' }, 
  '食べました': { reading: 'たべました', meaning: 'Ate', jlpt: 'N5' }, 
  '見る': { reading: 'みる', meaning: 'To see / look', jlpt: 'N5' }, 
  '帰る': { reading: 'かえる', meaning: 'To return home', jlpt: 'N5' } 
} 

const KANJI_REGEX = /[\u4E00-\u9FAF]/ 
const COMPOUND_REGEX = /([\u4E00-\u9FAF]+[\u3040-\u309F]*|[\u3040-\u309F]+|[\u30A0-\u30FF]+|[^\u4E00-\u9FAF\u3040-\u309F\u30A0-\u30FF\s]+|\s+)/g 

export function isKanji(char: string): boolean {
  return KANJI_REGEX.test(char) 
} 

export function parseBracketSegments(textWithBrackets: string): RubySegment[] {
  const segments: RubySegment[] = [] 
  const regex = /([^\[]+)(?:\[([^\]]+)\])?/g 
  let match: RegExpExecArray | null 

  while ((match = regex.exec(textWithBrackets)) !== null) {
    const rawText = match[1] 
    const ruby = match[2] 
    if (ruby) {
      segments.push({ text: rawText, ruby }) 
    } else {
      const parts = rawText.match(COMPOUND_REGEX) || [rawText] 
      for (const p of parts) {
        if (!p) continue 
        const entry = COMMON_DICTIONARY[p] 
        segments.push({ text: p, ruby: entry ? entry.reading : undefined }) 
      } 
    } 
  } 

  return segments 
} 

export function tokenizeSentence(sentence: string, targetWords: string[] = [], targetWordsData: StoryWordItem[] = []): StoryToken[] {
  const matches = sentence.match(COMPOUND_REGEX) || [sentence] 
  const tokens: StoryToken[] = [] 

  for (const item of matches) {
    if (!item) continue 
    const hasKanji = KANJI_REGEX.test(item) 
    const isPunct = /^[、。！？「」\s]+$/.test(item) 

    const matchedTargetData = targetWordsData.find(tw => tw.kanji === item || tw.reading === item) 
    const isTarget = targetWords.some(tw => tw === item || (tw.length >= 2 && item.includes(tw)) || (item.length >= 2 && tw.includes(item))) || Boolean(matchedTargetData) 

    const dictEntry = COMMON_DICTIONARY[item] 
    const reading = matchedTargetData?.reading || dictEntry?.reading || (hasKanji ? findBestReading(item) : undefined) 
    const meaning = matchedTargetData?.meaning || dictEntry?.meaning || '' 

    const segments: RubySegment[] = [] 
    if (reading && hasKanji) {
      segments.push({ text: item, ruby: reading }) 
    } else {
      segments.push({ text: item }) 
    } 

    tokens.push({
      surface: item, 
      segments, 
      reading, 
      furigana: reading, 
      isKanji: hasKanji, 
      isPunctuation: isPunct, 
      isTarget, 
      meaning 
    }) 
  } 

  return tokens 
} 

function findBestReading(word: string): string | undefined {
  const matches = Object.keys(COMMON_DICTIONARY).filter(k => word.includes(k)) 
  if (matches.length > 0) {
    const best = matches.sort((a, b) => b.length - a.length)[0] 
    return COMMON_DICTIONARY[best].reading 
  } 
  return undefined 
} 
