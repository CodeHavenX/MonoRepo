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
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
        assertEquals("prefill@example.com", viewModel.uiState.value.email)
    }

    /**
     * Test the [PasswordResetViewModel.initialize] method with blank email leaves email empty.
     */
    @Test
    fun `test initialize with blank email leaves email unchanged`() = runCoroutineTest {
        // Act
        viewModel.initialize("")

        // Assert
        assertEquals("", viewModel.uiState.value.email)
    }

    /**
     * Test the [PasswordResetViewModel.changeEmailValue] method updates email in UIState.
     */
    @Test
    fun `test changeEmailValue updates email in UIState`() = runCoroutineTest {
        // Arrange
        val email = "test@example.com"

        // Act
        viewModel.changeEmailValue(email)

        // Assert
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
     * Test the [PasswordResetViewModel.sendPasswordReset] method shows an error for invalid email
     * and does not call the manager.
     */
    @Test
    fun `test sendPasswordReset with invalid email shows error and does not call manager`() = runCoroutineTest {
        // Arrange
        viewModel.changeEmailValue("not-an-email")

        // Act
        viewModel.sendPasswordReset()

        // Assert
        assertTrue(viewModel.uiState.value.errorMessages?.isNotEmpty() == true)
        coVerify(exactly = 0) { authManager.sendPasswordReset(any()) }
    }

    /**
     * Test the [PasswordResetViewModel.sendPasswordReset] method shows error message on failure.
     */
    @Test
    fun `test sendPasswordReset failure shows error message`() = runCoroutineTest {
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
            assertEquals(listOf(errorMessage), viewModel.uiState.value.errorMessages)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [PasswordResetViewModel.sendPasswordReset] method clears previous errors on a new attempt.
     */
    @Test
    fun `test sendPasswordReset clears previous error on new successful attempt`() = runCoroutineTest {
        // Arrange — first attempt fails to put an error in state
        val errorMessage = "There was an unexpected error."
        coEvery { authManager.sendPasswordReset(any()) } returns Result.failure(Exception())
        coEvery { stringProvider.getString(Res.string.error_message_unexpected_error) } returns errorMessage

        viewModel.changeEmailValue("user@example.com")
        viewModel.sendPasswordReset()
        assertEquals(listOf(errorMessage), viewModel.uiState.value.errorMessages)

        // Arrange — second attempt succeeds
        coEvery { authManager.sendPasswordReset(any()) } returns Result.success(Unit)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.sendPasswordReset()

            // Assert
            assertNull(viewModel.uiState.value.errorMessages)
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
