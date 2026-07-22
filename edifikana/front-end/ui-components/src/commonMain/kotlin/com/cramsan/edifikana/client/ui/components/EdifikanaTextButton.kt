package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.edifikana.client.ui.theme.Shapes

/**
 * Edifikana text button component with rounded edges.
 * Used for "View More" links and similar actions.
 *
 * @param text Button text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 */
@Composable
fun EdifikanaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = Shapes.pill,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
