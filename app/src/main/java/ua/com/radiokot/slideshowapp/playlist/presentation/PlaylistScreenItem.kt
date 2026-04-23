package ua.com.radiokot.slideshowapp.playlist.presentation

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

@Immutable
data class PlaylistScreenItem(
    val lastModified: LocalDateTime,
    val key: String,
)
