package com.cramsan.framework.sample.shared.features.main.halt

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.halt.HaltUtil

/**
 * ViewModel for the HaltUtil screen.
 **/
class HaltUtilViewModel(
    dependencies: ViewModelDependencies,
    private val haltUtil: HaltUtil,
) : BaseViewModel<HaltUtilEvent, HaltUtilUIState>(
    dependencies,
    HaltUtilUIState.Initial,
    TAG,
) {

    /**
     * Crash the app.
     */
    fun crashApp() {
        haltUtil.crashApp()
    }

    /**
     * Stop the current thread.
     */
    fun stopThread() {
        haltUtil.stopThread()
    }

    /**
     * Resume the current thread.
     */
    fun resumeThread() {
        haltUtil.resumeThread()
    }

    companion object {
        private const val TAG = "HaltUtilViewModel"
    }
}
