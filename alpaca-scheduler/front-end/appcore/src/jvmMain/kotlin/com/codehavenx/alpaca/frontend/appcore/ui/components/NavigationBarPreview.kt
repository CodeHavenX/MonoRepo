package com.codehavenx.alpaca.frontend.appcore.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.codehavenx.alpaca.frontend.appcore.features.application.NavBarSegment

@Preview
@Composable
private fun NavigationBarPreview() {
    NavigationBar(
        navBar = listOf(
            NavBarSegment.NavBarItem("Home", "home"),
            NavBarSegment.NavBarGroup(
                "Clients",
                listOf(
                    NavBarSegment.NavBarItem("Add Client", "add-client"),
                    NavBarSegment.NavBarItem("List Clients", "list-clients"),
                )
            ),
        ),
        navController = NavHostController(),
        showNavigationBar = true,
    )
}
