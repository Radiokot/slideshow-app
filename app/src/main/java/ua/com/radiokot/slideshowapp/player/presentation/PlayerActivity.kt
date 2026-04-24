package ua.com.radiokot.slideshowapp.player.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.skydoves.landscapist.image.LocalLandscapist
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ua.com.radiokot.slideshowapp.session.data.UserSessionScope

class PlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        window.addFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        super.onCreate(savedInstanceState)

        setContent {
            UserSessionScope {
                val viewModel: PlayerScreenViewModel = koinViewModel {
                    parametersOf(
                        PlayerScreenViewModel.Parameters(
                            playlistKey = intent.getStringExtra(PLAYLIST_KEY_EXTRA)
                                ?: error("No $PLAYLIST_KEY_EXTRA extra passed"),
                        )
                    )
                }

                CompositionLocalProvider(
                    LocalLandscapist provides koinInject()
                ) {
                    PlayerScreen(
                        itemState = viewModel.playerItem.collectAsState(),
                        onSkipCurrentItemAction = viewModel::onSkipCurrentItemAction,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }

        WindowInsetsControllerCompat(window, window.decorView)
            .hide(WindowInsetsCompat.Type.systemBars())
    }

    companion object {
        private const val PLAYLIST_KEY_EXTRA = "playlist_key"

        fun getBundle(
            playlistKey: String,
        ) = Bundle().apply {
            putString(PLAYLIST_KEY_EXTRA, playlistKey)
        }
    }
}
