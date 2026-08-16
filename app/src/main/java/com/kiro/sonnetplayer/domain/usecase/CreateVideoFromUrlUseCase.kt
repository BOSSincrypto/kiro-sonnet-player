package com.kiro.sonnetplayer.domain.usecase

import com.kiro.sonnetplayer.domain.model.Video
import com.kiro.sonnetplayer.domain.repository.VideoRepository
import javax.inject.Inject

/**
 * Use case for creating a video from a network URL.
 */
class CreateVideoFromUrlUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(url: String): Result<Video> {
        return repository.createVideoFromUrl(url)
    }
}
