package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.DeviceSync
import com.example.data.model.DeviceType
import com.example.ui.theme.AppleMusicRed
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LosslessGold
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CloudSyncScreen(
    devices: List<DeviceSync>,
    isSyncing: Boolean,
    lastSyncTime: String,
    isOfflineMode: Boolean,
    streamingQuality: String,
    onToggleOfflineMode: () -> Unit,
    onSyncNow: () -> Unit,
    onSwitchDevice: (String) -> Unit,
    onSetStreamingQuality: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var wifiOnlyDownload by remember { mutableStateOf(true) }
    var qualityMenuExpanded by remember { mutableStateOf(false) }

    val qualityOptions = listOf(
        "Hi-Res Lossless 24-bit/192kHz",
        "Lossless ALAC 24-bit/48kHz",
        "High Quality 320kbps AAC",
        "Data Saver 128kbps OPUS"
    )

    val spinAnim = remember { Animatable(0f) }
    LaunchedEffect(isSyncing) {
        if (isSyncing) {
            spinAnim.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            spinAnim.snapTo(0f)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_cloud_sync"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Cloud Sync & Devices",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        // Cloud Status Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF2B200A),
                                Color(0xFF13151D)
                            )
                        )
                    )
                    .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSyncing) Icons.Default.Refresh else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (isSyncing) GoldPrimary else SpotifyGreen,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(if (isSyncing) spinAnim.value else 0f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isSyncing) "Syncing Across Devices..." else "Cloud Library In Sync",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Last synced: $lastSyncTime",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = onSyncNow,
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = BackgroundDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_sync_now")
                        ) {
                            Text(if (isSyncing) "Syncing..." else "Sync Now", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your playlists, liked songs, playback position, and creator uploads are securely mirrored across all your phones, tablets, and web players.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Connected Devices Section (Spotify Connect / Apple AirPlay style)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Connected Listening Devices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Seamlessly hand off playback to another speaker or screen",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        items(devices) { device ->
            DeviceRowItem(
                device = device,
                onSelect = { onSwitchDevice(device.id) }
            )
        }

        // Offline Mode & Audio Quality Section
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Offline Mode & High-Quality Audio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Configure offline caching and streaming bitrates",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Master Offline Mode Switch
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceVariantDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (isOfflineMode) Icons.Default.WifiOff else Icons.Default.Wifi,
                            contentDescription = null,
                            tint = if (isOfflineMode) SpotifyGreen else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Offline Mode",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isOfflineMode) "Active: Playing only local offline tracks" else "Disabled: Streaming from cloud catalog",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOfflineMode) SpotifyGreen else TextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isOfflineMode,
                        onCheckedChange = { onToggleOfflineMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SpotifyGreen,
                            checkedTrackColor = SpotifyGreen.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("switch_offline_mode_master")
                    )
                }
            }
        }

        // Streaming Quality Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { qualityMenuExpanded = true }
                    .testTag("card_streaming_quality"),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceVariantDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Audio Streaming Fidelity",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = streamingQuality,
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box {
                        Text(
                            text = "Change ▾",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        DropdownMenu(
                            expanded = qualityMenuExpanded,
                            onDismissRequest = { qualityMenuExpanded = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            qualityOptions.forEach { q ->
                                DropdownMenuItem(
                                    text = { Text(q, color = if (q == streamingQuality) GoldPrimary else TextPrimary) },
                                    onClick = {
                                        onSetStreamingQuality(q)
                                        qualityMenuExpanded = false
                                        Toast.makeText(context, "Audio quality updated: $q", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Wi-Fi Only Downloads
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceVariantDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Download over Wi-Fi Only",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Conserve mobile cellular data on lossless tracks",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = wifiOnlyDownload,
                        onCheckedChange = { wifiOnlyDownload = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldPrimary,
                            checkedTrackColor = GoldPrimary.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }

        // Cache Storage Manager
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceVariantDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Downloaded Music Storage",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "1.42 GB (42 tracks) • 58.2 GB Available",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Cache refreshed! 1.42 GB preserved", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Clear Cache", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceRowItem(
    device: DeviceSync,
    onSelect: () -> Unit
) {
    val icon: ImageVector = when (device.type) {
        DeviceType.PHONE -> Icons.Default.PhoneAndroid
        DeviceType.TABLET -> Icons.Default.Tablet
        DeviceType.WEB -> Icons.Default.Laptop
        DeviceType.SMART_SPEAKER -> Icons.Default.Speaker
        DeviceType.CAR -> Icons.Default.DirectionsCar
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("device_row_${device.id}"),
        shape = RoundedCornerShape(14.dp),
        color = if (device.isCurrent) GoldPrimary.copy(alpha = 0.12f) else SurfaceVariantDark
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (device.isCurrent) GoldPrimary else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (device.isCurrent) GoldPrimary else TextPrimary
                        )
                        if (device.isCurrent) {
                            Text(
                                text = " • ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = SpotifyGreen,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                    Text(
                        text = if (device.isCurrent) "Dolby Atmos Spatial Audio" else device.lastSyncTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            if (!device.isCurrent) {
                OutlinedButton(
                    onClick = onSelect,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Transfer Here", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active Device",
                    tint = SpotifyGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
