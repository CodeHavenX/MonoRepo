package com.cramsan.framework.httpserializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSerializationApi::class)
class QueryParamEncoderTest {
    @Serializable
    data class SingleField(val orgId: String)

    @Serializable
    data class MultiField(val orgId: String, val userId: Int, val active: Boolean)

    @Serializable
    data object Empty

    @Serializable
    data class Nested(val orgId: String, val nested: SingleField)

    @Serializable
    data class SpecialChars(val key: String, val value: String)

    @Serializable
    data class BoolAndNumbers(val b: Boolean, val i: Int, val f: Float, val d: Double)

    @Serializable
    data class ListField(val items: List<Int>)

    @Serializable
    data class NullField(val orgId: String?, val userId: String?)

    @Test
    fun `encodes single field data class`() {
        val obj = SingleField("123")
        val result = encodeToQueryParams(obj)
        assertEquals("orgId=123", result)
    }

    @Test
    fun `encodes multiple primitive fields`() {
        val obj = MultiField("123", 42, true)
        val result = encodeToQueryParams(obj)
        // Order is not guaranteed, so check all possible orders
        assertContains(result, "orgId=123")
        assertContains(result, "userId=42")
        assertContains(result, "active=true")
    }

    @Test
    fun `throws on nested data class`() {
        val obj = Nested("123", SingleField("456"))
        assertFailsWith<IllegalStateException> {
            encodeToQueryParams(obj)
        }
    }

    @Test
    fun `encodes empty data class as empty string`() {
        val obj = Empty
        val result = encodeToQueryParams(obj)
        assertEquals("", result)
    }

    @Test
    fun `encoding a primitive directly throws`() {
        assertFailsWith<IllegalStateException> {
            encodeToQueryParams("just a string")
        }
    }

    @Test
    fun `encodes special characters`() {
        val obj = SpecialChars("a b&c", "1=2&3%")
        val result = encodeToQueryParams(obj)
        assertContains(result, "key=a b&c")
        assertContains(result, "value=1=2&3%")
    }

    @Test
    fun `encodes booleans and numbers`() {
        val obj = BoolAndNumbers(false, 0, -1.5f, 2.5)
        val result = encodeToQueryParams(obj)
        assertContains(result, "b=false")
        assertContains(result, "i=0")
        assertContains(result, "f=-1.5")
        assertContains(result, "d=2.5")
    }

    @Test
    fun `encodes list field`() {
        val obj = ListField(listOf(1, 2, 3))
        assertFailsWith<IllegalStateException> {
            encodeToQueryParams(obj)
        }
    }

    @Test
    fun `encodes null and empty string values`() {
        val obj = NullField(null, "")
        val result = encodeToQueryParams(obj)
        assertContains(result, "orgId=")
        assertContains(result, "userId=")
    }
}