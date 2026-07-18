package com.cramsan.flyerboard.client.lib.features.main.my_flyers

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
import kotlin.test.assertTrue

class MyFlyersViewModelTest : CoroutineTest() {
    private lateinit var flyerManager: FlyerManager
    private lateinit var viewModel: MyFlyersViewModel
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
        viewModel =
            MyFlyersViewModel(
                dependencies =
                ViewModelDependencies(
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
    fun `initial UIState is Loading`(): Unit =
        runCoroutineTest {
            assertEquals(MyFlyersUIState.Initial, viewModel.uiState.value)
            assertTrue(viewModel.uiState.value is MyFlyersUIState.Loading)
        }

    @Test
    fun `loadFlyers success updates UIState to Content`(): Unit =
        runCoroutineTest {
            val flyers = listOf(makeFlyerModel("1", FlyerStatus.PENDING), makeFlyerModel("2", FlyerStatus.APPROVED))
            val paginated = PaginatedFlyerModel(flyers = flyers, total = 2, offset = 0, limit = 20)
            coEvery { flyerManager.listMyFlyers() } returns Result.success(paginated)

            viewModel.loadFlyers()

            coVerify { flyerManager.listMyFlyers() }
            val state = viewModel.uiState.value
            assertTrue(state is MyFlyersUIState.Content)
            assertEquals(2, state.flyers.size)
        }

    @Test
    fun `loadFlyers success with empty list transitions to Empty`(): Unit =
        runCoroutineTest {
            val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
            coEvery { flyerManager.listMyFlyers() } returns Result.success(paginated)

            viewModel.loadFlyers()

            assertTrue(viewModel.uiState.value is MyFlyersUIState.Empty)
        }

    @Test
    fun `loadFlyers failure transitions to Error and emits snackbar`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                coEvery { flyerManager.listMyFlyers() } returns Result.failure(RuntimeException("Unauthorized"))

                viewModel.loadFlyers()

                coVerify { flyerManager.listMyFlyers() }
                assertTrue(viewModel.uiState.value is MyFlyersUIState.Error)

                val event = turbine.awaitItem()
                assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `refresh calls loadFlyers`(): Unit =
        runCoroutineTest {
            val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 20)
            coEvery { flyerManager.listMyFlyers() } returns Result.success(paginated)

            viewModel.refresh()

            coVerify { flyerManager.listMyFlyers() }
        }

    @Test
    fun `onFlyerSelected emits NavigateToScreen for detail`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                val flyerId = FlyerId("my-flyer-1")

                viewModel.onFlyerSelected(flyerId)

                assertEquals(
                    FlyerBoardWindowsEvent.NavigateToScreen(
                        MainDestination.FlyerDetailDestination("my-flyer-1"),
                    ),
                    turbine.awaitItem(),
                )
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `onEditFlyer emits NavigateToScreen for edit`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                val flyerId = FlyerId("my-flyer-2")

                viewModel.onEditFlyer(flyerId)

                assertEquals(
                    FlyerBoardWindowsEvent.NavigateToScreen(
                        MainDestination.FlyerEditDestination("my-flyer-2"),
                    ),
                    turbine.awaitItem(),
                )
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `onSubmitFlyer emits NavigateToScreen for FlyerSubmitDestination`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)

                viewModel.onSubmitFlyer()

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
    fun `navigateBack emits NavigateBack event`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)

                viewModel.navigateBack()

                assertEquals(FlyerBoardWindowsEvent.NavigateBack, turbine.awaitItem())
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    private fun makeFlyerModel(id: String, status: FlyerStatus) =
        FlyerModel(
            id = FlyerId(id),
            title = "My Flyer $id",
            description = "Description",
            fileUrl = null,
            status = status,
            expiresAt = null,
            uploaderId = UserId("user-1"),
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
        )
}
