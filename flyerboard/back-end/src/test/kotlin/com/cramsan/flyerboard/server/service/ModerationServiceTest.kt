package com.cramsan.flyerboard.server.service

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.datastore.PagedResult
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.UserProfile
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Unit tests for [ModerationService].
 */
@OptIn(ExperimentalTime::class)
class ModerationServiceTest {

    private lateinit var flyerDatastore: FlyerDatastore
    private lateinit var fileDatastore: FileDatastore
    private lateinit var userProfileDatastore: UserProfileDatastore
    private lateinit var moderationService: ModerationService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        flyerDatastore = mockk()
        fileDatastore = mockk()
        userProfileDatastore = mockk()
        moderationService = ModerationService(flyerDatastore, fileDatastore, userProfileDatastore)
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

    private fun makeAdminProfile(userId: String = "admin-1") = UserProfile(
        id = UserId(userId),
        role = UserRole.ADMIN,
        createdAt = Instant.fromEpochSeconds(0),
        updatedAt = Instant.fromEpochSeconds(0),
    )

    private fun makeUserProfile(userId: String = "user-1") = UserProfile(
        id = UserId(userId),
        role = UserRole.USER,
        createdAt = Instant.fromEpochSeconds(0),
        updatedAt = Instant.fromEpochSeconds(0),
    )

    // ── approveFlyer ──────────────────────────────────────────────────────────

    @Test
    fun `approveFlyer transitions status to APPROVED`() = runTest {
        val flyerId = FlyerId("flyer-1")
        val adminId = UserId("admin-1")
        val pendingFlyer = makeFlyer(id = "flyer-1", status = FlyerStatus.PENDING)
        val approvedFlyer = pendingFlyer.copy(status = FlyerStatus.APPROVED)

        coEvery { userProfileDatastore.getUserProfile(adminId) } returns Result.success(makeAdminProfile("admin-1"))
        coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(pendingFlyer)
        coEvery { flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any()) } returns Result.success(approvedFlyer)
        coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

        val result = moderationService.approveFlyer(flyerId, adminId)

        assertTrue(result.isSuccess)
        assertEquals(FlyerStatus.APPROVED, result.getOrThrow().status)
        coVerify {
            flyerDatastore.updateFlyer(
                id = flyerId,
                title = null,
                description = null,
                filePath = null,
                status = FlyerStatus.APPROVED,
                expiresAt = null,
            )
        }
    }

    @Test
    fun `approveFlyer fails when user is not admin`() = runTest {
        val flyerId = FlyerId("flyer-1")
        val regularUserId = UserId("user-1")

        coEvery { userProfileDatastore.getUserProfile(regularUserId) } returns Result.success(makeUserProfile("user-1"))

        val result = moderationService.approveFlyer(flyerId, regularUserId)

        assertTrue(result.isFailure)
    }

    @Test
    fun `approveFlyer fails when user has no profile`() = runTest {
        val flyerId = FlyerId("flyer-1")
        val unknownUserId = UserId("unknown-user")

        coEvery { userProfileDatastore.getUserProfile(unknownUserId) } returns Result.success(null)

        val result = moderationService.approveFlyer(flyerId, unknownUserId)

        assertTrue(result.isFailure)
    }

    @Test
    fun `approveFlyer fails when flyer does not exist`() = runTest {
        val flyerId = FlyerId("nonexistent")
        val adminId = UserId("admin-1")

        coEvery { userProfileDatastore.getUserProfile(adminId) } returns Result.success(makeAdminProfile())
        coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(null)

        val result = moderationService.approveFlyer(flyerId, adminId)

        assertTrue(result.isFailure)
    }

    // ── rejectFlyer ───────────────────────────────────────────────────────────

    @Test
    fun `rejectFlyer transitions status to REJECTED`() = runTest {
        val flyerId = FlyerId("flyer-1")
        val adminId = UserId("admin-1")
        val pendingFlyer = makeFlyer(id = "flyer-1", status = FlyerStatus.PENDING)
        val rejectedFlyer = pendingFlyer.copy(status = FlyerStatus.REJECTED)

        coEvery { userProfileDatastore.getUserProfile(adminId) } returns Result.success(makeAdminProfile("admin-1"))
        coEvery { flyerDatastore.getFlyer(flyerId) } returns Result.success(pendingFlyer)
        coEvery { flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any()) } returns Result.success(rejectedFlyer)
        coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

        val result = moderationService.rejectFlyer(flyerId, adminId)

        assertTrue(result.isSuccess)
        assertEquals(FlyerStatus.REJECTED, result.getOrThrow().status)
        coVerify {
            flyerDatastore.updateFlyer(
                id = flyerId,
                title = null,
                description = null,
                filePath = null,
                status = FlyerStatus.REJECTED,
                expiresAt = null,
            )
        }
    }

    @Test
    fun `rejectFlyer fails when user is not admin`() = runTest {
        val flyerId = FlyerId("flyer-1")
        val regularUserId = UserId("user-1")

        coEvery { userProfileDatastore.getUserProfile(regularUserId) } returns Result.success(makeUserProfile("user-1"))

        val result = moderationService.rejectFlyer(flyerId, regularUserId)

        assertTrue(result.isFailure)
    }

    // ── listPendingFlyers ─────────────────────────────────────────────────────

    @Test
    fun `listPendingFlyers returns only PENDING flyers`() = runTest {
        val pendingFlyers = listOf(
            makeFlyer(id = "flyer-1", status = FlyerStatus.PENDING),
            makeFlyer(id = "flyer-2", status = FlyerStatus.PENDING),
        )
        val page = PagedResult(items = pendingFlyers, total = 2L)

        coEvery { flyerDatastore.listFlyers(FlyerStatus.PENDING, null, 0, 10) } returns Result.success(page)
        coEvery { fileDatastore.getSignedUrl(any()) } returns Result.success("https://signed.example.com/file.png")

        val result = moderationService.listPendingFlyers(offset = 0, limit = 10)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().items.size)
        coVerify { flyerDatastore.listFlyers(FlyerStatus.PENDING, null, 0, 10) }
    }
}
