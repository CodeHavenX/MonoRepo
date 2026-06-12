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
        return "$path?${params.entries.joinToString("&") { (k, v) -> v.joinToString("&") { "$k=$it" } }}"
    }

    /**
     * Parses [url] and returns a typed destination, or null if the path or required
     * parameters do not match.
     */
    fun fromWebPath(url: String): T? {
        val questionIdx = url.indexOf('?')
        val urlPath = if (questionIdx < 0) url else url.substring(0, questionIdx)
        if (urlPath != path) return null
        val params =
            if (questionIdx < 0) {
                emptyMap()
            } else {
                url
                    .substring(questionIdx + 1)
                    .split("&")
                    .groupBy(
                        keySelector = { it.substringBefore('=') },
                        valueTransform = { it.substringAfter('=') },
                    )
            }
        if (!matchesParams(params)) return null
        return decodeFromKeyValueMap(serializer, params)
    }

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
