package com.cramsan.framework.httpserializers

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * A decoder that can decode a map of key to list of values into a Kotlin object using kotlinx.serialization.
 *
 * The map should represent query parameters where each key can have multiple values.
 * Lists are supported as multiple values for the same key.
 *
 * Example usage:
 * ```
 * val params = mapOf(
 *     "orgId" to listOf("123"),
 *     "userId" to listOf("42"),
 *     "active" to listOf("true"),
 *     "items" to listOf("1", "2", "3")
 * )
 * val obj = decodeFromKeyValueMap<YourDataClass>(params)
 * ```
 *
 * Note: This decoder does not support nested objects or complex types.
 */
@ExperimentalSerializationApi
open class KeyValueMapDecoder(
    private val params: Map<String, List<String>>
) : AbstractDecoder() {
    private var currentIndex = -1
    private lateinit var currentDescriptor: SerialDescriptor
    override val serializersModule: SerializersModule = SerializersModule {}
    private var currentList: List<String>? = null
    private var currentListIndex: Int = -1

    override fun <T : Any?> decodeSerializableValue(
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T {
        return decodeSerializableValue(deserializer)
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val enumValue = decodeString()
        return enumDescriptor.getElementIndex(enumValue)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (descriptor.kind == StructureKind.LIST) {
            // Find the key for the list (from the parent object)
            val name = currentDescriptor.getElementName(currentIndex)
            val value = params[name] ?: emptyList()
            currentList = value
            currentListIndex = -1
        } else {
            currentDescriptor = descriptor
            currentIndex = -1
        }
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        if (descriptor.kind == StructureKind.LIST) {
            currentList = null
            currentListIndex = -1
        }
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (descriptor.kind == StructureKind.LIST) {
            currentListIndex++
            return if (currentListIndex < (currentList?.size ?: 0)) currentListIndex else CompositeDecoder.DECODE_DONE
        }
        for (i in (currentIndex + 1) until descriptor.elementsCount) {
            val name = descriptor.getElementName(i)
            if (params.contains(name)) {
                currentIndex = i
                return i
            }
        }
        return CompositeDecoder.DECODE_DONE
    }

    override fun decodeString(): String {
        return if (currentList != null) {
            currentList?.get(currentListIndex) ?: error("No value for list at index $currentListIndex")
        } else {
            val name = currentDescriptor.getElementName(currentIndex)
            val value = params[name]
            if (value.isNullOrEmpty()) {
                error("Missing value for key: $name")
            }
            if (value.size > 1) {
                error("Multiple values for key: $name. Found: $value")
            }
            value[0]
        }
    }

    override fun decodeInt(): Int = decodeString().toInt()
    override fun decodeLong(): Long = decodeString().toLong()
    override fun decodeBoolean(): Boolean = decodeString().toBoolean()
    override fun decodeFloat(): Float = decodeString().toFloat()
    override fun decodeDouble(): Double = decodeString().toDouble()
    override fun decodeChar(): Char = decodeString().single()
    override fun decodeShort(): Short = decodeString().toShort()
    override fun decodeByte(): Byte = decodeString().toByte()

    override fun decodeNull(): Nothing? = null
}

/**
 * Decodes a Kotlin object of type [T] from a map of key to list of values using the provided [deserializer].
 *
 * The map should represent query parameters where each key can have multiple values.
 * Lists are supported as multiple values for the same key.
 *
 * Example usage:
 * ```
 * val params = mapOf(
 *     "orgId" to listOf("123"),
 *     "userId" to listOf("42"),
 *     "active" to listOf("true"),
 *     "items" to listOf("1", "2", "3")
 * )
 * val obj = decodeFromKeyValueMap(YourDataClass.serializer(), params)
 * ```
 *
 * Note: This decoder does not support nested objects or complex types.
 */
@OptIn(ExperimentalSerializationApi::class)
fun <T> decodeFromKeyValueMap(deserializer: DeserializationStrategy<T>, map: Map<String, List<String>>): T {
    val decoder = KeyValueMapDecoder(map)
    return decoder.decodeSerializableValue(deserializer)
}

/**
 * Decodes a Kotlin object of type [T] from a map of key to list of values.
 *
 * This is an inline reified version of [decodeFromKeyValueMap] that automatically
 * provides the serializer for type [T].
 *
 * The map should represent query parameters where each key can have multiple values.
 * Lists are supported as multiple values for the same key.
 *
 * Example usage:
 * ```
 * val params = mapOf(
 *     "orgId" to listOf("123"),
 *     "userId" to listOf("42"),
 *     "active" to listOf("true"),
 *     "items" to listOf("1", "2", "3")
 * )
 * val obj = decodeFromKeyValueMap<YourDataClass>(params)
 */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> decodeFromKeyValueMap(map: Map<String, List<String>>): T =
    decodeFromKeyValueMap(serializer(), map)
