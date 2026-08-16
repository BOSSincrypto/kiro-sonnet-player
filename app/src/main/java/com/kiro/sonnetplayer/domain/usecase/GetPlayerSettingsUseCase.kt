package com.kiro.sonnetplayer.domain.usecase

import com.kiro.sonnetplayer.data.repository.SettingsRepository
import com.kiro.sonnetplayer.domain.model.PlayerSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayerSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<PlayerSettings> {
        return settingsRepository.playerSettings
    }
}
