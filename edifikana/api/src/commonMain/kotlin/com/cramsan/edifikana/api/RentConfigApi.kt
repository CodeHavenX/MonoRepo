package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.rent.RentConfigNetworkRequest
import com.cramsan.edifikana.lib.model.network.rent.RentConfigNetworkResponse
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for rent configuration operations.
 *
 * Rent config is unit-scoped and uses upsert semantics (PUT creates or updates).
 * Write operations require ADMIN role or higher. Read operations require EMPLOYEE role or higher.
 */
@OptIn(NetworkModel::class)
object RentConfigApi : Api("rent-config") {

    val getRentConfig = operation<
        NoRequestBody,
        NoQueryParam,
        UnitId,
        RentConfigNetworkResponse
        >(HttpMethod.Get)

    val setRentConfig = operation<
        RentConfigNetworkRequest,
        NoQueryParam,
        UnitId,
        RentConfigNetworkResponse
        >(HttpMethod.Put)
}
