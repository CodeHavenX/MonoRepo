package com.cramsan.framework.networkapi

import io.ktor.http.HttpStatusCode

/**
 * Declares which HTTP responses an [Operation] is allowed to produce.
 *
 * This drives both the responses documented in the generated OpenAPI spec and a runtime guard that
 * coerces any undeclared response to a 500 so an operation can only return responses it permits.
 *
 * The universal responses — the success status plus 400 (request validation), 401 (authentication),
 * and 500 (unexpected errors) — are produced by the framework itself and are therefore always
 * permitted and documented under the strict policies. Implementations:
 * - [AllowAnyResponse]: no enforcement; any response is permitted.
 * - [UniversalResponsesOnly]: only the universal responses are permitted.
 * - [AdditionalResponses]: the universal responses plus explicitly declared domain-specific ones.
 */
sealed interface ResponsePolicy

/**
 * Response policy that permits any response. No runtime enforcement is applied and the generated
 * documentation falls back to a generic set of responses.
 */
data object AllowAnyResponse : ResponsePolicy

/**
 * Strict response policy that permits only the universal responses (the success status plus 400,
 * 401, and 500). Use this for operations that produce no domain-specific responses.
 */
data object UniversalResponsesOnly : ResponsePolicy

/**
 * Strict response policy that permits the universal responses plus the domain-specific responses
 * declared here, keyed by status code with a human-readable description surfaced in the OpenAPI
 * documentation. Only domain-specific codes such as 403, 404, or 409 need to be declared.
 *
 * Build instances with the [invoke] DSL:
 * ```
 * AdditionalResponses {
 *     HttpStatusCode.NotFound describedAs "No property exists for the given id."
 *     HttpStatusCode.Forbidden describedAs "Caller lacks the required role."
 * }
 * ```
 *
 * @property responses The declared status codes mapped to their descriptions.
 */
class AdditionalResponses private constructor(val responses: Map<HttpStatusCode, String>) : ResponsePolicy {
    /**
     * Builder for [AdditionalResponses]. Use [describedAs] to declare each additional response.
     */
    class Builder {
        private val entries = linkedMapOf<HttpStatusCode, String>()

        /**
         * Declares that this status code is an allowed response, documented with [description].
         */
        infix fun HttpStatusCode.describedAs(description: String) {
            entries[this] = description
        }

        internal fun build(): AdditionalResponses = AdditionalResponses(entries.toMap())
    }

    companion object {
        /**
         * Builds an [AdditionalResponses] instance using the provided [block] DSL.
         */
        operator fun invoke(block: Builder.() -> Unit): AdditionalResponses = Builder().apply(block).build()
    }
}
