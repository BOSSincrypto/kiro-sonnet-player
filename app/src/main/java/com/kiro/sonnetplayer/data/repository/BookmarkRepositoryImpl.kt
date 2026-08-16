package com.kiro.sonnetplayer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kiro.sonnetplayer.domain.model.Bookmark
import com.kiro.sonnetplayer.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of BookmarkRepository using DataStore for persistence.
 * Optimized for fast read/write operations.
 */
@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : BookmarkRepository {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val BOOKMARKS_KEY = stringPreferencesKey("bookmarks")
    }

    override fun getBookmarksForVideo(videoId: String): Flow<List<Bookmark>> {
        return getAllBookmarks().map { bookmarks ->
            bookmarks.filter { it.videoId == videoId }
                .sortedBy { it.timestamp }
        }
    }

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return dataStore.data.map { preferences ->
            val bookmarksJson = preferences[BOOKMARKS_KEY] ?: "[]"
            try {
                json.decodeFromString<List<BookmarkData>>(bookmarksJson).map { it.toBookmark() }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        dataStore.edit { preferences ->
            val currentBookmarks = try {
                val bookmarksJson = preferences[BOOKMARKS_KEY] ?: "[]"
                json.decodeFromString<List<BookmarkData>>(bookmarksJson).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }

            // Add new bookmark
            currentBookmarks.add(BookmarkData.fromBookmark(bookmark))

            // Save back to DataStore
            preferences[BOOKMARKS_KEY] = json.encodeToString(currentBookmarks)
        }
    }

    override suspend fun removeBookmark(bookmarkId: String) {
        dataStore.edit { preferences ->
            val currentBookmarks = try {
                val bookmarksJson = preferences[BOOKMARKS_KEY] ?: "[]"
                json.decodeFromString<List<BookmarkData>>(bookmarksJson).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }

            // Remove bookmark
            currentBookmarks.removeAll { it.id == bookmarkId }

            // Save back to DataStore
            preferences[BOOKMARKS_KEY] = json.encodeToString(currentBookmarks)
        }
    }

    override suspend fun updateBookmarkLabel(bookmarkId: String, label: String) {
        dataStore.edit { preferences ->
            val currentBookmarks = try {
                val bookmarksJson = preferences[BOOKMARKS_KEY] ?: "[]"
                json.decodeFromString<List<BookmarkData>>(bookmarksJson).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }

            // Update label
            val index = currentBookmarks.indexOfFirst { it.id == bookmarkId }
            if (index != -1) {
                currentBookmarks[index] = currentBookmarks[index].copy(label = label)
            }

            // Save back to DataStore
            preferences[BOOKMARKS_KEY] = json.encodeToString(currentBookmarks)
        }
    }

    override suspend fun clearBookmarksForVideo(videoId: String) {
        dataStore.edit { preferences ->
            val currentBookmarks = try {
                val bookmarksJson = preferences[BOOKMARKS_KEY] ?: "[]"
                json.decodeFromString<List<BookmarkData>>(bookmarksJson).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }

            // Remove all bookmarks for video
            currentBookmarks.removeAll { it.videoId == videoId }

            // Save back to DataStore
            preferences[BOOKMARKS_KEY] = json.encodeToString(currentBookmarks)
        }
    }

    /**
     * Internal data class for serialization.
     */
    @kotlinx.serialization.Serializable
    private data class BookmarkData(
        val id: String,
        val videoId: String,
        val timestamp: Long,
        val label: String,
        val createdAt: Long
    ) {
        fun toBookmark(): Bookmark {
            return Bookmark(
                id = id,
                videoId = videoId,
                timestamp = timestamp,
                label = label,
                createdAt = createdAt
            )
        }

        companion object {
            fun fromBookmark(bookmark: Bookmark): BookmarkData {
                return BookmarkData(
                    id = bookmark.id,
                    videoId = bookmark.videoId,
                    timestamp = bookmark.timestamp,
                    label = bookmark.label,
                    createdAt = bookmark.createdAt
                )
            }
        }
    }
}
