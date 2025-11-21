package com.cramsan.runasimi.api

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * Singleton object representing the Runasimi API.
 */
@OptIn(NetworkModel::class)
object RunasimiApi : Api("example") {

    /**
     * A simple ping operation to check if the service is alive.
     */
    val ping = operation<NoRequestBody, NoQueryParam, NoPathParam, NoResponseBody>(HttpMethod.Get)
}
