package com.kiro.sonnetplayer.presentation.settings

import androidx.compose.runtime.Immutable
import com.kiro.sonnetplayer.domain.model.Settings.BufferSize

@Immutable
data class SettingsUiState(
    val defaultPlaybackSpeed: Float = 1.0f,
    val bufferSize: BufferSize = BufferSize.MEDIUM,
    val cacheSize: Long = 0L,
    val isLoading: Boolean = false
)

sealed interface SettingsEvent {
    data class UpdatePlaybackSpeed(val speed: Float) : SettingsEvent
    data class UpdateBufferSize(val bufferSize: BufferSize) : SettingsEvent
    data object ClearCache : SettingsEvent
}
