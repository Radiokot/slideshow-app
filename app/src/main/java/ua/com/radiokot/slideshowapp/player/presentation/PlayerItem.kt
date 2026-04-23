package ua.com.radiokot.slideshowapp.player.presentation

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class PlayerItem(
    val key: String,
    val content: Content,
) {
    @Immutable
    sealed interface Content {
        val uri: Uri

        data class Image(
            override val uri: Uri,
        ) : Content

        data class Video(
            override val uri: Uri,
            val volumePercent: Float,
        ) : Content
    }
}
