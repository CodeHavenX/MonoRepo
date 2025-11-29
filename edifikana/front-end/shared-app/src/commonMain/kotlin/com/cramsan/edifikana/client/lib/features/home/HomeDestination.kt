@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.home

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Management nav graph.
 */
@Serializable
sealed class HomeDestination : Destination {

    /**
     * A class representing navigating to the property screen.
     */
    @Serializable
    data class PropertyManagementDestination(
        val propertyId: PropertyId,
    ) : HomeDestination()

    /**
     * A class representing navigating to the add property screen.
     */
    @Serializable
    data class AddPropertyManagementDestination(
        val orgId: OrganizationId,
    ) : HomeDestination()

    /**
     * A class representing navigating to the add primary employee screen.
     */
    @Serializable
    data class AddPrimaryEmployeeManagementDestination(
        val orgId: OrganizationId,
    ) : HomeDestination()

    /**
     * A class representing navigating to the add secondary employee screen.
     */
    @Serializable
    data class AddSecondaryEmployeeManagementDestination(
        val propertyId: PropertyId,
    ) : HomeDestination()

    /**
     * A class representing navigating to the employee Screen.
     */
    @Serializable
    data class EmployeeDestination(
        val employeeId: EmployeeId,
    ) : HomeDestination()

    /**
     * A class representing navigating to the time card employee list screen.
     */
    @Serializable
    data class TimeCardEmployeeListDestination(
        val propertyId: PropertyId,
    ) : HomeDestination()

    /**
     * A class representing navigating to the time card single employee screen.
     */
    @Serializable
    data class TimeCardSingleEmployeeDestination(
        val employeePk: EmployeeId,
        val propertyId: PropertyId,
    ) : HomeDestination()

    /**
     * A class representing navigating to the event log single item screen.
     */
    @Serializable
    data class EventLogSingleItemDestination(val eventLogRecordPk: EventLogEntryId) : HomeDestination()

    /**
     * A class representing navigating to the event log add item screen.
     */
    @Serializable
    data class EventLogAddItemDestination(
        val propertyId: PropertyId
    ) : HomeDestination()

    /**
     * A class representing navigating to the management screen.
     */
    @Serializable
    data object ManagementHub : HomeDestination()
}
