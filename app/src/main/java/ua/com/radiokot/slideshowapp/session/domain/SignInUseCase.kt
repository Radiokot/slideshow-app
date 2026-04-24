package ua.com.radiokot.slideshowapp.session.domain

class SignInUseCase(
    private val userSessionHolder: UserSessionHolder,
) {
    operator fun invoke(
        screenKey: String,
    ) {
        userSessionHolder.set(
            UserSession(
                screenKey = screenKey,
            )
        )
    }
}
