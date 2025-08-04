@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.management

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Management activity.
 */
@Serializable
sealed class ManagementDestination : Destination {

    /**
     * A class representing navigating to the property list screen within
     * the Management activity.
     */
    @Serializable
    data object PropertiesManagementDestination : ManagementDestination()

    /**
     * A class representing navigating to the property screen.
     */
    @Serializable
    data class PropertyManagementDestination(
        val propertyId: PropertyId,
    ) : ManagementDestination()

    /**
     * A class representing navigating to the add property screen.
     */
    @Serializable
    data object AddPropertyManagementDestination : ManagementDestination()

    /**
     * A class representing navigating to the add primary staff screen.
     */
    @Serializable
    data object AddPrimaryStaffManagementDestination : ManagementDestination()

    /**
     * A class representing navigating to the add secondary staff screen.
     */
    @Serializable
    data object AddSecondaryStaffManagementDestination : ManagementDestination()

    /**
     * A class representing navigating to the Staff Screen.
     */
    @Serializable
    data class StaffDestination(
        val staffId: StaffId,
    ) : ManagementDestination()

    /**
     * A class representing navigating to the time card staff list screen.
     */
    @Serializable
    data object TimeCardStaffListDestination : ManagementDestination()

    /**
     * A class representing navigating to the time card single staff screen.
     */
    @Serializable
    data class TimeCardSingleStaffDestination(val staffPk: StaffId) : ManagementDestination()

    /**
     * A class representing navigating to the event log single item screen.
     */
    @Serializable
    data class EventLogSingleItemDestination(val eventLogRecordPk: EventLogEntryId) : ManagementDestination()

    /**
     * A class representing navigating to the event log add item screen.
     */
    @Serializable
    data object EventLogAddItemDestination : ManagementDestination()

    /**
     * A class representing navigating to the management screen.
     */
    @Serializable
    data object ManagementHub : ManagementDestination()
}
