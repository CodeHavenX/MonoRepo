package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.service.impl.AuthRequestPlugin
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val KtorModule = module {

    single<HttpClient> {
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
                val defaultUrl = "http://0.0.0.0:9292"

                val resolvedUrl = if (get<Boolean>(named(Overrides.KEY_EDIFIKANA_BE_OVERRIDE_ENABLED))) {
                    get<String>(named(Overrides.KEY_EDIFIKANA_BE_URL)).ifEmpty { defaultUrl }
                } else {
                    defaultUrl
                }

                url(resolvedUrl)
            }
            install(ContentNegotiation) {
                json(createJson())
            }
            install(AuthRequestPlugin(get()))
        }
    }
}
