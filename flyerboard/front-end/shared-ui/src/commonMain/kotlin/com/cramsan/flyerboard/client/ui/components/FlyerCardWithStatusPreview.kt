package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerStatus
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ApprovedPreview() =
    AppTheme {
        FlyerCardWithStatus(
            title = "Community Cleanup Day",
            description = "Join us for our monthly neighborhood cleanup. All are welcome.",
            status = FlyerStatus.APPROVED,
            expiresAt = "2026-06-01",
            onClick = {},
            onEdit = {},
        )
    }

@Preview
@Composable
private fun PendingPreview() =
    AppTheme {
        FlyerCardWithStatus(
            title = "Summer Art Walk Series",
            description = "Local artists showcasing their work downtown every Friday.",
            status = FlyerStatus.PENDING,
            onClick = {},
            onEdit = {},
        )
    }

@Preview
@Composable
private fun RejectedPreview() =
    AppTheme {
        FlyerCardWithStatus(
            title = "Found Bike – Blue Trek",
            description = "Found near the community center. Contact to claim.",
            status = FlyerStatus.REJECTED,
            onClick = {},
            onEdit = {},
        )
    }

@Preview
@Composable
private fun ArchivedPreview() =
    AppTheme {
        FlyerCardWithStatus(
            title = "Block Party Planning",
            description = "Annual neighborhood block party planning meeting.",
            status = FlyerStatus.ARCHIVED,
            onClick = {},
            onEdit = {},
        )
    }
