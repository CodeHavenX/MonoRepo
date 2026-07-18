package com.cramsan.flyerboard.client.lib.managers

import com.cramsan.flyerboard.client.lib.models.PaginatedFlyerModel
import com.cramsan.flyerboard.client.lib.service.FlyerService
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class FlyerManagerTest : CoroutineTest() {
    private lateinit var flyerService: FlyerService
    private lateinit var flyerManager: FlyerManager

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        flyerService = mockk()
        flyerManager =
            FlyerManager(
                dependencies =
                ManagerDependencies(
                    appScope = testCoroutineScope,
                    dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                ),
                flyerService = flyerService,
            )
    }

    @Test
    fun `listArchived with query forwards query to flyerService`(): Unit =
        runCoroutineTest {
            val paginated = emptyPaginated()
            coEvery { flyerService.listArchived(any(), any(), query = "foo") } returns Result.success(paginated)

            flyerManager.listArchived(query = "foo")

            coVerify { flyerService.listArchived(any(), any(), query = "foo") }
        }

    @Test
    fun `listArchived with null query forwards null to flyerService`(): Unit =
        runCoroutineTest {
            val paginated = emptyPaginated()
            coEvery { flyerService.listArchived(any(), any(), query = null) } returns Result.success(paginated)

            flyerManager.listArchived()

            coVerify { flyerService.listArchived(any(), any(), query = null) }
        }

    private fun emptyPaginated() = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
}
