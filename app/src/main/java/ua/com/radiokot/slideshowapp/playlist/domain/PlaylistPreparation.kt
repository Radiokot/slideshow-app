package ua.com.radiokot.slideshowapp.playlist.domain

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import ua.com.radiokot.slideshowapp.creative.domain.LocalCreativeRepository
import kotlin.time.Duration.Companion.seconds

class PlaylistPreparation(
    private val playlistRepository: PlaylistRepository,
    private val localCreativeRepository: LocalCreativeRepository,
) {
    /**
     * Prepares given [playlist] for playback:
     * - Saves locally all the creatives that aren't yet saved
     * - Sets the playlist as ready
     *
     * @return `true` if prepared successfully, `false` otherwise
     */
    suspend fun preparePlaylist(
        playlist: Playlist,
    ): Boolean = runCatching {

        val creativesToSave =
            playlist
                .items
                .map(Playlist.Item::creative)
                .distinct()
                .filterNot { it in localCreativeRepository }

        coroutineScope {
            creativesToSave.forEach { creative ->
                launch {
                    var attemptCount = 0
                    do {
                        try {
                            localCreativeRepository.saveCreativeLocally(creative)
                            break
                        } catch (e: Exception) {
                            if (++attemptCount >= 3) {
                                throw e
                            }
                            delay(5.seconds)
                        }
                    } while (true)
                }
            }
        }

        playlistRepository.setPlaylistReady(playlist)

    }.isSuccess
}
