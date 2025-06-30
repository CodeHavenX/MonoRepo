package com.cramsan.edifikana.client.lib.features.auth.signin

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import edifikana_lib.Res
import edifikana_lib.error_message_invalid_credentials
import edifikana_lib.error_message_unexpected_error
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test the [SignInViewModel] class.
 */
@Suppress("UNCHECKED_CAST")
class SignInViewModelTest : CoroutineTest() {
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: SignInViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var stringProvider: StringProvider

    /**
     * Setup the test.
     */
    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        stringProvider = mockk()
        viewModel = SignInViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            auth = authManager,
            stringProvider = stringProvider,
        )
    }

    /**
     * Test the [SignInViewModel.initializePage] method
     */
    @Test
    fun `test initializePage has expected UI state`() = runCoroutineTest {
        // ACT
        viewModel.initializePage()

        // Assert
        assertEquals(SignInUIState.Initial, viewModel.uiState.value)
    }

    /**
     * Test the [SignInViewModel.onUsernameValueChange] method update to either email or phone number username
     */
    @ParameterizedTest
    @CsvSource("test@example.com", "5456879123")
    fun `test onUsernameValueChange updates username value`(username: String) = runCoroutineTest {
        // Act
        viewModel.onUsernameValueChange(username)

        // Assert
        assertEquals(username, viewModel.uiState.value.email)
    }

    /**
     * Test the [SignInViewModel.onPasswordValueChange] method
     */
    @Test
    fun `test onPasswordValueChange updates password value`() = runCoroutineTest {
        // Arrange
        val password = "Password123"

        // Act
        viewModel.onPasswordValueChange(password)

        // Assert
        assertEquals(password, viewModel.uiState.value.password)
    }

    /**
     * Test the [SignInViewModel.signIn] method succeeds as expected
     */
    @Test
    fun `test SignIn success`() = runCoroutineTest {
        // Arrange
        val username = "real@email.com"
        val password = "Password123"

        coEvery {
            authManager.signIn(
                username,
                password,
            )
        } returns Result.success(mockk())

        viewModel.onUsernameValueChange(username)
        viewModel.onPasswordValueChange(password)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToActivity(
                        ActivityRouteDestination.ManagementRouteDestination,
                        clearTop = true,
                    ),
                    awaitItem()
                )
            }
        }
        viewModel.signIn()

        // Verify
        coVerify { authManager.signIn(username, password) }
        verificationJob.join()
    }

    /**
     * Test the [SignInViewModel.signIn] method fails with invalid login credentials
     */
    @Test
    fun `test SignIn fails with invalid login credentials`() = runCoroutineTest {
        // Arrange
        val username = "wrongUser@email.com"
        val password = "ValidPassword123"
        val errorMessage = "Invalid login credentials. Please check your username and password and try again."
        coEvery {
            authManager.signIn(
                username,
                password,
            )
        } returns Result.failure(mockk<ClientRequestExceptions.UnauthorizedException>())
        coEvery { stringProvider.getString(Res.string.error_message_invalid_credentials) } returns errorMessage

        viewModel.onUsernameValueChange(username)
        viewModel.onPasswordValueChange(password)

        // Act
        val verificationJob = launch {
            viewModel.events.test {
                advanceUntilIdleAndAwaitComplete(this)
            }
        }
        viewModel.onUsernameValueChange(username)
        viewModel.onPasswordValueChange(password)
        viewModel.signIn()

        // Assert & Verify
        coVerify { authManager.signIn(username, password) }
        assertEquals(errorMessage, viewModel.uiState.value.errorMessage)
        verificationJob.join()
    }

    /**
     * Test the [SignInViewModel.signIn] method fails with unexpected error
     *
     */
    @Test
    fun `test SignIn fails for unexpected reason`() = runCoroutineTest {
        // Arrange
        val username = "wrongUser@email.com"
        val password = "ValidPassword123"
        val errorMessage = "There was an unexpected error."
        coEvery {
            authManager.signIn(
                username,
                password,
            )
        } returns Result.failure(mockk<Exception>())
        coEvery { stringProvider.getString(Res.string.error_message_unexpected_error) } returns errorMessage

        // Act
        val verificationJob = launch {
            viewModel.events.test {
                advanceUntilIdleAndAwaitComplete(this)
            }
        }
        viewModel.onUsernameValueChange(username)
        viewModel.onPasswordValueChange(password)
        viewModel.signIn()

        // Assert & Verify
        coVerify { authManager.signIn(username, password) }
        assertEquals(errorMessage, viewModel.uiState.value.errorMessage)
        verificationJob.join()
    }

    /**
     * Test the [SignInViewModel.navigateToSignUpPage] method completes expected event
     */
    @Test
    fun `test navigateToSignUpPage calls expected event`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.signIn(any(), any()) } returns Result.success(mockk())

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(AuthRouteDestination.SignUpDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToSignUpPage()

        // Assert & Verify
        verificationJob.join()
    }

    /**
     * Test the [SignInViewModel.navigateToDebugPage] method completes expected event
     */
    @Test
    fun `test navigateToDebugPage calls expected event`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.signIn(any(), any()) } returns Result.success(mockk())

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToActivity(ActivityRouteDestination.DebugRouteDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToDebugPage()

        // Assert & Verify
        verificationJob.join()
    }
}