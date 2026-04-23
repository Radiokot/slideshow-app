package ua.com.radiokot.slideshowapp.playlist.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.com.radiokot.slideshowapp.backend.data.PlayerBackend
import ua.com.radiokot.slideshowapp.backend.data.toPlaylists
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository

class CachedPlaylistRepository(
    private val screenKey: String,
    private val playerBackend: PlayerBackend,
) : PlaylistRepository {

    override suspend fun getReadyPlaylist(
        key: String,
    ): Playlist? {
        return null
    }

    override fun getPlaylistsFlow(): Flow<List<Playlist>> = flow {
        // TODO emit offline list first.

        val remotePlaylists =
            playerBackend
                .getPlaylistItems(
                    screenKey = screenKey,
                )
                .toPlaylists()

        emit(remotePlaylists)
    }

    override suspend fun setPlaylistReady(playlist: Playlist) {
        TODO("Not yet implemented")
    }
}
