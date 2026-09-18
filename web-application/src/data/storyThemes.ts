export interface ThemeBadgeStyle {
  backgroundColor: string 
  contentColor: string 
  borderColor: string 
} 

export const STORY_CATEGORIES: Record<string, string[]> = {
  Happy: [
    'Love story - first love', 
    'Love story - reunited after years apart', 
    'Unexpected romance', 
    'Passing a dream university', 
    'Passing a big exam after struggling', 
    'Reconciling with an estranged family member', 
    'Achieving a long-held dream (music, art, sports)', 
    'Landing a dream career', 
    'Adopting a pet', 
    'Childhood friends reuniting', 
    'A small act of kindness from a stranger', 
    'Starting a new job that turns out great', 
    'Surprise birthday celebration', 
    'Surprise promotion at work', 
    'A proposal', 
    'Finding a lost item after a long search', 
    'Finding a lost person after a long search', 
    'Overcoming a fear of heights', 
    'Overcoming a fear of public speaking', 
    'Overcoming a fear of swimming', 
    'A community coming together to help someone', 
    'Winning a competition after many failed attempts', 
    'A long-distance friendship finally meeting in person' 
  ], 
  Sad: [
    'Broken love story - breakup', 
    'Unrequited love', 
    'A loved one passing away', 
    'Trying your best but failing anyway', 
    'Losing touch with an old friend', 
    'A pet passing away', 
    'Moving away from a beloved hometown', 
    'Betrayal by someone trusted', 
    'Regret over a missed opportunity', 
    'Watching someone you love struggle and being unable to help', 
    'Growing apart from family', 
    'A promise that couldn\'t be kept', 
    'Saying goodbye to a childhood home', 
    'Realizing a dream is no longer achievable', 
    'A friendship ending after a big fight', 
    'Missing someone who moved far away', 
    'Letting go of something you\'ve held onto for years' 
  ], 
  SliceOfLife: [
    'A day at school', 
    'First day at a new school', 
    'Office life', 
    'First day at a new job', 
    'Coworker dynamics', 
    'A trip to somewhere cool - a big city', 
    'A trip to somewhere cool - nature/mountains', 
    'A trip abroad', 
    'A rainy day at home', 
    'Grocery shopping', 
    'Cooking a meal for someone', 
    'Commuting and people-watching', 
    'A weekend with no plans', 
    'Family dinner conversations', 
    'Late-night conversation with a friend', 
    'The first snow of winter', 
    'The start of summer break', 
    'An autumn walk', 
    'A quiet hobby session (painting, gardening, gaming)', 
    'Running errands and small daily encounters', 
    'A lazy Sunday morning', 
    'Studying together with friends' 
  ], 
  Mystery: [
    'A strange letter with no sender', 
    'A neighbor who suddenly disappeared', 
    'An unsolved local legend', 
    'A locked room with no explanation', 
    'Following a trail of clues left behind by someone', 
    'A hidden message found in an old book', 
    'Someone who seems to know too much about you' 
  ], 
  Horror: [
    'A haunted house on the edge of town', 
    'An urban legend that turns out to be real', 
    'A ghost story passed down in a family', 
    'Something strange happening at midnight', 
    'A doll or object that shouldn\'t move on its own', 
    'A road that isn\'t supposed to exist' 
  ], 
  Adventure: [
    'A treasure hunt with an old map', 
    'Survival in the wild after getting lost', 
    'A road trip that goes completely sideways', 
    'Exploring an abandoned place', 
    'A journey to find something rare', 
    'Racing against time to reach a destination' 
  ], 
  Inspirational: [
    'An underdog story', 
    'A comeback after public failure', 
    'A mentor who changes someone\'s life', 
    'Someone overcoming a disability to achieve a goal', 
    'Starting over from nothing', 
    'A community rebuilding after a disaster' 
  ], 
  Fantasy: [
    'A hidden magic school', 
    'Discovering a hidden world within our own', 
    'An encounter with a mythical creature', 
    'A cursed object that grants wishes with a price', 
    'A prophecy that a young person must fulfill', 
    'Two rival kingdoms and an unlikely alliance' 
  ], 
  Comedy: [
    'Pretending to understand Japanese slang in front of cool teens', 
    'A disastrous first attempt at making sushi at home', 
    'Accidentally joining a competitive dodgeball club', 
    'Mistaking a costume festival for a normal day', 
    'A runaway robot vacuum that escapes into the neighborhood', 
    'Two shy people both trying desperately to pay the bill', 
    'Practicing a confident speech only to say the completely wrong word', 
    'A cat that behaves like a corporate CEO', 
    'Getting locked inside a shopping mall overnight', 
    'An epic battle against a stubborn mosquito before sleep' 
  ], 
  Nostalgic: [
    'Summer festival (Matsuri) memories from ten years ago', 
    'The taste of childhood shaved ice (Kakigori) on a hot day', 
    'Hearing the cicadas on the last day of summer vacation', 
    'An old cassette tape found in an attic', 
    'Walking past one\'s elementary school at dusk', 
    'The sound of the evening chime (Gojinohanashi) echoing through town', 
    'Finding an old handwritten letter in a textbook', 
    'Drinking ramune by the riverbank in high school uniform', 
    'The scent of tatami mats in a grandparents\' summer house', 
    'A faded polaroid photograph of old friends who lost touch' 
  ] 
} 

export const ALL_THEMES = Object.keys(STORY_CATEGORIES) 

export function formatThemeName(themeKey: string): string {
  if (themeKey === 'SliceOfLife') return 'Slice of Life' 
  return themeKey 
} 

export function getThemeBadgeColors(themeKey: string): ThemeBadgeStyle {
  switch (themeKey) {
    case 'Happy':
      return {
        backgroundColor: '#332717', 
        contentColor: '#CFA055', 
        borderColor: 'rgba(207, 160, 85, 0.4)' 
      } 
    case 'Sad':
      return {
        backgroundColor: '#1E2235', 
        contentColor: '#7C8CF8', 
        borderColor: 'rgba(124, 140, 248, 0.4)' 
      } 
    case 'SliceOfLife':
      return {
        backgroundColor: '#1B3024', 
        contentColor: '#5FA77C', 
        borderColor: 'rgba(95, 167, 124, 0.4)' 
      } 
    case 'Mystery':
      return {
        backgroundColor: '#291F35', 
        contentColor: '#9678B6', 
        borderColor: 'rgba(150, 120, 182, 0.4)' 
      } 
    case 'Horror':
      return {
        backgroundColor: '#2A1B20', 
        contentColor: '#E87A90', 
        borderColor: 'rgba(232, 122, 144, 0.4)' 
      } 
    case 'Adventure':
      return {
        backgroundColor: '#162B35', 
        contentColor: '#22D3EE', 
        borderColor: 'rgba(34, 211, 238, 0.4)' 
      } 
    case 'Inspirational':
      return {
        backgroundColor: '#1F2E28', 
        contentColor: '#4EBA6F', 
        borderColor: 'rgba(78, 186, 111, 0.4)' 
      } 
    case 'Fantasy':
      return {
        backgroundColor: '#2B1E38', 
        contentColor: '#C084FC', 
        borderColor: 'rgba(192, 132, 252, 0.4)' 
      } 
    case 'Comedy':
      return {
        backgroundColor: '#332415', 
        contentColor: '#FB923C', 
        borderColor: 'rgba(251, 146, 60, 0.4)' 
      } 
    case 'Nostalgic':
      return {
        backgroundColor: '#2A1B20', 
        contentColor: '#E87A90', 
        borderColor: 'rgba(232, 122, 144, 0.4)' 
      } 
    default:
      return {
        backgroundColor: '#252A35', 
        contentColor: '#E8EAF0', 
        borderColor: '#2C3240' 
      } 
  } 
} 
