package com.cramsan.edifikana.client.lib.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.settings.SettingsDestination
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Converts the current [NavBackStackEntry] to a canonical URL path for Edifikana.
 * Returns null for destinations that should not update the browser URL (nav-graph
 * containers, splash, and debug screens).
 */
fun edifikanaEntryToPath(entry: NavBackStackEntry): String? =
    authEntryToPath(entry)
        ?: homeEntryToPath(entry)
        ?: accountEntryToPath(entry)
        ?: settingsEntryToPath(entry)

/**
 * Converts a canonical URL path into the matching typed navigation [Destination] for Edifikana.
 * Returns null for unrecognized paths (the caller should fall back to the default start destination).
 */
fun edifikanaPathToDestination(path: String): Destination? =
    authPathToDestination(path)
        ?: homePathToDestination(path)
        ?: accountPathToDestination(path)
        ?: settingsPathToDestination(path)

private fun authEntryToPath(entry: NavBackStackEntry): String? {
    val destination = entry.destination
    return when {
        destination.hasRoute<AuthDestination.SignInDestination>() -> {
            "/auth/sign-in"
        }

        destination.hasRoute<AuthDestination.SignUpDestination>() -> {
            "/auth/sign-up/${entry.toRoute<AuthDestination.SignUpDestination>().userEmail}"
        }

        destination.hasRoute<AuthDestination.ValidationDestination>() -> {
            "/auth/validation/${entry.toRoute<AuthDestination.ValidationDestination>().userEmail}"
        }

        destination.hasRoute<AuthDestination.SelectOrgDestination>() -> {
            "/auth/select-org"
        }

        destination.hasRoute<AuthDestination.CreateNewOrgDestination>() -> {
            "/auth/create-org"
        }

        destination.hasRoute<AuthDestination.PasswordResetDestination>() -> {
            "/auth/password-reset"
        }

        destination.hasRoute<AuthDestination.PasswordResetConfirmationDestination>() -> {
            "/auth/password-reset-confirm/${entry.toRoute<AuthDestination.PasswordResetConfirmationDestination>().userEmail}"
        }

        else -> {
            null
        }
    }
}

private fun homeEntryToPath(entry: NavBackStackEntry): String? {
    val destination = entry.destination
    return when {
        destination.hasRoute<HomeDestination.ManagementHub>() -> {
            "/home"
        }

        destination.hasRoute<HomeDestination.PropertyManagementDestination>() -> {
            "/home/property/${entry.toRoute<HomeDestination.PropertyManagementDestination>().propertyId}"
        }

        destination.hasRoute<HomeDestination.AddPropertyManagementDestination>() -> {
            "/home/add-property/${entry.toRoute<HomeDestination.AddPropertyManagementDestination>().orgId}"
        }

        destination.hasRoute<HomeDestination.EmployeeDestination>() -> {
            "/home/employee/${entry.toRoute<HomeDestination.EmployeeDestination>().employeeId}"
        }

        destination.hasRoute<HomeDestination.TimeCardSingleEmployeeDestination>() -> {
            val route = entry.toRoute<HomeDestination.TimeCardSingleEmployeeDestination>()
            "/home/timecard/${route.propertyId}/${route.employeePk}"
        }

        destination.hasRoute<HomeDestination.TimeCardEmployeeListDestination>() -> {
            "/home/timecard/${entry.toRoute<HomeDestination.TimeCardEmployeeListDestination>().propertyId}"
        }

        destination.hasRoute<HomeDestination.EventLogAddItemDestination>() -> {
            "/home/event-log-add/${entry.toRoute<HomeDestination.EventLogAddItemDestination>().propertyId}"
        }

        destination.hasRoute<HomeDestination.EventLogSingleItemDestination>() -> {
            "/home/event-log/${entry.toRoute<HomeDestination.EventLogSingleItemDestination>().eventLogRecordPk}"
        }

        destination.hasRoute<HomeDestination.AddSecondaryEmployeeManagementDestination>() -> {
            "/home/add-secondary-employee/${entry.toRoute<HomeDestination.AddSecondaryEmployeeManagementDestination>().propertyId}"
        }

        destination.hasRoute<HomeDestination.InviteStaffMemberDestination>() -> {
            "/home/invite/${entry.toRoute<HomeDestination.InviteStaffMemberDestination>().orgId}"
        }

        else -> {
            null
        }
    }
}

private fun accountEntryToPath(entry: NavBackStackEntry): String? {
    val destination = entry.destination
    return when {
        destination.hasRoute<AccountDestination.MyAccountDestination>() -> "/account"
        destination.hasRoute<AccountDestination.NotificationsDestination>() -> "/account/notifications"
        destination.hasRoute<AccountDestination.ChangePasswordDestination>() -> "/account/change-password"
        else -> null
    }
}

private fun settingsEntryToPath(entry: NavBackStackEntry): String? {
    val destination = entry.destination
    return when {
        destination.hasRoute<SettingsDestination.GeneralSettingsDestination>() -> "/settings"
        else -> null
    }
}

private fun authPathToDestination(path: String): Destination? =
    when {
        path == "/auth/sign-in" -> {
            AuthDestination.SignInDestination
        }

        path.startsWith("/auth/sign-up/") -> {
            AuthDestination.SignUpDestination(path.removePrefix("/auth/sign-up/"))
        }

        path.startsWith("/auth/validation/") -> {
            AuthDestination.ValidationDestination(path.removePrefix("/auth/validation/"), accountCreationFlow = false)
        }

        path == "/auth/select-org" -> {
            AuthDestination.SelectOrgDestination
        }

        path == "/auth/create-org" -> {
            AuthDestination.CreateNewOrgDestination
        }

        path == "/auth/password-reset" -> {
            AuthDestination.PasswordResetDestination()
        }

        path.startsWith("/auth/password-reset-confirm/") -> {
            AuthDestination.PasswordResetConfirmationDestination(path.removePrefix("/auth/password-reset-confirm/"))
        }

        else -> {
            null
        }
    }

private fun homePathToDestination(path: String): Destination? =
    when {
        path == "/home" -> {
            HomeDestination.ManagementHub
        }

        path.startsWith("/home/property/") -> {
            HomeDestination.PropertyManagementDestination(PropertyId(path.removePrefix("/home/property/")))
        }

        path.startsWith("/home/add-property/") -> {
            HomeDestination.AddPropertyManagementDestination(OrganizationId(path.removePrefix("/home/add-property/")))
        }

        path.startsWith("/home/employee/") -> {
            HomeDestination.EmployeeDestination(EmployeeId(path.removePrefix("/home/employee/")))
        }

        path.startsWith("/home/timecard/") && path.removePrefix("/home/timecard/").contains('/') -> {
            val (propertyPart, employeePart) = path.removePrefix("/home/timecard/").split("/", limit = 2)
            HomeDestination.TimeCardSingleEmployeeDestination(
                propertyId = PropertyId(propertyPart),
                employeePk = EmployeeId(employeePart),
            )
        }

        path.startsWith("/home/timecard/") -> {
            HomeDestination.TimeCardEmployeeListDestination(PropertyId(path.removePrefix("/home/timecard/")))
        }

        path.startsWith("/home/event-log-add/") -> {
            HomeDestination.EventLogAddItemDestination(PropertyId(path.removePrefix("/home/event-log-add/")))
        }

        path.startsWith("/home/event-log/") -> {
            HomeDestination.EventLogSingleItemDestination(EventLogEntryId(path.removePrefix("/home/event-log/")))
        }

        path.startsWith("/home/add-secondary-employee/") -> {
            HomeDestination.AddSecondaryEmployeeManagementDestination(
                PropertyId(path.removePrefix("/home/add-secondary-employee/")),
            )
        }

        path.startsWith("/home/invite/") -> {
            HomeDestination.InviteStaffMemberDestination(OrganizationId(path.removePrefix("/home/invite/")))
        }

        else -> {
            null
        }
    }

private fun accountPathToDestination(path: String): Destination? =
    when (path) {
        "/account" -> AccountDestination.MyAccountDestination
        "/account/notifications" -> AccountDestination.NotificationsDestination
        "/account/change-password" -> AccountDestination.ChangePasswordDestination
        else -> null
    }

private fun settingsPathToDestination(path: String): Destination? =
    when (path) {
        "/settings" -> SettingsDestination.GeneralSettingsDestination
        else -> null
    }
