package com.example.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class OutputDeviceType {
    HEADPHONES_BT,
    BUILT_IN_SPEAKERS,
    WIRED_DAC,
    CAR_AUDIO,
    HOME_THEATER_62
}

data class MediaOutputDevice(
    val id: String,
    val name: String,
    val type: OutputDeviceType,
    val description: String,
    val latencyMs: Int,
    val sampleRateKhz: Int,
    val bitDepth: Int,
    val isAutoDetected: Boolean = false
)

data class HearingCalibration(
    val id: String = "profile_default",
    val profileName: String = "Personalized Studio Profile",
    val left250Hz: Float = 0.5f,
    val left1kHz: Float = 0.6f,
    val left4kHz: Float = 0.75f,
    val left8kHz: Float = 0.8f,
    val right250Hz: Float = 0.5f,
    val right1kHz: Float = 0.62f,
    val right4kHz: Float = 0.7f,
    val right8kHz: Float = 0.82f,
    val vocalClarityBoostDb: Float = 3.5f,
    val highFrequencyAirDb: Float = 4.0f,
    val isCalibrated: Boolean = true
)

enum class DolbyAtmos62Mode {
    MUSIC_STUDIO,
    CINEMA_IMMERSION,
    BINAURAL_HEADPHONE_3D,
    SPATIAL_STAGE_EXPAND
}

data class Dolby62ChannelState(
    val leftFront: Float = 0.85f,
    val center: Float = 0.95f,
    val rightFront: Float = 0.85f,
    val leftSurround: Float = 0.78f,
    val rightSurround: Float = 0.78f,
    val overheadHeight: Float = 0.82f,
    val sub1Lfe: Float = 0.90f,
    val sub2Lfe: Float = 0.90f
)

data class AiSoundProfile(
    val isAutoAiEnhanced: Boolean = true,
    val isDolbyAtmos62Enabled: Boolean = true,
    val dolbyMode: DolbyAtmos62Mode = DolbyAtmos62Mode.MUSIC_STUDIO,
    val activeOutputDevice: MediaOutputDevice,
    val hearingCalibration: HearingCalibration = HearingCalibration(),
    val dynamicCompression: Boolean = true,
    val subHarmonicSynthesizer: Boolean = true,
    val aiClarityLevel: Float = 0.85f
)

class AiSoundOptimizer {
    val availableDevices = listOf(
        MediaOutputDevice(
            id = "dev_bt_buds",
            name = "AirPods Pro / Galaxy Buds (Spatial Bluetooth)",
            type = OutputDeviceType.HEADPHONES_BT,
            description = "Binaural Dolby Atmos 6.2 with personalized ear canal curve",
            latencyMs = 38,
            sampleRateKhz = 96,
            bitDepth = 24,
            isAutoDetected = true
        ),
        MediaOutputDevice(
            id = "dev_speakers",
            name = "Quad Stereo Device Speakers",
            type = OutputDeviceType.BUILT_IN_SPEAKERS,
            description = "Acoustic cross-talk cancellation & dialogue booster",
            latencyMs = 5,
            sampleRateKhz = 48,
            bitDepth = 24
        ),
        MediaOutputDevice(
            id = "dev_wired_dac",
            name = "Hi-Res Type-C Audiophile DAC",
            type = OutputDeviceType.WIRED_DAC,
            description = "Bit-perfect Lossless 192kHz ALAC studio reference line",
            latencyMs = 2,
            sampleRateKhz = 192,
            bitDepth = 32
        ),
        MediaOutputDevice(
            id = "dev_car",
            name = "Car Audio / Android Auto Receiver",
            type = OutputDeviceType.CAR_AUDIO,
            description = "Road-noise dynamic compensation & punchy bass kick",
            latencyMs = 45,
            sampleRateKhz = 48,
            bitDepth = 16
        ),
        MediaOutputDevice(
            id = "dev_soundbar",
            name = "Nexus Home Theater Dolby Atmos 6.2",
            type = OutputDeviceType.HOME_THEATER_62,
            description = "Discrete 6 surround channels + dual subwoofer LFE matrix",
            latencyMs = 12,
            sampleRateKhz = 192,
            bitDepth = 24
        )
    )

    private val _aiSoundProfile = MutableStateFlow(
        AiSoundProfile(
            isAutoAiEnhanced = true,
            isDolbyAtmos62Enabled = true,
            dolbyMode = DolbyAtmos62Mode.MUSIC_STUDIO,
            activeOutputDevice = availableDevices[0]
        )
    )
    val aiSoundProfile: StateFlow<AiSoundProfile> = _aiSoundProfile.asStateFlow()

    private val _dolbyChannels = MutableStateFlow(Dolby62ChannelState())
    val dolbyChannels: StateFlow<Dolby62ChannelState> = _dolbyChannels.asStateFlow()

    fun toggleAutoAiEnhance() {
        val current = _aiSoundProfile.value
        _aiSoundProfile.value = current.copy(isAutoAiEnhanced = !current.isAutoAiEnhanced)
    }

    fun toggleDolbyAtmos62() {
        val current = _aiSoundProfile.value
        _aiSoundProfile.value = current.copy(isDolbyAtmos62Enabled = !current.isDolbyAtmos62Enabled)
    }

    fun setDolbyMode(mode: DolbyAtmos62Mode) {
        _aiSoundProfile.value = _aiSoundProfile.value.copy(dolbyMode = mode)
        updateChannelsForMode(mode)
    }

    fun selectOutputDevice(device: MediaOutputDevice) {
        val autoMode = when (device.type) {
            OutputDeviceType.HEADPHONES_BT -> DolbyAtmos62Mode.BINAURAL_HEADPHONE_3D
            OutputDeviceType.HOME_THEATER_62 -> DolbyAtmos62Mode.CINEMA_IMMERSION
            OutputDeviceType.WIRED_DAC -> DolbyAtmos62Mode.MUSIC_STUDIO
            OutputDeviceType.BUILT_IN_SPEAKERS -> DolbyAtmos62Mode.SPATIAL_STAGE_EXPAND
            OutputDeviceType.CAR_AUDIO -> DolbyAtmos62Mode.MUSIC_STUDIO
        }
        _aiSoundProfile.value = _aiSoundProfile.value.copy(
            activeOutputDevice = device,
            dolbyMode = autoMode
        )
        updateChannelsForMode(autoMode)
    }

    fun updateHearingCalibration(calibration: HearingCalibration) {
        _aiSoundProfile.value = _aiSoundProfile.value.copy(
            hearingCalibration = calibration
        )
    }

    private fun updateChannelsForMode(mode: DolbyAtmos62Mode) {
        _dolbyChannels.value = when (mode) {
            DolbyAtmos62Mode.MUSIC_STUDIO -> Dolby62ChannelState(
                leftFront = 0.90f,
                center = 0.95f,
                rightFront = 0.90f,
                leftSurround = 0.80f,
                rightSurround = 0.80f,
                overheadHeight = 0.75f,
                sub1Lfe = 0.88f,
                sub2Lfe = 0.88f
            )
            DolbyAtmos62Mode.CINEMA_IMMERSION -> Dolby62ChannelState(
                leftFront = 0.95f,
                center = 1.0f,
                rightFront = 0.95f,
                leftSurround = 0.92f,
                rightSurround = 0.92f,
                overheadHeight = 0.98f,
                sub1Lfe = 0.98f,
                sub2Lfe = 0.98f
            )
            DolbyAtmos62Mode.BINAURAL_HEADPHONE_3D -> Dolby62ChannelState(
                leftFront = 0.92f,
                center = 0.90f,
                rightFront = 0.92f,
                leftSurround = 0.88f,
                rightSurround = 0.88f,
                overheadHeight = 0.85f,
                sub1Lfe = 0.85f,
                sub2Lfe = 0.85f
            )
            DolbyAtmos62Mode.SPATIAL_STAGE_EXPAND -> Dolby62ChannelState(
                leftFront = 1.0f,
                center = 0.95f,
                rightFront = 1.0f,
                leftSurround = 0.70f,
                rightSurround = 0.70f,
                overheadHeight = 0.65f,
                sub1Lfe = 0.80f,
                sub2Lfe = 0.80f
            )
        }
    }
}
