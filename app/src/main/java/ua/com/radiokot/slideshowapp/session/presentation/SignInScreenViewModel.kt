package ua.com.radiokot.slideshowapp.session.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ua.com.radiokot.slideshowapp.session.domain.SignInUseCase
import ua.com.radiokot.slideshowapp.util.eventSharedFlow

@Immutable
class SignInScreenViewModel(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {

    val screenKeyInputState = TextFieldState()
    private val _events: MutableSharedFlow<Event> = eventSharedFlow()
    val events: SharedFlow<Event> = _events

    fun onGoAction() {
        val screenKey =
            screenKeyInputState
                .text
                .trim()
                .toString()
                .takeIf(String::isNotEmpty)
                ?: return

        signInUseCase(
            screenKey = screenKey,
        )

        _events.tryEmit(Event.SignedIn)
    }

    sealed interface Event {
        object SignedIn : Event
    }
}
