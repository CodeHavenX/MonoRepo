package com.codehavenx.alpaca.frontend.appcore.service.impl

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.frontend.appcore.service.UserService
import com.codehavenx.alpaca.frontend.appcore.service.impl.mappers.toModel
import com.codehavenx.alpaca.frontend.appcore.utils.runSuspendCatching
import com.codehavenx.alpaca.shared.api.Routes
import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import com.codehavenx.alpaca.shared.api.model.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * Default implementation of the user service.
 */
class UserServiceImpl(
    private val httpClient: HttpClient,
) : UserService {

    @OptIn(NetworkModel::class)
    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        httpClient.get(Routes.User.PATH).body<List<UserResponse>>().map { it.toModel() }
    }

    companion object {
        private const val TAG = "UserServiceImpl"
    }
}
