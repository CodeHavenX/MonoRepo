package com.codehavenx.alpaca.frontend.appcore.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController

@Preview
@Composable
private fun TopBarPreview() {
    TopBar(
        navController = NavHostController(),
        onNavIconClick = { },
        onSignOutClick = { },
    )
}
