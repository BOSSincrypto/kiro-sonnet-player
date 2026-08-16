package com.kiro.sonnetplayer.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.kiro.sonnetplayer.domain.model.Video
import com.kiro.sonnetplayer.domain.repository.VideoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VideoRepository with optimized MediaStore queries.
 * Uses projection and efficient cursor handling to minimize memory overhead.
 */
@Singleton
class VideoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : VideoRepository {

    private val contentResolver: ContentResolver = context.contentResolver
    private val cacheDir = File(context.cacheDir, "video_cache")

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    override fun getLocalVideos(): Flow<List<Video>> = flow {
        val videos = mutableListOf<Video>()

        // Optimized projection - only fetch what we need
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED
        )

        // Sort by date added (newest first) for better UX
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val size = cursor.getLong(sizeColumn)
                val mimeType = cursor.getString(mimeTypeColumn) ?: "video/*"
                val dateAdded = cursor.getLong(dateAddedColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val thumbnailUri = ContentUris.withAppendedId(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    id
                )

                videos.add(
                    Video(
                        id = id.toString(),
                        uri = contentUri,
                        title = name,
                        duration = duration,
                        thumbnailUri = thumbnailUri,
                        width = width,
                        height = height,
                        size = size,
                        mimeType = mimeType,
                        dateAdded = dateAdded,
                        isRemote = false
                    )
                )
            }
        }

        emit(videos)
    }.flowOn(Dispatchers.IO)

    override suspend fun getVideoById(id: String): Video? = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Video.Media._ID} = ?"
        val selectionArgs = arrayOf(id)

        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

                val videoId = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoId
                )

                val thumbnailUri = ContentUris.withAppendedId(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    videoId
                )

                return@withContext Video(
                    id = videoId.toString(),
                    uri = contentUri,
                    title = cursor.getString(nameColumn),
                    duration = cursor.getLong(durationColumn),
                    thumbnailUri = thumbnailUri,
                    width = cursor.getInt(widthColumn),
                    height = cursor.getInt(heightColumn),
                    size = cursor.getLong(sizeColumn),
                    mimeType = cursor.getString(mimeTypeColumn) ?: "video/*",
                    dateAdded = cursor.getLong(dateAddedColumn),
                    isRemote = false
                )
            }
        }

        null
    }

    override suspend fun createVideoFromUrl(url: String): Result<Video> = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(url)

            // Validate URL scheme
            if (uri.scheme != "http" && uri.scheme != "https") {
                return@withContext Result.failure(IllegalArgumentException("Only HTTP/HTTPS URLs are supported"))
            }

            // Extract filename from URL
            val filename = uri.lastPathSegment ?: "remote_video"
            val videoId = "remote_${url.hashCode()}"

            // Create video object with unknown duration (will be determined by player)
            val video = Video(
                id = videoId,
                uri = uri,
                title = filename,
                duration = Video.UNKNOWN_DURATION,
                thumbnailUri = null,
                isRemote = true
            )

            Result.success(video)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchVideos(query: String): Flow<List<Video>> = flow {
        val videos = mutableListOf<Video>()
        val normalizedQuery = query.trim().lowercase()

        if (normalizedQuery.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED
        )

        // Use LIKE for case-insensitive search
        val selection = "${MediaStore.Video.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$normalizedQuery%")
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val thumbnailUri = ContentUris.withAppendedId(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    id
                )

                videos.add(
                    Video(
                        id = id.toString(),
                        uri = contentUri,
                        title = cursor.getString(nameColumn),
                        duration = cursor.getLong(durationColumn),
                        thumbnailUri = thumbnailUri,
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        size = cursor.getLong(sizeColumn),
                        mimeType = cursor.getString(mimeTypeColumn) ?: "video/*",
                        dateAdded = cursor.getLong(dateAddedColumn),
                        isRemote = false
                    )
                )
            }
        }

        emit(videos)
    }.flowOn(Dispatchers.IO)

    override suspend fun clearCache() = withContext(Dispatchers.IO) {
        cacheDir.listFiles()?.forEach { file ->
            file.delete()
        }
    }

    override suspend fun getCacheSize(): Long = withContext(Dispatchers.IO) {
        cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }
}
