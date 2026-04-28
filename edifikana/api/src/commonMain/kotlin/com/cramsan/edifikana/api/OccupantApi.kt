package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.occupant.CreateOccupantNetworkRequest
import com.cramsan.edifikana.lib.model.network.occupant.GetOccupantsForUnitQueryParams
import com.cramsan.edifikana.lib.model.network.occupant.OccupantListNetworkResponse
import com.cramsan.edifikana.lib.model.network.occupant.OccupantNetworkResponse
import com.cramsan.edifikana.lib.model.network.occupant.UpdateOccupantNetworkRequest
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for occupant operations.
 *
 * Occupants are people associated with a unit — tenants or residents.
 * Write operations require ADMIN role or higher. Read operations require EMPLOYEE role or higher.
 */
@OptIn(NetworkModel::class)
object OccupantApi : Api("occupants") {

    val createOccupant = operation<
        CreateOccupantNetworkRequest,
        NoQueryParam,
        NoPathParam,
        OccupantNetworkResponse
        >(HttpMethod.Post)

    val getOccupant = operation<
        NoRequestBody,
        NoQueryParam,
        OccupantId,
        OccupantNetworkResponse
        >(HttpMethod.Get)

    val listOccupantsForUnit = operation<
        NoRequestBody,
        GetOccupantsForUnitQueryParams,
        NoPathParam,
        OccupantListNetworkResponse
        >(HttpMethod.Get)

    val updateOccupant = operation<
        UpdateOccupantNetworkRequest,
        NoQueryParam,
        OccupantId,
        OccupantNetworkResponse
        >(HttpMethod.Put)

    val removeOccupant = operation<
        NoRequestBody,
        NoQueryParam,
        OccupantId,
        OccupantNetworkResponse
        >(HttpMethod.Delete)
}
