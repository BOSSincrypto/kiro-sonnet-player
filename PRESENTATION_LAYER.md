# Kiro Sonnet Player - UI Components

Ultra-minimalist, high-performance Jetpack Compose video player UI following Material Design 3.

## Architecture

The presentation layer follows MVVM pattern with clean separation of concerns:

```
presentation/
├── player/
│   ├── PlayerScreen.kt         # Main video player UI
│   ├── PlayerViewModel.kt      # Player state management
│   ├── PlayerUiState.kt        # UI state definitions
│   └── PlaybackService.kt      # Media3 background playback
├── bookmarks/
│   ├── BookmarksSheet.kt       # Bottom sheet for bookmarks
│   ├── BookmarksViewModel.kt   # Bookmarks state management
│   └── BookmarksUiState.kt     # Bookmarks UI state
├── settings/
│   ├── SettingsScreen.kt       # Settings UI
│   ├── SettingsViewModel.kt    # Settings state management
│   └── SettingsUiState.kt      # Settings UI state
├── theme/
│   ├── Color.kt                # Material 3 dark theme colors
│   ├── Theme.kt                # Theme configuration
│   └── Type.kt                 # Typography definitions
├── util/
│   └── ComposeUtils.kt         # Performance utilities
└── Navigation.kt               # Compose Navigation setup
```

## Features

### PlayerScreen

Full-screen video player with custom gesture controls:

**Gesture Controls:**
- **Single tap**: Show/hide controls overlay
- **Double tap**: Play/pause video
- **Swipe left/right**: Seek backward/forward (10 seconds)
- **Swipe up/down (left side)**: Adjust brightness
- **Swipe up/down (right side)**: Adjust volume

**Controls Overlay:**
- Auto-hides after 3 seconds of inactivity
- Play/pause button (center)
- Video seek bar with buffering indicator
- Current position / Total duration display
- Playback speed selector (0.5x - 2.0x)
- Settings navigation button
- Bookmarks button
- Picture-in-Picture mode button

**Visual Feedback:**
- Brightness indicator (left side during adjustment)
- Volume indicator (right side during adjustment)
- Loading spinner during buffering
- Smooth fade animations for overlays

### BookmarksSheet

Modal bottom sheet for managing video bookmarks:

**Features:**
- Add bookmark at current timestamp
- Custom bookmark titles
- Tap to jump to timestamp
- Delete bookmarks
- Sorted by timestamp
- Empty state message

### SettingsScreen

Comprehensive settings for playback customization:

**Sections:**
1. **Playback**
   - Default playback speed (0.5x - 2.0x)

2. **Performance**
   - Buffer size (Small/Medium/Large)
   - Configurable buffer windows

3. **Storage**
   - Cache size display
   - Clear cache option

4. **About**
   - App version
   - Open source licenses

## Performance Optimizations

### 1. Minimal Recomposition

- **Immutable state classes**: All UI state classes use `@Immutable` annotation
- **Derived state**: Uses `derivedStateOf` for computed values
- **Remember**: Proper use of `remember` for expensive operations
- **Throttling**: Position updates throttled to reduce recomposition frequency

### 2. Efficient Rendering

- **Hardware acceleration**: Enabled in manifest
- **View recycling**: AndroidView properly configured
- **Lazy loading**: LazyColumn for bookmark lists
- **Conditional composition**: AnimatedVisibility for overlays

### 3. Smooth Animations

- **60 FPS target**: All animations use Material 3 defaults
- **Fade transitions**: fadeIn/fadeOut for overlays
- **Slide transitions**: slideInVertically/slideOutVertically for controls
- **No jank**: Gesture handling in separate pointerInput blocks

### 4. Memory Management

- **Singleton ExoPlayer**: Injected via Hilt, reused across screens
- **Proper cleanup**: ViewModels clean up listeners in onCleared()
- **Efficient state updates**: StateFlow with update() for atomic changes

## State Management

### PlayerViewModel

Manages video playback state and user interactions:

```kotlin
data class PlayerUiState(
    val isPlaying: Boolean,
    val currentPosition: Long,
    val duration: Long,
    val bufferedPosition: Long,
    val playbackSpeed: Float,
    val isControlsVisible: Boolean,
    val volume: Float,
    val brightness: Float
)
```

**Key responsibilities:**
- Player listener integration
- Position updates (10Hz for smooth seeking)
- Auto-hide controls scheduling
- Brightness/volume adjustments
- Picture-in-Picture mode

### BookmarksViewModel

Manages bookmark state:

```kotlin
data class BookmarksUiState(
    val bookmarks: List<Bookmark>,
    val isLoading: Boolean,
    val error: String?
)
```

**Operations:**
- Add/delete/update bookmarks
- Jump to timestamp
- Sorted by position

### SettingsViewModel

Manages app settings:

```kotlin
data class SettingsUiState(
    val defaultPlaybackSpeed: Float,
    val bufferSize: BufferSize,
    val cacheSize: Long
)
```

**Operations:**
- Load/save preferences (TODO: DataStore integration)
- Calculate cache size
- Clear cache

## Dependency Injection

ExoPlayer provided as singleton via Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer
}
```

## Material Design 3

**Dark Theme:**
- Primary: #BB86FC (Purple)
- Secondary: #03DAC6 (Teal)
- Background: #121212 (True black for OLED)
- Surface: #121212

**Typography:**
- Font Family: System default (Roboto)
- Proper hierarchy with Material 3 type scale

## Navigation

Simple navigation graph with two destinations:

```kotlin
sealed class Screen(val route: String) {
    object Player : Screen("player")
    object Settings : Screen("settings")
}
```

Bookmarks shown as overlay on Player screen (not a separate destination).

## TODO

Integration points requiring implementation:

1. **DataStore Preferences**
   - Save/load default playback speed
   - Save/load buffer size preferences

2. **Bookmark Persistence**
   - Room database for bookmarks
   - Repository pattern integration

3. **Video URI Handling**
   - Intent handling for opened videos
   - Local file picker integration

4. **ExoPlayer Configuration**
   - Apply buffer size settings
   - Handle playback errors
   - Network state monitoring

## Testing Recommendations

### Performance Testing

1. **Frame Rate**: Use GPU rendering profiler to ensure 60 FPS during:
   - Gesture interactions (seek, brightness, volume)
   - Control overlay animations
   - Seek bar dragging

2. **Memory**: Monitor memory usage during:
   - Extended playback sessions
   - Rapid seek operations
   - Navigation between screens

3. **Responsiveness**: Measure input latency for:
   - Single/double tap gestures
   - Swipe gestures
   - Button presses

### UI Testing

1. **Gesture conflicts**: Verify gestures don't interfere
2. **Auto-hide timing**: Controls hide at correct intervals
3. **PiP mode**: Proper behavior when entering/exiting
4. **Orientation changes**: State preservation

## License

Apache 2.0
