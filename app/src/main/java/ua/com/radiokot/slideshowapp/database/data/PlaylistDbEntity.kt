package ua.com.radiokot.slideshowapp.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Entity(
    tableName = "playlists",
    primaryKeys = [
        "key",
        "last_modified",
    ]
)
data class PlaylistDbEntity(
    @ColumnInfo(name = "key")
    val key: String,
    @ColumnInfo(name = "last_modified")
    val lastModifiedMs: Long,
    @ColumnInfo("is_ready")
    val isReadyToPlay: Boolean,
    @ColumnInfo("items")
    val items: List<Item>,
) {
    @Serializable
    data class Item(
        val durationMs: Long,
        val orderKey: Int,
        val creativeKey: String,
        val creativeContentType: String,
        val creativeProperties: Map<String, String>,
    )

    companion object Converters {
        @TypeConverter
        @JvmStatic
        fun itemsToJsonArray(items: List<Item>): String =
            Json.encodeToString(items)

        @TypeConverter
        @JvmStatic
        fun itemsFromJsonArray(jsonArray: String): List<Item> =
            Json.decodeFromString(jsonArray)
    }
}
