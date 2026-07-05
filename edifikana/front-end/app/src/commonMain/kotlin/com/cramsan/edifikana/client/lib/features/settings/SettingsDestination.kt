package com.cramsan.edifikana.client.lib.features.settings

import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.WebPath
import com.cramsan.framework.core.compose.navigation.WebDestination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Settings graph.
 */
@Serializable
sealed class SettingsDestination : WebDestination {
    /** General app settings screen destination. */
    @Serializable
    @WebPath("/settings")
    data object GeneralSettingsDestination : SettingsDestination()

    /** My Organizations list screen destination. */
    @Serializable
    @WebPath("/settings/organizations")
    data object MyOrganizationsDestination : SettingsDestination()

    /** Organization detail screen destination. */
    @Serializable
    @WebPath("/settings/organizations/detail")
    data class OrganizationDetailDestination(val orgId: OrganizationId) : SettingsDestination()

    /** Transfer ownership screen destination. */
    @Serializable
    @WebPath("/settings/organizations/transfer")
    data class TransferOwnershipDestination(val orgId: OrganizationId) : SettingsDestination()

    override fun toWebPath(): String = SettingsDestinationWebRoutes.toWebPath(this)

    companion object {
        /** Parses [path] and returns the matching [SettingsDestination], or null if unrecognised. */
        fun fromWebPath(path: String): SettingsDestination? = SettingsDestinationWebRoutes.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? = SettingsDestinationWebRoutes.toWebPath(entry)
    }
}
