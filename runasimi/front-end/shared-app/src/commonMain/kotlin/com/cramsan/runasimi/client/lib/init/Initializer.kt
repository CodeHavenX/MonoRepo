package com.cramsan.runasimi.client.lib.init

import com.cramsan.framework.logging.logI
import org.koin.core.component.KoinComponent

/**
 * Initializer class for the application.
 */
class Initializer : KoinComponent {

    /**
     * Start the initialization steps.
     */
    fun startStep() {
        logI(TAG, "Starting initialization steps")
    }

    companion object {
        private const val TAG = "Initializer"
    }
}
