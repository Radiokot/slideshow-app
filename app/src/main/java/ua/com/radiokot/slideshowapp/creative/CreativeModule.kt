package ua.com.radiokot.slideshowapp.creative

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.backend.backendModule
import ua.com.radiokot.slideshowapp.creative.data.FsLocalCreativeRepository
import ua.com.radiokot.slideshowapp.creative.domain.LocalCreativeRepository
import java.io.File

val creativeModule = module {

    includes(
        backendModule,
    )

    single {
        FsLocalCreativeRepository(
            playerBackend = get(),
            creativeDirectory = File(
                androidContext().cacheDir,
                "Creatives"
            ),
        )
    } bind LocalCreativeRepository::class
}
