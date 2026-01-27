package com.cramsan.edifikana.client.lib.features.home.drawer

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Management screen.
 **/
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
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Select a drawer item.
     */
    fun selectDrawerItem(drawerItem: SelectableDrawerItem) {
        viewModelScope.launch {
            updateUiState {
                it.copy(selectedItem = drawerItem)
            }
        }
        viewModelScope.launch {
            emitEvent(DrawerEvent.CloseDrawer)
        }
        viewModelScope.launch {
            emitEvent(DrawerEvent.CloseDrawer)
        }
    }

    /**
     * Toggle the navigation state of the drawer.
     */
    fun toggleNavigationState() {
        viewModelScope.launch {
            emitEvent(DrawerEvent.ToggleDrawer)
        }
    }

    companion object {
        private const val TAG = "DrawerViewModel"
    }
}
