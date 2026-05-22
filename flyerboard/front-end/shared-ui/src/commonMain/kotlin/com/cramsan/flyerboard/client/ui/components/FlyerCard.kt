package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.cramsan.ui.theme.Padding

/** Clickable card displaying a flyer's image, [title], [description], and optional [expiresAt] badge. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlyerCard(
    title: String,
    description: String,
    imageUrl: String? = null,
    expiresAt: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column {
            FlyerAsyncImage(
                url = imageUrl,
                contentDescription = title,
            )
            Column(
                modifier = Modifier.padding(Padding.MEDIUM),
                verticalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
                expiresAt?.let { expires ->
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "Expires: $expires",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                        border = null,
                    )
                }
            }
        }
    }
}
