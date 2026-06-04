package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.framework.annotations.FrontendManager
import com.cramsan.framework.logging.logI

/**
 * Manager responsible for running application initialization steps.
 */
@FrontendManager
class InitializerManager {
    /**
     * Start the initialization steps.
     */
    fun startStep() {
        logI(TAG, "Starting initialization steps")
    }

    companion object {
        private const val TAG = "InitializerManager"
    }
}
