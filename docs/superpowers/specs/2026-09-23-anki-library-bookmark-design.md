# Anki Library, Bookmark Feature & Story Vocabulary Design

## Overview
This feature introduces a comprehensive personal vocabulary and Anki deck-building system into AnkiLock:
1. **"Words From Story" Container**: AI extracts notable Japanese vocabulary words from each generated reading story, rendering them in a dedicated container below "Target Words". Tapping any word launches Jisho dictionary lookup.
2. **Persistent Bookmark Feature**: Tapping the bookmark button in `BlossomWordBottomSheet` (or in Jisho) persists the word, its reading, meaning, and source sentence into a local library.
3. **Anki Library Tab (3rd Tab)**: A new top-level tab in `BlossomBottomNav` (`Cards` | `Stories` | `Library` | `Jisho`) allowing learners to curate words, edit definitions, add words manually, preview cards, and manage deck tags.
4. **Native Kaishi 1.5k `.apkg` Compiler & AnkiDroid Sync**: A native SQLite/ZIP export engine replicating the [Anki-Package-Converter-2](https://github.com/KongSengHero/Anki-Package-Converter-2) structure, creating standalone `.apkg` packages with Kaishi 1.5k models, alongside 1-tap direct sync into AnkiDroid via `AnkiDroidHelper`.

---

## 1. Data Models & Persistence

### 1.1 `BookmarkedWord` Model
Located in `com.ankilock.data.BookmarkedWord`:
* `id: String` (UUID)
* `kanji: String` (target word)
* `reading: String` (hiragana / katakana reading)
* `meaning: String` (English definition)
* `furigana: String` (Anki ruby syntax e.g. `企[き] 業[ぎょう] 概[がい] 要[よう]`)
* `sentence: String` (example sentence from story or AI generation)
* `sentenceMeaning: String` (English translation of example sentence)
* `sentenceFurigana: String` (Furigana annotated sentence with `<b>...</b>` around target word)
* `sourceStoryTitle: String?` (title of story where mined, if applicable)
* `tags: List<String>` (deck tags, default: `["Blossom", "MinedVocab"]`)
* `createdAt: Long` (System.currentTimeMillis())

### 1.2 `BookmarkManager`
Located in `com.ankilock.data.BookmarkManager`:
* Singleton object holding in-memory `val bookmarkedWords = mutableStateListOf<BookmarkedWord>()`.
* Persists to `SharedPreferences` as JSON in `blossom_bookmarks_v1.json`.
* Thread-safe read/write:
  * `addWord(word: BookmarkedWord)`: deduplicates by `kanji.trim()`.
  * `removeWord(kanji: String)`: removes matching word.
  * `isBookmarked(kanji: String): Boolean`: checks if word exists.
  * `toggleBookmark(kanji: String, reading: String, meaning: String, sentence: String, storyTitle: String?)`: toggles state and returns new boolean.
  * `updateWord(word: BookmarkedWord)`: edits an existing entry.
  * `clearAll()`: empties the list.

### 1.3 `GeneratedStory` Model Update
In `com.ankilock.data.ReadingModels.kt`:
* Add `val storyWords: List<StoryWordItem> = emptyList()` to `GeneratedStory`.
* Update `ReadingHistoryManager.kt`:
  * In serialization, write `"storyWords"` JSONArray.
  * In deserialization, parse `"storyWords"` back into `List<StoryWordItem>`.

---

## 2. Gemini Story Generation & "Words From Story" UI

### 2.1 Gemini Prompt & Parsing (`GeminiStoryService.kt`)
* In `buildJlptStoryPrompt()`, extend the JSON schema requirement:
  ```json
  {
    "title": "Story Title",
    "storyJapanese": "...",
    "storyFurigana": "...",
    "questions": [ ... ],
    "storyWords": [
      {
        "kanji": "図書館",
        "reading": "としょかん",
        "meaning": "library"
      }
    ]
  }
  ```
* Instruction: Prompt Gemini to extract 6–10 authentic, natural vocabulary words that actively appear in the story text (excluding common particles like は, が, を, and basic pronouns).
* In `parseStoryResponse()`: parse `"storyWords"` into `List<StoryWordItem>` and assign to `GeneratedStory.storyWords`.

### 2.2 UI in `ReadingScreen.kt`
* Add container directly below `TARGET WORDS FROM YOUR CARDS`:
  * Surface card with `BlossomColors.SurfaceElevated` and `BlossomColors.CardBorder`.
  * Header: "WORDS FROM STORY (`count`)" with icon `Icons.AutoMirrored.Filled.MenuBook` tinted in `BlossomColors.SkyCyan`.
  * FlowRow of word chips:
    * Chip displays `word.kanji`.
    * Tapping chip sets `quickJishoWord = word.kanji`.
    * `quickJishoWord` triggers `BlossomWordBottomSheet`.

### 2.3 `BlossomWordBottomSheet` Reactive Bookmark Integration
* Pass `isBookmarked = BookmarkManager.isBookmarked(targetWord)` into `BlossomWordBottomSheet`.
* On bookmark click:
  * Extract current sentence context from story (finding sentence containing `targetWord`).
  * Call `BookmarkManager.toggleBookmark(...)`.
  * Immediate visual toggle between `Icons.Default.BookmarkBorder` and `Icons.Default.Bookmark` with amber glow.

---

## 3. Library Tab UI (`LibraryScreen.kt`) & Navigation

### 3.1 `BlossomBottomNav.kt`
* Expand `BlossomTab` enum to 4 tabs:
  1. `CARDS` ("Cards", `Icons.Default.Style`)
  2. `STORIES` ("Stories", `Icons.AutoMirrored.Filled.MenuBook`)
  3. `LIBRARY` ("Library", `Icons.Default.Bookmark`)
  4. `JISHO` ("Jisho", `Icons.Default.Search`)
* Theme colors:
  * `LIBRARY.activeColor = BlossomColors.WarmAmber`
  * `LIBRARY.containerColor = BlossomColors.WarmAmberContainer`
* Update `BlossomBottomNavTest.kt` for tab count = 4 and ordinal order.

### 3.2 `MainActivity.kt`
* Page 0: `ModernSettingsScreen`
* Page 1: `ReadingScreen`
* Page 2: `LibraryScreen`
* Page 3: `JishoScreen`

### 3.3 `LibraryScreen.kt` Design
* **Header**:
  * Title: "Blossom • Anki Library"
  * Subtitle: `${bookmarkedWords.size} words saved • Kaishi 1.5k Ready`
  * Top bar actions:
    * `+ Add Word`: Opens sheet to search Jisho and add any custom word directly.
    * `Deck Settings`: Modal to configure Deck Name (`Blossom::Vocabulary`) and Tag (`Blossom`).
* **Search / Filter**:
  * Real-time query field matching kanji, reading, or meaning.
* **Card Items**:
  * Word kanji in 20sp bold, reading in furigana accent, English meaning in soft green.
  * Sentence badge showing attached story sentence if available.
  * Actions: Pronounce (TTS), Edit word modal, Delete from library.
* **Bottom Action Bar**:
  * **"BUILD ANKI PACKAGE (.apkg)"**: Launches `AnkiPackageExporter`, generates `.apkg` file, and opens Android Share / Open With sheet.
  * **"SYNC TO ANKIDROID"**: Direct batch-insert via `AnkiDroidHelper` into local AnkiDroid.

---

## 4. Native Kaishi 1.5k `.apkg` Compiler & AnkiDroid Sync

### 4.1 Native SQLite Engine (`AnkiPackageExporter.kt`)
Replicates the database structure and schema from `Anki-Package-Converter-2`:
* Creates SQLite database `collection.anki2` using Android's native `SQLiteDatabase`:
  * Tables: `col`, `notes`, `cards`, `revlog`, `graves`.
  * Indexes: `ix_notes_usn`, `ix_cards_usn`, `ix_revlog_usn`, `ix_cards_nid`, `ix_cards_sched`, `ix_revlog_cid`, `ix_notes_csum`.
  * Injects model `Kaishi 1.5k` (id: `1708628080880`) with full CSS, front template, back template, and 14 fields separated by `\x1f`:
    0: `Word`
    1: `Word Reading`
    2: `Word Meaning`
    3: `Word Furigana`
    4: `Word Audio`
    5: `Sentence`
    6: `Sentence Meaning`
    7: `Sentence Furigana`
    8: `Sentence Audio`
    9: `Notes`
    10: `Pitch Accent`
    11: `Pitch Accent Notes`
    12: `Frequency`
    13: `Picture`
  * Inserts default decks and configurations.
  * For each `BookmarkedWord`:
    * Computes GUID (10-char random string from base charset).
    * Computes SHA-1 `csum` of the word.
    * Formats `<b>word</b>` in sentence and `Word[Reading]` in furigana.
    * Inserts record into `notes` and `cards`.
* Closes SQLite database.
* Archives `collection.anki2` + `media` (`{}`) into `<DeckName>.apkg` via `java.util.zip.ZipOutputStream`.
* Returns `File` ready for `FileProvider` sharing.

### 4.2 Direct Sync (`AnkiDroidHelper.kt`)
* Checks `hasApiPermission()`.
* Creates or locates deck `Blossom::Vocabulary`.
* Writes notes into AnkiDroid's content provider so cards appear immediately in AnkiDroid without file import.

---

## 5. Verification Plan

### Automated Tests
* `BlossomBottomNavTest.kt`: Verify 4 tabs, ordering, and selection.
* `BookmarkManagerTest.kt`: Verify adding, deduplication, JSON serialization, unbookmarking, and state updates.
* `AnkiPackageExporterTest.kt`: Verify `.apkg` creation, ZIP validity, SQLite table existence, and Kaishi 1.5k note insertion.

### Manual Verification
1. Open Stories tab and generate a new story: verify "TARGET WORDS" and "WORDS FROM STORY" both render.
2. Tap a word in "WORDS FROM STORY": verify `BlossomWordBottomSheet` opens with Jisho definition.
3. Tap the Bookmark icon: verify it turns solid amber.
4. Switch to the new 3rd tab "Library": verify the bookmarked word appears with its story sentence.
5. Tap "BUILD ANKI PACKAGE (.apkg)": verify package generation and Android share sheet opening.
6. Tap "SYNC TO ANKIDROID": verify cards appear in AnkiDroid.
