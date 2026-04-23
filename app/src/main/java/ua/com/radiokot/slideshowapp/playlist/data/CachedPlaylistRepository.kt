package ua.com.radiokot.slideshowapp.playlist.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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

    override suspend fun getReadyPlaylist(
        key: String,
    ): Playlist? =
        playlistDao
            .selectReadyToPlayPlaylist(
                key = key,
            )
            ?.let(PlaylistDbEntity::toPlaylist)

    override fun getMostRecentPlaylistsFlow(): Flow<List<Playlist>> =
        playlistDao
            .selectAllPlaylistsFlow()
            .map { playlists ->
                playlists
                    .groupBy(PlaylistDbEntity::key)
                    .map { (_, playlistVersions) ->
                        playlistVersions
                            .maxBy(PlaylistDbEntity::lastModifiedMs)
                            .toPlaylist()
                    }
            }
            .onStart {
                updatePlaylistsFromBackend()
            }

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
