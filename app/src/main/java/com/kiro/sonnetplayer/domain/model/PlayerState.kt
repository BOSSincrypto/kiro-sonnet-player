package com.kiro.sonnetplayer.domain.model

/**
 * Represents the current state of the video player.
 * Optimized for fast state updates with minimal allocations.
 */
data class PlayerState(
    val position: Long = 0L, // in milliseconds
    val duration: Long = 0L, // in milliseconds
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val isPictureInPicture: Boolean = false,
    val currentVideoId: String? = null,
    val error: String? = null
) {
    val progress: Float
        get() = if (duration > 0) position.toFloat() / duration else 0f

    companion object {
        val IDLE = PlayerState()

        // Supported playback speeds for quick access
        val SUPPORTED_SPEEDS = floatArrayOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.5f, 3.0f)
    }
}
