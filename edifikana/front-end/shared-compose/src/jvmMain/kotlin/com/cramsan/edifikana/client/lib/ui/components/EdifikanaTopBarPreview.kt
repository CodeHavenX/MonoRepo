package com.cramsan.edifikana.client.lib.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.ui.theme.AppTheme
import androidx.navigation.compose.rememberNavController

@Composable
@Preview
private fun EdifikanaTopBarPreview() = AppTheme {
    EdifikanaTopBar(
        title = "Title",
        navHostController = rememberNavController(),
        onCloseClicked = {},
    )
}
