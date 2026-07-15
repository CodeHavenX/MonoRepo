package com.cramsan.flyerboard.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.network.FlyerListNetworkResponse
import com.cramsan.flyerboard.lib.model.network.FlyerNetworkResponse
import com.cramsan.flyerboard.lib.model.network.UpdateFlyerNetworkResponse
import com.cramsan.flyerboard.lib.serialization.createJson
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.datastore.SignedUpload
import com.cramsan.flyerboard.server.dependencyinjection.TestFlyerControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.TestFlyerServiceModule
import com.cramsan.flyerboard.server.dependencyinjection.testApplicationModule
import com.cramsan.flyerboard.server.service.FlyerService
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.PaginatedList
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.exceptions.ForbiddenException
import com.cramsan.framework.utils.exceptions.NotFoundException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
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
import kotlin.test.assertNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class FlyerControllerTest :
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

    private fun makePage(vararg flyers: Flyer, offset: Int = 0, limit: Int = 20) =
        PaginatedList(items = flyers.toList(), total = flyers.size, offset = offset, limit = limit)

    private val signedUpload = SignedUpload(signedUrl = "https://signed.example.com/upload", token = "token")

    private fun stubUnauthenticated() {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()
    }

    private fun stubAuthenticated(userId: UserId, role: UserRole = UserRole.USER) {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        coEvery { contextRetriever.getContext(any()) } returns
            ClientContext.AuthenticatedClientContext(FlyerBoardContextPayload(userId = userId, role = role))
    }

    // ── GET /api/v1/flyers ────────────────────────────────────────────────────

    @Test
    fun `test listFlyers returns empty list`() =
        testBackEndApplication { client ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            coEvery {
                flyerService.listFlyers(status = null, query = null, offset = 0, limit = 20)
            } returns Result.success(makePage())

            val response = client.get("api/v1/flyers")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(0, body.total)
            assertEquals(emptyList<FlyerNetworkResponse>(), body.flyers)
        }

    @Test
    fun `test listFlyers returns flyers from service`() =
        testBackEndApplication { client ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            val flyer = makeFlyer(id = "flyer-1")
            coEvery {
                flyerService.listFlyers(status = null, query = null, offset = 0, limit = 20)
            } returns Result.success(makePage(flyer))

            val response = client.get("api/v1/flyers")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(1, body.total)
            assertEquals(FlyerId("flyer-1"), body.flyers.single().id)
        }

    @Test
    fun `test listFlyers forwards pagination params to service`() =
        testBackEndApplication { client ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            coEvery {
                flyerService.listFlyers(status = null, query = null, offset = 10, limit = 5)
            } returns Result.success(makePage(offset = 10, limit = 5))

            val response = client.get("api/v1/flyers?offset=10&limit=5")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(10, body.offset)
            assertEquals(5, body.limit)
        }

    @Test
    fun `test listFlyers forwards search query to service`() =
        testBackEndApplication { client ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            val match = makeFlyer(id = "flyer-match")
            coEvery {
                flyerService.listFlyers(status = null, query = "concert", offset = 0, limit = 20)
            } returns Result.success(makePage(match))

            val response = client.get("api/v1/flyers?q=concert")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerId("flyer-match"), body.flyers.single().id)
        }

    @Test
    fun `test listFlyers forwards status filter to service`() =
        testBackEndApplication { client ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            val approved = makeFlyer(id = "flyer-approved", status = FlyerStatus.APPROVED)
            coEvery {
                flyerService.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 20)
            } returns Result.success(makePage(approved))

            val response = client.get("api/v1/flyers?status=approved")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerStatus.APPROVED, body.flyers.single().status)
        }

    // ── GET /api/v1/flyers/{id} ───────────────────────────────────────────────

    @Test
    fun `test getFlyer returns flyer when found`() =
        testBackEndApplication { _ ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            val flyer = makeFlyer(id = "flyer-1")
            coEvery { flyerService.getFlyer(any(), FlyerId("flyer-1")) } returns Result.success(flyer)

            val response = client.get("api/v1/flyers/flyer-1")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerId("flyer-1"), body.id)
        }

    @Test
    fun `test getFlyer returns 404 when service returns null`() =
        testBackEndApplication { _ ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            coEvery { flyerService.getFlyer(any(), FlyerId("missing")) } returns Result.success(null)

            val response = client.get("api/v1/flyers/missing")

            assertEquals(HttpStatusCode.NotFound, response.status)
        }

    @Test
    fun `test getFlyer returns 404 when service throws NotFoundException`() =
        testBackEndApplication { _ ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            coEvery {
                flyerService.getFlyer(any(), FlyerId("missing"))
            } returns Result.failure(NotFoundException("Flyer not found: missing"))

            val response = client.get("api/v1/flyers/missing")

            assertEquals(HttpStatusCode.NotFound, response.status)
        }

    // ── GET /api/v1/flyers/archive ────────────────────────────────────────────

    @Test
    fun `test listArchived returns archived flyers`() =
        testBackEndApplication { client ->
            stubUnauthenticated()
            val flyerService = get<FlyerService>()
            val archived = makeFlyer(id = "archived-1", status = FlyerStatus.ARCHIVED)
            coEvery {
                flyerService.listFlyers(status = FlyerStatus.ARCHIVED, query = null, offset = 0, limit = 20)
            } returns Result.success(makePage(archived))

            val response = client.get("api/v1/flyers/archive")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerId("archived-1"), body.flyers.single().id)
        }

    // ── GET /api/v1/flyers/mine ───────────────────────────────────────────────

    @Test
    fun `test listMyFlyers fails for unauthenticated user`() =
        testBackEndApplication { client ->
            stubUnauthenticated()

            val response = client.get("api/v1/flyers/mine")

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `test listMyFlyers returns caller's flyers when authenticated`() =
        testBackEndApplication { client ->
            val userId = UserId("user-1")
            stubAuthenticated(userId)
            val flyerService = get<FlyerService>()
            val ownFlyer = makeFlyer(id = "my-flyer", uploaderId = "user-1")
            coEvery {
                flyerService.listFlyersByUploader(uploaderId = userId, offset = 0, limit = 20)
            } returns Result.success(makePage(ownFlyer))

            val response = client.get("api/v1/flyers/mine")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<FlyerListNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerId("my-flyer"), body.flyers.single().id)
        }

    // ── POST /api/v1/flyers ───────────────────────────────────────────────────

    @Test
    fun `test createFlyer succeeds for authenticated user`() =
        testBackEndApplication { client ->
            // Arrange
            val flyerService = get<FlyerService>()
            val flyer = makeFlyer()
            coEvery {
                flyerService.createFlyer(
                    uploaderId = UserId("user-1"),
                    title = "Test Flyer",
                    description = "Test Description",
                    expiresAt = null,
                )
            } returns Result.success(flyer to signedUpload)
            stubAuthenticated(UserId("user-1"))

            // Act
            val response =
                client.post("api/v1/flyers") {
                    setBody("""{"title":"Test Flyer","description":"Test Description","expires_at":null}""")
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test createFlyer fails for unauthenticated user`() =
        testBackEndApplication { client ->
            // Arrange
            stubUnauthenticated()

            // Act
            val response =
                client.post("api/v1/flyers") {
                    setBody("""{"title":"Test Flyer","description":"Test Description"}""")
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    // ── PUT /api/v1/flyers/{id} ───────────────────────────────────────────────

    @Test
    fun `test updateFlyer with requestUpload returns a signed upload URL`() =
        testBackEndApplication { client ->
            // Arrange
            val flyerService = get<FlyerService>()
            val flyerId = FlyerId("flyer-1")
            val updatedFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1")
            coEvery {
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = UserId("user-1"),
                    title = null,
                    description = null,
                    expiresAt = null,
                    requestUpload = true,
                )
            } returns Result.success(updatedFlyer to signedUpload)
            stubAuthenticated(UserId("user-1"))

            // Act
            val response =
                client.put("api/v1/flyers/flyer-1") {
                    setBody("""{"title":null,"description":null,"expires_at":null,"request_upload":true}""")
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<UpdateFlyerNetworkResponse>(response.bodyAsText())
            assertEquals(FlyerId("flyer-1"), body.flyer.id)
            assertEquals(FlyerStatus.PENDING, body.flyer.status)
            assertEquals("https://signed.example.com/upload", body.upload?.signedUrl)
            assertEquals("token", body.upload?.token)
        }

    @Test
    fun `test updateFlyer without requestUpload returns no upload URL`() =
        testBackEndApplication { client ->
            // Arrange
            val flyerService = get<FlyerService>()
            val flyerId = FlyerId("flyer-1")
            val updatedFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1")
            coEvery {
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = UserId("user-1"),
                    title = "Updated Title",
                    description = null,
                    expiresAt = null,
                    requestUpload = false,
                )
            } returns Result.success(updatedFlyer to null)
            stubAuthenticated(UserId("user-1"))

            // Act
            val response =
                client.put("api/v1/flyers/flyer-1") {
                    setBody("""{"title":"Updated Title","description":null,"expires_at":null,"request_upload":false}""")
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<UpdateFlyerNetworkResponse>(response.bodyAsText())
            assertNull(body.upload)
        }

    @Test
    fun `test updateFlyer fails for unauthenticated user`() =
        testBackEndApplication { client ->
            // Arrange
            stubUnauthenticated()

            // Act
            val response =
                client.put("api/v1/flyers/flyer-1") {
                    setBody("""{"title":"Updated Title","description":null,"expires_at":null,"request_upload":false}""")
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `test updateFlyer fails with 403 when requester is not the owner`() =
        testBackEndApplication { client ->
            // Arrange
            val flyerService = get<FlyerService>()
            val flyerId = FlyerId("flyer-1")
            coEvery {
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = UserId("user-2"),
                    title = "Hijacked Title",
                    description = null,
                    expiresAt = null,
                    requestUpload = false,
                )
            } returns Result.failure(ForbiddenException("You do not own this flyer."))
            stubAuthenticated(UserId("user-2"))

            // Act
            val response =
                client.put("api/v1/flyers/flyer-1") {
                    setBody(
                        """{"title":"Hijacked Title","description":null,"expires_at":null,"request_upload":false}""",
                    )
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
}
