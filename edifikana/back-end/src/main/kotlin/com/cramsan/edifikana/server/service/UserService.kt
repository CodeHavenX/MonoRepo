package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.NotificationDatastore
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logW
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

/**
 * Service for user operations.
 */
class UserService(
    private val userDatastore: UserDatastore,
    private val notificationDatastore: NotificationDatastore,
    private val organizationDatastore: OrganizationDatastore,
    private val clock: Clock,
) {

    /**
     * Creates a user with the provided information.
     */
    suspend fun createUser(
        email: String,
        phoneNumber: String,
        password: String?,
        firstName: String,
        lastName: String,
    ): Result<User> {
        logD(TAG, "createUser")
        val isTransient = password.isNullOrBlank()
        val result = userDatastore.createUser(
            email,
            phoneNumber,
            password,
            firstName,
            lastName,
            isTransient
        )

        if (!isTransient) {
            val user = result.getOrThrow()

            // Link any pending notifications to this user
            notificationDatastore.linkNotificationsToUser(email, user.id).onFailure { e ->
                logW(TAG, "Failed to link notifications to user", e)
            }
        }

        return result
    }

    /**
     * Associate a user from another service with a new user in our system.
     */
    suspend fun associateUser(
        id: UserId,
        email: String,
    ): Result<User> {
        logD(TAG, "associateUser")
        val result = userDatastore.associateUser(
            userId = id,
            email = email
        )

        if (result.isSuccess) {
            val user = result.getOrThrow()

            // Link any pending notifications to this user
            notificationDatastore.linkNotificationsToUser(email, user.id).onFailure { e ->
                logW(TAG, "Failed to link notifications to user", e)
            }
        }

        return result
    }

    /**
     * Retrieves a user with the provided [id].
     */
    suspend fun getUser(
        id: UserId,
    ): Result<User?> {
        logD(TAG, "getUser")
        return userDatastore.getUser(
            id = id,
        )
    }

    /**
     * Retrieves all users.
     */
    suspend fun getUsers(
        organizationId: OrganizationId,
    ): Result<List<User>> {
        logD(TAG, "getUsers")
        return userDatastore.getUsers(
            organizationId = organizationId,
        )
    }

    /**
     * Updates a user with the provided [id] and [email].
     */
    suspend fun updateUser(
        id: UserId,
        email: String?,
    ): Result<User> {
        logD(TAG, "updateUser")
        return userDatastore.updateUser(
            id = id,
            email = email,
        )
    }

    /**
     * Deletes a user with the provided [id].
     */
    suspend fun deleteUser(
        id: UserId,
    ): Result<Boolean> {
        logD(TAG, "deleteUser")
        return userDatastore.deleteUser(
            id = id,
        )
    }

    /**
     * Updates the password for a user with the provided [userId].
     */
    @OptIn(SecureStringAccess::class)
    suspend fun updatePassword(
        userId: UserId,
        currentHashedPassword: SecureString,
        newPassword: SecureString,
    ): Result<Unit> {
        logD(TAG, "updatePassword")
        return userDatastore.updatePassword(
            id = userId,
            currentHashedPassword = currentHashedPassword,
            newPassword = newPassword,
        )
    }

    /**
     * Records an invitation for a user with the provided [email], [organizationId], and [role].
     * Also creates a notification for the invited user.
     */
    suspend fun inviteUser(
        email: String,
        organizationId: OrganizationId,
        role: UserRole,
    ): Result<Unit> = runCatching {
        logD(TAG, "inviteUser with role: $role")
        val userId = userDatastore.getUser(email).getOrNull()?.id
        val organization = organizationDatastore.getOrganization(organizationId).getOrNull()
            ?: throw ClientRequestExceptions.NotFoundException("Organization not found")

        val invite = userDatastore.recordInvite(
            email,
            organizationId,
            expiration = clock.now() + 14.days,
            role = role,
        ).getOrThrow()

        // Create a notification for the invited user
        // If user doesn't exist yet, store the email for later association when they sign up
        val notificationResult = notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = if (userId == null) email else null,
            notificationType = NotificationType.INVITE,
            description = "You have been invited to join ${organization.name}.",
            inviteId = invite.id,
        ).onFailure { e ->
            logW(TAG, "Failed to create invite notification", e)
            throw e
        }
        logD(TAG, "Invite notification created: ${notificationResult.getOrThrow().id}")
    }

    /**
     * Retrieves all pending invites for the provided [organizationId].
     */
    suspend fun getInvites(
        organizationId: OrganizationId,
    ): Result<List<Invite>> {
        logD(TAG, "getInvites")
        return userDatastore.getInvites(organizationId)
    }

    /**
     * Checks if an email is registered to an existing user in our system
     */
    suspend fun checkUserIsRegistered(email: String): Result<Boolean> {
        logD(TAG, "checkUserIsRegistered")
        val registeredUser = userDatastore.getUser(email).getOrThrow()
        return Result.success(registeredUser != null)
    }

    /**
     * Accepts an invitation and adds the user to the organization with the specified role.
     * @param userId The ID of the user accepting the invite
     * @param inviteId The ID of the invite to accept
     */
    suspend fun acceptInvite(
        userId: UserId,
        inviteId: InviteId,
    ): Result<Unit> = runCatching {
        logD(TAG, "acceptInvite: $inviteId for user: $userId")

        // Get the invite
        val invite = userDatastore.getInvite(inviteId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Invite not found")

        // Verify invite is not expired
        if (invite.expiration < clock.now()) {
            throw ClientRequestExceptions.InvalidRequestException("Invite has expired")
        }

        // Verify the user email matches the invite email
        val user = userDatastore.getUser(userId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("User not found")

        if (user.email != invite.email) {
            throw ClientRequestExceptions.ForbiddenException(
                "This invite is not for your email address"
            )
        }

        // Add user to organization with the specified role
        organizationDatastore.addUserToOrganization(
            userId = userId,
            organizationId = invite.organizationId,
            role = invite.role,
        ).getOrThrow()

        val notification = notificationDatastore.getNotificationByInvite(inviteId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Invite notification not found")

        // Delete the notification for the invited user
        notificationDatastore.deleteNotification(notification.id).getOrThrow()

        // Remove the invite after successful acceptance
        userDatastore.removeInvite(inviteId).getOrThrow()

        logD(TAG, "User $userId successfully joined organization ${invite.organizationId}")
    }

    /**
     * Declines an invitation by removing it from the system.
     * @param userId The ID of the user declining the invite
     * @param inviteId The ID of the invite to decline
     */
    suspend fun declineInvite(
        userId: UserId,
        inviteId: InviteId,
    ): Result<Unit> = runCatching {
        logD(TAG, "declineInvite: $inviteId for user: $userId")

        // Get the invite to verify ownership
        val invite = userDatastore.getInvite(inviteId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Invite not found")

        // Verify the user email matches the invite email
        val user = userDatastore.getUser(userId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("User not found")

        if (user.email != invite.email) {
            throw ClientRequestExceptions.ForbiddenException(
                "This invite is not for your email address"
            )
        }

        val notification = notificationDatastore.getNotificationByInvite(inviteId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Invite notification not found")

        // Delete the notification for the invited user
        notificationDatastore.deleteNotification(notification.id).getOrThrow()

        // Remove the invite
        userDatastore.removeInvite(inviteId).getOrThrow()

        logD(TAG, "User $userId declined invite $inviteId")
    }

    /**
     * Gets the organization ID for an invite.
     * @param inviteId The ID of the invite
     * @return The organization ID the invite belongs to
     */
    suspend fun getInviteOrganization(
        inviteId: InviteId,
    ): Result<OrganizationId> = runCatching {
        logD(TAG, "getInviteOrganization: $inviteId")

        val invite = userDatastore.getInvite(inviteId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Invite not found")

        invite.organizationId
    }

    /**
     * Cancels a pending invite (manager action).
     * @param inviteId The ID of the invite to cancel
     */
    suspend fun cancelInvite(
        inviteId: InviteId,
    ): Result<Unit> = runCatching {
        logD(TAG, "cancelInvite: $inviteId")

        // Verify the invite exists
        val invite = userDatastore.getInvite(inviteId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Invite not found")

        // Remove the invite
        userDatastore.removeInvite(inviteId).getOrThrow()

        logD(TAG, "Invite $inviteId cancelled for organization ${invite.organizationId}")
    }

    companion object {
        private const val TAG = "UserService"
    }
}
