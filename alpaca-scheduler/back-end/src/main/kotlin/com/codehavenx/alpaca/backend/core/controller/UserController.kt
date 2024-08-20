package com.codehavenx.alpaca.backend.core.controller

import com.codehavenx.alpaca.backend.core.service.UserService
import com.codehavenx.alpaca.backend.core.service.models.UserId
import com.codehavenx.alpaca.shared.api.Routes
import com.codehavenx.alpaca.shared.api.USER_ID
import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import com.codehavenx.alpaca.shared.api.model.CreateUserRequest
import com.codehavenx.alpaca.shared.api.model.UpdateUserRequest
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
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
        val createUserRequest = call.receive<CreateUserRequest>()

        val newUser = userService.createUser(
            createUserRequest.username,
        )

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newUser,
        )
    }

    /**
     * Handles the retrieval of a user. The [call] parameter is the request context.
     */
    suspend fun getUser(call: ApplicationCall) = call.handleCall(TAG, "getUser") {
        val userId = requireNotNull(call.parameters[USER_ID])

        val user = userService.getUser(
            UserId(userId),
        )

        HttpResponse(
            status = HttpStatusCode.OK,
            body = user,
        )
    }

    /**
     * Handles the updating of a user. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateUser(call: ApplicationCall) = call.handleCall(TAG, "updateUser") {
        val userId = requireNotNull(call.parameters[USER_ID])

        val updateUserRequest = call.receive<UpdateUserRequest>()

        val updatedUser = userService.updateUser(
            id = UserId(userId),
            username = updateUserRequest.username,
        )

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedUser,
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
            }
        }
    }
}
