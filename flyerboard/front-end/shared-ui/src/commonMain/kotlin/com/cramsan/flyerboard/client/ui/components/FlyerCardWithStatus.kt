package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.ui.theme.Padding
import flyerboard_ui.Res
import flyerboard_ui.flyer_card_with_status_button_edit
import org.jetbrains.compose.resources.stringResource

/** Clickable card that extends [FlyerCard] with a [StatusBadge] and an optional Edit button shown when [onEdit] is non-null and [status] is not ARCHIVED. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlyerCardWithStatus(
    title: String,
    description: String,
    status: FlyerStatus,
    expiresAt: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEdit: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(Padding.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(status = status)
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
            )
            expiresAt?.let { expires ->
                Text(
                    text = expires,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            if (onEdit != null && status != FlyerStatus.ARCHIVED) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(stringResource(Res.string.flyer_card_with_status_button_edit))
                }
            }
        }
    }
}
