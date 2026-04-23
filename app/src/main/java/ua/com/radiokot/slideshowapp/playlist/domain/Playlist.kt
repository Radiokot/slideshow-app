package ua.com.radiokot.slideshowapp.playlist.domain

import ua.com.radiokot.slideshowapp.creative.domain.Creative
import java.time.Instant
import kotlin.time.Duration

class Playlist(
    val key: String,
    val lastModified: Instant,
    val isReadyToPlay: Boolean,
    val items: List<Item>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Playlist) return false

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun toString(): String {
        return "Playlist(key='$key', lastModified=$lastModified, isReadyToPlay=$isReadyToPlay)"
    }

    data class Item(
        val duration: Duration,
        val orderKey: Int,
        val creative: Creative,
    )
}
