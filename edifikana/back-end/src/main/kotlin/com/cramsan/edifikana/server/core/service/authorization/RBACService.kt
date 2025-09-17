package com.cramsan.edifikana.server.core.service.authorization

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.core.datastore.PropertyDatastore
import com.cramsan.edifikana.server.core.datastore.StaffDatastore
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.logging.logI
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.ForbiddenException
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.InvalidRequestException

/**
 * Service responsible for Role-Based Access Control (RBAC) checks.
 */
class RBACService(
    private val propertyDatastore: PropertyDatastore,
    private val orgDataStore: OrganizationDatastore,
    private val employeeDatastore: StaffDatastore,
) {

    val propertyNotFoundException = "ERROR: PROPERTY NOT FOUND!"
    val employeeNotFoundException = "ERROR: EMPLOYEE NOT FOUND!"
    val unauthorized = UserRole.UNAUTHORIZED

    /**
     * Checks if the user has the required role to perform actions on the target user.
     *
     * @param context The authenticated client context containing user information.
     * @param targetUser The ID of the target user on whom the action is to be performed.
     * @param requiredRole The role required to perform the action.
     * @return True if the user has the required role, false otherwise.
     */
    fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        targetUser: UserId,
        requiredRole: UserRole,
    ): Boolean {
        if (context.userId == targetUser) {
            logI(TAG, "User ${context.userId} matches target user $targetUser")
            return true
        }
        throw ForbiddenException("FORBIDDEN ATTEMPT MADE TO EDIT ANOTHER USER'S ACCOUNT!")
    }

    /**
     * Checks if the user has the required role to perform actions on the target organization.
     *
     * @param context The authenticated client context containing user information.
     * @param org The ID of the target organization on which the action is to be performed.
     * @param requiredRole The role required to perform the action.
     * @return True if the user has the required role, false otherwise.
     */
    suspend fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        org: OrganizationId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = getUserRoleForOrganizationAction(context, org)
        return userRole == requiredRole
    }

    /**
     * Checks if the user has the required role or higher to perform actions on the target organization.
     *
     * @param context The authenticated client context containing user information.
     * @param org The ID of the target organization on which the action is to be performed
     * @param requiredRole The minimum role required to perform the action.
     * @return True if the user has the required role or higher, false otherwise.
     */
    suspend fun hasRoleOrHigher(
        context: ClientContext.AuthenticatedClientContext,
        org: OrganizationId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = getUserRoleForOrganizationAction(context, org)
        return userRole.level <= requiredRole.level
    }

    /**
     * Checks if the user has the required role to perform actions on the target property.
     *
     * @param context The authenticated client context containing user information.
     * @param targetProperty The ID of the target property on which the action is to be performed.
     * @param requiredRole The role required to perform the action.
     * @return True if the user has the required role, false otherwise.
     */
    suspend fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        targetProperty: PropertyId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = getUserRoleForPropertyAction(context, targetProperty)
        return userRole == requiredRole
    }

    /**
     * Checks if the user has the required role or higher to perform actions on the target property.
     *
     * @param context The authenticated client context containing user information.
     * @param targetProperty The ID of the target property on which the action is to be performed.
     * @param requiredRole The minimum role required to perform the action.
     * @return True if the user has the required role or higher, false otherwise.
     */
    suspend fun hasRoleOrHigher(
        context: ClientContext.AuthenticatedClientContext,
        targetProperty: PropertyId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = getUserRoleForPropertyAction(context, targetProperty)
        return userRole.level <= requiredRole.level
    }

    /**
     * Checks if the user has the required role to perform actions on the target employee.
     *
     * @param context The authenticated client context containing user information.
     * @param targetEmployee The ID of the target employee on which the action is to be performed.
     * @param requiredRole The role required to perform the action.
     * @return True if the user has the required role, false otherwise.
     */
    suspend fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        targetEmployee: StaffId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = getUserRoleForEmployeeAction(context, targetEmployee)
        return userRole == requiredRole
    }

    /**
     * Checks if the user has the required role or higher to perform actions on the target employee.
     *
     * @param context The authenticated client context containing user information.
     * @param targetEmployee The ID of the target employee on which the action is to be performed.
     * @param requiredRole The minimum role required to perform the action.
     * @return True if the user has the required role or higher, false otherwise.
     */
    suspend fun hasRoleOrHigher(
        context: ClientContext.AuthenticatedClientContext,
        targetEmployee: StaffId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = getUserRoleForEmployeeAction(context, targetEmployee)
        return userRole.level <= requiredRole.level
    }

    /**
     * Retrieves the user role for the action being performed on the target organization.
     *
     * @param context The authenticated client context containing user information.
     * @param org The ID of the target organization on which the action is to be performed.
     * @return The user role if the action is allowed, or UNAUTHORIZED if not
     */
    private suspend fun getUserRoleForOrganizationAction(
        context: ClientContext.AuthenticatedClientContext,
        org: OrganizationId
    ): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val role = orgDataStore.getUserRole(context.userId, org).getOrNull()
        if (role != null) {
            logI(TAG, "User ${context.userId} has role(s) $role in organization $org")
            return role
        }
        logI(TAG, "User ${context.userId} is NOT authorized to perform action in this organization, $org")
        return unauthorized
    }

    /**
     * Retrieves the user role for the action being performed on the target property.
     *
     * @param context The authenticated client context containing user information.
     * @param targetProperty The ID of the target property on which the action is to be performed.
     * @return The user role if the action is allowed, or UNAUTHORIZED if not
     */
    private suspend fun getUserRoleForPropertyAction(
        context: ClientContext.AuthenticatedClientContext,
        targetProperty: PropertyId
    ): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val requestedProperty = propertyDatastore.getProperty(targetProperty)
        val property = requestedProperty.getOrThrow() ?: throw RuntimeException(propertyNotFoundException)
        val role = orgDataStore.getUserRole(context.userId, property.organizationId).getOrNull()

        if (role != null) {
            logI(TAG, "User ${context.userId} has role(s) $role in organization ${property.organizationId}")
            return role
        }
        logI(TAG, "User ${context.userId} is NOT authorized to perform action for this property.")
        return unauthorized
    }

    /**
     * Retrieves the user role for the action being performed on the target employee.
     *
     * @param context The authenticated client context containing user information.
     * @param targetEmployee The ID of the target employee on which the action is to be performed.
     * @return The user role if the action is allowed, or UNAUTHORIZED if not
     */
    private suspend fun getUserRoleForEmployeeAction(
        context: ClientContext.AuthenticatedClientContext,
        targetEmployee: StaffId
    ): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val employee =
            employeeDatastore.getStaff(targetEmployee).getOrThrow() ?: throw InvalidRequestException(
                employeeNotFoundException
            )
        val property = propertyDatastore.getProperty(employee.propertyId).getOrThrow() ?: throw InvalidRequestException(
            propertyNotFoundException
        )
        val role = orgDataStore.getUserRole(context.userId, property.organizationId).getOrNull()

        if (role != null) {
            logI(TAG, "User ${context.userId} has role(s) $role in organization ${property.organizationId}")
            return role
        }
        logI(TAG, "User ${context.userId} is NOT authorized to perform action for this employee.")
        return unauthorized
    }

    companion object {
        private const val TAG = "RBAC"
    }

}
