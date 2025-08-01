package com.cramsan.edifikana.client.lib.features.account.changepassword

import androidx.compose.runtime.Composable
import com.cramsan.framework.core.SecureStringAccess
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(SecureStringAccess::class)
@Preview
@Composable
private fun ChangePasswordDialogPreview() {
    RenderContent(
        uiState = ChangePasswordDialogUIState(
            currentPasswordMessage = "Current password is required",
            newPasswordMessage = "New password must be at least 8 characters",
            confirmPasswordMessage = "Passwords do not match",
            currentPasswordInError = true,
        ),
        onCurrentPasswordChange = {},
        onNewPasswordChange = {},
        onConfirmPasswordChange = {},
        onSubmitSelected = { /* No-op for preview */ },
    )
}
