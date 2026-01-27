package com.cramsan.framework.core.ktor

import com.cramsan.framework.httpserializers.KeyValueMapDecoder
import io.ktor.http.Parameters
import io.ktor.util.toMap
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer

/**
 * A decoder that can decode query parameters into a Kotlin object using kotlinx.serialization.
 *
 * The query parameters should be in the format: key1=value1&key2=value2
 * Lists are supported as comma-separated values: key=listValue1,listValue2
 *
 * Example usage:
 * ```
 * val query = "orgId=123&userId=42&active=true&items=1,2,3"
 * val obj = decodeFromQueryParams<YourDataClass>(query)
 * ```
 *
 * Note: This decoder does not support nested objects or complex types.
 */
@ExperimentalSerializationApi
class KtorParamDecoder(queryParams: Parameters) : KeyValueMapDecoder(queryParams.toMap())

/**
 * Decodes a query parameter string into an object of type [T] using the provided [deserializer].
 *
 * @param deserializer The deserialization strategy for type [T].
 * @param T The type of the object to decode.
 * @param parameters The query parameters to decode.
 * @return An instance of type [T] populated with values from the query parameters.
 * @throws IllegalStateException If the query parameters cannot be properly decoded into the target type.
 */
@OptIn(ExperimentalSerializationApi::class)
fun <T> decodeFromQueryParams(deserializer: DeserializationStrategy<T>, parameters: Parameters): T {
    val decoder = KtorParamDecoder(parameters)
    return decoder.decodeSerializableValue(deserializer)
}

/**
 * Decodes a query parameter string into an object of type [T].
 *
 * This is an inline reified version of [decodeFromQueryParams] that automatically
 * provides the serializer for type [T].
 *
 * @param T The type of the object to decode.
 * @param parameters The query parameters to decode.
 * @return An instance of type [T] populated with values from the query parameters.
 * @throws IllegalStateException If the query parameters cannot be properly decoded into the target type.
 */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> decodeFromQueryParams(parameters: Parameters): T =
    decodeFromQueryParams(serializer(), parameters)
