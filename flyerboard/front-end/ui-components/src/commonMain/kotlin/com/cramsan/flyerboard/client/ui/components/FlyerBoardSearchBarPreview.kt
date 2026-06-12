package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun SearchBarEmptyPreview() =
    AppTheme {
        FlyerBoardSearchBar(
            query = "",
            onQueryChange = {},
            placeholder = "Search flyers...",
        )
    }

@ComponentPreviews
@Composable
private fun SearchBarWithTextPreview() =
    AppTheme {
        FlyerBoardSearchBar(
            query = "summer",
            onQueryChange = {},
            placeholder = "Search flyers...",
        )
    }
