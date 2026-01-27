package com.cramsan.framework.sample.shared.features.main.logging

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logV
import com.cramsan.framework.logging.logW

/**
 * ViewModel for the Logging screen.
 **/
class LoggingViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<LoggingEvent, LoggingUIState>(
        dependencies,
        LoggingUIState.Initial,
        TAG,
    ) {

    /**
     * Log an info message.
     */
    fun logInfo() {
        logI(TAG, "Info log from LoggingViewModel")
    }

    /**
     * Log a warning message.
     */
    fun logWarning() {
        logW(TAG, "Warning log from LoggingViewModel")
    }

    /**
     * Log an error message.
     */
    fun logError() {
        logE(TAG, "Error log from LoggingViewModel")
    }

    /**
     * Log a debug message.
     */
    fun logDebug() {
        logD(TAG, "Debug log from LoggingViewModel")
    }

    /**
     * Log a verbose message.
     */
    fun logVerbose() {
        logV(TAG, "Verbose log from LoggingViewModel")
    }

    companion object {
        private const val TAG = "LoggingViewModel"
    }
}
