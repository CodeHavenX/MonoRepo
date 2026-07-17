package com.cramsan.flyerboard.server.service

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.datastore.PagedResult
import com.cramsan.flyerboard.server.datastore.SignedUpload
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.framework.assertlib.assertNull
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Unit tests for [FlyerService].
 */
@OptIn(ExperimentalTime::class)
class FlyerServiceTest {
    private lateinit var flyerDatastore: FlyerDatastore
    private lateinit var fileDatastore: FileDatastore
    private lateinit var flyerService: FlyerService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        flyerDatastore = mockk()
        fileDatastore = mockk()
        flyerService = FlyerService(flyerDatastore, fileDatastore)
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

    // ── createFlyer ───────────────────────────────────────────────────────────

    @Test
    fun `createFlyer returns flyer with PENDING status and a signed upload URL`() =
        runTest {
            val uploader = UserId("user-1")
            val expectedFlyer = makeFlyer(status = FlyerStatus.PENDING)

            coEvery {
                flyerDatastore.createFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.success(expectedFlyer)
            coEvery { fileDatastore.createSignedUploadUrl(any()) } returns Result.success(signedUpload)

            val result =
                flyerService.createFlyer(
                    uploaderId = uploader,
                    title = "Test Flyer",
                    description = "Test Description",
                    expiresAt = null,
                )

            assertTrue(result.isSuccess)
            val (flyer, upload) = result.getOrThrow()
            assertEquals(FlyerStatus.PENDING, flyer.status)
            assertEquals(signedUpload, upload)
        }

    @Test
    fun `createFlyer uses a server-generated id as the file path`() =
        runTest {
            val uploader = UserId("user-1")
            val expectedFlyer = makeFlyer()
            var capturedId: String? = null
            var capturedFilePath: String? = null

            coEvery {
                flyerDatastore.createFlyer(any(), any(), any(), any(), any(), any())
            } coAnswers {
                capturedId = arg<String>(0)
                capturedFilePath = arg<String>(3)
                Result.success(expectedFlyer)
            }
            coEvery { fileDatastore.createSignedUploadUrl(any()) } returns Result.success(signedUpload)

            flyerService.createFlyer(
                uploaderId = uploader,
                title = "Title",
                description = "Desc",
                expiresAt = null,
            )

            assertEquals(capturedId, capturedFilePath)
        }

    @Test
    fun `createFlyer sanitizes HTML tags from title and description`() =
        runTest {
            val uploader = UserId("user-1")
            val expectedFlyer = makeFlyer()

            coEvery {
                flyerDatastore.createFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.success(expectedFlyer)
            coEvery { fileDatastore.createSignedUploadUrl(any()) } returns Result.success(signedUpload)

            flyerService.createFlyer(
                uploaderId = uploader,
                title = "<script>alert('xss')</script>Clean Title",
                description = "<b>Bold</b> description",
                expiresAt = null,
            )

            // The sanitizer strips tags (<script>, </script>, <b>, </b>) but keeps text content
            coVerify {
                flyerDatastore.createFlyer(
                    id = any(),
                    title = "alert('xss')Clean Title",
                    description = "Bold description",
                    filePath = any(),
                    uploaderId = uploader,
                    expiresAt = null,
                )
            }
        }

    @Test
    fun `createFlyer fails when signed upload URL generation fails`() =
        runTest {
            val uploader = UserId("user-1")
            val expectedFlyer = makeFlyer()

            coEvery {
                flyerDatastore.createFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.success(expectedFlyer)
            coEvery { fileDatastore.createSignedUploadUrl(any()) } returns Result.failure(RuntimeException("boom"))

            val result =
                flyerService.createFlyer(
                    uploaderId = uploader,
                    title = "Title",
                    description = "Desc",
                    expiresAt = null,
                )

            assertTrue(result.isFailure)
        }

    @Test
    fun `createFlyer fails when flyerDatastore createFlyer fails and does not request a signed upload URL`() =
        runTest {
            val uploader = UserId("user-1")

            coEvery {
                flyerDatastore.createFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.failure(RuntimeException("db error"))

            val result =
                flyerService.createFlyer(
                    uploaderId = uploader,
                    title = "Title",
                    description = "Desc",
                    expiresAt = null,
                )

            assertTrue(result.isFailure)
            coVerify(exactly = 0) { fileDatastore.createSignedUploadUrl(any()) }
        }

    // ── getFlyer ──────────────────────────────────────────────────────────────

    @Test
    fun `getFlyer returns flyer with fileUrl populated when found`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val flyer = makeFlyer(id = "flyer-1", status = FlyerStatus.APPROVED)
            val context = ClientContext.UnauthenticatedClientContext<FlyerBoardContextPayload>()

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(flyer)
            coEvery { fileDatastore.getSignedUrl(flyer.filePath) } returns
                Result.success("https://signed.example.com/file.png")

            val result = flyerService.getFlyer(context, flyerId)

            assertTrue(result.isSuccess)
            assertEquals("https://signed.example.com/file.png", result.getOrThrow()?.fileUrl)
        }

    @Test
    fun `getFlyer returns null when flyer is pending and user not authenticated`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val flyer = makeFlyer(id = "flyer-1", status = FlyerStatus.PENDING)
            val context = ClientContext.UnauthenticatedClientContext<FlyerBoardContextPayload>()

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(flyer)

            val result = flyerService.getFlyer(context, flyerId)

            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }

    @Test
    fun `getFlyer returns null when flyer is pending and user is not admin`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val flyer = makeFlyer(id = "flyer-1", status = FlyerStatus.PENDING)
            val context =
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(
                        userId = UserId("userId"),
                        role = UserRole.USER,
                    ),
                )

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(flyer)

            val result = flyerService.getFlyer(context, flyerId)

            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }

    @Test
    fun `getFlyer returns null when flyer is not found`() =
        runTest {
            val flyerId = FlyerId("nonexistent")
            val context = ClientContext.UnauthenticatedClientContext<FlyerBoardContextPayload>()

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(null)

            val result = flyerService.getFlyer(context, flyerId)

            assertTrue(result.isSuccess)
            assertEquals(null, result.getOrThrow())
        }

    @Test
    fun `getFlyer returns flyer with null fileUrl when getSignedUrl fails`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val flyer = makeFlyer(id = "flyer-1")
            val context = ClientContext.UnauthenticatedClientContext<FlyerBoardContextPayload>()

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(flyer)
            coEvery { fileDatastore.getSignedUrl(flyer.filePath) } returns Result.failure(RuntimeException("boom"))

            val result = flyerService.getFlyer(context, flyerId)

            assertTrue(result.isSuccess)
            assertEquals(null, result.getOrThrow()?.fileUrl)
        }

    // ── updateFlyer ───────────────────────────────────────────────────────────

    @Test
    fun `updateFlyer resets status to PENDING`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val uploaderId = UserId("user-1")
            val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1", status = FlyerStatus.APPROVED)
            val updatedFlyer = existingFlyer.copy(status = FlyerStatus.PENDING)

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)
            coEvery {
                flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.success(updatedFlyer)

            val result =
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = uploaderId,
                    title = "Updated Title",
                    description = null,
                    expiresAt = null,
                    requestUpload = false,
                )

            assertTrue(result.isSuccess)
            val (flyer, upload) = result.getOrThrow()
            assertEquals(FlyerStatus.PENDING, flyer.status)
            assertEquals(null, upload)
            coVerify {
                flyerDatastore.updateFlyer(
                    id = flyerId,
                    title = "Updated Title",
                    description = null,
                    status = FlyerStatus.PENDING,
                    expiresAt = null,
                    rejectionReason = null,
                )
            }
        }

    @Test
    fun `updateFlyer with requestUpload returns a fresh signed upload URL`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val uploaderId = UserId("user-1")
            val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1", status = FlyerStatus.APPROVED)
            val updatedFlyer = existingFlyer.copy(status = FlyerStatus.PENDING)

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)
            coEvery {
                flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.success(updatedFlyer)
            coEvery { fileDatastore.createSignedUploadUrl(updatedFlyer.filePath) } returns Result.success(signedUpload)

            val result =
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = uploaderId,
                    title = null,
                    description = null,
                    expiresAt = null,
                    requestUpload = true,
                )

            assertTrue(result.isSuccess)
            val (_, upload) = result.getOrThrow()
            assertEquals(signedUpload, upload)
        }

    @Test
    fun `updateFlyer fails when requester is not the owner`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1")

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)

            val result =
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = UserId("user-2"), // not the owner
                    title = "Hijacked Title",
                    description = null,
                    expiresAt = null,
                    requestUpload = false,
                )

            assertTrue(result.isFailure)
        }

    @Test
    fun `updateFlyer fails when flyer does not exist`() =
        runTest {
            val flyerId = FlyerId("nonexistent")

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(null)

            val result =
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = UserId("user-1"),
                    title = "Title",
                    description = null,
                    expiresAt = null,
                    requestUpload = false,
                )

            assertTrue(result.isFailure)
        }

    @Test
    fun `updateFlyer with only title provided sanitizes title and keeps description null`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val uploaderId = UserId("user-1")
            val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1", status = FlyerStatus.APPROVED)
            val updatedFlyer = existingFlyer.copy(status = FlyerStatus.PENDING)

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)
            coEvery {
                flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.success(updatedFlyer)

            val result =
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = uploaderId,
                    title = "<b>New</b> Title",
                    description = null,
                    expiresAt = null,
                    requestUpload = false,
                )

            assertTrue(result.isSuccess)
            coVerify {
                flyerDatastore.updateFlyer(
                    id = flyerId,
                    title = "New Title",
                    description = null,
                    status = FlyerStatus.PENDING,
                    expiresAt = null,
                    rejectionReason = null,
                )
            }
        }

    @Test
    fun `updateFlyer passes through expiresAt when provided`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val uploaderId = UserId("user-1")
            val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1", status = FlyerStatus.APPROVED)
            val newExpiresAt = Instant.fromEpochSeconds(1_000_000)
            val updatedFlyer = existingFlyer.copy(status = FlyerStatus.PENDING, expiresAt = newExpiresAt)

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)
            coEvery {
                flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.success(updatedFlyer)

            val result =
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = uploaderId,
                    title = null,
                    description = null,
                    expiresAt = newExpiresAt,
                    requestUpload = false,
                )

            assertTrue(result.isSuccess)
            coVerify {
                flyerDatastore.updateFlyer(
                    id = flyerId,
                    title = null,
                    description = null,
                    status = FlyerStatus.PENDING,
                    expiresAt = newExpiresAt,
                    rejectionReason = null,
                )
            }
        }

    @Test
    fun `updateFlyer fails when requestUpload is true and signed upload URL generation fails`() =
        runTest {
            val flyerId = FlyerId("flyer-1")
            val uploaderId = UserId("user-1")
            val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1", status = FlyerStatus.APPROVED)
            val updatedFlyer = existingFlyer.copy(status = FlyerStatus.PENDING)

            coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)
            coEvery {
                flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any())
            } returns Result.success(updatedFlyer)
            coEvery { fileDatastore.createSignedUploadUrl(updatedFlyer.filePath) } returns
                Result.failure(RuntimeException("boom"))

            val result =
                flyerService.updateFlyer(
                    flyerId = flyerId,
                    requesterId = uploaderId,
                    title = null,
                    description = null,
                    expiresAt = null,
                    requestUpload = true,
                )

            assertTrue(result.isFailure)
        }

    // ── listFlyers ────────────────────────────────────────────────────────────

    @Test
    fun `listFlyers returns paginated flyers with signed URLs`() =
        runTest {
            val flyers = listOf(makeFlyer(id = "flyer-1"), makeFlyer(id = "flyer-2"))
            val page = PagedResult(items = flyers, total = 2L)

            coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)
            coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

            val result = flyerService.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            assertEquals(2, result.getOrThrow().items.size)
        }

    @Test
    fun `listFlyers passes non-null query through to flyerDatastore listFlyers`() =
        runTest {
            val flyers = listOf(makeFlyer(id = "flyer-1"))
            val page = PagedResult(items = flyers, total = 1L)

            coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)
            coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

            val result =
                flyerService.listFlyers(status = FlyerStatus.APPROVED, query = "search term", offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            coVerify {
                flyerDatastore.listFlyers(
                    status = FlyerStatus.APPROVED,
                    query = "search term",
                    offset = 0,
                    limit = 10,
                )
            }
        }

    @Test
    fun `listFlyers returns empty items list with correct total when page is empty`() =
        runTest {
            val page = PagedResult<Flyer>(items = emptyList(), total = 0L)

            coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)

            val result = flyerService.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            val paginated = result.getOrThrow()
            assertTrue(paginated.items.isEmpty())
            assertEquals(0, paginated.total)
        }

    @Test
    fun `listFlyers sets fileUrl to null for items when getSignedUrl fails`() =
        runTest {
            val flyers = listOf(makeFlyer(id = "flyer-1"))
            val page = PagedResult(items = flyers, total = 1L)

            coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)
            coEvery { fileDatastore.getSignedUrl(any()) } returns Result.failure(RuntimeException("boom"))

            val result = flyerService.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            assertEquals(
                null,
                result
                    .getOrThrow()
                    .items
                    .single()
                    .fileUrl,
            )
        }

    @Test
    fun `listFlyers with null status defaults to APPROVED to keep pending and rejected flyers private`() =
        runTest {
            val page = PagedResult<Flyer>(items = emptyList(), total = 0L)
            coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)

            val result = flyerService.listFlyers(status = null, query = null, offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            coVerify {
                flyerDatastore.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 10)
            }
        }

    @Test
    fun `listFlyers with status PENDING is coerced to APPROVED since this endpoint is public`() =
        runTest {
            val page = PagedResult<Flyer>(items = emptyList(), total = 0L)
            coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)

            val result = flyerService.listFlyers(status = FlyerStatus.PENDING, query = null, offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            coVerify {
                flyerDatastore.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 10)
            }
        }

    @Test
    fun `listFlyers with status REJECTED is coerced to APPROVED since this endpoint is public`() =
        runTest {
            val page = PagedResult<Flyer>(items = emptyList(), total = 0L)
            coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)

            val result = flyerService.listFlyers(status = FlyerStatus.REJECTED, query = null, offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            coVerify {
                flyerDatastore.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 10)
            }
        }

    @Test
    fun `listFlyers with status ARCHIVED passes through unchanged`() =
        runTest {
            val page = PagedResult<Flyer>(items = emptyList(), total = 0L)
            coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)

            val result = flyerService.listFlyers(status = FlyerStatus.ARCHIVED, query = null, offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            coVerify {
                flyerDatastore.listFlyers(status = FlyerStatus.ARCHIVED, query = null, offset = 0, limit = 10)
            }
        }

    // ── listFlyersByUploader ──────────────────────────────────────────────────

    @Test
    fun `listFlyersByUploader returns paginated flyers belonging to the uploader with signed URLs`() =
        runTest {
            val uploaderId = UserId("user-1")
            val flyers =
                listOf(
                    makeFlyer(id = "flyer-1", uploaderId = "user-1"),
                    makeFlyer(id = "flyer-2", uploaderId = "user-1"),
                )
            val page = PagedResult(items = flyers, total = 2L)

            coEvery { flyerDatastore.listFlyersByUploader(uploaderId, any(), any()) } returns Result.success(page)
            coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

            val result = flyerService.listFlyersByUploader(uploaderId = uploaderId, offset = 0, limit = 10)

            assertTrue(result.isSuccess)
            val paginated = result.getOrThrow()
            assertEquals(2, paginated.items.size)
            assertEquals(2, paginated.total)
            assertTrue(paginated.items.all { it.fileUrl == "https://signed.example.com/file.png" })
            assertTrue(paginated.items.all { it.uploaderId == uploaderId })
        }
}
