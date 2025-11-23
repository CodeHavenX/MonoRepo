package com.cramsan.edifikana.client.lib.init

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.lib.utils.requireSuccess
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
    private val organizationManager: OrganizationManager,
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
        loadOrganization()
    }

    private suspend fun enforcePermissions() {
        val result = authManager.verifyPermissions().getOrNull()
        assert(result == true, TAG, "Permission issue detected. Proceed with caution.")
    }

    private suspend fun loadOrganization() {
        val user = authManager.getUser().getOrNull()
        if (user == null) {
            eventLogger.log(
                Severity.WARNING,
                TAG,
                "No authenticated user found. Skipping organization load.",
            )
            return
        }

        eventLogger.log(
            Severity.INFO,
            TAG,
            "Loading organization data",
        )
        val result = organizationManager.getOrganizations()
        if (result.isFailure) {
            eventLogger.log(
                Severity.ERROR,
                TAG,
                "Failed to load organizations: ${result.exceptionOrNull()?.message}",
            )
            return
        }
        val organizations = result.requireSuccess()
        if (organizations.isEmpty()) {
            eventLogger.log(Severity.WARNING, TAG, "No organizations found for the user.")
            return
        }
        val selectedOrganization = organizations.first()
        organizationManager.setActiveOrganization(selectedOrganization.id)
    }

    companion object {
        private const val TAG = "Initializer"
    }
}
