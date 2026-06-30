package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.ui.theme.Padding

/**
 * Bottom sheet for selecting an image from a grid of options.
 *
 * @param label Title displayed at the top of the sheet
 * @param options List of image options to display in the grid
 * @param selectedOption Currently selected option, if any
 * @param onOptionSelected Callback when an option is selected
 * @param onDismiss Callback when the sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSelectorBottomsheet(
    label: String,
    options: List<ImageOptionUIModel>,
    selectedOption: ImageOptionUIModel?,
    onOptionSelected: (ImageOptionUIModel) -> Unit,
    onDismiss: () -> Unit,
) {
    EdifikanaBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(Padding.MEDIUM),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = Padding.MEDIUM),
            )

            EdifikanaImageGrid(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { option ->
                    onOptionSelected(option)
                    onDismiss()
                },
                columns = 3,
            )
        }
    }
}
