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
import com.cramsan.flyerboard.server.service.ModerationService
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.PaginatedList
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
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
 * Integration tests for [ModerationController].
 *
 * Tests the full HTTP request/response cycle for moderation operations using Ktor's test engine
 * and mocked service layer.
 */
@OptIn(ExperimentalTime::class)
class ModerationApiIntegrationTest : KoinTest {

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
        status: FlyerStatus = FlyerStatus.PENDING,
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

    private fun makePendingPage(vararg flyers: Flyer) = PaginatedList(
        items = flyers.toList(),
        total = flyers.size,
        offset = 0,
        limit = 20,
    )

    // ── GET /api/v1/moderation ────────────────────────────────────────────────

    @Test
    fun `GET api-v1-moderation without auth returns 401`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()

        val response = client.get("api/v1/moderation")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `GET api-v1-moderation with non-admin role returns 403`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val userId = UserId("user-1")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = userId, role = UserRole.USER)
        )

        val response = client.get("api/v1/moderation") {
            header(HttpHeaders.Authorization, "Bearer user-token")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `GET api-v1-moderation with admin role returns 200 with pending flyers`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val moderationService = get<ModerationService>()
        val adminId = UserId("admin-1")
        val pendingFlyer = makeFlyer(id = "pending-flyer", status = FlyerStatus.PENDING)

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = adminId, role = UserRole.ADMIN)
        )
        coEvery {
            moderationService.listPendingFlyers(any(), any())
        } returns Result.success(makePendingPage(pendingFlyer))

        val response = client.get("api/v1/moderation") {
            header(HttpHeaders.Authorization, "Bearer admin-token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("pending-flyer"), "Expected pending flyer ID in response: $body")
    }

    @Test
    fun `GET api-v1-moderation with admin role and no pending flyers returns empty list`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val moderationService = get<ModerationService>()
        val adminId = UserId("admin-1")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = adminId, role = UserRole.ADMIN)
        )
        coEvery {
            moderationService.listPendingFlyers(any(), any())
        } returns Result.success(makePendingPage())

        val response = client.get("api/v1/moderation") {
            header(HttpHeaders.Authorization, "Bearer admin-token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"total\": 0"), "Expected total=0 in response: $body")
    }

    // ── POST /api/v1/moderation/{id} ──────────────────────────────────────────

    @Test
    fun `POST api-v1-moderation-id without auth returns 401`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()

        val response = client.post("api/v1/moderation/flyer-1") {
            contentType(ContentType.Application.Json)
            setBody("""{"action":"approve"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST api-v1-moderation-id with non-admin role returns 403`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val userId = UserId("user-1")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = userId, role = UserRole.USER)
        )

        val response = client.post("api/v1/moderation/flyer-1") {
            contentType(ContentType.Application.Json)
            setBody("""{"action":"approve"}""")
            header(HttpHeaders.Authorization, "Bearer user-token")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `POST api-v1-moderation-id approve with admin returns 200 with approved flyer`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val moderationService = get<ModerationService>()
        val adminId = UserId("admin-1")
        val approvedFlyer = makeFlyer(id = "flyer-1", status = FlyerStatus.APPROVED)

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = adminId, role = UserRole.ADMIN)
        )
        coEvery {
            moderationService.approveFlyer(FlyerId("flyer-1"), adminId)
        } returns Result.success(approvedFlyer)

        val response = client.post("api/v1/moderation/flyer-1") {
            contentType(ContentType.Application.Json)
            setBody("""{"action":"approve"}""")
            header(HttpHeaders.Authorization, "Bearer admin-token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"approved\""), "Expected approved status in response: $body")
    }

    @Test
    fun `POST api-v1-moderation-id reject with admin returns 200 with rejected flyer`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val moderationService = get<ModerationService>()
        val adminId = UserId("admin-1")
        val rejectedFlyer = makeFlyer(id = "flyer-1", status = FlyerStatus.REJECTED)

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = adminId, role = UserRole.ADMIN)
        )
        coEvery {
            moderationService.rejectFlyer(FlyerId("flyer-1"), adminId)
        } returns Result.success(rejectedFlyer)

        val response = client.post("api/v1/moderation/flyer-1") {
            contentType(ContentType.Application.Json)
            setBody("""{"action":"reject"}""")
            header(HttpHeaders.Authorization, "Bearer admin-token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"rejected\""), "Expected rejected status in response: $body")
    }

    @Test
    fun `POST api-v1-moderation-id with invalid action returns 400`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val adminId = UserId("admin-1")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = adminId, role = UserRole.ADMIN)
        )

        val response = client.post("api/v1/moderation/flyer-1") {
            contentType(ContentType.Application.Json)
            setBody("""{"action":"delete"}""")
            header(HttpHeaders.Authorization, "Bearer admin-token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST api-v1-moderation-id when flyer not found returns 404`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        val moderationService = get<ModerationService>()
        val adminId = UserId("admin-1")

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = adminId, role = UserRole.ADMIN)
        )
        coEvery {
            moderationService.approveFlyer(FlyerId("nonexistent"), adminId)
        } returns Result.failure(
            ClientRequestExceptions.NotFoundException("Flyer not found: nonexistent")
        )

        val response = client.post("api/v1/moderation/nonexistent") {
            contentType(ContentType.Application.Json)
            setBody("""{"action":"approve"}""")
            header(HttpHeaders.Authorization, "Bearer admin-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
