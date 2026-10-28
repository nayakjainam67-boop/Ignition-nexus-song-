package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.ui.NavScreen
import com.example.ui.theme.AppleMusicRed
import com.example.ui.theme.BackgroundDark
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
fun HomeScreen(
    tracks: List<Track>,
    playlists: List<Playlist>,
    currentTrack: Track?,
    isPlaying: Boolean,
    activeCategory: String,
    isOfflineMode: Boolean,
    isAutoAiActive: Boolean,
    isAutoTuningActive: Boolean,
    tuningModeTitle: String,
    splDecibels: Float,
    onCategorySelected: (String) -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleDownload: (String) -> Unit,
    onOpenShareDialog: (Track) -> Unit,
    onOpenAiSoundDialog: () -> Unit,
    onOpenSoundControlCenter: () -> Unit,
    onNavigate: (NavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Music", "Uploads", "Podcasts", "Offline")
    val featuredTrack = tracks.find { it.id == "track_khudi" } ?: tracks.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_home"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ignition_logo),
                        contentDescription = "Ignition Nexus Studios Logo",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, GoldPrimary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "IGNITION NEXUS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldPrimary,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "STUDIOS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = " • Dolby Atmos 6.2",
                                style = MaterialTheme.typography.labelSmall,
                                color = com.example.ui.theme.DolbyAtmosBlue
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Auto AI Sound Enhanced Button / Indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAutoAiActive) GoldPrimary.copy(alpha = 0.2f) else SurfaceVariantDark,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenAiSoundDialog() }
                            .padding(end = 4.dp)
                            .testTag("btn_top_ai_sound")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = if (isAutoAiActive) GoldPrimary else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI 6.2",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isAutoAiActive) GoldPrimary else TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isOfflineMode) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SpotifyGreen.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.WifiOff, contentDescription = null, tint = SpotifyGreen, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Offline", style = MaterialTheme.typography.labelSmall, color = SpotifyGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    IconButton(
                        onClick = { onNavigate(NavScreen.CLOUD_SYNC) },
                        modifier = Modifier.testTag("btn_home_cloud_sync")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Cloud Sync & Devices",
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = { onNavigate(NavScreen.UPLOAD_STUDIO) },
                        modifier = Modifier.testTag("btn_home_upload")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload Music",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Studio Sound Control Management HUD Bar
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenSoundControlCenter() }
                    .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .testTag("hud_sound_control_center"),
                color = SurfaceVariantDark.copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SOUND CONTROL AI",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAutoTuningActive) "• $tuningModeTitle" else "• Manual Bypass",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "Auto AI Tuned • Dolby 6.2 Surround • ${String.format("%.1f", splDecibels)} dB SPL Safe",
                                style = MaterialTheme.typography.labelSmall,
                                color = com.example.ui.theme.DolbyAtmosBlue
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldPrimary
                    ) {
                        Text(
                            text = "SMART TUNE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = BackgroundDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == activeCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(cat) },
                        label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = BackgroundDark,
                            containerColor = SurfaceVariantDark,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.testTag("chip_cat_$cat")
                    )
                }
            }
        }

        // Hero Spotlight Banner (Khudi Se Khuda Tak / Ignition Nexus)
        if (featuredTrack != null) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SpotlightHeroCard(
                    track = featuredTrack,
                    isPlaying = isPlaying && currentTrack?.id == featuredTrack.id,
                    onPlay = { onTrackSelected(featuredTrack, tracks) },
                    onToggleLike = { onToggleLike(featuredTrack.id) },
                    onShare = { onOpenShareDialog(featuredTrack) }
                )
            }
        }

        // Section: Heavy Rotation / Quick Picks
        item {
            SectionHeader(
                title = "Made for You",
                subtitle = "Algorithmic mixes & personal heavy rotation"
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(tracks) { track ->
                    TrackCardSquare(
                        track = track,
                        isPlaying = isPlaying && currentTrack?.id == track.id,
                        onClick = { onTrackSelected(track, tracks) }
                    )
                }
            }
        }

        // Section: YouTube-style Creator Uploads Banner
        item {
            Spacer(modifier = Modifier.height(18.dp))
            CreatorStudioPromoCard(
                onUploadClick = { onNavigate(NavScreen.UPLOAD_STUDIO) }
            )
        }

        // Section: Curated Playlists
        item {
            SectionHeader(
                title = "Featured Nexus Playlists",
                subtitle = "Apple Music Lossless & Spotify Top Soundtracks"
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(playlists) { pl ->
                    PlaylistCard(
                        playlist = pl,
                        onClick = {
                            val plTracks = tracks.filter { pl.trackIds.contains(it.id) }
                            if (plTracks.isNotEmpty()) {
                                onTrackSelected(plTracks.first(), plTracks)
                            }
                        }
                    )
                }
            }
        }

        // Section: All Tracks List
        item {
            SectionHeader(
                title = if (activeCategory == "All") "Popular Releases" else "$activeCategory Tracks",
                subtitle = "Studio Master High-Fidelity Audio"
            )
        }

        items(tracks) { track ->
            TrackListItem(
                track = track,
                isCurrent = currentTrack?.id == track.id,
                isPlaying = isPlaying && currentTrack?.id == track.id,
                onClick = { onTrackSelected(track, tracks) },
                onToggleLike = { onToggleLike(track.id) },
                onToggleDownload = { onToggleDownload(track.id) },
                onShare = { onOpenShareDialog(track) }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun SpotlightHeroCard(
    track: Track,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onToggleLike: () -> Unit,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(210.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF32230D),
                        Color(0xFF1B1824),
                        Color(0xFF0D0E14)
                    )
                )
            )
            .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable { onPlay() }
            .testTag("hero_spotlight_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
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
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GoldPrimary,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = "FEATURED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = BackgroundDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "Hi-Res Lossless",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldVariant,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_hero_play")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPlaying) "Pause" else "Play Now", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onToggleLike,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (track.isLiked) AppleMusicRed else TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackCardSquare(
    track: Track,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(135.dp)
            .clickable { onClick() }
            .testTag("track_card_${track.id}")
    ) {
        Box(
            modifier = Modifier
                .size(135.dp)
                .clip(RoundedCornerShape(12.dp))
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
            }

            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = track.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = track.artist,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("playlist_card_${playlist.id}")
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariantDark)
        ) {
            if (playlist.coverDrawableRes != null) {
                Image(
                    painter = painterResource(id = playlist.coverDrawableRes),
                    contentDescription = playlist.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = playlist.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${playlist.trackIds.size} songs • ${playlist.creatorName}",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1
        )
    }
}

@Composable
fun CreatorStudioPromoCard(
    onUploadClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onUploadClick() }
            .testTag("promo_creator_upload"),
        color = SurfaceVariantDark,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Upload Your Music (Creator Studio)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "YouTube Music style upload with offline sync",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDownload: () -> Unit,
    onShare: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("track_row_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
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
            }

            if (isCurrent && isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Titles
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isCurrent) GoldPrimary else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (track.isUploaded) {
                    Text(
                        text = "UPLOAD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                if (track.isOfflineDownloaded) {
                    Icon(
                        imageVector = Icons.Default.DownloadDone,
                        contentDescription = "Downloaded",
                        tint = SpotifyGreen,
                        modifier = Modifier.size(12.dp).padding(end = 4.dp)
                    )
                }
                Text(
                    text = "${track.artist} • ${track.durationFormatted}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Like button
        IconButton(
            onClick = onToggleLike,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = if (track.isLiked) AppleMusicRed else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        // More options dropdown
        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(SurfaceDark)
            ) {
                DropdownMenuItem(
                    text = { Text("Share Story Card", color = TextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onShare()
                    },
                    leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = GoldPrimary) }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            if (track.isOfflineDownloaded) "Remove Offline Copy" else "Download for Offline",
                            color = TextPrimary
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onToggleDownload()
                    },
                    leadingIcon = {
                        Icon(
                            if (track.isOfflineDownloaded) Icons.Default.DownloadDone else Icons.Default.DownloadDone,
                            contentDescription = null,
                            tint = SpotifyGreen
                        )
                    }
                )
            }
        }
    }
}
