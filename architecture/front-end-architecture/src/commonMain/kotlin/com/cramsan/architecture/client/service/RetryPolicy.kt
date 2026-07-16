package com.cramsan.architecture.client.service

import io.ktor.client.plugins.HttpRequestRetryConfig
import io.ktor.http.HttpMethod

private val RETRYABLE_METHODS = setOf(HttpMethod.Get, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete)
private const val DEFAULT_MAX_RETRIES = 3

/**
 * Configures this [HttpRequestRetryConfig] to retry idempotent requests ([retryableMethods], GET/PUT/PATCH/DELETE
 * by default) on transient failures — 5xx responses and network exceptions — up to [maxRetries] times with
 * exponential backoff. POST requests are left alone by default since they are not guaranteed idempotent; a call
 * site that needs retry on a POST can opt in per-request via `OperationRequest.execute`'s `retryOverride`
 * parameter.
 */
fun HttpRequestRetryConfig.configureStandardRetry(
    maxRetries: Int = DEFAULT_MAX_RETRIES,
    retryableMethods: Set<HttpMethod> = RETRYABLE_METHODS,
) {
    retryOnExceptionOrServerErrors(maxRetries)
    val baseShouldRetry = checkNotNull(retryIf)
    val baseShouldRetryOnException = checkNotNull(retryOnExceptionIf)
    retryIf { request, response ->
        request.method in retryableMethods && baseShouldRetry(request, response)
    }
    retryOnExceptionIf { request, cause ->
        request.method in retryableMethods && baseShouldRetryOnException(request, cause)
    }
    exponentialDelay()
}
