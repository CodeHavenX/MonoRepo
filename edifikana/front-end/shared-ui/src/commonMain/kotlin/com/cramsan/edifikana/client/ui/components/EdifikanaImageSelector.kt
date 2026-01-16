package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.cramsan.ui.theme.Padding

/**
 * Image selector component with grid-based selection.
 *
 * Displays a preview of the selected image with a button to open a bottom sheet
 * containing a grid of selectable images. The bottom sheet adapts to screen size.
 *
 * @param label Label displayed above the component
 * @param options List of image options to display
 * @param selectedOption Currently selected option
 * @param onOptionSelected Callback when an option is selected
 * @param modifier Modifier for the component
 * @param placeholder Text shown when no option is selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdifikanaImageSelector(
    label: String,
    options: List<ImageOptionUIModel>,
    selectedOption: ImageOptionUIModel?,
    onOptionSelected: (ImageOptionUIModel) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select an image",
) {
    var selectorVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Padding.X_SMALL),
        )

        // Preview of selected image
        if (selectedOption != null) {
            EdifikanaImage(
                imageSource = selectedOption.imageSource,
                contentDescription = selectedOption.displayName,
                size = 80.dp,
                cornerRadius = 8.dp,
                modifier = Modifier.padding(vertical = Padding.X_SMALL),
            )
        }

        // Button to open selector
        EdifikanaSecondaryButton(
            text = if (selectedOption == null) placeholder else "Change Icon",
            onClick = { selectorVisible = true },
        )
    }

    // Bottom sheet with image grid
    EdifikanaBottomSheet(
        visible = selectorVisible,
        onDismissRequest = { selectorVisible = false },
    ) {
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
                    selectorVisible = false
                },
                columns = 3,
            )
        }
    }
}
