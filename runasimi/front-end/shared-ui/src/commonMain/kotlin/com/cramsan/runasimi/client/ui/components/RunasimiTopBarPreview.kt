package com.cramsan.runasimi.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun RunasimiTopBarPreview() = AppTheme {
    RunasimiTopBar(
        title = "Title",
        onNavigationIconSelected = {},
    )
}
