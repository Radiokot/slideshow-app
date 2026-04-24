package ua.com.radiokot.slideshowapp

import android.app.Application
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ua.com.radiokot.slideshowapp.player.playerModule
import ua.com.radiokot.slideshowapp.playlist.playlistModule
import ua.com.radiokot.slideshowapp.session.domain.UserSession
import ua.com.radiokot.slideshowapp.session.domain.UserSessionHolder
import ua.com.radiokot.slideshowapp.session.sessionModule

class SlideshowApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SlideshowApp)
            modules(
                sessionModule,
                playlistModule,
                playerModule,
            )
        }

        initSessionHolder()
    }

    private fun initSessionHolder() {
        get<UserSessionHolder>().set(
            UserSession(
                screenKey = "7d47b6d7-8294-4b33-8887-066961d79993",
            )
        )
    }
}
