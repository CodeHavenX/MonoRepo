package com.codehavenx.alpaca.backend.controller

import com.codehavenx.alpaca.backend.controller.network.CreateUserRequest
import com.codehavenx.alpaca.backend.models.UserId
import com.codehavenx.alpaca.backend.service.UserService
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
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
     * Registers the routes for the user controller. The [route] parameter is the root path for the controller.
     */
    fun registerRoutes(route: Route) {
        route.route("user") {
            post {
                createUser(call)
            }
            get("{userId}") {
                getUser(call)
            }
        }
    }

    /**
     * Handles the creation of a new user. The [call] parameter is the request context.
     */
    suspend fun createUser(call: ApplicationCall) = call.handleCall(TAG, "createUser") {
        val createUserRequest = call.receive<CreateUserRequest>()

        val newUser = userService.createUser(
            createUserRequest.name,
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
        val userId = requireNotNull(call.parameters["userId"])

        val user = userService.getUser(
            UserId(userId),
        )

        HttpResponse(
            status = HttpStatusCode.OK,
            body = user,
        )
    }

    /**
     * Companion object.
     */
    companion object {
        private const val TAG = "UserController"
    }
}
