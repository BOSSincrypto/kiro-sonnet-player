package com.kiro.sonnetplayer.data.player

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache

/**
 * Extensions and utility functions for ExoPlayer optimization.
 */

/**
 * Create an optimized media source with caching support.
 */
@UnstableApi
fun createCachedMediaSource(
    context: Context,
    cache: SimpleCache,
    uri: String
): MediaSource {
    val dataSourceFactory = DefaultDataSource.Factory(context)

    val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(dataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    return ProgressiveMediaSource.Factory(cacheDataSourceFactory)
        .createMediaSource(androidx.media3.common.MediaItem.fromUri(uri))
}

/**
 * Format milliseconds to HH:MM:SS or MM:SS string.
 */
fun Long.formatTime(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * Check if player is in a playable state.
 */
fun ExoPlayer.isPlayableState(): Boolean {
    return playbackState == ExoPlayer.STATE_READY || playbackState == ExoPlayer.STATE_BUFFERING
}

/**
 * Get current position percentage (0.0 to 1.0).
 */
fun ExoPlayer.getPositionPercentage(): Float {
    return if (duration > 0) {
        currentPosition.toFloat() / duration.toFloat()
    } else {
        0f
    }
}
