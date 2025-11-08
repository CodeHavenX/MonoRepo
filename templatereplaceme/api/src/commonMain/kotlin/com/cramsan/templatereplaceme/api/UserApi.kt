package com.cramsan.templatereplaceme.api

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.networkapi.Api
import com.cramsan.templatereplaceme.lib.model.network.CreateUserNetworkRequest
import com.cramsan.templatereplaceme.lib.model.network.UserNetworkResponse
import io.ktor.http.HttpMethod

/**
 * Singleton object representing an example API.
 */
@OptIn(NetworkModel::class)
object UserApi : Api("user") {

    val createUser =
        operation<
            CreateUserNetworkRequest,
            NoQueryParam,
            NoPathParam,
            UserNetworkResponse
            >(HttpMethod.Post)
}
