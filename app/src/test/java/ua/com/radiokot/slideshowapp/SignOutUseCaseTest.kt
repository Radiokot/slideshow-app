package ua.com.radiokot.slideshowapp

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import ua.com.radiokot.slideshowapp.session.domain.SignOutUseCase
import ua.com.radiokot.slideshowapp.session.domain.UserSessionHolder

@RunWith(MockitoJUnitRunner::class)
public class SignOutUseCaseTest {

    @Mock
    private lateinit var userSessionHolder: UserSessionHolder

    @Test
    fun `invocation clears the session holder`() {
        SignOutUseCase(
            userSessionHolder = userSessionHolder,
        )
            .invoke()

        verify(userSessionHolder).clear()
    }
}

