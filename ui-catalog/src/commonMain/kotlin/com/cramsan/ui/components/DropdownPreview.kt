package com.cramsan.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
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

@Preview
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
