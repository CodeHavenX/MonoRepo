package com.codehavenx.alpaca.frontend.appcore.features.signin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun SignInScreenPreview() {
    SignInContent(
        content = SignInUIModel(
            username = "cramsan",
            password = "password",
            error = false,
        ),
        loading = false,
        onSignInClicked = { },
    )
}

@Preview
@Composable
private fun SignInErrorScreenPreview() {
    SignInContent(
        content = SignInUIModel(
            username = "cramsan",
            password = "password",
            error = true,
        ),
        loading = false,
        onSignInClicked = { },
    )
}

@Preview
@Composable
private fun SignInLoadingScreenPreview() {
    SignInContent(
        content = SignInUIModel(
            username = "cramsan",
            password = "password",
            error = false,
        ),
        loading = true,
        onSignInClicked = { },
    )
}
