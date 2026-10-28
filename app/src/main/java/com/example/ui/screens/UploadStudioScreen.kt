package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Track
import com.example.ui.theme.AppleMusicRed
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LosslessGold
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UploadStudioScreen(
    onUploadSuccess: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var trackTitle by remember { mutableStateOf("") }
    var artistName by remember { mutableStateOf("") }
    var albumName by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("Electronic") }
    var lyricsInput by remember { mutableStateOf("") }

    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAudioName by remember { mutableStateOf<String?>(null) }
    var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }

    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }

    val genres = listOf("Electronic", "Devotional & Soulful", "Pop", "Cyber Synthwave", "Lo-Fi Beats", "Acoustic", "Rock")

    // Audio file picker
    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedAudioUri = uri
            selectedAudioName = uri.lastPathSegment ?: "Audio Track.mp3"
            if (trackTitle.isBlank()) {
                trackTitle = selectedAudioName?.substringBeforeLast(".") ?: "My Track"
            }
        }
    }

    // Cover art picker
    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedCoverUri = uri
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_upload_studio"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Creator Studio Upload",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Upload audio tracks like YouTube Music with cloud syncing and lossless sound",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Audio File Selector Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { audioPicker.launch("audio/*") }
                    .testTag("card_select_audio"),
                color = SurfaceVariantDark,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedAudioUri != null) SpotifyGreen.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = null,
                            tint = if (selectedAudioUri != null) SpotifyGreen else GoldPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedAudioUri != null) "Audio Selected" else "Select Audio File (MP3 / WAV / FLAC)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = selectedAudioName ?: "Tap to choose from device storage, or use synthesis",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selectedAudioUri != null) SpotifyGreen else TextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Upload",
                        tint = GoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Cover Art Selector Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { coverPicker.launch("image/*") }
                    .testTag("card_select_cover"),
                color = SurfaceVariantDark,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedCoverUri != null) {
                            AsyncImage(
                                model = selectedCoverUri,
                                contentDescription = "Cover preview",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedCoverUri != null) "Custom Artwork Set" else "Add Custom Album Artwork",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (selectedCoverUri != null) "Image ready for cloud publish" else "Square 1:1 image recommended (optional)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Track Details Form
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Track Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                OutlinedTextField(
                    value = trackTitle,
                    onValueChange = { trackTitle = it },
                    label = { Text("Track Title *") },
                    placeholder = { Text("e.g. Midnight Horizon") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_track_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = artistName,
                    onValueChange = { artistName = it },
                    label = { Text("Artist / Creator *") },
                    placeholder = { Text("e.g. Your Name or Band") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_artist_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = albumName,
                    onValueChange = { albumName = it },
                    label = { Text("Album / EP Name") },
                    placeholder = { Text("e.g. Singles 2026") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select Genre",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(genres) { g ->
                        val selected = g == selectedGenre
                        FilterChip(
                            selected = selected,
                            onClick = { selectedGenre = g },
                            label = { Text(g, style = MaterialTheme.typography.bodySmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = BackgroundDark,
                                containerColor = SurfaceVariantDark,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Synchronized Lyrics input
                Text(
                    text = "Add Song Lyrics (Apple Music Style Karaoke)",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = lyricsInput,
                    onValueChange = { lyricsInput = it },
                    label = { Text("Lyrics (one line per sentence)") },
                    placeholder = { Text("Line 1: Enter your lyrics here...\nLine 2: Each line synchronizes smoothly") },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_lyrics")
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (isUploading) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Uploading & Syncing to Cloud...", color = GoldPrimary, style = MaterialTheme.typography.bodySmall)
                            Text("${(uploadProgress * 100).toInt()}%", color = TextPrimary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GoldPrimary,
                            trackColor = SurfaceVariantDark
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (trackTitle.isBlank()) {
                                Toast.makeText(context, "Please enter a Track Title", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isUploading = true
                            uploadProgress = 0.1f

                            coroutineScope.launch {
                                delay(300)
                                uploadProgress = 0.45f
                                delay(400)
                                uploadProgress = 0.85f
                                delay(300)
                                uploadProgress = 1.0f

                                val uploadedTrack = Track(
                                    id = "upload_${System.currentTimeMillis()}",
                                    title = trackTitle.trim(),
                                    artist = if (artistName.isNotBlank()) artistName.trim() else "You (Creator)",
                                    album = if (albumName.isNotBlank()) albumName.trim() else "Studio Uploads",
                                    durationMs = 175_000L,
                                    genre = selectedGenre,
                                    coverUri = selectedCoverUri?.toString(),
                                    audioUri = selectedAudioUri?.toString(),
                                    lyrics = if (lyricsInput.isNotBlank()) {
                                        lyricsInput.lines().mapIndexed { i, line ->
                                            com.example.data.model.LyricLine(i * 5500L, line.trim())
                                        }
                                    } else emptyList(),
                                    isUploaded = true,
                                    isLiked = false,
                                    isOfflineDownloaded = true,
                                    audioQuality = "Creator Lossless 24-bit/48kHz",
                                    playsCount = 1,
                                    releaseYear = "2026",
                                    fileSizeBytes = 32_000_000L
                                )

                                isUploading = false
                                Toast.makeText(context, "Track '${uploadedTrack.title}' published to Ignition Nexus!", Toast.LENGTH_LONG).show()
                                onUploadSuccess(uploadedTrack)

                                // Clear
                                trackTitle = ""
                                artistName = ""
                                albumName = ""
                                lyricsInput = ""
                                selectedAudioUri = null
                                selectedAudioName = null
                                selectedCoverUri = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_publish_upload"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = BackgroundDark
                        )
                    ) {
                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish Track to Cloud & Sync", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
