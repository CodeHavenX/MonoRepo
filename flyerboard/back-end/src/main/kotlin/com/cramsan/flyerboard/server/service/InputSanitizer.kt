package com.cramsan.flyerboard.server.service

/**
 * Stateless utility for sanitizing user-supplied text fields.
 *
 * Strips HTML tags, trims surrounding whitespace, and truncates to a maximum length so that no
 * script injection or oversized content can reach storage.
 */
object InputSanitizer {

    private val HTML_TAG_REGEX = Regex("<[^>]*>")

    /**
     * Returns a sanitized copy of [input]:
     * 1. All HTML tags (anything matching `<...>`) are removed.
     * 2. Leading and trailing whitespace is trimmed.
     * 3. The result is truncated to [maxLength] characters.
     *
     * An empty or blank-only input returns an empty string.
     */
    fun sanitizeText(input: String, maxLength: Int): String {
        return input
            .replace(HTML_TAG_REGEX, "")
            .trim()
            .take(maxLength)
    }
}
