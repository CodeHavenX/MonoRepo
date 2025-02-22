package com.cramsan.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun PasswordOutlinedTextFieldPreview() {
    PasswordOutlinedTextField(
        value = "password",
        onValueChange = {},
        label = { Text("Password Label") },
    )
}
