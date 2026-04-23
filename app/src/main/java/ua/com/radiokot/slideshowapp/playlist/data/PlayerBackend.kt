package ua.com.radiokot.slideshowapp.playlist.data

import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.Serializable

interface PlayerBackend {

    /**
     * @return all playlists for the given screen key.
     */
    suspend fun getPlaylistItems(
        screenKey: String,
    ): PlaylistItemsResponse

    /**
     * @return a creative content streaming channel.
     */
    suspend fun getCreative(
        creativeKey: String,
    ): ByteReadChannel

    // region Models
    @Serializable
    data class PlaylistItemsResponse(
        val playlists: List<Playlist>,
    )

    @Serializable
    data class Playlist(
        val playlistKey: String,
        val playlistItems: List<PlaylistItem> = emptyList(),
    )

    @Serializable
    data class PlaylistItem(
        val creativeKey: String,
        val duration: Int,
        val modified: Long,
        val creativeProperties: String?,
        val orderKey: Int,
    )
    // endregion Models
}
