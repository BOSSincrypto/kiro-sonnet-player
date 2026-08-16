package com.kiro.sonnetplayer.domain.model

data class Bookmark(
    val id: String,
    val videoId: String,
    val timestamp: Long,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)
