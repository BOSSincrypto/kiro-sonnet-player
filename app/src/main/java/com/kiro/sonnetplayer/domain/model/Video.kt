package com.kiro.sonnetplayer.domain.model

import android.net.Uri

data class Video(
    val id: String,
    val uri: Uri,
    val title: String,
    val duration: Long = 0L,
    val thumbnailUri: Uri? = null,
    val width: Int = 0,
    val height: Int = 0,
    val size: Long = 0L,
    val mimeType: String? = null,
    val dateAdded: Long = 0L,
    val isRemote: Boolean = false
) {
    companion object {
        const val UNKNOWN_DURATION = -1L
    }
}
