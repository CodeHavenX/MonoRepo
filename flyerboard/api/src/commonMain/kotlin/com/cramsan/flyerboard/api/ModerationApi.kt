package com.cramsan.flyerboard.api

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.network.FlyerListNetworkResponse
import com.cramsan.flyerboard.lib.model.network.FlyerNetworkResponse
import com.cramsan.flyerboard.lib.model.network.ModerationActionNetworkRequest
import com.cramsan.flyerboard.lib.model.network.PaginationParams
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for moderation operations. All operations require admin role.
 */
@OptIn(NetworkModel::class)
object ModerationApi : Api("api/v1/moderation") {

    /**
     * List all flyers with pending status. Admin-only.
     */
    val listPending = operation<
        NoRequestBody,
        PaginationParams,
        NoPathParam,
        FlyerListNetworkResponse
        >(HttpMethod.Get)

    /**
     * Apply a moderation action to a flyer. Admin-only.
     *
     * Design decision: approve and reject are intentionally combined into a single endpoint
     * (`POST /api/v1/moderation/{id}`) with an `action` body field rather than two separate
     * sub-path endpoints (`/approve`, `/reject`). This keeps the API contract simple and avoids
     * the need for path-param-before-sub-path routing which the current framework does not
     * support directly. The controller dispatches to the appropriate service method based on
     * the `action` value ("approve" or "reject").
     */
    val moderate = operation<
        ModerationActionNetworkRequest,
        NoQueryParam,
        FlyerId,
        FlyerNetworkResponse
        >(HttpMethod.Post)
}
