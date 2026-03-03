package com.cramsan.edifikana.client.lib.features.account.changepassword

import app.cash.turbine.turbineScope
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.UserId
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
import edifikana_lib.Res
import edifikana_lib.change_password_dialog_error_confirm_password_empty
import edifikana_lib.change_password_dialog_error_current_password_empty
import edifikana_lib.change_password_dialog_error_failed
import edifikana_lib.change_password_dialog_error_new_password_empty
import edifikana_lib.change_password_dialog_error_new_password_too_short
import edifikana_lib.change_password_dialog_error_passwords_do_not_match
import edifikana_lib.change_password_dialog_success
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertInstanceOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChangePasswordDialogViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var stringProvider: StringProvider
    private lateinit var viewModel: ChangePasswordDialogViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        stringProvider = mockk()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            windowEventReceiver = windowEventBus,
            applicationEventReceiver = applicationEventReceiver,
        )
        viewModel = ChangePasswordDialogViewModel(authManager, stringProvider, dependencies)
    }

    @Test
    fun `onCurrentPasswordChange updates state and validates empty`() = runCoroutineTest {
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_current_password_empty) } returns "Current password cannot be empty"

        viewModel.onCurrentPasswordChange("")
        assertTrue(viewModel.uiState.value.currentPasswordInError)
        assertEquals("Current password cannot be empty", viewModel.uiState.value.currentPasswordMessage)

        viewModel.onCurrentPasswordChange("secret")
        assertFalse(viewModel.uiState.value.currentPasswordInError)
        assertEquals(null, viewModel.uiState.value.currentPasswordMessage)
    }

    @Test
    fun `onNewPasswordChange validates password rules`() = runCoroutineTest {
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_new_password_empty) } returns "New password cannot be empty"
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_new_password_too_short) } returns "New password must be at least 8 characters"

        viewModel.onNewPasswordChange("")
        assertEquals("New password cannot be empty", viewModel.uiState.value.newPasswordMessage)

        viewModel.onNewPasswordChange("short")
        assertEquals("New password must be at least 8 characters", viewModel.uiState.value.newPasswordMessage)

        viewModel.onNewPasswordChange("validPassword1!")
        assertEquals(null, viewModel.uiState.value.newPasswordMessage)
    }

    @Test
    fun `onConfirmPasswordChange validates match with new password`() = runCoroutineTest {
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_confirm_password_empty) } returns "Confirm password cannot be empty"
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_passwords_do_not_match) } returns "Passwords do not match"

        viewModel.onNewPasswordChange("validPassword1!")
        viewModel.onConfirmPasswordChange("")
        assertEquals("Confirm password cannot be empty", viewModel.uiState.value.confirmPasswordMessage)

        viewModel.onConfirmPasswordChange("notmatching")
        assertEquals("Passwords do not match", viewModel.uiState.value.confirmPasswordMessage)

        viewModel.onConfirmPasswordChange("validPassword1!")
        assertEquals(null, viewModel.uiState.value.confirmPasswordMessage)
    }

    @Test
    fun `submitEnabled is true only when all fields valid`() = runCoroutineTest {
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_passwords_do_not_match) } returns "Passwords do not match"

        // Set up valid state
        viewModel.onCurrentPasswordChange("oldPassword1!")
        viewModel.onNewPasswordChange("newPassword1!")
        viewModel.onConfirmPasswordChange("newPassword1!")
        assertTrue(viewModel.uiState.value.submitEnabled)

        // Invalidate one field
        viewModel.onConfirmPasswordChange("wrong")
        assertFalse(viewModel.uiState.value.submitEnabled)
    }

    @OptIn(SecureStringAccess::class)
    @Test
    fun `onSubmitSelected success emits NavigateBack`() = runCoroutineTest {
        coEvery { authManager.changePassword(any(), any()) } returns Result.success(Unit)
        coEvery { stringProvider.getString(Res.string.change_password_dialog_success) } returns "Password was updated!"

        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)
            viewModel.onCurrentPasswordChange("oldPassword1!")
            viewModel.onNewPasswordChange("newPassword1!")
            viewModel.onConfirmPasswordChange("newPassword1!")

            // Act
            viewModel.onSubmitSelected()

            // Assert
            assertInstanceOf<EdifikanaWindowsEvent.ShowSnackbar>(turbine.awaitItem())
            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
        coVerify { authManager.changePassword(any(), any()) }
    }

    @OptIn(SecureStringAccess::class)
    @Test
    fun `onSubmitSelected failure updates error state`() = runCoroutineTest {
        coEvery { authManager.changePassword(any(), any()) } returns Result.failure(Exception("bad password"))
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_failed) } returns "Failed to change password: %1\$s"

        viewModel.onCurrentPasswordChange("oldPassword1!")
        viewModel.onNewPasswordChange("newPassword1!")
        viewModel.onConfirmPasswordChange("newPassword1!")
        viewModel.onSubmitSelected()

        assertTrue(viewModel.uiState.value.currentPasswordInError)
        assertEquals("Failed to change password: bad password", viewModel.uiState.value.currentPasswordMessage)
    }

    @Test
    fun `loadUserData sets showCurrentPassword based on user authMetadata`() = runCoroutineTest {
        val user = UserModel(
            id = UserId("1"),
            firstName = "A",
            lastName = "B",
            email = "a@b.com",
            phoneNumber = "123",
            authMetadata = UserModel.AuthMetadataModel(isPasswordSet = true)
        )
        coEvery { authManager.getUser() } returns Result.success(user)

        viewModel.loadUserData()
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showCurrentPassword)
    }

    @Test
    fun `loadUserData handles getUser failure gracefully`() = runCoroutineTest {
        coEvery { authManager.getUser() } returns Result.failure(Exception("network error"))

        viewModel.loadUserData()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showCurrentPassword)
    }
}
