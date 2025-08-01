package com.cramsan.edifikana.client.lib.service.impl

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

class KtorTestEngine {

    val engine: HttpClientEngine
    private lateinit var responseEmitter: MockResponseProducer

    init {
        engine = MockEngine { request ->
            val response = responseEmitter.produceResponse(request)
            respond(
                content = response.content,
                status = response.status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
    }

    fun configure(block: suspend MockResponseProducer.() -> Unit) {
        responseEmitter = mockk()

        runBlocking {
            responseEmitter.block()
        }

    }

    interface MockResponseProducer {
        fun produceResponse(request: HttpRequestData): MockResponseData
    }
}

sealed class MockResponseData(
    val content: String,
    val status: HttpStatusCode,
) {
    class Success(content: String) : MockResponseData(content, HttpStatusCode.OK)

    class Error(content: String, status: HttpStatusCode) : MockResponseData(content, status)

    class NotFound(content: String) : MockResponseData(content, HttpStatusCode.NotFound)

    class Unauthorized(content: String) : MockResponseData(content, HttpStatusCode.Unauthorized)
}