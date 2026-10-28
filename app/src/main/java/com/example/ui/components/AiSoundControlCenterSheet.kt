package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AiTuningMode
import com.example.audio.AutoAiTuningState
import com.example.audio.EnvironmentAcousticMode
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

@Composable
fun AiSoundControlCenterSheet(
    tuningState: AutoAiTuningState,
    leftChannelDb: Float,
    rightChannelDb: Float,
    onToggleAutoTuning: () -> Unit,
    onSetTuningMode: (AiTuningMode) -> Unit,
    onSetEnvironmentMode: (EnvironmentAcousticMode) -> Unit,
    onToggleDolbyAutoAdjusted: () -> Unit,
    onToggleHearingProtection: () -> Unit,
    onSetMaxDecibelCap: (Float) -> Unit,
    onToggleVolumeNormalizer: () -> Unit,
    onToggleTransientEnhancer: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .testTag("dialog_ai_sound_control_center"),
            color = SurfaceDark,
            tonalElevation = 12.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                // Top Header Row
                item {
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
                                            listOf(GoldPrimary, DolbyAtmosBlue)
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
                                        text = "SOUND CONTROL AI",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GoldPrimary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "SMART SYSTEM",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Auto AI Tuning & Dolby 6.2 Adaptive Suite",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("btn_close_sound_control")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }
                }

                // Live Studio Stereo VU Meter & SPL Decibels
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    StudioDecibelVuCard(
                        leftDb = leftChannelDb,
                        rightDb = rightChannelDb,
                        currentSpl = tuningState.telemetry.currentSplDecibels,
                        maxCap = tuningState.maxDecibelCap
                    )
                }

                // Master Auto AI Tuning Banner
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                1.dp,
                                if (tuningState.isAutoTuningEnabled) GoldPrimary.copy(alpha = 0.35f) else Color.Transparent,
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
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = if (tuningState.isAutoTuningEnabled) GoldPrimary else TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Auto AI Sound Tuning",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (tuningState.isAutoTuningEnabled) "Active: ${tuningState.tuningMode.title}" else "Disabled: Manual DSP bypass",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (tuningState.isAutoTuningEnabled) GoldPrimary else TextSecondary
                                    )
                                }
                            }

                            Switch(
                                checked = tuningState.isAutoTuningEnabled,
                                onCheckedChange = { onToggleAutoTuning() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GoldPrimary,
                                    checkedTrackColor = GoldPrimary.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.testTag("switch_master_ai_tuning")
                            )
                        }
                    }
                }

                // AI Tuning Mode Selector
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "AI Sound Quality Enhancement Modes",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AiTuningMode.values().forEach { mode ->
                            val isSelected = tuningState.tuningMode == mode
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSetTuningMode(mode) }
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mode.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) GoldPrimary else TextPrimary
                                        )
                                        Text(
                                            text = mode.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }
                                    if (isSelected) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = GoldPrimary
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = BackgroundDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Environment Acoustic Noise Adaptation
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Environment Acoustic Auto-Adaptation",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EnvironmentAcousticMode.values().take(2).forEach { env ->
                            val isSelected = tuningState.environmentMode == env
                            EnvironmentCardItem(
                                env = env,
                                isSelected = isSelected,
                                onClick = { onSetEnvironmentMode(env) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EnvironmentAcousticMode.values().drop(2).forEach { env ->
                            val isSelected = tuningState.environmentMode == env
                            EnvironmentCardItem(
                                env = env,
                                isSelected = isSelected,
                                onClick = { onSetEnvironmentMode(env) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // AI Dolby Atmos 6.2 Auto-Adjusted System Telemetry
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, DolbyAtmosBlue.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                        color = Color(0xFF0C101A)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SurroundSound,
                                        contentDescription = null,
                                        tint = DolbyAtmosBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "AI Dolby Atmos 6.2 Auto-Adjust",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Dynamic Real-Time Surround Envelope",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DolbyAtmosBlue
                                        )
                                    }
                                }

                                Switch(
                                    checked = tuningState.isDolbyAutoAdjusted,
                                    onCheckedChange = { onToggleDolbyAutoAdjusted() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = DolbyAtmosBlue,
                                        checkedTrackColor = DolbyAtmosBlue.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("switch_dolby_auto_adjust")
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Telemetry Readouts
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TelemetryMetricBadge(
                                    label = "Soundstage Width",
                                    value = "${tuningState.telemetry.soundstageWidthPercent}%",
                                    color = NeonCyan
                                )
                                TelemetryMetricBadge(
                                    label = "Ceiling 6.2 Height",
                                    value = "${tuningState.telemetry.ceilingHeightElevationPercent}%",
                                    color = DolbyAtmosBlue
                                )
                                TelemetryMetricBadge(
                                    label = "Dual Subwoofer .2",
                                    value = "${tuningState.telemetry.dualSubwooferPowerPercent}%",
                                    color = DolbySurroundViolet
                                )
                            }
                        }
                    }
                }

                // Hearing Health & Smart Volume Protection
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Smart Safe-Listening & Volume Management",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceVariantDark
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "AI Hearing Fatigue Guard",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Limits harmful transient spikes above ${tuningState.maxDecibelCap.toInt()} dB",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = HearingProfileGreen
                                    )
                                }
                                Switch(
                                    checked = tuningState.isHearingProtectionActive,
                                    onCheckedChange = { onToggleHearingProtection() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = HearingProfileGreen,
                                        checkedTrackColor = HearingProfileGreen.copy(alpha = 0.4f)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Max SPL Threshold: ${tuningState.maxDecibelCap.toInt()} dB",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Slider(
                                value = tuningState.maxDecibelCap,
                                onValueChange = onSetMaxDecibelCap,
                                valueRange = 75f..92f,
                                colors = SliderDefaults.colors(
                                    thumbColor = HearingProfileGreen,
                                    activeTrackColor = HearingProfileGreen
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Smart Track Volume Normalizer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = tuningState.smartVolumeNormalizer,
                                    onCheckedChange = { onToggleVolumeNormalizer() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = GoldPrimary,
                                        checkedTrackColor = GoldPrimary.copy(alpha = 0.4f)
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AI Transient & Harmonic Exciter",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = tuningState.transientEnhancer,
                                    onCheckedChange = { onToggleTransientEnhancer() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = GoldPrimary,
                                        checkedTrackColor = GoldPrimary.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        }
                    }
                }

                // Done Button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_done_sound_control"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = BackgroundDark
                        )
                    ) {
                        Text("Apply & Lock AI Sound Tuning", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StudioDecibelVuCard(
    leftDb: Float,
    rightDb: Float,
    currentSpl: Float,
    maxCap: Float
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF090B10))
            .border(1.dp, GoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
        color = Color(0xFF090B10)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (currentSpl < maxCap) SpotifyGreen else AppleMusicRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REAL-TIME SPL OUTPUT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "${String.format("%.1f", currentSpl)} dB SPL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (currentSpl < maxCap) GoldPrimary else AppleMusicRed
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dual Left & Right VU Meter Bars
            VuMeterBar(channelLabel = "L", dbValue = leftDb)
            Spacer(modifier = Modifier.height(6.dp))
            VuMeterBar(channelLabel = "R", dbValue = rightDb)
        }
    }
}

@Composable
fun VuMeterBar(channelLabel: String, dbValue: Float) {
    val normalized = ((dbValue + 24f) / 24f).coerceIn(0.05f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = channelLabel,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.width(16.dp)
        )

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceVariantDark)
        ) {
            val fillWidth = size.width * normalized
            val brush = Brush.horizontalGradient(
                colors = listOf(
                    SpotifyGreen,
                    GoldPrimary,
                    DolbySurroundViolet
                )
            )

            drawRoundRect(
                brush = brush,
                topLeft = Offset.Zero,
                size = Size(fillWidth, size.height),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${dbValue.toInt()}dB",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.width(36.dp)
        )
    }
}

@Composable
fun EnvironmentCardItem(
    env: EnvironmentAcousticMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (env) {
        EnvironmentAcousticMode.STUDIO_QUIET -> Icons.Default.Room
        EnvironmentAcousticMode.COMMUTE_TRANSIT -> Icons.Default.Train
        EnvironmentAcousticMode.OUTDOOR_STREET -> Icons.Default.DirectionsRun
        EnvironmentAcousticMode.BEDTIME_RELAX -> Icons.Default.NightsStay
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                if (isSelected) GoldPrimary else Color.Transparent,
                RoundedCornerShape(12.dp)
            ),
        color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else SurfaceVariantDark
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) GoldPrimary else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = env.title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) GoldPrimary else TextPrimary
                )
            }
        }
    }
}

@Composable
fun TelemetryMetricBadge(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}
