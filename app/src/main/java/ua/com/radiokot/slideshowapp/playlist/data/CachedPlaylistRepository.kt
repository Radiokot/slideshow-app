package ua.com.radiokot.slideshowapp.playlist.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ua.com.radiokot.slideshowapp.backend.data.PlayerBackend
import ua.com.radiokot.slideshowapp.backend.data.toPlaylists
import ua.com.radiokot.slideshowapp.database.data.PlaylistDao
import ua.com.radiokot.slideshowapp.database.data.PlaylistDbEntity
import ua.com.radiokot.slideshowapp.database.data.toPlaylist
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository

class CachedPlaylistRepository(
    private val screenKey: String,
    private val playerBackend: PlayerBackend,
    private val playlistDao: PlaylistDao,
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

    private val sharedMostRecentPlaylistsFlow: SharedFlow<List<Playlist>> =
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
            .shareIn(coroutineScope, SharingStarted.Eagerly, replay = 1)

    private var updateFromBackendJob: Job? = null

    override fun getMostRecentPlaylistsFlow(): Flow<List<Playlist>> =
        sharedMostRecentPlaylistsFlow
            .onSubscription {
                if (updateFromBackendJob?.isActive != true) {
                    updateFromBackendJob = coroutineScope.launch {
                        updatePlaylistsFromBackend()
                    }
                }
            }

    override suspend fun getMostRecentPlaylist(
        key: String,
    ): Playlist? =
        sharedMostRecentPlaylistsFlow
            .first()
            .find { it.key == key }

    override suspend fun setPlaylistReady(
        playlist: Playlist,
    ) {
        playlistDao.selectReadyToPlayPlaylist(
            key = playlist.key,
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
