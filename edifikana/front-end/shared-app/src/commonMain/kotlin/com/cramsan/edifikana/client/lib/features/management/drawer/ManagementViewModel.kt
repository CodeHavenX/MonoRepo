package com.cramsan.edifikana.client.lib.features.management.drawer

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Management screen.
 **/
class ManagementViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<ManagementEvent, ManagementUIState>(
    dependencies,
    ManagementUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitApplicationEvent(EdifikanaApplicationEvent.NavigateBack)
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
            emitEvent(ManagementEvent.CloseDrawer)
        }
    }

    /**
     * Toggle the navigation state of the drawer.
     */
    fun toggleNavigationState() {
        viewModelScope.launch {
            emitEvent(ManagementEvent.ToggleDrawer)
        }
    }

    companion object {
        private const val TAG = "ManagementViewModel"
    }
}
