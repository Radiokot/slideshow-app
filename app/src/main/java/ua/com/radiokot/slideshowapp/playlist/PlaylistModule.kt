package ua.com.radiokot.slideshowapp.playlist

import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.backend.backendModule
import ua.com.radiokot.slideshowapp.creative.creativeModule
import ua.com.radiokot.slideshowapp.playlist.data.CachedPlaylistRepository
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistPreparation
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository

val playlistModule = module {

    includes(
        backendModule,
        creativeModule,
    )

    single {
        CachedPlaylistRepository(
            screenKey = "7d47b6d7-8294-4b33-8887-066961d79993",
            playerBackend = get(),
        )
    } bind PlaylistRepository::class

    single {
        PlaylistPreparation(
            playlistRepository = get(),
            localCreativeRepository = get(),
        )
    }
}
