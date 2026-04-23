package ua.com.radiokot.slideshowapp.database.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlists")
    fun selectAllPlaylistsFlow(): Flow<List<PlaylistDbEntity>>

    @Query("UPDATE playlists SET is_ready=1 WHERE `key`=:key AND `last_modified`=:lastModified")
    suspend fun setPlaylistReady(
        key: String,
        lastModified: Long,
    )

    @Insert(
        onConflict = OnConflictStrategy.IGNORE,
    )
    suspend fun insertMissingPlaylists(
        entities: List<PlaylistDbEntity>,
    )

    @Query("SELECT DISTINCT `key` FROM playlists")
    suspend fun selectDistinctPlaylistKeys(): List<String>

    @Query("DELETE FROM playlists WHERE `key` IN (:keys)")
    suspend fun deletePlaylists(
        keys: List<String>,
    )

    @Query("SELECT * FROM playlists WHERE `key`=:key AND is_ready=1")
    suspend fun selectReadyToPlayPlaylist(
        key: String,
    ): PlaylistDbEntity?
}
