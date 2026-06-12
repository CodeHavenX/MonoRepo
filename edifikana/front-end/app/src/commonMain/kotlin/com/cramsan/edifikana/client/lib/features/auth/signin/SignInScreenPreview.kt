package com.cramsan.edifikana.client.lib.features.auth.signin

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationUIState
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.components.themetoggle.SelectedTheme
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the SignInV2 screen.
 */
@ScreenPreviews
@Composable
private fun SignInScreenPreview() =
    AppTheme {
        SignInContent(
            uiState =
            SignInUIState(
                isLoading = false,
                email = "",
                password = "",
                showPassword = false,
                errorMessages = null,
            ),
            applicationUIState =
            EdifikanaApplicationUIState(
                theme = SelectedTheme.SYSTEM_DEFAULT,
                applicationLoaded = true,
                showDebugWindow = false,
            ),
            onUsernameValueChange = { },
            onPasswordValueChange = { },
            onContinueWithPWClicked = { },
            onPWSignInClicked = {},
            onSignInOtpClicked = { },
            onSignUpClicked = { },
            onForgotPasswordClicked = { },
            onThemeSelected = { _ -> },
            onInfoClicked = { },
        )
    }

/**
 * SignIn preview when user chooses to Contine with Password.
 * Button should change from default preview
 */
@ScreenPreviews
@Composable
private fun SignInContentPreview() =
    AppTheme {
        SignInContent(
            uiState =
            SignInUIState(
                isLoading = true,
                email = "username",
                password = "password",
                showPassword = true,
                errorMessages = null,
            ),
            applicationUIState =
            EdifikanaApplicationUIState(
                theme = SelectedTheme.LIGHT,
                applicationLoaded = true,
                showDebugWindow = false,
            ),
            onUsernameValueChange = {},
            onPasswordValueChange = {},
            onContinueWithPWClicked = { },
            onPWSignInClicked = {},
            onSignInOtpClicked = { },
            onSignUpClicked = {},
            onForgotPasswordClicked = {},
            onThemeSelected = { _ -> },
            onInfoClicked = {},
        )
    }
