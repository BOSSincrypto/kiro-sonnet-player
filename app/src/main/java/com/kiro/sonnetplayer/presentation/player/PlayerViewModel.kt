package com.kiro.sonnetplayer.presentation.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.util.Rational
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val player: ExoPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var hideControlsJob: Job? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            _uiState.update { it.copy(
                isLoading = playbackState == Player.STATE_BUFFERING
            ) }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
        }
    }

    init {
        player.addListener(playerListener)
        startPositionUpdate()

        // Initialize volume
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        _uiState.update { it.copy(volume = currentVolume.toFloat() / maxVolume) }
    }

    private fun startPositionUpdate() {
        viewModelScope.launch {
            while (true) {
                if (player.isPlaying) {
                    _uiState.update {
                        it.copy(
                            currentPosition = player.currentPosition,
                            duration = player.duration.coerceAtLeast(0L),
                            bufferedPosition = player.bufferedPosition
                        )
                    }
                }
                delay(100) // Update 10 times per second for smooth seeking
            }
        }
    }

    fun onEvent(event: PlayerEvent) {
        when (event) {
            PlayerEvent.PlayPause -> togglePlayPause()
            PlayerEvent.ShowControls -> showControls()
            PlayerEvent.HideControls -> hideControls()
            is PlayerEvent.SeekTo -> seekTo(event.position)
            is PlayerEvent.SeekBy -> seekBy(event.delta)
            is PlayerEvent.SetPlaybackSpeed -> setPlaybackSpeed(event.speed)
            is PlayerEvent.SetVolume -> setVolume(event.volume)
            is PlayerEvent.SetBrightness -> setBrightness(event.brightness)
            PlayerEvent.TogglePictureInPicture -> togglePictureInPicture()
        }
    }

    private fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    private fun showControls() {
        _uiState.update { it.copy(isControlsVisible = true) }
        scheduleHideControls()
    }

    private fun hideControls() {
        hideControlsJob?.cancel()
        _uiState.update { it.copy(isControlsVisible = false) }
    }

    private fun scheduleHideControls() {
        hideControlsJob?.cancel()
        hideControlsJob = viewModelScope.launch {
            delay(3000) // Auto-hide after 3 seconds
            if (player.isPlaying) {
                _uiState.update { it.copy(isControlsVisible = false) }
            }
        }
    }

    private fun seekTo(position: Long) {
        player.seekTo(position.coerceIn(0L, player.duration))
        showControls()
    }

    private fun seekBy(delta: Long) {
        val newPosition = (player.currentPosition + delta).coerceIn(0L, player.duration)
        player.seekTo(newPosition)
        showControls()
    }

    private fun setPlaybackSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
        _uiState.update { it.copy(playbackSpeed = speed) }
        showControls()
    }

    private fun setVolume(volume: Float) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volumeIndex = (volume * maxVolume).toInt().coerceIn(0, maxVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex, 0)
        _uiState.update { it.copy(volume = volume) }
    }

    private fun setBrightness(brightness: Float) {
        _uiState.update { it.copy(brightness = brightness) }
    }

    private fun togglePictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val activity = context as? Activity ?: return

            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()

            activity.enterPictureInPictureMode(params)
        }
    }

    fun applyBrightnessToWindow(activity: Activity) {
        val brightness = _uiState.value.brightness
        if (brightness >= 0f) {
            val layoutParams = activity.window.attributes
            layoutParams.screenBrightness = brightness
            activity.window.attributes = layoutParams
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(playerListener)
        hideControlsJob?.cancel()
    }
}
