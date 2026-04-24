package ua.com.radiokot.slideshowapp.session.domain

class SignOutUseCase(
    private val userSessionHolder: UserSessionHolder,
) {
    operator fun invoke() {
        userSessionHolder.clear()
    }
}
