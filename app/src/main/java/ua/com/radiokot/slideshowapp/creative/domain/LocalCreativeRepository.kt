package ua.com.radiokot.slideshowapp.creative.domain

import android.net.Uri

interface LocalCreativeRepository {

    /**
     * @return URI of the creative local file that can be used to play it,
     * or `null` if the creative is not found locally.
     */
    suspend fun getLocalCreativeUri(
        creative: Creative,
    ): Uri?

    suspend operator fun contains(
        creative: Creative,
    ): Boolean =
        getLocalCreativeUri(creative) != null

    /**
     * Saves the creative content locally.
     */
    suspend fun saveCreativeLocally(
        creative: Creative,
    )
}
