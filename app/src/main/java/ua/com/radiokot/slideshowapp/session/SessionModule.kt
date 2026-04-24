package ua.com.radiokot.slideshowapp.session

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.com.radiokot.slideshowapp.session.domain.KoinScopeUserSessionHolder
import ua.com.radiokot.slideshowapp.session.domain.SignInUseCase
import ua.com.radiokot.slideshowapp.session.domain.SignOutUseCase
import ua.com.radiokot.slideshowapp.session.domain.UserSessionHolder
import ua.com.radiokot.slideshowapp.session.presentation.SignInScreenViewModel
import ua.com.radiokot.slideshowapp.session.util.userSessionScope

val sessionModule = module {

    single {
        KoinScopeUserSessionHolder(
            koin = getKoin(),
        )
    } bind UserSessionHolder::class

    factory {
        SignInUseCase(
            userSessionHolder = get(),
        )
    }

    viewModel {
        SignInScreenViewModel(
            signInUseCase = get(),
        )
    }

    userSessionScope {
        factory {
            SignOutUseCase(
                userSessionHolder = get(),
            )
        }
    }
}
