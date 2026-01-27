package com.cramsan.edifikana.client.lib.features.home.drawer

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Management feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class DrawerUIState(
    val title: String?,
    val selectedItem: SelectableDrawerItem? = SelectableDrawerItem.Property,
    val drawerItems: List<DrawerItem> = emptyList(),
) : ViewModelUIState {
    companion object {
        val Initial = DrawerUIState(
            title = null,
            drawerItems = listOf(
                DrawerItem.Title("Management"),
                DrawerItem.Selectable(SelectableDrawerItem.Property),
                DrawerItem.Selectable(SelectableDrawerItem.Organization),
            ),
        )
    }
}

/**
 * Drawer item for the management screen.
 *
 * This class models the items in the drawer.
 * For modeling more specific details of the page, see the respective UI model class.
 */
sealed class DrawerItem {

    /**
     * Title item for the drawer.
     */
    data class Title(val title: String) : DrawerItem()

    /**
     * Selectable item for the drawer.
     */
    data class Selectable(val item: SelectableDrawerItem) : DrawerItem()

    /**
     * Divider item for the drawer.
     */
    object Divider : DrawerItem()
}

/**
 * Selectable drawer item for the management screen.
 */
enum class SelectableDrawerItem {
    Property,
    Organization,
    ResidentMode,
}
