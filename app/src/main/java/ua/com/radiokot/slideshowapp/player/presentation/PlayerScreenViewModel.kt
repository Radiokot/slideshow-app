@file:OptIn(ExperimentalCoroutinesApi::class)

package ua.com.radiokot.slideshowapp.player.presentation

import androidx.compose.runtime.Immutable
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository

@Immutable
class PlayerScreenViewModel(
    private val playlistRepository: PlaylistRepository,
    private val parameters: Parameters,
) : ViewModel() {

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

    val playerItem: StateFlow<PlayerItem?> =
        playlist
            .flatMapLatest(::createPlayerItemFlow)
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private fun createPlayerItemFlow(
        playlist: Playlist,
    ): Flow<PlayerItem?> {

        // TODO use actual items.
        val items = listOf(
            PlayerItem(
                key = "1",
                content = PlayerItem.Content.Image(
                    uri = "https://picsum.photos/720".toUri(),
                )
            ),
            PlayerItem(
                key = "2",
                content = PlayerItem.Content.Video(
                    uri = "https://radiokot.com.ua/Radiokot/rock_cat.mp4".toUri(),
                    volumePercent = 100f,
                )
            ),
            null,
        )

        return flow {
            var currentItemIndex = 0
            do {
                val currentItem = items.getOrNull(currentItemIndex)
                emit(currentItem)

                // TODO use the actual duration
                val presentationDuration = 20000L

                // Wait either for the presentation to finish
                // or for the user to skip the current item.
                merge(
                    flow {
                        delay(presentationDuration)
                        emit(Unit)
                    },
                    skipCurrentItemAction,
                ).first()

                currentItemIndex = (currentItemIndex + 1) % items.size
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
