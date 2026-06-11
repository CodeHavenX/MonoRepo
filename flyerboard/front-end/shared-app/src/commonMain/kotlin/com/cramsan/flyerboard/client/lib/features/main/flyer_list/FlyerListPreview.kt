package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.preview.ScreenPreviews

private val sampleFlyers =
    listOf(
        FlyerModel(
            id = FlyerId("1"),
            title = "Community Yard Sale",
            description = "Everything must go! Furniture, clothes, books and more.",
            fileUrl = null,
            status = FlyerStatus.APPROVED,
            expiresAt = "2026-05-01",
            uploaderId = UserId("user1"),
            createdAt = "2026-04-01",
            updatedAt = "2026-04-01",
        ),
        FlyerModel(
            id = FlyerId("2"),
            title = "Lost Cat – Please Help",
            description = "Orange tabby, answers to Mango. Last seen near Oak Street.",
            fileUrl = null,
            status = FlyerStatus.APPROVED,
            expiresAt = null,
            uploaderId = UserId("user2"),
            createdAt = "2026-04-10",
            updatedAt = "2026-04-10",
        ),
    )

@DevicePreviews
@Composable
private fun FlyerListScreenPreview() =
    AppTheme(dynamicColor = false) {
        FlyerListContent(
            uiState = FlyerListUIState.Content(flyers = sampleFlyers),
            isAuthenticated = false,
            onRefresh = {},
            onFlyerSelected = {},
        )
    }

/**
 * Preview for the Flyer List screen with content.
 */
@DevicePreviews
@Composable
private fun FlyerListScreenAuthenticatedPreview() =
    AppTheme {
        FlyerListContent(
            uiState = FlyerListUIState.Content(flyers = sampleFlyers),
            isAuthenticated = true,
            onRefresh = {},
            onFlyerSelected = {},
        )
    }

/**
 * Preview for the Flyer List screen in loading state.
 */
@ScreenPreviews
@Composable
private fun FlyerListScreenLoadingPreview() =
    AppTheme {
        FlyerListContent(
            uiState = FlyerListUIState.Loading(),
            isAuthenticated = false,
            onRefresh = {},
            onFlyerSelected = {},
        )
    }

/**
 * Preview for the Flyer List screen in empty state.
 */
@ScreenPreviews
@Composable
private fun FlyerListScreenEmptyPreview() =
    AppTheme {
        FlyerListContent(
            uiState = FlyerListUIState.Empty(),
            isAuthenticated = false,
            onRefresh = {},
            onFlyerSelected = {},
        )
    }
