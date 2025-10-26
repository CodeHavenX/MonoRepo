package com.cramsan.runasimi.client.lib.features.application

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.runasimi.client.lib.init.Initializer

/**
 * View model for the entire application.
 */
class RunasimiApplicationViewModel(
    private val initHandler: Initializer,
    dependencies: ViewModelDependencies,
) : BaseViewModel<RunasimiApplicationViewModelEvent, RunasimiApplicationUIState>(
    dependencies,
    RunasimiApplicationUIState(),
    TAG
) {

    /**
     * Run code that should be run before the application fully launches.
     */
    fun initialize() {
        initHandler.startStep()
    }

    companion object {
        private const val TAG = "RunasimiProcessViewModel"
    }
}
