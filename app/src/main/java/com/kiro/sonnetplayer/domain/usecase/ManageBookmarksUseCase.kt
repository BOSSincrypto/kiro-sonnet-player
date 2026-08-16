package com.kiro.sonnetplayer.domain.usecase

import com.kiro.sonnetplayer.domain.model.Bookmark
import com.kiro.sonnetplayer.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for managing bookmarks.
 */
class ManageBookmarksUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    fun getBookmarksForVideo(videoId: String): Flow<List<Bookmark>> {
        return repository.getBookmarksForVideo(videoId)
    }

    suspend fun addBookmark(bookmark: Bookmark) {
        repository.addBookmark(bookmark)
    }

    suspend fun removeBookmark(bookmarkId: String) {
        repository.removeBookmark(bookmarkId)
    }

    suspend fun updateBookmarkLabel(bookmarkId: String, label: String) {
        repository.updateBookmarkLabel(bookmarkId, label)
    }
}
