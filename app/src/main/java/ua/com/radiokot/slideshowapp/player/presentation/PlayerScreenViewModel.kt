@file:OptIn(ExperimentalCoroutinesApi::class)

package ua.com.radiokot.slideshowapp.player.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import ua.com.radiokot.slideshowapp.creative.domain.Creative
import ua.com.radiokot.slideshowapp.creative.domain.LocalCreativeRepository
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistPreparation
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository
import ua.com.radiokot.slideshowapp.util.coroutineScopeThatCancelsWith
import ua.com.radiokot.slideshowapp.util.lazyLogger

@Immutable
class PlayerScreenViewModel(
    private val playlistRepository: PlaylistRepository,
    private val localCreativeRepository: LocalCreativeRepository,
    private val playlistPreparation: PlaylistPreparation,
    private val parameters: Parameters,
) : ViewModel() {

    private val log by lazyLogger("PlayerScreenVM")

    private val playlist: MutableStateFlow<Playlist> = runBlocking {
        MutableStateFlow(
            value = playlistRepository.getReadyPlaylist(
                key = parameters.playlistKey,
            ) ?: error("Playlist ${parameters.playlistKey} is not ready to play")
        )
    }

    private val skipCurrentItemAction: MutableSharedFlow<Unit> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val playerItem: StateFlow<PlayerItem?> = runBlocking {
        playlist
            .onEach { playlist ->
                log.info {
                    "Playing playlist ${playlist.key} last modified ${playlist.lastModified}"
                }
            }
            .flatMapLatest(::createPlayerItemFlow)
            .onEach {
                log.debug {
                    "playerItem: switching item:" +
                            "\nitem=$it"
                }
            }
            .stateIn(coroutineScopeThatCancelsWith(viewModelScope))
    }

    init {
        // When playlists are updated, check if there's a newer version,
        // prepare it and start playing.
        playlistRepository
            .getMostRecentPlaylistsFlow()
            .conflate()
            .onEach { playlists ->
                val currentPlaylist = playlist.value
                val newerVersion = playlists.find {
                    it.key == currentPlaylist.key
                            && it.lastModified > currentPlaylist.lastModified
                }

                if (newerVersion != null) {
                    log.debug {
                        "init(): preparing newer version of the playlist:" +
                                "\nnewerVersion=$newerVersion"
                    }
                    log.info {
                        "Found newer version of the playlist, last modified ${newerVersion.lastModified}"
                    }

                    val isPreparedSuccessfully =
                        playlistPreparation.preparePlaylist(newerVersion)

                    log.debug {
                        "init(): done preparing newer version of the playlist:" +
                                "\nisPreparedSuccessfully=$isPreparedSuccessfully"
                    }

                    if (isPreparedSuccessfully) {
                        playlist.value = newerVersion
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun createPlayerItemFlow(
        playlist: Playlist,
    ): Flow<PlayerItem?> {

        val playlistItems =
            playlist
                .items
                .sortedBy(Playlist.Item::orderKey)

        val playerItems =
            playlistItems
                .map { item ->
                    val localCreativeUri =
                        localCreativeRepository.getLocalCreativeUri(item.creative)
                            ?: error("Missing creative locally: ${item.creative}")

                    PlayerItem(
                        key = "${item.creative.key}_${item.orderKey}",
                        content = when (item.creative.contentType) {

                            Creative.Type.Image ->
                                PlayerItem.Content.Image(
                                    uri = localCreativeUri,
                                )

                            Creative.Type.Video -> {
                                PlayerItem.Content.Video(
                                    uri = localCreativeUri,
                                    volumePercent = item.creative.soundVolumePercent ?: 100f,
                                )
                            }
                        },
                    )
                }

        return flow {
            var currentItemIndex = 0
            do {
                val currentItem = playerItems.getOrNull(currentItemIndex)
                emit(currentItem)

                // Wait either for the presentation to finish
                // or for the user to skip the current item.
                merge(
                    flow {
                        delay(playlistItems[currentItemIndex].duration)
                        emit(Unit)
                    },
                    skipCurrentItemAction,
                ).first()

                currentItemIndex = (currentItemIndex + 1) % playerItems.size
            } while (true)
        }
    }

    fun onSkipCurrentItemAction() {
        skipCurrentItemAction.tryEmit(Unit)
    }

    data class Parameters(
        val playlistKey: String,
    )
}
