package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import app.cash.turbine.turbineScope
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.AuthManager
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
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FlyerListViewModelTest : CoroutineTest() {
    private lateinit var flyerManager: FlyerManager
    private lateinit var authManager: AuthManager
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
        authManager = mockk()
        viewModel =
            FlyerListViewModel(
                dependencies =
                ViewModelDependencies(
                    appScope = testCoroutineScope,
                    dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                    coroutineExceptionHandler = exceptionHandler,
                    windowEventReceiver = windowEventBus,
                    applicationEventReceiver = applicationEventBus,
                ),
                flyerManager = flyerManager,
                authManager = authManager,
            )
    }

    @Test
    fun `initial UIState is Loading`(): Unit =
        runCoroutineTest {
            assertEquals(FlyerListUIState.Initial, viewModel.uiState.value)
            assertIs<FlyerListUIState.Loading>(viewModel.uiState.value)
        }

    @Test
    fun `loadFlyers success updates UIState to Content`(): Unit =
        runCoroutineTest {
            val flyers = listOf(makeFlyerModel("1"), makeFlyerModel("2"))
            val paginated = PaginatedFlyerModel(flyers = flyers, total = 2, offset = 0, limit = 20)
            coEvery { flyerManager.listFlyers() } returns Result.success(paginated)

            viewModel.loadFlyers()

            coVerify { flyerManager.listFlyers() }
            val state = viewModel.uiState.value
            assertIs<FlyerListUIState.Content>(state)
            assertEquals(2, state.flyers.size)
        }

    @Test
    fun `loadFlyers success with empty list updates UIState to Empty`(): Unit =
        runCoroutineTest {
            val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
            coEvery { flyerManager.listFlyers() } returns Result.success(paginated)

            viewModel.loadFlyers()

            assertIs<FlyerListUIState.Empty>(viewModel.uiState.value)
        }

    @Test
    fun `loadFlyers failure updates UIState to Error and emits snackbar`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                val error = RuntimeException("Network error")
                coEvery { flyerManager.listFlyers() } returns Result.failure(error)

                viewModel.loadFlyers()

                coVerify { flyerManager.listFlyers() }
                val state = viewModel.uiState.value
                assertIs<FlyerListUIState.Error>(state)
                assertEquals("Network error", state.message)

                val event = turbine.awaitItem()
                assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `refresh calls loadFlyers`(): Unit =
        runCoroutineTest {
            val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
            coEvery { flyerManager.listFlyers() } returns Result.success(paginated)

            viewModel.refresh()

            coVerify { flyerManager.listFlyers() }
        }

    @Test
    fun `primaryAction emits NavigateToScreen for AuthNavGraphDestination when unauthenticated`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                coEvery { authManager.isAuthenticated() } returns Result.success(false)

                viewModel.primaryAction()

                assertEquals(
                    FlyerBoardWindowsEvent.NavigateToNavGraph(
                        FlyerBoardWindowNavGraphDestination.AuthNavGraphDestination,
                    ),
                    turbine.awaitItem(),
                )
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `primaryAction emits NavigateToScreen for FlyerSubmitDestination when authenticated`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                coEvery { authManager.isAuthenticated() } returns Result.success(true)

                viewModel.primaryAction()

                assertEquals(
                    FlyerBoardWindowsEvent.NavigateToScreen(
                        MainDestination.FlyerSubmitDestination,
                    ),
                    turbine.awaitItem(),
                )
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `onFlyerSelected emits NavigateToScreen event`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                val flyerId = FlyerId("flyer-123")

                viewModel.selectFlyer(flyerId)

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

    private fun makeFlyerModel(id: String) =
        FlyerModel(
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
