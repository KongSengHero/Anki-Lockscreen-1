import type { GeneratedStory } from '../types/story' 

const DEFAULT_COVER = 'https://images.unsplash.com/photo-1528164344705-475426879c0d?w=800&auto=format&fit=crop&q=80' 

export async function generateJapaneseStory(
  apiKey: string, 
  jlptLevel: string, 
  targetVocabulary: string[], 
  modelName: string = 'gemini-2.5-flash', 
  theme?: string, 
  topic?: string, 
  storyLength: string = 'Medium', 
  questionsCount: number = 3 
): Promise<Omit<GeneratedStory, 'id'>> {
  if (!apiKey || apiKey.trim() === '') {
    throw new Error('Please enter your Google Gemini API key in Settings') 
  } 

  const vocabPrompt = targetVocabulary.length > 0 
    ? `Incorporate these target vocabulary words naturally: ${targetVocabulary.join(', ')}.` 
    : 'Use common daily conversation vocabulary suited for this level.' 

  const themePrompt = theme 
    ? `Theme category: "${theme}"${topic ? ` with topic focus: "${topic}"` : ''}.` 
    : 'Theme: Daily Life, Friendship, or Japanese Culture.' 

  const lengthPrompt = storyLength === 'Short' 
    ? 'Write a short story of around 150 words in 2-3 paragraphs.' 
    : storyLength === 'Long' 
    ? 'Write an extended story of around 500 words in 4-6 paragraphs.' 
    : 'Write a standard story of around 300 words in 3-4 paragraphs.' 

  const prompt = `
You are an expert Japanese language educator and storyteller.
Write an engaging, culturally authentic story for a Japanese learner at JLPT level ${jlptLevel}.
${themePrompt}
${lengthPrompt}
${vocabPrompt}

Return ONLY valid JSON strictly adhering to this structure without markdown fences or backticks:
{
  "title": "Story title in Japanese",
  "storyJapanese": "The full Japanese story in natural paragraphs. Use standard kanji and kana appropriate for ${jlptLevel}.",
  "storyFurigana": "Same story with bracketed furigana for kanji, like 私[わたし]は...",
  "englishTranslation": "Full English translation of the story.",
  "targetWords": ["4-8", "vocabulary", "words", "used"],
  "questions": [
    {
      "id": 1,
      "questionText": "Comprehension question in Japanese",
      "options": ["Option 1", "Option 2", "Option 3", "Option 4"],
      "correctOptionIndex": 0,
      "explanation": "Brief explanation in Japanese"
    }
  ]
}
Generate exactly ${questionsCount} comprehension questions.
` 

  const url = `https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent?key=${apiKey.trim()}` 

  const res = await fetch(url, {
    method: 'POST', 
    headers: {
      'Content-Type': 'application/json' 
    }, 
    body: JSON.stringify({
      contents: [
        {
          parts: [{ text: prompt }] 
        } 
      ], 
      generationConfig: {
        responseMimeType: 'application/json', 
        temperature: 0.7 
      } 
    }) 
  }) 

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({})) 
    const message = errorData?.error?.message || `API error ${res.status}: ${res.statusText}` 
    throw new Error(message) 
  } 

  const data = await res.json() 
  const rawText = data?.candidates?.[0]?.content?.parts?.[0]?.text 
  if (!rawText) {
    throw new Error('Gemini returned an empty response') 
  } 

  const cleanJson = rawText.replace(/```json/gi, '').replace(/```/g, '').trim() 
  const parsed = JSON.parse(cleanJson) 

  const content = parsed.storyJapanese || parsed.content || '' 
  const furiganaContent = parsed.storyFurigana || null 
  const translation = parsed.englishTranslation || parsed.translation || '' 
  const title = parsed.title || '日本語の物語' 

  const questions = (parsed.questions || []).map((q: any, i: number) => ({
    id: q.id || i + 1, 
    questionText: q.questionText || q.question || '', 
    question: q.questionText || q.question || '', 
    options: q.options || [], 
    correctOptionIndex: q.correctOptionIndex !== undefined ? q.correctOptionIndex : (q.correctIndex || 0), 
    correctIndex: q.correctOptionIndex !== undefined ? q.correctOptionIndex : (q.correctIndex || 0), 
    explanation: q.explanation || '' 
  })) 

  return {
    title, 
    content, 
    furiganaContent, 
    translation, 
    jlptLevel, 
    targetWords: parsed.targetWords || targetVocabulary, 
    questions, 
    theme: theme || 'Daily Life', 
    topic: topic || '', 
    isPinned: false, 
    isPassed: false, 
    coverUrl: DEFAULT_COVER, 
    createdAt: Date.now() 
  } 
} 
