package com.cramsan.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Dropdown component.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Dropdown(
    label: String,
    modifier: Modifier,
    items: List<T>,
    itemLabels: List<String>,
    startValueMatcher: ((T) -> Boolean)?,
    expanded: Boolean = false,
    valueSelected: (T) -> Unit,
) {
    var expanded by remember(expanded) { mutableStateOf(false) }
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
        OutlinedTextField(
            value = value,
            modifier = Modifier
                .menuAnchor()
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
                        valueSelected(s)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
