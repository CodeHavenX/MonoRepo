package com.cramsan.architecture.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.retry
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for [configureStandardRetry].
 */
class RetryPolicyTest {

    @Test
    fun `GET request retries on 5xx responses until it succeeds`() = runTest {
        var attempts = 0
        val engine = MockEngine {
            attempts++
            if (attempts < 3) {
                respond(content = "", status = HttpStatusCode.InternalServerError)
            } else {
                respond(content = "ok", status = HttpStatusCode.OK)
            }
        }
        val http = HttpClient(engine) {
            install(HttpRequestRetry) { configureStandardRetry() }
        }

        val response = http.get("/foo")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(3, attempts)
    }

    @Test
    fun `GET request stops after maxRetries and surfaces the last failure`() = runTest {
        var attempts = 0
        val engine = MockEngine {
            attempts++
            respond(content = "", status = HttpStatusCode.InternalServerError)
        }
        val http = HttpClient(engine) {
            install(HttpRequestRetry) { configureStandardRetry(maxRetries = 2) }
        }

        val response = http.get("/foo")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals(3, attempts) // Initial attempt + 2 retries.
    }

    @Test
    fun `GET request retries on network exceptions`() = runTest {
        var attempts = 0
        val engine = MockEngine {
            attempts++
            if (attempts < 2) {
                throw IOException("Simulated network failure")
            }
            respond(content = "ok", status = HttpStatusCode.OK)
        }
        val http = HttpClient(engine) {
            install(HttpRequestRetry) { configureStandardRetry() }
        }

        val response = http.get("/foo")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, attempts)
    }

    @Test
    fun `POST request is not retried by default on 5xx responses`() = runTest {
        var attempts = 0
        val engine = MockEngine {
            attempts++
            respond(content = "", status = HttpStatusCode.InternalServerError)
        }
        val http = HttpClient(engine) {
            install(HttpRequestRetry) { configureStandardRetry() }
        }

        val response = http.post("/foo")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals(1, attempts)
    }

    @Test
    fun `POST request does not retry on network exceptions by default`() = runTest {
        var attempts = 0
        val engine = MockEngine {
            attempts++
            throw IOException("Simulated network failure")
        }
        val http = HttpClient(engine) {
            install(HttpRequestRetry) { configureStandardRetry() }
        }

        assertFailsWith<IOException> {
            http.post("/foo")
        }
        assertEquals(1, attempts)
    }

    @Test
    fun `POST request retries when opted in per-request`() = runTest {
        var attempts = 0
        val engine = MockEngine {
            attempts++
            if (attempts < 2) {
                respond(content = "", status = HttpStatusCode.InternalServerError)
            } else {
                respond(content = "ok", status = HttpStatusCode.OK)
            }
        }
        val http = HttpClient(engine) {
            install(HttpRequestRetry) { configureStandardRetry() }
        }

        val response = http.post("/foo") {
            retry { configureStandardRetry(retryableMethods = setOf(HttpMethod.Post)) }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, attempts)
    }
}
