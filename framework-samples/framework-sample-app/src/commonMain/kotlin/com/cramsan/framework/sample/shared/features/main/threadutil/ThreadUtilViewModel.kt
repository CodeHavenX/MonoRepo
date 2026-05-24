package com.cramsan.framework.sample.shared.features.main.threadutil

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.thread.ThreadUtilInterface
import kotlinx.coroutines.launch

/**
 * ViewModel for the ThreadUtil screen.
 */
@FrontendViewModel
class ThreadUtilViewModel(dependencies: ViewModelDependencies, private val threadUtil: ThreadUtilInterface) :
    BaseViewModel<ThreadUtilEvent, ThreadUtilUIState>(
        dependencies,
        ThreadUtilUIState.Initial,
        TAG,
    ) {
    /**
     * Check and display whether the current thread is the UI thread.
     */
    fun checkIsUIThread() {
        val result = threadUtil.isUIThread()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isUIThread = result, lastAction = "isUIThread() → $result") }
        }
    }

    /**
     * Check and display whether the current thread is a background thread.
     */
    fun checkIsBackgroundThread() {
        val result = threadUtil.isBackgroundThread()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isBackgroundThread = result, lastAction = "isBackgroundThread() → $result") }
        }
    }

    /**
     * Call assertIsUIThread — passes when on the UI thread, logs an error otherwise.
     */
    fun assertIsUIThread() {
        threadUtil.assertIsUIThread()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "assertIsUIThread() called (check logs for result)") }
        }
    }

    /**
     * Call assertIsBackgroundThread — passes when on a background thread, logs an error otherwise.
     */
    fun assertIsBackgroundThread() {
        threadUtil.assertIsBackgroundThread()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "assertIsBackgroundThread() called (check logs for result)") }
        }
    }

    /**
     * Navigate back to the main menu.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "ThreadUtilViewModel"
    }
}
