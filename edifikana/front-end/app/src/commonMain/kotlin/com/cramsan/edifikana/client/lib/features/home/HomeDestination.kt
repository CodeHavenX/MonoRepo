package com.cramsan.edifikana.client.lib.features.home

import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.WebPath
import com.cramsan.framework.core.compose.navigation.WebDestination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Management nav graph.
 */
@Serializable
sealed class HomeDestination : WebDestination {
    /** Main management hub screen destination. */
    @Serializable
    @WebPath("/home")
    data object ManagementHub : HomeDestination()

    /** Property management detail screen destination. */
    @Serializable
    @WebPath("/home/property")
    data class PropertyManagementDestination(val propertyId: PropertyId) : HomeDestination()

    /** Add new property screen destination. */
    @Serializable
    @WebPath("/home/add-property")
    data class AddPropertyManagementDestination(val orgId: OrganizationId) : HomeDestination()

    /** Add secondary employee to a property screen destination. */
    @Serializable
    @WebPath("/home/add-secondary-employee")
    data class AddSecondaryEmployeeManagementDestination(val propertyId: PropertyId) : HomeDestination()

    /** Employee detail screen destination. */
    @Serializable
    @WebPath("/home/employee")
    data class EmployeeDestination(val employeeId: EmployeeId) : HomeDestination()

    /** Time card list for all employees in a property screen destination. */
    @Serializable
    @WebPath("/home/timecard")
    data class TimeCardEmployeeListDestination(val propertyId: PropertyId) : HomeDestination()

    /** Time card detail for a single employee screen destination. */
    @Serializable
    @WebPath("/home/timecard")
    data class TimeCardSingleEmployeeDestination(val employeePk: EmployeeId, val propertyId: PropertyId) :
        HomeDestination()

    /** Event log single entry detail screen destination. */
    @Serializable
    @WebPath("/home/event-log")
    data class EventLogSingleItemDestination(val eventLogRecordPk: EventLogEntryId) : HomeDestination()

    /** Add new event log entry screen destination. */
    @Serializable
    @WebPath("/home/event-log-add")
    data class EventLogAddItemDestination(val propertyId: PropertyId) : HomeDestination()

    /** Invite a staff member to the organization screen destination. */
    @Serializable
    @WebPath("/home/invite")
    data class InviteStaffMemberDestination(val orgId: OrganizationId) : HomeDestination()

    override fun toWebPath(): String = HomeDestinationWebRoutes.toWebPath(this)

    companion object {
        /** Parses [path] and returns the matching [HomeDestination], or null if unrecognised. */
        fun fromWebPath(path: String): HomeDestination? = HomeDestinationWebRoutes.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? = HomeDestinationWebRoutes.toWebPath(entry)
    }
}
