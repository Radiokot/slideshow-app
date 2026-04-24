package ua.com.radiokot.slideshowapp.player

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.creative.creativeModule
import ua.com.radiokot.slideshowapp.player.presentation.PlayerScreenViewModel
import ua.com.radiokot.slideshowapp.playlist.playlistModule

val playerModule = module {

    includes(
        playlistModule,
        creativeModule,
    )

    viewModel {
        PlayerScreenViewModel(
            playlistRepository = get(),
            localCreativeRepository = get(),
            parameters = getOrNull()
                ?: error("No PlayerScreenViewModel.Parameters provided"),
        )
    }
}
