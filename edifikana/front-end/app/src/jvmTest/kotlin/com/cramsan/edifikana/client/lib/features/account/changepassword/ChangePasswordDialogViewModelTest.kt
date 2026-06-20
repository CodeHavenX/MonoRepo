package com.cramsan.edifikana.client.lib.features.account.changepassword

import app.cash.turbine.turbineScope
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.user.UserId
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
import edifikana_lib.change_password_dialog_error_new_password_empty
import edifikana_lib.change_password_dialog_error_new_password_too_short
import edifikana_lib.change_password_dialog_error_passwords_do_not_match
import edifikana_lib.change_password_dialog_error_verify_password_exists
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

    // region — field validation

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

    // endregion

    // region — submitEnabled gating

    @Test
    fun `submitEnabled when no password set requires only new and confirm fields`() = runCoroutineTest {
        // showCurrentPassword = false (default — no password on account)
        assertFalse(viewModel.uiState.value.submitEnabled)

        viewModel.onNewPasswordChange("newPassword1!")
        assertFalse(viewModel.uiState.value.submitEnabled)

        viewModel.onConfirmPasswordChange("newPassword1!")
        assertTrue(viewModel.uiState.value.submitEnabled)
    }

    @Test
    fun `submitEnabled when password is set requires all three fields`() = runCoroutineTest {
        // Arrange — load user with password set so showCurrentPassword = true
        val user = userWithPassword(isPasswordSet = true)
        coEvery { authManager.getUser() } returns Result.success(user)
        viewModel.loadUserData()
        assertTrue(viewModel.uiState.value.showCurrentPassword)

        // new + confirm valid but no current password — should still be disabled
        viewModel.onNewPasswordChange("newPassword1!")
        viewModel.onConfirmPasswordChange("newPassword1!")
        assertFalse(viewModel.uiState.value.submitEnabled)

        // all three valid — now enabled
        viewModel.onCurrentPasswordChange("oldPassword1!")
        assertTrue(viewModel.uiState.value.submitEnabled)
    }

    @Test
    fun `submitEnabled when password is set becomes false when current password is cleared`() = runCoroutineTest {
        // Arrange
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_current_password_empty) } returns "Current password cannot be empty"
        val user = userWithPassword(isPasswordSet = true)
        coEvery { authManager.getUser() } returns Result.success(user)
        viewModel.loadUserData()

        viewModel.onCurrentPasswordChange("oldPassword1!")
        viewModel.onNewPasswordChange("newPassword1!")
        viewModel.onConfirmPasswordChange("newPassword1!")
        assertTrue(viewModel.uiState.value.submitEnabled)

        // Clear current password — submit must be disabled again
        viewModel.onCurrentPasswordChange("")
        assertFalse(viewModel.uiState.value.submitEnabled)
    }

    // endregion

    // region — onSubmitSelected routing

    @OptIn(SecureStringAccess::class)
    @Test
    fun `onSubmitSelected when no password set calls setNewPassword and emits NavigateBack`() = runCoroutineTest {
        // Arrange — showCurrentPassword = false (default)
        coEvery { authManager.setNewPassword(any()) } returns Result.success(Unit)
        coEvery { stringProvider.getString(Res.string.change_password_dialog_success) } returns "Password was updated!"

        viewModel.onNewPasswordChange("newPassword1!")
        viewModel.onConfirmPasswordChange("newPassword1!")

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.onSubmitSelected()

            // Assert
            assertInstanceOf<EdifikanaWindowsEvent.ShowSnackbar>(turbine.awaitItem())
            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
        coVerify { authManager.setNewPassword(any()) }
        coVerify(exactly = 0) { authManager.changePassword(any(), any()) }
    }

    @OptIn(SecureStringAccess::class)
    @Test
    fun `onSubmitSelected when password is set calls changePassword and emits NavigateBack`() = runCoroutineTest {
        // Arrange — load user with password set so showCurrentPassword = true
        val user = userWithPassword(isPasswordSet = true)
        coEvery { authManager.getUser() } returns Result.success(user)
        viewModel.loadUserData()

        coEvery { authManager.changePassword(any(), any()) } returns Result.success(Unit)
        coEvery { stringProvider.getString(Res.string.change_password_dialog_success) } returns "Password was updated!"

        viewModel.onCurrentPasswordChange("oldPassword1!")
        viewModel.onNewPasswordChange("newPassword1!")
        viewModel.onConfirmPasswordChange("newPassword1!")

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.onSubmitSelected()

            // Assert
            assertInstanceOf<EdifikanaWindowsEvent.ShowSnackbar>(turbine.awaitItem())
            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
        coVerify { authManager.changePassword(any(), any()) }
        coVerify(exactly = 0) { authManager.setNewPassword(any()) }
    }

    @OptIn(SecureStringAccess::class)
    @Test
    fun `onSubmitSelected when no password set and setNewPassword fails updates error state`() = runCoroutineTest {
        // Arrange — showCurrentPassword = false (default)
        coEvery { authManager.setNewPassword(any()) } returns Result.failure(Exception("network error"))

        viewModel.onNewPasswordChange("newPassword1!")
        viewModel.onConfirmPasswordChange("newPassword1!")

        // Act
        viewModel.onSubmitSelected()

        // Assert
        assertTrue(viewModel.uiState.value.currentPasswordInError)
        assertEquals("Failed to change password: network error", viewModel.uiState.value.currentPasswordMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @OptIn(SecureStringAccess::class)
    @Test
    fun `onSubmitSelected when password is set and changePassword fails updates error state`() = runCoroutineTest {
        // Arrange — load user with password set
        val user = userWithPassword(isPasswordSet = true)
        coEvery { authManager.getUser() } returns Result.success(user)
        viewModel.loadUserData()

        coEvery { authManager.changePassword(any(), any()) } returns Result.failure(Exception("bad credentials"))

        viewModel.onCurrentPasswordChange("oldPassword1!")
        viewModel.onNewPasswordChange("newPassword1!")
        viewModel.onConfirmPasswordChange("newPassword1!")

        // Act
        viewModel.onSubmitSelected()

        // Assert
        assertTrue(viewModel.uiState.value.currentPasswordInError)
        assertEquals("Failed to change password: bad credentials", viewModel.uiState.value.currentPasswordMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // endregion

    // region — loadUserData

    @Test
    fun `loadUserData sets showCurrentPassword true when password is set`() = runCoroutineTest {
        val user = userWithPassword(isPasswordSet = true)
        coEvery { authManager.getUser() } returns Result.success(user)

        viewModel.loadUserData()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showCurrentPassword)
    }

    @Test
    fun `loadUserData sets showCurrentPassword false when no password is set`() = runCoroutineTest {
        val user = userWithPassword(isPasswordSet = false)
        coEvery { authManager.getUser() } returns Result.success(user)

        viewModel.loadUserData()

        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.showCurrentPassword)
    }

    @Test
    fun `loadUserData shows current password field on getUser failure as safe fallback`() = runCoroutineTest {
        coEvery { authManager.getUser() } returns Result.failure(Exception("network error"))
        coEvery { stringProvider.getString(Res.string.change_password_dialog_error_verify_password_exists) } returns "Could not verify password status"

        viewModel.loadUserData()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showCurrentPassword)
    }

    // endregion

    // region — helpers

    private fun userWithPassword(isPasswordSet: Boolean) = UserModel(
        id = UserId("1"),
        firstName = "A",
        lastName = "B",
        email = "a@b.com",
        phoneNumber = "123",
        authMetadata = UserModel.AuthMetadataModel(isPasswordSet = isPasswordSet),
    )

    // endregion
}
