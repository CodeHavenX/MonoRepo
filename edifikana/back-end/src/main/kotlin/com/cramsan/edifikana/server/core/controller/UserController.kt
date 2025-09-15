package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.api.UserApi
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetAllUsersQueryParams
import com.cramsan.edifikana.lib.model.network.InviteNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePasswordNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.utils.exceptions.requireAll
import io.ktor.server.routing.Routing

/**
 * Controller for user related operations. CRUD operations for users.
 */
class UserController(
    private val userService: UserService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Handles the creation of a new user.
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
     * Handles the retrieval of a user.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUser(userId: String): UserNetworkResponse? {
        return userService.getUser(
            id = UserId(userId),
        ).getOrNull()?.toUserNetworkResponse()
    }

    /**
     * Handles the updating of a user's password.
     */
    @OptIn(NetworkModel::class, SecureStringAccess::class)
    suspend fun updatePassword(
        authenticatedContext: ClientContext.AuthenticatedClientContext,
        updatePasswordRequest: UpdatePasswordNetworkRequest,
    ) {
        val userId = authenticatedContext.userId

        val result = userService.updatePassword(
            userId = userId,
            currentHashedPassword = SecureString(updatePasswordRequest.currentPasswordHashed),
            newPassword = SecureString(updatePasswordRequest.newPassword),
        )

        result.requireSuccess()
    }

    /**
     * Handles the retrieval of all users.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUsers(
        queryParams: GetAllUsersQueryParams,
    ): List<UserNetworkResponse> {
        val orgId = requireNotBlank(queryParams.orgId, "An organization ID must be provided.")

        val users = userService.getUsers(
            organizationId = OrganizationId(orgId)
        ).getOrThrow().map { it.toUserNetworkResponse() }

        return users
    }

    /**
     * Handles the updating of a user.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateUser(updateUserRequest: UpdateUserNetworkRequest, param: String): UserNetworkResponse {
        val userId = requireNotBlank(param)

        val updatedUser = userService.updateUser(
            id = UserId(userId),
            email = updateUserRequest.email,
        ).getOrThrow().toUserNetworkResponse()

        return updatedUser
    }

    /**
     * Handles the deletion of a user.
     */
    suspend fun deleteUser(param: String) {
        val result = userService.deleteUser(
            UserId(param),
        )

        result.requireSuccess()
    }

    /**
     * Handle a call to associate a user created in another system (e.g., Supabase) with our system.
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
     */
    @OptIn(NetworkModel::class)
    suspend fun inviteUser(inviteRequest: InviteUserNetworkRequest) {
        val email = inviteRequest.email
        val organizationId = OrganizationId(inviteRequest.organizationId)

        userService.inviteUser(
            email,
            organizationId,
        ).requireSuccess()
    }

    /**
     * Handle a call to get all pending invites for an organization.
     */
    @OptIn(NetworkModel::class)
    suspend fun getInvites(param: String): List<InviteNetworkResponse> {
        val orgId = requireNotBlank(param)

        val invites = userService.getInvites(
            organizationId = OrganizationId(orgId)
        ).getOrThrow().map { it.toInviteNetworkResponse() }

        return invites
    }

    /**
     * Handle a call to get all pending invites for an organization.
     */
    @OptIn(NetworkModel::class)
    suspend fun getInvites(call: RoutingCall) = call.handleCall(TAG, "getInvites", contextRetriever) { _ ->
        val orgId = requireNotBlank(call.request.queryParameters[Routes.User.QueryParams.ORG_ID])

        val invites = userService.getInvites(
            organizationId = OrganizationId(orgId)
        ).getOrThrow().map { it.toInviteNetworkResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = invites,
        )
    }

    /**
     * Registers the routes for the user controller. The [route] parameter is the root path for the controller.
     */
    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        UserApi.register(route) {
            handler(api.getUser, contextRetriever) { _, _, _, param ->
                getUser(param)
            }
            unauthenticatedHandler(api.createUser, contextRetriever) { _, body, _ ->
                createUser(body)
            }
            handler(api.updatePassword, contextRetriever) { context, body, _ ->
                updatePassword(context, body)
            }
            handler(api.getAllUsers, contextRetriever) { _, _, queryParam ->
                getUsers(queryParam)
            }
            handler(api.updateUser, contextRetriever) { _, body, _, param ->
                updateUser(body, param)
            }
            handler(api.deleteUser, contextRetriever) { _, _, _, param ->
                deleteUser(param)
            }
            handler(api.associateUser, contextRetriever) { context, _, _ ->
                associate(context)
            }
            handler(api.inviteUser, contextRetriever) { _, body, _ ->
                inviteUser(body)
            }
            handler(api.getInvites, contextRetriever) { _, _, _, param ->
                getInvites(param)
            }
        }
    }
}
