package com.cramsan.framework.core.ktor

import io.ktor.http.HttpStatusCode

data class HttpResponse(
    val status: HttpStatusCode,
    val body: Any?,
)
