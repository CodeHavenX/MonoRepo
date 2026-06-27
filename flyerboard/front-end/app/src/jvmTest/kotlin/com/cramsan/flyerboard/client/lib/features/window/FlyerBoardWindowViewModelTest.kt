package com.cramsan.flyerboard.client.lib.features.window

import com.cramsan.flyerboard.client.lib.managers.AuthManager
import com.cramsan.flyerboard.client.lib.models.UserModel
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
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
    private lateinit var viewModel: FlyerBoardWindowViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>
    private lateinit var windowEventEmitterBus: EventBus<WindowEvent>
    private lateinit var delegatedEventBus: EventBus<FlyerBoardWindowDelegatedEvent>
    private lateinit var activeUserFlow: MutableStateFlow<UserModel?>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
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
        )

    private fun userModel(userId: UserId, role: UserRole) =
        UserModel(id = userId, firstName = "John", lastName = "Doe", role = role)

    @Test
    fun `initial authState is Unauthenticated`() =
        runCoroutineTest {
            viewModel = buildViewModel()

            assertEquals(AuthState.Undefined, viewModel.uiState.value.authState)
        }

    @Test
    fun `auth emits userId and Authenticated is isAdmin false for a regular user`() =
        runCoroutineTest {
            coEvery { authManager.isAuthenticated() } returns Result.success(true)
            viewModel = buildViewModel()

            activeUserFlow.emit(userModel(UserId("user-regular"), UserRole.USER))

            val state = viewModel.uiState.value.authState
            assertIs<AuthState.Authenticated>(state)
            assertEquals(false, state.isAdmin)
        }

    @Test
    fun `auth emits userId and Authenticated is isAdmin true for an admin`() =
        runCoroutineTest {
            coEvery { authManager.isAuthenticated() } returns Result.success(true)
            viewModel = buildViewModel()

            activeUserFlow.emit(userModel(UserId("user-admin"), UserRole.ADMIN))

            val state = viewModel.uiState.value.authState
            assertIs<AuthState.Authenticated>(state)
            assertEquals(true, state.isAdmin)
        }

    @Test
    fun `auth resolves to Unauthenticated after init when no user is signed in`() =
        runCoroutineTest {
            coEvery { authManager.isAuthenticated() } returns Result.success(false)
            viewModel = buildViewModel()

            assertEquals(AuthState.Unauthenticated, viewModel.uiState.value.authState)
        }

    @Test
    fun `auth emits null reverts to Unauthenticated`() =
        runCoroutineTest {
            coEvery { authManager.isAuthenticated() } returns Result.success(true)
            viewModel = buildViewModel()

            activeUserFlow.emit(userModel(UserId("user-admin"), UserRole.ADMIN))
            val authenticatedState = viewModel.uiState.value.authState
            assertIs<AuthState.Authenticated>(authenticatedState)

            activeUserFlow.emit(null)
            assertEquals(AuthState.Unauthenticated, viewModel.uiState.value.authState)
        }
}
