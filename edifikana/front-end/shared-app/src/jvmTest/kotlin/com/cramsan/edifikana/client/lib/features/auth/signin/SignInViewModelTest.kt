package com.cramsan.edifikana.client.lib.features.auth.signin

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.SharedFlowApplicationReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.github.jan.supabase.auth.exception.AuthRestException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/**
 * Test the [SignInViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class SignInViewModelTest : TestBase() {
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: SignInViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    /**
     * Setup the test.
     */
    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        applicationEventReceiver = SharedFlowApplicationReceiver()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = SignInViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
            ),
            authManager
        )
    }

    /**
     * Test the [SignInViewModel.initializePage] method
     */
    @Test
    fun `test initializePage has expected UI state`() = runBlockingTest {
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
    fun `test onUsernameValueChange updates username value`(username: String) = runBlockingTest {
        // Act
        viewModel.onUsernameValueChange(username)

        // Assert
        assertEquals(username, viewModel.uiState.value.email)
    }

    /**
     * Test the [SignInViewModel.onPasswordValueChange] method
     */
    @Test
    fun `test onPasswordValueChange updates password value`() = runBlockingTest {
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
    fun `test SignIn success`() = runBlockingTest {
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
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToActivity(
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
     * TODO: Fix logging capabilities to be able to figure out why this test fails on CI but no on local
     *
     */
    @Ignore
    @Test
    fun `test SignIn fails with invalid login credentials`() = runBlockingTest {
        // Arrange
        val username = "wrongUser@email.com"
        val password = "ValidPassword123"
        val errorMessage = "Username and/or password didn't match our records."
        coEvery {
            authManager.signIn(
                username,
                password,
            )
        } returns Result.failure(mockk<AuthRestException>())

        viewModel.onUsernameValueChange(username)
        viewModel.onPasswordValueChange(password)

        // Act
        val verificationJob = launch {
            viewModel.events.test {
                advanceUntilIdleAndAwaitComplete(this)
            }
        }
        viewModel.signIn()

        // Assert & Verify
        coVerify { authManager.signIn(username, password) }
        assertEquals(errorMessage, viewModel.uiState.value.errorMessage)
        verificationJob.join()
    }

    /**
     * Test the [SignInViewModel.signIn] method fails with unexpected error
     * TODO: Fix logging capabilities to be able to figure out why this test fails on CI but no on local
     *
     */
    @Ignore
    @Test
    fun `test SignIn fails for unexpected reason`() = runBlockingTest {
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

        viewModel.onUsernameValueChange(username)
        viewModel.onPasswordValueChange(password)

        // Act
        val verificationJob = launch {
            viewModel.events.test {
                advanceUntilIdleAndAwaitComplete(this)
            }
        }
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
    fun `test navigateToSignUpPage calls expected event`() = runBlockingTest {
        // Arrange
        coEvery { authManager.signIn(any(), any()) } returns Result.success(mockk())

        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToScreen(AuthRouteDestination.SignUpDestination),
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
    fun `test navigateToDebugPage calls expected event`() = runBlockingTest {
        // Arrange
        coEvery { authManager.signIn(any(), any()) } returns Result.success(mockk())

        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.DebugRouteDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToDebugPage()

        // Assert & Verify
        verificationJob.join()
    }
}