package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import org.jetbrains.compose.ui.tooling.preview.Preview

private val samplePendingFlyers = listOf(
    FlyerModel(
        id = FlyerId("1"),
        title = "Neighborhood Watch Meeting",
        description = "Join us to discuss safety improvements in our area. All welcome.",
        fileUrl = null,
        status = FlyerStatus.PENDING,
        expiresAt = "2026-05-15",
        uploaderId = UserId("user1"),
        createdAt = "2026-04-14",
        updatedAt = "2026-04-14",
    ),
    FlyerModel(
        id = FlyerId("2"),
        title = "Piano Lessons Available",
        description = "Experienced teacher offering lessons for all ages and skill levels.",
        fileUrl = null,
        status = FlyerStatus.PENDING,
        expiresAt = null,
        uploaderId = UserId("user2"),
        createdAt = "2026-04-13",
        updatedAt = "2026-04-13",
    ),
)

/**
 * Preview for the Moderation Queue screen with content.
 */
@Preview
@Composable
private fun ModerationQueueScreenPreview() = AppTheme {
    ModerationQueueContent(
        uiState = ModerationQueueUIState(
            isLoading = false,
            pendingFlyers = samplePendingFlyers,
            errorMessage = null,
        ),
        onNavigateBack = {},
        onRefresh = {},
        onApprove = {},
        onReject = {},
    )
}

/**
 * Preview for the Moderation Queue screen in loading state.
 */
@Preview
@Composable
private fun ModerationQueueScreenLoadingPreview() = AppTheme {
    ModerationQueueContent(
        uiState = ModerationQueueUIState(
            isLoading = true,
            pendingFlyers = emptyList(),
            errorMessage = null,
        ),
        onNavigateBack = {},
        onRefresh = {},
        onApprove = {},
        onReject = {},
    )
}

/**
 * Preview for the Moderation Queue screen in empty state.
 */
@Preview
@Composable
private fun ModerationQueueScreenEmptyPreview() = AppTheme {
    ModerationQueueContent(
        uiState = ModerationQueueUIState(
            isLoading = false,
            pendingFlyers = emptyList(),
            errorMessage = null,
        ),
        onNavigateBack = {},
        onRefresh = {},
        onApprove = {},
        onReject = {},
    )
}
