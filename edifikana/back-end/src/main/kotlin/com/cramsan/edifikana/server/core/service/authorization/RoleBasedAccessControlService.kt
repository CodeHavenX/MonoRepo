package com.cramsan.edifikana.server.core.service.authorization

import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.service.PropertyService
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.logging.logI

/**
 * Service for role-based access control (RBAC).
 */
class RoleBasedAccessControlService(
    private val userService: UserService,
) {
    /**
     * Retrieves the role of the user making the request. Verifies the target User being accessed is in the same
     * organization as the requesting user
     */
    suspend fun retrieveUserRole(context: ClientContext.AuthenticatedClientContext, target: User): UserRole {
        logI(TAG, "Retrieving user role(s) for ${context.userId}")
        val requester = userService.getUser(context.userId)
        val user = requester.getOrThrow() ?: throw RuntimeException("ERROR: USER NOT FOUND!")

        // TODO: UPDATE TO USE ORGANIZATION TO VERIFY THE USER IS WORKING IN THEIR ORG (email should be replaced w/ org)
        if (user.email == target.email) {
            logI(TAG, "User ${user.id} has role ${user.role} in organization ${user.email}")
            return user.role
        }
        logI(TAG, "User ${user.id} is NOT authorized to perform action due to non-matching organizations")
        return UserRole.UNAUTHORIZED
    }

    /**
     * Retrieves the role of the user making the request. Verifies the target Property being accessed is in the same
     * organization as the requesting user
     */
    suspend fun retrieveUserRole(context: ClientContext.AuthenticatedClientContext, target: Property): UserRole {
        logI(TAG, "Retreiving user role(s) for ${context.userId}")
        val requester = userService.getUser(context.userId)
        val user = requester.getOrThrow() ?: throw RuntimeException("ERROR: USER NOT FOUND!")

        // TODO: UPDATE TO USE ORGANIZATION TO VERIFY THE USER IS WORKING IN THEIR ORG
        if (user.firstName == target.name) {
            logI(TAG, "User ${user.id} has role(s): ${user.role} in organization: ${user.firstName}")
            return user.role
        }
        logI(TAG, "User ${user.id} is NOT authorized to perform action due to non-matching organizations")
        return UserRole.UNAUTHORIZED
    }

    /**
     * Retrieves the role of the user making the request. Verifies the target Staff being accessed is in the same
     * organization as the requesting user
     */
    suspend fun retrieveUserRole(context: ClientContext.AuthenticatedClientContext, target: Staff): UserRole {
        logI(TAG, "Retreiving user role(s) for ${context.userId}")
        val requester = userService.getUser(context.userId)
        val user = requester.getOrThrow() ?: throw RuntimeException("ERROR: USER NOT FOUND!")

        // TODO: UPDATE TO USE ORGANIZATION TO VERIFY THE USER IS WORKING IN THEIR ORG
        if (user.firstName == target.propertyId.toString()) {
            logI(TAG, "User ${user.id} has role(s) ${user.role} in organization ${target.propertyId}")
            return user.role
        }
        logI(TAG, "User ${user.id} is NOT authorized to perform action due to non-matching organizations")
        return UserRole.UNAUTHORIZED
    }

    companion object {
        private const val TAG = "RBAC"
    }

}