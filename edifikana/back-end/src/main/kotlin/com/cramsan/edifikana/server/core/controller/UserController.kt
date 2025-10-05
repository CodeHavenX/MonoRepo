package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.api.UserApi
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
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
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.authorization.RBACService
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.utils.exceptions.UnauthorizedException
import com.cramsan.framework.utils.exceptions.requireAll
import io.ktor.server.routing.Routing

/**
 * Controller for user related operations. CRUD operations for users.
 */
class UserController(
    private val userService: UserService,
    private val contextRetriever: ContextRetriever,
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
            createUserRequest.phoneNumber
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
    suspend fun getUser(context: ClientContext.AuthenticatedClientContext, userId: UserId): UserNetworkResponse? {
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
        authenticatedContext: ClientContext.AuthenticatedClientContext,
        updatePasswordRequest: UpdatePasswordNetworkRequest,
    ): NoResponseBody {
        val userId = authenticatedContext.userId
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
        context: ClientContext.AuthenticatedClientContext,
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
        context: ClientContext.AuthenticatedClientContext,
        updateUserRequest: UpdateUserNetworkRequest,
        userId: UserId
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
    suspend fun deleteUser(context: ClientContext.AuthenticatedClientContext, userId: UserId): NoResponseBody {
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
    suspend fun associate(authenticatedContext: ClientContext.AuthenticatedClientContext): UserNetworkResponse {
        val userId = authenticatedContext.userId
        val email = authenticatedContext.userInfo.email

        requireNotBlank(email, "User does not have a configured email.")

        val newUserResult = userService.associateUser(
            userId,
            email,
        )

        return newUserResult.requireSuccess().toUserNetworkResponse()
    }

    /**
     * Handle a call to invite a user to the system via email.
     * Invites a user with the given [inviteRequest] if the authenticated context has the required role.
     * Returns [NoResponseBody] to indicate successful invitation.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    @OptIn(NetworkModel::class)
    suspend fun inviteUser(
        context: ClientContext.AuthenticatedClientContext,
        inviteRequest: InviteUserNetworkRequest
    ): NoResponseBody {
        val email = inviteRequest.email
        val orgId = inviteRequest.organizationId
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }

        userService.inviteUser(
            email,
            orgId,
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
        context: ClientContext.AuthenticatedClientContext,
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
        }
    }
}
