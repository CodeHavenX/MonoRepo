package com.codehavenx.alpaca.frontend.appcore.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dropdown component.
 *
 * @param label The label for the dropdown.
 * @param modifier The modifier for the dropdown.
 * @param items The list of items to display in the dropdown.
 * @param itemLabels The list of labels to display for each item.
 * @param startValueMatcher The matcher to find the start value.
 * @param onValueSelected The callback when a value is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Dropdown(
    label: String,
    modifier: Modifier,
    items: List<T>,
    itemLabels: List<String>,
    startValueMatcher: ((T) -> Boolean)?,
    onValueSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember {
        val startIndex = startValueMatcher?.let {
            items.indexOfFirst(startValueMatcher)
        }
        mutableStateOf(startIndex)
    }
    val value = selectedIndex?.let {
        itemLabels.getOrNull(it)
    }.orEmpty()

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            value = value,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            trailingIcon = {
                val icon = if (expanded) {
                    Icons.Filled.ArrowDropUp
                } else {
                    Icons.Filled.ArrowDropDown
                }
                Icon(icon, value)
            },
            singleLine = true,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(
                    text = { Text(itemLabels[index]) },
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        onValueSelected(s)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview
@Composable
private fun DropdownPreview() {
    var selected by remember { mutableIntStateOf(0) }
    Dropdown(
        label = "Dropdown",
        modifier = Modifier.fillMaxWidth(),
        items = listOf("One", "Two", "Three"),
        itemLabels = listOf("One", "Two", "Three"),
        startValueMatcher = { it == "Two" },
    ) {
        selected = 1
    }
}
