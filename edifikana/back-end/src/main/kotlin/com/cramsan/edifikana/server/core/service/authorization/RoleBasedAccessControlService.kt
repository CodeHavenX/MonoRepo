package com.cramsan.edifikana.server.core.service.authorization

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.logging.logI

/**
 * Service for role-based access control (RBAC).
 */
class RoleBasedAccessControlService(
    private val userService: UserService,
) {
    val runTimeExceptionMsg = "ERROR: USER NOT FOUND!"

    /**
     * Checks if the user making the request has the required role to perform User actions.
     */
    suspend fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        target: UserId,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = retrieveUserRole(context, target)
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
        val userRole = retrieveUserRole(context, target)
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
        val userRole = retrieveUserRole(context, target)
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
        val userRole = retrieveUserRole(context, target)
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
        val userRole = retrieveUserRole(context, target)
        return userRole.ordinal >= requiredRole.ordinal
    }

    /**
     * Checks if the user making the request has the required role to perform general actions (not tied to a specific
     * target).
     */
    suspend fun hasRole(
        context: ClientContext.AuthenticatedClientContext,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = retrieveUserRole(context)
        return userRole == requiredRole
    }

    /**
     * Checks if the user making the request has the required role or higher to perform general actions (not tied to
     * a specific target).
     */
    suspend fun hasRoleOrHigher(
        context: ClientContext.AuthenticatedClientContext,
        requiredRole: UserRole,
    ): Boolean {
        val userRole = retrieveUserRole(context)
        return userRole.ordinal >= requiredRole.ordinal
    }

    /**
     * Retrieves the role of the user making the request.
     */
    suspend fun retrieveUserRole(context: ClientContext.AuthenticatedClientContext): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val requester = userService.getUser(context.userId)
        val user = requester.getOrThrow() ?: throw java.lang.RuntimeException(runTimeExceptionMsg)

        logI(TAG, "User ${user.id} has role(s) ${user.role}")
        return user.role

    }

    /**
     * Retrieves the role of the user making the request. Verifies the target User being accessed is the same as the
     * requester
     */
    private suspend fun retrieveUserRole(
        context: ClientContext.AuthenticatedClientContext,
        target: UserId
    ): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val requester = userService.getUser(context.userId)
        val user = requester.getOrThrow() ?: throw RuntimeException(runTimeExceptionMsg)

        if (user.id == target) {
            logI(TAG, "User ${user.id} matches target user, ${target.userId}")
            return user.role
        }
        logI(TAG, "FORBIDDEN: ATTEMPT TO EDIT ANOTHER USER ACCOUNT!")
        return UserRole.UNAUTHORIZED
    }

    /**
     * Retrieves the role of the user making the request. Verifies the target Property being accessed is in the same
     * organization as the requesting user
     */
    private suspend fun retrieveUserRole(
        context: ClientContext.AuthenticatedClientContext,
        target: PropertyId,
    ): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val requester = userService.getUser(context.userId)
        val user = requester.getOrThrow() ?: throw RuntimeException(runTimeExceptionMsg)

        // TODO: UPDATE TO USE ORGANIZATION TO VERIFY THE USER IS WORKING IN THEIR ORG
        if (user.firstName == target.propertyId) {
            logI(TAG, "User ${user.id} has role(s) ${user.role} in organization: ${user.firstName}")
            return user.role
        }
        logI(TAG, "User ${user.id} is NOT authorized to perform action due to non-matching organizations")
        return UserRole.UNAUTHORIZED
    }

    /**
     * Retrieves the role of the user making the request. Verifies the target Staff being accessed is in the same
     * organization as the requesting user
     */
    private suspend fun retrieveUserRole(
        context: ClientContext.AuthenticatedClientContext,
        target: StaffId,
    ): UserRole {
        logI(TAG, "Retreiving user role(s) for ${context.userId}")
        val requester = userService.getUser(context.userId)
        val user = requester.getOrThrow() ?: throw RuntimeException(runTimeExceptionMsg)

        // TODO: UPDATE TO USE ORGANIZATION TO VERIFY THE USER IS WORKING IN THEIR ORG
        if (user.firstName == target.staffId) {
            logI(TAG, "User ${user.id} has role(s) ${user.role} in organization")
            return user.role
        }
        logI(TAG, "User ${user.id} is NOT authorized to perform action due to non-matching organizations")
        return UserRole.UNAUTHORIZED
    }

    companion object {
        private const val TAG = "RBAC"
    }
}