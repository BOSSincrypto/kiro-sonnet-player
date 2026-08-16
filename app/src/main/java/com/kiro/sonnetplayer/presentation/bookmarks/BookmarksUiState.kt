package com.kiro.sonnetplayer.presentation.bookmarks

import androidx.compose.runtime.Immutable
import com.kiro.sonnetplayer.domain.model.Bookmark

@Immutable
data class BookmarksUiState(
    val bookmarks: List<Bookmark> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface BookmarksEvent {
    data class AddBookmark(val timestamp: Long, val title: String) : BookmarksEvent
    data class DeleteBookmark(val bookmarkId: String) : BookmarksEvent
    data class UpdateBookmark(val bookmark: Bookmark) : BookmarksEvent
    data class JumpToBookmark(val timestamp: Long) : BookmarksEvent
}
