package com.cramsan.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ButtonSectionPreview() = MaterialTheme {
    ButtonSection { buttonModifier ->
        Button(
            onClick = {},
            modifier = buttonModifier,
        ) {
            Text("Filled")
        }
        OutlinedButton(
            onClick = {},
            modifier = buttonModifier,
        ) {
            Text("Outlined")
        }
        TextButton(
            onClick = {},
            modifier = buttonModifier,
        ) {
            Text("Text")
        }
    }
}
