package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Generic dropdown component for selecting images.
 *
 * Displays a list of image options with image-only previews (no text labels).
 * Uses [EdifikanaImage] component for efficient rendering of both drawable resources and URLs.
 * Follows Edifikana design patterns with rounded corners and Material3 colors.
 *
 * @param label The label displayed above the dropdown
 * @param options List of image options to display
 * @param selectedOption Currently selected option (or null if none selected)
 * @param onOptionSelected Callback when an option is selected
 * @param modifier Modifier for the component
 * @param placeholder Text to show when no option is selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdifikanaImageDropdown(
    label: String,
    options: List<ImageOptionUIModel>,
    selectedOption: ImageOptionUIModel?,
    onOptionSelected: (ImageOptionUIModel) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select an image",
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = selectedOption?.displayName ?: placeholder

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = displayValue,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                placeholder = {
                    if (selectedOption == null) {
                        Text(placeholder)
                    }
                },
                leadingIcon = {
                    selectedOption?.let { option ->
                        EdifikanaImage(
                            imageSource = option.imageSource,
                            contentDescription = option.displayName,
                            size = 40.dp,
                            cornerRadius = 8.dp,
                        )
                    }
                },
                trailingIcon = {
                    val icon = if (expanded) {
                        Icons.Filled.ArrowDropUp
                    } else {
                        Icons.Filled.ArrowDropDown
                    }
                    Icon(icon, displayValue)
                },
                singleLine = true,
                onValueChange = { },
                readOnly = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                EdifikanaImage(
                                    imageSource = option.imageSource,
                                    contentDescription = option.displayName,
                                    size = 48.dp,
                                    cornerRadius = 8.dp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Text(option.displayName)
                            }
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
