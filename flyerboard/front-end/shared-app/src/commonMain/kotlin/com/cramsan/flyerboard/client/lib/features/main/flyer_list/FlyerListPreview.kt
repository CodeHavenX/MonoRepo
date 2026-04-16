package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import org.jetbrains.compose.ui.tooling.preview.Preview

private val sampleFlyers = listOf(
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

/**
 * Preview for the Flyer List screen with content.
 */
@Preview
@Composable
private fun FlyerListScreenPreview() = AppTheme {
    FlyerListContent(
        uiState = FlyerListUIState(
            isLoading = false,
            flyers = sampleFlyers,
            errorMessage = null,
        ),
        onRefresh = {},
        onFlyerSelected = {},
    )
}

/**
 * Preview for the Flyer List screen in loading state.
 */
@Preview
@Composable
private fun FlyerListScreenLoadingPreview() = AppTheme {
    FlyerListContent(
        uiState = FlyerListUIState(
            isLoading = true,
            flyers = emptyList(),
            errorMessage = null,
        ),
        onRefresh = {},
        onFlyerSelected = {},
    )
}

/**
 * Preview for the Flyer List screen in empty state.
 */
@Preview
@Composable
private fun FlyerListScreenEmptyPreview() = AppTheme {
    FlyerListContent(
        uiState = FlyerListUIState(
            isLoading = false,
            flyers = emptyList(),
            errorMessage = null,
        ),
        onRefresh = {},
        onFlyerSelected = {},
    )
}
