package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.network.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetTimeCardEventsQueryParams
import com.cramsan.edifikana.lib.model.network.TimeCardEventListNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for time card related operations.
 */
@OptIn(NetworkModel::class)
object TimeCardApi : Api("time_card") {
    val createTimeCardEvent = operation<
        CreateTimeCardEventNetworkRequest,
        NoQueryParam,
        NoPathParam,
        TimeCardEventNetworkResponse
        >(HttpMethod.Post)

    val getTimeCardEvent = operation<
        NoRequestBody,
        NoQueryParam,
        TimeCardEventId,
        TimeCardEventNetworkResponse
        >(HttpMethod.Get)

    val getTimeCardEvents = operation<
        NoRequestBody,
        GetTimeCardEventsQueryParams,
        NoPathParam,
        TimeCardEventListNetworkResponse
        >(HttpMethod.Get)
}
