package com.cramsan.flyerboard.server.service

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Unit tests for [ExpiryService].
 */
@OptIn(ExperimentalTime::class)
class ExpiryServiceTest {

    private lateinit var flyerDatastore: FlyerDatastore
    private lateinit var expiryService: ExpiryService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        flyerDatastore = mockk()
        expiryService = ExpiryService(flyerDatastore)
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun makeFlyer(
        id: String = "flyer-1",
        status: FlyerStatus = FlyerStatus.APPROVED,
    ) = Flyer(
        id = FlyerId(id),
        title = "Test Flyer",
        description = "Test Description",
        filePath = "uploads/file.png",
        status = status,
        expiresAt = Instant.fromEpochSeconds(0),
        uploaderId = UserId("user-1"),
        createdAt = Instant.fromEpochSeconds(0),
        updatedAt = Instant.fromEpochSeconds(0),
    )

    // ── start / archiveExpiredFlyers ──────────────────────────────────────────

    @Test
    fun `expired flyers are transitioned to ARCHIVED after one tick`() = runTest {
        val expiredFlyer = makeFlyer(id = "flyer-expired", status = FlyerStatus.APPROVED)
        val archivedFlyer = expiredFlyer.copy(status = FlyerStatus.ARCHIVED)

        coEvery { flyerDatastore.listExpiredFlyers(any()) } returns Result.success(listOf(expiredFlyer))
        coEvery { flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any()) } returns Result.success(archivedFlyer)

        expiryService.start(backgroundScope)

        // Advance past the initial 1-hour delay to trigger the first archival run
        advanceTimeBy(1.hours + 1.milliseconds)

        coVerify {
            flyerDatastore.updateFlyer(
                id = expiredFlyer.id,
                title = null,
                description = null,
                filePath = null,
                status = FlyerStatus.ARCHIVED,
                expiresAt = null,
            )
        }
    }

    @Test
    fun `no updates are made when there are no expired flyers`() = runTest {
        coEvery { flyerDatastore.listExpiredFlyers(any()) } returns Result.success(emptyList())

        expiryService.start(backgroundScope)

        advanceTimeBy(1.hours + 1.milliseconds)

        coVerify(exactly = 0) {
            flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `service runs again on the second tick`() = runTest {
        val expiredFlyer = makeFlyer(id = "flyer-expired", status = FlyerStatus.APPROVED)
        val archivedFlyer = expiredFlyer.copy(status = FlyerStatus.ARCHIVED)

        coEvery { flyerDatastore.listExpiredFlyers(any()) } returns Result.success(listOf(expiredFlyer))
        coEvery { flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any()) } returns Result.success(archivedFlyer)

        expiryService.start(backgroundScope)

        // Advance past two ticks
        advanceTimeBy(2.hours + 1.milliseconds)

        coVerify(exactly = 2) {
            flyerDatastore.updateFlyer(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `service does not run before the first hour has elapsed`() = runTest {
        coEvery { flyerDatastore.listExpiredFlyers(any()) } returns Result.success(emptyList())

        expiryService.start(backgroundScope)

        // Advance less than 1 hour — the tick should not have fired yet
        advanceTimeBy(30.minutes - 1.milliseconds)

        coVerify(exactly = 0) {
            flyerDatastore.listExpiredFlyers(any())
        }
    }
}
