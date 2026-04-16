package com.cramsan.flyerboard.client.lib.features.auth.sign_in

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Sign In screen.
 */
@Preview
@Composable
private fun SignInScreenPreview() = AppTheme {
    SignInContent(
        uiState = SignInUIState(
            isLoading = false,
            email = "user@example.com",
            password = "",
        ),
        onEmailChanged = {},
        onPasswordChanged = {},
        onSignInClicked = {},
        onSignUpClicked = {},
    )
}

/**
 * Preview for the Sign In screen in loading state.
 */
@Preview
@Composable
private fun SignInScreenLoadingPreview() = AppTheme {
    SignInContent(
        uiState = SignInUIState(
            isLoading = true,
            email = "user@example.com",
            password = "",
        ),
        onEmailChanged = {},
        onPasswordChanged = {},
        onSignInClicked = {},
        onSignUpClicked = {},
    )
}
