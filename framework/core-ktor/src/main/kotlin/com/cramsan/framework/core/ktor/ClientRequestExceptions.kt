package com.cramsan.framework.core.ktor

import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

/**
 * Validate the client error. This function will log the error and respond to the client with the result.
 * TODO: We need to have this function be an inline function due to a weird java.lang.NoSuchMethodError when being
 * invoked. I dont know the source of this issue, but making this function inline fixes it for now.
 *
 * After calling this function, the caller should return from the request handler, as this function
 * sends a response to the client.
 *
 * @param tag The tag to use for logging.
 * @param result The result of the function call.
 */
suspend inline fun ApplicationCall.validateClientError(tag: String, result: Result<*>) {
    // Handle the error based on our created exceptions.
    val originalException = result.exceptionOrNull()
    val exception = originalException as? ClientRequestExceptions
    if (exception == null) {
        // If the exception is not a ClientRequestException, we need to log it and return a 500 error.
        logE(tag, "Unexpected failure when handing request", originalException)
        respond(
            HttpStatusCode.InternalServerError,
            originalException?.localizedMessage.orEmpty(),
        )
        return
    }
    validateClientError(tag, exception)
}

/**
 * Validate the client error. This function will log the error and respond to the client with the result.
 * TODO: We need to have this function be an inline function due to a weird java.lang.NoSuchMethodError when being
 * invoked. I dont know the source of this issue, but making this function inline fixes it
 * for now.
 *
 * After calling this function, the caller should return from the request handler, as this function
 * sends a response to the client.
 *
 * @param exception The exception to validate.
 * @param tag The tag to use for logging.
 */
suspend inline fun ApplicationCall.validateClientError(tag: String, exception: ClientRequestExceptions) {
    // Log the error
    logE(tag, "Client Request Exception:", exception)
    when (exception) {
        is ClientRequestExceptions.ConflictException -> {
            respond(
                HttpStatusCode.Conflict,
                exception.localizedMessage.orEmpty(),
            )
            return
        }

        is ClientRequestExceptions.ForbiddenException -> {
            respond(
                HttpStatusCode.Forbidden,
                exception.localizedMessage.orEmpty(),
            )
            return
        }

        is ClientRequestExceptions.InvalidRequestException -> {
            respond(
                HttpStatusCode.BadRequest,
                exception.localizedMessage.orEmpty(),
            )
            return
        }

        is ClientRequestExceptions.NotFoundException -> {
            respond(
                HttpStatusCode.NotFound,
                exception.localizedMessage.orEmpty(),
            )
            return
        }

        is ClientRequestExceptions.UnauthorizedException -> {
            respond(
                HttpStatusCode.Unauthorized,
                exception.localizedMessage.orEmpty(),
            )
            return
        }
    }
}
