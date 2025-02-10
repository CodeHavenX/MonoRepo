package com.codehavenx.alpaca.frontend.appcore.features.home

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies

/**
 * The ViewModel for the Home screen.
 */
class HomeViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<HomeEvent, HomeUIState>(
    dependencies,
    HomeUIState.Initial,
    TAG,
) {
    companion object {
        private const val TAG = "HomeViewModel"
    }
}
