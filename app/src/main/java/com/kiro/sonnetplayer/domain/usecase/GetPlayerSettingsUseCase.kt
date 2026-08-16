package com.kiro.sonnetplayer.domain.usecase

import com.kiro.sonnetplayer.data.repository.SettingsRepository
import com.kiro.sonnetplayer.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Settings> {
        return settingsRepository.playerSettings
    }
}
