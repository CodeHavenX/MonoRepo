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

    companion object {
        private const val TAG = "MenuViewModel"
    }
}
