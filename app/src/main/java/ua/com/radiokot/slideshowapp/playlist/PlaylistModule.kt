package ua.com.radiokot.slideshowapp.playlist

import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository

val playlistModule = module {

    single {
        // TODO provide actual impl
        object : PlaylistRepository {
            override suspend fun getReadyPlaylist(key: String): Playlist =
                Playlist()
        }
    } bind PlaylistRepository::class
}
