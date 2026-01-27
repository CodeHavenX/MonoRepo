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
 * An encoder that can encode a Kotlin object into a map of key to list of values using kotlinx.serialization.
 *
 * The resulting map can represent query parameters where each key can have multiple values.
 * Lists are supported as multiple values for the same key.
 *
 * Example usage:
 * ```
 * val obj = YourDataClass(orgId = "123", userId = 42, active = true, items = listOf(1, 2, 3))
 * val map = encodeToKeyValueMap(obj)
 * ```
 *
 * Note: This encoder does not support nested objects or complex types.
 */
@ExperimentalSerializationApi
class KeyValueMapEncoder : AbstractEncoder() {
    private val map = mutableMapOf<String, List<String>>()
    private val array = mutableListOf<String>()
    private var currentTag: String = ""
    override val serializersModule: SerializersModule = SerializersModule {}
    private var depth = DEPTH_UNINITIALIZED

    override fun encodeValue(value: Any) {
        when (depth) {
            DEPTH_OBJECT -> map[currentTag] = listOf(value.toString())

            DEPTH_LIST -> {
                array.add(value.toString())
            }

            else -> error("Unsupported depth $depth")
        }
    }

    override fun encodeNull() = Unit // Ignore nulls

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
                                "Found nested list for ${descriptor.getElementName(index)}",
                        )
                    }
                } else if (it.kind == StructureKind.CLASS || it.kind == StructureKind.OBJECT) {
                    // Allow value classes
                    if (!it.isInline) {
                        error(
                            "Only value classes are supported as nested classes. " +
                                "Found ${it.kind} for ${descriptor.getElementName(index)}",
                        )
                    }
                } else {
                    error(
                        "Only primitives, enums and lists are supported as query params. " +
                            "Found ${it.kind} for ${descriptor.getElementName(index)}",
                    )
                }
            }
        }

        if (depth == DEPTH_OBJECT) {
            // First level must be a class or object
            // We only can add field names at the object level
            currentTag = descriptor.getElementName(index)
        }
        return true
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encodeString(enumDescriptor.getElementName(index))
    }

    @Suppress("ComplexCondition")
    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        val kind = serializer.descriptor.kind
        if (
            // First level must be a class or object. Value classes are not allowed at top level.
            // Value classes can be used as values in the map.
            (kind == StructureKind.CLASS && depth != DEPTH_UNINITIALIZED) ||
            (kind == StructureKind.OBJECT && depth != DEPTH_UNINITIALIZED)
        ) {
            if (!serializer.descriptor.isInline) {
                error("Class or object only allowed on top level or as value class. Found $kind")
            }
        }
        if (kind == StructureKind.LIST && depth != DEPTH_OBJECT) {
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
            map[currentTag] = array.toList()
            array.clear()
        }
        depth--
    }

    /**
     * Returns the encoded map of key to list of values.
     */
    fun encode(): Map<String, List<String>> = map.toMap()
}

/**
 * Encodes a Kotlin object into a map of key to list of values using the provided serializer.
 *
 * The resulting map can represent query parameters where each key can have multiple values.
 * Lists are supported as multiple values for the same key.
 *
 * Example usage:
 * ```
 * val obj = YourDataClass(orgId = "123", userId = 42, active = true, items = listOf(1, 2, 3))
 * val map = encodeToKeyValueMap(YourDataClass.serializer(), obj)
 * ```
 *
 * Note: This encoder does not support nested objects or complex types.
 */
@OptIn(ExperimentalSerializationApi::class)
fun <T> encodeToKeyValueMap(serializer: SerializationStrategy<T>, value: T): Map<String, List<String>> {
    val encoder = KeyValueMapEncoder()
    encoder.encodeSerializableValue(serializer, value)
    return encoder.encode()
}

/**
 * Encodes a Kotlin object into a map of key to list of values.
 *
 * This is an inline reified version of [encodeToKeyValueMap] that automatically
 * provides the serializer for type [T].
 *
 * Example usage:
 * ```
 * val obj = YourDataClass(orgId = "123", userId = 42, active = true, items = listOf(1, 2, 3))
 * val map = encodeToKeyValueMap(obj)
 * ```
 *
 * Note: This encoder does not support nested objects or complex types.
 */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> encodeToKeyValueMap(value: T): Map<String, List<String>> =
    encodeToKeyValueMap(serializer(), value)

private const val DEPTH_UNINITIALIZED = -1
private const val DEPTH_OBJECT = 0
private const val DEPTH_LIST = 1
