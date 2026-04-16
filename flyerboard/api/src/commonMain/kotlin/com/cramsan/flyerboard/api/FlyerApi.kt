package com.cramsan.flyerboard.api

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.network.FlyerListNetworkResponse
import com.cramsan.flyerboard.lib.model.network.FlyerNetworkResponse
import com.cramsan.flyerboard.lib.model.network.ListFlyersQueryParams
import com.cramsan.flyerboard.lib.model.network.PaginationParams
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.BytesRequestBody
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for flyer operations.
 */
@OptIn(NetworkModel::class)
object FlyerApi : Api("api/v1/flyers") {

    /**
     * List publicly visible flyers with optional status filter and pagination.
     */
    val listFlyers = operation<
        NoRequestBody,
        ListFlyersQueryParams,
        NoPathParam,
        FlyerListNetworkResponse
        >(HttpMethod.Get)

    /**
     * Get a single flyer by ID.
     */
    val getFlyer = operation<
        NoRequestBody,
        NoQueryParam,
        FlyerId,
        FlyerNetworkResponse
        >(HttpMethod.Get)

    /**
     * Create a new flyer.
     *
     * Note: This operation deviates from the standard JSON body contract.
     * The request must be sent as multipart/form-data, with the flyer metadata
     * fields and the file binary included as separate parts.
     */
    val createFlyer = operation<
        BytesRequestBody,
        NoQueryParam,
        NoPathParam,
        FlyerNetworkResponse
        >(HttpMethod.Post)

    /**
     * Update an existing flyer.
     *
     * Note: This operation deviates from the standard JSON body contract.
     * The request must be sent as multipart/form-data, with the updated metadata
     * fields and an optional replacement file included as separate parts.
     */
    val updateFlyer = operation<
        BytesRequestBody,
        NoQueryParam,
        FlyerId,
        FlyerNetworkResponse
        >(HttpMethod.Put)

    /**
     * List archived flyers with pagination.
     */
    val listArchived = operation<
        NoRequestBody,
        PaginationParams,
        NoPathParam,
        FlyerListNetworkResponse
        >(HttpMethod.Get, path = "archive")

    /**
     * List the authenticated user's own flyers with pagination.
     */
    val listMyFlyers = operation<
        NoRequestBody,
        PaginationParams,
        NoPathParam,
        FlyerListNetworkResponse
        >(HttpMethod.Get, path = "mine")
}
