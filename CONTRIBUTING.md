# Contributing to Kiro Sonnet Player

Thank you for your interest in contributing to Kiro Sonnet Player! We welcome contributions from the community.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Coding Standards](#coding-standards)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Pull Request Process](#pull-request-process)
- [Testing Guidelines](#testing-guidelines)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)

## 📜 Code of Conduct

### Our Standards

- **Be Respectful**: Treat everyone with respect and kindness
- **Be Collaborative**: Work together and help each other
- **Be Professional**: Keep discussions focused and constructive
- **Be Inclusive**: Welcome people of all backgrounds and experience levels

### Unacceptable Behavior

- Harassment, discrimination, or offensive comments
- Trolling, insulting, or derogatory remarks
- Personal or political attacks
- Publishing others' private information

## 🚀 Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/your-username/kiro-sonnet-player.git
   cd kiro-sonnet-player
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/yourusername/kiro-sonnet-player.git
   ```
4. **Create a branch** for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## 🛠️ Development Setup

### Prerequisites

- JDK 17 or higher
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK with API level 35
- Git

### Initial Setup

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Build the project to ensure everything works:
   ```bash
   ./gradlew build
   ```
4. Run on an emulator or physical device:
   ```bash
   ./gradlew installDebug
   ```

## 🤝 How to Contribute

### Types of Contributions

- **Bug Fixes**: Fix existing issues
- **Features**: Add new functionality
- **Documentation**: Improve docs and examples
- **Performance**: Optimize code and improve efficiency
- **Tests**: Add or improve test coverage
- **UI/UX**: Enhance user interface and experience

## 💻 Coding Standards

### Kotlin Style Guide

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Use meaningful variable and function names
- Add documentation for public APIs

### Code Organization

```kotlin
// Good: Clear, organized structure
class AudioPlayer @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val sessionManager: MediaSessionManager
) {
    fun play(track: Track) {
        // Implementation
    }
}

// Bad: Poor organization
class AudioPlayer {
    var player: ExoPlayer? = null
    fun doStuff() { ... }
}
```

### Android Best Practices

- **Use Jetpack Compose** for all UI components
- **Follow MVVM architecture** with ViewModels
- **Use Hilt** for dependency injection
- **Prefer Kotlin Coroutines** over callbacks
- **Use sealed classes** for state management
- **Implement proper lifecycle handling**

### Compose Guidelines

```kotlin
// Good: Composable with clear responsibilities
@Composable
fun TrackListItem(
    track: Track,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementation
}

// Bad: Doing too much in one composable
@Composable
fun Everything() {
    // Avoid this
}
```

### Naming Conventions

- **Classes**: PascalCase (`AudioPlayer`, `TrackViewModel`)
- **Functions**: camelCase (`playTrack()`, `getCurrentPosition()`)
- **Variables**: camelCase (`trackList`, `isPlaying`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_VOLUME`, `DEFAULT_BUFFER_SIZE`)
- **Composables**: PascalCase (`PlayerScreen()`, `TrackList()`)

## 📝 Commit Message Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/) for automated changelog generation and semantic versioning.

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code style changes (formatting, missing semicolons, etc.)
- `refactor:` - Code refactoring (no feature change or bug fix)
- `perf:` - Performance improvements
- `test:` - Adding or updating tests
- `build:` - Build system or dependency changes
- `ci:` - CI/CD changes
- `chore:` - Other changes that don't modify src or test files

### Scope (Optional)

- `player` - Audio player functionality
- `ui` - User interface
- `api` - API changes
- `deps` - Dependencies

### Examples

```bash
# Feature
feat(player): add shuffle mode functionality

# Bug fix
fix(ui): correct track duration display format

# Breaking change
feat!: redesign audio playback API

BREAKING CHANGE: The playTrack() method now requires a PlaybackConfig parameter

# Multiple paragraphs
fix(player): resolve memory leak in background playback

The ExoPlayer instance was not being properly released when
the activity was destroyed. This commit ensures proper cleanup
in the ViewModel's onCleared() method.

Fixes #123
```

## 🔄 Pull Request Process

### Before Submitting

1. **Update your branch** with the latest upstream changes:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run tests** and ensure they pass:
   ```bash
   ./gradlew test
   ```

3. **Run lint checks**:
   ```bash
   ./gradlew lintDebug
   ```

4. **Build the project**:
   ```bash
   ./gradlew assembleDebug
   ```

5. **Test on a device/emulator** to verify your changes

### Submitting Your PR

1. **Push your branch**:
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create a Pull Request** on GitHub

3. **Fill out the PR template** completely:
   - Clear title following commit conventions
   - Description of changes
   - Screenshots/videos for UI changes
   - Related issue numbers

4. **Request review** from maintainers

### PR Title Format

```
feat(player): add repeat mode functionality
fix(ui): resolve playback controls alignment issue
docs: update installation instructions
```

### PR Checklist

- [ ] Code follows the project's style guidelines
- [ ] Self-review of code completed
- [ ] Comments added for complex logic
- [ ] Documentation updated if needed
- [ ] No new warnings generated
- [ ] Tests added/updated for changes
- [ ] All tests passing
- [ ] Lint checks passing
- [ ] UI changes tested on multiple screen sizes (if applicable)

### Review Process

- Maintainers will review your PR within a few days
- Address any requested changes
- Once approved, maintainers will merge your PR
- Your contribution will be included in the next release

## 🧪 Testing Guidelines

### Writing Tests

- Write unit tests for business logic
- Write UI tests for Compose components
- Aim for meaningful test coverage
- Use descriptive test names

### Running Tests

```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests AudioPlayerTest

# With coverage
./gradlew testDebugUnitTestCoverage
```

### Test Structure

```kotlin
@Test
fun `playTrack should update playback state to playing`() {
    // Given
    val track = Track(id = "1", title = "Test Track")
    
    // When
    viewModel.playTrack(track)
    
    // Then
    assertEquals(PlaybackState.PLAYING, viewModel.playbackState.value)
}
```

## 🐛 Reporting Bugs

### Before Reporting

1. **Search existing issues** to avoid duplicates
2. **Update to the latest version** to see if it's already fixed
3. **Test on a clean installation** if possible

### Bug Report Template

When creating a bug report, include:

- **Description**: Clear description of the bug
- **Steps to Reproduce**: Step-by-step instructions
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens
- **Screenshots**: If applicable
- **Environment**:
  - App version
  - Android version
  - Device model
  - Build type (debug/release)
- **Logs**: Relevant logcat output if available

## 💡 Suggesting Features

### Feature Request Template

When suggesting a feature, include:

- **Problem**: What problem does this solve?
- **Solution**: Your proposed solution
- **Alternatives**: Alternative solutions considered
- **Additional Context**: Mockups, examples, references
- **Use Case**: How would this be used?

### Before Suggesting

1. **Check existing issues** for similar requests
2. **Consider if it fits** the project's scope and goals
3. **Think about implementation** complexity

## 📞 Getting Help

- **Issues**: [GitHub Issues](https://github.com/yourusername/kiro-sonnet-player/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/kiro-sonnet-player/discussions)

## 🙏 Thank You

Your contributions make this project better! We appreciate your time and effort.

---

**Happy Coding! 🎵**
