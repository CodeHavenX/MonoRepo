package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.CHECK_GLOBAL_PERMS
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.USER_ID
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePasswordNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateUserNetworkRequest
import com.cramsan.edifikana.lib.utils.requireAtLeastOne
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.UserService
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

        requireAtLeastOne(
            "An email or phone number must be provided.",
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
        val checkGlobalPerms = call.request.queryParameters[CHECK_GLOBAL_PERMS]?.toBooleanStrictOrNull() == true

        val user = userService.getUser(
            id = UserId(userId),
            checkGlobalPerms = checkGlobalPerms
        )?.toUserNetworkResponse()

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
    @OptIn(NetworkModel::class)
    suspend fun updatePassword(call: RoutingCall) = call.handleCall(TAG, "updatePassword", contextRetriever) {
        val userId = requireNotNull(call.parameters[USER_ID])

        // TODO: Verify permissions
        val updatePasswordRequest = call.receive<UpdatePasswordNetworkRequest>()

        val success = userService.updatePassword(
            userId = UserId(userId),
            password = updatePasswordRequest.password,
        )

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
     * Handles the retrieval of all users. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUsers(call: ApplicationCall) = call.handleCall(TAG, "getUsers", contextRetriever) { _ ->
        val users = userService.getUsers().map { it.toUserNetworkResponse() }

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
        ).toUserNetworkResponse()

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
        )

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
                put("{$USER_ID}/password") {
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
            }
        }
    }
}
