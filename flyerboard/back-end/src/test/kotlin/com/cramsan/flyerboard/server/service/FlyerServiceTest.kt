package com.cramsan.flyerboard.server.service

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.datastore.PagedResult
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.settings.FlyerBoardSettingKey
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.koin.core.context.stopKoin

/**
 * Unit tests for [FlyerService].
 */
@OptIn(ExperimentalTime::class)
class FlyerServiceTest {

    private lateinit var flyerDatastore: FlyerDatastore
    private lateinit var fileDatastore: FileDatastore
    private lateinit var settingsHolder: SettingsHolder
    private lateinit var flyerService: FlyerService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        flyerDatastore = mockk()
        fileDatastore = mockk()
        settingsHolder = mockk()
        flyerService = FlyerService(flyerDatastore, fileDatastore, settingsHolder)

        // Default: no max file size configured; falls back to default 10 MB
        every { settingsHolder.getLong(FlyerBoardSettingKey.MaxFileSizeBytes) } returns null
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
    )

    // ── createFlyer ───────────────────────────────────────────────────────────

    @Test
    fun `createFlyer returns flyer with PENDING status`() = runTest {
        val uploader = UserId("user-1")
        val expectedFlyer = makeFlyer(status = FlyerStatus.PENDING)

        coEvery { fileDatastore.uploadFile(any(), any()) } returns Result.success("uploads/file.png")
        coEvery { flyerDatastore.createFlyer(any(), any(), any(), any(), any()) } returns Result.success(expectedFlyer)
        coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

        val result = flyerService.createFlyer(
            uploaderId = uploader,
            title = "Test Flyer",
            description = "Test Description",
            expiresAt = null,
            fileContent = ByteArray(1024),
            fileName = "flyer.png",
            mimeType = "image/png",
        )

        assertTrue(result.isSuccess)
        assertEquals(FlyerStatus.PENDING, result.getOrThrow().status)
        coVerify { fileDatastore.uploadFile("flyer.png", any()) }
    }

    @Test
    fun `createFlyer rejects unsupported MIME type`() = runTest {
        val result = flyerService.createFlyer(
            uploaderId = UserId("user-1"),
            title = "Title",
            description = "Desc",
            expiresAt = null,
            fileContent = ByteArray(100),
            fileName = "malware.exe",
            mimeType = "application/exe",
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `createFlyer rejects file larger than max allowed size`() = runTest {
        // Default max is 10 MB; 11 MB should be rejected
        val oversized = ByteArray(11 * 1024 * 1024)

        val result = flyerService.createFlyer(
            uploaderId = UserId("user-1"),
            title = "Title",
            description = "Desc",
            expiresAt = null,
            fileContent = oversized,
            fileName = "big.png",
            mimeType = "image/png",
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `createFlyer respects custom max file size from settings`() = runTest {
        // Set max to 1 MB
        every { settingsHolder.getLong(FlyerBoardSettingKey.MaxFileSizeBytes) } returns 1L * 1024L * 1024L
        val oversized = ByteArray(2 * 1024 * 1024)

        val result = flyerService.createFlyer(
            uploaderId = UserId("user-1"),
            title = "Title",
            description = "Desc",
            expiresAt = null,
            fileContent = oversized,
            fileName = "file.png",
            mimeType = "image/png",
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `createFlyer sanitizes HTML tags from title and description`() = runTest {
        val uploader = UserId("user-1")
        val expectedFlyer = makeFlyer()

        coEvery { fileDatastore.uploadFile(any(), any()) } returns Result.success("uploads/file.png")
        coEvery { flyerDatastore.createFlyer(any(), any(), any(), any(), any()) } returns Result.success(expectedFlyer)
        coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

        flyerService.createFlyer(
            uploaderId = uploader,
            title = "<script>alert('xss')</script>Clean Title",
            description = "<b>Bold</b> description",
            expiresAt = null,
            fileContent = ByteArray(100),
            fileName = "flyer.png",
            mimeType = "image/png",
        )

        // The sanitizer strips tags (<script>, </script>, <b>, </b>) but keeps text content
        coVerify {
            flyerDatastore.createFlyer(
                title = "alert('xss')Clean Title",
                description = "Bold description",
                filePath = any(),
                uploaderId = uploader,
                expiresAt = null,
            )
        }
    }

    @Test
    fun `createFlyer attaches signed URL to returned flyer`() = runTest {
        val uploader = UserId("user-1")
        val expectedFlyer = makeFlyer()
        val signedUrl = "https://signed.example.com/file.png"

        coEvery { fileDatastore.uploadFile(any(), any()) } returns Result.success("uploads/file.png")
        coEvery { flyerDatastore.createFlyer(any(), any(), any(), any(), any()) } returns Result.success(expectedFlyer)
        coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success(signedUrl)

        val result = flyerService.createFlyer(
            uploaderId = uploader,
            title = "Title",
            description = "Desc",
            expiresAt = null,
            fileContent = ByteArray(100),
            fileName = "flyer.png",
            mimeType = "image/png",
        )

        assertEquals(signedUrl, result.getOrThrow().fileUrl)
    }

    // ── updateFlyer ───────────────────────────────────────────────────────────

    @Test
    fun `updateFlyer resets status to PENDING`() = runTest {
        val flyerId = FlyerId("flyer-1")
        val uploaderId = UserId("user-1")
        val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1", status = FlyerStatus.APPROVED)
        val updatedFlyer = existingFlyer.copy(status = FlyerStatus.PENDING)

        coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)
        coEvery { flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any()) } returns Result.success(updatedFlyer)
        coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

        val result = flyerService.updateFlyer(
            flyerId = flyerId,
            requesterId = uploaderId,
            title = "Updated Title",
            description = null,
            expiresAt = null,
            fileContent = null,
            fileName = null,
            mimeType = null,
        )

        assertTrue(result.isSuccess)
        coVerify {
            flyerDatastore.updateFlyer(
                id = flyerId,
                title = "Updated Title",
                description = null,
                filePath = null,
                status = FlyerStatus.PENDING,
                expiresAt = null,
            )
        }
    }

    @Test
    fun `updateFlyer fails when requester is not the owner`() = runTest {
        val flyerId = FlyerId("flyer-1")
        val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1")

        coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)

        val result = flyerService.updateFlyer(
            flyerId = flyerId,
            requesterId = UserId("user-2"), // not the owner
            title = "Hijacked Title",
            description = null,
            expiresAt = null,
            fileContent = null,
            fileName = null,
            mimeType = null,
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `updateFlyer fails when flyer does not exist`() = runTest {
        val flyerId = FlyerId("nonexistent")

        coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(null)

        val result = flyerService.updateFlyer(
            flyerId = flyerId,
            requesterId = UserId("user-1"),
            title = "Title",
            description = null,
            expiresAt = null,
            fileContent = null,
            fileName = null,
            mimeType = null,
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `updateFlyer validates MIME type when new file is provided`() = runTest {
        val flyerId = FlyerId("flyer-1")
        val uploaderId = UserId("user-1")
        val existingFlyer = makeFlyer(id = "flyer-1", uploaderId = "user-1")

        coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(existingFlyer)

        val result = flyerService.updateFlyer(
            flyerId = flyerId,
            requesterId = uploaderId,
            title = null,
            description = null,
            expiresAt = null,
            fileContent = ByteArray(100),
            fileName = "file.exe",
            mimeType = "application/exe",
        )

        assertTrue(result.isFailure)
    }

    // ── listFlyers ────────────────────────────────────────────────────────────

    @Test
    fun `listFlyers returns paginated flyers with signed URLs`() = runTest {
        val flyers = listOf(makeFlyer(id = "flyer-1"), makeFlyer(id = "flyer-2"))
        val page = PagedResult(items = flyers, total = 2L)

        coEvery { flyerDatastore.listFlyers(any(), any(), any(), any()) } returns Result.success(page)
        coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

        val result = flyerService.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 10)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().items.size)
    }
}
