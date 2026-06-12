package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Edifikana section header component for displaying section titles.
 *
 * @param title Section title text
 * @param modifier Modifier for the section header
 */
@Composable
fun EdifikanaSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(vertical = 16.dp),
    )
}
