package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import com.cramsan.templatereplaceme.api.UserApi
import com.cramsan.templatereplaceme.lib.model.network.CreateUserNetworkRequest
import com.cramsan.templatereplaceme.lib.model.network.UserNetworkResponse
import com.cramsan.templatereplaceme.server.service.UserService
import io.ktor.server.routing.Routing

/**
 * Controller for user related operations.
 */
class UserController(
    private val userService: UserService,
    private val contextRetriever: ContextRetriever<Unit>,
) : Controller {

    /**
     * Creates a new user.
     *
     * @param createUserRequest The request containing user details.
     * @return The created user's network response.
     */
    @OptIn(NetworkModel::class)
    suspend fun createUser(createUserRequest: CreateUserNetworkRequest): UserNetworkResponse {
        val newUserResult = userService.createUser(
            firstName = createUserRequest.firstName,
            lastName = createUserRequest.lastName,
        )

        return newUserResult.getOrThrow().toUserNetworkResponse()
    }

    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        UserApi.register(route) {
            unauthenticatedHandler(api.createUser, contextRetriever) { request ->
                createUser(request.requestBody)
            }
        }
    }
}
