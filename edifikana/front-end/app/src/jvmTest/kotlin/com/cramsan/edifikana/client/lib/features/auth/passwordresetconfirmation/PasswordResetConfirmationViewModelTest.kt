package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import app.cash.turbine.turbineScope
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
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
import edifikana_lib.error_message_unexpected_error
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Test the [PasswordResetConfirmationViewModel] class.
 */
class PasswordResetConfirmationViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var stringProvider: StringProvider
    private lateinit var viewModel: PasswordResetConfirmationViewModel
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        stringProvider = mockk()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = PasswordResetConfirmationViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            authManager = authManager,
            stringProvider = stringProvider,
        )
    }

    /**
     * Test the [PasswordResetConfirmationViewModel.initialize] method transitions to Stable with the given email.
     */
    @Test
    fun `test initialize transitions to Stable with email`() = runCoroutineTest {
        // Act
        viewModel.initialize("user@example.com")

        // Assert
        assertIs<PasswordResetConfirmationUIState.Stable>(viewModel.uiState.value)
        assertEquals("user@example.com", viewModel.uiState.value.email)
    }

    /**
     * Test the [PasswordResetConfirmationViewModel.resend] method calls sendPasswordReset on AuthManager.
     */
    @Test
    fun `test resend calls sendPasswordReset on AuthManager`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.sendPasswordReset("user@example.com") } returns Result.success(Unit)

        viewModel.initialize("user@example.com")

        turbineScope {
            val turbine = viewModel.events.testIn(backgroundScope)

            // Act
            viewModel.resend()

            // Assert
            coVerify { authManager.sendPasswordReset("user@example.com") }
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [PasswordResetConfirmationViewModel.resend] method transitions to Error on failure.
     */
    @Test
    fun `test resend failure transitions to Error state`() = runCoroutineTest {
        // Arrange
        val errorMessage = "There was an unexpected error."
        coEvery { authManager.sendPasswordReset(any()) } returns Result.failure(Exception("network error"))
        coEvery { stringProvider.getString(Res.string.error_message_unexpected_error) } returns errorMessage

        viewModel.initialize("user@example.com")

        turbineScope {
            val turbine = viewModel.events.testIn(backgroundScope)

            // Act
            viewModel.resend()

            // Assert
            val state = viewModel.uiState.value
            assertIs<PasswordResetConfirmationUIState.Error>(state)
            assertEquals(listOf(errorMessage), state.messages)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [PasswordResetConfirmationViewModel.resend] method returns to Stable on success.
     */
    @Test
    fun `test resend success returns to Stable state`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.sendPasswordReset(any()) } returns Result.success(Unit)

        viewModel.initialize("user@example.com")

        turbineScope {
            val turbine = viewModel.events.testIn(backgroundScope)

            // Act
            viewModel.resend()

            // Assert
            assertIs<PasswordResetConfirmationUIState.Stable>(viewModel.uiState.value)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [PasswordResetConfirmationViewModel.navigateBackToSignIn] method emits
     * NavigateToScreen with SignInDestination and clearTop.
     */
    @Test
    fun `test navigateBackToSignIn emits NavigateToScreen SignInDestination with clearTop`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.navigateBackToSignIn()

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(
                    destination = AuthDestination.SignInDestination,
                    clearTop = true,
                ),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
