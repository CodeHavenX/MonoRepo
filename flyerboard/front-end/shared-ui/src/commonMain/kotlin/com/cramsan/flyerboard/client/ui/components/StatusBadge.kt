package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.ui.theme.Padding
import flyerboard_ui.Res
import flyerboard_ui.flyer_status_approved
import flyerboard_ui.flyer_status_archived
import flyerboard_ui.flyer_status_pending
import flyerboard_ui.flyer_status_rejected
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatusBadge(
    status: FlyerStatus,
    modifier: Modifier = Modifier,
) {
    val (label, backgroundColor, textColor) = when (status) {
        FlyerStatus.APPROVED -> Triple(
            stringResource(Res.string.flyer_status_approved),
            Color(STATUS_APPROVED_BACKGROUND),
            Color(STATUS_APPROVED_TEXT),
        )
        FlyerStatus.PENDING -> Triple(
            stringResource(Res.string.flyer_status_pending),
            Color(STATUS_PENDING_BACKGROUND),
            Color.White,
        )
        FlyerStatus.REJECTED -> Triple(
            stringResource(Res.string.flyer_status_rejected),
            Color(STATUS_REJECTED_BACKGROUND),
            Color.White,
        )
        FlyerStatus.ARCHIVED -> Triple(
            stringResource(Res.string.flyer_status_archived),
            Color(STATUS_ARCHIVED_BACKGROUND),
            Color.White,
        )
    }
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50),
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = Padding.X_SMALL, vertical = Padding.XXX_SMALL),
        )
    }
}

private const val STATUS_APPROVED_BACKGROUND = 0xFF84CC16L
private const val STATUS_APPROVED_TEXT = 0xFF1C1917L
private const val STATUS_PENDING_BACKGROUND = 0xFFF43F5EL
private const val STATUS_REJECTED_BACKGROUND = 0xFFDC2626L
private const val STATUS_ARCHIVED_BACKGROUND = 0xFF9CA3AFL
