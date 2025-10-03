@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.management

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Management nav graph.
 */
@Serializable
sealed class ManagementDestination : Destination {

    /**
     * A class representing navigating to the property list screen within
     * the Management nav graph.
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
     * A class representing navigating to the add primary employee screen.
     */
    @Serializable
    data object AddPrimaryEmployeeManagementDestination : ManagementDestination()

    /**
     * A class representing navigating to the add secondary employee screen.
     */
    @Serializable
    data object AddSecondaryEmployeeManagementDestination : ManagementDestination()

    /**
     * A class representing navigating to the employee Screen.
     */
    @Serializable
    data class EmployeeDestination(
        val employeeId: EmployeeId,
    ) : ManagementDestination()

    /**
     * A class representing navigating to the time card employee list screen.
     */
    @Serializable
    data object TimeCardEmployeeListDestination : ManagementDestination()

    /**
     * A class representing navigating to the time card single employee screen.
     */
    @Serializable
    data class TimeCardSingleEmployeeDestination(val employeePk: EmployeeId) : ManagementDestination()

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
