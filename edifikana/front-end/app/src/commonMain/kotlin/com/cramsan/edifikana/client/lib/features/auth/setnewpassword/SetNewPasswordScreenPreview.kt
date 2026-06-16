package com.cramsan.edifikana.client.lib.features.auth.setnewpassword

import androidx.compose.runtime.Composable
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.ui.preview.ScreenPreviews

@OptIn(SecureStringAccess::class)
@ScreenPreviews
@Composable
internal fun SetNewPasswordContentPreview() {
    SetNewPasswordContent(
        uiState = SetNewPasswordUIState(
            isLoading = false,
            submitEnabled = false,
        ),
        onNewPasswordChange = {},
        onConfirmPasswordChange = {},
        onSubmitSelected = {},
        onCloseClicked = {},
    )
}

@OptIn(SecureStringAccess::class)
@ScreenPreviews
@Composable
internal fun SetNewPasswordContentWithErrorsPreview() {
    SetNewPasswordContent(
        uiState = SetNewPasswordUIState(
            isLoading = false,
            newPassword = SecureString("short"),
            confirmPassword = SecureString("different"),
            newPasswordMessage = "New password must be at least 8 characters",
            confirmPasswordMessage = "Passwords do not match",
            submitEnabled = false,
        ),
        onNewPasswordChange = {},
        onConfirmPasswordChange = {},
        onSubmitSelected = {},
        onCloseClicked = {},
    )
}
