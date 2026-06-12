package com.cramsan.flyerboard.client.lib.features.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cramsan.flyerboard.client.ui.components.FlyerBoardTopBarTab
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the FlyerBoard window chrome on the splash destination, where the top bar is
 * hidden.
 */
@ScreenPreviews
@Composable
private fun FlyerBoardWindowSplashPreview() =
    AppTheme {
        FlyerBoardWindowChrome(
            showTopBar = false,
            authState = AuthState.Unauthenticated,
            tabs = emptyList(),
            onSignIn = {},
            onSignOut = {},
            snackbarHostState = remember { SnackbarHostState() },
            content = { paddingValues -> PlaceholderContent(paddingValues) },
        )
    }

/**
 * Preview for the FlyerBoard window chrome on a top-level destination while signed out.
 */
@ScreenPreviews
@Composable
private fun FlyerBoardWindowSignedOutPreview() =
    AppTheme {
        FlyerBoardWindowChrome(
            showTopBar = true,
            authState = AuthState.Unauthenticated,
            tabs =
            listOf(
                FlyerBoardTopBarTab(label = "Browse", selected = true, onClick = {}),
                FlyerBoardTopBarTab(label = "My Flyers", selected = false, onClick = {}),
                FlyerBoardTopBarTab(label = "Archive", selected = false, onClick = {}),
            ),
            onSignIn = {},
            onSignOut = {},
            snackbarHostState = remember { SnackbarHostState() },
            content = { paddingValues -> PlaceholderContent(paddingValues) },
        )
    }

/**
 * Preview for the FlyerBoard window chrome on a top-level destination while signed in as an
 * admin, showing the Moderation tab.
 */
@ScreenPreviews
@Composable
private fun FlyerBoardWindowAdminSignedInPreview() =
    AppTheme {
        FlyerBoardWindowChrome(
            showTopBar = true,
            authState = AuthState.Authenticated(isAdmin = true),
            tabs =
            listOf(
                FlyerBoardTopBarTab(label = "Browse", selected = false, onClick = {}),
                FlyerBoardTopBarTab(label = "My Flyers", selected = true, onClick = {}),
                FlyerBoardTopBarTab(label = "Archive", selected = false, onClick = {}),
                FlyerBoardTopBarTab(label = "Moderation", selected = false, onClick = {}),
            ),
            onSignIn = {},
            onSignOut = {},
            snackbarHostState = remember { SnackbarHostState() },
            content = { paddingValues -> PlaceholderContent(paddingValues) },
        )
    }

@Composable
private fun PlaceholderContent(paddingValues: PaddingValues) =
    Box(
        modifier =
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Content",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
