@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.home

import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.core.compose.navigation.WebDestination
import com.cramsan.framework.core.compose.navigation.toWebPathIfRoute
import com.cramsan.framework.core.compose.navigation.webRoute
import kotlinx.serialization.Serializable

/**
 * Destinations in the Management nav graph.
 */
@Serializable
sealed class HomeDestination : WebDestination {
    /** Main management hub screen destination. */
    @Serializable
    data object ManagementHub : HomeDestination()

    /** Property management detail screen destination. */
    @Serializable
    data class PropertyManagementDestination(val propertyId: PropertyId) : HomeDestination()

    /** Add new property screen destination. */
    @Serializable
    data class AddPropertyManagementDestination(val orgId: OrganizationId) : HomeDestination()

    /** Add secondary employee to a property screen destination. */
    @Serializable
    data class AddSecondaryEmployeeManagementDestination(val propertyId: PropertyId) : HomeDestination()

    /** Employee detail screen destination. */
    @Serializable
    data class EmployeeDestination(val employeeId: EmployeeId) : HomeDestination()

    /** Time card list for all employees in a property screen destination. */
    @Serializable
    data class TimeCardEmployeeListDestination(val propertyId: PropertyId) : HomeDestination()

    /** Time card detail for a single employee screen destination. */
    @Serializable
    data class TimeCardSingleEmployeeDestination(val employeePk: EmployeeId, val propertyId: PropertyId) :
        HomeDestination()

    /** Event log single entry detail screen destination. */
    @Serializable
    data class EventLogSingleItemDestination(val eventLogRecordPk: EventLogEntryId) : HomeDestination()

    /** Add new event log entry screen destination. */
    @Serializable
    data class EventLogAddItemDestination(val propertyId: PropertyId) : HomeDestination()

    /** Invite a staff member to the organization screen destination. */
    @Serializable
    data class InviteStaffMemberDestination(val orgId: OrganizationId) : HomeDestination()

    override fun toWebPath(): String =
        when (this) {
        is ManagementHub -> Companion.managementHubRoute.toWebPath(this)
        is PropertyManagementDestination -> Companion.propertyManagementRoute.toWebPath(this)
        is AddPropertyManagementDestination -> Companion.addPropertyManagementRoute.toWebPath(this)
        is AddSecondaryEmployeeManagementDestination -> Companion.addSecondaryEmployeeManagementRoute.toWebPath(this)
        is EmployeeDestination -> Companion.employeeRoute.toWebPath(this)
        is TimeCardEmployeeListDestination -> Companion.timeCardEmployeeListRoute.toWebPath(this)
        is TimeCardSingleEmployeeDestination -> Companion.timeCardSingleEmployeeRoute.toWebPath(this)
        is EventLogSingleItemDestination -> Companion.eventLogSingleItemRoute.toWebPath(this)
        is EventLogAddItemDestination -> Companion.eventLogAddItemRoute.toWebPath(this)
        is InviteStaffMemberDestination -> Companion.inviteStaffMemberRoute.toWebPath(this)
    }

    companion object {
        private val managementHubRoute by lazy { webRoute<ManagementHub>("/home") }
        private val propertyManagementRoute by lazy { webRoute<PropertyManagementDestination>("/home/property") }
        private val addPropertyManagementRoute by lazy {
            webRoute<AddPropertyManagementDestination>(
                "/home/add-property",
            )
        }
        private val addSecondaryEmployeeManagementRoute by lazy {
            webRoute<AddSecondaryEmployeeManagementDestination>(
                "/home/add-secondary-employee",
            )
        }
        private val employeeRoute by lazy { webRoute<EmployeeDestination>("/home/employee") }
        private val timeCardEmployeeListRoute by lazy { webRoute<TimeCardEmployeeListDestination>("/home/timecard") }
        private val timeCardSingleEmployeeRoute by lazy {
            webRoute<TimeCardSingleEmployeeDestination>(
                "/home/timecard",
            )
        }
        private val eventLogSingleItemRoute by lazy { webRoute<EventLogSingleItemDestination>("/home/event-log") }
        private val eventLogAddItemRoute by lazy { webRoute<EventLogAddItemDestination>("/home/event-log-add") }
        private val inviteStaffMemberRoute by lazy { webRoute<InviteStaffMemberDestination>("/home/invite") }

        /** Parses [path] and returns the matching [HomeDestination], or null if unrecognised. */
        fun fromWebPath(path: String): HomeDestination? =
            managementHubRoute.fromWebPath(path)
                ?: propertyManagementRoute.fromWebPath(path)
                ?: addPropertyManagementRoute.fromWebPath(path)
                ?: addSecondaryEmployeeManagementRoute.fromWebPath(path)
                ?: employeeRoute.fromWebPath(path)
                ?: timeCardSingleEmployeeRoute.fromWebPath(path)
                ?: timeCardEmployeeListRoute.fromWebPath(path)
                ?: eventLogSingleItemRoute.fromWebPath(path)
                ?: eventLogAddItemRoute.fromWebPath(path)
                ?: inviteStaffMemberRoute.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? =
            entry.toWebPathIfRoute<ManagementHub>()
                ?: entry.toWebPathIfRoute<PropertyManagementDestination>()
                ?: entry.toWebPathIfRoute<AddPropertyManagementDestination>()
                ?: entry.toWebPathIfRoute<AddSecondaryEmployeeManagementDestination>()
                ?: entry.toWebPathIfRoute<EmployeeDestination>()
                ?: entry.toWebPathIfRoute<TimeCardEmployeeListDestination>()
                ?: entry.toWebPathIfRoute<TimeCardSingleEmployeeDestination>()
                ?: entry.toWebPathIfRoute<EventLogSingleItemDestination>()
                ?: entry.toWebPathIfRoute<EventLogAddItemDestination>()
                ?: entry.toWebPathIfRoute<InviteStaffMemberDestination>()
    }
}
