package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

/**
 * Edifikana list item component that can display either an icon or an avatar.
 * Used for both event items and visitor log items.
 *
 * Uses [EdifikanaImage] internally with [ImageSource.Url] for avatar URLs.
 *
 * @param title Primary text (e.g., "Maintenance Request", "Arrived")
 * @param subtitle Secondary text (e.g., "Reported by: Alex", "Visitor: Olivia")
 * @param onClick Callback when item is clicked
 * @param modifier Modifier for the item
 * @param icon Optional icon vector for events
 * @param imageUrl Optional avatar URL for visitor logs
 */
@Composable
fun EdifikanaListItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    imageUrl: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon or Avatar
        when {
            imageUrl != null -> {
                // Avatar for visitor logs
                EdifikanaImage(
                    imageSource = ImageSource.Url(imageUrl),
                    contentDescription = title,
                    size = 56.dp,
                    cornerRadius = 28.dp, // Circle
                    contentScale = ContentScale.Crop,
                )
            }
            icon != null -> {
                // Icon for events
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Text content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
