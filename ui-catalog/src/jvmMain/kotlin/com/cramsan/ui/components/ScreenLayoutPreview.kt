package com.cramsan.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Preview
@Composable
private fun ScreenLayoutPreview() = MaterialTheme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ScreenLayout(
            modifier = debugModifier(),
            fixedFooter = false,
            sectionContent = { sectionModifier ->
                Text(
                    text = "Sample Section Content 1",
                    modifier = debugModifier(sectionModifier),
                )
                Text(
                    text = "Sample Section Content 2",
                    modifier = debugModifier(sectionModifier),
                )
                Text(
                    text = "Sample Section Content 3",
                    modifier = debugModifier(sectionModifier),
                )
                Text(
                    text = "Sample Section Content 4",
                    modifier = debugModifier(sectionModifier),
                )
            },
            buttonContent = { sectionModifier ->
                Button(
                    modifier = debugModifier(sectionModifier),
                    onClick = {},
                ) {
                    Text("Default Button")
                }
                FilledTonalButton(
                    modifier = debugModifier(sectionModifier),
                    onClick = {},
                ) {
                    Text("Tonal Button")
                }
                OutlinedButton(
                    modifier = debugModifier(sectionModifier),
                    onClick = {},
                ) {
                    Text("Outlined Button")
                }
                TextButton(
                    modifier = debugModifier(sectionModifier),
                    onClick = {},
                ) {
                    Text("Text Button")
                }
            },
        )
    }
}
