package ua.com.radiokot.slideshowapp.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository
import java.time.ZoneId

class PlaylistsScreenViewModel(
    val screenKey: String,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

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
}
