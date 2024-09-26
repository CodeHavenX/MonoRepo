package com.codehavenx.alpaca.frontend.appcore.service.impl

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.frontend.appcore.service.UserService
import com.codehavenx.alpaca.frontend.appcore.utils.runSuspendCatching
import io.ktor.client.HttpClient

/**
 * Default implementation of the user service.
 */
class UserServiceImpl(
    private val httpClient: HttpClient,
) : UserService {

    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        listOf()
    }

    companion object {
        private const val TAG = "UserServiceImpl"
    }
}
