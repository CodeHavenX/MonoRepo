package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ModerationFlyerCardWithExpiryPreview() =
    AppTheme {
        ModerationFlyerCard(
            title = "Community Event",
            description = "Join us for a community gathering in the park this weekend.",
            expiresAt = "2026-07-01",
            onApprove = {},
            onReject = {},
        )
    }

@Preview
@Composable
private fun ModerationFlyerCardNoExpiryPreview() =
    AppTheme {
        ModerationFlyerCard(
            title = "Lost Dog Reward",
            description = "Missing golden retriever, last seen near Elm Street. Please call if found.",
            onApprove = {},
            onReject = {},
        )
    }
