package com.kiro.sonnetplayer.domain.usecase

import com.kiro.sonnetplayer.domain.model.Video
import com.kiro.sonnetplayer.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all local videos.
 */
class GetLocalVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    operator fun invoke(): Flow<List<Video>> {
        return repository.getLocalVideos()
    }
}
