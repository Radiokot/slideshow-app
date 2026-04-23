package ua.com.radiokot.slideshowapp.playlist.domain

import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    /**
     * @return ready to play version of the playlist with the given [key],
     * `null` if there is no such version.
     */
    suspend fun getReadyPlaylist(
        key: String,
    ): Playlist?

    /**
     * @return a flow of most recent playlists.
     */
    fun getMostRecentPlaylistsFlow(): Flow<List<Playlist>>

    /**
     * @return most recent playlist with the given [key],
     * `null` if there's no such playlist.
     */
    suspend fun getMostRecentPlaylist(
        key: String,
    ): Playlist?

    /**
     * Sets the given [playlist] as ready to play.
     */
    suspend fun setPlaylistReady(
        playlist: Playlist,
    )
}
