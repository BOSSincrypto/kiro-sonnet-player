package com.kiro.sonnetplayer.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Throttles state updates to reduce recomposition frequency.
 * Useful for high-frequency updates like video position.
 */
@Composable
fun <T> rememberThrottled(
    value: T,
    delayMillis: Long = 100
): State<T> {
    var throttledValue by remember { mutableStateOf(value) }
    var lastUpdateTime by remember { mutableStateOf(0L) }

    LaunchedEffect(value) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime >= delayMillis) {
            throttledValue = value
            lastUpdateTime = currentTime
        } else {
            delay(delayMillis - (currentTime - lastUpdateTime))
            throttledValue = value
            lastUpdateTime = System.currentTimeMillis()
        }
    }

    return remember { derivedStateOf { throttledValue } }
}

/**
 * Formats milliseconds to human-readable time string.
 */
fun formatDuration(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

/**
 * Converts bytes to human-readable format.
 */
fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.2f KB", bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024))
        else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
    }
}
