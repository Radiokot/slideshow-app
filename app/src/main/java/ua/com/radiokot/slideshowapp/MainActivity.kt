package ua.com.radiokot.slideshowapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ua.com.radiokot.slideshowapp.player.presentation.PlayerActivity
import ua.com.radiokot.slideshowapp.playlist.presentation.PlaylistPreparationScreen
import ua.com.radiokot.slideshowapp.playlist.presentation.PlaylistPreparationScreenViewModel
import ua.com.radiokot.slideshowapp.playlist.presentation.PlaylistsScreen
import ua.com.radiokot.slideshowapp.playlist.presentation.PlaylistsScreenViewModel
import ua.com.radiokot.slideshowapp.session.data.UserSessionScope

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
            UserSessionScope {
                MainNavHost(
                    onProceedToPlayer = ::openPlayer,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }

    private fun openPlayer(
        playlistKey: String,
    ) {
        val playerIntent =
            Intent(this, PlayerActivity::class.java)
                .putExtras(
                    PlayerActivity.getBundle(
                        playlistKey = playlistKey,
                    )
                )
        startActivity(playerIntent)
    }
}

@Composable
private fun MainNavHost(
    modifier: Modifier = Modifier,
    onProceedToPlayer: (playlistKey: String) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PlaylistsRoute,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        modifier = modifier
    ) {
        composable(
            route = PlaylistsRoute,
        ) {
            val viewModel: PlaylistsScreenViewModel = koinViewModel()

            PlaylistsScreen(
                screenKey = viewModel.screenKey,
                itemsState = viewModel.items.collectAsState(),
                onItemClick = viewModel::onItemClick,
                onSignOutAction = {},
                modifier = Modifier
                    .fillMaxSize()
            )

            LaunchedEffect(viewModel) {

                viewModel.events.collect { event ->
                    when (event) {
                        is PlaylistsScreenViewModel.Event.ProceedToPlayer -> {
                            onProceedToPlayer(event.playlistKey)
                        }

                        is PlaylistsScreenViewModel.Event.ProceedToPlaylistPreparation -> {
                            navController.navigate(
                                route = PlaylistPreparationRoute(
                                    playlistKey = event.playlistKey,
                                )
                            )
                        }
                    }
                }
            }
        }

        composable(
            route = PlaylistPreparationRoute,
            arguments = listOf(
                navArgument("playlistKey") {
                    type = NavType.StringType
                },
            ),
        ) { navEntry ->

            val viewModel: PlaylistPreparationScreenViewModel = koinViewModel {
                parametersOf(
                    PlaylistPreparationScreenViewModel.Parameters(
                        playlistKey = navEntry.arguments?.getString("playlistKey")
                            ?: error("No playlist key argument passed"),
                    )
                )
            }

            PlaylistPreparationScreen(
                modifier = Modifier
                    .fillMaxSize()
            )

            val context = LocalContext.current

            LaunchedEffect(viewModel) {

                viewModel.events.collect { event ->
                    when (event) {
                        PlaylistPreparationScreenViewModel.Event.PreparationFailed -> {
                            Toast.makeText(
                                context,
                                "Preparation failed",
                                Toast.LENGTH_LONG,
                            ).show()
                            navController.navigateUp()
                        }

                        is PlaylistPreparationScreenViewModel.Event.ProceedToPlayer -> {
                            if (event.isContentOutdated) {
                                Toast.makeText(
                                    context,
                                    "Playing last prepared version",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            navController.navigateUp()
                            onProceedToPlayer(event.playlistKey)
                        }
                    }
                }
            }
        }
    }
}

private const val PlaylistsRoute = "playlists"

private const val PlaylistPreparationRoute = "$PlaylistsRoute/{playlistKey}/prepare"
private fun PlaylistPreparationRoute(
    playlistKey: String,
) = PlaylistPreparationRoute.replace("{playlistKey}", playlistKey)
