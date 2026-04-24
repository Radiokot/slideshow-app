package ua.com.radiokot.slideshowapp.playlist

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.backend.backendModule
import ua.com.radiokot.slideshowapp.creative.creativeModule
import ua.com.radiokot.slideshowapp.database.data.ScreenDatabase
import ua.com.radiokot.slideshowapp.database.databaseModule
import ua.com.radiokot.slideshowapp.playlist.data.CachedPlaylistRepository
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistPreparation
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository
import ua.com.radiokot.slideshowapp.playlist.presentation.PlaylistPreparationScreenViewModel
import ua.com.radiokot.slideshowapp.playlist.presentation.PlaylistsScreenViewModel
import kotlin.time.Duration.Companion.minutes

val playlistModule = module {

    includes(
        backendModule,
        databaseModule,
        creativeModule,
    )

    single {
        CachedPlaylistRepository(
            screenKey = "7d47b6d7-8294-4b33-8887-066961d79993",
            playerBackend = get(),
            playlistDao = get<ScreenDatabase>().playlists(),
            periodicBackendUpdateInterval = 1.minutes,
        )
    } bind PlaylistRepository::class

    single {
        PlaylistPreparation(
            playlistRepository = get(),
            localCreativeRepository = get(),
        )
    }

    viewModel {
        PlaylistsScreenViewModel(
            screenKey = "7d47b6d7-8294-4b33-8887-066961d79993",
            playlistRepository = get(),
        )
    }

    viewModel {
        PlaylistPreparationScreenViewModel(
            playlistRepository = get(),
            playlistPreparation = get(),
            parameters = getOrNull()
                ?: error("No PlaylistPreparationScreenViewModel.Parameters provided"),
        )
    }
}
