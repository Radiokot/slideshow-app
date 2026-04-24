package ua.com.radiokot.slideshowapp.player

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.creative.creativeModule
import ua.com.radiokot.slideshowapp.player.presentation.PlayerScreenViewModel
import ua.com.radiokot.slideshowapp.playlist.playlistModule
import ua.com.radiokot.slideshowapp.session.util.userSessionScope

val playerModule = module {

    includes(
        playlistModule,
        creativeModule,
    )

    userSessionScope {
        viewModel {
            PlayerScreenViewModel(
                playlistRepository = get(),
                localCreativeRepository = get(),
                playlistPreparation = get(),
                parameters = getOrNull()
                    ?: error("No PlayerScreenViewModel.Parameters provided"),
            )
        }
    }
}
