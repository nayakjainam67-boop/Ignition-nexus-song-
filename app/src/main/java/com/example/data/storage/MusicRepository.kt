package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.data.model.DeviceSync
import com.example.data.model.DeviceType
import com.example.data.model.LyricLine
import com.example.data.model.Playlist
import com.example.data.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class MusicRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ignition_music_prefs", Context.MODE_PRIVATE)

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _streamingQuality = MutableStateFlow(
        prefs.getString("pref_quality", "Hi-Res Lossless 24-bit/192kHz") ?: "Hi-Res Lossless 24-bit/192kHz"
    )
    val streamingQuality: StateFlow<String> = _streamingQuality.asStateFlow()

    private val _lastCloudSyncTime = MutableStateFlow("Just now")
    val lastCloudSyncTime: StateFlow<String> = _lastCloudSyncTime.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _likedTrackIds = MutableStateFlow<Set<String>>(loadLikedTrackIds())
    val likedTrackIds: StateFlow<Set<String>> = _likedTrackIds.asStateFlow()

    private val _offlineDownloadedIds = MutableStateFlow<Set<String>>(loadDownloadedTrackIds())
    val offlineDownloadedIds: StateFlow<Set<String>> = _offlineDownloadedIds.asStateFlow()

    private val _uploadedTracks = MutableStateFlow<List<Track>>(loadUploadedTracks())
    val uploadedTracks: StateFlow<List<Track>> = _uploadedTracks.asStateFlow()

    private val _devices = MutableStateFlow<List<DeviceSync>>(
        listOf(
            DeviceSync("dev_1", "Pixel 9 Pro (This Device)", DeviceType.PHONE, isCurrent = true, isConnected = true, lastSyncTime = "Active now"),
            DeviceSync("dev_2", "Ignition Web Player (Chrome)", DeviceType.WEB, isCurrent = false, isConnected = true, lastSyncTime = "2 mins ago"),
            DeviceSync("dev_3", "Nexus Soundbar (AirPlay/Cast)", DeviceType.SMART_SPEAKER, isCurrent = false, isConnected = true, lastSyncTime = "Standby"),
            DeviceSync("dev_4", "Audi MMI / Android Auto", DeviceType.CAR, isCurrent = false, isConnected = false, lastSyncTime = "Yesterday")
        )
    )
    val devices: StateFlow<List<DeviceSync>> = _devices.asStateFlow()

    val defaultTracks: List<Track> = listOf(
        Track(
            id = "track_khudi",
            title = "Khudi Se Khuda Tak",
            artist = "Jainam Nayak",
            album = "Spiritual Awakening",
            durationMs = 158_000L,
            genre = "Devotional & Soulful",
            coverDrawableRes = R.drawable.art_khudi_se_khuda,
            lyrics = listOf(
                LyricLine(0L, "♬ [Serene Flute & Lotus Echoes] ♬"),
                LyricLine(8000L, "Khudi se khuda tak ka yeh safar..."),
                LyricLine(18000L, "Rooh ki gehraiyon mein ek noor sa jagar..."),
                LyricLine(29000L, "Har saans mein tera hi naam goonje..."),
                LyricLine(41000L, "Mann ke mandir mein bas tera hi deedar..."),
                LyricLine(54000L, "Chhod ke saare bandhan iss jahaan ke..."),
                LyricLine(68000L, "Mil gaya mujhe mera parvardigaar..."),
                LyricLine(82000L, "Khudi se khuda tak... mera yeh safar..."),
                LyricLine(96000L, "Om Namo Bhagavate Vasudevaya..."),
                LyricLine(112000L, "Jahan Tu hai, wahin meri manzil hai..."),
                LyricLine(130000L, "Khudi mit gayi, reh gaya bas Tu hi Tu...")
            ),
            isUploaded = false,
            audioQuality = "Hi-Res Lossless 24-bit/192kHz",
            playsCount = 482000,
            releaseYear = "2026",
            synthScaleIndex = 3,
            fileSizeBytes = 42_500_000L
        ),
        Track(
            id = "track_ignition_flagship",
            title = "Ignition Nexus (Powering the Connected World)",
            artist = "Ignition Sound Lab",
            album = "Nexus Evolution",
            durationMs = 142_000L,
            genre = "Electronic / Cinematic",
            coverDrawableRes = R.drawable.ic_ignition_logo,
            lyrics = listOf(
                LyricLine(0L, "♬ [Orchestral Phoenix Awakening] ♬"),
                LyricLine(10000L, "Ignition Nexus, power the world..."),
                LyricLine(22000L, "Waves of gold through the digital soundscape..."),
                LyricLine(35000L, "Connected minds, sonic frequency unleashed..."),
                LyricLine(48000L, "Break through the silence, ignite the future..."),
                LyricLine(62000L, "Rising higher than the neon horizon..."),
                LyricLine(78000L, "Powering the connected world, forever ignited!"),
                LyricLine(94000L, "♬ [Harmonic Synthesizer Solo] ♬"),
                LyricLine(112000L, "Ignition Nexus... Sound Beyond Limits.")
            ),
            isUploaded = false,
            audioQuality = "Dolby Atmos 24-bit Studio Master",
            playsCount = 1250000,
            releaseYear = "2026",
            synthScaleIndex = 0,
            fileSizeBytes = 51_200_000L
        ),
        Track(
            id = "track_cyber_pulse",
            title = "Cyber Pulse 2026",
            artist = "Neon Mirage",
            album = "Quantum City",
            durationMs = 165_000L,
            genre = "Cyber Synthwave",
            coverDrawableRes = R.drawable.art_ignition_banner,
            lyrics = listOf(
                LyricLine(0L, "♬ [Analog Bass Pulse & Neon Arpeggio] ♬"),
                LyricLine(12000L, "Rays of starlight bouncing off glass towers..."),
                LyricLine(25000L, "Running through electric veins of the grid..."),
                LyricLine(40000L, "Feel the baseline hit the floor..."),
                LyricLine(55000L, "Speeding through midnight highways..."),
                LyricLine(72000L, "We never sleep in the neon glow..."),
                LyricLine(92000L, "Pure energy, endless frequency.")
            ),
            isUploaded = false,
            audioQuality = "Hi-Res Lossless 24-bit/96kHz",
            playsCount = 890000,
            releaseYear = "2026",
            synthScaleIndex = 2,
            fileSizeBytes = 38_100_000L
        ),
        Track(
            id = "track_lotus_serenity",
            title = "Lotus Serenity (Chants of Bliss)",
            artist = "Soma Ensemble",
            album = "Inner Voyage",
            durationMs = 175_000L,
            genre = "Devotional & Soulful",
            coverDrawableRes = R.drawable.art_khudi_se_khuda,
            lyrics = listOf(
                LyricLine(0L, "♬ [Tanpura & Ambient Morning Chimes] ♬"),
                LyricLine(14000L, "Floating lotus petals on sacred waters..."),
                LyricLine(28000L, "Peace awakens in the quiet soul..."),
                LyricLine(45000L, "Breathe in harmony, let go of the storm..."),
                LyricLine(64000L, "Divine presence shining within...")
            ),
            isUploaded = false,
            audioQuality = "Hi-Res Lossless 24-bit/192kHz",
            playsCount = 310000,
            releaseYear = "2026",
            synthScaleIndex = 3,
            fileSizeBytes = 40_200_000L
        ),
        Track(
            id = "track_golden_flight",
            title = "Golden Phoenix Ascent",
            artist = "Aura & Orchestral",
            album = "Wings of Gold",
            durationMs = 138_000L,
            genre = "Epic Orchestral",
            coverDrawableRes = R.drawable.ic_ignition_logo,
            lyrics = listOf(
                LyricLine(0L, "♬ [Cinematic Strings & Brass Flourish] ♬"),
                LyricLine(15000L, "From embers into eternal flight..."),
                LyricLine(32000L, "Ascend into the golden heavens..."),
                LyricLine(52000L, "The phoenix ignites the dawn.")
            ),
            isUploaded = false,
            audioQuality = "Hi-Res Lossless 24-bit/192kHz",
            playsCount = 670000,
            releaseYear = "2026",
            synthScaleIndex = 0,
            fileSizeBytes = 47_000_000L
        )
    )

    private val _playlists = MutableStateFlow<List<Playlist>>(
        listOf(
            Playlist(
                id = "pl_nexus_hits",
                title = "Ignition Nexus Top Hits",
                description = "The hottest tracks defining the global soundscape with Apple Music & Spotify master fidelity.",
                coverDrawableRes = R.drawable.ic_ignition_logo,
                trackIds = listOf("track_ignition_flagship", "track_khudi", "track_cyber_pulse", "track_golden_flight"),
                isOfflineDownloaded = true,
                creatorName = "Ignition Curators"
            ),
            Playlist(
                id = "pl_khudi_spiritual",
                title = "Khudi Se Khuda Tak Special",
                description = "Transcendent spiritual devotion featuring Jainam Nayak and soulful classical fusion.",
                coverDrawableRes = R.drawable.art_khudi_se_khuda,
                trackIds = listOf("track_khudi", "track_lotus_serenity"),
                isOfflineDownloaded = true,
                creatorName = "Soulful Radio"
            ),
            Playlist(
                id = "pl_lossless_showcase",
                title = "Spatial Audio & Hi-Res ALAC",
                description = "Experience immersive soundstage and 192kHz losslessly mastered tracks.",
                coverDrawableRes = R.drawable.art_ignition_banner,
                trackIds = listOf("track_ignition_flagship", "track_cyber_pulse", "track_khudi"),
                isOfflineDownloaded = false,
                creatorName = "Apple Music / Audio Lab"
            )
        )
    )
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    fun getAllTracks(): List<Track> {
        val liked = _likedTrackIds.value
        val downloaded = _offlineDownloadedIds.value

        val baseAndUploaded = defaultTracks + _uploadedTracks.value
        return baseAndUploaded.map { track ->
            track.copy(
                isLiked = liked.contains(track.id),
                isOfflineDownloaded = downloaded.contains(track.id)
            )
        }
    }

    fun getTrackById(id: String): Track? {
        return getAllTracks().find { it.id == id }
    }

    fun toggleLikeTrack(trackId: String) {
        val current = _likedTrackIds.value.toMutableSet()
        if (current.contains(trackId)) {
            current.remove(trackId)
        } else {
            current.add(trackId)
        }
        _likedTrackIds.value = current
        saveLikedTrackIds(current)
    }

    fun toggleDownloadOffline(trackId: String) {
        val current = _offlineDownloadedIds.value.toMutableSet()
        if (current.contains(trackId)) {
            current.remove(trackId)
        } else {
            current.add(trackId)
        }
        _offlineDownloadedIds.value = current
        saveDownloadedTrackIds(current)
    }

    fun toggleOfflineMode() {
        _isOfflineMode.value = !_isOfflineMode.value
    }

    fun setStreamingQuality(quality: String) {
        _streamingQuality.value = quality
        prefs.edit().putString("pref_quality", quality).apply()
    }

    fun syncNow(onCompleted: () -> Unit) {
        _isSyncing.value = true
        // Simulate real cloud handshake and state reconciliation
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _isSyncing.value = false
            _lastCloudSyncTime.value = "Just now"
            onCompleted()
        }, 1500)
    }

    fun switchActiveDevice(deviceId: String) {
        _devices.value = _devices.value.map { device ->
            device.copy(isCurrent = (device.id == deviceId))
        }
    }

    fun addUploadedTrack(
        title: String,
        artist: String,
        album: String,
        genre: String,
        audioUri: String?,
        coverUri: String?,
        lyricsText: String
    ): Track {
        val lyricLines = if (lyricsText.isNotBlank()) {
            lyricsText.lines().mapIndexed { index, line ->
                LyricLine(index * 6000L, line.trim())
            }
        } else {
            listOf(
                LyricLine(0L, "♬ [User Created Audio Stream] ♬"),
                LyricLine(6000L, title),
                LyricLine(12000L, "Uploaded by $artist to Ignition Nexus")
            )
        }

        val newTrack = Track(
            id = "upload_${System.currentTimeMillis()}",
            title = title,
            artist = artist.ifBlank { "You (Creator)" },
            album = album.ifBlank { "Studio Uploads" },
            durationMs = 184_000L,
            genre = genre.ifBlank { "Creator Mix" },
            coverDrawableRes = R.drawable.ic_ignition_logo,
            coverUri = coverUri,
            audioUri = audioUri,
            lyrics = lyricLines,
            isUploaded = true,
            isLiked = false,
            isOfflineDownloaded = true,
            audioQuality = "Creator Lossless 24-bit/48kHz",
            playsCount = 1,
            releaseYear = "2026",
            synthScaleIndex = ((_uploadedTracks.value.size + 1) % 4),
            fileSizeBytes = 28_400_000L
        )

        val updated = listOf(newTrack) + _uploadedTracks.value
        _uploadedTracks.value = updated
        saveUploadedTracks(updated)

        // Automatically mark user uploads as offline available
        val downloaded = _offlineDownloadedIds.value.toMutableSet()
        downloaded.add(newTrack.id)
        _offlineDownloadedIds.value = downloaded
        saveDownloadedTrackIds(downloaded)

        return newTrack
    }

    fun createPlaylist(title: String, description: String, trackIds: List<String>) {
        val newPl = Playlist(
            id = "pl_custom_${System.currentTimeMillis()}",
            title = title,
            description = description,
            coverDrawableRes = R.drawable.ic_ignition_logo,
            trackIds = trackIds,
            isOfflineDownloaded = false,
            creatorName = "You"
        )
        _playlists.value = listOf(newPl) + _playlists.value
    }

    private fun loadLikedTrackIds(): Set<String> {
        val raw = prefs.getString("liked_tracks", "track_khudi,track_ignition_flagship") ?: ""
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    private fun saveLikedTrackIds(ids: Set<String>) {
        prefs.edit().putString("liked_tracks", ids.joinToString(",")).apply()
    }

    private fun loadDownloadedTrackIds(): Set<String> {
        val raw = prefs.getString("downloaded_tracks", "track_khudi,track_ignition_flagship,track_cyber_pulse") ?: ""
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    private fun saveDownloadedTrackIds(ids: Set<String>) {
        prefs.edit().putString("downloaded_tracks", ids.joinToString(",")).apply()
    }

    private fun saveUploadedTracks(tracks: List<Track>) {
        val array = JSONArray()
        for (t in tracks) {
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("title", t.title)
            obj.put("artist", t.artist)
            obj.put("album", t.album)
            obj.put("durationMs", t.durationMs)
            obj.put("genre", t.genre)
            obj.put("audioUri", t.audioUri ?: "")
            obj.put("coverUri", t.coverUri ?: "")
            array.put(obj)
        }
        prefs.edit().putString("uploaded_tracks_json", array.toString()).apply()
    }

    private fun loadUploadedTracks(): List<Track> {
        val raw = prefs.getString("uploaded_tracks_json", null) ?: return emptyList()
        val list = mutableListOf<Track>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Track(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        artist = obj.getString("artist"),
                        album = obj.getString("album"),
                        durationMs = obj.optLong("durationMs", 180000L),
                        genre = obj.optString("genre", "Uploaded"),
                        coverDrawableRes = R.drawable.ic_ignition_logo,
                        coverUri = obj.optString("coverUri").ifEmpty { null },
                        audioUri = obj.optString("audioUri").ifEmpty { null },
                        lyrics = listOf(
                            LyricLine(0L, "♬ [User Created Audio Stream] ♬"),
                            LyricLine(6000L, obj.getString("title")),
                            LyricLine(12000L, "Uploaded by ${obj.getString("artist")} to Ignition Nexus")
                        ),
                        isUploaded = true,
                        isLiked = false,
                        isOfflineDownloaded = true,
                        audioQuality = "Creator Lossless 24-bit/48kHz",
                        playsCount = 12,
                        releaseYear = "2026",
                        synthScaleIndex = (i % 4),
                        fileSizeBytes = 24_000_000L
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
