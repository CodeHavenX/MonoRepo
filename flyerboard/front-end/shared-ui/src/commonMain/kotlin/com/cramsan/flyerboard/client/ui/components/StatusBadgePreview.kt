package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerStatus
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ApprovedPreview() =
    AppTheme {
        StatusBadge(status = FlyerStatus.APPROVED)
    }

@Preview
@Composable
private fun PendingPreview() =
    AppTheme {
        StatusBadge(status = FlyerStatus.PENDING)
    }

@Preview
@Composable
private fun RejectedPreview() =
    AppTheme {
        StatusBadge(status = FlyerStatus.REJECTED)
    }

@Preview
@Composable
private fun ArchivedPreview() =
    AppTheme {
        StatusBadge(status = FlyerStatus.ARCHIVED)
    }
