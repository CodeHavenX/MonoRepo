package com.codehavenx.alpaca.frontend.appcore.features.createaccount

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Class for internal testing of screen. We can preview the UI with our mock data.
 */
@Preview
@Composable
private fun CreateAccountScreenPreview() {
    CreateAccountContent(
        content = CreateAccountUIModel("garcia.alicia1990@gmail.com", "1234567890"),
    )
}
