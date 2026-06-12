package com.cramsan.flyerboard.client.lib.service.impl

import com.cramsan.flyerboard.client.lib.service.AuthService
import com.cramsan.flyerboard.lib.serialization.createJson
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FlyerServiceImplTest {
    private lateinit var authService: AuthService
    private lateinit var capturedRequests: MutableList<HttpRequestData>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authService = mockk()
        every { authService.getAccessToken() } returns null
        capturedRequests = mutableListOf()
    }

    private fun buildService(): FlyerServiceImpl {
        val mockEngine =
            MockEngine { request ->
                capturedRequests.add(request)
                respond(
                    content = """{"flyers":[],"total":0,"offset":0,"limit":20}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val http =
            HttpClient(mockEngine) {
                install(ContentNegotiation) { json(createJson()) }
                defaultRequest { url("http://localhost/") }
            }
        return FlyerServiceImpl(http, authService)
    }

    @Test
    fun `listArchived with query includes q param in URL`() =
        runTest {
            val service = buildService()

            service.listArchived(query = "foo")

            assertTrue(capturedRequests.isNotEmpty())
            val request = capturedRequests.last()
            assertTrue(request.url.encodedPath.contains("archive"), "Expected path to contain 'archive'")
            assertEquals("foo", request.url.parameters["q"])
        }

    @Test
    fun `listArchived without query omits q param from URL`() =
        runTest {
            val service = buildService()

            service.listArchived()

            assertTrue(capturedRequests.isNotEmpty())
            val request = capturedRequests.last()
            assertTrue(request.url.encodedPath.contains("archive"), "Expected path to contain 'archive'")
            assertNull(request.url.parameters["q"], "Expected no q param when query is null")
        }
}
