package com.cramsan.framework.core.ktor

import io.ktor.http.HttpStatusCode

/**
 * Represents an HTTP response to be used within Ktor applications.
 * The goal of this class is to provide a common response format for all responses.
 */
data class HttpResponse<T : Any>(val status: HttpStatusCode, val body: T?)
