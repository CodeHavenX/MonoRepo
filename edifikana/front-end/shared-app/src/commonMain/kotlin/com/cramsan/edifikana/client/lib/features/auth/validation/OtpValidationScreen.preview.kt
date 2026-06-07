@file:Suppress("MaximumLineLength")

package com.cramsan.edifikana.client.lib.features.auth.validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun ValidationScreenPreview() =
    AppTheme {
        OtpValidationContent(
            uiState =
            OtpValidationUIState(
                isLoading = true,
                errorMessage = "",
                email = "garcia.alicia1990@gmail.com",
                otpCode = "123456",
                accountCreationFlow = true,
                enabledContinueButton = true,
                otpLength = 6,
            ),
            modifier = Modifier,
            onBackClicked = {},
            onLoginClicked = {},
            onValueChanged = {},
        )
    }

@ScreenPreviews
@Composable
private fun ValidationScreenPreview_ES() =
    AppTheme {
        OtpValidationContent(
            uiState =
            OtpValidationUIState(
                isLoading = true,
                errorMessage = "",
                email = "garcia.alicia1990@gmail.com",
                otpCode = "123456",
                accountCreationFlow = true,
                enabledContinueButton = true,
                otpLength = 6,
            ),
            modifier = Modifier,
            onBackClicked = {},
            onLoginClicked = {},
            onValueChanged = {},
        )
    }
