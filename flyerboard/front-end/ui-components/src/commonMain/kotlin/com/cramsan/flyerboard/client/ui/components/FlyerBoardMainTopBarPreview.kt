package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.DevicePreviews

@DevicePreviews
@Composable
private fun FlyerBoardMainTopBarSignedOutPreview() =
    AppTheme {
        FlyerBoardMainTopBar(
            tabs =
            listOf(
                FlyerBoardTopBarTab(label = "Browse", selected = true, onClick = {}),
                FlyerBoardTopBarTab(label = "My Flyers", selected = false, onClick = {}),
                FlyerBoardTopBarTab(label = "Archive", selected = false, onClick = {}),
            ),
            isAuthenticated = false,
            onSignIn = {},
            onSignOut = {},
        )
    }

@DevicePreviews
@Composable
private fun FlyerBoardMainTopBarAdminSignedInPreview() =
    AppTheme {
        FlyerBoardMainTopBar(
            tabs =
            listOf(
                FlyerBoardTopBarTab(label = "Browse", selected = false, onClick = {}),
                FlyerBoardTopBarTab(label = "My Flyers", selected = true, onClick = {}),
                FlyerBoardTopBarTab(label = "Archive", selected = false, onClick = {}),
                FlyerBoardTopBarTab(label = "Moderation", selected = false, onClick = {}),
            ),
            isAuthenticated = true,
            onSignIn = {},
            onSignOut = {},
        )
    }
