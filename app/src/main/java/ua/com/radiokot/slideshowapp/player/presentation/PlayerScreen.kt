@file:OptIn(UnstableApi::class)

package ua.com.radiokot.slideshowapp.player.presentation

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.image.LandscapistImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    itemState: State<PlayerItem?>,
    onSkipCurrentItemAction: () -> Unit,
) = Box(
    modifier = modifier,
) {
    AnimatedContent(
        targetState = itemState.value,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = fadeIn(
                    animationSpec = tween(750),
                ),
                initialContentExit = fadeOut(
                    animationSpec = tween(750),
                ),
                sizeTransform = null,
            )
        },
        contentKey = { it?.key },
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = {},
                onLongClick = onSkipCurrentItemAction,
                interactionSource = null,
                indication = null,
            )
    ) { playerItem ->

        if (playerItem == null) {
            NoContentPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
            )
            return@AnimatedContent
        }

        when (val content = playerItem.content) {
            is PlayerItem.Content.Image -> {
                ImagePlayer(
                    content = content,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            is PlayerItem.Content.Video -> {
                VideoPlayer(
                    content = content,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun NoContentPlaceholder(
    modifier: Modifier = Modifier,
) = Box(
    contentAlignment = Alignment.Center,
    modifier = modifier,
) {
    BasicText(
        text = "No content to play",
        style = TextStyle(
            color = Color.White,
        ),
    )
}

@Composable
private fun ImagePlayer(
    modifier: Modifier = Modifier,
    content: PlayerItem.Content.Image,
) {
    LandscapistImage(
        imageModel = content::uri,
        imageOptions = ImageOptions(
            contentScale = ContentScale.Fit,
        ),
        modifier = modifier
    )
}

@Composable
private fun VideoPlayer(
    modifier: Modifier = Modifier,
    content: PlayerItem.Content.Video,
) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(
                // Small buffer for local playback.
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        100,
                        100,
                        100,
                        100,
                    )
                    .build()
            )
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = true
            }
    }
    LaunchedEffect(content) {
        with(player) {
            stop()
            setMediaItem(MediaItem.fromUri(content.uri))
            volume = content.volumePercent / 100f
            prepare()
        }
    }
    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    ContentFrame(
        player = player,
        surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

@Preview
@Composable
private fun PlayerScreenPreview() {
    val items = remember {
        listOf(
            PlayerItem(
                key = "1",
                content = PlayerItem.Content.Image(
                    uri = "https://picsum.photos/720".toUri(),
                )
            ),
            PlayerItem(
                key = "2",
                content = PlayerItem.Content.Video(
                    uri = "https://radiokot.com.ua/Radiokot/rock_cat.mp4".toUri(),
                    volumePercent = 100f,
                )
            ),
            null,
        )
    }
    val itemState = produceState(items.first()) {
        do {
            delay(3000)
            value = items[(items.indexOf(value) + 1) % items.size]
        } while (isActive)
    }

    PlayerScreen(
        itemState = itemState,
        onSkipCurrentItemAction = {},
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}
