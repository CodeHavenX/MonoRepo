package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.ui.theme.Padding

/**
 * Bottom sheet dialog for selecting an image from a grid of options.
 *
 * @param label Title displayed at the top of the sheet
 * @param options List of image options to display in the grid
 * @param selectedOption Currently selected option, if any
 * @param onOptionSelected Callback when an option is selected; the sheet will automatically hide after selection
 */
class ImageSelectorBottomsheet(
    private val label: String,
    private val options: List<ImageOptionUIModel>,
    private val selectedOption: ImageOptionUIModel?,
    private val onOptionSelected: (ImageOptionUIModel) -> Unit,
) : EdifikanaBottomSheetDialog() {

    @Composable
    override fun BottomSheetContent() {
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
                    hide()
                },
                columns = 3,
            )
        }
    }
}
