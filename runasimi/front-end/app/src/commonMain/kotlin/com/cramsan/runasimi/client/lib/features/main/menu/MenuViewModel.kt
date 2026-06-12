package com.cramsan.runasimi.client.lib.features.main.menu

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowsEvent
import com.cramsan.runasimi.client.lib.settings.RunasimiSettingKey
import kotlinx.coroutines.launch

/**
 * ViewModel for the Menu screen.
 **/
@FrontendViewModel
class MenuViewModel(dependencies: ViewModelDependencies, private val preferencesManager: PreferencesManager) :
    BaseViewModel<MenuEvent, MenuUIState>(
        dependencies,
        MenuUIState.Initial,
        TAG,
    ) {
    /**
     * Initialize the ViewModel.
     */
    fun initialize() {
        viewModelCoroutineScope.launch {
            val selectedItemOrdinal =
                preferencesManager
                    .getIntPreference(RunasimiSettingKey.MainMenuSelectedDrawerItem)
                    .getOrNull()
            val selectedItem = selectedItemOrdinal?.let { SelectableDrawerItem.entries.getOrNull(it) }
            if (selectedItem != null) {
                updateUiState {
                    it.copy(
                        selectedItem = selectedItem,
                    )
                }
            }
        }
    }

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(RunasimiWindowsEvent.NavigateBack)
        }
    }

    /**
     * Handle drawer item selection.
     */
    fun onDrawerItemSelected(item: SelectableDrawerItem) {
        viewModelCoroutineScope.launch {
            preferencesManager.updatePreference(RunasimiSettingKey.MainMenuSelectedDrawerItem, item.ordinal)
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
        viewModelCoroutineScope.launch {
            emitEvent(MenuEvent.ToggleDrawer)
        }
    }

    companion object {
        private const val TAG = "MenuViewModel"
    }
}
