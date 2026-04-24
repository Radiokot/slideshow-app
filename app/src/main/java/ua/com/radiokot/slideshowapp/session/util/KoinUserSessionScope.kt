package ua.com.radiokot.slideshowapp.session.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.getKoin
import org.koin.androidx.scope.createActivityScope
import org.koin.androidx.scope.createFragmentScope
import org.koin.compose.ComposeContextWrapper
import org.koin.compose.LocalKoinScopeContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.mp.KoinPlatform
import ua.com.radiokot.slideshowapp.session.domain.DI_SCOPE_SESSION
import ua.com.radiokot.slideshowapp.session.domain.UserSession

/**
 * @return Activity [Scope] linked to the [UserSession] scope, if it exists.
 */
fun ComponentActivity.createActivityScopeWithSession(): Scope =
    getKoin().getScopeOrNull(DI_SCOPE_SESSION)
        ?.apply { linkTo(createActivityScope()) }
        ?: createActivityScope()

/**
 * @return Fragment [Scope] linked to the [UserSession] scope, if it exists.
 */
fun Fragment.createFragmentScopeWithSession(): Scope =
    getKoin().getScopeOrNull(DI_SCOPE_SESSION)
        ?.apply { linkTo(createFragmentScope()) }
        ?: createFragmentScope()

fun Module.userSessionScope(scopeSet: ScopeDSL.() -> Unit): Unit =
    scope<UserSession>(scopeSet)

/**
 * Runs the [content] in the [UserSession] scope, if it exists.
 */
@OptIn(KoinInternalApi::class, KoinExperimentalAPI::class)
@Composable
fun UserSessionScope(
    content: @Composable () -> Unit,
) {
    val koin = KoinPlatform.getKoin()
    CompositionLocalProvider(
        LocalKoinScopeContext provides ComposeContextWrapper(
            koin.getScopeOrNull(DI_SCOPE_SESSION)
                ?: koin.scopeRegistry.rootScope
        ),
        content = content,
    )
}
