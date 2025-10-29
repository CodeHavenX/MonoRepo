package com.cramsan.framework.networkapi

import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.http.HttpMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OperationTest {

    @Test
    fun buildRequestWithValidParameters() {
        val operation = Operation(
            method = HttpMethod.Post,
            apiPath = "/api",
            path = "/resource",
            requestBodyType = SampleRequestBody::class,
            queryParamType = SampleQueryParam::class,
            pathParamType = SamplePathParam::class,
            responseBodyType = SampleResponseBody::class
        )

        val request = operation.buildRequest(
            argument = SamplePathParam("123"),
            body = SampleRequestBody("data"),
            queryParam = SampleQueryParam("query")
        )

        assertEquals("api/resource/SamplePathParam(id=123)", request.fullPath)
        assertEquals(HttpMethod.Post, request.method)
    }

    @Test
    fun buildRequestWithNoRequestBody() {
        val operation = Operation(
            method = HttpMethod.Get,
            apiPath = "/api",
            path = "/resource",
            requestBodyType = NoRequestBody::class,
            queryParamType = SampleQueryParam::class,
            pathParamType = SamplePathParam::class,
            responseBodyType = SampleResponseBody::class
        )

        val request = operation.buildRequest(
            argument = SamplePathParam("123"),
            queryParam = SampleQueryParam("query")
        )

        assertEquals("api/resource/SamplePathParam(id=123)", request.fullPath)
        assertEquals(HttpMethod.Get, request.method)
    }

    @Test
    fun buildRequestFailsForGetWithRequestBody() {
        val exception = assertFailsWith<IllegalStateException> {
            Operation(
                method = HttpMethod.Get,
                apiPath = "/api",
                path = "/resource",
                requestBodyType = SampleRequestBody::class,
                queryParamType = SampleQueryParam::class,
                pathParamType = SamplePathParam::class,
                responseBodyType = SampleResponseBody::class
            )
        }

        assertEquals("GET operations cannot have a request body", exception.message)
    }

    @Test
    fun toOperationHandlerGeneratesCorrectFullPath() {
        val operation = Operation(
            method = HttpMethod.Put,
            apiPath = "/api",
            path = "/resource",
            requestBodyType = SampleRequestBody::class,
            queryParamType = SampleQueryParam::class,
            pathParamType = SamplePathParam::class,
            responseBodyType = SampleResponseBody::class
        )

        val handler = operation.toOperationHandler()

        assertEquals("resource/{param}", handler.fullPath)
        assertEquals("param", handler.param)
    }

    @Test
    fun operationRequestFullPathHandlesNoPathParam() {
        val request = OperationRequest(
            method = HttpMethod.Delete,
            apiPath = "/api",
            path = "/resource",
            param = NoPathParam,
            body = NoRequestBody,
            queryParam = NoQueryParam,
            responseBodyType = NoResponseBody::class
        )

        assertEquals("api/resource", request.fullPath)
    }

    @Test
    fun operationRequestSegmentTests() {
        val request = OperationRequest(
            method = HttpMethod.Delete,
            apiPath = "/api",
            path = "/resource",
            param = NoPathParam,
            body = NoRequestBody,
            queryParam = NoQueryParam,
            responseBodyType = NoResponseBody::class
        )
        val requestNoSlashApiPath = request.copy(
            apiPath = "api"
        )
        val requestNoSlashPath = request.copy(
            path = "resource",
        )
        val requestNoSlash = request.copy(
            path = "resource",
            apiPath = "api"
        )

        assertEquals("api/resource", request.fullPath)
        assertEquals("api/resource", requestNoSlashPath.fullPath)
        assertEquals("api/resource", requestNoSlashApiPath.fullPath)
        assertEquals("api/resource", requestNoSlash.fullPath)
    }
}

data class SampleRequestBody(val data: String) : RequestBody
data class SampleQueryParam(val query: String) : QueryParam
data class SamplePathParam(val id: String) : PathParam
data class SampleResponseBody(val result: String) : ResponseBody
