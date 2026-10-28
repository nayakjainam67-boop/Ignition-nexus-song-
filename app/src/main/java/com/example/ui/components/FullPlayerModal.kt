package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.audio.RepeatMode
import com.example.data.model.Track
import com.example.ui.theme.AppleMusicRed
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DolbyAtmosBlue
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldVariant
import com.example.ui.theme.LosslessGold
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun FullPlayerModal(
    track: Track,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    repeatMode: RepeatMode,
    isShuffled: Boolean,
    visualizerAmplitudes: List<Float>,
    isLyricsActive: Boolean,
    isVisualizerActive: Boolean,
    isLiked: Boolean,
    isOfflineDownloaded: Boolean,
    spatialAudioEnabled: Boolean,
    onDismiss: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDownloadOffline: () -> Unit,
    onToggleLyrics: () -> Unit,
    onToggleVisualizer: () -> Unit,
    onOpenShareDialog: () -> Unit,
    onOpenEqualizerDialog: () -> Unit,
    onOpenAiSoundDialog: () -> Unit,
    onOpenSoundControlCenter: () -> Unit
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragSliderPosition by remember { mutableFloatStateOf(0f) }

    val effectivePosition = if (isDraggingSlider) {
        (dragSliderPosition * durationMs).toLong()
    } else {
        currentPositionMs
    }

    val progress = if (durationMs > 0) {
        (effectivePosition.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    // Find active lyric index
    val activeLyricIndex = remember(effectivePosition, track.lyrics) {
        val idx = track.lyrics.indexOfLast { it.timestampMs <= effectivePosition }
        if (idx >= 0) idx else 0
    }

    val lyricsListState = rememberLazyListState()
    LaunchedEffect(activeLyricIndex, isLyricsActive) {
        if (isLyricsActive && track.lyrics.isNotEmpty()) {
            lyricsListState.animateScrollToItem(
                (activeLyricIndex - 1).coerceAtLeast(0)
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("full_player_modal"),
        color = BackgroundDark
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ambient Backdrop Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF261D09).copy(alpha = 0.6f),
                                Color(0xFF13151D).copy(alpha = 0.8f),
                                BackgroundDark
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_collapse_player")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Collapse",
                            tint = TextPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PLAYING FROM PLAYLIST",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = track.album,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onOpenShareDialog,
                        modifier = Modifier.testTag("btn_top_share")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Center Content: Album Artwork vs Synchronized Lyrics vs Live Visualizer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLyricsActive -> {
                            // Synchronized Lyrics View (Apple Music Style)
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SYNCHRONIZED LYRICS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Tap any line to seek",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                LazyColumn(
                                    state = lyricsListState,
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    itemsIndexed(track.lyrics) { index, line ->
                                        val isActive = index == activeLyricIndex
                                        val textColor by animateColorAsState(
                                            targetValue = if (isActive) GoldPrimary else TextSecondary.copy(alpha = 0.45f),
                                            animationSpec = tween(300),
                                            label = "lyric_color"
                                        )
                                        Text(
                                            text = line.text,
                                            style = if (isActive) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = textColor,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onSeekTo(line.timestampMs) }
                                                .padding(vertical = 4.dp, horizontal = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        isVisualizerActive -> {
                            // YouTube Music style live audio visualizer screen
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "LIVE SPECTRUM VISUALIZER",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                SpectrumVisualizer(
                                    amplitudes = visualizerAmplitudes,
                                    isPlaying = isPlaying,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "${track.audioQuality} • Dolby Atmos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        else -> {
                            // Default Large Album Art
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .aspectRatio(1f)
                                    .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = GoldPrimary)
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(1.dp, GoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                                    .background(SurfaceVariantDark)
                            ) {
                                if (track.coverUri != null) {
                                    AsyncImage(
                                        model = track.coverUri,
                                        contentDescription = track.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (track.coverDrawableRes != null) {
                                    Image(
                                        painter = painterResource(id = track.coverDrawableRes),
                                        contentDescription = track.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(90.dp).align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Song Title, Artist, Like & Offline Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleDownloadOffline,
                            modifier = Modifier.testTag("btn_full_offline")
                        ) {
                            Icon(
                                imageVector = if (isOfflineDownloaded) Icons.Default.DownloadDone else Icons.Default.Download,
                                contentDescription = "Offline Download",
                                tint = if (isOfflineDownloaded) SpotifyGreen else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleLike,
                            modifier = Modifier.testTag("btn_full_like")
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isLiked) AppleMusicRed else TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Apple Music Lossless & Dolby Atmos 6.2 Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceVariantDark,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenEqualizerDialog() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = track.audioQuality,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        }
                    }

                    // Dolby Atmos 6.2 & Auto AI Button Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = com.example.ui.theme.DolbyAtmosBlue.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenAiSoundDialog() }
                            .testTag("badge_dolby_62_player")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = com.example.ui.theme.DolbyAtmosBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "DOLBY 6.2",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = com.example.ui.theme.DolbyAtmosBlue
                            )
                        }
                    }

                    // Sound Control AI Smart System Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldPrimary.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenSoundControlCenter() }
                            .testTag("badge_sound_control_player")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Tune,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI TUNE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldPrimary
                            )
                        }
                    }
                }

                // Scrubbable Progress Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = progress,
                        onValueChange = { newProg ->
                            isDraggingSlider = true
                            dragSliderPosition = newProg
                        },
                        onValueChangeFinished = {
                            isDraggingSlider = false
                            val seekTarget = (dragSliderPosition * durationMs).toLong()
                            onSeekTo(seekTarget)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = TextPrimary,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = SurfaceVariantDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_playback_progress")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val elapsedSeconds = effectivePosition / 1000
                        val durationSeconds = durationMs / 1000
                        Text(
                            text = String.format("%d:%02d", elapsedSeconds / 60, elapsedSeconds % 60),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = String.format("%d:%02d", durationSeconds / 60, durationSeconds % 60),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Playback Control Buttons (Spotify / Apple Music style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onToggleShuffle,
                        modifier = Modifier.testTag("btn_shuffle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffled) GoldPrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onSkipPrevious,
                        modifier = Modifier.testTag("btn_prev")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = TextPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Main Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(12.dp, CircleShape, spotColor = GoldPrimary)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                            .clickable { onTogglePlayPause() }
                            .testTag("btn_full_play_pause"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = BackgroundDark,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(
                        onClick = onSkipNext,
                        modifier = Modifier.testTag("btn_next")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = TextPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleRepeat,
                        modifier = Modifier.testTag("btn_repeat")
                    ) {
                        Icon(
                            imageVector = if (repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            contentDescription = "Repeat",
                            tint = if (repeatMode != RepeatMode.OFF) GoldPrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Bar: Connected Device & Visualizer/Lyrics/EQ/Share toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Equalizer
                    IconButton(
                        onClick = onOpenEqualizerDialog,
                        modifier = Modifier.testTag("btn_open_eq")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Equalizer",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Auto AI Sound Lab & Dolby 6.2
                    IconButton(
                        onClick = onOpenAiSoundDialog,
                        modifier = Modifier.testTag("btn_open_ai_sound_full")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Auto AI Sound & Dolby Atmos 6.2",
                            tint = DolbyAtmosBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Sound Control AI Smart System
                    IconButton(
                        onClick = onOpenSoundControlCenter,
                        modifier = Modifier.testTag("btn_open_sound_control_full")
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Tune,
                            contentDescription = "Sound Control AI Smart System",
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Lyrics toggle
                    IconButton(
                        onClick = onToggleLyrics,
                        modifier = Modifier.testTag("btn_toggle_lyrics")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lyrics,
                            contentDescription = "Lyrics",
                            tint = if (isLyricsActive) GoldPrimary else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Visualizer toggle
                    IconButton(
                        onClick = onToggleVisualizer,
                        modifier = Modifier.testTag("btn_toggle_visualizer")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Visualizer",
                            tint = if (isVisualizerActive) GoldPrimary else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Social Share
                    IconButton(
                        onClick = onOpenShareDialog,
                        modifier = Modifier.testTag("btn_social_share_full")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
