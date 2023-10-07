package com.codehavenx.platform.bot.ktor

import io.ktor.http.HttpStatusCode

/**
 * Data class to wrap an HTTP response. The [body] can be of type [Any] and it will be handled by ktor into the right
 * content type.
 */
data class HttpResponse(
    val status: HttpStatusCode,
    val body: Any,
)
