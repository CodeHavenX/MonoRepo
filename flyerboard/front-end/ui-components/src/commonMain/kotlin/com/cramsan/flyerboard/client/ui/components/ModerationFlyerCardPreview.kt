package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun ModerationFlyerCardNoImagePreview() =
    AppTheme {
        ModerationFlyerCard(
            title = "Community Event",
            description = "Join us for a community gathering in the park this weekend.",
            uploaderHandle = "cramsan",
            postedAt = "2026-04-17",
            onApprove = {},
            onReject = {},
            onClick = {},
        )
    }

@ComponentPreviews
@Composable
private fun ModerationFlyerCardWithImagePreview() =
    AppTheme {
        ModerationFlyerCard(
            title = "Lost Dog Reward",
            description = "Missing golden retriever, last seen near Elm Street. Please call if found.",
            uploaderHandle = "neighbor_bob",
            postedAt = "2026-04-18",
            imageUrl = "https://example.com/flyer.jpg",
            onApprove = {},
            onReject = {},
            onClick = {},
        )
    }
