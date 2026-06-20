package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.theme.Padding

@DevicePreviews
@Composable
private fun FlyerBoardPageShellCenteredPreview() =
    AppTheme {
        FlyerBoardPageShell {
            Text(
                text = "Centered form content",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(Padding.LARGE),
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Field") },
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Padding.LARGE),
            )
            Button(
                onClick = {},
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Padding.LARGE),
            ) {
                Text("Submit")
            }
        }
    }

@ComponentPreviews
@Composable
private fun FlyerBoardPageShellTopAlignedPreview() =
    AppTheme {
        FlyerBoardPageShell(
            contentAlignment = Alignment.TopCenter,
            cardPadding = Padding.MEDIUM,
        ) {
            Text(
                text = "Top-aligned content with less padding",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(Padding.MEDIUM),
            )
            Text(
                text = "Full-bleed or custom inner layout goes here.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = Padding.MEDIUM),
            )
        }
    }
