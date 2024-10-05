package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.USER_ID
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.UpdateUserNetworkRequest
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.models.UserId
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
) {

    /**
     * Handles the creation of a new user. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createUser(call: ApplicationCall) = call.handleCall(TAG, "createUser") {
        val createUserRequest = call.receive<CreateUserNetworkRequest>()

        val newUser = userService.createUser(
            createUserRequest.email,
        ).toUserNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newUser,
        )
    }

    /**
     * Handles the retrieval of a user. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUser(call: ApplicationCall) = call.handleCall(TAG, "getUser") {
        val userId = requireNotNull(call.parameters[USER_ID])

        val user = userService.getUser(
            UserId(userId),
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
     * Handles the retrieval of all users. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getUsers(call: ApplicationCall) = call.handleCall(TAG, "getUsers") {
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
    suspend fun updateUser(call: ApplicationCall) = call.handleCall(TAG, "updateUser") {
        val userId = requireNotNull(call.parameters[USER_ID])

        val updateUserRequest = call.receive<UpdateUserNetworkRequest>()

        val updatedUser = userService.updateUser(
            id = UserId(userId),
            username = updateUserRequest.email,
        ).toUserNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedUser,
        )
    }

    /**
     * Handles the deletion of a user. The [call] parameter is the request context.
     */
    suspend fun deleteUser(call: RoutingCall) {
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
