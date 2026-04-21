package com.cramsan.flyerboard.client.lib.features.main.my_flyers

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
        status = FlyerStatus.PENDING,
        expiresAt = null,
        uploaderId = UserId("user1"),
        createdAt = "2026-04-10",
        updatedAt = "2026-04-10",
    ),
    FlyerModel(
        id = FlyerId("3"),
        title = "Old Event Flyer",
        description = "This event has already passed.",
        fileUrl = null,
        status = FlyerStatus.ARCHIVED,
        expiresAt = "2026-03-01",
        uploaderId = UserId("user1"),
        createdAt = "2026-03-01",
        updatedAt = "2026-03-15",
    ),
)

/**
 * Preview for the My Flyers screen with content.
 */
@Preview
@Composable
private fun MyFlyersScreenPreview() = AppTheme {
    MyFlyersContent(
        uiState = MyFlyersUIState(
            isLoading = false,
            flyers = sampleFlyers,
            errorMessage = null,
        ),
        onNavigateBack = {},
        onRefresh = {},
        onFlyerSelected = {},
        onEditFlyer = {},
    )
}

/**
 * Preview for the My Flyers screen in loading state.
 */
@Preview
@Composable
private fun MyFlyersScreenLoadingPreview() = AppTheme {
    MyFlyersContent(
        uiState = MyFlyersUIState(
            isLoading = true,
            flyers = emptyList(),
            errorMessage = null,
        ),
        onNavigateBack = {},
        onRefresh = {},
        onFlyerSelected = {},
        onEditFlyer = {},
    )
}

/**
 * Preview for the My Flyers screen in empty state.
 */
@Preview
@Composable
private fun MyFlyersScreenEmptyPreview() = AppTheme {
    MyFlyersContent(
        uiState = MyFlyersUIState(
            isLoading = false,
            flyers = emptyList(),
            errorMessage = null,
        ),
        onNavigateBack = {},
        onRefresh = {},
        onFlyerSelected = {},
        onEditFlyer = {},
    )
}
