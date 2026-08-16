package com.kiro.sonnetplayer.domain.model

data class PlayerSettings(
    val defaultPlaybackSpeed: Float = 1.0f,
    val bufferSize: BufferSize = BufferSize.MEDIUM,
    val autoHideControlsDelay: Long = 3000L,
    val seekInterval: Long = 10000L
)

enum class BufferSize(val minBufferMs: Int, val maxBufferMs: Int) {
    SMALL(15000, 30000),
    MEDIUM(30000, 50000),
    LARGE(50000, 80000)
}
