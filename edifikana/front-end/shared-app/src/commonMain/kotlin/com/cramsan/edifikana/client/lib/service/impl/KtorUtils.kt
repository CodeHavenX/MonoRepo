package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.framework.httpserializers.encodeToKeyValueMap
import com.cramsan.framework.networkapi.OperationRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HeadersBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.parameters
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi

/**
 * Executes the given [OperationRequest] using the provided [HttpClient].
 *
 * This function constructs and sends an HTTP request based on the details provided in the
 * [OperationRequest] instance. It handles query parameters, request body, and response deserialization.
 *
 * @param http The [HttpClient] to use for making the request.
 * @return The response deserialized into the expected type [Response].
 * @throws Exception if the request fails or if deserialization fails.
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
suspend inline fun <reified Request : Any, reified QueryParams : Any, reified Response : Any>
    OperationRequest<Request, QueryParams, Response>.execute(
        http: HttpClient,
        noinline headersBlock: (HeadersBuilder.() -> Unit)? = null,
    ): Response {
    val request = this
    val httpRequest = http.request {
        method = request.method
        headersBlock?.let {
            headers(it)
        }
        url {
            appendPathSegments(request.fullPath)
            if (QueryParams::class != Unit::class) {
                val paramsMap = encodeToKeyValueMap(request.queryParam)

                parameters {
                    paramsMap.forEach { (key, value) ->
                        appendAll(key, value)
                    }
                }
            }
        }
        if (request.body != Unit) {
            setBody(request.body)
            contentType(ContentType.Application.Json)
        }
    }

    return if (Response::class == Unit::class) {
        Unit as Response
    } else {
        httpRequest.body()
    }
}
