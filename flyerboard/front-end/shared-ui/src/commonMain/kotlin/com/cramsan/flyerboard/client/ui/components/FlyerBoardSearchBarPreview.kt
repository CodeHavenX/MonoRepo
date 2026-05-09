package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SearchBarEmptyPreview() = AppTheme {
    FlyerBoardSearchBar(
        query = "",
        onQueryChange = {},
        placeholder = "Search flyers...",
    )
}

@Preview
@Composable
private fun SearchBarWithTextPreview() = AppTheme {
    FlyerBoardSearchBar(
        query = "summer",
        onQueryChange = {},
        placeholder = "Search flyers...",
    )
}
