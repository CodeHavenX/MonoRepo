package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import app.cash.turbine.turbineScope
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
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
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import net.bytebuddy.matcher.ElementMatchers.any
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertInstanceOf
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
        viewModel =
            SignUpViewModel(
                dependencies =
                ViewModelDependencies(
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
    fun `initial UIState is correct`(): Unit =
        runCoroutineTest {
            assertEquals(SignUpUIState.Initial, viewModel.uiState.value)
            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals("", viewModel.uiState.value.firstName)
            assertEquals("", viewModel.uiState.value.lastName)
            assertEquals("", viewModel.uiState.value.email)
            assertEquals("", viewModel.uiState.value.password)
            assertEquals("", viewModel.uiState.value.confirmPassword)
        }

    @Test
    fun `onEmailChanged updates email in UIState`(): Unit =
        runCoroutineTest {
            viewModel.onEmailChanged("new@example.com")

            assertEquals("new@example.com", viewModel.uiState.value.email)
        }

    @Test
    fun `onPasswordChanged updates password in UIState`(): Unit =
        runCoroutineTest {
            viewModel.onPasswordChanged("mypassword")

            assertEquals("mypassword", viewModel.uiState.value.password)
        }

    @Test
    fun `onConfirmPasswordChanged updates confirmPassword in UIState`(): Unit =
        runCoroutineTest {
            viewModel.onConfirmPasswordChanged("mypassword")

            assertEquals("mypassword", viewModel.uiState.value.confirmPassword)
        }

    @Test
    fun `signUp success calls createUser then navigates to main nav graph`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                coEvery { authManager.signUp("jane@example.com", "pass123", "Jane", "Doe") } returns Result.success(Unit)

                viewModel.onFirstNameChanged("Jane")
                viewModel.onLastNameChanged("Doe")
                viewModel.onEmailChanged("jane@example.com")
                viewModel.onPasswordChanged("pass123")
                viewModel.onConfirmPasswordChanged("pass123")
                viewModel.signUp()

                coVerify { authManager.signUp("jane@example.com", "pass123", "Jane", "Doe") }
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
    fun `createUser failure after successful signUp should block navigation`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                coEvery { authManager.signUp("jane@example.com", "pass123", "Jane", "Doe") } returns Result.failure(
                    RuntimeException("Unexpected error")
                )

                viewModel.onFirstNameChanged("Jane")
                viewModel.onLastNameChanged("Doe")
                viewModel.onEmailChanged("jane@example.com")
                viewModel.onPasswordChanged("pass123")
                viewModel.onConfirmPasswordChanged("pass123")
                viewModel.signUp()

                val event = turbine.awaitItem()

                assertFalse(viewModel.uiState.value.isLoading)
                assertInstanceOf<FlyerBoardWindowsEvent.ShowSnackbar>(event)
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `signUp failure emits snackbar event`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                coEvery { authManager.signUp(any(), any(), any(), any()) } returns Result.failure(RuntimeException("Email taken"))

                viewModel.onFirstNameChanged("Jane")
                viewModel.onLastNameChanged("Doe")
                viewModel.onEmailChanged("taken@example.com")
                viewModel.onPasswordChanged("pass123")
                viewModel.onConfirmPasswordChanged("pass123")
                viewModel.signUp()

                coVerify { authManager.signUp("taken@example.com", "pass123","Jane", "Doe") }
                assertFalse(viewModel.uiState.value.isLoading)

                val event = turbine.awaitItem()
                assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `signUp with blank email emits snackbar and does not call authManager`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)

                viewModel.onPasswordChanged("pass123")
                viewModel.onConfirmPasswordChanged("pass123")
                viewModel.signUp()

                val event = turbine.awaitItem()
                val snackbar = event as FlyerBoardWindowsEvent.ShowSnackbar
                assertEquals(SignUpViewModel.MSG_FILL_ALL_FIELDS, snackbar.message)
                assertFalse(viewModel.uiState.value.isLoading)
                coVerify(exactly = 0) { authManager.signUp(any(), any(), any(), any()) }
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `signUp with mismatched passwords emits snackbar and does not call authManager`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)

                viewModel.onFirstNameChanged("Jane")
                viewModel.onLastNameChanged("Doe")
                viewModel.onEmailChanged("user@example.com")
                viewModel.onPasswordChanged("pass123")
                viewModel.onConfirmPasswordChanged("different")
                viewModel.signUp()

                val event = turbine.awaitItem()
                val snackbar = event as FlyerBoardWindowsEvent.ShowSnackbar
                assertEquals(SignUpViewModel.MSG_PASSWORD_MISMATCH, snackbar.message)
                assertFalse(viewModel.uiState.value.isLoading)
                coVerify(exactly = 0) { authManager.signUp(any(), any(), any(), any()) }
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `navigateToSignIn emits NavigateBack event`(): Unit =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)

                viewModel.navigateToSignIn()

                assertEquals(FlyerBoardWindowsEvent.NavigateBack, turbine.awaitItem())
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }
}
