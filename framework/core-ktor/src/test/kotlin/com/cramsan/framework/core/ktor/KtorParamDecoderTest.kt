package com.cramsan.framework.core.ktor

import io.ktor.http.Parameters
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSerializationApi::class)
class KtorParamDecoderTest {
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
        val params = Parameters.build { append("orgId", "123") }
        val obj = decodeFromQueryParams<SingleField>(params)
        assertEquals(SingleField("123"), obj)
    }

    @Test
    fun `decodes multiple primitive fields`() {
        val params = Parameters.build {
            append("orgId", "123")
            append("userId", "42")
            append("active", "true")
        }
        val obj = decodeFromQueryParams<MultiField>(params)
        assertEquals(MultiField("123", 42, true), obj)
    }

    @Test
    fun `throws on invalid query`() {
        val params = Parameters.build { append("orgId", "") } // missing userId and active
        assertFailsWith<MissingFieldException> {
            decodeFromQueryParams<MultiField>(params)
        }
    }

    @Test
    fun `throws on missing field`() {
        val params = Parameters.build { append("orgId", "123") }
        assertFailsWith<MissingFieldException> {
            decodeFromQueryParams<MultiField>(params)
        }
    }

    @Test
    fun `decodes empty data class as object`() {
        val params = Parameters.Empty
        val obj = decodeFromQueryParams<Empty>(params)
        assertEquals(Empty, obj)
    }

    @Test
    fun `decoding special characters throws`() {
        val params = Parameters.build {
            append("key", "a b&c")
            append("value", "1=2&3%")
        }
        val obj = decodeFromQueryParams<SpecialChars>(params)
        assertEquals(SpecialChars("a b&c", "1=2&3%"), obj)
    }

    @Test
    fun `decodes booleans and numbers`() {
        val params = Parameters.build {
            append("b", "false")
            append("i", "0")
            append("f", "-1.5")
            append("d", "2.5")
        }
        val obj = decodeFromQueryParams<BoolAndNumbers>(params)
        assertEquals(BoolAndNumbers(false, 0, -1.5f, 2.5), obj)
    }

    @Test
    fun `decodes list field`() {
        val params = Parameters.build { append("items", "1,2,3") }
        val obj = decodeFromQueryParams<ListField>(params)
        assertEquals(ListField(listOf(1, 2, 3)), obj)
    }

    @Test
    fun `decodes null and empty string values`() {
        val params = Parameters.build {
            append("orgId", "")
            append("userId", "")
        }
        val obj = decodeFromQueryParams<NullField>(params)
        assertEquals(NullField("", ""), obj)
    }

    @Test
    fun `ignores extra parameters`() {
        val params = Parameters.build {
            append("orgId", "abc")
            append("userId", "123")
            append("extra", "zzz")
        }
        val obj = decodeFromQueryParams<SingleField>(params)
        assertEquals(SingleField("abc"), obj)
    }

    @Test
    fun `order independence`() {
        val params = Parameters.build {
            append("userId", "42")
            append("active", "true")
            append("orgId", "123")
        }
        val obj = decodeFromQueryParams<MultiField>(params)
        assertEquals(MultiField("123", 42, true), obj)
    }

    @Test
    fun `malformed query string throws`() {
        val params = Parameters.build {
            append("orgId123", "")
            append("userId", "42")
        }
        assertFailsWith<MissingFieldException> {
            decodeFromQueryParams<MultiField>(params)
        }
    }
}
