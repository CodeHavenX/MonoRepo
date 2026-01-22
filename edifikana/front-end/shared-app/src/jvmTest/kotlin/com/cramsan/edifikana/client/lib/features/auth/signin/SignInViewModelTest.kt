package com.cramsan.edifikana.client.lib.features.auth.signin

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
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
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import edifikana_lib.Res
import edifikana_lib.error_message_invalid_credentials
import edifikana_lib.error_message_unexpected_error
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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
    private lateinit var organizationManager: OrganizationManager
    private lateinit var preferencesManager: PreferencesManager


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
        organizationManager = mockk()
        preferencesManager = mockk()
        viewModel = SignInViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            auth = authManager,
            organizationManager = organizationManager,
            stringProvider = stringProvider,
            preferencesManager = preferencesManager,
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
     * Test the [SignInViewModel.changeUsernameValue] method update to either email or phone number username
     */
    @ParameterizedTest
    @CsvSource("test@example.com", "5456879123")
    fun `test onUsernameValueChange updates username value`(username: String) = runCoroutineTest {
        // Act
        viewModel.changeUsernameValue(username)

        // Assert
        assertEquals(username, viewModel.uiState.value.email)
    }

    /**
     * Test the [SignInViewModel.changePasswordValue] method
     */
    @Test
    fun `test onPasswordValueChange updates password value`() = runCoroutineTest {
        // Arrange
        val password = "Password123"

        // Act
        viewModel.changePasswordValue(password)

        // Assert
        assertEquals(password, viewModel.uiState.value.password)
    }

    /**
     * Test the [SignInViewModel.signInWithPassword] method succeeds as expected
     */
    @Test
    fun `test SignIn success`() = runCoroutineTest { turbineScope {
        // Arrange
        val username = "real@email.com"
        val password = "Password123"
        val turbine = windowEventBus.events.testIn(backgroundScope)

        coEvery {
            authManager.signInWithPassword(
                username,
                password,
            )
        } returns Result.success(mockk())
        coEvery {
            organizationManager.getOrganizations()
        } returns Result.success(listOf(mockk()))

        // Act
        viewModel.changeUsernameValue(username)
        viewModel.changePasswordValue(password)
        viewModel.signInWithPassword()

        // Verify
        coVerify { authManager.signInWithPassword(username, password) }
        assertEquals(
            EdifikanaWindowsEvent.NavigateToNavGraph(
                EdifikanaNavGraphDestination.HomeNavGraphDestination,
                clearTop = true,
            ),
            turbine.awaitItem()
        )
    } }

    /**
     * Test the [SignInViewModel.signInWithPassword] method fails with invalid login credentials
     */
    @Test
    fun `test SignIn fails with invalid login credentials`() = runCoroutineTest {
        // Arrange
        val username = "wrongUser@email.com"
        val password = "ValidPassword123"
        val errorMessage = "Invalid login credentials. Please check your username and password and try again."
        coEvery {
            authManager.signInWithPassword(
                username,
                password,
            )
        } returns Result.failure(mockk<ClientRequestExceptions.UnauthorizedException>())
        coEvery { stringProvider.getString(Res.string.error_message_invalid_credentials) } returns errorMessage

        viewModel.changeUsernameValue(username)
        viewModel.changePasswordValue(password)

        // Act
        val verificationJob = launch {
            viewModel.events.test {
                advanceUntilIdleAndAwaitComplete(this)
            }
        }
        viewModel.changeUsernameValue(username)
        viewModel.changePasswordValue(password)
        viewModel.signInWithPassword()

        // Assert & Verify
        coVerify { authManager.signInWithPassword(username, password) }
        assertEquals(listOf(errorMessage).toString(), viewModel.uiState.value.errorMessages.toString())
        verificationJob.join()
    }

    /**
     * Test the [SignInViewModel.signInWithPassword] method fails with unexpected error
     *
     */
    @Test
    fun `test SignIn fails for unexpected reason`() = runCoroutineTest {
        // Arrange
        val username = "wrongUser@email.com"
        val password = "ValidPassword123"
        val errorMessage = "There was an unexpected error."
        coEvery {
            authManager.signInWithPassword(
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
        viewModel.changeUsernameValue(username)
        viewModel.changePasswordValue(password)
        viewModel.signInWithPassword()

        // Assert & Verify
        coVerify { authManager.signInWithPassword(username, password) }
        assertEquals(listOf(errorMessage).toString(), viewModel.uiState.value.errorMessages.toString())
        verificationJob.join()
    }

    /**
     * Test the [SignInViewModel.navigateToSignUpPage] method completes expected event
     */
    @Test
    fun `test navigateToSignUpPage calls expected event`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.signInWithPassword(any(), any()) } returns Result.success(mockk())

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.SignUpDestination),
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
        coEvery { authManager.signInWithPassword(any(), any()) } returns Result.success(mockk())

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.DebugNavGraphDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToDebugPage()

        // Assert & Verify
        verificationJob.join()
    }

    /**
     * Test the [SignInViewModel.signInWithOtp] method emits navigation event with trimmed email
     */
    @Test
    fun `test signInWithOtp emits navigation event with trimmed email`() = runCoroutineTest {
        // Arrange
        val email = " test@email.com "
        viewModel.changeUsernameValue(email)
        coEvery { authManager.checkUserExists(email.trim()) } returns Result.success(true)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        AuthDestination.ValidationDestination(
                            email.trim(),
                            accountCreationFlow = false,
                        )
                    ),
                    awaitItem()
                )
            }
        }
        viewModel.signInWithOtp()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test signInWithOtp redirects to SignUp page when email is not registered`() = runCoroutineTest {
        // Arrange
        val email = "unregistered@email.com"
        viewModel.changeUsernameValue(email)
        coEvery { authManager.checkUserExists(email.trim()) } returns Result.success(false)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.SignUpDestination),
                    awaitItem()
                )
            }
        }
        viewModel.signInWithOtp()

        // Assert & Verify
        coVerify { authManager.checkUserExists(email.trim()) }
        verificationJob.join()
    }
}