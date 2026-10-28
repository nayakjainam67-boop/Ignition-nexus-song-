package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MusicViewModel
import com.example.ui.NavScreen
import com.example.ui.components.AiSoundControlCenterSheet
import com.example.ui.components.AiSoundStudioDialog
import com.example.ui.components.EqualizerDialog
import com.example.ui.components.FullPlayerModal
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.SocialShareDialog
import com.example.ui.screens.CloudSyncScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.UploadStudioScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val screen: NavScreen,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun MainAppContent(viewModel: MusicViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isFullPlayerVisible by viewModel.isFullPlayerVisible.collectAsStateWithLifecycle()
    val isLyricsActive by viewModel.isLyricsActive.collectAsStateWithLifecycle()
    val isVisualizerActive by viewModel.isVisualizerActive.collectAsStateWithLifecycle()
    val isShareDialogOpen by viewModel.isShareDialogOpen.collectAsStateWithLifecycle()
    val trackToShare by viewModel.trackToShare.collectAsStateWithLifecycle()
    val isEqualizerDialogOpen by viewModel.isEqualizerDialogOpen.collectAsStateWithLifecycle()
    val isAiSoundDialogOpen by viewModel.isAiSoundDialogOpen.collectAsStateWithLifecycle()
    val isSoundControlCenterOpen by viewModel.isSoundControlCenterOpen.collectAsStateWithLifecycle()
    val aiProfile by viewModel.aiProfile.collectAsStateWithLifecycle()
    val dolbyChannels by viewModel.dolbyChannels.collectAsStateWithLifecycle()
    val autoAiTuningState by viewModel.autoAiTuningState.collectAsStateWithLifecycle()
    val leftChannelDb by viewModel.leftChannelDb.collectAsStateWithLifecycle()
    val rightChannelDb by viewModel.rightChannelDb.collectAsStateWithLifecycle()

    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val isShuffled by viewModel.isShuffled.collectAsStateWithLifecycle()
    val visualizerAmplitudes by viewModel.visualizerAmplitudes.collectAsStateWithLifecycle()
    val equalizerPreset by viewModel.equalizerPreset.collectAsStateWithLifecycle()
    val spatialAudioEnabled by viewModel.spatialAudioEnabled.collectAsStateWithLifecycle()

    val displayedTracks by viewModel.displayedTracks.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val activeCategory by viewModel.activeCategory.collectAsStateWithLifecycle()
    val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastCloudSyncTime.collectAsStateWithLifecycle()
    val streamingQuality by viewModel.streamingQuality.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem(NavScreen.HOME, "Home", Icons.Default.Home, "nav_home"),
        NavItem(NavScreen.SEARCH, "Search", Icons.Default.Search, "nav_search"),
        NavItem(NavScreen.LIBRARY, "Library", Icons.Default.LibraryMusic, "nav_library"),
        NavItem(NavScreen.UPLOAD_STUDIO, "Upload", Icons.Default.Upload, "nav_upload"),
        NavItem(NavScreen.CLOUD_SYNC, "Cloud Sync", Icons.Default.CloudSync, "nav_cloud")
    )

    // Back handling
    BackHandler(enabled = isFullPlayerVisible || currentScreen != NavScreen.HOME) {
        if (isFullPlayerVisible) {
            viewModel.hideFullPlayer()
        } else if (currentScreen != NavScreen.HOME) {
            viewModel.setScreen(NavScreen.HOME)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BackgroundDark,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    // Mini Player Bar
                    if (currentTrack != null && !isFullPlayerVisible) {
                        MiniPlayerBar(
                            track = currentTrack!!,
                            isPlaying = isPlaying,
                            currentPositionMs = currentPositionMs,
                            durationMs = durationMs,
                            isLiked = currentTrack!!.isLiked,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onToggleLike = { viewModel.toggleLikeTrack(currentTrack!!.id) },
                            onSkipNext = { viewModel.skipNext() },
                            onExpandFullPlayer = { viewModel.showFullPlayer() }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = SurfaceDark,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.setScreen(item.screen) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BackgroundDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    NavScreen.HOME -> HomeScreen(
                        tracks = displayedTracks,
                        playlists = playlists,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        activeCategory = activeCategory,
                        isOfflineMode = isOfflineMode,
                        isAutoAiActive = aiProfile.isAutoAiEnhanced,
                        isAutoTuningActive = autoAiTuningState.isAutoTuningEnabled,
                        tuningModeTitle = autoAiTuningState.tuningMode.title,
                        splDecibels = autoAiTuningState.telemetry.currentSplDecibels,
                        onCategorySelected = { viewModel.setActiveCategory(it) },
                        onTrackSelected = { track, list -> viewModel.playTrack(track, list) },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onToggleLike = { viewModel.toggleLikeTrack(it) },
                        onToggleDownload = { viewModel.toggleDownloadOffline(it) },
                        onOpenShareDialog = { viewModel.openShareDialog(it) },
                        onOpenAiSoundDialog = { viewModel.openAiSoundDialog() },
                        onOpenSoundControlCenter = { viewModel.openSoundControlCenter() },
                        onNavigate = { viewModel.setScreen(it) }
                    )

                    NavScreen.SEARCH -> SearchScreen(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        filteredTracks = displayedTracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        onTrackSelected = { track, list -> viewModel.playTrack(track, list) },
                        onToggleLike = { viewModel.toggleLikeTrack(it) },
                        onToggleDownload = { viewModel.toggleDownloadOffline(it) },
                        onOpenShareDialog = { viewModel.openShareDialog(it) }
                    )

                    NavScreen.LIBRARY -> LibraryScreen(
                        tracks = displayedTracks,
                        playlists = playlists,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        isOfflineMode = isOfflineMode,
                        onToggleOfflineMode = { viewModel.toggleOfflineMode() },
                        onTrackSelected = { track, list -> viewModel.playTrack(track, list) },
                        onToggleLike = { viewModel.toggleLikeTrack(it) },
                        onToggleDownload = { viewModel.toggleDownloadOffline(it) },
                        onOpenShareDialog = { viewModel.openShareDialog(it) },
                        onCreatePlaylist = { title, desc -> viewModel.createNewPlaylist(title, desc, emptyList()) }
                    )

                    NavScreen.UPLOAD_STUDIO -> UploadStudioScreen(
                        onUploadSuccess = { newTrack ->
                            viewModel.uploadCustomMusic(
                                title = newTrack.title,
                                artist = newTrack.artist,
                                album = newTrack.album,
                                genre = newTrack.genre,
                                audioUri = newTrack.audioUri,
                                coverUri = newTrack.coverUri,
                                lyrics = newTrack.lyrics.joinToString("\n") { it.text }
                            )
                            viewModel.setScreen(NavScreen.LIBRARY)
                        }
                    )

                    NavScreen.CLOUD_SYNC -> CloudSyncScreen(
                        devices = devices,
                        isSyncing = isSyncing,
                        lastSyncTime = lastSyncTime,
                        isOfflineMode = isOfflineMode,
                        streamingQuality = streamingQuality,
                        onToggleOfflineMode = { viewModel.toggleOfflineMode() },
                        onSyncNow = { viewModel.syncCloudNow() },
                        onSwitchDevice = { viewModel.switchActiveDevice(it) },
                        onSetStreamingQuality = { viewModel.setStreamingQuality(it) }
                    )
                }
            }
        }

        // Full Screen Player Modal with Slide In/Out Animation
        AnimatedVisibility(
            visible = isFullPlayerVisible && currentTrack != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            currentTrack?.let { track ->
                FullPlayerModal(
                    track = track,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    repeatMode = repeatMode,
                    isShuffled = isShuffled,
                    visualizerAmplitudes = visualizerAmplitudes,
                    isLyricsActive = isLyricsActive,
                    isVisualizerActive = isVisualizerActive,
                    isLiked = track.isLiked,
                    isOfflineDownloaded = track.isOfflineDownloaded,
                    spatialAudioEnabled = spatialAudioEnabled,
                    onDismiss = { viewModel.hideFullPlayer() },
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onSeekTo = { viewModel.seekTo(it) },
                    onSkipNext = { viewModel.skipNext() },
                    onSkipPrevious = { viewModel.skipPrevious() },
                    onToggleRepeat = { viewModel.toggleRepeat() },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onToggleLike = { viewModel.toggleLikeTrack(track.id) },
                    onToggleDownloadOffline = { viewModel.toggleDownloadOffline(track.id) },
                    onToggleLyrics = { viewModel.toggleLyricsMode() },
                    onToggleVisualizer = { viewModel.toggleVisualizerMode() },
                    onOpenShareDialog = { viewModel.openShareDialog(track) },
                    onOpenEqualizerDialog = { viewModel.openEqualizerDialog() },
                    onOpenAiSoundDialog = { viewModel.openAiSoundDialog() },
                    onOpenSoundControlCenter = { viewModel.openSoundControlCenter() }
                )
            }
        }

        // Social Share Dialog
        if (isShareDialogOpen && trackToShare != null) {
            SocialShareDialog(
                track = trackToShare!!,
                onDismiss = { viewModel.closeShareDialog() }
            )
        }

        // Equalizer & Spatial Audio Dialog
        if (isEqualizerDialogOpen) {
            EqualizerDialog(
                currentPreset = equalizerPreset,
                spatialAudioEnabled = spatialAudioEnabled,
                onPresetSelected = { viewModel.setEqualizerPreset(it) },
                onToggleSpatialAudio = { viewModel.toggleSpatialAudio() },
                onDismiss = { viewModel.closeEqualizerDialog() }
            )
        }

        // Auto AI Sound Lab & Dolby Atmos 6.2 Dialog
        if (isAiSoundDialogOpen) {
            AiSoundStudioDialog(
                aiProfile = aiProfile,
                dolbyChannels = dolbyChannels,
                availableDevices = viewModel.aiSoundOptimizer.availableDevices,
                onToggleAutoAi = { viewModel.toggleAutoAiEnhance() },
                onToggleDolbyAtmos = { viewModel.toggleDolbyAtmos62() },
                onSetDolbyMode = { viewModel.setDolbyMode(it) },
                onSelectOutputDevice = { viewModel.selectOutputDevice(it) },
                onUpdateHearingCalibration = { viewModel.updateHearingCalibration(it) },
                onDismiss = { viewModel.closeAiSoundDialog() }
            )
        }

        // Sound Controlled Management AI Smart System Sheet
        if (isSoundControlCenterOpen) {
            AiSoundControlCenterSheet(
                tuningState = autoAiTuningState,
                leftChannelDb = leftChannelDb,
                rightChannelDb = rightChannelDb,
                onToggleAutoTuning = { viewModel.toggleAutoTuning() },
                onSetTuningMode = { viewModel.setTuningMode(it) },
                onSetEnvironmentMode = { viewModel.setEnvironmentMode(it) },
                onToggleDolbyAutoAdjusted = { viewModel.toggleDolbyAutoAdjusted() },
                onToggleHearingProtection = { viewModel.toggleHearingProtection() },
                onSetMaxDecibelCap = { viewModel.setMaxDecibelCap(it) },
                onToggleVolumeNormalizer = { viewModel.toggleVolumeNormalizer() },
                onToggleTransientEnhancer = { viewModel.toggleTransientEnhancer() },
                onDismiss = { viewModel.closeSoundControlCenter() }
            )
        }
    }
}
