package com.cramsan.framework.httpserializers

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * A decoder that decodes a single primitive value from a string.
 * Supports primitive types and inline value classes with a single property.
 */
@ExperimentalSerializationApi
class SingleValueDecoder(private val value: String) : AbstractDecoder() {
    override val serializersModule: SerializersModule = SerializersModule {}

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        TODO("Invalid call to decodeElementIndex on SingleValueDecoder")
    }

    override fun decodeString(): String = value ?: error("No value provided")
    override fun decodeInt(): Int = decodeString().toInt()
    override fun decodeLong(): Long = decodeString().toLong()
    override fun decodeBoolean(): Boolean = decodeString().toBoolean()
    override fun decodeFloat(): Float = decodeString().toFloat()
    override fun decodeDouble(): Double = decodeString().toDouble()
    override fun decodeChar(): Char = decodeString().single()
    override fun decodeShort(): Short = decodeString().toShort()
    override fun decodeByte(): Byte = decodeString().toByte()
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = enumDescriptor.getElementIndex(decodeString())

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        when (deserializer.descriptor.kind) {
            PrimitiveKind.BOOLEAN, PrimitiveKind.BYTE, PrimitiveKind.CHAR, PrimitiveKind.DOUBLE,
            PrimitiveKind.FLOAT, PrimitiveKind.INT, PrimitiveKind.LONG, PrimitiveKind.SHORT,
            PrimitiveKind.STRING, SerialKind.ENUM,
            -> {
                // Supported primitive types
            }

            StructureKind.CLASS -> {
                if (!deserializer.descriptor.isInline || deserializer.descriptor.elementsCount != 1) {
                    error("SingleValueDecoder only supports inline value classes with a single property")
                }
            }

            SerialKind.CONTEXTUAL, StructureKind.LIST, StructureKind.MAP, StructureKind.OBJECT,
            PolymorphicKind.OPEN, PolymorphicKind.SEALED,
            -> {
                error("SingleValueDecoder does not support ${deserializer.descriptor.kind}")
            }
        }
        return deserializer.deserialize(this)
    }
}

/**
 * Decodes a value from its string representation using the provided deserializer.
 *
 * @param deserializer The deserialization strategy for the target type.
 * @param value The string representation of the value to decode.
 * @return The decoded value, or null if the input value is null.
 */
@OptIn(ExperimentalSerializationApi::class)
fun <T> decodeFromValue(deserializer: DeserializationStrategy<T>, value: String): T =
    SingleValueDecoder(value).decodeSerializableValue(deserializer)

/**
 * Inline function to decode a value from its string representation using reified type.
 *
 * @param T The target type to decode to.
 * @param value The string representation of the value to decode.
 * @return The decoded value of type T, or null if the input value is null.
 */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> decodeFromValue(value: String): T = decodeFromValue(serializer(), value)
