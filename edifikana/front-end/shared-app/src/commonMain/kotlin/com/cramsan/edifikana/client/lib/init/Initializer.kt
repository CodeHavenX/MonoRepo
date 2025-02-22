package com.cramsan.edifikana.client.lib.init

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.assert
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.thread.ThreadUtilInterface
import org.koin.core.component.KoinComponent

/**
 * Initializer class for the application.
 */
@Suppress("UnusedPrivateProperty")
class Initializer(
    private val eventLogger: EventLoggerInterface,
    private val assertUtil: AssertUtilInterface, // This is here to force the initialization of the assert util
    private val threadUtil: ThreadUtilInterface, // This is here to force the initialization of the thread util
    private val authManager: AuthManager,
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
        enforcePermissions()
    }

    private suspend fun enforcePermissions() {
        val result = authManager.verifyPermissions().getOrThrow()
        assert(result, TAG, "Permission issue detected. Proceed with caution.")
    }

    companion object {
        private const val TAG = "Initializer"
    }
}
