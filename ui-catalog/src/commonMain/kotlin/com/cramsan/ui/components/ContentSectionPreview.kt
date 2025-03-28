package com.cramsan.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ContentSectionPreview() = MaterialTheme {
    ContentSection { sectionModifier ->
        OutlinedTextField(
            value = "Value",
            onValueChange = {},
            label = { Text("Label") },
            modifier = sectionModifier,
        )
        HorizontalDivider(sectionModifier)
        OutlinedTextField(
            value = "Value",
            onValueChange = {},
            label = { Text("Label") },
            modifier = sectionModifier,
        )
        OutlinedTextField(
            value = "Value",
            onValueChange = {},
            label = { Text("Label") },
            modifier = sectionModifier,
        )
        HorizontalDivider(sectionModifier)
        ListCell(
            modifier = sectionModifier,
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
            modifier = sectionModifier,
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
    }
}
