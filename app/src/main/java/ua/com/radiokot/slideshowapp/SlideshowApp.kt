package ua.com.radiokot.slideshowapp

import android.app.Application
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ua.com.radiokot.slideshowapp.player.playerModule
import ua.com.radiokot.slideshowapp.playlist.playlistModule
import ua.com.radiokot.slideshowapp.session.domain.UserSession
import ua.com.radiokot.slideshowapp.session.domain.UserSessionHolder
import ua.com.radiokot.slideshowapp.session.sessionModule
import ua.com.radiokot.slideshowapp.util.KoinSlf4jLogger
import java.lang.Thread.UncaughtExceptionHandler
import kotlin.system.exitProcess

class SlideshowApp : Application() {

    private val log by lazy {
        // Logger with a static name.
        // Must be lazy so it is not created before initLogging()
        KotlinLogging.logger("App")
    }

    override fun onCreate() {
        super.onCreate()

        initLogging()

        startKoin {
            logger(KoinSlf4jLogger)
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

    private fun initLogging() {
        // The Logback configuration is in the app/src/main/assets/logback.xml

        @Suppress("KotlinConstantConditions", "RedundantSuppression")
        System.setProperty(
            "LOG_LEVEL",
            if (BuildConfig.DEBUG)
                "TRACE"
            else
                "INFO"
        )

        val defaultUncaughtExceptionHandler: UncaughtExceptionHandler? =
            Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            log.error(exception) { "Fatal exception\n" }

            if (defaultUncaughtExceptionHandler != null) {
                defaultUncaughtExceptionHandler.uncaughtException(thread, exception)
            } else {
                exitProcess(10)
            }
        }

        log.trace {
            "initLogging(): trace logger enabled"
        }
        log.debug {
            "initLogging(): debug logger enabled"
        }
        log.info {
            "initLogging(): info logger enabled"
        }
    }
}
