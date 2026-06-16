package com.cramsan.architecture.client.deeplink

/**
 * Parsed representation of a deep link's URL parameters.
 *
 * @property rawInput The original URL or fragment string passed to the router.
 * @property params Key-value pairs parsed from the fragment or query portion of [rawInput].
 */
data class DeepLinkParams(val rawInput: String, val params: Map<String, String>) {
    companion object {
        /**
         * Parses [rawInput] into a [DeepLinkParams]. Handles full URLs with a fragment (`#key=val`),
         * full URLs with a query string (`?key=val`), and bare `key=val&...` strings.
         */
        fun parse(rawInput: String): DeepLinkParams {
            val queryStr = if ('?' in rawInput) {
                rawInput.substringAfter('?').let { afterQ ->
                    if ('#' in afterQ) afterQ.substringBefore('#') else afterQ
                }
            } else null

            val fragmentStr = if ('#' in rawInput) rawInput.substringAfter('#') else null

            fun toMap(s: String?) = s
                ?.split('&')
                ?.filter { '=' in it }
                ?.associate { it.substringBefore('=') to it.substringAfter('=') }
                ?: emptyMap()

            // Fragment params take precedence over query params on key collision.
            val params = toMap(queryStr) + toMap(fragmentStr)
            return DeepLinkParams(rawInput, params)
        }
    }
}
