package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.network.GetRentConfigQueryParams
import com.cramsan.edifikana.lib.model.network.RentConfigNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpsertRentConfigNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for rent configuration operations.
 */
@OptIn(NetworkModel::class)
object RentConfigApi : Api("rent-config") {

    val getRentConfig = operation<
        NoRequestBody,
        GetRentConfigQueryParams,
        UnitId,
        RentConfigNetworkResponse
        >(HttpMethod.Get)

    val upsertRentConfig = operation<
        UpsertRentConfigNetworkRequest,
        NoQueryParam,
        UnitId,
        RentConfigNetworkResponse
        >(HttpMethod.Put)
}
