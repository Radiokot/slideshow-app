package ua.com.radiokot.slideshowapp.database.data

import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import java.time.Instant
import kotlin.time.Duration.Companion.microseconds

fun PlaylistDbEntity(
    source: Playlist,
) = PlaylistDbEntity(
    key = source.key,
    lastModifiedMs = source.lastModified.toEpochMilli(),
    isReadyToPlay = source.isReadyToPlay,
    items = source.items.map { sourceItem ->
        PlaylistDbEntity.Item(
            durationMs = sourceItem.duration.inWholeMilliseconds,
            orderKey = sourceItem.orderKey,
            creativeKey = sourceItem.creative.key,
            creativeContentType = sourceItem.creative.contentType.name,
            creativeProperties = sourceItem.creative.properties,
        )
    },
)

fun PlaylistDbEntity.toPlaylist() = Playlist(
    key = key,
    lastModified = Instant.ofEpochMilli(lastModifiedMs),
    isReadyToPlay = isReadyToPlay,
    items = items.map { dbItem ->
        Playlist.Item(
            duration = dbItem.durationMs.microseconds,
            orderKey = dbItem.orderKey,
            creative = ua.com.radiokot.slideshowapp.creative.domain.Creative(
                key = dbItem.creativeKey,
                contentType = ua.com.radiokot.slideshowapp.creative.domain.Creative.Type.valueOf(
                    dbItem.creativeContentType
                ),
                properties = dbItem.creativeProperties,
            )
        )
    }
)
