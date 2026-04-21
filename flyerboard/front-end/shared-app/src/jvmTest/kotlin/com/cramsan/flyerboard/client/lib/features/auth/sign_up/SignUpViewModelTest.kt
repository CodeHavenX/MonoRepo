package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import app.cash.turbine.turbineScope
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

class SignUpViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var viewModel: SignUpViewModel
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
        viewModel = SignUpViewModel(
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
        assertEquals(SignUpUIState.Initial, viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("", viewModel.uiState.value.email)
        assertEquals("", viewModel.uiState.value.password)
    }

    @Test
    fun `onEmailChanged updates email in UIState`() = runCoroutineTest {
        viewModel.onEmailChanged("new@example.com")

        assertEquals("new@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `onPasswordChanged updates password in UIState`() = runCoroutineTest {
        viewModel.onPasswordChanged("mypassword")

        assertEquals("mypassword", viewModel.uiState.value.password)
    }

    @Test
    fun `signUp success navigates to main nav graph`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            coEvery { authManager.signUp("new@example.com", "pass123") } returns Result.success(Unit)

            viewModel.onEmailChanged("new@example.com")
            viewModel.onPasswordChanged("pass123")
            viewModel.signUp()

            coVerify { authManager.signUp("new@example.com", "pass123") }
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
    fun `signUp failure emits snackbar event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)
            coEvery { authManager.signUp(any(), any()) } returns Result.failure(RuntimeException("Email taken"))

            viewModel.onEmailChanged("taken@example.com")
            viewModel.onPasswordChanged("pass123")
            viewModel.signUp()

            coVerify { authManager.signUp("taken@example.com", "pass123") }
            assertFalse(viewModel.uiState.value.isLoading)

            val event = turbine.awaitItem()
            assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToSignIn emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToSignIn()

            assertEquals(FlyerBoardWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
