package com.cramsan.flyerboard.client.lib.features.main.archive

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.ui.preview.ScreenPreviews

private val sampleArchivedFlyers =
    listOf(
        FlyerModel(
            id = FlyerId("1"),
            title = "Spring Festival 2026",
            description = "A celebration of spring with music, food, and fun. Already ended.",
            fileUrl = null,
            status = FlyerStatus.ARCHIVED,
            expiresAt = "2026-03-20",
            uploaderId = UserId("user1"),
            createdAt = "2026-03-01",
            updatedAt = "2026-03-21",
        ),
        FlyerModel(
            id = FlyerId("2"),
            title = "Garage Sale – April 5",
            description = "Tools, furniture, and household items for sale.",
            fileUrl = null,
            status = FlyerStatus.ARCHIVED,
            expiresAt = "2026-04-05",
            uploaderId = UserId("user2"),
            createdAt = "2026-04-01",
            updatedAt = "2026-04-06",
        ),
    )

/**
 * Preview for the Archive screen with content.
 */
@ScreenPreviews
@Composable
private fun ArchiveScreenPreview() =
    AppTheme {
        ArchiveContent(
            uiState = ArchiveUIState.Loading(),
            onNavigateBack = {},
            onRefresh = {},
            onFlyerSelected = {},
        )
    }

/**
 * Preview for the Archive screen in loading state.
 */
@ScreenPreviews
@Composable
private fun ArchiveScreenLoadingPreview() =
    AppTheme {
        ArchiveContent(
            uiState = ArchiveUIState.Empty(),
            onNavigateBack = {},
            onRefresh = {},
            onFlyerSelected = {},
        )
    }

/**
 * Preview for the Archive screen in empty state.
 */
@ScreenPreviews
@Composable
private fun ArchiveScreenEmptyPreview() =
    AppTheme {
        ArchiveContent(
            uiState = ArchiveUIState.Content(flyers = sampleArchivedFlyers),
            onNavigateBack = {},
            onRefresh = {},
            onFlyerSelected = {},
        )
    }

@ScreenPreviews
@Composable
private fun ArchiveWithQueryPreview() =
    AppTheme(dynamicColor = false) {
        ArchiveContent(
            uiState = ArchiveUIState.Content(flyers = sampleArchivedFlyers, query = "spring"),
            onNavigateBack = {},
            onRefresh = {},
            onFlyerSelected = {},
        )
    }
