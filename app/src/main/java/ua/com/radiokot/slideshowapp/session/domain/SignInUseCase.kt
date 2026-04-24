package ua.com.radiokot.slideshowapp.session.domain

import ua.com.radiokot.slideshowapp.util.lazyLogger

class SignInUseCase(
    private val userSessionHolder: UserSessionHolder,
) {
    private val log by lazyLogger("SignInUC")

    operator fun invoke(
        screenKey: String,
    ) {
        userSessionHolder.set(
            UserSession(
                screenKey = screenKey,
            )
        )

        log.debug {
            "invoke(): signed in:" +
                    "\nscreenKey: $screenKey"
        }
        log.info {
            "Signed in as the screen $screenKey"
        }
    }
}
