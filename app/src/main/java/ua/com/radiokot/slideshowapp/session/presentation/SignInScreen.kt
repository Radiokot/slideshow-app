package ua.com.radiokot.slideshowapp.session.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    keyInputState: TextFieldState,
    onGoAction: () -> Unit,
) = Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
        .background(Color(0xffda1d52))
        .safeContentPadding()
        .padding(24.dp)
) {
    BasicText(
        text = "Screen setup",
        style = TextStyle(
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        ),
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(
                top = 24.dp,
            )
            .border(
                width = 1.dp,
                color = Color.White,
            )
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        if (keyInputState.text.isEmpty()) {
            BasicText(
                text = "Enter the screen key",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(
                        alpha = 0.6f,
                    ),
                    fontSize = 14.sp,
                ),
            )
        }

        val inputFocusRequester =
            remember(::FocusRequester)

        val textSelectionColors = TextSelectionColors(
            handleColor = Color.White,
            backgroundColor = Color.White.copy(alpha = 0.4f)
        )
        CompositionLocalProvider(
            LocalTextSelectionColors provides textSelectionColors
        ) {
            BasicTextField(
                state = keyInputState,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 14.sp,
                ),
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    showKeyboardOnFocus = true,
                    imeAction = ImeAction.Go,
                ),
                onKeyboardAction = {
                    onGoAction()
                },
                modifier = Modifier
                    .focusRequester(inputFocusRequester)
            )
        }

        LaunchedEffect(Unit) {
            inputFocusRequester.requestFocus()
        }
    }
}

@Composable
@Preview
private fun SignInScreenPreview() {
    SignInScreen(
        keyInputState = rememberTextFieldState("key"),
        onGoAction = {},
        modifier = Modifier
            .fillMaxSize()
    )
}
