package com.cramsan.runasimi.client.lib.features.main.menu

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowsEvent
import com.cramsan.runasimi.client.lib.settings.RunasimiSettingKey
import kotlinx.coroutines.launch

/**
 * ViewModel for the Menu screen.
 **/
class MenuViewModel(
    dependencies: ViewModelDependencies,
    private val preferencesManager: PreferencesManager,
) : BaseViewModel<MenuEvent, MenuUIState>(
    dependencies,
    MenuUIState.Initial,
    TAG,
) {

    /**
     * Initialize the ViewModel.
     */
    fun initialize() {
        viewModelScope.launch {
            val selectedItemOrdinal = preferencesManager
                .getIntPreference(RunasimiSettingKey.MainMenuSelectedDrawerItem).getOrNull()
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
        viewModelScope.launch {
            emitWindowEvent(RunasimiWindowsEvent.NavigateBack)
        }
    }

    /**
     * Handle drawer item selection.
     */
    fun onDrawerItemSelected(item: SelectableDrawerItem) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            emitEvent(MenuEvent.ToggleDrawer)
        }
    }

    companion object {
        private const val TAG = "MenuViewModel"
    }
}
