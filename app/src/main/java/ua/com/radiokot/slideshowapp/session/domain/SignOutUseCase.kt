package ua.com.radiokot.slideshowapp.session.domain

import ua.com.radiokot.slideshowapp.util.lazyLogger

class SignOutUseCase(
    private val userSessionHolder: UserSessionHolder,
) {
    private val log by lazyLogger("SignOutUC")

    operator fun invoke() {
        userSessionHolder.clear()

        log.debug {
            "invoke(): signed out"
        }
        log.info {
            "Signed out"
        }
    }
}
