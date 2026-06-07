package com.cramsan.edifikana.client.lib.features.home.drawer

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
fun DrawerScreenPreview() {
    DrawerContent(
        content =
        DrawerUIState(
            title = "Management",
            drawerItems =
            listOf(
                DrawerItem.Title("Management"),
                DrawerItem.Selectable(SelectableDrawerItem.Property),
                DrawerItem.Selectable(SelectableDrawerItem.Organization),
                DrawerItem.Divider,
                DrawerItem.Title("View Mode"),
                DrawerItem.Selectable(SelectableDrawerItem.ResidentMode),
            ),
            selectedItem = null,
        ),
        drawerState =
        DrawerState(
            initialValue = DrawerValue.Open,
        ),
        onDrawerItemSelected = {},
    )
}
