package com.kiro.sonnetplayer.data.repository

import com.kiro.sonnetplayer.data.local.PreferencesDataSource
import com.kiro.sonnetplayer.domain.model.PlayerSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) {
    val playerSettings: Flow<PlayerSettings> = preferencesDataSource.playerSettings

    suspend fun updateSettings(settings: PlayerSettings) {
        preferencesDataSource.updateSettings(settings)
    }
}
