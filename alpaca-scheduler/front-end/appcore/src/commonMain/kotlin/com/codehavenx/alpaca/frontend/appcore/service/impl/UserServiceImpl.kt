package com.codehavenx.alpaca.frontend.appcore.service.impl

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.frontend.appcore.service.UserService
import com.cramsan.framework.core.runSuspendCatching

/**
 * Default implementation of the user service.
 */
class UserServiceImpl : UserService {

    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        listOf()
    }

    override suspend fun createUser(
        userName: String,
        email: String?,
        phoneNumber: String?,
    ) = runSuspendCatching(TAG) {
        // TODO: Implement this
        /*
        val request = CreateUserRequest.create(userName, email, phoneNumber)
        httpClient.post(Routes.User.PATH) {
            setBody(request)
        }.body<UserResponse>().toModel()
         */
        User(
            id = "1",
            username = userName,
        )
    }

    companion object {
        private const val TAG = "UserServiceImpl"
    }
}
