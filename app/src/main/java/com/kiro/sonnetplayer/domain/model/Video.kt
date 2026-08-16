package com.kiro.sonnetplayer.domain.model

import android.net.Uri

data class Video(
    val id: String,
    val uri: Uri,
    val title: String,
    val duration: Long = 0L,
    val thumbnailUri: Uri? = null
)
