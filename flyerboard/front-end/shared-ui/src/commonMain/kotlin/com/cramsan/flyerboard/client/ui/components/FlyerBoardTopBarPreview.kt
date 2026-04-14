package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun FlyerBoardTopBarPreview() = AppTheme {
    FlyerBoardTopBar(
        title = "Title",
        onNavigationIconSelected = {},
    )
}
