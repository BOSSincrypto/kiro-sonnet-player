package com.kiro.sonnetplayer.presentation.player

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerEntryPoint {
    fun exoPlayer(): ExoPlayer
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    videoUri: String?,
    onNavigateToSettings: () -> Unit,
    onShowBookmarks: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    var showSpeedMenu by remember { mutableStateOf(false) }

    // Apply brightness to window
    LaunchedEffect(uiState.brightness) {
        activity?.let { viewModel.applyBrightnessToWindow(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video Surface
        VideoSurface(
            modifier = Modifier.fillMaxSize(),
            onSingleTap = {
                if (uiState.isControlsVisible) {
                    viewModel.onEvent(PlayerEvent.HideControls)
                } else {
                    viewModel.onEvent(PlayerEvent.ShowControls)
                }
            },
            onDoubleTap = {
                viewModel.onEvent(PlayerEvent.PlayPause)
            },
            onHorizontalDrag = { delta ->
                val seekDelta = (delta * 100).toLong() // Adjust sensitivity
                viewModel.onEvent(PlayerEvent.SeekBy(seekDelta))
            },
            onVerticalDragLeft = { delta ->
                // Brightness control
                val newBrightness = (uiState.brightness + delta * 0.01f).coerceIn(0f, 1f)
                viewModel.onEvent(PlayerEvent.SetBrightness(newBrightness))
            },
            onVerticalDragRight = { delta ->
                // Volume control
                val newVolume = (uiState.volume + delta * 0.01f).coerceIn(0f, 1f)
                viewModel.onEvent(PlayerEvent.SetVolume(newVolume))
            }
        )

        // Loading indicator
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Gesture feedback overlays
        AnimatedVisibility(
            visible = uiState.brightness >= 0f && uiState.isControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(32.dp)
        ) {
            BrightnessFeedback(brightness = uiState.brightness)
        }

        AnimatedVisibility(
            visible = uiState.isControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(32.dp)
        ) {
            VolumeFeedback(volume = uiState.volume)
        }

        // Playback controls overlay
        AnimatedVisibility(
            visible = uiState.isControlsVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            PlayerControls(
                uiState = uiState,
                onPlayPause = { viewModel.onEvent(PlayerEvent.PlayPause) },
                onSeek = { viewModel.onEvent(PlayerEvent.SeekTo(it)) },
                onSpeedClick = { showSpeedMenu = true },
                onSettingsClick = onNavigateToSettings,
                onBookmarksClick = onShowBookmarks,
                onPipClick = { viewModel.onEvent(PlayerEvent.TogglePictureInPicture) }
            )
        }

        // Speed selection menu
        if (showSpeedMenu) {
            SpeedSelectionMenu(
                currentSpeed = uiState.playbackSpeed,
                onSpeedSelected = { speed ->
                    viewModel.onEvent(PlayerEvent.SetPlaybackSpeed(speed))
                    showSpeedMenu = false
                },
                onDismiss = { showSpeedMenu = false }
            )
        }
    }
}

@Composable
private fun VideoSurface(
    modifier: Modifier = Modifier,
    onSingleTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onHorizontalDrag: (Float) -> Unit,
    onVerticalDragLeft: (Float) -> Unit,
    onVerticalDragRight: (Float) -> Unit
) {
    val context = LocalContext.current
    val viewModel: PlayerViewModel = hiltViewModel()

    var lastTapTime by remember { mutableStateOf(0L) }
    var dragStartX by remember { mutableStateOf(0f) }
    var dragStartY by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTapTime < 300) {
                            onDoubleTap()
                            lastTapTime = 0L
                        } else {
                            lastTapTime = currentTime
                            coroutineScope.launch {
                                delay(300)
                                if (lastTapTime == currentTime) {
                                    onSingleTap()
                                }
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartX = offset.x
                        dragStartY = offset.y
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        val horizontalDrag = abs(dragAmount.x)
                        val verticalDrag = abs(dragAmount.y)

                        when {
                            horizontalDrag > verticalDrag * 1.5f -> {
                                // Horizontal swipe - seek
                                onHorizontalDrag(dragAmount.x)
                            }
                            verticalDrag > horizontalDrag * 1.5f -> {
                                // Vertical swipe - brightness or volume
                                val isLeftSide = dragStartX < size.width / 2
                                if (isLeftSide) {
                                    onVerticalDragLeft(-dragAmount.y)
                                } else {
                                    onVerticalDragRight(-dragAmount.y)
                                }
                            }
                        }
                    }
                )
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                // Get ExoPlayer from DI via ViewModel
                val injector = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    PlayerEntryPoint::class.java
                )
                playerView.player = injector.exoPlayer()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PlayerControls(
    uiState: PlayerUiState,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSpeedClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onPipClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onBookmarksClick) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Bookmarks",
                    tint = Color.White
                )
            }
            IconButton(onClick = onPipClick) {
                Icon(
                    imageVector = Icons.Default.PictureInPicture,
                    contentDescription = "Picture in Picture",
                    tint = Color.White
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        // Center play/pause button
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier
                .align(Alignment.Center)
                .size(72.dp)
        ) {
            Icon(
                imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            // Seek bar
            VideoSeekBar(
                currentPosition = uiState.currentPosition,
                duration = uiState.duration,
                bufferedPosition = uiState.bufferedPosition,
                onSeek = onSeek
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time and speed control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatTime(uiState.currentPosition)} / ${formatTime(uiState.duration)}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )

                TextButton(onClick = onSpeedClick) {
                    Text(
                        text = "${uiState.playbackSpeed}x",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoSeekBar(
    currentPosition: Long,
    duration: Long,
    bufferedPosition: Long,
    onSeek: (Long) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0f) }

    val progress = if (isDragging) {
        dragPosition
    } else {
        if (duration > 0) currentPosition.toFloat() / duration else 0f
    }

    val bufferedProgress = if (duration > 0) bufferedPosition.toFloat() / duration else 0f

    Column {
        Slider(
            value = progress,
            onValueChange = { value ->
                isDragging = true
                dragPosition = value
            },
            onValueChangeFinished = {
                onSeek((dragPosition * duration).toLong())
                isDragging = false
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun BrightnessFeedback(brightness: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                Color.Black.copy(alpha = 0.6f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Brightness6,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${(brightness * 100).toInt()}%",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun VolumeFeedback(volume: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                Color.Black.copy(alpha = 0.6f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = when {
                volume == 0f -> Icons.Default.VolumeOff
                volume < 0.5f -> Icons.Default.VolumeDown
                else -> Icons.Default.VolumeUp
            },
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${(volume * 100).toInt()}%",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SpeedSelectionMenu(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Playback Speed") },
        text = {
            Column {
                speeds.forEach { speed ->
                    TextButton(
                        onClick = { onSpeedSelected(speed) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${speed}x")
                            if (speed == currentSpeed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
