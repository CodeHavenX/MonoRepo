package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import app.cash.turbine.turbineScope
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
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

class FlyerListViewModelTest : CoroutineTest() {

    private lateinit var flyerManager: FlyerManager
    private lateinit var viewModel: FlyerListViewModel
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
        viewModel = FlyerListViewModel(
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
        assertEquals(FlyerListUIState.Initial, viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.flyers.isEmpty())
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadFlyers success updates UIState with flyers`() = runCoroutineTest {
        val flyers = listOf(makeFlyerModel("1"), makeFlyerModel("2"))
        val paginated = PaginatedFlyerModel(flyers = flyers, total = 2, offset = 0, limit = 20)
        coEvery { flyerManager.listFlyers() } returns Result.success(paginated)

        viewModel.loadFlyers()

        coVerify { flyerManager.listFlyers() }
        assertEquals(2, viewModel.uiState.value.flyers.size)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadFlyers failure sets errorMessage and emits snackbar`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            val error = RuntimeException("Network error")
            coEvery { flyerManager.listFlyers() } returns Result.failure(error)

            viewModel.loadFlyers()

            coVerify { flyerManager.listFlyers() }
            assertEquals("Network error", viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.isLoading)

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `refresh calls loadFlyers`() = runCoroutineTest {
        val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
        coEvery { flyerManager.listFlyers() } returns Result.success(paginated)

        viewModel.refresh()

        coVerify { flyerManager.listFlyers() }
    }

    @Test
    fun `loadFlyers success with empty list updates UIState`() = runCoroutineTest {
        val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
        coEvery { flyerManager.listFlyers() } returns Result.success(paginated)

        viewModel.loadFlyers()

        assertTrue(viewModel.uiState.value.flyers.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onFlyerSelected emits NavigateToScreen event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            val flyerId = FlyerId("flyer-123")

            viewModel.onFlyerSelected(flyerId)

            val event = turbine.awaitItem()
            assertEquals(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerDetailDestination("flyer-123"),
                ),
                event,
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    private fun makeFlyerModel(id: String) = FlyerModel(
        id = FlyerId(id),
        title = "Flyer $id",
        description = "Description $id",
        fileUrl = null,
        status = FlyerStatus.APPROVED,
        expiresAt = null,
        uploaderId = UserId("user-1"),
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z",
    )
}
