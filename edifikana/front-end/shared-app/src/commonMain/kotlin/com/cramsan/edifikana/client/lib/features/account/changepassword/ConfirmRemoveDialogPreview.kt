package com.cramsan.edifikana.client.lib.features.account.changepassword

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.framework.core.SecureStringAccess
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(SecureStringAccess::class)
@Preview
@Composable
private fun ChangePasswordDialogPreview() = AppTheme {
    RenderContent(
        uiState = ChangePasswordDialogUIState(
            currentPasswordMessage = "Current password is required",
            newPasswordMessage = "Must be at least 8 characters",
            confirmPasswordMessage = "Passwords do not match",
            currentPasswordInError = true,
            showCurrentPassword = true,
            isLoading = false,
        ),
        onCurrentPasswordChange = {},
        onNewPasswordChange = {},
        onConfirmPasswordChange = {},
        onSubmitSelected = { /* No-op for preview */ },
    )
}
