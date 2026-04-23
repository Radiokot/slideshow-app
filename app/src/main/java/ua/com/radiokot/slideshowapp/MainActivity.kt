package ua.com.radiokot.slideshowapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.compose.viewmodel.koinViewModel
import ua.com.radiokot.slideshowapp.playlist.presentation.PlaylistsScreen
import ua.com.radiokot.slideshowapp.playlist.presentation.PlaylistsScreenViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        with(WindowInsetsControllerCompat(window, window.decorView)) {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: PlaylistsScreenViewModel = koinViewModel()

            PlaylistsScreen(
                screenKey = viewModel.screenKey,
                itemsState = viewModel.items.collectAsState(),
                onItemClickAction = {},
                onSignOutAction = {},
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xffda1d52))
                    .safeContentPadding()
                    .padding(24.dp)
            )
        }
    }
}
