package ua.com.radiokot.slideshowapp.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistPreparation
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository
import ua.com.radiokot.slideshowapp.util.eventSharedFlow

class PlaylistPreparationScreenViewModel(
    private val playlistRepository: PlaylistRepository,
    private val playlistPreparation: PlaylistPreparation,
    private val parameters: Parameters,
) : ViewModel() {

    private val _events: MutableSharedFlow<Event> = eventSharedFlow()
    val events: SharedFlow<Event> = _events

    init {
        viewModelScope.launch {
            val mostRecentPlaylist =
                playlistRepository
                    .getMostRecentPlaylist(
                        key = parameters.playlistKey,
                    )
                    ?: error("Playlist ${parameters.playlistKey} not found")

            val readyPlaylist =
                playlistRepository
                    .getReadyPlaylist(
                        key = parameters.playlistKey,
                    )

            val isPreparedSuccessfully = playlistPreparation.preparePlaylist(mostRecentPlaylist)

            if (isPreparedSuccessfully) {
                _events.emit(
                    Event.ProceedToPlayer(
                        playlistKey = mostRecentPlaylist.key,
                        isContentOutdated = false,
                    )
                )
            } else if (readyPlaylist != null) {
                _events.emit(
                    Event.ProceedToPlayer(
                        playlistKey = mostRecentPlaylist.key,
                        isContentOutdated = true,
                    )
                )
            } else {
                _events.emit(Event.PreparationFailed)
            }
        }
    }

    sealed interface Event {
        class ProceedToPlayer(
            val playlistKey: String,
            val isContentOutdated: Boolean,
        ) : Event

        object PreparationFailed : Event
    }

    data class Parameters(
        val playlistKey: String,
    )
}
