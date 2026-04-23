package ua.com.radiokot.slideshowapp.io

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val ioModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            encodeDefaults = true
        }
    } bind Json::class

    single {
        HttpClient(OkHttp) {
            engine {
                config {
                    connectTimeout(15.seconds)
                    readTimeout(30.seconds)
                    writeTimeout(30.seconds)
                }
            }

            install(ContentNegotiation) {
                json(
                    json = get(),
                )
            }
        }
    } bind HttpClient::class
}
