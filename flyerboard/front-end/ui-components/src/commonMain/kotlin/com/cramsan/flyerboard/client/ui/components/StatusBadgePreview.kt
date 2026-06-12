package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun ApprovedPreview() =
    AppTheme {
        StatusBadge(status = FlyerStatus.APPROVED)
    }

@ComponentPreviews
@Composable
private fun PendingPreview() =
    AppTheme {
        StatusBadge(status = FlyerStatus.PENDING)
    }

@ComponentPreviews
@Composable
private fun RejectedPreview() =
    AppTheme {
        StatusBadge(status = FlyerStatus.REJECTED)
    }

@ComponentPreviews
@Composable
private fun ArchivedPreview() =
    AppTheme {
        StatusBadge(status = FlyerStatus.ARCHIVED)
    }
