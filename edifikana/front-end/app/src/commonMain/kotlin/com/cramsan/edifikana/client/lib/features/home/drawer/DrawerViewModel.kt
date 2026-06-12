package com.cramsan.edifikana.client.lib.features.home.drawer

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Management screen.
 **/
@FrontendViewModel
class DrawerViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<DrawerEvent, DrawerUIState>(
        dependencies,
        DrawerUIState.Initial,
        TAG,
    ) {
    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Select a drawer item.
     */
    fun selectDrawerItem(drawerItem: SelectableDrawerItem) {
        viewModelCoroutineScope.launch {
            updateUiState {
                it.copy(selectedItem = drawerItem)
            }
        }
        viewModelCoroutineScope.launch {
            emitEvent(DrawerEvent.CloseDrawer)
        }
        viewModelCoroutineScope.launch {
            emitEvent(DrawerEvent.CloseDrawer)
        }
    }

    /**
     * Toggle the navigation state of the drawer.
     */
    fun toggleNavigationState() {
        viewModelCoroutineScope.launch {
            emitEvent(DrawerEvent.ToggleDrawer)
        }
    }

    companion object {
        private const val TAG = "DrawerViewModel"
    }
}
