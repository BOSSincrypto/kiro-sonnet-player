package com.kiro.sonnetplayer.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kiro.sonnetplayer.domain.model.PlayerSettings
import com.kiro.sonnetplayer.domain.model.VideoQuality
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "player_settings")

@Singleton
class PreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val HARDWARE_ACCELERATION = booleanPreferencesKey("hardware_acceleration")
        val AUTO_PLAY = booleanPreferencesKey("auto_play")
        val REMEMBER_POSITION = booleanPreferencesKey("remember_position")
        val PREFERRED_QUALITY = stringPreferencesKey("preferred_quality")
    }

    val playerSettings: Flow<PlayerSettings> = context.dataStore.data.map { preferences ->
        PlayerSettings(
            hardwareAcceleration = preferences[PreferencesKeys.HARDWARE_ACCELERATION] ?: true,
            autoPlay = preferences[PreferencesKeys.AUTO_PLAY] ?: true,
            rememberPosition = preferences[PreferencesKeys.REMEMBER_POSITION] ?: true,
            preferredQuality = VideoQuality.valueOf(
                preferences[PreferencesKeys.PREFERRED_QUALITY] ?: VideoQuality.AUTO.name
            )
        )
    }

    suspend fun updateSettings(settings: PlayerSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HARDWARE_ACCELERATION] = settings.hardwareAcceleration
            preferences[PreferencesKeys.AUTO_PLAY] = settings.autoPlay
            preferences[PreferencesKeys.REMEMBER_POSITION] = settings.rememberPosition
            preferences[PreferencesKeys.PREFERRED_QUALITY] = settings.preferredQuality.name
        }
    }
}
