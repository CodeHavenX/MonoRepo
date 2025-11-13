package com.cramsan.templatereplaceme.client.lib.init

import com.cramsan.framework.logging.logI

/**
 * Initializer class for the application.
 */
class Initializer {

    /**
     * Start the initialization steps.
     */
    fun startStep() {
        logI(
            TAG,
            "Starting initialization steps",
        )
    }

    companion object {
        private const val TAG = "Initializer"
    }
}
