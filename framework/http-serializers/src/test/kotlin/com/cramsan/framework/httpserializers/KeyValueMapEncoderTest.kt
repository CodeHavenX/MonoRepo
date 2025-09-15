package com.cramsan.framework.httpserializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSerializationApi::class)
class KeyValueMapEncoderTest {
    @Serializable
    data class SimplePrimitives(val a: Int, val b: String, val c: Boolean)

    @Serializable
    data class ListOfPrimitives(val items: List<Int>)

    @Serializable
    enum class Color { RED, GREEN, BLUE }

    @Serializable
    data class WithEnum(val color: Color)

    @Serializable
    data class AllSupportedTypes(
        val bool: Boolean,
        val byte: Byte,
        val char: Char,
        val double: Double,
        val float: Float,
        val int: Int,
        val long: Long,
        val short: Short,
        val string: String,
        val enum: Color
    )

    @Serializable
    data class NestedClass(val a: Int, val nested: SimplePrimitives)

    @Serializable
    data class NestedList(val a: Int, val nested: List<List<Int>>)

    @Test
    fun `encodes simple primitives`() {
        val obj = SimplePrimitives(1, "test", true)
        val map = encodeToKeyValueMap(obj)
        assertEquals(
            mapOf(
                "a" to listOf("1"),
                "b" to listOf("test"),
                "c" to listOf("true")
            ), map)
    }

    @Test
    fun `encodes list of primitives`() {
        val obj = ListOfPrimitives(listOf(1, 2, 3))
        val map = encodeToKeyValueMap(obj)
        assertEquals(
            mapOf("items" to listOf("1", "2", "3")),
            map
        )
    }

    @Test
    fun `encodes enum`() {
        val obj = WithEnum(Color.GREEN)
        val map = encodeToKeyValueMap(obj)
        assertEquals(
            mapOf("color" to listOf("GREEN")),
            map
        )
    }

    @Test
    fun `encodes all supported types`() {
        val obj = AllSupportedTypes(
            bool = true,
            byte = 1,
            char = 'x',
            double = 2.0,
            float = 3.0f,
            int = 4,
            long = 5L,
            short = 6,
            string = "str",
            enum = Color.BLUE,
        )
        val map = encodeToKeyValueMap(obj)
        assertEquals(
            mapOf(
                "bool" to listOf("true"),
                "byte" to listOf("1"),
                "char" to listOf("x"),
                "double" to listOf("2.0"),
                "float" to listOf("3.0"),
                "int" to listOf("4"),
                "long" to listOf("5"),
                "short" to listOf("6"),
                "string" to listOf("str"),
                "enum" to listOf("BLUE")
            ),
            map
        )
    }

    @Test
    fun `throws on nested class`() {
        val obj = NestedClass(1, SimplePrimitives(2, "fail", false))
        assertFailsWith<IllegalStateException> {
            encodeToKeyValueMap(obj)
        }
    }

    @Test
    fun `throws on nested list`() {
        val obj = NestedList(1, listOf(listOf(1, 2), listOf(3, 4)))
        assertFailsWith<IllegalStateException> {
            encodeToKeyValueMap(obj)
        }
    }
}

