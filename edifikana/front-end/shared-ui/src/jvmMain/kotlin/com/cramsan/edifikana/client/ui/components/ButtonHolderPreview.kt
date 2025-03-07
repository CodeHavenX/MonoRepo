package com.cramsan.edifikana.client.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.components.ButtonSection

@Preview
@Composable
private fun ButtonHolderPreview() = AppTheme {
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
