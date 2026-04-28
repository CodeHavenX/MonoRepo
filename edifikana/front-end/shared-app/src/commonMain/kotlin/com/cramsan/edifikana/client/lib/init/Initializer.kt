package com.cramsan.edifikana.client.lib.init

import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.edifikana.client.lib.BuildConfig
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.assertlib.assert
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import org.koin.core.component.KoinComponent

/**
 * Initializer class for the application.
 */
class Initializer(
    private val eventLogger: EventLoggerInterface,
    private val authManager: AuthManager,
    private val settingsHolder: SettingsHolder,
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
        seedDefaults()
        enforcePermissions()
    }

    // Once seeded, this value is sticky forever. If a future release ships a new DEFAULT_API_URL (e.g., dev → prod cutover),
    // existing installs will keep pointing at the old URL.
    private fun seedDefaults() {
        if (settingsHolder.getString(FrontEndApplicationSettingKey.BackEndUrl) == null) {
            settingsHolder.saveString(
                FrontEndApplicationSettingKey.BackEndUrl,
                BuildConfig.DEFAULT_API_URL,
            )
        }
    }

    private suspend fun enforcePermissions() {
        val result = authManager.verifyPermissions().getOrNull()
        assert(result == true, TAG, "Permission issue detected. Proceed with caution.")
    }

    companion object {
        private const val TAG = "Initializer"
    }
}
