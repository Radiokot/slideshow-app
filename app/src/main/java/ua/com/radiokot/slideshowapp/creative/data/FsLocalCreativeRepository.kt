package ua.com.radiokot.slideshowapp.creative.data

import android.net.Uri
import androidx.core.net.toUri
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import ua.com.radiokot.slideshowapp.backend.data.PlayerBackend
import ua.com.radiokot.slideshowapp.creative.domain.Creative
import ua.com.radiokot.slideshowapp.creative.domain.LocalCreativeRepository
import ua.com.radiokot.slideshowapp.util.lazyLogger
import java.io.File

/**
 * [LocalCreativeRepository] that stores creatives in the device file system.
 */
class FsLocalCreativeRepository(
    private val creativeDirectory: File,
    private val playerBackend: PlayerBackend,
) : LocalCreativeRepository {

    private val log by lazyLogger("FsLocalCreativeRepo")

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

        val file =
            getCreativeFile(
                creativeKey = creative.key,
            )
                .takeIf(File::exists)
                ?: return@withContext null

        "file://${file.absolutePath}".toUri()
    }

    override suspend fun saveCreativeLocally(
        creative: Creative,
    ): Unit = withContext(Dispatchers.IO) {

        log.debug {
            "saveCreativeLocally(): saving:" +
                    "\ncreative=$creative"
        }

        val outputFile = getCreativeFile(
            creativeKey = creative.key,
        )
        try {
            val content = playerBackend.getCreative(
                creativeKey = creative.key,
            )
            content.copyTo(outputFile.writeChannel())

            log.debug {
                "saveCreativeLocally(): saved:" +
                        "\ncreative=$creative" +
                        "\noutputFile=${outputFile.absolutePath}"
            }
        } catch (e: Exception) {
            outputFile.delete()
            ensureActive()

            log.error(e) {
                "saveCreativeLocally(): failed saving:" +
                        "\ncreative=$creative"
            }

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
