package com.cramsan.framework.core.compose.navigation

import com.cramsan.framework.httpserializers.decodeFromKeyValueMap
import com.cramsan.framework.httpserializers.encodeToKeyValueMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Bidirectional mapping between a typed [Destination] and its canonical browser URL.
 *
 * [path] is the fixed path prefix (e.g. "/auth/sign-up"). Destination properties are
 * serialized automatically via [encodeToKeyValueMap] / [decodeFromKeyValueMap], so no
 * manual render/parse lambdas are needed. Before decoding, the query parameters are checked
 * against the serializer descriptor: every required field must be present, and every
 * parameter must correspond to a known field. This allows multiple routes sharing the same
 * path prefix (e.g. a list view and a detail view) to be tried in any order, with the route
 * matching the most specific set of parameters winning.
 */
@OptIn(ExperimentalSerializationApi::class)
class WebRoute<T : Destination>(val path: String, private val serializer: KSerializer<T>) {
    /**
     * Serializes [destination] to its canonical URL string.
     */
    fun toWebPath(destination: T): String {
        val params = encodeToKeyValueMap(serializer, destination)
        if (params.isEmpty()) return path
        return "$path?${
            params.entries.joinToString("&") { (k, v) -> v.joinToString("&") { "$k=${it.percentEncode()}" } }
        }"
    }

    /**
     * Parses [url] and returns a typed destination, or null if the path or required
     * parameters do not match.
     *
     * Both the query string (`?k=v`) and the fragment (`#k=v`) are parsed as parameter
     * sources, with fragment values taking precedence over query values on key collision.
     * This single shape covers every external entry point: a plain browser path/query,
     * an OAuth/Supabase PKCE redirect (`?code=...`), and an implicit-flow redirect
     * (`#access_token=...&type=recovery`).
     */
    fun fromWebPath(url: String): T? {
        val pathEndIdx = url.indexOfFirst { it == '?' || it == '#' }
        val urlPath = if (pathEndIdx < 0) url else url.substring(0, pathEndIdx)
        if (urlPath != path) return null

        val queryStr =
            if ('?' in url) {
                url.substringAfter('?').let { afterQ -> if ('#' in afterQ) afterQ.substringBefore('#') else afterQ }
            } else {
                null
            }
        val fragmentStr = if ('#' in url) url.substringAfter('#') else null

        // Fragment params win over query params on key collision.
        val params = paramsOf(queryStr) + paramsOf(fragmentStr)
        if (!matchesParams(params)) return null
        return decodeFromKeyValueMap(serializer, params)
    }

    private fun paramsOf(source: String?): Map<String, List<String>> =
        source
            ?.split("&")
            ?.filter { '=' in it }
            ?.map { it.substringBefore('=').percentDecode() to it.substringAfter('=').percentDecode() }
            ?.groupBy(keySelector = { it.first }, valueTransform = { it.second })
            ?: emptyMap()

    /**
     * Returns true if [params] contains every required field of [serializer] and no
     * parameters that don't correspond to a field, so other routes sharing [path] aren't
     * mistaken for this one.
     */
    private fun matchesParams(params: Map<String, List<String>>): Boolean {
        val descriptor = serializer.descriptor
        val elementNames = (0 until descriptor.elementsCount).map { descriptor.getElementName(it) }
        if (params.keys.any { it !in elementNames }) return false
        for (i in 0 until descriptor.elementsCount) {
            if (!descriptor.isElementOptional(i)) {
                if (!params.containsKey(descriptor.getElementName(i))) return false
            }
        }
        return true
    }
}

/**
 * Creates a [WebRoute] for the given [path] using the serializer resolved at compile time for [T].
 */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Destination> webRoute(path: String): WebRoute<T> = WebRoute(path, serializer())

private const val HEX_DIGITS = "0123456789ABCDEF"
private val unreservedChars = (('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('-', '_', '.', '~')).toSet()

/**
 * Percent-encodes this string for safe inclusion as a query/fragment value, leaving only
 * RFC 3986 unreserved characters untouched. Multiplatform-safe (no `java.net.URLEncoder`).
 */
internal fun String.percentEncode(): String {
    val builder = StringBuilder()
    for (byte in encodeToByteArray()) {
        val codePoint = byte.toInt() and 0xFF
        val char = codePoint.toChar()
        if (codePoint < BYTE_MAX_VALUE && char in unreservedChars) {
            builder.append(char)
        } else {
            builder
                .append('%')
                .append(HEX_DIGITS[(codePoint shr HEX_SHIFT) and HEX_MASK])
                .append(HEX_DIGITS[codePoint and HEX_MASK])
        }
    }
    return builder.toString()
}

/**
 * Reverses [percentEncode]. Also tolerates literal (non-percent-encoded) text arriving from
 * external producers that don't escape their output, re-encoding it to UTF-8 bytes as-is.
 */
internal fun String.percentDecode(): String {
    val bytes = mutableListOf<Byte>()
    var i = 0

    fun isEscapeAt(idx: Int) = this[idx] == '%' && idx + 2 < length
    while (i < length) {
        if (isEscapeAt(i)) {
            bytes.add(substring(i + 1, i + PERCENT_ESCAPE_LENGTH).toInt(HEX_RADIX).toByte())
            i += PERCENT_ESCAPE_LENGTH
        } else {
            val start = i
            while (i < length && !isEscapeAt(i)) i++
            bytes.addAll(substring(start, i).encodeToByteArray().toList())
        }
    }
    return bytes.toByteArray().decodeToString()
}

private const val BYTE_MAX_VALUE = 128
private const val HEX_SHIFT = 4
private const val HEX_MASK = 0xF
private const val PERCENT_ESCAPE_LENGTH = 3
private const val HEX_RADIX = 16
