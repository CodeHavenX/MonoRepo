package com.cramsan.flyerboard.client.lib.features.window

import com.cramsan.flyerboard.client.lib.managers.AuthManager
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.client.lib.models.PaginatedFlyerModel
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
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FlyerBoardWindowViewModelTest : CoroutineTest() {
    private lateinit var authManager: AuthManager
    private lateinit var flyerManager: FlyerManager
    private lateinit var viewModel: FlyerBoardWindowViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>
    private lateinit var windowEventEmitterBus: EventBus<WindowEvent>
    private lateinit var delegatedEventBus: EventBus<FlyerBoardWindowDelegatedEvent>
    private lateinit var activeUserFlow: MutableStateFlow<UserId?>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        flyerManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        windowEventBus = EventBus()
        applicationEventBus = EventBus()
        windowEventEmitterBus = EventBus()
        delegatedEventBus = EventBus()
        activeUserFlow = MutableStateFlow(null)
        every { authManager.activeUser() } returns activeUserFlow
    }

    private fun buildViewModel(): FlyerBoardWindowViewModel =
        FlyerBoardWindowViewModel(
            dependencies =
            ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            windowEventEmitter = windowEventEmitterBus,
            delegatedEvents = delegatedEventBus,
            authManager = authManager,
            flyerManager = flyerManager,
        )

    @Test
    fun `initial authState is Unauthenticated`() =
        runCoroutineTest {
            viewModel = buildViewModel()

            assertEquals(AuthState.Unauthenticated, viewModel.uiState.value.authState)
        }

    @Test
    fun `auth emits userId and listPendingFlyers succeeds sets Authenticated isAdmin true`() =
        runCoroutineTest {
            val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 1)
            coEvery { flyerManager.listPendingFlyers(any(), any()) } returns Result.success(paginated)
            viewModel = buildViewModel()

            activeUserFlow.emit(UserId("user-admin"))

            val state = viewModel.uiState.value.authState
            assertIs<AuthState.Authenticated>(state)
            assertEquals(true, state.isAdmin)
        }

    @Test
    fun `auth emits userId and listPendingFlyers fails sets Authenticated isAdmin false`() =
        runCoroutineTest {
            coEvery {
                flyerManager.listPendingFlyers(
                    any(),
                    any(),
                )
            } returns Result.failure(RuntimeException("Forbidden"))
            viewModel = buildViewModel()

            activeUserFlow.emit(UserId("user-regular"))

            val state = viewModel.uiState.value.authState
            assertIs<AuthState.Authenticated>(state)
            assertEquals(false, state.isAdmin)
        }

    @Test
    fun `auth emits null reverts to Unauthenticated`() =
        runCoroutineTest {
            val paginated = PaginatedFlyerModel(flyers = emptyList(), total = 0, offset = 0, limit = 1)
            coEvery { flyerManager.listPendingFlyers(any(), any()) } returns Result.success(paginated)
            viewModel = buildViewModel()

            activeUserFlow.emit(UserId("user-admin"))
            val authenticatedState = viewModel.uiState.value.authState
            assertIs<AuthState.Authenticated>(authenticatedState)

            activeUserFlow.emit(null)
            assertEquals(AuthState.Unauthenticated, viewModel.uiState.value.authState)
        }
}
