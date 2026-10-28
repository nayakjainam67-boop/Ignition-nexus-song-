package com.example.data.model

data class LyricLine(
    val timestampMs: Long,
    val text: String
)

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val genre: String,
    val coverDrawableRes: Int? = null,
    val coverUri: String? = null,
    val audioUri: String? = null,
    val lyrics: List<LyricLine> = emptyList(),
    val isUploaded: Boolean = false,
    val isLiked: Boolean = false,
    val isOfflineDownloaded: Boolean = false,
    val audioQuality: String = "Hi-Res Lossless 24-bit/192kHz",
    val playsCount: Long = 124500,
    val releaseYear: String = "2026",
    val synthScaleIndex: Int = 0,
    val fileSizeBytes: Long = 34_800_000L
) {
    val durationFormatted: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}
