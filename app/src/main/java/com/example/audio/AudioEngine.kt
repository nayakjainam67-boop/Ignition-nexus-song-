package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.data.model.Track
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

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

class AudioEngine(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(180_000L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: StateFlow<Boolean> = _isShuffled.asStateFlow()

    private val _visualizerAmplitudes = MutableStateFlow<List<Float>>(List(28) { 0.1f })
    val visualizerAmplitudes: StateFlow<List<Float>> = _visualizerAmplitudes.asStateFlow()

    private val _equalizerPreset = MutableStateFlow("Vocal Enhancer")
    val equalizerPreset: StateFlow<String> = _equalizerPreset.asStateFlow()

    private val _spatialAudioEnabled = MutableStateFlow(true)
    val spatialAudioEnabled: StateFlow<Boolean> = _spatialAudioEnabled.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var synthAudioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private var progressJob: Job? = null

    var onTrackCompleted: (() -> Unit)? = null

    fun playTrack(track: Track) {
        stopCurrentPlayback()
        _currentTrack.value = track
        _durationMs.value = track.durationMs
        _currentPositionMs.value = 0L
        _isPlaying.value = true

        if (track.audioUri != null) {
            playFromUri(Uri.parse(track.audioUri))
        } else {
            playSynthesizedMelody(track)
        }
        startProgressTimer()
    }

    private fun playFromUri(uri: Uri) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, uri)
                prepare()
                start()
                setOnCompletionListener {
                    handleTrackEnd()
                }
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "Failed to play URI: ${e.message}, falling back to synth", e)
            _currentTrack.value?.let { playSynthesizedMelody(it) }
        }
    }

    private fun playSynthesizedMelody(track: Track) {
        synthJob?.cancel()
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        synthAudioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        synthAudioTrack?.play()

        // Scales for melodic progression
        val pentatonicNotes = when (track.synthScaleIndex % 4) {
            0 -> doubleArrayOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25) // C Major / Serene
            1 -> doubleArrayOf(220.00, 261.63, 293.66, 329.63, 392.00, 440.00) // A Minor / Ambient
            2 -> doubleArrayOf(146.83, 174.61, 196.00, 220.00, 261.63, 293.66) // D Minor / Deep Electronic
            else -> doubleArrayOf(311.13, 349.23, 415.30, 466.16, 523.25, 622.25) // Eb Spiritual / Lotus
        }

        synthJob = scope.launch(Dispatchers.IO) {
            val chunkSamples = 2048
            val buffer = ShortArray(chunkSamples)
            var currentNoteIdx = 0
            var sampleCounter = 0L

            try {
                while (isActive && _isPlaying.value) {
                    val baseFreq = pentatonicNotes[currentNoteIdx % pentatonicNotes.size]
                    val harmonicFreq = baseFreq * 1.5

                    for (i in 0 until chunkSamples) {
                        val t = (sampleCounter + i).toDouble() / sampleRate
                        // Soft envelope & combined wave
                        val wave = 0.65 * sin(2.0 * Math.PI * baseFreq * t) +
                                0.25 * sin(2.0 * Math.PI * harmonicFreq * t) +
                                0.10 * sin(2.0 * Math.PI * (baseFreq * 0.5) * t)
                        val amplitude = (wave * 7000).toInt().coerceIn(-32767, 32767)
                        buffer[i] = amplitude.toShort()
                    }

                    synthAudioTrack?.write(buffer, 0, chunkSamples)
                    sampleCounter += chunkSamples

                    // Switch notes every ~0.65s for melodic flow
                    if (sampleCounter % (sampleRate * 0.65).toLong() < chunkSamples) {
                        currentNoteIdx = (currentNoteIdx + 1) % pentatonicNotes.size
                    }
                }
            } catch (e: Exception) {
                Log.d("AudioEngine", "Synth stopped: ${e.message}")
            }
        }
    }

    private fun startProgressTimer() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                delay(120)
                if (_isPlaying.value) {
                    val currentPos: Long
                    val totalDur = _durationMs.value

                    if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                        currentPos = mediaPlayer?.currentPosition?.toLong() ?: 0L
                    } else {
                        currentPos = _currentPositionMs.value + 120L
                    }

                    if (currentPos >= totalDur) {
                        handleTrackEnd()
                    } else {
                        _currentPositionMs.value = currentPos
                        generateVisualizerAmplitudes()
                    }
                } else {
                    // Decay visualizer
                    _visualizerAmplitudes.value = _visualizerAmplitudes.value.map { (it * 0.85f).coerceAtLeast(0.08f) }
                }
            }
        }
    }

    private fun generateVisualizerAmplitudes() {
        val count = 28
        val posRatio = (_currentPositionMs.value % 4000) / 4000f
        val amps = List(count) { i ->
            val wave = sin((i * 0.45 + posRatio * Math.PI * 4)).toFloat()
            val noise = ((i * 7 + (posRatio * 100).toInt()) % 11) / 11f
            ((wave * 0.4f + noise * 0.6f).coerceIn(0.12f, 0.95f))
        }
        _visualizerAmplitudes.value = amps
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        _isPlaying.value = false
        mediaPlayer?.pause()
        synthJob?.cancel()
    }

    fun resume() {
        if (_currentTrack.value == null) return
        _isPlaying.value = true
        if (mediaPlayer != null) {
            mediaPlayer?.start()
        } else {
            _currentTrack.value?.let { playSynthesizedMelody(it) }
        }
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0L, _durationMs.value)
        _currentPositionMs.value = clamped
        mediaPlayer?.seekTo(clamped.toInt())
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun toggleShuffle() {
        _isShuffled.value = !_isShuffled.value
    }

    fun setEqualizerPreset(preset: String) {
        _equalizerPreset.value = preset
    }

    fun toggleSpatialAudio() {
        _spatialAudioEnabled.value = !_spatialAudioEnabled.value
    }

    private fun handleTrackEnd() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                seekTo(0)
                resume()
            }
            RepeatMode.ALL, RepeatMode.OFF -> {
                onTrackCompleted?.invoke()
            }
        }
    }

    private fun stopCurrentPlayback() {
        synthJob?.cancel()
        synthJob = null
        try {
            synthAudioTrack?.stop()
            synthAudioTrack?.release()
        } catch (_: Exception) {}
        synthAudioTrack = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    fun release() {
        stopCurrentPlayback()
        progressJob?.cancel()
    }
}
