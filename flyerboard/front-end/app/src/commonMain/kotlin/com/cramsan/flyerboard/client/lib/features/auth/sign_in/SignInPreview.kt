package com.cramsan.flyerboard.client.lib.features.auth.sign_in

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the Sign In screen.
 */
@DevicePreviews
@Composable
private fun SignInScreenPreview() =
    AppTheme {
        SignInContent(
            uiState = SignInUIState(isLoading = false, email = "", password = ""),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignInClicked = {},
            onSignUpClicked = {},
            onDebugIconClicked = {},
        )
    }

/**
 * Preview for the Sign In screen in loading state.
 */
@ScreenPreviews
@Composable
private fun SignInScreenLoadingPreview() =
    AppTheme {
        SignInContent(
            uiState = SignInUIState(isLoading = false, email = "user@example.com", password = "••••••••"),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignInClicked = {},
            onSignUpClicked = {},
            onDebugIconClicked = {},
        )
    }
