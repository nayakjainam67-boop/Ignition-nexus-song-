package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.audio.RepeatMode
import com.example.data.model.DeviceSync
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.data.storage.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavScreen {
    HOME,
    SEARCH,
    LIBRARY,
    UPLOAD_STUDIO,
    CLOUD_SYNC
}

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context get() = getApplication()
    val repository = MusicRepository(context)
    val audioEngine = AudioEngine(context)
    val aiSoundOptimizer = com.example.audio.AiSoundOptimizer()
    val autoAiTuningManager = com.example.audio.AutoAiTuningManager()

    // Navigation and Modals
    private val _currentScreen = MutableStateFlow(NavScreen.HOME)
    val currentScreen: StateFlow<NavScreen> = _currentScreen.asStateFlow()

    private val _isFullPlayerVisible = MutableStateFlow(false)
    val isFullPlayerVisible: StateFlow<Boolean> = _isFullPlayerVisible.asStateFlow()

    private val _isLyricsActive = MutableStateFlow(false)
    val isLyricsActive: StateFlow<Boolean> = _isLyricsActive.asStateFlow()

    private val _isVisualizerActive = MutableStateFlow(false)
    val isVisualizerActive: StateFlow<Boolean> = _isVisualizerActive.asStateFlow()

    private val _isShareDialogOpen = MutableStateFlow(false)
    val isShareDialogOpen: StateFlow<Boolean> = _isShareDialogOpen.asStateFlow()

    private val _trackToShare = MutableStateFlow<Track?>(null)
    val trackToShare: StateFlow<Track?> = _trackToShare.asStateFlow()

    private val _isEqualizerDialogOpen = MutableStateFlow(false)
    val isEqualizerDialogOpen: StateFlow<Boolean> = _isEqualizerDialogOpen.asStateFlow()

    private val _isAiSoundDialogOpen = MutableStateFlow(false)
    val isAiSoundDialogOpen: StateFlow<Boolean> = _isAiSoundDialogOpen.asStateFlow()

    private val _isSoundControlCenterOpen = MutableStateFlow(false)
    val isSoundControlCenterOpen: StateFlow<Boolean> = _isSoundControlCenterOpen.asStateFlow()

    val aiProfile = aiSoundOptimizer.aiSoundProfile
    val dolbyChannels = aiSoundOptimizer.dolbyChannels
    val autoAiTuningState = autoAiTuningManager.tuningState
    val leftChannelDb = autoAiTuningManager.leftChannelDb
    val rightChannelDb = autoAiTuningManager.rightChannelDb

    // Filters & Search
    private val _activeCategory = MutableStateFlow("All")
    val activeCategory: StateFlow<String> = _activeCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Playback state from AudioEngine
    val isPlaying = audioEngine.isPlaying
    val currentTrack = audioEngine.currentTrack
    val currentPositionMs = audioEngine.currentPositionMs
    val durationMs = audioEngine.durationMs
    val repeatMode = audioEngine.repeatMode
    val isShuffled = audioEngine.isShuffled
    val visualizerAmplitudes = audioEngine.visualizerAmplitudes
    val equalizerPreset = audioEngine.equalizerPreset
    val spatialAudioEnabled = audioEngine.spatialAudioEnabled

    // Repository state
    val isOfflineMode = repository.isOfflineMode
    val streamingQuality = repository.streamingQuality
    val likedTrackIds = repository.likedTrackIds
    val offlineDownloadedIds = repository.offlineDownloadedIds
    val uploadedTracks = repository.uploadedTracks
    val playlists = repository.playlists
    val devices = repository.devices
    val isSyncing = repository.isSyncing
    val lastCloudSyncTime = repository.lastCloudSyncTime

    // Queue
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    init {
        val initialTracks = repository.getAllTracks()
        _queue.value = initialTracks
        // Set Khudi Se Khuda Tak as initial ready track
        val firstTrack = initialTracks.firstOrNull()
        if (firstTrack != null && currentTrack.value == null) {
            // Prepared in queue
        }

        audioEngine.onTrackCompleted = {
            skipNext()
        }
    }

    // Filtered Tracks
    val displayedTracks: StateFlow<List<Track>> = combine(
        repository.uploadedTracks,
        repository.offlineDownloadedIds,
        repository.isOfflineMode,
        _activeCategory,
        _searchQuery
    ) { uploads, downloaded, offlineMode, category, query ->
        var list = repository.getAllTracks()

        if (offlineMode) {
            list = list.filter { downloaded.contains(it.id) }
        }

        when (category) {
            "Music" -> list = list.filter { !it.isUploaded }
            "Uploads" -> list = list.filter { it.isUploaded }
            "Offline" -> list = list.filter { downloaded.contains(it.id) }
            "Podcasts" -> list = list.filter { it.genre.contains("Spiritual", ignoreCase = true) || it.genre.contains("Talk", ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                        it.artist.lowercase().contains(q) ||
                        it.genre.lowercase().contains(q) ||
                        it.lyrics.any { l -> l.text.lowercase().contains(q) }
            }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getAllTracks())

    fun setScreen(screen: NavScreen) {
        _currentScreen.value = screen
    }

    fun setActiveCategory(category: String) {
        _activeCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playTrack(track: Track, newQueue: List<Track>? = null) {
        if (newQueue != null && newQueue.isNotEmpty()) {
            _queue.value = newQueue
        } else if (!_queue.value.any { it.id == track.id }) {
            _queue.value = listOf(track) + _queue.value
        }
        audioEngine.playTrack(track)
    }

    fun togglePlayPause() {
        if (currentTrack.value == null) {
            val first = displayedTracks.value.firstOrNull() ?: repository.defaultTracks.first()
            playTrack(first)
        } else {
            audioEngine.togglePlayPause()
        }
    }

    fun seekTo(positionMs: Long) {
        audioEngine.seekTo(positionMs)
    }

    fun skipNext() {
        val q = _queue.value
        if (q.isEmpty()) return
        val current = currentTrack.value
        val currentIndex = q.indexOfFirst { it.id == current?.id }
        val nextIndex = if (isShuffled.value) {
            (0 until q.size).random()
        } else {
            if (currentIndex in 0 until q.size - 1) currentIndex + 1 else 0
        }
        playTrack(q[nextIndex])
    }

    fun skipPrevious() {
        val q = _queue.value
        if (q.isEmpty()) return
        val current = currentTrack.value
        if (audioEngine.currentPositionMs.value > 3000L) {
            audioEngine.seekTo(0)
            return
        }
        val currentIndex = q.indexOfFirst { it.id == current?.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else q.size - 1
        playTrack(q[prevIndex])
    }

    fun toggleRepeat() {
        audioEngine.toggleRepeat()
    }

    fun toggleShuffle() {
        audioEngine.toggleShuffle()
    }

    fun toggleLikeTrack(trackId: String) {
        repository.toggleLikeTrack(trackId)
    }

    fun toggleDownloadOffline(trackId: String) {
        repository.toggleDownloadOffline(trackId)
    }

    fun toggleOfflineMode() {
        repository.toggleOfflineMode()
    }

    fun setStreamingQuality(quality: String) {
        repository.setStreamingQuality(quality)
    }

    fun setEqualizerPreset(preset: String) {
        audioEngine.setEqualizerPreset(preset)
    }

    fun toggleSpatialAudio() {
        audioEngine.toggleSpatialAudio()
    }

    fun syncCloudNow(onComplete: () -> Unit = {}) {
        repository.syncNow(onComplete)
    }

    fun switchActiveDevice(deviceId: String) {
        repository.switchActiveDevice(deviceId)
    }

    fun showFullPlayer() {
        _isFullPlayerVisible.value = true
    }

    fun hideFullPlayer() {
        _isFullPlayerVisible.value = false
    }

    fun toggleLyricsMode() {
        _isLyricsActive.value = !_isLyricsActive.value
    }

    fun toggleVisualizerMode() {
        _isVisualizerActive.value = !_isVisualizerActive.value
    }

    fun openShareDialog(track: Track) {
        _trackToShare.value = track
        _isShareDialogOpen.value = true
    }

    fun closeShareDialog() {
        _isShareDialogOpen.value = false
        _trackToShare.value = null
    }

    fun openEqualizerDialog() {
        _isEqualizerDialogOpen.value = true
    }

    fun closeEqualizerDialog() {
        _isEqualizerDialogOpen.value = false
    }

    fun openAiSoundDialog() {
        _isAiSoundDialogOpen.value = true
    }

    fun closeAiSoundDialog() {
        _isAiSoundDialogOpen.value = false
    }

    fun toggleAutoAiEnhance() {
        aiSoundOptimizer.toggleAutoAiEnhance()
    }

    fun toggleDolbyAtmos62() {
        aiSoundOptimizer.toggleDolbyAtmos62()
    }

    fun setDolbyMode(mode: com.example.audio.DolbyAtmos62Mode) {
        aiSoundOptimizer.setDolbyMode(mode)
    }

    fun selectOutputDevice(device: com.example.audio.MediaOutputDevice) {
        aiSoundOptimizer.selectOutputDevice(device)
    }

    fun updateHearingCalibration(calibration: com.example.audio.HearingCalibration) {
        aiSoundOptimizer.updateHearingCalibration(calibration)
    }

    fun openSoundControlCenter() {
        _isSoundControlCenterOpen.value = true
    }

    fun closeSoundControlCenter() {
        _isSoundControlCenterOpen.value = false
    }

    fun toggleAutoTuning() {
        autoAiTuningManager.toggleAutoTuning()
    }

    fun setTuningMode(mode: com.example.audio.AiTuningMode) {
        autoAiTuningManager.setTuningMode(mode)
    }

    fun setEnvironmentMode(mode: com.example.audio.EnvironmentAcousticMode) {
        autoAiTuningManager.setEnvironmentMode(mode)
    }

    fun toggleDolbyAutoAdjusted() {
        autoAiTuningManager.toggleDolbyAutoAdjusted()
    }

    fun toggleHearingProtection() {
        autoAiTuningManager.toggleHearingProtection()
    }

    fun setMaxDecibelCap(cap: Float) {
        autoAiTuningManager.setMaxDecibelCap(cap)
    }

    fun toggleVolumeNormalizer() {
        autoAiTuningManager.toggleVolumeNormalizer()
    }

    fun toggleTransientEnhancer() {
        autoAiTuningManager.toggleTransientEnhancer()
    }

    fun uploadCustomMusic(
        title: String,
        artist: String,
        album: String,
        genre: String,
        audioUri: String?,
        coverUri: String?,
        lyrics: String
    ): Track {
        val newTrack = repository.addUploadedTrack(
            title = title,
            artist = artist,
            album = album,
            genre = genre,
            audioUri = audioUri,
            coverUri = coverUri,
            lyricsText = lyrics
        )
        // Add to active queue and play immediately
        _queue.value = listOf(newTrack) + _queue.value
        playTrack(newTrack)
        return newTrack
    }

    fun createNewPlaylist(title: String, description: String, trackIds: List<String>) {
        repository.createPlaylist(title, description, trackIds)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
        autoAiTuningManager.release()
    }
}
