package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.ui.theme.Padding

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
