package com.codehavenx.alpaca.frontend.appcore.service.impl

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.frontend.appcore.service.UserService
import com.codehavenx.alpaca.frontend.appcore.utils.runSuspendCatching

/**
 * Default implementation of the user service.
 */
class UserServiceImpl(
    private val httpClient: HttpClient
) : UserService {

    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        listOf()
    }

    override suspend fun createUser(userName: String, email: String?, phoneNumber: String?) {
        // TODO: Implement this
        /*
        val request = CreateUserRequest.create(userName, email, phoneNumber)
        httpClient.post(Routes.User.PATH) {
            setBody(request)
        }.body<UserResponse>().toModel()
        */
    }

    companion object {
        private const val TAG = "UserServiceImpl"
    }
}
