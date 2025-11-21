package com.cramsan.runasimi.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Menu feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class MenuUIState(
    val selectedItem: SelectableDrawerItem? = SelectableDrawerItem.Numbers,
    val drawerItems: List<DrawerItem> = emptyList(),
) : ViewModelUIState {
    companion object {
        val Initial = MenuUIState(
            drawerItems = listOf(
                DrawerItem.Selectable(SelectableDrawerItem.Numbers),
                DrawerItem.Selectable(SelectableDrawerItem.Verbs),
                DrawerItem.Selectable(SelectableDrawerItem.Questions),
            ),
        )
    }
}

/**
 * Represents an item in the navigation drawer.
 */
sealed class DrawerItem {

    /**
     * A selectable item in the navigation drawer.
     */
    data class Selectable(
        val item: SelectableDrawerItem,
    ) : DrawerItem()
}

/**
 * Represents the selectable items in the navigation drawer.
 */
enum class SelectableDrawerItem {
    Numbers,
    Verbs,
    Questions,
}
