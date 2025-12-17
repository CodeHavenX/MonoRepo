package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Edifikana profile header component displaying avatar, name, and optional role or organization.
 * Can be used for user profiles, employee pages, and property pages.
 *
 * @param name Primary name/title to display
 * @param avatarUrl URL of the profile image
 * @param modifier Modifier for the profile header
 * @param role Optional role or title (e.g., "Administrator")
 * @param org Optional organization (e.g., "Acme Corporation")
 */
@Composable
fun EdifikanaProfileHeader(
    name: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    role: String? = null,
    org: String? = null,
) {
    Column(
        modifier = modifier.padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EdifikanaAvatar(
            imageUrl = avatarUrl,
            contentDescription = name,
            size = 120.dp,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            // Display role if provided
            role?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            // Display org if provided
            org?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
