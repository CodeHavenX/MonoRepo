package com.cramsan.framework.httpserializers

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

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
abstract class BaseQueryParamDecoder : AbstractDecoder() {
    protected abstract val params: Map<String, String>
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

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (descriptor.kind == StructureKind.LIST) {
            // Find the key for the list (from the parent object)
            val name = currentDescriptor.getElementName(currentIndex)
            val value = params[name] ?: ""
            currentList = if (value.isEmpty()) emptyList() else value.split(",")
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
            currentList!![currentListIndex]
        } else {
            val name = currentDescriptor.getElementName(currentIndex)
            params[name] ?: error("Missing query param: $name")
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
