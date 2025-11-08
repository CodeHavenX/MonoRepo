package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.templatereplaceme.api.UserApi
import com.cramsan.templatereplaceme.client.lib.models.UserModel
import com.cramsan.templatereplaceme.client.lib.service.UserService
import com.cramsan.templatereplaceme.lib.model.network.CreateUserNetworkRequest
import io.ktor.client.HttpClient

/**
 * Implementation of [UserService].
 */
class UserServiceImpl(
    private val http: HttpClient,
) : UserService {

    @OptIn(NetworkModel::class)
    override suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<UserModel> = runSuspendCatching(TAG) {
        val response = UserApi.createUser.buildRequest(
            CreateUserNetworkRequest(
                firstName = firstName,
                lastName = lastName,
            ),
        ).execute(http)
        val userModel = response.toUserModel()
        userModel
    }

    companion object {
        private const val TAG = "UserServiceImpl"
    }
}
