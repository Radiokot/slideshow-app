package ua.com.radiokot.slideshowapp.database

import androidx.room.Room
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.database.data.ScreenDatabase

val databaseModule = module {

    single {
        Room
            .databaseBuilder(
                context = get(),
                klass = ScreenDatabase::class.java,
                name = "screen_7d47b6d7-8294-4b33-8887-066961d79993",
            )
            .build()
    } bind ScreenDatabase::class
}
