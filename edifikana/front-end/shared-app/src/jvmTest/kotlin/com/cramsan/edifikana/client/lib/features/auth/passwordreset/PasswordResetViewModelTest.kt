package com.cramsan.edifikana.client.lib.features.auth.passwordreset

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
import kotlin.test.assertNull

/**
 * Test the [PasswordResetViewModel] class.
 */
class PasswordResetViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var stringProvider: StringProvider
    private lateinit var viewModel: PasswordResetViewModel
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
        viewModel = PasswordResetViewModel(
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
     * Test the [PasswordResetViewModel.initialize] method pre-fills email from destination.
     */
    @Test
    fun `test initialize pre-fills email from destination`() = runCoroutineTest {
        // Act
        viewModel.initialize("prefill@example.com")

        // Assert
        assertIs<PasswordResetUIState.Stable>(viewModel.uiState.value)
        assertEquals("prefill@example.com", viewModel.uiState.value.email)
    }

    /**
     * Test the [PasswordResetViewModel.initialize] method with blank email leaves state unchanged.
     */
    @Test
    fun `test initialize with blank email leaves state unchanged`() = runCoroutineTest {
        // Act
        viewModel.initialize("")

        // Assert
        assertEquals(PasswordResetUIState.Initial, viewModel.uiState.value)
    }

    /**
     * Test the [PasswordResetViewModel.changeEmailValue] method transitions to Stable with updated email.
     */
    @Test
    fun `test changeEmailValue transitions to Stable with updated email`() = runCoroutineTest {
        // Arrange
        val email = "test@example.com"

        // Act
        viewModel.changeEmailValue(email)

        // Assert
        assertIs<PasswordResetUIState.Stable>(viewModel.uiState.value)
        assertEquals(email, viewModel.uiState.value.email)
    }

    /**
     * Test the [PasswordResetViewModel.sendPasswordReset] method navigates to confirmation on success.
     */
    @Test
    fun `test sendPasswordReset success navigates to confirmation screen`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.sendPasswordReset("user@example.com") } returns Result.success(Unit)

        viewModel.changeEmailValue("user@example.com")

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.sendPasswordReset()

            // Assert
            coVerify { authManager.sendPasswordReset("user@example.com") }
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.PasswordResetConfirmationDestination(userEmail = "user@example.com"),
                ),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [PasswordResetViewModel.sendPasswordReset] method trims whitespace from email before sending.
     */
    @Test
    fun `test sendPasswordReset trims email before sending`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.sendPasswordReset("user@example.com") } returns Result.success(Unit)

        viewModel.changeEmailValue("  user@example.com  ")

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.sendPasswordReset()

            // Assert
            coVerify { authManager.sendPasswordReset("user@example.com") }
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.PasswordResetConfirmationDestination(userEmail = "user@example.com"),
                ),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [PasswordResetViewModel.sendPasswordReset] method transitions to Error for an invalid email
     * and does not call the manager.
     */
    @Test
    fun `test sendPasswordReset with invalid email shows error and does not call manager`() = runCoroutineTest {
        // Arrange
        viewModel.changeEmailValue("not-an-email")

        // Act
        viewModel.sendPasswordReset()

        // Assert
        val state = viewModel.uiState.value
        assertIs<PasswordResetUIState.Error>(state)
        assertEquals(true, state.messages.isNotEmpty())
        coVerify(exactly = 0) { authManager.sendPasswordReset(any()) }
    }

    /**
     * Test the [PasswordResetViewModel.sendPasswordReset] method transitions to Error on manager failure.
     */
    @Test
    fun `test sendPasswordReset failure transitions to Error state`() = runCoroutineTest {
        // Arrange
        val errorMessage = "There was an unexpected error."
        coEvery { authManager.sendPasswordReset(any()) } returns Result.failure(Exception("network error"))
        coEvery { stringProvider.getString(Res.string.error_message_unexpected_error) } returns errorMessage

        viewModel.changeEmailValue("user@example.com")

        turbineScope {
            val turbine = viewModel.events.testIn(backgroundScope)

            // Act
            viewModel.sendPasswordReset()

            // Assert
            val state = viewModel.uiState.value
            assertIs<PasswordResetUIState.Error>(state)
            assertEquals(listOf(errorMessage), state.messages)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [PasswordResetViewModel.sendPasswordReset] method clears errors on a subsequent successful attempt.
     */
    @Test
    fun `test sendPasswordReset clears Error state on successful retry`() = runCoroutineTest {
        // Arrange — first attempt fails
        val errorMessage = "There was an unexpected error."
        coEvery { authManager.sendPasswordReset(any()) } returns Result.failure(Exception())
        coEvery { stringProvider.getString(Res.string.error_message_unexpected_error) } returns errorMessage

        viewModel.changeEmailValue("user@example.com")
        viewModel.sendPasswordReset()
        assertIs<PasswordResetUIState.Error>(viewModel.uiState.value)

        // Arrange — second attempt succeeds
        coEvery { authManager.sendPasswordReset(any()) } returns Result.success(Unit)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.sendPasswordReset()

            // Assert — navigated, no longer in Error state
            turbine.awaitItem()
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [PasswordResetViewModel.navigateBack] method emits NavigateBack event.
     */
    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.navigateBack()

            // Assert
            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
