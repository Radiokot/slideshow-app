package ua.com.radiokot.slideshowapp.playlist

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.io.ioModule
import ua.com.radiokot.slideshowapp.playlist.data.KtorPlayerBackend
import ua.com.radiokot.slideshowapp.playlist.data.PlayerBackend
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository

val playlistModule = module {

    includes(
        ioModule,
    )

    single {
        KtorPlayerBackend(
            baseUrl = "https://test.onsignage.com/PlayerBackend/".toHttpUrl(),
            client = get(),
        )
    } bind PlayerBackend::class

    single {
        // TODO provide actual impl
        object : PlaylistRepository {
            override suspend fun getReadyPlaylist(key: String): Playlist =
                Playlist()
        }
    } bind PlaylistRepository::class
}
