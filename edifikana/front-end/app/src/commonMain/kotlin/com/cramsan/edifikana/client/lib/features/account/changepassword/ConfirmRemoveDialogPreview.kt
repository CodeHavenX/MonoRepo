package com.cramsan.edifikana.client.lib.features.account.changepassword

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.ui.preview.ComponentPreviews

@OptIn(SecureStringAccess::class)
@ComponentPreviews
@Composable
private fun ChangePasswordDialogPreview() =
    AppTheme {
        RenderContent(
            uiState =
            ChangePasswordDialogUIState(
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

@OptIn(SecureStringAccess::class)
@ComponentPreviews
@Composable
private fun ChangePasswordDialogPreview_ES() =
    AppTheme {
        RenderContent(
            uiState =
            ChangePasswordDialogUIState(
                currentPasswordMessage = "Se requiere la contraseña actual",
                newPasswordMessage = "Debe tener al menos 8 caracteres",
                confirmPasswordMessage = "Las contraseñas no coinciden",
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
