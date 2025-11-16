package com.cramsan.architecture.client.di

import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

/**
 * Koin module for initializing Ktor HTTP client for front-end applications.
 * This module configures the HTTP client with content negotiation, exception handling,
 * and default request settings including the back-end URL.
 */
internal val KtorModule = module {

    single<HttpClient> {
        val settingsHolder: SettingsHolder = get()

        HttpClient {
            expectSuccess = true
            HttpResponseValidator {
                // Handle 4xx errors and map to custom exceptions
                handleResponseExceptionWithRequest { exception, request ->
                    val clientException =
                        exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                    val exceptionResponse = clientException.response
                    when (exceptionResponse.status) {
                        HttpStatusCode.NotFound ->
                            throw ClientRequestExceptions.NotFoundException(exceptionResponse.bodyAsText())
                        HttpStatusCode.Unauthorized ->
                            throw ClientRequestExceptions.UnauthorizedException(exceptionResponse.bodyAsText())
                        HttpStatusCode.Forbidden ->
                            throw ClientRequestExceptions.ForbiddenException(exceptionResponse.bodyAsText())
                        HttpStatusCode.Conflict ->
                            throw ClientRequestExceptions.ConflictException(exceptionResponse.bodyAsText())
                        HttpStatusCode.BadRequest ->
                            throw ClientRequestExceptions.InvalidRequestException(exceptionResponse.bodyAsText())
                        else -> {
                            // Throw the default exception
                            throw clientException
                        }
                    }
                }
            }
            defaultRequest {
                val resolvedUrl =
                    settingsHolder.getString(FrontEndApplicationSettingKey.BackEndUrl) ?: "http://0.0.0.0:9292"

                url(resolvedUrl)
            }
            install(ContentNegotiation) {
                json(get())
            }
            install(Logging)

            val pluginList: List<ClientPlugin<*>> = getAll()
            pluginList.forEach { plugin ->
                install(plugin)
            }
        }
    }
}
