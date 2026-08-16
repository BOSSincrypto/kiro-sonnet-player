package com.kiro.sonnetplayer.domain.repository

import com.kiro.sonnetplayer.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for bookmark operations.
 */
interface BookmarkRepository {
    /**
     * Get all bookmarks for a specific video.
     */
    fun getBookmarksForVideo(videoId: String): Flow<List<Bookmark>>

    /**
     * Get all bookmarks.
     */
    fun getAllBookmarks(): Flow<List<Bookmark>>

    /**
     * Add a new bookmark.
     */
    suspend fun addBookmark(bookmark: Bookmark)

    /**
     * Remove a bookmark.
     */
    suspend fun removeBookmark(bookmarkId: String)

    /**
     * Update bookmark label.
     */
    suspend fun updateBookmarkLabel(bookmarkId: String, label: String)

    /**
     * Clear all bookmarks for a video.
     */
    suspend fun clearBookmarksForVideo(videoId: String)
}
