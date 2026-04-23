package ua.com.radiokot.slideshowapp.database.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 1,
    exportSchema = true,
    entities = [
        PlaylistDbEntity::class,
    ],
)
@TypeConverters(
    value = [
        PlaylistDbEntity.Converters::class,
    ],
)
abstract class ScreenDatabase : RoomDatabase() {
    abstract fun playlists(): PlaylistDao
}
