package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.templatereplaceme.client.lib.settings.FrontEndApplicationSettingKey
import com.cramsan.templatereplaceme.client.lib.settings.SettingsHolder
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

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
        }
    }
}
