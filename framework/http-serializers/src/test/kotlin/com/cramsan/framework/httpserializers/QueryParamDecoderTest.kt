package com.cramsan.framework.httpserializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSerializationApi::class)
class QueryParamDecoderTest {
    @Serializable
    data class SingleField(val orgId: String)

    @Serializable
    data class MultiField(val orgId: String, val userId: Int, val active: Boolean)

    @Serializable
    data object Empty

    @Serializable
    data class SpecialChars(val key: String, val value: String)

    @Serializable
    data class BoolAndNumbers(val b: Boolean, val i: Int, val f: Float, val d: Double)

    @Serializable
    data class ListField(val items: List<Int>)

    @Serializable
    data class NullField(val orgId: String?, val userId: String?)

    @Test
    fun `decodes single field data class`() {
        val query = "orgId=123"
        val obj = decodeFromQueryParams<SingleField>(query)
        assertEquals(SingleField("123"), obj)
    }

    @Test
    fun `decodes multiple primitive fields`() {
        val query = "orgId=123&userId=42&active=true"
        val obj = decodeFromQueryParams<MultiField>(query)
        assertEquals(MultiField("123", 42, true), obj)
    }

    @Test
    fun `throws on invalid query`() {
        val query = "orgId"
        assertFailsWith<IllegalStateException> {
            decodeFromQueryParams<MultiField>(query)
        }
    }

    @Test
    fun `throws on missing field`() {
        val query = "orgId=123"
        assertFailsWith<MissingFieldException> {
            decodeFromQueryParams<MultiField>(query)
        }
    }

    @Test
    fun `decodes empty data class as object`() {
        val query = ""
        val obj = decodeFromQueryParams<Empty>(query)
        assertEquals(Empty, obj)
    }

    @Test
    fun `decoding special characters throws`() {
        val query = "key=a b&c&value=1=2&3%"
        assertFailsWith<IllegalStateException> {
            decodeFromQueryParams<SpecialChars>(query)
        }
    }

    @Test
    fun `decodes booleans and numbers`() {
        val query = "b=false&i=0&f=-1.5&d=2.5"
        val obj = decodeFromQueryParams<BoolAndNumbers>(query)
        assertEquals(BoolAndNumbers(false, 0, -1.5f, 2.5), obj)
    }

    @Test
    fun `decodes list field`() {
        val query = "items=1,2,3"
        val obj = decodeFromQueryParams<ListField>(query)
        assertEquals(ListField(listOf(1,2,3)), obj)
    }

    @Test
    fun `decodes null and empty string values`() {
        val query = "orgId=&userId="
        val obj = decodeFromQueryParams<NullField>(query)
        assertEquals(NullField("", ""), obj)
    }

    @Test
    fun `ignores extra parameters`() {
        val query = "orgId=abc&userId=123&extra=zzz"
        val obj = decodeFromQueryParams<SingleField>(query)
        assertEquals(SingleField("abc"), obj)
    }

    @Test
    fun `order independence`() {
        val query = "userId=42&active=true&orgId=123"
        val obj = decodeFromQueryParams<MultiField>(query)
        assertEquals(MultiField("123", 42, true), obj)
    }

    @Test
    fun `malformed query string throws`() {
        val query = "orgId123&userId=42"
        assertFailsWith<IllegalStateException> {
            decodeFromQueryParams<MultiField>(query)
        }
    }
}

