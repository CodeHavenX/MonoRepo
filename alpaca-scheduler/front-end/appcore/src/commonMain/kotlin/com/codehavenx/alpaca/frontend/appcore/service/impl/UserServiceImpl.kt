package com.codehavenx.alpaca.frontend.appcore.service.impl

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.frontend.appcore.service.UserService
import com.codehavenx.alpaca.frontend.appcore.utils.runSuspendCatching
import com.codehavenx.alpaca.shared.api.Routes
import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import com.codehavenx.alpaca.shared.api.model.CreateUserRequest
import com.codehavenx.alpaca.shared.api.model.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * Default implementation of the user service.
 */
class UserServiceImpl : UserService {

    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        listOf()
    }

    @OptIn(NetworkModel::class)
    override suspend fun createUser(userName: String, email: String?, phoneNumber: String?) {
        val request = CreateUserRequest.create(userName, email, phoneNumber)
        httpClient.post(Routes.User.PATH) {
            setBody(request)
        }.body<UserResponse>().toModel()
    }

    companion object {
        private const val TAG = "UserServiceImpl"
    }
}
