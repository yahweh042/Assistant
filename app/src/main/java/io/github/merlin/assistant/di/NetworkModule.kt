package io.github.merlin.assistant.di

import coil.intercept.Interceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.merlin.assistant.data.network.GlobalExceptionHandler
import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.repo.AccountRepo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.api.ClientHook
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient {
        return HttpClient(CIO) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
            install(UserAgent) {
                agent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
            }
            install(ContentNegotiation) {
                json(json, ContentType.Text.Html)
            }
            install(GlobalExceptionHandler)
        }
    }

    @Provides
    @Singleton
    fun provideNetworkDataSource(httpClient: HttpClient, accountRepo: AccountRepo): NetworkDataSource {
        return NetworkDataSource(httpClient, accountRepo)
    }

}