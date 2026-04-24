package ua.com.radiokot.slideshowapp.session.domain

import org.koin.core.Koin
import org.koin.core.qualifier._q

const val DI_SCOPE_SESSION = "user-session"

class KoinScopeUserSessionHolder(
    private val koin: Koin,
) : UserSessionHolder {

    override fun set(session: UserSession): Unit = with(koin) {
        closeExistingScope()

        createScope(
            scopeId = DI_SCOPE_SESSION,
            qualifier = _q<UserSession>(),
            source = session,
        )
    }

    override fun clear() {
        closeExistingScope()
    }

    private fun closeExistingScope() = with(koin) {
        getScopeOrNull(DI_SCOPE_SESSION)?.close()
    }

    override val isSet: Boolean
        get() = with(koin) {
            return getScopeOrNull(DI_SCOPE_SESSION)?.isNotClosed() == true
        }
}
