package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import org.jetbrains.compose.ui.tooling.preview.Preview

private val sampleFlyer = FlyerModel(
    id = FlyerId("1"),
    title = "Community Yard Sale",
    description = "Everything must go! Furniture, clothes, books and more. Come early for the best deals.",
    fileUrl = null,
    status = FlyerStatus.APPROVED,
    expiresAt = "2026-05-01",
    uploaderId = UserId("user1"),
    createdAt = "2026-04-01",
    updatedAt = "2026-04-01",
)

/**
 * Preview for the Flyer Detail screen with content.
 */
@Preview
@Composable
private fun FlyerDetailScreenPreview() = AppTheme {
    FlyerDetailContent(
        uiState = FlyerDetailUIState(
            isLoading = false,
            flyer = sampleFlyer,
        ),
        onNavigateBack = {},
    )
}

/**
 * Preview for the Flyer Detail screen in loading state.
 */
@Preview
@Composable
private fun FlyerDetailScreenLoadingPreview() = AppTheme {
    FlyerDetailContent(
        uiState = FlyerDetailUIState(
            isLoading = true,
            flyer = null,
        ),
        onNavigateBack = {},
    )
}

/**
 * Preview for the Flyer Detail screen when flyer is not found.
 */
@Preview
@Composable
private fun FlyerDetailScreenNotFoundPreview() = AppTheme {
    FlyerDetailContent(
        uiState = FlyerDetailUIState(
            isLoading = false,
            flyer = null,
        ),
        onNavigateBack = {},
    )
}
