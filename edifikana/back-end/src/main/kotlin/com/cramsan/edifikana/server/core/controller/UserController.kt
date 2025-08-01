package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.USER_ID
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePasswordNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateUserNetworkRequest
import com.cramsan.edifikana.lib.utils.requireAll
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

/**
 * Controller for user related operations. CRUD operations for users.
 */
class UserController(
    private val userService: UserService,
    private val contextRetriever: ContextRetriever,
) {

    /**
     * Handles the creation of a new user. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createUser(call: ApplicationCall) = call.handleCall(TAG, "createUser", contextRetriever) {
        val createUserRequest = call.receive<CreateUserNetworkRequest>()

        requireAll(
            "An email and phone number must be provided.",
            createUserRequest.email,
            createUserRequest.phoneNumber
        )

        val newUserResult = userService.createUser(
            createUserRequest.email,
            createUserRequest.phoneNumber,
            createUserRequest.password,
            createUserRequest.firstName,
            createUserRequest.lastName,
        )

        val newUser = newUserResult.requireSuccess().toUserNetworkResponse()
        HttpResponse(
            status = HttpStatusCode.OK,
            body = newUser,
        )
    }

    /**
     * Handles the retrieval of a user. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUser(call: ApplicationCall) = call.handleCall(TAG, "getUser", contextRetriever) {
        val userId = requireNotNull(call.parameters[USER_ID])

        val user = userService.getUser(
            id = UserId(userId),
        ).getOrNull()?.toUserNetworkResponse()

        val statusCode = if (user == null) {
            HttpStatusCode.NotFound
        } else {
            HttpStatusCode.OK
        }

        HttpResponse(
            status = statusCode,
            body = user,
        )
    }

    /**
     * Handles the updating of a user's password. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class, SecureStringAccess::class)
    suspend fun updatePassword(call: RoutingCall) = call.handleCall(
        TAG,
        "updatePassword",
        contextRetriever,
    ) { context ->
        val authenticatedContext = getAuthenticatedClientContext(context)
        val userId = authenticatedContext.userId

        val updatePasswordRequest = call.receive<UpdatePasswordNetworkRequest>()

        val result = userService.updatePassword(
            userId = userId,
            currentHashedPassword = SecureString(updatePasswordRequest.currentPasswordHashed),
            newPassword = SecureString(updatePasswordRequest.newPassword),
        )

        val statusCode = if (result.isSuccess) {
            HttpStatusCode.OK
        } else {
            HttpStatusCode.BadRequest
        }

        val responseBody = if (result.isSuccess) {
            null
        } else {
            result.exceptionOrNull()?.message ?: "Failed to update password."
        }

        HttpResponse(
            status = statusCode,
            body = responseBody,
        )
    }

    /**
     * Handles the retrieval of all users. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUsers(call: ApplicationCall) = call.handleCall(TAG, "getUsers", contextRetriever) { _ ->
        val users = userService.getUsers().getOrThrow().map { it.toUserNetworkResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = users,
        )
    }

    /**
     * Handles the updating of a user. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateUser(call: ApplicationCall) = call.handleCall(TAG, "updateUser", contextRetriever) { _ ->
        val userId = requireNotNull(call.parameters[USER_ID])

        val updateUserRequest = call.receive<UpdateUserNetworkRequest>()

        val updatedUser = userService.updateUser(
            id = UserId(userId),
            email = updateUserRequest.email,
        ).getOrThrow().toUserNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedUser,
        )
    }

    /**
     * Handles the deletion of a user. The [call] parameter is the request context.
     */
    suspend fun deleteUser(call: RoutingCall) = call.handleCall(TAG, "deleteUser", contextRetriever) { _ ->
        val userId = requireNotNull(call.parameters[USER_ID])

        val success = userService.deleteUser(
            UserId(userId),
        ).isSuccess

        val statusCode = if (success) {
            HttpStatusCode.OK
        } else {
            HttpStatusCode.NotFound
        }

        HttpResponse(
            status = statusCode,
            body = null,
        )
    }

    /**
     * Handle a call to associate a user created in another system (e.g., Supabase) with our system.
     */
    @OptIn(NetworkModel::class)
    suspend fun associate(call: RoutingCall) = call.handleCall(TAG, "associate", contextRetriever) { context ->
        val authenticatedContext = getAuthenticatedClientContext(context)
        val userId = authenticatedContext.userId
        val email = authenticatedContext.userInfo.email

        requireNotBlank(email, "User does not have a configured email.")

        val newUserResult = userService.associateUser(
            userId,
            email,
        )

        val newUser = newUserResult.requireSuccess().toUserNetworkResponse()
        HttpResponse(
            status = HttpStatusCode.OK,
            body = newUser,
        )
    }

    /**
     * Companion object.
     */
    companion object {
        private const val TAG = "UserController"

        /**
         * Registers the routes for the user controller. The [route] parameter is the root path for the controller.
         */
        fun UserController.registerRoutes(route: Routing) {
            route.route(Routes.User.PATH) {
                post {
                    createUser(call)
                }
                get("{$USER_ID}") {
                    getUser(call)
                }
                put("/password") {
                    updatePassword(call)
                }
                get {
                    getUsers(call)
                }
                put("{$USER_ID}") {
                    updateUser(call)
                }
                delete("{$USER_ID}") {
                    deleteUser(call)
                }
                post("associate") {
                    associate(call)
                }
            }
        }
    }
}
