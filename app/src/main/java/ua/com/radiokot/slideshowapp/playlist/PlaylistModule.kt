package ua.com.radiokot.slideshowapp.playlist

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.creative.creativeModule
import ua.com.radiokot.slideshowapp.io.ioModule
import ua.com.radiokot.slideshowapp.playlist.data.CachedPlaylistRepository
import ua.com.radiokot.slideshowapp.playlist.data.KtorPlayerBackend
import ua.com.radiokot.slideshowapp.playlist.data.PlayerBackend
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository

val playlistModule = module {

    includes(
        ioModule,
        creativeModule,
    )

    single {
        KtorPlayerBackend(
            baseUrl = "https://test.onsignage.com/PlayerBackend/".toHttpUrl(),
            client = get(),
        )
    } bind PlayerBackend::class

    single {
        CachedPlaylistRepository(
            screenKey = "7d47b6d7-8294-4b33-8887-066961d79993",
            playerBackend = get(),
        )
    } bind PlaylistRepository::class
}
