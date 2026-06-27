package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.cramsan.ui.theme.Padding
import flyerboard_ui.Res
import flyerboard_ui.moderation_flyer_card_button_approve
import flyerboard_ui.moderation_flyer_card_button_reject
import org.jetbrains.compose.resources.stringResource

/**
 * Card showing a pending flyer with a thumbnail, uploader/posted-date metadata, and Reject/Approve
 * action buttons for moderation.
 */
@Composable
fun ModerationFlyerCard(
    title: String,
    description: String,
    uploaderHandle: String,
    postedAt: String,
    imageUrl: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    Card(
        modifier =
        modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .height(Padding.XX_SMALL)
                .background(MaterialTheme.colorScheme.primary),
        )
        Row(
            modifier = Modifier.padding(Padding.MEDIUM),
            horizontalArrangement = Arrangement.spacedBy(Padding.MEDIUM),
        ) {
            ModerationFlyerThumbnail(
                imageUrl = imageUrl,
                contentDescription = title,
            )
            Column(
                modifier = Modifier.weight(1f),
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
                Row(horizontalArrangement = Arrangement.spacedBy(Padding.SMALL)) {
                    MetadataItem(icon = Icons.Default.Person, text = uploaderHandle)
                    MetadataItem(icon = Icons.Default.DateRange, text = postedAt)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(Padding.X_SMALL)) {
                Button(
                    onClick = onApprove,
                    colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(BUTTON_APPROVE_COLOR),
                        contentColor = Color.White,
                    ),
                ) {
                    Text(stringResource(Res.string.moderation_flyer_card_button_approve))
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                ) {
                    Text(stringResource(Res.string.moderation_flyer_card_button_reject))
                }
            }
        }
    }
}

@Composable
private fun MetadataItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Padding.XXX_SMALL),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(METADATA_ICON_SIZE),
            tint = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
private fun ModerationFlyerThumbnail(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val thumbnailModifier = modifier.size(THUMBNAIL_SIZE).clip(RoundedCornerShape(Padding.X_SMALL))
    if (imageUrl != null) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = thumbnailModifier,
            loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(Padding.LARGE))
                }
            },
        )
    } else {
        Box(modifier = thumbnailModifier.background(MaterialTheme.colorScheme.surfaceVariant))
    }
}

private val THUMBNAIL_SIZE = 72.dp
private val METADATA_ICON_SIZE = 14.dp
private const val BUTTON_APPROVE_COLOR = 0xFF84CC16L
