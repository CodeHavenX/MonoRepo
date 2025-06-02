<<<<<<<< HEAD:edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth/validation/ValidationScreen.preview.kt
========
@file:Suppress("MaximumLineLength")

package com.cramsan.edifikana.client.lib.features.auth.validation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ValidationScreenPreview() = AppTheme {
    OtpValidationContent(
        uiState = OtpValidationUIState(
            isLoading = true,
            errorMessage = "",
            email = "garcia.alicia1990@gmail.com",
            otpCode = listOf(1, 2, 3, 4, 5, 6),
            focusedIndex = 0
        ),
        modifier = Modifier,
        onBackClicked = {},
        onKeyboardBack = {},
        onEnterOtpValue = { _, _ -> },
        onOtpFieldFocused = {},
        onLoginClicked = {},
    )
}

@Preview
@Composable
private fun OtpInputFieldPreview() = AppTheme {
    OtpInputField(
        value = 6,
        focusRequester = remember { FocusRequester() },
        onFocusChanged = {},
        onKeyboardBack = {},
        onValueChanged = {},
        modifier = Modifier,
        onBackClicked = {}
    )
}
