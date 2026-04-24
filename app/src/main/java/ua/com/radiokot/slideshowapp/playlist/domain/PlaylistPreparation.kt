package ua.com.radiokot.slideshowapp.playlist.domain

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.com.radiokot.slideshowapp.creative.domain.LocalCreativeRepository
import ua.com.radiokot.slideshowapp.util.lazyLogger
import kotlin.time.Duration.Companion.seconds

class PlaylistPreparation(
    private val playlistRepository: PlaylistRepository,
    private val localCreativeRepository: LocalCreativeRepository,
) {
    private val log by lazyLogger("PlaylistPreparation")

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

        log.debug {
            "preparePlaylist(): starting preparation:" +
                    "\nplaylist=$playlist," +
                    "\ncreativesToSave=${creativesToSave.size}"
        }

        coroutineScope {
            creativesToSave.forEach { creative ->
                launch {
                    var attemptCount = 0
                    val maxAttemptCount = 3
                    do {
                        try {
                            localCreativeRepository.saveCreativeLocally(creative)
                            break
                        } catch (e: Exception) {
                            if (++attemptCount == maxAttemptCount) {
                                log.error(e) {
                                    "preparePlaylist(): failed saving creative after $maxAttemptCount attempts:" +
                                            "\ncreative=$creative"
                                }
                                throw e
                            }
                            delay(5.seconds)
                        }
                    } while (true)
                }
            }
        }

        playlistRepository.setPlaylistReady(playlist)

        log.debug {
            "preparePlaylist(): preparation done, set ready:" +
                    "\nplaylist=$playlist"
        }
    }.isSuccess
}
