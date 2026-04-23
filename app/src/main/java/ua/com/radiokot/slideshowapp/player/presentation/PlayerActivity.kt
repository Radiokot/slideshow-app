package ua.com.radiokot.slideshowapp.player.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        window.addFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: PlayerScreenViewModel = koinViewModel {
                parametersOf(
                    PlayerScreenViewModel.Parameters(
                        playlistKey = "1",
                    )
                )
            }

            PlayerScreen(
                itemState = viewModel.playerItem.collectAsState(),
                onSkipCurrentItemAction = viewModel::onSkipCurrentItemAction,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        WindowInsetsControllerCompat(window, window.decorView)
            .hide(WindowInsetsCompat.Type.systemBars())
    }
}
