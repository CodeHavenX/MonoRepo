package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the PropertiesOverview screen.
 **/
class PropertiesOverviewViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<PropertiesOverviewEvent, PropertiesOverviewUIState>(
    dependencies,
    PropertiesOverviewUIState.Initial,
    TAG,
) {
    fun initialize() {

    }


    companion object {
        private const val TAG = "PropertiesOverviewViewModel"
    }
}
