# 🚀 Development Setup Guide

Complete guide for setting up the Kiro Sonnet Player development environment.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Initial Setup](#initial-setup)
- [Building the Project](#building-the-project)
- [Running the App](#running-the-app)
- [Code Style](#code-style)
- [Testing](#testing)
- [Debugging](#debugging)
- [Common Issues](#common-issues)

## 📦 Prerequisites

### Required Software

1. **JDK 17 or higher**
   - Download from [Adoptium](https://adoptium.net/)
   - Verify installation: `java -version`

2. **Android Studio Hedgehog (2023.1.1) or later**
   - Download from [developer.android.com](https://developer.android.com/studio)
   - Recommended: Latest stable version

3. **Android SDK**
   - API Level 34 (Android 14) - Minimum
   - API Level 35 - Target
   - Install via Android Studio SDK Manager

4. **Git**
   - Download from [git-scm.com](https://git-scm.com/)
   - Verify installation: `git --version`

### Recommended Tools

- **ADB (Android Debug Bridge)** - Included with Android SDK
- **Scrcpy** - For device mirroring (optional)
- **Android Emulator** - For testing without physical device

## 🛠️ Initial Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/kiro-sonnet-player.git
cd kiro-sonnet-player
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select **File → Open**
3. Navigate to the cloned directory
4. Click **OK**

### 3. Sync Project

Android Studio will automatically:
- Download Gradle wrapper
- Sync Gradle files
- Download dependencies
- Index the project

Wait for "Gradle sync finished" in the bottom status bar.

### 4. Configure Android SDK

If prompted, install missing SDK components:

```
Tools → SDK Manager → SDK Platforms
```

Install:
- ✅ Android 14.0 (API 34)
- ✅ Android 15.0 (API 35)

```
Tools → SDK Manager → SDK Tools
```

Install:
- ✅ Android SDK Build-Tools 35
- ✅ Android Emulator
- ✅ Android SDK Platform-Tools

## 🔨 Building the Project

### Via Android Studio

1. **Build Menu → Make Project** (Ctrl+F9 / Cmd+F9)
2. Or click the hammer icon in the toolbar

### Via Command Line

#### Debug Build

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

#### Release Build

First, set up signing (see [Keystore Setup](#keystore-setup)), then:

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

#### Clean Build

```bash
./gradlew clean assembleDebug
```

### Build Variants

Access build variants:
1. **View → Tool Windows → Build Variants**
2. Select variant:
   - `debug` - Development builds
   - `release` - Production builds

## 📱 Running the App

### On Emulator

#### Create Emulator

1. **Tools → Device Manager**
2. Click **Create Device**
3. Select hardware (e.g., Pixel 7)
4. Select system image (Android 14/15)
5. Click **Finish**

#### Run App

1. Select emulator from device dropdown
2. Click **Run** (Shift+F10 / Ctrl+R)

### On Physical Device

#### Enable Developer Options

1. Go to **Settings → About Phone**
2. Tap **Build Number** 7 times
3. Go back to **Settings → System → Developer Options**
4. Enable **USB Debugging**

#### Connect Device

1. Connect via USB
2. Accept "Allow USB Debugging" prompt
3. Verify connection: `adb devices`

#### Run App

1. Select device from dropdown
2. Click **Run**

### Via Command Line

```bash
# Install debug build
./gradlew installDebug

# Install and run
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.kiro.sonnetplayer/.MainActivity
```

## 🎨 Code Style

### EditorConfig

The project includes `.editorconfig` for consistent formatting:
- 4 spaces for Kotlin
- UTF-8 encoding
- LF line endings
- Trim trailing whitespace

### Kotlin Style Guide

Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// ✅ Good
class AudioPlayer @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val repository: MusicRepository
) {
    fun play(track: Track) {
        exoPlayer.setMediaItem(track.toMediaItem())
        exoPlayer.play()
    }
}

// ❌ Bad
class AudioPlayer{
  var player:ExoPlayer?=null
  fun play(t:Track){player?.play()}
}
```

### Format Code

- **Code → Reformat Code** (Ctrl+Alt+L / Cmd+Opt+L)
- **Code → Optimize Imports** (Ctrl+Alt+O / Cmd+Opt+O)

### Lint

```bash
# Run lint checks
./gradlew lintDebug

# View results
open app/build/reports/lint-results-debug.html
```

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test Class

```bash
./gradlew test --tests AudioPlayerTest
```

### Run with Coverage

```bash
./gradlew testDebugUnitTestCoverage
```

View coverage report:
```
app/build/reports/coverage/test/debug/index.html
```

### In Android Studio

1. Right-click test file/class/method
2. Select **Run 'TestName'**
3. Or **Run 'TestName' with Coverage**

## 🐛 Debugging

### Breakpoints

1. Click line number gutter to set breakpoint
2. Run in debug mode (Shift+F9 / Ctrl+D)
3. Use debug controls to step through

### Logcat

View logs in Android Studio:
1. **View → Tool Windows → Logcat**
2. Filter by package: `com.kiro.sonnetplayer`

### Command Line Logs

```bash
# All logs
adb logcat

# Filter by tag
adb logcat -s AudioPlayer

# Filter by priority
adb logcat *:E  # Errors only

# Clear logs
adb logcat -c
```

### Layout Inspector

Inspect Compose UI:
1. **Tools → Layout Inspector**
2. Select running process
3. View hierarchy and properties

### Compose Preview

View Composables without running:
1. Add `@Preview` annotation
2. Build project
3. Preview appears in split view

```kotlin
@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {
    KiroSonnetPlayerTheme {
        PlayerScreen()
    }
}
```

## 🔐 Keystore Setup

### For Local Development

Generate a release keystore:

```bash
./generate-keystore.sh
```

Follow the prompts. This creates:
- `keystore.jks` - Signing key
- `keystore.properties` - Configuration

These files are git-ignored for security.

### For GitHub Actions

See [GitHub Secrets Setup Guide](.github/SECRETS_SETUP.md)

## 🚨 Common Issues

### Issue: Gradle Sync Failed

**Solution:**
```bash
./gradlew clean
# In Android Studio: File → Invalidate Caches → Invalidate and Restart
```

### Issue: SDK Not Found

**Solution:**
1. Set `ANDROID_HOME` environment variable
2. Point to SDK location (check in Android Studio → Settings → Android SDK)

```bash
# Linux/macOS
export ANDROID_HOME=$HOME/Android/Sdk

# Windows
setx ANDROID_HOME "C:\Users\YourName\AppData\Local\Android\Sdk"
```

### Issue: Device Not Detected

**Solution:**
```bash
# Restart ADB
adb kill-server
adb start-server

# Check devices
adb devices
```

### Issue: Out of Memory

**Solution:**
Increase Gradle memory in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
```

### Issue: Emulator Won't Start

**Solution:**
1. Enable virtualization in BIOS
2. Install Intel HAXM or AMD Hypervisor
3. Grant permissions if prompted

### Issue: Build Too Slow

**Solutions:**
1. Enable Gradle daemon (already enabled in `gradle.properties`)
2. Enable parallel builds
3. Use configuration cache
4. Exclude virus scanner from project directory

```properties
# gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
```

### Issue: Code Changes Not Reflected

**Solution:**
```bash
# Clean build
./gradlew clean assembleDebug

# Or use Android Studio
Build → Clean Project
Build → Rebuild Project
```

## 🔄 Keeping Up to Date

### Update Dependencies

Check for updates:
1. **Tools → Android SDK → SDK Tools**
2. **Project → Dependencies → Check for Updates**

### Update Gradle

```bash
./gradlew wrapper --gradle-version=8.5
```

### Sync with Upstream

```bash
git fetch upstream
git merge upstream/main
```

## 📚 Additional Resources

- [Android Developer Documentation](https://developer.android.com/docs)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs)
- [ExoPlayer Documentation](https://developer.android.com/guide/topics/media/exoplayer)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)

## 💬 Getting Help

- **GitHub Issues**: [Report bugs](https://github.com/yourusername/kiro-sonnet-player/issues)
- **Discussions**: [Ask questions](https://github.com/yourusername/kiro-sonnet-player/discussions)
- **Contributing**: See [CONTRIBUTING.md](CONTRIBUTING.md)

---

**Happy Coding! 🎵**
