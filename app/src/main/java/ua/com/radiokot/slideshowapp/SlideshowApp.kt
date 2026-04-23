package ua.com.radiokot.slideshowapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ua.com.radiokot.slideshowapp.player.playerModule

class SlideshowApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SlideshowApp)
            modules(
                playerModule,
            )
        }
    }
}
