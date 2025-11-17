package com.cramsan.runasimi.client.lib.features.main.menu

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowsEvent
import kotlinx.coroutines.launch

/**
 * ViewModel for the Menu screen.
 **/
class MenuViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<MenuEvent, MenuUIState>(
    dependencies,
    MenuUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(RunasimiWindowsEvent.NavigateBack)
        }
    }

    /**
     * Handle drawer item selection.
     */
    fun onDrawerItemSelected(item: SelectableDrawerItem) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    selectedItem = item,
                )
            }
            emitEvent(MenuEvent.CloseDrawer)
        }
    }

    /**
     * Toggle the drawer state.
     */
    fun toggleDrawer() {
        viewModelScope.launch {
            emitEvent(MenuEvent.ToggleDrawer)
        }
    }

    companion object {
        private const val TAG = "MenuViewModel"
    }
}
