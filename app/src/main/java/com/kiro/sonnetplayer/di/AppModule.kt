package com.kiro.sonnetplayer.di

import android.content.Context
import com.kiro.sonnetplayer.data.local.PreferencesDataSource
import com.kiro.sonnetplayer.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesDataSource(
        @ApplicationContext context: Context
    ): PreferencesDataSource {
        return PreferencesDataSource(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        preferencesDataSource: PreferencesDataSource
    ): SettingsRepository {
        return SettingsRepository(preferencesDataSource)
    }
}
