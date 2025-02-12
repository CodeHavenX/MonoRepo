package com.cramsan.edifikana.client.lib.features.main.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the Home feature screen.
 */
@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreenContent(
        uiState = HomeUIModel.Empty,
        onAccountButtonClicked = {},
        onAdminButtonClicked = {},
        onPropertySelected = {},
    )
}
