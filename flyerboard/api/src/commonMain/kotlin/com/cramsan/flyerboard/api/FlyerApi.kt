package com.cramsan.flyerboard.api

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.network.CreateFlyerNetworkRequest
import com.cramsan.flyerboard.lib.model.network.CreateFlyerNetworkResponse
import com.cramsan.flyerboard.lib.model.network.FlyerListNetworkResponse
import com.cramsan.flyerboard.lib.model.network.FlyerNetworkResponse
import com.cramsan.flyerboard.lib.model.network.ListFlyersQueryParams
import com.cramsan.flyerboard.lib.model.network.PaginationParams
import com.cramsan.flyerboard.lib.model.network.UpdateFlyerNetworkRequest
import com.cramsan.flyerboard.lib.model.network.UpdateFlyerNetworkResponse
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for flyer operations.
 */

object FlyerApi : Api("api/v1/flyers") {
    /**
     * List publicly visible flyers with optional status filter and pagination.
     */
    val listFlyers =
        operation<
            NoRequestBody,
            ListFlyersQueryParams,
            NoPathParam,
            FlyerListNetworkResponse,
            >(HttpMethod.Get)

    /**
     * Get a single flyer by ID.
     */
    val getFlyer =
        operation<
            NoRequestBody,
            NoQueryParam,
            FlyerId,
            FlyerNetworkResponse,
            >(HttpMethod.Get)

    /**
     * Create a new flyer.
     *
     * The backend generates the flyer's [FlyerId] and creates the flyer with
     * [com.cramsan.flyerboard.lib.model.FlyerStatus.PENDING] status. The response includes a
     * signed upload URL the client should use to upload the flyer's asset.
     */
    val createFlyer =
        operation<
            CreateFlyerNetworkRequest,
            NoQueryParam,
            NoPathParam,
            CreateFlyerNetworkResponse,
            >(HttpMethod.Post)

    /**
     * Update an existing flyer.
     *
     * Set [UpdateFlyerNetworkRequest.requestUpload] to `true` to receive a fresh signed upload
     * URL for this flyer's asset. Any edit, including requesting an upload, resets status to
     * [com.cramsan.flyerboard.lib.model.FlyerStatus.PENDING].
     */
    val updateFlyer =
        operation<
            UpdateFlyerNetworkRequest,
            NoQueryParam,
            FlyerId,
            UpdateFlyerNetworkResponse,
            >(HttpMethod.Put)

    /**
     * List archived flyers with optional search and pagination.
     */
    val listArchived =
        operation<
            NoRequestBody,
            ListFlyersQueryParams,
            NoPathParam,
            FlyerListNetworkResponse,
            >(HttpMethod.Get, path = "archive")

    /**
     * List the authenticated user's own flyers with pagination.
     */
    val listMyFlyers =
        operation<
            NoRequestBody,
            PaginationParams,
            NoPathParam,
            FlyerListNetworkResponse,
            >(HttpMethod.Get, path = "mine")
}
