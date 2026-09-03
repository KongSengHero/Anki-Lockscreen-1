# AnkiLock Redesign (Shinobi Edition) Design Specification

## Overview & Goals
Redesign **AnkiLock**—an Android application built with Kotlin and Jetpack Compose—into a tactile, gamified Japanese learning ecosystem inspired by the **Shinobi: Read & Learn Japanese** app. 

The redesigned application combines AnkiLock's existing flashcard and lockscreen foundation with Shinobi's signature visual style:
1. Physical tactile 3D "push" buttons with extruded bottom bevels and haptic depression.
2. Distinct squircle action docks and a vibrant 4-tab squircle navigation system.
3. An illustrated Japanese reader with color-coded token underlines and a tap-to-define bottom sheet.
4. Smart Story Forging that extracts 5–8 due/learning Anki flashcard words into natural stories scaling from N5 to N1.
5. Gamification loops including daily quests, streaks, XP, and beveled hexagonal Shinobi rank badges.

---

## 1. 4-Tab Architecture

The app is structured into 4 primary tabs:

```
┌─────────────────────────────────────────────────────────────────┐
│   [ 🎴 Study ]    [ 📖 Stories ]    [ 🔖 Vocab ]    [ 👤 Dojo ]   │
└─────────────────────────────────────────────────────────────────┘
```

### Tab 1: 🎴 Study (Anki Flashcards, Listening & Lock Screen)
* **SRS Summary Header**: Histogram displaying current card counts: `New`, `Now / Due` (highlighted red), `<24h`, `<1wk`, `Future`.
* **Quick Review Deck**: Interactive study mode with tactile 3D rating buttons (`Again`, `Hard`, `Good`, `Easy`).
* **Listening & Shadowing Drill**: Audio-first dictation mode with sentence masking and slow-speed snail toggle.
* **Lock Screen Studio**: Quick toggle to enable/disable lock screen flashcards, wallpaper selection, blur/tint sliders, and ruby furigana formatting.

### Tab 2: 📖 Stories (Shinobi Reader & Story Forge)
* **Curated Story Library**: Default starter stories organized by JLPT level (Starter/N5 to Master/N1) and categories (Daily Life, School, Mystery, Culture).
* **Smart Forge Story CTA**: One-tap action that automatically pulls 5–8 due or learning flashcard words from Anki and generates a cohesive illustrated story.
* **Interactive Shinobi Reader**:
  * Top illustration card with progress indicator pill (`Page 3/5`) and flag report button.
  * Japanese text with alternating orange and blue underlined word tokens.
  * Sticky squircle control dock: Settings, Furigana toggle (`あ` crossed out), Play/Pause audio, Snail slow speed (0.75x), and Prev/Next buttons.
  * Tap-to-define bottom sheet with word pronunciation audio, English definition box, part of speech pill, and kanji details.
* **Comprehension Quiz**: 4 end-of-story questions using 2x2 option cards or 3D circular True/False buttons (`Red ✕` / `Green ✓`), followed by a green success bottom sheet.

### Tab 3: 🔖 Vocab & Decks (Mined Words & Furigana Bank)
* **Mined Vocabulary List**: Words saved by tapping the bookmark button inside stories.
* **Sync to Anki Action**: One-tap button to export mined words and context sentences directly into user's AnkiDroid decks.
* **Furigana & Dictionary Search**: Search bar to query readings, kanji breakdowns, and example sentences.

### Tab 4: 👤 Dojo (Gamification, Quests & Settings)
* **Practice Streak Tracker**: Current streak, longest streak, and calendar visualization.
* **Daily Quests & Weekly Challenges**: Bite-sized goals (e.g., "Read 1 story: 50 XP", "Review 10 flashcards: 100 XP", "Perfect quiz score: 100 XP").
* **Shinobi Rank Badges**: 3D beveled hexagonal rank badges (`Tetsu`, `Do`, `Gin`, `Kin`, `Hisui`, `Kongo`, `Meijin`).
* **Total XP & Statistics**: Aggregate cards studied, stories read, and study time.
* **Settings & Integrations**: AnkiDroid API authorization status, Gemini AI API key configuration, and audio engine preferences.

---

## 2. Shinobi UI Design System (Jetpack Compose)

### 2.1 Color Tokens
* **Background Deep (OLED)**: `0xFF111214`
* **Surface Card Level 1**: `0xFF1C1D21`
* **Surface Card Level 2 (Elevated)**: `0xFF24262B`
* **Card Border / Hairline**: `0xFF2E3036` (1dp stroke)
* **Primary Electric Blue**: Face `0xFF2F69FF`, 3D Lip `0xFF1A47C7`
* **Success Emerald Green**: Face `0xFF16A34A`, 3D Lip `0xFF0F7535`, Surface `0xFF122D1B`
* **Coral Red**: Face `0xFFF04438`, 3D Lip `0xFFBA251A`
* **Warm Amber / Gold**: Face `0xFFF59E0B`, 3D Lip `0xFFB45309`, Surface `0xFF2A2210`
* **Violet / Purple**: Face `0xFFA855F7`, 3D Lip `0xFF7E22CE`
* **Text Colors**: Primary `0xFFFFFFFF`, Secondary `0xFF94A3B8`, Muted `0xFF64748B`

### 2.2 Component Specifications

#### `ShinobiTactileButton`
* Two-layer box with rounded corners (`14dp`).
* Bottom lip has `3.5dp` height in darker tone.
* Tap down: Face offsets down by `3.5dp`, flattening the lip to produce a physical mechanical sensation.
* Typography: Bold uppercase (`FontWeight.Bold`, 15sp, letterSpacing 0.5sp).

#### `ShinobiSquircleButton`
* Smooth continuous squircle shape (`12dp` radius).
* Dark surface (`0xFF22242A`) with semantic colored outline (1dp stroke) matching its role:
  * Blue outline for Furigana/Translate
  * Red outline for Audio Play
  * Orange outline for Snail Speed
  * White outline for Next/Prev arrows

#### `ShinobiBottomNav`
* Bar height `72dp`, background `0xFF16171B` with top border `0xFF26272D`.
* 4 squircle icons with high-saturation solid backgrounds:
  * Study: Blue `0xFF2F69FF`
  * Stories: Green `0xFF16A34A`
  * Vocab: Amber `0xFFF59E0B`
  * Dojo: Purple `0xFFA855F7`
* Selected tab displays an elevated glow halo and scale animation (`1.08f`).

#### `ShinobiReaderWordToken`
* Clickable text span with padded bounds.
* Bottom underline stroke (2dp) alternating between Orange (`0xFFE5983A`) and Cornflower Blue (`0xFF4A88E5`).
* Selected token displays a soft highlight background (`0x332F69FF`).

#### `ShinobiWordBottomSheet`
* Draggable modal bottom sheet on `0xFF18191D`.
* Top drag pill handle (`4dp x 36dp`).
* Word header with audio speaker squircle, furigana toggle, and bookmark ribbon squircle.
* Part of speech pill tag (`0xFF2A2210` with amber border).
* English definitions container in dark forest green (`0xFF122D1B`).
* Kanji details card in deep terracotta (`0xFF341B18`) displaying meanings and On/Kun readings.

---

## 3. Story Progression Engine (N5–N1)

| Level | Shinobi Stage | Pages | Sentences / Page | Total Chars | Linguistic Style | Target Time |
| :--- | :--- | :---: | :---: | :---: | :--- | :---: |
| **N5** | **Starter** | **5** | 1–2 | ~100–150 | Simple S-V-O, basic particles, everyday scenarios, full furigana | ~3–5 mins |
| **N4** | **Beginner** | **5–6** | 2–3 | ~200–300 | Compound sentences (`から`, `けど`, `たり`), conditional `たら`, potential forms | ~5–7 mins |
| **N3** | **Intermediate** | **6–7** | 3–4 | ~400–550 | Relative clauses, passive/causative, conversational nuance (`わけ`, `ように`) | ~7–10 mins |
| **N2** | **Advanced** | **7–8** | 4–5 | ~600–800 | Authentic written Japanese, news/social themes, complex conjunctions | ~10–12 mins |
| **N1** | **Master** | **8** | 4–6 | ~850–1,100 | Literary depth, abstract themes, four-character idioms (*Yojijukugo*), rare kanji | ~12–15 mins |

---

## 4. Smart Story Forging Pipeline

1. **Card Selection**:
   * Scans AnkiDroid for cards where `dueToday == true` or `reps == 0` (new).
   * Selects **5 to 8 anchor words**.
   * If reviews due = 0, falls back to recent weak cards or prompt to read curated library stories.
2. **AI Generation Contract**:
   * Prompt specifies target JLPT level, page count (5–8), sentence count per page, and required anchor words.
   * Model returns structured JSON:
     * `title`: English and Japanese title
     * `pages`: Array of page objects containing `text`, `audioSentence`, and `tokens` (word, reading, furigana, pos, englishDefinition, kanjiInfo)
     * `quiz`: 4 questions with type (`multiple_choice` or `true_false`), prompt, options, answerIndex, and explanation.
3. **Local Persistence**:
   * Saved into local JSON / Room database with reading progress and completion states.

---

## 5. Implementation Phasing

1. **Phase 1: Foundation & Shinobi Design System**:
   * Create `com.ankilock.ui.shinobi` package.
   * Build `ShinobiColors`, `ShinobiTactileButton`, `ShinobiSquircleButton`, `ShinobiBottomNav`, and `ShinobiPillBadge`.
2. **Phase 2: Main Navigation & Tab Shell**:
   * Refactor `MainActivity.kt` to host the 4-tab `ShinobiBottomNav`.
   * Wire tab transitions and top status headers (Streak, XP, Due counts).
3. **Phase 3: Interactive Shinobi Story Reader & Quizzes**:
   * Redesign `ForgeStoryScreen.kt` reader: artwork card, underlined word tokens, squircle audio dock.
   * Build `ShinobiWordBottomSheet` dictionary lookup.
   * Build Shinobi comprehension quiz screens (2x2 cards, circular 3D buttons).
4. **Phase 4: Smart Story Forging & N5–N1 Scaling**:
   * Implement anchor card extraction from AnkiDroid.
   * Connect Gemini structured prompt with N5–N1 scaling rules.
5. **Phase 5: Vocab Mining & Dojo Gamification**:
   * Build Tab 3 (Vocab bookmarks + sync to Anki).
   * Build Tab 4 (Daily quests, weekly challenges, streak calendar, rank badges).
