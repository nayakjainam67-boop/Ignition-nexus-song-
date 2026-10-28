package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AiSoundProfile
import com.example.audio.Dolby62ChannelState
import com.example.audio.DolbyAtmos62Mode
import com.example.audio.HearingCalibration
import com.example.audio.MediaOutputDevice
import com.example.audio.OutputDeviceType
import com.example.ui.theme.AppleMusicRed
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DolbyAtmosBlue
import com.example.ui.theme.DolbySurroundViolet
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HearingProfileGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AiSoundStudioDialog(
    aiProfile: AiSoundProfile,
    dolbyChannels: Dolby62ChannelState,
    availableDevices: List<MediaOutputDevice>,
    onToggleAutoAi: () -> Unit,
    onToggleDolbyAtmos: () -> Unit,
    onSetDolbyMode: (DolbyAtmos62Mode) -> Unit,
    onSelectOutputDevice: (MediaOutputDevice) -> Unit,
    onUpdateHearingCalibration: (HearingCalibration) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dolby Atmos 6.2", "Output Device AI", "Hearing Calibration")

    // State for Hearing Calibration
    var leftEarSensitivity by remember { mutableFloatStateOf(aiProfile.hearingCalibration.left4kHz) }
    var rightEarSensitivity by remember { mutableFloatStateOf(aiProfile.hearingCalibration.right4kHz) }
    var vocalClarityDb by remember { mutableFloatStateOf(aiProfile.hearingCalibration.vocalClarityBoostDb) }
    var highAirDb by remember { mutableFloatStateOf(aiProfile.hearingCalibration.highFrequencyAirDb) }
    var isAbTestActive by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .testTag("dialog_ai_sound_studio"),
            color = SurfaceDark,
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(GoldPrimary, DolbySurroundViolet)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = BackgroundDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AUTO AI SOUND LAB",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DolbyAtmosBlue.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "6.2 ATMOS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DolbyAtmosBlue,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Personalized Acoustic Hearing Engine",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_ai_sound")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Master Auto AI Sound Toggle Banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (aiProfile.isAutoAiEnhanced) GoldPrimary.copy(alpha = 0.4f) else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        ),
                    color = SurfaceVariantDark
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = if (aiProfile.isAutoAiEnhanced) GoldPrimary else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Auto AI Sound Enhanced",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (aiProfile.isAutoAiEnhanced) "Active: Adapting to ${aiProfile.activeOutputDevice.name}" else "Disabled: Flat reference output",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (aiProfile.isAutoAiEnhanced) GoldPrimary else TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = aiProfile.isAutoAiEnhanced,
                            onCheckedChange = { onToggleAutoAi() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldPrimary,
                                checkedTrackColor = GoldPrimary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("switch_master_ai_sound")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = GoldPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldPrimary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) GoldPrimary else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> DolbyAtmos62TabContent(
                        isDolbyEnabled = aiProfile.isDolbyAtmos62Enabled,
                        currentMode = aiProfile.dolbyMode,
                        channelState = dolbyChannels,
                        onToggleDolby = onToggleDolbyAtmos,
                        onSelectMode = onSetDolbyMode
                    )
                    1 -> MediaOutputDeviceTabContent(
                        devices = availableDevices,
                        selectedDevice = aiProfile.activeOutputDevice,
                        onSelectDevice = onSelectOutputDevice
                    )
                    2 -> HearingCalibrationTabContent(
                        leftEarSensitivity = leftEarSensitivity,
                        rightEarSensitivity = rightEarSensitivity,
                        vocalClarityDb = vocalClarityDb,
                        highAirDb = highAirDb,
                        isAbTesting = isAbTestActive,
                        onLeftEarChange = { leftEarSensitivity = it },
                        onRightEarChange = { rightEarSensitivity = it },
                        onVocalClarityChange = { vocalClarityDb = it },
                        onHighAirChange = { highAirDb = it },
                        onToggleAbTest = { isAbTestActive = !isAbTestActive },
                        onSaveProfile = {
                            val updated = aiProfile.hearingCalibration.copy(
                                left4kHz = leftEarSensitivity,
                                right4kHz = rightEarSensitivity,
                                vocalClarityBoostDb = vocalClarityDb,
                                highFrequencyAirDb = highAirDb,
                                isCalibrated = true
                            )
                            onUpdateHearingCalibration(updated)
                            Toast.makeText(context, "Personalized Hearing Profile Saved!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DolbyAtmos62TabContent(
    isDolbyEnabled: Boolean,
    currentMode: DolbyAtmos62Mode,
    channelState: Dolby62ChannelState,
    onToggleDolby: () -> Unit,
    onSelectMode: (DolbyAtmos62Mode) -> Unit
) {
    val modes = listOf(
        DolbyAtmos62Mode.MUSIC_STUDIO to "6.2 Music Studio",
        DolbyAtmos62Mode.CINEMA_IMMERSION to "6.2 Cinema 3D",
        DolbyAtmos62Mode.BINAURAL_HEADPHONE_3D to "Binaural Headphone",
        DolbyAtmos62Mode.SPATIAL_STAGE_EXPAND to "Stage Expand"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dolby Atmos 6.2 Matrixing",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "6 Surround channels + 2 Dual Subwoofers (.2 LFE)",
                    style = MaterialTheme.typography.bodySmall,
                    color = DolbyAtmosBlue
                )
            }
            Switch(
                checked = isDolbyEnabled,
                onCheckedChange = { onToggleDolby() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DolbyAtmosBlue,
                    checkedTrackColor = DolbyAtmosBlue.copy(alpha = 0.4f)
                ),
                modifier = Modifier.testTag("switch_dolby_62")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mode Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            modes.take(2).forEach { (mode, label) ->
                val isSelected = currentMode == mode
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectMode(mode) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DolbyAtmosBlue,
                        selectedLabelColor = BackgroundDark,
                        containerColor = SurfaceVariantDark,
                        labelColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            modes.drop(2).forEach { (mode, label) ->
                val isSelected = currentMode == mode
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectMode(mode) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DolbyAtmosBlue,
                        selectedLabelColor = BackgroundDark,
                        containerColor = SurfaceVariantDark,
                        labelColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dolby 6.2 Surround Soundstage Radar Diagram
        Dolby62RadarView(
            channelState = channelState,
            isDolbyActive = isDolbyEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Dual Subwoofer (.2) Status
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = SurfaceVariantDark
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(DolbySurroundViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(".2", fontWeight = FontWeight.ExtraBold, color = DolbySurroundViolet, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Dual LFE Subwoofer Channel", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Sub 1 (30Hz) • Sub 2 (55Hz Punch)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
                Text("OPTIMIZED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = DolbySurroundViolet)
            }
        }
    }
}

@Composable
fun Dolby62RadarView(
    channelState: Dolby62ChannelState,
    isDolbyActive: Boolean,
    modifier: Modifier = Modifier
) {
    val pulseAnim = remember { Animatable(0.9f) }
    LaunchedEffect(isDolbyActive) {
        if (isDolbyActive) {
            pulseAnim.animateTo(
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0C0E14))
            .border(1.dp, DolbyAtmosBlue.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.height * 0.38f)

            // Draw radar concentric rings
            drawCircle(
                color = DolbyAtmosBlue.copy(alpha = 0.12f),
                radius = radius,
                center = centerOffset,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(
                color = DolbyAtmosBlue.copy(alpha = 0.2f),
                radius = radius * 0.65f,
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )

            // Draw Listener in center
            drawCircle(
                color = GoldPrimary,
                radius = 6.dp.toPx() * (if (isDolbyActive) pulseAnim.value else 1f),
                center = centerOffset
            )

            // Speaker Channel positions (6.2 setup: LF, C, RF, RS, LS, Overhead, Sub1, Sub2)
            val speakers = listOf(
                Pair("C", Offset(centerOffset.x, centerOffset.y - radius)),
                Pair("LF", Offset(centerOffset.x - radius * 0.7f, centerOffset.y - radius * 0.7f)),
                Pair("RF", Offset(centerOffset.x + radius * 0.7f, centerOffset.y - radius * 0.7f)),
                Pair("LS", Offset(centerOffset.x - radius * 0.9f, centerOffset.y + radius * 0.3f)),
                Pair("RS", Offset(centerOffset.x + radius * 0.9f, centerOffset.y + radius * 0.3f)),
                Pair("Height", Offset(centerOffset.x, centerOffset.y - radius * 0.45f)),
                Pair(".2 Sub1", Offset(centerOffset.x - radius * 0.4f, centerOffset.y + radius * 0.75f)),
                Pair(".2 Sub2", Offset(centerOffset.x + radius * 0.4f, centerOffset.y + radius * 0.75f))
            )

            speakers.forEach { (label, pos) ->
                val nodeColor = if (label.startsWith(".2")) DolbySurroundViolet else DolbyAtmosBlue
                val alphaVal = if (isDolbyActive) 0.9f else 0.4f

                // Outer halo
                if (isDolbyActive) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.25f),
                        radius = 12.dp.toPx() * pulseAnim.value,
                        center = pos
                    )
                }

                // Core node
                drawCircle(
                    color = nodeColor.copy(alpha = alphaVal),
                    radius = 5.dp.toPx(),
                    center = pos
                )
            }
        }

        // Listener label
        Text(
            text = "YOU (LISTENER)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary,
            fontSize = 9.sp,
            modifier = Modifier.align(Alignment.Center).padding(top = 22.dp)
        )
    }
}

@Composable
fun MediaOutputDeviceTabContent(
    devices: List<MediaOutputDevice>,
    selectedDevice: MediaOutputDevice,
    onSelectDevice: (MediaOutputDevice) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Output Media Device AI Adaptation",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "AI automatically optimizes frequency response & latency per hardware",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(devices) { device ->
                val isSelected = device.id == selectedDevice.id
                val icon: ImageVector = when (device.type) {
                    OutputDeviceType.HEADPHONES_BT -> Icons.Default.Headphones
                    OutputDeviceType.BUILT_IN_SPEAKERS -> Icons.Default.PhoneAndroid
                    OutputDeviceType.WIRED_DAC -> Icons.Default.Usb
                    OutputDeviceType.CAR_AUDIO -> Icons.Default.DirectionsCar
                    OutputDeviceType.HOME_THEATER_62 -> Icons.Default.SurroundSound
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectDevice(device) }
                        .border(
                            1.dp,
                            if (isSelected) GoldPrimary else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        ),
                    color = if (isSelected) GoldPrimary.copy(alpha = 0.12f) else SurfaceVariantDark
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) GoldPrimary else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = device.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) GoldPrimary else TextPrimary
                                    )
                                    if (device.isAutoDetected) {
                                        Text(
                                            text = "AUTO DETECT",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SpotifyGreen,
                                            modifier = Modifier.padding(start = 6.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${device.bitDepth}-bit / ${device.sampleRateKhz}kHz • Latency: ${device.latencyMs}ms",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = device.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) GoldPrimary.copy(alpha = 0.8f) else TextSecondary.copy(alpha = 0.8f)
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HearingCalibrationTabContent(
    leftEarSensitivity: Float,
    rightEarSensitivity: Float,
    vocalClarityDb: Float,
    highAirDb: Float,
    isAbTesting: Boolean,
    onLeftEarChange: (Float) -> Unit,
    onRightEarChange: (Float) -> Unit,
    onVocalClarityChange: (Float) -> Unit,
    onHighAirChange: (Float) -> Unit,
    onToggleAbTest: () -> Unit,
    onSaveProfile: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Personalized Hearing Equalizer",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Compensates for unique ear canal acoustics & sensitivities",
                    style = MaterialTheme.typography.bodySmall,
                    color = HearingProfileGreen
                )
            }

            OutlinedButton(
                onClick = onToggleAbTest,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isAbTesting) AppleMusicRed else HearingProfileGreen
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isAbTesting) "Raw Audio (Bypassed)" else "A/B Compare AI",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Left Ear Sensitivity
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Left Ear High-Freq Sensitivity (4 - 8 kHz)", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                Text("+${((leftEarSensitivity) * 8).toInt()} dB", style = MaterialTheme.typography.bodySmall, color = GoldPrimary, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = leftEarSensitivity,
                onValueChange = onLeftEarChange,
                colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Right Ear Sensitivity
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Right Ear High-Freq Sensitivity (4 - 8 kHz)", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                Text("+${((rightEarSensitivity) * 8).toInt()} dB", style = MaterialTheme.typography.bodySmall, color = GoldPrimary, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = rightEarSensitivity,
                onValueChange = onRightEarChange,
                colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Vocal Presence
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("AI Vocal Presence & Intelligibility", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                Text("+${vocalClarityDb.toInt()} dB", style = MaterialTheme.typography.bodySmall, color = HearingProfileGreen, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = vocalClarityDb,
                onValueChange = onVocalClarityChange,
                valueRange = 0f..8f,
                colors = SliderDefaults.colors(thumbColor = HearingProfileGreen, activeTrackColor = HearingProfileGreen),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onSaveProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("btn_save_hearing_profile"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldPrimary,
                contentColor = BackgroundDark
            )
        ) {
            Text("Save Personalized Hearing Profile", fontWeight = FontWeight.Bold)
        }
    }
}
