package com.cramsan.framework.httpserializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * An encoder that encodes a single primitive value to a string.
 * Supports primitive types and inline value classes with a single property.
 */
@ExperimentalSerializationApi
class SingleValueEncoder : AbstractEncoder() {
    private var encoded: String? = null
    override val serializersModule: SerializersModule = SerializersModule {}

    override fun encodeValue(value: Any) {
        encoded = value.toString()
    }

    override fun encodeNull() {
        encoded = null
    }

    override fun encodeEnum(enumDescriptor: kotlinx.serialization.descriptors.SerialDescriptor, index: Int) {
        encoded = enumDescriptor.getElementName(index)
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        when (serializer.descriptor.kind) {
            PrimitiveKind.BOOLEAN, PrimitiveKind.BYTE, PrimitiveKind.CHAR, PrimitiveKind.DOUBLE,
            PrimitiveKind.FLOAT, PrimitiveKind.INT, PrimitiveKind.LONG, PrimitiveKind.SHORT,
            PrimitiveKind.STRING, SerialKind.ENUM,
            -> {
                // Supported primitive types
            }

            StructureKind.CLASS -> {
                if (!serializer.descriptor.isInline || serializer.descriptor.elementsCount != 1) {
                    error("SingleValueEncoder only supports inline value classes with a single property")
                }
            }

            SerialKind.CONTEXTUAL, StructureKind.LIST, StructureKind.MAP, StructureKind.OBJECT,
            PolymorphicKind.OPEN, PolymorphicKind.SEALED,
            -> {
                error("SingleValueEncoder does not support ${serializer.descriptor.kind}")
            }
        }
        serializer.serialize(this, value)
    }

    /**
     * Returns the encoded string value.
     *
     * @return The encoded string, or null if the value was null.
     */
    fun encode(): String = encoded.orEmpty()
}

/**
 * Encodes a value of type [T] to its string representation using the provided [serializer].
 *
 * @param serializer The serialization strategy for type [T].
 * @param value The value to encode.
 * @return The string representation of the value, or null if the value is null.
 */
@OptIn(ExperimentalSerializationApi::class)
fun <T> encodeToValue(serializer: SerializationStrategy<T>, value: T): String =
    SingleValueEncoder().apply { encodeSerializableValue(serializer, value) }.encode()

/**
 * Encodes a value of type [T] to its string representation using the serializer inferred from the reified type.
 */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> encodeToValue(value: T): String = encodeToValue(serializer(), value)
