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
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for flyer operations.
 */

object FlyerApi : Api("api/v1/flyers", group = "flyers") {
    /**
     * List publicly visible flyers with optional status filter and pagination.
     */
    val listFlyers =
        publicOperation<
            NoRequestBody,
            ListFlyersQueryParams,
            NoPathParam,
            FlyerListNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "List flyers",
            description = "Lists publicly visible flyers with optional status filter and pagination.",
            responses = UniversalResponsesOnly,
        )

    /**
     * Get a single flyer by ID.
     */
    val getFlyer =
        optionalOperation<
            NoRequestBody,
            NoQueryParam,
            FlyerId,
            FlyerNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Get a flyer",
            description = "Retrieves a single flyer by its identifier.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs
                    "No flyer exists for the given id, or it exists but requires admin privileges to view " +
                    "(pending or rejected status)."
            },
        )

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
            >(
            method = HttpMethod.Post,
            summary = "Create a flyer",
            description =
            "Creates a new flyer with PENDING status. The response includes a signed upload URL the " +
                "client should use to upload the flyer's asset.",
            responses = UniversalResponsesOnly,
        )

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
            >(
            method = HttpMethod.Put,
            summary = "Update a flyer",
            description =
            "Updates an existing flyer. Any edit, including requesting an upload, resets status to " +
                "PENDING for re-moderation. Requires the caller to be the flyer's original uploader.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No flyer exists for the given id."
                HttpStatusCode.Forbidden describedAs "Caller is not the flyer's original uploader."
            },
        )

    /**
     * List archived flyers with optional search and pagination.
     */
    val listArchived =
        publicOperation<
            NoRequestBody,
            ListFlyersQueryParams,
            NoPathParam,
            FlyerListNetworkResponse,
            >(
            method = HttpMethod.Get,
            path = "archive",
            summary = "List archived flyers",
            description = "Lists archived flyers with optional search and pagination.",
            responses = UniversalResponsesOnly,
        )

    /**
     * List the authenticated user's own flyers with pagination.
     */
    val listMyFlyers =
        operation<
            NoRequestBody,
            PaginationParams,
            NoPathParam,
            FlyerListNetworkResponse,
            >(
            method = HttpMethod.Get,
            path = "mine",
            summary = "List my flyers",
            description = "Lists the authenticated user's own flyers with pagination, regardless of status.",
            responses = UniversalResponsesOnly,
        )
}
