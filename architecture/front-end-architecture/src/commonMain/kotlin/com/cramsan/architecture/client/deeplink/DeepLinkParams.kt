package com.cramsan.architecture.client.deeplink

/**
 * Parsed representation of a deep link's URL parameters.
 *
 * @property rawInput The original URL or fragment string passed to the router.
 * @property params Key-value pairs parsed from the fragment or query portion of [rawInput].
 */
data class DeepLinkParams(
    val rawInput: String,
    val params: Map<String, String>,
) {
    companion object {
        /**
         * Parses [rawInput] into a [DeepLinkParams]. Handles full URLs with a fragment (`#key=val`),
         * full URLs with a query string (`?key=val`), and bare `key=val&...` strings.
         */
        fun parse(rawInput: String): DeepLinkParams {
            val paramStr = when {
                '#' in rawInput -> rawInput.substringAfter('#')
                '?' in rawInput -> rawInput.substringAfter('?')
                else -> rawInput
            }
            val params = paramStr.split('&')
                .filter { '=' in it }
                .associate { it.substringBefore('=') to it.substringAfter('=') }
            return DeepLinkParams(rawInput, params)
        }
    }
}
