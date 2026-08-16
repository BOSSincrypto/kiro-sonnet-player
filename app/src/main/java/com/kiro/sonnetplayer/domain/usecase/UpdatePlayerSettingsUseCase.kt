package com.kiro.sonnetplayer.domain.usecase

import com.kiro.sonnetplayer.data.repository.SettingsRepository
import com.kiro.sonnetplayer.domain.model.PlayerSettings
import javax.inject.Inject

class UpdatePlayerSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: PlayerSettings) {
        settingsRepository.updateSettings(settings)
    }
}
