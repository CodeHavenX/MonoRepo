package com.cramsan.edifikana.lib.utils

/**
 * Custom exceptions for server errors. This class is used to validate calls to the api contain what we
 * expect and return custom error messaging with corresponding 400 status codes.
 */
@Suppress("MagicNumber")
sealed class ClientRequestExceptions(
    val statusCode: Int,
    override val message: String
) : Throwable() {
    /**
     * Invalid request exception.
     */
    class InvalidRequestException(message: String) : ClientRequestExceptions(400, message)

    /**
     * Unauthorized exception.
     */
    class UnauthorizedException(message: String) : ClientRequestExceptions(401, message)

    /**
     * Forbidden exception.
     */
    class ForbiddenException(message: String) : ClientRequestExceptions(403, message)

    /**
     * Not found exception.
     */
    class NotFoundException(message: String) : ClientRequestExceptions(404, message)

    /**
     * Conflict exception.
     */
    class ConflictException(message: String) : ClientRequestExceptions(409, message)
}

/**
 * Requires that at least one of the values is not null.
 *
 * @param message The message to use if none of the values are not null.
 * @param predicate The predicate to use to check if the value is valid.
 * @param values The values to check.
 * @throws ClientRequestExceptions.InvalidRequestException If none of the values are not null.
 */
fun <T> requireAtLeastOne(
    message: String = "At least one field must be provided.",
    predicate: (T?) -> Boolean = { it != null },
    vararg values: T?,
) {
    if (values.any(predicate)) {
        return
    }
    throw ClientRequestExceptions.InvalidRequestException(message)
}

/**
 * Requires that at least one of the string values is not null or blank.
 *
 * @param message The message to use if none of the values are not null.
 * @param values The values to check.
 * @throws ClientRequestExceptions.InvalidRequestException If none of the values are not null.
 */
fun requireAtLeastOne(
    message: String = "At least one field must be provided.",
    vararg values: String?,
) {
    requireAtLeastOne(message, { !it.isNullOrBlank() }, *values)
}

/**
 * Checks that the result was successful and returns the value if it was. Otherwise, throws the exception.
 */
fun <T> Result<T>.requireSuccess(): T {
    if (isFailure) {
        val rootException = exceptionOrNull()!!
        throw rootException
    }
    return getOrThrow()
}
