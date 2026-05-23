package com.cramsan.flyerboard.client.lib.features.main.my_flyers

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import org.jetbrains.compose.ui.tooling.preview.Preview

private val mixedFlyers =
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
            status = FlyerStatus.PENDING,
            expiresAt = null,
            uploaderId = UserId("user1"),
            createdAt = "2026-04-10",
            updatedAt = "2026-04-10",
        ),
        FlyerModel(
            id = FlyerId("3"),
            title = "Rejected Flyer",
            description = "This flyer was rejected by a moderator.",
            fileUrl = null,
            status = FlyerStatus.REJECTED,
            expiresAt = null,
            uploaderId = UserId("user1"),
            createdAt = "2026-04-12",
            updatedAt = "2026-04-13",
        ),
        FlyerModel(
            id = FlyerId("4"),
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

private val archivedOnlyFlyers =
    listOf(
        FlyerModel(
            id = FlyerId("5"),
            title = "Spring Clean-Up Drive",
            description = "Neighbourhood spring clean-up. Come join us!",
            fileUrl = null,
            status = FlyerStatus.ARCHIVED,
            expiresAt = "2026-03-15",
            uploaderId = UserId("user1"),
            createdAt = "2026-03-01",
            updatedAt = "2026-03-16",
        ),
        FlyerModel(
            id = FlyerId("6"),
            title = "Block Party 2025",
            description = "Annual block party — food, games, and live music.",
            fileUrl = null,
            status = FlyerStatus.ARCHIVED,
            expiresAt = "2025-08-20",
            uploaderId = UserId("user1"),
            createdAt = "2025-08-01",
            updatedAt = "2025-08-21",
        ),
    )

@Preview
@Composable
private fun MyFlyersLoadingPreview() =
    AppTheme(dynamicColor = false) {
        MyFlyersContent(
            uiState = MyFlyersUIState.Loading,
            onNavigateBack = {},
            onRefresh = {},
            onFlyerSelected = {},
            onEditFlyer = {},
        )
    }

@Preview
@Composable
private fun MyFlyersEmptyPreview() =
    AppTheme(dynamicColor = false) {
        MyFlyersContent(
            uiState = MyFlyersUIState.Empty,
            onNavigateBack = {},
            onRefresh = {},
            onFlyerSelected = {},
            onEditFlyer = {},
        )
    }

@Preview
@Composable
private fun MyFlyersWithFlyersPreview() =
    AppTheme(dynamicColor = false) {
        MyFlyersContent(
            uiState = MyFlyersUIState.Content(flyers = mixedFlyers),
            onNavigateBack = {},
            onRefresh = {},
            onFlyerSelected = {},
            onEditFlyer = {},
        )
    }

@Preview
@Composable
private fun MyFlyersArchivedOnlyPreview() =
    AppTheme(dynamicColor = false) {
        MyFlyersContent(
            uiState = MyFlyersUIState.Content(flyers = archivedOnlyFlyers),
            onNavigateBack = {},
            onRefresh = {},
            onFlyerSelected = {},
            onEditFlyer = {},
        )
    }
