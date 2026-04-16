package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Sign Up screen.
 */
@Preview
@Composable
private fun SignUpScreenPreview() = AppTheme {
    SignUpContent(
        uiState = SignUpUIState(
            isLoading = false,
            email = "",
            password = "",
        ),
        onEmailChanged = {},
        onPasswordChanged = {},
        onSignUpClicked = {},
        onSignInClicked = {},
    )
}

/**
 * Preview for the Sign Up screen in loading state.
 */
@Preview
@Composable
private fun SignUpScreenLoadingPreview() = AppTheme {
    SignUpContent(
        uiState = SignUpUIState(
            isLoading = true,
            email = "new@example.com",
            password = "",
        ),
        onEmailChanged = {},
        onPasswordChanged = {},
        onSignUpClicked = {},
        onSignInClicked = {},
    )
}
