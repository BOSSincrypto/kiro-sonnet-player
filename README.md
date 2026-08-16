# 🎬 Kiro Sonnet Player

[![Android](https://img.shields.io/badge/Platform-Android_14+-3DDC84?logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/YOUR_USERNAME/kiro-sonnet-player)](https://github.com/YOUR_USERNAME/kiro-sonnet-player/releases)

**Ultra-optimized Android video player** with hardware acceleration, built for zero-lag performance and smooth playback.

---

## ✨ Features

### 🚀 Performance Optimized
- **Hardware-accelerated playback** with ExoPlayer (Media3)
- **Zero-lag controls** - instant play/pause and seeking
- **50MB smart buffering** for seamless playback
- **Network caching** for remote videos (200MB cache)
- **R8 full mode** with aggressive optimization
- Optimized for **60 FPS** smooth UI

### 🎮 Intuitive Gesture Controls
- **Single tap** - Show/hide controls
- **Double tap** - Play/pause
- **Swipe left/right** - Seek backward/forward (10 seconds)
- **Swipe up/down (left side)** - Brightness control
- **Swipe up/down (right side)** - Volume control

### 🎯 Player Features
- **Variable playback speed** (0.25x - 3.0x)
- **Picture-in-Picture (PiP)** mode
- **Bookmarks/timestamps** for quick navigation
- Support for **MP4, MKV, AVI, WebM** formats
- Local files and network URLs
- Auto-hide controls with smooth animations

### 🎨 Modern UI/UX
- **Material Design 3** dark theme
- **Jetpack Compose** native UI
- Minimalist interface
- Smooth animations

---

## 📱 Screenshots

_Coming soon - Add your screenshots here_

---

## 🏗️ Architecture

Built with **Clean Architecture** principles:

```
app/
├── data/               # Data layer
│   ├── local/         # DataStore, local data sources
│   ├── player/        # PlayerManager with ExoPlayer
│   └── repository/    # Repository implementations
├── domain/            # Domain layer
│   ├── model/         # Domain models (Video, Bookmark, PlayerState)
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Business logic use cases
├── presentation/      # Presentation layer
│   ├── player/        # Player screen & ViewModel
│   ├── settings/      # Settings screen & ViewModel
│   ├── bookmarks/     # Bookmarks management
│   └── theme/         # Material3 theme configuration
└── di/               # Dependency Injection (Hilt)
```

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin 2.0.20 |
| **Min SDK** | 34 (Android 14) |
| **Target SDK** | 35 (Android 15) |
| **Architecture** | Clean Architecture (MVVM) |
| **UI Framework** | Jetpack Compose + Material3 |
| **Video Player** | ExoPlayer (Media3 1.4.1) |
| **DI** | Hilt 2.52 |
| **Async** | Kotlin Coroutines + Flow |
| **Storage** | DataStore Preferences |
| **Navigation** | Jetpack Navigation Compose |

---

## 🔧 Build Optimizations

This player is built for **maximum performance**:

- ✅ **R8 full mode** enabled (5 optimization passes)
- ✅ **ProGuard** rules for ExoPlayer & Hilt
- ✅ **Resource shrinking** enabled
- ✅ **Hardware acceleration** enforced
- ✅ **ABI splits** for optimized APK size (arm64-v8a, armeabi-v7a, x86_64)
- ✅ **Aggressive obfuscation** and repackaging
- ✅ **Gradle build cache** and parallel execution
- ✅ **No debug symbols** in release builds

---

## 📦 Download & Installation

### Latest Release
Download the latest APK from [Releases](https://github.com/YOUR_USERNAME/kiro-sonnet-player/releases/latest)

### Installation Steps
1. Download the APK file
2. Enable "Install from Unknown Sources" in Settings
3. Open the APK and install
4. Grant storage permissions when prompted

---

## 🏗️ Building from Source

### Prerequisites
- Android Studio Koala | 2024.1.1 or later
- JDK 17
- Android SDK 35

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/kiro-sonnet-player.git
cd kiro-sonnet-player

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires keystore configuration)
./gradlew assembleRelease
```

### Setup Keystore for Signing (Optional)
```bash
# Generate keystore
./generate-keystore.sh

# Follow the prompts to create keystore.properties
```

---

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Quick Start
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'feat: add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

**Note:** Use [Conventional Commits](https://www.conventionalcommits.org/) for automatic changelog generation.

---

## 🔒 Permissions

| Permission | Purpose |
|------------|---------|
| `READ_MEDIA_VIDEO` | Access video files on device |
| `INTERNET` | Stream videos from network URLs |

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [ExoPlayer](https://github.com/google/ExoPlayer) - Powerful media player library
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Material Design 3](https://m3.material.io/) - Design system

---

## 📞 Contact & Support

- **Issues:** [GitHub Issues](https://github.com/YOUR_USERNAME/kiro-sonnet-player/issues)
- **Discussions:** [GitHub Discussions](https://github.com/YOUR_USERNAME/kiro-sonnet-player/discussions)

---

<p align="center">
  <sub>Built with ❤️ for smooth video playback</sub>
</p>

