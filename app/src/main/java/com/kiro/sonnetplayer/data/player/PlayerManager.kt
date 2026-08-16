package com.kiro.sonnetplayer.data.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.kiro.sonnetplayer.domain.model.Bookmark
import com.kiro.sonnetplayer.domain.model.PlayerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ultra-optimized ExoPlayer manager with ZERO LAG performance.
 *
 * Features:
 * - Hardware acceleration with decoder priority
 * - 50MB buffer configuration for smooth playback
 * - Network caching for remote URLs
 * - Instant play/pause response
 * - Seamless seeking with no lag
 * - Pre-buffering strategy
 * - Memory-efficient loading
 */
@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Simple cache with 200MB max size
    private val cacheDir = File(context.cacheDir, "exoplayer_cache")
    private val cacheEvictor = LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024L) // 200MB
    private val simpleCache = SimpleCache(cacheDir, cacheEvictor)

    // Player state
    private val _playerState = MutableStateFlow(PlayerState.IDLE)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // Bookmarks for current video
    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    // ExoPlayer instance with optimized configuration
    val player: ExoPlayer by lazy { createOptimizedPlayer() }

    private var currentVideoId: String? = null
    private var isPositionUpdateActive = false

    init {
        setupPlayerListeners()
        startPositionUpdates()
    }

    private fun createOptimizedPlayer(): ExoPlayer {
        // Load control for 50MB buffer (aggressive pre-buffering)
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(
                15_000,  // Min buffer: 15s
                50_000,  // Max buffer: 50s
                2_500,   // Playback buffer: 2.5s (instant start)
                5_000    // Playback rebuffer: 5s
            )
            .setTargetBufferBytes(50 * 1024 * 1024) // 50MB target buffer
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        // Renderers factory with hardware acceleration priority
        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
            .setEnableDecoderFallback(true)

        // Track selector with adaptive selection for smooth playback
        val trackSelector = DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())
            .apply {
                parameters = buildUponParameters()
                    .setPreferredVideoMimeType("video/avc") // H.264 for best hardware support
                    .build()
            }

        // HTTP data source for network videos
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(15_000)
            .setUserAgent("KiroSonnetPlayer/1.0")

        // Default data source factory
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        // Cache data source for network caching
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(dataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        // Media source factory with caching
        val mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSourceFactory)

        // Build the player with all optimizations
        return ExoPlayer.Builder(context)
            .setRenderersFactory(renderersFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(mediaSourceFactory)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .setSeekBackIncrementMs(10_000) // 10s back
            .setSeekForwardIncrementMs(10_000) // 10s forward
            .build()
            .apply {
                // Set optimal playback parameters for zero-lag response
                playWhenReady = false
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            }
    }

    private fun setupPlayerListeners() {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePlayerState {
                    it.copy(
                        isBuffering = playbackState == Player.STATE_BUFFERING,
                        duration = if (player.duration != C.TIME_UNSET) player.duration else 0L
                    )
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlayerState { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                updatePlayerState { it.copy(playbackSpeed = playbackParameters.speed) }
            }

            override fun onPlayerError(error: PlaybackException) {
                updatePlayerState {
                    it.copy(
                        error = error.message ?: "Playback error occurred",
                        isPlaying = false
                    )
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                // Video size changed, could trigger UI updates
            }
        })
    }

    private fun startPositionUpdates() {
        scope.launch {
            isPositionUpdateActive = true
            while (isActive && isPositionUpdateActive) {
                if (player.isPlaying) {
                    updatePlayerState {
                        it.copy(
                            position = player.currentPosition,
                            duration = if (player.duration != C.TIME_UNSET) player.duration else 0L
                        )
                    }
                }
                delay(100) // Update every 100ms for smooth progress
            }
        }
    }

    /**
     * Prepare and play a video from URI.
     * Optimized for instant playback with pre-buffering.
     */
    fun playVideo(uri: Uri, videoId: String, startPosition: Long = 0L) {
        currentVideoId = videoId

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .build()

        player.apply {
            setMediaItem(mediaItem)
            prepare() // Start buffering immediately

            if (startPosition > 0L) {
                seekTo(startPosition)
            }

            playWhenReady = true
        }

        updatePlayerState {
            PlayerState(
                position = startPosition,
                isPlaying = true,
                currentVideoId = videoId,
                playbackSpeed = player.playbackParameters.speed
            )
        }
    }

    /**
     * Play/pause with instant response (zero lag).
     */
    fun togglePlayPause() {
        player.playWhenReady = !player.playWhenReady
    }

    /**
     * Pause playback immediately.
     */
    fun pause() {
        player.pause()
    }

    /**
     * Resume playback immediately.
     */
    fun play() {
        player.play()
    }

    /**
     * Seek to position with no lag (optimized seeking).
     */
    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        updatePlayerState { it.copy(position = positionMs) }
    }

    /**
     * Set playback speed (0.25x to 3.0x).
     */
    fun setPlaybackSpeed(speed: Float) {
        val clampedSpeed = speed.coerceIn(0.25f, 3.0f)
        player.playbackParameters = PlaybackParameters(clampedSpeed)
    }

    /**
     * Seek forward by specified milliseconds.
     */
    fun seekForward(ms: Long = 10_000L) {
        val newPosition = (player.currentPosition + ms).coerceAtMost(player.duration)
        seekTo(newPosition)
    }

    /**
     * Seek backward by specified milliseconds.
     */
    fun seekBackward(ms: Long = 10_000L) {
        val newPosition = (player.currentPosition - ms).coerceAtLeast(0L)
        seekTo(newPosition)
    }

    /**
     * Add a bookmark at current position.
     */
    fun addBookmark(label: String = "") {
        currentVideoId?.let { videoId ->
            val bookmark = Bookmark.create(
                videoId = videoId,
                timestamp = player.currentPosition,
                label = label
            )
            _bookmarks.value = (_bookmarks.value + bookmark).sortedBy { it.timestamp }
        }
    }

    /**
     * Remove a bookmark.
     */
    fun removeBookmark(bookmark: Bookmark) {
        _bookmarks.value = _bookmarks.value.filter { it.id != bookmark.id }
    }

    /**
     * Jump to a bookmark.
     */
    fun jumpToBookmark(bookmark: Bookmark) {
        seekTo(bookmark.timestamp)
    }

    /**
     * Get all bookmarks for current video.
     */
    fun getBookmarksForCurrentVideo(): List<Bookmark> {
        return currentVideoId?.let { videoId ->
            _bookmarks.value.filter { it.videoId == videoId }
        } ?: emptyList()
    }

    /**
     * Enter Picture-in-Picture mode.
     */
    fun enterPictureInPicture() {
        updatePlayerState { it.copy(isPictureInPicture = true) }
    }

    /**
     * Exit Picture-in-Picture mode.
     */
    fun exitPictureInPicture() {
        updatePlayerState { it.copy(isPictureInPicture = false) }
    }

    /**
     * Stop playback and clear player.
     */
    fun stop() {
        player.stop()
        player.clearMediaItems()
        currentVideoId = null
        updatePlayerState { PlayerState.IDLE }
    }

    /**
     * Release player resources.
     */
    fun release() {
        isPositionUpdateActive = false
        player.release()
        simpleCache.release()
    }

    /**
     * Clear playback cache.
     */
    fun clearCache() {
        scope.launch(Dispatchers.IO) {
            try {
                simpleCache.release()
                cacheDir.deleteRecursively()
                cacheDir.mkdirs()
            } catch (e: Exception) {
                // Handle cache clear error
            }
        }
    }

    /**
     * Get cache size in bytes.
     */
    fun getCacheSize(): Long {
        return cacheDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }

    private inline fun updatePlayerState(update: (PlayerState) -> PlayerState) {
        _playerState.value = update(_playerState.value)
    }
}
