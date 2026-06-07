package com.cramsan.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun DropdownPreviewCollapsed() {
    Dropdown(
        label = "Dropdown",
        modifier = Modifier.fillMaxWidth(),
        items = listOf("One", "Two", "Three"),
        itemLabels = listOf("One", "Two", "Three"),
        startValueMatcher = { it == "Two" },
    ) {}
}

@ComponentPreviews
@Composable
private fun DropdownPreviewExpanded() {
    Dropdown(
        label = "Dropdown",
        modifier = Modifier.fillMaxWidth(),
        items = listOf("One", "Two", "Three"),
        itemLabels = listOf("One", "Two", "Three"),
        startValueMatcher = { it == "Two" },
        expanded = true,
    ) { }
}
