package com.kiro.sonnetplayer.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiro.sonnetplayer.domain.model.Bookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    private var currentVideoId: String = ""
    private var onJumpToTimestamp: ((Long) -> Unit)? = null

    fun initialize(videoId: String, onJump: (Long) -> Unit) {
        currentVideoId = videoId
        onJumpToTimestamp = onJump
        loadBookmarks(videoId)
    }

    fun onEvent(event: BookmarksEvent) {
        when (event) {
            is BookmarksEvent.AddBookmark -> addBookmark(event.timestamp, event.title)
            is BookmarksEvent.DeleteBookmark -> deleteBookmark(event.bookmarkId)
            is BookmarksEvent.UpdateBookmark -> updateBookmark(event.bookmark)
            is BookmarksEvent.JumpToBookmark -> jumpToBookmark(event.timestamp)
        }
    }

    private fun loadBookmarks(videoId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // TODO: Load from repository
            // For now, use in-memory storage
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun addBookmark(timestamp: Long, title: String) {
        viewModelScope.launch {
            val bookmark = Bookmark(
                id = UUID.randomUUID().toString(),
                videoId = currentVideoId,
                timestamp = timestamp,
                title = title
            )

            val updatedList = _uiState.value.bookmarks + bookmark
            _uiState.update {
                it.copy(bookmarks = updatedList.sortedBy { b -> b.timestamp })
            }

            // TODO: Save to repository
        }
    }

    private fun deleteBookmark(bookmarkId: String) {
        viewModelScope.launch {
            val updatedList = _uiState.value.bookmarks.filterNot { it.id == bookmarkId }
            _uiState.update { it.copy(bookmarks = updatedList) }

            // TODO: Delete from repository
        }
    }

    private fun updateBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            val updatedList = _uiState.value.bookmarks.map {
                if (it.id == bookmark.id) bookmark else it
            }
            _uiState.update {
                it.copy(bookmarks = updatedList.sortedBy { b -> b.timestamp })
            }

            // TODO: Update in repository
        }
    }

    private fun jumpToBookmark(timestamp: Long) {
        onJumpToTimestamp?.invoke(timestamp)
    }
}
