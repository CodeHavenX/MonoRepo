package com.cramsan.framework.httpserializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * An encoder that can encode a Kotlin object into query parameters using kotlinx.serialization.
 *
 * The resulting query parameters will be in the format: key1=value1&key2=value2
 * Lists are supported as comma-separated values: key=listValue1,listValue2
 *
 * Example usage:
 * ```
 * val obj = YourDataClass(...)
 * val query = encodeToQueryParams(obj)
 * ```
 *
 * Note: This encoder does not support nested objects or complex types.
 */
@ExperimentalSerializationApi
class QueryParamEncoder : AbstractEncoder() {
    private val map = mutableMapOf<String, String>()
    private val array = mutableListOf<String>()
    private var currentTag: String = ""
    override val serializersModule: SerializersModule = SerializersModule {}
    private var depth = DEPTH_UNITILIZED

    override fun encodeValue(value: Any) {
        when (depth) {
            DEPTH_OBJECT -> map[currentTag] = value.toString()
            DEPTH_LIST -> {
                array.add(value.toString())
            }
            else -> error("Unsupported depth $depth")
        }
    }

    override fun encodeNull() {
        map[currentTag] = ""
    }

    private val allowedKinds = setOf(
        PrimitiveKind.BOOLEAN,
        PrimitiveKind.BYTE,
        PrimitiveKind.CHAR,
        PrimitiveKind.DOUBLE,
        PrimitiveKind.FLOAT,
        PrimitiveKind.INT,
        PrimitiveKind.LONG,
        PrimitiveKind.SHORT,
        PrimitiveKind.STRING,
        SerialKind.ENUM,
    )

    @Suppress("NestedBlockDepth")
    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        descriptor.getElementDescriptor(index).let {
            if (!allowedKinds.contains(it.kind)) {
                if (it.kind == StructureKind.LIST) {
                    if (depth != DEPTH_OBJECT) {
                        error(
                            "Lists are only supported as top level properties. " +
                                "Found nested list for ${descriptor.getElementName(index)}"
                        )
                    }
                } else {
                    error(
                        "Only primitives, enums and lists are supported as query params. " +
                            "Found ${it.kind} for ${descriptor.getElementName(index)}"
                    )
                }
            }
        }

        currentTag = descriptor.getElementName(index)
        return true
    }

    @Suppress("ComplexCondition")
    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        val kind = serializer.descriptor.kind
        if (
            // First level must be a class or object
            kind == StructureKind.CLASS && depth != DEPTH_UNITILIZED ||
            kind == StructureKind.OBJECT && depth != DEPTH_UNITILIZED
        ) {
            error("Top level value must be a class or object. Found $kind")
        }
        if (kind == StructureKind.LIST && depth != DEPTH_LIST) {
            error("Nested lists are not supported")
        }
        if (
            // Nested level must be primitive, enum or list
            !allowedKinds.contains(kind) &&
            kind != StructureKind.LIST &&
            kind != StructureKind.CLASS &&
            kind != StructureKind.OBJECT
        ) {
            error("Only primitives, enums and lists are supported as query params. Found $kind")
        }
        serializer.serialize(this, value)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        depth++
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        if (depth == DEPTH_LIST) {
            map[currentTag] = array.joinToString(",")
            array.clear()
        }
        depth--
    }

    /**
     * Encodes the collected key-value pairs into a query parameter string.
     *
     * @return A string representing the encoded query parameters.
     */
    fun encode(): String {
        if (map.isEmpty()) return ""
        return map.entries.joinToString(separator = "&") { (k, v) ->
            "$k=$v"
        }
    }
}

/**
 * Encodes a Kotlin object into a query parameter string using the provided serializer.
 *
 * @param T The type of the object to encode.
 * @param serializer The serializer for the type T.
 * @param value The object to encode.
 * @return A string representing the encoded query parameters.
 */
@ExperimentalSerializationApi
fun <T> encodeToQueryParams(serializer: SerializationStrategy<T>, value: T): String {
    val encoder = QueryParamEncoder()
    encoder.encodeSerializableValue(serializer, value)
    return encoder.encode()
}

/**
 * Encodes a Kotlin object into a query parameter string using its reified serializer.
 *
 * @param T The type of the object to encode.
 * @param value The object to encode.
 * @return A string representing the encoded query parameters.
 */
@ExperimentalSerializationApi
inline fun <reified T> encodeToQueryParams(value: T) = encodeToQueryParams(serializer(), value)

private const val DEPTH_UNITILIZED = -1
private const val DEPTH_OBJECT = 0
private const val DEPTH_LIST = 1
