package com.cramsan.flyerboard.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.network.UpdateFlyerNetworkResponse
import com.cramsan.flyerboard.lib.serialization.createJson
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.datastore.SignedUpload
import com.cramsan.flyerboard.server.dependencyinjection.TestFlyerControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.TestFlyerServiceModule
import com.cramsan.flyerboard.server.dependencyinjection.testApplicationModule
import com.cramsan.flyerboard.server.service.FlyerService
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.exceptions.ForbiddenException
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.coEvery
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

    private val signedUpload = SignedUpload(signedUrl = "https://signed.example.com/upload", token = "token")

    @Test
    fun `test createFlyer succeeds for authenticated user`() =
        testBackEndApplication {
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

            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            } returns
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(userId = UserId("user-1"), role = UserRole.USER),
                )

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
        testBackEndApplication {
            // Arrange
            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            } returns ClientContext.UnauthenticatedClientContext()

            // Act
            val response =
                client.post("api/v1/flyers") {
                    setBody("""{"title":"Test Flyer","description":"Test Description"}""")
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `test updateFlyer with requestUpload returns a signed upload URL`() =
        testBackEndApplication {
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

            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            } returns
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(userId = UserId("user-1"), role = UserRole.USER),
                )

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
    fun `test updateFlyer fails with 403 when requester is not the owner`() =
        testBackEndApplication {
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

            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            } returns
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(userId = UserId("user-2"), role = UserRole.USER),
                )

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
