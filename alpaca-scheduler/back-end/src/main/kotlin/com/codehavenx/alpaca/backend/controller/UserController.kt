package com.codehavenx.alpaca.backend.controller

import com.codehavenx.alpaca.backend.models.UserId
import com.codehavenx.alpaca.backend.service.UserService
import com.codehavenx.alpaca.shared.api.Routes
import com.codehavenx.alpaca.shared.api.annotations.ApiNetwork
import com.codehavenx.alpaca.shared.api.controller.USER_ID
import com.codehavenx.alpaca.shared.api.model.CreateUserRequest
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
) : BaseController() {

    /**
     * Registers the routes for the user controller. The [route] parameter is the root path for the controller.
     */
    override fun registerRoutes(route: Routing) {
        route.route(Routes.User.PATH) {
            post {
                createUser(call)
            }
            get("{$USER_ID}") {
                getUser(call)
            }
        }
    }

    /**
     * Handles the creation of a new user. The [call] parameter is the request context.
     */
    @OptIn(ApiNetwork::class)
    suspend fun createUser(call: ApplicationCall) = call.handleCall(TAG, "createUser") {
        val createUserRequest = call.receive<CreateUserRequest>()

        val newUser = userService.createUser(
            createUserRequest.userName,
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
     * Companion object.
     */
    companion object {
        private const val TAG = "UserController"
    }
}
