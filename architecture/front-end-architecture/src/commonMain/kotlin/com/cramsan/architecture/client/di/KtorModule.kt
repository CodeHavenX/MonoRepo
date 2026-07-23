package com.cramsan.architecture.client.di

import com.cramsan.architecture.client.service.configureStandardRetry
import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
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
internal val KtorModule =
    module {

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
                            HttpStatusCode.NotFound -> {
                                throw ClientRequestExceptions.NotFoundException(exceptionResponse.bodyAsText())
                            }

                            HttpStatusCode.Unauthorized -> {
                                throw ClientRequestExceptions.UnauthorizedException(exceptionResponse.bodyAsText())
                            }

                            HttpStatusCode.Forbidden -> {
                                throw ClientRequestExceptions.ForbiddenException(exceptionResponse.bodyAsText())
                            }

                            HttpStatusCode.Conflict -> {
                                throw ClientRequestExceptions.ConflictException(exceptionResponse.bodyAsText())
                            }

                            HttpStatusCode.BadRequest -> {
                                throw ClientRequestExceptions.InvalidRequestException(exceptionResponse.bodyAsText())
                            }

                            else -> {
                                // Throw the default exception
                                throw clientException
                            }
                        }
                    }
                }
                defaultRequest {
                    val resolvedUrl =
                        settingsHolder.getString(FrontEndApplicationSettingKey.BackEndUrl) ?: "http://localhost:9292"

                    url(resolvedUrl)
                }
                install(ContentNegotiation) {
                    json(get())
                }
                install(Logging) {
                    val loggingSeverity = get<Severity>()
                    level = loggingSeverity.toKtorLogLevel()
                }
                install(HttpRequestRetry) {
                    configureStandardRetry()
                }

                val pluginList: List<ClientPlugin<*>> = getAll()
                pluginList.forEach { plugin ->
                    install(plugin)
                }
            }
        }
    }

private fun Severity.toKtorLogLevel(): LogLevel {
    return when (this) {
        Severity.VERBOSE -> LogLevel.ALL
        Severity.DEBUG -> LogLevel.BODY
        Severity.INFO -> LogLevel.INFO
        Severity.WARNING -> LogLevel.NONE
        Severity.ERROR -> LogLevel.NONE
        Severity.DISABLED -> LogLevel.NONE
    }
}
