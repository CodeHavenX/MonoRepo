package com.cramsan.architecture.client.service

import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi

/**
 * Executes the given [OperationRequest] using the provided [HttpClient].
 *
 * This function constructs and sends an HTTP request based on the details provided in the
 * [OperationRequest] instance. It handles query parameters, request body, and response deserialization.
 * If [NoRequestBody] is used, no body is sent. If [NoResponseBody] is expected, the function returns [NoResponseBody].
 *
 * @param http The [HttpClient] to use for making the request.
 * @param headersBlock Optional block to add custom headers to the request.
 * @return The response deserialized into the expected type [ResponseType], or [NoResponseBody] if specified.
 * @throws Exception if the request fails or if deserialization fails.
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
suspend inline fun <
    reified RequestType : RequestBody,
    reified QueryParamsType : QueryParam,
    reified PathParamsType : PathParam,
    reified ResponseType : ResponseBody,
    > OperationRequest<RequestType, QueryParamsType, PathParamsType, ResponseType>.execute(
    http: HttpClient,
    noinline headersBlock: (HeadersBuilder.() -> Unit)? = null,
): ResponseType {
    val request = this
    val httpRequest = http.request {
        method = request.method
        headersBlock?.let {
            headers(it)
        }
        url {
            appendPathSegments(request.fullPath)
            if (QueryParamsType::class != NoQueryParam::class) {
                val paramsMap = encodeToKeyValueMap(request.queryParam)

                paramsMap.forEach { (key, value) ->
                    parameters.appendAll(key, value)
                }
            }
        }
        if (request.body != NoRequestBody) {
            setBody(request.body)
            contentType(ContentType.Application.Json)
        }
    }

    return if (ResponseType::class == NoResponseBody::class) {
        NoResponseBody as ResponseType
    } else {
        httpRequest.body()
    }
}
