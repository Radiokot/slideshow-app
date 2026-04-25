package ua.com.radiokot.slideshowapp.session.presentation

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import org.koin.compose.viewmodel.koinViewModel
import ua.com.radiokot.slideshowapp.MainActivity

class SignInActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: SignInScreenViewModel = koinViewModel()

            SignInScreen(
                keyInputState = viewModel.screenKeyInputState,
                onGoAction = viewModel::onGoAction,
                modifier = Modifier
                    .fillMaxSize()
            )

            val softKeyboardController = LocalSoftwareKeyboardController.current

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        SignInScreenViewModel.Event.SignedIn -> {
                            softKeyboardController?.hide()
                            startActivity(
                                Intent(this@SignInActivity, MainActivity::class.java)
                            )
                            finish()
                        }
                    }
                }
            }
        }
    }
}
