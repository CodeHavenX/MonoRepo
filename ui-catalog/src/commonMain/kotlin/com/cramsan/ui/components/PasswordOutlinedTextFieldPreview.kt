package com.cramsan.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun PasswordOutlinedTextFieldPreview() {
    PasswordOutlinedTextField(
        value = "password",
        onValueChange = {},
        label = { Text("Password Label") },
    )
}
