package ua.com.radiokot.slideshowapp.session

import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.session.data.KoinScopeUserSessionHolder
import ua.com.radiokot.slideshowapp.session.domain.UserSessionHolder

val sessionModule = module {

    single {
        KoinScopeUserSessionHolder(
            koin = getKoin(),
        )
    } bind UserSessionHolder::class
}
