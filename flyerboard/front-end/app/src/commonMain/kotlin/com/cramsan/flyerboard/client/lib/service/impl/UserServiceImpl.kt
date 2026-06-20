package com.cramsan.flyerboard.client.lib.service.impl

import com.cramsan.architecture.client.service.execute
import com.cramsan.flyerboard.api.UserApi
import com.cramsan.flyerboard.client.lib.models.UserModel
import com.cramsan.flyerboard.client.lib.service.UserService
import com.cramsan.flyerboard.lib.model.network.CreateUserNetworkRequest
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import io.ktor.client.HttpClient

/**
 * Implementation of [UserService].
 */
@FrontendService
class UserServiceImpl(private val http: HttpClient) : UserService {
    override suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<UserModel> =
        runSuspendCatching(TAG) {
            val response =
                UserApi.createUser
                    .buildRequest(
                        CreateUserNetworkRequest(
                            firstName = firstName,
                            lastName = lastName,
                        ),
                    ).execute(http)
            val userModel = response.toUserModel()
            userModel
        }

    override suspend fun getCurrentUser(): Result<UserModel> =
        runSuspendCatching(TAG) {
            UserApi.getCurrentUser
                .buildRequest()
                .execute(http)
                .toUserModel()
        }

    companion object {
        private const val TAG = "UserServiceImpl"
    }
}
