---
name: Release Notes
about: Template for release notes generation
title: 'Release v[VERSION]'
labels: 'release'
---

## 🎉 What's New in v[VERSION]

### ✨ New Features
<!-- List new features added in this release -->
- 

### 🐛 Bug Fixes
<!-- List bugs fixed in this release -->
- 

### 🔧 Improvements
<!-- List improvements and enhancements -->
- 

### ⚡ Performance
<!-- List performance optimizations -->
- 

### 📚 Documentation
<!-- List documentation updates -->
- 

### 🏗️ Technical Changes
<!-- List internal/technical changes -->
- 

---

## 📦 Installation

Download the APK below and install it on your Android device (Android 14+).

**APK Size:** [SIZE]

### First-time Installation
1. Download the APK file
2. Enable "Install from Unknown Sources" in Settings
3. Open the APK and follow installation prompts

### Updating from Previous Version
1. Download the new APK
2. Install over existing version (your data will be preserved)

---

## 🔐 Security

This release is signed with our official release key. Verify the signature:

```bash
apksigner verify --verbose kiro-sonnet-player-v[VERSION].apk
```

**SHA-256 Fingerprint:** [FINGERPRINT]

---

## 📋 Build Information

- **Version Name:** [VERSION]
- **Version Code:** [VERSION_CODE]
- **Min SDK:** 34 (Android 14)
- **Target SDK:** 35
- **Build Type:** Release (Optimized with ProGuard)
- **Supported ABIs:** arm64-v8a, armeabi-v7a, x86_64

---

## 🔄 Migration Notes

<!-- Any breaking changes or migration steps required -->

No migration steps required for this release.

---

## 🐛 Known Issues

<!-- List any known issues in this release -->

None at this time.

---

## 📝 Changelog

<!-- Full commit log since last release -->

**Full Changelog:** https://github.com/yourusername/kiro-sonnet-player/compare/v[PREVIOUS_VERSION]...v[VERSION]

---

## 🙏 Contributors

<!-- Acknowledge contributors -->

Thank you to all contributors who made this release possible!

---

## 💬 Feedback

Found a bug or have a feature request? Please [open an issue](https://github.com/yourusername/kiro-sonnet-player/issues).

---

**Enjoy the new release! 🎵**
