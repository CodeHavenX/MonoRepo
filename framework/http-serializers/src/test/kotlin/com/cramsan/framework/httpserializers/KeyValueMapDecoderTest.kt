package com.cramsan.framework.httpserializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSerializationApi::class)
class KeyValueMapDecoderTest {
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
    fun `decodes simple primitives`() {
        val map = mapOf("a" to listOf("1"), "b" to listOf("test"), "c" to listOf("true"))
        val obj: SimplePrimitives = decodeFromKeyValueMap(map)
        assertEquals(SimplePrimitives(1, "test", true), obj)
    }

    @Test
    fun `decodes list of primitives`() {
        val map = mapOf("items" to listOf("1", "2", "3"))
        val obj: ListOfPrimitives = decodeFromKeyValueMap(map)
        assertEquals(ListOfPrimitives(listOf(1, 2, 3)), obj)
    }

    @Test
    fun `decodes enum`() {
        val map = mapOf("color" to listOf("GREEN"))
        val obj: WithEnum = decodeFromKeyValueMap(map)
        assertEquals(WithEnum(Color.GREEN), obj)
    }

    @Test
    fun `decodes all supported types`() {
        val map = mapOf(
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
        )
        val obj: AllSupportedTypes = decodeFromKeyValueMap(map)
        assertEquals(
            AllSupportedTypes(true, 1, 'x', 2.0, 3.0f, 4, 5L, 6, "str", Color.BLUE),
            obj
        )
    }

    @Test
    fun `throws on nested class`() {
        val map = mapOf("a" to listOf("1"), "nested" to listOf("should-fail"))
        assertFailsWith<Exception> {
            decodeFromKeyValueMap(NestedClass.serializer(), map)
        }
    }

    @Test
    fun `throws on nested list`() {
        val map = mapOf("a" to listOf("1"), "nested" to listOf("1,2", "3,4"))
        assertFailsWith<Exception> {
            decodeFromKeyValueMap(NestedList.serializer(), map)
        }
    }

    @Test
    fun `throws on missing value`() {
        val map = mapOf("a" to listOf("1")) // missing b and c
        assertFailsWith<Exception> {
            decodeFromKeyValueMap(SimplePrimitives.serializer(), map)
        }
    }

    @Test
    fun `throws on multiple values for primitive`() {
        val map = mapOf("a" to listOf("1", "2"), "b" to listOf("test"), "c" to listOf("true"))
        assertFailsWith<Exception> {
            decodeFromKeyValueMap(SimplePrimitives.serializer(), map)
        }
    }
}

