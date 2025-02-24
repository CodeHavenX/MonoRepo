package com.cramsan.edifikana.client.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.cramsan.ui.components.ListCell
import com.cramsan.ui.components.ScreenLayout

@Preview
@Composable
private fun ScreenLayoutPreview() {
    ScreenLayout(
        fixedFooter = true,
        sectionContent = { modifier ->
            OutlinedTextField(
                value = "Value",
                onValueChange = {},
                label = { Text("Label") },
                modifier = modifier,
            )
            HorizontalDivider(modifier)
            OutlinedTextField(
                value = "Value",
                onValueChange = {},
                label = { Text("Label") },
                modifier = modifier,
            )
            OutlinedTextField(
                value = "Value",
                onValueChange = {},
                label = { Text("Label") },
                modifier = modifier,
            )
            HorizontalDivider(modifier)
            ListCell(
                modifier = modifier,
                onSelection = null,
                startSlot = {
                    Text("Start")
                },
                endSlot = {
                    Text("End")
                },
                content = {
                    Text("Content")
                },
            )
            ListCell(
                modifier = modifier,
                onSelection = null,
                startSlot = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "",
                    )
                },
                endSlot = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "",
                    )
                },
                content = {
                    Column {
                        Text(
                            "This is a title",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Text(
                            "This is a subtitle with a longer text",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                },
            )
        },
        buttonContent = { modifier ->
            Button(
                onClick = {},
                modifier = modifier,
            ) {
                Text("Filled")
            }
            OutlinedButton(
                onClick = {},
                modifier = modifier,
            ) {
                Text("Outlined")
            }
            TextButton(
                onClick = {},
                modifier = modifier,
            ) {
                Text("Text")
            }
        }
    )
}
