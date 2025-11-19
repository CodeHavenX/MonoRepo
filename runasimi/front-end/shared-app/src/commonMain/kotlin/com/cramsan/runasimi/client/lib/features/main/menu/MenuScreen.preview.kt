package com.cramsan.runasimi.client.lib.features.main.menu

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Menu feature screen.
 */
@Preview
@Composable
private fun MenuScreenPreview() {
    AppTheme {
        MenuContent(
            content = MenuUIState(
                selectedItem = null,
                drawerItems = listOf(
                    DrawerItem.Selectable(SelectableDrawerItem.Numbers),
                    DrawerItem.Selectable(SelectableDrawerItem.Verbs),
                    DrawerItem.Selectable(SelectableDrawerItem.Questions),
                ),
            ),
            drawerState = rememberDrawerState(DrawerValue.Open),
            onDrawerItemSelected = { _ -> },
        )
    }
}
