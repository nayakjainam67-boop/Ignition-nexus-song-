package com.example.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

enum class AiTuningMode(val title: String, val description: String) {
    ADAPTIVE_MASTERING("Adaptive Studio Master", "AI analyzes music genre & dynamically optimizes bit-depth & harmonics"),
    VOCAL_PERFECTION("Vocal Purity & Clarity", "Isolates vocal range, removes acoustic mud, and injects 4kHz presence"),
    BASS_HARMONIC_PUNCH("Dual Subwoofer .2 Punch", "Synthesizes sub-harmonic frequencies (30Hz-55Hz) with zero clipping"),
    LOUDNESS_EQUALIZER("Dynamic Loudness Curve", "Fletcher-Munson acoustic compensation for rich fullness at any volume")
}

enum class EnvironmentAcousticMode(val title: String, val iconName: String, val noiseProfile: String) {
    STUDIO_QUIET("Studio Quiet", "Room", "Zero background noise • Max dynamic range"),
    COMMUTE_TRANSIT("Commute & Metro", "Train", "Low-frequency rumble cancellation • Enhanced dialogue"),
    OUTDOOR_STREET("Outdoor & Gym", "DirectionsRun", "Dynamic punch boost • Active ambient noise defense"),
    BEDTIME_RELAX("Night & Relaxation", "NightsStay", "Warm high rolloff • Soothing harmonics • Anti-fatigue")
}

data class AiDolbyTelemetry(
    val soundstageWidthPercent: Int = 125,
    val ceilingHeightElevationPercent: Int = 88,
    val dualSubwooferPowerPercent: Int = 92,
    val dynamicHeadroomDb: Float = 4.2f,
    val harmonicExcitationPercent: Int = 84,
    val vocalCenteringPercent: Int = 96,
    val currentSplDecibels: Float = 73.5f,
    val safeListeningExposurePercent: Int = 22
)

data class AutoAiTuningState(
    val isAutoTuningEnabled: Boolean = true,
    val tuningMode: AiTuningMode = AiTuningMode.ADAPTIVE_MASTERING,
    val environmentMode: EnvironmentAcousticMode = EnvironmentAcousticMode.STUDIO_QUIET,
    val isDolbyAutoAdjusted: Boolean = true,
    val isHearingProtectionActive: Boolean = true,
    val maxDecibelCap: Float = 85.0f,
    val smartVolumeNormalizer: Boolean = true,
    val transientEnhancer: Boolean = true,
    val telemetry: AiDolbyTelemetry = AiDolbyTelemetry()
)

class AutoAiTuningManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var telemetryJob: Job? = null

    private val _tuningState = MutableStateFlow(AutoAiTuningState())
    val tuningState: StateFlow<AutoAiTuningState> = _tuningState.asStateFlow()

    private val _leftChannelDb = MutableStateFlow(-6.0f)
    val leftChannelDb: StateFlow<Float> = _leftChannelDb.asStateFlow()

    private val _rightChannelDb = MutableStateFlow(-5.8f)
    val rightChannelDb: StateFlow<Float> = _rightChannelDb.asStateFlow()

    init {
        startTelemetryLoop()
    }

    private fun startTelemetryLoop() {
        telemetryJob?.cancel()
        telemetryJob = scope.launch {
            var tick = 0
            while (isActive) {
                delay(150)
                tick++
                val currentState = _tuningState.value

                if (currentState.isAutoTuningEnabled) {
                    val baseWave = sin(tick * 0.25).toFloat()
                    val randomJitter = (Random.nextFloat() - 0.5f) * 1.5f

                    val baseDb = when (currentState.environmentMode) {
                        EnvironmentAcousticMode.STUDIO_QUIET -> 72.0f
                        EnvironmentAcousticMode.COMMUTE_TRANSIT -> 78.5f
                        EnvironmentAcousticMode.OUTDOOR_STREET -> 81.0f
                        EnvironmentAcousticMode.BEDTIME_RELAX -> 65.0f
                    }

                    val dynamicDb = (baseDb + baseWave * 3.5f + randomJitter).coerceIn(55.0f, currentState.maxDecibelCap)

                    val stageWidth = (120 + (baseWave * 15).toInt()).coerceIn(100, 150)
                    val heightElevation = (85 + (sin(tick * 0.18) * 10).toInt()).coerceIn(70, 100)
                    val subPower = (90 + (sin(tick * 0.32) * 8).toInt()).coerceIn(75, 100)

                    val updatedTelemetry = currentState.telemetry.copy(
                        soundstageWidthPercent = if (currentState.isDolbyAutoAdjusted) stageWidth else 100,
                        ceilingHeightElevationPercent = if (currentState.isDolbyAutoAdjusted) heightElevation else 75,
                        dualSubwooferPowerPercent = if (currentState.isDolbyAutoAdjusted) subPower else 80,
                        currentSplDecibels = dynamicDb,
                        vocalCenteringPercent = if (currentState.tuningMode == AiTuningMode.VOCAL_PERFECTION) 99 else 92,
                        harmonicExcitationPercent = if (currentState.transientEnhancer) 88 else 60
                    )

                    _tuningState.value = currentState.copy(telemetry = updatedTelemetry)

                    // VU meters in dB (-24dB to 0dB)
                    _leftChannelDb.value = (-12.0f + baseWave * 5.0f + randomJitter).coerceIn(-24f, 0f)
                    _rightChannelDb.value = (-11.5f + sin(tick * 0.27).toFloat() * 5.0f).coerceIn(-24f, 0f)
                } else {
                    _leftChannelDb.value = -18.0f
                    _rightChannelDb.value = -18.0f
                }
            }
        }
    }

    fun toggleAutoTuning() {
        val current = _tuningState.value
        _tuningState.value = current.copy(isAutoTuningEnabled = !current.isAutoTuningEnabled)
    }

    fun setTuningMode(mode: AiTuningMode) {
        _tuningState.value = _tuningState.value.copy(tuningMode = mode)
    }

    fun setEnvironmentMode(mode: EnvironmentAcousticMode) {
        _tuningState.value = _tuningState.value.copy(environmentMode = mode)
    }

    fun toggleDolbyAutoAdjusted() {
        val current = _tuningState.value
        _tuningState.value = current.copy(isDolbyAutoAdjusted = !current.isDolbyAutoAdjusted)
    }

    fun toggleHearingProtection() {
        val current = _tuningState.value
        _tuningState.value = current.copy(isHearingProtectionActive = !current.isHearingProtectionActive)
    }

    fun setMaxDecibelCap(cap: Float) {
        _tuningState.value = _tuningState.value.copy(maxDecibelCap = cap)
    }

    fun toggleVolumeNormalizer() {
        val current = _tuningState.value
        _tuningState.value = current.copy(smartVolumeNormalizer = !current.smartVolumeNormalizer)
    }

    fun toggleTransientEnhancer() {
        val current = _tuningState.value
        _tuningState.value = current.copy(transientEnhancer = !current.transientEnhancer)
    }

    fun release() {
        telemetryJob?.cancel()
    }
}
