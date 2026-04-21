package com.cramsan.flyerboard.client.lib.features.auth.sign_in

import app.cash.turbine.turbineScope
import com.cramsan.flyerboard.client.lib.features.auth.AuthDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.AuthManager
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
import kotlin.test.assertTrue

class SignInViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var viewModel: SignInViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = SignInViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            authManager = authManager,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(SignInUIState.Initial, viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("", viewModel.uiState.value.email)
        assertEquals("", viewModel.uiState.value.password)
    }

    @Test
    fun `onEmailChanged updates email in UIState`() = runCoroutineTest {
        viewModel.onEmailChanged("test@example.com")

        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `onPasswordChanged updates password in UIState`() = runCoroutineTest {
        viewModel.onPasswordChanged("secret123")

        assertEquals("secret123", viewModel.uiState.value.password)
    }

    @Test
    fun `signIn success navigates to main nav graph`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            coEvery { authManager.signIn("user@test.com", "pass") } returns Result.success(Unit)

            viewModel.onEmailChanged("user@test.com")
            viewModel.onPasswordChanged("pass")
            viewModel.signIn()

            coVerify { authManager.signIn("user@test.com", "pass") }
            assertFalse(viewModel.uiState.value.isLoading)

            val event = turbine.awaitItem()
            assertEquals(
                FlyerBoardWindowsEvent.NavigateToNavGraph(
                    destination = FlyerBoardWindowNavGraphDestination.MainNavGraphDestination,
                    clearStack = true,
                ),
                event,
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `signIn failure emits snackbar event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            coEvery { authManager.signIn(any(), any()) } returns Result.failure(RuntimeException("Bad credentials"))

            viewModel.onEmailChanged("wrong@test.com")
            viewModel.onPasswordChanged("wrong")
            viewModel.signIn()

            coVerify { authManager.signIn("wrong@test.com", "wrong") }
            assertFalse(viewModel.uiState.value.isLoading)

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToSignUp emits NavigateToScreen for SignUpDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToSignUp()

            assertEquals(
                FlyerBoardWindowsEvent.NavigateToScreen(AuthDestination.SignUpDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
