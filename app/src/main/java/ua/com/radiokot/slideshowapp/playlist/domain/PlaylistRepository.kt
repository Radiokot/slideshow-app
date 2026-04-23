package ua.com.radiokot.slideshowapp.playlist.domain

interface PlaylistRepository {

    /**
     * @return ready to play version of the playlist with the given [key],
     * `null` if there is no such version.
     */
    suspend fun getReadyPlaylist(
        key: String,
    ): Playlist?
}
