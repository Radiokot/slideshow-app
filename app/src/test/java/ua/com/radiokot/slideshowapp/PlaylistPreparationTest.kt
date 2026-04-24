package ua.com.radiokot.slideshowapp

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ua.com.radiokot.slideshowapp.creative.domain.Creative
import ua.com.radiokot.slideshowapp.creative.domain.LocalCreativeRepository
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistPreparation
import ua.com.radiokot.slideshowapp.playlist.domain.PlaylistRepository
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

@RunWith(MockitoJUnitRunner::class)
class PlaylistPreparationTest {

    @Mock
    private lateinit var playlistRepository: PlaylistRepository
    @Mock
    private lateinit var localCreativeRepository: LocalCreativeRepository

    private lateinit var playlistPreparation: PlaylistPreparation

    @Before
    fun setUp() {
        playlistPreparation = PlaylistPreparation(
            playlistRepository = playlistRepository,
            localCreativeRepository = localCreativeRepository,
        )
    }

    @Test
    fun `preparePlaylist saves all creatives and marks playlist as ready`() = runTest {
        // Arrange
        val creative1 = Creative(
            key = "creative_1",
            contentType = Creative.Type.Image,
            properties = emptyMap(),
        )
        val creative2 = Creative(
            key = "creative_2",
            contentType = Creative.Type.Video,
            properties = mapOf("soundVolume" to "50"),
        )

        val playlist = Playlist(
            key = "playlist_1",
            lastModified = Instant.now(),
            isReadyToPlay = false,
            items = listOf(
                Playlist.Item(
                    duration = 10.seconds,
                    orderKey = 1,
                    creative = creative1,
                ),
                Playlist.Item(
                    duration = 15.seconds,
                    orderKey = 2,
                    creative = creative2,
                ),
            ),
        )

        // Mock that creatives are not yet saved locally
        whenever(localCreativeRepository.contains(creative1)).thenReturn(false)
        whenever(localCreativeRepository.contains(creative2)).thenReturn(false)

        // Act
        val result = playlistPreparation.preparePlaylist(playlist)

        // Assert
        assertTrue(result)
        verify(localCreativeRepository, times(1)).saveCreativeLocally(creative1)
        verify(localCreativeRepository, times(1)).saveCreativeLocally(creative2)
        verify(playlistRepository, times(1)).setPlaylistReady(playlist)
    }

    @Test
    fun `preparePlaylist skips creatives already saved locally`() = runTest {
        // Arrange
        val creative1 = Creative(
            key = "creative_1",
            contentType = Creative.Type.Image,
            properties = emptyMap(),
        )
        val creative2 = Creative(
            key = "creative_2",
            contentType = Creative.Type.Video,
            properties = emptyMap(),
        )

        val playlist = Playlist(
            key = "playlist_1",
            lastModified = Instant.now(),
            isReadyToPlay = false,
            items = listOf(
                Playlist.Item(
                    duration = 10.seconds,
                    orderKey = 1,
                    creative = creative1,
                ),
                Playlist.Item(
                    duration = 15.seconds,
                    orderKey = 2,
                    creative = creative2,
                ),
            ),
        )

        // Mock that creative1 is already saved, creative2 is not
        whenever(localCreativeRepository.contains(creative1)).thenReturn(true)
        whenever(localCreativeRepository.contains(creative2)).thenReturn(false)

        // Act
        val result = playlistPreparation.preparePlaylist(playlist)

        // Assert
        assertTrue(result)
        verify(localCreativeRepository, times(0)).saveCreativeLocally(creative1)
        verify(localCreativeRepository, times(1)).saveCreativeLocally(creative2)
        verify(playlistRepository, times(1)).setPlaylistReady(playlist)
    }

    @Test
    fun `preparePlaylist handles duplicate creatives`() = runTest {
        // Arrange
        val creative = Creative(
            key = "creative_1",
            contentType = Creative.Type.Image,
            properties = emptyMap(),
        )

        val playlist = Playlist(
            key = "playlist_1",
            lastModified = Instant.now(),
            isReadyToPlay = false,
            items = listOf(
                Playlist.Item(
                    duration = 10.seconds,
                    orderKey = 1,
                    creative = creative,
                ),
                Playlist.Item(
                    duration = 15.seconds,
                    orderKey = 2,
                    creative = creative, // Same creative
                ),
            ),
        )

        whenever(localCreativeRepository.contains(creative)).thenReturn(false)

        // Act
        val result = playlistPreparation.preparePlaylist(playlist)

        // Assert
        assertTrue(result)
        // Should save the creative only once despite appearing twice in the playlist
        verify(localCreativeRepository, times(1)).saveCreativeLocally(creative)
        verify(playlistRepository, times(1)).setPlaylistReady(playlist)
    }

    @Test
    fun `preparePlaylist returns false when saving creative fails`() = runTest {
        // Arrange
        val creative = Creative(
            key = "creative_1",
            contentType = Creative.Type.Image,
            properties = emptyMap(),
        )

        val playlist = Playlist(
            key = "playlist_1",
            lastModified = Instant.now(),
            isReadyToPlay = false,
            items = listOf(
                Playlist.Item(
                    duration = 10.seconds,
                    orderKey = 1,
                    creative = creative,
                ),
            ),
        )

        whenever(localCreativeRepository.contains(creative)).thenReturn(false)
        whenever(localCreativeRepository.saveCreativeLocally(any())).thenThrow(
            IllegalStateException("Network error")
        )

        // Act
        val result = playlistPreparation.preparePlaylist(playlist)

        // Assert
        assertFalse(result)
        verify(playlistRepository, times(0)).setPlaylistReady(any())
    }

    @Test
    fun `preparePlaylist returns false when setPlaylistReady fails`() = runTest {
        // Arrange
        val creative = Creative(
            key = "creative_1",
            contentType = Creative.Type.Image,
            properties = emptyMap(),
        )

        val playlist = Playlist(
            key = "playlist_1",
            lastModified = Instant.now(),
            isReadyToPlay = false,
            items = listOf(
                Playlist.Item(
                    duration = 10.seconds,
                    orderKey = 1,
                    creative = creative,
                ),
            ),
        )

        whenever(localCreativeRepository.contains(creative)).thenReturn(false)
        whenever(playlistRepository.setPlaylistReady(any())).thenThrow(
            IllegalStateException("Database error")
        )

        // Act
        val result = playlistPreparation.preparePlaylist(playlist)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `preparePlaylist succeeds when playlist has no creatives`() = runTest {
        // Arrange
        val playlist = Playlist(
            key = "playlist_empty",
            lastModified = Instant.now(),
            isReadyToPlay = false,
            items = emptyList(),
        )

        // Act
        val result = playlistPreparation.preparePlaylist(playlist)

        // Assert
        assertTrue(result)
        verify(localCreativeRepository, times(0)).saveCreativeLocally(any())
        verify(playlistRepository, times(1)).setPlaylistReady(playlist)
    }
}
