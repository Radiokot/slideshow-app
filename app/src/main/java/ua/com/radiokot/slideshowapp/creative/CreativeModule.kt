package ua.com.radiokot.slideshowapp.creative

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.creative.data.FsLocalCreativeRepository
import ua.com.radiokot.slideshowapp.creative.domain.LocalCreativeRepository
import java.io.File

val creativeModule = module {

    single {
        FsLocalCreativeRepository(
            creativeDirectory = File(
                androidContext().noBackupFilesDir,
                "Creatives"
            ),
        )
    } bind LocalCreativeRepository::class
}
