package com.cramsan.framework.httpserializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
class SingleValueEncoderDecoderTest {

    @Test
    fun `encode and decode String`() {
        val value = "hello"
        val encoded = encodeToValue(value)
        assertEquals("hello", encoded)
        val decoded: String? = decodeFromValue(encoded)
        assertEquals(value, decoded)
    }

    @Test
    fun `encode and decode Int`() {
        val value = 42
        val encoded = encodeToValue(value)
        assertEquals("42", encoded)
        val decoded: Int? = decodeFromValue(encoded)
        assertEquals(value, decoded)
    }

    @Test
    fun `encode and decode Boolean`() {
        val value = true
        val encoded = encodeToValue(value)
        assertEquals("true", encoded)
        val decoded: Boolean? = decodeFromValue(encoded)
        assertEquals(value, decoded)
    }

    @Test
    fun `encode and decode Enum`() {
        val value = TestEnum.SECOND
        val encoded = encodeToValue(value)
        assertEquals("SECOND", encoded)
        val decoded: TestEnum? = decodeFromValue(encoded)
        assertEquals(value, decoded)
    }

    @Test
    fun `encode and decode null`() {
        val encoded = encodeToValue<String?>(null)
        assertEquals("", encoded)
        val decoded: String = decodeFromValue(encoded)
        assertTrue(decoded.isEmpty())
    }

    @Test
    fun `encode and decode value class`() {
        val value = TestValue(200)
        val encoded = encodeToValue(value)
        assertEquals("200", encoded)
        val decoded: TestValue? = decodeFromValue(encoded)
        assertEquals(value, decoded)
    }

    @Test
    fun `decoding invalid data class throws error`() {
        val encoded = "invalid"
        try {
            decodeFromValue<InvalidDataClass>(encoded)
            assert(false) { "Decoding should have failed" }
        } catch (e: Exception) {
            // Expected exception
        }
    }

    @Test
    fun `encoding invalid data class throws error`() {
        try {
            encodeToValue(InvalidDataClass(1, "test"))
            assert(false) { "Encoding should have failed" }
        } catch (e: Exception) {
            // Expected exception
        }
    }

    @Serializable
    enum class TestEnum { FIRST, SECOND }

    @JvmInline
    @Serializable
    value class TestValue(val value: Int)

    @Serializable
    data class InvalidDataClass(
        val number: Int,
        val text: String
    )
}
