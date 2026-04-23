package ua.com.radiokot.slideshowapp.backend

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.backend.data.KtorPlayerBackend
import ua.com.radiokot.slideshowapp.backend.data.PlayerBackend
import ua.com.radiokot.slideshowapp.io.ioModule

val backendModule = module {

    includes(
        ioModule,
    )

    single {
        KtorPlayerBackend(
            baseUrl = "https://test.onsignage.com/PlayerBackend/".toHttpUrl(),
            client = get(),
        )
    } bind PlayerBackend::class
}
