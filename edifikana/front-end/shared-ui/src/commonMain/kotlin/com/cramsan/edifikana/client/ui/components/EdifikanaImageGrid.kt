package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.cramsan.ui.theme.Padding

/**
 * Grid of selectable images.
 *
 * @param options List of image options to display
 * @param selectedOption Currently selected option
 * @param onOptionSelected Callback when an option is selected
 * @param columns Number of grid columns
 * @param modifier Modifier for the grid
 */
@Composable
fun EdifikanaImageGrid(
    options: List<ImageOptionUIModel>,
    selectedOption: ImageOptionUIModel?,
    onOptionSelected: (ImageOptionUIModel) -> Unit,
    columns: Int = 3,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(options) { option ->
            ImageGridItem(
                option = option,
                isSelected = option.id == selectedOption?.id,
                onSelected = { onOptionSelected(option) },
                modifier = Modifier.padding(Padding.SMALL),
            )
        }
    }
}

@Composable
private fun ImageGridItem(
    option: ImageOptionUIModel,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelected),
        contentAlignment = Alignment.Center,
    ) {
        EdifikanaImage(
            imageSource = option.imageSource,
            contentDescription = option.displayName,
            cornerRadius = 8.dp,
            contentScale = ContentScale.Fit,
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Padding.XX_SMALL)
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
