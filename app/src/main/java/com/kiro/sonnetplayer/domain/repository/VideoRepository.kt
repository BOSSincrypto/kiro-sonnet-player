package com.kiro.sonnetplayer.domain.repository

import android.net.Uri
import com.kiro.sonnetplayer.domain.model.Video
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for video data operations.
 * Follows Clean Architecture principles.
 */
interface VideoRepository {
    /**
     * Get all locally available videos.
     * Uses MediaStore API for efficient scanning.
     */
    fun getLocalVideos(): Flow<List<Video>>

    /**
     * Get a specific video by ID.
     */
    suspend fun getVideoById(id: String): Video?

    /**
     * Create a video entry from a network URL.
     * Validates URL and fetches metadata when possible.
     */
    suspend fun createVideoFromUrl(url: String): Result<Video>

    /**
     * Search videos by title.
     */
    fun searchVideos(query: String): Flow<List<Video>>

    /**
     * Clear cache for remote videos.
     */
    suspend fun clearCache()

    /**
     * Get cache size in bytes.
     */
    suspend fun getCacheSize(): Long
}
