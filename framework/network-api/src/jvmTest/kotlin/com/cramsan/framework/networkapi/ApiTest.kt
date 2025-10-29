package com.cramsan.framework.networkapi

import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.http.HttpMethod
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ApiTest {

    class SampleRequest : RequestBody
    class SampleQuery : QueryParam
    class SamplePath : PathParam
    class SampleResponse : ResponseBody

    @Test
    fun `operation registers and exposes expected properties`() {
        val api = Api("/base")

        val op = api.operation<SampleRequest, SampleQuery, SamplePath, SampleResponse>(
            HttpMethod.Post,
            "/sub"
        )

        // reflectively inspect Operation properties (names expected from Api usage)
        val opClass = op::class.java

        val methodField = opClass.getDeclaredField("method").apply { isAccessible = true }
        val basePathField = opClass.getDeclaredField("apiPath").apply { isAccessible = true }
        val pathField = opClass.getDeclaredField("path").apply { isAccessible = true }
        val requestTypeField = opClass.getDeclaredField("requestBodyType").apply { isAccessible = true }
        val queryTypeField = opClass.getDeclaredField("queryParamType").apply { isAccessible = true }
        val pathTypeField = opClass.getDeclaredField("pathParamType").apply { isAccessible = true }
        val responseTypeField = opClass.getDeclaredField("responseBodyType").apply { isAccessible = true }

        assertEquals(HttpMethod.Post, methodField.get(op))
        assertEquals("/base", basePathField.get(op))
        assertEquals("/sub", pathField.get(op))
        assertEquals(SampleRequest::class, requestTypeField.get(op))
        assertEquals(SampleQuery::class, queryTypeField.get(op))
        assertEquals(SamplePath::class, pathTypeField.get(op))
        assertEquals(SampleResponse::class, responseTypeField.get(op))

        // verify internal operations list contains the created operation
        val opsField = Api::class.java.getDeclaredField("operations").apply { isAccessible = true }
        val ops = opsField.get(api) as MutableList<*>
        assertTrue(ops.contains(op))
        assertEquals(1, ops.size)
    }

    @Test
    fun `multiple operations register and preserve order`() {
        val api = Api("/base")

        val op1 = api.operation<NoRequestBody, SampleQuery, SamplePath, SampleResponse>(
            HttpMethod.Get,
            "/one"
        )
        val op2 = api.operation<SampleRequest, SampleQuery, SamplePath, SampleResponse>(
            HttpMethod.Post,
            "/two"
        )

        val opsField = Api::class.java.getDeclaredField("operations").apply { isAccessible = true }
        val ops = opsField.get(api) as MutableList<*>

        assertEquals(2, ops.size)
        assertSame(op1, ops[0])
        assertSame(op2, ops[1])
    }
}