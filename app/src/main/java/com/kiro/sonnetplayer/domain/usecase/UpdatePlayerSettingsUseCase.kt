package com.kiro.sonnetplayer.domain.usecase

import com.kiro.sonnetplayer.data.repository.SettingsRepository
import com.kiro.sonnetplayer.domain.model.Settings
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: Settings) {
        settingsRepository.updateSettings(settings)
    }
}
