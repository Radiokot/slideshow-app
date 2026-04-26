package ua.com.radiokot.slideshowapp

import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
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

    @Test
    fun `getMostRecentPlaylistsFlow triggers update from the backend`() = runTest {
        // Arrange
        val backendPlaylists = listOf(
            PlayerBackend.Playlist(
                playlistKey = "playlist_1",
                playlistItems = listOf(
                    PlayerBackend.PlaylistItem(
                        creativeKey = "creative_1.mp4",
                        duration = 30,
                        modified = 1000L,
                        orderKey = 0
                    )
                )
            )
        )
        val playerBackend = FakePlayerBackend(backendPlaylists)
        val playlistDao = FakePlaylistDao()

        val repository = CachedPlaylistRepository(
            screenKey = screenKey,
            playerBackend = playerBackend,
            playlistDao = playlistDao,
            periodicBackendUpdateInterval = 1.minutes,
        )

        // Act
        val flow = repository.getMostRecentPlaylistsFlow()

        // First emission is empty (from shareIn replay=1 with empty DB)
        // Second emission should have backend data after onStart triggers backend fetch
        val result = flow.take(2).last()

        // Assert
        assertEquals(1, result.size)
        assertEquals("playlist_1", result.first().key)
        assertEquals("playlist_1", playlistDao.selectAllPlaylistsFlow().first().first().key)
    }

    private class FakePlaylistDao : PlaylistDao {
        private val _playlistFlow = MutableStateFlow<List<PlaylistDbEntity>>(emptyList())

        override fun selectAllPlaylistsFlow(): Flow<List<PlaylistDbEntity>> =
            _playlistFlow.asStateFlow()

        override suspend fun setPlaylistReady(key: String, lastModifiedMs: Long) {
            val current = _playlistFlow.value.toMutableList()
            val index =
                current.indexOfFirst { it.key == key && it.lastModifiedMs == lastModifiedMs }
            if (index != -1) {
                current[index] = current[index].copy(isReadyToPlay = true)
                _playlistFlow.value = current
            }
        }

        override suspend fun insertMissingPlaylists(entities: List<PlaylistDbEntity>) {
            val current = _playlistFlow.value.toMutableList()
            current.addAll(entities)
            _playlistFlow.value = current
        }

        override suspend fun selectDistinctPlaylistKeys(): List<String> =
            _playlistFlow.value.map { it.key }.distinct()

        override suspend fun deletePlaylists(keys: List<String>) {
            _playlistFlow.value = _playlistFlow.value.filter { it.key !in keys }
        }

        override suspend fun selectReadyToPlayPlaylist(key: String): PlaylistDbEntity? =
            _playlistFlow.value.filter { it.key == key && it.isReadyToPlay }
                .maxByOrNull { it.lastModifiedMs }

        override suspend fun selectMostRecentPlaylist(key: String): PlaylistDbEntity? =
            _playlistFlow.value.filter { it.key == key }.maxByOrNull { it.lastModifiedMs }
    }

    private class FakePlayerBackend(
        private val playlists: List<PlayerBackend.Playlist>,
    ) : PlayerBackend {
        override suspend fun getPlaylistItems(screenKey: String): PlayerBackend.PlaylistItemsResponse =
            PlayerBackend.PlaylistItemsResponse(
                playlists = playlists,
                modified = System.currentTimeMillis(),
            )

        override suspend fun getCreative(creativeKey: String): ByteReadChannel =
            error("Not implemented")
    }
}
