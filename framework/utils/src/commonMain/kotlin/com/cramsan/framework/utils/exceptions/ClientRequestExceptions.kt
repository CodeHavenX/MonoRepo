package com.cramsan.framework.utils.exceptions

/**
 * Custom exceptions for server errors. This class is used to validate calls to the api contain what we
 * expect and return custom error messaging with corresponding 400 status codes.
 */
@Suppress("MagicNumber")
sealed class ClientRequestExceptions(
    val statusCode: Int,
    override val message: String,
    override val cause: Throwable?
) : Throwable(message, cause) {
    /**
     * Invalid request exception.
     */
    class InvalidRequestException(message: String, cause: Throwable? = null) : ClientRequestExceptions(400, message, cause)

    /**
     * Unauthorized exception.
     */
    class UnauthorizedException(message: String, cause: Throwable? = null) : ClientRequestExceptions(401, message, cause)

    /**
     * Forbidden exception.
     */
    class ForbiddenException(message: String, cause: Throwable? = null) : ClientRequestExceptions(403, message, cause)

    /**
     * Not found exception.
     */
    class NotFoundException(message: String, cause: Throwable? = null) : ClientRequestExceptions(404, message, cause)

    /**
     * Conflict exception.
     */
    class ConflictException(message: String, cause: Throwable? = null) : ClientRequestExceptions(409, message, cause)
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
 * Requires that all the values are not null.
 *
 * @param message The message to use if any of the values are null.
 * @param predicate The predicate to use to check if the value is valid.
 * @param values The values to check.
 * @throws ClientRequestExceptions.InvalidRequestException If any of the values are null.
 */
fun <T> requireAll(
    message: String = "All fields must be provided.",
    predicate: (T?) -> Boolean = { it != null },
    vararg values: T?,
) {
    if (values.all(predicate)) {
        return
    }
    throw ClientRequestExceptions.InvalidRequestException(message)
}

/**
 * Helper function: Requires that at least one of the string values is not null or blank.
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
 * Helper function: Requires that all the string values are not null or blank.
 */
fun requireAll(
    message: String = "All fields must be provided.",
    vararg values: String?,
) {
    requireAll(message, { !it.isNullOrBlank() }, *values)
}

// Type aliases for easier access to exceptions
typealias InvalidRequestException = ClientRequestExceptions.InvalidRequestException
typealias UnauthorizedException = ClientRequestExceptions.UnauthorizedException
typealias ForbiddenException = ClientRequestExceptions.ForbiddenException
typealias NotFoundException = ClientRequestExceptions.NotFoundException
typealias ConflictException = ClientRequestExceptions.ConflictException
