package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import app.cash.turbine.turbineScope
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.client.lib.models.FlyerModel
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FlyerDetailViewModelTest : CoroutineTest() {

    private lateinit var flyerManager: FlyerManager
    private lateinit var viewModel: FlyerDetailViewModel
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
        viewModel = FlyerDetailViewModel(
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
        assertEquals(FlyerDetailUIState.Initial, viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.flyer)
    }

    @Test
    fun `loadFlyer success updates UIState with flyer`() = runCoroutineTest {
        val flyer = makeFlyerModel("flyer-abc")
        coEvery { flyerManager.getFlyer(FlyerId("flyer-abc")) } returns Result.success(flyer)

        viewModel.loadFlyer("flyer-abc")

        coVerify { flyerManager.getFlyer(FlyerId("flyer-abc")) }
        assertNotNull(viewModel.uiState.value.flyer)
        assertEquals("flyer-abc", viewModel.uiState.value.flyer?.id?.flyerId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadFlyer returns null emits snackbar`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            coEvery { flyerManager.getFlyer(FlyerId("missing")) } returns Result.success(null)

            viewModel.loadFlyer("missing")

            assertNull(viewModel.uiState.value.flyer)
            assertFalse(viewModel.uiState.value.isLoading)

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `loadFlyer failure emits snackbar`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            coEvery { flyerManager.getFlyer(any()) } returns Result.failure(RuntimeException("Error"))

            viewModel.loadFlyer("flyer-xyz")

            assertNull(viewModel.uiState.value.flyer)
            assertFalse(viewModel.uiState.value.isLoading)

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
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
        title = "Flyer $id",
        description = "Description",
        fileUrl = "https://example.com/file.jpg",
        status = FlyerStatus.APPROVED,
        expiresAt = null,
        uploaderId = UserId("user-1"),
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z",
    )
}
