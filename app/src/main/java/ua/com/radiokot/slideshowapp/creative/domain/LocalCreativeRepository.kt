package ua.com.radiokot.slideshowapp.creative.domain

import android.net.Uri
import io.ktor.utils.io.ByteReadChannel

interface LocalCreativeRepository {

    /**
     * @return URI of the creative local file that can be used to play it,
     * or `null` if the creative is not found locally.
     */
    suspend fun getLocalCreativeUri(
        creative: Creative,
    ): Uri?

    /**
     * Saves the creative content locally.
     */
    suspend fun saveCreativeLocally(
        creative: Creative,
        content: ByteReadChannel,
    )
}
