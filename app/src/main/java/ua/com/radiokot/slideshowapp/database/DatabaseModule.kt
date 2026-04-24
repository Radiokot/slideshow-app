package ua.com.radiokot.slideshowapp.database

import androidx.room.Room
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.database.data.ScreenDatabase
import ua.com.radiokot.slideshowapp.session.domain.UserSession
import ua.com.radiokot.slideshowapp.session.util.userSessionScope

val databaseModule = module {

    userSessionScope {
        scoped {
            val screenKey = get<UserSession>().screenKey

            Room
                .databaseBuilder(
                    context = get(),
                    klass = ScreenDatabase::class.java,
                    name = "screen_$screenKey",
                )
                .build()
        } bind ScreenDatabase::class
    }
}
