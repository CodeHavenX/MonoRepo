package com.cramsan.edifikana.client.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.components.ContentSection
import com.cramsan.ui.components.ListCell

@Preview
@Composable
private fun SectionHolderPreview() = AppTheme {
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
