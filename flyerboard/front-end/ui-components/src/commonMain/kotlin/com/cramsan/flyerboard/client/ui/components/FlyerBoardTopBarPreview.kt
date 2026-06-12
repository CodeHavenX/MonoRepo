package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@Composable
@ComponentPreviews
private fun FlyerBoardTopBarPreview() =
    AppTheme {
        FlyerBoardTopBar(
            title = "Title",
            onNavigationIconSelected = {},
        )
    }
