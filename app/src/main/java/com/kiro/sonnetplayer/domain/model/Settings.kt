package com.kiro.sonnetplayer.domain.model

data class Settings(
    val defaultPlaybackSpeed: Float = 1.0f,
    val bufferSize: BufferSize = BufferSize.MEDIUM,
    val autoPlay: Boolean = true,
    val rememberPosition: Boolean = true
) {
    enum class BufferSize {
        SMALL,
        MEDIUM,
        LARGE
    }
}
