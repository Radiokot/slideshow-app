package ua.com.radiokot.slideshowapp.creative

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
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

    single(named(DIRECTORY_CREATIVES)) {
        File(
            androidContext().noBackupFilesDir,
            "Creatives"
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    single {
        FsLocalCreativeRepository(
            playerBackend = get(),
            creativeDirectory = get(named(DIRECTORY_CREATIVES)),
        )
    } bind LocalCreativeRepository::class
}

const val DIRECTORY_CREATIVES = "creatives"
