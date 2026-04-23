package ua.com.radiokot.slideshowapp.playlist.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import okhttp3.HttpUrl

class KtorPlayerBackend(
    private val baseUrl: HttpUrl,
    private val client: HttpClient,
) : PlayerBackend {

    override suspend fun getPlaylistItems(
        screenKey: String,
    ): PlayerBackend.PlaylistItemsResponse =
        client
            .get(
                urlString =
                    baseUrl
                        .newBuilder()
                        .addPathSegments("screen/playlistItems/$screenKey")
                        .toString()
            )
            .body()


    override suspend fun getCreative(
        creativeKey: String,
    ): ByteReadChannel =
        client
            .get(
                urlString =
                    baseUrl
                        .newBuilder()
                        .addPathSegments("creative/get/$creativeKey")
                        .toString()
            )
            .bodyAsChannel()
}
