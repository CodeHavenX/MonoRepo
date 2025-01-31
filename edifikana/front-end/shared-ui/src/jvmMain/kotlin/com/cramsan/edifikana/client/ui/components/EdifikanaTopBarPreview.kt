package com.cramsan.edifikana.client.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.ui.theme.AppTheme

@Composable
@Preview
private fun EdifikanaTopBarPreview() = AppTheme {
    EdifikanaTopBar(
        title = "Title",
        navHostController = rememberNavController(),
        onCloseClicked = {},
    )
}
