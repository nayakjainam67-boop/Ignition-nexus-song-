package com.example.data.model

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverDrawableRes: Int? = null,
    val coverUri: String? = null,
    val trackIds: List<String> = emptyList(),
    val isOfflineDownloaded: Boolean = false,
    val creatorName: String = "Ignition Curators"
)
