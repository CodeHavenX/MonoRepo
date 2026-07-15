package com.cramsan.flyerboard.server.controller

import com.cramsan.flyerboard.api.UserApi
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.service.UserService
import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.handler
import io.ktor.server.routing.Routing

/**
 * Controller for user related operations.
 */
@BackendController
class UserController(private val userService: UserService) : Controller {
    override fun registerRoutes(route: Routing) {
        UserApi.register(route, FlyerBoardContextPayload::class) {
            handler(api.createUser) { request ->
                userService
                    .createUser(
                        userId = request.context.payload.userId,
                        firstName = request.requestBody.firstName,
                        lastName = request.requestBody.lastName,
                    ).toUserNetworkResponse(request.context.payload.role)
            }

            // GET /user/me — retrieve the currently authenticated user, including their role.
            handler(api.getCurrentUser) { request ->
                userService
                    .getUser(request.context.payload.userId)
                    .toUserNetworkResponse(request.context.payload.role)
            }
        }
    }
}
