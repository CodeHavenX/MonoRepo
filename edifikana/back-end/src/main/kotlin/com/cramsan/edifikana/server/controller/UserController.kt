package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.UserApi
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.CheckUserNetworkResponse
import com.cramsan.edifikana.lib.model.network.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetAllUsersQueryParams
import com.cramsan.edifikana.lib.model.network.InviteListNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePasswordNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UserListNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.UserService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import com.cramsan.framework.utils.exceptions.UnauthorizedException
import com.cramsan.framework.utils.exceptions.requireAll
import io.ktor.server.routing.Routing

/**
 * Controller for user related operations. CRUD operations for users.
 */
class UserController(
    private val userService: UserService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
    private val rbacService: RBACService,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action."

    /**
     * Handles the creation of a new user.
     * Creates a user with the provided request data and returns the created user as a network response.
     * Throws [IllegalArgumentException] if required fields are missing.
     */
    @OptIn(NetworkModel::class)
    suspend fun createUser(createUserRequest: CreateUserNetworkRequest): UserNetworkResponse {
        requireAll(
            "An email and phone number must be provided.",
            createUserRequest.email,
            createUserRequest.phoneNumber,
        )

        val newUserResult = userService.createUser(
            email = createUserRequest.email,
            phoneNumber = createUserRequest.phoneNumber,
            password = createUserRequest.password,
            firstName = createUserRequest.firstName,
            lastName = createUserRequest.lastName,
        )

        return newUserResult.requireSuccess().toUserNetworkResponse()
    }

    /**
     * Handles the retrieval of a user by ID.
     * Returns the user as a network response if the authenticated context has the required role, or null if not found.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUser(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        userId: UserId,
    ): UserNetworkResponse? {
        if (!rbacService.hasRole(context, userId)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return userService.getUser(
            id = userId,
        ).getOrNull()?.toUserNetworkResponse()
    }

    /**
     * Handles the updating of a user's password.
     * Updates the password for the authenticated user if they have the required role.
     * Returns [NoResponseBody] to indicate successful update.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    @OptIn(NetworkModel::class, SecureStringAccess::class)
    suspend fun updatePassword(
        authenticatedContext: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        updatePasswordRequest: UpdatePasswordNetworkRequest,
    ): NoResponseBody {
        val userId = authenticatedContext.payload.userId
        if (!rbacService.hasRole(authenticatedContext, userId)) {
            throw UnauthorizedException(unauthorizedMsg)
        }

        val result = userService.updatePassword(
            userId = userId,
            currentHashedPassword = SecureString(updatePasswordRequest.currentPasswordHashed),
            newPassword = SecureString(updatePasswordRequest.newPassword),
        )

        result.requireSuccess()
        return NoResponseBody
    }

    /**
     * Handles the retrieval of all users within an organization.
     * Returns a list of users for the organization identified by [queryParams.orgId] if
     * the authenticated context has the required role.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUsers(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        queryParams: GetAllUsersQueryParams,
    ): UserListNetworkResponse {
        val orgId = requireNotBlank(queryParams.orgId.id, "An organization ID must be provided.")
        if (!rbacService.hasRoleOrHigher(context, OrganizationId(orgId), UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }

        val users = userService.getUsers(
            organizationId = queryParams.orgId,
        ).getOrThrow().map { it.toUserNetworkResponse() }

        return UserListNetworkResponse(users)
    }

    /**
     * Handles the updating of a user.
     * Updates the user identified by [userId] with the provided [updateUserRequest] if the
     * authenticated context has the required role.
     * Returns the updated user as a network response.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateUser(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        updateUserRequest: UpdateUserNetworkRequest,
        userId: UserId,
    ): UserNetworkResponse {
        if (!rbacService.hasRole(context, userId)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val updatedUser = userService.updateUser(
            id = userId,
            email = updateUserRequest.email,
        ).getOrThrow().toUserNetworkResponse()

        return updatedUser
    }

    /**
     * Handles the deletion of a user.
     * Deletes the user identified by [userId] if the authenticated context has the required role.
     * Returns [NoResponseBody] to indicate successful deletion.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    suspend fun deleteUser(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        userId: UserId,
    ): NoResponseBody {
        if (!rbacService.hasRole(context, userId)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val result = userService.deleteUser(
            userId,
        )

        result.requireSuccess()
        return NoResponseBody
    }

    /**
     * Handle a call to associate a user created in another system (e.g., Supabase) with our system.
     * Associates the authenticated user with the system using their email and userId.
     * Returns the associated user as a network response.
     * Throws [IllegalArgumentException] if the user does not have a configured email.
     */
    @OptIn(NetworkModel::class)
    suspend fun associate(
        authenticatedContext: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
    ): UserNetworkResponse {
        val userId = authenticatedContext.payload.userId
        val email = authenticatedContext.payload.userInfo.email

        requireNotBlank(email, "User does not have a configured email.")

        val newUserResult = userService.associateUser(
            userId,
            email,
        )

        return newUserResult.requireSuccess().toUserNetworkResponse()
    }

    /**
     * Handle a call to invite a user to the system via email with a specified role.
     * Invites a user with the given [inviteRequest] if the authenticated context has the required role.
     * The inviter cannot assign a role higher than their own.
     * Returns [NoResponseBody] to indicate successful invitation.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    @OptIn(NetworkModel::class)
    suspend fun inviteUser(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        inviteRequest: InviteUserNetworkRequest,
    ): NoResponseBody {
        val email = inviteRequest.email
        val orgId = inviteRequest.organizationId
        val inviteRole: UserRole = inviteRequest.role.toServiceUserRole()

        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }

        // Validate that the inviter cannot assign a role higher than their own
        val inviterRole = rbacService.getUserRoleForOrganizationAction(context, orgId)
        if (inviteRole.level < inviterRole.level) {
            throw UnauthorizedException("Cannot invite users with higher privileges than your own")
        }

        userService.inviteUser(
            email,
            orgId,
            inviteRole,
        ).requireSuccess()
        return NoResponseBody
    }

    /**
     * Handle a call to get all pending invites for an organization.
     * Returns a list of pending invites for the organization identified by [orgId] if the
     * authenticated context has the required role.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    @OptIn(NetworkModel::class)
    suspend fun getInvites(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        orgId: OrganizationId,
    ): InviteListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val invites = userService.getInvites(
            organizationId = orgId,
        ).getOrThrow().map { it.toInviteNetworkResponse() }

        return InviteListNetworkResponse(invites)
    }

    /**
     * Handle a call to check if a user exists in the system.
     * Returns a [CheckUserNetworkResponse] indicating whether the user exists.
     * Throws [IllegalArgumentException] if required fields are missing.
     */
    @OptIn(NetworkModel::class)
    suspend fun checkUserIsRegistered(email: String): CheckUserNetworkResponse {
        val registeredUser = userService.checkUserIsRegistered(email).getOrThrow()
        return CheckUserNetworkResponse(registeredUser)
    }

    /**
     * Handle a call to accept a pending invitation.
     * Accepts the invitation identified by [inviteId] for the authenticated user.
     * Returns [NoResponseBody] to indicate successful acceptance.
     */
    suspend fun acceptInvite(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        inviteId: InviteId,
    ): NoResponseBody {
        val userId = context.payload.userId

        userService.acceptInvite(
            userId = userId,
            inviteId = inviteId,
        ).requireSuccess()

        return NoResponseBody
    }

    /**
     * Handle a call to decline a pending invitation.
     * Declines the invitation identified by [inviteId] for the authenticated user.
     * Returns [NoResponseBody] to indicate successful decline.
     */
    suspend fun declineInvite(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        inviteId: InviteId,
    ): NoResponseBody {
        val userId = context.payload.userId

        userService.declineInvite(
            userId = userId,
            inviteId = inviteId,
        ).requireSuccess()

        return NoResponseBody
    }

    /**
     * Handle a call to cancel a pending invite (manager action).
     * Cancels the invitation identified by [inviteId] if the authenticated context has the required role.
     * Returns [NoResponseBody] to indicate successful cancellation.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    suspend fun cancelInvite(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        inviteId: InviteId,
    ): NoResponseBody {
        // First get the organization ID for authorization check
        val orgId = userService.getInviteOrganization(inviteId).requireSuccess()

        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }

        userService.cancelInvite(inviteId).requireSuccess()

        return NoResponseBody
    }

    /**
     * Registers the routes for the user controller. The [route] parameter is the root path for the controller.
     */
    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        UserApi.register(route) {
            handler(api.getUser, contextRetriever) { request ->
                getUser(request.context, request.pathParam)
            }
            unauthenticatedHandler(api.createUser, contextRetriever) { request ->
                createUser(request.requestBody)
            }
            handler(api.updatePassword, contextRetriever) { request ->
                updatePassword(request.context, request.requestBody)
            }
            handler(api.getAllUsers, contextRetriever) { request ->
                getUsers(request.context, request.queryParam)
            }
            handler(api.updateUser, contextRetriever) { request ->
                updateUser(request.context, request.requestBody, request.pathParam)
            }
            handler(api.deleteUser, contextRetriever) { request ->
                deleteUser(request.context, request.pathParam)
            }
            handler(api.associateUser, contextRetriever) { request ->
                associate(request.context)
            }
            handler(api.inviteUser, contextRetriever) { request ->
                inviteUser(request.context, request.requestBody)
            }
            handler(api.getInvites, contextRetriever) { request ->
                getInvites(request.context, request.pathParam)
            }
            handler(api.acceptInvite, contextRetriever) { request ->
                acceptInvite(request.context, request.pathParam)
            }
            handler(api.declineInvite, contextRetriever) { request ->
                declineInvite(request.context, request.pathParam)
            }
            handler(api.cancelInvite, contextRetriever) { request ->
                cancelInvite(request.context, request.pathParam)
            }
            unauthenticatedHandler(api.checkUserExists, contextRetriever) { request ->
                checkUserIsRegistered(request.queryParam.email)
            }
        }
    }
}
