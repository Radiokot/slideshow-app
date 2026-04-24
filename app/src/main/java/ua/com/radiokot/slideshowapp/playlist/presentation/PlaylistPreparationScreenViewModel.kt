package ua.com.radiokot.slideshowapp.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistPreparation
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository
import ua.com.radiokot.slideshowapp.util.eventSharedFlow
import ua.com.radiokot.slideshowapp.util.lazyLogger

class PlaylistPreparationScreenViewModel(
    private val playlistRepository: PlaylistRepository,
    private val playlistPreparation: PlaylistPreparation,
    private val parameters: Parameters,
) : ViewModel() {

    private val log by lazyLogger("PlaylistPreparationScreenVM")
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
                log.debug {
                    "init(): prepared successfully, proceeding to player"
                }
                log.info {
                    "Playlist ${mostRecentPlaylist.key} prepared"
                }

                _events.emit(
                    Event.ProceedToPlayer(
                        playlistKey = mostRecentPlaylist.key,
                        isContentOutdated = false,
                    )
                )
            } else if (readyPlaylist != null) {
                log.debug {
                    "init(): preparation failed, proceeding to player with the ready version:" +
                            "\nreadyPlaylist=$readyPlaylist"
                }
                log.info {
                    "Older version of the playlist ${mostRecentPlaylist.key} will be played," +
                            "as its preparation failed"
                }

                _events.emit(
                    Event.ProceedToPlayer(
                        playlistKey = mostRecentPlaylist.key,
                        isContentOutdated = true,
                    )
                )
            } else {
                log.debug {
                    "init(): preparation failed, no ready playlist version found, emitting failure"
                }
                log.info {
                    "Playlist ${mostRecentPlaylist.key} preparation failed, no ready version found"
                }

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
