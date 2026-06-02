package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.architecture.client.service.execute
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.templatereplaceme.api.PingPonApi
import com.cramsan.templatereplaceme.client.lib.models.UserModel
import com.cramsan.templatereplaceme.client.lib.service.UserService
import com.cramsan.templatereplaceme.lib.model.network.PingNetworkRequest
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
                PingPonApi.ping
                    .buildRequest(
                        PingNetworkRequest(
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
