package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

import app.cash.turbine.turbineScope
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.lib.models.PaginatedFlyerModel
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ModerationQueueViewModelTest : CoroutineTest() {

    private lateinit var flyerManager: FlyerManager
    private lateinit var viewModel: ModerationQueueViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        flyerManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = ModerationQueueViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            flyerManager = flyerManager,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(ModerationQueueUIState.Initial, viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.pendingFlyers.isEmpty())
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadPendingFlyers success updates UIState with pending flyers`() = runCoroutineTest {
        val flyers = listOf(makeFlyerModel("p1"), makeFlyerModel("p2"))
        val paginated = PaginatedFlyerModel(flyers = flyers, total = 2, offset = 0, limit = 20)
        coEvery { flyerManager.listPendingFlyers() } returns Result.success(paginated)

        viewModel.loadPendingFlyers()

        coVerify { flyerManager.listPendingFlyers() }
        assertEquals(2, viewModel.uiState.value.pendingFlyers.size)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadPendingFlyers failure sets errorMessage and emits snackbar`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            coEvery { flyerManager.listPendingFlyers() } returns Result.failure(RuntimeException("Forbidden"))

            viewModel.loadPendingFlyers()

            coVerify { flyerManager.listPendingFlyers() }
            assertEquals("Forbidden", viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.isLoading)

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `approveFlyer success emits snackbar and reloads list`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            val flyerId = FlyerId("flyer-to-approve")
            val approvedFlyer = makeFlyerModel("flyer-to-approve")
            coEvery {
                flyerManager.moderate(flyerId, ModerationQueueViewModel.ACTION_APPROVE)
            } returns Result.success(approvedFlyer)
            val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
            coEvery { flyerManager.listPendingFlyers() } returns Result.success(paginated)

            viewModel.approveFlyer(flyerId)

            coVerify { flyerManager.moderate(flyerId, ModerationQueueViewModel.ACTION_APPROVE) }
            coVerify { flyerManager.listPendingFlyers() }

            val snackbar = turbine.awaitItem()
            assertTrue(snackbar is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `approveFlyer failure emits snackbar`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            val flyerId = FlyerId("flyer-fail")
            coEvery {
                flyerManager.moderate(flyerId, ModerationQueueViewModel.ACTION_APPROVE)
            } returns Result.failure(RuntimeException("Not allowed"))

            viewModel.approveFlyer(flyerId)

            coVerify { flyerManager.moderate(flyerId, ModerationQueueViewModel.ACTION_APPROVE) }

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `rejectFlyer success emits snackbar and reloads list`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            val flyerId = FlyerId("flyer-to-reject")
            val rejectedFlyer = makeFlyerModel("flyer-to-reject")
            coEvery {
                flyerManager.moderate(flyerId, ModerationQueueViewModel.ACTION_REJECT)
            } returns Result.success(rejectedFlyer)
            val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
            coEvery { flyerManager.listPendingFlyers() } returns Result.success(paginated)

            viewModel.rejectFlyer(flyerId)

            coVerify { flyerManager.moderate(flyerId, ModerationQueueViewModel.ACTION_REJECT) }
            coVerify { flyerManager.listPendingFlyers() }

            val snackbar = turbine.awaitItem()
            assertTrue(snackbar is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `rejectFlyer failure emits snackbar`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            val flyerId = FlyerId("flyer-fail")
            coEvery {
                flyerManager.moderate(flyerId, ModerationQueueViewModel.ACTION_REJECT)
            } returns Result.failure(RuntimeException("Not allowed"))

            viewModel.rejectFlyer(flyerId)

            coVerify { flyerManager.moderate(flyerId, ModerationQueueViewModel.ACTION_REJECT) }

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `refresh calls loadPendingFlyers`() = runCoroutineTest {
        val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
        coEvery { flyerManager.listPendingFlyers() } returns Result.success(paginated)

        viewModel.refresh()

        coVerify { flyerManager.listPendingFlyers() }
    }

    @Test
    fun `loadPendingFlyers success with empty list updates UIState`() = runCoroutineTest {
        val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
        coEvery { flyerManager.listPendingFlyers() } returns Result.success(paginated)

        viewModel.loadPendingFlyers()

        assertTrue(viewModel.uiState.value.pendingFlyers.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `navigateBack emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateBack()

            assertEquals(FlyerBoardWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    private fun makeFlyerModel(id: String) = FlyerModel(
        id = FlyerId(id),
        title = "Pending Flyer $id",
        description = "Description",
        fileUrl = null,
        status = FlyerStatus.PENDING,
        expiresAt = null,
        uploaderId = UserId("user-1"),
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z",
    )
}
