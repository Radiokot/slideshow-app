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
import ua.com.radiokot.slideshowapp.session.domain.UserSession
import ua.com.radiokot.slideshowapp.session.sessionModule
import ua.com.radiokot.slideshowapp.session.util.userSessionScope
import kotlin.time.Duration.Companion.minutes

val playlistModule = module {

    includes(
        backendModule,
        databaseModule,
        creativeModule,
        sessionModule,
    )

    userSessionScope {
        scoped {
            CachedPlaylistRepository(
                screenKey = get<UserSession>().screenKey,
                playerBackend = get(),
                playlistDao = get<ScreenDatabase>().playlists(),
                periodicBackendUpdateInterval = 1.minutes,
            )
        } bind PlaylistRepository::class

        scoped {
            PlaylistPreparation(
                playlistRepository = get(),
                localCreativeRepository = get(),
            )
        }

        viewModel {
            PlaylistsScreenViewModel(
                screenKey = get<UserSession>().screenKey,
                signOutUseCase = get(),
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
}
