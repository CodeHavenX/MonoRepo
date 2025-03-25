package com.cramsan.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun PasswordOutlinedTextFieldPreview() {
    PasswordOutlinedTextField(
        value = "password",
        onValueChange = {},
        label = { Text("Password Label") },
    )
}
