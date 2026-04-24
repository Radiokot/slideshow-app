package ua.com.radiokot.slideshowapp

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ua.com.radiokot.slideshowapp.backend.data.PlayerBackend
import ua.com.radiokot.slideshowapp.database.data.PlaylistDao
import ua.com.radiokot.slideshowapp.database.data.PlaylistDbEntity
import ua.com.radiokot.slideshowapp.playlist.data.CachedPlaylistRepository
import ua.com.radiokot.slideshowapp.playlist.domain.Playlist
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

@RunWith(MockitoJUnitRunner::class)
class CachedPlaylistRepositoryTest {

    @Mock
    private lateinit var playerBackend: PlayerBackend

    @Mock
    private lateinit var playlistDao: PlaylistDao

    private lateinit var repository: CachedPlaylistRepository
    private val screenKey = "test_screen_key"

    @Before
    fun setUp() {
        repository = CachedPlaylistRepository(
            screenKey = screenKey,
            playerBackend = playerBackend,
            playlistDao = playlistDao,
            periodicBackendUpdateInterval = 1.minutes,
        )
    }

    @Test
    fun `getReadyPlaylist returns ready playlist when it exists`() = runTest {
        // Arrange
        val playlistKey = "playlist_1"
        val readyPlaylistEntity = PlaylistDbEntity(
            key = playlistKey,
            lastModifiedMs = System.currentTimeMillis(),
            isReadyToPlay = true,
            items = emptyList(),
        )

        whenever(playlistDao.selectReadyToPlayPlaylist(playlistKey))
            .thenReturn(readyPlaylistEntity)

        // Act
        val result = repository.getReadyPlaylist(playlistKey)

        // Assert
        assertNotNull(result)
        assertEquals(playlistKey, result?.key)
        assertTrue(result?.isReadyToPlay == true)
    }

    @Test
    fun `getReadyPlaylist returns null when no ready playlist exists`() = runTest {
        // Arrange
        val playlistKey = "nonexistent_playlist"

        whenever(playlistDao.selectReadyToPlayPlaylist(playlistKey))
            .thenReturn(null)

        // Act
        val result = repository.getReadyPlaylist(playlistKey)

        // Assert
        assertNull(result)
    }

    @Test
    fun `getMostRecentPlaylist returns most recent version of playlist`() = runTest {
        // Arrange
        val playlistKey = "playlist_1"
        val mostRecentEntity = PlaylistDbEntity(
            key = playlistKey,
            lastModifiedMs = System.currentTimeMillis(),
            isReadyToPlay = false,
            items = emptyList(),
        )

        whenever(playlistDao.selectMostRecentPlaylist(playlistKey))
            .thenReturn(mostRecentEntity)

        // Act
        val result = repository.getMostRecentPlaylist(playlistKey)

        // Assert
        assertNotNull(result)
        assertEquals(playlistKey, result?.key)
    }

    @Test
    fun `getMostRecentPlaylist returns null when no playlist exists`() = runTest {
        // Arrange
        val playlistKey = "nonexistent_playlist"

        whenever(playlistDao.selectMostRecentPlaylist(playlistKey))
            .thenReturn(null)

        // Act
        val result = repository.getMostRecentPlaylist(playlistKey)

        // Assert
        assertNull(result)
    }

    @Test
    fun `setPlaylistReady marks playlist as ready in database`() = runTest {
        // Arrange
        val playlist = Playlist(
            key = "playlist_1",
            lastModified = Instant.now(),
            isReadyToPlay = false,
            items = emptyList(),
        )

        // Act
        repository.setPlaylistReady(playlist)

        // Assert
        verify(playlistDao).setPlaylistReady(
            key = playlist.key,
            lastModifiedMs = playlist.lastModified.toEpochMilli(),
        )
    }
}
