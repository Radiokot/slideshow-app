package ua.com.radiokot.slideshowapp.backend.data

import ua.com.radiokot.slideshowapp.backend.data.PlayerBackend.PlaylistItem
import ua.com.radiokot.slideshowapp.creative.domain.Creative
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

fun PlayerBackend.PlaylistItemsResponse.toPlaylists(): List<Playlist> =
    playlists
        .map { (playlistKey, playlistItems) ->
            Playlist(
                key = playlistKey,
                lastModified =
                    playlistItems
                        .maxOf(PlaylistItem::modified)
                        .let(Instant::ofEpochMilli),
                items =
                    playlistItems.map { playlistItem ->
                        Playlist.Item(
                            duration = playlistItem.duration.seconds,
                            orderKey = playlistItem.orderKey,
                            creative = playlistItem.toCreative(),
                        )
                    },
                isReadyToPlay = false,
            )
        }

fun PlaylistItem.toCreative(): Creative {
    val extension =
        creativeKey
            .substringAfter(
                delimiter = '.',
                missingDelimiterValue = "",
            )
            .takeIf(String::isNotEmpty)
            ?.lowercase()
            ?: error("Unable to find a creative extension in the key: $creativeKey")

    val contentType = when (extension) {
        "jpg" -> Creative.Type.Image
        "mp4" -> Creative.Type.Video
        else -> error("Unsupported creative extension: $extension")
    }

    val properties =
        creativeProperties
            ?.split(',')
            ?.associate { property ->
                val (key, value) =
                    property
                        .split('=')
                        .takeIf { it.size == 2 }
                        ?: error("Unsupported creative property format: $property")
                key to value
            }

    return Creative(
        key = creativeKey,
        contentType = contentType,
        properties = properties ?: emptyMap(),
    )
}
