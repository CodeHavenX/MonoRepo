package com.cramsan.flyerboard.api

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.networkapi.Api
import com.cramsan.flyerboard.lib.model.network.CreateUserNetworkRequest
import com.cramsan.flyerboard.lib.model.network.UserNetworkResponse
import io.ktor.http.HttpMethod

/**
 * API definition for user-related endpoints.
 */
@OptIn(NetworkModel::class)
object UserApi : Api("user") {

    /** Operation to create a new user. */
    val createUser =
        operation<
            CreateUserNetworkRequest,
            NoQueryParam,
            NoPathParam,
            UserNetworkResponse
            >(HttpMethod.Post)
}
