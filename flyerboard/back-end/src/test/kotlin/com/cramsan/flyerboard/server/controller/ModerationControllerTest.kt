package com.cramsan.flyerboard.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.network.FlyerListNetworkResponse
import com.cramsan.flyerboard.lib.model.network.FlyerNetworkResponse
import com.cramsan.flyerboard.lib.serialization.createJson
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.dependencyinjection.TestFlyerControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.TestFlyerServiceModule
import com.cramsan.flyerboard.server.dependencyinjection.testApplicationModule
import com.cramsan.flyerboard.server.service.ModerationService
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.PaginatedList
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.exceptions.NotFoundException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.serialization.decodeFromString
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Unit tests for [ModerationController].
 */
@OptIn(ExperimentalTime::class)
class ModerationControllerTest :
    CoroutineTest(),
    KoinTest {
    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestFlyerControllerModule,
            TestFlyerServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    private fun makeFlyer(
        id: String = "flyer-1",
        uploaderId: String = "user-1",
        status: FlyerStatus = FlyerStatus.PENDING,
    ) = Flyer(
        id = FlyerId(id),
        title = "Test Flyer",
        description = "Test Description",
        filePath = id,
        status = status,
        expiresAt = null,
        uploaderId = UserId(uploaderId),
        createdAt = Instant.fromEpochSeconds(0),
        updatedAt = Instant.fromEpochSeconds(0),
        fileUrl = null,
        rejectionReason = null,
    )

    private fun makePage(vararg flyers: Flyer) =
        PaginatedList(items = flyers.toList(), total = flyers.size, offset = 0, limit = 20)

    private fun stubUnauthenticated() {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
    }

    private fun stubAuthenticated(userId: UserId, role: UserRole) {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        coEvery { contextRetriever.getContext(any()) } returns
            ClientContext.AuthenticatedClientContext(FlyerBoardContextPayload(userId = userId, role = role))
    }

    // ── GET /api/v1/moderation ────────────────────────────────────────────────

    @Test
    fun `test listPending fails for unauthenticated user`() =
        testBackEndApplication { client ->
            stubUnauthenticated()

            val response = client.get("api/v1/moderation")

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `test listPending fails with 403 for non-admin role`() =
        testBackEndApplication { client ->
            stubAuthenticated(UserId("user-1"), UserRole.USER)

            val response = client.get("api/v1/moderation")

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }

    @Test
    fun `test listPending returns pending flyers for admin role`() =
        testBackEndApplication { client ->
            stubAuthenticated(UserId("admin-1"), UserRole.ADMIN)
            val moderationService = get<ModerationService>()
            val pending = makeFlyer(id = "pending-1", status = FlyerStatus.PENDING)
            coEvery {
                moderationService.listPendingFlyers(offset = 0, limit = 20)
            } returns Result.success(makePage(pending))

            val response = client.get("api/v1/moderation")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerId("pending-1"), body.flyers.single().id)
        }

    @Test
    fun `test listPending returns empty list when nothing pending`() =
        testBackEndApplication { client ->
            stubAuthenticated(UserId("admin-1"), UserRole.ADMIN)
            val moderationService = get<ModerationService>()
            coEvery {
                moderationService.listPendingFlyers(offset = 0, limit = 20)
            } returns Result.success(makePage())

            val response = client.get("api/v1/moderation")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(0, body.total)
            assertEquals(emptyList<FlyerNetworkResponse>(), body.flyers)
        }

    @Test
    fun `test listPending forwards pagination params to service`() =
        testBackEndApplication { client ->
            stubAuthenticated(UserId("admin-1"), UserRole.ADMIN)
            val moderationService = get<ModerationService>()
            coEvery {
                moderationService.listPendingFlyers(offset = 10, limit = 5)
            } returns Result.success(makePage())

            val response = client.get("api/v1/moderation?offset=10&limit=5")

            assertEquals(HttpStatusCode.OK, response.status)
            coVerify { moderationService.listPendingFlyers(offset = 10, limit = 5) }
        }

    // ── POST /api/v1/moderation/{id} ──────────────────────────────────────────

    @Test
    fun `test moderate fails for unauthenticated user`() =
        testBackEndApplication { client ->
            stubUnauthenticated()

            val response =
                client.post("api/v1/moderation/flyer-1") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"action":"approve","reason":null}""")
                }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `test moderate fails with 403 for non-admin role`() =
        testBackEndApplication { client ->
            stubAuthenticated(UserId("user-1"), UserRole.USER)

            val response =
                client.post("api/v1/moderation/flyer-1") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"action":"approve","reason":null}""")
                }

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }

    @Test
    fun `test moderate approve with admin role returns approved flyer`() =
        testBackEndApplication { client ->
            val adminId = UserId("admin-1")
            stubAuthenticated(adminId, UserRole.ADMIN)
            val moderationService = get<ModerationService>()
            val approved = makeFlyer(id = "flyer-1", status = FlyerStatus.APPROVED)
            coEvery {
                moderationService.approveFlyer(FlyerId("flyer-1"), adminId)
            } returns Result.success(approved)

            val response =
                client.post("api/v1/moderation/flyer-1") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"action":"approve","reason":null}""")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerStatus.APPROVED, body.status)
            coVerify { moderationService.approveFlyer(FlyerId("flyer-1"), adminId) }
        }

    @Test
    fun `test moderate reject with admin role returns rejected flyer`() =
        testBackEndApplication { client ->
            val adminId = UserId("admin-1")
            stubAuthenticated(adminId, UserRole.ADMIN)
            val moderationService = get<ModerationService>()
            val rejected = makeFlyer(id = "flyer-1", status = FlyerStatus.REJECTED)
            coEvery {
                moderationService.rejectFlyer(FlyerId("flyer-1"), adminId, "Inappropriate content")
            } returns Result.success(rejected)

            val response =
                client.post("api/v1/moderation/flyer-1") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"action":"reject","reason":"Inappropriate content"}""")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerStatus.REJECTED, body.status)
            coVerify { moderationService.rejectFlyer(FlyerId("flyer-1"), adminId, "Inappropriate content") }
        }

    @Test
    fun `test moderate with invalid action returns 400`() =
        testBackEndApplication { client ->
            stubAuthenticated(UserId("admin-1"), UserRole.ADMIN)

            val response =
                client.post("api/v1/moderation/flyer-1") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"action":"delete","reason":null}""")
                }

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun `test moderate when flyer not found returns 404`() =
        testBackEndApplication { client ->
            val adminId = UserId("admin-1")
            stubAuthenticated(adminId, UserRole.ADMIN)
            val moderationService = get<ModerationService>()
            coEvery {
                moderationService.approveFlyer(FlyerId("missing"), adminId)
            } returns Result.failure(NotFoundException("Flyer not found: missing"))

            val response =
                client.post("api/v1/moderation/missing") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"action":"approve","reason":null}""")
                }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
}
