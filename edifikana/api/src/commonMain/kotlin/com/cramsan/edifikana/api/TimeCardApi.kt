package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.timeCard.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.lib.model.network.timeCard.GetTimeCardEventsQueryParams
import com.cramsan.edifikana.lib.model.network.timeCard.TimeCardEventListNetworkResponse
import com.cramsan.edifikana.lib.model.network.timeCard.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventId
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for time card related operations.
 */

object TimeCardApi : Api("time_card") {
    val createTimeCardEvent =
        operation<
            CreateTimeCardEventNetworkRequest,
            NoQueryParam,
            NoPathParam,
            TimeCardEventNetworkResponse,
            >(
            method = HttpMethod.Post,
            summary = "Create a time card event",
            description = "Records a new clock-in, clock-out, or other time card event for an employee.",
            responses = UniversalResponsesOnly,
        )

    val getTimeCardEvent =
        operation<
            NoRequestBody,
            NoQueryParam,
            TimeCardEventId,
            TimeCardEventNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Get a time card event",
            description = "Retrieves a single time card event by its identifier.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No time card event exists for the given id."
            },
        )

    val getTimeCardEvents =
        operation<
            NoRequestBody,
            GetTimeCardEventsQueryParams,
            NoPathParam,
            TimeCardEventListNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "List time card events",
            description = "Lists time card events for a property, optionally filtered by employee.",
            responses = UniversalResponsesOnly,
        )
}
