package com.cramsan.edifikana.client.lib.features.auth.setnewpassword

import app.cash.turbine.turbineScope
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
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
import edifikana_lib.change_password_dialog_error_confirm_password_empty
import edifikana_lib.change_password_dialog_error_new_password_empty
import edifikana_lib.change_password_dialog_error_new_password_too_short
import edifikana_lib.change_password_dialog_error_passwords_do_not_match
import edifikana_lib.error_message_unexpected_error
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Test the [SetNewPasswordViewModel] class.
 */
class SetNewPasswordViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var stringProvider: StringProvider
    private lateinit var viewModel: SetNewPasswordViewModel
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
        viewModel = SetNewPasswordViewModel(
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
     * Test the [SetNewPasswordViewModel.initialize] method sets isLoading to false.
     */
    @Test
    fun `test initialize sets isLoading to false`() = runCoroutineTest {
        // Act
        viewModel.initialize()

        // Assert
        assertFalse(viewModel.uiState.value.isLoading)
    }

    /**
     * Test the [SetNewPasswordViewModel.onNewPasswordChange] method with an empty password sets newPasswordMessage.
     */
    @Test
    fun `test onNewPasswordChange with empty password sets newPasswordMessage`() = runCoroutineTest {
        // Arrange
        val message = "New password cannot be empty"
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_new_password_empty) } returns message

        // Act
        viewModel.onNewPasswordChange("")

        // Assert
        assertEquals(message, viewModel.uiState.value.newPasswordMessage)
    }

    /**
     * Test the [SetNewPasswordViewModel.onNewPasswordChange] method with a short password sets newPasswordMessage.
     */
    @Test
    fun `test onNewPasswordChange with too short password sets newPasswordMessage`() = runCoroutineTest {
        // Arrange
        val message = "New password must be at least 8 characters"
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_new_password_too_short) } returns message

        // Act
        viewModel.onNewPasswordChange("short")

        // Assert
        assertEquals(message, viewModel.uiState.value.newPasswordMessage)
    }

    /**
     * Test the [SetNewPasswordViewModel.onNewPasswordChange] method with a valid password clears newPasswordMessage.
     */
    @Test
    fun `test onNewPasswordChange with valid password clears newPasswordMessage`() = runCoroutineTest {
        // Act
        viewModel.onNewPasswordChange("ValidPass1!")

        // Assert
        assertNull(viewModel.uiState.value.newPasswordMessage)
    }

    /**
     * Test the [SetNewPasswordViewModel.onConfirmPasswordChange] method with an empty value sets confirmPasswordMessage.
     */
    @Test
    fun `test onConfirmPasswordChange with empty value sets confirmPasswordMessage`() = runCoroutineTest {
        // Arrange
        val message = "Confirm password cannot be empty"
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_confirm_password_empty) } returns message

        // Act
        viewModel.onConfirmPasswordChange("")

        // Assert
        assertEquals(message, viewModel.uiState.value.confirmPasswordMessage)
    }

    /**
     * Test the [SetNewPasswordViewModel.onConfirmPasswordChange] method with a mismatched value sets confirmPasswordMessage.
     */
    @OptIn(SecureStringAccess::class)
    @Test
    fun `test onConfirmPasswordChange with mismatched value sets confirmPasswordMessage`() = runCoroutineTest {
        // Arrange
        val message = "Passwords do not match"
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_passwords_do_not_match) } returns message

        viewModel.onNewPasswordChange("ValidPass1!")

        // Act
        viewModel.onConfirmPasswordChange("DifferentPass1!")

        // Assert
        assertEquals(message, viewModel.uiState.value.confirmPasswordMessage)
    }

    /**
     * Test the [SetNewPasswordViewModel.onConfirmPasswordChange] method with a matching value clears confirmPasswordMessage.
     */
    @OptIn(SecureStringAccess::class)
    @Test
    fun `test onConfirmPasswordChange matching new password clears confirmPasswordMessage`() = runCoroutineTest {
        // Arrange
        viewModel.onNewPasswordChange("ValidPass1!")

        // Act
        viewModel.onConfirmPasswordChange("ValidPass1!")

        // Assert
        assertNull(viewModel.uiState.value.confirmPasswordMessage)
    }

    /**
     * Test that submitEnabled becomes true only when both fields are valid and matching.
     */
    @OptIn(SecureStringAccess::class)
    @Test
    fun `test submitEnabled is true only when both fields are valid and matching`() = runCoroutineTest {
        // Assert — initially disabled
        assertFalse(viewModel.uiState.value.submitEnabled)

        // Act — fill only new password
        viewModel.onNewPasswordChange("ValidPass1!")

        // Assert — still disabled with only one field
        assertFalse(viewModel.uiState.value.submitEnabled)

        // Act — fill matching confirm password
        viewModel.onConfirmPasswordChange("ValidPass1!")

        // Assert — now enabled
        assertTrue(viewModel.uiState.value.submitEnabled)
    }

    /**
     * Test the [SetNewPasswordViewModel.onSubmitSelected] method navigates to SignInDestination on success.
     */
    @OptIn(SecureStringAccess::class)
    @Test
    fun `test onSubmitSelected success navigates to SignInDestination`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.setNewPassword(any()) } returns Result.success(Unit)

        viewModel.onNewPasswordChange("ValidPass1!")
        viewModel.onConfirmPasswordChange("ValidPass1!")

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.onSubmitSelected()

            // Assert
            coVerify { authManager.setNewPassword(any()) }
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.SignInDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test the [SetNewPasswordViewModel.onSubmitSelected] method sets newPasswordMessage to the error string on failure.
     */
    @OptIn(SecureStringAccess::class)
    @Test
    fun `test onSubmitSelected failure sets newPasswordMessage to error string`() = runCoroutineTest {
        // Arrange
        val errorMessage = "There was an unexpected error."
        coEvery { authManager.setNewPassword(any()) } returns Result.failure(Exception("service error"))
        coEvery { stringProvider.getString(Res.string.error_message_unexpected_error) } returns errorMessage

        viewModel.onNewPasswordChange("ValidPass1!")
        viewModel.onConfirmPasswordChange("ValidPass1!")

        // Act
        viewModel.onSubmitSelected()

        // Assert
        assertEquals(errorMessage, viewModel.uiState.value.newPasswordMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    /**
     * Test the [SetNewPasswordViewModel.navigateBack] method emits NavigateBack event.
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
