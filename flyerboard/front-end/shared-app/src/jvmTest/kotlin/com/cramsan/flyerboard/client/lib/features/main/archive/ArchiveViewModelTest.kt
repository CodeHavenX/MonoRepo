package com.cramsan.flyerboard.client.lib.features.main.archive

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

class ArchiveViewModelTest : CoroutineTest() {

    private lateinit var flyerManager: FlyerManager
    private lateinit var viewModel: ArchiveViewModel
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
        viewModel = ArchiveViewModel(
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
        assertEquals(ArchiveUIState.Initial, viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.flyers.isEmpty())
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadFlyers success updates UIState with archived flyers`() = runCoroutineTest {
        val flyers = listOf(makeFlyerModel("a1"), makeFlyerModel("a2"), makeFlyerModel("a3"))
        val paginated = PaginatedFlyerModel(flyers = flyers, total = 3, offset = 0, limit = 20)
        coEvery { flyerManager.listArchived() } returns Result.success(paginated)

        viewModel.loadFlyers()

        coVerify { flyerManager.listArchived() }
        assertEquals(3, viewModel.uiState.value.flyers.size)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadFlyers failure sets errorMessage and emits snackbar`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            coEvery { flyerManager.listArchived() } returns Result.failure(RuntimeException("Server error"))

            viewModel.loadFlyers()

            coVerify { flyerManager.listArchived() }
            assertEquals("Server error", viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.isLoading)

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `refresh calls loadFlyers`() = runCoroutineTest {
        val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
        coEvery { flyerManager.listArchived() } returns Result.success(paginated)

        viewModel.refresh()

        coVerify { flyerManager.listArchived() }
    }

    @Test
    fun `onFlyerSelected emits NavigateToScreen for detail`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            val flyerId = FlyerId("archived-flyer-1")

            viewModel.onFlyerSelected(flyerId)

            assertEquals(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerDetailDestination("archived-flyer-1"),
                ),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
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
        title = "Archived Flyer $id",
        description = "Description",
        fileUrl = "https://example.com/file-$id.jpg",
        status = FlyerStatus.ARCHIVED,
        expiresAt = "2023-12-31T00:00:00Z",
        uploaderId = UserId("user-1"),
        createdAt = "2023-01-01T00:00:00Z",
        updatedAt = "2023-12-31T00:00:00Z",
    )
}
