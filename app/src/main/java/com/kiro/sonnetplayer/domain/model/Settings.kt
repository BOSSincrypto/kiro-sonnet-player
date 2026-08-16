package com.kiro.sonnetplayer.domain.model

data class Settings(
    val defaultPlaybackSpeed: Float = 1.0f,
    val bufferSize: BufferSize = BufferSize.MEDIUM,
    val autoPlay: Boolean = true,
    val rememberPosition: Boolean = true,
    val hardwareAcceleration: Boolean = true,
    val preferredQuality: VideoQuality = VideoQuality.AUTO
) {
    enum class BufferSize(val minBufferMs: Int, val maxBufferMs: Int) {
        SMALL(15_000, 30_000),
        MEDIUM(30_000, 50_000),
        LARGE(50_000, 100_000)
    }

    enum class VideoQuality {
        AUTO,
        LOW,
        MEDIUM,
        HIGH,
        ULTRA_HD
    }
}
