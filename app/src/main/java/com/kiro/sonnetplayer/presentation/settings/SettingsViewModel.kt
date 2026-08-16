package com.kiro.sonnetplayer.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiro.sonnetplayer.domain.model.Settings.BufferSize
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        calculateCacheSize()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdatePlaybackSpeed -> updatePlaybackSpeed(event.speed)
            is SettingsEvent.UpdateBufferSize -> updateBufferSize(event.bufferSize)
            SettingsEvent.ClearCache -> clearCache()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: Load from DataStore preferences
            _uiState.update {
                it.copy(
                    defaultPlaybackSpeed = 1.0f,
                    bufferSize = BufferSize.MEDIUM
                )
            }
        }
    }

    private fun updatePlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(defaultPlaybackSpeed = speed) }
            // TODO: Save to DataStore preferences
        }
    }

    private fun updateBufferSize(bufferSize: BufferSize) {
        viewModelScope.launch {
            _uiState.update { it.copy(bufferSize = bufferSize) }
            // TODO: Save to DataStore preferences
        }
    }

    private fun calculateCacheSize() {
        viewModelScope.launch {
            val cacheDir = context.cacheDir
            val cacheSize = cacheDir.walkTopDown()
                .filter { it.isFile }
                .map { it.length() }
                .sum()

            _uiState.update { it.copy(cacheSize = cacheSize) }
        }
    }

    private fun clearCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                context.cacheDir.deleteRecursively()
                context.cacheDir.mkdirs()
                calculateCacheSize()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
