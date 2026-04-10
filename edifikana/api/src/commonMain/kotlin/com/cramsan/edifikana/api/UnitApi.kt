package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.GetUnitsQueryParams
import com.cramsan.edifikana.lib.model.network.unit.CreateUnitNetworkRequest
import com.cramsan.edifikana.lib.model.network.unit.UnitListNetworkResponse
import com.cramsan.edifikana.lib.model.network.unit.UnitNetworkResponse
import com.cramsan.edifikana.lib.model.network.unit.UpdateUnitNetworkRequest
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for unit CRUD operations.
 */
@OptIn(NetworkModel::class)
object UnitApi : Api("unit") {

    val createUnit = operation<
        CreateUnitNetworkRequest,
        NoQueryParam,
        NoPathParam,
        UnitNetworkResponse
        >(HttpMethod.Post)

    val getUnit = operation<
        NoRequestBody,
        NoQueryParam,
        UnitId,
        UnitNetworkResponse
        >(HttpMethod.Get)

    val getUnits = operation<
        NoRequestBody,
        GetUnitsQueryParams,
        NoPathParam,
        UnitListNetworkResponse,
        >(HttpMethod.Get)

    val updateUnit = operation<
        UpdateUnitNetworkRequest,
        NoQueryParam,
        UnitId,
        UnitNetworkResponse
        >(HttpMethod.Put)

    val deleteUnit = operation<
        NoRequestBody,
        NoQueryParam,
        UnitId,
        NoResponseBody
        >(HttpMethod.Delete)
}
