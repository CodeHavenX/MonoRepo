package com.cramsan.flyerboard.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import com.cramsan.flyerboard.api.UserApi
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.service.UserService
import io.ktor.server.routing.Routing

/**
 * Controller for user related operations.
 */
class UserController(
    private val userService: UserService,
    private val contextRetriever: ContextRetriever<FlyerBoardContextPayload>,
) : Controller {

    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        UserApi.register(route) {
            unauthenticatedHandler(api.createUser, contextRetriever) { request ->
                userService.createUser(
                    firstName = request.requestBody.firstName,
                    lastName = request.requestBody.lastName,
                ).getOrThrow().toUserNetworkResponse()
            }
        }
    }
}
