package com.cramsan.flyerboard.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.dependencyinjection.IntegTestFlyerControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.IntegTestFlyerServiceModule
import com.cramsan.flyerboard.server.dependencyinjection.integTestFlyerApplicationModule
import com.cramsan.flyerboard.server.service.FlyerService
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.PaginatedList
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Integration tests for [FlyerController].
 *
 * Tests the full HTTP request/response cycle for flyer operations using Ktor's test engine and
 * mocked service layer.
 */
@OptIn(ExperimentalTime::class)
class FlyerApiIntegrationTest : KoinTest {

    @BeforeTest
    fun setUp() {
        startTestKoin(
            integTestFlyerApplicationModule(),
            IntegTestFlyerControllerModule,
            IntegTestFlyerServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun makeFlyer(
        id: String = "flyer-1",
        uploaderId: String = "user-1",
        status: FlyerStatus = FlyerStatus.APPROVED,
    ) = Flyer(
        id = FlyerId(id),
        title = "Test Flyer",
        description = "Test Description",
        filePath = "uploads/file.png",
        status = status,
        expiresAt = null,
        uploaderId = UserId(uploaderId),
        createdAt = Instant.fromEpochSeconds(0),
        updatedAt = Instant.fromEpochSeconds(0),
        fileUrl = "https://signed.example.com/file.png",
    )

    private fun makeEmptyPage() = PaginatedList(
        items = emptyList<Flyer>(),
        total = 0,
        offset = 0,
        limit = 20,
    )

    private fun makeSingleItemPage(flyer: Flyer) = PaginatedList(
        items = listOf(flyer),
        total = 1,
        offset = 0,
        limit = 20,
    )

    // ── GET /api/v1/flyers ────────────────────────────────────────────────────

    @Test
    fun `GET api-v1-flyers returns 200 with empty list`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
        coEvery { flyerService.listFlyers(any(), any(), any(), any()) } returns Result.success(makeEmptyPage())

        val response = client.get("api/v1/flyers")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"flyers\""), "Expected 'flyers' field in response: $body")
        assertTrue(body.contains("\"total\": 0"), "Expected total=0 in response: $body")
    }

    @Test
    fun `GET api-v1-flyers returns 200 with flyer list`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val flyer = makeFlyer()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
        coEvery { flyerService.listFlyers(any(), any(), any(), any()) } returns Result.success(makeSingleItemPage(flyer))

        val response = client.get("api/v1/flyers")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("flyer-1"), "Expected flyer ID in response: $body")
        assertTrue(body.contains("Test Flyer"), "Expected flyer title in response: $body")
    }

    @Test
    fun `GET api-v1-flyers with pagination params returns correct limit`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
        coEvery { flyerService.listFlyers(any(), any(), 0, 5) } returns Result.success(
            PaginatedList(items = emptyList(), total = 0, offset = 0, limit = 5)
        )

        val response = client.get("api/v1/flyers?offset=0&limit=5")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"limit\": 5"), "Expected limit=5 in response: $body")
    }

    @Test
    fun `GET api-v1-flyers with search query returns matching flyers`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val matchingFlyer = makeFlyer(id = "flyer-match")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
        coEvery { flyerService.listFlyers(null, "concert", any(), any()) } returns Result.success(
            makeSingleItemPage(matchingFlyer)
        )

        val response = client.get("api/v1/flyers?q=concert")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("flyer-match"), "Expected matching flyer in response: $body")
    }

    // ── GET /api/v1/flyers/{id} ───────────────────────────────────────────────

    @Test
    fun `GET api-v1-flyers-id returns 200 with flyer`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val flyer = makeFlyer(id = "flyer-1")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
        coEvery { flyerService.getFlyer(FlyerId("flyer-1")) } returns Result.success(flyer)

        val response = client.get("api/v1/flyers/flyer-1")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("flyer-1"), "Expected flyer ID in response: $body")
    }

    @Test
    fun `GET api-v1-flyers-id returns 404 when flyer not found`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
        coEvery { flyerService.getFlyer(any()) } returns Result.failure(
            ClientRequestExceptions.NotFoundException("Flyer not found: nonexistent")
        )

        val response = client.get("api/v1/flyers/nonexistent")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    // ── GET /api/v1/flyers/archive ────────────────────────────────────────────

    @Test
    fun `GET api-v1-flyers-archive returns 200 with archived flyers`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val archivedFlyer = makeFlyer(id = "archived-1", status = FlyerStatus.ARCHIVED)

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
        coEvery {
            flyerService.listFlyers(FlyerStatus.ARCHIVED, null, any(), any())
        } returns Result.success(makeSingleItemPage(archivedFlyer))

        val response = client.get("api/v1/flyers/archive")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("archived-1"), "Expected archived flyer in response: $body")
    }

    // ── GET /api/v1/flyers/mine ───────────────────────────────────────────────

    @Test
    fun `GET api-v1-flyers-mine without auth returns 401`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()

        val response = client.get("api/v1/flyers/mine")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `GET api-v1-flyers-mine with auth returns 200`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val userId = UserId("user-1")
        val userFlyer = makeFlyer(id = "my-flyer", uploaderId = "user-1")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = userId, role = UserRole.USER)
        )
        coEvery {
            flyerService.listFlyersByUploader(userId, any(), any())
        } returns Result.success(makeSingleItemPage(userFlyer))

        val response = client.get("api/v1/flyers/mine") {
            header(HttpHeaders.Authorization, "Bearer test-token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("my-flyer"), "Expected user's flyer in response: $body")
    }

    // ── POST /api/v1/flyers ───────────────────────────────────────────────────

    @Test
    fun `POST api-v1-flyers without auth returns 401`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()

        val response = client.submitFormWithBinaryData(
            url = "api/v1/flyers",
            formData = formData {
                append("title", "Test Flyer")
                append("description", "Test Description")
            },
        )

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST api-v1-flyers with invalid MIME type returns 400`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val userId = UserId("user-1")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = userId, role = UserRole.USER)
        )
        coEvery {
            flyerService.createFlyer(any(), any(), any(), any(), any(), any(), any())
        } returns Result.failure(
            ClientRequestExceptions.InvalidRequestException("Unsupported file type: application/exe")
        )

        val response = client.submitFormWithBinaryData(
            url = "api/v1/flyers",
            formData = formData {
                append("title", "Test Flyer")
                append("description", "Test Description")
                append(
                    "file",
                    ByteArray(100),
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/exe")
                        append(HttpHeaders.ContentDisposition, "filename=\"malware.exe\"")
                    },
                )
            },
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST api-v1-flyers with valid data returns 200`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val userId = UserId("user-1")
        val createdFlyer = makeFlyer(id = "new-flyer", uploaderId = "user-1", status = FlyerStatus.PENDING)

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = userId, role = UserRole.USER)
        )
        coEvery {
            flyerService.createFlyer(any(), any(), any(), any(), any(), any(), any())
        } returns Result.success(createdFlyer)

        val response = client.submitFormWithBinaryData(
            url = "api/v1/flyers",
            formData = formData {
                append("title", "Test Flyer")
                append("description", "Test Description")
                append(
                    "file",
                    ByteArray(100),
                    Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(HttpHeaders.ContentDisposition, "filename=\"flyer.png\"")
                    },
                )
            },
        )

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("new-flyer"), "Expected new flyer ID in response: $body")
        assertTrue(body.contains("\"pending\""), "Expected pending status in response: $body")
    }

    // ── PUT /api/v1/flyers/{id} ───────────────────────────────────────────────

    @Test
    fun `PUT api-v1-flyers-id without auth returns 401`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()

        val response = client.submitFormWithBinaryData(
            url = "api/v1/flyers/flyer-1",
            formData = formData {
                append("title", "Updated Title")
            },
        ) {
            method = io.ktor.http.HttpMethod.Put
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `PUT api-v1-flyers-id with valid data returns 200`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val requesterId = UserId("user-1")
        val updatedFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1", status = FlyerStatus.PENDING)

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = requesterId, role = UserRole.USER)
        )
        coEvery {
            flyerService.updateFlyer(any(), any(), any(), any(), any(), any(), any(), any())
        } returns Result.success(updatedFlyer)

        val response = client.submitFormWithBinaryData(
            url = "api/v1/flyers/flyer-1",
            formData = formData {
                append("title", "Updated Title")
            },
        ) {
            method = io.ktor.http.HttpMethod.Put
            header(HttpHeaders.Authorization, "Bearer test-token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("flyer-1"), "Expected flyer ID in response: $body")
        assertTrue(body.contains("\"pending\""), "Expected pending status after update in response: $body")
    }

    @Test
    fun `PUT api-v1-flyers-id by non-owner returns 403`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val flyerService = get<FlyerService>()
        val requesterId = UserId("user-2")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = requesterId, role = UserRole.USER)
        )
        coEvery {
            flyerService.updateFlyer(any(), any(), any(), any(), any(), any(), any(), any())
        } returns Result.failure(
            ClientRequestExceptions.ForbiddenException("User user-2 does not own flyer flyer-1")
        )

        val response = client.submitFormWithBinaryData(
            url = "api/v1/flyers/flyer-1",
            formData = formData {
                append("title", "Hijacked Title")
            },
        ) {
            method = io.ktor.http.HttpMethod.Put
            header(HttpHeaders.Authorization, "Bearer test-token")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
