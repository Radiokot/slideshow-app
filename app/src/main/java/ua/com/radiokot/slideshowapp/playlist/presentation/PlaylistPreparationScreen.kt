package ua.com.radiokot.slideshowapp.playlist.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.com.radiokot.slideshowapp.R

@Composable
fun PlaylistPreparationScreen(
    modifier: Modifier = Modifier,
) = Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
        .background(Color(0xFFD98AB2))
        .safeContentPadding()
        .padding(24.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "indicator_pulse",
    )
    val indicatorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 750,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "indicator_pulse_alpha",
    )

    Image(
        painter = painterResource(R.drawable.ic_download),
        contentDescription = "Loading indicator",
        colorFilter = ColorFilter.tint(Color(0xff260211)),
        modifier = Modifier
            .size(64.dp)
            .graphicsLayer {
                alpha = indicatorAlpha
            }
    )

    BasicText(
        text = "Preparing the playlist",
        style = TextStyle(
            textAlign = TextAlign.Center,
            color = Color(0xff260211),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
        ),
        modifier = Modifier
            .padding(
                top = 40.dp,
            )
    )

    BasicText(
        text = "Playback will start soon",
        style = TextStyle(
            textAlign = TextAlign.Center,
            color = Color(0xff260211),
            fontSize = 14.sp,
        ),
        modifier = Modifier
            .padding(
                top = 8.dp,
            )
    )
}

@Composable
@Preview
private fun PlaylistPreparationScreenPreview() {
    PlaylistPreparationScreen(
        modifier = Modifier
            .fillMaxSize()
    )
}
