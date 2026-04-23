package ua.com.radiokot.slideshowapp.creative.data

import android.net.Uri
import androidx.core.net.toUri
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.radiokot.slideshowapp.backend.data.PlayerBackend
import ua.com.radiokot.slideshowapp.creative.domain.Creative
import ua.com.radiokot.slideshowapp.creative.domain.LocalCreativeRepository
import java.io.File

/**
 * [LocalCreativeRepository] that stores creatives in the device file system.
 */
class FsLocalCreativeRepository(
    private val creativeDirectory: File,
    private val playerBackend: PlayerBackend,
) : LocalCreativeRepository {
    init {
        require(creativeDirectory.exists()) {
            "Provided file doesn't exist: $creativeDirectory"
        }

        require(creativeDirectory.isDirectory) {
            "Provided file is not a directory: $creativeDirectory"
        }

        require(creativeDirectory.canRead()) {
            "Can't read the directory: $creativeDirectory"
        }
    }

    override suspend fun getLocalCreativeUri(
        creative: Creative,
    ): Uri? = withContext(Dispatchers.IO) {

        getCreativeFile(
            creativeKey = creative.key,
        )
            .takeIf(File::exists)
            ?.absolutePath
            ?.toUri()
    }

    override suspend fun saveCreativeLocally(
        creative: Creative,
    ): Unit = withContext(Dispatchers.IO) {

        val outputFile = getCreativeFile(
            creativeKey = creative.key,
        )
        try {
            val content = playerBackend.getCreative(
                creativeKey = creative.key,
            )
            content.copyTo(outputFile.writeChannel())
        } catch (e: Exception) {
            outputFile.delete()
            throw e
        }
    }

    private fun getCreativeFile(
        creativeKey: String,
    ) = File(
        creativeDirectory,
        creativeKey
    )
}
