package com.kiro.sonnetplayer.presentation.player

import androidx.compose.runtime.Immutable

@Immutable
data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val bufferedPosition: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isControlsVisible: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val volume: Float = 1.0f,
    val brightness: Float = -1f // -1 means system default
)

sealed interface PlayerEvent {
    data object PlayPause : PlayerEvent
    data object ShowControls : PlayerEvent
    data object HideControls : PlayerEvent
    data class SeekTo(val position: Long) : PlayerEvent
    data class SeekBy(val delta: Long) : PlayerEvent
    data class SetPlaybackSpeed(val speed: Float) : PlayerEvent
    data class SetVolume(val volume: Float) : PlayerEvent
    data class SetBrightness(val brightness: Float) : PlayerEvent
    data object TogglePictureInPicture : PlayerEvent
}
