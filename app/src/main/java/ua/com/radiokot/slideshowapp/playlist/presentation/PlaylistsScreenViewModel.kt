package ua.com.radiokot.slideshowapp.playlist.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository
import ua.com.radiokot.slideshowapp.session.domain.SignOutUseCase
import ua.com.radiokot.slideshowapp.util.eventSharedFlow
import ua.com.radiokot.slideshowapp.util.lazyLogger
import java.time.ZoneId

@Immutable
class PlaylistsScreenViewModel(
    val screenKey: String,
    private val signOutUseCase: SignOutUseCase,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val log by lazyLogger("PlaylistsScreenVM")
    private val _events: MutableSharedFlow<Event> = eventSharedFlow()
    val events: SharedFlow<Event> = _events

    val items: StateFlow<ImmutableList<PlaylistScreenItem>> =
        playlistRepository
            .getMostRecentPlaylistsFlow()
            .map { playlists ->
                playlists.map { playlist ->
                    PlaylistScreenItem(
                        lastModified =
                            playlist
                                .lastModified
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime(),
                        key = playlist.key,
                    )
                }.toPersistentList()
            }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.Eagerly, persistentListOf())

    fun onItemClick(
        item: PlaylistScreenItem,
    ) {
        val playlist = runBlocking {
            playlistRepository.getMostRecentPlaylist(
                key = item.key,
            )
        } ?: return

        if (playlist.isReadyToPlay) {
            log.debug {
                "onItemClick(): playlist is ready, proceeding to player:" +
                        "\nplaylist=$playlist"
            }

            _events.tryEmit(
                Event.ProceedToPlayer(
                    playlistKey = playlist.key,
                )
            )
        } else {
            log.debug {
                "onItemClick(): playlist is not ready, proceeding to preparation:" +
                        "\nplaylist=$playlist"
            }

            _events.tryEmit(
                Event.ProceedToPlaylistPreparation(
                    playlistKey = playlist.key,
                )
            )
        }
    }

    fun onSignOutAction() {
        signOutUseCase()
        _events.tryEmit(Event.SignedOut)
    }

    sealed interface Event {
        class ProceedToPlayer(
            val playlistKey: String,
        ) : Event

        class ProceedToPlaylistPreparation(
            val playlistKey: String,
        ) : Event

        object SignedOut : Event
    }
}
