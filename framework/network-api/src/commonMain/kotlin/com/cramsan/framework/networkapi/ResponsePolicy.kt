package com.cramsan.framework.networkapi

import io.ktor.http.HttpStatusCode

/**
 * Declares which HTTP responses an [Operation] is allowed to produce.
 *
 * This drives two things: the responses documented in the generated OpenAPI spec, and a runtime
 * guard that coerces any undeclared response to a 500 so an operation can only return responses it
 * has declared. Use [AllowAll] to opt out of enforcement, or [AllowedResponses] to declare a strict
 * allow-list.
 */
sealed interface ResponsePolicy

/**
 * Response policy that permits any response. No runtime enforcement is applied and the generated
 * documentation falls back to a generic set of responses.
 */
data object AllowAll : ResponsePolicy

/**
 * Strict allow-list of the domain-specific responses an operation may produce, keyed by status code
 * with a human-readable description surfaced in the OpenAPI documentation.
 *
 * The framework always additionally permits the universal responses (400, 401, 500) and the success
 * response, so only domain-specific codes such as 403, 404, or 409 need to be declared here.
 *
 * Build instances with the [invoke] DSL:
 * ```
 * AllowedResponses {
 *     HttpStatusCode.NotFound describedAs "No property exists for the given id."
 *     HttpStatusCode.Forbidden describedAs "Caller lacks the required role."
 * }
 * ```
 *
 * @property responses The declared status codes mapped to their descriptions.
 */
class AllowedResponses private constructor(val responses: Map<HttpStatusCode, String>) : ResponsePolicy {
    /**
     * Builder for [AllowedResponses]. Use [describedAs] to declare each allowed response.
     */
    class Builder {
        private val entries = linkedMapOf<HttpStatusCode, String>()

        /**
         * Declares that this status code is an allowed response, documented with [description].
         */
        infix fun HttpStatusCode.describedAs(description: String) {
            entries[this] = description
        }

        internal fun build(): AllowedResponses = AllowedResponses(entries.toMap())
    }

    companion object {
        /**
         * Builds an [AllowedResponses] instance using the provided [block] DSL.
         */
        operator fun invoke(block: Builder.() -> Unit): AllowedResponses = Builder().apply(block).build()
    }
}
