package com.cramsan.edifikana.server.core.service.authorization

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.core.datastore.StaffDatastore
import com.cramsan.edifikana.server.core.datastore.UserDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.SupabasePropertyDatastore
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.framework.logging.logI

/**
 * Service for role-based access control (RBAC).
 */
class RoleBasedAccessControlService(
    private val propertyDatastore: SupabasePropertyDatastore,
    private val userDatastore: UserDatastore,
    private val orgDatastore: OrganizationDatastore,
    private val staffDatastore: StaffDatastore,

    ) {
    val userNotFound = "ERROR: USER NOT FOUND!"
    val propertyNotFound = "ERROR: PROPERTY NOT FOUND!"
    val employeeNotFound = "ERROR: EMPLOYEE NOT FOUND!"

    /**
     * Checks if the user making the request has the required role to perform User actions.
     */
    suspend fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        target: UserId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = retrieveUserRoleForUserAction(context, target)
        return userRole == requiredRole
    }

    /**
     * Checks if the user making the request has the required role to perform Property actions.
     */
    suspend fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        target: PropertyId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = retrieveUserRoleForPropertyAction(context, target)
        return userRole == requiredRole
    }

    /**
     * Checks if the user making the request has the required role or higher to perform Property actions.
     */
    suspend fun hasRoleOrHigher(
        context: ClientContext.AuthenticatedClientContext,
        target: PropertyId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = retrieveUserRoleForPropertyAction(context, target)
        return userRole.ordinal >= requiredRole.ordinal
    }

    /**
     * Checks if the user making the request has the required role to perform Staff actions.
     */
    suspend fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        target: StaffId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = retrieveUserRoleStaffAction(context, target)
        return userRole == requiredRole
    }

    /**
     * Checks if the user making the request has the required role or higher to perform Staff actions.
     */
    suspend fun hasRoleOrHigher(
        context: ClientContext.AuthenticatedClientContext,
        target: StaffId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = retrieveUserRoleStaffAction(context, target)
        return userRole.ordinal >= requiredRole.ordinal
    }

    /**
     * Retrieves the role of the user making the request. Verifies the target User being accessed is the same as the
     * requester
     */
    private fun retrieveUserRoleForUserAction(
        context: ClientContext.AuthenticatedClientContext,
        target: UserId
    ): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        // only allow users to access their own account
        if (context.userId == target) {
            logI(TAG, "User ${context.userId} matches target user, ${target}")
            return UserRole.USER
        }
        logI(TAG, "FORBIDDEN: ATTEMPT TO EDIT ANOTHER USER ACCOUNT!")
        return UserRole.UNAUTHORIZED
    }

    /**
     * Retrieves the role of the user making the request. Verifies the target Property being accessed is in the same
     * organization as the requesting user
     */
    private suspend fun retrieveUserRoleForPropertyAction(
        context: ClientContext.AuthenticatedClientContext,
        target: PropertyId,
    ): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val requestProperty = propertyDatastore.getProperty(GetPropertyRequest(target))
        val property = requestProperty.getOrThrow() ?: throw RuntimeException(propertyNotFound)
        val role = orgDatastore.getUserRole(context.userId, property.organizationId).getOrNull()

        if (role != null) {
            logI(TAG, "User ${context.userId} has role(s) $role in organization: ${property.organizationId}")
            return role
        }

        logI(TAG, "User ${context.userId} is NOT authorized to perform action due to non-matching organizations")
        return UserRole.UNAUTHORIZED
    }

    /**
     * Retrieves the role of the user making the request. Verifies the target Staff being accessed is in the same
     * organization as the requesting user
     */
    private suspend fun retrieveUserRoleStaffAction(
        context: ClientContext.AuthenticatedClientContext,
        target: StaffId,
    ): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val employee =
            staffDatastore.getStaff(GetStaffRequest(target)).getOrThrow() ?: throw RuntimeException(employeeNotFound)
        val property = propertyDatastore.getProperty(GetPropertyRequest(employee.propertyId)).getOrThrow()
            ?: throw RuntimeException(propertyNotFound)
        val role = orgDatastore.getUserRole(context.userId, property.organizationId).getOrNull()

        if (role != null) {
            logI(TAG, "User ${context.userId} has role(s) $role in organization: ${property.organizationId}")
            return role
        }

        logI(TAG, "User ${context.userId} is NOT authorized to perform action due to non-matching organizations")
        return UserRole.UNAUTHORIZED
    }

    companion object {
        private const val TAG = "RBAC"
    }
}
