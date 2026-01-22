package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: (() -> Unit)?,
    colors: CardColors = CardDefaults.cardColors(),
    modifier: Modifier = Modifier,
) {
    Card (
        modifier = modifier
            .fillMaxWidth(),
        colors = colors,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .clickable(enabled = onClick != null) {
                    onClick?.invoke()
                }
                .fillMaxWidth()
                .padding(Padding.MEDIUM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column (
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.size(Padding.X_SMALL))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.contentColor,
                )
            }

            Spacer(modifier = Modifier.size(Padding.MEDIUM))

            // Icon with circular background
            Box(
                modifier = Modifier
                    .size(Size.xx_large)
                    .clip(CircleShape)
                    .background(colors.containerColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.contentColor,
                    modifier = Modifier.size(Size.large),
                )
            }
        }
    }
}

@Preview
@Composable
private fun OptionCardPreview_Disabled_Card() = AppTheme {
    OptionCard(
        title = "Join an existing team",
        description = "I have an invite code or want to search for my company.",
        icon = Icons.Default.Groups,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
        ),
        onClick = null,
    )
}

@Preview
@Composable
private fun OptionCardPreview_Enabled_Card() = AppTheme {
    OptionCard(
        title = "Create a new workspace",
        description = "I want to set up a new property portfolio for my team.",
        icon = Icons.Default.Domain,
        onClick = { },
    )
}