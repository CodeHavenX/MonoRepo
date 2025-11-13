package com.cramsan.framework.sample.shared.init

import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import org.koin.core.component.KoinComponent

/**
 * Initializer class for the application.
 */
class Initializer(
    private val eventLogger: EventLoggerInterface,
) : KoinComponent {

    /**
     * Start the initialization steps.
     */
    suspend fun startStep() {
        eventLogger.log(
            Severity.INFO,
            TAG,
            "Starting initialization steps",
        )
    }

    companion object {
        private const val TAG = "Initializer"
    }
}
