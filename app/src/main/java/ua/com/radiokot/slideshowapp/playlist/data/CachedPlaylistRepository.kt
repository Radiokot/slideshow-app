package ua.com.radiokot.slideshowapp.playlist.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ua.com.radiokot.slideshowapp.backend.data.PlayerBackend
import ua.com.radiokot.slideshowapp.backend.data.toPlaylists
import ua.com.radiokot.slideshowapp.database.data.PlaylistDao
import ua.com.radiokot.slideshowapp.database.data.PlaylistDbEntity
import ua.com.radiokot.slideshowapp.database.data.toPlaylist
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class CachedPlaylistRepository(
    private val screenKey: String,
    private val playerBackend: PlayerBackend,
    private val playlistDao: PlaylistDao,
    private val periodicBackendUpdateInterval: Duration,
) : PlaylistRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun getReadyPlaylist(
        key: String,
    ): Playlist? =
        playlistDao
            .selectReadyToPlayPlaylist(
                key = key,
            )
            ?.let(PlaylistDbEntity::toPlaylist)

    private var periodicBackendUpdatesJob: Job? = null

    private val mostRecentPlaylistsFlowWithUpdates: SharedFlow<List<Playlist>> =
        playlistDao
            .selectAllPlaylistsFlow()
            .map { dbEntities ->
                dbEntities
                    .groupBy(PlaylistDbEntity::key)
                    .map { (_, playlistVersions) ->
                        playlistVersions
                            .maxBy(PlaylistDbEntity::lastModifiedMs)
                            .toPlaylist()
                    }
            }
            .onStart {
                periodicBackendUpdatesJob = coroutineScope.launch {
                    do {
                        updatePlaylistsFromBackend()
                        delay(periodicBackendUpdateInterval)
                    } while (isActive)
                }
            }
            .onCompletion {
                periodicBackendUpdatesJob?.cancel()
            }
            .shareIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeout = 10.seconds,
                ),
                replay = 1,
            )

    override fun getMostRecentPlaylistsFlow(): Flow<List<Playlist>> =
        mostRecentPlaylistsFlowWithUpdates

    override suspend fun getMostRecentPlaylist(
        key: String,
    ): Playlist? =
        playlistDao
            .selectMostRecentPlaylist(
                key = key,
            )
            ?.let(PlaylistDbEntity::toPlaylist)

    override suspend fun setPlaylistReady(
        playlist: Playlist,
    ) {
        playlistDao.setPlaylistReady(
            key = playlist.key,
            lastModifiedMs = playlist.lastModified.toEpochMilli(),
        )
    }

    private suspend fun updatePlaylistsFromBackend() = runCatching {

        val cachedPlaylistKeys =
            playlistDao
                .selectDistinctPlaylistKeys()
                .toSet()

        val backendPlaylists =
            playerBackend
                .getPlaylistItems(screenKey)
                .toPlaylists()

        val backendPlaylistKeys =
            backendPlaylists
                .mapTo(mutableSetOf(), Playlist::key)

        playlistDao.insertMissingPlaylists(
            entities =
                backendPlaylists
                    .map(::PlaylistDbEntity),
        )
        playlistDao.deletePlaylists(
            keys = (cachedPlaylistKeys - backendPlaylistKeys).toList(),
        )
    }
}
