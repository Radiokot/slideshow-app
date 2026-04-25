package ua.com.radiokot.slideshowapp.playlist.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ua.com.radiokot.slideshowapp.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    screenKey: String,
    itemsState: State<ImmutableList<PlaylistScreenItem>>,
    onItemClick: (PlaylistScreenItem) -> Unit,
    onSignOutAction: () -> Unit,
) = Column(
    modifier = modifier
        .background(Color(0xFFD98AB2))
        .safeContentPadding()
        .padding(24.dp),
) {
    Row {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            BasicText(
                text = "Screen",
                style = TextStyle(
                    color = Color(0xff260211),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                )
            )
            BasicText(
                text = screenKey,
                style = TextStyle(
                    color = Color(0xff260211),
                    fontSize = 14.sp,
                ),
                modifier = Modifier
                    .padding(
                        top = 4.dp,
                    )
            )
        }

        Image(
            painter = painterResource(R.drawable.ic_logout),
            contentDescription = "Sign out",
            colorFilter = ColorFilter.tint(Color(0xff260211)),
            modifier = Modifier
                .offset(
                    x = 8.dp,
                )
                .clickable(
                    onClick = onSignOutAction,
                )
                .padding(8.dp)
        )
    }

    BasicText(
        text = "Playlists",
        style = TextStyle(
            color = Color(0xff260211),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
        ),
        modifier = Modifier
            .padding(
                top = 40.dp,
            )
    )

    val dateTimeFormat = remember {
        DateTimeFormatter.ofLocalizedDateTime(
            FormatStyle.SHORT,
            FormatStyle.SHORT,
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(
                top = 16.dp,
            )
    ) {
        items(
            items = itemsState.value,
            key = PlaylistScreenItem::key
        ) { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Color(0xff260211),
                    )
                    .clickable(
                        onClick = {
                            onItemClick(item)
                        },
                    )
                    .padding(16.dp)
            ) {
                BasicText(
                    text = item.key,
                    style = TextStyle(
                        color = Color(0xff260211),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                )

                BasicText(
                    text = "Last modified: ${dateTimeFormat.format(item.lastModified)}",
                    style = TextStyle(
                        color = Color(0xff260211),
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                        )
                )
            }
        }
    }
}

@Composable
@Preview
private fun PlaylistsScreenPreview() {
    PlaylistsScreen(
        modifier = Modifier
            .fillMaxSize(),
        screenKey = "19ce0ec4-320c-4b52-b02a-01c4fc1a91f3",
        itemsState =
            persistentListOf(
                PlaylistScreenItem(
                    lastModified = LocalDateTime.now(),
                    key = "test1",
                ),
                PlaylistScreenItem(
                    lastModified = LocalDateTime.now(),
                    key = "test2",
                ),
            ).let(::mutableStateOf),
        onItemClick = {},
        onSignOutAction = {},
    )
}
