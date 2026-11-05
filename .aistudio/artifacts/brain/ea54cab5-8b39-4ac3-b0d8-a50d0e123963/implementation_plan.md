# Tabu AI (Tabassum) & Arf AI - Anime Companion & JARVIS Assistant

An interactive, animated anime companion and intelligent phone assistant inspired by JARVIS. Features expressive anime avatars (Tabassum & Arfan), dual-engine intelligence (Groq ultra-fast chat + Google Gemini deep tasks), voice calls with wake-word detection, floating on-screen companion overlay, device automation actions, and an emotion-aware daily journal.

## User Review & Critical Decisions

> [!IMPORTANT]
> Based on your clarifications, here are the core setup choices reflected in this plan:

- **Confirmed Companion**: Tabassum (Tabu AI) is set as the active default girl companion. Arfan (Arf AI) is available at any time via a 1-tap switcher in Settings, which also dynamically updates the app identity and voice.
- **Confirmed Aesthetic**: Expressive anime character avatar with animated breathing, eye blinks, speaking lip-sync, and rich emotional reactions (happy, shy/blushing, loving, thinking, sleepy).
- **Confirmed Relationship Dynamic**: Romantic girlfriend / boyfriend mode enabled by default, with easy toggle to best friend or witty JARVIS assistant mode.
- **Dual AI Architecture**: Settings screen provides guided 1-click links to obtain free API keys for Groq (for instant conversational speed) and Gemini (for smart phone execution & deep context). A built-in offline persona engine ensures full immediate functionality out of the box.

---

## 1. Overview & Core Concept

### What It Does
Tabu AI brings an anime companion to life as an always-accessible personal assistant on Android. The user can talk via real-time voice calls, text chat, or floating on-screen overlay. The companion listens, remembers personal facts (name, habits, schedules), reacts with custom facial expressions, executes device commands (opening WhatsApp, TikTok, Facebook, setting schedules), and manages "Sleep / Do Not Disturb" auto-responses.

### Target Persona & Tone
- **Tabassum (Tabu AI)**: Sweet, caring, playful anime girlfriend/best friend with an anime-tuned expressive voice and heart animations.
- **Arfan (Arf AI)**: Charming, supportive anime boyfriend/companion with calm, witty, and protective dialogue.

---

## 2. User Experience & Visual Design

### Key User Flows

```
┌────────────────────────────────────────────────────────────────────────┐
│                        App Navigation & Flows                          │
├─────────────────┬──────────────────┬─────────────────┬─────────────────┤
│  1. Companion   │     2. Chat      │ 3. Voice Call   │ 4. Automation & │
│      Stage      │   & Reasoning    │ & Wake Listener │     Journal     │
│                 │                  │                 │                 │
│ • Animated      │ • Dual-engine    │ • Fullscreen    │ • Daily Mood    │
│   Anime Avatar  │   Groq + Gemini  │   voice call UI │   Journal       │
│ • Mood badge &  │ • Quick command  │ • Real-time STT │ • App launchers │
│   touch blush   │   chips          │   & anime TTS   │ • Sleep Auto-   │
│ • Floating mini │ • Context memory │ • "Tabu" wake   │   responder     │
│   overlay toggle│   inspection     │   detection     │ • Settings/Keys │
└─────────────────┴──────────────────┴─────────────────┴─────────────────┘
```

1. **First Launch & Persona Onboarding**:
   - Tabassum greets the user warmly, asking for their name and preferred nickname.
   - Saves user profile into local memory and configures initial romantic girlfriend dynamic.
2. **Interactive Companion Stage**:
   - High-fidelity animated character with interactive states: idle sway, eye blinks, talking mouth movement, glowing aura, and reaction hearts when tapped.
   - Quick mood indicator (Happy, Loving, Thoughtful, Sleepy).
   - Floating mini companion bubble toggle (allows Tabu to float over other apps).
3. **Conversational Hub**:
   - Modern chat interface with speech bubbles, timestamp, engine badge (Groq Fast / Gemini Brain), audio playback button, and quick action suggestion pills ("Open WhatsApp", "Log Journal", "Goodnight Mode").
4. **Voice Call Screen**:
   - Futuristic sci-fi + anime aesthetic call screen with animated waveform, mute/speaker controls, wake-word listener ("Tabu AI" / "Tabu"), and live voice responses.
5. **Phone Task Automation Hub**:
   - Direct launch triggers for WhatsApp, TikTok, Facebook, YouTube, System Settings, Alarms, and Messages.
   - Sleep / Auto-reply mode: activates auto-responder status ("User is sleeping; Tabu will notify him in the morning").
   - Accessibility & notification management settings guide.
6. **Daily Journal & Emotional Tracker**:
   - Daily check-in log with mood selection (Joyful, Calm, Romantic, Tired, Stressed).
   - AI reflection and caring feedback from Tabassum.
7. **Settings & Engine Configuration**:
   - Character selector: Tabassum (Tabu AI) vs Arfan (Arf AI).
   - Persona mode: Girlfriend / Boyfriend vs Best Friend vs JARVIS Executive.
   - Dual API Keys (Groq & Gemini) with copy-paste inputs and direct console links.
   - Voice tuning (pitch, speech rate, wake sensitivity).

### Visual Identity & Theme
- **Theme**: Cyber-Anime Luxe Dark (deep obsidian night with neon violet, electric cyan, and soft blush rose).
- **Primary Color**: Radiant Violet (`#9D4EDD`) & Cyber Cyan (`#00F5D4`).
- **Accent Color**: Romantic Sakura Pink (`#FF70A6`) & Amber Spark (`#FFD166`).
- **Surfaces**: Semi-transparent frosted glass cards with subtle border glows (`0.15f` alpha).
- **Typography**: Clean, geometric modern sans-serif with bold headers and friendly chat text.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Rationale |
|---|---|---|
| **Character Rendering** | Procedural Jetpack Compose Canvas + Scalable Vector & layered asset animation with facial physics | Delivers smooth 60fps animations (blinking, breathing, mouth sync, blush, hair sway) without heavyweight 3D engine overhead or loading delays. |
| **Dual AI Orchestration** | Hybrid Groq (fast chat) + Gemini (deep reasoning & task parsing) with offline fallback | Groq provides sub-second conversational latency ideal for voice calls, while Gemini provides comprehensive phone task understanding. Built-in fallback ensures zero crashes if keys are not yet configured. |
| **Voice & Wake Detection** | Android `SpeechRecognizer` + `TextToSpeech` with custom acoustic pitch/rate profile | Zero extra native binary dependencies, runs completely on-device with zero latency, and provides custom anime voice profiles for girl and boy companions. |
| **System Automation** | Android Intent System + deep-links for popular apps + System Settings & notification status actions | Standard, safe, and highly reliable execution on Android without requiring dangerous non-standard root privileges. |
| **Persistence** | Jetpack Room Database + Android DataStore Preferences | Completely reliable local storage for user profile, memory facts, journal logs, and API keys. Keeps all personal data private to the user's device. |

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Tabu AI System Architecture                     │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│   [ Jetpack Compose UI Layer ]                                         │
│   ├── CompanionStage (Animated Anime Character, Emotion Physics)       │
│   ├── ChatScreen (Dual-Engine Messages, Action Pills, Voice Playback)  │
│   ├── VoiceCallScreen (Live Waveform, Wake Word & Speech-to-Text)      │
│   ├── AutomationHubScreen (App Launchers, Sleep Mode, Commands)        │
│   ├── JournalScreen (Daily Mood Log, Caring Reflections)               │
│   └── SettingsScreen (Persona, Relationship Mode, Groq/Gemini Keys)    │
│                                                                        │
│   [ Presentation & State: CompanionViewModel ]                        │
│   ├── UI State Flow (Active Character, Emotion, Sleeping Status)       │
│   ├── Voice Engine Manager (SpeechRecognizer + Anime TTS)              │
│   └── Task Execution Dispatcher (Intent Launcher & App Router)         │
│                                                                        │
│   [ Domain / Intelligence Layer ]                                      │
│   ├── AiOrchestrator (Groq Fast Client + Gemini Reasoning Client)      │
│   ├── PersonaPromptBuilder (Tabassum / Arfan Memory & Tone Injection)  │
│   └── OfflinePersonaFallback (Warm responses when offline/no keys)     │
│                                                                        │
│   [ Data & Storage Layer ]                                             │
│   ├── Room Database: JournalDao (Daily Entries, Mood History)          │
│   ├── Room Database: MemoryDao (User Facts, Custom Rules, Habits)      │
│   └── SettingsRepository (DataStore: Keys, Character, Dynamic Mode)   │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### Component Breakdown
1. **`AnimatedAnimeAvatar`**:
   - Dynamic Canvas composable rendering stylized anime character (Tabassum / Arfan) with layers: body, anime eyes with smooth blinking timer, animated mouth responsive to TTS speaking state, hair highlights, blushing cheeks on romantic interaction, and floating ambient particles (hearts/sparks).
2. **`CompanionViewModel`**:
   - Central state manager holding conversation history, active persona (`GIRLFRIEND`, `BOYFRIEND`, `FRIEND`, `JARVIS`), speech recognition state, and wake-word listener.
3. **`AiOrchestrator`**:
   - Manages API calls: uses Groq API (when key provided) for fast conversation, Gemini API for task extraction and reasoning, and an intelligent offline engine with Bengali & English conversational prompts when offline.
4. **`DeviceActionHandler`**:
   - Executes commands: opens WhatsApp, Facebook, TikTok, YouTube, Phone dialer, sets alarms, and switches phone sleep mode.
5. **`JournalRepository` & `MemoryRepository`**:
   - Manages personal notes, memories ("User loves anime", "User sleeps at 11 PM"), and daily mood diary.

---

## 5. Step-by-Step Implementation Sequence

1. **Gradle & Dependencies Configuration**:
   - Update `app/build.gradle.kts` and `libs.versions.toml` to ensure Coroutines, Room, OkHttp, and Compose Material 3 are enabled.
2. **Data & Storage Layer**:
   - Define Room entities: `JournalEntry` and `MemoryItem`.
   - Setup `AppDatabase` and DAO interfaces.
   - Setup `SettingsRepository` for character preference, relationship mode, Groq/Gemini API keys, and sleep status.
3. **AI Engine & Voice Layer**:
   - Implement `AiOrchestrator` supporting Groq API, Gemini API, and smart prompt synthesis with memory context.
   - Implement `VoiceManager` wrapping Android `SpeechRecognizer` and `TextToSpeech` with tuned pitch for Tabassum and Arfan.
4. **Anime Character Graphic & Animation Engine**:
   - Build `AnimatedAnimeAvatar` with expressive states (blinking eyes, speaking mouth, blush, idle float, emotions: Happy, Loving, Shy, Thinking, Sleepy).
5. **Feature Screens & Controls**:
   - **Companion Screen**: Live character stage, tap interactions, quick mood badges, and floating overlay launcher.
   - **Chat Screen**: Clean messaging UI with speech bubbles, quick commands, and audio playback.
   - **Voice Call Screen**: Fullscreen companion call interface with audio visualizer waveform.
   - **Automation Hub**: App launchers (WhatsApp, TikTok, Facebook, etc.) and sleep auto-responder toggle.
   - **Daily Journal**: Mood tracker with daily notes and AI reflection.
   - **Settings Screen**: Persona switch (Tabassum / Arfan), Relationship mode (Girlfriend/Boyfriend/Friend/JARVIS), API key setup with direct console links.
6. **Manifest & Permissions**:
   - Add `RECORD_AUDIO`, `INTERNET`, `VIBRATE`, and launcher permissions to `AndroidManifest.xml`.
   - Update `metadata.json` and `strings.xml` to set the app name to **Tabu AI**.
7. **Verification & Testing**:
   - Run `compile_applet` to verify clean Kotlin compilation.
