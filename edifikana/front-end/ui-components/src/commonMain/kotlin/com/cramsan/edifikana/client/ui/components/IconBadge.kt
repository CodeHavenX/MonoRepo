package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.cramsan.ui.theme.Size

/**
 * Rounded-square badge with a centered icon.
 *
 * Used as a leading visual in list items and cards when no avatar image is available.
 *
 * @param icon Icon to display within the badge.
 * @param contentDescription Accessibility description for the icon, or null if purely decorative.
 * @param modifier Modifier for the badge container.
 * @param size Side length of the badge.
 * @param backgroundColor Background color of the badge.
 * @param tint Tint color applied to the icon.
 */
@Composable
fun IconBadge(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = Size.ICON_BADGE,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier =
        modifier
            .size(size)
            .clip(MaterialTheme.shapes.large)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
