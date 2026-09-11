# 📱 AnkiLock

[![Download Latest APK](https://img.shields.io/github/v/release/KongSengHero/Anki-Lockscreen-1?label=Download%20APK%20(v2.5)&logo=android&color=success)](https://github.com/KongSengHero/Anki-Lockscreen-1/releases/latest)

> **Learn flashcards every time you look at your phone!**

AnkiLock puts your Anki flashcards directly onto your Android **Lock Screen** (styled like a music player) and your **Home Screen** (as a widget).

---

## 💡 Why AnkiLock?

Opening an app every day to study is hard to remember. 

With AnkiLock, you don't even have to open the app. Every time you turn on your phone to check the time or a message, your next flashcard is right there waiting for you!

---

## ✨ Features

- 🎵 **Music Player Lock Screen**: Cards look like album artwork on your lock screen.
- 🈸 **Furigana (Ruby) Support**: Japanese readings appear neatly on top of Kanji.
- 🧩 **Home Screen Widget**: Study and flip cards directly from your home screen.
- 🎨 **Background Studio**: Use custom wallpapers, dark blur, sunsets, and adjust blur/tint.
- ⚡ **Instant Sync**: Lock screen, notification, widget, and app stay in sync.

---

## 🚀 Quick Setup (3 Easy Steps)

### Step 0: Downloading
1. Download the latest **[AnkiLock (v2.5) APK](https://github.com/KongSengHero/Anki-Lockscreen-1/releases/latest)** directly from the **[Releases](https://github.com/KongSengHero/Anki-Lockscreen-1/releases)** section (or grab `AnkiLock_(v2.5).apk` inside this repository).
2. If the system prompts you that this app is unknown or dangerous, tap the dropdown and select **"Install Anyway"**.
3. There is no malware, all your data stays 100% local on your device, and no internet access is required. 

### Step 1: Allow AnkiDroid API
1. Open **AnkiDroid**.
2. Go to **Settings ⚙️** > **Advanced**.
3. Enable **AnkiDroid API** (allow third-party apps).

### Step 2: Open AnkiLock
1. Open the **AnkiLock** app.
2. Allow notification and storage permissions when asked.
3. Select which Anki deck you want to study.

### Step 3: Turn It On!
1. Flip the big switch at the top to **ON**.
2. Lock your phone — your first flashcard is now on your lock screen! 🎉

---

## 🎮 How to Use

| Button | What it does |
| :--- | :--- |
| **▶ Reveal / Hide** | Flips the card to show or hide the answer. |
| **\|◀ Again** | You forgot this card. Anki will show it to you again soon. |
| **▶\| Good** | You remembered it! Moves to the next card. |
| **Anki** | Opens AnkiDroid if you want to edit or browse your deck. |

---

## 🛠️ Developer Notes & Architecture

For developers contributing to or modifying AnkiLock:

### 🏗️ Architecture Overview

- **`CardSessionManager.kt`**: The single source of truth for active card state, reveal status, and due count stats. Any state mutation (revealing or grading a card) broadcasts updates synchronously across all 4 surfaces:
  1. **Lock Screen Media Session** (`AnkiNotificationService`)
  2. **Notification Drawer** (`AnkiNotificationService`)
  3. **Home Screen Widget** (`AnkiAppWidgetProvider`)
  4. **In-App Live Card Preview** (`MainActivity`)

- **`AnkiDroidHelper.kt`**: Handles IPC with AnkiDroid's `ContentProvider` (`com.ichi2.anki.provider`) to fetch due cards, parse note fields, extract card media images, and record card answers (`1 = Again`, `3 = Good`).

- **`MediaArtworkGenerator.kt` & `RubyTextRenderer.kt`**: High-performance Canvas rendering engine that generates 800×800 pixel-perfect card graphics. It handles:
  - Japanese Furigana/Ruby tokenization and vertical alignment above Kanji.
  - Dynamic vertical centering of questions, sentences, translations, and answers.
  - Background rendering: custom wallpapers, RenderScript gaussian blur, dimming overlays, and sunset/cyberpunk gradient shaders.

- **`AnkiNotificationService.kt`**: A foreground service maintaining an Android `MediaSessionCompat`. It exposes standard playback actions (`ACTION_PREVIOUS`, `ACTION_PLAY_PAUSE`, `ACTION_NEXT`) so Android's native lock screen media controls trigger Anki reviews seamlessly.

### 📦 Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Background Tasks**: Android WorkManager & Foreground Services
- **Minimum SDK**: Android 8.0 (API 26) / Target SDK: Android 14+ (API 34)

### 🔨 Build & Install
```powershell
# Build debug APK
./gradlew assembleDebug

# Install and launch on connected device
./gradlew installDebug
adb shell am start -n com.ankilock/.MainActivity
```
