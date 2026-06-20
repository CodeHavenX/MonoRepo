package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun FlyerDetailLoadingPreview() =
    AppTheme(dynamicColor = false) {
        FlyerDetailContent(
            uiState = FlyerDetailUIState.Loading,
        )
    }

@ScreenPreviews
@Composable
private fun FlyerDetailNotFoundPreview() =
    AppTheme(dynamicColor = false) {
        FlyerDetailContent(
            uiState = FlyerDetailUIState.NotFound,
        )
    }

@DevicePreviews
@Composable
private fun FlyerDetailApprovedPreview() =
    AppTheme(dynamicColor = false) {
        FlyerDetailContent(
            uiState =
            FlyerDetailUIState.Content(
                flyer =
                FlyerModel(
                    id = FlyerId("1"),
                    title = "Community Cleanup Day — Riverside Park",
                    description =
                    "Join us for our monthly community cleanup at Riverside Park! We'll be gathering" +
                        " near the north entrance to pick up litter, trim overgrown paths, and ensure our favorite" +
                        " local spot stays beautiful for everyone.",
                    fileUrl = null,
                    status = FlyerStatus.APPROVED,
                    expiresAt = "May 1",
                    uploaderId = UserId("cramsan"),
                    createdAt = "Apr 17, 2026",
                    updatedAt = "Apr 17, 2026",
                ),
            ),
        )
    }

@DevicePreviews
@Composable
private fun FlyerDetailPendingWithActionsPreview() =
    AppTheme(dynamicColor = false) {
        FlyerDetailContent(
            uiState =
            FlyerDetailUIState.Content(
                flyer =
                FlyerModel(
                    id = FlyerId("2"),
                    title = "Community Cleanup Day — Riverside Park",
                    description =
                    "Join us for our monthly community cleanup at Riverside Park! We'll be gathering" +
                        " near the north entrance to pick up litter, trim overgrown paths, and ensure our favorite" +
                        " local spot stays beautiful for everyone. Bags, gloves, and pickers will be provided." +
                        " Please wear comfortable shoes and bring a water bottle.",
                    fileUrl = null,
                    status = FlyerStatus.PENDING,
                    expiresAt = "May 1",
                    uploaderId = UserId("cramsan"),
                    createdAt = "Apr 17, 2026",
                    updatedAt = "Apr 17, 2026",
                ),
                canEdit = true,
                canModerate = true,
            ),
        )
    }

@ScreenPreviews
@Composable
private fun FlyerDetailRejectedPreview() =
    AppTheme(dynamicColor = false) {
        FlyerDetailContent(
            uiState =
            FlyerDetailUIState.Content(
                flyer =
                FlyerModel(
                    id = FlyerId("3"),
                    title = "Garage Sale This Weekend",
                    description = "Come find a bargain at 42 Oak Street. Furniture, clothes, tools and more.",
                    fileUrl = null,
                    status = FlyerStatus.REJECTED,
                    expiresAt = null,
                    uploaderId = UserId("user2"),
                    createdAt = "Apr 20, 2026",
                    updatedAt = "Apr 21, 2026",
                    rejectionReason = "Content does not meet community guidelines.",
                ),
            ),
        )
    }
