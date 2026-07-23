package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.cramsan.edifikana.client.ui.theme.Shapes
import com.cramsan.edifikana.client.ui.theme.Spacing

/**
 * Compact pill-shaped status badge, e.g. "Active" or "Current".
 *
 * @param text Label to display within the badge.
 * @param containerColor Background color of the badge.
 * @param contentColor Text color of the badge.
 * @param modifier Modifier for the badge container.
 */
@Composable
fun StatusPillBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = Shapes.badgePill,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            modifier =
            Modifier.padding(
                horizontal = Spacing.md,
                vertical = Spacing.xs,
            ),
        )
    }
}
