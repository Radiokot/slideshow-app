package ua.com.radiokot.slideshowapp

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import ua.com.radiokot.slideshowapp.session.domain.SignInUseCase
import ua.com.radiokot.slideshowapp.session.domain.UserSession
import ua.com.radiokot.slideshowapp.session.domain.UserSessionHolder

@RunWith(MockitoJUnitRunner::class)
class SignInUseCaseTest {

    @Mock
    private lateinit var userSessionHolder: UserSessionHolder

    @Test
    fun `invocation sets up the session holder`() {
        val screenKey = "test-key"

        SignInUseCase(
            userSessionHolder = userSessionHolder,
        )
            .invoke(
                screenKey = screenKey,
            )

        verify(userSessionHolder).set(
            UserSession(
                screenKey = screenKey,
            )
        )
    }
}
